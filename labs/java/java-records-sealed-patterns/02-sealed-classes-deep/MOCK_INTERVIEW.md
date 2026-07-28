# Mock Interview: Sealed Classes

**Interviewer:** "Design a type-safe JSON library using sealed classes."

**Candidate:** "I'd start with a sealed interface:

```java
sealed interface JsonValue
    permits JsonString, JsonNumber, JsonBool, JsonArray, JsonObject, JsonNull {}
```

Each permitted type is a record implementing `JsonValue`. This gives me:
1. **Exhaustive pattern matching** — a switch over `JsonValue` won't compile if I miss a case
2. **Immutability** — each node is a record
3. **No runtime surprises** — no unknown subclass can appear

For serialization, I'd add a `String toJson()` method and use record patterns:

```java
String toJson(JsonValue v) {
    return switch (v) {
        case JsonString(String s)  -> "\"" + escape(s) + "\"";
        case JsonNumber(double n)  -> String.valueOf(n);
        case JsonBool(boolean b)   -> String.valueOf(b);
        case JsonArray(var items)  -> items.stream().map(this::toJson).toList().toString();
        case JsonObject(var fields) -> ...
        case JsonNull _            -> "null";
    };
}
```

The compiler guarantees I handle every possible JSON value type."

**Interviewer:** "What if someone later wants to add `JsonBigDecimal`?"

**Candidate:** "That's the trade-off — they must modify the sealed interface to add a `permits` clause. If I foresee extension, I'd mark the subclass as `non-sealed`. But for a tightly controlled API, sealed types prevent users from accidentally creating invalid JSON nodes."
