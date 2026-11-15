// Generated from Transformations.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Transformations(private val client: OpenHABClient) {

    /**
     * Get a list of all transformations.
     *
     * @return A list of transformations (JSON).
     */
    suspend fun getTransformations(
    ): Any? = withContext(Dispatchers.IO) {
        getTransformationsBlocking()
    }

    private fun getTransformationsBlocking(): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get("/transformations", Py.m(Py.dict("Accept", "application/json")), null)
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
     * Get a single transformation.
     *
     * @param transformationUID The transformationUID of the transformation to retrieve.
     *
     * @return The transformation (JSON).
     */
    suspend fun getTransformation(
        transformationUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        getTransformationBlocking(transformationUID)
    }

    private fun getTransformationBlocking(transformationUID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/transformations/" + Py.str(transformationUID)), Py.m(Py.dict("Accept", "application/json")), null)
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
     * Update a single transformation.
     *
     * @param transformationUID The transformationUID of the transformation to update.
     * @param transformationData The new data for the transformation.
     *
     * @return The response to the transformation update request (JSON).
     */
    suspend fun updateTransformation(
        transformationUID: String,
        transformationData: Any?,
    ): Any? = withContext(Dispatchers.IO) {
        updateTransformationBlocking(transformationUID, transformationData)
    }

    private fun updateTransformationBlocking(transformationUID: String, transformationData: Any?): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.put(("/transformations/" + Py.str(transformationUID)), Py.m(Py.dict("Content-Type", "application/json")), Py.dumps(transformationData), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Bad Request (content missing or invalid)")
            } else if (Py.eq(status_code, 405)) {
                return Py.dict("error", "Transformation not editable.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Bad Request (content missing or invalid)")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "Transformation not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Delete a single transformation.
     *
     * @param transformationUID The transformationUID of the transformation to delete.
     *
     * @return The response to the transformation update request (JSON) or status/error message.
     */
    suspend fun deleteTransformation(
        transformationUID: String,
    ): Any? = withContext(Dispatchers.IO) {
        deleteTransformationBlocking(transformationUID)
    }

    private fun deleteTransformationBlocking(transformationUID: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/transformations/" + Py.str(transformationUID)), null, null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 405)) {
                return Py.dict("error", "Transformation not editable.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "UID not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "UID not found.")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "Transformation not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Get all transformation services.
     *
     * @return A list of transformation services (JSON).
     */
    suspend fun getTransformationServices(
    ): Any? = withContext(Dispatchers.IO) {
        getTransformationServicesBlocking()
    }

    private fun getTransformationServicesBlocking(): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get("/transformations/services", Py.m(Py.dict("Accept", "application/json")), null)
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
