# Garbage Collection Theory

## Why Garbage Collection?
Manual memory management (malloc/free) causes two classes of bugs:
- Memory leaks: forgetting to free memory
- Dangling pointers: freeing memory that's still referenced

GC eliminates both by automatically reclaiming memory that is no longer reachable.

## Reachability
An object is "live" if it's reachable from a GC root. GC roots include:
- Stack references (local variables in active methods)
- Static references (class static fields)
- JNI global references (native code references)
- Thread references (active thread objects)

The transitive closure of references from roots defines the live object set. Everything else is garbage.

## Mark-Sweep Algorithm
1. **Mark**: Trace from roots, marking all reachable objects
2. **Sweep**: Scan the heap, reclaim unmarked objects
3. **Compact** (optional): Move objects to eliminate fragmentation

## Generational Hypothesis
Most objects die young. The generational hypothesis states that:
- ~90% of objects are garbage before the next collection
- Objects that survive one collection tend to survive longer

Generational collectors divide the heap into young and old generations:
- Young generation: frequent collections (minor GC) for short-lived objects
- Old generation: infrequent collections (major GC) for long-lived objects

## GC Algorithms in Java
| Collector | Algorithm | Parallel | Concurrent | Pause Target |
|-----------|-----------|----------|------------|--------------|
| Serial | Mark-Sweep-Compact | No | No | Short |
| Parallel | Mark-Sweep-Compact | Yes | No | Throughput |
| G1 | Region-based + SATB | Yes | Yes | Configurable |
| ZGC | Colored pointers + Load barriers | Yes | Yes | < 1ms |
| Shenandoah | Brooks pointers | Yes | Yes | < 1ms |

## GC Phases (Generational)
- **Minor GC**: Collect young generation only (fast, frequent)
- **Major GC**: Collect old generation (slower, less frequent)
- **Full GC**: Collect entire heap (slowest, stop-the-world)
