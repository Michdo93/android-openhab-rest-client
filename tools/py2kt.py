"""
Generates the REST classes of android-openhab-rest-client from python-openhab-rest-client.

The Python library is the reference: every method of a REST class is translated
statement by statement (same endpoint, headers, query parameters, body, status
handling and messages). Only a small, regular subset of Python is supported;
anything else raises an error so that nothing is translated silently wrong.

Kotlin has default and named arguments, so every method keeps the Python signature
(same names, same order, same default values). Public methods are `suspend` functions
that run the request on Dispatchers.IO – safe to call from the main thread of an app.

Usage: python3 py2kt.py <path to python-openhab-rest-client/openhab> <output directory>
       python3 py2kt.py --readme <path>   (method tables for README.md)
"""
import ast
import glob
import json
import os
import re
import sys

PKG = "io.github.michdo93.openhab"
SKIP_FILES = ("Async", "__", "Client", "SSE")

ANN = {"str": "String", "bool": "Boolean", "int": "Int", "float": "Double",
       "dict": "Map<String, Any?>", "list": "List<*>"}
NAME_TYPES = {"language": "String", "prefix": "String", "typeFilter": "String",
              "moduleTypeUID": "String", "channelTypeUID": "String", "itemType": "String",
              "summary": "Boolean", "staticDataOnly": "Boolean"}
OPEN_STREAM_PARAMS = ["client", "topics", "path", "headers", "errors"]
KEYWORDS = {"as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in",
            "interface", "is", "null", "object", "package", "return", "super", "this", "throw",
            "true", "try", "typealias", "typeof", "val", "var", "when", "while"}


class Unsupported(Exception):
    pass


def kid(name):
    return f"`{name}`" if name in KEYWORDS else name


def kstr(s):
    return json.dumps(s, ensure_ascii=False).replace("$", "\\$")


def kt_type(arg, default):
    if arg.annotation is not None:
        a = ast.unparse(arg.annotation)
        if a not in ANN:
            raise Unsupported(f"annotation {a}")
        return ANN[a]
    if arg.arg in NAME_TYPES:
        return NAME_TYPES[arg.arg]
    if isinstance(default, ast.Constant):
        v = default.value
        if isinstance(v, bool):
            return "Boolean"
        if isinstance(v, str):
            return "String"
        if isinstance(v, int):
            return "Int"
    return "Any"


class Method:
    def __init__(self, cls, fn):
        self.cls, self.fn, self.name = cls, fn, fn.name
        a = fn.args
        args = a.args[1:]
        defaults = [None] * (len(args) - len(a.defaults)) + list(a.defaults)
        self.params = []  # (name, kotlinType, defaultNode or None, required)
        for arg, d in zip(args, defaults):
            self.params.append((arg.arg, kt_type(arg, d), d, d is None))
        self.private = fn.name.startswith("_")
        self.stream = False

    @property
    def impl(self):
        """Name of the blocking implementation."""
        return self.name if self.private else self.name + "Blocking"


def ptype(t, required):
    return t + "?" if (not required or t == "Any") and not t.endswith("?") else t


def default_literal(node, kt):
    if node is None or (isinstance(node, ast.Constant) and node.value is None):
        return "null"
    if isinstance(node, ast.Constant):
        v = node.value
        if isinstance(v, bool):
            return "true" if v else "false"
        if isinstance(v, str):
            return kstr(v)
        if isinstance(v, int):
            return f"{v}.0" if kt == "Double" else str(v)
        if isinstance(v, float):
            return repr(v)
    raise Unsupported(f"default {ast.unparse(node)}")


def always_returns(stmts):
    for s in stmts:
        if isinstance(s, (ast.Return, ast.Raise)):
            return True
        if isinstance(s, ast.If) and s.orelse and always_returns(s.body) and always_returns(s.orelse):
            return True
        if isinstance(s, ast.Try) and always_returns(s.body) and \
                all(always_returns(h.body) for h in s.handlers):
            return True
    return False


class Translator:
    def __init__(self, method, methods):
        self.m, self.methods = method, methods
        self.where = f"{method.cls}.{method.name}"
        self.params = {p[0]: (p[1], p[3]) for p in method.params}
        self.renamed = {}

    def fail(self, what):
        raise Unsupported(f"{self.where}: {what}")

    # Expressions are Kotlin code of static type Any? (kinds: obj, str = non-null String,
    # bool = Boolean, int = Int, stream = SSEConnection)
    def cond(self, n):
        code, kind = self.expr(n)
        return code if kind == "bool" else f"Py.truthy({code})"

    def obj(self, n):
        return self.expr(n)[0]

    def string(self, n):
        code, kind = self.expr(n)
        return code if kind == "str" else f"Py.str({code})"

    def expr(self, n):
        m = getattr(self, "e_" + type(n).__name__, None)
        if not m:
            self.fail(f"expression {ast.unparse(n)}")
        return m(n)

    def e_Constant(self, n):
        v = n.value
        if v is None:
            return "null", "obj"
        if v is True:
            return "true", "bool"
        if v is False:
            return "false", "bool"
        if isinstance(v, str):
            return kstr(v), "str"
        if isinstance(v, int):
            return str(v), "int"
        return repr(v), "obj"

    def e_Name(self, n):
        if n.id in self.renamed:
            return self.renamed[n.id], "obj"
        if n.id in self.params:
            t, req = self.params[n.id]
            kind = "obj"
            if req and t == "String":
                kind = "str"
            return kid(n.id), kind
        return kid(n.id), "obj"

    def e_JoinedStr(self, n):
        parts = []
        for v in n.values:
            if isinstance(v, ast.Constant):
                parts.append(kstr(v.value))
            elif isinstance(v, ast.FormattedValue):
                if v.format_spec is not None or v.conversion != -1:
                    self.fail("format spec")
                parts.append(f"Py.str({self.obj(v.value)})")
        if not parts:
            return '""', "str"
        if not parts[0].startswith('"'):
            parts.insert(0, '""')
        return "(" + " + ".join(parts) + ")", "str"

    def e_Dict(self, n):
        parts = []
        anykey = False
        for k, v in zip(n.keys, n.values):
            if not (isinstance(k, ast.Constant) and isinstance(k.value, (str, int))):
                self.fail(f"dict key {ast.unparse(k) if k else '**'}")
            anykey |= not isinstance(k.value, str)
            parts += [kstr(k.value) if isinstance(k.value, str) else str(k.value), self.obj(v)]
        return f"Py.{'dictAny' if anykey else 'dict'}({', '.join(parts)})", "obj"

    def e_List(self, n):
        return f"Py.list({', '.join(self.obj(e) for e in n.elts)})", "obj"

    e_Tuple = e_List

    def e_Subscript(self, n):
        if isinstance(n.slice, ast.Slice):
            self.fail("slice")
        return f"Py.get({self.obj(n.value)}, {self.obj(n.slice)})", "obj"

    def e_Attribute(self, n):
        src = ast.unparse(n)
        if src == "err.response.status_code":
            return "err.status", "int"
        if src == "self.client":
            return "client", "obj"
        self.fail(f"attribute {src}")

    def e_BoolOp(self, n):
        fn = "Py.and" if isinstance(n.op, ast.And) else "Py.or"
        vals = [self.obj(v) for v in n.values]
        out = vals[-1]
        for v in reversed(vals[:-1]):
            out = f"{fn}({v}, {out})"
        return out, "obj"

    def e_UnaryOp(self, n):
        if isinstance(n.op, ast.Not):
            return f"!{self.cond(n.operand)}", "bool"
        if isinstance(n.op, ast.USub) and isinstance(n.operand, ast.Constant):
            return f"-{n.operand.value}", "int"
        self.fail(ast.unparse(n))

    def e_IfExp(self, n):
        return f"(if ({self.cond(n.test)}) {self.obj(n.body)} else {self.obj(n.orelse)})", "obj"

    def e_Compare(self, n):
        if len(n.ops) != 1:
            self.fail(ast.unparse(n))
        op, L, R = n.ops[0], n.left, n.comparators[0]
        lc, rc = self.obj(L), self.obj(R)
        if isinstance(op, ast.Is):
            return f"({lc} === {rc})", "bool"
        if isinstance(op, ast.IsNot):
            return f"({lc} !== {rc})", "bool"
        if isinstance(op, ast.Eq):
            return f"Py.eq({lc}, {rc})", "bool"
        if isinstance(op, ast.NotEq):
            return f"!Py.eq({lc}, {rc})", "bool"
        if isinstance(op, ast.In):
            return f"Py.contains({rc}, {lc})", "bool"
        if isinstance(op, ast.NotIn):
            return f"!Py.contains({rc}, {lc})", "bool"
        sym = {ast.Lt: "<", ast.Gt: ">", ast.LtE: "<=", ast.GtE: ">="}[type(op)]
        return f"(Py.cmp({lc}, {rc}) {sym} 0)", "bool"

    def e_DictComp(self, n):
        src = ast.unparse(n)
        gen = n.generators[0]
        if (len(n.generators) == 1 and ast.unparse(gen.iter).endswith(".items()")
                and len(gen.ifs) == 1 and ast.unparse(n.key) == ast.unparse(gen.target.elts[0])
                and ast.unparse(n.value) == ast.unparse(gen.target.elts[1])
                and ast.unparse(gen.ifs[0]) == f"{gen.target.elts[1].id} is not None"):
            return f"Py.withoutNone({self.obj(gen.iter.func.value)})", "obj"
        self.fail(f"dict comprehension {src}")

    def call_args(self, target, n):
        names = [p[0] for p in target]
        vals = [None] * len(names)
        for i, a in enumerate(n.args):
            vals[i] = a
        for k in n.keywords:
            if k.arg not in names:
                self.fail(f"keyword {k.arg}")
            vals[names.index(k.arg)] = k.value
        return vals

    def convert(self, code, kind, kt, required):
        """Converts an expression of the given kind to a parameter of type kt."""
        if code == "null" or kt == "Any":
            return code
        if (kt, kind) in (("String", "str"), ("Boolean", "bool"), ("Int", "int")):
            return code
        if kt == "Double" and kind == "int":
            return f"{code}.toDouble()"
        if kt.startswith("Map"):
            return f"Py.m({code})" + ("!!" if required else "")
        return f"({code} as {ptype(kt, required)})"

    def e_Call(self, n):
        f = n.func
        src = ast.unparse(f)
        if isinstance(f, ast.Attribute) and ast.unparse(f.value) == "self.client":
            verb = f.attr
            kw = {k.arg: k.value for k in n.keywords}
            path = n.args[0] if n.args else kw.pop("endpoint")
            pc = self.string(path)
            h = f"Py.m({self.obj(kw['headers'])})" if "headers" in kw else "null"
            prm = f"Py.m({self.obj(kw['params'])})" if "params" in kw else "null"
            data = self.obj(kw["data"]) if "data" in kw else "null"
            if verb == "get":
                return f"client.get({pc}, {h}, {prm})", "obj"
            if verb in ("post", "put", "delete"):
                return f"client.{verb}({pc}, {h}, {data}, {prm})", "obj"
            if verb == "executeSSE":
                return f"client.executeSSE({pc}, {h}, {prm})", "stream"
            self.fail(f"client.{verb}")
        if src == "client.executeSSE":
            self.fail("client.executeSSE outside a class")
        if isinstance(f, ast.Attribute) and isinstance(f.value, ast.Name) and f.value.id == "self":
            target = self.methods.get(f.attr)
            if target is None:
                self.fail(f"unknown method self.{f.attr}")
            vals = self.call_args(target.params, n)
            args = []
            for (pname, kt, d, req), v in zip(target.params, vals):
                if v is None:
                    if req:
                        self.fail(f"missing argument {pname}")
                    continue
                code, kind = self.expr(v)
                args.append(f"{kid(pname)} = {self.convert(code, kind, kt, req)}")
            kind = "stream" if target.stream else "obj"
            return f"{kid(target.impl)}({', '.join(args)})", kind
        if isinstance(f, ast.Name):
            name = f.id
            args = [self.obj(a) for a in n.args]
            if name == "str":
                return f"Py.str({args[0]})", "str"
            if name == "isinstance":
                t = ast.unparse(n.args[1])
                fn = {"StatusResponse": "isStatus", "dict": "isDict", "list": "isList",
                      "(list, tuple)": "isList", "str": "isStr", "bool": "isBool"}.get(t)
                if not fn:
                    self.fail(f"isinstance {t}")
                return f"Py.{fn}({args[0]})", "bool"
            if name == "quote":
                safe = {k.arg: k.value for k in n.keywords}.get("safe")
                if safe is not None and isinstance(safe, ast.Constant) and safe.value == "":
                    return f"Py.quoteAll({args[0]})", "str"
                if n.keywords and safe is None:
                    self.fail("quote keywords")
                return f"Py.quote({args[0]})", "str"
            if name == "urlencode":
                return f"Py.urlencode({args[0]})", "str"
            if name == "dict":
                return f"Py.copy({args[0]})", "obj"
            if name == "len":
                return f"Py.len({args[0]})", "int"
            if name == "_openStream":
                vals = self.call_args([(p, None, None, None) for p in OPEN_STREAM_PARAMS], n)
                a = [self.obj(v) if v is not None else "null" for v in vals]
                a[0] = "client"
                return f"Py.openStream({', '.join(a)})", "stream"
            self.fail(f"call {name}")
        if isinstance(f, ast.Attribute):
            a = f.attr
            args = [self.obj(x) for x in n.args]
            vsrc = ast.unparse(f.value)
            obj = None if vsrc in ("json", "urllib.parse") else self.obj(f.value)
            if vsrc == "json" and a == "dumps":
                return f"Py.dumps({args[0]})", "str"
            if vsrc == "urllib.parse" and a == "quote":
                safe = {k.arg: k.value for k in n.keywords}.get("safe")
                if safe is not None and isinstance(safe, ast.Constant) and safe.value == "":
                    return f"Py.quoteAll({args[0]})", "str"
                return f"Py.quote({args[0]})", "str"
            if a == "get":
                return f"Py.getOr({obj}, {args[0]}, {args[1] if len(args) > 1 else 'null'})", "obj"
            if a == "lower":
                return f"Py.lower({obj})", "str"
            if a == "strip":
                return f"Py.strip({obj})", "str"
            if a == "rstrip":
                return f"Py.rstrip({obj}, {self.string(n.args[0])})", "str"
            if a == "split":
                return f"Py.split({obj}, {self.string(n.args[0])})", "obj"
            if a == "join":
                return f"Py.join({self.string(f.value)}, {args[0]})", "str"
        self.fail(f"call {ast.unparse(n)}")

    # ------------------------------------------------------------------ statements
    def block(self, stmts, ind):
        out = []
        for s in stmts:
            out += self.stmt(s, ind)
            if always_returns([s]):
                break
        return out

    def ret(self, value):
        if value is None:
            return "null"
        code, kind = self.expr(value)
        if self.m.stream and kind != "stream":
            return f"Py.stream({code})"
        return code

    def stmt(self, s, ind):
        p = "    " * ind
        if isinstance(s, ast.Expr) and isinstance(s.value, ast.Constant):
            return []
        if isinstance(s, ast.Expr) and isinstance(s.value, ast.Call):
            return [p + self.obj(s.value)]
        if isinstance(s, ast.Return):
            return [p + "return " + self.ret(s.value)]
        if isinstance(s, ast.Assign):
            if len(s.targets) != 1:
                self.fail("multiple assignment")
            t = s.targets[0]
            if isinstance(t, ast.Name):
                name = self.renamed.get(t.id, kid(t.id))
                return [f"{p}{name} = {self.obj(s.value)}"]
            if isinstance(t, ast.Subscript):
                return [f"{p}Py.set({self.obj(t.value)}, {self.obj(t.slice)}, {self.obj(s.value)})"]
            self.fail(f"assignment to {ast.unparse(t)}")
        if isinstance(s, ast.If):
            out = [f"{p}if ({self.cond(s.test)}) {{"] + self.block(s.body, ind + 1)
            orelse = s.orelse
            while len(orelse) == 1 and isinstance(orelse[0], ast.If):
                e = orelse[0]
                out += [f"{p}}} else if ({self.cond(e.test)}) {{"] + self.block(e.body, ind + 1)
                orelse = e.orelse
            if orelse:
                out += [f"{p}}} else {{"] + self.block(orelse, ind + 1)
            return out + [p + "}"]
        if isinstance(s, ast.For):
            it = self.obj(s.iter.func.value) if ast.unparse(s.iter).endswith(".items()") else None
            if it is not None and isinstance(s.target, ast.Tuple):
                k, v = (self.renamed.get(e.id, kid(e.id)) for e in s.target.elts)
                out = [f"{p}for (entry_ in Py.entries({it})) {{",
                       f"{p}    {k} = entry_.key", f"{p}    {v} = entry_.value"]
            elif isinstance(s.target, ast.Name):
                out = [f"{p}for (loop_ in Py.iter({self.obj(s.iter)})) {{",
                       f"{p}    {self.renamed.get(s.target.id, kid(s.target.id))} = loop_"]
            else:
                self.fail("for loop")
            return out + self.block(s.body, ind + 1) + [p + "}"]
        if isinstance(s, ast.Try):
            if s.orelse or s.finalbody:
                self.fail("try/else/finally")
            out = [p + "try {"] + self.block(s.body, ind + 1)
            http = req = None
            for h in s.handlers:
                t = ast.unparse(h.type)
                if t.endswith("HTTPError"):
                    http = h
                elif t.endswith("RequestException"):
                    req = h
                else:
                    self.fail(f"except {t}")
            if http:
                out += [p + "} catch (err: OpenHABHTTPError) {"] + self.block(http.body, ind + 1)
            if req:
                out += [p + "} catch (err: OpenHABRequestException) {"] + self.block(req.body, ind + 1)
            return out + [p + "}"]
        if isinstance(s, ast.Pass):
            return []
        self.fail(f"statement {type(s).__name__}: {ast.unparse(s)[:80]}")


def returns_stream(fn, methods):
    for n in ast.walk(fn):
        if isinstance(n, ast.Return) and isinstance(n.value, ast.Call):
            src = ast.unparse(n.value.func)
            if src in ("_openStream", "self.client.executeSSE", "client.executeSSE"):
                return True
            if src.startswith("self.") and src[5:] in methods and methods[src[5:]].stream:
                return True
    return False


def kdoc(fn, ind):
    doc = ast.get_docstring(fn) or ""
    p = "    " * ind
    lines = []
    for l in doc.splitlines():
        l = l.rstrip().replace("*/", "* /").replace("/*", "/ *")
        l = l.replace(":param ", "@param ").replace(":return:", "@return")
        l = re.sub(r"^(\s*@param \w+):", r"\1", l)
        lines.append(l)
    while lines and not lines[-1]:
        lines.pop()
    if not lines:
        return []
    return [p + "/**"] + [p + (" * " + l if l else " *") for l in lines] + [p + " */"]


def signature(m, defaults):
    out = []
    for n, t, d, req in m.params:
        s = f"{kid(n)}: {ptype(t, req)}"
        if defaults and not req:
            s += f" = {default_literal(d, t)}"
        out.append(s)
    return out


def method_code(m, methods):
    tr = Translator(m, methods)
    fn = m.fn
    assigned = {t.id for n in ast.walk(fn) if isinstance(n, ast.Assign)
                for t in n.targets if isinstance(t, ast.Name)}
    assigned |= {n.target.id for n in ast.walk(fn) if isinstance(n, ast.For) and isinstance(n.target, ast.Name)}
    assigned |= {e.id for n in ast.walk(fn) if isinstance(n, ast.For) and isinstance(n.target, ast.Tuple)
                 for e in n.target.elts}
    params = [p[0] for p in m.params]
    for name in sorted(assigned & set(params)):
        tr.renamed[name] = name + "_"
    locals_ = sorted(assigned - set(params))
    body = []
    for name in sorted(tr.renamed):
        body.append(f"        var {tr.renamed[name]}: Any? = {kid(name)}")
    for v in locals_:
        body.append(f"        var {kid(v)}: Any? = null")
    body += tr.block(fn.body, 2)
    if not always_returns(fn.body):
        body.append("        return null")
    rtype = "SSEConnection" if m.stream else "Any?"
    sig = signature(m, defaults=True)
    out = []
    if m.private:
        out += kdoc(fn, 1)
        out.append(f"    private fun {kid(m.name)}({', '.join(sig)}): {rtype} {{")
    else:
        # public suspend function with the Python signature, then the blocking implementation
        out += kdoc(fn, 1)
        out.append(f"    suspend fun {kid(m.name)}(")
        out += [f"        {s}," for s in signature(m, defaults=True)]
        out.append(f"    ): {rtype} = withContext(Dispatchers.IO) {{")
        call = ", ".join(kid(n) for n, _, _, _ in m.params)
        out.append(f"        {m.impl}({call})")
        out.append("    }")
        out.append("")
        out.append(f"    private fun {m.impl}({', '.join(sig)}): {rtype} {{")
    out += body
    out.append("    }")
    return out


def translate_file(path, outdir):
    tree = ast.parse(open(path, encoding="utf-8").read())
    names = []
    for c in tree.body:
        if not isinstance(c, ast.ClassDef):
            continue
        fns = [fn for fn in c.body if isinstance(fn, ast.FunctionDef) and fn.name != "__init__"]
        methods = {fn.name: Method(c.name, fn) for fn in fns}
        for _ in range(2):
            for m in methods.values():
                m.stream = returns_stream(m.fn, methods)
        out = [f"// Generated from {os.path.basename(path)} of python-openhab-rest-client "
               "(tools/py2kt.py) - do not edit.",
               '@file:Suppress("UNUSED_VARIABLE", "UNUSED_VALUE", "UNCHECKED_CAST", '
               '"RedundantSuppression", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", '
               '"UNREACHABLE_CODE", "USELESS_CAST", "NAME_SHADOWING", "FunctionName", '
               '"LocalVariableName", "CanBeVal", "VARIABLE_WITH_REDUNDANT_INITIALIZER")',
               "", f"package {PKG}", "",
               "import kotlinx.coroutines.Dispatchers",
               "import kotlinx.coroutines.withContext", ""]
        cdoc = ast.get_docstring(c)
        out += ["/**"]
        if cdoc:
            out += [" * " + l if l.strip() else " *" for l in cdoc.strip().splitlines()]
            out += [" *"]
        out += [" * All public methods are `suspend` functions; they run the request on",
                " * [Dispatchers.IO] and may therefore be called from the main thread.", " *",
                f" * @param client the [OpenHABClient] used for REST-API communication", " */",
                f"class {c.name}(private val client: OpenHABClient) {{"]
        for m in methods.values():
            out.append("")
            out += method_code(m, methods)
        out.append("}")
        with open(os.path.join(outdir, c.name + ".kt"), "w", encoding="utf-8") as f:
            f.write("\n".join(out) + "\n")
        names.append(c.name)
    return names


def prop_name(cls):
    """Items -> items, ThingTypes -> thingTypes, UI -> ui, UUID -> uuid."""
    if cls.isupper():
        return cls.lower()
    return cls[0].lower() + cls[1:]


def write_facade(names, outdir):
    out = ["// Generated by tools/py2kt.py - do not edit.", "", f"package {PKG}", "",
           "/**",
           " * Convenience entry point: one [OpenHABClient] and all REST classes as properties.",
           " *",
           " * ```kotlin",
           ' * val openHAB = OpenHAB("http://192.168.0.10:8080", token = BuildConfig.OPENHAB_TOKEN)',
           ' * openHAB.items.sendCommand("Wohnzimmer_Licht", "ON")      // in a coroutine',
           " * ```",
           " *",
           " * `OpenHAB(url, …).items` is the same as `Items(OpenHABClient(url, …))`.",
           " */",
           "class OpenHAB @JvmOverloads constructor(",
           "    url: String,",
           "    username: String? = null,",
           "    password: String? = null,",
           "    token: String? = null,",
           "    timeout: Double = OpenHABClient.DEFAULT_TIMEOUT,",
           ") : AutoCloseable {", "",
           "    /** The underlying HTTP client (for [OpenHABClient.login] and low-level calls). */",
           "    val client = OpenHABClient(url, username, password, token, timeout)", ""]
    w = max(len(prop_name(n)) for n in names)
    for n in sorted(names, key=str.lower):
        out.append(f"    val {prop_name(n).ljust(w)} by lazy {{ {n}(client) }}")
    out += ["", "    override fun close() = client.close()", "}"]
    with open(os.path.join(outdir, "OpenHAB.kt"), "w", encoding="utf-8") as f:
        f.write("\n".join(out) + "\n")


def readme_tables(src):
    """Markdown tables of all classes and methods (for README.md)."""
    files = sorted(f for f in glob.glob(os.path.join(src, "*.py"))
                   if not os.path.basename(f).startswith(SKIP_FILES))
    classes = {}
    for f in files:
        tree = ast.parse(open(f, encoding="utf-8").read())
        for c in tree.body:
            if isinstance(c, ast.ClassDef):
                fns = [fn for fn in c.body if isinstance(fn, ast.FunctionDef) and fn.name != "__init__"]
                methods = {fn.name: Method(c.name, fn) for fn in fns}
                for _ in range(2):
                    for m in methods.values():
                        m.stream = returns_stream(m.fn, methods)
                classes[c.name] = [m for m in methods.values() if not m.private]
    out = []
    for name in sorted(classes, key=str.lower):
        out += [f"### {name}", "", "| Method | Parameters | Returns |", "|---|---|---|"]
        for m in classes[name]:
            req = [p[0] for p in m.params if p[3]]
            opt = [p[0] for p in m.params if not p[3]]
            ps = ", ".join(req + ([f"[{', '.join(opt)}]"] if opt else []))
            out.append(f"| `{m.name}` | {ps} | {'`SSEConnection`' if m.stream else 'result'} |")
        out.append("")
    return "\n".join(out)


def main():
    if sys.argv[1] == "--readme":
        print(readme_tables(sys.argv[2]))
        return
    src, outdir = sys.argv[1], sys.argv[2]
    files = sorted(f for f in glob.glob(os.path.join(src, "*.py"))
                   if not os.path.basename(f).startswith(SKIP_FILES))
    names, errors = [], []
    for f in files:
        try:
            names += translate_file(f, outdir)
        except Unsupported as e:
            errors.append(str(e))
    if not errors:
        write_facade(names, outdir)
    for e in errors:
        print("UNSUPPORTED:", e, file=sys.stderr)
    print(f"{len(names)} classes: {', '.join(names)}", file=sys.stderr)
    sys.exit(1 if errors else 0)


if __name__ == "__main__":
    main()
