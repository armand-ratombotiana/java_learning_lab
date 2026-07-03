# Why Enums Exist

## The Problem: Integer Constants
Before Java 5, the standard pattern for constants was `public static final int`:
```java
public static final int MONDAY = 0;
public static final int TUESDAY = 1;
```
Problems: not type-safe (any int accepted), no namespace, no toString(), brittle (order matters), no iteration over values.

## The Problem: Typesafe Enum Pattern
The pre-Java 5 "typesafe enum" pattern used a class with private constructor and public static final instances. This worked but required significant boilerplate:
```java
public class Suit {
    private final String name;
    private Suit(String name) { this.name = name; }
    public static final Suit HEARTS = new Suit("Hearts");
    public static final Suit DIAMONDS = new Suit("Diamonds");
}
```

## The Solution
Enums provide language-level support with:
- Concise syntax (`enum Day { MONDAY, TUESDAY }`)
- Type safety (compiler rejects invalid values)
- Built-in methods (values(), valueOf(), ordinal(), name())
- Switch support
- Serialization safety
- Singleton guarantee
