package com.pedro.thingscontroller.data.model.dto.shadow

import com.google.gson.JsonElement

/**
 * Represents the reported state section of an AWS IoT Shadow document.
 *
 * In AWS IoT, the shadow's reported state reflects the actual current state
 * of the device as last reported by the Thing itself.
 *
 * [reported] follows a two-level structure:
 * - The outer key is the **component type** (e.g. `"LED"`, `"TEMPERATURE_UMIDITY_SENSOR"`)
 * - The inner key is the **component instance identifier** (e.g. `"greenLed"`, `"enviroment1"`)
 * - The value is a [JsonElement] representing that component's current properties,
 *   which may include `state`, `updatedAt`, and optionally `requestId`
 *
 * Example for a LED component:
 * ```json
 * {
 *   "reported": {
 *     "LED": {
 *       "greenLed": {
 *         "state": "BLINKING",
 *         "updatedAt": 1774928189,
 *         "requestId": "46f39c32-6a9f-41e8-9d99-51160a42b343"
 *       }
 *     }
 *   }
 * }
 * ```
 *
 * Example for a temperature/humidity sensor component:
 * ```json
 * {
 *   "reported": {
 *     "TEMPERATURE_UMIDITY_SENSOR": {
 *       "enviroment1": {
 *         "state": {
 *           "temperature": 29,
 *           "humidity": 58,
 *           "updatedAt": 1774928118
 *         }
 *       }
 *     }
 *   }
 * }
 * ```
 *
 * Note that [JsonElement] is used as the value type because component properties
 * are heterogeneous — a LED state is a plain string (`"BLINKING"`) while a
 * sensor state is a nested object with multiple readings.
 *
 * @property reported A two-level map of component type → component id → properties.
 * Null if the shadow document contains no reported state.
 */
data class ShadowReportedState(
    val reported: Map<String, Map<String, JsonElement>>?
)