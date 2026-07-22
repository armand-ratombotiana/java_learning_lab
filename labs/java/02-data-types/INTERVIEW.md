# Interview Questions: Data Types

## Company-Specific Focus

### Google
- Integer overflow behavior: how Java differs from C/C++ in signed overflow invariance
- `==` vs `.equals()` with Integer cache values (-128 to 127)
- Floating-point precision in financial calculations — why never use `float`/`double` for money

### Microsoft
- Type conversion and upcasting/downcasting in Java vs C#
- Value types: does Java have anything like C#'s `struct`? Comparison with records in Java 16+
- Nullable reference types — C# handles them; where does Java stand?

### Amazon
- Memory cost of objects vs primitives in high-scale services; object header overhead
- BigInteger usage for large number calculations in analytics systems
- String vs char[] for sensitive data: security implications in payment processing

### Meta
- Primitive array vs wrapper array memory consumption
- Cache-line effects of boolean arrays in high-frequency polling
- Preferring `int` over `long` when value fits: CPU register usage

### Apple
- Value-based classes in Java: `LocalDate`, `Optional` — immutability guarantees
- Equality semantics: custom `.equals()` implementing type-checks properly
- Tightly packing data for Java object memory efficiency

### Oracle
- JLS 4: Types, Values, and Variables; JVM specification on type representations
- What is a "primitive value" vs "reference value" in terms of JVM spec
- Evolution of types across Java versions: value types (Project Valhalla)
- How do type annotations (@NonNull, @Nullable) relate to the type system?

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 7 Reverse Integer | Easy | Google, Amazon | Overflow detection via comparison |
| 9 Palindrome Number | Easy | Apple, Adobe | Reversing half the number vs full string conversion |
| 202 Happy Number | Easy | Facebook, Microsoft | Cycle detection using HashSet of sums |
| 231 Power of Two | Easy | Google | Bit manipulation on primitive int |
| 268 Missing Number | Easy | Amazon, Apple | XOR gauss technique for finding missing int |

## Real Production Scenarios
- **Airbnb**: Integer overflow in timestamp calculation for reward expiry dates — production tickets expired years early
- **LinkedIn**: Float cumulative error in analytics — database storage of 2.01 as 2.00999999 ruined weekly reports
- **GitHub**: `==` Integer comparison bug in rest API — IDs larger than 127 caused duplicate entries in cache

## Interview Patterns & Tips
- **The Integer cache trap**: `Integer.valueOf(100) == Integer.valueOf(100)` is true, but `Integer.valueOf(200) == Integer.valueOf(200)` is false. Always use `.equals()` for object comparisons
- **Float/Double IEEE 754**: 0.1 + 0.2 != 0.3 — explain why rounding errors occur
- **Integer overflow is silent** in Java (C# can do `checked` blocks; Java can't)
- **Switch with String**: The compiler uses `hashCode()` + `equals()`. This handles collisions and is safe but not free
- **Unicode and char**: Java char is 16-bit; points above U+FFFF require two chars (surrogate pairs), use code point based methods instead
- **Bitwise & Logical**: `&` and `|` vs `&&` and `||` — the difference between bitwise AND and short-circuit logical AND

## Deep Dive Questions
- **JVM internals**: How are primitive types actually stored on the JVM stack vs the heap? Explain operand stack, local variable array, and object fields representation.
- **Memory model**: What is the object header structure? How much overhead does an `Integer` wrapper object impose over a plain `int`?
- **Class file format**: How are String literals stored in the constant pool? How does the CONSTANT_String_info differ from CONSTANT_Utf8_info?
- **JIT**: Can the JIT optimize escape-analyzed objects to have value-like behavior on stack?
- **Java 21+**: What would nullable primitive types look like if they were added to Java? (JEP draft related)
