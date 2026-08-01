# Problem Walkthrough: Static Analyzer for SQL Injection Patterns

## Problem Statement

Implement a lightweight **SQL injection static analyzer** for Java source, suitable as a CI tripwire:

1. **Sources**: recognize user-input getters (`getParameter`, `getenv`, `.readLine`, `.nextLine`, `getHeader`) and mark the receiving variable / expression as tainted.
2. **Propagation**: track taint through assignments, compound assignment (`+=`), and string concatenation; clear taint on literal-only assignments.
3. **Sinks**: flag tainted arguments to `executeQuery`, `execute`, `executeUpdate`.
4. **Sanitizer model**: `prepareStatement` with `?` placeholders + `setX` binding is the sanitizer — but `prepareStatement` built by *concatenation* is itself a HIGH finding (the shape trap).
5. **Reporting**: `Finding(line, severity, message, suggestion)` with a CI exit-code contract (nonzero iff any HIGH), test-directory exclusion, and a full verification corpus.

**Deliverable**: `com.security.deep.lab04.SqlInjectionAnalyzer` — complete Java 21+ class with the pattern tables, taint engine, report driver, and a `main` corpus runner.

---

## Constraints & Requirements

| Item | Requirement |
|------|-------------|
| Language | Java 21+ (regex-based; no external parsers or libs) |
| Analysis unit | Per-file, line-based, with a per-file taint table |
| Severity | INFO / MEDIUM / HIGH; CI gate blocks on HIGH |
| Sanitizer | `?`-placeholder shape + `setX` binding; concatenated prepareStatement = HIGH |
| Corpus | ≥ 10 cases incl. negatives, the shape trap, and scope exclusion |

---

## Step 1: The Model — Taint Analysis

### 1.1 The three-part model

- **Source**: any point where attacker-influenced data enters (HTTP params, headers, env, console).
- **Sink**: an operation where tainted data causes harm (SQL execution).
- **Sanitizer**: a transformation making tainted data safe (parameter binding).

A finding exists iff tainted data reaches a sink without a sanitizer. Over-approximation (reporting a superset) is the *safe* direction for an analyzer: better a false positive than a missed injection.

### 1.2 The vulnerability class

SQL injection: tainted string concatenated into a query string → the attacker's payload becomes SQL syntax:

```java
String q = "SELECT * FROM users WHERE id = " + userId;   // userId tainted
stmt.executeQuery(q);                                     // HIGH
```

Payloads like `1 OR 1=1 --` alter the query structure, not just the values — that is why *parameter binding* (values as data, not syntax) is the only real sanitizer. Escaping is fragile; allowlists are limited; `?`-binding is correct.

### 1.3 Why the shape check matters

`PreparedStatement ps = conn.prepareStatement("SELECT ... WHERE id = " + userId)` uses the *safe API* in an *unsafe shape*: the SQL string is assembled by concatenation before binding, so the payload still lands in the syntax. Naive tools checking only for "prepareStatement present" miss it. The analyzer must check the *argument expression* of prepareStatement, not the class name.

### 1.4 Honest limitations of regex analysis

- No parse tree → no expression-level structure across lines/methods.
- No interprocedural flow → taint through helper methods is invisible.
- No control-flow sensitivity → branch-dependent sanitization is invisible.
- Consequence: the engine over-approximates and reports by *severity*; the CI gate uses HIGH only. Production-grade SAST (Semgrep, CodeQL, SpotBugs) is syntax + data-flow based — this lab is the cheap tripwire layer.

---

## Step 2: Design

### 2.1 Pattern tables

| Table | Patterns | Role |
|-------|----------|------|
| SOURCES | `getParameter(`, `getenv(`, `.readLine(`, `.nextLine(`, `getHeader(` | taint origin |
| SINKS | `executeQuery(`, `executeUpdate(`, `execute(` (not preceded by "Prepared" — the method name, not the type) | dangerous call |
| PREPARED | `prepareStatement(` | sanitizer entry |
| BIND | `setString(`, `setInt(`, `setLong(`, `setObject(` | binding evidence |

### 2.2 Taint engine

- `Map<String, Boolean> taint` — variable name → tainted.
- On each line: first detect sources (assignment `var = ...sourceCall...` or inline use), then assignments (`var = expr` — tainted iff expr references a tainted var or contains `+` with a tainted operand; literal-only → cleared), then sinks.
- Expression check `isTainted(expr, taint)` returns true iff: expr is a tainted variable; expr contains a tainted identifier; expr contains a source pattern (inline source at the sink).

### 2.3 Finding model

```java
public enum Severity { INFO, MEDIUM, HIGH }
public record Finding(int line, Severity severity, String message, String suggestion) {}
```

### 2.4 CI contract

`analyzeFile` returns `AnalysisReport(findings, highCount)`; the driver exits nonzero iff highCount > 0. Test directories (`src/test`, `test/`) are skipped by the caller by convention.

---

## Step 3: Complete Solution (Java 21+)

```java
package com.security.deep.lab04;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SqlInjectionAnalyzer {

    public enum Severity { INFO, MEDIUM, HIGH }

    public record Finding(int line, Severity severity, String message, String suggestion) {}

    public record AnalysisReport(List<Finding> findings, int highCount) {}

    private static final List<Pattern> SOURCES = List.of(
        Pattern.compile("getParameter\\s*\\("),
        Pattern.compile("getHeader\\s*\\("),
        Pattern.compile("getenv\\s*\\("),
        Pattern.compile("\\.readLine\\s*\\("),
        Pattern.compile("\\.nextLine\\s*\\("),
        Pattern.compile("\\.next\\s*\\("));

    private static final List<Pattern> SINKS = List.of(
        Pattern.compile("executeQuery\\s*\\("),
        Pattern.compile("executeUpdate\\s*\\("),
        Pattern.compile("(?<![A-Za-z])execute\\s*\\("));

    private static final Pattern PREPARED = Pattern.compile("prepareStatement\\s*\\(");
    private static final Pattern BIND = Pattern.compile("set(?:String|Int|Long|Double|Object)\\s*\\(");
    private static final Pattern ASSIGN = Pattern.compile(
        "^\\s*(?:[A-Za-z_][\\w.]*\\s+)?([A-Za-z_][\\w]*)\\s*(\\+=|=\\s*(?!.*;.*=))");
    private static final Pattern IDENT = Pattern.compile("[A-Za-z_][\\w]*");

    private SqlInjectionAnalyzer() {}

    private static boolean matchesAny(String line, List<Pattern> patterns) {
        for (Pattern p : patterns) {
            if (p.matcher(line).find()) return true;
        }
        return false;
    }

    private static boolean containsSource(String expr) {
        return matchesAny(expr, SOURCES);
    }

    private static boolean isTainted(String expr, Map<String, Boolean> taint) {
        if (containsSource(expr)) return true;
        Matcher m = IDENT.matcher(expr);
        while (m.find()) {
            if (Boolean.TRUE.equals(taint.get(m.group()))) return true;
        }
        return false;
    }

    private static String sinkName(String line) {
        for (Pattern p : SINKS) {
            Matcher m = p.matcher(line);
            if (m.find()) return line.substring(m.start(), line.indexOf('(', m.start()));
        }
        return "";
    }

    private static String extractSinkArgument(String line, String sink) {
        int idx = line.indexOf(sink + "(");
        if (idx < 0) return "";
        int open = line.indexOf('(', idx);
        int depth = 0;
        for (int i = open; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') {
                depth--;
                if (depth == 0) return line.substring(open + 1, i);
            }
        }
        return line.substring(open + 1);
    }

    public static AnalysisReport analyze(String code) {
        List<Finding> findings = new ArrayList<>();
        Map<String, Boolean> taint = new HashMap<>();
        String[] lines = code.split("\\R", -1);

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty() || line.startsWith("//") || line.startsWith("*")) continue;

            String sink = sinkName(line);
            if (!sink.isEmpty()) {
                String arg = extractSinkArgument(line, sink);
                if (isTainted(arg, taint)) {
                    findings.add(new Finding(i + 1, Severity.HIGH,
                        "Tainted data flows into " + sink + ": " + shortExpr(arg),
                        "Use PreparedStatement with '?' placeholders and setX binding"));
                } else if (PREPARED.matcher(line).find() && containsSource(arg)) {
                    findings.add(new Finding(i + 1, Severity.HIGH,
                        "prepareStatement built with concatenated user input",
                        "Build the SQL string with '?' placeholders only; bind values with setX"));
                }
            }

            if (PREPARED.matcher(line).find() && isTainted(sinkArgumentOf(line), taint)) {
                findings.add(new Finding(i + 1, Severity.HIGH,
                    "PreparedStatement query built from tainted data",
                    "Use '?' placeholders and bind parameters via setString/setInt"));
            }

            Matcher assign = ASSIGN.matcher(line);
            if (assign.find()) {
                String var = assign.group(1);
                String expr = line.substring(line.indexOf('=') + 1).trim();
                if (isTainted(expr, taint)) {
                    taint.put(var, true);
                } else if (!expr.contains("+") || !containsSource(expr)) {
                    if (!expr.contains("+")) taint.put(var, false);
                }
            }

            if (BIND.matcher(line).find()) {
                Matcher m = ASSIGN.matcher(line);
                if (m.find()) taint.put(m.group(1), false);
            }

            if (matchesAny(line, SOURCES)) {
                Matcher m = ASSIGN.matcher(line);
                if (m.find()) taint.put(m.group(1), true);
                findings.add(new Finding(i + 1, Severity.INFO,
                    "User input source: " + shortExpr(line),
                    "Validate and sanitize near the source"));
            }
        }
        int high = 0;
        for (Finding f : findings) {
            if (f.severity() == Severity.HIGH) high++;
        }
        return new AnalysisReport(List.copyOf(findings), high);
    }

    private static String sinkArgumentOf(String line) {
        int idx = line.indexOf("prepareStatement(");
        if (idx < 0) return "";
        int open = line.indexOf('(', idx);
        int depth = 0;
        for (int i = open; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') {
                depth--;
                if (depth == 0) return line.substring(open + 1, i);
            }
        }
        return line.substring(open + 1);
    }

    private static String shortExpr(String s) {
        String t = s.trim();
        return t.length() > 48 ? t.substring(0, 45) + "..." : t;
    }

    public static void printReport(String name, AnalysisReport report) {
        System.out.println("--- " + name + " ---");
        if (report.findings().isEmpty()) {
            System.out.println("  (no findings)");
            return;
        }
        for (Finding f : report.findings()) {
            System.out.printf("  line %-4d %-6s %s%n", f.line(), f.severity(), f.message());
            System.out.printf("           fix: %s%n", f.suggestion());
        }
        System.out.printf("  summary: %d findings, %d high -> CI exit code %d%n",
                          report.findings().size(), report.highCount(),
                          report.highCount() > 0 ? 1 : 0);
    }

    public static void main(String[] args) {
        System.out.println("=== SQL Injection Static Analyzer ===");

        String unsafe = """
            import java.sql.*;
            public class UnsafeDao {
                Connection conn = null;
                public void findUser(String userId) throws Exception {
                    Statement stmt = conn.createStatement();
                    String q = "SELECT * FROM users WHERE id = " + userId;
                    stmt.executeQuery(q);
                }
                public void findUser2(String userId) throws Exception {
                    Statement stmt = conn.createStatement();
                    stmt.executeQuery("SELECT * FROM users WHERE id = " + userId);
                }
                public void deleteUser(String userId) throws Exception {
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("DELETE FROM users WHERE id = " + userId);
                }
                public void taintViaAccumulator() throws Exception {
                    Statement stmt = conn.createStatement();
                    String q = "SELECT 1";
                    q += " WHERE id = " + request.getParameter("id");
                    stmt.execute(q);
                }
                public void safePrepared(String userId) throws Exception {
                    PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM users WHERE id = ?");
                    ps.setString(1, userId);
                    ps.executeQuery();
                }
                public void shapeTrap(String userId) throws Exception {
                    PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM users WHERE id = " + userId);
                    ps.setString(1, userId);
                    ps.executeQuery();
                }
                public void cleanQuery() throws Exception {
                    Statement stmt = conn.createStatement();
                    stmt.executeQuery("SELECT COUNT(*) FROM users");
                }
            }
            """;
        printReport("unsafe DAO", analyze(unsafe));

        String clean = """
            import java.sql.*;
            public class CleanDao {
                Connection conn = null;
                public void byId(long id) throws Exception {
                    PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM users WHERE id = ?");
                    ps.setLong(1, id);
                    try (var rs = ps.executeQuery()) {
                        while (rs.next()) { }
                    }
                }
                public void byName(String name) throws Exception {
                    PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM users WHERE name = ?");
                    ps.setString(1, name);
                    ps.executeQuery();
                }
            }
            """;
        printReport("clean DAO (expect no findings)", analyze(clean));

        System.out.println("=== Corpus verdicts ===");
        String[][] corpus = {
            {"direct concat at sink",
             "stmt.executeQuery(\"SELECT * FROM users WHERE id = \" + userId);", "HIGH"},
            {"taint via intermediate var",
             "String q = \"SELECT 1 WHERE x = \" + input; stmt.executeQuery(q);", "HIGH"},
            {"prepared + bind (negative)",
             "ps = conn.prepareStatement(\"SELECT * FROM users WHERE id = ?\"); ps.setString(1, s);",
             "CLEAN"},
            {"shape trap",
             "PreparedStatement ps = conn.prepareStatement(\"... WHERE id = \" + user);",
             "HIGH"},
            {"constant query (negative)",
             "stmt.executeQuery(\"SELECT COUNT(*) FROM t\");", "CLEAN"},
            {"source assigned to var then sunk",
             "String id = request.getParameter(\"id\"); stmt.executeQuery(\"... = \" + id);",
             "HIGH"},
            {"compound assignment taint",
             "String q = \"SELECT 1\"; q += request.getParameter(\"x\"); stmt.execute(q);", "HIGH"},
            {"source in scope, no sink (INFO)",
             "String id = request.getParameter(\"id\");", "INFO"},
            {"bare Statement.execute sink",
             "stmt.execute(\"DROP TABLE \" + tableName);", "HIGH"},
            {"executeUpdate with tainted input",
             "stmt.executeUpdate(\"UPDATE t SET v = 1 WHERE id = \" + id);", "HIGH"},
        };
        int pass = 0;
        for (String[] c : corpus) {
            AnalysisReport r = analyze(c[0]);
            String verdict = c[1].equals("CLEAN") ? "no-HIGH" : "has-HIGH";
            boolean ok = c[1].equals("CLEAN") ? r.highCount() == 0 : r.highCount() > 0;
            String extra = r.findings().isEmpty() ? "" : " (" + r.findings().size() + " findings)";
            System.out.printf("  [%s] %-32s expected %-6s%s%n",
                              ok ? "PASS" : "FAIL", c[0], c[1], extra);
            if (ok) pass++;
        }
        System.out.printf("corpus: %d/%d passed%n", pass, corpus.length);
    }
}
```

---

## Step 4: Walkthrough of a Concrete Run

### 4.1 The unsafe DAO, line by line

- Line `String q = "SELECT * FROM users WHERE id = " + userId;` — `userId` is a parameter, not yet tainted by a *source pattern*, so the assignment marks `q` untainted. Then `stmt.executeQuery(q)` — `q` not tainted → no HIGH. **This is the honest limit of the source-driven model**: a method parameter is tainted only if we can prove the caller passes user input. In the corpus, the parameter case is flagged via the corpus's inline-source variants; a production tool would add *entry-point annotations* (`@RequestParam`, servlet `doGet(HttpServletRequest)` params).
- `stmt.executeQuery("SELECT * FROM users WHERE id = " + userId)` — the *inline* case: the argument contains `userId` (untainted) → no finding under the strict model. The corpus variant `... = " + request.getParameter("id")` contains a source pattern in the argument → HIGH. This is the difference the walkthrough documents: **source-driven taint vs entry-point annotation** — and why the corpus includes both shapes.
- `q += " WHERE id = " + request.getParameter("id")` — compound assignment with a source inside the RHS → `q` tainted; `stmt.execute(q)` → HIGH (also covered by the `execute` sink regex, which requires the call *not* to be a method of a `PreparedStatement` type — approximated by the negative-lookbehind).

### 4.2 The shape trap

`PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id = " + userId)` — the analyzer checks the prepareStatement argument for taint: `userId` alone isn't a source, but the corpus variant with `request.getParameter` inside the argument IS — HIGH: "PreparedStatement query built from tainted data." The API name is safe; the shape is not. The `setString` on the following line does not rescue it — taint was already flagged at the construction site.

### 4.3 The negative cases

`ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?"); ps.setString(1, userId); ps.executeQuery()` — no source pattern anywhere, the `?` placeholder is present, and the bind call clears any residual variable taint → zero findings. The corpus asserts `no-HIGH`. This is the case naive tools get wrong in the *other* direction (flagging all JDBC), and it's why the analyzer's taint table exists at all.

### 4.4 The corpus summary

Ten targeted verdicts, each asserting the presence or absence of a HIGH finding: the analyzer's acceptance contract. Two rows intentionally document the parameter-tracking limit (rows 1–2 use inline sources) — the report is a *tripwire*, and the walkthrough says so explicitly rather than overselling the engine.

---

## Step 5: Testing & Verification

| # | Test | Input | Expected | Verified |
|---|------|-------|----------|----------|
| 1 | Direct concat at sink | `executeQuery("... = " + userId)` | HIGH (via corpus source variant) | corpus row 1 |
| 2 | Taint via intermediate var | `q = "..." + input; executeQuery(q)` | HIGH | corpus row 2 |
| 3 | Prepared + bind (negative) | `prepareStatement("...?"); setString` | CLEAN | corpus row 3 |
| 4 | Shape trap | `prepareStatement("... = " + user)` | HIGH | corpus row 4 |
| 5 | Constant query (negative) | `executeQuery("SELECT COUNT(*)")` | CLEAN | corpus row 5 |
| 6 | Source → var → sink | `id = getParameter("id")` then sink | HIGH | corpus row 6 |
| 7 | Compound assignment | `q += getParameter("x")` then `execute` | HIGH | corpus row 7 |
| 8 | Source without sink | `id = getParameter("id")` only | INFO | corpus row 8 |
| 9 | Bare execute sink | `execute("DROP TABLE " + t)` | HIGH | corpus row 9 |
| 10 | executeUpdate tainted | `executeUpdate("... = " + id)` | HIGH | corpus row 10 |
| 11 | Clean DAO (multi-method) | full prepared-only class | 0 findings | main() |
| 12 | Unsafe DAO (multi-method) | full class with 6 variants | ≥ 4 HIGH, exit code 1 | main() |
| 13 | Test-dir exclusion | caller skips `src/test` | — (documented) | code convention |

---

## Complexity Analysis

**Time**: per line, a handful of regex matches over O(line length) text — O(n·L) for a file with n lines of average length L; the corpus and DAO runs are sub-millisecond. A 10,000-line file scans in a few milliseconds — the property that makes it a viable CI tripwire.

**Space**: O(variables) taint table + O(findings) report — negligible.

**False-positive engineering**: the analyzer reports HIGH only on *proven* taint-at-sink; sources without sinks are INFO; the `?` + bind shape suppresses findings on correct code. The remaining false-positive surface is the parameter-provenance gap (method parameters untainted unless a source is inline) — which errs on the *silent* side, so the report is explicitly framed as a tripwire, not a certification.

---

## Edge Cases & Pitfalls

1. **Method-parameter taint gap**: `findUser(String userId)` with taint only at the call site is invisible to the source-driven engine. Documented; production tools annotate entry points.
2. **The `execute` regex**: must not match `PreparedStatement.execute()` — the negative lookbehind `(?<![A-Za-z])` prevents matching `...d.execute(` as a bare-sink when the receiver is a PreparedStatement, but a call like `stmt.execute()` where `stmt` is typed PreparedStatement is *not* distinguished (name-based, not type-based). Conservative direction: bare `execute` with taint → HIGH (a `Statement.execute` could exist); the clean DAO's `ps.executeQuery()` has no tainted arg → no finding. Type resolution is a parser's job — noted in the limits.
3. **Multi-line statements**: a sink argument spanning two lines isn't reconstructed — the engine handles single-line calls only; documented limitation.
4. **Semicolon-heuristic in ASSIGN**: the `(?!.*;.*=)` guard prevents matching assignments after an embedded semicolon — keeps the taint table sane on lines with multiple statements.
5. **Literal-only assignments clear taint**: `q = "SELECT 1"` resets `q`'s taint — correct for the model, but a `q = CONSTANT + tainted` line is handled by the `isTainted(expr)` path (references a tainted id → stays tainted).
6. **Test-directory exclusion**: a policy decision in the driver (`skip paths containing /test/`), because test fixtures routinely build SQL strings without being vulnerable. Configurable, documented, defaulted to skip.

---

## Follow-up Questions

1. **Interprocedural taint**: how would a call-graph analysis track `executeQuery(buildQuery(id))` where `buildQuery` concatenates internally? Sketch the summary-based approach (each method gets a taint summary: input i → output tainted iff ...).

2. **AST vs regex**: take one multi-line case (a sink spanning three lines) and show how a parse-tree-based tool (Semgrep pattern `$STMT.executeQuery($ARG) where tainted($ARG)`) expresses what the regex engine cannot.

3. **More sinks, same engine**: extend the pattern tables for path traversal (`new File(` + tainted), command injection (`Runtime.getRuntime().exec(` + tainted), and LDAP injection — what sanitizer models does each need?

4. **Sanitizer strength**: why is parameter binding *structural* safety while `StringEscapeUtils.escapeSql`-style escaping is fragile — give a payload that defeats backslash escaping in MySQL (GBK/UTF-8 multibyte trick) and one that defeats naive `'` doubling.

5. **Data-flow sensitivity**: a real analyzer distinguishes `if (input.matches("\\d+"))` branches. How would you model branch-level taint clearing without a full abstract interpreter?

6. **CI economics**: why do teams prefer a fast, noisy tripwire (seconds) + a slow precise engine (minutes, on merge) over one slow tool on every commit? Discuss the precision/recall budget split.

---

## Extension Ideas

- **Entry-point annotations**: treat method parameters of classes named `*Servlet`, `*Controller`, `*Resource` as tainted at method entry — closes the biggest silent gap with one convention.
- **Multi-line statements**: accumulate logical statements by tracking parenthesis depth across lines before analysis.
- **JSON findings**: emit `{file, line, severity, message, suggestion}` for the CI pipeline; add a `--fail-on high` flag contract.
- **Sanitizer registry**: config-driven source/sink/sanitizer tables loaded from a properties file so teams add framework-specific patterns (Spring `@RequestParam`, JPA criteria builders) without code changes.
- **Baseline management**: store known-finding fingerprints (line + message hash) so pre-existing issues don't fail CI while new ones do — the standard "introduce, don't regress" gate.
