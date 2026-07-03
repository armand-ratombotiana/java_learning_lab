# Internals: Enums in Java

## Enum Class Structure
Every enum in Java is compiled to a final class that extends `java.lang.Enum<E>`. For example:

```java
// Source
enum Color { RED, GREEN, BLUE }
```

```java
// Compiled form (simplified)
public final class Color extends Enum<Color> {
    public static final Color RED = new Color("RED", 0);
    public static final Color GREEN = new Color("GREEN", 1);
    public static final Color BLUE = new Color("BLUE", 2);
    
    private static final Color[] $VALUES = { RED, GREEN, BLUE };
    
    public static Color[] values() { return $VALUES.clone(); }
    public static Color valueOf(String name) { return Enum.valueOf(Color.class, name); }
    
    private Color(String name, int ordinal) { super(name, ordinal); }
}
```

## Enum.valueOf()
The generated `valueOf()` delegates to `java.lang.Enum.valueOf()`, which uses reflection (`Class.enumConstantDirectory`) to look up the constant by name. This is a HashMap lookup, so O(1) average.

## Enum ordinal
Each enum constant has an ordinal (position index starting from 0). The ordinal is assigned by the compiler in declaration order. Relying on ordinal() is fragile; use a custom field instead.

## EnumSet and EnumMap
- `EnumSet` uses a bit vector (long or long[]) internally. For enums with ≤64 constants, it uses a single long (bitmask). Operations are O(1) bitwise operations.
- `EnumMap` uses an array indexed by ordinal. Faster than HashMap (no hashing, direct array access).

## Enum Serialization
Enum serialization uses the constant name, not the instance fields. On deserialization, `readObject()` returns `Enum.valueOf(class, name)` — the existing singleton instance. This guarantees the singleton property even across serialization.

## Enum Compiler Support
The compiler restricts:
- Enums cannot extend other classes (they implicitly extend Enum)
- Enums cannot have public constructors
- Enums are implicitly final
- Switch on enum uses a lookup table (tableswitch or lookupswitch bytecode)
