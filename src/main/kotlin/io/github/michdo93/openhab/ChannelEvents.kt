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
class ChannelEvents(private val client: OpenHABClient) {

    /**
     * Get events for changes in channel descriptions.
     *
     * @param channelUID The UID of the channel (default is "*").
     *
     * @return A SSE stream of channel description changed events.
     */
    suspend fun ChannelDescriptionChangedEvent(
        channelUID: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ChannelDescriptionChangedEventBlocking(channelUID)
    }

    private fun ChannelDescriptionChangedEventBlocking(channelUID: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/channels/" + Py.str(channelUID) + "/descriptionchanged"), null, null, null)
    }

    /**
     * Get events for triggered channels.
     *
     * @param channelUID The UID of the channel (default is "*").
     *
     * @return A SSE stream of channel triggered events.
     */
    suspend fun ChannelTriggeredEvent(
        channelUID: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ChannelTriggeredEventBlocking(channelUID)
    }

    private fun ChannelTriggeredEventBlocking(channelUID: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/channels/" + Py.str(channelUID) + "/triggered"), null, null, null)
    }
}
