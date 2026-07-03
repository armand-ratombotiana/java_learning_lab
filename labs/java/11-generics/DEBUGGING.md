# Generics — Debugging Strategies

## Deciphering Generic Compile Errors

Generic compile errors can be cryptic. Common patterns:

### "incompatible types: Object cannot be converted to String"

```java
// Cause: Raw type used
List list = new ArrayList();
String s = list.get(0);  // Error: Object → String

// Fix: Parameterize
List<String> list = new ArrayList<>();
```

### "unchecked call to add(E) as a member of raw type List"

```java
// Cause: Raw type
List list = new ArrayList();
list.add("hello");  // Warning: unchecked call

// Fix: Parameterize
List<String> list = new ArrayList<>();
```

### "type argument T is not within bounds of type-variable T"

```java
// Cause: Bounded type mismatch
public <T extends Number> void process(T value) { }
process("hello");  // Error: String is not within bounds

// Fix: Use correct type
process(42);  // OK
```

## Debugging ClassCastException at Runtime

Since generics are erased, a `ClassCastException` usually means heap pollution:

```java
List<String> strings = new ArrayList<>();
List raw = strings;
raw.add(42);  // Heap pollution — no compile error!
String s = strings.get(0);  // ClassCastException here
```

**Debug:** Add `-XX:+ShowCodeDetailsInExceptionMessages` (default in Java 15+). The NPE/Cast message shows the source.

## Using javap to Inspect Generic Signatures

```java
// MyClass.java
public class MyClass<T extends Comparable<T>> {
    public T process(T value) { return value; }
}
```

```shell
javap -verbose MyClass.class
# Shows:
# Signature: <T::Ljava/lang/Comparable<TT;>;>Ljava/lang/Object;
```

## Debugging Bridge Methods

When you see methods you didn't write in stack traces or decompiled code, they're bridge methods:

```shell
javap -verbose Name.class
# Shows synthetic bridge:
# public int compareTo(java.lang.Object);
#   descriptor: (Ljava/lang/Object;)I
#   flags: ACC_PUBLIC, ACC_BRIDGE, ACC_SYNTHETIC
```

## Common Patterns and Their Fixes

| Error | Likely Cause | Fix |
|-------|-------------|-----|
| `Object cannot be converted to String` | Raw type | Add type parameter |
| `unchecked cast` | Cast across erased type | Verify safety, add `@SuppressWarnings` |
| `incompatible types: List<Integer> cannot be converted to List<Number>` | Invariance confusion | Use `? extends Number` |
| `cannot find symbol: method add(String)` | Wrong wildcard | Use exact type or `? super String` |
| `type parameter T is hidden` | Shadowed type parameter | Rename one parameter |

## IntelliJ/IDE Debug Tips

1. Enable "show compiler warnings" — unchecked warnings appear as yellow squiggles
2. During debugging, variables show erased types — use expressions panel with casts
3. The "Structure" view shows bridge methods marked as synthetic
4. Code inspection: "Raw use of parameterized class" — enable as error in settings
