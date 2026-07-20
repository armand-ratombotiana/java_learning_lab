# Mini Spark - Code Deep Dive

## Implementation Details

This project demonstrates modern Java 21+ features including records for value objects, sealed classes where applicable, pattern matching, and comprehensive use of the java.util.concurrent package.

### Key Code Patterns

- **Records**: Used for all value objects (DTOs, configs, results) providing automatic equals/hashCode/toString
- **ConcurrentHashMap**: Primary storage for thread-safe data access
- **CopyOnWriteArrayList**: Thread-safe iteration for audit logs and event lists
- **AtomicLong/LongAdder**: Lock-free counters for statistics and ID generation
- **Optional**: Null-safe return values indicating presence/absence
- **Records as DTOs**: Immutable data carriers with validation in compact constructor

### Error Handling

All components throw IllegalArgumentException for invalid inputs, IllegalStateException for invalid operations, and NoSuchElementException for missing entities. Custom exceptions extend RuntimeException for unchecked exception handling.

### Testing Strategy

- JUnit 5 with parameterized tests for edge cases
- @BeforeEach setup for clean test state
- Comprehensive assertion coverage for success and failure paths
- Mock implementations for external dependencies
- Concurrent access testing where applicable
