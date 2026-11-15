package io.github.michdo93.openhab

/**
 * Marker for responses without a body (e.g. `200 OK` with an empty body, `201 Created`,
 * `202 Accepted`).
 *
 * It is a plain map with the keys `status` (HTTP status code) and, if the server sent one,
 * `location`. Because it is its own type, it can never be confused with a JSON response that
 * happens to contain a `status` field (e.g. a thing status or a rule).
 */
class StatusResponse(status: Int) : LinkedHashMap<String, Any?>() {
    init {
        put("status", status)
    }

    /** The HTTP status code. */
    val status: Int get() = get("status") as Int
}
