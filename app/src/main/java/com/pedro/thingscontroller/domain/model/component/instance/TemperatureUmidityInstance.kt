package com.pedro.thingscontroller.domain.model.component.instance

import com.pedro.thingscontroller.domain.model.component.ComponentPrecision
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
    override val componentId: String,
    override val componentType: ComponentType = ComponentType.TEMPERATURE_UMIDITY_SENSOR,
    override val available: Boolean,
    override val state: ComponentState,
    override val updatedAt: Long,
    override val pendingRequestId: String?,
    val precision: ComponentPrecision.TemperatureUmiditySensorPrecision?
): ComponentInstance() {
    override fun withPendingRequest(requestId: String?): ComponentInstance  = copy(pendingRequestId = requestId)

    override fun updateState(
        newState: ComponentState,
        updatedAt: Long,
        requestId: String?
    ): ComponentInstance {
        val shouldClearPending = requestId == null || pendingRequestId == requestId

        return copy(
            state = newState,
            updatedAt = updatedAt,
            pendingRequestId = if (shouldClearPending) null else pendingRequestId
        )
    }
}
