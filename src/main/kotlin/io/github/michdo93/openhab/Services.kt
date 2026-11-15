// Generated from Services.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Services(private val client: OpenHABClient) {

    /**
     * Get all configurable services.
     *
     * @param language Optional language setting (as header).
     *
     * @return A list of services (JSON).
     */
    suspend fun getServices(
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getServicesBlocking(language)
    }

    private fun getServicesBlocking(language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get("/services", Py.m(headers), null)
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
     * Get configurable service for the given service ID.
     *
     * @param serviceID The ID of the service to retrieve.
     * @param language Optional language setting (as header).
     *
     * @return The service object (JSON).
     */
    suspend fun getService(
        serviceID: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getServiceBlocking(serviceID, language)
    }

    private fun getServiceBlocking(serviceID: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/services/" + Py.str(serviceID)), Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
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
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Get service configuration for the given service ID.
     *
     * @param serviceID The ID of the service.
     *
     * @return The configuration of the service (JSON).
     */
    suspend fun getServiceConfig(
        serviceID: String,
    ): Any? = withContext(Dispatchers.IO) {
        getServiceConfigBlocking(serviceID)
    }

    private fun getServiceConfigBlocking(serviceID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/services/" + Py.str(serviceID) + "/config"), Py.m(Py.dict("Accept", "application/json")), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 500)) {
                return Py.dict("error", "Configuration can not be read due to internal error.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 500)) {
            return Py.dict("error", "Configuration can not be read due to internal error.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Updates a service configuration for the given service ID and returns the old configuration.
     *
     * @param serviceID The ID of the service.
     * @param configData The new configuration data (as a dictionary).
     *
     * @return The old configuration of the service (JSON).
     */
    suspend fun updateServiceConfig(
        serviceID: String,
        configData: Map<String, Any?>,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        updateServiceConfigBlocking(serviceID, configData, language)
    }

    private fun updateServiceConfigBlocking(serviceID: String, configData: Map<String, Any?>, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Content-Type", "application/json", "Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.put(("/services/" + Py.str(serviceID) + "/config"), Py.m(headers), Py.dumps(configData), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 500)) {
                return Py.dict("error", "Configuration can not be updated due to internal error.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 204)) {
            return Py.dict("message", "OK, no old configuration.")
        } else if (Py.eq(status_code, 500)) {
            return Py.dict("error", "Configuration can not be updated due to internal error.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Deletes a service configuration for the given service ID and returns the old configuration.
     *
     * @param serviceID The ID of the service.
     *
     * @return The old configuration of the service (JSON).
     */
    suspend fun deleteServiceConfig(
        serviceID: String,
    ): Any? = withContext(Dispatchers.IO) {
        deleteServiceConfigBlocking(serviceID)
    }

    private fun deleteServiceConfigBlocking(serviceID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/services/" + Py.str(serviceID) + "/config"), Py.m(Py.dict("Accept", "application/json")), null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 500)) {
                return Py.dict("error", "Configuration can not be updated due to internal error.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 204)) {
            return Py.dict("message", "OK, no old configuration.")
        } else if (Py.eq(status_code, 500)) {
            return Py.dict("error", "Configuration can not be updated due to internal error.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Get existing multiple context service configurations for the given factory PID.
     *
     * @param serviceID The ID of the service.
     * @param language Optional language setting (as header).
     *
     * @return A list of contexts (JSON).
     */
    suspend fun getServiceContexts(
        serviceID: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getServiceContextsBlocking(serviceID, language)
    }

    private fun getServiceContextsBlocking(serviceID: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/services/" + Py.str(serviceID) + "/contexts"), Py.m(headers), null)
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
