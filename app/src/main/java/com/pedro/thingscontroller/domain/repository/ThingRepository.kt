package com.pedro.thingscontroller.domain.repository

import com.pedro.thingscontroller.domain.model.command.ThingCommand
import com.pedro.thingscontroller.domain.model.ThingException
import com.pedro.thingscontroller.domain.model.thing.Thing
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface responsible for managing Things (IoT devices).
 *
 * This abstraction defines how the application interacts with Things,
 * including observing their state and sending commands.
 *
 * Implementations may use remote data sources (e.g., AWS IoT), local cache,
 * or a combination of both.
 */
interface ThingRepository {
    /**
     * A reactive stream that emits the current list of all available Things.
     *
     * This flow updates whenever there are changes in any Thing state,
     * such as connection status or component updates.
     */
    val allThings: Flow<Map<String, Thing>>

    /**
     * Observes a specific Thing by its identifier.
     *
     * @param thingId Unique identifier of the Thing.
     * @return A [Flow] that emits updates for the requested Thing,
     * or null if the Thing is not found.
     */
    fun observeThing(thingId: String): Flow<Thing?>


    /**
     * Initializes the repository by fetching all available Things from the
     * remote data source and setting up live MQTT subscriptions for each one.
     *
     * This function should be called once when the application starts.
     * Calling it more than once will re-fetch all Things and re-subscribe
     * to all topics, replacing any previously active subscriptions.
     *
     * The initialization process follows these steps:
     * 1. Fetches all Things from the remote data source (e.g., AWS IoT API).
     * 2. Seeds the in-memory state with the retrieved Things.
     * 3. Subscribes to the presence and shadow topics for each Thing,
     *    enabling live updates via [allThings].
     *
     * After this function completes, [allThings] will emit the initial
     * list and continue emitting updates as Thing states change.
     *
     */
    suspend fun initialize()

    /**
     * Sends a command to a specific Thing.
     *
     * @param thingId Unique identifier of the target Thing.
     * @param thingCommand Command to be executed by the Thing.
     *
     * @throws ThingException.ThingDoesNotExistException if no Thing with [thingId] exists.
     * @throws ThingException.ThingNotAvailableException if the Thing exists but cannot process commands.
     * @throws ThingException.ThingNotConnectedException if the Thing is not connected to the cloud.
     * @throws ThingException.ComponentNotFoundException if a targeted component does not exist.
     * @throws ThingException.ComponentNotAvailableException if a targeted component cannot process the action.
     * @throws ThingException.UnsupportedComponentActionException if the component does not support the given action.
     */
    suspend fun sendCommand(thingId: String, thingCommand: ThingCommand)
}