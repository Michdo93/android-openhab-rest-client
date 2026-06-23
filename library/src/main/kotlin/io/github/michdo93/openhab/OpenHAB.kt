package io.github.michdo93.openhab

import io.github.michdo93.openhab.api.*
import io.github.michdo93.openhab.client.OpenHABClient
import io.github.michdo93.openhab.events.*

/**
 * Main entry point for the openHAB Android REST Client.
 *
 * All API classes are lazily created and share the underlying [OpenHABClient].
 *
 * ```kotlin
 * // In your ViewModel or Repository:
 * val openHAB = OpenHAB(
 *     url      = "http://192.168.1.100:8080",
 *     username = "openhab",
 *     password = "habopen",
 *     debug    = BuildConfig.DEBUG,
 * )
 *
 * // Connect (optional, auto-detected on first call)
 * val connected = openHAB.login()
 *
 * // Use API classes
 * val state = openHAB.items.getItemState("LivingRoomLight")
 * openHAB.items.sendCommand("LivingRoomLight", "ON")
 *
 * // Real-time updates via Flow
 * lifecycleScope.launch {
 *     openHAB.itemEvents.itemStateChangedEvent("LivingRoomLight")
 *         .collect()
 *         .collect { json -> updateUI(json) }
 * }
 * ```
 */
class OpenHAB(
    url: String,
    username: String?  = null,
    password: String?  = null,
    token: String?     = null,
    debug: Boolean     = false,
) {
    /** Underlying HTTP client — exposed for advanced use. */
    val client = OpenHABClient(url, username, password, token, debug)

    // ── REST API ──────────────────────────────────────────────────────────────
    val items              by lazy { Items(client) }
    val things             by lazy { Things(client) }
    val rules              by lazy { Rules(client) }
    val actions            by lazy { Actions(client) }
    val addons             by lazy { Addons(client) }
    val audio              by lazy { Audio(client) }
    val auth               by lazy { Auth(client) }
    val channelTypes       by lazy { ChannelTypes(client) }
    val configDescriptions by lazy { ConfigDescriptions(client) }
    val discovery          by lazy { Discovery(client) }
    val iconsets           by lazy { Iconsets(client) }
    val inbox              by lazy { Inbox(client) }
    val links              by lazy { Links(client) }
    val logging            by lazy { Logging(client) }
    val moduleTypes        by lazy { ModuleTypes(client) }
    val persistence        by lazy { Persistence(client) }
    val profileTypes       by lazy { ProfileTypes(client) }
    val services           by lazy { Services(client) }
    val sitemaps           by lazy { Sitemaps(client) }
    val systeminfo         by lazy { Systeminfo(client) }
    val tags               by lazy { Tags(client) }
    val templates          by lazy { Templates(client) }
    val thingTypes         by lazy { ThingTypes(client) }
    val transformations    by lazy { Transformations(client) }
    val ui                 by lazy { UI(client) }
    val uuid               by lazy { UUID(client) }
    val voice              by lazy { Voice(client) }

    // ── SSE / Events ──────────────────────────────────────────────────────────
    val events         by lazy { Events(client) }
    val itemEvents     by lazy { ItemEvents(client) }
    val thingEvents    by lazy { ThingEvents(client) }
    val inboxEvents    by lazy { InboxEvents(client) }
    val linkEvents     by lazy { LinkEvents(client) }
    val channelEvents  by lazy { ChannelEvents(client) }

    // ── Connectivity ──────────────────────────────────────────────────────────
    val isLoggedIn: Boolean get() = client.isLoggedIn
    val isCloud:    Boolean get() = client.isCloud

    /** Verifies connectivity. Returns true if successfully connected. */
    suspend fun login(): Boolean = client.login()
}
