# Mathematical Foundations of Pattern Matching

## Algebraic Data Types

Pattern matching in Java is based on **algebraic data types (ADTs)** — defined as combinations of product types (records) and sum types (sealed classes).

### Product Types
A product type represents a combination of values, like a tuple or record:

```
Point ≅ int × int
```

The number of distinct `Point` values is the product of the number of `int` values: `2³² × 2³² = 2⁶⁴`.

### Sum Types
A sum type represents a choice between alternatives:

```
Shape ≅ Circle + Rectangle + Triangle
```

The number of distinct `Shape` values is the sum of the number of values of each variant.

### Pattern Matching as a Universal Property
Pattern matching on a sum type is characterized by a **universal property**:

For any type `T`, given functions:
- `f₁: Circle → T`
- `f₂: Rectangle → T`
- `f₃: Triangle → T`

There exists a unique function `match: Shape → T` such that:
- `match(new Circle(...)) = f₁(new Circle(...))`
- `match(new Rectangle(...)) = f₂(new Rectangle(...))`
- `match(new Triangle(...)) = f₃(new Triangle(...))`

This is exactly what a pattern matching switch expression does.

## The Exhaustiveness Principle

Exhaustiveness checking is based on the **principle of proof by cases**:

```
To prove ∀x: S, P(x):
  ∀cᵢ ∈ subtypes(S):
    Let x be a cᵢ (with component bindings):
      Prove P(x)
  → Therefore, ∀x: S, P(x)
```

The compiler verifies that the cases form a **cover** of the type:

```
cases = {c₁, c₂, ..., cₙ}
cover = ⋃ matchSet(cᵢ) for i = 1 to n
exhaustive ⇔ cover ⊇ domain(S)
```

## Pattern Matching as a Function

A switch expression with patterns can be seen as a **piecewise function**:

```
f: T → R

f(x) = f₁(x)  if x ∈ matchSet(p₁)
     = f₂(x)  if x ∈ matchSet(p₂) \ matchSet(p₁)
     = f₃(x)  if x ∈ matchSet(p₃) \ (matchSet(p₁) ∪ matchSet(p₂))
     ...
     = fₙ(x)  if x ∈ matchSet(pₙ) \ (⋃ matchSet(pᵢ) for i < n)
     = f_default(x)  otherwise
```

The patterns are checked in order, and each subsequent pattern only matches values not matched by previous patterns.

## Semantics of Guards

A guarded pattern `p when g` has the match set:

```
matchSet(p when g) = {x | x ∈ matchSet(p) ∧ g(x) = true}
```

This is a **subset** of `matchSet(p)`.

Two patterns with different guards may have overlapping match sets:

```
matchSet(Integer i when i > 0) ∩ matchSet(Integer i when i < 0) = ∅
```

The compiler does not analyze guard conditions for dominance — it only checks that the type patterns are in order.

## Record Patterns as Composition

A record pattern `R(p₁, ..., pₙ)` has a match set that is the **composition** of the record's projection functions with the nested patterns:

```
matchSet(R(p₁, ..., pₙ)) = 
    {x | x instanceof R ∧ 
         π₁(x) ∈ matchSet(p₁) ∧
         ... ∧
         πₙ(x) ∈ matchSet(pₙ)}
```

Where `πᵢ(x)` is the i-th accessor of record `R`.

For nested destructuring:

```
matchSet(Line(Point(int x1, int y1), Point(int x2, int y2))) =
    {x | x instanceof Line ∧
         π₁(x) instanceof Point ∧
         π₁(π₁(x)) instanceof Integer ∧
         ...
         }
```

## Binding and Scope

Pattern variable binding can be formalized as:

```
variable v = cast(selector, T)  if selector instanceof T
```

The scope of a pattern variable is defined by the **definitely matched** region:

```
∃p ∈ cases: selector ∈ matchSet(p) ⇒ v ∈ scope
```

This is a static approximation (the compiler doesn't know which case will match) — the variable is in scope in any region where it's guaranteed that the pattern matched.
