# Mock Interview Transcript: Pattern Matching

## Interviewer: Staff Engineer, Oracle
## Candidate: Senior Java developer
## Time: 40 minutes
## Focus: instanceof pattern, switch patterns, deconstruction

---

**Q1: Trace the evolution of pattern matching in Java.**

**Candidate**: Java 16: `instanceof` pattern matching (JEP 394) — `if (obj instanceof String s)`. Java 17: Sealed classes + `instanceof`. Java 19: Preview of switch pattern matching. Java 21: Final — `switch` with patterns (JEP 441), record patterns (JEP 440).

**Interviewer**: Write a method using switch pattern matching that processes a JSON-like node.

**Candidate**: 
```java
sealed interface JsonNode permits JsonObject, JsonArray, JsonString, JsonNumber, JsonNull {}
record JsonObject(Map<String, JsonNode> fields) implements JsonNode {}
record JsonArray(List<JsonNode> elements) implements JsonNode {}
record JsonString(String value) implements JsonNode {}
record JsonNumber(double value) implements JsonNode {}
record JsonNull() implements JsonNode {}

String jsonToString(JsonNode node) {
    return switch (node) {
        case JsonObject(var fields) -> {
            var entries = fields.entrySet().stream()
                .map(e -> "\"" + e.getKey() + "\":" + jsonToString(e.getValue()))
                .collect(Collectors.joining(","));
            yield "{" + entries + "}";
        }
        case JsonArray(var elements) -> {
            var items = elements.stream().map(e -> jsonToString(e)).collect(Collectors.joining(","));
            yield "[" + items + "]";
        }
        case JsonString(var v) -> "\"" + v + "\"";
        case JsonNumber(var v) -> String.valueOf(v);
        case JsonNull() -> "null";
    };
}
```

**Interviewer**: How does the compiler check exhaustiveness?

**Candidate**: The compiler analyzes the sealed hierarchy (all permitted subtypes must be covered) OR there must be a `default` case. If new subtypes are added, unhandled switches fail compilation. This is a key benefit — no more runtime errors from unhandled cases.

**Interviewer**: What's the difference between a guarded pattern and a regular pattern?

**Candidate**: Guarded patterns add a condition after `when`:
```java
String describe(Object obj) {
    return switch (obj) {
        case String s when s.length() > 100 -> "Long string";
        case String s -> "Short string";
        case Integer i when i < 0 -> "Negative";
        case Integer i -> "Non-negative";
        case null -> "null";
        default -> "Unknown";
    };
}
```

**Interviewer**: How does the compiler resolve pattern dominance? What if a more general pattern precedes a specific one?

**Candidate**: The compiler checks for unreachable patterns — if a pattern dominates (matches a superset of) a later pattern, it's a compile error. For example, `case String` dominates `case String s when s.length() > 0`. The compiler catches this: "Pattern is dominated by previous pattern."

**Interviewer**: What's the `yield` keyword in switch expressions?

**Candidate**: `yield` returns a value from a switch branch that has a block body (with braces). `yield` is like `return` but specifically for switch branches. Before Java 14, `break value` was used (in preview). Arrow syntax `->` returns the expression directly without `yield`.

**Interviewer**: How does `switch` handle null in Java 21?

**Candidate**: In Java 17+, switch expressions throw `NullPointerException` for null. In Java 21, you can add `case null` explicitly. If no `case null` exists and null is passed, it throws NPE. `default` does NOT match null unless there's a `case null`.

**Interviewer**: Final: Compile-time vs runtime cost of pattern matching.

**Candidate**: At compile time, the compiler verifies exhaustiveness and dominance. At runtime, switch with type patterns uses `invokedynamic` — the bootstrap method (at runtime) generates efficient dispatch logic. Instanceof with patterns is like regular `instanceof` with a variable declaration. Record patterns use component accessors. Overall overhead is minimal compared to manual `instanceof` chains with casts.

---

## Feedback

**Strengths**:
- Comprehensive JSON node pattern matching
- Understands exhaustiveness, guarded patterns, dominance
- Knows null handling and yield semantics
- Compile-time vs runtime analysis

**Areas for Improvement**:
- Could discuss `when` vs `&&` for guarded conditions
- Might mention type inference in record patterns with `var`

**Score**: 5/5 — Master of pattern matching
