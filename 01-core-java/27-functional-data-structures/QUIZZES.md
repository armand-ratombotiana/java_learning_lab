# Quizzes: Functional Data Structures

Test your knowledge of persistent data structures, structural sharing, and lazy evaluation.

## Quiz 1: Persistent Data Structures

**Q1: What defines a "Persistent" Data Structure in the context of functional programming?**
- A) It is automatically saved to a database or file system on disk.
- B) It is immutable, but when "modified", it returns a new version of itself while sharing as much structural memory as possible with the previous version.
- C) It resists being garbage collected.
- D) It uses the `transient` keyword.
*Answer: B*

**Q2: In a functional Singly Linked List (Cons List), what is the time complexity of prepending an element (adding to the head) vs appending an element (adding to the tail)?**
- A) Prepend: $O(1)$, Append: $O(1)$
- B) Prepend: $O(N)$, Append: $O(1)$
- C) Prepend: $O(1)$, Append: $O(N)$
- D) Prepend: $O(\log N)$, Append: $O(\log N)$
*Answer: C (Prepending just creates a new node pointing to the existing list. Appending requires copying the entire list).*

## Quiz 2: Advanced Structures and Java Limits

**Q1: To achieve near $O(1)$ performance for lookups and updates in a persistent collection, what underlying data structure is typically used?**
- A) A standard Java Array
- B) A Hash Array Mapped Trie (HAMT) or Bitmapped Vector Trie
- C) A Doubly Linked List
- D) A Red-Black Tree
*Answer: B (Tries are wide, shallow trees that allow for very fast path-copying).*

**Q2: Why is it dangerous to write deep recursive functions to process functional lists in Java?**
- A) Because Java does not support Tail Call Optimization (TCO), deep recursion will quickly cause a `StackOverflowError`.
- B) Because recursive functions cannot use lambdas.
- C) Because it will cause a memory leak in the Heap.
- D) Because the Java compiler will optimize the recursion away entirely.
*Answer: A*

## Quiz 3: Lazy Evaluation

**Q1: Which of the following is an example of Lazy Evaluation in the standard Java API?**
- A) `Collections.sort()`
- B) `ArrayList.add()`
- C) The `java.util.stream.Stream` API
- D) `CompletableFuture`
*Answer: C (Stream intermediate operations like `filter` and `map` do not execute until a terminal operation is invoked).*