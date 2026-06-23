package io.github.michdo93.openhab.api

import io.github.michdo93.openhab.client.OpenHABClient

/** openHAB Things REST API. */
class Things(private val client: OpenHABClient) {

    suspend fun getThings(summary: Boolean = false, staticDataOnly: Boolean = false, language: String? = null): String =
        client.get("/things", mapOf("summary" to summary.toString(), "staticDataOnly" to staticDataOnly.toString()), language.langHeader())

    suspend fun createThing(json: String, language: String? = null): String =
        client.post("/things", body = json, extraHeaders = language.langHeader())

    suspend fun getThing(uid: String, language: String? = null): String =
        client.get("/things/$uid", extraHeaders = language.langHeader())

    suspend fun updateThing(uid: String, json: String, language: String? = null): String =
        client.put("/things/$uid", body = json, extraHeaders = language.langHeader())

    suspend fun deleteThing(uid: String, force: Boolean = false, language: String? = null): String =
        client.delete("/things/$uid", mapOf("force" to force.toString()), language.langHeader())

    suspend fun updateThingConfiguration(uid: String, configJson: String, language: String? = null): String =
        client.put("/things/$uid/config", body = configJson, extraHeaders = language.langHeader())

    suspend fun getThingConfigStatus(uid: String, language: String? = null): String =
        client.get("/things/$uid/config/status", extraHeaders = language.langHeader())

    suspend fun setThingStatus(uid: String, enabled: Boolean, language: String? = null): String =
        client.put("/things/$uid/enable", body = enabled.toString(), contentType = "text/plain", extraHeaders = language.langHeader())

    suspend fun enableThing(uid: String): String  = setThingStatus(uid, true)
    suspend fun disableThing(uid: String): String = setThingStatus(uid, false)

    suspend fun updateThingFirmware(uid: String, version: String, language: String? = null): String =
        client.put("/things/$uid/firmware/$version", extraHeaders = language.langHeader())

    suspend fun getThingFirmwareStatus(uid: String, language: String? = null): String =
        client.get("/things/$uid/firmware/status", extraHeaders = language.langHeader())

    suspend fun getThingFirmwares(uid: String, language: String? = null): String =
        client.get("/things/$uid/firmwares", extraHeaders = language.langHeader())

    suspend fun getThingStatus(uid: String, language: String? = null): String =
        client.get("/things/$uid/status", extraHeaders = language.langHeader())
}
