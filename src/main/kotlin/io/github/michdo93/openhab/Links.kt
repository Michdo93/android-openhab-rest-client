// Generated from Links.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Links(private val client: OpenHABClient) {

    /**
     * Gets all available links.
     *
     * @param channelUID Optional; Filters by the Channel UID.
     * @param itemName Optional; Filters by the Item Name.
     *
     * @return A list of links containing details such as ItemName, ChannelUID, and configuration.
     */
    suspend fun getLinks(
        channelUID: String? = null,
        itemName: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getLinksBlocking(channelUID, itemName)
    }

    private fun getLinksBlocking(channelUID: String? = null, itemName: String? = null): Any? {
        var params: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        params = Py.dict()
        if (Py.truthy(channelUID)) {
            Py.set(params, "channelUID", channelUID)
        }
        if (Py.truthy(itemName)) {
            Py.set(params, "itemName", itemName)
        }
        try {
            response = client.get("/links", Py.m(Py.dict("Accept", "application/json")), Py.m(params))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (!Py.eq(status_code, 200)) {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Retrieves an individual link.
     *
     * @param itemName The name of the item.
     * @param channelUID The UID of the channel.
     *
     * @return The link with details of the item, channel UID, and configuration.
     */
    suspend fun getLink(
        itemName: String,
        channelUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        getLinkBlocking(itemName, channelUID)
    }

    private fun getLinkBlocking(itemName: String, channelUID: String): Any? {
        var channelPath: Any? = null
        var itemPath: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        itemPath = Py.quoteAll(itemName)
        channelPath = Py.quoteAll(channelUID)
        try {
            response = client.get(("/links/" + Py.str(itemPath) + "/" + Py.str(channelPath)), Py.m(Py.dict("Accept", "application/json")), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Link not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Link not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Links an item to a channel.
     *
     * @param itemName The name of the item.
     * @param channelUID The UID of the channel.
     * @param configuration The configuration for the link.
     *
     * @return The API response when the link is successfully created.
     */
    suspend fun linkItemToChannel(
        itemName: String,
        channelUID: String,
        configuration: Map<String, Any?>,
    ): Any? = withContext(Dispatchers.IO) {
        linkItemToChannelBlocking(itemName, channelUID, configuration)
    }

    private fun linkItemToChannelBlocking(itemName: String, channelUID: String, configuration: Map<String, Any?>): Any? {
        var channelPath: Any? = null
        var itemPath: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        itemPath = Py.quoteAll(itemName)
        channelPath = Py.quoteAll(channelUID)
        try {
            response = client.put(("/links/" + Py.str(itemPath) + "/" + Py.str(channelPath)), Py.m(Py.dict("Content-Type", "application/json")), Py.dumps(Py.dict("itemName", itemName, "channelUID", channelUID, "configuration", configuration)), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 405)) {
                return Py.dict("error", "Link is not editable.")
            } else if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Content does not match the path.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Content does not match the path.")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "Link is not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Unlinks an item from a channel.
     *
     * @param itemName The name of the item.
     * @param channelUID The UID of the channel.
     *
     * @return The API response when the link is successfully removed.
     */
    suspend fun unlinkItemFromChannel(
        itemName: String,
        channelUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        unlinkItemFromChannelBlocking(itemName, channelUID)
    }

    private fun unlinkItemFromChannelBlocking(itemName: String, channelUID: String): Any? {
        var channelPath: Any? = null
        var itemPath: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        itemPath = Py.quoteAll(itemName)
        channelPath = Py.quoteAll(channelUID)
        try {
            response = client.delete(("/links/" + Py.str(itemPath) + "/" + Py.str(channelPath)), Py.m(Py.dict("Content-Type", "application/json")), null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 405)) {
                return Py.dict("error", "Link not editable.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Link not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Link not found.")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "Link not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Delete all links that refer to an item or thing.
     *
     * @param object The name of the item or the UID of the thing.
     *
     * @return The API response when all links are successfully deleted.
     */
    suspend fun deleteAllLinks(
        `object`: String,
    ): Any? = withContext(Dispatchers.IO) {
        deleteAllLinksBlocking(`object`)
    }

    private fun deleteAllLinksBlocking(`object`: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/links/" + Py.str(`object`)), Py.m(Py.dict("Accept", "application/json")), null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (!Py.eq(status_code, 200)) {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Get orphan links between items and broken/non-existent thing channels.
     *
     * @return A list of orphan links.
     */
    suspend fun getOrphanLinks(
    ): Any? = withContext(Dispatchers.IO) {
        getOrphanLinksBlocking()
    }

    private fun getOrphanLinksBlocking(): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get("/links/orphans", Py.m(Py.dict("Accept", "application/json")), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (!Py.eq(status_code, 200)) {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Remove unused/orphaned links.
     *
     * @return The API response when the links are successfully removed.
     */
    suspend fun purgeUnusedLinks(
    ): Any? = withContext(Dispatchers.IO) {
        purgeUnusedLinksBlocking()
    }

    private fun purgeUnusedLinksBlocking(): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.post("/links/purge", null, null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (!Py.eq(status_code, 200)) {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }
}
