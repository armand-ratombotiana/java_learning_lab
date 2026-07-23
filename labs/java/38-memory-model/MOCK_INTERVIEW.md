# Mock Interview Transcript: Memory Model

## Interviewer: Staff Engineer, Oracle
## Candidate: Senior Java developer
## Time: 45 minutes
## Focus: JMM, happens-before, barriers, reordering

---

**Q1: Define the Java Memory Model. What does it guarantee?**

**Candidate**: The JMM (defined in JLS §17.4) defines when one thread's writes are visible to another thread. It doesn't specify hardware behavior — it specifies legal program behaviors. The core guarantee is the happens-before relationship: if A happens-before B, then A is visible to B and A precedes B in order.

**Interviewer**: List all 8 happens-before rules.

**Candidate**: 
1. Program order — actions in a thread happen-before later actions in that thread.
2. Monitor lock — unlock happens-before subsequent lock on same monitor.
3. Volatile — write to volatile happens-before subsequent read of same volatile.
4. Thread start — `Thread.start()` happens-before any action in the started thread.
5. Thread join — last action in thread happens-before `Thread.join()` returns.
6. Transitivity — if A → B and B → C, then A → C.
7. Interruption — `interrupt()` happens-before detection of interruption.
8. Finalizer — constructor completion happens-before finalizer start.

**Interviewer**: Write code that demonstrates a data race fixable only with volatile.

**Candidate**: 
```java
class DataRace {
    int data = 0;
    volatile boolean flag = false;
    
    void writer() {
        data = 42;           // normal write
        flag = true;         // volatile write (release barrier)
    }
    
    void reader() {
        if (flag) {          // volatile read (acquire barrier)
            System.out.println(data);  // guaranteed to see 42
        }
    }
}
```
Without volatile on `flag`, the reader might see `flag == true` but `data == 0` (reordering).

**Interviewer**: What does a volatile read/write compile to on x86 vs ARM?

**Candidate**: On x86 (TSO — Total Store Order): volatile reads are essentially regular loads (x86 never reorders loads with other loads). Volatile writes are regular stores with a `StoreLoad` barrier (usually an `mfence` or `lock addl`). On ARM (weak ordering): volatile reads need load-load and load-store barriers (`dmb ishld`). Volatile writes need store-store and store-load barriers (`dmb ish`). ARM inserts more barriers because it allows more reorderings.

**Interviewer**: What happens with `final` fields in the JMM?

**Candidate**: The JMM guarantees that a thread seeing a reference to an object with `final` fields sees the correctly constructed values of those fields — even without synchronization. This is achieved by: (1) The compiler adds a `store-store` barrier after all `final` field stores in the constructor. (2) Reads of `final` fields don't need barriers because the barrier prevents the reference from being published before final fields are set. (3) Exception: if the `this` reference escapes during construction (e.g., passing `this` to another method), the guarantee is lost.

**Interviewer**: Prove the final field guarantee is needed with an example.

**Candidate**: 
```java
class Holder {
    final int x;
    Holder(int x) { this.x = x; }
}

static Holder holder;

void init() { holder = new Holder(42); }  // Thread 1
void print() { if (holder != null) System.out.println(holder.x); }  // Thread 2
```
Without `final`, the JIT could reorder: allocate `Holder`, publish `holder`, then set `x = 42`. Thread 2 might see `holder != null` but `holder.x == 0`. With `final`, this reordering is prohibited.

**Interviewer**: Explain the DCL pattern and why volatile is needed.

**Candidate**: Double-Checked Locking:
```java
private static volatile Singleton instance;
public static Singleton get() {
    if (instance == null) {                 // First check (unsynchronized)
        synchronized (Singleton.class) {
            if (instance == null) {          // Second check (synchronized)
                instance = new Singleton();  // Problem!
            }
        }
    }
    return instance;
}
```
Without `volatile`, the `instance = new Singleton()` can be reordered: allocate memory → assign reference → call constructor. Another thread sees non-null `instance` but the object isn't fully constructed. `volatile` prevents this reordering (store-store barrier).

**Interviewer**: Can you have a data race in a program that uses only `final` fields?

**Candidate**: Yes. If the reference to the object is published without proper synchronization (e.g., through an unsynchronized field), the reader might see a stale reference (null or different object). Final fields guarantee that IF you see the reference, the final fields are correctly constructed. They don't guarantee you see the reference.

**Interviewer**: Final question: How does `VarHandle` provide finer control?

**Candidate**: `VarHandle` (Java 9+) provides access modes: `get`, `set`, `getVolatile`, `setVolatile`, `getAcquire`, `setRelease`, `getOpaque`, `setOpaque`, `compareAndSet`, `getAndAdd`, etc. This allows the minimum necessary barrier for an operation, avoiding the full volatile barrier:

```java
VarHandle HANDLE = MethodHandles.lookup()
    .findVarHandle(MyClass.class, "value", int.class);

void setRelease(int v) { HANDLE.setRelease(this, v); }  // StoreStore only
int getAcquire() { return (int) HANDLE.getAcquire(this); }  // LoadLoad + LoadStore only
```

---

## Feedback

**Strengths**:
- All 8 happens-before rules with explanations
- x86 vs ARM barrier comparison
- Final field semantics with example
- DCL pattern and volatile requirement
- VarHandle access modes

**Areas for Improvement**:
- Could discuss `@Contended` and false sharing
- Mention causality tests in JLS

**Score**: 5/5 — Masterful JMM understanding
