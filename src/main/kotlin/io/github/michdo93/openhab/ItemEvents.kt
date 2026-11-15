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
class ItemEvents(private val client: OpenHABClient) {

    /**
     * Get all item-related events.
     *
     * @return A SSE stream of item events.
     */
    suspend fun ItemEvent(
    ): SSEConnection = withContext(Dispatchers.IO) {
        ItemEventBlocking()
    }

    private fun ItemEventBlocking(): SSEConnection {
        return Py.openStream(client, "openhab/items/*", null, null, null)
    }

    /**
     * Get events for added items.
     *
     * @param itemName The name of the item (default is "*").
     *
     * @return A SSE stream of added item events.
     */
    suspend fun ItemAddedEvent(
        itemName: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ItemAddedEventBlocking(itemName)
    }

    private fun ItemAddedEventBlocking(itemName: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/items/" + Py.str(itemName) + "/added"), null, null, null)
    }

    /**
     * Get events for removed items.
     *
     * @param itemName The name of the item (default is "*").
     *
     * @return A SSE stream of removed item events.
     */
    suspend fun ItemRemovedEvent(
        itemName: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ItemRemovedEventBlocking(itemName)
    }

    private fun ItemRemovedEventBlocking(itemName: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/items/" + Py.str(itemName) + "/removed"), null, null, null)
    }

    /**
     * Get events for updated items.
     *
     * @param itemName The name of the item (default is "*").
     *
     * @return A SSE stream of updated item events.
     */
    suspend fun ItemUpdatedEvent(
        itemName: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ItemUpdatedEventBlocking(itemName)
    }

    private fun ItemUpdatedEventBlocking(itemName: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/items/" + Py.str(itemName) + "/updated"), null, null, null)
    }

    /**
     * Get events for item commands.
     *
     * @param itemName The name of the item (default is "*").
     *
     * @return A SSE stream of item command events.
     */
    suspend fun ItemCommandEvent(
        itemName: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ItemCommandEventBlocking(itemName)
    }

    private fun ItemCommandEventBlocking(itemName: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/items/" + Py.str(itemName) + "/command"), null, null, null)
    }

    /**
     * Get events for item state updates.
     *
     * @param itemName The name of the item (default is "*").
     *
     * @return A SSE stream of item state events.
     */
    suspend fun ItemStateEvent(
        itemName: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ItemStateEventBlocking(itemName)
    }

    private fun ItemStateEventBlocking(itemName: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/items/" + Py.str(itemName) + "/state"), null, null, null)
    }

    /**
     * Get events for predicted item state changes.
     *
     * @param itemName The name of the item (default is "*").
     *
     * @return A SSE stream of item state predicted events.
     */
    suspend fun ItemStatePredictedEvent(
        itemName: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ItemStatePredictedEventBlocking(itemName)
    }

    private fun ItemStatePredictedEventBlocking(itemName: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/items/" + Py.str(itemName) + "/statepredicted"), null, null, null)
    }

    /**
     * Get events for item state changes.
     *
     * Note: with the default "*", state changes of group items are included as
     * well, because their topic openhab/items/<group>/<member>/statechanged also
     * matches the pattern.
     *
     * @param itemName The name of the item (default is "*").
     *
     * @return A SSE stream of item state changed events.
     */
    suspend fun ItemStateChangedEvent(
        itemName: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        ItemStateChangedEventBlocking(itemName)
    }

    private fun ItemStateChangedEventBlocking(itemName: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/items/" + Py.str(itemName) + "/statechanged"), null, null, null)
    }

    /**
     * Get events for state changes of group items.
     *
     * @param itemName The name of the group item (default is "*").
     * @param memberName The name of the group member that caused the change (default is "*").
     *
     * @return A SSE stream of group item state changed events.
     */
    suspend fun GroupItemStateChangedEvent(
        itemName: String? = "*",
        memberName: String? = "*",
    ): SSEConnection = withContext(Dispatchers.IO) {
        GroupItemStateChangedEventBlocking(itemName, memberName)
    }

    private fun GroupItemStateChangedEventBlocking(itemName: String? = "*", memberName: String? = "*"): SSEConnection {
        return Py.openStream(client, ("openhab/items/" + Py.str(itemName) + "/" + Py.str(memberName) + "/statechanged"), null, null, null)
    }
}
