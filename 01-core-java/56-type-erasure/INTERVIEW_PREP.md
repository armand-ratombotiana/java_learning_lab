# Interview Preparation: Type Erasure

This document covers advanced questions related to the mechanics of type erasure, heap pollution, and reification.

## Q1: Explain exactly what happens to `<T extends Number>` during Type Erasure.
**Answer:**
Generics are a compile-time construct. During compilation, the Java compiler performs Type Erasure to ensure the resulting bytecode is compatible with older JVMs that do not understand generics.
For `<T extends Number>`, the compiler replaces every instance of the type parameter `T` with its first bound, which is `Number`. If it were an unbounded wildcard `<?>` or `<T>`, it would be replaced with `Object`. After replacing the types, the compiler inserts type casts where necessary in the calling code to preserve type safety.

## Q2: Why does Java forbid the creation of generic arrays (e.g., `new T[10]`)?
**Answer:**
Because of Type Erasure and Array Covariance.
Arrays in Java contain runtime type information about their component type (they are *reified*). If you try to put a `String` into an `Integer[]`, the JVM throws an `ArrayStoreException` at runtime.
Generics, however, are *erased* at runtime. If Java allowed `new T[10]`, the JVM wouldn't know what type of array to actually create; it would just create an `Object[]`. This would destroy the runtime type safety that arrays are supposed to guarantee, leading to unpredictable `ClassCastException`s later.

## Q3: What is "Heap Pollution", and how do you cause it?
**Answer:**
Heap Pollution occurs when a variable of a parameterized type refers to an object that is not of that parameterized type.
It is most commonly caused by mixing legacy "raw types" with generic types.
```java
List<String> strings = new ArrayList<>();
List rawList = strings; // Allowed for backward compatibility
rawList.add(new Integer(5)); // Heap Pollution!
```
The compiler allows this with only an unchecked warning. The list now physically contains an `Integer`, but the reference `strings` expects only `String`s. When you iterate over `strings`, the implicit cast inserted by the compiler will fail, throwing a `ClassCastException`.

## Q4: How does the "Super Type Token" pattern bypass Type Erasure?
**Answer:**
While local variables and method parameters lose their generic type information at runtime, the generic type information of a **Class definition** (its superclass and implemented interfaces) is preserved in the compiled `.class` file metadata.
The Super Type Token pattern exploits this. By creating an anonymous subclass of a generic abstract class (e.g., `new TypeReference<List<String>>() {}`), the generic parameter `List<String>` becomes part of the anonymous class's definition. Libraries (like Jackson or Gson) can use Reflection (`getClass().getGenericSuperclass()`) at runtime to read this metadata and accurately reconstruct complex generic types.

## Q5: What is the `@SafeVarargs` annotation used for?
**Answer:**
When you write a method that takes generic varargs (`public <T> void print(T... args)`), the compiler converts the varargs into an array (`T[] args`). Since generic arrays are erased to `Object[]`, this creates a risk of Heap Pollution. The compiler issues a warning on the method declaration.
If you, as the developer, can guarantee that your method only *reads* from the array and does not let the array escape the method's scope, the operation is safe. You apply the `@SafeVarargs` annotation to suppress the compiler warning and signal to other developers that the vararg usage is secure.