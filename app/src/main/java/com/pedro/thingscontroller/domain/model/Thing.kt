package com.pedro.thingscontroller.domain.model

/**
 * Represents an IoT device ("Thing") and its current state in the cloud.
 *
 * @property available Indicates whether the device is able to receive and execute commands.
 * This may be false even if the device is connected (e.g., temporarily unavailable or busy).
 * @property connection Current connection state of the thing (e.g., connected, disconnected).
 * @property thingId Unique identifier of the device in the cloud (e.g., esp32_A2F00F4134B1).
 * @property thingName User-friendly name assigned to the device.
 * @property type Thing model or category (e.g., ESP32, Arduino, Raspberry Pi).
 */
data class Thing(
    val available: Boolean,
    val connection: ThingState,
    val thingId: String,
    val thingName: String,
    val type: String,
)
