# openhab-rest-client-android

Kotlin Android Library für die openHAB REST API.  
Mirrors **python-openhab-rest-client**: gleiche Klassen, gleiche Methoden.

**Technologie-Stack:** Kotlin · OkHttp 4 · Moshi · Coroutines · Flow

---

## Installation

### Gradle (empfohlen)
```kotlin
// settings.gradle.kts
repositories { maven("https://jitpack.io") }

// build.gradle.kts
implementation("com.github.Michdo93:android-openhab-rest-client:v1.0.0")
```

### Lokales AAR
```kotlin
implementation(files("libs/openhab-rest-client-android-1.0.0.aar"))
```

---

## Quick Start

```kotlin
import io.github.michdo93.openhab.OpenHAB

val openHAB = OpenHAB(
    url      = "http://192.168.1.100:8080",
    username = "openhab",
    password = "habopen",
    debug    = BuildConfig.DEBUG,
)

// Coroutine-kontext (ViewModel, lifecycleScope, etc.)
lifecycleScope.launch {
    // Items
    openHAB.items.sendCommand("LivingRoomLight", "ON")
    val state = openHAB.items.getItemState("LivingRoomLight")

    // Things
    openHAB.things.enableThing("astro:sun:local")

    // Rules
    openHAB.rules.runNow("my-rule-uid")

    // SSE — Live-Updates als Flow
    openHAB.itemEvents
        .itemStateChangedEvent("LivingRoomLight")
        .collect()
        .collect { json -> /* json = roher Event-String */ }
}
```

---

## Alle API-Klassen

Alle Klassen werden über `OpenHAB.` aufgerufen.  
Jede Methode ist eine `suspend fun` und gibt rohe JSON-Strings zurück.

| Klasse | Beschreibung |
|---|---|
| `items` | Items verwalten, Befehle senden, Status lesen |
| `things` | Things verwalten, aktivieren/deaktivieren |
| `rules` | Regeln verwalten, ausführen |
| `actions` | Thing-Aktionen ausführen |
| `addons` | Erweiterungen installieren/verwalten |
| `audio` | Audioquellen und Senken |
| `auth` | Tokens, Sessions |
| `channelTypes` | Kanaltypen |
| `configDescriptions` | Konfigurationsbeschreibungen |
| `discovery` | Geräteerkennung |
| `iconsets` | Icon-Sets |
| `inbox` | Entdeckte Geräte |
| `links` | Item-Channel-Verknüpfungen |
| `logging` | Logger konfigurieren |
| `moduleTypes` | Regelmodule |
| `persistence` | Historische Daten |
| `profileTypes` | Linkprofile |
| `services` | Konfigurierbare Services |
| `sitemaps` | UI-Sitemaps |
| `systeminfo` | Serverinformationen |
| `tags` | Semantische Tags |
| `templates` | Regel-Templates |
| `thingTypes` | Gerätetypen |
| `transformations` | Datentransformationen |
| `ui` | UI-Komponenten |
| `uuid` | System-Identifier |
| `voice` | Sprachsteuerung |
| `events` | Alle Events (SSE) |
| `itemEvents` | Item-Events (SSE) |
| `thingEvents` | Thing-Events (SSE) |
| `inboxEvents` | Inbox-Events (SSE) |
| `linkEvents` | Link-Events (SSE) |
| `channelEvents` | Kanal-Events (SSE) |

---

## JSON parsen

Die Library gibt rohe JSON-Strings zurück.  
Moshi ist bereits als Abhängigkeit enthalten:

```kotlin
val moshi  = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
val items  = moshi.adapter<List<Map<String,Any>>>(
    Types.newParameterizedType(List::class.java, Map::class.java)
).fromJson(openHAB.items.getItems())
```

---

## Mindestanforderungen

- Android API 21+
- Kotlin 2.0+

---

## Lizenz

MIT
