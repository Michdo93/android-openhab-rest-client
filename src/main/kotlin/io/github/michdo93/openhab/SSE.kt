package io.github.michdo93.openhab

import kotlinx.coroutines.flow.Flow

/**
 * Helpers for reading openHAB's Server-Sent Events streams – the counterparts of
 * `iterSSELines`, `parseSSEData` and `iterSSEEvents` in python-openhab-rest-client.
 */
object SSE {

    /** The lines of a stream as a [Flow], each one as soon as it has arrived. */
    @JvmStatic
    fun iterSSELines(stream: SSEConnection): Flow<String> = stream.lines()

    /**
     * Decodes one `data:` line of the openHAB event stream.
     *
     * @return the event as map with the payload already decoded (JSON in JSON), the raw map for
     *   control messages such as `{"type": "ALIVE"}`, or `null` for all other lines
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun parseSSEData(line: String?): MutableMap<String, Any?>? {
        if (line == null || !line.startsWith("data:")) return null
        val event = try {
            Json.parse(line.substring(5).trim())
        } catch (e: IllegalArgumentException) {
            return null
        }
        if (event !is Map<*, *>) return null
        val m = event as MutableMap<String, Any?>
        val payload = m["payload"]
        if (payload is String) {
            try {
                m["payload"] = Json.parse(payload)
            } catch (ignored: IllegalArgumentException) {
                // plain text payload stays a string
            }
        }
        return m
    }

    /** The events of a stream as maps with decoded payload ([includeAlive]: also ALIVE messages). */
    @JvmStatic
    @JvmOverloads
    fun iterSSEEvents(stream: SSEConnection, includeAlive: Boolean = false): Flow<Map<String, Any?>> =
        stream.events(includeAlive)
}
