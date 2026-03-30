package com.pedro.thingscontroller.domain.model

import com.pedro.thingscontroller.domain.model.component.ComponentAction

sealed class ThingException : Exception() {

    /**
     * Thrown when the requested Thing does not exist.
     *
     * @property thingId Identifier of the requested Thing.
     */
    data class ThingDoesNotExistException(
        val thingId: String
    ) : ThingException()

    /**
     * Thrown when the Thing exists but is not available to process commands.
     *
     * @property thingId Identifier of the Thing.
     */
    data class ThingNotAvailableException(
        val thingId: String
    ) : ThingException()

    /**
     * Thrown when the Thing is not connected to the cloud.
     *
     * @property thingId Identifier of the Thing.
     */
    data class ThingNotConnectedException(
        val thingId: String
    ) : ThingException()

    /**
     * Thrown when the requested component does not exist in the Thing.
     *
     * @property thingId Identifier of the Thing.
     * @property componentId Identifier of the component.
     */
    data class ComponentNotFoundException(
        val thingId: String,
        val componentId: String
    ) : ThingException()

    /**
     * Thrown when the component exists but is not available.
     *
     * @property thingId Identifier of the Thing.
     * @property componentId Identifier of the component.
     */
    data class ComponentNotAvailableException(
        val thingId: String,
        val componentId: String
    ) : ThingException()

    /**
     * Thrown when the component does not support the given action.
     *
     * @property thingId Identifier of the Thing.
     * @property componentId Identifier of the component.
     * @property action Action that was attempted.
     */
    data class UnsupportedComponentActionException(
        val thingId: String,
        val componentId: String,
        val action: ComponentAction
    ) : ThingException()
}