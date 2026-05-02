package com.pedro.thingscontroller.domain.model.component.instance

import com.pedro.thingscontroller.domain.model.component.ComponentState
import com.pedro.thingscontroller.domain.model.component.ComponentType

/**
 * Represents a runtime instance of a component within a Thing.
 *
 * A component instance contains its current state, availability, and
 * metadata related to updates and pending operations.
 *
 * @property componentType Type of the component (e.g., LED, BUZZER).
 *
 * @property available Indicates whether the component is able to receive
 * and execute commands at the current moment.
 *
 * @property state Current state of the component. The concrete type of
 * [ComponentState] depends on the component type.
 *
 * @property updatedAt Unix timestamp (in milliseconds) indicating when
 * the component state was last updated.
 *
 * @property pendingRequestId Optional identifier of a command that has been
 * sent but not yet confirmed by the device. Used for tracking in-flight operations.
 */
sealed class ComponentInstance<T: ComponentState>{
    abstract val componentId: String
    abstract val componentType: ComponentType
    abstract val available: Boolean
    abstract val state: T
    abstract val updatedAt: Long
    abstract val pendingRequestId: String?

    // Mover as implementações para cá!!!
    // Cada filha faz literalmente a mesma coisa com essa função
    abstract fun withPendingRequest(requestId: String?): ComponentInstance<T>

    abstract fun updateState(
        newState: T,
        updatedAt: Long,
        requestId: String?
    ): ComponentInstance<T>
}