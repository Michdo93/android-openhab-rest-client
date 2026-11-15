// Generated from Addons.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Addons(private val client: OpenHABClient) {

    /**
     * Retrieves a list of all available add-ons.
     *
     * @param serviceID Optional service ID to filter the results.
     * @param language Optional language preference for the response.
     *
     * @return A dictionary containing the add-ons data.
     */
    suspend fun getAddons(
        serviceID: String? = null,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getAddonsBlocking(serviceID, language)
    }

    private fun getAddonsBlocking(serviceID: String? = null, language: String? = null): Any? {
        var headers: Any? = null
        var params: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        params = (if (Py.truthy(serviceID)) Py.dict("serviceId", serviceID) else Py.dict())
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get("/addons", Py.m(headers), Py.m(params))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Service not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Service not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Retrieves details of a specific add-on by its ID.
     *
     * @param addonID The unique identifier of the add-on.
     * @param serviceID Optional service ID to filter the results.
     * @param language Optional language preference for the response.
     *
     * @return A dictionary containing details of the specified add-on.
     */
    suspend fun getAddon(
        addonID: String,
        serviceID: String? = null,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getAddonBlocking(addonID, serviceID, language)
    }

    private fun getAddonBlocking(addonID: String, serviceID: String? = null, language: String? = null): Any? {
        var headers: Any? = null
        var params: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        params = (if (Py.truthy(serviceID)) Py.dict("serviceId", serviceID) else Py.dict())
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/addons/" + Py.str(addonID)), Py.m(headers), Py.m(params))
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
     * Retrieves the configuration of a specific add-on.
     *
     * @param addonID The unique identifier of the add-on.
     * @param serviceID Optional service ID to filter the results.
     *
     * @return A dictionary containing the configuration of the specified add-on.
     */
    suspend fun getAddonConfig(
        addonID: String,
        serviceID: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getAddonConfigBlocking(addonID, serviceID)
    }

    private fun getAddonConfigBlocking(addonID: String, serviceID: String? = null): Any? {
        var params: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        params = (if (Py.truthy(serviceID)) Py.dict("serviceId", serviceID) else Py.dict())
        try {
            response = client.get(("/addons/" + Py.str(addonID) + "/config"), Py.m(Py.dict("Accept", "application/json")), Py.m(params))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 500)) {
                return Py.dict("error", "Configuration can not be read due to internal error.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Add-on does not exist.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Add-on does not exist.")
        } else if (Py.eq(status_code, 500)) {
            return Py.dict("error", "Configuration can not be read due to internal error.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Updates the configuration of a specific add-on and returns the updated configuration.
     *
     * @param addonID The unique identifier of the add-on.
     * @param configData A dictionary containing the new configuration settings.
     * @param serviceID Optional service ID to specify the target service.
     *
     * @return A dictionary containing the updated configuration.
     */
    suspend fun updateAddonConfig(
        addonID: String,
        configData: Map<String, Any?>,
        serviceID: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        updateAddonConfigBlocking(addonID, configData, serviceID)
    }

    private fun updateAddonConfigBlocking(addonID: String, configData: Map<String, Any?>, serviceID: String? = null): Any? {
        var data: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        data = Py.copy(configData)
        try {
            response = client.put(("/addons/" + Py.str(addonID) + "/config"), Py.m(Py.dict("Content-Type", "application/json", "Accept", "application/json")), Py.dumps(data), Py.m(Py.dict("serviceId", serviceID)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 500)) {
                return Py.dict("error", "Configuration can not be updated due to internal error.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Add-on does not exist.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Add-on does not exist.")
        } else if (Py.eq(status_code, 500)) {
            return Py.dict("error", "Configuration can not be updated due to internal error.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Installs an add-on by its ID.
     *
     * @param addonID The unique identifier of the add-on.
     * @param serviceID Optional service ID to specify the target service.
     *
     * @return A dictionary containing the installation status.
     */
    suspend fun installAddon(
        addonID: String,
        serviceID: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        installAddonBlocking(addonID, serviceID)
    }

    private fun installAddonBlocking(addonID: String, serviceID: String? = null): Any? {
        var data: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        data = (if (Py.truthy(serviceID)) Py.dict("serviceId", serviceID) else Py.dict())
        try {
            response = client.post(("/addons/" + Py.str(addonID) + "/install"), null, null, Py.m(data))
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
     * Uninstalls an add-on by its ID.
     *
     * @param addonID The unique identifier of the add-on.
     * @param serviceID Optional service ID to specify the target service.
     *
     * @return A dictionary containing the uninstallation status.
     */
    suspend fun uninstallAddon(
        addonID: String,
        serviceID: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        uninstallAddonBlocking(addonID, serviceID)
    }

    private fun uninstallAddonBlocking(addonID: String, serviceID: String? = null): Any? {
        var data: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        data = (if (Py.truthy(serviceID)) Py.dict("serviceId", serviceID) else Py.dict())
        try {
            response = client.post(("/addons/" + Py.str(addonID) + "/uninstall"), null, null, Py.m(data))
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
     * Retrieves a list of all available add-on services.
     *
     * @param language Optional language preference for the response.
     *
     * @return A dictionary containing the available add-on services.
     */
    suspend fun getAddonServices(
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getAddonServicesBlocking(language)
    }

    private fun getAddonServicesBlocking(language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get("/addons/services", Py.m(headers), Py.m(null))
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
     * Retrieves a list of suggested add-ons for installation.
     *
     * @param language Optional language preference for the response.
     *
     * @return A dictionary containing suggested add-ons.
     */
    suspend fun getAddonSuggestions(
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getAddonSuggestionsBlocking(language)
    }

    private fun getAddonSuggestionsBlocking(language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get("/addons/suggestions", Py.m(headers), Py.m(null))
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
     * Retrieves a list of all available add-on types.
     *
     * @param language Optional language preference for the response.
     *
     * @return A dictionary containing available add-on types.
     */
    suspend fun getAddonTypes(
        serviceID: String? = null,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getAddonTypesBlocking(serviceID, language)
    }

    private fun getAddonTypesBlocking(serviceID: String? = null, language: String? = null): Any? {
        var headers: Any? = null
        var params: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        params = (if (Py.truthy(serviceID)) Py.dict("serviceId", serviceID) else Py.dict())
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get("/addons/types", Py.m(headers), Py.m(params))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Service not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Service not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Installs an add-on from a given URL.
     *
     * @param url The URL of the add-on to install.
     *
     * @return A dictionary containing the installation status.
     */
    suspend fun installAddonFromUrl(
        url: String,
    ): Any? = withContext(Dispatchers.IO) {
        installAddonFromUrlBlocking(url)
    }

    private fun installAddonFromUrlBlocking(url: String): Any? {
        var encoded_url: Any? = null
        var endpoint: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        encoded_url = Py.quoteAll(url)
        endpoint = ("/addons/url/" + Py.str(encoded_url) + "/install")
        try {
            response = client.post(Py.str(endpoint), null, null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "The given URL is malformed or not valid.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "The given URL is malformed or not valid.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }
}
