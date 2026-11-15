// Generated from Actions.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Actions(private val client: OpenHABClient) {

    /**
     * Get all available actions for provided thing UID.
     *
     * @param thingUID The UID of the thing for which actions are to be retrieved.
     * @param language (Optional) Language setting for the Accept-Language headers.
     *
     * @return A list of actions.
     */
    suspend fun getActions(
        thingUID: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getActionsBlocking(thingUID, language)
    }

    private fun getActionsBlocking(thingUID: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/actions/" + Py.str(thingUID)), Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 204)) {
                return Py.dict("error", "No actions found.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", ("Thing not found: " + Py.str(thingUID)))
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 204)) {
            return Py.dict("error", "No actions found.")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", ("Thing not found: " + Py.str(thingUID)))
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Executes a thing action.
     *
     * @param thingUID The UID of the thing on which the action is to be executed.
     * @param actionUID The UID of the action to be executed.
     * @param actionInputs The inputs for the action as a dictionary.
     * @param language (Optional) Language setting for the Accept-Language headers.
     *
     * @return A response from the server.
     */
    suspend fun executeAction(
        thingUID: String,
        actionUID: String,
        actionInputs: Map<String, Any?>,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        executeActionBlocking(thingUID, actionUID, actionInputs, language)
    }

    private fun executeActionBlocking(thingUID: String, actionUID: String, actionInputs: Map<String, Any?>, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Content-Type", "application/json", "Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.post(("/actions/" + Py.str(thingUID) + "/" + Py.str(actionUID)), Py.m(headers), Py.dumps(actionInputs), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Action not found.")
            } else if (Py.eq(status_code, 500)) {
                return Py.dict("error", "Creation of action handler or execution failed.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Action not found.")
        } else if (Py.eq(status_code, 500)) {
            return Py.dict("error", "Creation of action handler or execution failed.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }
}
