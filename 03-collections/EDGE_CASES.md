# Java Collections - Edge Cases

## Overview

This document covers tricky scenarios and pitfalls with Java Collections.

---

## 1. UnsupportedOperationException

### Problem
Some collections don't support modifications.

```java
List<String> list = List.of("A", "B", "C");
list.add("D");  // UnsupportedOperationException!

List<String> fixed = Arrays.asList("A", "B", "C");
fixed.add("D");  // UnsupportedOperationException!

List<String> unmodifiable = Collections.unmodifiableList(new ArrayList<>());
unmodifiable.add("D");  // UnsupportedOperationException!
```

### Solution
Create mutable copies when needed.

```java
List<String> mutable = new ArrayList<>(List.of("A", "B", "C"));
mutable.add("D");  // Works!
```

---

## 2. ConcurrentModificationException

### Problem
Modifying collection while iterating.

```java
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");
list.add("C");

for (String s : list) {  // ConcurrentModificationException
    if (s.equals("B")) {
        list.remove(s);
    }
}
```

### Solution
Use Iterator.remove() or removeIf().

```java
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().equals("B")) {
        it.remove();
    }
}

// Or with removeIf (Java 8+)
list.removeIf(s -> s.equals("B"));
```

---

## 3. Null Handling

### Problem
Different collections handle null differently.

```java
List<String> arrayList = new ArrayList<>();
arrayList.add(null);  // OK

List<String> linkedList = new LinkedList<>();
linkedList.add(null);  // OK

TreeSet<String> treeSet = new TreeSet<>();
treeSet.add(null);  // NullPointerException!

Map<String, String> hashMap = new HashMap<>();
hashMap.put(null, "value");  // OK

TreeMap<String, String> treeMap = new TreeMap<>();
treeMap.put(null, "value");  // NullPointerException!
```

---

## 4. HashMap Collision

### Problem
Poor hashCode causes performance degradation.

```java
// All keys have same hashCode!
class BadKey {
    String value;
    BadKey(String value) { this.value = value; }
    
    public int hashCode() { return 1; }  // Always returns 1!
}

// Performance degrades to O(n) instead of O(1)
```

### Solution
Implement good hashCode().

```java
class GoodKey {
    String value;
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
```

---

## 5. TreeMap Comparable Requirement

### Problem
TreeMap requires elements to be Comparable or use Comparator.

```java
TreeSet<CustomObject> set = new TreeSet<>();
set.add(new CustomObject());  // ClassCastException!

class CustomObject {
    String name;
}
```

### Solution
Implement Comparable or provide Comparator.

```java
class CustomObject implements Comparable<CustomObject> {
    String name;
    
    @Override
    public int compareTo(CustomObject o) {
        return this.name.compareTo(o.name);
    }
}

// Or provide comparator
TreeSet<CustomObject> set = new TreeSet<>(
    Comparator.comparing(o -> o.name)
);
```

---

## 6. Reference vs Value Equality

### Problem
Set contains() vs equals() confusion.

```java
String s1 = new String("hello");
String s2 = new String("hello");

Set<String> set = new HashSet<>();
set.add(s1);
System.out.println(set.contains(s2));  // true (same content)

Set<String[]> set2 = new HashSet<>();
String[] arr1 = {"hello"};
String[] arr2 = {"hello"};
set2.add(arr1);
System.out.println(set2.contains(arr2));  // false (different arrays!)
```

### Solution
Understand equals() implementation.

```java
// Arrays don't override equals()
Arrays.equals(arr1, arr2);  // true
```

---

## 7. Immutable Collection Reassignment

### Problem
Reassigning to reference pointing to immutable collection.

```java
List<String> list = List.of("A", "B");
list = new ArrayList<>();  // This works (reassigning reference)
```

---

## Summary Table

| Edge Case | Problem | Solution |
|-----------|---------|----------|
| Unsupported operations | Immutable lists don't support add | Use mutable copy |
| Concurrent modification | Modifying while iterating | Use Iterator.remove() |
| Null handling | Some collections reject null | Check documentation |
| Hash collisions | Poor hashCode causes O(n) | Implement good hashCode() |
| TreeMap requirements | Elements must be Comparable | Implement Comparable or use Comparator |
| Array equality | Arrays use reference equality | Use Arrays.equals() |
