// Generated from Items.py of python-openhab-rest-client (tools/py2kt.py) - do not edit.
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
class Items(private val client: OpenHABClient) {

    /**
     * Get all available items.
     *
     * @param type Optional; Item type filter.
     * @param tags Optional; Item tag filter.
     * @param metadata Optional; Metadata selector (default: .*).
     * @param recursive Optional; Whether to fetch group members recursively (default: False).
     * @param fields Optional; Limit to specific fields (comma-separated).
     * @param staticDataOnly Optional; Only returns cached data (default: False).
     * @param language Optional; Language filter for headers "Accept-Language".
     * @param parents Optional; Include the parent groups of each item (openHAB 4.1+).
     *
     * @return A list containing the item data as returned by the API.
     */
    suspend fun getItems(
        type: String? = null,
        tags: String? = null,
        metadata: String? = ".*",
        recursive: Boolean? = false,
        fields: String? = null,
        staticDataOnly: Boolean? = false,
        language: String? = null,
        parents: Boolean? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getItemsBlocking(type, tags, metadata, recursive, fields, staticDataOnly, language, parents)
    }

    private fun getItemsBlocking(type: String? = null, tags: String? = null, metadata: String? = ".*", recursive: Boolean? = false, fields: String? = null, staticDataOnly: Boolean? = false, language: String? = null, parents: Boolean? = null): Any? {
        var headers: Any? = null
        var params: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        params = Py.dict("type", type, "tags", tags, "metadata", metadata, "recursive", Py.lower(Py.str(recursive)), "fields", fields, "staticDataOnly", Py.lower(Py.str(staticDataOnly)), "parents", parents)
        params = Py.withoutNone(params)
        try {
            response = client.get("/items", Py.m(headers), Py.m(params))
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
     * Adds a list of items to the registry or updates the existing items.
     *
     * @param items A list of item data (Dictionary).
     *
     * @return A response object or confirmation that the items were successfully added or updated.
     */
    suspend fun addOrUpdateItems(
        items: List<*>,
    ): Any? = withContext(Dispatchers.IO) {
        addOrUpdateItemsBlocking(items)
    }

    private fun addOrUpdateItemsBlocking(items: List<*>): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.put("/items", Py.m(Py.dict("Content-Type", "application/json", "Accept", "*/*")), Py.dumps(items), null)
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
     * Gets a single item.
     *
     * @param itemName The name of the item.
     * @param metadata Optional; Metadata selector (default: .*).
     * @param recursive Optional; Whether to fetch group members recursively (default: True).
     * @param language Optional; Language filter for headers "Accept-Language".
     *
     * @return A dictionary containing the requested item data.
     */
    suspend fun getItem(
        itemName: String,
        metadata: String? = ".*",
        recursive: Boolean? = true,
        language: String? = null,
        parents: Boolean? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getItemBlocking(itemName, metadata, recursive, language, parents)
    }

    private fun getItemBlocking(itemName: String, metadata: String? = ".*", recursive: Boolean? = true, language: String? = null, parents: Boolean? = null): Any? {
        var headers: Any? = null
        var params: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        params = Py.dict("metadata", metadata, "recursive", Py.lower(Py.str(recursive)), "parents", parents)
        try {
            response = client.get(("/items/" + Py.str(itemName)), Py.m(headers), Py.m(params))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Item not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Item not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Adds a new item to the registry or updates the existing item.
     *
     * @param itemName The name of the item.
     * @param itemData The data of the item (Dictionary).
     * @param language Optional; Language filter for headers "Accept-Language".
     *
     * @return A response object or confirmation that the item was successfully added or updated.
     */
    suspend fun addOrUpdateItem(
        itemName: String,
        itemData: Map<String, Any?>,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        addOrUpdateItemBlocking(itemName, itemData, language)
    }

    private fun addOrUpdateItemBlocking(itemName: String, itemData: Map<String, Any?>, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Content-Type", "application/json", "Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.put(("/items/" + Py.str(itemName)), Py.m(headers), Py.dumps(itemData), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Payload invalid.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Item not found or name in path invalid.")
            } else if (Py.eq(status_code, 405)) {
                return Py.dict("error", "Item not editable.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 201)) {
            return Py.dict("message", "Item created.")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Payload invalid.")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Item not found or name in path invalid.")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "Item not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Sends a command to an item.
     *
     * @param itemName The name of the item.
     * @param command The command to be sent (e.g., ON, OFF).
     * @param source Optional; Source of the command, shown in the event (openHAB 4.1+).
     *
     * @return A response object or confirmation that the command was successfully sent.
     */
    suspend fun sendCommand(
        itemName: String,
        command: String,
        source: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        sendCommandBlocking(itemName, command, source)
    }

    private fun sendCommandBlocking(itemName: String, command: String, source: String? = null): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.post(("/items/" + Py.str(itemName)), Py.m(Py.dict("Content-Type", "text/plain")), Py.str(command), Py.m(Py.dict("source", source)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Invalid command for this item.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Item not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Item not found.")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Invalid command for this item.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Updates the state of an item.
     *
     * @param itemName The name of the item.
     * @param state The state to be signaled for the item.
     *
     * @return A response object or confirmation that the state was successfully updated.
     */
    suspend fun postUpdate(
        itemName: String,
        state: String,
        source: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        postUpdateBlocking(itemName, state, source)
    }

    private fun postUpdateBlocking(itemName: String, state: String, source: String? = null): Any? {
        return updateItemStateBlocking(itemName = itemName, state = state, source = (source as String?))
    }

    /**
     * Removes an item from the registry.
     *
     * @param itemName The name of the item.
     *
     * @return A response object or confirmation that the item was successfully deleted.
     */
    suspend fun deleteItem(
        itemName: String,
    ): Any? = withContext(Dispatchers.IO) {
        deleteItemBlocking(itemName)
    }

    private fun deleteItemBlocking(itemName: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/items/" + Py.str(itemName)), null, null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Item not found or item is not editable.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Item not found or item is not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Adds a new member to a group item.
     *
     * @param itemName The name of the group item.
     * @param memberItemName The name of the member item.
     *
     * @return A response object or confirmation that the group member was successfully added.
     */
    suspend fun addGroupMember(
        itemName: String,
        memberItemName: String,
    ): Any? = withContext(Dispatchers.IO) {
        addGroupMemberBlocking(itemName, memberItemName)
    }

    private fun addGroupMemberBlocking(itemName: String, memberItemName: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.put(("/items/" + Py.str(itemName) + "/members/" + Py.str(memberItemName)), null, null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 405)) {
                return Py.dict("error", "Member item is not editable.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Item or member item not found or item is not of type group item.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Item or member item not found or item is not of type group item.")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "Member item is not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Removes an existing member from a group item.
     *
     * @param itemName The name of the group item.
     * @param memberItemName The name of the member item.
     *
     * @return A response object or confirmation that the group member was successfully removed.
     */
    suspend fun removeGroupMember(
        itemName: String,
        memberItemName: String,
    ): Any? = withContext(Dispatchers.IO) {
        removeGroupMemberBlocking(itemName, memberItemName)
    }

    private fun removeGroupMemberBlocking(itemName: String, memberItemName: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/items/" + Py.str(itemName) + "/members/" + Py.str(memberItemName)), null, null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 405)) {
                return Py.dict("error", "Member item is not editable.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Item or member item not found or item is not of type group item.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Item or member item not found or item is not of type group item.")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "Member item is not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Adds metadata to an item.
     *
     * @param itemName The name of the item.
     * @param namespace The namespace of the metadata.
     * @param metadata A dictionary containing the metadata.
     *
     * @return A response object or confirmation that the metadata was successfully added.
     */
    suspend fun addMetadata(
        itemName: String,
        namespace: String,
        metadata: Map<String, Any?>,
    ): Any? = withContext(Dispatchers.IO) {
        addMetadataBlocking(itemName, namespace, metadata)
    }

    private fun addMetadataBlocking(itemName: String, namespace: String, metadata: Map<String, Any?>): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.put(("/items/" + Py.str(itemName) + "/metadata/" + Py.str(namespace)), Py.m(Py.dict("Content-Type", "application/json")), Py.dumps(metadata), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Metadata value empty.")
            } else if (Py.eq(status_code, 405)) {
                return Py.dict("error", "Metadata not editable.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Item not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 201)) {
            return Py.dict("message", "Created.")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Metadata value empty.")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Item not found.")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "Metadata not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Removes metadata from an item.
     *
     * @param itemName The name of the item.
     * @param namespace The namespace of the metadata.
     *
     * @return A response object or confirmation that the metadata was successfully removed.
     */
    suspend fun removeMetadata(
        itemName: String,
        namespace: String,
    ): Any? = withContext(Dispatchers.IO) {
        removeMetadataBlocking(itemName, namespace)
    }

    private fun removeMetadataBlocking(itemName: String, namespace: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/items/" + Py.str(itemName) + "/metadata/" + Py.str(namespace)), null, null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 405)) {
                return Py.dict("error", "Meta data not editable.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Item not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Item not found.")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "Meta data not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets the namespaces of an item.
     *
     * @param itemName The name of the item.
     * @param language Optional; Language filter for headers "Accept-Language".
     *
     * @return A list of namespaces associated with the item.
     */
    suspend fun getMetadataNamespaces(
        itemName: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getMetadataNamespacesBlocking(itemName, language)
    }

    private fun getMetadataNamespacesBlocking(itemName: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/items/" + Py.str(itemName) + "/metadata/namespaces"), Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Item not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Item not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets the item that defines the requested semantics of an item.
     *
     * @param itemName The name of the item.
     * @param semanticClass The requested semantic class.
     * @param language Optional; Language filter for headers "Accept-Language".
     *
     * @return A dictionary containing the semantic item data.
     */
    suspend fun getSemanticItem(
        itemName: String,
        semanticClass: String,
        language: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        getSemanticItemBlocking(itemName, semanticClass, language)
    }

    private fun getSemanticItemBlocking(itemName: String, semanticClass: String, language: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "application/json")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.get(("/items/" + Py.str(itemName) + "/semantic/" + Py.str(semanticClass)), Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Item not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Item not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Gets the state of an item.
     *
     * @param itemName The name of the item.
     *
     * @return A dictionary containing the current state of the item.
     */
    suspend fun getItemState(
        itemName: String,
    ): Any? = withContext(Dispatchers.IO) {
        getItemStateBlocking(itemName)
    }

    private fun getItemStateBlocking(itemName: String): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Accept", "text/plain")
        try {
            response = client.get(("/items/" + Py.str(itemName) + "/state"), Py.m(headers), null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Item not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Item not found.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Updates the state of an item.
     *
     * @param itemName The name of the item.
     * @param state The new state of the item.
     * @param language Optional; Language filter for headers "Accept-Language".
     *
     * @return A response object or confirmation that the state was successfully updated.
     */
    suspend fun updateItemState(
        itemName: String,
        state: String,
        language: String? = null,
        source: String? = null,
    ): Any? = withContext(Dispatchers.IO) {
        updateItemStateBlocking(itemName, state, language, source)
    }

    private fun updateItemStateBlocking(itemName: String, state: String, language: String? = null, source: String? = null): Any? {
        var headers: Any? = null
        var response: Any? = null
        var status_code: Any? = null
        headers = Py.dict("Content-Type", "text/plain")
        if (Py.truthy(language)) {
            Py.set(headers, "Accept-Language", language)
        }
        try {
            response = client.put(("/items/" + Py.str(itemName) + "/state"), Py.m(headers), Py.str(state), Py.m(Py.dict("source", source)))
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 400)) {
                return Py.dict("error", "Invalid state for this item.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Item not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 202)) {
            return Py.dict("message", "Accepted.")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Item not found.")
        } else if (Py.eq(status_code, 400)) {
            return Py.dict("error", "Invalid state for this item.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Adds a tag to an item.
     *
     * @param itemName The name of the item.
     * @param tag The tag to be added.
     *
     * @return A response object or confirmation that the tag was successfully added.
     */
    suspend fun addTag(
        itemName: String,
        tag: String,
    ): Any? = withContext(Dispatchers.IO) {
        addTagBlocking(itemName, tag)
    }

    private fun addTagBlocking(itemName: String, tag: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.put(("/items/" + Py.str(itemName) + "/tags/" + Py.str(tag)), null, null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 405)) {
                return Py.dict("error", "Item not editable.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Item not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Item not found.")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "Item not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Removes a tag from an item.
     *
     * @param itemName The name of the item.
     * @param tag The tag to be removed.
     *
     * @return A response object or confirmation that the tag was successfully removed.
     */
    suspend fun removeTag(
        itemName: String,
        tag: String,
    ): Any? = withContext(Dispatchers.IO) {
        removeTagBlocking(itemName, tag)
    }

    private fun removeTagBlocking(itemName: String, tag: String): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.delete(("/items/" + Py.str(itemName) + "/tags/" + Py.str(tag)), null, null, null)
            if (Py.isStatus(response)) {
                status_code = Py.get(response, "status")
            } else {
                return response
            }
        } catch (err: OpenHABHTTPError) {
            status_code = err.status
            if (Py.eq(status_code, 405)) {
                return Py.dict("error", "Item not editable.")
            } else if (Py.eq(status_code, 404)) {
                return Py.dict("error", "Item not found.")
            } else {
                return Py.dict("error", ("HTTP error " + Py.str(status_code) + ": " + Py.str(Py.str(err))))
            }
        } catch (err: OpenHABRequestException) {
            return Py.dict("error", ("Request error: " + Py.str(Py.str(err))))
        }
        if (Py.eq(status_code, 200)) {
            return Py.dict("message", "OK")
        } else if (Py.eq(status_code, 404)) {
            return Py.dict("error", "Item not found.")
        } else if (Py.eq(status_code, 405)) {
            return Py.dict("error", "Item not editable.")
        }
        return Py.dict("error", ("Unexpected response: " + Py.str(status_code)))
    }

    /**
     * Remove unused/orphaned metadata.
     *
     * @return A response object or confirmation that orphaned metadata was successfully purged.
     */
    suspend fun purgeOrphanedMetadata(
    ): Any? = withContext(Dispatchers.IO) {
        purgeOrphanedMetadataBlocking()
    }

    private fun purgeOrphanedMetadataBlocking(): Any? {
        var response: Any? = null
        var status_code: Any? = null
        try {
            response = client.post("/items/metadata/purge", null, null, null)
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
