# Why Generics Exist

## The Problem: Type-Unsafe Collections

Before generics (Java 5), collections held `Object` — you could put any type in a `List` and had to cast when retrieving:

```java
List list = new ArrayList();
list.add("hello");
list.add(42);  // Accidentally added Integer
String s = (String) list.get(1);  // ClassCastException at runtime!
```

The mistake was only caught at runtime, potentially far from the source.

## Solution: Compile-Time Type Checking

Generics attach type information to declarations so the compiler can verify correctness:

```java
List<String> list = new ArrayList<>();
list.add("hello");
list.add(42);  // Compile error!
String s = list.get(0);  // No cast needed
```

## Why Not Just Subclass?

You could create `StringList`, `IntegerList`, etc., but:
- Exponential duplication — every type needs its own class
- Cannot write generic algorithms (sorting, searching) that work for any type
- No way to express constraints like "must be comparable"

Generics solve all these problems with a single parameterized abstraction.

## Historical Context

- **Java 1.0-1.4**: No generics. Collections used `Object`. Casting everywhere.
- **Java 5 (2004)**: Generics introduced via JSR 14 with backward-compatible type erasure
- **Why erasure?** Backward compatibility — generic code (`List<String>`) works with non-generic libraries (`List`). The JVM never changed; only the compiler did.
- **Alternative approach**: .NET generics are reified (type info preserved at runtime). Java chose erasure for compatibility with existing bytecode.
