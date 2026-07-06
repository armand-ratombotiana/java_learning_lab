# Tests for JVM Tuning & Optimization

## Test Categories

### Unit Tests (JvmTuningTest.java)
- `testHeapAllocation()` — verifies HeapSizingDemo.allocate() returns positive time
- `testCodeCache()` — verifies CodeCacheDemo generates and exercises proxy methods
- `testStringDedupWithIntern()` — verifies String.intern() returns same reference for identical content
- `testJvmFlagReporter()` — verifies JvmFlagReporter reads JVM flags without error

### Running Tests
```bash
mvn test -pl labs/java/46-jvm-tuning
```

### Test Environment Notes
- Tests run with the default JVM configuration (may differ from optimal tuning)
- CodeCacheDemo uses dynamic proxies — may fail if code cache is full (unlikely at test scale)
- MetaspaceDemo requires the Java Compiler API (JDK, not JRE)
- Flags reported by JvmFlagReporter are those of the test JVM, not necessarily optimal

### Additional Test Scenarios
- Verify heap allocation in different sizes produces measurable timing differences
- Verify String.intern() reduces memory for duplicate strings
- Verify MetaspaceDemo can generate at least one class
- Verify JvmFlagReporter identifies the current GC collector

### Coverage Targets
- Heap: allocation timing for varying sizes
- Code cache: proxy generation and invocation
- String dedup: interning guarantees reference equality
- Flag reporting: reads input arguments and system properties
