# Mock Interview: Static Analyzer for SQL Injection

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Application Security Engineer (DevSecOps / AppSec Tooling)
**Candidate Level**: Senior Engineer
**Focus Area**: Static analysis, taint tracking, pattern matching, false-positive engineering
**Problem**: Implement a static analyzer that flags SQL injection in Java source: taint sources (user input), propagation (string concatenation/assignment), and sinks (JDBC execute calls) — with severity, line numbers, and fix suggestions.
**Language**: Java 21+ (regex-based analysis of source text, no external parsers)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. Define taint analysis: sources, sinks, sanitizers. Where does SQL injection fit?
2. Why is regex-based analysis fundamentally limited, and what do real tools (Semgrep, CodeQL, SpotBugs) do instead?
3. How do you keep the false-positive rate manageable?
4. Why does prepared-statement usage count as a sanitizer, and what does the analyzer need to verify about it?
5. What sinks beyond executeQuery matter (JPA, MyBatis, string builders)?
6. Follow-up: data flow vs control flow, interprocedural analysis, and how CI gating works with severity levels.

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "Our CI needs a first-pass SQL injection scanner for Java code — fast, dependency-free, integrated into a build step. Not a replacement for Semgrep/CodeQL — a tripwire. Scope it."

**Candidate**: "Three scoping questions. First, the analysis unit: whole files, one statement at a time — I'll assume line-based analysis with a lightweight *taint table* tracking which variables hold user-controlled data, which is the minimum viable model. Second, the sources/sinks contract: sources are the classic user-input getters — `getParameter`, `getenv`, `System.console().readLine`, Scanner — and sinks are the JDBC execution calls: `executeQuery(String)`, `execute(String)`, `executeUpdate(String)`. Third: what the output is — findings with line numbers, severity, and a suggested fix, so the CI gate has an actionable artifact."

**Interviewer**: "Correct. And the sanitizer — what counts?"

**Candidate**: "PreparedStatement with '?' placeholders is the sanitizer: `setString(1, userInput)` binds parameters, so concatenation into the SQL string is absent. But the analyzer must verify the *shape*, not the name: `PreparedStatement ps = conn.prepareStatement("SELECT ... WHERE id = " + userInput)` — a prepared *statement* built by concatenation — is still injectable, and my analyzer must catch that (a classic false-negative in naive tools). Also: `Statement stmt = conn.createStatement()` without a sanitizer on the query string is a finding in its own right — medium severity, 'prefer PreparedStatement'."

### Part 2: Theory — Taint Analysis (10 minutes)

**Interviewer**: "Define the three-part taint model."

**Candidate**: "Taint analysis tracks whether attacker-controlled data can flow into a dangerous operation. The model: **sources** — places where untrusted data enters (HTTP parameters, headers, env vars, files, user input streams); **sinks** — operations that misuse data (SQL execution, eval, file paths, HTML rendering); **sanitizers** — transformations that neutralize taint (parameterized queries, HTML escaping, allowlist validation). A finding exists iff a tainted value reaches a sink without passing a sanitizer. SQL injection is the canonical example: tainted string + string concatenation + `executeQuery` = HIGH finding."

**Interviewer**: "What are the limits of a regex/line-based analyzer?"

**Candidate**: "Three fundamental ones. (1) **No parse tree**: string concatenation across lines, method calls, and expression nesting need a real parser — `executeQuery(buildQuery(id))` where `buildQuery` concatenates internally is invisible to line-level regex. (2) **No data flow across methods**: interprocedural taint — parameter passed to a helper that executes it — requires call-graph analysis; that's where CodeQL's data-flow library shines. (3) **No control-flow sensitivity**: `if (userInput.matches("[0-9]+"))` — a sanitizer branch — needs conditional analysis to model. So the honest design: the regex engine is a *tripwire* that over-approximates (safe direction: report more, not less), with severity calibrated so CI gating stays sane. Real tools are syntax-based: Semgrep pattern-matches on ASTs with metavariables, SpotBugs uses bytecode + visitor patterns, CodeQL builds full data-flow graphs."

**Interviewer**: "So how do you control false positives?"

**Candidate**: "Four levers. (1) **Taint-tracking discipline**: only flag a sink when the taint is *proven* by the table — untainted concatenations produce no finding. (2) **Sanitizer whitelist**: recognize the `?`-placeholder shape and `setString/setInt` binding calls as neutralizers. (3) **Severity calibration**: taint-at-sink = HIGH; statement-without-prepared = MEDIUM (style/defense-in-depth); taint observed but not reaching a sink = INFO. (4) **Scope filters**: ignore test directories by default (`src/test`), since test fixtures routinely build SQL strings. The CI gate then only blocks on HIGH, letting MEDIUM flow into the review queue."

### Part 3: Design (8 minutes)

**Interviewer**: "Design the analysis pipeline."

**Candidate**: "Four stages over the file's lines: (1) **Source detection**: match source patterns; mark the assigned variable or the expression as tainted. (2) **Propagation**: assignments and compound assignments — if the RHS references any tainted identifier or concatenates a tainted value, the LHS becomes tainted; string-literal-only assignments clean the variable (with a comment noting the caveat). (3) **Sink detection**: match the JDBC call shapes; extract the argument; if the argument expression is tainted or contains a tainted identifier — HIGH finding with the line number. (4) **Sanitizer verification**: if the call is a `prepareStatement` whose argument is tainted, that's itself a HIGH finding (the shape trap); `setString` on a placeholder-built statement is a clean signal that clears the statement variable's taint."

**Interviewer**: "What data structure backs the taint table?"

**Candidate**: "A `Map<String, Boolean>` — variable name → tainted — per file, plus a special handling for *inline* taint: a sink argument is tainted if it (a) is a tainted table entry, or (b) contains `+` with a tainted operand, or (c) contains a source-pattern call directly (`executeQuery("... " + request.getParameter("id"))`). String scanning of the argument expression, not just variable lookup — that's the difference between an analyzer and a grep."

### Part 4: Implementation (18 minutes)

**Interviewer**: "Code the core."

**Candidate**:

```java
private static final List<Pattern> SOURCES = List.of(
    Pattern.compile("getParameter\\s*\\("),
    Pattern.compile("getenv\\s*\\("),
    Pattern.compile("\\.readLine\\s*\\("),
    Pattern.compile("\\.nextLine\\s*\\("),
    Pattern.compile("getHeader\\s*\\("));

private static final List<Pattern> SINKS = List.of(
    Pattern.compile("executeQuery\\s*\\("),
    Pattern.compile("executeUpdate\\s*\\("),
    Pattern.compile("(?<!Prepared)execute\\s*\\("));  // bare Statement.execute

private static final Pattern PREPARED_BUILD = Pattern.compile("prepareStatement\\s*\\(");
private static final Pattern PARAM_BIND = Pattern.compile("set(?:String|Int|Long|Object)\\s*\\(");
```

**Candidate**: "Then the per-line loop with the taint table and the finding accumulator — I'll write it in the walkthrough in full, but the decision core is: a variable becomes tainted iff a source pattern appears in its assigned expression; a sink argument is a finding iff the taint check on that expression returns true; and prepareStatement with a tainted argument is a HIGH finding *before* the binding calls are considered, because the shape is broken regardless of what gets bound later."

**Interviewer**: "How do you report?"

**Candidate**: "A `Finding(line, severity, message, suggestion)` record — severity enum {INFO, MEDIUM, HIGH} — with a stable, machine-readable format: `file:line: severity: message` plus the suggested fix text. The main driver prints a summary: N findings, M high — and the CI contract: exit code nonzero iff any HIGH."

### Part 5: Testing (5 minutes)

**Interviewer**: "The test corpus — what does it need to cover?"

**Candidate**: "Ten cases: (1) direct concatenation at the sink — `executeQuery("... WHERE id = " + id)` → HIGH; (2) taint via intermediate variable — `String q = "..." + user; stmt.executeQuery(q)` → HIGH; (3) prepared statement with `?` + `setString` → clean (the negative case that naive tools get wrong); (4) the shape trap — `prepareStatement("... WHERE id = " + user)` → HIGH even though it's a PreparedStatement; (5) untainted constant query → clean; (6) source assigned then `+=` concatenated → HIGH; (7) taint source in scope but never reaching a sink → INFO at most; (8) bare `Statement.execute` sink; (9) `executeUpdate` with tainted delete/update → HIGH; (10) test-directory exclusion — fixture code in `src/test` produces nothing."

### Part 6: Follow-ups (5 minutes)

**Interviewer**: "Beyond JDBC — where does the same model go next?"

**Candidate**: "Same three-part model, different sinks: path traversal (`File` + `getParameter`), command injection (`Runtime.exec` + tainted input), log forging (newline injection into log lines), XSS in server-rendered templates, and ORM query building (JPA `createQuery("... " + input)`, MyBatis `${}` vs `#{}` — the latter is the injection-prone form). Each is a new sink table and a sanitizer model — the engine doesn't change. The deeper upgrade: parse-based analysis with a real Java grammar so taint flows across expressions and methods; the lab's regex core is the pedagogical first step and a genuinely useful tripwire, but the honest engineering answer is that production-grade SAST is syntax + data-flow — Semgrep or CodeQL — and this tool's job is to catch the 80% cheaply and fast in the local build."

---

## Extended Q&A: Follow-up Round

**Q: Give a concrete interprocedural bug this line-level analyzer misses.**

**A**: `String q = QueryBuilder.forId(userInput).build(); stmt.executeQuery(q);` — the taint is constructed inside `QueryBuilder.forId`, so the line-level analyzer sees an untainted `q` at the sink and reports nothing. A data-flow engine follows the argument into the method, taints its return value, and flags the sink. The tripwire's contract is honest about this: catch the direct 80% fast in the local build, document the gap, and let the CI pipeline's Semgrep/CodeQL stage catch the flow-through cases.

**Q: Why is over-approximation (reporting more, not less) the safe direction?**

**A**: A false positive costs a developer a minute of triage; a false negative costs a breach. Calibrated severity keeps the noise manageable — HIGH findings block CI, MEDIUM flow to the review queue, INFO are context — while the bias stays conservative: taint evidence → report; uncertain → report as INFO; never silently drop.

**Q: What is SARIF and why does the output format matter?**

**A**: SARIF (Static Analysis Results Interchange Format) is the vendor-neutral JSON schema for findings. Once the tool emits SARIF, it integrates with GitHub code scanning, IDEs, and Jira without per-tool connectors. The CI primitive stays the exit-code contract — nonzero iff any HIGH — but the artifact becomes portable.

**Q: Why does the analyzer need to see the `?`-placeholder shape, not just the name `PreparedStatement`?**

**A**: Because `prepareStatement("SELECT ... WHERE id = " + user)` is a prepared statement built by concatenation — the placeholder shape is absent, so binding calls later cannot save it. Name-based checks create false confidence; shape-based checks catch the actual injection.

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Taint model | Sources/propagation/sinks/sanitizers all explicit | Sources and sinks only | Sink grep only |
| Analyzer design | Taint table + expression-level checks + sanitizer shape check | Variable-level taint | Line matching |
| The shape trap | Catches prepareStatement built by concatenation | Misses it | Misses it |
| Reporting | Severity + suggestion + CI exit-code contract | Line numbers only | No structure |
| Honesty | States regex limits and names real tools (Semgrep/CodeQL) | Vague about limits | Claims full coverage |

## Red Flags
- Flagging *every* executeQuery regardless of taint (grep, not analysis).
- Missing the prepareStatement-with-concatenation trap.
- No severity/CI gating story.
- Claiming the regex tool replaces a real SAST engine.

## Key Takeaways
- Taint = source → propagation → sink, with sanitizers as the neutralizer.
- Analyzers over-approximate safely; severity + CI gating manage the noise.
- Prepared statements with '?' bind values — but only if the SQL string itself is untainted.
- Regex engines are tripwires; parse/data-flow engines (Semgrep, CodeQL) are the production answer.

## Glossary

- **Taint** — a value that traces back to an untrusted source.
- **Source** — where untrusted data enters (HTTP parameters, headers, env vars).
- **Sink** — a dangerous operation on data (SQL execution, eval, file paths).
- **Sanitizer** — a transformation that neutralizes taint (parameter binding, escaping).
- **Tripwire** — a deliberately simple scanner that catches the common cases fast.
- **False positive / false negative** — a finding that isn't a bug / a bug that isn't found.
- **Interprocedural analysis** — following data flow across method boundaries.
- **Data flow** — the path a value takes from source to sink through assignments and calls.
- **SARIF** — the vendor-neutral JSON format for static-analysis findings.
- **Shape check** — verifying the *structure* of an API use (placeholders), not just its name.
- **Severity gating** — CI blocks only on HIGH findings; MEDIUM flows to the review queue.
