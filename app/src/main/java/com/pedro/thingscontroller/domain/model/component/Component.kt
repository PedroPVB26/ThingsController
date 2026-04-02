package com.pedro.thingscontroller.domain.model.component

import com.pedro.thingscontroller.domain.model.component.instance.ComponentInstance
import com.pedro.thingscontroller.domain.model.component.instance.*

/**
 * Represents a component of a Thing, grouping its type, supported actions,
 * and concrete instances (devices).
 *
 * Based on the API response:
 * - A "component" (e.g., LED) contains available actions and multiple instances
 *   (e.g., "greenLed", "yellowLed").
 *
 * @property type Identifies the component type (e.g., LED, TEMPERATURE_UMIDITY_SENSOR).
 *
 * @property actions List of actions that this component type can perform. Example: (e.g. LedActions -> ON, OFF, BLINK)
 *
 * @property instances
 * List of actual instances of this component type.
 * Each instance represents a specific target (e.g., "greenLed") where actions
 * can be applied and from which state is retrieved.
 *
 * @see LedInstance
 * @see BuzzerInstance
 * @see TemperatureUmidityInstance
 */
data class Component(
    val type: ComponentType,
    val actions: List<ComponentActionDescriptor>,
    val instances: List<ComponentInstance>
)
