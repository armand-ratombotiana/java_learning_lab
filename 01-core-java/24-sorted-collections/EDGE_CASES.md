# Edge Cases & Pitfalls: Sorted Collections

Sorted collections rely on comparison logic. If the comparison logic is flawed, the entire collection breaks down in unpredictable ways.

## 1. The `compareTo` vs `equals` Consistency Trap
*   **The Scenario**: You implement `Comparable` on a `User` class. You base `compareTo` solely on the `age` field, but you base `equals` on the `id` field.
*   **The Pitfall**: The `Map` and `Set` interfaces state that elements are duplicates if `e1.equals(e2) == true`. However, `TreeMap` and `TreeSet` **do not use `equals()`**. They rely entirely on `compareTo()` (or the `Comparator`). If `compareTo` returns `0`, the `TreeSet` considers the elements to be duplicates and will not add the second element.
    ```java
    User u1 = new User(id: 1, age: 30);
    User u2 = new User(id: 2, age: 30);
    TreeSet<User> set = new TreeSet<>();
    set.add(u1);
    set.add(u2); // Fails! compareTo returns 0 because ages are the same. u2 is lost!
    ```
*   **Mitigation**: It is strongly recommended that `compareTo` be consistent with `equals`. If you must sort by a non-unique field (like age), you must chain comparators to include a unique identifier as a tie-breaker: `Comparator.comparing(User::getAge).thenComparing(User::getId)`.

## 2. Mutating Keys After Insertion
*   **The Scenario**: You add an object to a `TreeSet` or use it as a key in a `TreeMap`. Later, you modify a field on that object that is used in the `compareTo` logic.
*   **The Pitfall**: The Red-Black Tree places the node based on its value *at the time of insertion*. If you mutate the object, its position in the tree is now incorrect. Subsequent calls to `contains()`, `remove()`, or even iterators will fail to find the object or will traverse the tree out of order, effectively corrupting the entire collection.
*   **Mitigation**: Objects used as keys in a `TreeMap` or elements in a `TreeSet` **must be immutable** (at least regarding the fields used for comparison).

## 3. `ClassCastException` on Insertion
*   **The Scenario**: You instantiate a `TreeSet` without providing a `Comparator`, and you try to add an object that does not implement `Comparable` (e.g., a basic POJO).
*   **The Pitfall**: `HashSet` will happily accept any object because everything inherits `hashCode()` from `Object`. `TreeSet` cannot. When you call `add()`, it attempts to cast the object to `Comparable`. If it doesn't implement it, it throws a `ClassCastException` at runtime.
*   **Mitigation**: Always ensure elements implement `Comparable`, or explicitly pass a `Comparator` to the constructor.

## 4. SubMap Out-of-Bounds Exceptions
*   **The Scenario**: You create a sub-map view: `SortedMap<Integer, String> sub = myTreeMap.subMap(10, 20);`.
*   **The Pitfall**: The sub-map is restricted to its defined range. If you try to insert an element into the sub-map that falls outside this range (e.g., `sub.put(25, "Data")`), it will throw an `IllegalArgumentException`.
*   **Mitigation**: Be aware that views enforce their boundary constraints on all write operations.

## 5. Performance Degradation with Complex Comparators
*   **The Scenario**: You write a complex `Comparator` that does database lookups, heavy string manipulation, or complex math.
*   **The Pitfall**: In a Red-Black Tree, adding or finding an element requires $O(\log N)$ comparisons. If $N$ is 1,000,000, a single `put()` might call your `Comparator` 20 times. If your comparator is slow, the entire collection becomes a massive performance bottleneck.
*   **Mitigation**: Comparators must be extremely fast and deterministic. Pre-compute complex sorting values if necessary.