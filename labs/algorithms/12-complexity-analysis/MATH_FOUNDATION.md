# Math Foundation for Complexity

## Formal Definitions
`
O(g(n)) = {f(n): ∃c,n₀>0 ∀n≥n₀ 0≤f(n)≤c·g(n)}
Ω(g(n)) = {f(n): ∃c,n₀>0 ∀n≥n₀ 0≤c·g(n)≤f(n)}
Θ(g(n)) = O(g(n)) ∩ Ω(g(n))
`

## log Properties
- n log b a = a log b n
- log a (n) = log b (n) / log b (a)

## Useful Summations
- Σᵢ₌₁ⁿ 1 = n
- Σᵢ₌₁ⁿ i = n(n+1)/2 = Θ(n²)
- Σᵢ₌₁ⁿ i² = n(n+1)(2n+1)/6 = Θ(n³)
- Σᵢ₌₁ⁿ 2ⁱ = 2ⁿ⁺¹ - 1 = Θ(2ⁿ)
- Σᵢ₌₁ⁿ 1/i = ln n + γ = Θ(log n)
