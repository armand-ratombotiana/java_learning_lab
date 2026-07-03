# Generics — Internal Mechanics

## Signature Attribute

The compiler preserves generic type information in the `Signature` attribute of the class file for reflection and compilation of dependent code:

```
// Class file for Container<String>:
Signature: #<index> // Ljava/util/Container<Ljava/lang/String;>;
```

This is why `ParameterizedType` works in reflection — the info is in the class file, just not used by the JVM for dispatch.

## Reifiable vs Non-Reifiable Types

- **Reifiable**: Type info fully available at runtime — `List<?>`, `List<String>` (raw), `int[]`
- **Non-reifiable**: Type info erased — `List<String>` (parameterized), `List<List<String>>`

Operations that require reifiable types (like `instanceof`) are forbidden for non-reifiable types.

## Heap Pollution

When a variable of a parameterized type refers to an object not of that type:

```java
List<String> strings = new ArrayList<>();
List raw = strings;           // Raw type assignment
raw.add(42);                  // Heap pollution — Integer added to List<String>
String s = strings.get(0);   // Implicit cast fails — ClassCastException!
```

The compiler warns about unchecked operations that could cause heap pollution. Suppress with `@SuppressWarnings("unchecked")` only when you've verified safety.

## Unbounded Wildcards Are Reifiable

`List<?>` is reifiable because the compiler treats `?` as a specific but unknown type. The JVM can check `if (list instanceof List<?>)`.

## Checked Collections

`Collections.checkedList()`, `checkedSet()`, etc. wrap collections with runtime type checks:

```java
List<String> safe = Collections.checkedList(new ArrayList<>(), String.class);
// Throws ClassCastException at add time if wrong type
```

Useful for debugging heap pollution at the insertion point rather than at the retrieval point.

## Enum and Generic Type Parameters

Enums cannot have type parameters:

```java
enum MyEnum<T> { A, B }  // Error
```

## Anonymous Classes and Type Parameters

Anonymous classes inherit the enclosing context's type parameters:

```java
public class Outer<T> {
    public void method() {
        // Anonymous class has access to T:
        Supplier<T> supplier = new Supplier<T>() {
            public T get() { return null; }
        };
    }
}
```

## Covariance and Contravariance

Arrays are covariant (reified), generics are invariant (erased):

```java
String[] arr = new String[10];
Object[] objArr = arr;  // OK — covariance

List<String> list = new ArrayList<>();
List<Object> objList = list;  // Error — invariance
```

Use wildcards for covariance/contravariance in generics:
- `List<? extends String>` is covariant
- `List<? super String>` is contravariant
