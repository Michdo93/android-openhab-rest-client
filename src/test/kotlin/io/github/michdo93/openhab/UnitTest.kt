package io.github.michdo93.openhab

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/** Unit tests against a local HTTP server that echoes what it receives (no openHAB needed). */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UnitTest {

    private lateinit var server: HttpServer
    private lateinit var base: String

    private val events = "event: message\ndata: {\"topic\":\"openhab/items/A/statechanged\"," +
        "\"payload\":\"{\\\"value\\\":\\\"ON\\\"}\",\"type\":\"ItemStateChangedEvent\"}\n\n" +
        "data: {\"type\":\"ALIVE\",\"interval\":10}\n\n"

    @BeforeAll
    fun start() {
        server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { ex ->
            val path = ex.requestURI.rawPath
            val query = ex.requestURI.rawQuery ?: ""
            val body = String(ex.requestBody.readBytes(), Charsets.UTF_8)
            var status = 200
            var type = "application/json"
            var chunked = false
            var hold = false
            val out: ByteArray = when {
                path.contains("notfound") || query.contains("notfound") -> {
                    status = 404
                    "{\"error\":{\"message\":\"Item gibtsnicht does not exist!\",\"http-code\":404}}".toByteArray()
                }
                path.startsWith("/rest/empty") -> {
                    status = 201
                    ex.responseHeaders.add("Location", "http://x/rest/rules/neu")
                    ByteArray(0)
                }
                path.startsWith("/rest/text") -> {
                    type = "text/plain"
                    "21.5 °C".toByteArray(Charsets.UTF_8)
                }
                path.startsWith("/rest/events") -> {
                    type = "text/event-stream"
                    chunked = query.contains("chunked") || query.contains("hold")
                    hold = query.contains("hold")
                    events.toByteArray(Charsets.UTF_8)
                }
                else -> Json.stringify(linkedMapOf(
                    "method" to ex.requestMethod, "path" to path, "query" to ex.requestURI.rawQuery,
                    "auth" to ex.requestHeaders.getFirst("Authorization"),
                    "accept" to ex.requestHeaders.getFirst("Accept"),
                    "contentType" to ex.requestHeaders.getFirst("Content-Type"), "body" to body,
                )).toByteArray(Charsets.UTF_8)
            }
            ex.responseHeaders.add("Content-Type", type)
            ex.sendResponseHeaders(status, if (chunked) 0 else if (out.isEmpty()) -1 else out.size.toLong())
            ex.responseBody.use { os ->
                os.write(out)
                os.flush()
                if (hold) Thread.sleep(30_000)   // a stream that stays silent
            }
        }
        server.executor = java.util.concurrent.Executors.newCachedThreadPool()
        server.start()
        base = "http://127.0.0.1:" + server.address.port
    }

    @AfterAll
    fun stop() = server.stop(0)

    @Test
    fun jsonRoundTrip() {
        val v = Json.parse("{\"a\":[1,2.5,\"x\\u00e4\",true,null],\"b\":{}}")
        assertEquals("{\"a\":[1,2.5,\"xä\",true,null],\"b\":{}}", Json.stringify(v))
        assertEquals(2.5, Json.get(v, "a", 1))
        assertEquals("xä", Json.str(v, "a", -3))
    }

    @Test
    fun tokenAndBasicAuth() {
        val t = OpenHABClient(base, token = "oh.test.123")
        assertEquals("Bearer oh.test.123", Json.str(t.get("/echo"), "auth"))
        val b = OpenHABClient("$base/", "admin", "pässwörd")
        val auth = Json.str(b.get("/echo"), "auth")!!
        assertEquals("admin:pässwörd",
            String(java.util.Base64.getDecoder().decode(auth.substring(6)), Charsets.UTF_8))
        // own Base64 encoder (java.util.Base64 needs Android 8) – all padding variants
        for (s in listOf("", "a", "ab", "abc", "abcd", "äöü€")) {
            assertEquals(java.util.Base64.getEncoder().encodeToString(s.toByteArray()),
                OpenHABClient.base64(s.toByteArray()))
        }
    }

    @Test
    fun sendCommandIsPlainText() = runBlocking {
        val r = Items(OpenHABClient(base)).sendCommand("Wohnzimmer_Dimmer", "30")
        assertEquals("POST", Json.str(r, "method"))
        assertEquals("/rest/items/Wohnzimmer_Dimmer", Json.str(r, "path"))
        assertEquals("30", Json.str(r, "body"))
        assertTrue(Json.str(r, "contentType")!!.startsWith("text/plain"))
    }

    @Test
    fun hashInUidIsEncoded() = runBlocking {
        val r = Links(OpenHABClient(base)).getLink("Item", "astro:sun:home:rise#start")
        assertEquals("/rest/links/Item/astro%3Asun%3Ahome%3Arise%23start", Json.str(r, "path"))
    }

    @Test
    fun deleteAllLinksAcceptsJson() = runBlocking {
        val r = Links(OpenHABClient(base)).deleteAllLinks("Item")
        assertEquals("DELETE", Json.str(r, "method"))
        assertEquals("application/json", Json.str(r, "accept"))
    }

    @Test
    fun storeItemDataUsesQuery() = runBlocking {
        val r = Persistence(OpenHABClient(base)).storeItemData("T", "2026-01-01T00:00:00Z", "21.5")
        assertTrue(Json.str(r, "query")!!.contains("state=21.5"), Json.str(r, "query"))
        assertEquals("", Json.str(r, "body"))
    }

    @Test
    fun formBodyIsUrlencoded() {
        val r = OpenHABClient(base).post("/auth/logout",
            mapOf("Content-Type" to "application/x-www-form-urlencoded"), mapOf("refresh_token" to "r t"))
        assertEquals("refresh_token=r+t", Json.str(r, "body"))
    }

    @Test
    fun errorsAreObjectsNotExceptions() = runBlocking {
        val c = OpenHABClient(base)
        assertThrows(OpenHABHTTPError::class.java) { c.get("/notfound") }
        assertTrue(Json.isError(Items(c).getItem("notfound")))
        val down = Items(OpenHABClient("http://127.0.0.1:9", timeout = 2.0)).getItems()
        assertTrue(Json.error(down)!!.startsWith("Request error:"), Json.error(down))
    }

    @Test
    fun emptyBodyGivesStatusResponse() {
        val c = OpenHABClient(base)
        val r = c.post("/empty", data = "x")
        assertTrue(r is StatusResponse)
        assertEquals(201, (r as StatusResponse).status)
        assertEquals("http://x/rest/rules/neu", r["location"])
        assertFalse(Json.isError(r))
        assertEquals("21.5 °C", c.get("/text"))
    }

    @Test
    fun namedOptionalParameters() = runBlocking {
        val r = Items(OpenHABClient(base)).getItems(tags = "Light", fields = "name,state")
        assertTrue(Json.str(r, "query")!!.contains("tags=Light"), Json.str(r, "query"))
        assertTrue(Json.str(r, "query")!!.contains("fields=name%2Cstate"), Json.str(r, "query"))
    }

    @Test
    fun eventStreamFixedLengthAndChunked() = runBlocking {
        val c = OpenHABClient(base)
        val s = ItemEvents(c).ItemStateChangedEvent()
        assertFalse(s.isError)
        val list = s.events().toList()
        assertEquals(1, list.size)
        assertEquals("ON", Json.get(list[0], "payload", "value"))
        assertFalse(s.isOpen)
        val chunked = Events(c).getEvents("chunked")
        assertEquals(2, chunked.eventSequence(includeAlive = true).count())
        chunked.close()
        val bad = Events(c).getEvents("notfound")
        assertTrue(bad.isError)
        assertEquals(0, bad.events().toList().size)
    }

    @Test
    fun cancellingTheCollectorClosesTheStreamAtOnce() = runBlocking {
        val s = Events(OpenHABClient(base)).getEvents("hold")
        var n = 0
        val job = launch { s.events(includeAlive = true).collect { n++ } }
        delay(500)
        val t0 = System.currentTimeMillis()
        withTimeout(2000) { job.cancel(); job.join() }
        assertTrue(System.currentTimeMillis() - t0 < 1000)
        assertEquals(2, n)
        assertFalse(s.isOpen)
    }

    @Test
    fun closeFromAnotherThreadEndsABlockingLoop() {
        val s = Events(OpenHABClient(base)).let { runBlocking { it.getEvents("hold") } }
        val loop = Thread { for (line in s) { } }
        loop.start()
        Thread.sleep(500)
        s.close()
        loop.join(2000)
        assertFalse(loop.isAlive)
    }
}
