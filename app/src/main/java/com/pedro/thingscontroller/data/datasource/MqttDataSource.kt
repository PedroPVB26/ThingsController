package com.pedro.thingscontroller.data.datasource

import com.pedro.thingscontroller.data.model.MqttConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MqttDataSource {
    val connectionState: StateFlow<MqttConnectionState>
    suspend fun connect()
    suspend fun disconnect()
    suspend fun subscribe(topic: String)
    suspend fun unsubscribe(topic: String)
    suspend fun publish(topic: String, payload: String)
    fun observeTopic(topic: String): Flow<String>
}