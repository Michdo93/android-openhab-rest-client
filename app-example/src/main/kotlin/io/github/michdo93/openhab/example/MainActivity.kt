package io.github.michdo93.openhab.example

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import io.github.michdo93.openhab.OpenHAB
import io.github.michdo93.openhab.client.OpenHABException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch

/**
 * Vollständige Beispiel-Activity für die openHAB Android REST Client Library.
 *
 * Zeigt:
 *  - Verbindungsaufbau und Login
 *  - Items: Zustand lesen, Befehle senden, State ändern
 *  - Things: Auflisten, aktivieren/deaktivieren
 *  - Rules: Aktivieren, deaktivieren, sofort ausführen
 *  - SSE: Live-Updates per Flow mit automatischer Lifecycle-Bindung
 *  - Fehlerbehandlung mit OpenHABException
 *
 * Konfiguration:
 *  Ersetze URL, USERNAME und PASSWORD mit deinen Werten.
 */
class MainActivity : AppCompatActivity() {

    // ── Konfiguration ─────────────────────────────────────────────────────────
    private val URL      = "http://192.168.1.100:8080"
    private val USERNAME = "openhab"
    private val PASSWORD = "habopen"
    // Token-Auth (Alternative zu Username/Password):
    // private val TOKEN = "oh.openhab.xxxx"

    private val LIGHT_ITEM  = "testSwitch"
    private val THING_UID   = "astro:sun:local"
    private val RULE_UID    = "my-automation-rule"

    // ── Library-Instanz ───────────────────────────────────────────────────────
    private val openHAB by lazy {
        OpenHAB(
            url      = URL,
            username = USERNAME,
            password = PASSWORD,
            // token = TOKEN,  // Token-Auth
            debug    = BuildConfig.DEBUG,  // OkHttp-Logging im Debug-Build
        )
    }

    // ── UI-Referenzen ─────────────────────────────────────────────────────────
    private lateinit var tvStatus: TextView
    private lateinit var tvLog:    TextView
    private lateinit var btnConnect: Button
    private lateinit var btnLightOn: Button
    private lateinit var btnLightOff: Button
    private lateinit var btnGetState: Button
    private lateinit var btnGetThings: Button
    private lateinit var btnEnableRule: Button
    private lateinit var btnStartSSE: Button
    private lateinit var btnStopSSE: Button

    private var sseJob: Job? = null

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(buildLayout())
        setupButtons()
    }

    override fun onDestroy() {
        super.onDestroy()
        sseJob?.cancel()
    }

    // ── Button-Handler ────────────────────────────────────────────────────────

    private fun setupButtons() {

        // ── 1. Verbinden ──────────────────────────────────────────────────────
        btnConnect.setOnClickListener {
            lifecycleScope.launch {
                try {
                    log("Verbinde mit $URL …")
                    val ok = openHAB.login()
                    if (ok) {
                        status("✓ Verbunden  |  isCloud=${openHAB.isCloud}")
                        log("Login erfolgreich")
                    } else {
                        status("✗ Verbindung fehlgeschlagen")
                    }
                } catch (e: OpenHABException) {
                    handleError("login", e)
                }
            }
        }

        // ── 2. Licht EIN ──────────────────────────────────────────────────────
        btnLightOn.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val result = openHAB.items.sendCommand(LIGHT_ITEM, "ON")
                    log("sendCommand ON → $result")
                } catch (e: OpenHABException) {
                    handleError("sendCommand ON", e)
                }
            }
        }

        // ── 3. Licht AUS ──────────────────────────────────────────────────────
        btnLightOff.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val result = openHAB.items.sendCommand(LIGHT_ITEM, "OFF")
                    log("sendCommand OFF → $result")
                } catch (e: OpenHABException) {
                    handleError("sendCommand OFF", e)
                }
            }
        }

        // ── 4. Status lesen ───────────────────────────────────────────────────
        btnGetState.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val state = openHAB.items.getItemState(LIGHT_ITEM)
                    log("getItemState($LIGHT_ITEM) = $state")
                    status("Lichtstatus: $state")
                } catch (e: OpenHABException) {
                    handleError("getItemState", e)
                }
            }
        }

        // ── 5. Things auflisten ───────────────────────────────────────────────
        btnGetThings.setOnClickListener {
            lifecycleScope.launch {
                try {
                    // Alle Things (summary = kurze Antwort)
                    val things = openHAB.things.getThings(summary = true)
                    val preview = if (things.length > 200) things.take(200) + "…" else things
                    log("getThings → $preview")

                    // Thing aktivieren/deaktivieren
                    val status = openHAB.things.getThingStatus(THING_UID)
                    log("getThingStatus($THING_UID) = $status")

                    // System-Info
                    val sysInfo = openHAB.systeminfo.getSystemInfo()
                    log("getSystemInfo → ${sysInfo.take(100)}…")

                    // UUID
                    val uuid = openHAB.uuid.getUUID()
                    log("UUID = $uuid")

                } catch (e: OpenHABException) {
                    handleError("getThings", e)
                }
            }
        }

        // ── 6. Regel aktivieren ───────────────────────────────────────────────
        btnEnableRule.setOnClickListener {
            lifecycleScope.launch {
                try {
                    openHAB.rules.enable(RULE_UID)
                    log("enable($RULE_UID) → OK")

                    // Sofort ausführen
                    openHAB.rules.runNow(RULE_UID)
                    log("runNow($RULE_UID) → OK")
                } catch (e: OpenHABException) {
                    handleError("enableRule", e)
                }
            }
        }

        // ── 7. SSE-Stream starten ─────────────────────────────────────────────
        btnStartSSE.setOnClickListener {
            if (sseJob?.isActive == true) {
                log("SSE läuft bereits — stoppen und neu starten")
                sseJob?.cancel()
            }

            sseJob = lifecycleScope.launch {
                log("SSE: Öffne ItemStateChangedEvent für $LIGHT_ITEM …")
                status("SSE aktiv für $LIGHT_ITEM")

                openHAB.itemEvents
                    .itemStateChangedEvent(LIGHT_ITEM)
                    .collect()
                    .catch { e ->
                        log("SSE Fehler: ${e.message}")
                    }
                    .collect { data ->
                        // data = roher JSON-String:
                        // {"topic":"openhab/items/LivingRoomLight/statechanged",
                        //  "payload":"{\"type\":\"OnOff\",\"value\":\"ON\",...}",
                        //  "type":"ItemStateChangedEvent"}
                        log("SSE ⚡ ${data.take(150)}")

                        // JSON mit Moshi oder org.json parsen:
                        // val event = moshi.adapter(ItemEvent::class.java).fromJson(data)
                        // updateLightSwitch(event.newValue == "ON")
                    }

                log("SSE: Stream beendet")
            }
        }

        // ── 8. SSE-Stream stoppen ─────────────────────────────────────────────
        btnStopSSE.setOnClickListener {
            sseJob?.cancel()
            sseJob = null
            status("SSE gestoppt")
            log("SSE: Stream abgebrochen")
        }
    }

    // ── Erweiterte Beispiele (in auskommentierten Blöcken) ───────────────────

    @Suppress("unused")
    private suspend fun advancedExamples() {

        // Items erstellen / aktualisieren
        openHAB.items.addOrUpdateItem(
            name     = "MeinSchalter",
            itemJson = """{"type":"Switch","name":"MeinSchalter","label":"Mein Schalter"}"""
        )

        // Metadaten setzen
        openHAB.items.addMetadata(
            name         = LIGHT_ITEM,
            namespace    = "homekit",
            metadataJson = """{"value":"Lighting","config":{}}"""
        )

        // Persistence: historische Daten
        val history = openHAB.persistence.getItemPersistenceData(
            item      = LIGHT_ITEM,
            serviceId = "rrd4j",
            startTime = "2026-01-01T00:00:00.000+0100",
            endTime   = "2026-01-02T00:00:00.000+0100",
        )
        log("History: ${history.take(100)}")

        // Logging konfigurieren
        openHAB.logging.modifyOrAddLogger(
            name  = "org.openhab.core",
            level = "DEBUG"
        )

        // Alle SSE-Topics auf einmal hören
        val allEvents = openHAB.events.getEvents("openhab/items,openhab/things")
        lifecycleScope.launch {
            allEvents.collect()
                .collect { data -> log("Global event: ${data.take(80)}") }
        }

        // Sitemap-Events (für UI-Updates)
        val subId    = openHAB.sitemaps.subscribeToSitemapEvents()
        val sseMap   = openHAB.sitemaps.getSitemapEvents(
            subId       = subId,
            sitemapName = "default"
        )
        lifecycleScope.launch {
            sseMap.collect().collect { data -> log("Sitemap event: $data") }
        }
    }

    // ── UI-Hilfsmethoden ──────────────────────────────────────────────────────

    private fun log(msg: String) {
        val current = tvLog.text.toString()
        val lines   = current.lines().takeLast(30)  // max 30 Zeilen
        runOnUiThread {
            tvLog.text = (lines + msg).joinToString("\n")
        }
        Log.d("openHAB", msg)
    }

    private fun status(msg: String) = runOnUiThread { tvStatus.text = msg }

    private fun handleError(ctx: String, e: OpenHABException) {
        val msg = "[$ctx] HTTP ${e.statusCode}: ${e.message}"
        log("✗ $msg")
        Log.e("openHAB", msg, e)
    }

    // ── Layout (programmatisch, kein XML nötig für das Beispiel) ─────────────

    private fun buildLayout(): android.view.View {
        val ctx  = this
        val root = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 48, 24, 24)
        }

        tvStatus = TextView(ctx).apply {
            text     = "Nicht verbunden"
            textSize = 16f
            setPadding(0, 0, 0, 16)
        }
        root.addView(tvStatus)

        fun btn(label: String, init: Button.() -> Unit = {}): Button {
            val b = Button(ctx).apply {
                text = label
                init()
            }
            root.addView(b, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 4, 0, 4) })
            return b
        }

        btnConnect  = btn("🔌 Verbinden")
        btnLightOn  = btn("💡 Licht EIN")
        btnLightOff = btn("🌙 Licht AUS")
        btnGetState = btn("📊 Status lesen")
        btnGetThings = btn("🏠 Things + System-Info")
        btnEnableRule = btn("⚙️ Regel aktivieren + ausführen")
        btnStartSSE  = btn("📡 SSE starten (Live-Updates)")
        btnStopSSE   = btn("⛔ SSE stoppen")

        tvLog = TextView(ctx).apply {
            text     = "Log:\n"
            textSize = 11f
            fontFeatureSettings = "mono"
            setPadding(0, 16, 0, 0)
            setTextIsSelectable(true)
        }
        root.addView(ScrollView(ctx).also { sv ->
            sv.addView(tvLog)
        }, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 400
        ))

        return root
    }
}
