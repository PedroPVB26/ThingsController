package com.pedro.thingscontroller.data.datasource.impl

import android.util.Log
import com.google.gson.Gson
import com.pedro.thingscontroller.data.datasource.MqttDataSource
import com.pedro.thingscontroller.data.datasource.impl.retrofit.ThingsApi
import com.pedro.thingscontroller.data.model.dto.ThingPresence
import com.pedro.thingscontroller.data.model.dto.shadow.ShadowResponse
import com.pedro.thingscontroller.data.model.mapper.toDomain
import com.pedro.thingscontroller.di.modules.ApplicationScope
import com.pedro.thingscontroller.domain.model.ThingException
import com.pedro.thingscontroller.domain.model.command.ThingCommand
import com.pedro.thingscontroller.domain.model.component.Component
import com.pedro.thingscontroller.domain.model.component.instance.ComponentInstanceStateUpdate
import com.pedro.thingscontroller.domain.model.thing.Thing
import com.pedro.thingscontroller.domain.model.thing.ThingStateStatus
import com.pedro.thingscontroller.domain.repository.ThingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class ThingRepositoryImpl @Inject constructor(
    private val thingsApi: ThingsApi,
    private val mqttDataSource: MqttDataSource,
    @ApplicationScope private val appScope: CoroutineScope,
    private val gson: Gson
): ThingRepository {
    private val TAG = "ThingRepositoryImpl"


    private val _allThings = MutableStateFlow<Map<String, Thing>>(emptyMap())
    override val allThings = _allThings.asStateFlow()

    private val _allThingsComponents = MutableStateFlow<Map<String, List<Component>>>(emptyMap())
    override val allThingsComponents: StateFlow<Map<String, List<Component>>> = _allThingsComponents

    override fun observeThing(thingId: String): Flow<Thing?> = _allThings.map { it[thingId] }

    override suspend fun initialize() {
        // Fetching all things from the API
        val response = thingsApi.getAllThings()
        if(!response.isSuccessful || response.body() == null){
            throw ThingException.Unknown(
                Throwable("Failed to fetch things: HTTP ${response.code()}")
            )
        }

        // Seed in-meory state
        val things = response.body()!!.things
        _allThings.value = things.associateBy { it.thingName }

        // Connect to MQTT
        mqttDataSource.connect()

        // Subscribe to presence and shadow topics for each thing
        observePresence()
        things.forEach { thing ->
            mqttDataSource.subscribe("\$aws/events/presence/+/${thing.thingName}")
            mqttDataSource.subscribe("\$aws/things/${thing.thingName}/shadow/update/accepted")
        }

        // Start observing presence and shadow updates
        things.forEach { thing -> observeShadow(thing.thingName) }
    }

    override suspend fun sendCommand(
        thingId: String,
        thingCommand: ThingCommand
    ) {
        val thing = _allThings.value[thingId]
            ?: throw ThingException.ThingDoesNotExistException(thingId)

        if (!thing.available)
            throw ThingException.ThingNotAvailableException(thingId)

        val requestId = UUID.randomUUID().toString()
        thingCommand.requestId = requestId

        try {
            val response = thingsApi.postCommand(thingId, thingCommand)
            if (!response.isSuccessful) {
                throw ThingException.Unknown(
                    Throwable("Failed to send command: HTTP ${response.code()}")
                )
            }
        } catch (e: ThingException) {
            throw e
        } catch (e: Exception) {
            throw ThingException.Unknown(e)
        }
    }

    override fun markComponentPending(
        thingId: String,
        componentId: String,
        requestId: String?
    ) {
        val currentComponents = _allThingsComponents.value[thingId] ?: return
        val updatedComponents = currentComponents.map { component ->
            val updatedInstances = component.instances.map { instance ->
                if (instance.componentId == componentId) {
                    instance.withPendingRequest(requestId)
                }else instance
            }
            component.copy(instances = updatedInstances)
        }
        _allThingsComponents.update { it + (thingId to updatedComponents) }
    }

    // ─── Private observers ────────────────────────────────────────────────────
    private fun observePresence(){
        appScope.launch {
            mqttDataSource.observeTopic("\$aws/events/presence/+/+")
                .collect { message ->
                    try {
                        val presence = gson.fromJson(message, ThingPresence::class.java)
                        val isConnected = presence.eventType.lowercase() == "connected"
                        updateThingPresence(presence.clientId, isConnected, presence.timestamp)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing presence message", e)
                    }
                }
        }
    }
    private fun observeShadow(thingId: String){
        appScope.launch {
            mqttDataSource.observeTopic("\$aws/things/$thingId/shadow/update/accepted")
                .collect { message ->
                    val shadow = gson.fromJson(message, ShadowResponse::class.java)
                    val update = shadow.toDomain()
                    if (update != null){
                        updateComponentState(thingId, update)
                    }
                }
        }
    }

    // ─── State updaters ───────────────────────────────────────────────────────
    private fun updateThingPresence(thingId: String, isConnected: Boolean, timestamp: Long) {
        _allThings.update { current ->
            val thing = current[thingId] ?: return
            val updated = thing.copy(
                available = thing.available,
                connection = thing.connection.copy(
                    status = if (isConnected) ThingStateStatus.CONNECTED else ThingStateStatus.DISCONNECTED,
                    timestamp = timestamp
                )
            )
            current + (thingId to updated)
        }
    }

    private fun updateComponentState(thingId: String, update: ComponentInstanceStateUpdate){
        val currentComponents = _allThingsComponents.value[thingId] ?: return
        val updatedComponents = currentComponents.map { component ->
            if(component.type != update.componentType) component else {
                val updatedInstances = component.instances.map { instance ->
                    if (instance.componentId != update.componentId) instance
                    else instance.updateState(update.state, update.updatedAt, update.requestId)
                }
                component.copy(instances = updatedInstances)
            }
        }
        _allThingsComponents.update { it + (thingId to updatedComponents) }
    }
}