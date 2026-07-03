# Generics — Mathematical Foundation

## Type Theory Basics

Generics are rooted in **parametric polymorphism** (as opposed to ad-hoc polymorphism — overloading — or subtype polymorphism — inheritance). In type theory, a generic type `Box<T>` corresponds to a type constructor that takes a type argument and returns a concrete type.

## The Substitution Principle (Liskov Substitution)

**LSP**: If `S` is a subtype of `T`, then objects of type `T` can be replaced with objects of type `S`.

With generics, this principle interacts with variance:

- **Invariant**: `Box<Number>` is not a subtype of `Box<Object>` nor `Box<Integer>`. Default for generics.
- **Covariant**: `Box<? extends Number>` — `Box<Integer>` is a subtype of `Box<? extends Number>`.
- **Contravariant**: `Box<? super Number>` — `Box<Object>` is a subtype of `Box<? super Number>`.

## Variance Formulas

Let `≤` denote subtype relation.

- **Covariance**: If `A ≤ B` then `F<A> ≤ F<B>`. In Java: `F<? extends A>`.
- **Contravariance**: If `A ≤ B` then `F<B> ≤ F<A>`. In Java: `F<? super B>`.
- **Invariance**: Neither direction. Default in Java generics.

## The Get/Put Principle

Mathematically, for any type `T`:

- Reading from a structure yields values of type `T` → producer, use `? extends T` (covariant)
- Writing to a structure accepts values of type `T` → consumer, use `? super T` (contravariant)
- Both reading and writing → use exact type (invariant)

This is the PECS principle formalized.

## Type Erasure as a Forgetful Functor

In category theory terms, type erasure is a forgetful functor from the category of generic types to the category of raw types. It "forgets" the type parameter structure while preserving the underlying shape (bytecode). This is why generic type information is available for reflection (stored in class file metadata) but not for runtime dispatch.

## Bounded Quantification

`<T extends Number>` corresponds to **bounded universal quantification** in type theory: "for all types T that are subtypes of Number, the following definition holds."

`? extends Number` is an **existential type**: "there exists some type that is a subtype of Number." You can read `Number` values from it (the existential is unpacked to its bound), but you cannot insert values of any specific subtype.

## The Wildcard Capture Rule

When the compiler processes `List<?>`, it creates a fresh type variable `X` (the capture). The wildcard `?` is replaced with `X` in internal reasoning. Capture conversion allows the compiler to treat `X` as a specific (but unknown) type, enabling operations like `containsAll` that depend only on `Object` methods.

## Recursive Type Bounds

```java
<T extends Comparable<T>>
```

This declares that `T` must be comparable to itself — a recursive bound. Mathematically, this constrains `T` to types that implement `Comparable<Self>`. Used in `Collections.sort()` and natural ordering.

## The Diamond Operator as Type Inference

`new ArrayList<>()` is a syntactic convenience for **type inference via unification**. The compiler unifies the expected type (from the left side or method call context) with the constructor's type parameter, solving for the type argument.
