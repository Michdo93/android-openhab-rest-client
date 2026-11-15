import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    `maven-publish`
    signing
}

group = "io.github.michdo93"
version = "2.0.0"

// Java 8 bytecode: runs on every Android version (minSdk 21) and on every JVM
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

dependencies {
    // the only dependency: suspend functions and Flow
    api(libs.coroutines.core)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
}

tasks.test {
    useJUnitPlatform()
    // the integration tests run only if OPENHAB_URL and OPENHAB_TOKEN are set
    testLogging { events("passed", "skipped", "failed") }
}

tasks.jar {
    manifest {
        attributes("Implementation-Title" to "android-openhab-rest-client", "Implementation-Version" to version)
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["java"])
            artifactId = "android-openhab-rest-client"
            pom {
                name.set("android-openhab-rest-client")
                description.set("Kotlin client for the openHAB REST API for Android and the JVM")
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
                    }
                }
                scm {
                    url.set("https://github.com/Michdo93/android-openhab-rest-client")
                    connection.set("scm:git:git://github.com/Michdo93/android-openhab-rest-client.git")
                    developerConnection.set("scm:git:ssh://git@github.com/Michdo93/android-openhab-rest-client.git")
                }
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Michdo93/android-openhab-rest-client")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: ""
                password = System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }
}

signing {
    val signingKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    if (!signingKey.isNullOrEmpty() && !signingPassword.isNullOrEmpty()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["release"])
    }
}
