package com.pedro.thingscontroller.domain.repository

import com.pedro.thingscontroller.domain.model.command.ThingCommand
import com.pedro.thingscontroller.domain.model.ThingException
import com.pedro.thingscontroller.domain.model.component.Component
import com.pedro.thingscontroller.domain.model.thing.Thing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

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
     * A reactive stream that emits the current state of all available Things,
     * keyed by their unique identifier.
     *
     * Using a [Map] allows O(1) lookup by [Thing.thingName] without iterating the
     * entire collection. This flow updates whenever any Thing changes state,
     * such as connection status or component updates.
     */
    val allThings: Flow<Map<String, Thing>>

    /**
     * A reactive stream that emits the current component state for all Things,
     * keyed by Thing identifier.
     *
     * Each entry maps a Thing's identifier to its list of [Component],
     * where each component holds the current state of all its instances,
     * including any pending request awaiting confirmation from the device.
     *
     * This flow updates whenever a shadow update accepted message arrives
     * for any subscribed Thing, or when [markComponentPending] is called.
     *
     * @see markComponentPending
     */
    val allThingsComponents: StateFlow<Map<String, List<Component>>>

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

    /**
     * Marks a component instance as pending, indicating a command has been
     * sent and the UI should show a loading state while awaiting confirmation.
     *
     * The pending state is cleared automatically when a shadow update accepted
     * message arrives containing a matching [requestId].
     *
     * @param thingId Identifier of the Thing.
     * @param componentId Identifier of the component instance.
     * @param requestId The request identifier sent with the command, used to
     * match the incoming shadow update that clears the pending state.
     */
    fun markComponentPending(thingId: String, componentId: String, requestId: String?)
}