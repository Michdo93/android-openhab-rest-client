package io.github.michdo93.openhab

import java.io.IOException
import java.io.InputStream
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Core HTTP client for the openHAB REST API – the counterpart of `OpenHABClient` in
 * python-openhab-rest-client.
 *
 * Without external HTTP or JSON libraries: it uses `HttpURLConnection`, which Android and
 * every JVM provide. The REST classes ([Items], [Things], [Rules], …) take an instance in
 * their constructor; their methods are `suspend` functions that run on [Dispatchers.IO].
 *
 * ```kotlin
 * // API token (recommended)
 * val client = OpenHABClient("http://192.168.1.10:8080", token = "oh.mytoken.xxx")
 * // Basic Authentication (has to be enabled in openHAB, also used by myopenHAB)
 * val client = OpenHABClient("https://home.myopenhab.org", "user", "password")
 * ```
 *
 * Unlike the Python library the constructor does not contact the server, because Android
 * forbids network access on the main thread. Call [connect] (suspend) or [login] (blocking)
 * to check the connection and set [isLoggedIn].
 *
 * The low-level methods [get], [post], [put], [delete] block and return the decoded JSON
 * (`Map`, `List`, …), the response text, or a [StatusResponse] for empty bodies. They throw
 * [OpenHABHTTPError] for 4xx/5xx and [OpenHABRequestException] if no answer arrives. The REST
 * classes translate both into `{"error": "..."}`, just like the Python library.
 *
 * @param url base URL of the openHAB server, e.g. `"http://192.168.1.10:8080"`
 * @param username optional; user name for Basic Authentication
 * @param password optional; password for Basic Authentication
 * @param token optional; API token (Bearer), takes precedence over Basic Authentication
 * @param timeout timeout in seconds for normal requests (event streams: only for connecting)
 */
class OpenHABClient @JvmOverloads constructor(
    url: String,
    val username: String? = null,
    val password: String? = null,
    val token: String? = null,
    val timeout: Double = DEFAULT_TIMEOUT,
) : AutoCloseable {

    /** Base URL without trailing slashes. */
    val url: String = url.trimEnd('/')

    /** `true` for myopenHAB (cloud access). */
    val isCloud: Boolean = this.url == "https://myopenhab.org" || this.url == "https://home.myopenhab.org"

    /** `true` after a successful [connect] or [login]. */
    @Volatile
    var isLoggedIn: Boolean = false
        private set

    /** The reason why the last [connect] or [login] failed, otherwise `null`. */
    @Volatile
    var loginError: String? = null
        private set

    // ── Connection check ─────────────────────────────────────────────────────

    /** Checks whether openHAB answers `/rest/` with these credentials (blocking). */
    fun login(): Boolean {
        val target = this.url + "/rest/"
        try {
            val c = open(target, "GET", timeoutMs())
            val status = c.responseCode
            c.disconnect()
            isLoggedIn = status < 400
            loginError = if (isLoggedIn) null
            else "HTTP error occurred: $status ${kind(status)}: ${reason(status)} for url: $target"
        } catch (e: Exception) {
            isLoggedIn = false
            loginError = "Connection error occurred: " + describe(e)
        }
        return isLoggedIn
    }

    /** Like [login], as a suspend function on [Dispatchers.IO] (safe on Android's main thread). */
    suspend fun connect(): Boolean = withContext(Dispatchers.IO) { login() }

    private fun timeoutMs(): Int = (timeout * 1000).toInt()

    private fun open(target: String, method: String, readTimeout: Int): HttpURLConnection {
        val c = try {
            URL(target).openConnection() as HttpURLConnection
        } catch (e: Exception) {
            throw OpenHABRequestException("Invalid URL $target: ${e.message}", e)
        }
        c.requestMethod = method
        c.connectTimeout = (maxOf(timeout, SSE_CONNECT_TIMEOUT) * 1000).toInt()
        c.readTimeout = readTimeout
        c.instanceFollowRedirects = true
        c.useCaches = false
        authorization()?.let { c.setRequestProperty("Authorization", it) }
        return c
    }

    private fun authorization(): String? = when {
        !token.isNullOrEmpty() -> "Bearer $token"
        !username.isNullOrEmpty() && !password.isNullOrEmpty() ->
            "Basic " + base64("$username:$password".toByteArray(Charsets.UTF_8))
        else -> null
    }

    // ── URLs ─────────────────────────────────────────────────────────────────

    /** Builds the absolute URL for a resource path (with or without the /rest prefix). */
    internal fun buildUrl(resourcePath: String, params: Map<String, Any?>?): String {
        var path = resourcePath.trim()
        if (!path.startsWith("http://") && !path.startsWith("https://")) {
            if (!path.startsWith("/")) path = "/$path"
            if (!(path == "/rest" || path.startsWith("/rest/") || path.startsWith("/rest?"))) path = "/rest$path"
            path = url + path
        }
        val q = path.indexOf('?')
        var base = if (q >= 0) path.substring(0, q) else path
        var query = if (q >= 0) path.substring(q + 1) else ""
        // "#" is valid inside openHAB UIDs (channel and action UIDs) but would start a fragment.
        base = encodePath(base.replace("#", "%23"))
        val extra = urlencode(cleanParams(params))
        if (extra.isNotEmpty()) query = if (query.isEmpty()) extra else "$query&$extra"
        return if (query.isEmpty()) base else "$base?$query"
    }

    // ── Requests ─────────────────────────────────────────────────────────────

    private fun executeRequest(method: String, resourcePath: String?, headers: Map<String, Any?>?,
                               data: Any?, params: Map<String, Any?>?): Any? {
        if (resourcePath.isNullOrEmpty()) {
            throw IllegalArgumentException("You must specify a valid resource path and HTTP method!")
        }
        val target = buildUrl(resourcePath, params)
        val h = LinkedHashMap<String, String>()
        var contentTypeKey: String? = null
        headers?.forEach { (k, v) ->
            if (v != null) {
                h[k] = v.toString()
                if (k.equals("content-type", ignoreCase = true)) contentTypeKey = k
            }
        }
        var body: String? = null
        if (data != null) {
            when {
                data is String -> {
                    body = data
                    if (data.isNotEmpty() && contentTypeKey == null) {
                        // openHAB rejects bodies without a matching media type (415)
                        val t = data.trimStart()
                        h["Content-Type"] = if (t.startsWith("{") || t.startsWith("[")) "application/json" else "text/plain"
                    }
                }
                data is Map<*, *> && contentTypeKey != null &&
                    h[contentTypeKey]!!.lowercase().contains("x-www-form-urlencoded") -> {
                    @Suppress("UNCHECKED_CAST")
                    body = urlencode(cleanParams(data as Map<String, Any?>))
                }
                data is Number || data is Boolean -> {
                    body = data.toString()
                    if (contentTypeKey == null) h["Content-Type"] = "text/plain"
                }
                else -> {
                    body = Json.stringify(data)
                    if (contentTypeKey == null) h["Content-Type"] = "application/json"
                }
            }
        }
        val c = open(target, method, timeoutMs())
        try {
            h.forEach { (k, v) -> c.setRequestProperty(k, v) }
            if (body != null) {
                c.doOutput = true
                val bytes = body.toByteArray(Charsets.UTF_8)
                c.setFixedLengthStreamingMode(bytes.size)
                c.outputStream.use { it.write(bytes) }
            }
            val status = c.responseCode
            // openHAB sends UTF-8 (e.g. "21.5 °C") without always declaring it
            val stream: InputStream? = if (status >= 400) c.errorStream else c.inputStream
            val text = stream?.use { String(it.readBytes(), Charsets.UTF_8) } ?: ""
            raiseForStatus(status, target, text)
            if (text.isBlank()) {
                val s = StatusResponse(status)
                c.getHeaderField("Location")?.let { s["location"] = it }
                return s
            }
            if ((c.contentType ?: "").contains("json")) {
                return try {
                    Json.parse(text)
                } catch (e: IllegalArgumentException) {
                    text
                }
            }
            return text
        } catch (e: OpenHABRequestException) {
            throw e
        } catch (e: IOException) {
            throw OpenHABRequestException(describe(e) + " (" + target + ")", e)
        } finally {
            c.disconnect()
        }
    }

    /** GET request (blocking). Returns decoded JSON, text, or a [StatusResponse] for empty bodies. */
    @JvmOverloads
    fun get(endpoint: String, headers: Map<String, Any?>? = null, params: Map<String, Any?>? = null): Any? =
        executeRequest("GET", endpoint, headers, null, params)

    /** POST request (blocking); `data` may be a String, a Map/List (sent as JSON) or a number. */
    @JvmOverloads
    fun post(endpoint: String, headers: Map<String, Any?>? = null, data: Any? = null,
             params: Map<String, Any?>? = null): Any? = executeRequest("POST", endpoint, headers, data, params)

    /** PUT request (blocking); `data` may be a String, a Map/List (sent as JSON) or a number. */
    @JvmOverloads
    fun put(endpoint: String, headers: Map<String, Any?>? = null, data: Any? = null,
            params: Map<String, Any?>? = null): Any? = executeRequest("PUT", endpoint, headers, data, params)

    /** DELETE request (blocking). */
    @JvmOverloads
    fun delete(endpoint: String, headers: Map<String, Any?>? = null, data: Any? = null,
               params: Map<String, Any?>? = null): Any? = executeRequest("DELETE", endpoint, headers, data, params)

    // ── Server-Sent Events ───────────────────────────────────────────────────

    /**
     * Opens a Server-Sent Events stream (blocking until openHAB sends the response headers).
     * There is no read timeout, because an event stream may stay silent for a long time.
     *
     * @throws OpenHABHTTPError if the server rejects the request
     * @throws OpenHABRequestException if the server is not reachable
     */
    @JvmOverloads
    fun executeSSE(url: String, headers: Map<String, Any?>? = null, params: Map<String, Any?>? = null): SSEConnection {
        val target = buildUrl(url, params)
        val h = LinkedHashMap<String, String>()
        authorization()?.let { h["Authorization"] = it }
        // identity: a compressed stream would be flushed late, events would arrive delayed
        h["Accept"] = "text/event-stream"
        h["Accept-Encoding"] = "identity"
        h["Cache-Control"] = "no-cache"
        headers?.forEach { (k, v) -> if (v != null) h[k] = v.toString() }
        val s = try {
            SSESocket.get(target, h, (maxOf(timeout, SSE_CONNECT_TIMEOUT) * 1000).toInt())
        } catch (e: IOException) {
            throw OpenHABRequestException(describe(e) + " (" + target + ")", e)
        } catch (e: IllegalArgumentException) {
            throw OpenHABRequestException("Invalid URL $target: ${e.message}", e)
        } catch (e: java.net.URISyntaxException) {
            throw OpenHABRequestException("Invalid URL $target: ${e.message}", e)
        }
        if (s.status >= 400) {
            val text = try {
                String(s.body.readBytes(), Charsets.UTF_8)
            } catch (e: IOException) {
                ""
            } finally {
                s.close()
            }
            raiseForStatus(s.status, target, text)
        }
        return SSEConnection(s, s.body, target)
    }

    /** For symmetry with the Python library; `HttpURLConnection` keeps no session to close. */
    override fun close() {}

    companion object {
        /** Default timeout in seconds for normal requests. */
        const val DEFAULT_TIMEOUT = 10.0
        /** Timeout in seconds for connecting an event stream (there is no read timeout). */
        const val SSE_CONNECT_TIMEOUT = 10.0

        private val REASONS = mapOf(
            400 to "Bad Request", 401 to "Unauthorized", 403 to "Forbidden", 404 to "Not Found",
            405 to "Method Not Allowed", 406 to "Not Acceptable", 408 to "Request Timeout",
            409 to "Conflict", 410 to "Gone", 412 to "Precondition Failed", 413 to "Payload Too Large",
            415 to "Unsupported Media Type", 422 to "Unprocessable Entity", 429 to "Too Many Requests",
            500 to "Internal Server Error", 501 to "Not Implemented", 502 to "Bad Gateway",
            503 to "Service Unavailable", 504 to "Gateway Timeout")

        internal fun reason(status: Int): String = REASONS[status] ?: ""

        /** Base64 without java.util.Base64 (only available from Android 8). */
        internal fun base64(data: ByteArray): String {
            val t = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            val sb = StringBuilder()
            var i = 0
            while (i < data.size) {
                val b0 = data[i].toInt() and 0xff
                val b1 = if (i + 1 < data.size) data[i + 1].toInt() and 0xff else 0
                val b2 = if (i + 2 < data.size) data[i + 2].toInt() and 0xff else 0
                sb.append(t[b0 shr 2]).append(t[((b0 and 3) shl 4) or (b1 shr 4)])
                sb.append(if (i + 1 < data.size) t[((b1 and 15) shl 2) or (b2 shr 6)] else '=')
                sb.append(if (i + 2 < data.size) t[b2 and 63] else '=')
                i += 3
            }
            return sb.toString()
        }

        private fun kind(status: Int) = if (status < 500) "Client Error" else "Server Error"

        internal fun describe(e: Throwable): String {
            val m = e.message
            return when (e) {
                is SocketTimeoutException -> "Timeout: " + (m ?: "request timed out")
                is ConnectException -> if (!m.isNullOrEmpty()) "Connection refused: $m" else "Connection refused"
                else -> if (!m.isNullOrEmpty()) m else e.javaClass.simpleName
            }
        }

        /** Percent-encodes characters that are not allowed in a URL path (spaces, umlauts, …). */
        internal fun encodePath(s: String): String {
            val sb = StringBuilder()
            for (b in s.toByteArray(Charsets.UTF_8)) {
                val c = b.toInt() and 0xff
                if (c in 'a'.code..'z'.code || c in 'A'.code..'Z'.code || c in '0'.code..'9'.code ||
                    "-._~:/?@!$&'()*+,;=%".indexOf(c.toChar()) >= 0) sb.append(c.toChar())
                else sb.append('%').append(String.format("%02X", c))
            }
            return sb.toString()
        }

        /** Removes `null` values and converts booleans to "true"/"false", lists to "a,b". */
        internal fun cleanParams(params: Map<String, Any?>?): Map<String, String> {
            val clean = LinkedHashMap<String, String>()
            params?.forEach { (k, v) ->
                when (v) {
                    null -> {}
                    is Collection<*> -> clean[k] = v.joinToString(",")
                    else -> clean[k] = v.toString()
                }
            }
            return clean
        }

        internal fun urlencode(params: Map<String, *>?): String =
            params?.entries?.joinToString("&") {
                URLEncoder.encode(it.key, "UTF-8") + "=" + URLEncoder.encode(it.value.toString(), "UTF-8")
            } ?: ""

        /** Like requests' raise_for_status(), with the error message openHAB sends in the body. */
        internal fun raiseForStatus(status: Int, requestUrl: String, body: String?) {
            if (status < 400) return
            var message = ""
            val text = body?.trim() ?: ""
            try {
                var parsed = Json.parse(text)
                // some endpoints (e.g. the sitemap event stream) send the error object as a JSON string
                if (parsed is String && parsed.trim().startsWith("{")) parsed = Json.parse(parsed)
                if (parsed is Map<*, *>) {
                    val error = parsed["error"]
                    message = if (error is Map<*, *>) error["message"]?.toString() ?: ""
                    else error?.toString() ?: ""
                }
            } catch (e: IllegalArgumentException) {
                message = if (text.length > 200) text.substring(0, 200) else text
            }
            var msg = "$status ${kind(status)}: ${reason(status)} for url: $requestUrl"
            if (message.isNotEmpty()) msg += " ($message)"
            throw OpenHABHTTPError(msg, status, message)
        }
    }
}
