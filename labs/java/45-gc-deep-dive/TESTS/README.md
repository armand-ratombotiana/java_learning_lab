# Tests for GC Deep Dive

## Test Categories

### Unit Tests (GcDeepDiveTest.java)
- `testGcComparisonRuns()` — verifies GC comparison demo runs without error
- `testG1Allocation()` — verifies G1 demo allocates and reports stats
- `testZgcAllocation()` — verifies ZGC pattern demo runs without error
- `testGcRoots()` — verifies static and stack root demonstrations
- `testGcLogging()` — verifies ManagementFactory GC bean access

### Running Tests
```bash
mvn test -pl labs/java/45-gc-deep-dive
```

### Test Considerations
- GC tests are inherently non-deterministic — test for success/no-error only
- Avoid asserting specific collection counts (varies by JVM and environment)
- `System.gc()` is a hint, not a guarantee — don't rely on it for test assertions
- Tests should work with any GC collector (Serial, Parallel, G1, ZGC, Shenandoah)

### Additional Test Scenarios
- Verify `GarbageCollectorMXBean` returns non-null collection count
- Verify GC notification listener receives events
- Verify allocation pattern doesn't throw OutOfMemoryError prematurely
- Verify GC root references are discoverable via heap dump analysis tools

### Coverage Targets
- GC MXBean: non-null, collection count accessible
- G1 allocation: creates region-crossing object graphs
- ZGC allocation: many short-lived objects with survivors
- GC roots: demonstrates stack, static, and JNI reference types
