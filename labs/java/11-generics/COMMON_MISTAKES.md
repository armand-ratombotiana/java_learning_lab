# Generics — Common Mistakes and Anti-Patterns

## Mistake 1: Using Raw Types

```java
// BAD:
List list = new ArrayList();
list.add("hello");
String s = (String) list.get(0);  // Cast needed, no type safety

// GOOD:
List<String> list = new ArrayList<>();
list.add("hello");
String s = list.get(0);
```

**Why it's wrong**: Raw types bypass all generic type checking. The compiler issues a warning for a reason.

## Mistake 2: Confusing `? extends T` and `? super T`

```java
// BAD: Can't add to a producer
List<? extends Number> numbers = new ArrayList<Integer>();
numbers.add(42);  // Compile error!

// BAD: Can't read specific type from a consumer
List<? super Integer> ints = new ArrayList<Number>();
Integer i = ints.get(0);  // Compile error — returns Object

// GOOD:
List<Integer> ints = new ArrayList<>();
ints.add(42);
List<? extends Number> nums = ints;  // Read-only view
Number n = nums.get(0);
```

## Mistake 3: Treating Generics as Covariant

```java
// BAD: This does NOT compile
List<Integer> ints = List.of(1, 2, 3);
List<Number> nums = ints;  // Error: List<Integer> is not List<Number>

// GOOD:
List<? extends Number> nums = ints;
```

## Mistake 4: Creating Arrays of Parameterized Types

```java
// BAD:
List<String>[] array = new List<String>[10];  // Compile error

// Workaround:
List<String>[] array = new List[10];  // Raw type — warning!
@SuppressWarnings("unchecked")
List<String>[] array = (List<String>[]) new List<?>[10];  // Ugly but works
```

## Mistake 5: Ignoring Unchecked Warnings

```java
// BAD: Ignoring warning
@SuppressWarnings("unchecked")  // Without verifying safety!
public <T> T cast(Object o) { return (T) o; }

// GOOD: Only suppress when you've verified correctness
// Better: Use Class.cast() for safe casting
public <T> T cast(Object o, Class<T> type) {
    return type.cast(o);
}
```

## Mistake 6: Using instanceof with Parameterized Types

```java
// BAD:
if (list instanceof List<String>) { }  // Compile error

// WORKAROUND:
if (list instanceof List<?>) {
    @SuppressWarnings("unchecked")
    List<String> strings = (List<String>) list;
}
```

## Mistake 7: Static Fields of Type Parameter

```java
// BAD:
public class Cache<T> {
    private static T instance;  // Compile error!
}

// GOOD: Use a specific type or a different design
public class Cache<T> {
    private T instance;  // Instance field — OK
}
```

## Mistake 8: Overusing Wildcards

```java
// TOO NARROW:
public void process(List<? extends Object> list) { }

// BETTER (equivalent but simpler):
public void process(List<?> list) { }
```

## Mistake 9: Type Parameter Naming Confusion

Use conventional single-letter names:
- `E` — Element (collections)
- `K` — Key
- `V` — Value
- `T` — Type
- `R` — Return type
- `N` — Number

## Mistake 10: Assuming Reified Generics

Java generics are erased. `new T()`, `new T[]`, `T.class` are all compile errors. Use `Class<T>` tokens if runtime type info is needed.
