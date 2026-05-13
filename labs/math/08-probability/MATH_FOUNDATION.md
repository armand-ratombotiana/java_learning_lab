# MATH FOUNDATION: Proofs

## Probability Theorems

### Bayes' Theorem
P(A|B) = P(A∩B)/P(B) = P(B|A)P(A)/P(B)

Proof:
P(A|B) = P(A∩B)/P(B)
By definition of conditional probability:
P(A∩B) = P(B|A)P(A)
Therefore: P(A|B) = P(B|A)P(A)/P(B) ∎

### Central Limit Theorem Sketch
For i.i.d. random variables X₁, X₂, ..., Xₙ with mean μ and variance σ²:
Z = (X̄ - μ)/(σ/√n) → N(0,1) as n → ∞

Proof outline: Use characteristic functions and Lindeberg conditions.

### Law of Large Numbers
Sample mean X̄ = (1/n)ΣXᵢ → E[X] as n → ∞

Proof uses Chebyshev's inequality and Borel-Cantelli lemma.