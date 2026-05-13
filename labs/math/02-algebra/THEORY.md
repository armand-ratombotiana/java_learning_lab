# Algebra: Equations, Polynomials, and Factoring

## 1. Fundamental Concepts

### 1.1 Algebraic Expressions
- **Variable**: A symbol representing an unknown value (x, y, z)
- **Coefficient**: The numerical factor of a term
- **Term**: A single number, variable, or product
- **Expression**: Combination of terms with operations

### 1.2 Basic Laws
- Commutative: a + b = b + a, ab = ba
- Associative: (a+b)+c = a+(b+c), (ab)c = a(bc)
- Distributive: a(b+c) = ab + ac
- Identity: a + 0 = a, a × 1 = a
- Inverse: a + (-a) = 0, a × (1/a) = 1

## 2. Linear Equations

### 2.1 One-Variable Linear Equations
Form: ax + b = 0, where a ≠ 0
Solution: x = -b/a

**Steps to solve:**
1. Simplify both sides
2. Move variable terms to one side
3. Move constants to other side
4. Divide by coefficient

### 2.2 Two-Variable Linear Equations
Form: ax + by = c

**Methods to solve:**
- Substitution
- Elimination
- Graphical method
- Matrix method

### 2.3 Systems of Linear Equations
- **Substitution**: Solve one equation, substitute
- **Elimination**: Add/subtract equations to eliminate variable
- **Cramer's Rule**: Using determinants
- **Matrix Method**: Ax = b → x = A⁻¹b

## 3. Quadratic Equations

### 3.1 Standard Form
ax² + bx + c = 0, where a ≠ 0

### 3.2 Factoring Method
- Find two numbers that multiply to ac and add to b
- Example: x² + 5x + 6 = (x+2)(x+3)

### 3.3 Quadratic Formula
x = (-b ± √(b²-4ac)) / 2a

**Discriminant**: Δ = b² - 4ac
- Δ > 0: Two distinct real roots
- Δ = 0: One repeated real root
- Δ < 0: Two complex conjugate roots

### 3.4 Completing the Square
1. Move constant: x² + bx = -c
2. Add (b/2)² to both sides
3. Factor: (x + b/2)² = value
4. Solve

### 3.5 Vieta's Formulas
- Sum of roots: α + β = -b/a
- Product of roots: αβ = c/a

## 4. Polynomials

### 4.1 Definition
P(x) = aₙxⁿ + aₙ₋₁xⁿ⁻¹ + ... + a₁x + a₀

**Degree**: Highest power of x with non-zero coefficient

### 4.2 Operations
- **Addition**: Combine like terms
- **Subtraction**: Distribute negative, combine
- **Multiplication**: Use distributive property, FOIL
- **Division**: Long division, synthetic division

### 4.3 Division Algorithm
For polynomials P(x) and D(x):
P(x) = Q(x)·D(x) + R(x)
where deg(R) < deg(D) or R = 0

### 4.4 Remainder Theorem
P(a) = remainder when P(x) divided by (x-a)

### 4.5 Factor Theorem
(x-a) is a factor of P(x) iff P(a) = 0

### 4.6 Rational Root Theorem
If P(x) has rational root p/q (in lowest terms), then:
- p divides constant term a₀
- q divides leading coefficient aₙ

## 5. Factoring Techniques

### 5.1 Common Factors
ab + ac = a(b + c)

### 5.2 Difference of Squares
a² - b² = (a + b)(a - b)

### 5.3 Perfect Square Trinomials
- a² + 2ab + b² = (a + b)²
- a² - 2ab + b² = (a - b)²

### 5.4 Sum/Difference of Cubes
- a³ + b³ = (a + b)(a² - ab + b²)
- a³ - b³ = (a - b)(a² + ab + b²)

### 5.5 Quadratic Trinomials
ax² + bx + c = (px + q)(rx + s)
where pq = ac and rs = c

### 5.6 Grouping
ab + ac + db + dc = (a + d)(b + c)

## 6. Rational Expressions

### 6.1 Simplification
P/Q simplified when P and Q have no common factor

### 6.2 Operations
- **Multiplication**: (A/B)·(C/D) = (AC)/(BD)
- **Division**: (A/B)÷(C/D) = (AD)/(BC)
- **Addition**: A/B + C/D = (AD + BC)/(BD)
- **Subtraction**: A/B - C/D = (AD - BC)/(BD)

### 6.3 Partial Fraction Decomposition
Used for integrating rational functions and solving differential equations.

## 7. Inequalities

### 7.1 Linear Inequalities
Solve like equations, but reverse sign when multiplying by negative

### 7.2 Quadratic Inequalities
1. Find roots of corresponding equation
2. Test intervals
3. Consider sign of leading coefficient

### 7.3 Absolute Value Inequalities
- |x| < a → -a < x < a
- |x| > a → x < -a or x > a

## 8. Sequences and Series

### 8.1 Arithmetic Sequence
aₙ = a₁ + (n-1)d
Sₙ = n(a₁ + aₙ)/2 = n(2a₁ + (n-1)d)/2

### 8.2 Geometric Sequence
aₙ = a₁·rⁿ⁻¹
Sₙ = a₁(1-rⁿ)/(1-r) for r ≠ 1

### 8.3 Infinite Geometric Series
S∞ = a₁/(1-r) for |r| < 1

## 9. Binomial Theorem

### 9.1 Formula
(a + b)ⁿ = Σₖ₌₀ⁿ C(n,k)aⁿ⁻ᵏbᵏ

### 9.2 Binomial Coefficients
C(n,k) = n!/[k!(n-k)!] = nCk

### 9.3 Pascal's Triangle
Row n gives coefficients for (a+b)ⁿ

## 10. Complex Numbers

### 10.1 Definition
z = a + bi where i² = -1

### 10.2 Operations
- Addition: (a+bi) + (c+di) = (a+c) + (b+d)i
- Multiplication: (a+bi)(c+di) = (ac-bd) + (ad+bc)i
- Conjugate: a - bi
- Modulus: |z| = √(a² + b²)

### 10.3 Polar Form
z = r(cos θ + i sin θ) = re^(iθ)
where r = |z|, θ = arg(z)

## 11. Matrices (Introduction)

### 11.1 Matrix Operations
- Addition: Same dimensions, element-wise
- Scalar multiplication: Multiply each element
- Matrix multiplication: (AB)ᵢⱼ = Σₖ AᵢₖBₖⱼ

### 11.2 Determinants
det(A) for 2×2: ad - bc
det(A) for 3×3: Use cofactor expansion

### 11.3 Matrix Methods
- Solve systems using Cramer's rule
- Find inverses using adjugate method

## 12. Functions

### 12.1 Function Definition
f: A → B where each element in A maps to exactly one in B

### 12.2 Types of Functions
- One-to-one (injective)
- Onto (surjective)
- Bijective (one-to-one and onto)

### 12.3 Function Operations
- Composition: (f∘g)(x) = f(g(x))
- Inverse: f⁻¹(f(x)) = x