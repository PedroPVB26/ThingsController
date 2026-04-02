package com.pedro.thingscontroller.data.model.dto

import com.google.gson.JsonElement
import com.pedro.thingscontroller.data.datasource.impl.retrofit.ThingsApi

/**
 * Raw response representing all components of a Thing as returned by the api method [ThingsApi.postCommand].
 *
 * @property thingName Unique name of the Thing.
 *
 * @property components
 * Map where:
 * - key → component type as String (e.g., "LED", "TEMPERATURE_UMIDITY_SENSOR", "BUZZER")
 * - value → data related to that component type
 */
data class GetThingComponentsResponse(
    val thingName: String,
    //           ComponentType
    val components: Map<String, ComponentResponse>
)

/**
 * Raw response for a specific component type.
 *
 * @property actions
 * List of supported actions as raw strings.
 * May be null for components that do not support commands (e.g., sensors).
 *
 * @property instances
 * Map where:
 * - key → instance identifier (e.g., "greenLed", "environment1")
 * - value → instance data (e.g. "ON", "BLINKING", "BEEPING")
 */
data class ComponentResponse(
    val actions: List<String>? = null, // Sensores podem não ter actions
    //            ComponentId
    val instances: Map<String, ComponentInstanceResponse>
)

/**
 * Raw response for a specific component instance.
 *
 * @property available
 * Indicates whether the instance is currently reachable/active.
 *
 * @property state
 * Raw state of the instance.
 * Can be:
 * - a primitive (e.g., "OFF" for LED)
 * - an object (e.g., temperature/humidity data for sensors)
 *
 * @property type
 * Optional subtype of the component (e.g., "DHT11" for sensors).
 *
 * @property precision
 * Raw precision data (if applicable).
 * Stored as JsonElement because its structure depends on the component type
 * and must be parsed manually later.
 *
 * @property updatedAt
 * Timestamp (epoch) indicating when the state was last updated.
 * May be null depending on the component or API response.
 */
data class ComponentInstanceResponse(
    val available: Boolean,
    val state: JsonElement,
    val type: String? = null,

    /*
    Se eu não por esse campo, ele será perdido, porque quando recebo da api, ainda não sei exatamente qual o tipo de componente
    Eh do tipo JsonElement essa precisão pode ser de qualquer coisa -> Fazer o parsingManual
     */
    val precision: JsonElement? = null,
    val updatedAt: Long? = null
)
