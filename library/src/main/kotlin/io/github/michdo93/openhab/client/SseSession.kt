package io.github.michdo93.openhab.client

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*
import okhttp3.sse.*

/**
 * Wraps an SSE connection from openHAB as a Kotlin [Flow].
 *
 * Usage:
 * ```kotlin
 * val sse = itemEvents.itemStateChangedEvent("testSwitch")
 * sse.collect { data ->
 *     println(data)          // raw JSON string after "data: "
 * }
 *
 * // With cancellation:
 * val job = lifecycleScope.launch {
 *     sse.collect { ... }
 * }
 * job.cancel()   // closes the SSE stream automatically
 * ```
 */
class SseSession(
    private val http: OkHttpClient,
    private val url:  String,
) {

    /** Returns a cold [Flow] that emits each SSE `data` payload. */
    fun collect(): Flow<String> = callbackFlow {
        val factory  = EventSources.createFactory(http)
        val request  = Request.Builder()
            .url(url)
            .addHeader("Accept",        "text/event-stream")
            .addHeader("Cache-Control", "no-cache")
            .build()

        val source = factory.newEventSource(request, object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String,
            ) {
                trySend(data)
            }

            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?,
            ) {
                val err = t?.message ?: "SSE failure (HTTP ${response?.code})"
                close(OpenHABException(err, response?.code ?: -1, t))
            }

            override fun onClosed(eventSource: EventSource) {
                close()
            }
        })

        awaitClose { source.cancel() }
    }
}
