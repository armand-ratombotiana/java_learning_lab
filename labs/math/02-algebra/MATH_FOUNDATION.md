# MATH FOUNDATION: Proofs

## Algebra Foundations

### Binomial Theorem Proof
By induction on n:
Base n=0: (a+b)⁰=1=ΣC(0,k)a⁰⁻ᵏbᵏ ✓
Assume true for n=k
(a+b)^{k+1} = (a+b)(a+b)^k = (a+b)ΣC(k,i)a^{k-i}b^i = ΣC(k,i)a^{k+1-i}b^i + ΣC(k,i)a^{k-i}b^{i+1}
= C(k,0)a^{k+1} + Σ[i=1 to k][C(k,i)+C(k,i-1)]a^{k+1-i}b^i + C(k,k)b^{k+1}
= ΣC(k+1,i)a^{k+1-i}b^i ✓

### Quadratic Formula Derivation
ax² + bx + c = 0
4a²x² + 4abx + 4ac = 0
(2ax+b)² = b²-4ac
2ax = -b ± √(b²-4ac)
x = (-b ± √(b²-4ac))/2a ∎

### AM-GM Inequality
(a+b)/2 ≥ √(ab)
Proof: (√a - √b)² ≥ 0
a - 2√(ab) + b ≥ 0
a + b ≥ 2√(ab)
(a+b)/2 ≥ √(ab) ∎