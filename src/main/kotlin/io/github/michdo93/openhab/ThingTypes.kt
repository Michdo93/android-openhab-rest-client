// Generated from ThingTypes.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class ThingTypes(private val client: OpenHABClient) {

    /**
     * Gets all available thing types without config description, channels, and properties.
     *
     * @param bindingID (Optional) Filter by binding ID.
     * @param language (Optional) Language setting for the Accept-Language header.
     *
     * @return A list of thing types.
     */
    suspend fun getThingTypes(
        bindingID: String? = null,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getThingTypesBlocking(bindingID, language)
    }

    private fun getThingTypesBlocking(bindingID: String? = null, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get("/thing-types", Py.m(headers), Py.m((if (Py.truthy(bindingID)) Py.dict("bindingId", bindingID) else Py.dict())))
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
     * Gets a thing type by UID.
     *
     * @param thingTypeUID The UID of the thing type.
     * @param language (Optional) Language setting for the Accept-Language header.
     *
     * @return A dictionary with the details of the thing type or an empty response with status 204.
     */
    suspend fun getThingType(
        thingTypeUID: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getThingTypeBlocking(thingTypeUID, language)
    }

    private fun getThingTypeBlocking(thingTypeUID: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/thing-types/" + Py.str(thingTypeUID)), Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "No Content.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "No Content.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }
}
