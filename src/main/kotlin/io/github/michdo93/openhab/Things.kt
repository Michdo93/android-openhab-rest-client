// Generated from Things.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Things(private val client: OpenHABClient) {

    /**
     * Get all available things.
     *
     * @param summary If True, returns only the summary fields.
     * @param staticDataOnly If True, returns a cacheable list.
     * @param language The preferred language for the response.
     *
     * @return JSON response with the things.
     */
    suspend fun getThings(
        summary: Boolean? = false,
        staticDataOnly: Boolean? = false,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getThingsBlocking(summary, staticDataOnly, language)
    }

    private fun getThingsBlocking(summary: Boolean? = false, staticDataOnly: Boolean? = false, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get("/things", Py.m(headers), Py.m(Py.dict("summary", summary, "staticDataOnly", staticDataOnly)))
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
     * Creates a new thing and adds it to the registry.
     *
     * @param thingData The JSON object containing the thing data.
     * @param language The preferred language for the response.
     *
     * @return The API response.
     */
    suspend fun createThing(
        thingData: Map<String, Any?>,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        createThingBlocking(thingData, language)
    }

    private fun createThingBlocking(thingData: Map<String, Any?>, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Content-Type", "application/json", "Accept", "*/*")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.post("/things", Py.m(headers), Py.dumps(thingData), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 409)) {
                return Py.dict("error", "A thing with the same uid already exists.")
            } else if (Py.eq(status_code, 400)) {
                return Py.dict("error", "A uid must be provided, if no binding can create a thing of this type.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "A uid must be provided, if no binding can create a thing of this type.")
        } else if (Py.eq(status_code, 409)) {
            return Py.dict("error", "A thing with the same uid already exists.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets a thing by UID.
     *
     * @param thingUID The UID of the thing.
     * @param language The preferred language for the response.
     *
     * @return JSON response with the thing data.
     */
    suspend fun getThing(
        thingUID: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getThingBlocking(thingUID, language)
    }

    private fun getThingBlocking(thingUID: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/things/" + Py.str(thingUID)), Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Thing not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "THing not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Updates a thing.
     *
     * @param thingUID The UID of the thing.
     * @param thingData The JSON object containing the updated thing data.
     * @param language The preferred language for the response.
     *
     * @return The API response.
     */
    suspend fun updateThing(
        thingUID: String,
        thingData: Map<String, Any?>,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        updateThingBlocking(thingUID, thingData, language)
    }

    private fun updateThingBlocking(thingUID: String, thingData: Map<String, Any?>, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Content-Type", "application/json", "Accept", "*/*")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.put(("/things/" + Py.str(thingUID)), Py.m(headers), Py.dumps(thingData), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 409)) {
                return Py.dict("error", "Thing could not be updated as it is not editable.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Thing not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Thing not found.")
        } else if (Py.eq(status_code, 409)) {
            return Py.dict("error", "Thing could not be updated as it is not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Removes a thing from the registry. Set 'force' to __true__ if you want the thing to be removed immediately.
     *
     * @param thingUID The UID of the thing.
     * @param force If True, the thing will be immediately removed.
     * @param language The preferred language for the response.
     *
     * @return The API response.
     */
    suspend fun deleteThing(
        thingUID: String,
        force: Boolean? = false,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        deleteThingBlocking(thingUID, force, language)
    }

    private fun deleteThingBlocking(thingUID: String, force: Boolean? = false, language: String? = null): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/things/" + Py.str(thingUID)), Py.m((if (Py.truthy(language)) Py.dict("Accept-Language", language) else Py.dict())), null, Py.m(Py.dict("force", force)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 409)) {
                return Py.dict("error", "Thing could not be deleted because it's not editable.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Thing not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK, was deleted.")
        } else if (Py.eq(status_code, 202)) {
            return Py.dict("message", "ACCEPTED for asynchronous deletion.")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Thing not found.")
        } else if (Py.eq(status_code, 409)) {
            return Py.dict("error", "Thing could not be deleted because it's not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Updates a thing's configuration.
     *
     * @param thingUID The UID of the thing.
     * @param configurationData The configuration data of the thing.
     * @param language The preferred language for the response.
     *
     * @return The API response.
     */
    suspend fun updateThingConfiguration(
        thingUID: String,
        configurationData: Map<String, Any?>,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        updateThingConfigurationBlocking(thingUID, configurationData, language)
    }

    private fun updateThingConfigurationBlocking(thingUID: String, configurationData: Map<String, Any?>, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Content-Type", "application/json", "Accept", "*/*")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.put(("/things/" + Py.str(thingUID) + "/config"), Py.m(headers), Py.dumps(configurationData), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Configuration of the thing is not valid.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Thing not found.")
            } else if (Py.eq(status_code, 409)) {
                return Py.dict("error", "Thing could not be updated as it is not editable.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Configuration of the thing is not valid.")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Thing not found.")
        } else if (Py.eq(status_code, 409)) {
            return Py.dict("error", "Thing could not be updated as it is not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets the thing's configuration status.
     *
     * @param thingUID The UID of the thing.
     * @param language The preferred language for the response.
     *
     * @return JSON response with the thing's configuration status.
     */
    suspend fun getThingConfigStatus(
        thingUID: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getThingConfigStatusBlocking(thingUID, language)
    }

    private fun getThingConfigStatusBlocking(thingUID: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/things/" + Py.str(thingUID) + "/config/status"), Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Thing not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Thing not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Sets the thing's enabled status.
     *
     * @param thingUID The UID of the thing.
     * @param enabled If True, the thing will be enabled.
     * @param language The preferred language for the response.
     *
     * @return The API response.
     */
    suspend fun setThingStatus(
        thingUID: String,
        enabled: Boolean,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        setThingStatusBlocking(thingUID, enabled, language)
    }

    private fun setThingStatusBlocking(thingUID: String, enabled: Boolean, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Content-Type", "text/plain", "Accept", "*/*")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.put(("/things/" + Py.str(thingUID) + "/enable"), Py.m(headers), (if (Py.truthy(enabled)) "true" else "false"), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Thing not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Thing not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    suspend fun enableThing(
        thingUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        enableThingBlocking(thingUID)
    }

    private fun enableThingBlocking(thingUID: String): Any? {
        return setThingStatusBlocking(thingUID = thingUID, enabled = true)
    }

    suspend fun disableThing(
        thingUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        disableThingBlocking(thingUID)
    }

    private fun disableThingBlocking(thingUID: String): Any? {
        return setThingStatusBlocking(thingUID = thingUID, enabled = false)
    }

    /**
     * Updates the firmware of a thing.
     *
     * @param thingUID The UID of the thing.
     * @param firmwareVersion The firmware version.
     * @param language The preferred language for the response.
     *
     * @return The API response.
     */
    suspend fun updateThingFirmware(
        thingUID: String,
        firmwareVersion: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        updateThingFirmwareBlocking(thingUID, firmwareVersion, language)
    }

    private fun updateThingFirmwareBlocking(thingUID: String, firmwareVersion: String, language: String? = null): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.put(("/things/" + Py.str(thingUID) + "/firmware/" + Py.str(firmwareVersion)), Py.m((if (Py.truthy(language)) Py.dict("Accept-Language", language) else Py.dict())), null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Firmware update preconditions not satisfied.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Thing not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Thing not found.")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Firmware update preconditions not satisfied.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets the thing's firmware status.
     *
     * @param thingUID The UID of the thing.
     * @param language The preferred language for the response.
     *
     * @return JSON response with the firmware status.
     */
    suspend fun getThingFirmwareStatus(
        thingUID: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getThingFirmwareStatusBlocking(thingUID, language)
    }

    private fun getThingFirmwareStatusBlocking(thingUID: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "*/*")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/things/" + Py.str(thingUID) + "/firmware/status"), Py.m(headers), null)
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
        } else if (Py.eq(status_code, 204)) {
            return Py.dict("error", "No firmware status provided by this Thing.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets all available firmwares for the provided thing UID.
     *
     * @param thingUID The UID of the thing.
     * @param language The preferred language for the response.
     *
     * @return A list of available firmwares.
     */
    suspend fun getThingFirmwares(
        thingUID: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getThingFirmwaresBlocking(thingUID, language)
    }

    private fun getThingFirmwaresBlocking(thingUID: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/things/" + Py.str(thingUID) + "/firmwares"), Py.m(headers), null)
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
        } else if (Py.eq(status_code, 204)) {
            return Py.dict("error", "No firmwares found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets the thing's status.
     *
     * @param thingUID The UID of the thing.
     * @param language The preferred language for the response.
     *
     * @return JSON response with the thing's status.
     */
    suspend fun getThingStatus(
        thingUID: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getThingStatusBlocking(thingUID, language)
    }

    private fun getThingStatusBlocking(thingUID: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/things/" + Py.str(thingUID) + "/status"), Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Thing not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Thing not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }
}
