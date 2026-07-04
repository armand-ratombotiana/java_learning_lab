# Theory: Hash Tables

## Hash Table Concept

A hash table is a data structure that maps **keys to values** using a **hash function** to compute an index into an array of buckets.

### Components

1. **Key**: the input to the hash function (must have `hashCode()`)
2. **Value**: the data associated with the key
3. **Hash function**: maps a key to an integer
4. **Compression**: maps hash to array index (modulo, bit masking)
5. **Bucket array**: stores the key-value pairs
6. **Collision resolution**: handles multiple keys mapping to the same index

## Hash Functions

### Properties of a Good Hash Function

- **Deterministic**: same key always produces same hash
- **Uniform**: distributes keys evenly across the table
- **Fast**: O(1) computation
- **Non-invertible**: hard to recover key from hash

### Java's hashCode Contract

```java
// If a.equals(b), then a.hashCode() == b.hashCode()
// If a.hashCode() == b.hashCode(), a.equals(b) is NOT required (collision)
// HashCode should be consistent during one execution
```

## Collision Resolution

### Separate Chaining

Each bucket is a linked list (or tree) of entries. Insert: compute index, append to list. Search: compute index, scan list.

### Open Addressing

All entries stored directly in the array. On collision, probe for next empty slot:

- **Linear probing**: `index = (hash + i) % capacity`
- **Quadratic probing**: `index = (hash + c₁i + c₂i²) % capacity`
- **Double hashing**: `index = (hash + i × hash₂(key)) % capacity`

## Load Factor and Resizing

```
load factor = number of entries / capacity
```

- Typical threshold: 0.75
- When exceeded: **rehash** (double capacity, reinsert all entries)
- Higher load factor: less memory, more collisions
- Lower load factor: more memory, fewer collisions

## Time Complexity

| Operation | Average | Worst Case |
|-----------|---------|------------|
| Get | O(1) | O(n) |
| Put | O(1)* | O(n) |
| Remove | O(1) | O(n) |
| Contains | O(1) | O(n) |

\*Amortized — includes occasional rehashing
