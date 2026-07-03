# Quizzes: Sorted Collections

Test your knowledge of TreeMap internals, Comparators, and Navigable operations.

## Quiz 1: Internals and Big O

**Q1: What is the underlying data structure of a `TreeMap` and `TreeSet`?**
- A) A Hash Table
- B) A Singly Linked List
- C) A Red-Black Tree (a self-balancing binary search tree)
- D) An Array
*Answer: C*

**Q2: What is the time complexity of the `contains()` method in a `TreeSet` containing $N$ elements?**
- A) $O(1)$
- B) $O(\log N)$
- C) $O(N)$
- D) $O(N \log N)$
*Answer: B (Because the tree is balanced, the search path is logarithmic to the number of elements).*

## Quiz 2: Comparators and Consistency

**Q1: What happens if you add two objects to a `TreeSet` where `obj1.equals(obj2)` is `false`, but `obj1.compareTo(obj2)` returns `0`?**
- A) Both objects are added to the Set.
- B) An `IllegalArgumentException` is thrown.
- C) The `TreeSet` considers them duplicates based purely on `compareTo` returning 0, so the second object is NOT added.
- D) The `TreeSet` falls back to using `hashCode()`.
*Answer: C*

**Q2: You create a `TreeSet` without passing a `Comparator` to the constructor. What is required of the objects you add to this set?**
- A) They must override `hashCode()`.
- B) They must implement the `java.lang.Comparable` interface.
- C) They must be primitive wrapper classes.
- D) They must be immutable.
*Answer: B (If they don't, a `ClassCastException` is thrown at runtime).*

## Quiz 3: Navigable Interfaces

**Q1: You have a `NavigableMap<Integer, String>` containing keys `[10, 20, 30]`. What does `map.floorKey(25)` return?**
- A) 20 (The greatest key less than or equal to 25)
- B) 30 (The least key greater than or equal to 25)
- C) 25
- D) null
*Answer: A*

**Q2: What is the defining characteristic of the map returned by `myTreeMap.subMap(fromKey, toKey)`?**
- A) It is a deep copy of the data.
- B) It is immutable.
- C) It is a view; modifications to the sub-map affect the original map, and vice versa.
- D) It throws an exception if the keys don't exist.
*Answer: C*