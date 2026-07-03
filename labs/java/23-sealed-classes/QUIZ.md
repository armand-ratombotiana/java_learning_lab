# Quiz: Sealed Classes

## Question 1
Which keyword is used to declare a sealed class or interface?

A) `closed`
B) `restricted`
C) `sealed`
D) `final`

## Question 2
What does the `permits` clause do?

A) Allows a class to extend multiple sealed classes
B) Lists which classes can extend or implement the sealed type
C) Permits the sealed class to access private members of subclasses
D) Enables serialization of sealed types

## Question 3
Which modifiers are valid for a permitted subtype of a sealed class?

A) `final`, `abstract`, `public`
B) `final`, `sealed`, `non-sealed`
C) `final`, `open`, `closed`
D) `final`, `public`, `protected`

## Question 4
Can a sealed class be instantiated directly?

A) Yes, always
B) Yes, if it's not abstract
C) No, sealed classes are always abstract
D) Only through a factory method

## Question 5
Where must permitted subtypes be located?

A) In the same file
B) In the same package
C) In the same module (or same package for unnamed modules)
D) In the same JAR file

## Question 6
What happens if you try to extend a sealed class without being in its permits clause?

A) Compilation error
B) Runtime ClassCastException
C) It works but with a warning
D) The subclass is automatically added to the permits clause

## Question 7
Which Java version finalized sealed classes?

A) Java 15
B) Java 16
C) Java 17
D) Java 21

## Question 8
What is the purpose of the `non-sealed` modifier on a permitted subtype?

A) To mark the subtype as extensible by anyone
B) To remove the subtype from the sealed hierarchy
C) To allow the subtype to have non-final fields
D) To indicate that the subtype doesn't implement all interface methods

## Question 9
How does sealed class information benefit pattern matching?

A) It enables exhaustive switch without a default case
B) It makes instanceof checks faster
C) It allows pattern matching on primitive types
D) It enables wildcard patterns

## Question 10
Can a record be a permitted subtype of a sealed class?

A) No, records cannot participate in inheritance
B) Yes, records are implicitly final
C) Yes, but only if they are marked `sealed`
D) No, records must extend Record explicitly

## Answer Key
1. C, 2. B, 3. B, 4. B (if non-abstract, yes), 5. C, 6. A, 7. C, 8. A, 9. A, 10. B
