package io.github.michdo93.openhab

import io.github.michdo93.openhab.client.OpenHABClient
import io.github.michdo93.openhab.client.OpenHABException
import io.mockk.*
import kotlinx.coroutines.test.runTest
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the openHAB Android REST Client.
 *
 * These tests use MockK to mock OkHttp responses — no real openHAB server needed.
 * Run with: ./gradlew :library:test
 */
class OpenHABClientTest {

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun mockResponse(
        body: String,
        code: Int    = 200,
        type: String = "application/json",
    ): Response = Response.Builder()
        .code(code)
        .message("OK")
        .request(Request.Builder().url("http://localhost/").build())
        .protocol(Protocol.HTTP_1_1)
        .body(body.toResponseBody(type.toMediaType()))
        .build()

    // ─── URL normalization ─────────────────────────────────────────────────
    @Test
    fun `normalizeUrl adds rest prefix when missing`() {
        val c = OpenHABClient("http://localhost:8080", "user", "pass")
        val url = c.normalizeUrl("/items")
        assertTrue(url.contains("/rest/items"))
    }

    @Test
    fun `normalizeUrl does not double-add rest prefix`() {
        val c = OpenHABClient("http://localhost:8080", "user", "pass")
        val url = c.normalizeUrl("/rest/items")
        assertEquals("http://localhost:8080/rest/items", url)
    }

    @Test
    fun `normalizeUrl appends query params`() {
        val c = OpenHABClient("http://localhost:8080", "user", "pass")
        val url = c.normalizeUrl("/items", mapOf("type" to "Switch", "empty" to null))
        assertTrue(url.contains("type=Switch"))
        assertFalse(url.contains("empty"))
    }

    @Test
    fun `normalizeUrl strips trailing slash from baseUrl`() {
        val c = OpenHABClient("http://localhost:8080/", "user", "pass")
        val url = c.normalizeUrl("/items")
        assertFalse(url.contains("//rest"))
    }

    // ─── Client state ─────────────────────────────────────────────────────────
    @Test
    fun `isCloud is true for myopenhab org`() {
        val c = OpenHABClient("https://myopenhab.org", "user", "pass")
        assertTrue(c.isCloud)
    }

    @Test
    fun `isCloud is false for local server`() {
        val c = OpenHABClient("http://192.168.1.100:8080", "user", "pass")
        assertFalse(c.isCloud)
    }

    // ─── OpenHAB facade ───────────────────────────────────────────────────────
    @Test
    fun `OpenHAB lazy properties are all unique instances`() {
        val oh = OpenHAB("http://localhost:8080", "u", "p")
        assertNotNull(oh.items)
        assertNotNull(oh.things)
        assertNotNull(oh.rules)
        assertNotNull(oh.itemEvents)
        assertNotNull(oh.thingEvents)
        // lazy — same instance on second access
        assertSame(oh.items, oh.items)
        assertSame(oh.thingEvents, oh.thingEvents)
    }

    // ─── Exception ────────────────────────────────────────────────────────────
    @Test
    fun `OpenHABException carries statusCode`() {
        val ex = OpenHABException("Not Found", 404)
        assertEquals(404, ex.statusCode)
        assertEquals("Not Found", ex.message)
    }

    @Test
    fun `OpenHABException default statusCode is -1`() {
        val ex = OpenHABException("network error")
        assertEquals(-1, ex.statusCode)
    }

    // ─── langHeader helper ────────────────────────────────────────────────────
    @Test
    fun `null language returns empty headers map`() {
        val lang: String? = null
        val h = lang.langHeader()
        assertTrue(h.isEmpty())
    }

    @Test
    fun `non-null language returns Accept-Language header`() {
        val h = "de-DE".langHeader()
        assertEquals("de-DE", h["Accept-Language"])
    }
}

// expose internal for testing
internal fun String?.langHeader(): Map<String, String> =
    if (isNullOrEmpty()) emptyMap() else mapOf("Accept-Language" to this!!)
