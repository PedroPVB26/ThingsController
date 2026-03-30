package com.pedro.thingscontroller.domain.model.thing

/**
 * Represents the connection state of a Thing (IoT).
 *
 * @property status Current connection status of the thing.
 * Possible values:
 * - "connected": the thing is currently connected to the cloud.
 * - "disconnected": the thing is not connected to the cloud.
 *
 * @property timestamp Unix timestamp (in milliseconds) indicating when
 * the last status change occurred.
 */
data class ThingState(
    val status: ThingStateStatus,
    val timestamp: Long
)

enum class ThingStateStatus{
    CONNECTED, DISCONNECTED, UNKNOWN
}
