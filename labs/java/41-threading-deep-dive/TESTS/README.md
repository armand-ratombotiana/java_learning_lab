# Tests for Threading Deep Dive

## Test Categories

### Unit Tests (ThreadingDeepDiveTest.java)
- `testThreadLifecycleStates()` — verifies NEW, RUNNABLE, TERMINATED state transitions
- `testCompletableFutureThenApply()` — verifies synchronous transformation pipeline
- `testCompletableFutureAllOf()` — verifies allOf waits for all futures
- `testForkJoinSum()` — validates RecursiveTask parallel sum correctness
- `testRejectionHandler()` — ensures AbortPolicy throws RejectedExecutionException

### Running Tests
```bash
mvn test -pl labs/java/41-threading-deep-dive
```

### Expected Output
All tests pass with green bar. ForkJoin sum test handles up to 50K integers.

### Test Design Principles
- Each test is independent and has a single assertion focus
- Thread-based tests include timeout handling via join(timeout)
- Tests avoid real I/O or network dependencies
- Race conditions are mitigated with join() and latch synchronization

### Coverage Targets
- Thread lifecycle: 3 state transitions verified
- CompletableFuture: thenApply, allOf, exceptionally, thenCompose
- ForkJoinPool: RecursiveTask correctness and steal count
- ThreadPoolExecutor: core/max pool configuration, rejection handling
