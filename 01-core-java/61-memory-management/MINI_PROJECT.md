# Mini Project: Memory-Sensitive Image Cache

## Objective
Build a caching system that automatically adapts to the JVM's memory pressure. You will use `SoftReference` to hold cached objects, allowing the Garbage Collector to automatically clear the cache *before* throwing an `OutOfMemoryError`.

## Prerequisites
*   Java 17+

## Step 1: The Heavy Object
Create an object that consumes a significant amount of memory.

```java
public class HeavyImage {
    private final String name;
    // Simulate a 5MB image payload
    private final byte[] data = new byte[5 * 1024 * 1024]; 

    public HeavyImage(String name) {
        this.name = name;
        System.out.println("Loaded heavy image from disk: " + name);
    }

    public String getName() { return name; }
}
```

## Step 2: The Soft Cache
Implement a cache using a `HashMap` where the values are wrapped in `SoftReference`s.

```java
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private final Map<String, SoftReference<HeavyImage>> cache = new HashMap<>();

    public HeavyImage getImage(String name) {
        SoftReference<HeavyImage> ref = cache.get(name);
        
        if (ref != null) {
            HeavyImage image = ref.get();
            if (image != null) {
                System.out.println("Cache HIT for: " + name);
                return image;
            } else {
                System.out.println("Cache MISS (GC cleared it) for: " + name);
            }
        } else {
            System.out.println("Cache MISS (Not found) for: " + name);
        }

        // Image not in cache, or was cleared by GC. Load it from "disk".
        HeavyImage newImage = new HeavyImage(name);
        
        // Wrap in a SoftReference and put in cache
        cache.put(name, new SoftReference<>(newImage));
        
        return newImage;
    }
    
    public int getCacheSize() {
        return cache.size();
    }
}
```

## Step 3: The Memory Stress Test
We will intentionally create a loop that consumes all available memory to force the JVM to run out of RAM. We will observe the GC clearing our cache to save the application.

*Important: To see this work quickly, run the JVM with a small heap size, e.g., `-Xmx50m` (50 Megabytes).*

```java
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Starting SoftReference Cache Test ---");
        System.out.println("Please run with JVM argument: -Xmx50m");
        
        ImageCache cache = new ImageCache();

        // 1. Load a few images into the cache
        cache.getImage("logo.png");
        cache.getImage("background.jpg");
        
        // 2. Verify they are in the cache
        cache.getImage("logo.png"); // Should be a HIT

        System.out.println("\n--- Simulating Memory Pressure ---");
        
        // 3. Create a memory leak to force the GC to panic
        List<byte[]> memoryLeak = new ArrayList<>();
        
        try {
            for (int i = 0; i < 100; i++) {
                System.out.println("Allocating 2MB block " + i + "...");
                memoryLeak.add(new byte[2 * 1024 * 1024]); // Allocate 2MB strong references
                
                // Pause slightly to let logs print
                Thread.sleep(50); 
            }
        } catch (OutOfMemoryError e) {
            System.err.println("\nOut Of Memory Error caught! The JVM heap is completely full.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n--- Checking Cache After Memory Pressure ---");
        
        // 4. Try to get the image again. 
        // Even though we never explicitly removed it from the cache, 
        // the GC should have cleared the SoftReference to try and prevent the OOM.
        cache.getImage("logo.png"); // Should be a MISS (GC cleared it)
    }
}
```

## Expected Output (Run with `-Xmx50m`)
Notice that the cache initially works. As the memory fills up, the GC desperately looks for memory. It finds our `SoftReference`s, clears them, reclaims the 10MB of images, and delays the OOM. Eventually, the OOM still happens (because of our intentional strong-reference leak), but our cache proves it yielded its memory safely.

```text
--- Starting SoftReference Cache Test ---
Please run with JVM argument: -Xmx50m
Loaded heavy image from disk: logo.png
Loaded heavy image from disk: background.jpg
Cache HIT for: logo.png

--- Simulating Memory Pressure ---
Allocating 2MB block 0...
Allocating 2MB block 1...
...
Allocating 2MB block 15...

Out Of Memory Error caught! The JVM heap is completely full.

--- Checking Cache After Memory Pressure ---
Cache MISS (GC cleared it) for: logo.png
Loaded heavy image from disk: logo.png
```