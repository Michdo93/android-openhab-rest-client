// Generated from ProfileTypes.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class ProfileTypes(private val client: OpenHABClient) {

    /**
     * Gets all available profile types.
     *
     * @param channelTypeUID Optional filter for the channel type.
     * @param itemType Optional filter for the item type.
     * @param language (Optional) Language setting for the Accept-Language header.
     *
     * @return A list of profile types.
     */
    suspend fun getProfileTypes(
        channelTypeUID: String? = null,
        itemType: String? = null,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getProfileTypesBlocking(channelTypeUID, itemType, language)
    }

    private fun getProfileTypesBlocking(channelTypeUID: String? = null, itemType: String? = null, language: String? = null): Any? {
        var headers: Any? = null
        var params: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        params = Py.dict()
        if (Py.truthy(channelTypeUID)) {
            Py.set(params, "channelTypeUID", channelTypeUID)
        }
        if (Py.truthy(itemType)) {
            Py.set(params, "itemType", itemType)
        }
        try {
            response = client.get("/profile-types", Py.m(headers), Py.m(params))
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
