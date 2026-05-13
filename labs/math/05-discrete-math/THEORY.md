# Discrete Mathematics: Logic, Sets, Relations, and Functions

## 1. Propositional Logic

### 1.1 Basic Connectives
- Negation (¬p): not p
- Conjunction (p ∧ q): p and q
- Disjunction (p ∨ q): p or q
- Implication (p → q): if p then q
- Biconditional (p ↔ q): p if and only if q

### 1.2 Truth Tables
- p ∧ q is true only when both are true
- p ∨ q is false only when both are false
- p → q is false only when p is true and q is false

### 1.3 Logical Equivalences
- De Morgan's: ¬(p ∧ q) ≡ ¬p ∨ ¬q
- ¬(p ∨ q) ≡ ¬p ∧ ¬q
- p → q ≡ ¬p ∨ q
- p ↔ q ≡ (p → q) ∧ (q → p)

## 2. Predicate Logic

### 2.1 Predicates
- P(x) is a statement depending on x
- Universal: ∀x P(x)
- Existential: ∃x P(x)

### 2.2 Quantifiers
- ∀x ∈ ℝ: x² ≥ 0
- ∃x ∈ ℕ: x² = 4

## 3. Set Theory

### 3.1 Operations
- Union: A ∪ B
- Intersection: A ∩ B
- Difference: A \ B
- Complement: A'
- Cartesian product: A × B

### 3.2 Properties
- Commutative: A ∪ B = B ∪ A
- Associative: (A ∪ B) ∪ C = A ∪ (B ∪ C)
- Distributive: A ∪ (B ∩ C) = (A ∪ B) ∩ (A ∪ C)
- De Morgan: (A ∪ B)' = A' ∩ B'

### 3.3 Special Sets
- ∅: empty set
- ℕ: natural numbers
- ℤ: integers
- ℚ: rationals
- ℝ: reals

## 4. Relations

### 4.1 Properties
- Reflexive: ∀a, aRa
- Symmetric: aRb → bRa
- Transitive: aRb, bRc → aRc

### 4.2 Equivalence Relations
- Reflexive, symmetric, transitive
- Partitions set into equivalence classes

### 4.3 Partial Orders
- Reflexive, antisymmetric, transitive
- Example: ≤ on ℝ

## 5. Functions

### 5.1 Types
- One-to-one (injective): f(a) = f(b) → a = b
- Onto (surjective): ∀y ∈ B, ∃x ∈ A: f(x) = y
- Bijective: both injective and surjective

### 5.2 Operations
- Composition: (f ∘ g)(x) = f(g(x))
- Inverse: f⁻¹(f(x)) = x

## 6. Graph Theory Basics

### 6.1 Definitions
- Vertex: V
- Edge: E ⊆ V × V
- Degree: number of edges incident

### 6.2 Paths and Cycles
- Path: sequence of distinct vertices
- Cycle: path returning to start

## 7. Combinatorics Basics

### 7.1 Counting Principles
- Addition: |A ∪ B| = |A| + |B| - |A ∩ B|
- Multiplication: |A × B| = |A| × |B|

### 7.2 Permutations and Combinations
- P(n,r) = n!/(n-r)!
- C(n,r) = n!/[r!(n-r)!]