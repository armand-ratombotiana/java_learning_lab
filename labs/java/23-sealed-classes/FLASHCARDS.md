# Flashcards: Sealed Classes

## Card 1
**Q**: What is a sealed class/interface?
**A**: A class/interface that restricts which other classes can extend/implement it via a `permits` clause.

## Card 2
**Q**: What is the syntax for a sealed interface with two subtypes?
**A**: `sealed interface Shape permits Circle, Rectangle {}`

## Card 3
**Q**: What three modifiers can a permitted subtype have?
**A**: `final` (no further extension), `sealed` (continues the sealed hierarchy), `non-sealed` (opens extension).

## Card 4
**Q**: What happens if a class tries to extend a sealed class without being in the permits clause?
**A**: Compilation error.

## Card 5
**Q**: Where must permitted subtypes be located?
**A**: In the same module (or same package for unnamed modules).

## Card 6
**Q**: Why were sealed classes introduced?
**A**: To enable exhaustive pattern matching, controlled inheritance, and compile-time verification of type hierarchies.

## Card 7
**Q**: How does sealed help with pattern matching?
**A**: The compiler knows all possible subtypes, so switch expressions can be verified as exhaustive without a default case.

## Card 8
**Q**: What does `non-sealed` mean?
**A**: The subtype opens the hierarchy; any class can extend it.

## Card 9
**Q**: What is the dual relationship between sealed classes and enums?
**A**: Enums restrict VALUES; sealed classes restrict SUBTYPES. Both enable exhaustive switching.

## Card 10
**Q**: Can a sealed class be abstract?
**A**: Yes. A sealed abstract class can have abstract methods that permitted subtypes implement.

## Card 11
**Q**: What is the reflection method to check if a class is sealed?
**A**: `Class.isSealed()` returns `boolean`.

## Card 12
**Q**: How do you get the permitted subclasses via reflection?
**A**: `Class.getPermittedSubclasses()` returns `Class<?>[]`.

## Card 13
**Q**: Can records be permitted subtypes of sealed classes?
**A**: Yes. Records are implicitly `final`, making them natural leaf types.

## Card 14
**Q**: What is the difference between `final` and `sealed`?
**A**: `final` prevents all subclassing. `sealed` allows controlled subclassing through its own `permits` clause.

## Card 15
**Q**: Can a sealed class have no permitted subtypes?
**A**: No, a sealed class must have at least one permitted subtype (unless all subtypes are in the same compilation unit).

## Card 16
**Q**: What JEP finalized sealed classes?
**A**: JEP 409, in Java 17.

## Card 17
**Q**: What bytecode flag is set for sealed classes?
**A**: `ACC_SEALED` (0x0400) in the class access flags.

## Card 18
**Q**: What class file attribute stores the permitted subtypes?
**A**: The `PermittedSubclasses` attribute.

## Card 19
**Q**: Can sealed classes span multiple packages?
**A**: Yes, if they are in the same module.

## Card 20
**Q**: What is the relationship between sealed classes and the Visitor pattern?
**A**: Sealed classes + pattern matching replace the Visitor pattern for many use cases, eliminating the need for accept/visit boilerplate.
