# Module 46: Performance Optimization - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is Lock Contention, and how do you reduce it?
**Answer**:
Lock contention happens when multiple threads try to acquire the same lock simultaneously. The JVM has to pause the threads, put them in a waiting state, and perform context switches, which severely degrades performance.
**To reduce it**:
1. **Reduce Lock Granularity**: Instead of locking a whole object, lock only the specific fields being modified (e.g., Lock Striping used in `ConcurrentHashMap`).
2. **Shorten Lock Duration**: Do not perform slow I/O operations (like database calls) inside a synchronized block.
3. **Use Lock-Free Algorithms**: Use classes from `java.util.concurrent.atomic` (like `AtomicInteger`) which use CPU-level Compare-And-Swap (CAS) instructions instead of OS-level locks.

### Q2: Why are Exceptions expensive in Java?
**Answer**:
Instantiating an `Exception` is incredibly slow compared to a normal object because the JVM must execute the `fillInStackTrace()` method. This method walks down the entire thread execution stack to capture the current state of method calls so it can print the stack trace. 
Using exceptions for regular control flow (e.g., throwing a `NotFoundException` instead of returning `Optional.empty()` or `null` during a frequent database query) will tank the application's performance.

### Q3: What is "False Sharing" in a multithreaded environment?
**Answer**:
Modern CPUs do not read memory one byte at a time; they read chunks into Cache Lines (usually 64 bytes). 
If two distinct threads are independently updating two different variables (e.g., `thread1Count` and `thread2Count`), but those two variables happen to be located next to each other in memory (sharing the same cache line), the CPU will constantly invalidate and reload the entire cache line across different CPU cores. This destroys CPU cache efficiency. 
It is mitigated using the `@Contended` annotation in Java, which pads the variables with empty space so they sit on different cache lines.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Optimizing an O(N^2) Algorithm
**Problem**: An interviewer shows you a method that takes two lists of 100,000 users and returns a list of users present in both lists. The current code uses nested `for` loops. Why is it slow, and how do you fix it?

```java
public List<User> findCommon(List<User> list1, List<User> list2) {
    List<User> common = new ArrayList<>();
    for (User u1 : list1) {
        for (User u2 : list2) {
            if (u1.getId().equals(u2.getId())) {
                common.add(u1);
            }
        }
    }
    return common;
}
```

**Solution**:
The current solution has an $O(N \times M)$ time complexity (10 billion iterations). It can be optimized to $O(N + M)$ by using a `HashSet`. Lookups in a HashSet are $O(1)$.

```java
public List<User> findCommon(List<User> list1, List<User> list2) {
    // Put list2 IDs into a HashSet for O(1) lookups
    Set<String> idsInList2 = list2.stream()
                                  .map(User::getId)
                                  .collect(Collectors.toSet());
                                  
    List<User> common = new ArrayList<>();
    for (User u1 : list1) {
        // HashSet.contains() is O(1)
        if (idsInList2.contains(u1.getId())) {
            common.add(u1);
        }
    }
    return common;
}
```