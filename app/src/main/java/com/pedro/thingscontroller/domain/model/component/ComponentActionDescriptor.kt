package com.pedro.thingscontroller.domain.model.component

/**
 * Defines the set of available actions for a given component type,
 * without including any execution-specific parameters.
 *
 * This abstraction is used to expose, in a type-safe way, which actions
 * a component supports. It should be consumed by the UI and use cases
 * to guide user interaction.
 *
 * Unlike [ComponentAction], this class does not represent an executable
 * action, but only the possible operations that can be performed.
 *
 * Typical flow:
 * - A mapper provides a list of descriptors based on the component type
 * - The UI displays the available actions
 * - The selected descriptor is later converted into a [ComponentAction]
 *
 * @see ComponentAction
 */
sealed interface ComponentActionDescriptor {
    // LED
    enum class Led : ComponentActionDescriptor {
        ON, OFF, BLINK
    }

    // BUZZER
    enum class Buzzer : ComponentActionDescriptor {
        ON, BEEP, OFF
    }
}