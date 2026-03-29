package com.pedro.thingscontroller.domain.model

/**
 * Represents an action that can be performed by a hardware component
 * susch as Led, Buzzer, Sensor, etc.
 *
 * This interface is used to standardize commands sent to the thing.
 */
sealed interface ComponentAction {
    /**
     * Represents actions that can be performed on a LED component.
     *
     * - [ON]: Turns the LED on continuously.
     * - [OFF]: Turns the LED off.
     * - [BLINK]: Makes the LED blink at a predefined interval.
     *
     * The blink interval is defined on the thing side.
     */
    enum class LedAction: ComponentAction{
        ON, OFF, BLINK
    }

    /**
     * Represents actions that can be executed by a buzzer component.
     *
     * - [On]: Plays a continuous sound. Optionally accepts a frequency in Hz.
     * - [Beep]: Plays an intermittent sound (beeping). Allows configuring
     *   frequency and interval between beeps.
     *
     * If frequency is not provided, a default value will be used by the device.
     */
    sealed interface BuzzerAction: ComponentAction{
        /**
         * Plays a continuous sound.
         *
         * @property frequency Frequency in Hertz (Hz). Optional.
         */
        data class ON(
            val frequency: Int? = null
        ): BuzzerAction

        /**
         * Plays a beeping sound (on/off repeatedly).
         *
         * @property frequency Frequency in Hertz (Hz). Optional.
         */
        data class BEEP(
            val frequency: Int? = null
        ): BuzzerAction

        /**
         * Stops any sound currently being played.
         */
        object OFF : BuzzerAction
    }
}