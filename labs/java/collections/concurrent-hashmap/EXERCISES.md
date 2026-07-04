# ConcurrentHashMap Exercises

Complete these exercises to build muscle memory for concurrent programming in Java.

## Level 1: Basic Operations
1. **Word Frequency Counter (Thread-Safe)**: Write a method that takes a List of strings and uses a `ConcurrentHashMap` and `parallelStream()` to count the frequency of each word. Ensure you do not lose updates.
2. **Cache Initialization**: Implement a thread-safe cache using `ConcurrentHashMap`. Write a method `String getUserData(String userId)` that returns data from the cache if present, or simulates a 1-second delay to "fetch" it and stores it in the cache before returning. Use `computeIfAbsent`.

## Level 2: Advanced Usage
3. **The Anti-Pattern Fix**: Write a program that spawns 100 threads, each attempting to increment the same key in a `ConcurrentHashMap` 1,000 times using a `get` then `put` approach. Print the final count (it will be less than 100,000). Then, rewrite the program using `merge()` and verify the final count is exactly 100,000.
4. **Concurrent Removal**: Implement a session manager. Use a `ConcurrentHashMap<String, Long>` to store session IDs and their last active timestamp. Write a background thread that periodically iterates over the map and removes any session older than 30 minutes. Ensure this iteration does not throw a `ConcurrentModificationException`.

## Level 3: Architecture
5. **Simulated Striping**: Write a custom wrapper around a standard `HashMap` that mimics Java 7's lock striping. Create an array of 16 `Object` locks. When a key is inserted, hash it, modulo 16 to pick a lock, synchronize on that specific lock, and then perform the standard `HashMap` insertion.