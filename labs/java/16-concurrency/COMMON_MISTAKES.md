# Common Mistakes — Concurrency

## 1. Missing `unlock()` in finally
```java
lock.lock();
// code throws exception — lock never released!
lock.unlock();
```
**Fix:** Always use try/finally.

## 2. Double-Checked Locking (without volatile)
```java
private static MySingleton instance;
public static MySingleton getInstance() {
    if (instance == null) {        // Read without memory barrier
        synchronized (MySingleton.class) {
            if (instance == null) {
                instance = new MySingleton();
            }
        }
    }
    return instance;
}
```
**Fix:** `instance` must be `volatile` or use holder class pattern.

## 3. Thread-Safe Collection ≠ Thread-Safe Usage
```java
if (!map.containsKey(key)) { map.put(key, value); } // Still a race!
```
**Fix:** Use `ConcurrentHashMap.computeIfAbsent()`.

## 4. Deadlock with Nested Locks
Always acquire locks in a consistent global order.

## 5. Calling `Thread.run()` Instead of `start()`
`.run()` executes on the calling thread synchronously.

## 6. Swallowing InterruptedException
```java
catch (InterruptedException e) { /* ignore */ } // Burying the signal
```
**Fix:** Restore the interrupted flag: `Thread.currentThread().interrupt();`
