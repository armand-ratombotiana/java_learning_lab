# Why Arrays & Strings Exist

## The Problem Arrays Solve

Variables hold a single value. To process 1000 numbers without arrays would require 1000 separate variables. Arrays provide indexed access to a contiguous block of elements — the foundation of efficient data processing.

## Historical Context

Arrays are one of the oldest data structures in programming (FORTRAN, 1957). Java's arrays are:
- **Objects**: Unlike C arrays, Java arrays know their length and check bounds
- **Type-safe**: `String[]` cannot hold integers
- **Covariant**: Permits polymorphic array handling but with runtime store checks

## The Problem Strings Solve

Characters are the building blocks of text. A `String` type provides a high-level abstraction over character sequences, with operations for search, comparison, concatenation, and transformation.

## Historical Context

C used null-terminated `char*` arrays — error-prone and unsafe. Java made `String` a first-class type from day one:
- **Immutable**: Thread-safe, can be shared, hashable
- **Unicode**: Uses UTF-16 natively (was progressive in 1995)
- **String pool**: Interning for memory efficiency
- **Literals**: `"hello"` is a String object, not a char array

`StringBuilder` was added in Java 5 to address `String` concatenation performance in loops. `StringBuffer` existed since Java 1.0 for thread-safe mutable strings.
