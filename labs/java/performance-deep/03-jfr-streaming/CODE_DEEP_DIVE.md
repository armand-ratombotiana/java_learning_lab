# JFR Streaming — Code Deep Dive

## Main Implementation

### Class Structure
The main class implements the core data structure and operations:

**Package**: com.javalab.03

### Fields
- Internal storage array or structure
- Size counter to track number of elements
- Modification counter for fail-fast behavior
- Capacity and threshold values for resizing

### Constructor
Initializes the data structure with default or specified capacity and load factor.

### Core Methods

#### add/put Operation
1. Hash the key to determine bucket index
2. Handle existing entries (update or chain)
3. Check load factor and resize if needed
4. Increment size and modification counters

#### get/contains Operation
1. Hash the key to determine bucket index
2. Search the bucket chain or tree
3. Return the value or null if not found

#### remove Operation
1. Hash the key to determine bucket index
2. Remove the entry from the chain
3. Decrement size counter
4. Return the removed value (or null)

### Helper Methods
- **hash()**: Applies supplemental hash function to improve distribution
- **resize()**: Doubles capacity and rehashes all entries
- **getNode()**: Core lookup logic used by get() and containsKey()

### Inner Classes
- **Node**: Holds key, value, hash, and next pointer for linked list chaining
- **TreeNode**: Extended Node for red-black tree bins (treeified buckets)
- **EntrySet/KeySet/Values**: View collections backed by the main structure

## Code Walkthrough

### Insertion Flow
```
put(key, value)
  -> hash(key) -> index = (n - 1) & hash
  -> if table[index] == null
       create node and insert
     else
       traverse chain
       if key found -> update value, return old
       else -> insert at end of chain
  -> if size > threshold -> resize()
  -> return null
```

### Lookup Flow
```
get(key)
  -> hash(key) -> index = (n - 1) & hash
  -> if table[index] exists
       traverse chain comparing hash and key (== or equals)
       if found -> return value
  -> return null
```

### Resize Flow
```
resize()
  -> newCapacity = oldCapacity * 2
  -> newThreshold = (int)(newCapacity * loadFactor)
  -> newTable = new Node[newCapacity]
  -> for each bucket in oldTable
       split chain into lo/hi based on (hash & oldCapacity)
       lo remains at same index
       hi moves to index + oldCapacity
  -> assign newTable to table
```

## Performance Considerations
- **Initial capacity**: Choosing the right initial capacity prevents costly resizing operations
- **Load factor tuning**: Lower = fewer collisions but more memory; Higher = less memory but more collisions
- **Hash distribution**: The quality of the hash function directly impacts lookup performance
- **Treeification**: Prevents O(n) worst-case from hash collision attacks

## Implementation Details

### Hash Function
```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

### Index Calculation
```java
index = (capacity - 1) & hash  // Fast bitwise AND when capacity is power of 2
```

## Debug Output Example
When debugging, the internal state can reveal performance issues:
```
Size=1000, Capacity=1024, Load=0.98, Threshold=768
```
A load of 0.98 indicates the table is nearly full and should have resized. This suggests
the load factor or threshold calculation may have an issue.
