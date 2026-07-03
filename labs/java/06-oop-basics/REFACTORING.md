# Refactoring OOP Basics

## Encapsulate Fields

Before: `public String name;`
After: `private String name; public String getName() { return name; }`

## Use Constructor Overloading

Before: Multiple separate constructors with different params.
After: Chain constructors: `this(name, 0);`

## Make Immutable Objects

Before: Mutable class with setters.
After: Final fields, no setters, return new instances from "mutating" methods.

## Replace Utility Methods with Static Imports

Before: `Math.max(a, b)` used many times in a class.
After: `import static java.lang.Math.max;` then `max(a, b)`.

## Extract Builder Pattern

Before: Constructor with many parameters.
After: `new PersonBuilder().name("Alice").age(30).build();`

## Use Factory Method Instead of Constructor

Before: `new Point(0, 0);`
After: `Point.origin();` or `Point.of(0, 0);`
