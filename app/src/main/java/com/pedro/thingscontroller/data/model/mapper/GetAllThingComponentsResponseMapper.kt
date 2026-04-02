package com.pedro.thingscontroller.data.model.mapper

import com.google.gson.JsonElement
import com.pedro.thingscontroller.data.model.dto.ComponentInstanceResponse
import com.pedro.thingscontroller.data.model.dto.ComponentResponse
import com.pedro.thingscontroller.data.model.dto.GetThingComponentsResponse
import com.pedro.thingscontroller.domain.model.component.Component
import com.pedro.thingscontroller.domain.model.component.ComponentActionDescriptor
import com.pedro.thingscontroller.domain.model.component.ComponentPrecision
import com.pedro.thingscontroller.domain.model.component.ComponentState
import com.pedro.thingscontroller.domain.model.component.ComponentType
import com.pedro.thingscontroller.domain.model.component.instance.ComponentInstance
import com.pedro.thingscontroller.domain.model.component.instance.LedInstance
import com.pedro.thingscontroller.domain.model.component.instance.TemperatureUmidityInstance

/**
 * Maps the API response [GetThingComponentsResponse] into domain models [Component].
 *
 * - Converts raw String types into [ComponentType]
 * - Maps raw actions into [ComponentActionDescriptor]
 * - Transforms each instance into a typed [ComponentInstance]
 */
fun GetThingComponentsResponse.toDomain(): List<Component>{
    return components.map { (componentTypeString: String, componentResponse: ComponentResponse) ->
        val componentType = try{
            ComponentType.valueOf(componentTypeString)
        }catch (e: Exception){
            ComponentType.UNKNOWN
        }

        val actions: List<ComponentActionDescriptor> = when(componentType){
            ComponentType.LED -> ComponentActionDescriptor.Led.entries
            ComponentType.BUZZER -> ComponentActionDescriptor.Buzzer.entries
            else -> emptyList()
        }

        val instances: List<ComponentInstance> = componentResponse.instances.map { (instanceId: String, instanceResponse: ComponentInstanceResponse) ->
            val state = parseState(componentType, instanceResponse.state)
            val updatedAt = instanceResponse.updatedAt!!
            val precision = parsePrecision(componentType, instanceResponse)

            when (componentType) {
                ComponentType.LED -> {
                    LedInstance(
                        componentId = instanceId,
                        componentType = ComponentType.LED,
                        state = state,
                        updatedAt = updatedAt,
                        available = instanceResponse.available,
                        pendingRequestId = null
                    )
                }
                ComponentType.TEMPERATURE_UMIDITY_SENSOR -> {
                    TemperatureUmidityInstance(
                        componentId = instanceId,
                        componentType = ComponentType.TEMPERATURE_UMIDITY_SENSOR,
                        state = state,
                        updatedAt = updatedAt,
                        available = instanceResponse.available,
                        precision = precision as? ComponentPrecision.TemperatureUmiditySensorPrecision,
                        pendingRequestId = null
                    )
                }
                else -> {
                    throw IllegalArgumentException("Unsupported type: $componentType")
                }
            }
        }



        Component(
            type = componentType,
            actions = actions,
            instances = instances
        )
    }
}

/**
 * Parses and maps raw precision data into a typed [ComponentPrecision].
 *
 * @param componentType
 * Used to determine how the precision should be interpreted.
 *
 * @param instanceResponse
 * Source of raw precision data (JsonElement).
 *
 * @return
 * A specific [ComponentPrecision] implementation or null if not applicable.
 */
fun parsePrecision(
    componentType: ComponentType,
    instanceResponse: ComponentInstanceResponse
): ComponentPrecision? {

    val precisionElement = instanceResponse.precision ?: return null

    if (!precisionElement.isJsonObject) return null

    val obj = precisionElement.asJsonObject

    return when (componentType) {

        ComponentType.TEMPERATURE_UMIDITY_SENSOR -> {
            ComponentPrecision.TemperatureUmiditySensorPrecision(
                temperaturePrecision = obj.get("temperature")?.asInt ?: 0,
                humidityPrecision = obj.get("humidity")?.asInt ?: 0
            )
        }

        // Sensor de proximidade, ultrassonico....

        else -> null
    }
}

/**
 * Parses and maps raw state data into a typed [ComponentState].
 *
 * @param componentType
 * Defines how the state should be interpreted.
 *
 * @param componentStateJson
 * Raw state value from API (can be primitive or object).
 *
 * @return
 * A specific [ComponentState] implementation according to the component type.
 *
 * Examples:
 * - LED → "ON", "OFF"
 * - SENSOR → { temperature, humidity, updatedAt }
 */
fun parseState(componentType: ComponentType, componentStateJson: JsonElement): ComponentState{
    return when(componentType){
        ComponentType.LED -> {
            val stateString = componentStateJson.asString
            ComponentState.LedState.valueOf(stateString)
        }

        ComponentType.TEMPERATURE_UMIDITY_SENSOR -> {
            if(componentStateJson.isJsonObject){
                val obj = componentStateJson.asJsonObject
                ComponentState.TemperatureHumidityState(
                    temperature = obj.get("temperature").asDouble,
                    humidity = obj.get("humidity").asDouble,
                    updatedAt = obj.get("updatedAt").asLong
                )
            }else{
                ComponentState.TemperatureHumidityState(0.0, 0.0, 0L)
            }
        }

        ComponentType.BUZZER -> {
            val stateString = componentStateJson.asString
            ComponentState.BuzzerState.valueOf(stateString)
        }

        else -> throw IllegalArgumentException("Unsupported component type: $componentType")
    }
}