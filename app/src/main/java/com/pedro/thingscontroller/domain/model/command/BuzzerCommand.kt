package com.pedro.thingscontroller.domain.model.command

import com.pedro.thingscontroller.domain.model.component.ComponentAction
import com.pedro.thingscontroller.domain.model.component.ComponentType

/**
 * Command used to control a BUZZER component of a Thing.
 *
 * This command specializes [ThingCommand] by restricting the action
 * to [ComponentAction.BuzzerAction], ensuring only valid BUZZER operations
 * (e.g., ON, OFF, BEEP) can be executed.
 */
data class BuzzerCommand(
    override val requestId: String?,
    override val componentId: String,
    override val componentCommand: ComponentType = ComponentType.BUZZER,
    override val action: ComponentAction.BuzzerAction

): ThingCommand()
