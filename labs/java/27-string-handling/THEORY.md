# Theory: String Handling

## String Immutability
Strings in Java are immutable. Once created, a String's value cannot change. All "modification" operations return a new String. This enables:
- **String pool**: JVM can cache String literals
- **Thread safety**: Strings can be shared without synchronization
- **Security**: Strings used for passwords, class names, etc. cannot be modified
- **Hash caching**: String's hashCode is cached after first computation

## String Pool
The String pool is a JVM-managed collection of unique String literals. When a String literal is used, the JVM checks the pool first:
- If found, the pooled reference is returned
- If not found, the String is interned and added to the pool
`String.intern()` manually adds Strings to the pool.

## StringBuilder vs StringBuffer
Both provide mutable String sequences. `StringBuffer` is synchronized (thread-safe), `StringBuilder` is not. In single-threaded contexts, `StringBuilder` is faster. Use `StringBuffer` only when sharing a mutable string across threads.

## Text Blocks (Java 13+)
Text blocks provide multi-line string literals using triple quotes:
```java
String json = """
    {
        "name": "Alice",
        "age": 30
    }
    """;
```
The compiler processes the text block by removing common leading whitespace and processing escape sequences.

## String Formatting
Java provides multiple formatting options:
- `String.format()` / `String.formatted()` — C-style printf formatting
- `java.util.Formatter` — More control over formatting
- `java.text.MessageFormat` — Pattern-based with indexed arguments
- `java.text.DecimalFormat` — Number-specific formatting
- `java.time.format.DateTimeFormatter` — Date/time formatting

## Regular Expressions
Java's regex support via `java.util.regex.Pattern` and `Matcher` provides:
- Pattern compilation and caching
- Match, find, replace, split operations
- Named capturing groups (Java 7+)
- Performance optimizations through Pattern.compile() caching
