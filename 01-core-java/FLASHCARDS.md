# Flashcards for Core Java

## Q: What is the difference between == and .equals()?
**A:** `==` compares references (memory addresses), while `.equals()` compares object values. For strings, always use `.equals()`.

## Q: What is the heap and stack in Java?
**A:** Heap stores objects and class data, stack stores method calls, local variables, and references.

## Q: What is autoboxing?
**A:** Automatic conversion between primitive types and their wrapper classes (int → Integer).

## Q: What is the purpose of the `final` keyword?
**A:** For classes: prevents inheritance. For methods: prevents overriding. For variables: makes them constants.

## Q: What is a constructor?
**A:** A special method called when creating an object to initialize its state.

## Q: What is method overloading?
**A:** Defining multiple methods with the same name but different parameters.

## Q: What is method overriding?
**A:** Redefining a parent class method in a subclass with the same signature.

## Q: What is an interface?
**A:** A contract specifying methods that a class must implement. Supports multiple inheritance of type.

## Q: What is an abstract class?
**A:** A class that cannot be instantiated and may contain abstract (unimplemented) methods.

## Q: What is the purpose of `static`?
**A:** Makes a member belong to the class, not instances. Shared across all objects.