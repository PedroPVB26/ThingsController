package com.pedro.thingscontroller.domain.model.component.instance

import com.pedro.thingscontroller.domain.model.component.ComponentState
import com.pedro.thingscontroller.domain.model.component.ComponentType

/**
 * Represents a runtime instance of a buzzer component.
 *
 * Specializes [ComponentInstance] for buzzer-specific behavior and state.
 */
data class BuzzerInstance(
    override val componentType: ComponentType = ComponentType.BUZZER,
    override val available: Boolean,
    override val state: ComponentState,
    override val updatedAt: Long,
    override val pendingRequest: String?
): ComponentInstance()
