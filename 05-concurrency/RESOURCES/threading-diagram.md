# Threading Concepts Diagram

Visual representations of Java concurrency concepts.

## Thread Lifecycle

```
                    ┌────────────────────────────────────┐
                    │           THREAD STATES             │
                    └────────────────────────────────────┘
                    
    ┌─────────┐      start()      ┌─────────────┐
    │  NEW    │ ──────────────→ │  RUNNABLE    │
    └─────────┘                  │              │
        │                         └──────┬───────┘
        │ exit()                        │
        │                         ┌─────▼──────────┐
        │                         │    RUNNING     │
        │                         │                │
        │                         │   (may yield   │
        │                         │    to scheduler)│
        │                         └─────┬──────────┘
        │                               │
        │                               ▼
        │                    ┌─────────────────┐
        │                    │ WAITING/BLOCKED  │
        │  notify()          │                  │
        │◄────────────────── │  - sleep()      │
        │  park()            │  - wait()       │
        │                    │  - join()       │
        │                    │  - blocking I/O  │
        │                    │  - lock.acquire()│
        │                    └─────────────────┘
        │                               │
        │                               ▼
    ┌───▼───────────┐          ┌────────────────┐
    │   TERMINATED  │          │  DEAD (done)   │
    └───────────────┘          └────────────────┘
```

## Synchronization Mechanisms

### Synchronized Keyword

```
┌─────────────────────────────────────────────────────────────┐
│                 synchronized (monitor lock)                  │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   Method Level:                                             │
│   ─────────────                                             │
│   public synchronized void method() {                        │
│       // entire method body is synchronized                 │
│   }                                                          │
│                                                              │
│   Block Level:                                               │
│   ───────────                                               │
│   public void method() {                                    │
│       synchronized(this) {                                  │
│           // only this block is synchronized               │
│       }                                                     │
│   }                                                          │
└─────────────────────────────────────────────────────────────┘
```

### ReentrantLock

```
┌─────────────────────────────────────────────────────────────┐
│                    ReentrantLock                            │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   Lock lock = new ReentrantLock();                          │
│                                                              │
│   lock.lock();                  // acquire                  │
│   try {                                                      │
│       // critical section                                   │
│   } finally {                                                │
│       lock.unlock();            // ALWAYS release          │
│   }                                                          │
│                                                              │
│   lock.tryLock() = non-blocking attempt                     │
│   lock.tryLock(timeout, unit) = timed attempt               │
│   lock.newCondition() = await/signal                        │
└─────────────────────────────────────────────────────────────┘
```

## Thread Communication

### Producer-Consumer Pattern

```
    ┌─────────────┐         ┌─────────────────┐         ┌─────────────┐
    │  PRODUCER   │ ──────→ │    BUFFER       │ ──────→ │  CONSUMER   │
    │             │         │   (BlockingQueue)│         │             │
    └─────────────┘         └─────────────────┘         └─────────────┘
           │                         │                         │
           │                         │                         │
           ▼                         ▼                         ▼
    put() when full            ┌─────────────────┐      take() when empty
    blocks/throws             │    capacity: N   │      blocks/throws
                              └─────────────────┘
```

### Wait/Notify Pattern

```java
// Producer
synchronized(obj) {
    while (buffer.full) {
        obj.wait();  // releases lock
    }
    buffer.add(item);
    obj.notify();     // wakes one waiting thread
}

// Consumer
synchronized(obj) {
    while (buffer.empty) {
        obj.wait();
    }
    item = buffer.remove();
    obj.notify();
}
```

## Executor Framework

```
┌─────────────────────────────────────────────────────────────┐
│                    EXECUTOR FRAMEWORK                       │
├─────────────────────────────────────────────────────────────┤
                                                              │
│   ┌──────────────┐      ┌──────────────┐      ┌───────────┐ │
│   │ Runnable/    │ ──→ │   Executor   │ ──→  │  Thread   │ │
│   │ Callable     │      │   Service    │      │  Pool     │ │
│   └──────────────┘      └──────────────┘      └───────────┘ │
│                                                              │
│   Thread Pool Types:                                         │
│   ───────────────                                            │
│   ┌─────────────────────────────────────────────────────┐    │
│   │ FixedThreadPool     │ Unlimited tasks, N threads   │    │
│   │ CachedThreadPool    │ 0 to Integer.MAX_VALUE       │    │
│   │ SingleThreadPool    │ 1 thread, queue tasks        │    │
│   │ ScheduledThreadPool │ Delayed/repeated tasks       │    │
│   └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

## Memory Consistency

### Happens-Before Guarantee

```
┌─────────────────────────────────────────────────────────────┐
│              HAPPENS-BEFORE RELATIONSHIPS                   │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   Thread start() ──────→ Thread actions                     │
│   Thread join()  ←────── Thread termination                 │
│   Lock acquire  ──────→ Lock release                       │
│   volatile write ──────→ volatile read                     │
│   Thread interruption ──→ InterruptedException             │
│   Field write ──────────→ Thread death                      │
└─────────────────────────────────────────────────────────────┘
```

### Race Condition Example

```
    Thread A                          Thread B
    ┌─────────┐                       ┌─────────┐
    │count++  │                       │count++  │
    │  │      │                       │  │      │
    │  ▼      │                       │  ▼      │
    │ READ 1  │                       │ READ 1  │
    │  │      │                       │  │      │
    │  ▼      │                       │  ▼      │
    │ INC 2   │                       │ INC 2   │
    │  │      │                       │  │      │
    │  ▼      │                       │  ▼      │
    │WRITE 2  │                       │WRITE 2  │
    └─────────┘                       └─────────┘
    
    Expected: count = 2
    Actual:   count = 1  (lost update!)
```
