# Probability Theory for Machine Learning - Complete Theory

## Table of Contents
1. [Foundations of Probability](#1-foundations-of-probability)
2. [Random Variables](#2-random-variables)
3. [Common Probability Distributions](#3-common-probability-distributions)
4. [Joint Distributions](#4-joint-distributions)
5. [Expectation and Moments](#5-expectation-and-moments)
6. [Information Theory](#6-information-theory)
7. [Maximum Likelihood Estimation](#7-maximum-likelihood-estimation)
8. [Bayesian Inference](#8-bayesian-inference)
9. [Conjugate Priors](#9-conjugate-priors)
10. [ML Applications](#10-ml-applications)

---

## 1. Foundations of Probability

### 1.1 Sample Space and Events

**Sample Space (Ω)**: The set of all possible outcomes of an experiment.

**Event**: A subset of the sample space.

**Probability Function P**: Maps events to [0, 1] satisfying:
1. P(A) ≥ 0 for any event A
2. P(Ω) = 1
3. For countable sequence of disjoint events {Aᵢ}: P(∪Aᵢ) = ΣP(Aᵢ)

### 1.2 Conditional Probability

For events A and B with P(B) > 0:
$$P(A|B) = \frac{P(A \cap B)}{P(B)}$$

**Interpretation**: Probability of A given that B has occurred.

### 1.3 Bayes' Theorem

$$P(A|B) = \frac{P(B|A)P(A)}{P(B)}$$

**Proof**:
From definition of conditional probability:
$$P(A|B) = \frac{P(A \cap B)}{P(B)} = \frac{P(B|A)P(A)}{P(B)}$$

**Extended form**:
$$P(A_i|B) = \frac{P(B|A_i)P(A_i)}{\sum_j P(B|A_j)P(A_j)}$$

### 1.4 Independence

**Two events A and B are independent** if:
$$P(A \cap B) = P(A)P(B)$$

This is equivalent to:
- P(A|B) = P(A)
- P(B|A) = P(B)

**Conditional independence**: A and B are conditionally independent given C if:
$$P(A \cap B | C) = P(A|C)P(B|C)$$

### 1.5 Chain Rule

$$P(X_1, X_2, ..., X_n) = P(X_1) \cdot P(X_2|X_1) \cdot ... \cdot P(X_n|X_1, ..., X_{n-1})$$

### 1.6 Total Probability Rule

For partition {Aᵢ} of sample space:
$$P(B) = \sum_i P(B|A_i)P(A_i)$$

---

## 2. Random Variables

### 2.1 Discrete Random Variables

**Probability Mass Function (PMF)**: P(X = x) for each possible value.

**Cumulative Distribution Function (CDF)**:
$$F(x) = P(X \leq x) = \sum_{t \leq x} P(X = t)$$

**Properties**:
- 0 ≤ F(x) ≤ 1
- Non-decreasing
- lim_{x→-∞} F(x) = 0, lim_{x→∞} F(x) = 1

### 2.2 Continuous Random Variables

**Probability Density Function (PDF)** f(x) such that:
$$P(a \leq X \leq b) = \int_a^b f(x)dx$$

**Properties**:
- f(x) ≥ 0
- ∫_{-∞}^{∞} f(x)dx = 1
- P(X = c) = 0 for any specific value c

**Cumulative Distribution Function**:
$$F(x) = \int_{-∞}^x f(t)dt$$

### 2.3 Expected Value

**Discrete**: 
$$E[X] = \sum_x x \cdot P(X = x)$$

**Continuous**:
$$E[X] = \int_{-\infty}^{\infty} x \cdot f(x)dx$$

**Properties**:
- E[c] = c for constant c
- E[cX] = cE[X]
- E[X + Y] = E[X] + E[Y]
- E[XY] = E[X]E[Y] if X, Y independent

### 2.4 Variance and Standard Deviation

**Variance**:
$$Var(X) = E[(X - E[X])^2] = E[X^2] - (E[X])^2$$

**Standard Deviation**: σ = √Var(X)

**Properties**:
- Var(c) = 0
- Var(cX) = c²Var(X)
- Var(X + Y) = Var(X) + Var(Y) + 2Cov(X,Y)
- Var(X + Y) = Var(X) + Var(Y) if X, Y independent

### 2.5 Quantiles

**p-th quantile**: x_p such that F(x_p) = p

**Median**: 0.5 quantile

**Interquartile Range (IQR)**: Q3 - Q1

---

## 3. Common Probability Distributions

### 3.1 Discrete Distributions

#### Bernoulli(p)
- Single trial with success probability p
- PMF: P(X=1) = p, P(X=0) = 1-p
- Mean: p, Variance: p(1-p)

#### Binomial(n, p)
- Number of successes in n independent trials
- PMF: P(X=k) = C(n,k) p^k (1-p)^{n-k}
- Mean: np, Variance: np(1-p)

#### Geometric(p)
- Number of trials until first success
- PMF: P(X=k) = (1-p)^{k-1} p, k = 1, 2, ...
- Mean: 1/p, Variance: (1-p)/p²

#### Negative Binomial(r, p)
- Number of trials until r successes
- PMF: P(X=k) = C(k-1, r-1) p^r (1-p)^{k-r}
- Mean: r/p, Variance: r(1-p)/p²

#### Poisson(λ)
- Count of rare events in fixed interval
- PMF: P(X=k) = λ^k e^{-λ} / k!
- Mean: λ, Variance: λ

#### Multinomial(n, p)
- Generalization of binomial for k categories
- PMF: P(X₁=x₁, ..., X_k=x_k) = n!/(x₁!...x_k!) ∏ p_i^{x_i}
- Mean: np_i, Variance: np_i(1-p_i)

### 3.2 Continuous Distributions

#### Uniform(a, b)
- Equal probability on interval [a, b]
- PDF: f(x) = 1/(b-a) for a ≤ x ≤ b
- Mean: (a+b)/2, Variance: (b-a)²/12

#### Normal/Gaussian(μ, σ²)
- Most important distribution in statistics
- PDF: f(x) = (1/√(2πσ²)) exp(-(x-μ)²/(2σ²))
- Mean: μ, Variance: σ²
- 68-95-99.7 rule: μ±σ, μ±2σ, μ±3σ

#### Exponential(λ)
- Time between events in Poisson process
- PDF: f(x) = λe^{-λx}, x ≥ 0
- Mean: 1/λ, Variance: 1/λ²
- Memoryless property: P(X > s + t | X > s) = P(X > t)

#### Gamma(α, β)
- Generalization of exponential
- PDF: f(x) = β^α x^{α-1} e^{-βx} / Γ(α)
- Mean: α/β, Variance: α/β²
- Special cases: Exponential(α=1), Chi-squared(α=n/2, β=1/2)

#### Beta(α, β)
- Distribution over [0, 1]
- PDF: f(x) = x^{α-1} (1-x)^{β-1} / B(α,β)
- Mean: α/(α+β), Variance: αβ/((α+β)²(α+β+1))
- Useful for modeling probabilities

#### Dirichlet(α₁, ..., α_k)
- Multivariate generalization of Beta
- PDF: f(x) = Γ(Σα_i) / (∏Γ(α_i)) ∏ x_i^{α_i-1}
- Mean: α_i/Σα_j

#### t-Distribution(ν)
- Used for small sample inference
- PDF: heavier tails than normal
- As ν→∞, converges to Normal(0,1)

#### Chi-Squared(ν)
- Distribution of sum of squared standard normals
- PDF: gamma-based
- Used in hypothesis testing

---

## 4. Joint Distributions

### 4.1 Joint PMF/PDF

**Discrete**: P(X = x, Y = y) for all pairs

**Continuous**: f(x, y) such that:
$$P((X, Y) \in A) = \iint_A f(x,y) dxdy$$

### 4.2 Marginal Distributions

**From joint PMF**:
$$P_X(x) = \sum_y P_{XY}(x,y)$$

**From joint PDF**:
$$f_X(x) = \int_{-\infty}^{\infty} f_{XY}(x,y) dy$$

### 4.3 Conditional Distributions

**Discrete**:
$$P_{Y|X}(y|x) = \frac{P_{XY}(x,y)}{P_X(x)}$$

**Continuous**:
$$f_{Y|X}(y|x) = \frac{f_{XY}(x,y)}{f_X(x)}$$

### 4.4 Independence

**Random variables X and Y are independent** if:
$$P(X \in A, Y \in B) = P(X \in A) \cdot P(Y \in B)$$

Equivalently:
- Joint PMF/PDF = Product of marginals
- Conditional distribution = Marginal

### 4.5 Covariance

$$Cov(X, Y) = E[(X - E[X])(Y - E[Y])] = E[XY] - E[X]E[Y]$$

**Properties**:
- Cov(X, X) = Var(X)
- Cov(aX, bY) = ab Cov(X, Y)
- Cov(X + Y, Z) = Cov(X, Z) + Cov(Y, Z)

### 4.6 Correlation

$$Corr(X, Y) = \frac{Cov(X, Y)}{\sqrt{Var(X)Var(Y)}}$$

**Properties**:
- -1 ≤ Corr ≤ 1
- Corr = 0: uncorrelated (not necessarily independent)
- |Corr| = 1: perfectly linearly related

### 4.7 Covariance Matrix

For random vector **X** = [X₁, ..., Xₙ]:
$$Sigma = Cov(X) = E[(X - μ)(X - μ)^T]$$

**Properties**:
- Symmetric positive semi-definite
- Diagonal elements: variances
- Off-diagonal: covariances

---

## 5. Expectation and Moments

### 5.1 Raw Moments

$$E[X^k] \text{ is the k-th raw moment}$$

### 5.2 Central Moments

$$E[(X - μ)^k] \text{ is the k-th central moment}$$

- Variance = 2nd central moment
- Skewness = 3rd central moment / σ³
- Kurtosis = 4th central moment / σ⁴

### 5.3 Moment Generating Function

$$M_X(t) = E[e^{tX}]$$

**Properties**:
- M_X^{(k)}(0) = E[X^k]
- Exists if distribution has finite moments

### 5.4 Common Expectation Results

**Linear combinations**:
$$E[a + b_1X_1 + ... + b_nX_n] = a + \sum b_i E[X_i]$$

**For independent variables**:
$$E[\prod X_i] = \prod E[X_i]$$

**Law of unconscious statistician**:
$$E[g(X)] = \int g(x) f_X(x) dx$$

---

## 6. Information Theory

### 6.1 Entropy

**Shannon entropy** measures uncertainty:
$$H(X) = -\sum_x P(X=x) \log_2 P(X=x)$$

**Properties**:
- H(X) ≥ 0
- H(X) = 0 iff X is deterministic
- Maximum entropy for uniform distribution
- Binary entropy: H_b(p) = -p log p - (1-p) log(1-p)

**Interpretation**: Average information content or minimum code length.

### 6.2 Joint Entropy

$$H(X, Y) = -\sum_{x,y} P(x,y) \log P(x,y)$$

**Properties**:
- H(X, Y) ≤ H(X) + H(Y)
- Equality iff X, Y independent

### 6.3 Conditional Entropy

$$H(Y|X) = \sum_x P(x) H(Y|X=x) = -\sum_{x,y} P(x,y) \log P(y|x)$$

**Chain rule**:
$$H(X, Y) = H(X) + H(Y|X)$$

### 6.4 Cross-Entropy

$$H(P, Q) = -\sum_x P(x) \log Q(x)$$

**Interpretation**: Average code length when using wrong distribution Q to encode data from true distribution P.

**Note**: H(P, Q) ≥ H(P)

### 6.5 KL Divergence

**Relative entropy** measures distance between distributions:
$$D_{KL}(P || Q) = \sum_x P(x) \log \frac{P(x)}{Q(x)}$$

**Properties**:
- Non-negative: D_{KL}(P||Q) ≥ 0
- Asymmetric: D_{KL}(P||Q) ≠ D_{KL}(Q||P)
- Zero iff P = Q

**Relationship**: D_{KL}(P||Q) = H(P, Q) - H(P)

### 6.6 Mutual Information

$$I(X; Y) = \sum_{x,y} P(x,y) \log \frac{P(x,y)}{P(x)P(y)}$$

**Properties**:
- I(X; Y) = D_{KL}(P(X,Y) || P(X)P(Y))
- I(X; Y) = H(X) - H(X|Y) = H(Y) - H(Y|X)
- I(X; Y) ≥ 0
- I(X; Y) = I(Y; X)

**Interpretation**: Amount of information shared between X and Y.

---

## 7. Maximum Likelihood Estimation

### 7.1 Likelihood Function

For data X = {x₁, ..., xₙ} from distribution with parameter θ:
$$L(\theta | X) = P(X | \theta) = \prod_{i=1}^n P(x_i | \theta)$$

### 7.2 Log Likelihood

$$l(\theta | X) = \log L(\theta | X) = \sum_{i=1}^n \log P(x_i | \theta)$$

**Advantage**: Converts products to sums, easier to optimize.

### 7.3 MLE Definition

$$\hat{\theta}_{MLE} = \arg\max_\theta l(\theta | X) = \arg\max_\theta L(\theta | X)$$

### 7.4 MLE Properties

1. **Consistency**: θ̂ → θ as n → ∞
2. **Asymptotic normality**: √n(θ̂ - θ) → N(0, I^{-1}(θ))
3. **Efficiency**: Achieves Cramér-Rao lower bound asymptotically
4. **Invariance**: If τ = g(θ), then τ̂ = g(θ̂)

### 7.5 Examples

**Bernoulli MLE**: If X ~ Bernoulli(p), data {xᵢ}:
$$\hat{p} = \frac{1}{n} \sum x_i$$

**Normal MLE**: For X ~ N(μ, σ²):
$$\hat{\mu} = \frac{1}{n} \sum x_i, \quad \hat{\sigma}^2 = \frac{1}{n} \sum (x_i - \bar{x})^2$$

---

## 8. Bayesian Inference

### 8.1 Prior Distribution

P(θ) represents beliefs about θ before observing data.

**Types**:
- Informative: Strong prior beliefs
- Uninformative: No strong beliefs (e.g., uniform)
- Weakly informative: Regularizing priors

### 8.2 Likelihood

P(X|θ) as before - how data relates to parameters.

### 8.3 Posterior Distribution

$$P(\theta | X) = \frac{P(X | \theta) P(\theta)}{P(X)}$$

**Unnormalized form**: P(θ|X) ∝ P(X|θ) P(θ)

### 8.4 Evidence/Marginal Likelihood

$$P(X) = \int P(X | \theta) P(\theta) d\theta$$

Normalizing constant for posterior.

### 8.5 Point Estimates from Posterior

**Posterior Mean**:
$$\hat{\theta} = E[\theta | X] = \int \theta P(\theta | X) d\theta$$

**Posterior Mode** (Maximum A Posteriori - MAP):
$$\hat{\theta}_{MAP} = \arg\max_\theta P(\theta | X)$$

**Posterior Median**: θ such that P(θ ≤ x|X) = 0.5

### 8.6 Credible Intervals

**Bayesian analog of confidence intervals**.

**100(1-α)% credible interval**: P(θ ∈ [a, b] | X) = 1 - α

**Highest Posterior Density (HPD)**: Shortest interval containing 1-α probability.

---

## 9. Conjugate Priors

### 9.1 Definition

A prior distribution is conjugate to a likelihood if the posterior is in the same family as the prior.

### 9.2 Common Conjugate Pairs

| Likelihood | Prior | Posterior |
|------------|-------|-----------|
| Bernoulli | Beta(α, β) | Beta(α + Σxᵢ, β + n - Σxᵢ) |
| Binomial | Beta(α, β) | Beta(α + k, β + nk - k) |
| Poisson | Gamma(α, β) | Gamma(α + Σxᵢ, β + n) |
| Normal (unknown μ) | Normal(μ₀, σ₀²) | Normal(μₙ, σₙ²) |
| Normal (unknown σ²) | Inverse-Gamma | Inverse-Gamma |
| Normal (both unknown) | Normal-Inverse-Gamma | Normal-Inverse-Gamma |
| Multinomial | Dirichlet(α₁, ..., α_k) | Dirichlet(α₁ + n₁, ...) |

### 9.3 Beta-Bernoulli Example

**Prior**: θ ~ Beta(α, β)
**Likelihood**: Xᵢ | θ ~ Bernoulli(θ)
**Posterior**: θ | X ~ Beta(α + Σxᵢ, β + n - Σxᵢ)

**Interpretation**:
- α = number of prior successes + 1
- β = number of prior failures + 1
- Posterior mean = (α + Σxᵢ)/(α + β + n)

### 9.4 Normal-Normal Example

**Prior**: μ ~ N(μ₀, σ₀²)
**Likelihood**: Xᵢ | μ ~ N(μ, σ²) with known variance
**Posterior**: μ | X ~ N(μₙ, σₙ²)

Where:
$$\sigma_n^2 = \frac{1}{\frac{1}{\sigma_0^2} + \frac{n}{\sigma^2}}$$
$$\mu_n = \sigma_n^2 \left(\frac{\mu_0}{\sigma_0^2} + \frac{n\bar{x}}{\sigma^2}\right)$$

---

## 10. ML Applications

### 10.1 Naive Bayes Classifier

基于贝叶斯定理 with conditional independence assumption.

$$P(y | x_1, ..., x_n) \propto P(y) \prod_{i=1}^n P(x_i | y)$$

**Features given class**: Assume independence.

**Types**:
- Gaussian Naive Bayes: P(xᵢ|y) ~ N(μᵢᵧ, σᵢᵧ²)
- Multinomial Naive Bayes: for text classification
- Bernoulli Naive Bayes: binary features

### 10.2 Gaussian Discriminant Analysis

**Quadratic Discriminant Analysis (QDA)**:
$$P(y | x) \propto P(y) \cdot \mathcal{N}(x | μ_y, Σ_y)$$

**Linear Discriminant Analysis (LDA)** - assume equal covariance:
$$P(y | x) \propto P(y) \cdot \exp(-\frac{1}{2}(x - μ_y)^T Σ^{-1}(x - μ_y))$$

### 10.3 Maximum A Posteriori (MAP) for Linear Regression

**Posterior**: P(w | X, y) ∝ P(y | X, w) P(w)

**L2 regularization (Ridge)**: P(w) = N(0, λ⁻¹I)
Result: Maximize log posterior = Minimize MSE + λ||w||²

**L1 regularization (Lasso)**: P(w) = Laplace(0, b)
Results in sparse solutions.

### 10.4 Expectation-Maximization (EM) Algorithm

**For latent variable models**.

**E-step**: Compute Q(z) = E_{Z|X,θ}[log P(X, Z|θ)]

**M-step**: θ^{new} = argmax_θ Q(z) + log P(θ)

**Converges to local maximum of likelihood.

### 10.5 Variational Inference

Approximate posterior with simpler distribution q(Z).

Minimize KL(q||p) = E_q[log q(Z) - log P(Z|X)]

Results in Evidence Lower Bound (ELBO):
log P(X) ≥ E_q[log P(X,Z) - log q(Z)]

---

## Summary

Key probability concepts for ML:
1. Conditional probability and Bayes' theorem
2. Random variables and distributions
3. Joint and conditional distributions
4. Expectation, variance, covariance
5. Information theory (entropy, KL divergence)
6. MLE and Bayesian inference
7. Conjugate priors for tractable inference

These foundations enable understanding and implementing ML algorithms.

---

## Further Reading

1. "Pattern Recognition and Machine Learning" - Bishop
2. "All of Statistics" - Wasserman
3. "Information Theory" - Cover and Thomas