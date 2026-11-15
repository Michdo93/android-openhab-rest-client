package io.github.michdo93.openhab

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * An open Server-Sent Events stream – what the event methods ([ItemEvents], [ThingEvents],
 * [Events], [Sitemaps.getSitemapEvents], …) return.
 *
 * If the stream could not be opened, [error] holds the error object (`{"error": "..."}`) and
 * the stream yields nothing – the same information the Python library returns instead of the
 * stream.
 *
 * ```kotlin
 * val stream = ItemEvents(client).ItemStateChangedEvent()
 * if (stream.isError) { println(Json.error(stream.error)); return }
 * stream.events().collect { event ->                 // a Flow, e.g. in lifecycleScope
 *     println("${event["topic"]} ${Json.get(event, "payload", "value")}")
 * }
 * ```
 *
 * Cancelling the coroutine that collects the flow closes the connection. [close] ends the
 * stream from anywhere; a blocking `for` loop over the stream (JVM programs) then returns.
 */
class SSEConnection internal constructor(
    private val connection: AutoCloseable?,
    private val input: InputStream,
    /** The URL of the stream (`null` for a stream that could not be opened). */
    val url: String?,
    /** The error object if the stream could not be opened, otherwise `null`. */
    val error: Map<String, Any?>? = null,
) : AutoCloseable, Iterable<String> {

    @Volatile
    private var closed = error != null

    /** `true` if the stream could not be opened. */
    val isError: Boolean get() = error != null

    /** `true` while the stream is open. */
    val isOpen: Boolean get() = !closed

    /** Closes the stream; reading ends (also when called from another thread). */
    override fun close() {
        closed = true
        // closes the socket: a read waiting for the next event ends at once
        try {
            connection?.close()
            input.close()
        } catch (ignored: Exception) {
        }
    }

    /** The raw lines of the stream (blocking), each one as soon as it is complete. */
    override fun iterator(): Iterator<String> = object : Iterator<String> {
        private var next: String? = null
        private var done = false

        override fun hasNext(): Boolean {
            if (next != null) return true
            if (done) return false
            next = readLine()
            if (next == null) done = true
            return next != null
        }

        override fun next(): String {
            if (!hasNext()) throw NoSuchElementException()
            val l = next!!
            next = null
            return l
        }
    }

    /**
     * The raw lines (`data: …`, `event: …`, empty lines) as a [Flow]. Reading runs on
     * [Dispatchers.IO]; cancelling the collector closes the stream.
     */
    fun lines(): Flow<String> = flow {
        coroutineScope {
            // cancelling the collector closes the socket, which ends a read that is waiting
            val closer = launch(start = CoroutineStart.UNDISPATCHED) {
                try {
                    awaitCancellation()
                } finally {
                    close()
                }
            }
            try {
                while (true) {
                    val line = withContext(Dispatchers.IO) { readLine() } ?: break
                    emit(line)
                }
            } finally {
                close()
                closer.cancel()
            }
        }
    }

    /** The events as maps with decoded payload; ALIVE messages are skipped unless [includeAlive]. */
    @JvmOverloads
    fun events(includeAlive: Boolean = false): Flow<Map<String, Any?>> =
        lines().mapNotNull { line ->
            SSE.parseSSEData(line)?.takeIf { it.containsKey("topic") || includeAlive }
        }

    /** The events as a blocking sequence (for JVM programs and tests). */
    @JvmOverloads
    fun eventSequence(includeAlive: Boolean = false): Sequence<Map<String, Any?>> =
        asSequence().mapNotNull { line ->
            SSE.parseSSEData(line)?.takeIf { it.containsKey("topic") || includeAlive }
        }

    override fun toString(): String = if (error != null) "SSEConnection$error" else "SSEConnection[$url]"

    /** Reads byte by byte up to '\n', so that every line is handed out as soon as it is complete. */
    private fun readLine(): String? {
        val buf = ByteArrayOutputStream()
        try {
            while (true) {
                val b = input.read()
                if (b < 0) return if (buf.size() > 0) buf.toString("UTF-8") else null
                if (b == '\n'.code) {
                    val s = buf.toString("UTF-8")
                    return if (s.endsWith("\r")) s.dropLast(1) else s
                }
                buf.write(b)
            }
        } catch (e: IOException) {
            // closed from another thread or connection lost: the stream simply ends
            closed = true
            return null
        }
    }

    companion object {
        /** A stream that could not be opened. */
        internal fun failed(error: Map<String, Any?>): SSEConnection =
            SSEConnection(null, ByteArrayInputStreamEmpty, null, error)

        private val ByteArrayInputStreamEmpty: InputStream get() = java.io.ByteArrayInputStream(ByteArray(0))
    }
}
