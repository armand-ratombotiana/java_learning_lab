# Virtual Threads - Learning Path

## Beginner
- Understand virtual vs platform threads
- Create virtual threads with Thread.ofVirtual()
- Learn when to use virtual threads
- Understand carrier threads

## Intermediate
- Migrate from thread pools
- Use Executors.newVirtualThreadPerTaskExecutor()
- Handle thread-local variables
- Debug virtual thread issues

## Advanced
- Structured concurrency patterns
- Thread.ScopedValue for context
- Pinning and virtual threads
- Performance tuning

## Key Concepts
- Platform threads
- Structured concurrency
- Carrier threads
- Scoped values

## Dependencies
- Java 21+ (virtual threads)
- Loom-compatible JVM

## Assessment
- Migrate an existing thread pool to virtual threads
- Implement structured concurrency with try-with-resources
- Debug pinning issues when using synchronized