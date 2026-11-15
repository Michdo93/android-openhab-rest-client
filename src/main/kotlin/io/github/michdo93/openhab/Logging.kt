// Generated from Logging.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Logging(private val client: OpenHABClient) {

    /**
     * Get all loggers.
     *
     * @return A list of loggers with names and levels.
     */
    suspend fun getLoggers(
    ): Any? = withContext(Dispatchers.IO) {
        getLoggersBlocking()
    }

    private fun getLoggersBlocking(): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get("/logging", Py.m(Py.dict("Accept", "application/json")), null)
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
     * Get a single logger.
     *
     * @param loggerName The name of the logger.
     *
     * @return The logger with the specified name and level.
     */
    suspend fun getLogger(
        loggerName: String,
    ): Any? = withContext(Dispatchers.IO) {
        getLoggerBlocking(loggerName)
    }

    private fun getLoggerBlocking(loggerName: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get(("/logging/" + Py.str(loggerName)), Py.m(Py.dict("Accept", "application/json")), null)
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
     * Modify or add a logger.
     *
     * @param loggerName The name of the logger.
     * @param level The level of the logger.
     *
     * @return The API response after modification or addition.
     */
    suspend fun modifyOrAddLogger(
        loggerName: String,
        level: String,
    ): Any? = withContext(Dispatchers.IO) {
        modifyOrAddLoggerBlocking(loggerName, level)
    }

    private fun modifyOrAddLoggerBlocking(loggerName: String, level: String): Any? {
        var data: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        data = Py.dict("loggerName", loggerName, "level", level)
        try {
            response = client.put(("/logging/" + Py.str(loggerName)), Py.m(Py.dict("Content-Type", "application/json")), Py.dumps(data), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Payload is invalid.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Payload is invalid.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Remove a single logger.
     *
     * @param loggerName The name of the logger.
     *
     * @return The API response after removing the logger.
     */
    suspend fun removeLogger(
        loggerName: String,
    ): Any? = withContext(Dispatchers.IO) {
        removeLoggerBlocking(loggerName)
    }

    private fun removeLoggerBlocking(loggerName: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/logging/" + Py.str(loggerName)), null, null, null)
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
