# Abstraction & Interfaces — Exercises

## Exercise 1: Drawable Interface
Create a `Drawable` interface with `draw()`. Implement for Circle, Square, Triangle.

## Exercise 2: Abstract Database
Abstract `Database` class with `connect()`, `disconnect()`, `execute()`. Implement MySQL and PostgreSQL.

## Exercise 3: Multiple Interfaces
Create interfaces `Flyable` and `Swimmable`. Class `Duck` implements both.

## Exercise 4: Default Method
Add a default method `print()` to an interface. Override in one implementation, inherit in another.

## Exercise 5: Functional Interface + Lambda
Create `@FunctionalInterface` `StringTransformer`. Implement with lambda for uppercase, reverse, trim.

## Exercise 6: Comparator
Create `Comparator<Person>` using interface. Sort by name, age, or height using different implementations.

## Exercise 7: Static Method in Interface
Add a `static Comparator<Person> byName()` factory method to your `PersonComparators` interface.

## Exercise 8: Private Interface Method
Use private methods to share code between default methods in an interface.

## Exercise 9: Sealed Interface
Create a `sealed interface` with 2-3 permitted implementations (Java 17+).

## Exercise 10: Abstract vs Interface Decision
Given a problem, decide whether abstract class or interface is more appropriate. Justify.

## Exercise 11: Adapter Pattern
Wrap an existing class behind your own interface using the adapter pattern.

## Exercise 12: Plugin System
Create a plugin system with `ServiceLoader` that discovers and loads interface implementations.
