# Security Considerations for Locking

## Lock Hijacking
An attacker who can acquire a lock and hold it indefinitely causes denial of service. Use `tryLock(timeout)` instead of `lock()` to bound wait time. For synchronized blocks, there's no timeout — the thread waits forever.

## Lock Ordering Attacks
If lock acquisition order is not consistent across threads, an attacker can trigger deadlock by arranging thread interleaving. Enforce strict lock ordering (e.g., always lock resource A before B) and document the order. Use `ThreadMXBean.findDeadlockedThreads()` for runtime detection.

## Thread Interruption and Resource Leaks
If a thread holding a lock is interrupted, it must release the lock before exiting. Use try-finally to guarantee release. An attacker can send interruption signals to cause lock leaks if the code doesn't handle InterruptedException properly.

## Reflection-Based Lock Access
An attacker with reflection access can acquire private locks: `LockSupport.unpark(thread)` can be called to release a thread from park, or `Unsafe.monitorEnter(object)` can acquire a synchronized block without using the synchronized keyword. Restrict reflection access with `--add-opens` module flags in production.

## Thread Injection via ThreadLocal
Attackers who can execute code on worker threads (e.g., via expression evaluation plugins) can access ThreadLocal variables containing sensitive data from previous tasks. Always call `ThreadLocal.remove()` after processing.

## StampedLock Validation Spoofing
An attacker who can control the version counter in StampedLock (though difficult) could cause `validate()` to return true for inconsistent data. Protect StampedLock instances from untrusted access.

## CAS and the ABA Problem
In CAS-based data structures, the ABA problem can cause incorrect state transitions. An attacker could exploit this by modifying memory through other means (e.g., Unsafe) to create A→B→A transitions. Use `AtomicStampedReference` or `AtomicMarkableReference` for pointer structures.
