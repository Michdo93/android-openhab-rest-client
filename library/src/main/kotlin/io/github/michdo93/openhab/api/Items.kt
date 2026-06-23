package io.github.michdo93.openhab.api

import io.github.michdo93.openhab.client.OpenHABClient

/**
 * openHAB Items REST API.
 *
 * ```kotlin
 * val items = Items(client)
 * val all   = items.getItems()
 * items.sendCommand("LivingRoomLight", "ON")
 * val state = items.getItemState("LivingRoomLight")
 * ```
 */
class Items(private val client: OpenHABClient) {

    suspend fun getItems(
        type: String?         = null,
        tags: String?         = null,
        metadata: String      = ".*",
        recursive: Boolean    = false,
        fields: String?       = null,
        staticDataOnly: Boolean = false,
        language: String?     = null,
    ): String = client.get(
        path   = "/items",
        params = mapOf(
            "type"          to type,
            "tags"          to tags,
            "metadata"      to metadata,
            "recursive"     to recursive.toString(),
            "fields"        to fields,
            "staticDataOnly" to staticDataOnly.toString(),
        ),
        extraHeaders = language.langHeader()
    )

    suspend fun addOrUpdateItems(itemsJson: String): String =
        client.put("/items", body = itemsJson)

    suspend fun getItem(
        name: String,
        metadata: String   = ".*",
        recursive: Boolean = true,
        language: String?  = null,
    ): String = client.get(
        path   = "/items/$name",
        params = mapOf("metadata" to metadata, "recursive" to recursive.toString()),
        extraHeaders = language.langHeader()
    )

    suspend fun addOrUpdateItem(
        name: String,
        itemJson: String,
        language: String? = null,
    ): String = client.put(
        path         = "/items/$name",
        body         = itemJson,
        extraHeaders = language.langHeader()
    )

    suspend fun sendCommand(name: String, command: String): String =
        client.post("/items/$name", body = command, contentType = "text/plain")

    suspend fun postUpdate(name: String, state: String): String =
        updateItemState(name, state)

    suspend fun deleteItem(name: String): String =
        client.delete("/items/$name")

    suspend fun addGroupMember(name: String, member: String): String =
        client.put("/items/$name/members/$member")

    suspend fun removeGroupMember(name: String, member: String): String =
        client.delete("/items/$name/members/$member")

    suspend fun addMetadata(name: String, namespace: String, metadataJson: String): String =
        client.put("/items/$name/metadata/$namespace", body = metadataJson)

    suspend fun removeMetadata(name: String, namespace: String): String =
        client.delete("/items/$name/metadata/$namespace")

    suspend fun getMetadataNamespaces(name: String, language: String? = null): String =
        client.get("/items/$name/metadata/namespaces", extraHeaders = language.langHeader())

    suspend fun getSemanticItem(name: String, semanticClass: String, language: String? = null): String =
        client.get("/items/$name/semantic/$semanticClass", extraHeaders = language.langHeader())

    suspend fun getItemState(name: String): String =
        client.get("/items/$name/state", extraHeaders = mapOf("Accept" to "text/plain"))

    suspend fun updateItemState(name: String, state: String, language: String? = null): String =
        client.put(
            path         = "/items/$name/state",
            body         = state,
            contentType  = "text/plain",
            extraHeaders = language.langHeader()
        )

    suspend fun addTag(name: String, tag: String): String =
        client.put("/items/$name/tags/$tag")

    suspend fun removeTag(name: String, tag: String): String =
        client.delete("/items/$name/tags/$tag")

    suspend fun purgeOrphanedMetadata(): String =
        client.post("/items/metadata/purge")
}
