# Exercises: Discrete Math

**1.** Evaluate: (p ∧ q) ∨ ¬r when p=T, q=T, r=F
- Solution: (T ∧ T) ∨ ¬F = T ∨ T = T (True)

**2.** Prove: ¬(p ∨ q) ≡ ¬p ∧ ¬q
- Solution: Using truth table or De Morgan's law. Both sides have same truth values for all combinations of p,q. Left: (F,F)=T, others=F. Right: (F,F)=T, others=F. Equivalent.

**3.** Find: |A ∪ B| if |A|=5, |B|=7, |A∩B|=2
- Solution: |A ∪ B| = |A| + |B| - |A ∩ B| = 5 + 7 - 2 = 10

**4.** Is relation R={(1,1),(2,2),(1,2)} reflexive? Symmetric?
- Solution: Reflexive? No (missing (3,3) if set has 3 elements, but on {1,2} yes it's reflexive). Symmetric? No (has (1,2) but not (2,1)).

**5.** Find number of 5-digit numbers using digits {1,2,3} (repetition allowed)
- Solution: 3⁵ = 243 (each of 5 positions can be 1 of 3 digits)

**6.** Prove: A × (B ∪ C) = (A × B) ∪ (A × C)
- Solution: Show (a,b) ∈ LHS ⟺ (a,b) ∈ RHS. (a,b) ∈ A×(B∪C) means a∈A and (b∈B or b∈C) ⟺ (a∈A and b∈B) or (a∈A and b∈C) ⟺ (a,b) ∈ (A×B) ∪ (A×C).

**7.** Show that relation "x divides y" is a partial order
- Solution: Need reflexive: x|x. Antisymmetric: if x|y and y|x then x=y. Transitive: if x|y and y|z then x|z. All hold for positive integers.

**8.** Count: Number of ways to arrange "DISCRETE"
- Solution: "DISCRETE" has 8 letters with D,I,S,C,R,E,T + one more E. Total = 8!/2! = 20160

**9.** Find all equivalence classes of relation mod 3 on ℤ
- Solution: Three classes: [0] = {...,-6,-3,0,3,6,...}, [1] = {...,-5,-2,1,4,7,...}, [2] = {...,-4,-1,2,5,8,...}

**10.** Prove: (f ∘ g) is injective if f and g are injective
- Solution: If (f∘g)(x₁) = (f∘g)(x₂), then f(g(x₁)) = f(g(x₂)). Since f is injective, g(x₁) = g(x₂). Since g is injective, x₁ = x₂. Therefore (f∘g) is injective.