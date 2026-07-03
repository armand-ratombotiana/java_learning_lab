# Interview Preparation: Immutability Patterns

This document covers advanced questions related to true immutability, deep copying, and the Java Memory Model's initialization guarantees.

## Q1: What are the strict requirements to make a Java class truly immutable?
**Answer:**
1.  **`final` class**: Prevent subclassing, which could override getters to return mutable state.
2.  **`private final` fields**: Ensure fields are assigned only once during construction and are not accessible from the outside.
3.  **No Setters**: Do not provide any methods that mutate state.
4.  **Deep Defensive Copying**: If the class holds references to mutable objects (like `java.util.Date` or `ArrayList`), it must create deep copies of those objects in the constructor (so the caller can't modify the internal state later) and in the getters (so the caller can't modify the returned state).

## Q2: What is the difference between a "Shallow Copy" and a "Deep Copy"?
**Answer:**
*   **Shallow Copy**: Creates a new collection or object, but the references *inside* the new collection point to the exact same objects as the original. E.g., `new ArrayList<>(oldList)`. If you modify an object inside the new list, the object inside the old list is also modified because they are the same object.
*   **Deep Copy**: Creates a new collection AND recursively creates new copies of every single object inside the collection. This ensures complete isolation between the original and the copy.

## Q3: What is "Initialization Safety" in the Java Memory Model, and how does it relate to `final` fields?
**Answer:**
In multi-threaded environments, instruction reordering can cause an object reference to be published (made visible to other threads) before its constructor has finished executing. If another thread reads the object, it might see uninitialized fields (e.g., a String might be `null` instead of its assigned value).
**Initialization Safety** is a special JMM guarantee for `final` fields. It guarantees that any thread reading a published object will see the fully initialized values of its `final` fields, even without synchronization or `volatile`. 
*Caveat*: This guarantee is broken if the `this` reference "escapes" during construction (e.g., if you pass `this` to a listener inside the constructor).

## Q4: Why is `CopyOnWriteArrayList` inefficient for write-heavy workloads, and what is the functional alternative?
**Answer:**
`CopyOnWriteArrayList` achieves thread-safety by creating a brand new, full copy of the underlying array every time an element is added, modified, or removed. If the list has 10,000 elements, adding one element requires allocating a new array of 10,001 elements and copying all 10,000 existing elements. This is $O(N)$ and generates massive garbage collection pressure.
The functional alternative is a **Persistent Data Structure** (like a Hash Array Mapped Trie). When you add an element to a persistent vector, it uses "Structural Sharing" to create a new version of the collection that shares 99% of its internal tree structure with the old version. This achieves $O(\log N)$ or near $O(1)$ update performance while maintaining strict immutability.

## Q5: How do you change the state of an immutable object?
**Answer:**
You don't. You create a new object that represents the new state.
This is typically done using **Wither methods** (e.g., `withName(String newName)`). A wither method takes the new value, copies all the unchanged fields from the current object, and passes them along with the new value into the constructor to return a brand new, immutable instance.