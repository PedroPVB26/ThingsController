package com.pedro.thingscontroller.domain.model.component.instance

import com.pedro.thingscontroller.domain.model.component.ComponentState
import com.pedro.thingscontroller.domain.model.component.ComponentType

/**
 * Represents a runtime instance of a buzzer component.
 *
 * Specializes [ComponentInstance] for buzzer-specific behavior and state.
 */
data class BuzzerInstance(
    override val componentId: String,
    override val componentType: ComponentType = ComponentType.BUZZER,
    override val available: Boolean,
    override val state: ComponentState.BuzzerState,
    override val updatedAt: Long,
    override val pendingRequestId: String?
): ComponentInstance<ComponentState.BuzzerState>() {
    override fun withPendingRequest(requestId: String?) = copy(pendingRequestId = requestId)

    override fun updateState(
        newState: ComponentState.BuzzerState,
        updatedAt: Long,
        requestId: String?
    ) = copy(
        state = newState,
        updatedAt = updatedAt,
        pendingRequestId = if (requestId == null || pendingRequestId == requestId) null else pendingRequestId
    )
}
