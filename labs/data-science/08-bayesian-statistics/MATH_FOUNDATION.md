# Bayesian Statistics — Mathematical Foundations

## 1. Linear Algebra

Matrix operations, eigenvectors, eigenvalues, and SVD are fundamental to Bayesian Statistics. The eigendecomposition A = Q * Lambda * Q^(-1) where columns of Q are eigenvectors and Lambda contains eigenvalues. SVD generalizes to non-square matrices: A = U * Sigma * V^T.

### 1.1 Vector Spaces

A vector space V over a field F is a set of vectors closed under addition and scalar multiplication. Key properties: associativity, commutativity, distributivity, existence of identity and inverses.

### 1.2 Linear Transformations

A linear transformation T: V -> W satisfies T(u+v) = T(u) + T(v) and T(cv) = cT(v). Represented by matrices, linear transformations map between vector spaces while preserving structure.

### 1.3 Eigenvalues and Eigenvectors

For a square matrix A, vector v is an eigenvector with eigenvalue lambda if Av = lambda*v. The characteristic equation det(A - lambda*I) = 0 determines eigenvalues.

### 1.4 Singular Value Decomposition

Every matrix A (m x n) can be decomposed as A = U * Sigma * V^T where U (m x m) and V (n x n) are orthogonal matrices and Sigma (m x n) contains singular values.

## 2. Probability Theory

Probability distributions model uncertainty. The Gaussian distribution N(mu, sigma^2) = (1/sqrt(2*pi*sigma^2)) * exp(-(x-mu)^2/(2*sigma^2)). The Beta distribution is a conjugate prior for Bernoulli likelihood.

### 2.1 Probability Axioms

Kolmogorov's axioms: P(A) >= 0, P(Omega) = 1, and for disjoint events P(union A_i) = sum P(A_i). These form the foundation of modern probability theory.

### 2.2 Random Variables

A random variable X: Omega -> R maps outcomes to real numbers. Discrete random variables have probability mass functions, continuous ones have probability density functions.

### 2.3 Expectation and Variance

E[X] = sum x * P(x) for discrete, integral x * f(x) dx for continuous. Var(X) = E[(X - E[X])^2] = E[X^2] - E[X]^2.

### 2.4 Bayes' Theorem

P(A|B) = P(B|A) * P(A) / P(B). This fundamental result updates beliefs based on evidence, forming the basis of Bayesian inference.

## 3. Calculus and Optimization

Gradient-based optimization finds minima of differentiable functions. The gradient is the vector of partial derivatives. The Hessian matrix contains second-order partial derivatives.

### 3.1 Derivatives

The derivative f'(x) = lim_{h->0} (f(x+h) - f(x))/h measures the instantaneous rate of change.

### 3.2 Gradient Descent

theta_{t+1} = theta_t - alpha * nabla L(theta_t). The learning rate alpha controls step size.

### 3.3 Convex Optimization

A function f is convex if f(tx + (1-t)y) <= tf(x) + (1-t)f(y) for all t in [0,1]. Convex functions have no local minima that aren't global.

## 4. Information Theory

Self-information I(x) = -log P(x). Entropy H(X) = E[I(x)] measures average information content. Cross-entropy H(P,Q) = -sum P(x) log Q(x).

## 5. Statistical Inference

Maximum Likelihood Estimation finds parameters maximizing P(D|theta). MLE is consistent and asymptotically normal. Bayesian inference computes P(theta|D) = P(D|theta)*P(theta)/P(D).

### 5.1 Point Estimation

Estimators produce a single best guess for parameters. Desirable properties: unbiasedness (E[theta_hat] = theta), efficiency (minimum variance), consistency (converges to true value).

### 5.2 Interval Estimation

Confidence intervals provide a range of plausible values. A 95% confidence interval means that 95% of such intervals contain the true parameter.

### 5.3 Hypothesis Testing

Null hypothesis H_0 vs alternative H_1. Type I error: rejecting true H_0. Type II error: failing to reject false H_0. p-value: probability of observing data at least as extreme under H_0.

## 6. Numerical Methods

Numerical stability is critical. Use log-sum-exp trick for probability calculations. Cholesky decomposition for positive definite matrices. QR decomposition for least squares.

### 6.1 Floating Point Arithmetic

IEEE 754 standard defines floating point representation. Double precision gives about 15-17 decimal digits. Catastrophic cancellation occurs when subtracting nearly equal numbers.

### 6.2 Matrix Factorization

LU decomposition: A = LU for solving linear systems. QR decomposition: A = QR for least squares. Cholesky: A = LL^T for positive definite matrices.

### 6.3 Iterative Methods

For large systems, iterative methods (conjugate gradient, GMRES) are more efficient than direct methods.

## 7. Probability Distributions

Gaussian: Symmetric, bell-shaped, characterized by mean and variance. Bernoulli: Binary outcomes (0/1). Categorical: Multiple unordered categories. Poisson: Count data. Exponential: Continuous waiting times.

## 8. Matrix Calculus

Derivatives with respect to vectors and matrices follow specific rules. d/dx (x^T A x) = 2Ax for symmetric A. d/dX (trace(X^T A X)) = 2AX.

## 9. Spectral Methods

Spectral methods use eigenvalues and eigenvectors of data-derived matrices. The Laplacian matrix L = D - W captures graph structure. Spectral clustering uses eigenvectors of L for dimensionality reduction before clustering.

## 10. Asymptotic Theory

As sample size increases, estimators converge to true values under regularity conditions. The Central Limit Theorem states that sample means are approximately normally distributed for large n, regardless of the underlying distribution.

## 11. Optimization Algorithms

### 11.1 Batch Gradient Descent
Batch GD computes the gradient using the entire dataset: theta = theta - alpha * (1/m) * sum_i nabla L_i(theta). It provides stable convergence but can be slow for large datasets.

### 11.2 Stochastic Gradient Descent
SGD uses one random sample per update: theta = theta - alpha * nabla L_i(theta). It is faster but has higher variance in the gradient estimates. Learning rate schedules are important for convergence.

### 11.3 Mini-Batch Gradient Descent
Mini-batch GD uses a small random subset (batch) of data: theta = theta - alpha * (1/b) * sum_{i in batch} nabla L_i(theta). It balances the stability of batch GD with the speed of SGD.

### 11.4 Momentum Methods
Momentum accelerates SGD by adding a fraction of the previous update: v_t = beta*v_{t-1} + (1-beta)*nabla L(theta_t); theta = theta - alpha*v_t. Typical beta values are 0.9 or 0.99.

### 11.5 Adaptive Methods
AdaGrad adapts learning rates per parameter based on historical gradients. RMSProp uses a moving average of squared gradients. Adam combines momentum with RMSProp for robust performance across many problem types.

## 12. Regularization Theory

L1 regularization (Lasso) adds |w| to the loss, encouraging sparsity by driving some weights exactly to zero. L2 regularization (Ridge) adds w^2, encouraging small weights but not sparsity. Elastic Net combines both: lambda_1*|w| + lambda_2*w^2.

## 13. Loss Function Derivations

### 13.1 MSE Gradient
For mean squared error L = (1/2n) * sum_i (y_i - y_hat_i)^2, the gradient is dL/dw = (1/n) * sum_i (y_hat_i - y_i) * x_i.

### 13.2 Cross-Entropy Gradient
For binary cross-entropy L = -(1/n) * sum_i [y_i log(y_hat_i) + (1-y_i) log(1-y_hat_i)], the gradient is dL/dw = (1/n) * sum_i (y_hat_i - y_i) * x_i with sigmoid activation.

### 13.3 Regularized Loss Gradients
With L2 regularization, the gradient becomes dL/dw = data_gradient + lambda * w. With L1 regularization, the subgradient is dL/dw = data_gradient + lambda * sign(w).