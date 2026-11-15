// Generated from Sitemaps.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Sitemaps(private val client: OpenHABClient) {

    /**
     * Opens a sitemap SSE stream; returns the streaming response or {"error": ...}.
     */
    private fun _openEventStream(path: String, params: Map<String, Any?>, errors: Map<String, Any?>): SSEConnection {
        var statusCode: Any? = null
        try {
            return client.executeSSE(path, null, Py.m(params))
        } catch (err: OpenHABHTTPError) {
            statusCode = err.status
            if (Py.contains(errors, statusCode)) {
                return Py.stream(Py.dict("error", Py.get(errors, statusCode)))
            }
            return Py.stream(Py.dict("error", ("HTTP error " + Py.str(statusCode) + ": " + Py.str(Py.str(err)))))
        } catch (err: OpenHABRequestException) {
            return Py.stream(Py.dict("error", ("Request error: " + Py.str(Py.str(err)))))
        }
    }

    /**
     * Get all available sitemaps.
     *
     * @return A list of sitemaps (JSON).
     */
    suspend fun getSitemaps(
    ): Any? = withContext(Dispatchers.IO) {
        getSitemapsBlocking()
    }

    private fun getSitemapsBlocking(): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.get("/sitemaps", Py.m(Py.dict("Accept", "application/json")), null)
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
     * Get sitemap by name.
     *
     * @param sitemapName The name of the sitemap to retrieve.
     * @param type Optional query parameter for type.
     * @param jsonCallback Optional query parameter for JSON callback.
     * @param includeHidden Whether hidden widgets should be included.
     * @param language Optional language setting (as header).
     *
     * @return The sitemap object (JSON).
     */
    suspend fun getSitemap(
        sitemapName: String,
        type: String? = null,
        jsonCallback: String? = null,
        includeHidden: Boolean? = false,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getSitemapBlocking(sitemapName, type, jsonCallback, includeHidden, language)
    }

    private fun getSitemapBlocking(sitemapName: String, type: String? = null, jsonCallback: String? = null, includeHidden: Boolean? = false, language: String? = null): Any? {
        var includeHidden_: Any? = includeHidden
        var response: Any? = null
        var status_code: Any? = null
        if (Py.truthy(includeHidden_)) {
            includeHidden_ = "true"
        } else {
            includeHidden_ = "false"
        }
        try {
            response = client.get(("/sitemaps/" + Py.str(sitemapName)), Py.m((if (Py.truthy(language)) Py.dict("Accept-Language", language) else Py.dict())), Py.m(Py.dict("type", type, "jsoncallback", jsonCallback, "includeHidden", includeHidden_)))
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
     * Polls the data for one page of a sitemap.
     *
     * @param sitemapName The name of the sitemap.
     * @param pageID The ID of the page.
     * @param subscriptionID Optional query parameter for the subscription ID.
     * @param includeHidden Whether hidden widgets should be included.
     * @param language Optional language setting (as header).
     *
     * @return The sitemap page (JSON).
     */
    suspend fun getSitemapPage(
        sitemapName: String,
        pageID: String,
        subscriptionID: String? = null,
        includeHidden: Boolean? = false,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getSitemapPageBlocking(sitemapName, pageID, subscriptionID, includeHidden, language)
    }

    private fun getSitemapPageBlocking(sitemapName: String, pageID: String, subscriptionID: String? = null, includeHidden: Boolean? = false, language: String? = null): Any? {
        var includeHidden_: Any? = includeHidden
        var response: Any? = null
        var status_code: Any? = null
        if (Py.truthy(includeHidden_)) {
            includeHidden_ = "true"
        } else {
            includeHidden_ = "false"
        }
        try {
            response = client.get(("/sitemaps/" + Py.str(sitemapName) + "/" + Py.str(pageID)), Py.m((if (Py.truthy(language)) Py.dict("Accept-Language", language) else Py.dict())), Py.m(Py.dict("subscriptionid", subscriptionID, "includeHidden", includeHidden_)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Invalid subscription id has been provided.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Sitemap with requested name does not exist or page does not exist, or page refers to a non-linkable widget.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Sitemap with requested name does not exist or page does not exist, or page refers to a non-linkable widget.")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Invalid subscription id has been provided.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Polls the data for a whole sitemap. Not recommended due to potentially high traffic.
     *
     * @param sitemapName The name of the sitemap.
     * @param subscriptionID Optional query parameter for the subscription ID.
     * @param includeHidden Whether hidden widgets should be included.
     * @param language Optional language setting (as header).
     *
     * @return The complete sitemap (JSON).
     */
    suspend fun getFullSitemap(
        sitemapName: String,
        subscriptionID: String? = null,
        includeHidden: Boolean? = false,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getFullSitemapBlocking(sitemapName, subscriptionID, includeHidden, language)
    }

    private fun getFullSitemapBlocking(sitemapName: String, subscriptionID: String? = null, includeHidden: Boolean? = false, language: String? = null): Any? {
        var includeHidden_: Any? = includeHidden
        var response: Any? = null
        var status_code: Any? = null
        if (Py.truthy(includeHidden_)) {
            includeHidden_ = "true"
        } else {
            includeHidden_ = "false"
        }
        try {
            response = client.get(("/sitemaps/" + Py.str(sitemapName) + "/*"), Py.m((if (Py.truthy(language)) Py.dict("Accept-Language", language) else Py.dict())), Py.m(Py.dict("subscriptionid", subscriptionID, "includeHidden", includeHidden_)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Invalid subscription id has been provided.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Sitemap with requested name does not exist.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Sitemap with requested name does not exist.")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Invalid subscription id has been provided.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Opens the event stream (SSE) of a sitemap subscription for one page.
     *
     * @param subscriptionID The ID of the subscription (see subscribeToSitemapEvents()).
     * @param sitemapName The name of the sitemap.
     * @param pageID The ID of the page (the sitemap name for the home page).
     *
     * @return A SSE stream (requests.Response, read it with iterSSEEvents()) or {"error": ...}.
     */
    suspend fun getSitemapEvents(
        subscriptionID: String,
        sitemapName: String? = null,
        pageID: String? = null,
    ): SSEConnection = withContext(Dispatchers.IO) {
        getSitemapEventsBlocking(subscriptionID, sitemapName, pageID)
    }

    private fun getSitemapEventsBlocking(subscriptionID: String, sitemapName: String? = null, pageID: String? = null): SSEConnection {
        return _openEventStream(path = ("/rest/sitemaps/events/" + Py.str(subscriptionID)), params = Py.m(Py.dict("sitemap", sitemapName, "pageid", pageID))!!, errors = Py.m(Py.dictAny(400, "Missing sitemap or page parameter, or page not linked successfully to the subscription.", 404, "Subscription not found."))!!)
    }

    /**
     * Opens the event stream (SSE) of a sitemap subscription for the whole sitemap.
     * Not recommended due to potentially high traffic.
     *
     * @param subscriptionID The ID of the subscription.
     * @param sitemapName The name of the sitemap.
     *
     * @return A SSE stream (requests.Response) or {"error": ...}.
     */
    suspend fun getFullSitemapEvents(
        subscriptionID: String,
        sitemapName: String? = null,
    ): SSEConnection = withContext(Dispatchers.IO) {
        getFullSitemapEventsBlocking(subscriptionID, sitemapName)
    }

    private fun getFullSitemapEventsBlocking(subscriptionID: String, sitemapName: String? = null): SSEConnection {
        return _openEventStream(path = ("/rest/sitemaps/events/" + Py.str(subscriptionID) + "/*"), params = Py.m(Py.dict("sitemap", sitemapName))!!, errors = Py.m(Py.dictAny(400, "Missing sitemap parameter, or sitemap not linked successfully to the subscription.", 404, "Subscription not found."))!!)
    }

    /**
     * Creates a sitemap event subscription.
     *
     * @return The response to the subscription request (JSON).
     */
    suspend fun subscribeToSitemapEvents(
    ): Any? = withContext(Dispatchers.IO) {
        subscribeToSitemapEventsBlocking()
    }

    private fun subscribeToSitemapEventsBlocking(): Any? {
        var headers: Any? = null
        var location: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        var value: Any? = null
        try {
            response = client.post("/sitemaps/events/subscribe", null, null, null)
            location = null
            if (Py.isDict(response)) {
                location = Py.getOr(response, "location", null)
                if (!Py.truthy(location)) {
                    headers = Py.or(Py.getOr(Py.or(Py.getOr(response, "context", null), Py.dict()), "headers", null), Py.dict())
                    value = Py.getOr(headers, "Location", null)
                    location = (if (Py.truthy(Py.and(Py.isList(value), value))) Py.get(value, 0) else value)
                }
            }
            if (Py.truthy(location)) {
                return Py.dict("message", "Subscription created.", "location", location, "subscriptionID", Py.get(Py.split(Py.rstrip(location, "/"), "/"), -1))
            }
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 503)) {
                return Py.dict("error", "Subscriptions limit reached.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 201)) {
            return Py.dict("message", "Subscription created.")
        } else if (Py.eq(status_code, 503)) {
            return Py.dict("error", "Subscriptions limit reached.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }
}
