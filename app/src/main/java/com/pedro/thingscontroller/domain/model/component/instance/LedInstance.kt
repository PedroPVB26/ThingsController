package com.pedro.thingscontroller.domain.model.component.instance

import com.pedro.thingscontroller.domain.model.component.ComponentState
import com.pedro.thingscontroller.domain.model.component.ComponentType

/**
 * Represents a runtime instance of a LED component.
 *
 * Specializes [ComponentInstance] for LED-specific behavior and state.
 */
data class LedInstance(
    override val componentType: ComponentType = ComponentType.LED,
    override val available: Boolean,
    override val state: ComponentState,
    override val updatedAt: Long,
    override val pendingRequest: String?
): ComponentInstance()
