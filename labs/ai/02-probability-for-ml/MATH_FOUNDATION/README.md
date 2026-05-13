# Probability for ML - Mathematical Foundations

## 1. Bayes Theorem Derivation

Starting from conditional probability:
```
P(A|B) = P(A ∩ B) / P(B)
P(B|A) = P(A ∩ B) / P(A)
```

Cross-multiplying and equating:
```
P(A|B) * P(B) = P(B|A) * P(A)
P(A|B) = P(B|A) * P(A) / P(B)
```

### Naive Bayes Derivation
For classification with features x and class c:
```
P(c|x) = P(x|c) * P(c) / P(x)

Assuming feature independence:
P(x|c) = ∏ᵢ P(xᵢ | c)
```

## 2. Gaussian Distribution Properties

### Univariate Gaussian
```
P(x|μ, σ²) = (1/√(2πσ²)) * exp(-(x-μ)²/(2σ²))
```

### Multivariate Gaussian
```
P(x|μ, Σ) = (1/(2π)^(k/2)|Σ|^(1/2)) * exp(-0.5(x-μ)ᵀΣ⁻¹(x-μ))
```

### Properties
- Linear combinations of Gaussians are Gaussian
- Conditional distributions are Gaussian
- Marginal distributions are Gaussian

### MLE for Gaussian
```
μ̂ = (1/n) Σxᵢ
σ̂² = (1/n) Σ(xᵢ - μ̂)²
```

## 3. Maximum Likelihood Estimation

### Binomial Likelihood
```
L(p) = ∏ᵢ P(xᵢ | p) = p^k (1-p)^(n-k)
log L = k log p + (n-k) log(1-p)
d/dp log L = k/p - (n-k)/(1-p) = 0
p̂ = k/n
```

### Poisson Likelihood
```
L(λ) = ∏ (e^(-λ) λ^xᵢ / xᵢ!)
λ̂ = (1/n) Σxᵢ
```

## 4. Entropy Calculations

### Binary Entropy
```
H(X) = -p log p - (1-p) log(1-p)
```
Maximum at p=0.5: H = 1 bit

### Gaussian Entropy
```
H(X) = 0.5 * log(2πe * σ²)
```
Higher variance → higher entropy

### Joint Entropy
```
H(X, Y) = -Σ Σ P(x,y) log P(x,y)
H(X, Y) ≤ H(X) + H(Y)
```

## 5. KL Divergence

### Properties
- **Non-negativity**: KL(P||Q) ≥ 0
- **Zero iff equal**: KL = 0 iff P = Q
- **Asymmetry**: KL(P||Q) ≠ KL(Q||P)

### Gaussian KL Divergence
```
KL(N₀ || N₁) = 0.5 * [tr(Σ₁⁻¹Σ₀) + (μ₁-μ₀)ᵀΣ₁⁻¹(μ₁-μ₀) - k + log(|Σ₁|/|Σ₀|)]
```

## 6. Expectation Maximization (Sketch)

For latent variable models:
1. **E-step**: Compute posterior over latent variables
2. **M-step**: Update parameters to maximize expected log-likelihood
3. Iterate until convergence

```
Q(θ|θ^(t)) = E[log P(X,Z|θ) | X, θ^(t)]
θ^(t+1) = argmax_θ Q(θ|θ^(t))
```

## 7. Practice Problems

1. Given P(A)=0.3, P(B)=0.4, P(A∪B)=0.6, find P(A|B)
2. Derive MLE for exponential distribution
3. Calculate entropy of a 4-sided die
4. Compute KL divergence between two Gaussians
5. Show that Gaussian is maximum entropy for fixed variance