# Interview Questions: String Handling

## Q1: Why are Strings immutable in Java?
Security (class loading, network connections), thread safety (no synchronization needed), String pool optimization (safe reference sharing), hashCode caching (computed once, stored permanently).

## Q2: How does the String pool work?
When a String literal is used, the JVM checks the pool. If found, returns the pooled reference. If not, creates a new String in the pool. `String.intern()` manually adds heap Strings. The pool lives in the permanent generation (Java 7-) or heap (Java 8+).

## Q3: StringBuilder vs StringBuffer?
Both are mutable character sequences. StringBuffer methods are synchronized, making it thread-safe but slower. StringBuilder is not synchronized, making it faster. Use StringBuilder in single-threaded contexts.

## Q4: How does the + operator work for strings?
The compiler optimizes `+` concatenation into `StringBuilder.append()` chains. However, in loops, each iteration may create a new StringBuilder, making explicit StringBuilder more efficient.

## Q5: What are text blocks?
Multi-line string literals delimited by triple quotes `"""`. They preserve indentation (removing common leading whitespace) and support escape sequences. Finalized in Java 15.

## Q6: How do you optimize string concatenation?
Use StringBuilder for multiple concatenations, especially in loops. Set initial capacity if known. Avoid StringBuffer unless synchronization is needed. Use String.join() for delimited collections.

## Q7: Explain regex performance considerations.
Compile patterns once and cache them. Use `Pattern.compile(regex)` rather than `String.matches(regex)` which compiles every call. Be aware of catastrophic backtracking (ReDoS). Use possessive quantifiers (`*+`, `++`) when possible.

## Q8: What string methods were added in Java 11?
`isBlank()` (true if empty or whitespace), `lines()` (Stream of lines), `strip()`/`stripLeading()`/`stripTrailing()` (Unicode whitespace), `repeat(int)` (repeated string).
