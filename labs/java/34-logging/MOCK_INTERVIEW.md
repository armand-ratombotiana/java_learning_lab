# Mock Interview Transcript: Logging

## Interviewer: Senior SWE, Microsoft
## Candidate: Mid-level Java developer
## Time: 25 minutes
## Focus: Logging frameworks, best practices, MDC, performance

---

**Q1: Compare SLF4J, Log4j 2, Logback, and java.util.logging.**

**Candidate**: SLF4J is a facade, not an implementation — it provides a unified API that delegates to Logback, Log4j 2, or java.util.logging. Logback is the most popular implementation (same author as Log4j 1). Log4j 2 is the successor to Log4j 1 with better performance, asynchronous logging, and zero-garbage logging. java.util.logging (JUL) is built-in but rarely used directly.

**Interviewer**: Why is SLF4J better than directly using a logging framework?

**Candidate**: (1) Library independence — libraries can use SLF4J without dictating the logging implementation. (2) The application chooses the implementation at deploy time. (3) Prevents classpath conflicts from different logging libs. (4) Easy migration — swap implementations by changing the classpath.

**Interviewer**: What is parameterized logging? Why is it important?

**Candidate**: 
```java
// Bad — expensive even when debug is disabled
log.debug("User " + user + " logged in from " + ip);

// Good — lazy evaluation, no allocation if debug is off
log.debug("User {} logged in from {}", user, ip);
```
With parameterized logging, the message template is checked against the log level first. Only if the level is enabled are the arguments concatenated. This avoids string concatenation and StringBuilder allocation for disabled log levels.

**Interviewer**: What is MDC (Mapped Diagnostic Context)?

**Candidate**: MDC is a thread-local map for contextual information:
```java
// In the request filter
MDC.put("requestId", UUID.randomUUID().toString());
MDC.put("userId", authenticatedUser.id());

// In the log pattern: %X{requestId} %X{userId}
// Output: 2024-03-15 10:30:00 [a1b2c3] [user42] ...
```
MDC is automatically included in every log message from that thread. In async logging, MDC must be explicitly propagated (Logback's `AsyncAppender` handles this).

**Interviewer**: How does asynchronous logging work? What are the trade-offs?

**Candidate**: Async logging uses a separate thread to write log events. The application thread writes to a bounded queue (disruptor for Log4j 2, ArrayBlockingQueue for Logback). The background thread consumes and writes. Benefits: (1) App thread returns immediately — no I/O wait. (2) Higher throughput under load. (3) Less application thread blocking. Drawbacks: (1) Events may be lost if the JVM crashes before the queue is flushed. (2) Memory overhead for the queue. (3) Context switching overhead.

**Interviewer**: How do you avoid logging sensitive data?

**Candidate**: (1) Use `log.isDebugEnabled()` checks for expensive data serialization. (2) Implement custom converters that mask sensitive fields (emails, SSNs, passwords). (3) Use Log4j 2's `RegexReplacement` converter. (4) Never log request/response bodies in production. (5) Audit your logging configuration regularly. (6) Use structured logging (JSON) with PII filtering.

**Interviewer**: Define the standard logging levels and when to use each.

**Candidate**: `ERROR` — something is broken, needs immediate attention (exceptions, external service failures). `WARN` — something unexpected but not critical (deprecated API usage, approaching rate limit). `INFO` — important business events (user signup, order placed). `DEBUG` — detailed diagnostic information for developers. `TRACE` — very fine-grained (rarely used). Tip: In production, typically set to INFO or WARN. Use `-Dlogging.level.com.example=DEBUG` for specific packages during debugging.

---

## Feedback

**Strengths**:
- Clear comparison of logging frameworks
- Explains SLF4J value proposition
- Correct MDC understanding
- Good async logging trade-off analysis

**Areas for Improvement**:
- Could discuss Log4j 2's garbage-free logging (reuse objects)
- Mention structured logging (JSON format for log aggregation)

**Score**: 3.5/5 — Solid logging knowledge
