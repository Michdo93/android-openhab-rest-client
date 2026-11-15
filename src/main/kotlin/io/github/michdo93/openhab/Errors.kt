package io.github.michdo93.openhab

/**
 * Thrown by [OpenHABClient.get], [OpenHABClient.post], [OpenHABClient.put],
 * [OpenHABClient.delete] and [OpenHABClient.executeSSE] if no answer arrives
 * (connection refused, timeout, invalid URL).
 *
 * The REST classes of this library catch it and return `{"error": "..."}` instead,
 * exactly like python-openhab-rest-client. You only see it when calling the client directly.
 */
open class OpenHABRequestException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)

/**
 * Thrown by the low-level client methods for 4xx/5xx responses. Like requests' HTTPError it
 * is a subclass of [OpenHABRequestException].
 *
 * @property status HTTP status code
 * @property openhabMessage the error message openHAB sent in the body, or an empty string
 */
class OpenHABHTTPError(message: String, val status: Int, openhabMessage: String?) :
    OpenHABRequestException(message, null) {
    val openhabMessage: String = openhabMessage ?: ""
}
