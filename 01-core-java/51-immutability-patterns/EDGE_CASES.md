# Edge Cases & Pitfalls: Immutability Patterns

Immutability is powerful, but enforcing it strictly in Java requires vigilance. A single leaked reference can compromise the safety of the entire object.

## 1. The "Shallow Copy" Trap in Constructors
*   **The Scenario**: You want to make a defensive copy of a `List<User>` passed into an immutable class's constructor.
    ```java
    public Company(List<User> users) {
        this.users = new ArrayList<>(users); // Defensive copy
    }
    ```
*   **The Pitfall**: `new ArrayList<>(users)` performs a **shallow copy**. It creates a new list, but the *elements* inside the list are the exact same object references as the original list. If external code modifies the `User` objects (e.g., `externalUser.setName("Hacked")`), the `User` objects inside your "immutable" `Company` class are also modified.
*   **Mitigation**: True immutability requires **deep copying**. You must clone or copy every mutable object inside the collection. Alternatively, ensure that the `User` class itself is strictly immutable.

## 2. The `java.util.Date` Vulnerability
*   **The Scenario**: You use `java.util.Date` in your domain model. You defensively copy it in the constructor.
    ```java
    public Event(Date date) {
        this.date = (Date) date.clone();
    }
    ```
*   **The Pitfall**: The `clone()` method is not guaranteed to return an object of the exact same class. An attacker could pass a malicious subclass of `Date` whose `clone()` method returns a reference to the original mutable object, bypassing your defensive copy.
*   **Mitigation**: Never use `clone()` to defensively copy untrusted input. Always use the copy constructor or factory methods: `this.date = new Date(date.getTime());`. Better yet, migrate to the immutable `java.time` API (`LocalDate`, `Instant`).

## 3. The Object Publication Race Condition
*   **The Scenario**: You create an immutable object and assign it to a shared variable.
    ```java
    class Config { final int x; Config(int x) { this.x = x; } }
    Config sharedConfig; // Not volatile
    sharedConfig = new Config(10);
    ```
*   **The Pitfall**: As discussed in the Memory Visibility module, instruction reordering can cause the reference to be published before the constructor finishes. However, Java provides a special guarantee for `final` fields: **Initialization Safety**. The JVM guarantees that `final` fields are fully initialized and visible to all threads before the object reference is published.
*   **The Edge Case**: This guarantee *only* holds if the `this` reference does not escape the constructor. If you pass `this` to a listener or another thread inside the constructor, the initialization safety guarantee is broken, and other threads might see uninitialized `final` fields.

## 4. The Memory Churn of "Wither" Methods
*   **The Scenario**: You have a complex immutable object. You write a loop that calls a "Wither" method 10,000 times to update a counter.
    ```java
    State s = new State(0);
    for (int i=0; i<10000; i++) { s = s.withCount(i); }
    ```
*   **The Pitfall**: Every call to `withCount()` allocates a brand new object on the heap. Doing this in a tight loop generates massive amounts of short-lived garbage, putting heavy pressure on the Garbage Collector and degrading throughput.
*   **Mitigation**: If you need to perform bulk updates, use a mutable Builder to accumulate the changes, and only call `build()` once at the end to freeze the state into an immutable object.

## 5. `CopyOnWriteArrayList` Iteration Stale Data
*   **The Scenario**: You use `CopyOnWriteArrayList` for thread safety. Thread A starts iterating over it. Thread B adds a new element to the list.
*   **The Pitfall**: The iterator returned by `CopyOnWriteArrayList` operates on a snapshot of the array taken at the exact moment the iterator was created. Thread A will *not* see the new element added by Thread B. It will not throw a `ConcurrentModificationException`, but it will process stale data.
*   **Mitigation**: This is the intended behavior (Fail-Safe iterator), but developers must be aware of it. If strict real-time consistency is required during iteration, you must use a different data structure or explicit locks.