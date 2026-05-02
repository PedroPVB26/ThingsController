package com.pedro.thingscontroller.data.model.mapper

import com.google.gson.JsonElement
import com.pedro.thingscontroller.data.model.dto.shadow.ShadowResponse
import com.pedro.thingscontroller.domain.model.component.ComponentState
import com.pedro.thingscontroller.domain.model.component.ComponentType
import com.pedro.thingscontroller.domain.model.component.instance.ComponentInstanceStateUpdate

/**
 * Maps a [ShadowResponse] received from an AWS IoT shadow update into a
 * [ComponentInstanceStateUpdate] domain model.
 *
 * Only the first component type and the first component instance found in the
 * shadow's reported state are processed, since AWS IoT shadow updates are
 * expected to carry a single component update per message.
 *
 * @receiver The [ShadowResponse] to be mapped.
 * @return A [ComponentInstanceStateUpdate] if the reported state contains a
 * recognized component type and valid data, or null if the state is absent,
 * empty, or the component type is unknown.
 */
fun ShadowResponse.toDomain(): ComponentInstanceStateUpdate?{
    val reported = state?.reported ?: return null

    // Get the componentType string and its instances
    val (typeString, instances) = reported.entries.firstOrNull() ?: return null

    // Get the instanceid and its state
    val (componentId, stateElement) = instances.entries.firstOrNull() ?: return null

    val componentType = typeString.toComponentType()

    return try{
        when(componentType){
            ComponentType.LED -> mapLed(componentType, componentId, stateElement)
            ComponentType.TEMPERATURE_UMIDITY_SENSOR -> mapTemperatureUmiditySensor(componentType, componentId, stateElement)
            ComponentType.BUZZER -> mapBuzzer(componentType, componentId, stateElement)
            else -> null
        }
    }catch (e: Exception) {
        null
    }
}

/**
 * Converts a raw string received from the AWS IoT shadow into a [ComponentType].
 *
 * @receiver The component type string as received in the shadow's reported state.
 * @return The matching [ComponentType], or [ComponentType.UNKNOWN] if the string
 * does not match any known type.
 */
private fun String.toComponentType(): ComponentType = when (this) {
    "LED" -> ComponentType.LED
    "TEMPERATURE_UMIDITY_SENSOR" -> ComponentType.TEMPERATURE_UMIDITY_SENSOR
    "BUZZER" -> ComponentType.BUZZER
    else -> ComponentType.UNKNOWN
}

/**
 * Maps a raw [JsonElement] into a [ComponentInstanceStateUpdate] for a LED component.
 *
 * Expected JSON structure:
 * ```json
 * {
 *   "state": "BLINKING",
 *   "updatedAt": 1774928189,
 *   "requestId": "46f39c32-6a9f-41e8-9d99-51160a42b343"
 * }
 * ```
 *
 * @param componentType The resolved [ComponentType] for this component.
 * @param componentId The unique identifier of the LED instance (e.g. `"greenLed"`).
 * @param stateElement The [JsonElement] containing the LED's reported properties.
 * @return A [ComponentInstanceStateUpdate] with a [ComponentState.LedState] state.
 */
private fun mapLed(componentType: ComponentType, componentId: String, stateElement: JsonElement): ComponentInstanceStateUpdate{
    val obj = stateElement.asJsonObject
    return ComponentInstanceStateUpdate(
        componentType = ComponentType.LED,
        componentId = componentId,
        state = ComponentState.LedState.valueOf(obj.get("state").asString),
        updatedAt = obj.get("updatedAt").asLong,
        requestId = obj.get("requestId")?.asString
    )
}

/**
 * Maps a raw [JsonElement] into a [ComponentInstanceStateUpdate] for a
 * temperature and humidity sensor component.
 *
 * Unlike other components, the sensor state is a nested object rather than
 * a plain string, containing individual sensor readings.
 *
 * Expected JSON structure:
 * ```json
 * {
 *   "state": {
 *     "temperature": 29,
 *     "humidity": 58,
 *     "updatedAt": 1774928118
 *   }
 * }
 * ```
 *
 * Note: Although sensors do not receive commands, [ComponentInstanceStateUpdate.requestId]
 * is still mapped when present, as the Thing itself may include an internal
 * request identifier (e.g. `"thingUpdateRequest"`) in its reported state.
 *
 * @param componentType The resolved [ComponentType] for this component.
 * @param componentId The unique identifier of the sensor instance (e.g. `"enviroment1"`).
 * @param stateElement The [JsonElement] containing the sensor's reported properties.
 * @return A [ComponentInstanceStateUpdate] with a [ComponentState.TemperatureHumidityState] state.
 */
private fun mapTemperatureUmiditySensor(componentType: ComponentType, componentId: String, stateElement: JsonElement): ComponentInstanceStateUpdate{
    val obj = stateElement.asJsonObject
    val sensorData = obj.get("state").asJsonObject
    val timestamp = sensorData.get("updatedAt").asLong
    return ComponentInstanceStateUpdate(
        componentType = ComponentType.TEMPERATURE_UMIDITY_SENSOR,
        componentId = componentId,
        state = ComponentState.TemperatureHumidityState(
            temperature = sensorData.get("temperature").asDouble,
            humidity = sensorData.get("humidity").asDouble,
            updatedAt = timestamp
        ),
        updatedAt = timestamp,
        // Even tho sensors may not receive a command (with a request id) it is better to store its requestId(coming from the Thing it, e.g. "thingUpdateRequest")
        requestId = obj.get("requestId")?.asString
    )
}

/**
 * Maps a raw [JsonElement] into a [ComponentInstanceStateUpdate] for a Buzzer component.
 *
 * Expected JSON structure:
 * ```json
 * {
 *   "state": "BEEPING",
 *   "updatedAt": 1774928189,
 *   "requestId": "46f39c32-6a9f-41e8-9d99-51160a42b343"
 * }
 * ```
 *
 * @param componentType The resolved [ComponentType] for this component.
 * @param componentId The unique identifier of the Buzzer instance.
 * @param stateElement The [JsonElement] containing the Buzzer's reported properties.
 * @return A [ComponentInstanceStateUpdate] with a [ComponentState.BuzzerState] state.
 */
private fun mapBuzzer(componentType: ComponentType, componentId: String, stateElement: JsonElement): ComponentInstanceStateUpdate{
    val obj = stateElement.asJsonObject
    return ComponentInstanceStateUpdate(
        componentType = ComponentType.BUZZER,
        componentId = componentId,
        state = ComponentState.BuzzerState.valueOf(obj.get("state").asString),
        updatedAt = obj.get("updatedAt").asLong,
        requestId = obj.get("requestId")?.asString
    )
}