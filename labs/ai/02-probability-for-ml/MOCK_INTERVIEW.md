# Mock Interview: Probability for ML

## Question 1: Probability Distributions
**Q**: Compare Gaussian, Bernoulli, Binomial, and Beta distributions. When would you use each in ML?

**A**:
- **Gaussian (Normal)**: Continuous, symmetric. Used for regression errors, weight initialization, latent variables in VAEs.
- **Bernoulli**: Single binary trial. Used for binary classification (logistic regression output).
- **Binomial**: Sum of independent Bernoulli trials. Used in A/B testing significance.
- **Beta**: Distribution of probabilities. Conjugate prior for Bernoulli in Bayesian inference.

## Question 2: Bayes' Theorem & Naive Bayes
**Q**: Derive Naive Bayes classifier. Why is it "naive"?

**A**: P(y|x) = P(x|y) * P(y) / P(x)

"Naive" because it assumes features are conditionally independent given class.
P(x|y) = prod(P(x_i|y))

```python
class GaussianNB:
    def fit(self, X, y):
        self.classes = np.unique(y)
        self.params = []
        for c in self.classes:
            X_c = X[y == c]
            self.params.append({
                'mean': X_c.mean(axis=0),
                'var': X_c.var(axis=0),
                'prior': len(X_c) / len(X)
            })

    def predict_proba(self, X):
        probs = []
        for c, param in zip(self.classes, self.params):
            likelihood = np.exp(-0.5 * ((X - param['mean'])**2 / param['var']))
            likelihood /= np.sqrt(2 * np.pi * param['var'])
            probs.append(np.prod(likelihood, axis=1) * param['prior'])
        return np.array(probs).T / np.sum(probs, axis=0)
```

## Question 3: Maximum Likelihood vs Bayesian
**Q**: Compare MLE and MAP estimation. When would you use each?

**A**: MLE: w* = argmax P(D|w). MAP: w* = argmax P(D|w) * P(w)
- MLE: Only considers data, prone to overfitting with small data
- MAP: Includes prior, provides regularization (L2 = Gaussian prior, L1 = Laplace prior)
- Bayesian: Full posterior P(w|D), not just point estimate

## Question 4: Expected Value & Variance
**Q**: Given a random variable X with distribution P(X), compute E[X] and Var[X]. How do these relate to MSE?

**A**: The MSE of an estimator can be decomposed:
MSE = Bias^2 + Variance
E[(y - y_hat)^2] = (E[y] - y_hat)^2 + Var[y]

## Question 5: Practical Probability Problem
**Q**: You have a test for a rare disease (1% prevalence) that is 99% accurate. You test positive. What's the probability you have the disease?

**A**: Using Bayes' Theorem:
P(disease|positive) = P(positive|disease) * P(disease) / P(positive)
= 0.99 * 0.01 / (0.99 * 0.01 + 0.01 * 0.99)
= 0.0099 / (0.0099 + 0.0099)
= 50%

Counter-intuitive result because false positives match true positives at low prevalence.
