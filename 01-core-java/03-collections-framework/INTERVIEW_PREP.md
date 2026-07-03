# Module 03: Collections Framework - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: How does a HashMap work internally in Java?
**Answer**: 
- A `HashMap` stores data in an array of "buckets" (Nodes/Entries). 
- When `put(Key, Value)` is called, it calculates the `hashCode()` of the key to determine the bucket index.
- If multiple keys land in the same bucket (a collision), they are stored as a Linked List. 
- In Java 8+, if the linked list grows beyond a certain threshold (usually 8), it transforms into a balanced Red-Black Tree to improve search performance from O(n) to O(log n).

### Q2: ArrayList vs LinkedList
**Answer**:
- **ArrayList**: Backed by a dynamic array. Fast random access O(1). Slow insertion/deletion in the middle O(n) because elements must be shifted. Better cache locality.
- **LinkedList**: Backed by a doubly-linked list. Slow random access O(n) because it must traverse pointers. Fast insertion/deletion O(1) *if* the node reference is known.

### Q3: What is the difference between fail-fast and fail-safe iterators?
**Answer**:
- **Fail-Fast**: Iterators over standard collections (e.g., `ArrayList`, `HashMap`). If the collection is structurally modified while iterating (other than through the iterator's own `remove` method), it immediately throws a `ConcurrentModificationException`.
- **Fail-Safe**: Iterators over concurrent collections (e.g., `ConcurrentHashMap`, `CopyOnWriteArrayList`). They operate on a clone of the underlying collection, so they do not throw exceptions if the original collection is modified during iteration.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Find First Non-Repeating Character
**Problem**: Given a string, find the first non-repeating character and return its index. If it doesn't exist, return -1.

**Solution**:
Use a `LinkedHashMap` (which preserves insertion order) to count character frequencies, or iterate twice using a standard `HashMap`.

```java
public int firstUniqChar(String s) {
    Map<Character, Integer> counts = new HashMap<>();
    
    // First pass: count frequencies
    for (char c : s.toCharArray()) {
        counts.put(c, counts.getOrDefault(c, 0) + 1);
    }
    
    // Second pass: find first char with count == 1
    for (int i = 0; i < s.length(); i++) {
        if (counts.get(s.charAt(i)) == 1) {
            return i;
        }
    }
    return -1;
}
```

### Scenario 2: Valid Parentheses
**Problem**: Given a string containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid using a Stack.

**Solution**:
```java
public boolean isValid(String s) {
    Stack<Character> stack = new Stack<>();
    for (char c : s.toCharArray()) {
        if (c == '(' || c == '{' || c == '[') {
            stack.push(c);
        } else {
            if (stack.isEmpty()) return false;
            char top = stack.pop();
            if (c == ')' && top != '(') return false;
            if (c == '}' && top != '{') return false;
            if (c == ']' && top != '[') return false;
        }
    }
    return stack.isEmpty();
}
```