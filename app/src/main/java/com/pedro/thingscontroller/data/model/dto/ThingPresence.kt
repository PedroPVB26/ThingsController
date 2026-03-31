package com.pedro.thingscontroller.data.model.dto

/**
 * DTO for AWS IoT presence events (aws/events/presence),
 * used to update device connection status in real time.
 *
 * @property clientId Thing id (e.g. esp32_A4F00F6783A8)
 * @property eventType Can either be "diconnected" or "connected"
 */
data class ThingPresence(
    val clientId: String,
    val eventType: String,
    val ipAddress: String,
    val principalIdentifier: String,
    val sessionIdentifier: String,
    val disconnectReason: String?,
    val timestamp: Long,
    val versionNumber: Int
)