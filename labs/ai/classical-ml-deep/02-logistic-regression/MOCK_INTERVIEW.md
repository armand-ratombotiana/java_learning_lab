# Mock Interview: Logistic Regression

**Topic:** Derive logistic regression — loss function, gradient, and multiclass extension

## Core Questions

### Q1: Derive the logistic regression model and loss function.

**Answer:**
Model probability: $P(y=1|x) = \sigma(w^T x + b) = \frac{1}{1 + e^{-(w^T x + b)}}$

Decision boundary is linear in $x$ space.

We use **cross-entropy loss** (aka negative log-likelihood):

For a single example: $L = -[y \log(\hat{y}) + (1-y) \log(1-\hat{y})]$

For all examples: $L = -\frac{1}{n}\sum_{i=1}^n [y_i \log(\sigma(w^T x_i)) + (1-y_i) \log(1 - \sigma(w^T x_i))]$

**Why not MSE?** Non-convex in logistic case; cross-entropy is convex and derived from MLE.

### Q2: Derive the gradient.

**Answer:**
Let $\hat{y} = \sigma(z)$ where $z = w^T x$.

Key property: $\sigma'(z) = \sigma(z)(1 - \sigma(z)) = \hat{y}(1 - \hat{y})$

Gradient for one example:
$\frac{\partial L}{\partial w} = (\hat{y} - y) x$

Gradient for full dataset:
$\frac{\partial L}{\partial w} = \frac{1}{n}\sum_{i=1}^n (\hat{y}_i - y_i) x_i = \frac{1}{n} X^T (\hat{y} - y)$

Same form as linear regression! Only the prediction function differs.

### Q3: Extend to multiclass (softmax regression).

**Answer:**
For $K$ classes, softmax gives probabilities:
$P(y=k|x) = \frac{e^{w_k^T x}}{\sum_{j=1}^K e^{w_j^T x}}$

Cross-entropy loss: $L = -\frac{1}{n}\sum_{i=1}^n \sum_{k=1}^K \mathbb{1}[y_i = k] \log(\hat{y}_{i,k})$

Gradient: $\frac{\partial L}{\partial w_k} = \frac{1}{n} \sum_{i=1}^n (\hat{y}_{i,k} - \mathbb{1}[y_i = k]) x_i$

## Advanced

- **Regularization:** L1 (lasso) for sparsity, L2 (ridge) for shrinkage
- **Newton's method** (IRLS) vs gradient descent for convergence
- **Separation & perfect collinearity:** coefficients diverge to $\pm\infty$
- **Odds ratio:** $e^{w_j}$ gives multiplicative change in odds per unit of $x_j$
