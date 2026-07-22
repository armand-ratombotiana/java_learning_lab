# Interview Questions: JFR Streaming

## Company-Specific Focus

### Google
- JDK Flight Recorder (JFR): built-in, low-overhead event recording
- JFR streaming: continuous monitoring by consuming events as they're recorded
- Event types: GC, JIT compilation, allocation, thread, locks, I/O

### Microsoft
- JFR vs .NET Event Tracing/EventSource
- JMC (Mission Control): UI for analyzing JFR recordings

### Amazon
- Production monitoring: JFR in production with <1% overhead
- Custom events: defining application-specific JFR events
- JFR streaming: sending events to observability platforms

### Meta
- JFR recording: starting/stopping recordings via jcmd
- Event categories: GC, compiler, memory, threading, profiling
- JFR file format: recording structure and analysis

### Apple
- JFR on macOS: full support
- Low overhead: designed for always-on production profiling

### Oracle
- JFR: Java Flight Recorder (commercial feature made open source in JDK 11)
- JMC: JDK Mission Control for analyzing recordings
- JFR events: 100+ built-in event types
- Event streaming: jdk.jfr.consumer.RecordingStream

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — JFR is an observability tool) |

## Real Production Scenarios
- **Netflix**: JFR streaming to detect memory leaks in production without restarting
- **LinkedIn**: Custom JFR events for tracking request processing time in microservices

## Interview Patterns & Tips
- **Always-on**: JFR can run continuously in production with <1% overhead
- **Events**: over 100 built-in JFR events
- **Custom events**: extend jdk.jfr.Event for application-specific events
- **Streaming**: consume JFR events in real-time for monitoring

## Deep Dive Questions
- **Ring buffer**: How does JFR use a ring buffer for event recording?
- **Overhead**: Why is JFR overhead less than 1%?
- **File format**: What is the JFR file format structure?
- **Custom events**: How are custom JFR events defined and recorded?
- **Streaming API**: How does RecordingStream consume events?