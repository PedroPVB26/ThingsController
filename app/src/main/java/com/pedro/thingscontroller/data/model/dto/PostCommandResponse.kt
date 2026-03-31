package com.pedro.thingscontroller.data.model.dto

import kotlinx.serialization.SerialName

/**
 * Data Transfer Object representing the response of a command sent to a device.
 *
 * This response contains metadata about the executed command, including the
 * targeted component, the action performed, and a unique request identifier
 * for tracking purposes.
 *
 * @property action The action that was requested to be executed on the device.
 * @property componentType The type of component that received the command
 * (mapped from "componentCommand" in the response payload).
 * @property componentId Unique identifier of the target component.
 * @property requestId Unique identifier of the request, used for tracing
 * and correlating command execution.
 *
 * @see [com.pedro.thingscontroller.domain.model.component.ComponentAction]
 */
data class PostCommandResponse(
    val action: String,
    @SerialName("componentCommand")
    val componentType: String,
    val componentId: String,
    val requestId: String
)