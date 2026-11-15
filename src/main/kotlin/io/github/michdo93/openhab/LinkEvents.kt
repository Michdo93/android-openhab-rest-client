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
class LinkEvents(private val client: OpenHABClient) {

    /**
     * Get events for added item-channel links.
     *
     * @param itemName The name of the item (default is "*").
     * @param channelUID The UID of the channel (default is "*").
     *
     * @return A SSE stream of added item-channel link events.
     */
    suspend fun ItemChannelLinkAddedEvent(
        itemName: String? = "*",
        channelUID: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ItemChannelLinkAddedEventBlocking(itemName, channelUID)
    }

    private fun ItemChannelLinkAddedEventBlocking(itemName: String? = "*", channelUID: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/links/" + Py.str(itemName) + "-" + Py.str(channelUID) + "/added"), null, null, null)
    }

    /**
     * Get events for removed item-channel links.
     *
     * @param itemName The name of the item (default is "*").
     * @param channelUID The UID of the channel (default is "*").
     *
     * @return A SSE stream of removed item-channel link events.
     */
    suspend fun ItemChannelLinkRemovedEvent(
        itemName: String? = "*",
        channelUID: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ItemChannelLinkRemovedEventBlocking(itemName, channelUID)
    }

    private fun ItemChannelLinkRemovedEventBlocking(itemName: String? = "*", channelUID: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/links/" + Py.str(itemName) + "-" + Py.str(channelUID) + "/removed"), null, null, null)
    }
}
