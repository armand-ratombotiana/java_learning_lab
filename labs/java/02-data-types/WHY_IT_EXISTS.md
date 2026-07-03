# Why Data Types Exist

## The Problem Data Types Solve

Without type information, computer memory is just bytes — 0s and 1s with no inherent meaning. Data types provide the interpretation: the same sequence of bits `01000001` could be the integer 65, the character 'A', or part of a floating-point number. Types resolve this ambiguity.

## Historical Context

Early languages (assembly, FORTRAN) had minimal type systems. C introduced a richer type system but left many details to the implementation (int could be 16 or 32 bits depending on platform). Java's designers wanted:
- **Fixed sizes**: `int` is always 32 bits, regardless of platform
- **Safety**: No pointer arithmetic, array bounds checking, type-safe casts
- **Simplicity**: Fewer types than C++, without C's confusing type rules

Java's 8 primitives were chosen as the minimum set for practical computation. The omission of unsigned types was controversial but simplified the language.

## Why Wrappers Exist

Objects need to be treated uniformly (e.g., stored in Collections). Since primitives aren't objects, wrapper classes bridge the gap. Before Java 5, manual boxing was required. Autoboxing/unboxing added syntactic convenience while maintaining type safety.
