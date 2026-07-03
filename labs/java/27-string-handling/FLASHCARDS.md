# Flashcards: String Handling

## Card 1: String Immutability
Once created, a String cannot be changed. Operations return new Strings.

## Card 2: StringBuilder vs StringBuffer
StringBuilder: faster, not thread-safe. StringBuffer: slower, thread-safe.

## Card 3: Text Blocks
Multi-line strings with triple quotes `"""`. Removes common leading whitespace.

## Card 4: String Pool
JVM caches String literals. `intern()` adds Strings to the pool.

## Card 5: String.format
C-style formatting: `String.format("Hello %s", name)`

## Card 6: Regex Pattern Compilation
Cache patterns: `Pattern.compile()` is expensive. Use `static final` fields.

## Card 7: strip() vs trim()
`strip()` removes Unicode whitespace (Java 11). `trim()` removes ASCII whitespace only.

## Card 8: StringJoiner
Joins strings with delimiter, prefix, suffix. `new StringJoiner(", ", "[", "]")`
