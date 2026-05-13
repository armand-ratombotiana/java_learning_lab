# Probability Theory - Quiz

## Part A: Multiple Choice

**Q1.** If P(A) = 0.3, P(B) = 0.4, and P(A ∩ B) = 0.1, what is P(A|B)?
- A) 0.25
- B) 0.33
- C) 0.5
- D) 0.12

**Q2.** Which distribution has the maximum entropy for a fixed variance?
- A) Uniform
- B) Exponential
- C) Gaussian
- D) Laplace

**Q3.** The KL divergence KL(P||Q) is:
- A) Symmetric
- B) A metric
- C) Non-negative
- D) Always zero

**Q4.** For a conjugate prior, the posterior distribution is in the same family as:
- A) Likelihood
- B) Prior
- C) Both
- D) Neither

**Q5.** The MLE estimate for a Bernoulli distribution is:
- A) geometric mean
- B) arithmetic mean
- C) median
- D) mode

## Part B: True/False

1. The sum of two independent Gaussians is Gaussian. (T/F)
2. P(A|B) = P(A) if A and B are independent. (T/F)
3. Entropy is always non-negative. (T/F)
4. The mode of a Gaussian is equal to its mean. (T/F)
5. Mutual information is symmetric: I(X;Y) = I(Y;X). (T/F)

## Part C: Problems

1. Calculate the entropy of a fair 6-sided die.
2. If X ~ Binomial(10, 0.3), find P(X = 3).
3. Show that for Bernoulli with p=0.5, H(X) = 1 bit.
4. Compute Cov(X, Y) given E[XY] = 5, E[X] = 2, E[Y] = 2.
5. If prior is Beta(1,1) and we observe 3 heads in 5 flips, what is the posterior?

## Answers
- A: A (0.25), C, C, C, B (arithmetic mean)
- B: T, T, T, T, T
- C1: log2(6) ≈ 2.585 bits
- C2: C(10,3) * 0.3³ * 0.7⁷ ≈ 0.266
- C4: 5 - 2*2 = 1
- C5: Beta(4, 3)