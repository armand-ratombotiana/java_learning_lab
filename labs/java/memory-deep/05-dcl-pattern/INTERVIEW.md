# Interview Questions: Double-Checked Locking

## Company-Specific Focus

### Google
- Double-checked locking pattern: lazy initialization with reduced synchronization
- Classic bug: DCL without volatile allows publication of partially constructed object
- Fix: volatile ensures visibility of the fully constructed object

### Microsoft
- DCL in Java vs C#: C# also requires volatile for correct DCL
- Lazy initialization: Lazy<T> in C# provides thread-safe lazy init

### Amazon
- Singleton with DCL: thread-safe, lazy initialization
- Holder idiom: static inner class uses class loading guarantee for thread safety

### Meta
- JVM memory model: DCL bug was discovered when JMM was formalized in JSR 133
- Volatile fix: ensures happens-before between constructor and read

### Apple
- Enum singleton: simplest thread-safe singleton
- Holder pattern: Initialization-on-demand holder idiom

### Oracle
- JSR 133 (JMM) fixed DCL: volatile was needed before JSR 133 but was not sufficient
- Freeze: volatile ensures that the freeze action of the constructor is visible
- DCL alternatives: holder pattern, enum singleton, eager initialization

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — DCL is a concurrency pattern) |
| 146 LRU Cache | Medium | Google, Amazon, Apple | Singleton cache manager |

## Real Production Scenarios
- **Amazon**: DCL without volatile caused intermittent NPE in product metadata cache
- **Uber**: Migrating from DCL volatile singleton to holder pattern for better performance

## Interview Patterns & Tips
- **volatile required**: DCL without volatile is broken
- **Holder pattern**: Initialization-on-demand holder idiom is simpler
- **Enum singleton**: best singleton pattern (serialization safe, thread safe)

## Deep Dive Questions
- **DCL without volatile**: What exactly goes wrong?
- **JSR 133**: How did the JMM fix DCL?
- **Holder pattern**: How does the holder pattern achieve thread safety?
- **Enum singleton**: Why is the enum singleton thread-safe and serialization-safe?
- **Performance**: Is DCL with volatile faster than synchronized?