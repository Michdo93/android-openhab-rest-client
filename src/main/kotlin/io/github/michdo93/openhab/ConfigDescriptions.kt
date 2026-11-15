// Generated from ConfigDescriptions.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class ConfigDescriptions(private val client: OpenHABClient) {

    /**
     * Retrieves all available config descriptions.
     *
     * @param language Optional headers 'Accept-Language' to specify the preferred language.
     * @param scheme Optional query parameter to filter results by a specific scheme.
     *
     * @return A list of configuration descriptions.
     */
    suspend fun getConfigDescriptions(
        scheme: String? = null,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getConfigDescriptionsBlocking(scheme, language)
    }

    private fun getConfigDescriptionsBlocking(scheme: String? = null, language: String? = null): Any? {
        var headers: Any? = null
        var params: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        params = Py.dict()
        if (Py.truthy(scheme)) {
            Py.set(params, "scheme", scheme)
        }
        try {
            response = client.get("/config-descriptions", Py.m(headers), Py.m(params))
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
     * Retrieves a config description by URI.
     *
     * @param uri The URI of the requested configuration description.
     * @param language Optional headers 'Accept-Language' to specify the preferred language.
     *
     * @return Details of the specific configuration description.
     */
    suspend fun getConfigDescription(
        uri: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getConfigDescriptionBlocking(uri, language)
    }

    private fun getConfigDescriptionBlocking(uri: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/config-descriptions/" + Py.str(uri)), Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Invalid URI syntax.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Not found.")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Invalid URI syntax.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }
}
