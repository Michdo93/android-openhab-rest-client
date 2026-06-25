# android-openhab-rest-client

A Kotlin Android client for the openHAB REST API. This library enables easy interaction with the openHAB REST API from Android applications — send commands to devices, read item states, manage rules, and receive real-time updates via Server-Sent Events as Kotlin Flows.

It mirrors the [python-openhab-rest-client](https://github.com/Michdo93/python-openhab-rest-client) library: same class names, same method names, same usage pattern — adapted for Kotlin and Android.

**Dependencies:**
- [OkHttp 4](https://square.github.io/okhttp/) — HTTP and SSE communication
- [Moshi](https://github.com/square/moshi) — JSON serialization (available for consumers)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) — all REST methods are `suspend` functions
- [kotlinx-coroutines-android](https://github.com/Kotlin/kotlinx.coroutines) — Android dispatcher

## Features

Supports the following openHAB REST API endpoints:

- Actions
- Addons
- Audio
- Auth
- ChannelTypes
- ConfigDescriptions
- Discovery
- Events (general + ItemEvents, ThingEvents, InboxEvents, LinkEvents, ChannelEvents)
- Iconsets
- Inbox
- Items
- Links
- Logging
- ModuleTypes
- Persistence
- ProfileTypes
- Rules
- Services
- Sitemaps
- Systeminfo
- Tags
- Templates
- ThingTypes
- Things
- Transformations
- UI
- UUID
- Voice

All REST methods are **suspend functions** running on `Dispatchers.IO`. SSE streams are exposed as Kotlin **Flows** via `SseSession.collect()`.

## Requirements

- **Android API 21** (Android 5.0 Lollipop) or higher
- **Kotlin 2.0+**
- **Android Gradle Plugin 8.7+**
- Internet permission in `AndroidManifest.xml`

---

## Adding the Library to Your Project

There are three ways to add `android-openhab-rest-client` to your project.

---

### Option 1: JitPack

JitPack builds the library directly from GitHub. No additional login or token required.

**Step 1** — Add the JitPack repository to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")   // ← add this
    }
}
```

Or in `settings.gradle` (Groovy):

```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2** — Add the dependency to your app module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.Michdo93:android-openhab-rest-client:1.0.5")
}
```

Or in `build.gradle` (Groovy):

```groovy
dependencies {
    implementation 'com.github.Michdo93:android-openhab-rest-client:1.0.5'
}
```

Check [jitpack.io/#Michdo93/android-openhab-rest-client](https://jitpack.io/#Michdo93/android-openhab-rest-client) for the latest available version.

---

### Option 2: GitHub Packages

The library is published to GitHub Packages under `io.github.michdo93:openhab-rest-client-android`.

GitHub Packages requires authentication — you need a GitHub account and a Personal Access Token (PAT) with at least `read:packages` scope.

**Step 1** — Create a GitHub PAT at **Settings → Developer settings → Personal access tokens** with `read:packages` selected.

**Step 2** — Add your credentials. The recommended approach is to store them in `~/.gradle/gradle.properties` (never commit credentials to your repo):

```properties
# ~/.gradle/gradle.properties
GITHUB_USERNAME=your_github_username
GITHUB_TOKEN=ghp_your_personal_access_token
```

**Step 3** — Add the GitHub Packages repository to `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url  = uri("https://maven.pkg.github.com/Michdo93/android-openhab-rest-client")
            credentials {
                username = providers.gradleProperty("GITHUB_USERNAME").orNull
                password = providers.gradleProperty("GITHUB_TOKEN").orNull
            }
        }
    }
}
```

**Step 4** — Add the dependency to `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.michdo93:openhab-rest-client-android:1.0.5")
}
```

---

### Option 3: AAR file (manual)

Download the pre-built AAR from the [GitHub Releases page](https://github.com/Michdo93/android-openhab-rest-client/releases) and add it manually to your project.

**Step 1** — Copy `openhab-rest-client-android-1.0.5.aar` into your module's `libs/` folder:

```
MyApp/
└── app/
    └── libs/
        └── openhab-rest-client-android-1.0.5.aar
```

**Step 2** — Add the `libs` directory as a file tree dependency in `app/build.gradle.kts`:

```kotlin
dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))

    // The library's own dependencies must be added manually:
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
```

Or in `build.gradle` (Groovy):

```groovy
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.aar', '*.jar'])

    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.squareup.okhttp3:okhttp-sse:4.12.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
    implementation 'com.squareup.moshi:moshi:1.15.1'
    implementation 'com.squareup.moshi:moshi-kotlin:1.15.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1'
}
```

**Step 3 (Android Studio)** — Sync the project via **File → Sync Project with Gradle Files**.

---

## AndroidManifest.xml

Add the Internet permission to your `AndroidManifest.xml`:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />
    ...
</manifest>
```

---

## Network Security (HTTP / Self-Signed Certificates)

If your openHAB instance uses plain HTTP or a self-signed HTTPS certificate, you need a Network Security Configuration.

Create `res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">192.168.1.100</domain>
        <domain includeSubdomains="true">myopenhab.local</domain>
    </domain-config>
</network-security-config>
```

Reference it in your `AndroidManifest.xml`:

```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

---

## Imports

```kotlin
import io.github.michdo93.openhab.OpenHAB
import io.github.michdo93.openhab.client.OpenHABClient
import io.github.michdo93.openhab.client.OpenHABException
import io.github.michdo93.openhab.client.SseSession
import io.github.michdo93.openhab.api.*
import io.github.michdo93.openhab.events.*
```

---

## Usage

### Quick Start with the `OpenHAB` Facade

`OpenHAB` is the recommended entry point. It lazily creates all API class instances and shares a single `OpenHABClient` under the hood:

```kotlin
val openHAB = OpenHAB(
    url      = "http://192.168.1.100:8080",
    username = "openhab",
    password = "habopen",
    debug    = BuildConfig.DEBUG,  // enables OkHttp request/response logging
)
```

### Authentication

#### Basic Authentication

```kotlin
val openHAB = OpenHAB(
    url      = "http://192.168.1.100:8080",
    username = "openhab",
    password = "habopen",
)
```

#### Token Authentication

```kotlin
val openHAB = OpenHAB(
    url   = "http://192.168.1.100:8080",
    token = "oh.openhab.U0doM1Lz4kJ6tPlVGjH17jjm4ZcTHIHi7sMwESzrIybKbCGySmBMtysPnObQLuLf7PgqnI7jWQ5LosySY8Q",
)
```

#### myopenhab.org Cloud

```kotlin
val openHAB = OpenHAB(
    url      = "https://myopenhab.org",
    username = "your@email.com",
    password = "yourpassword",
)
```

### Verify Connectivity

```kotlin
lifecycleScope.launch {
    val connected = openHAB.login()
    if (connected) {
        Log.d("openHAB", "Connected. isCloud=${openHAB.isCloud}")
    }
}
```

### Calling API Methods

All methods are `suspend` functions — call them from a coroutine scope. In an Activity use `lifecycleScope`, in a ViewModel use `viewModelScope`:

```kotlin
// In an Activity:
lifecycleScope.launch {
    try {
        val state = openHAB.items.getItemState("LivingRoomLight")
        Log.d("openHAB", "State: $state")

        openHAB.items.sendCommand("LivingRoomLight", "ON")
    } catch (e: OpenHABException) {
        Log.e("openHAB", "HTTP ${e.statusCode}: ${e.message}")
    }
}

// In a ViewModel:
viewModelScope.launch {
    val allItems = openHAB.items.getItems()
    _itemsState.value = allItems
}
```

### Parsing JSON Responses

All methods return raw JSON `String`. Parse with Moshi (already a dependency) or `org.json`:

```kotlin
// With org.json (built into Android):
val json   = openHAB.items.getItem("LivingRoomLight")
val obj    = org.json.JSONObject(json)
val state  = obj.getString("state")

// With Moshi:
val moshi  = com.squareup.moshi.Moshi.Builder().build()
val item   = moshi.adapter(MyItemModel::class.java).fromJson(json)
```

### Server-Sent Events (SSE)

SSE streams return an `SseSession`. Call `.collect()` to get a `Flow<String>`. Each emitted string is the raw JSON payload after `"data: "`. Cancel the coroutine to stop the stream automatically.

```kotlin
// lifecycleScope automatically cancels the stream when the Activity is destroyed:
lifecycleScope.launch {
    openHAB.itemEvents
        .itemStateChangedEvent("LivingRoomLight")
        .collect()
        .catch { e -> Log.e("openHAB", "SSE error: ${e.message}") }
        .collect { data ->
            Log.d("openHAB", "Event: $data")
            // data = raw JSON, e.g.:
            // {"topic":"openhab/items/LivingRoomLight/statechanged",
            //  "payload":"{\"type\":\"OnOff\",\"value\":\"ON\",...}",
            //  "type":"ItemStateChangedEvent"}
        }
}
```

Stop the stream manually:

```kotlin
val sseJob = lifecycleScope.launch {
    openHAB.itemEvents.itemStateChangedEvent("LivingRoomLight")
        .collect().collect { data -> ... }
}

// Stop later:
sseJob.cancel()
```

### Using API Classes Directly (without the `OpenHAB` facade)

You can instantiate API classes directly if you prefer:

```kotlin
val client = OpenHABClient("http://192.168.1.100:8080", "openhab", "habopen")

val items      = Items(client)
val things     = Things(client)
val itemEvents = ItemEvents(client)

lifecycleScope.launch {
    client.login()
    val state = items.getItemState("MySwitch")
    Log.d("openHAB", state)
}
```

### Dependency Injection (Hilt / Koin)

**Hilt:**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object OpenHABModule {

    @Provides
    @Singleton
    fun provideOpenHAB(): OpenHAB = OpenHAB(
        url      = BuildConfig.OPENHAB_URL,
        token    = BuildConfig.OPENHAB_TOKEN,
        debug    = BuildConfig.DEBUG,
    )
}

// In your ViewModel:
@HiltViewModel
class HomeViewModel @Inject constructor(private val openHAB: OpenHAB) : ViewModel() {
    fun sendCommand(item: String, command: String) {
        viewModelScope.launch {
            openHAB.items.sendCommand(item, command)
        }
    }
}
```

**Koin:**

```kotlin
val openHABModule = module {
    single {
        OpenHAB(url = "http://192.168.1.100:8080", username = "openhab", password = "habopen")
    }
}
```

---

## Full List of Methods

All REST methods are `suspend` functions returning `String` (raw JSON or plain text). All throw `OpenHABException` on HTTP errors or network failures. SSE methods are regular functions returning `SseSession`.

Optional parameters default to `null` (not sent to the server).

---

### `OpenHAB` (Facade)

The recommended entry point. All API classes are lazily initialized and share one `OpenHABClient`.

#### Package

```kotlin
import io.github.michdo93.openhab.OpenHAB
```

#### Constructor

```kotlin
OpenHAB(
    url: String,
    username: String? = null,
    password: String? = null,
    token: String?    = null,
    debug: Boolean    = false,
)
```

**Parameters:**
- `url` — Base URL of the openHAB server (e.g. `"http://192.168.1.100:8080"`).
- `username` — Username for Basic Authentication.
- `password` — Password for Basic Authentication.
- `token` — Bearer token for Token Authentication.
- `debug` — Enable OkHttp body logging (use `BuildConfig.DEBUG`).

#### Properties

| Property | Type | Description |
|---|---|---|
| `client` | `OpenHABClient` | The underlying HTTP client |
| `isLoggedIn` | `Boolean` | `true` after a successful `login()` |
| `isCloud` | `Boolean` | `true` when connected to `myopenhab.org` |

#### Lazy API properties

| Property | Type |
|---|---|
| `items` | `Items` |
| `things` | `Things` |
| `rules` | `Rules` |
| `actions` | `Actions` |
| `addons` | `Addons` |
| `audio` | `Audio` |
| `auth` | `Auth` |
| `channelTypes` | `ChannelTypes` |
| `configDescriptions` | `ConfigDescriptions` |
| `discovery` | `Discovery` |
| `iconsets` | `Iconsets` |
| `inbox` | `Inbox` |
| `links` | `Links` |
| `logging` | `Logging` |
| `moduleTypes` | `ModuleTypes` |
| `persistence` | `Persistence` |
| `profileTypes` | `ProfileTypes` |
| `services` | `Services` |
| `sitemaps` | `Sitemaps` |
| `systeminfo` | `Systeminfo` |
| `tags` | `Tags` |
| `templates` | `Templates` |
| `thingTypes` | `ThingTypes` |
| `transformations` | `Transformations` |
| `ui` | `UI` |
| `uuid` | `UUID` |
| `voice` | `Voice` |
| `events` | `Events` |
| `itemEvents` | `ItemEvents` |
| `thingEvents` | `ThingEvents` |
| `inboxEvents` | `InboxEvents` |
| `linkEvents` | `LinkEvents` |
| `channelEvents` | `ChannelEvents` |

#### Methods

##### `suspend fun login(): Boolean`

Verifies connectivity. Sets `isLoggedIn`. Returns `true` on success.

---

### `OpenHABClient`

The core HTTP client. Use `OpenHAB` in most cases; use this directly for advanced scenarios.

#### Package

```kotlin
import io.github.michdo93.openhab.client.OpenHABClient
```

#### Constructor

```kotlin
OpenHABClient(
    baseUrl: String,
    username: String? = null,
    password: String? = null,
    token: String?    = null,
    debug: Boolean    = false,
)
```

#### Properties

| Property | Type | Description |
|---|---|---|
| `baseUrl` | `String` | Base URL without trailing slash |
| `username` | `String?` | Username (Basic Auth) |
| `isLoggedIn` | `Boolean` | `true` after successful `login()` |
| `isCloud` | `Boolean` | `true` when URL is `myopenhab.org` |

#### Methods

| Method | Description |
|---|---|
| `suspend fun login(): Boolean` | Verifies connectivity |
| `suspend fun get(path, params, extraHeaders): String` | GET request |
| `suspend fun post(path, body, contentType, params, extraHeaders): String` | POST request |
| `suspend fun put(path, body, contentType, params, extraHeaders): String` | PUT request |
| `suspend fun delete(path, params, extraHeaders): String` | DELETE request |
| `fun sse(url: String): SseSession` | Opens an SSE stream |

---

### `OpenHABException`

Thrown by all `suspend` REST methods on HTTP errors or network failures.

```kotlin
import io.github.michdo93.openhab.client.OpenHABException
```

| Property | Type | Description |
|---|---|---|
| `message` | `String` | Human-readable error description |
| `statusCode` | `Int` | HTTP status code, or `-1` if not applicable |
| `cause` | `Throwable?` | Underlying exception |

```kotlin
try {
    openHAB.items.sendCommand("NonExistent", "ON")
} catch (e: OpenHABException) {
    Log.e("openHAB", "HTTP ${e.statusCode}: ${e.message}")
}
```

---

### `SseSession`

Wraps an OkHttp SSE connection. Call `.collect()` to get a cold `Flow<String>`.

```kotlin
import io.github.michdo93.openhab.client.SseSession
```

#### Methods

##### `fun collect(): Flow<String>`

Returns a cold Kotlin Flow. Each emission is the raw payload string after `"data: "`. The stream closes when the coroutine is cancelled or on SSE failure.

```kotlin
val session: SseSession = openHAB.itemEvents.itemStateChangedEvent("MySwitch")
val flow: Flow<String>  = session.collect()

lifecycleScope.launch {
    flow
        .catch { e -> Log.e("openHAB", "SSE error: ${e.message}") }
        .collect { data -> Log.d("openHAB", data) }
}
```

---

### `Actions`

Provides methods to retrieve and execute thing actions.

#### Constructor

```kotlin
val actions = Actions(client)
// or via facade:
openHAB.actions
```

#### Methods

| Method | Description |
|---|---|
| `suspend fun getActions(thingUID: String, language: String? = null): String` | Gets all available actions for a thing |
| `suspend fun executeAction(thingUID: String, actionUID: String, inputsJson: String, language: String? = null): String` | Executes a thing action |

```kotlin
openHAB.actions.executeAction(
    thingUID   = "myThingUID",
    actionUID  = "myActionUID",
    inputsJson = """{"param1":"value1"}""",
)
```

---

### `Addons`

Provides methods to manage openHAB add-ons.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getAddons(serviceId: String? = null, language: String? = null): String` | Gets all add-ons |
| `suspend fun getAddon(id: String, serviceId: String? = null, language: String? = null): String` | Gets a specific add-on |
| `suspend fun getAddonConfig(id: String, serviceId: String? = null): String` | Gets the add-on configuration |
| `suspend fun updateAddonConfig(id: String, configJson: String, serviceId: String? = null): String` | Updates the add-on configuration |
| `suspend fun installAddon(id: String, serviceId: String? = null): String` | Installs an add-on |
| `suspend fun uninstallAddon(id: String, serviceId: String? = null): String` | Uninstalls an add-on |
| `suspend fun getAddonServices(language: String? = null): String` | Gets all add-on services |
| `suspend fun getAddonSuggestions(language: String? = null): String` | Gets suggested add-ons |
| `suspend fun getAddonTypes(serviceId: String? = null, language: String? = null): String` | Gets all add-on types |
| `suspend fun installAddonFromUrl(url: String): String` | Installs an add-on from a URL |

---

### `Audio`

Provides methods to interact with the openHAB audio system.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getDefaultSink(language: String? = null): String` | Gets the default audio sink |
| `suspend fun getDefaultSource(language: String? = null): String` | Gets the default audio source |
| `suspend fun getSinks(language: String? = null): String` | Gets all available sinks |
| `suspend fun getSources(language: String? = null): String` | Gets all available sources |

---

### `Auth`

Provides methods for authentication token and session management.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getApiTokens(): String` | Gets all API tokens for the current user |
| `suspend fun revokeApiToken(name: String): String` | Revokes an API token by name |
| `suspend fun logout(refreshToken: String, sessionId: String): String` | Terminates a session |
| `suspend fun getSessions(): String` | Gets all active sessions |
| `suspend fun getToken(grantType: String? = null, code: String? = null, redirectUri: String? = null, clientId: String? = null, refreshToken: String? = null, codeVerifier: String? = null): String` | Obtains OAuth access and refresh tokens |

---

### `ChannelTypes`

Provides methods to retrieve channel type information.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getChannelTypes(prefixes: String? = null, language: String? = null): String` | Gets all channel types |
| `suspend fun getChannelType(uid: String, language: String? = null): String` | Gets a specific channel type |
| `suspend fun getLinkableItemTypes(uid: String): String` | Gets item types linkable to a trigger channel type |

---

### `ConfigDescriptions`

Provides methods to retrieve configuration descriptions.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getConfigDescriptions(scheme: String? = null, language: String? = null): String` | Gets all configuration descriptions |
| `suspend fun getConfigDescription(uri: String, language: String? = null): String` | Gets a specific configuration description |

---

### `Discovery`

Provides methods to interact with the openHAB discovery service.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getDiscoveryBindings(): String` | Gets all bindings that support discovery |
| `suspend fun getBindingInfo(bindingId: String, language: String? = null): String` | Gets information about a binding |
| `suspend fun startBindingScan(bindingId: String, input: String? = null): String` | Starts a discovery scan |

---

### `Events`

Provides general openHAB event bus access via SSE.

#### Methods

| Method | Returns | Description |
|---|---|---|
| `fun getEvents(topics: String? = null): SseSession` | `SseSession` | All events, optionally filtered by topic |
| `fun initiateStateTracker(): SseSession` | `SseSession` | Initiates a new SSE state tracker |
| `suspend fun updateSSEConnectionItems(connectionId: String, itemsJson: String): String` | `String` | Updates items tracked by a state tracker |

```kotlin
lifecycleScope.launch {
    openHAB.events
        .getEvents("openhab/items,openhab/things")
        .collect()
        .collect { data -> Log.d("openHAB", data) }
}
```

---

### `ItemEvents`

Provides SSE streams for item-related events. All methods return `SseSession`. The optional `name` parameter defaults to `"*"` (all items).

#### Methods

| Method | Description |
|---|---|
| `fun itemEvent(): SseSession` | All item events |
| `fun itemAddedEvent(name: String = "*"): SseSession` | Item added events |
| `fun itemRemovedEvent(name: String = "*"): SseSession` | Item removed events |
| `fun itemUpdatedEvent(name: String = "*"): SseSession` | Item updated events |
| `fun itemCommandEvent(name: String = "*"): SseSession` | Item command events |
| `fun itemStateEvent(name: String = "*"): SseSession` | Item state events |
| `fun itemStatePredictedEvent(name: String = "*"): SseSession` | Item state predicted events |
| `fun itemStateChangedEvent(name: String = "*"): SseSession` | Item state changed events |
| `fun groupItemStateChangedEvent(item: String, member: String): SseSession` | Group item state changed for a specific member |

```kotlin
// Observe all item state changes
lifecycleScope.launch {
    openHAB.itemEvents
        .itemStateChangedEvent()
        .collect()
        .catch { e -> Log.e("openHAB", "SSE error: ${e.message}") }
        .collect { data -> Log.d("openHAB", "Event: $data") }
}
```

---

### `ThingEvents`

Provides SSE streams for thing-related events. All methods return `SseSession`. The optional `uid` parameter defaults to `"*"`.

#### Methods

| Method | Description |
|---|---|
| `fun thingAddedEvent(uid: String = "*"): SseSession` | Thing added events |
| `fun thingRemovedEvent(uid: String = "*"): SseSession` | Thing removed events |
| `fun thingUpdatedEvent(uid: String = "*"): SseSession` | Thing updated events |
| `fun thingStatusInfoEvent(uid: String = "*"): SseSession` | Thing status info events |
| `fun thingStatusInfoChangedEvent(uid: String = "*"): SseSession` | Thing status info changed events |

---

### `InboxEvents`

Provides SSE streams for inbox (discovery) events. All methods return `SseSession`. The optional `uid` parameter defaults to `"*"`.

#### Methods

| Method | Description |
|---|---|
| `fun inboxAddedEvent(uid: String = "*"): SseSession` | Inbox added events |
| `fun inboxRemovedEvent(uid: String = "*"): SseSession` | Inbox removed events |
| `fun inboxUpdatedEvent(uid: String = "*"): SseSession` | Inbox updated events |

---

### `LinkEvents`

Provides SSE streams for item-channel link events. Both methods return `SseSession`.

#### Methods

| Method | Description |
|---|---|
| `fun itemChannelLinkAddedEvent(item: String = "*", ch: String = "*"): SseSession` | Link added events |
| `fun itemChannelLinkRemovedEvent(item: String = "*", ch: String = "*"): SseSession` | Link removed events |

---

### `ChannelEvents`

Provides SSE streams for channel events. Both methods return `SseSession`.

#### Methods

| Method | Description |
|---|---|
| `fun channelDescriptionChangedEvent(uid: String = "*"): SseSession` | Channel description changed events |
| `fun channelTriggeredEvent(uid: String = "*"): SseSession` | Channel triggered events |

---

### `Iconsets`

#### Methods

| Method | Description |
|---|---|
| `suspend fun getIconsets(language: String? = null): String` | Gets all available iconsets |

---

### `Inbox`

Provides methods to manage the openHAB inbox (discovery results).

#### Methods

| Method | Description |
|---|---|
| `suspend fun getDiscoveredThings(includeIgnored: Boolean = true): String` | Gets all discovered things |
| `suspend fun removeDiscoveryResult(uid: String): String` | Removes a discovery result |
| `suspend fun approveDiscoveryResult(uid: String, label: String, newId: String? = null, language: String? = null): String` | Approves a discovery result and creates the thing |
| `suspend fun ignoreDiscoveryResult(uid: String): String` | Marks a discovery result as ignored |
| `suspend fun unignoreDiscoveryResult(uid: String): String` | Removes the ignore flag |

---

### `Items`

Provides methods to manage openHAB items.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getItems(type: String? = null, tags: String? = null, metadata: String = ".*", recursive: Boolean = false, fields: String? = null, staticDataOnly: Boolean = false, language: String? = null): String` | Gets all items with optional filters |
| `suspend fun addOrUpdateItems(itemsJson: String): String` | Adds or updates a list of items |
| `suspend fun getItem(name: String, metadata: String = ".*", recursive: Boolean = true, language: String? = null): String` | Gets a single item |
| `suspend fun addOrUpdateItem(name: String, itemJson: String, language: String? = null): String` | Adds or updates a single item |
| `suspend fun sendCommand(name: String, command: String): String` | Sends a command to an item |
| `suspend fun postUpdate(name: String, state: String): String` | Updates the state of an item |
| `suspend fun deleteItem(name: String): String` | Removes an item |
| `suspend fun addGroupMember(name: String, member: String): String` | Adds a member to a group item |
| `suspend fun removeGroupMember(name: String, member: String): String` | Removes a member from a group item |
| `suspend fun addMetadata(name: String, namespace: String, metadataJson: String): String` | Adds metadata to an item |
| `suspend fun removeMetadata(name: String, namespace: String): String` | Removes metadata from an item |
| `suspend fun getMetadataNamespaces(name: String, language: String? = null): String` | Gets all metadata namespaces of an item |
| `suspend fun getSemanticItem(name: String, semanticClass: String, language: String? = null): String` | Gets the item defining the requested semantics |
| `suspend fun getItemState(name: String): String` | Gets the current state of an item |
| `suspend fun updateItemState(name: String, state: String, language: String? = null): String` | Updates the state of an item |
| `suspend fun addTag(name: String, tag: String): String` | Adds a tag to an item |
| `suspend fun removeTag(name: String, tag: String): String` | Removes a tag from an item |
| `suspend fun purgeOrphanedMetadata(): String` | Removes unused/orphaned metadata from all items |

```kotlin
// Send command
openHAB.items.sendCommand("LivingRoomLight", "ON")

// Read state
val state = openHAB.items.getItemState("LivingRoomLight")

// Get filtered items
val switches = openHAB.items.getItems(type = "Switch")
```

---

### `Links`

Provides methods to manage item-channel links.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getLinks(channelUID: String? = null, itemName: String? = null): String` | Gets all links, optionally filtered |
| `suspend fun getLink(item: String, channelUID: String): String` | Gets a specific link |
| `suspend fun linkItemToChannel(item: String, channelUID: String, configJson: String): String` | Links an item to a channel |
| `suspend fun unlinkItemFromChannel(item: String, channelUID: String): String` | Unlinks an item from a channel |
| `suspend fun deleteAllLinks(obj: String): String` | Deletes all links for an item or thing |
| `suspend fun getOrphanLinks(): String` | Gets all orphan links |
| `suspend fun purgeUnusedLinks(): String` | Removes all unused/orphaned links |

---

### `Logging`

Provides methods to manage openHAB loggers.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getLoggers(): String` | Gets all loggers and their levels |
| `suspend fun getLogger(name: String): String` | Gets a specific logger |
| `suspend fun modifyOrAddLogger(name: String, level: String): String` | Modifies or adds a logger (`"DEBUG"`, `"INFO"`, `"WARN"`, `"ERROR"`) |
| `suspend fun removeLogger(name: String): String` | Removes a logger |

---

### `ModuleTypes`

Provides methods to retrieve rule module types.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getModuleTypes(tags: String? = null, typeFilter: String? = null, language: String? = null): String` | Gets all module types |
| `suspend fun getModuleType(uid: String, language: String? = null): String` | Gets a specific module type |

---

### `Persistence`

Provides methods to interact with openHAB persistence services.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getServices(language: String? = null): String` | Gets all persistence services |
| `suspend fun getServiceConfiguration(serviceId: String): String` | Gets a service configuration |
| `suspend fun setServiceConfiguration(serviceId: String, json: String): String` | Sets a service configuration |
| `suspend fun deleteServiceConfiguration(serviceId: String): String` | Deletes a service configuration |
| `suspend fun getItemsFromService(serviceId: String? = null): String` | Gets items available via a service |
| `suspend fun getItemPersistenceData(item: String, serviceId: String, startTime: String? = null, endTime: String? = null, page: Int = 1, pageLength: Int = 50, boundary: Boolean = false, itemState: Boolean = false): String` | Gets item persistence data |
| `suspend fun storeItemData(item: String, time: String, state: String, serviceId: String? = null): String` | Stores a data point |
| `suspend fun deleteItemData(item: String, startTime: String, endTime: String, serviceId: String): String` | Deletes item data within a time range |

```kotlin
val history = openHAB.persistence.getItemPersistenceData(
    item      = "MyTemperatureSensor",
    serviceId = "rrd4j",
    startTime = "2024-01-01T00:00:00.000+0100",
    endTime   = "2024-01-02T00:00:00.000+0100",
    pageLength = 100,
)
```

---

### `ProfileTypes`

#### Methods

| Method | Description |
|---|---|
| `suspend fun getProfileTypes(channelTypeUID: String? = null, itemType: String? = null, language: String? = null): String` | Gets all available profile types |

---

### `Rules`

Provides methods to manage openHAB rules.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getRules(prefix: String? = null, tags: String? = null, summary: Boolean = false, staticDataOnly: Boolean = false): String` | Gets all rules |
| `suspend fun createRule(json: String): String` | Creates a new rule |
| `suspend fun getRule(uid: String): String` | Gets a specific rule |
| `suspend fun updateRule(uid: String, json: String): String` | Updates a rule |
| `suspend fun deleteRule(uid: String): String` | Deletes a rule |
| `suspend fun getModule(uid: String, cat: String, mid: String): String` | Gets a specific module |
| `suspend fun getModuleConfig(uid: String, cat: String, mid: String): String` | Gets the module configuration |
| `suspend fun getModuleConfigParam(uid: String, cat: String, mid: String, param: String): String` | Gets a module config parameter |
| `suspend fun setModuleConfigParam(uid: String, cat: String, mid: String, param: String, value: String): String` | Sets a module config parameter |
| `suspend fun getActions(uid: String): String` | Gets all action modules of a rule |
| `suspend fun getConditions(uid: String): String` | Gets all condition modules |
| `suspend fun getTriggers(uid: String): String` | Gets all trigger modules |
| `suspend fun getConfiguration(uid: String): String` | Gets the rule configuration |
| `suspend fun updateConfiguration(uid: String, configJson: String): String` | Updates the rule configuration |
| `suspend fun setRuleState(uid: String, enable: Boolean): String` | Enables or disables a rule |
| `suspend fun enable(uid: String): String` | Enables a rule |
| `suspend fun disable(uid: String): String` | Disables a rule |
| `suspend fun runNow(uid: String, contextJson: String? = null): String` | Executes a rule immediately |
| `suspend fun simulateSchedule(from: String, until: String): String` | Simulates the rule schedule |

```kotlin
openHAB.rules.enable("my-rule-uid")
openHAB.rules.runNow("my-rule-uid")
```

---

### `Services`

Provides methods to manage openHAB configurable services.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getServices(language: String? = null): String` | Gets all configurable services |
| `suspend fun getService(id: String, language: String? = null): String` | Gets a specific service |
| `suspend fun getServiceConfig(id: String): String` | Gets the service configuration |
| `suspend fun updateServiceConfig(id: String, json: String, language: String? = null): String` | Updates the service configuration |
| `suspend fun deleteServiceConfig(id: String): String` | Deletes the service configuration |
| `suspend fun getServiceContexts(id: String, language: String? = null): String` | Gets all contexts of a multi-context service |

---

### `Sitemaps`

Provides methods to interact with openHAB sitemaps.

#### Methods

| Method | Returns | Description |
|---|---|---|
| `suspend fun getSitemaps(): String` | `String` | Gets all sitemaps |
| `suspend fun getSitemap(name: String, type: String? = null, includeHidden: Boolean = false, language: String? = null): String` | `String` | Gets a specific sitemap |
| `suspend fun getSitemapPage(name: String, pageId: String, subscriptionId: String? = null, includeHidden: Boolean = false, language: String? = null): String` | `String` | Gets a sitemap page |
| `fun getSitemapEvents(subId: String, sitemapName: String? = null, pageId: String? = null): SseSession` | `SseSession` | Gets sitemap events as SSE stream |
| `suspend fun subscribeToSitemapEvents(): String` | `String` | Creates a sitemap event subscription |

```kotlin
// Subscribe and listen to sitemap events
val subId = openHAB.sitemaps.subscribeToSitemapEvents()
lifecycleScope.launch {
    openHAB.sitemaps
        .getSitemapEvents(subId, sitemapName = "default")
        .collect()
        .collect { data -> Log.d("openHAB", "Sitemap event: $data") }
}
```

---

### `Systeminfo`

#### Methods

| Method | Description |
|---|---|
| `suspend fun getSystemInfo(): String` | Gets general system information |
| `suspend fun getUoMInfo(): String` | Gets units of measurement information |

---

### `Tags`

Provides methods to manage openHAB semantic tags.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getTags(language: String? = null): String` | Gets all semantic tags |
| `suspend fun createTag(json: String, language: String? = null): String` | Creates a new semantic tag |
| `suspend fun getTag(id: String, language: String? = null): String` | Gets a specific tag |
| `suspend fun updateTag(id: String, json: String, language: String? = null): String` | Updates a tag |
| `suspend fun deleteTag(id: String, language: String? = null): String` | Deletes a tag |

---

### `Templates`

#### Methods

| Method | Description |
|---|---|
| `suspend fun getTemplates(language: String? = null): String` | Gets all rule templates |
| `suspend fun getTemplate(uid: String, language: String? = null): String` | Gets a specific rule template |

---

### `ThingTypes`

#### Methods

| Method | Description |
|---|---|
| `suspend fun getThingTypes(bindingId: String? = null, language: String? = null): String` | Gets all available thing types |
| `suspend fun getThingType(uid: String, language: String? = null): String` | Gets a specific thing type |

---

### `Things`

Provides methods to manage openHAB things.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getThings(summary: Boolean = false, staticDataOnly: Boolean = false, language: String? = null): String` | Gets all things |
| `suspend fun createThing(json: String, language: String? = null): String` | Creates a new thing |
| `suspend fun getThing(uid: String, language: String? = null): String` | Gets a specific thing |
| `suspend fun updateThing(uid: String, json: String, language: String? = null): String` | Updates a thing |
| `suspend fun deleteThing(uid: String, force: Boolean = false, language: String? = null): String` | Deletes a thing |
| `suspend fun updateThingConfiguration(uid: String, configJson: String, language: String? = null): String` | Updates the thing configuration |
| `suspend fun getThingConfigStatus(uid: String, language: String? = null): String` | Gets the configuration status |
| `suspend fun setThingStatus(uid: String, enabled: Boolean, language: String? = null): String` | Enables or disables a thing |
| `suspend fun enableThing(uid: String): String` | Enables a thing |
| `suspend fun disableThing(uid: String): String` | Disables a thing |
| `suspend fun updateThingFirmware(uid: String, version: String, language: String? = null): String` | Updates the firmware |
| `suspend fun getThingFirmwareStatus(uid: String, language: String? = null): String` | Gets the firmware status |
| `suspend fun getThingFirmwares(uid: String, language: String? = null): String` | Gets available firmware versions |
| `suspend fun getThingStatus(uid: String, language: String? = null): String` | Gets the thing status |

```kotlin
openHAB.things.enableThing("zwave:device:controller:node5")
openHAB.things.updateThingConfiguration(
    uid        = "my:thing:uid",
    configJson = """{"port":"/dev/ttyUSB0"}""",
)
```

---

### `Transformations`

#### Methods

| Method | Description |
|---|---|
| `suspend fun getTransformations(): String` | Gets all transformations |
| `suspend fun getTransformation(uid: String): String` | Gets a specific transformation |
| `suspend fun updateTransformation(uid: String, json: String): String` | Updates a transformation |
| `suspend fun deleteTransformation(uid: String): String` | Deletes a transformation |
| `suspend fun getTransformationServices(): String` | Gets all transformation services |

---

### `UI`

Provides methods to manage UI components and tiles.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getUIComponents(namespace: String, summary: Boolean = false): String` | Gets all UI components in a namespace |
| `suspend fun addUIComponent(namespace: String, json: String): String` | Adds a UI component |
| `suspend fun getUIComponent(namespace: String, uid: String): String` | Gets a specific UI component |
| `suspend fun updateUIComponent(namespace: String, uid: String, json: String): String` | Updates a UI component |
| `suspend fun deleteUIComponent(namespace: String, uid: String): String` | Deletes a UI component |
| `suspend fun getUITiles(): String` | Gets all registered UI tiles |

---

### `UUID`

#### Methods

| Method | Description |
|---|---|
| `suspend fun getUUID(): String` | Gets the UUID of the openHAB instance |

```kotlin
val id = openHAB.uuid.getUUID()
Log.d("openHAB", "UUID: $id")
```

---

### `Voice`

Provides methods to interact with the openHAB voice system.

#### Methods

| Method | Description |
|---|---|
| `suspend fun getDefaultVoice(): String` | Gets the default voice |
| `suspend fun getVoices(): String` | Gets all available voices |
| `suspend fun getInterpreters(language: String? = null): String` | Gets all human language interpreters |
| `suspend fun interpretText(text: String, language: String? = null): String` | Sends text to the default interpreter |
| `suspend fun getInterpreter(id: String, language: String? = null): String` | Gets a specific interpreter |
| `suspend fun interpretTextBatch(text: String, ids: String, language: String? = null): String` | Sends text to multiple interpreters (comma-separated IDs) |
| `suspend fun startDialog(sourceId: String, ksId: String? = null, sttId: String? = null, ttsId: String? = null, voiceId: String? = null, hliIds: String? = null, sinkId: String? = null, keyword: String? = null, listeningItem: String? = null): String` | Starts dialog processing for an audio source |
| `suspend fun stopDialog(sourceId: String): String` | Stops dialog processing |
| `suspend fun listenAndAnswer(sourceId: String, sttId: String, ttsId: String, voiceId: String, hliIds: String? = null, sinkId: String? = null, listeningItem: String? = null): String` | Single listen-and-answer dialog without keyword spotting |
| `suspend fun sayText(text: String, voiceId: String, sinkId: String, volume: String = "100"): String` | Speaks text aloud |

```kotlin
openHAB.voice.sayText(
    text    = "Hello from openHAB!",
    voiceId = "voicerss:en-us",
    sinkId  = "javasound:sink:default",
)

openHAB.voice.startDialog(
    sourceId = "javasound:source:microphone",
    sttId    = "googlestt",
    ttsId    = "googletts",
    voiceId  = "google:en-US:en-US-Wavenet-A",
)
```

---

## Android vs. Python — Key Differences

| Topic | Android / Kotlin | Python |
|---|---|---|
| Distribution | JitPack / GitHub Packages / AAR | PyPI (`pip install`) |
| Import | `import io.github.michdo93.openhab.OpenHAB` | `from openhab import Items` |
| Entry point | `OpenHAB` facade with lazy properties | Individual class instances |
| All methods | `suspend` functions (Kotlin Coroutines) | Sync or `async` (aiohttp) |
| Coroutine scope | `lifecycleScope` / `viewModelScope` | `asyncio.run()` |
| SSE | `SseSession.collect()` → `Flow<String>` | `response.iter_lines()` |
| SSE cancellation | Cancel the coroutine — stream closes automatically | `response.close()` |
| Error handling | `OpenHABException` with `statusCode` property | `requests.exceptions.*` |
| Return type | `String` (raw JSON) | `dict`, `list`, or `str` |
| JSON parsing | Moshi / `org.json.JSONObject` | Built-in `json` module |
| Naming | camelCase (`sendCommand`, `getItemState`) | camelCase (identical) |
| Debug logging | `debug = BuildConfig.DEBUG` → OkHttp body log | Python `logging` module |
| Network config | `network_security_config.xml` for plain HTTP | `requests` `verify=False` |
| Min platform | Android API 21 (Android 5.0) | Python 3.x |

---

## Contributing

Contributions are welcome! Please create an issue or pull request to suggest changes.

### How to contribute:
1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add your feature description"
   ```
4. Push to the branch:
   ```bash
   git push origin feature/your-feature-name
   ```
5. Open a pull request.

Please ensure your code compiles with Kotlin 2.0+, all REST methods are `suspend` functions, and unit tests pass with `./gradlew :library:test`.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
