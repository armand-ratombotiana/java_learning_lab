# Probability for Machine Learning - THEORY

## Overview

Probability provides the mathematical foundation for reasoning under uncertainty, essential for ML algorithms and inference.

## 1. Basic Probability

### Sample Space & Events
- **Sample Space (S)**: All possible outcomes
- **Event (E)**: Subset of outcomes
- **P(E)**: Probability of event E

### Axioms
1. 0 ≤ P(E) ≤ 1
2. P(S) = 1
3. P(E₁ ∪ E₂) = P(E₁) + P(E₂) if E₁ and E₂ are mutually exclusive

### Complement Rule
```
P(¬E) = 1 - P(E)
```

## 2. Conditional Probability

### Definition
```
P(A|B) = P(A ∩ B) / P(B)
```

### Independence
```
P(A ∩ B) = P(A) × P(B)  ⟺  A and B are independent
```

### Bayes' Theorem
```
P(A|B) = P(B|A) × P(A) / P(B)
```

## 3. Random Variables

### Discrete
- Bernoulli: P(X=1) = p
- Binomial: Number of successes in n trials
- Poisson: Count of rare events

### Continuous
- Uniform: Equal probability in range
- Normal (Gaussian): Bell curve
- Exponential: Time between events

## 4. Expectation & Variance

### Expectation
```
E[X] = Σ x × P(X=x)  (discrete)
E[X] = ∫ x × f(x) dx  (continuous)
```

### Variance
```
Var(X) = E[(X - E[X])²] = E[X²] - E[X]²
```

## 5. Common Distributions

### Normal Distribution
```
N(μ, σ²): f(x) = (1/√(2πσ²)) × exp(-(x-μ)²/(2σ²))
```

### Maximum Likelihood Estimation
Given data D, find θ that maximizes P(D|θ)

```java
public class GaussianML {
    public double[] mle(double[] data) {
        double mean = mean(data);
        double variance = variance(data, mean);
        return new double[]{mean, variance};
    }
    
    private double mean(double[] data) {
        return Arrays.stream(data).average().orElse(0);
    }
    
    private double variance(double[] data, double mean) {
        return Arrays.stream(data)
            .map(x -> (x - mean) * (x - mean))
            .average().orElse(0);
    }
}
```

## 6. Entropy

### Information Theory
```
H(X) = -Σ P(x) × log₂(P(x))
```

Measures uncertainty or information content.

### Cross-Entropy
```
H(P, Q) = -Σ P(x) × log(Q(x))
```

### KL Divergence
```
D(P||Q) = Σ P(x) × log(P(x)/Q(x))
```

## 7. Bayesian Inference

```java
public class BayesianInference {
    public double posterior(double prior, double likelihood, double evidence) {
        return (likelihood * prior) / evidence;
    }
    
    public Map<String, Double> updateBelief(
            Map<String, Double> prior,
            Map<String, Double> likelihoods) {
        // Update beliefs based on new evidence
        double total = likelihoods.values().stream()
            .mapToDouble(Double::doubleValue).sum();
        return likelihoods.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> (e.getValue() * prior.get(e.getKey())) / total
            ));
    }
}
```

## Summary

1. **Conditional probability**: Foundation for inference
2. **Bayes' Theorem**: Update beliefs with evidence
3. **Distributions**: Model uncertainty
4. **Entropy**: Measure information
5. **MLE**: Find parameters that best explain data