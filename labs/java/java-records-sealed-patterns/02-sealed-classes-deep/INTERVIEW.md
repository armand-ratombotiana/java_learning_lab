# Interview Questions: Sealed Classes

## Basic
1. What is a sealed class and what problem does it solve?
2. What three modifiers must a permitted subclass use?
3. How does `sealed` differ from `final`?

## Intermediate
4. Can a sealed class and its permitted subclasses be in different packages?
5. What happens if a switch on a sealed type is not exhaustive?
6. How do sealed interfaces work with records?

## Advanced
7. Explain the relationship between sealed classes and the Visitor pattern.
8. How does the compiler track permitted subclasses across compilation units?
9. Can a permitted subclass be in a different module? What are the rules?
10. How do sealed types enable algebraic data types (ADTs)?

## Expert
11. What is the `non-sealed` modifier and when is it appropriate?
12. How do sealed classes interact with instanceof pattern matching?
13. Can a sealed class be abstract? What does that mean for exhaustiveness?
14. How does the JVM implement sealed class verification at runtime?
