# LeetCode Solution: Design HashSet (Modularized)

**Problem:** [705. Design HashSet](https://leetcode.com/problems/design-hashset/)

Demonstrates a clean module structure with a service-based key-value store.

## Module Structure

```
module-info.java
com/java/module/hashset/
    HashSetModule.java
    spi/
        HashProvider.java
    impl/
        ChainHashProvider.java
```

## module-info.java

```java
module com.java.module.hashset {
    exports com.java.module.hashset;
    exports com.java.module.hashset.spi;
    provides com.java.module.hashset.spi.HashProvider
        with com.java.module.hashset.impl.ChainHashProvider;
}
```

## Java 21 Solution

```java
package com.java.module.hashset;

import com.java.module.hashset.spi.HashProvider;
import java.util.*;

public class MyHashSet {
    private static final int SIZE = 1000;
    private final List<HashProvider> buckets;

    public MyHashSet() {
        ServiceLoader<HashProvider> loader = ServiceLoader.load(HashProvider.class);
        buckets = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            buckets.add(loader.iterator().hasNext()
                    ? loader.iterator().next() : new ChainHashProvider());
        }
    }

    private int hash(int key) { return key % SIZE; }

    public void add(int key) { buckets.get(hash(key)).add(key); }
    public void remove(int key) { buckets.get(hash(key)).remove(key); }
    public boolean contains(int key) { return buckets.get(hash(key)).contains(key); }
}

// SPI
package com.java.module.hashset.spi;
public interface HashProvider {
    void add(int key);
    void remove(int key);
    boolean contains(int key);
}

// Default implementation
package com.java.module.hashset.impl;
public class ChainHashProvider implements HashProvider {
    private final LinkedList<Integer> list = new LinkedList<>();
    public void add(int key) { if (!list.contains(key)) list.add(key); }
    public void remove(int key) { list.remove((Integer) key); }
    public boolean contains(int key) { return list.contains(key); }
}
```

## Key Takeaway

JPMS allows swapping `HashProvider` implementations at deploy time via `ServiceLoader` — **zero code changes, just a different module on the module path**.
