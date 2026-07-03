# Quizzes: Specialized Collections

Test your knowledge of WeakHashMap, IdentityHashMap, EnumSet, and EnumMap.

## Quiz 1: Memory Management

**Q1: What is the primary use case for a `WeakHashMap`?**
- A) To store data that needs to be accessed extremely quickly.
- B) To build a cache where entries are automatically removed by the Garbage Collector when the key is no longer strongly referenced anywhere else in the application.
- C) To store keys that have weak (poorly implemented) `hashCode()` methods.
- D) To create a map that is thread-safe.
*Answer: B*

**Q2: You put the entry `("Session-123", new UserData())` into a `WeakHashMap`. Why will this entry likely NEVER be garbage collected?**
- A) Because `UserData` is a strong reference.
- B) Because the key is a String literal, which is stored in the String Pool and strongly referenced by the JVM forever.
- C) Because `WeakHashMap` is broken in Java 17.
- D) Because the map is too small.
*Answer: B*

## Quiz 2: Identity and Enums

**Q1: How does `IdentityHashMap` determine if two keys are the same?**
- A) By calling `key1.equals(key2)`.
- B) By comparing their hash codes using `key1.hashCode() == key2.hashCode()`.
- C) By using reference equality (`key1 == key2`), meaning they must be the exact same object in memory.
- D) By comparing their `toString()` outputs.
*Answer: C*

**Q2: Why is `EnumSet` significantly faster and more memory-efficient than `HashSet` when storing Enum values?**
- A) Because Enums don't have hash codes.
- B) Because `EnumSet` is backed by a single `long` primitive (a bit vector) instead of an array of Node objects, using bitwise operations for `add`, `remove`, and `contains`.
- C) Because `EnumSet` runs in a separate thread.
- D) Because `HashSet` has to sort the elements.
*Answer: B*

## Quiz 3: Edge Cases

**Q1: You have a `WeakHashMap<Key, Value>`. The `Value` object contains a field that holds a reference back to the `Key` object. What is the result?**
- A) The map works perfectly as a cache.
- B) A `StackOverflowError` occurs.
- C) A memory leak occurs because the map strongly references the Value, and the Value strongly references the Key, preventing the Key from ever becoming weakly reachable.
- D) The compiler throws an error.
*Answer: C*