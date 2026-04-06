package com.pedro.thingscontroller.domain.model.command

import com.pedro.thingscontroller.domain.model.component.ComponentAction
import com.pedro.thingscontroller.domain.model.component.ComponentType

/**
 * Represents a command to be sent to a Thing (IoT).
 *
 * A command targets a specific component of the thing and defines
 * which action should be executed.
 *
 * @property requestId Identifier used to track the command request.
 * Used for correlation between client and thing responses.
 * Generated in the repository implementation
 *
 * @property componentId Unique identifier of the target component inside the Thing
 * (e.g., "redLed", "greenLed", "environment1").
 *
 * @property componentType Type of the component that will receive the command
 * (e.g., LED, BUZZER).
 *
 * @property action Action to be performed on the component. The action must be
 * compatible with the specified component type.
 */
sealed class ThingCommand {
    abstract var requestId: String?
    abstract val componentId: String
    abstract val componentType: ComponentType
    abstract val action: ComponentAction
}