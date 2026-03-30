package com.pedro.thingscontroller.domain.model.component

/**
 * Represents the current state of a component in a Thing.
 *
 * Each component type has its own specific state representation:
 *
 * - [LedState]: Describes whether the LED is on, off, or blinking.
 * - [BuzzerState]: Describes whether the buzzer is active, inactive, or beeping.
 * - [TemperatureHumidityState]: Contains sensor readings such as temperature
 *   and humidity, along with the timestamp of the measurement.
 *
 * This state is typically synchronized with the device (e.g., via AWS IoT shadow)
 * and reflects the latest known status of each component.
 */
sealed interface ComponentState {
    enum class LedState: ComponentState{
        ON, OFF, BLINKING
    }

    enum class BuzzerState: ComponentState{
        ON, OFF, BEEPING
    }

    /**
     * Represents the state of a temperature and humidity sensor.
     *
     * @property temperature Current temperature value (in Celsius).
     * @property humidity Current humidity percentage (0–100).
     * @property updatedAt Unix timestamp (in milliseconds) indicating when
     * the measurement was taken.
     */
    data class TemperatureHumidityState(
        val temperature: Double,
        val humidity: Double,
        val updatedAt: Long
    ): ComponentState
}

/**
 * Converts a [ComponentState.LedState] into its corresponding [ComponentAction.LedAction].
 *
 * Useful when the UI or system needs to trigger an action based on the current state.
 */
fun ComponentState.LedState.toAction(): ComponentAction{
    return when(this){
        ComponentState.LedState.ON -> ComponentAction.LedAction.ON
        ComponentState.LedState.OFF -> ComponentAction.LedAction.OFF
        ComponentState.LedState.BLINKING -> ComponentAction.LedAction.BLINK
    }
}

/**
 * Converts a [ComponentState.BuzzerState] into its corresponding [ComponentAction.BuzzerAction].
 *
 * Note: Default parameters are used for actions that support configuration
 * (e.g., frequency), since state does not carry those details.
 */
fun ComponentState.BuzzerState.toAction(): ComponentAction{
    return when(this){
        ComponentState.BuzzerState.ON -> ComponentAction.BuzzerAction.ON()
        ComponentState.BuzzerState.OFF -> ComponentAction.BuzzerAction.OFF
        ComponentState.BuzzerState.BEEPING -> ComponentAction.BuzzerAction.BEEP()
    }
}