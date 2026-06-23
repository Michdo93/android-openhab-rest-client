package io.github.michdo93.openhab.client

/**
 * Exception thrown when an openHAB REST request fails.
 *
 * @property statusCode HTTP status code, or -1 if not applicable.
 */
class OpenHABException(
    message: String,
    val statusCode: Int = -1,
    cause: Throwable?   = null,
) : Exception(message, cause)
