package io.github.michdo93.openhab

import java.math.BigDecimal
import java.math.BigInteger

/**
 * Minimal JSON support without external dependencies.
 *
 * The library returns JSON exactly as the Python reference does: objects become
 * `Map<String, Any?>` (insertion order kept), arrays become `List<Any?>`, numbers become
 * [Long], [Double] or [BigDecimal], and `null` stays `null`.
 *
 * ```kotlin
 * val answer = items.getItem("Wohnzimmer_Licht")
 * if (Json.isError(answer)) println(Json.error(answer))
 * else println(Json.map(answer)["state"])
 * ```
 */
object Json {

    // ── Results of the library ───────────────────────────────────────────────

    /** `true` if a result of this library is an error object `{"error": ...}`. */
    @JvmStatic
    fun isError(result: Any?): Boolean =
        result is Map<*, *> && result.containsKey("error") && result !is StatusResponse

    /** The error text of an error object, otherwise `null`. */
    @JvmStatic
    fun error(result: Any?): String? {
        if (!isError(result)) return null
        val e = (result as Map<*, *>)["error"]
        if (e is Map<*, *> && e["message"] != null) return e["message"].toString()
        return e.toString()
    }

    /** Casts a result to a JSON object; throws [IllegalStateException] with the error text. */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun map(result: Any?): Map<String, Any?> {
        if (isError(result)) throw IllegalStateException(error(result))
        if (result !is Map<*, *>) throw IllegalStateException("Not a JSON object: " + abbreviate(result))
        return result as Map<String, Any?>
    }

    /** Casts a result to a JSON array of objects (items, things, rules, …). */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun list(result: Any?): List<Map<String, Any?>> {
        if (isError(result)) throw IllegalStateException(error(result))
        if (result !is List<*>) throw IllegalStateException("Not a JSON array: " + abbreviate(result))
        return result as List<Map<String, Any?>>
    }

    /** Casts a result to a JSON array of any elements. */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun array(result: Any?): List<Any?> {
        if (isError(result)) throw IllegalStateException(error(result))
        if (result !is List<*>) throw IllegalStateException("Not a JSON array: " + abbreviate(result))
        return result
    }

    /**
     * Follows a path of keys and indices, e.g. `Json.get(thing, "statusInfo", "status")`
     * or `Json.get(data, "data", 0, "state")`. Returns `null` if a step is missing.
     */
    @JvmStatic
    fun get(value: Any?, vararg path: Any): Any? {
        var cur = value
        for (step in path) {
            cur = when {
                cur is Map<*, *> && step is String -> cur[step]
                cur is List<*> && step is Int -> {
                    val i = if (step < 0) step + cur.size else step
                    if (i >= 0 && i < cur.size) cur[i] else null
                }
                else -> return null
            }
        }
        return cur
    }

    /** Like [get], but as text (`null` stays `null`). */
    @JvmStatic
    fun str(value: Any?, vararg path: Any): String? = get(value, *path)?.toString()

    private fun abbreviate(o: Any?): String {
        val s = o.toString()
        return if (s.length > 120) s.substring(0, 120) + "…" else s
    }

    // ── Writing ──────────────────────────────────────────────────────────────

    /** Serializes maps, lists, arrays, strings, numbers, booleans and `null` to JSON. */
    @JvmStatic
    @JvmOverloads
    fun stringify(value: Any?, indent: Int = 0): String {
        val sb = StringBuilder()
        write(sb, value, if (indent > 0) " ".repeat(indent) else null, 0)
        return sb.toString()
    }

    private fun newline(sb: StringBuilder, indent: String?, level: Int) {
        if (indent != null) {
            sb.append('\n')
            repeat(level) { sb.append(indent) }
        }
    }

    private fun write(sb: StringBuilder, v: Any?, indent: String?, level: Int) {
        when (v) {
            null -> sb.append("null")
            is String -> quote(sb, v)
            is Boolean -> sb.append(v)
            is Double, is Float -> {
                val d = (v as Number).toDouble()
                if (d.isNaN() || d.isInfinite()) sb.append("null")
                else if (d == Math.floor(d) && Math.abs(d) < 1e15) sb.append(d.toLong()).append(".0")
                else sb.append(d)
            }
            is Number -> sb.append(v)
            is Map<*, *> -> {
                if (v.isEmpty()) { sb.append("{}"); return }
                sb.append('{')
                var first = true
                for ((k, value) in v) {
                    if (!first) sb.append(',')
                    first = false
                    newline(sb, indent, level + 1)
                    quote(sb, k.toString())
                    sb.append(if (indent != null) ": " else ":")
                    write(sb, value, indent, level + 1)
                }
                newline(sb, indent, level)
                sb.append('}')
            }
            is Iterable<*> -> writeList(sb, v.toList(), indent, level)
            is Array<*> -> writeList(sb, v.toList(), indent, level)
            is Char -> quote(sb, v.toString())
            else -> quote(sb, v.toString())
        }
    }

    private fun writeList(sb: StringBuilder, l: List<*>, indent: String?, level: Int) {
        if (l.isEmpty()) { sb.append("[]"); return }
        sb.append('[')
        for ((i, e) in l.withIndex()) {
            if (i > 0) sb.append(',')
            newline(sb, indent, level + 1)
            write(sb, e, indent, level + 1)
        }
        newline(sb, indent, level)
        sb.append(']')
    }

    private fun quote(sb: StringBuilder, s: String) {
        sb.append('"')
        for (c in s) {
            when (c) {
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                '\b' -> sb.append("\\b")
                '\u000c' -> sb.append("\\f")
                else -> if (c < ' ') sb.append(String.format("\\u%04x", c.code)) else sb.append(c)
            }
        }
        sb.append('"')
    }

    // ── Reading ──────────────────────────────────────────────────────────────

    /** Parses JSON text; throws [IllegalArgumentException] if it is not valid JSON. */
    @JvmStatic
    fun parse(text: String): Any? {
        val p = Parser(text)
        p.ws()
        val v = p.value()
        p.ws()
        if (p.i != text.length) throw p.fail("unexpected data after JSON value")
        return v
    }

    private class Parser(val s: String) {
        var i = 0

        fun fail(what: String) = IllegalArgumentException("Invalid JSON at position $i: $what")

        fun ws() {
            while (i < s.length && (s[i] == ' ' || s[i] == '\n' || s[i] == '\r' || s[i] == '\t')) i++
        }

        fun value(): Any? {
            if (i >= s.length) throw fail("unexpected end")
            return when (val c = s[i]) {
                '{' -> obj()
                '[' -> arr()
                '"' -> string()
                't' -> literal("true", true)
                'f' -> literal("false", false)
                'n' -> literal("null", null)
                else -> if (c == '-' || c in '0'..'9') number() else throw fail("unexpected character '$c'")
            }
        }

        fun literal(word: String, v: Any?): Any? {
            if (!s.startsWith(word, i)) throw fail("expected $word")
            i += word.length
            return v
        }

        fun obj(): Map<String, Any?> {
            val m = LinkedHashMap<String, Any?>()
            i++
            ws()
            if (i < s.length && s[i] == '}') { i++; return m }
            while (true) {
                ws()
                if (i >= s.length || s[i] != '"') throw fail("expected key")
                val k = string()
                ws()
                if (i >= s.length || s[i] != ':') throw fail("expected ':'")
                i++
                ws()
                m[k] = value()
                ws()
                if (i >= s.length) throw fail("unexpected end")
                when (s[i++]) {
                    '}' -> return m
                    ',' -> {}
                    else -> throw fail("expected ',' or '}'")
                }
            }
        }

        fun arr(): List<Any?> {
            val l = ArrayList<Any?>()
            i++
            ws()
            if (i < s.length && s[i] == ']') { i++; return l }
            while (true) {
                ws()
                l.add(value())
                ws()
                if (i >= s.length) throw fail("unexpected end")
                when (s[i++]) {
                    ']' -> return l
                    ',' -> {}
                    else -> throw fail("expected ',' or ']'")
                }
            }
        }

        fun string(): String {
            val sb = StringBuilder()
            i++
            while (true) {
                if (i >= s.length) throw fail("unterminated string")
                val c = s[i++]
                if (c == '"') return sb.toString()
                if (c != '\\') { sb.append(c); continue }
                if (i >= s.length) throw fail("unterminated escape")
                when (val e = s[i++]) {
                    '"' -> sb.append('"')
                    '\\' -> sb.append('\\')
                    '/' -> sb.append('/')
                    'b' -> sb.append('\b')
                    'f' -> sb.append('\u000c')
                    'n' -> sb.append('\n')
                    'r' -> sb.append('\r')
                    't' -> sb.append('\t')
                    'u' -> {
                        if (i + 4 > s.length) throw fail("bad unicode escape")
                        sb.append(s.substring(i, i + 4).toInt(16).toChar())
                        i += 4
                    }
                    else -> throw fail("bad escape \\$e")
                }
            }
        }

        fun number(): Any {
            val start = i
            if (s[i] == '-') i++
            var decimal = false
            while (i < s.length) {
                val c = s[i]
                if (c in '0'..'9') i++
                else if (c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-') { decimal = true; i++ }
                else break
            }
            val t = s.substring(start, i)
            try {
                if (!decimal) {
                    val b = BigInteger(t)
                    return if (b.bitLength() < 64) b.toLong() else BigDecimal(b)
                }
                val d = t.toDouble()
                return if (d.isInfinite()) BigDecimal(t) else d
            } catch (e: NumberFormatException) {
                throw fail("bad number $t")
            }
        }
    }
}
