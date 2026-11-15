// Generated from Events.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
@file:Suppress("UNUSED_VARIABLE", "UNUSED_VALUE", "UNCHECKED_CAST", "RedundantSuppression", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "UNREACHABLE_CODE", "USELESS_CAST", "NAME_SHADOWING", "FunctionName", "LocalVariableName", "CanBeVal", "VARIABLE_WITH_REDUNDANT_INITIALIZER")

package io.github.michdo93.openhab

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * All public methods are `suspend` functions; they run the request on
 * [Dispatchers.IO] and may therefore be called from the main thread.
 *
 * @param client the [OpenHABClient] used for REST-API communication
 */
class ThingEvents(private val client: OpenHABClient) {

    /**
     * Get events for added things.
     *
     * @param thingUID The UID of the thing (default is "*").
     *
     * @return A SSE stream of added thing events.
     */
    suspend fun ThingAddedEvent(
        thingUID: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ThingAddedEventBlocking(thingUID)
    }

    private fun ThingAddedEventBlocking(thingUID: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/things/" + Py.str(thingUID) + "/added"), null, null, null)
    }

    /**
     * Get events for removed things.
     *
     * @param thingUID The UID of the thing (default is "*").
     *
     * @return A SSE stream of removed thing events.
     */
    suspend fun ThingRemovedEvent(
        thingUID: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ThingRemovedEventBlocking(thingUID)
    }

    private fun ThingRemovedEventBlocking(thingUID: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/things/" + Py.str(thingUID) + "/removed"), null, null, null)
    }

    /**
     * Get events for updated things.
     *
     * @param thingUID The UID of the thing (default is "*").
     *
     * @return A SSE stream of updated thing events.
     */
    suspend fun ThingUpdatedEvent(
        thingUID: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ThingUpdatedEventBlocking(thingUID)
    }

    private fun ThingUpdatedEventBlocking(thingUID: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/things/" + Py.str(thingUID) + "/updated"), null, null, null)
    }

    /**
     * Get events for thing status information.
     *
     * @param thingUID The UID of the thing (default is "*").
     *
     * @return A SSE stream of thing status information events.
     */
    suspend fun ThingStatusInfoEvent(
        thingUID: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ThingStatusInfoEventBlocking(thingUID)
    }

    private fun ThingStatusInfoEventBlocking(thingUID: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/things/" + Py.str(thingUID) + "/status"), null, null, null)
    }

    /**
     * Get events for thing status information changes.
     *
     * @param thingUID The UID of the thing (default is "*").
     *
     * @return A SSE stream of thing status information changed events.
     */
    suspend fun ThingStatusInfoChangedEvent(
        thingUID: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ThingStatusInfoChangedEventBlocking(thingUID)
    }

    private fun ThingStatusInfoChangedEventBlocking(thingUID: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/things/" + Py.str(thingUID) + "/statuschanged"), null, null, null)
    }
}
