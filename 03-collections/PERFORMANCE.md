# Collection Performance

## Operation Complexity

| Collection | Add | Remove | Get | Contains |
|------------|-----|--------|-----|----------|
| ArrayList | O(1)* | O(n) | O(1) | O(n) |
| LinkedList | O(1) | O(1)* | O(n) | O(n) |
| HashSet | O(1) | O(1) | N/A | O(1) |
| TreeSet | O(log n) | O(log n) | N/A | O(log n) |
| HashMap | O(1) | O(1) | O(1) | O(1) |
| TreeMap | O(log n) | O(log n) | O(log n) | O(log n) |

*Amortized

## Performance Tips

### ArrayList
- Set initial capacity: `new ArrayList<>(1000)`
- Use for frequent random access
- Avoid frequent insertions in middle

### HashMap
- Ensure good hashCode() implementation
- Set initial capacity: `new HashMap<>(initialCapacity, 0.75f)`
- Use HashMap, not Hashtable (unless thread-safety needed)

### HashSet
- Same performance characteristics as HashMap
- Use EnumSet for enum types

### TreeSet/TreeMap
- Use when sorted data required
- Consider ConcurrentSkipListSet/Map for thread-safe sorted collections

## Memory Considerations

- ArrayList: ~40% overhead
- LinkedList: ~100% overhead (node objects)
- HashMap: ~100% overhead (entry objects)
- TreeMap: ~50% overhead (tree nodes)