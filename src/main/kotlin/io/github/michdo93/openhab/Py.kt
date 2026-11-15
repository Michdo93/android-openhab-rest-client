package io.github.michdo93.openhab

import java.net.URLEncoder

/**
 * Runtime helpers for the REST classes generated from python-openhab-rest-client
 * (tools/py2kt.py). They reproduce the Python semantics the generated code relies on
 * (truthiness, `==` between numbers, `str()`, dict access). Not part of the public API.
 */
@Suppress("UNCHECKED_CAST")
internal object Py {

    // ── Values ───────────────────────────────────────────────────────────────

    fun truthy(v: Any?): Boolean = when (v) {
        null -> false
        is Boolean -> v
        is String -> v.isNotEmpty()
        is Number -> v.toDouble() != 0.0
        is Map<*, *> -> v.isNotEmpty()
        is Collection<*> -> v.isNotEmpty()
        else -> true
    }

    /** Python's `a or b`. */
    fun or(a: Any?, b: Any?): Any? = if (truthy(a)) a else b

    /** Python's `a and b`. */
    fun and(a: Any?, b: Any?): Any? = if (truthy(a)) b else a

    fun eq(a: Any?, b: Any?): Boolean {
        if (a === b) return true
        if (a == null || b == null) return false
        if (a is Number && b is Number) return a.toDouble() == b.toDouble()
        return a == b
    }

    fun cmp(a: Any?, b: Any?): Int = (a as Number).toDouble().compareTo((b as Number).toDouble())

    /** Python's `str()`. */
    fun str(v: Any?): String = when (v) {
        null -> "None"
        is Boolean -> if (v) "True" else "False"
        is Map<*, *>, is Collection<*> -> Json.stringify(v)
        is Throwable -> v.message ?: v.javaClass.simpleName
        else -> v.toString()
    }

    fun lower(s: Any?): String = str(s).lowercase()
    fun strip(s: Any?): String = str(s).trim()
    fun rstrip(s: Any?, chars: String): String = str(s).trimEnd { it in chars }
    fun split(s: Any?, sep: String): List<Any?> = str(s).split(sep)
    fun join(sep: String, items: Any?): String = (items as Collection<*>).joinToString(sep) { str(it) }

    fun len(v: Any?): Int = when (v) {
        is String -> v.length
        is Map<*, *> -> v.size
        else -> (v as Collection<*>).size
    }

    // ── Types ────────────────────────────────────────────────────────────────

    fun isDict(v: Any?) = v is Map<*, *>
    fun isList(v: Any?) = v is List<*> || v is Array<*>
    fun isStr(v: Any?) = v is String
    fun isBool(v: Any?) = v is Boolean
    fun isStatus(v: Any?) = v is StatusResponse

    // ── Containers ───────────────────────────────────────────────────────────

    /** Dictionary literal; `null` values are kept (like Python). */
    fun dict(vararg kv: Any?): MutableMap<String, Any?> {
        val m = LinkedHashMap<String, Any?>()
        var i = 0
        while (i + 1 < kv.size) { m[kv[i] as String] = kv[i + 1]; i += 2 }
        return m
    }

    /** Dictionary literal with non-string keys (e.g. HTTP status codes). */
    fun dictAny(vararg kv: Any?): MutableMap<Any?, Any?> {
        val m = LinkedHashMap<Any?, Any?>()
        var i = 0
        while (i + 1 < kv.size) { m[kv[i]] = kv[i + 1]; i += 2 }
        return m
    }

    /** `dict(x)`: a shallow copy. */
    fun copy(m: Any?): MutableMap<String, Any?> {
        val out = LinkedHashMap<String, Any?>()
        (m as Map<*, *>?)?.forEach { (k, v) -> out[k.toString()] = v }
        return out
    }

    /** `{k: v for k, v in m.items() if v is not None}`. */
    fun withoutNone(m: Any?): MutableMap<String, Any?> {
        val out = LinkedHashMap<String, Any?>()
        (m as Map<*, *>).forEach { (k, v) -> if (v != null) out[k.toString()] = v }
        return out
    }

    fun list(vararg values: Any?): MutableList<Any?> = values.toMutableList()

    /** Unchecked cast for maps passed on to the client. */
    fun m(v: Any?): Map<String, Any?>? = v as Map<String, Any?>?

    /** Iterates over a list (or the keys of a map, like Python). */
    fun iter(v: Any?): Iterable<Any?> = when (v) {
        is Map<*, *> -> v.keys
        is Array<*> -> v.asIterable()
        else -> v as Iterable<Any?>
    }

    fun entries(v: Any?): Set<Map.Entry<Any?, Any?>> = (v as Map<Any?, Any?>).entries

    /** `obj[key]`. */
    fun get(obj: Any?, key: Any?): Any? {
        if (obj is Map<*, *>) return obj[key]
        if (obj is List<*> && key is Number) {
            val i = key.toInt()
            return obj[if (i < 0) i + obj.size else i]
        }
        if (obj is String && key is Number) {
            val i = key.toInt()
            return obj[if (i < 0) i + obj.length else i].toString()
        }
        throw IllegalArgumentException("cannot index $obj with $key")
    }

    /** `dict.get(key, default)`. */
    fun getOr(obj: Any?, key: Any?, dflt: Any?): Any? =
        if (obj is Map<*, *> && obj.containsKey(key)) obj[key] else dflt

    fun set(obj: Any?, key: Any?, value: Any?) {
        (obj as MutableMap<Any?, Any?>)[key] = value
    }

    fun contains(container: Any?, x: Any?): Boolean = when (container) {
        is Map<*, *> -> container.containsKey(x)
        is Collection<*> -> container.any { eq(it, x) }
        is String -> container.contains(str(x))
        else -> false
    }

    // ── URLs and JSON ────────────────────────────────────────────────────────

    private fun enc(s: String) = URLEncoder.encode(s, "UTF-8")

    /** `urllib.parse.quote(s)` with `safe="/"`. */
    fun quote(s: Any?): String =
        enc(str(s)).replace("+", "%20").replace("%2F", "/").replace("%7E", "~")

    /** `urllib.parse.quote(s, safe="")`. */
    fun quoteAll(s: Any?): String = enc(str(s)).replace("+", "%20").replace("%7E", "~")

    fun urlencode(params: Any?): String =
        (params as Map<String, Any?>).entries.joinToString("&") { enc(it.key) + "=" + enc(str(it.value)) }

    /** `json.dumps(v)`. */
    fun dumps(v: Any?): String = Json.stringify(v)

    // ── Event streams ────────────────────────────────────────────────────────

    /** Opens an SSE stream and translates errors into an error object (Events.py: _openStream). */
    fun openStream(client: OpenHABClient, topics: Any?, path: Any?, headers: Any?, errors: Any?): SSEConnection {
        val errs = (errors as Map<*, *>?) ?: emptyMap<Any?, Any?>()
        return try {
            client.executeSSE(if (path == null) "/rest/events" else str(path), m(headers),
                if (truthy(topics)) dict("topics", topics) else null)
        } catch (err: OpenHABHTTPError) {
            for ((k, v) in errs) {
                if (eq(k, err.status)) return SSEConnection.failed(dict("error", v))
            }
            SSEConnection.failed(dict("error", "HTTP error ${err.status}: ${err.message}"))
        } catch (err: OpenHABRequestException) {
            SSEConnection.failed(dict("error", "Request error: ${err.message}"))
        }
    }

    /** Wraps a result that should be a stream; anything else (an error object) becomes a failed stream. */
    fun stream(v: Any?): SSEConnection = when (v) {
        is SSEConnection -> v
        is Map<*, *> -> SSEConnection.failed(v as Map<String, Any?>)
        else -> SSEConnection.failed(dict("error", "Unexpected response: ${str(v)}"))
    }
}
