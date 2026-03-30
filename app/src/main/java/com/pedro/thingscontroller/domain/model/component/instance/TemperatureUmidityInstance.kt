package com.pedro.thingscontroller.domain.model.component.instance

import com.pedro.thingscontroller.domain.model.component.ComponentState
import com.pedro.thingscontroller.domain.model.component.ComponentType

/**
 * Represents a runtime instance of a temperature and humidity sensor.
 *
 * In addition to the base component properties, this class includes
 * precision metadata for sensor readings.
 *
 * @property precision Defines the measurement precision for temperature
 * and humidity values.
 */
data class TemperatureUmidityInstance(
    override val componentType: ComponentType,
    override val available: Boolean,
    override val state: ComponentState,
    override val updatedAt: Long,
    override val pendingRequest: String?,
    val precision: TemperatureUmiditySensorPrecision
): ComponentInstance()

/**
 * Represents the precision configuration of a temperature and humidity sensor.
 *
 * @property temperaturePrecision Number of decimal places for temperature values.
 * @property humidityPrecision Number of decimal places for humidity values.
 */
data class TemperatureUmiditySensorPrecision(
    val temperaturePrecision: Int,
    val humidityPrecision: Int
)
