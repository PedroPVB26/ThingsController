package com.pedro.thingscontroller.domain.model.component

/**
 * Represents precision configurations for sensor-type components.
 *
 * Precision defines how raw sensor values should be interpreted or
 * presented, such as the number of decimal places or measurement granularity.
 *
 * This abstraction is part of the domain layer and should not depend
 * on how data is received from external sources.
 *
 * Each subclass represents the precision model for a specific type of sensor.
 */
sealed class ComponentPrecision{
    /**
     * Represents the precision configuration of a temperature and humidity sensor.
     *
     * @property temperaturePrecision Number of decimal places for temperature values.
     * @property humidityPrecision Number of decimal places for humidity values.
     */
    data class TemperatureUmiditySensorPrecision(
        val temperaturePrecision: Int,
        val humidityPrecision: Int
    ): ComponentPrecision()

    /**
     * Precision configuration for proximity sensors.
     *
     * This can represent sensors such as infrared (IR) reflective sensors
     * or distance-based proximity sensors.
     *
     * @property distancePrecision Number of decimal places used for distance measurements.
     * If the sensor is binary (object detected / not detected), this value can be zero.
     */
    data class ProximitySensorPrecision(
        val distancePrecision: Int
    ) : ComponentPrecision()
}

