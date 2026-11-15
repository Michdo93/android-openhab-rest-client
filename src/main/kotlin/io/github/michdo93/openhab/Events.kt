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
class Events(private val client: OpenHABClient) {

    /**
     * Get all available events, optionally filtered by topic.
     *
     * @param topics A comma-separated list of topics to filter the events by
     *                (e.g. "openhab/items/ * /statechanged,openhab/things/ * /statuschanged").
     *                The wildcard * also matches slashes.
     *
     * @return A SSE stream of events (requests.Response) or {"error": ...}.
     */
    suspend fun getEvents(
        topics: String? = null,
    ): SSEConnection = withContext(Dispatchers.IO) {
        getEventsBlocking(topics)
    }

    private fun getEventsBlocking(topics: String? = null): SSEConnection {
        return Py.openStream(client, topics, null, null, Py.dictAny(400, "Topic is empty or contains invalid characters."))
    }

    /**
     * Initiates a new item state tracker connection.
     *
     * The first event of the stream carries the connection ID, which is needed for
     * updateSSEConnectionItems().
     *
     * @return A SSE stream (requests.Response) or {"error": ...}.
     */
    suspend fun initiateStateTracker(
    ): SSEConnection = withContext(Dispatchers.IO) {
        initiateStateTrackerBlocking()
    }

    private fun initiateStateTrackerBlocking(): SSEConnection {
        return Py.openStream(client, null, "/rest/events/states", Py.dict("Accept", "*/*"), null)
    }

    /**
     * Changes the list of items a SSE connection will receive state updates for.
     *
     * @param connectionID The ID of the existing connection.
     * @param items A list of item names to subscribe to for state updates.
     *
     * @return A success message when the update is completed.
     */
    suspend fun updateSSEConnectionItems(
        connectionID: String,
        items: List<*>,
    ): Any? = withContext(Dispatchers.IO) {
        updateSSEConnectionItemsBlocking(connectionID, items)
    }

    private fun updateSSEConnectionItemsBlocking(connectionID: String, items: List<*>): Any? {
        var response: Any? = null
        var statusCode: Any? = null
        try {
            response = client.post(("/events/states/" + Py.str(connectionID)), Py.m(Py.dict("Content-Type", "application/json")), Py.dumps(items), null)
            if (Py.isStatus(response)) {
                statusCode = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            statusCode = err.status
            if (Py.eq(statusCode, 404)) {
                return Py.dict("error", "Unknown connectionID.")
            }
            return Py.dict("error", ("HTTP error " + Py.str(statusCode) + ": " + Py.str(Py.str(err))))
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(statusCode, 200)) {
            return Py.dict("message", "OK")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(statusCode)))
    }
}
