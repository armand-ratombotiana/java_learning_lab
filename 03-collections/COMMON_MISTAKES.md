# Common Mistakes with Collections

## 1. Using ArrayList for Everything

Don't use ArrayList when:
- Frequent insertions/deletions in middle → use LinkedList
- Need sorted data → use TreeSet/TreeMap
- Need uniqueness → use HashSet
- Need key-value mapping → use HashMap

## 2. Not Considering Performance

- HashMap with poor hashCode() causes collisions
- Adding to ArrayList in loop without capacity
- Using raw types instead of generics

## 3. Modifying Collection While Iterating

```java
// Wrong - ConcurrentModificationException
for (String s : list) {
    if (s.equals("bad")) list.remove(s);
}

// Correct - use Iterator
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().equals("bad")) it.remove();
}
```

## 4. Returning Null Instead of Empty Collections

```java
// Bad
public List<Item> getItems() {
    return items; // could be null
}

// Good
public List<Item> getItems() {
    return items == null ? Collections.emptyList() : items;
}
```

## 5. Not Using Appropriate Interfaces

- Declare as List, Set, Map not concrete types
- Allows changing implementation without changing code