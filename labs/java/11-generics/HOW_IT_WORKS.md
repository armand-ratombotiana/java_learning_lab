# Generics — How It Works

## Step 1: Declaration with Type Parameters

A generic class is declared with formal type parameters:

```java
public class Container<T> {
    private T item;
    public T get() { return item; }
    public void set(T item) { this.item = item; }
}
```

`T` is a placeholder — it will be replaced with an actual type at usage sites.

## Step 2: Usage — Type Argument Substitution

When using the class, supply a type argument:

```java
Container<String> c = new Container<>();
c.set("hello");
String s = c.get();  // No cast needed
```

The compiler performs **type checking**: `c.set(42)` would fail because `42` is not `String`.

## Step 3: Compilation — Type Erasure

The compiler erases type parameters to their leftmost bound:

```java
// Source:
Container<String> c = new Container<>();
c.set("hello");
String s = c.get();

// After erasure:
Container c = new Container();
c.set("hello");
String s = (String) c.get();
```

- `Container<T>` becomes `Container` (raw)
- `T` in fields/methods becomes `Object` (since T is unbounded)
- Compiler inserts casts where needed

## Step 4: Bridge Methods

When a generic interface is implemented, the compiler generates bridge methods to preserve polymorphism:

```java
// Generic interface:
interface Comparable<T> {
    int compareTo(T o);
}

// Implementation:
class Name implements Comparable<Name> {
    public int compareTo(Name o) { ... }
}

// Compiler generates bridge:
public int compareTo(Object o) {  // synthetic bridge
    return compareTo((Name) o);
}
```

The bridge delegates to the typed method, maintaining correct virtual dispatch.

## Step 5: Wildcard Capture

When the compiler encounters `?`, it creates a fresh type variable (capture):

```java
public void swap(List<?> list, int i, int j) {
    // Compiler infers: List<CAP#1> where CAP#1 is a fresh type variable
    // Can only read as Object, cannot add (except null)
}
```

## Step 6: Checked vs Unchecked

```
Compile-time:
  - Type checking on generic declarations
  - Erasure removes type info
  - Casts inserted at call sites
  - Bridge methods generated

Runtime:
  - No generic type info in bytecode (except signatures attribute — debugging/reflection)
  - instanceof checks work on erased types only
  - Class objects do not carry type parameters
```
