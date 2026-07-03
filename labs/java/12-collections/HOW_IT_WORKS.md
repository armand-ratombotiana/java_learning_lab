# Collections — How It Works

## ArrayList — Resizable Array

```java
ArrayList<String> list = new ArrayList<>();
```

1. Initial internal `Object[]` array of size 10 (default)
2. `add("a")` → stores at index 0: `elementData[0] = "a"`
3. `add("b")` → stores at index 1
4. When array is full → **grow**: create new array 1.5× size, `System.arraycopy()` old → new
5. `get(0)` → `elementData[0]` — O(1)
6. `add(0, "x")` → shift right: `System.arraycopy(elementData, 0, elementData, 1, size)`, then store at 0
7. `remove(0)` → shift left: `System.arraycopy(elementData, 1, elementData, 0, size - 1)`, set last to null

## HashMap — Hash Table with Buckets

```java
HashMap<String, Integer> map = new HashMap<>();
map.put("key", 42);
```

1. Compute hash: `hash = key.hashCode() ^ (key.hashCode() >>> 16)`
2. Compute bucket index: `(n - 1) & hash` where n is table size
3. If bucket empty → create node with key, value, hash
4. If bucket occupied → compare hash and `equals()`
   - If same key → replace value
   - If different → add to linked list or tree (if bucket count ≥ 8 and table size ≥ 64 → treeify)
5. Load factor 0.75 → when 75% full, **resize**: double table size, rehash all entries

### TreeNode (Red-Black Tree)

When buckets exceed threshold, the linked list converts to a `TreeNode` (red-black tree):
- Linked list search: O(n)
- Tree search: O(log n)

This prevents hash collision DoS attacks.

## TreeSet/TreeMap — Red-Black Tree

```java
TreeSet<String> set = new TreeSet<>();
set.add("b");
set.add("a");
set.add("c");  // Now in order: a, b, c
```

1. Compare new key with existing nodes using `compareTo()` or `Comparator`
2. Traverse left (less) or right (greater) until null position
3. Insert node, then rebalance red-black tree (rotate + recolor)
4. Guarantees O(log n) for add, remove, contains
5. Iterator traverses in-order (sorted)

## HashSet — Delegation to HashMap

```java
HashSet<String> set = new HashSet<>();
set.add("a");
```

`HashSet` internally uses a `HashMap<E, Object>`:
- `add("a")` → `map.put("a", PRESENT)` where `PRESENT` is a dummy Object
- `contains("a")` → `map.containsKey("a")`
- All the performance and ordering characteristics come from HashMap

## Queue/Deque Operations

**ArrayDeque** uses a resizable circular array:
- `addFirst()` → decrement head index, wrap around
- `addLast()` → increment tail index, wrap around
- When full → grow and realign

**PriorityQueue** uses a binary heap:
- `offer()` → add at end, bubble up
- `poll()` → remove root, move last to root, bubble down
- Comparator determines priority ordering
