package io.github.michdo93.openhab.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Core HTTP client for the openHAB REST API.
 *
 * Supports Basic Authentication and Bearer Token Authentication.
 * All request methods are **suspend functions** and run on [Dispatchers.IO].
 *
 * ```kotlin
 * // Basic Auth
 * val client = OpenHABClient("http://192.168.1.100:8080", "openhab", "habopen")
 *
 * // Token Auth
 * val client = OpenHABClient("http://192.168.1.100:8080", token = "oh.openhab.xxx")
 *
 * // With logging (for debug builds)
 * val client = OpenHABClient(..., debug = BuildConfig.DEBUG)
 * ```
 */
class OpenHABClient(
    val baseUrl: String,
    val username: String? = null,
    val password: String? = null,
    val token: String?    = null,
    debug: Boolean        = false,
) {

    // ── State ─────────────────────────────────────────────────────────────────
    var isCloud:    Boolean = false; private set
    var isLoggedIn: Boolean = false; private set

    // ── OkHttp ───────────────────────────────────────────────────────────────
    internal val http: OkHttpClient

    init {
        isCloud = baseUrl.trimEnd('/') == "https://myopenhab.org"

        val builder = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10,    TimeUnit.SECONDS)
            .writeTimeout(10,   TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(username, password, token))

        if (debug) {
            builder.addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
        }

        http = builder.build()
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    /** Verifies connectivity. Sets [isLoggedIn] on success. */
    suspend fun login(): Boolean = withContext(Dispatchers.IO) {
        try {
            val resp = http.newCall(
                Request.Builder().url(normalizeUrl("/rest")).build()
            ).execute()
            isLoggedIn = resp.isSuccessful
            resp.close()
        } catch (_: Exception) {
            isLoggedIn = false
        }
        isLoggedIn
    }

    // ── URL builder ───────────────────────────────────────────────────────────
    internal fun normalizeUrl(path: String, params: Map<String, String?> = emptyMap()): String {
        val base  = baseUrl.trimEnd('/')
        val clean = when {
            path.startsWith("/rest") -> path
            path.startsWith("/")     -> "/rest$path"
            else                     -> "/rest/$path"
        }
        val filtered = params.filterValues { !it.isNullOrEmpty() }
        if (filtered.isEmpty()) return "$base$clean"

        val qs = filtered.entries.joinToString("&") { (k, v) ->
            "${k.encodeUrl()}=${v!!.encodeUrl()}"
        }
        return "$base$clean?$qs"
    }

    private fun String.encodeUrl() = java.net.URLEncoder.encode(this, "UTF-8")

    // ── HTTP helpers ──────────────────────────────────────────────────────────

    /** GET — returns raw response body string. */
    suspend fun get(
        path: String,
        params: Map<String, String?> = emptyMap(),
        extraHeaders: Map<String, String> = emptyMap()
    ): String = execute(
        method  = "GET",
        url     = normalizeUrl(path, params),
        headers = extraHeaders
    )

    /** POST — returns raw response body string. */
    suspend fun post(
        path: String,
        body: String? = null,
        contentType: String = "application/json",
        params: Map<String, String?> = emptyMap(),
        extraHeaders: Map<String, String> = emptyMap()
    ): String = execute(
        method      = "POST",
        url         = normalizeUrl(path, params),
        body        = body,
        contentType = contentType,
        headers     = extraHeaders
    )

    /** PUT — returns raw response body string. */
    suspend fun put(
        path: String,
        body: String? = null,
        contentType: String = "application/json",
        params: Map<String, String?> = emptyMap(),
        extraHeaders: Map<String, String> = emptyMap()
    ): String = execute(
        method      = "PUT",
        url         = normalizeUrl(path, params),
        body        = body,
        contentType = contentType,
        headers     = extraHeaders
    )

    /** DELETE — returns raw response body string. */
    suspend fun delete(
        path: String,
        params: Map<String, String?> = emptyMap(),
        extraHeaders: Map<String, String> = emptyMap()
    ): String = execute(
        method  = "DELETE",
        url     = normalizeUrl(path, params),
        headers = extraHeaders
    )

    // ── Core executor ─────────────────────────────────────────────────────────
    private suspend fun execute(
        method: String,
        url: String,
        body: String? = null,
        contentType: String = "application/json",
        headers: Map<String, String> = emptyMap()
    ): String = withContext(Dispatchers.IO) {
        val reqBody = body?.toRequestBody(contentType.toMediaType())

        val req = Request.Builder()
            .url(url)
            .apply {
                when (method) {
                    "GET"    -> get()
                    "POST"   -> post(reqBody ?: "".toRequestBody())
                    "PUT"    -> put(reqBody  ?: "".toRequestBody())
                    "DELETE" -> delete()
                }
                headers.forEach { (k, v) -> addHeader(k, v) }
            }
            .build()

        val resp = try {
            http.newCall(req).execute()
        } catch (e: IOException) {
            throw OpenHABException("Network error: ${e.message}", cause = e)
        }

        val bodyStr = try { resp.body?.string() ?: "" } finally { }

        if (!resp.isSuccessful) {
            throw OpenHABException(
                message    = "HTTP ${resp.code}: $bodyStr",
                statusCode = resp.code
            )
        }
        if (bodyStr.isBlank()) """{"status":${resp.code}}""" else bodyStr
    }

    // ── SSE ───────────────────────────────────────────────────────────────────
    /**
     * Opens a Server-Sent Events stream.
     * Returns an [SseSession] — call [SseSession.collect] (Flow) or
     * [SseSession.cancel] to stop.
     */
    fun sse(url: String): SseSession = SseSession(http, url)
}

// ── Auth Interceptor ──────────────────────────────────────────────────────────

private class AuthInterceptor(
    private val username: String?,
    private val password: String?,
    private val token: String?,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder().apply {
            when {
                !token.isNullOrEmpty() ->
                    addHeader("Authorization", "Bearer $token")
                !username.isNullOrEmpty() && !password.isNullOrEmpty() -> {
                    val cred = okhttp3.Credentials.basic(username, password)
                    addHeader("Authorization", cred)
                }
            }
        }.build()
        return chain.proceed(req)
    }
}
