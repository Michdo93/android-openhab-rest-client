plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("maven-publish")
}

android {
    namespace   = "io.github.michdo93.openhab"
    compileSdk  = 35

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    // OkHttp — HTTP + SSE
    api(libs.okhttp)
    api(libs.okhttp.sse)
    implementation(libs.okhttp.logging)

    // Moshi — JSON
    api(libs.moshi)
    api(libs.moshi.kotlin)
    kapt(libs.moshi.kotlin.codegen)

    // Coroutines
    api(libs.coroutines.core)
    api(libs.coroutines.android)

    // Tests
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
}

// Publish to local Maven (~/.m2) via `./gradlew publishToMavenLocal`
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId    = "io.github.michdo93"
                artifactId = "openhab-rest-client-android"
                version    = "1.0.0"

                pom {
                    name.set("OpenHAB REST Client Android")
                    description.set("Kotlin Android client for the openHAB REST API")
                    url.set("https://github.com/Michdo93/android-openhab-rest-client")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            id.set("michdo93")
                            name.set("Michael Christian Dörflinger")
                            email.set("michaeldoerflinger93@gmail.com")
                        }
                    }
                }
            }
        }
    }
}