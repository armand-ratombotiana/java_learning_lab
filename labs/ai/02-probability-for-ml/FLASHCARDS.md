# Probability for ML - FLASHCARDS

## Basics

### Card 1
**Q:** Conditional probability formula?
**A:** P(A|B) = P(A ∩ B) / P(B)

### Card 2
**Q:** Independence definition?
**A:** P(A ∩ B) = P(A) × P(B) ⟺ P(A|B) = P(A)

### Card 3
**Q:** Complement rule?
**A:** P(¬E) = 1 - P(E)

### Card 4
**Q:** Sample space?
**A:** Set of all possible outcomes.

## Bayes' Theorem

### Card 5
**Q:** Bayes' theorem?
**A:** P(A|B) = P(B|A) × P(A) / P(B)

### Card 6
**Q:** Prior probability?
**A:** P(Hypothesis) before observing evidence.

### Card 7
**Q:** Posterior probability?
**A:** P(Hypothesis|Data) after observing evidence.

### Card 8
**Q:** Likelihood?
**A:** P(Data|Hypothesis) - how likely data given hypothesis.

### Card 9
**Q:** Evidence?
**A:** P(Data) - normalizing constant.

## Distributions

### Card 10
**Q:** Normal distribution parameters?
**A:** Mean (μ) and variance (σ²).

### Card 11
**Q:** Gaussian PDF formula?
**A:** (1/√(2πσ²)) × exp(-(x-μ)²/(2σ²))

### Card 12
**Q:** Bernoulli distribution?
**A:** Binary outcome with probability p.

### Card 13
**Q:** Poisson distribution use?
**A:** Count of rare events in fixed interval.

### Card 14
**Q:** Exponential distribution use?
**A:** Time between events (waiting time).

## Expectation

### Card 15
**Q:** Expectation of X?
**A:** E[X] = Σ x × P(X=x) (discrete) or ∫ x × f(x) dx (continuous)

### Card 16
**Q:** Variance formula?
**A:** Var(X) = E[(X-E[X])²] = E[X²] - E[X]²

### Card 17
**Q:** Standard deviation?
**A:** √Var(X) - same units as X

### Card 18
**Q:** E[aX + b] = ?
**A:** aE[X] + b

## Entropy

### Card 19
**Q:** Entropy formula?
**A:** H(X) = -Σ P(x) × log₂(P(x))

### Card 20
**Q:** Fair coin entropy?
**A:** 1 bit (maximum uncertainty)

### Card 21
**Q:** Zero entropy when?
**A:** One outcome certain (P=1)

### Card 22
**Q:** KL Divergence?
**A:** D(P||Q) = Σ P(x) × log(P(x)/Q(x)) - distance between distributions.

## MLE

### Card 23
**Q:** MLE goal?
**A:** Find parameters θ that maximize P(Data|θ)

### Card 24
**Q:** Normal MLE estimates?
**A:** μ̂ = sample mean, σ̂² = sample variance

### Card 25
**Q:** Bernoulli MLE?
**A:** p̂ = successes / total trials

---

**Total: 25 flashcards**