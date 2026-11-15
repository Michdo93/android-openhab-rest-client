package io.github.michdo93.openhab

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Integration tests against a real openHAB with the example configuration of the book series
 * (item Wohnzimmer_Licht). Skipped without OPENHAB_URL/OPENHAB_TOKEN.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTest {

    private lateinit var client: OpenHABClient

    @BeforeAll
    fun connect() {
        val url = System.getenv("OPENHAB_URL")
        val token = System.getenv("OPENHAB_TOKEN")
        assumeTrue(url != null && token != null, "OPENHAB_URL/OPENHAB_TOKEN not set")
        client = OpenHABClient(url, token = token)
        assumeTrue(client.login(), "openHAB not reachable")
    }

    @Test
    fun readAndCommand() = runBlocking {
        val items = Items(client)
        assertEquals("Switch", Json.map(items.getItem("Wohnzimmer_Licht"))["type"])
        assertEquals(mapOf("message" to "OK"), items.sendCommand("Wohnzimmer_Licht", "ON"))
        assertEquals("Item not found.", Json.error(items.getItem("Gibtsnicht")))
    }

    @Test
    fun stateChangedEventArrives() = runBlocking {
        val items = Items(client)
        items.postUpdate("Wohnzimmer_Licht", "OFF")
        delay(300)
        val stream = ItemEvents(client).ItemStateChangedEvent("Wohnzimmer_Licht")
        assertFalse(stream.isError, stream.error.toString())
        // the update to OFF may still arrive as an event: wait for the change to ON
        val got = async { stream.events().first { Json.get(it, "payload", "value") == "ON" } }
        delay(500)
        items.sendCommand("Wohnzimmer_Licht", "ON")
        val event = withTimeout(10_000) { got.await() }
        assertEquals("ON", Json.get(event, "payload", "value"))
        assertFalse(stream.isOpen)
    }

    @Test
    fun stateTracker() = runBlocking {
        val events = Events(client)
        val s = events.initiateStateTracker()
        // first message: the connection id
        val id = s.lines().filter { it.startsWith("data:") }.first().substring(5).trim()
        assertEquals(mapOf("message" to "OK"), events.updateSSEConnectionItems(id, listOf("Wohnzimmer_Licht")))
    }
}
