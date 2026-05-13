# MATH FOUNDATION: Proofs

## Combinatorics Proofs

### Binomial Coefficient Identity
C(n,k) = C(n-1,k-1) + C(n-1,k)

Proof:
C(n-1,k-1) + C(n-1,k) = (n-1)!/[(k-1)!(n-k)!] + (n-1)!/[k!(n-1-k)!]
= (n-1)!/[k!(n-k)!] × [k + (n-k)]
= n!/[k!(n-k)!]
= C(n,k) ∎

### Derangements Recurrence
D(n) = (n-1)(D(n-1) + D(n-2))

Proof: Consider position n. Element n goes to position k (k≠n). 
Element k must go to position n or elsewhere. Cases give (n-1)(D(n-1)+D(n-2)).

### Sum of Powers Formula
Σi = n(n+1)/2
Σi² = n(n+1)(2n+1)/6
Σi³ = [n(n+1)/2]²

Proof by induction for each formula. ✓