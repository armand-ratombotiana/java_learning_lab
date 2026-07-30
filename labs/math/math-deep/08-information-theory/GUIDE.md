# Information Theory — Study Guide

## Core Concepts

### Entropy
- H(X) = -Σ p(x) log₂ p(x) — average information content (bits)
- Joint entropy: H(X,Y) = -ΣΣ p(x,y) log₂ p(x,y)
- Conditional entropy: H(Y|X) = H(X,Y) - H(X)
- Chain rule: H(X₁,...,Xₙ) = Σ H(Xᵢ|X₁,...,Xᵢ₋₁)

### Mutual Information
- I(X;Y) = H(X) - H(X|Y) = H(Y) - H(Y|X)
- I(X;Y) = D_KL(p(x,y) || p(x)p(y))
- Symmetric, non-negative, zero iff independent

### Channel Capacity
- C = max_{p(x)} I(X;Y)
- BSC: C = 1 - H(p) where p is crossover probability
- AWGN: C = (1/2) log₂(1 + SNR)

## Implementation Checklist
1. Use log₂ for entropy in bits; use Natural log for nats
2. Handle zero probabilities carefully (0*log(0) = 0 by convention)
3. For Huffman: use priority queue for efficiency
4. Mutual information: estimate from empirical distributions

## Common Pitfalls
- Using log₂ vs log_e — specify units (bits vs nats)
- Zero probabilities: define 0*log(0) = 0
- Channel capacity requires maximization over input distribution
