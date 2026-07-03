# Collections — Common Mistakes and Anti-Patterns

## Mistake 1: ConcurrentModificationException

```java
// BAD:
for (String s : list) {
    if (s.equals("bad")) list.remove(s);  // ConcurrentModificationException!
}

// GOOD:
list.removeIf(s -> s.equals("bad"));
// OR:
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().equals("bad")) it.remove();
}
```

## Mistake 2: Using ArrayList When LinkedList Is Better

```java
// BAD: Frequent insertion at beginning
List<String> list = new ArrayList<>();
for (int i = 0; i < 100000; i++) {
    list.add(0, "x");  // O(n) each — O(n²) total!
}

// GOOD: Use LinkedList or reverse order
List<String> list = new LinkedList<>();
for (int i = 0; i < 100000; i++) {
    list.addFirst("x");  // O(1)
}
```

## Mistake 3: Forgetting equals() and hashCode()

```java
// BAD: Using mutable objects as Map keys
Map<List<String>, String> map = new HashMap<>();
List<String> key = new ArrayList<>(List.of("a"));
map.put(key, "value");
key.add("b");  // Now key.hashCode() changed — can't find value! Memory leak!

// GOOD: Use immutable keys (String, Integer, records)
Map<String, String> map = new HashMap<>();
```

## Mistake 4: Incorrect Initial Capacity

```java
// BAD: No initial capacity — many resizes
List<String> list = new ArrayList<>();
for (int i = 0; i < 100000; i++) list.add("x");  // Many resize operations

// GOOD: Specify capacity
List<String> list = new ArrayList<>(100000);
```

## Mistake 5: Not Using Immutable Collections

```java
// BAD: Exposing mutable internal state
public class OrderService {
    private List<Order> orders = new ArrayList<>();

    public List<Order> getOrders() {
        return orders;  // Caller can modify! Breaks encapsulation
    }
}

// GOOD: Return unmodifiable view
public List<Order> getOrders() {
    return Collections.unmodifiableList(orders);
}
```

## Mistake 6: Using == Instead of equals() for Map Keys

```java
// BAD:
Map<String, Integer> map = new HashMap<>();
map.put(new String("key"), 1);
map.get(new String("key"));  // Works — String.equals() is correct

// But wrong equals() implementation:
class BadKey {
    private int id;
    // forgot to override equals() and hashCode()!
}
Map<BadKey, String> bad = new HashMap<>();
bad.put(new BadKey(1), "value");
bad.get(new BadKey(1));  // null — different objects!
```

## Mistake 7: Iterating with index and modifying

```java
// BAD: Skip behavior when removing
for (int i = 0; i < list.size(); i++) {
    if (list.get(i).equals("x")) list.remove(i);  // Skips next element!
}

// GOOD: Iterator or removeIf
list.removeIf("x"::equals);
```

## Mistake 8: Null in TreeSet/TreeMap

```java
// BAD:
TreeSet<String> set = new TreeSet<>();
set.add(null);  // NullPointerException!

// GOOD: Use HashSet for nullable elements, or handle null specially
```

## Mistake 9: Assuming HashMap Ordering

```java
// BAD: Relying on HashMap iteration order
Map<String, Integer> map = new HashMap<>();
map.put("a", 1);
map.put("b", 2);
map.forEach((k, v) -> System.out.println(k));  // Order NOT guaranteed!

// GOOD: Use LinkedHashMap for predictable order
Map<String, Integer> ordered = new LinkedHashMap<>();
```

## Mistake 10: Using Legacy Classes

```java
// BAD: Legacy synchronized classes
Vector<String> v = new Vector<>();
Hashtable<String, String> ht = new Hashtable<>();
Stack<String> s = new Stack<>();

// GOOD: Modern replacements
List<String> list = new ArrayList<>();  // Newer, wrap if needed
Map<String, String> map = new HashMap<>();
Deque<String> deque = new ArrayDeque<>();  // Use Deque instead of Stack
```
