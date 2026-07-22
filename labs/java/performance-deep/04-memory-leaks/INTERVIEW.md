# Interview Questions: Memory Leaks

## Company-Specific Focus

### Google
- Memory leak: objects no longer needed but still reachable from GC roots
- Leak detection: heap dump analysis with MAT (Memory Analyzer Tool)
- Common causes: ThreadLocal, forgotten listeners, caches, static collections

### Microsoft
- Memory leaks in Java vs .NET: similar patterns
- Weak references: WeakReference, SoftReference, PhantomReference

### Amazon
- ThreadLocal leaks: ThreadLocal objects not removed, tied to thread lifecycle
- Application server deployment: ClassLoader leaks from redeployment
- Leak detection in production: heap dump + MAT analysis

### Meta
- Static collections: HashMap or ArrayList in static field growing unbounded
- Cache without eviction: HashMap used as cache without size limit
- Listener/callback registration: registering but never unregistering listeners

### Apple
- WeakHashMap: use for caches where keys have weak references
- SoftReference: memory-sensitive caches
- Cleaner: registering cleanup actions for unreachable objects

### Oracle
- Reference classes: WeakReference, SoftReference, PhantomReference, Cleaner
- Heap dump analysis: jmap, jhat, jvisualvm
- Leak suspects: MAT can identify leak suspects automatically

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — memory leaks are a production issue) |
| 146 LRU Cache | Medium | Google, Amazon, Apple | Cache eviction prevents memory leak |

## Real Production Scenarios
- **LinkedIn**: ThreadLocal memory leak in web application - user session data never cleared
- **Uber**: Static HashMap cache without eviction caused OOM after 48 hours
- **Netflix**: ClassLoader leak from redeployment - PermGen filled up, Full GC loop

## Interview Patterns & Tips
- **GC roots**: stack variables, static fields, JNI references, active threads
- **DOM**: Dominator tree in MAT for leak detection
- **ThreadLocal**: always remove() after use, especially in thread pools
- **Listeners**: unregister in a finally block or use WeakReference

## Deep Dive Questions
- **GC roots**: What are all types of GC roots?
- **ThreadLocal leak**: Why does ThreadLocal cause memory leaks in thread pools?
- **ClassLoader leak**: How does redeployment cause ClassLoader leaks?
- **WeakHashMap**: How does WeakHashMap prevent memory leaks?
- **Dominator tree**: How does the dominator tree help find leaks in MAT?