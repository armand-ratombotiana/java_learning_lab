# Lab 06: Naive Bayes Classifier

## Topics Covered
- Gaussian Naive Bayes
- Multinomial Naive Bayes
- Bernoulli Naive Bayes
- Laplace Smoothing

## Objective
Implement Gaussian Naive Bayes for continuous features and apply it to classification.

## Key Concepts
| Concept | Description |
|---|---|
| Bayes Theorem | P(y|x) ∝ P(x|y)·P(y) |
| Naive Assumption | Features are conditionally independent given class |
| Gaussian NB | P(xᵢ|y) ~ N(μᵢⱼ, σᵢⱼ²) |
| Laplace Smoothing | Add 1 to all counts to avoid zero probabilities |

## Files
- `GUIDE.md` — Step-by-step lab walkthrough
- `INTERVIEW.md` — Interview Q&A on Naive Bayes
- `src/com/ml/lab06/Main.java` — Compilable Java source with test cases
