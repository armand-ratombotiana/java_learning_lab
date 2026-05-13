# Probability Theory for Machine Learning - Theory

## 1. Probability Fundamentals

### Sample Space and Events
- **Sample space (Ω)**: Set of all possible outcomes
- **Event**: Subset of sample space
- **Probability**: P(A) ∈ [0, 1]

### Axioms of Probability
1. P(A) ≥ 0
2. P(Ω) = 1
3. For mutually exclusive events: P(∪Aᵢ) = ΣP(Aᵢ)

### Conditional Probability
```
P(A|B) = P(A ∩ B) / P(B)
```

### Bayes' Theorem
```
P(A|B) = P(B|A) * P(A) / P(B)
```
Fundamental to many ML algorithms.

## 2. Random Variables

### Discrete Random Variables
- **Probability Mass Function (PMF)**: P(X = x)
- **Expected Value**: E[X] = Σx * P(X = x)
- **Variance**: Var(X) = E[(X - E[X])²]

### Continuous Random Variables
- **Probability Density Function (PDF)**: P(a ≤ X ≤ b) = ∫ₐᵇ f(x)dx
- **Cumulative Distribution Function (CDF)**: F(x) = P(X ≤ x)

## 3. Common Distributions

### Discrete Distributions
- **Bernoulli**: Single binary trial
- **Binomial**: Number of successes in n trials
- **Poisson**: Count of rare events
- **Categorical/Multinomial**: Multiple categories

### Continuous Distributions
- **Uniform**: Equal probability on interval
- **Gaussian/Normal**: Most common in ML
- **Exponential**: Time between events
- **Beta**: Conjugate prior for Bernoulli
- **Dirichlet**: Conjugate prior for Multinomial

## 4. Joint and Marginal Distributions

### Joint Probability
```
P(X = x, Y = y) = P(X = x | Y = y) * P(Y = y)
```

### Marginalization
```
P(X = x) = Σ_y P(X = x, Y = y)
```

### Independence
```
P(X, Y) = P(X) * P(Y)  (if independent)
```

## 5. Expectation and Variance Properties

### Expected Value
- E[aX + b] = aE[X] + b
- E[X + Y] = E[X] + E[Y]
- E[XY] = E[X]E[Y] (if independent)

### Variance
- Var(aX + b) = a²Var(X)
- Var(X + Y) = Var(X) + Var(Y) + 2Cov(X,Y)

### Covariance
```
Cov(X, Y) = E[(X - E[X])(Y - E[Y])]
```
- Positive: X increases with Y
- Negative: X decreases with Y

## 6. Information Theory

### Entropy (Uncertainty)
```
H(X) = -Σ P(x) * log(P(x))
```
- High entropy = more uncertainty
- Maximum for uniform distribution

### Cross-Entropy
```
H(P, Q) = -Σ P(x) * log(Q(x))
```

### KL Divergence (Relative Entropy)
```
KL(P || Q) = Σ P(x) * log(P(x) / Q(x))
```
- Measures "distance" between distributions
- Non-negative, asymmetric

### Mutual Information
```
I(X; Y) = H(X) + H(Y) - H(X, Y)
```
Measures dependency between variables.

## 7. Maximum Likelihood Estimation

### Likelihood Function
```
L(θ | X) = P(X | θ) = ∏ᵢ P(xᵢ | θ)
```

### Log Likelihood
```
ℓ(θ | X) = log L(θ | X) = Σᵢ log P(xᵢ | θ)
```

### MLE Principle
```
θ̂ = argmax_θ ℓ(θ | X)
```

## 8. Bayesian Inference

### Prior Distribution
P(θ) - beliefs before seeing data

### Posterior Distribution
```
P(θ | X) = P(X | θ) * P(θ) / P(X)
```
- Posterior ∝ Likelihood × Prior

### Common Priors
- **Conjugate priors**: Same family as likelihood
- **Non-informative priors**: Flat/weak