# Flashcards: Enums

## Card 1: Enum Definition
`enum Color { RED, GREEN, BLUE }` — defines a fixed set of constants.

## Card 2: Enum with Fields
Enums can have fields, constructors, and methods. Constructor called for each constant.

## Card 3: EnumSet
Bit-vector implementation for enum sets. Extremely compact and fast.

## Card 4: EnumMap
Array-backed map optimized for enum keys. O(1) operations.

## Card 5: ordinal()
Returns zero-based position of the enum constant. Avoid for business logic.

## Card 6: valueOf()
Converts string to enum constant: `Color.valueOf("RED")`

## Card 7: values()
Returns array of all enum constants: `Color.values()`

## Card 8: Enum Singleton
`enum Singleton { INSTANCE }` — serialization-safe singleton.

## Card 9: Behavior-Driven Enum
Abstract method implemented differently by each constant.

## Card 10: Enum Serialization
JVM serializes by name, not by field values. Deserialization returns the same singleton.
