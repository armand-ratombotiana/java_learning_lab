# Quizzes: Immutability Patterns

Test your knowledge of defensive copying, Initialization Safety, and Builder patterns.

## Quiz 1: True Immutability

**Q1: Which of the following is REQUIRED to make a Java class truly immutable?**
- A) All fields must be declared `static`.
- B) The class must be declared `final`, all fields must be `private final`, and defensive copies must be made of any mutable object fields in both the constructor and getters.
- C) The class must implement the `Serializable` interface.
- D) The class must use the `synchronized` keyword on all methods.
*Answer: B*

**Q2: Why is `new ArrayList<>(originalList)` inside a constructor considered a "Shallow Copy"?**
- A) Because it only copies the first 10 elements.
- B) Because it creates a new list structure, but the elements inside the new list are the exact same object references as the elements in the original list. If those elements are mutable, the encapsulation is broken.
- C) Because it doesn't copy the capacity of the original list.
- D) Because it uses a weak reference.
*Answer: B*

## Quiz 2: Java Memory Model Guarantees

**Q1: What special guarantee does the Java Memory Model provide for `final` fields?**
- A) They are automatically encrypted in memory.
- B) "Initialization Safety": The JVM guarantees that `final` fields are fully initialized and visible to all threads before the object reference itself is published, provided the `this` reference does not escape the constructor.
- C) They can be garbage collected faster.
- D) They do not consume heap memory.
*Answer: B*

**Q2: You have a `CopyOnWriteArrayList`. Thread A gets an iterator. Thread B adds an element. What does Thread A see?**
- A) A `ConcurrentModificationException`.
- B) The new element added by Thread B.
- C) Only the elements that existed at the exact moment the iterator was created. It iterates over a frozen snapshot.
- D) Null.
*Answer: C*

## Quiz 3: Design Patterns

**Q1: In an immutable class, what is the purpose of a "Wither" method (e.g., `withName(String newName)`)?**
- A) To mutate the internal `name` field directly.
- B) To return a brand new instance of the class that is an exact copy of the current instance, except with the `name` field updated to the new value.
- C) To delete the object.
- D) To serialize the object to JSON.
*Answer: B*