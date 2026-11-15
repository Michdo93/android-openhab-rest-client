// Generated from Tags.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Tags(private val client: OpenHABClient) {

    /**
     * Get all available semantic tags.
     *
     * @param language Optional headers for language setting.
     *
     * @return A list of semantic tags (JSON).
     */
    suspend fun getTags(
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getTagsBlocking(language)
    }

    private fun getTagsBlocking(language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get("/tags", Py.m(headers), null)
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
     * Creates a new semantic tag and adds it to the registry.
     *
     * @param tagData The data object for the tag to be created.
     * @param language Optional headers for language setting.
     *
     * @return The response to the tag creation request (JSON).
     */
    suspend fun createTag(
        tagData: Any?,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        createTagBlocking(tagData, language)
    }

    private fun createTagBlocking(tagData: Any?, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Content-Type", "application/json", "Accept", "*/*")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.post("/tags", Py.m(headers), Py.dumps(tagData), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "The tag identifier is invalid or the tag label is missing.")
            } else if (Py.eq(status_code, 409)) {
                return Py.dict("error", "A tag with the same identifier already exists.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 201)) {
            return Py.dict("message", "Created")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "The tag identifier is invalid or the tag label is missing.")
        } else if (Py.eq(status_code, 409)) {
            return Py.dict("error", "A tag with the same identifier already exists.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets a semantic tag and its sub-tags.
     *
     * @param tagID The ID of the tag to retrieve.
     * @param language Optional headers for language setting.
     *
     * @return The tag object and its sub-tags (JSON).
     */
    suspend fun getTag(
        tagID: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getTagBlocking(tagID, language)
    }

    private fun getTagBlocking(tagID: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/tags/" + Py.str(tagID)), Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Semantic tag not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Semantic tag not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Updates a semantic tag.
     *
     * @param tagID The ID of the tag to be updated.
     * @param tagData The new tag data.
     * @param language Optional headers for language setting.
     *
     * @return The response to the tag update request (JSON).
     */
    suspend fun updateTag(
        tagID: String,
        tagData: Any?,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        updateTagBlocking(tagID, tagData, language)
    }

    private fun updateTagBlocking(tagID: String, tagData: Any?, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Content-Type", "application/json", "Accept", "*/*")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.put(("/tags/" + Py.str(tagID)), Py.m(headers), Py.dumps(tagData), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 405)) {
                return Py.dict("error", "Semantic tag not editable.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Semantic tag not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Semantic tag not found.")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "Semantic tag not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Removes a semantic tag and its sub-tags from the registry.
     *
     * @param tagID The ID of the tag to be removed.
     * @param language Optional headers for language setting.
     *
     * @return The response to the tag deletion request (JSON).
     */
    suspend fun deleteTag(
        tagID: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        deleteTagBlocking(tagID, language)
    }

    private fun deleteTagBlocking(tagID: String, language: String? = null): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/tags/" + Py.str(tagID)), Py.m((if (Py.truthy(language)) Py.dict("Accept-Language", language) else Py.dict())), null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 405)) {
                return Py.dict("error", "Semantic tag not removable.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Semantic tag not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK, was deleted.")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Semantic tag not found.")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "Semantic tag not removable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }
}
