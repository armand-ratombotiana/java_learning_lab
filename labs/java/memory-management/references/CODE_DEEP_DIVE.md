# Reference Types Code Deep Dive

This lab provides pure Java examples demonstrating the exact moment the Garbage Collector clears a Weak Reference compared to a Strong Reference.

## 💻 Pure Java Implementation

```java file="labs/java/memory-management/references/SOLUTION/ReferenceDemo.java"
package java.memorymanagement.references;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * A demonstration of Strong vs Weak References and WeakHashMap behavior.
 */
public class ReferenceDemo {

    // A large object to make it obvious to the GC
    static class HeavyObject {
        private final int id;
        private final byte[] data = new byte[1024 * 1024]; // 1MB
        
        HeavyObject(int id) { this.id = id; }
        
        @Override
        public String toString() { return "HeavyObject-" + id; }
        
        @Override
        protected void finalize() throws Throwable {
            System.out.println("[GC] HeavyObject-" + id + " was destroyed by the Garbage Collector.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- 1. Weak Reference Demonstration ---");
        
        // 1. Create a strong reference
        HeavyObject strongObj = new HeavyObject(1);
        
        // 2. Wrap it in a weak reference
        WeakReference<HeavyObject> weakRef = new WeakReference<>(strongObj);
        
        System.out.println("Before GC: weakRef.get() = " + weakRef.get());
        
        // 3. Trigger GC. 
        // The object survives because 'strongObj' still holds a strong reference to it.
        System.gc();
        Thread.sleep(500);
        System.out.println("After GC (with strong ref): weakRef.get() = " + weakRef.get());
        
        // 4. Drop the strong reference
        strongObj = null;
        System.out.println("Dropped strong reference.");
        
        // 5. Trigger GC again.
        // The object is now ONLY reachable via the WeakReference. It will be destroyed.
        System.gc();
        Thread.sleep(500);
        System.out.println("After GC (without strong ref): weakRef.get() = " + weakRef.get());

        
        System.out.println("\n--- 2. WeakHashMap Demonstration ---");
        
        WeakHashMap<HeavyObject, String> cache = new WeakHashMap<>();
        
        HeavyObject key1 = new HeavyObject(100);
        HeavyObject key2 = new HeavyObject(200);
        
        cache.put(key1, "Metadata for 100");
        cache.put(key2, "Metadata for 200");
        
        System.out.println("Cache size before GC: " + cache.size());
        
        // Drop the strong reference to key1
        key1 = null;
        System.out.println("Dropped strong reference to key1.");
        
        // Trigger GC. key1 will be destroyed.
        System.gc();
        Thread.sleep(500);
        
        // The WeakHashMap automatically removes the entry for key1!
        System.out.println("Cache size after GC: " + cache.size());
        System.out.println("Cache contains key2? " + cache.containsKey(key2));
    }
}
```

## 🔍 Key Takeaways
1. **The `get()` Method**: Notice how we access the object inside the weak reference using `weakRef.get()`. If you run this code, the final print statement will output `weakRef.get() = null`. The GC literally reached inside the `WeakReference` wrapper and nullified the pointer before destroying the object.
2. **The `finalize()` Method**: If you run this code, you will see the `[GC] HeavyObject-1 was destroyed...` message print to the console exactly when the strong reference is dropped and `System.gc()` is called. (Note: `finalize()` is deprecated in modern Java because it is unpredictable, but it is useful for pedagogical demonstrations).
3. **Automatic Cleanup**: In the `WeakHashMap` demo, we never explicitly called `cache.remove()`. Simply dropping the strong reference to `key1` and letting the GC run caused the map to shrink from size 2 to size 1 automatically.