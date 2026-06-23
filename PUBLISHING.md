# Android Library veröffentlichen — Schritt-für-Schritt

## Was wurde erstellt

```
openhab-android/
├── library/                       ← Das AAR-Paket (die eigentliche Library)
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       └── io/github/michdo93/openhab/
│           ├── OpenHAB.kt         ← Einstiegspunkt (Facade)
│           ├── client/
│           │   ├── OpenHABClient.kt
│           │   ├── OpenHABException.kt
│           │   └── SseSession.kt
│           ├── api/               ← Items, Things, Rules, ... (25 Klassen)
│           └── events/            ← ItemEvents, ThingEvents, ... (SSE)
├── app-example/                   ← Vollständige Beispiel-App
│   └── src/main/kotlin/.../MainActivity.kt
├── settings.gradle.kts
├── build.gradle.kts
└── gradle/libs.versions.toml
```

---

## Schritt 1: In Android Studio öffnen

1. Android Studio öffnen
2. **File → Open** → `openhab-android/` auswählen
3. Gradle sync abwarten (lädt OkHttp, Moshi, Coroutines)
4. Beispiel-App direkt starten: Gerät/Emulator wählen → **Run** auf `app-example`

---

## Schritt 2: AAR bauen

Ein `.aar` ist das Android-Äquivalent einer `.jar`-Datei — enthält Klassen,
Ressourcen, AndroidManifest und Proguard-Regeln.

### In Android Studio:
```
Build → Make Module 'library'
```

Das AAR liegt dann in:
```
library/build/outputs/aar/library-release.aar
```

### Per Kommandozeile:
```bash
cd openhab-android
./gradlew :library:assembleRelease
```

---

## Schritt 3: Library in ein anderes Projekt einbinden

### Option A — AAR direkt einbinden

1. `library-release.aar` in `app/libs/` kopieren
2. In der `app/build.gradle.kts`:

```kotlin
dependencies {
    implementation(files("libs/library-release.aar"))

    // Transitive Abhängigkeiten manuell angeben:
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
```

### Option B — Lokales Maven (empfohlen für Entwicklung)

Library ins lokale Maven-Repository (`~/.m2`) installieren:
```bash
./gradlew :library:publishToMavenLocal
```

In jedem anderen Projekt dann in `settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal()   // ← zuerst!
        google()
        mavenCentral()
    }
}
```

Und in `build.gradle.kts`:
```kotlin
dependencies {
    implementation("io.github.michdo93:openhab-rest-client-android:1.0.0")
}
```

### Option C — git submodule (für Monorepos)

```bash
git submodule add https://github.com/Michdo93/android-openhab-rest-client
```

In `settings.gradle.kts`:
```kotlin
include(":openhab-rest-client-android:library")
project(":openhab-rest-client-android:library").projectDir =
    file("openhab-rest-client-android/library")
```

In `build.gradle.kts`:
```kotlin
dependencies {
    implementation(project(":openhab-rest-client-android:library"))
}
```

---

## Schritt 4: Öffentlich veröffentlichen

### GitHub Packages (einfachster öffentlicher Weg)

In `library/build.gradle.kts` ergänzen:
```kotlin
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url  = uri("https://maven.pkg.github.com/Michdo93/android-openhab-rest-client")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

Veröffentlichen:
```bash
GITHUB_ACTOR=Michdo93 GITHUB_TOKEN=ghp_xxx \
    ./gradlew :library:publish
```

Nutzer fügen in ihrer `settings.gradle.kts` ein:
```kotlin
maven {
    url = uri("https://maven.pkg.github.com/Michdo93/android-openhab-rest-client")
    credentials {
        username = "GithubUsername"
        password = "ghp_PersonalAccessToken"
    }
}
```

---

### Maven Central (maximale Reichweite)

Erfordert:
- Account auf [central.sonatype.com](https://central.sonatype.com)
- GPG-Schlüssel für Signierung
- Namespace-Verifizierung (`io.github.michdo93`)

Dann in `library/build.gradle.kts`:
```kotlin
plugins {
    id("signing")
    id("maven-publish")
}

signing {
    sign(publishing.publications["release"])
}
```

```bash
./gradlew :library:publishToSonatype :library:closeAndReleaseSonatypeStagingRepository
```

Danach ist die Library über:
```kotlin
implementation("io.github.michdo93:openhab-rest-client-android:1.0.0")
```

ohne extra Repository-Angabe verfügbar.

---

### JitPack (zero Setup)

Sobald das Repo auf GitHub liegt:
```kotlin
// settings.gradle.kts
repositories { maven("https://jitpack.io") }

// build.gradle.kts
implementation("com.github.Michdo93:android-openhab-rest-client:v1.0.0")
```

---

## Verwendung in der App

### Minimales Setup

```kotlin
// Einmalig (z.B. in ViewModel oder Application)
val openHAB = OpenHAB(
    url      = "http://192.168.1.100:8080",
    username = "openhab",
    password = "habopen",
)

// Login (optional, wird automatisch beim ersten Request geprüft)
lifecycleScope.launch {
    openHAB.login()
}
```

### Items

```kotlin
// Alle Items
val items = openHAB.items.getItems()

// Befehl senden
openHAB.items.sendCommand("LivingRoomLight", "ON")

// Status lesen
val state = openHAB.items.getItemState("LivingRoomLight")

// State setzen
openHAB.items.updateItemState("Thermostat", "21.5")
```

### SSE (Live-Updates)

```kotlin
// In einem ViewModel:
class SmartHomeViewModel : ViewModel() {
    private val openHAB = OpenHAB("http://192.168.1.100:8080", "openhab", "habopen")

    val lightState = openHAB.itemEvents
        .itemStateChangedEvent("LivingRoomLight")
        .collect()                         // Flow<String>
        .map { json -> parseState(json) }  // eigener Parser
        .stateIn(viewModelScope, SharingStarted.Lazily, "UNKNOWN")
}

// In der Activity:
viewModel.lightState
    .flowWithLifecycle(lifecycle)
    .onEach { state -> updateSwitch(state == "ON") }
    .launchIn(lifecycleScope)
```

### Fehlerbehandlung

```kotlin
try {
    openHAB.items.sendCommand("LivingRoomLight", "ON")
} catch (e: OpenHABException) {
    when (e.statusCode) {
        401  -> showError("Keine Berechtigung")
        404  -> showError("Item nicht gefunden")
        else -> showError("Fehler ${e.statusCode}: ${e.message}")
    }
}
```

### JSON parsen (mit Moshi — bereits als Abhängigkeit enthalten)

```kotlin
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class OpenHABItem(
    val name: String,
    val type: String,
    val state: String? = null,
)

val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
val adapter = moshi.adapter(OpenHABItem::class.java)

val item = adapter.fromJson(openHAB.items.getItem("LivingRoomLight"))
println(item?.state)   // "ON"
```

---

## Berechtigungen

Die Library fügt automatisch `INTERNET` ins Manifest ein (Manifest Merger).
Keine weitere Konfiguration nötig.

---

## ProGuard / R8

Für Release-Builds mit Minifizierung in `proguard-rules.pro` eintragen:
```proguard
-keep class io.github.michdo93.openhab.** { *; }
-keepnames class io.github.michdo93.openhab.** { *; }
```

Oder die mitgelieferte `consumer-rules.pro` wird automatisch beim AAR-Import
angewendet (empfohlen).

---

## Mindestanforderungen

| | Anforderung |
|---|---|
| Android API | 21+ (Android 5.0 Lollipop) |
| Kotlin | 2.0+ |
| Coroutines | 1.8+ |
| OkHttp | 4.12+ |
| Gradle | 8.5+ |
| Android Gradle Plugin | 8.5+ |
