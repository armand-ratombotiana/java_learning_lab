# Polymorphism — Exercises

## Exercise 1: Polymorphic Shape
Shape interface with `area()` and `perimeter()`. Implement Circle, Rectangle, Triangle. Process polymorphically.

## Exercise 2: Payment System
PaymentMethod interface with `pay()`. Implement CreditCard, PayPal, Cash. Process in a loop.

## Exercise 3: Overloading Puzzle
Write methods `print(Object)`, `print(String)`, `print(Integer)`. Call with various argument types.

## Exercise 4: Dynamic Dispatch
Create Animal → Dog/Cat. Show that method called depends on runtime type, not reference type.

## Exercise 5: Instanceof Pattern Matching
Use pattern matching (Java 16+) to `instanceof` check and call type-specific methods.

## Exercise 6: Covariant Return
Animal → Dog with covariant `reproduce()`. Show that Dog's version returns Dog without cast.

## Exercise 7: Comparable Interface
Make a Person class implement `Comparable` by age. Sort a list.

## Exercise 8: Polymorphic Collections
Store different Shape subtypes in a `List<Shape>` and process polymorphically.

## Exercise 9: Strategy Pattern
Implement sorting strategies (BubbleSort, QuickSort, MergeSort) via Strategy interface.

## Exercise 10: Factory + Polymorphism
ShapeFactory that returns different Shape subtypes based on input. Client uses polymorphically.

## Exercise 11: Switch on Type (Java 21+)
Use pattern matching switch to handle different Shape subtypes.

## Exercise 12: Method Reference Polymorphism
Use method references `System.out::println`, `String::toUpperCase` — show they are polymorphic functional interface implementations.
