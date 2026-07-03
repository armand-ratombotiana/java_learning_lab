# Debugging: Enums

## Common Issues

### Issue 1: Enum ordinal Changes
```java
// Version 1
enum Status { PENDING, ACTIVE, COMPLETED }

// Version 2 (new constant added)
enum Status { PENDING, ACTIVE, IN_REVIEW, COMPLETED }
// ALL ordinals shifted after ACTIVE!
```
Never rely on ordinal() for persistence or comparison. Use a custom id field.

### Issue 2: Enum with Mutable State
```java
// BAD
enum Country {
    USA, CANADA, UK;
    int population;  // Mutable!
}
```
Enum constants are singletons. Mutable state is shared across all users. This can cause thread-safety issues and unexpected behavior.

### Issue 3: Enum Deserialization
```java
// BAD: Trying to deserialize removed constant
Status s = Status.valueOf("DELETED");  // IllegalArgumentException if DELETED no longer exists
```
Use a custom lookup method that handles missing values gracefully.

### Issue 4: Enum in Switch Without Default
```java
switch (day) {
    case MONDAY: break;
    case TUESDAY: break;
    // WEDNESDAY missing — compiler won't warn (unlike sealed classes)
}
```
Use exhaustive switch over enum (Java 17+ pattern matching with switch can help).

### Issue 5: Enum.values() Returns New Array
`values()` creates a clone each call. Don't call it in a tight loop — cache it:
```java
private static final Day[] DAYS = Day.values();
```

## Debugging Techniques
- Enum constants show name and ordinal in debugger
- EnumSet.toString() shows `[MONDAY, WEDNESDAY]`
- `javap -p EnumClass` shows the synthetic methods and fields
- Use `java.lang.Enum.getEnumConstants()` via reflection for dynamic access
