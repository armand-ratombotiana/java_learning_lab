# Memory Model — Ultra Deep Dive

## 1. JSR-133 Happens-Before: The Formal Foundation

The Java Memory Model (JMM), defined in JSR-133 (Java 5), is based on the concept of **happens-before** ordering. The JMM §17.4.5 defines happens-before as a partial order on actions.

### The Happens-Before Rules

1. **Program order rule**: Each action in a thread happens-before every subsequent action in that thread (intra-thread semantics)
2. **Monitor lock rule**: An unlock on a monitor happens-before every subsequent lock on the same monitor
3. **Volatile variable rule**: A write to a volatile field happens-before every subsequent read of that same field
4. **Thread start rule**: A call to `Thread.start()` happens-before any action in the started thread
5. **Thread termination rule**: Any action in a thread happens-before another thread detects its termination (via `Thread.join()` or `Thread.isAlive()`)
6. **Interruption rule**: A call to `thread.interrupt()` happens-before the interrupted thread detects the interruption
7. **Finalizer rule**: The end of a constructor for an object happens-before the start of the finalizer for that object
8. **Transitivity**: If `A happens-before B` and `B happens-before C`, then `A happens-before C`

### Formal Definition

The JMM defines a **causality** requirement: executions must be "happens-before consistent." For any read of a variable `r`, you must be able to see a write `w` such that:
- `w happens-before r`, OR
- There's no intervening write `w'` such that `w happens-before w' happens-before r`

### Example: The Classic Race

```java
int x = 0, y = 0;
// Thread 1:          // Thread 2:
x = 1;                 y = 1;
y = 2;                 x = 2;
```

**Possible results**:
- `(x=1, y=1)` — both writes from Thread 1 are visible to Thread 2? NO!
- `(x=2, y=1)` — possible
- `(x=1, y=2)` — possible
- `(x=2, y=2)` — possible
- `(x=0, y=0)` — possible (no happens-before!)

Without synchronization, there is NO guaranteed visibility.

## 2. Volatile Semantics in Depth

### The volatile Contract

```java
private volatile boolean flag = false;

// Thread 1:
flag = true;  // Volatile write

// Thread 2:
if (flag) {   // Volatile read
    // Guaranteed to see Thread 1's write (and all prior writes)
}
```

A volatile field guarantees:
1. **Visibility**: A write to a volatile is visible to all subsequent reads (of the same field)
2. **Atomicity**: Reads and writes of 32-bit volatile primitives are atomic (64-bit `long`/`double` also atomic with volatile)
3. **Ordering**: All actions before a volatile write in program order happen-before the volatile write, and all actions after a volatile read happen-after the volatile read

### volatile Memory Barriers

The JIT compiler inserts memory barriers around volatile accesses:

```java
// Volatile write:
flag = true;

// Inserted barriers:
StoreStore();  // Ensure all prior stores are visible before this store
StoreLoad();   // Prevent this store from being reordered with subsequent loads

// Volatile read:
if (flag) { }

// Inserted barriers:
LoadLoad();    // Ensure all prior loads are complete before subsequent loads
LoadStore();   // Prevent this load from being reordered with subsequent stores
```

### x86 Implementation

On x86 (which has a strongly-ordered memory model), the only barrier needed is `StoreLoad` for volatile writes:

```java
// x86 volatile write:
mov [flag], 1      // Store
mfence              // StoreLoad barrier (full memory fence)
// No barriers needed for volatile reads on x86!
```

On ARM/PowerPC (weakly-ordered), ALL four barriers may be needed.

## 3. Final Field Guarantees

### The Final Field Freeze

The JMM guarantees that final fields are properly initialized when the constructor completes:

```java
class FinalFieldExample {
    final int x;
    static FinalFieldExample instance;
    
    public FinalFieldExample() {
        x = 42;              // Write to final field
    }                        // "Freeze" point for final fields
    
    static void writer() {
        instance = new FinalFieldExample();
    }
    
    static void reader() {
        if (instance != null) {
            int r = instance.x;  // Guaranteed to see 42!
        }
    }
}
```

**The freeze**: The constructor's return point acts as a "freeze" action for all final fields. Any thread that sees a reference to a properly-constructed object is guaranteed to see correct values for all final fields.

### What "Properly Constructed" Means

```java
class UnsafeFinal {
    final int x;
    static UnsafeFinal instance;
    
    UnsafeFinal() {
        x = 42;
        instance = this;  // THIS IS UNSAFE! Object escapes before constructor ends
    }
}
```

If a reference to the object is published before the constructor completes (the "this reference escape" anti-pattern), the final field guarantee is void. The reader may see the default value (0) instead of 42.

### Final Field in JMM

The JMM provides special semantics:
1. A write to a final field in a constructor is not reordered with the freeze action (the constructor's return)
2. A load of a reference to an object with final fields is not reordered with loads of the final fields from that object

This effectively inserts a `LoadLoad` barrier between reading the object reference and reading final fields.

## 4. Out-of-Order Execution — The Hardware Reality

### Why CPUs Reorder

Modern CPUs execute instructions **out-of-order** (OoO) to maximize instruction-level parallelism:

```java
// Source order:
x = 1;    // Store
y = 2;    // Store (independent of x)
z = x + y;  // Dependent on both

// Possible execution order (x86 with store buffer):
mov [y], 2    // Store y FIRST (while x's store is buffered)
mov [x], 1    // Then store x
mov eax, [x]  // Read x
add eax, [y]  // Add y
```

The CPU reorders the independent stores to `x` and `y`. This is invisible in a single thread (program order is preserved architecturally) but visible from another thread.

### Store Buffers

Each CPU core has a **store buffer** (write buffer):

```
Core 0:
  store [x]=1 → store buffer → L1d$ → L2$ → L3$ → RAM
  store [y]=1 → store buffer → L1d$ → ...
  
Core 1 can see [x] and [y] only AFTER they leave the store buffer
```

When a store is in the buffer, the core can continue executing while the store propagates. This is called "store-forwarding" — subsequent loads from the same core can see the buffered store without waiting for it to hit the cache.

### Store Buffer → Memory Fence

A `StoreLoad` fence (like `mfence` on x86 or `DMB` on ARM) drains the store buffer:
```
Before mfence:
  [store buffer: x=1, y=1]
  Core is executing future instructions

After mfence:
  [store buffer: empty]
  Stores are visible to all cores
  Core continues, but subsequent loads block until buffer drains
```

## 5. Memory Barriers in Detail

### The Four Barrier Types

| Barrier | What it prevents | x86 instruction | Cost |
|---------|-----------------|-----------------|------|
| LoadLoad | Load before load | None needed | 0 |
| StoreStore | Store before store | None needed | 0 |
| LoadStore | Load before store | None needed | 0 |
| StoreLoad | Store before load | `mfence`/`lock addl` | ~20-100 ns |

On x86, only `StoreLoad` requires an instruction. On ARM/PowerPC, all four may require instructions:
- ARM: `dmb` (data memory barrier) — can be configured for any combination
- PowerPC: `sync` or `lwsync` (lightweight sync)

### JMM Barrier Insertion

The C2 compiler applies the following barrier pattern for volatile accesses:

**Volatile read** (on x86):
```
  [LoadLoad]  (no-op on x86)
  [LoadStore]  (no-op on x86)
  actual load
```
(No-op on x86 — volatile reads are free!)

**Volatile write** (on x86):
```
  [StoreStore]  (no-op on x86)
  actual store
  [StoreLoad]   → mfence or lock addl [rsp], 0
```
(The `StoreLoad` is the expensive part.)

### The `VarHandle` API (Java 9+)

```java
VarHandle handle = MethodHandles.lookup()
    .findVarHandle(MyClass.class, "myField", int.class);
handle.setVolatile(this, 42);
int x = (int) handle.getVolatile(this);
```

`VarHandle` provides finer-grained access modes:
- `setVolatile` / `getVolatile`
- `setOpaque` / `getOpaque`
- `setRelease` / `getAcquire`
- Plain access (no guarantees)

## 6. Double-Checked Locking (DCL) — Fixed with volatile

### The Broken DCL (Pre-Java 5)

```java
class Singleton {
    private static Singleton instance;
    
    static Singleton getInstance() {
        if (instance == null) {           // Check 1 (unsafe)
            synchronized (Singleton.class) {
                if (instance == null) {   // Check 2 (safe)
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

**The problem**: The `instance = new Singleton()` operation is NOT atomic:
1. Allocate memory
2. Call constructor (initialize fields)
3. Set `instance` to the allocated address

Steps 2 and 3 can be **reordered** by the compiler or CPU. Thread 2 may see a non-null `instance` before the constructor completes, reading an uninitialized object.

### The Fixed DCL (Java 5+)

```java
class Singleton {
    private static volatile Singleton instance;  // volatile!
    
    static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

The `volatile` keyword prevents the reordering of the write to `instance` with the constructor's writes. The `StoreStore` barrier (implied by volatile write semantics) ensures the constructor completes before `instance` is published.

### Alternative: Initialization-on-Demand Holder Idiom

```java
class Singleton {
    private Singleton() {}
    private static class Holder {
        static final Singleton INSTANCE = new Singleton();
    }
    static Singleton getInstance() {
        return Holder.INSTANCE;
    }
}
```

This uses the JVM's class loading guarantee: a class is loaded and initialized exactly once, and no thread sees the class in an incomplete state. No `volatile` needed!

## 7. The `happens-before` Visualization

```
Thread 1                          Thread 2
─────────────────                 ─────────────────
write x = 1
                   ─────────────►
write flag = true (volatile)    
                   ─────────────► 
                                   read flag = true (volatile)
                                   ─────────────►
                                   read x = 1  (guaranteed!)

The volatile write → volatile read edge creates a happens-before
relationship that makes x=1 visible.
```

## 8. The JMM and `java.util.concurrent`

The entire `java.util.concurrent` package is built on JMM guarantees:

| Class | JMM Mechanism |
|-------|--------------|
| `ConcurrentHashMap` | `volatile` on Node arrays, `Unsafe.putOrderedObject` |
| `ReentrantLock` | `volatile` state + `Unsafe` for CAS |
| `AtomicInteger` | `Unsafe.compareAndSwapInt` (CAS, stronger than volatile) |
| `ThreadPoolExecutor` | `volatile` runState, `volatile` worker count |
| `CountDownLatch` | `AbstractQueuedSynchronizer` with volatile state |
| `CompletableFuture` | `volatile` result field |

CAS (Compare-And-Swap) provides **read-modify-write** atomicity:
```java
AtomicInteger ai = new AtomicInteger(0);
ai.compareAndSet(0, 1);  // Atomically: if ai == 0, set ai = 1
```

CAS has the same happens-before semantics as volatile — a successful CAS happens-before subsequent reads of the same field.

## 9. Sequential Consistency vs JMM

**Sequential consistency** (SC) is the intuitive model: programs execute as if all actions are in a single total order that respects program order. The JMM is **weaker** than SC.

### SC vs JMM Example

```java
// Initially: x = y = 0
Thread 1:      Thread 2:
x = 1;         y = 1;
r1 = y;        r2 = x;
```

SC impossible result: `r1 == 0 && r2 == 0`

JMM allows this result (both writes are buffered, both reads see the initial values). This is called a "four-cycle" behavior.

## 10. JMM and Final Fields in Records

Records (Java 16+) have `private final` fields. The JMM's final field guarantees apply:

```java
record Point(int x, int y) { }

// Proper publication:
Point p = new Point(1, 2);
// Any thread reading p is guaranteed to see x=1, y=2

// BUT: sharing via unsynchronized field still needs volatile:
// class Holder { Point p; } — this field must be volatile or synchronized
```
