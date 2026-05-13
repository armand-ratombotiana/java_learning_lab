# How Collections Work

## ArrayList Internals

ArrayList uses a dynamic array internally:
1. Initially creates array of size 10
2. When full, creates new array at 1.5x size
3. Copies elements using System.arraycopy()
4. Add at index shifts subsequent elements

## HashMap Internals

HashMap uses hashing and bucket array:
1. Keys are hashed using hashCode()
2. Hash determines bucket index
3. In each bucket, entries form linked list (or tree in Java 8+)
4. Collisions handled via chaining
5. When load factor exceeded, rehashes to new array

## HashSet Internals

HashSet uses HashMap internally:
- Stores elements as map keys
- Uses a dummy value for all entries
- Inherits HashMap performance characteristics

## TreeMap Internals

TreeMap uses Red-Black tree:
- Self-balancing binary search tree
- Maintains O(log n) performance
- Keys must be Comparable or provide Comparator

## HashMap Collision Resolution

- Java 7 and earlier: Linked list chaining
- Java 8+: Switch to balanced tree when bucket has 8+ entries
- This improves worst-case from O(n) to O(log n)