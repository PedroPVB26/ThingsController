package com.pedro.thingscontroller.domain.model.component.instance

import com.pedro.thingscontroller.domain.model.component.ComponentState
import com.pedro.thingscontroller.domain.model.component.ComponentType

/**
 * Represents a runtime instance of a LED component.
 *
 * Specializes [ComponentInstance] for LED-specific behavior and state.
 */
data class LedInstance(
    override val componentId: String,
    override val componentType: ComponentType = ComponentType.LED,
    override val available: Boolean,
    override val state: ComponentState.LedState,
    override val updatedAt: Long,
    override val pendingRequestId: String?
): ComponentInstance<ComponentState.LedState>() {
    override fun withPendingRequest(requestId: String?) = copy(pendingRequestId = requestId)

    override fun updateState(
        newState: ComponentState.LedState,
        updatedAt: Long,
        requestId: String?
    ) = copy(
        state = newState,
        updatedAt = updatedAt,
        pendingRequestId = if (requestId == null || pendingRequestId == requestId) null else pendingRequestId
    )
}
