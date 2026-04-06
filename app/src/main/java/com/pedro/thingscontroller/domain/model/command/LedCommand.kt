package com.pedro.thingscontroller.domain.model.command

import com.pedro.thingscontroller.domain.model.component.ComponentAction
import com.pedro.thingscontroller.domain.model.component.ComponentType

/**
 * Command used to control a LED component of a Thing.
 *
 * This command specializes [ThingCommand] by restricting the action
 * to [ComponentAction.LedAction], ensuring only valid LED operations
 * (e.g., ON, OFF, BLINK) can be executed.
 */
data class LedCommand(
    override var requestId: String? = null,
    override val componentId: String,
    override val componentType: ComponentType = ComponentType.LED,
    override val action: ComponentAction.LedAction,

    ): ThingCommand()
