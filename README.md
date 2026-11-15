# android-openhab-rest-client

Kotlin client for the [openHAB](https://www.openhab.org/) REST API – for **Android apps and every
JVM** – with the **same classes, methods, parameters and results** as
[python-openhab-rest-client](https://github.com/Michdo93/python-openhab-rest-client) and the other
libraries of the family
([openHAB-REST-API-Client-Libraries](https://github.com/Michdo93/openHAB-REST-API-Client-Libraries)).

- Android 5.0 (API 21) or newer, or any JVM 8+; Kotlin 1.9+ in the app
- one dependency: `kotlinx-coroutines-core` – HTTP with `HttpURLConnection`, built-in JSON parser
- 33 REST classes (Items, Things, Rules, Persistence, Sitemaps, Voice, …), all methods `suspend`
  and safe to call from the main thread (they run on `Dispatchers.IO`)
- Server-Sent Events as Kotlin `Flow` – cancelling the coroutine closes the connection at once
- errors as `{"error": "..."}` instead of exceptions – exactly like the Python library

## Installation

The library is served by [JitPack](https://jitpack.io/#Michdo93/android-openhab-rest-client).

`settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

`app/build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.Michdo93:android-openhab-rest-client:2.0.0")
}
```

Groovy: `maven { url 'https://jitpack.io' }` and
`implementation 'com.github.Michdo93:android-openhab-rest-client:2.0.0'`.

The app needs the internet permission in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

For an openHAB in the local network over plain `http://` (Android 9+ blocks clear text by default)
allow it for that address, e.g. `android:usesCleartextTraffic="true"` on `<application>` or a
`network_security_config.xml` for the openHAB host.

Without JitPack: download `android-openhab-rest-client-2.0.0.jar` from the
[releases](https://github.com/Michdo93/android-openhab-rest-client/releases), put it into `app/libs/`
and add `implementation(files("libs/android-openhab-rest-client-2.0.0.jar"))` plus
`implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")`.

## Quick start

```kotlin
import io.github.michdo93.openhab.*

// e.g. in a ViewModel
val client = OpenHABClient("http://192.168.0.10:8080", token = BuildConfig.OPENHAB_TOKEN)
val items = Items(client)

viewModelScope.launch {
    val answer = items.getItem("Wohnzimmer_Licht")
    if (Json.isError(answer)) {
        status.value = "Error: ${Json.error(answer)}"
        return@launch
    }
    val item = Json.map(answer)
    status.value = "${item["label"]}: ${item["state"]}"

    items.sendCommand("Wohnzimmer_Licht", "ON")

    for (light in Json.list(items.getItems(tags = "Light"))) {
        println(light["name"])
    }
}
```

On the JVM (command line, server, tests) the same code runs inside `runBlocking { … }`.

### Connecting

```kotlin
OpenHABClient(url, token = token)                       // API token (recommended)
OpenHABClient(url, "user", "password")                  // Basic Authentication
OpenHABClient(url, token = token, timeout = 30.0)       // timeout in seconds (default 10)
client.connect()            // suspend: checks the connection, sets client.isLoggedIn
OpenHAB(url, token = token).items                       // all classes as properties
```

The constructor does no network access (Android forbids that on the main thread); `connect()`
(or the blocking `login()`) checks the connection on demand. Never hard-code tokens – put them in
`local.properties`/`BuildConfig`, the app settings or an environment variable.

### Results

Every REST method returns what the Python library returns:

| Response | Kotlin type |
|---|---|
| JSON object / array | `Map<String, Any?>` / `List<Any?>` (numbers as `Long`/`Double`) |
| text (e.g. `getItemState`) | `String` |
| empty body | `{"message": "OK"}` or `StatusResponse` (`status`, optional `location`) |
| error | `{"error": "Item not found."}` – check with `Json.isError(result)` |

`Json` helps with the types: `Json.map(r)`, `Json.list(r)` (array of objects), `Json.array(r)`,
`Json.get(r, "statusInfo", "status")`, `Json.str(...)`, `Json.isError(r)`, `Json.error(r)`,
`Json.parse(text)`, `Json.stringify(value)`. `Json.map` and `Json.list` throw an
`IllegalStateException` with the error text if the result is an error object.

### Optional parameters

The methods have the parameters of the Python library – same names, same order, same default
values – so optional parameters are passed by name:

```kotlin
items.getItems()                                            // all items
items.getItems(tags = "Light", fields = "name,state")
persistence.getItemPersistenceData("Wohnzimmer_Temperatur", serviceID = "rrd4j",
    startTime = "2026-11-01T00:00:00.000Z")
```

### Events (Server-Sent Events)

```kotlin
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {          // stops in the background
        val stream = ItemEvents(client).ItemStateChangedEvent("Wohnzimmer_*")
        if (stream.isError) {
            status.text = Json.error(stream.error)
            return@repeatOnLifecycle
        }
        stream.events().collect { event ->                 // payload already decoded
            val name = event["topic"].toString().split("/")[2]
            status.text = "$name -> ${Json.get(event, "payload", "value")}"
        }
    }
}
```

- `stream.events()` – `Flow` of events with decoded payload (ALIVE messages skipped),
  `events(includeAlive = true)` with them
- `stream.lines()` – `Flow` of raw lines (e.g. the connection id of `Events.initiateStateTracker()`)
- cancelling the collecting coroutine (or `take`, `first`, …) closes the connection immediately
- `stream.close()` ends the stream from anywhere; `for (line in stream)` and
  `stream.eventSequence()` read blocking (JVM programs, tests)
- there is no read timeout – a stream may stay quiet for hours
- `SSE.iterSSELines`, `SSE.iterSSEEvents`, `SSE.parseSSEData` – the names of the Python helpers

### Low-level access

`client.get(path, headers, params)`, `client.post(path, headers, data, params)`, `client.put(...)`,
`client.delete(...)` and `client.executeSSE(path, headers, params)` reach any endpoint. They are
**blocking** (call them on `Dispatchers.IO`) and throw `OpenHABHTTPError` (with `status` and
`openhabMessage`) for 4xx/5xx and `OpenHABRequestException` if no answer arrives. `data` may be a
`String` (sent as text or JSON), a `Map`/`List` (sent as JSON) or a number.

## Classes and methods

Parameters in brackets are optional (pass them by name). The KDoc of every method (generated from
the Python docstrings) describes the parameters in detail. Every method is a `suspend fun`.

### Actions

| Method | Parameters | Returns |
|---|---|---|
| `getActions` | thingUID, [language] | result |
| `executeAction` | thingUID, actionUID, actionInputs, [language] | result |

### Addons

| Method | Parameters | Returns |
|---|---|---|
| `getAddons` | [serviceID, language] | result |
| `getAddon` | addonID, [serviceID, language] | result |
| `getAddonConfig` | addonID, [serviceID] | result |
| `updateAddonConfig` | addonID, configData, [serviceID] | result |
| `installAddon` | addonID, [serviceID] | result |
| `uninstallAddon` | addonID, [serviceID] | result |
| `getAddonServices` | [language] | result |
| `getAddonSuggestions` | [language] | result |
| `getAddonTypes` | [serviceID, language] | result |
| `installAddonFromUrl` | url | result |

### Audio

| Method | Parameters | Returns |
|---|---|---|
| `getDefaultSink` | [language] | result |
| `getDefaultSource` | [language] | result |
| `getSinks` | [language] | result |
| `getSources` | [language] | result |

### Auth

| Method | Parameters | Returns |
|---|---|---|
| `getAPITokens` |  | result |
| `revokeAPIToken` | tokenName | result |
| `logout` | refreshToken, sessionID | result |
| `getSessions` |  | result |
| `getToken` | [useCookie, grantType, code, redirectURI, clientID, refreshToken, codeVerifier] | result |

### ChannelEvents

| Method | Parameters | Returns |
|---|---|---|
| `ChannelDescriptionChangedEvent` | [channelUID] | `SSEConnection` |
| `ChannelTriggeredEvent` | [channelUID] | `SSEConnection` |

### ChannelTypes

| Method | Parameters | Returns |
|---|---|---|
| `getChannelTypes` | [prefixes, language] | result |
| `getChannelType` | channelTypeUID, [language] | result |
| `getLinkableItemTypes` | channelTypeUID | result |

### ConfigDescriptions

| Method | Parameters | Returns |
|---|---|---|
| `getConfigDescriptions` | [scheme, language] | result |
| `getConfigDescription` | uri, [language] | result |

### Discovery

| Method | Parameters | Returns |
|---|---|---|
| `getDiscoveryBindings` |  | result |
| `getBindingInfo` | bindingID, [language] | result |
| `startBindingScan` | bindingID, [input] | result |

### Events

| Method | Parameters | Returns |
|---|---|---|
| `getEvents` | [topics] | `SSEConnection` |
| `initiateStateTracker` |  | `SSEConnection` |
| `updateSSEConnectionItems` | connectionID, items | result |

### Iconsets

| Method | Parameters | Returns |
|---|---|---|
| `getIconsets` | [language] | result |

### Inbox

| Method | Parameters | Returns |
|---|---|---|
| `getDiscoveredThings` | [includeIgnored] | result |
| `removeDiscoveryResult` | thingUID | result |
| `approveDiscoveryResult` | thingUID, thingLabel, [newThingID, language] | result |
| `ignoreDiscoveryResult` | thingUID | result |
| `unignoreDiscoveryResult` | thingUID | result |

### InboxEvents

| Method | Parameters | Returns |
|---|---|---|
| `InboxAddedEvent` | [thingUID] | `SSEConnection` |
| `InboxRemovedEvent` | [thingUID] | `SSEConnection` |
| `InboxUpdatedEvent` | [thingUID] | `SSEConnection` |

### ItemEvents

| Method | Parameters | Returns |
|---|---|---|
| `ItemEvent` |  | `SSEConnection` |
| `ItemAddedEvent` | [itemName] | `SSEConnection` |
| `ItemRemovedEvent` | [itemName] | `SSEConnection` |
| `ItemUpdatedEvent` | [itemName] | `SSEConnection` |
| `ItemCommandEvent` | [itemName] | `SSEConnection` |
| `ItemStateEvent` | [itemName] | `SSEConnection` |
| `ItemStatePredictedEvent` | [itemName] | `SSEConnection` |
| `ItemStateChangedEvent` | [itemName] | `SSEConnection` |
| `GroupItemStateChangedEvent` | [itemName, memberName] | `SSEConnection` |

### Items

| Method | Parameters | Returns |
|---|---|---|
| `getItems` | [type, tags, metadata, recursive, fields, staticDataOnly, language, parents] | result |
| `addOrUpdateItems` | items | result |
| `getItem` | itemName, [metadata, recursive, language, parents] | result |
| `addOrUpdateItem` | itemName, itemData, [language] | result |
| `sendCommand` | itemName, command, [source] | result |
| `postUpdate` | itemName, state, [source] | result |
| `deleteItem` | itemName | result |
| `addGroupMember` | itemName, memberItemName | result |
| `removeGroupMember` | itemName, memberItemName | result |
| `addMetadata` | itemName, namespace, metadata | result |
| `removeMetadata` | itemName, namespace | result |
| `getMetadataNamespaces` | itemName, [language] | result |
| `getSemanticItem` | itemName, semanticClass, [language] | result |
| `getItemState` | itemName | result |
| `updateItemState` | itemName, state, [language, source] | result |
| `addTag` | itemName, tag | result |
| `removeTag` | itemName, tag | result |
| `purgeOrphanedMetadata` |  | result |

### LinkEvents

| Method | Parameters | Returns |
|---|---|---|
| `ItemChannelLinkAddedEvent` | [itemName, channelUID] | `SSEConnection` |
| `ItemChannelLinkRemovedEvent` | [itemName, channelUID] | `SSEConnection` |

### Links

| Method | Parameters | Returns |
|---|---|---|
| `getLinks` | [channelUID, itemName] | result |
| `getLink` | itemName, channelUID | result |
| `linkItemToChannel` | itemName, channelUID, configuration | result |
| `unlinkItemFromChannel` | itemName, channelUID | result |
| `deleteAllLinks` | object | result |
| `getOrphanLinks` |  | result |
| `purgeUnusedLinks` |  | result |

### Logging

| Method | Parameters | Returns |
|---|---|---|
| `getLoggers` |  | result |
| `getLogger` | loggerName | result |
| `modifyOrAddLogger` | loggerName, level | result |
| `removeLogger` | loggerName | result |

### ModuleTypes

| Method | Parameters | Returns |
|---|---|---|
| `getModuleTypes` | [tags, typeFilter, language] | result |
| `getModuleType` | moduleTypeUID, [language] | result |

### Persistence

| Method | Parameters | Returns |
|---|---|---|
| `getServices` | [language] | result |
| `getServiceConfiguration` | serviceID | result |
| `setServiceConfiguration` | serviceID, config | result |
| `deleteServiceConfiguration` | serviceID | result |
| `getItemsFromService` | [serviceID, itemName] | result |
| `getItemPersistenceData` | itemName, [serviceID, startTime, endTime, page, pageLength, boundary, itemState, displayState, language] | result |
| `storeItemData` | itemName, time, state, [serviceID] | result |
| `deleteItemData` | itemName, startTime, endTime, serviceID | result |

### ProfileTypes

| Method | Parameters | Returns |
|---|---|---|
| `getProfileTypes` | [channelTypeUID, itemType, language] | result |

### Rules

| Method | Parameters | Returns |
|---|---|---|
| `getRules` | [prefix, tags, summary, staticDataOnly] | result |
| `createRule` | ruleData | result |
| `getRule` | ruleUID | result |
| `updateRule` | ruleUID, ruleData | result |
| `deleteRule` | ruleUID | result |
| `getModule` | ruleUID, moduleCategory, moduleID | result |
| `getModuleConfig` | ruleUID, moduleCategory, moduleID | result |
| `getModuleConfigParam` | ruleUID, moduleCategory, moduleID, param | result |
| `setModuleConfigParam` | ruleUID, moduleCategory, moduleID, param, value | result |
| `getActions` | ruleUID | result |
| `getConditions` | ruleUID | result |
| `getConfiguration` | ruleUID | result |
| `updateConfiguration` | ruleUID, configData | result |
| `setRuleState` | ruleUID, enable | result |
| `enable` | ruleUID | result |
| `disable` | ruleUID | result |
| `runNow` | ruleUID, [contextData] | result |
| `getTriggers` | ruleUID | result |
| `simulateSchedule` | fromTime, untilTime | result |

### Services

| Method | Parameters | Returns |
|---|---|---|
| `getServices` | [language] | result |
| `getService` | serviceID, [language] | result |
| `getServiceConfig` | serviceID | result |
| `updateServiceConfig` | serviceID, configData, [language] | result |
| `deleteServiceConfig` | serviceID | result |
| `getServiceContexts` | serviceID, [language] | result |

### Sitemaps

| Method | Parameters | Returns |
|---|---|---|
| `getSitemaps` |  | result |
| `getSitemap` | sitemapName, [type, jsonCallback, includeHidden, language] | result |
| `getSitemapPage` | sitemapName, pageID, [subscriptionID, includeHidden, language] | result |
| `getFullSitemap` | sitemapName, [subscriptionID, includeHidden, language] | result |
| `getSitemapEvents` | subscriptionID, [sitemapName, pageID] | `SSEConnection` |
| `getFullSitemapEvents` | subscriptionID, [sitemapName] | `SSEConnection` |
| `subscribeToSitemapEvents` |  | result |

### Systeminfo

| Method | Parameters | Returns |
|---|---|---|
| `getSystemInfo` |  | result |
| `getUoMInfo` |  | result |

### Tags

| Method | Parameters | Returns |
|---|---|---|
| `getTags` | [language] | result |
| `createTag` | tagData, [language] | result |
| `getTag` | tagID, [language] | result |
| `updateTag` | tagID, tagData, [language] | result |
| `deleteTag` | tagID, [language] | result |

### Templates

| Method | Parameters | Returns |
|---|---|---|
| `getTemplates` | [language] | result |
| `getTemplate` | templateUID, [language] | result |

### ThingEvents

| Method | Parameters | Returns |
|---|---|---|
| `ThingAddedEvent` | [thingUID] | `SSEConnection` |
| `ThingRemovedEvent` | [thingUID] | `SSEConnection` |
| `ThingUpdatedEvent` | [thingUID] | `SSEConnection` |
| `ThingStatusInfoEvent` | [thingUID] | `SSEConnection` |
| `ThingStatusInfoChangedEvent` | [thingUID] | `SSEConnection` |

### Things

| Method | Parameters | Returns |
|---|---|---|
| `getThings` | [summary, staticDataOnly, language] | result |
| `createThing` | thingData, [language] | result |
| `getThing` | thingUID, [language] | result |
| `updateThing` | thingUID, thingData, [language] | result |
| `deleteThing` | thingUID, [force, language] | result |
| `updateThingConfiguration` | thingUID, configurationData, [language] | result |
| `getThingConfigStatus` | thingUID, [language] | result |
| `setThingStatus` | thingUID, enabled, [language] | result |
| `enableThing` | thingUID | result |
| `disableThing` | thingUID | result |
| `updateThingFirmware` | thingUID, firmwareVersion, [language] | result |
| `getThingFirmwareStatus` | thingUID, [language] | result |
| `getThingFirmwares` | thingUID, [language] | result |
| `getThingStatus` | thingUID, [language] | result |

### ThingTypes

| Method | Parameters | Returns |
|---|---|---|
| `getThingTypes` | [bindingID, language] | result |
| `getThingType` | thingTypeUID, [language] | result |

### Transformations

| Method | Parameters | Returns |
|---|---|---|
| `getTransformations` |  | result |
| `getTransformation` | transformationUID | result |
| `updateTransformation` | transformationUID, transformationData | result |
| `deleteTransformation` | transformationUID | result |
| `getTransformationServices` |  | result |

### UI

| Method | Parameters | Returns |
|---|---|---|
| `getUIComponents` | namespace, [summary] | result |
| `addUIComponent` | namespace, componentData | result |
| `getUIComponent` | namespace, componentUID | result |
| `updateUIComponent` | namespace, componentUID, componentData | result |
| `deleteUIComponent` | namespace, componentUID | result |
| `getUITiles` |  | result |

### UUID

| Method | Parameters | Returns |
|---|---|---|
| `getUUID` |  | result |

### Voice

| Method | Parameters | Returns |
|---|---|---|
| `getDefaultVoice` | [language] | result |
| `startDialog` | sourceID, [ksID, sttID, ttsID, voiceID, hliIDs, sinkID, keyword, listeningItem, language] | result |
| `stopDialog` | sourceID | result |
| `getInterpreters` | [language] | result |
| `interpretText` | text, [language] | result |
| `getInterpreter` | interpreterID, [language] | result |
| `interpretTextBatch` | text, IDs, [language] | result |
| `listenAndAnswer` | sourceID, sttID, ttsID, voiceID, hliIDs, sinkID, listeningItem, [language] | result |
| `sayText` | text, voiceID, sinkID, [volume] | result |
| `getVoices` | [language] | result |


## Changes in 2.0.0

2.0.0 is generated from the Python reference and replaces the hand-written 1.x:

- one plain Kotlin/JVM library instead of an Android AAR: no OkHttp, no Moshi, no kapt – only
  `kotlinx-coroutines-core`; runs in Android apps and on any JVM (tests, servers, Raspberry Pi)
- one package `io.github.michdo93.openhab` (1.x: `…openhab.api`, `…openhab.client`, `…openhab.events`)
- method and parameter names as in Python (e.g. `ItemStateChangedEvent`, not `itemStateChangedEvent`),
  with the Python default values
- results are decoded JSON (`Map`/`List`) instead of raw JSON strings
- errors are returned as `{"error": "..."}` instead of throwing `OpenHABException`
- all 179 methods of the Python library (1.x had about 150, partly with different parameters) and
  the classes `ChannelEvents`, `InboxEvents`, `LinkEvents`
- fixes: `Persistence.storeItemData` sends the state as query parameter, `ItemEvents.ItemEvent()`
  uses `openhab/items/*`, `#` in UIDs is encoded, event streams have no read timeout
- `SSEConnection` with `Flow`s instead of `SseSession.collect()`; `OpenHAB` keeps all classes as
  properties as before (without the `debug` parameter)
- Maven coordinates outside JitPack: `io.github.michdo93:android-openhab-rest-client`
  (1.x: `openhab-rest-client-android`)

## Development

```bash
# regenerate the REST classes from python-openhab-rest-client
python3 tools/py2kt.py ../python-openhab-rest-client/openhab src/main/kotlin/io/github/michdo93/openhab
python3 tools/py2kt.py --readme ../python-openhab-rest-client/openhab   # tables for this README
./gradlew build                        # unit tests run against a local HTTP server
OPENHAB_URL=http://127.0.0.1:8080 OPENHAB_TOKEN=oh.… ./gradlew test     # plus integration tests
```

Hand-written: `OpenHABClient`, `Json`, `SSEConnection`, `SSESocket`, `SSE`, `StatusResponse`, the
exceptions and `Py` (runtime helpers of the generated code). Everything else comes from
`tools/py2kt.py`. Publishing: see [PUBLISHING.md](PUBLISHING.md).

## License

MIT – see [LICENSE](LICENSE).
