# HashMap Exercises

Complete these exercises to build muscle memory and practical mastery of hash-based data structures.

## Level 1: Core Usage
1. **Frequency Counter**: Given a string, use a `HashMap` to count the frequency of each character.
2. **Two Sum**: Given an array of integers and a target sum, return the indices of the two numbers that add up to the target. Use a `HashMap` to achieve O(n) time complexity.
3. **Intersection of Arrays**: Given two integer arrays, return their intersection. Each element in the result must appear as many times as it shows in both arrays.

## Level 2: Custom Keys & Contracts
4. **The Broken Contract**: Create a `Person` class with `firstName` and `lastName`. Do *not* override `hashCode()` or `equals()`. Insert a `Person` into a `HashMap`, then try to retrieve it using a new `Person` instance with the same names. Observe the result.
5. **Fixing the Contract**: Properly override `hashCode()` and `equals()` in the `Person` class. Verify that retrieval now works correctly.
6. **The Mutable Key Trap**: Insert a `Person` into a `HashMap`. Change the `Person`'s `lastName`. Try to retrieve the value using the `Person` object. Explain why it returns `null`.

## Level 3: Internals & Performance
7. **Collision Generator**: Write a custom class `BadKey` where `hashCode()` always returns `1`. Insert 10,000 items into a `HashMap`. Measure the time it takes to retrieve the last inserted item compared to a normal `HashMap` with well-distributed hash codes.
8. **Initial Capacity Tuning**: Write a JMH benchmark comparing the insertion of 1,000,000 elements into a `HashMap` created with `new HashMap<>()` versus `new HashMap<>(1_400_000)`. Explain the performance difference.

## Level 4: Implementation
9. **Build a LRU Cache**: Use `HashMap` and a doubly-linked list to implement an LRU (Least Recently Used) Cache with O(1) `get` and `put` operations. (Do not use `LinkedHashMap`).
10. **Build SimpleHashMap**: Implement the `SimpleHashMap` from the `CODE_DEEP_DIVE.md` from memory. Add support for generics and dynamic resizing.