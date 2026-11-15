// Generated from UI.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class UI(private val client: OpenHABClient) {

    /**
     * Get all registered UI components in the specified namespace.
     *
     * @param namespace The namespace for which UI components should be retrieved.
     * @param summary If True, only summary fields will be returned.
     *
     * @return A list of UI components (JSON).
     */
    suspend fun getUIComponents(
        namespace: String,
        summary: Boolean? = false,
    ): Any? = withContext(Dispatchers.IO) {
        getUIComponentsBlocking(namespace, summary)
    }

    private fun getUIComponentsBlocking(namespace: String, summary: Boolean? = false): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/ui/components/" + Py.str(namespace)), Py.m(Py.dict("Accept", "application/json")), Py.m((if (Py.truthy(summary)) Py.dict("summary", summary) else Py.dict())))
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
     * Add a UI component in the specified namespace.
     *
     * @param namespace The namespace where the UI component should be added.
     * @param componentData The data of the UI component (JSON) to be added.
     *
     * @return The response to the request (JSON).
     */
    suspend fun addUIComponent(
        namespace: String,
        componentData: Any?,
    ): Any? = withContext(Dispatchers.IO) {
        addUIComponentBlocking(namespace, componentData)
    }

    private fun addUIComponentBlocking(namespace: String, componentData: Any?): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.post(("/ui/components/" + Py.str(namespace)), Py.m(Py.dict("Content-Type", "application/json", "Accept", "application/json")), Py.dumps(componentData), null)
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
     * Get a specific UI component in the specified namespace.
     *
     * @param namespace The namespace where the UI component is located.
     * @param componentUID The UID of the UI component to retrieve.
     *
     * @return The UI component (JSON).
     */
    suspend fun getUIComponent(
        namespace: String,
        componentUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        getUIComponentBlocking(namespace, componentUID)
    }

    private fun getUIComponentBlocking(namespace: String, componentUID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/ui/components/" + Py.str(namespace) + "/" + Py.str(componentUID)), Py.m(Py.dict("Accept", "application/json")), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Component not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Component not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Update a specific UI component in the specified namespace.
     *
     * @param namespace The namespace where the UI component should be updated.
     * @param componentUID The UID of the UI component to update.
     * @param componentData The new data for the UI component (JSON).
     *
     * @return The response to the request (JSON).
     */
    suspend fun updateUIComponent(
        namespace: String,
        componentUID: String,
        componentData: Any?,
    ): Any? = withContext(Dispatchers.IO) {
        updateUIComponentBlocking(namespace, componentUID, componentData)
    }

    private fun updateUIComponentBlocking(namespace: String, componentUID: String, componentData: Any?): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.put(("/ui/components/" + Py.str(namespace) + "/" + Py.str(componentUID)), Py.m(Py.dict("Content-Type", "application/json", "Accept", "application/json")), Py.dumps(componentData), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Component not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Component not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Remove a specific UI component in the specified namespace.
     *
     * @param namespace The namespace where the UI component should be removed.
     * @param componentUID The UID of the UI component to delete.
     *
     * @return The response to the request (JSON).
     */
    suspend fun deleteUIComponent(
        namespace: String,
        componentUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        deleteUIComponentBlocking(namespace, componentUID)
    }

    private fun deleteUIComponentBlocking(namespace: String, componentUID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/ui/components/" + Py.str(namespace) + "/" + Py.str(componentUID)), null, null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Component not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Component not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Get all registered UI tiles.
     *
     * @return A list of UI tiles (JSON).
     */
    suspend fun getUITiles(
    ): Any? = withContext(Dispatchers.IO) {
        getUITilesBlocking()
    }

    private fun getUITilesBlocking(): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get("/ui/tiles", Py.m(Py.dict("Accept", "application/json")), null)
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
