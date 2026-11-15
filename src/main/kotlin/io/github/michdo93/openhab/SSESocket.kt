package io.github.michdo93.openhab

import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URI
import javax.net.ssl.SSLSocketFactory

/**
 * HTTP/1.1 GET over a plain socket, used for event streams only.
 *
 * `HttpURLConnection` cannot be interrupted while it waits for data: `disconnect()` and
 * `close()` block until the server sends the next chunk (openHAB sends ALIVE every ~10 s).
 * Closing a socket, in contrast, ends a waiting read at once – so cancelling a coroutine that
 * collects [SSEConnection.events] really closes the connection immediately.
 */
internal class SSESocket private constructor(
    private val socket: Socket,
    val status: Int,
    val headers: Map<String, String>,
    raw: InputStream,
) : AutoCloseable {

    /** The response body (chunked transfer decoded). */
    val body: InputStream = when {
        headers["transfer-encoding"]?.contains("chunked", ignoreCase = true) == true -> ChunkedInput(raw)
        headers["content-length"] != null -> LimitedInput(raw, headers["content-length"]!!.trim().toLong())
        else -> raw
    }

    override fun close() {
        try {
            socket.close()
        } catch (ignored: IOException) {
        }
    }

    companion object {
        fun get(target: String, requestHeaders: Map<String, String>, connectTimeoutMs: Int): SSESocket {
            val uri = URI(target)
            val https = uri.scheme.equals("https", ignoreCase = true)
            val host = uri.host ?: throw IOException("Invalid URL $target")
            val port = if (uri.port > 0) uri.port else if (https) 443 else 80
            val plain = Socket()
            try {
                plain.connect(InetSocketAddress(host, port), connectTimeoutMs)
                val socket = if (https) {
                    (SSLSocketFactory.getDefault() as SSLSocketFactory)
                        .createSocket(plain, host, port, true)
                        .also { (it as javax.net.ssl.SSLSocket).startHandshake() }
                } else plain
                // until the response headers are there, the normal timeout applies; then none
                socket.soTimeout = connectTimeoutMs
                val path = (uri.rawPath ?: "/").ifEmpty { "/" } + (uri.rawQuery?.let { "?$it" } ?: "")
                val hostHeader = if (uri.port > 0) "$host:$port" else host
                val req = StringBuilder("GET $path HTTP/1.1\r\nHost: $hostHeader\r\n")
                requestHeaders.forEach { (k, v) -> req.append(k).append(": ").append(v).append("\r\n") }
                req.append("Connection: close\r\n\r\n")
                socket.getOutputStream().apply { write(req.toString().toByteArray(Charsets.UTF_8)); flush() }
                val input = BufferedInputStream(socket.getInputStream())
                val statusLine = readLine(input) ?: throw IOException("No response from $host")
                val status = statusLine.split(" ").getOrNull(1)?.toIntOrNull()
                    ?: throw IOException("Invalid response: $statusLine")
                val headers = LinkedHashMap<String, String>()
                while (true) {
                    val l = readLine(input) ?: break
                    if (l.isEmpty()) break
                    val i = l.indexOf(':')
                    if (i > 0) headers[l.substring(0, i).trim().lowercase()] = l.substring(i + 1).trim()
                }
                socket.soTimeout = 0
                return SSESocket(socket, status, headers, input)
            } catch (e: IOException) {
                try {
                    plain.close()
                } catch (ignored: IOException) {
                }
                throw e
            }
        }

        internal fun readLine(input: InputStream): String? {
            val buf = ByteArrayOutputStream()
            while (true) {
                val b = input.read()
                if (b < 0) return if (buf.size() > 0) buf.toString("UTF-8") else null
                if (b == '\n'.code) {
                    val s = buf.toString("UTF-8")
                    return if (s.endsWith("\r")) s.dropLast(1) else s
                }
                buf.write(b)
            }
        }
    }

    /** Decodes `Transfer-Encoding: chunked`. */
    private class ChunkedInput(private val input: InputStream) : InputStream() {
        private var left = 0L
        private var done = false

        override fun read(): Int {
            if (done) return -1
            if (left == 0L) {
                var line = readLine(input) ?: return end()
                if (line.isEmpty()) line = readLine(input) ?: return end()   // CRLF after a chunk
                left = line.substringBefore(';').trim().toLongOrNull(16) ?: return end()
                if (left == 0L) return end()
            }
            val b = input.read()
            if (b < 0) return end()
            left--
            return b
        }

        private fun end(): Int {
            done = true
            return -1
        }
    }

    private class LimitedInput(private val input: InputStream, private var left: Long) : InputStream() {
        override fun read(): Int {
            if (left <= 0) return -1
            val b = input.read()
            if (b >= 0) left--
            return b
        }
    }
}
