package com.pedro.thingscontroller.domain.model.component.instance

import com.pedro.thingscontroller.domain.model.component.ComponentState
import com.pedro.thingscontroller.domain.model.component.ComponentType


/**
 * Domain event representing a state change of a specific component instance.
 *
 * This event is typically produced from external data sources (e.g., AWS IoT Shadow updates)
 * and encapsulates the minimal information required to update the current state of a
 * [ComponentInstance] within the system.
 *
 * It does not represent the full state of a component, but rather a single transition
 * that should be applied to the existing state.
 *
 * @property type Type of the component being updated (e.g., LED, SERVO, SENSOR).
 * @property componentId Unique identifier of the component instance within the Thing.
 * @property state New state to be applied to the component. The concrete implementation
 * of [ComponentState] depends on the component type.
 * @property updatedAt Unix timestamp (in milliseconds) indicating when the state change occurred.
 * @property requestId Optional identifier used to correlate this update with a previously
 * issued command. Useful for resolving pending operations and avoiding stale updates.
 */
data class ComponentInstanceStateUpdate(
    val componentType: ComponentType,
    val componentId: String,
    val state: ComponentState,
    val updatedAt: Long,
    val requestId: String?
)
