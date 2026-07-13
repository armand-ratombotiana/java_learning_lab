# Reference Type Internals

## 🗺️ How WeakHashMap Works
`WeakHashMap` is a highly misunderstood data structure. It is often used to prevent memory leaks in caches.

**The Mechanism**:
In a standard `HashMap`, the map holds a strong reference to both the Key and the Value.
In a `WeakHashMap`, the map holds a **Weak Reference** to the *Key*. (It holds a strong reference to the Value).

If the application drops all strong references to the Key object, the GC will notice that the Key is now only weakly reachable. 
During the next GC cycle:
1. The GC destroys the Key object.
2. The `WeakHashMap` detects that the Key was destroyed.
3. The `WeakHashMap` automatically removes the entire Entry (the destroyed Key and the strong Value) from the map.

**The Danger**:
If the Value holds a strong reference back to its own Key, the Key will never be collected, defeating the entire purpose of the `WeakHashMap`.

## 📬 The ReferenceQueue
How does the `WeakHashMap` know that the GC destroyed the Key? It doesn't poll every single key constantly. That would be too slow.
It uses a **ReferenceQueue**.

When you create a Weak, Soft, or Phantom reference, you can optionally attach it to a `ReferenceQueue`.
```java
ReferenceQueue<Object> queue = new ReferenceQueue<>();
WeakReference<Object> weakRef = new WeakReference<>(myObject, queue);
```

When the GC decides to collect `myObject`, it takes the `weakRef` wrapper object and appends it to the `queue`.
The application can have a background thread blocked on `queue.remove()`. As soon as the GC enqueues the dead reference, the background thread wakes up and can perform cleanup logic (like removing the stale Value from the map). This is highly efficient, event-driven garbage cleanup.