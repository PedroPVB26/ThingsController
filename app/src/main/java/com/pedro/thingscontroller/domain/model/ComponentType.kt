package com.pedro.thingscontroller.domain.model

/**
 * Represents the type of hardware component available in a device.
 *
 * Each component type defines what kind of actions can be performed
 * (e.g., turning on a LED or reading sensor data).
 */
enum class ComponentType {
    LED, TEMPERATURE_UMIDITY_SENSOR, BUZZER
}