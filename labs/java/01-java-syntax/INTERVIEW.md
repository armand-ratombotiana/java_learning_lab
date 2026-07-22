# Interview Questions: Java Syntax

## Company-Specific Focus

### Google
- Language fundamentals as building blocks for complex systems; expects precise understanding of JVM memory model for primitives vs objects
- Tricky questions on autoboxing in loops causing hidden allocation overhead
- Multiplication vs bit-shifting micro-optimizations in high-frequency trading contexts

### Microsoft
- Syntax differences in Java vs C#; questions on cross-language migration patterns
- `var` keyword in Java 10+ vs implicit typing in C#
- Byte-level operations for protocol parsing in Azure IoT scenarios

### Amazon
- Primitive vs Object memory overhead in high-scale cloud services
- Syntax-level performance considerations for cost-efficient computing
- Fail-fast vs fail-safe iteration semantics with concurrent modification

### Meta
- String concatenation in loops: `+` vs `StringBuilder` and the impact on GC
- Enhanced for-each loop with `Iterable`: performance considerations
- Java 17 (LTS) vs Java 21 features in production

### Apple
- Immutability enforcement through syntax: `final` fields, unmodifiable wrappers
- Memory-efficient idioms: primitives over wrappers, array over ArrayList
- Annotation syntax and retention policies

### Oracle
- Java Language Specification (JLS) sections relevant to each syntax element
- `switch` expressions vs statements: evolution from JEP 325 to JEP 420
- JVM specification compliance: exactly how `javac` compiles each syntax element
- Language evolution: from Java 1.0 syntax to modern Java (preview features)

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 150 Evaluate Reverse Polish Notation | Medium | Amazon, Google | Token parsing, string-to-integer conversion |
| 227 Basic Calculator II | Medium | Facebook, Amazon | Parsing expressions with operator precedence |
| 8 String to Integer (atoi) | Medium | Microsoft, Apple | State machine, overflow handling, whitespace/characters |
| 282 Expression Add Operators | Hard | Google, Meta | Recursive parsing, operator precedence |
| 224 Basic Calculator | Hard | Facebook, Amazon, Apple | Expression parsing with parentheses |

## Real Production Scenarios
- **Amazon DynamoDB team**: Integer overflow bug in token-based pagination that caused 5-hour outage — root cause was unsigned comparison emulation
- **Google**: Legacy code migration from Java 8 to Java 17 uncovered `T` <-> `? super T` incompatibility in generic methods with varargs — required 200+ code changes
- **Uber**: Autoboxing in big-data pipeline caused 2TB memory increase in production — hidden allocation through implicit `Integer.valueOf()` call in loop

## Interview Patterns & Tips
- **Recognize the pattern**: Syntax questions are often disguised as performance or correctness problems
- **Time complexity**: Syntax-level decisions (e.g., using a for-each loop vs indexed loop for a list) usually O(n) but constant factors matter
- **Space complexity**: `String.split()` generates an array of new strings; if processing many records, consider `Pattern.split()` instead
- **Java-specific gotcha**: The default `toString()` of an array returns a weird string; always use `Arrays.toString()` for debugging
- **Gotcha**: Autoboxing of `int` in collections uses `Integer.valueOf()` with an internal cache for -128 to 127 — outside this range, new objects are created
- **Gotcha**: The `++` prefix/postfix operator in Java is not atomic, even for `int` or `long`, without synchronization

## Deep Dive Questions
- **JVM/bytecode**: What bytecode instructions are generated for `String x = "hello" + name;`? Show the `invokevirtual` for StringBuilder.append
- **Memory model**: How does the JVM ensure visibility when a `volatile` variable is read? What is the store-load barrier ordering?
- **Compiler optimization**: Under what conditions will the JIT compiler inline a getter method? What are the default-XX:InlineSmallCode and -XX:FreqInlineSize settings?
- **Java 21+ features**: How does pattern matching in `switch` (JEP 441) change the way you handle null in switch expressions? How does the compiler resolve exhaustiveness?
- **Concurrency**: If you have a class with only `final` fields that are all immutable objects, is it automatically thread-safe? Explain with reference to the JMM
