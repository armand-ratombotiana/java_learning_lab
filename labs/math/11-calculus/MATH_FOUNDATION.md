# MATH FOUNDATION: Proofs

## Calculus Fundamentals

### Fundamental Theorem of Calculus
If F(x) = ∫ₐˣ f(t)dt, then F'(x) = f(x)

Proof using limit definition:
F'(x) = lim(h→0) [∫ₐ^{x+h}f(t)dt - ∫ₐˣf(t)dt]/h
= lim(h→0) ∫ₓ^{x+h}f(t)dt/h
By Mean Value Theorem for integrals, ∫ₓ^{x+h}f(t)dt = f(c)·h for some c∈[x,x+h]
As h→0, c→x, and since f is continuous, f(c)→f(x)
Therefore F'(x) = f(x) ∎

### Taylor's Theorem
f(x) = Σf⁽ⁿ⁾(a)/n! · (x-a)ⁿ + Rₙ

Proof by repeated integration by parts and bounding the remainder term Rₙ.