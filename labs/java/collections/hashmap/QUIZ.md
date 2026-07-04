# HashMap Assessment Quiz

Test your deep understanding of Java's `HashMap`. 

## Core Mechanics
1. What is the time complexity of a `get()` operation in an ideal scenario, and what is the worst-case scenario prior to Java 8?
2. Explain the significance of the `hashCode()` contract. What happens if two equal objects return different hash codes?
3. Why does `HashMap` enforce its capacity to always be a power of two?
4. Explain the bitwise operation `(n - 1) & hash`. Why is it faster than modulo `%`?

## Collisions & Resizing
5. What is a hash collision, and how does Java's `HashMap` resolve it?
6. Define "Load Factor". What is the default value in Java, and why was that specific number chosen?
7. Describe the `resize()` process. What is the time complexity of this operation?
8. True or False: When a `HashMap` resizes, elements might end up in a different bucket index than they were originally. Explain why.

## Treeification (Java 8+)
9. What is treeification, and what specific problem does it solve?
10. At what threshold does a bin convert from a linked list to a Red-Black tree? 
11. What is `MIN_TREEIFY_CAPACITY`, and why does `HashMap` resize instead of treeify if the table capacity is below this threshold?
12. Based on the Poisson distribution, what is the mathematical probability of a well-distributed hash function causing a bin to treeify?

## Concurrency & Edge Cases
13. Is `HashMap` thread-safe? What specific failure can occur if multiple threads call `put()` concurrently during a resize operation (especially pre-Java 8)?
14. If you need a thread-safe map with high concurrency, what should you use instead of `Collections.synchronizedMap(new HashMap<>())`?
15. What happens if you use a mutable object as a key in a `HashMap`, and you mutate the object after inserting it?

*(Answers provided in the SOLUTION directory)*