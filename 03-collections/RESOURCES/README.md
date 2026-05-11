# Collections Resources

Reference materials for Java Collections Framework.

## Contents

- [Complexity Chart](./complexity-chart.md) - Time/space complexity for all operations
- [Official Documentation](#official-documentation)
- [Key Concepts](#key-concepts)

---

## Official Documentation

| Topic | Link |
|-------|------|
| Collections Framework | https://docs.oracle.com/javase/tutorial/collections/index.html |
| List Interface | https://docs.oracle.com/javase/8/docs/api/java/util/List.html |
| Set Interface | https://docs.oracle.com/javase/8/docs/api/java/util/Set.html |
| Map Interface | https://docs.oracle.com/javase/8/docs/api/java/util/Map.html |
| Queue Interface | https://docs.oracle.com/javase/8/docs/api/java/util/Queue.html |

---

## Key Concepts

### Interface Hierarchy
- **Iterable** → **Collection** → List, Set, Queue
- **Map** is separate (implements not extend Collection)

### Common Implementations
| Interface | Implementation | Notes |
|-----------|---------------|-------|
| List | ArrayList | Fast random access |
| List | LinkedList | Fast insert/delete at ends |
| Set | HashSet | Fast, no order |
| Set | TreeSet | Sorted, O(log n) |
| Set | LinkedHashSet | Insertion order |
| Map | HashMap | Fast key-value |
| Map | TreeMap | Sorted keys |
| Map | LinkedHashMap | Insertion order |

### Choosing Collections
1. **Need duplicates?** → List
2. **Need uniqueness?** → Set
3. **Need key-value?** → Map
4. **Need ordering?** → TreeSet/TreeMap or LinkedHashSet/Map

### Performance Tips
- ArrayList: default for most cases
- HashMap: default for key-value
- Avoid Vector/Hashtable (legacy, synchronized)
- Use ConcurrentHashMap for thread-safety
