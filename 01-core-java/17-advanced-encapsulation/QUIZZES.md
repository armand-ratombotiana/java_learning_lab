# Quizzes: Advanced Encapsulation

Test your knowledge of Immutability, Records, and Sealed Classes.

## Quiz 1: True Immutability

**Q1: You have a class with all `private final` fields and no setters. One of the fields is a `java.util.Date`. To ensure true immutability, what must you do?**
- A) Annotate the class with `@Immutable`.
- B) Ensure the getter returns the `Date` object directly so the garbage collector can track it.
- C) Perform a defensive copy of the `Date` object in the constructor and return a clone of it in the getter.
- D) Make the `Date` field `static`.
*Answer: C*

**Q2: Why must a truly immutable class in Java be declared as `final`?**
- A) To prevent the compiler from optimizing it away.
- B) To prevent developers from creating subclasses that might override getters to return mutable state, breaking the immutability contract.
- C) To allow it to be serialized.
- D) Because all fields are final.
*Answer: B*

## Quiz 2: Java Records

**Q1: Which of the following is true about Java Records?**
- A) They can extend other classes.
- B) Their fields can be modified after instantiation using automatically generated setter methods.
- C) They automatically provide implementations for `equals()`, `hashCode()`, and `toString()` based on their components.
- D) They cannot implement interfaces.
*Answer: C*

**Q2: How do you add validation logic to a Java Record (e.g., ensuring an ID is greater than zero)?**
- A) You cannot add logic to a Record; they are strictly for data.
- B) By using a Compact Constructor.
- C) By overriding the generated setter method.
- D) By extending the `java.lang.Record` class manually.
*Answer: B*

## Quiz 3: Sealed Classes

**Q1: What is the primary purpose of a `sealed` class in Java?**
- A) To prevent the class from being instantiated (like an abstract class).
- B) To encrypt the bytecode of the class.
- C) To restrict which other classes or interfaces are permitted to extend or implement it.
- D) To prevent reflection from accessing its private fields.
*Answer: C*

**Q2: If class `Shape` is `sealed` and permits `Circle`, what modifier MUST the `Circle` class declare?**
- A) It must be `public`.
- B) It must be `static`.
- C) It must choose either `final`, `sealed`, or `non-sealed`.
- D) It must be `abstract`.
*Answer: C*