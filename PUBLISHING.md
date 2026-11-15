# android-openhab-rest-client veröffentlichen

Ab 2.0.0 ist die Bibliothek ein reines Kotlin/JVM-Projekt (ein Modul im Hauptordner, kein
`library/`, keine `app-example`). Das Ergebnis ist eine JAR-Datei, die Android-Apps und jede JVM
verwenden. Einzige Abhängigkeit: `kotlinx-coroutines-core`.

```
android-openhab-rest-client/
├── build.gradle.kts          ← Version, Abhängigkeiten, Veröffentlichung
├── settings.gradle.kts       ← Maven-Central-Plugin (nmcp)
├── gradle/libs.versions.toml ← Kotlin-, Coroutines- und JUnit-Version
├── jitpack.yml               ← JDK 17 für JitPack
├── src/main/kotlin/io/github/michdo93/openhab/
│   ├── OpenHABClient.kt, Json.kt, SSEConnection.kt, SSESocket.kt, SSE.kt, Py.kt, …  (von Hand)
│   └── Items.kt, Things.kt, … OpenHAB.kt                                            (erzeugt)
├── src/test/kotlin/…         ← Unit- und Integrationstests (JUnit 5)
└── tools/py2kt.py            ← Generator aus python-openhab-rest-client
```

## 1. Klassen neu erzeugen (bei einer neuen Python-Version)

```bash
python3 tools/py2kt.py ../python-openhab-rest-client/openhab src/main/kotlin/io/github/michdo93/openhab
python3 tools/py2kt.py --readme ../python-openhab-rest-client/openhab   # Tabellen für README.md
```

## 2. Bauen und testen

```bash
./gradlew build                  # Unit-Tests gegen einen lokalen HTTP-Server, JARs in build/libs/
OPENHAB_URL=http://127.0.0.1:8080 OPENHAB_TOKEN=oh.… ./gradlew test   # zusätzlich gegen openHAB
```

`build/libs/` enthält danach `android-openhab-rest-client-<version>.jar` sowie `-sources.jar` und
`-javadoc.jar`.

## 3. Version erhöhen

`version` in `build.gradle.kts` und die Versionsnummern in `README.md` anpassen.

## 4. Veröffentlichen

### JitPack (Hauptweg, ohne Einrichtung)

Tag **ohne `v`** pushen – JitPack verwendet den Tag-Namen genau als Version:

```bash
git tag 2.0.0
git push origin main 2.0.0
```

Danach auf [jitpack.io/#Michdo93/android-openhab-rest-client](https://jitpack.io/#Michdo93/android-openhab-rest-client)
„Get it“ bei 2.0.0 anklicken und im Log prüfen, ob der Bau durchläuft. Einbinden:

```kotlin
implementation("com.github.Michdo93:android-openhab-rest-client:2.0.0")
```

Ein fehlgeschlagener Bau bleibt zwischengespeichert: bei jitpack.io mit GitHub anmelden, dann
erscheint neben dem Log ein Papierkorb-Symbol zum Löschen.

### GitHub Release, GitHub Packages und Maven Central

Ein GitHub-Release zum Tag startet den Workflow `.github/workflows/ci.yml`:

- **GitHub Packages:** `io.github.michdo93:android-openhab-rest-client` (Nutzer brauchen ein Token
  mit `read:packages`)
- **Maven Central:** nur wenn die Secrets `SIGNING_KEY`, `SIGNING_PASSWORD`, `SONATYPE_USERNAME`
  und `SONATYPE_PASSWORD` im Repository hinterlegt sind (Namespace `io.github.michdo93`)

Die JAR-Datei aus `build/libs/` (oder aus dem Artefakt des CI-Laufs) als Asset an das Release
hängen – für alle, die ohne Repository arbeiten.

Lokal ins eigene Maven-Repository (zum Ausprobieren in einer App):

```bash
./gradlew publishToMavenLocal    # danach mavenLocal() in settings.gradle.kts der App
```

## Mindestanforderungen

| | Anforderung |
|---|---|
| Android | API 21+ (Android 5.0), Berechtigung `INTERNET` |
| JVM | Java 8+ (gebaut mit JDK 17) |
| Kotlin in der App | 1.9+ |
| Coroutines | 1.8+ |
| Gradle (Bau der Bibliothek) | 8.11 (Wrapper) |

ProGuard/R8: keine eigenen Regeln nötig, die Bibliothek verwendet keine Reflexion.
