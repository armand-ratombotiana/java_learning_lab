# Mathematical Foundations of Records

## Product Types

Records are the Java implementation of **product types** from type theory. A product type combines multiple types into a single composite type. The name comes from the cardinality formula:

A record `R` with components `C₁, C₂, ..., Cₙ` has a set of possible values that is the Cartesian product of the component value sets:

```
R ≅ C₁ × C₂ × ... × Cₙ
|R| = |C₁| · |C₂| · ... · |Cₙ|
```

For example, `record BoolPair(boolean a, boolean b)` has `2 × 2 = 4` possible values: `(F,F)`, `(F,T)`, `(T,F)`, `(T,T)`.

## The Curry-Howard Correspondence

Records relate to the **Curry-Howard correspondence**, which states that types correspond to logical propositions and programs correspond to proofs. Under this interpretation:

- A product type `A × B` corresponds to the logical conjunction "A AND B"
- An instance of a record is a proof that all its component types are inhabited
- The components are the proof terms for each conjunct

## Equality and Congruence

The automatically derived `equals()` implements **structural equality**: two record values are equal iff they are of the same type and all corresponding components are equal componentwise. This satisfies the standard equivalence relation properties:

- **Reflexivity**: `∀x: recordType, x.equals(x)`
- **Symmetry**: `∀x,y: recordType, x.equals(y) ⇔ y.equals(x)`
- **Transitivity**: `∀x,y,z: recordType, x.equals(y) ∧ y.equals(z) ⇒ x.equals(z)`

Additionally, records satisfy the **substitutability principle**: if `x.equals(y)`, then `x` and `y` can be used interchangeably in any pure function.

## The Algebraic Structure of Records

Records form a **monoid** under certain operations:

- **Unit type**: `record Unit() {}` — a record with no components, having exactly 1 value (the singleton)
- **Type isomorphism**: Two record types that have the same components (up to order and naming) are isomorphic

```
record Point(int x, int y) ≅ record Coordinates(int x, int y)
```

This isomorphism means we can transform between them without information loss.

## Decomposition via Accessors

The record accessors are **projection functions** that decompose product types:

```
π₁: Point → int    (where π₁(point) = point.x())
π₂: Point → int    (where π₂(point) = point.y())
```

These satisfy the universal property of products: for any type `T` with functions `f: T → int` and `g: T → int`, there exists a unique function `h: T → Point` such that `π₁ ∘ h = f` and `π₂ ∘ h = g`.

In Java terms, given two functions mapping something to integers, we can create a mapper to `Point`:

```java
Function<T, Point> h = t -> new Point(f.apply(t), g.apply(t));
```

## hashCode and the Birthday Paradox

The `hashCode()` of a record is a function from product types to integers:

```
hashCode: R → int
hashCode(x₁, ..., xₙ) = hash(combine(x₁.hashCode(), ..., xₙ.hashCode()))
```

where `combine` is typically done with `Objects.hash(...)` using the formula:

```
result = 31 * (31 * result + c₁.hashCode()) + c₂.hashCode() + ...
```

The **birthday paradox** applies: with `n` records, the probability of at least one hashCode collision is approximately `n²/(2·2³²)`. For large numbers of records, collisions are statistically inevitable but harmless (equals() resolves them).

## Generic Records and Parametric Polymorphism

Generic records implement **parametric polymorphism**:

```
record Pair<A, B>(A first, B second) {}
```

This is a **type constructor**: `Pair` takes two types and produces a record type. The type `Pair<String, Integer>` is different from `Pair<Integer, String>`. The type parameters are invariant by default.

## Records and Functors

A record with a single generic component can be seen as a **functor** in category theory terms. Consider:

```java
record Box<T>(T value) {}
```

The mapping `Box` from type `T` to type `Box<T>`, together with a mapping function:

```java
<A, B> Box<B> map(Function<A, B> f, Box<A> box) {
    return new Box<>(f.apply(box.value()));
}
```

satisfies the functor laws: identity and composition preservation.
