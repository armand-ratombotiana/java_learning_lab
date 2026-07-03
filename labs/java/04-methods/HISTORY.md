# Methods — Evolution Across Java Versions

## Java 1.0 (1996)

Original method features: declaration, parameters, return types, overloading, `main` entry point. No varargs, no annotations.

## Java 5 (2004)

- **Varargs**: `void method(String... args)` — variable-length argument lists
- **Covariant return types**: Overriding method can return subtype
- **Annotations**: `@Override`, `@SuppressWarnings`, `@Deprecated`

## Java 7 (2011)

- **String in switch** (indirectly affects methods accepting String parameters)

## Java 8 (2014)

- **Lambdas**: Methods can accept functional interfaces as parameters — `process(x -> x * 2)`
- **Default methods**: Interfaces can define method bodies
- **Static methods in interfaces**: Utility methods in interfaces
- **Method references**: `String::length` as shorthand for `s -> s.length()`

## Java 10 (2018)

- **Local variable type inference in methods**: `var x = getValue();`

## Java 11 (2018)

- **Lambda parameter type inference**: `(var x, var y) -> x + y`

## Java 16 (2021)

- **Records**: Compact method declarations — `record Point(int x, int y) { }` generates constructor, accessors, equals, hashCode, toString

## Java 17 (2021)

- **Sealed classes**: Interfaces controlling which classes can implement them, affecting method dispatch

## Java 21 (2023)

- **Pattern matching for method parameters**: Deconstruction in method signatures

## Method Design Trends

Over time, Java methods have become more expressive with less boilerplate, while maintaining backward compatibility.
