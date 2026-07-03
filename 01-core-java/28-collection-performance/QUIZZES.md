# Quizzes: Collection Performance Tuning

Test your knowledge of collection internals, memory overhead, and resizing mechanics.

## Quiz 1: Resizing and Capacity

**Q1: You know you will insert exactly 100 elements into a new `HashMap`. To prevent the map from resizing during these insertions (assuming the default load factor of 0.75), what is the smallest initial capacity you should provide in the constructor?**
- A) 100
- B) 125
- C) 134
- D) 200
*Answer: C (100 / 0.75 = 133.3. You must round up to the next integer, 134. If you set it to 100, it resizes at 75).*

**Q2: What happens internally when an `ArrayList` reaches its capacity and you add another element?**
- A) It throws an `OutOfMemoryError`.
- B) It creates a linked list node for the new element.
- C) It allocates a new array that is 50% larger, copies all elements from the old array to the new one, and adds the new element.
- D) It shifts all elements to the left.
*Answer: C*

## Quiz 2: Memory and Autoboxing

**Q1: Why is `List<Integer>` significantly less memory-efficient than a primitive `int[]` array?**
- A) Because `List` has a larger object header.
- B) Because `Integer` objects are immutable.
- C) Because each `Integer` is a full object requiring an object header (16 bytes) plus the primitive payload (4 bytes) and padding, plus the array stores references (pointers) to these objects rather than the raw values.
- D) It is not less efficient; modern JVMs optimize this away entirely.
*Answer: C (This is the cost of Autoboxing).*

**Q2: You have an `ArrayList` that grew to 1,000,000 elements. You call `list.clear()`. What happens to the memory consumed by the backing array?**
- A) The array is immediately garbage collected.
- B) The array shrinks back to its default size of 10.
- C) The array elements are set to `null`, but the array itself remains size 1,000,000 in memory.
- D) The JVM throws a `MemoryLeakException`.
*Answer: C (You must call `trimToSize()` to shrink the physical array).*

## Quiz 3: Algorithmic Complexity

**Q1: What is the time complexity of iterating through a `LinkedList` using `list.get(i)` inside a standard `for` loop?**
- A) $O(1)$
- B) $O(N)$
- C) $O(N \log N)$
- D) $O(N^2)$
*Answer: D (The loop runs N times, and `get(i)` takes $O(N)$ time to traverse the list from the head to index i).*