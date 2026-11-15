// Generated from Discovery.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Discovery(private val client: OpenHABClient) {

    /**
     * Gets all bindings that support discovery.
     *
     * @return Eine Liste der Bindings als Strings.
     */
    suspend fun getDiscoveryBindings(
    ): Any? = withContext(Dispatchers.IO) {
        getDiscoveryBindingsBlocking()
    }

    private fun getDiscoveryBindingsBlocking(): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get("/discovery", Py.m(Py.dict("Accept", "application/json")), null)
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
     * Gets information about the discovery services for a binding.
     *
     * @param bindingId The ID of the binding to get discovery information for.
     * @param language Optional language parameter for the response (Accept-Language header).
     *
     * @return Information about the discovery service for the specified binding.
     */
    suspend fun getBindingInfo(
        bindingID: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getBindingInfoBlocking(bindingID, language)
    }

    private fun getBindingInfoBlocking(bindingID: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/discovery/bindings/" + Py.str(bindingID) + "/info"), Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Discovery service not found.")
            } else if (!Py.eq(status_code, 200)) {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return response
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Starts asynchronous discovery process for a binding and returns the timeout in seconds of the discovery operation.
     *
     * @param bindingID The ID of the binding for which the discovery is to be started.
     * @param input An input parameter to start the discovery.
     *
     * @return Timeout duration of the discovery operation in seconds.
     */
    suspend fun startBindingScan(
        bindingID: String,
        input: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        startBindingScanBlocking(bindingID, input)
    }

    private fun startBindingScanBlocking(bindingID: String, input: String? = null): Any? {
        var params: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        params = Py.dict("input", input)
        try {
            response = client.post(("/discovery/bindings/" + Py.str(bindingID) + "/scan"), Py.m(Py.dict("Accept", "text/plain")), null, Py.m(params))
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
