# Guide: Probability Distributions in Java

## Normal Distribution
- PDF: f(x) = (1/σ√(2π)) * e^(-(x-μ)²/(2σ²))
- CDF: approximated using the error function (Erf)
- Sampling: Box-Muller transform

## Binomial Distribution
- PMF: P(X=k) = C(n,k) * p^k * (1-p)^(n-k)
- CDF: sum of PMF from 0 to k
- Sampling: count successes in n Bernoulli trials

## Poisson Distribution
- PMF: P(X=k) = (λ^k * e^(-λ)) / k!
- CDF: sum of PMF from 0 to k
- Sampling: using exponential inter-arrival times

## Exponential Distribution
- PDF: f(x) = λe^(-λx)
- CDF: F(x) = 1 - e^(-λx)
- Sampling: inverse transform -ln(1-U)/λ

## Java Implementation Notes
- Use `Random` or `ThreadLocalRandom` for uniform RNG
- Use `Math.exp()`, `Math.pow()`, `Math.sqrt()` for computations
- For factorial, use iterative multiplication or `double` approximation
