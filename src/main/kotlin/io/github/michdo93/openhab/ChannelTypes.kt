// Generated from ChannelTypes.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class ChannelTypes(private val client: OpenHABClient) {

    /**
     * Retrieves all available channel types.
     *
     * @param language Optional headers 'Accept-Language' to specify the preferred language.
     * @param prefixes Optional query parameter to filter channel types by prefix.
     *
     * @return A list of channel types.
     */
    suspend fun getChannelTypes(
        prefixes: String? = null,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getChannelTypesBlocking(prefixes, language)
    }

    private fun getChannelTypesBlocking(prefixes: String? = null, language: String? = null): Any? {
        var headers: Any? = null
        var params: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        params = Py.dict()
        if (Py.truthy(prefixes)) {
            Py.set(params, "prefixes", prefixes)
        }
        try {
            response = client.get("/channel-types", Py.m(headers), Py.m(params))
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
     * Retrieves the item types the given trigger channel type UID can be linked to.
     *
     * @param channelTypeUID The unique UID of the channel type.
     * @param language Optional headers 'Accept-Language' to specify the preferred language.
     *
     * @return Details of the specific channel type.
     */
    suspend fun getChannelType(
        channelTypeUID: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getChannelTypeBlocking(channelTypeUID, language)
    }

    private fun getChannelTypeBlocking(channelTypeUID: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/channel-types/" + Py.str(channelTypeUID)), Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Channel type with provided channelTypeUID does not exist.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Channel type with provided channelTypeUID does not exist.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Retrieves the item types that can be linked to the specified trigger channel type.
     *
     * @param channelTypeUID The unique UID of the channel type.
     *
     * @return A list of item types.
     */
    suspend fun getLinkableItemTypes(
        channelTypeUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        getLinkableItemTypesBlocking(channelTypeUID)
    }

    private fun getLinkableItemTypesBlocking(channelTypeUID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/channel-types/" + Py.str(channelTypeUID) + "/linkableItemTypes"), Py.m(Py.dict("Accept", "application/json")), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 204)) {
                return Py.dict("error", "No content: channel type has no linkable items or is no trigger channel.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Given channel type UID not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Given channel type UID not found.")
        } else if (Py.eq(status_code, 204)) {
            return Py.dict("error", "No content: channel type has no linkable items or is no trigger channel.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }
}
