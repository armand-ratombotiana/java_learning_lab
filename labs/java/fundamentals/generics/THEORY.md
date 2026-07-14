# Generics Theory & Intuition

## 💡 The Problem: Runtime ClassCastException
Before Java 5, collections like `ArrayList` stored everything as `Object`. 
- **The Risk**: You could add a `String` to a list intended for `Integer`s. The compiler wouldn't complain.
- **The Crash**: When you retrieved the item and tried to cast it to `Integer`, the application crashed with a `ClassCastException`.

## 🚀 The Solution: Generics
Generics allow you to parameterize types. Instead of an "ArrayList of anything", you declare an `ArrayList<Integer>`.
- **Compile-Time Safety**: The compiler now checks that you only add Integers. If you try to add a String, the code won't even compile.
- **Elimination of Casts**: Since the compiler knows the list contains Integers, you don't need to manually cast the result of `get()`.

## 🔄 Type Variance and Wildcards
A common misconception is that if `Integer` is a subtype of `Number`, then `List<Integer>` is a subtype of `List<Number>`. **This is False.**
Generics are **Invariant**. A `List<Number>` cannot accept a `List<Integer>` because if it did, you could then add a `Double` to that list, breaking the type guarantee of the original `List<Integer>`.

To handle this, Java introduced **Wildcards**:
1. **Upper Bounded (`? extends T`)**: Can read items of type T, but cannot write to it safely.
2. **Lower Bounded (`? super T`)**: Can write items of type T, but cannot read them as T safely.

## 🍎 The PECS Principle
**Producer Extends, Consumer Super**.
- Use `extends` when you only want to **get** (read) values out of a collection (the collection is a Producer).
- Use `super` when you only want to **put** (write) values into a collection (the collection is a Consumer).