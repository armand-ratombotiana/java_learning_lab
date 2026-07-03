# Mathematical Foundations of Sealed Classes

## Sum Types and Cardinality

Sealed classes implement **sum types** (tagged unions, disjoint unions). A sum type `T = A | B | C` has cardinality equal to the sum of the cardinalities of its variants:

```
|T| = |A| + |B| + |C|
```

For example:
```java
sealed interface EmptyOrInt permits Empty, IntWrapper {}
record Empty() implements EmptyOrInt {}
record IntWrapper(int value) implements EmptyOrInt {}
```

Cardinality: `|EmptyOrInt| = |Empty| + |IntWrapper| = 1 + 2³²`

The `Empty` variant has exactly one value (like the unit type), and `IntWrapper` has 2³² values (one for each possible `int`).

## The Sum-Product Duality

Sum types and product types are dual:

- **Product**: `Point = int × int` — a Point has BOTH x AND y
- **Sum**: `Shape = Circle + Rectangle` — a Shape is EITHER Circle OR Rectangle

This duality is formally expressed as:

```
(A × B) × C ≈ A × (B × C)     — product associativity
(A + B) + C ≈ A + (B + C)     — sum associativity
A × (B + C) ≈ A × B + A × C   — distributivity
```

The distributivity law is particularly useful: a record containing a sealed type is isomorphic to a sealed type containing records:

```java
// Either: record contains sealed field
record HasShape(String name, Shape shape) {}

// Equivalent to: sealed types containing records
sealed interface NamedShape permits NamedCircle, NamedRectangle {}
record NamedCircle(String name, double radius) implements NamedShape {}
record NamedRectangle(String name, double w, double h) implements NamedShape {}
```

## Exhaustiveness as Proof by Cases

Exhaustiveness checking corresponds to the logical principle of **proof by cases**:

```
To prove P(T) for all T:
  Case 1: T = A → prove P(A)
  Case 2: T = B → prove P(B)
  ...
  All cases covered → P(T) holds for all T
```

The compiler checks that the set of cases in a switch expression is a cover of the sealed type. Formally, for a sealed type `S` with permitted subtypes `P₁, ..., Pₙ`:

```
∀x ∈ S: ∃i ∈ {1, ..., n}: x instanceof Pᵢ
```

The compiler verifies that the union of the match sets of all case labels equals the set of all possible values of the input type.

## Fixpoints and Recursive Types

Sealed classes can define recursive types (like algebraic data types for recursive structures):

```java
sealed interface Tree permits Leaf, Node {}
record Leaf(int value) implements Tree {}
record Node(Tree left, Tree right) implements Tree {}
```

This defines a type satisfying the fixpoint equation:

```
Tree ≅ ℤ + Tree × Tree
```

The number of binary trees with `n` leaves follows the Catalan numbers:

```
C₀ = 1
Cₙ₊₁ = Σᵢ₌₀ⁿ Cᵢ · Cₙ₋ᵢ
```

The compiler's exhaustiveness checker handles recursive types through fixed-point computation.

## The Sealed Hierarchy as a Grammar

A sealed hierarchy can be viewed as a **grammar** for valid type structures:

```
Expr ::= Constant(int)
       | Negate(Expr)
       | Add(Expr, Expr)
       | Multiply(Expr, Expr)
```

Each production corresponds to a permitted subtype. The grammar is unambiguous: any valid expression tree has exactly one derivation (equivalently, one path through the sealed hierarchy).

## Boolean Algebra and Sealed Types

Sealed types with `non-sealed` subtypes lose exhaustiveness guarantees. This is analogous to introducing a "catch-all" in boolean algebra:

```
Exhaustive switch → all atoms covered
non-sealed subtype → introduces an "unknown" atom
```

The compiler cannot know the cardinality of a `non-sealed` subtype's extension, so exhaustive switching is impossible and a `default` case is required.

## Permits Clause as a Finite Set

The `permits` clause defines a finite set of type names. The compiler checks:

- **Membership**: `class C extends S` ⇒ `C ∈ permits(S)`
- **Exclusion**: `class D extends S` ∧ `D ∉ permits(S)` ⇒ compilation error
- **Completeness**: `permits(S) = {P₁, ..., Pₙ}` ⇒ each `Pᵢ` defined in the same module

This transforms inheritance from an open-world assumption ("any class may extend") to a closed-world assumption ("only these classes extend").
