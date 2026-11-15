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
class InboxEvents(private val client: OpenHABClient) {

    /**
     * Get events for added things in the inbox.
     *
     * @param thingUID The UID of the thing (default is "*").
     *
     * @return A SSE stream of added inbox events.
     */
    suspend fun InboxAddedEvent(
        thingUID: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        InboxAddedEventBlocking(thingUID)
    }

    private fun InboxAddedEventBlocking(thingUID: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/inbox/" + Py.str(thingUID) + "/added"), null, null, null)
    }

    /**
     * Get events for removed things in the inbox.
     *
     * @param thingUID The UID of the thing (default is "*").
     *
     * @return A SSE stream of removed inbox events.
     */
    suspend fun InboxRemovedEvent(
        thingUID: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        InboxRemovedEventBlocking(thingUID)
    }

    private fun InboxRemovedEventBlocking(thingUID: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/inbox/" + Py.str(thingUID) + "/removed"), null, null, null)
    }

    /**
     * Get events for updated things in the inbox.
     *
     * @param thingUID The UID of the thing (default is "*").
     *
     * @return A SSE stream of updated inbox events.
     */
    suspend fun InboxUpdatedEvent(
        thingUID: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        InboxUpdatedEventBlocking(thingUID)
    }

    private fun InboxUpdatedEventBlocking(thingUID: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/inbox/" + Py.str(thingUID) + "/updated"), null, null, null)
    }
}
