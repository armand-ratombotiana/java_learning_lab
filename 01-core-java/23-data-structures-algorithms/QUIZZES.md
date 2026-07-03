# Module 23: Data Structures & Algorithms - Quizzes

---

## Q1: Big O Notation
Which algorithm exhibits O(log n) time complexity?

A) Linear Search
B) Binary Search
C) Bubble Sort
D) Accessing an array element by index

**Answer**: B
**Explanation**: Binary search repeatedly divides the search space in half, resulting in a logarithmic time complexity.

---

## Q2: Hash Tables
What happens when two different keys in a Hash Table produce the same hash code?

A) An exception is thrown.
B) The new key overwrites the old key.
C) A collision occurs, which is usually resolved via chaining (storing elements in a linked list at that index) or open addressing.
D) The Hash Table automatically resizes.

**Answer**: C
**Explanation**: Collisions are an expected part of hashing, and Hash Tables resolve them using mechanisms like chaining or open addressing to ensure all data is stored.

---

## Q3: Sorting Space Complexity
Which of the following sorting algorithms is NOT considered an "in-place" sort, because it requires O(n) auxiliary space?

A) Quick Sort
B) Insertion Sort
C) Bubble Sort
D) Merge Sort

**Answer**: D
**Explanation**: Merge Sort requires an additional array of size `n` to merge the divided halves, meaning its space complexity is O(n).