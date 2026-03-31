package com.pedro.thingscontroller.data.model.dto.shadow

/**
 * Represents the root structure of an AWS IoT Shadow update message.
 *
 * Received when AWS IoT publishes a shadow update to the subscribed topic:
 * `$aws/things/{thingId}/shadow/update/accepted`
 *
 * The [state] field is parsed and used to update the in-memory component
 * states held by the repository, which in turn triggers live UI updates
 * through the active [Flow].
 *
 * @property state The reported state of the Thing at the time of the update.
 * Null if the shadow document is empty or malformed.
 *
 * @see ShadowReportedState
 */
data class ShadowResponse(
    val state: ShadowReportedState?
)