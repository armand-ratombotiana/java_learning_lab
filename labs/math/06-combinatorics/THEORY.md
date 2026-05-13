# Combinatorics: Permutations, Combinations, and Probability

## 1. Fundamental Counting Principles

### 1.1 Addition Principle
If event A has m outcomes and event B has n outcomes, and they are mutually exclusive:
|A ∪ B| = m + n

### 1.2 Multiplication Principle
If task A has m ways and task B has n ways (independent):
|A × B| = m × n

## 2. Permutations

### 2.1 Definition
Arrangement of objects in a specific order

### 2.2 Formula
P(n,r) = n!/(n-r)!

### 2.3 Types
- With repetition: n^r
- Circular: (n-1)!
- Distinct objects: n!

## 3. Combinations

### 3.1 Definition
Selection without regard to order

### 3.2 Formula
C(n,r) = n!/[r!(n-r)!]

### 3.3 Properties
- C(n,k) = C(n,n-k)
- C(n,0) = C(n,n) = 1
- Pascal's identity: C(n,k) = C(n-1,k-1) + C(n,k-1)

## 4. Binomial Theorem

### 4.1 Formula
(x + y)^n = Σ C(n,k)x^(n-k)y^k

### 4.2 Coefficients
Row n of Pascal's triangle

## 5. Probability Basics

### 5.1 Definitions
- Sample space (S): all possible outcomes
- Event (E): subset of S
- P(E) = |E|/|S|

### 5.2 Axioms
- P(S) = 1
- 0 ≤ P(E) ≤ 1
- P(E ∪ F) = P(E) + P(F) - P(E ∩ F)

## 6. Conditional Probability

### 6.1 Definition
P(A|B) = P(A ∩ B)/P(B)

### 6.2 Independence
P(A ∩ B) = P(A) × P(B)

## 7. Bayes' Theorem

P(A|B) = P(B|A) × P(A) / P(B)