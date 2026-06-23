package io.github.michdo93.openhab.api

import io.github.michdo93.openhab.client.OpenHABClient
import io.github.michdo93.openhab.client.SseSession

// ─────────────────────────────────────────────────────────────────────────────
// Actions
// ─────────────────────────────────────────────────────────────────────────────

/** openHAB Actions REST API. */
class Actions(private val c: OpenHABClient) {
    suspend fun getActions(thingUID: String, language: String? = null): String =
        c.get("/actions/$thingUID", extraHeaders = language.langHeader())
    suspend fun executeAction(thingUID: String, actionUID: String, inputsJson: String, language: String? = null): String =
        c.post("/actions/$thingUID/$actionUID", body = inputsJson, extraHeaders = language.langHeader())
}

// ─────────────────────────────────────────────────────────────────────────────
// Addons
// ─────────────────────────────────────────────────────────────────────────────

/** openHAB Addons REST API. */
class Addons(private val c: OpenHABClient) {
    suspend fun getAddons(serviceId: String? = null, language: String? = null): String =
        c.get("/addons", mapOf("serviceId" to serviceId), language.langHeader())
    suspend fun getAddon(id: String, serviceId: String? = null, language: String? = null): String =
        c.get("/addons/$id", mapOf("serviceId" to serviceId), language.langHeader())
    suspend fun getAddonConfig(id: String, serviceId: String? = null): String =
        c.get("/addons/$id/config", mapOf("serviceId" to serviceId))
    suspend fun updateAddonConfig(id: String, configJson: String, serviceId: String? = null): String =
        c.put("/addons/$id/config", body = configJson, params = mapOf("serviceId" to serviceId))
    suspend fun installAddon(id: String, serviceId: String? = null): String =
        c.post("/addons/$id/install", params = mapOf("serviceId" to serviceId))
    suspend fun uninstallAddon(id: String, serviceId: String? = null): String =
        c.post("/addons/$id/uninstall", params = mapOf("serviceId" to serviceId))
    suspend fun getAddonServices(language: String? = null): String =
        c.get("/addons/services", extraHeaders = language.langHeader())
    suspend fun getAddonSuggestions(language: String? = null): String =
        c.get("/addons/suggestions", extraHeaders = language.langHeader())
    suspend fun getAddonTypes(serviceId: String? = null, language: String? = null): String =
        c.get("/addons/types", mapOf("serviceId" to serviceId), language.langHeader())
    suspend fun installAddonFromUrl(url: String): String =
        c.post("/addons/url", body = url, contentType = "text/plain")
}

// ─────────────────────────────────────────────────────────────────────────────
// Audio
// ─────────────────────────────────────────────────────────────────────────────

/** openHAB Audio REST API. */
class Audio(private val c: OpenHABClient) {
    suspend fun getDefaultSink(language: String? = null): String   = c.get("/audio/defaultsink",   extraHeaders = language.langHeader())
    suspend fun getDefaultSource(language: String? = null): String = c.get("/audio/defaultsource", extraHeaders = language.langHeader())
    suspend fun getSinks(language: String? = null): String         = c.get("/audio/sinks",         extraHeaders = language.langHeader())
    suspend fun getSources(language: String? = null): String       = c.get("/audio/sources",       extraHeaders = language.langHeader())
}

// ─────────────────────────────────────────────────────────────────────────────
// Auth
// ─────────────────────────────────────────────────────────────────────────────

/** openHAB Auth REST API. */
class Auth(private val c: OpenHABClient) {
    suspend fun getApiTokens(): String                        = c.get("/auth/apitokens")
    suspend fun revokeApiToken(name: String): String          = c.delete("/auth/apitokens/$name")
    suspend fun logout(refreshToken: String, sessionId: String): String =
        c.delete("/auth/logout", mapOf("refreshToken" to refreshToken, "sessionId" to sessionId))
    suspend fun getSessions(): String                         = c.get("/auth/sessions")
    suspend fun getToken(
        grantType: String? = null, code: String? = null, redirectUri: String? = null,
        clientId: String? = null, refreshToken: String? = null, codeVerifier: String? = null,
    ): String {
        val body = buildString {
            if (!grantType.isNullOrEmpty())    append("grant_type=$grantType")
            if (!code.isNullOrEmpty())         append("&code=$code")
            if (!redirectUri.isNullOrEmpty())  append("&redirect_uri=$redirectUri")
            if (!clientId.isNullOrEmpty())     append("&client_id=$clientId")
            if (!refreshToken.isNullOrEmpty()) append("&refresh_token=$refreshToken")
            if (!codeVerifier.isNullOrEmpty()) append("&code_verifier=$codeVerifier")
        }
        return c.post("/auth/token", body = body, contentType = "application/x-www-form-urlencoded")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ChannelTypes
// ─────────────────────────────────────────────────────────────────────────────

class ChannelTypes(private val c: OpenHABClient) {
    suspend fun getChannelTypes(prefixes: String? = null, language: String? = null): String =
        c.get("/channel-types", mapOf("prefixes" to prefixes), language.langHeader())
    suspend fun getChannelType(uid: String, language: String? = null): String =
        c.get("/channel-types/$uid", extraHeaders = language.langHeader())
    suspend fun getLinkableItemTypes(uid: String): String =
        c.get("/channel-types/$uid/linkableItemTypes")
}

// ─────────────────────────────────────────────────────────────────────────────
// ConfigDescriptions
// ─────────────────────────────────────────────────────────────────────────────

class ConfigDescriptions(private val c: OpenHABClient) {
    suspend fun getConfigDescriptions(scheme: String? = null, language: String? = null): String =
        c.get("/config-descriptions", mapOf("scheme" to scheme), language.langHeader())
    suspend fun getConfigDescription(uri: String, language: String? = null): String =
        c.get("/config-descriptions/${java.net.URLEncoder.encode(uri, "UTF-8")}", extraHeaders = language.langHeader())
}

// ─────────────────────────────────────────────────────────────────────────────
// Discovery
// ─────────────────────────────────────────────────────────────────────────────

class Discovery(private val c: OpenHABClient) {
    suspend fun getDiscoveryBindings(): String = c.get("/discovery")
    suspend fun getBindingInfo(bindingId: String, language: String? = null): String =
        c.get("/bindings/$bindingId", extraHeaders = language.langHeader())
    suspend fun startBindingScan(bindingId: String, input: String? = null): String =
        c.post("/discovery/bindings/$bindingId/scan", body = input)
}

// ─────────────────────────────────────────────────────────────────────────────
// Iconsets
// ─────────────────────────────────────────────────────────────────────────────

class Iconsets(private val c: OpenHABClient) {
    suspend fun getIconsets(language: String? = null): String =
        c.get("/iconsets", extraHeaders = language.langHeader())
}

// ─────────────────────────────────────────────────────────────────────────────
// Inbox
// ─────────────────────────────────────────────────────────────────────────────

class Inbox(private val c: OpenHABClient) {
    suspend fun getDiscoveredThings(includeIgnored: Boolean = true): String =
        c.get("/inbox", mapOf("includeIgnored" to includeIgnored.toString()))
    suspend fun removeDiscoveryResult(uid: String): String = c.delete("/inbox/$uid")
    suspend fun approveDiscoveryResult(uid: String, label: String, newId: String? = null, language: String? = null): String =
        c.post("/inbox/$uid/approve", body = label, contentType = "text/plain",
            params = mapOf("newThingId" to newId), extraHeaders = language.langHeader())
    suspend fun ignoreDiscoveryResult(uid: String): String   = c.post("/inbox/$uid/ignore")
    suspend fun unignoreDiscoveryResult(uid: String): String = c.post("/inbox/$uid/unignore")
}

// ─────────────────────────────────────────────────────────────────────────────
// Links
// ─────────────────────────────────────────────────────────────────────────────

class Links(private val c: OpenHABClient) {
    suspend fun getLinks(channelUID: String? = null, itemName: String? = null): String =
        c.get("/links", mapOf("channelUID" to channelUID, "itemName" to itemName))
    suspend fun getLink(item: String, channelUID: String): String =
        c.get("/links/$item/${channelUID.encUrl()}")
    suspend fun linkItemToChannel(item: String, channelUID: String, configJson: String): String =
        c.put("/links/$item/${channelUID.encUrl()}", body = configJson)
    suspend fun unlinkItemFromChannel(item: String, channelUID: String): String =
        c.delete("/links/$item/${channelUID.encUrl()}")
    suspend fun deleteAllLinks(obj: String): String = c.delete("/links/$obj")
    suspend fun getOrphanLinks(): String             = c.get("/links/orphan")
    suspend fun purgeUnusedLinks(): String           = c.post("/links/purge")
    private fun String.encUrl() = java.net.URLEncoder.encode(this, "UTF-8")
}

// ─────────────────────────────────────────────────────────────────────────────
// Logging
// ─────────────────────────────────────────────────────────────────────────────

class Logging(private val c: OpenHABClient) {
    suspend fun getLoggers(): String = c.get("/loggers")
    suspend fun getLogger(name: String): String = c.get("/loggers/$name")
    suspend fun modifyOrAddLogger(name: String, level: String): String =
        c.put("/loggers/$name", body = """{"level":"$level"}""")
    suspend fun removeLogger(name: String): String = c.delete("/loggers/$name")
}

// ─────────────────────────────────────────────────────────────────────────────
// ModuleTypes
// ─────────────────────────────────────────────────────────────────────────────

class ModuleTypes(private val c: OpenHABClient) {
    suspend fun getModuleTypes(tags: String? = null, typeFilter: String? = null, language: String? = null): String =
        c.get("/module-types", mapOf("tags" to tags, "type" to typeFilter), language.langHeader())
    suspend fun getModuleType(uid: String, language: String? = null): String =
        c.get("/module-types/$uid", extraHeaders = language.langHeader())
}

// ─────────────────────────────────────────────────────────────────────────────
// Persistence
// ─────────────────────────────────────────────────────────────────────────────

class Persistence(private val c: OpenHABClient) {
    suspend fun getServices(language: String? = null): String =
        c.get("/persistence", extraHeaders = language.langHeader())
    suspend fun getServiceConfiguration(serviceId: String): String = c.get("/persistence/$serviceId")
    suspend fun setServiceConfiguration(serviceId: String, json: String): String =
        c.put("/persistence/$serviceId", body = json)
    suspend fun deleteServiceConfiguration(serviceId: String): String = c.delete("/persistence/$serviceId")
    suspend fun getItemsFromService(serviceId: String? = null): String =
        c.get("/persistence/items", mapOf("serviceId" to serviceId))
    suspend fun getItemPersistenceData(
        item: String, serviceId: String,
        startTime: String? = null, endTime: String? = null,
        page: Int = 1, pageLength: Int = 50,
        boundary: Boolean = false, itemState: Boolean = false,
    ): String = c.get(
        "/persistence/items/$item",
        mapOf("serviceId" to serviceId, "starttime" to startTime, "endtime" to endTime,
            "page" to page.toString(), "pagelength" to pageLength.toString(),
            "boundary" to boundary.toString(), "itemState" to itemState.toString())
    )
    suspend fun storeItemData(item: String, time: String, state: String, serviceId: String? = null): String =
        c.put("/persistence/items/$item", body = state, contentType = "text/plain",
            params = mapOf("time" to time, "serviceId" to serviceId))
    suspend fun deleteItemData(item: String, startTime: String, endTime: String, serviceId: String): String =
        c.delete("/persistence/items/$item",
            mapOf("serviceId" to serviceId, "starttime" to startTime, "endtime" to endTime))
}

// ─────────────────────────────────────────────────────────────────────────────
// ProfileTypes
// ─────────────────────────────────────────────────────────────────────────────

class ProfileTypes(private val c: OpenHABClient) {
    suspend fun getProfileTypes(channelTypeUID: String? = null, itemType: String? = null, language: String? = null): String =
        c.get("/profile-types", mapOf("channelTypeUID" to channelTypeUID, "itemType" to itemType), language.langHeader())
}

// ─────────────────────────────────────────────────────────────────────────────
// Services
// ─────────────────────────────────────────────────────────────────────────────

class Services(private val c: OpenHABClient) {
    suspend fun getServices(language: String? = null): String = c.get("/services", extraHeaders = language.langHeader())
    suspend fun getService(id: String, language: String? = null): String = c.get("/services/$id", extraHeaders = language.langHeader())
    suspend fun getServiceConfig(id: String): String = c.get("/services/$id/config")
    suspend fun updateServiceConfig(id: String, json: String, language: String? = null): String =
        c.put("/services/$id/config", body = json, extraHeaders = language.langHeader())
    suspend fun deleteServiceConfig(id: String): String = c.delete("/services/$id/config")
    suspend fun getServiceContexts(id: String, language: String? = null): String =
        c.get("/services/$id/contexts", extraHeaders = language.langHeader())
}

// ─────────────────────────────────────────────────────────────────────────────
// Sitemaps
// ─────────────────────────────────────────────────────────────────────────────

class Sitemaps(private val c: OpenHABClient) {
    suspend fun getSitemaps(): String = c.get("/sitemaps")
    suspend fun getSitemap(name: String, type: String? = null, includeHidden: Boolean = false, language: String? = null): String =
        c.get("/sitemaps/$name", mapOf("type" to type, "includeHidden" to includeHidden.toString()), language.langHeader())
    suspend fun getSitemapPage(name: String, pageId: String, subscriptionId: String? = null, includeHidden: Boolean = false, language: String? = null): String =
        c.get("/sitemaps/$name/$pageId", mapOf("subscriptionid" to subscriptionId, "includeHidden" to includeHidden.toString()), language.langHeader())
    fun getSitemapEvents(subId: String, sitemapName: String? = null, pageId: String? = null): SseSession {
        var url = "${c.baseUrl.trimEnd('/')}/rest/sitemaps/events/$subId"
        if (!sitemapName.isNullOrEmpty()) url += "?sitemap=$sitemapName"
        if (!pageId.isNullOrEmpty()) url += (if (sitemapName.isNullOrEmpty()) "?" else "&") + "pageid=$pageId"
        return c.sse(url)
    }
    suspend fun subscribeToSitemapEvents(): String = c.post("/sitemaps/events/subscribe")
}

// ─────────────────────────────────────────────────────────────────────────────
// Systeminfo
// ─────────────────────────────────────────────────────────────────────────────

class Systeminfo(private val c: OpenHABClient) {
    suspend fun getSystemInfo(): String = c.get("/systeminfo")
    suspend fun getUoMInfo(): String    = c.get("/systeminfo/uom")
}

// ─────────────────────────────────────────────────────────────────────────────
// Tags
// ─────────────────────────────────────────────────────────────────────────────

class Tags(private val c: OpenHABClient) {
    suspend fun getTags(language: String? = null): String = c.get("/tags", extraHeaders = language.langHeader())
    suspend fun createTag(json: String, language: String? = null): String =
        c.post("/tags", body = json, extraHeaders = language.langHeader())
    suspend fun getTag(id: String, language: String? = null): String =
        c.get("/tags/$id", extraHeaders = language.langHeader())
    suspend fun updateTag(id: String, json: String, language: String? = null): String =
        c.put("/tags/$id", body = json, extraHeaders = language.langHeader())
    suspend fun deleteTag(id: String, language: String? = null): String =
        c.delete("/tags/$id", extraHeaders = language.langHeader())
}

// ─────────────────────────────────────────────────────────────────────────────
// Templates
// ─────────────────────────────────────────────────────────────────────────────

class Templates(private val c: OpenHABClient) {
    suspend fun getTemplates(language: String? = null): String = c.get("/templates", extraHeaders = language.langHeader())
    suspend fun getTemplate(uid: String, language: String? = null): String =
        c.get("/templates/$uid", extraHeaders = language.langHeader())
}

// ─────────────────────────────────────────────────────────────────────────────
// ThingTypes
// ─────────────────────────────────────────────────────────────────────────────

class ThingTypes(private val c: OpenHABClient) {
    suspend fun getThingTypes(bindingId: String? = null, language: String? = null): String =
        c.get("/thing-types", mapOf("bindingId" to bindingId), language.langHeader())
    suspend fun getThingType(uid: String, language: String? = null): String =
        c.get("/thing-types/$uid", extraHeaders = language.langHeader())
}

// ─────────────────────────────────────────────────────────────────────────────
// Transformations
// ─────────────────────────────────────────────────────────────────────────────

class Transformations(private val c: OpenHABClient) {
    suspend fun getTransformations(): String = c.get("/transformations")
    suspend fun getTransformation(uid: String): String = c.get("/transformations/$uid")
    suspend fun updateTransformation(uid: String, json: String): String =
        c.put("/transformations/$uid", body = json)
    suspend fun deleteTransformation(uid: String): String = c.delete("/transformations/$uid")
    suspend fun getTransformationServices(): String = c.get("/transformations/services")
}

// ─────────────────────────────────────────────────────────────────────────────
// UI
// ─────────────────────────────────────────────────────────────────────────────

class UI(private val c: OpenHABClient) {
    suspend fun getUIComponents(namespace: String, summary: Boolean = false): String =
        c.get("/ui/components/$namespace", mapOf("summary" to summary.toString()))
    suspend fun addUIComponent(namespace: String, json: String): String =
        c.post("/ui/components/$namespace", body = json)
    suspend fun getUIComponent(namespace: String, uid: String): String =
        c.get("/ui/components/$namespace/$uid")
    suspend fun updateUIComponent(namespace: String, uid: String, json: String): String =
        c.put("/ui/components/$namespace/$uid", body = json)
    suspend fun deleteUIComponent(namespace: String, uid: String): String =
        c.delete("/ui/components/$namespace/$uid")
    suspend fun getUITiles(): String = c.get("/ui/tiles")
}

// ─────────────────────────────────────────────────────────────────────────────
// UUID
// ─────────────────────────────────────────────────────────────────────────────

class UUID(private val c: OpenHABClient) {
    suspend fun getUUID(): String = c.get("/uuid")
}

// ─────────────────────────────────────────────────────────────────────────────
// Voice
// ─────────────────────────────────────────────────────────────────────────────

class Voice(private val c: OpenHABClient) {
    suspend fun getDefaultVoice(): String = c.get("/voice/defaultvoice")
    suspend fun startDialog(
        sourceId: String, ksId: String? = null, sttId: String? = null, ttsId: String? = null,
        voiceId: String? = null, hliIds: String? = null, sinkId: String? = null,
        keyword: String? = null, listeningItem: String? = null,
    ): String = c.post("/voice/dialog/start", params = mapOf(
        "sourceId" to sourceId, "ksId" to ksId, "sttId" to sttId, "ttsId" to ttsId,
        "voiceId" to voiceId, "hliIds" to hliIds, "sinkId" to sinkId,
        "keyword" to keyword, "listeningItem" to listeningItem))
    suspend fun stopDialog(sourceId: String): String =
        c.post("/voice/dialog/stop", params = mapOf("sourceId" to sourceId))
    suspend fun getInterpreters(language: String? = null): String =
        c.get("/voice/interpreters", extraHeaders = language.langHeader())
    suspend fun interpretText(text: String, language: String? = null): String =
        c.post("/voice/interpreters", body = text, contentType = "text/plain", extraHeaders = language.langHeader())
    suspend fun getInterpreter(id: String, language: String? = null): String =
        c.get("/voice/interpreters/$id", extraHeaders = language.langHeader())
    suspend fun interpretTextBatch(text: String, ids: String, language: String? = null): String =
        c.post("/voice/interpreters/$ids", body = text, contentType = "text/plain", extraHeaders = language.langHeader())
    suspend fun listenAndAnswer(
        sourceId: String, sttId: String, ttsId: String, voiceId: String,
        hliIds: String? = null, sinkId: String? = null, listeningItem: String? = null,
    ): String = c.post("/voice/listenandanswer", params = mapOf(
        "sourceId" to sourceId, "sttId" to sttId, "ttsId" to ttsId, "voiceId" to voiceId,
        "hliIds" to hliIds, "sinkId" to sinkId, "listeningItem" to listeningItem))
    suspend fun sayText(text: String, voiceId: String, sinkId: String, volume: String = "100"): String =
        c.post("/voice/say", params = mapOf("text" to text, "voiceId" to voiceId, "sinkId" to sinkId, "volume" to volume))
    suspend fun getVoices(): String = c.get("/voice/voices")
}

// ─────────────────────────────────────────────────────────────────────────────
// Internal extension — language header helper used by all classes
// ─────────────────────────────────────────────────────────────────────────────

internal fun String?.langHeader(): Map<String, String> =
    if (isNullOrEmpty()) emptyMap() else mapOf("Accept-Language" to this!!)
