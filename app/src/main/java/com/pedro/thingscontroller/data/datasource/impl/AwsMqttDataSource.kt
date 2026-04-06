package com.pedro.thingscontroller.data.datasource.impl

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableIntState
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.amazonaws.regions.Regions
import com.pedro.thingscontroller.data.datasource.MqttDataSource
import com.pedro.thingscontroller.data.model.MqttConnectionState
import com.pedro.thingscontroller.data.model.MqttException
import com.pedro.thingscontroller.data.model.toMqttConnectionState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AwsMqttDataSource @Inject constructor(
    @ApplicationContext private val context: Context
): MqttDataSource {
    private val TAG = "AwsMqttDataSource"
    private val IDENTIY_POOL_ID = "us-east-2:897f1853-5ca5-4967-a291-941afbac20a5"
    private val ENDPOINT = "ayudx6zl9j13x-ats.iot.us-east-2.amazonaws.com"
    private val AWS_REGION = Regions.US_EAST_2

    private var mqttManager: AWSIotMqttManager? = null
    private var credentialsProvider: CognitoCachingCredentialsProvider? = null

    private val _connectionState = MutableStateFlow<MqttConnectionState>(MqttConnectionState.Disconnected)
    override val connectionState: StateFlow<MqttConnectionState> = _connectionState

    // Single shared flow — all incoming messages funnel here
    private val _messageFlow = MutableSharedFlow<Pair<String, String>>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )

    // Tracks active subscriptions to avoid duplicates
    private val activeSubscriptions = mutableSetOf<String>()


    override suspend fun connect() {
        if (_connectionState.value is MqttConnectionState.Connected) return

        credentialsProvider = CognitoCachingCredentialsProvider(
            context,
            IDENTIY_POOL_ID,
            AWS_REGION
        )

        mqttManager = AWSIotMqttManager(
            "app-client-${System.currentTimeMillis()}",
            ENDPOINT
        ).apply { keepAlive = 10 }

        val connectionDeferred = CompletableDeferred<Unit>()

        mqttManager?.connect(credentialsProvider){status, throwable ->
            _connectionState.value = status.toMqttConnectionState(throwable)
            Log.d(TAG, "connect: Mqtt Connection state changed: ${_connectionState.value}")

            if(_connectionState.value is MqttConnectionState.Connected){
                connectionDeferred.complete(Unit)
            }else if (throwable != null){
                connectionDeferred.completeExceptionally(throwable)
            }
        }

        connectionDeferred.await()
    }

    override suspend fun disconnect() {
        try{
            mqttManager?.disconnect()
            activeSubscriptions.clear()
            _connectionState.value = MqttConnectionState.Disconnected
        } catch (e: Exception){
            Log.e(TAG, "disconnect: Error disconnecting", e)
        } finally {
            mqttManager = null
            credentialsProvider = null
        }
    }

    override suspend fun subscribe(topic: String) {
        if(_connectionState.value !is MqttConnectionState.Connected){
            Log.w(TAG, "subscribe: Cannot subscribe to $topic: not connected")
            return
        }
        if (topic in activeSubscriptions) {
            Log.d(TAG, "subscribe: Already subscribed to $topic")
            return
        }
        try {
            mqttManager?.subscribeToTopic(topic, AWSIotMqttQos.QOS1){returnedTopic, data ->
                val message = String(data, Charsets.UTF_8)
                Log.d(TAG, "subscribe: Message received on '$returnedTopic'")
                _messageFlow.tryEmit(Pair(returnedTopic, message))
            }
            activeSubscriptions.add(topic)
        } catch (e: Exception) {
            Log.e(TAG, "subscribe: Error subscribing to $topic", e)
            throw MqttException.SubscriptionFailed(topic, e)
        }
    }

    override suspend fun unsubscribe(topic: String) {
        try {
            mqttManager?.unsubscribeTopic(topic)
            activeSubscriptions.remove(topic)
        } catch (e: Exception) {
            Log.e(TAG, "Error unsubscribing from $topic", e)
        }
    }

    override suspend fun publish(topic: String, payload: String) {
        // Not used....
    }

    override fun observeTopic(topic: String): Flow<String> =
        _messageFlow
            .filter { (returnedTopic, _) -> topicMatches(topic, returnedTopic) }
            .map { it.second }


    private fun topicMatches(pattern: String, actual: String): Boolean {
        if (!pattern.contains("+")) return pattern == actual
        val prefix = pattern.substringBefore("+")
        return actual.startsWith(prefix)
    }
}