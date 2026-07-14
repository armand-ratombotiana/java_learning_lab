# Generics Internals & Type Erasure

## 🔬 Type Erasure
Java Generics are implemented using **Type Erasure**. This was a controversial design choice made to ensure backward compatibility with code written before Java 5.

When you compile a generic class, the compiler:
1. Replaces all type parameters with their bounds (usually `Object`).
2. Inserts type casts where necessary to ensure type safety.
3. Generates **Bridge Methods** to preserve polymorphism in extended generic types.

**The Consequence**: At runtime, the JVM has no idea what the generic type was. A `List<String>` and a `List<Integer>` are exactly the same class: `ArrayList.class`.

## 🌉 Bridge Methods
Consider this scenario:
```java
public class Node<T> {
    public void setData(T data) { ... }
}
public class MyNode extends Node<Integer> {
    @Override
    public void setData(Integer data) { ... }
}
```
After erasure, `Node.setData` takes an `Object`. But `MyNode.setData` takes an `Integer`. This breaks polymorphism because the signatures don't match.
To fix this, the compiler generates a "Bridge Method" in `MyNode`:
```java
public void setData(Object data) {
    setData((Integer) data);
}
```

## 🛑 Heap Pollution
Heap pollution occurs when a variable of a parameterized type refers to an object that is not of that parameterized type. This usually happens when mixing generic and raw types, or when using varargs with generics.
It can lead to a `ClassCastException` at a location where there is no explicit cast, which is extremely difficult to debug.