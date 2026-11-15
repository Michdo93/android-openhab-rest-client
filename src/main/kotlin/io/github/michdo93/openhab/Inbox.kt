// Generated from Inbox.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Inbox(private val client: OpenHABClient) {

    /**
     * Get all discovered things.
     *
     * @param includeIgnored Whether ignored entries should also be included (default: True).
     *
     * @return A list of discovered things with details such as UID, flag, label, and properties.
     */
    suspend fun getDiscoveredThings(
        includeIgnored: Boolean? = true,
    ): Any? = withContext(Dispatchers.IO) {
        getDiscoveredThingsBlocking(includeIgnored)
    }

    private fun getDiscoveredThingsBlocking(includeIgnored: Boolean? = true): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get("/inbox", Py.m(Py.dict("Accept", "application/json")), Py.m(Py.dict("includeIgnored", Py.lower(Py.str(includeIgnored)))))
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
     * Removes the discovery result from the inbox.
     *
     * @param thingUID The UID of the discovered thing to be removed.
     *
     * @return The API response to the delete request.
     */
    suspend fun removeDiscoveryResult(
        thingUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        removeDiscoveryResultBlocking(thingUID)
    }

    private fun removeDiscoveryResultBlocking(thingUID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/inbox/" + Py.str(thingUID)), null, null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Discovery result not found in the inbox.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Discovery result not found in the inbox.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Approves the discovery result by adding the thing to the registry.
     *
     * @param thingUID The UID of the discovered thing.
     * @param thingLabel The new name of the thing.
     * @param newThingID Optional: The new thing ID.
     * @param language Optional: Language preference for the response.
     *
     * @return The API response to the approval request.
     */
    suspend fun approveDiscoveryResult(
        thingUID: String,
        thingLabel: String,
        newThingID: String? = null,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        approveDiscoveryResultBlocking(thingUID, thingLabel, newThingID, language)
    }

    private fun approveDiscoveryResultBlocking(thingUID: String, thingLabel: String, newThingID: String? = null, language: String? = null): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.post(("/inbox/" + Py.str(thingUID) + "/approve"), Py.m((if (Py.truthy(language)) Py.dict("Accept-Language", language, "Content-Type", "text/plain") else Py.dict("Content-Type", "text/plain"))), thingLabel, Py.m((if (Py.truthy(newThingID)) Py.dict("newThingId", newThingID) else Py.dict())))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Invalid new thing ID.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Thing unable to be approved.")
            } else if (Py.eq(status_code, 409)) {
                return Py.dict("error", "No binding found that supports this thing.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Invalid new thing ID.")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Thing unable to be approved.")
        } else if (Py.eq(status_code, 409)) {
            return Py.dict("error", "No binding found that supports this thing.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Flags a discovery result as ignored for further processing.
     *
     * @param thingUID The UID of the discovered thing.
     *
     * @return The API response to the ignore request.
     */
    suspend fun ignoreDiscoveryResult(
        thingUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        ignoreDiscoveryResultBlocking(thingUID)
    }

    private fun ignoreDiscoveryResultBlocking(thingUID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.post(("/inbox/" + Py.str(thingUID) + "/ignore"), null, null, null)
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
     * Removes the ignore flag from a discovery result.
     *
     * @param thingUID The UID of the discovered thing.
     *
     * @return The API response to the unignore request.
     */
    suspend fun unignoreDiscoveryResult(
        thingUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        unignoreDiscoveryResultBlocking(thingUID)
    }

    private fun unignoreDiscoveryResultBlocking(thingUID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.post(("/inbox/" + Py.str(thingUID) + "/unignore"), null, null, null)
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
