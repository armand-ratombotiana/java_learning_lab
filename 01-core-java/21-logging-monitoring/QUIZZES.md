# Module 21: Logging & Monitoring - Quizzes

---

## Q1: Parameterized Logging
Why is parameterized logging (e.g., `logger.debug("Value: {}", val)`) preferred over string concatenation?

A) It allows formatting strings like `printf`.
B) It is evaluated only if the corresponding log level is enabled, improving performance.
C) It automatically encrypts the logged data.
D) It prevents exceptions from being thrown.

**Answer**: B
**Explanation**: Parameterized logging defers the cost of constructing the string until the logging framework verifies that the specific log level is active.

---

## Q2: Exception Logging
What is the correct way to log an exception with its stack trace using SLF4J?

A) `logger.error(e);`
B) `logger.error("Error occurred: " + e.getMessage());`
C) `logger.error("Error occurred", e);`
D) `logger.error("Error occurred: {}", e.getStackTrace());`

**Answer**: C
**Explanation**: Passing the `Throwable` as the last parameter to the logging method ensures the logging framework prints the full stack trace.

---

## Q3: SLF4J Purpose
What is the primary role of SLF4J in a Java application?

A) It is the fastest logging implementation available.
B) It acts as a facade, allowing developers to plug in different logging frameworks at deployment time.
C) It is a monitoring tool for the JVM.
D) It formats logs into JSON automatically.

**Answer**: B
**Explanation**: SLF4J (Simple Logging Facade for Java) is merely an abstraction layer. It delegates actual logging work to implementations like Logback or Log4j2.