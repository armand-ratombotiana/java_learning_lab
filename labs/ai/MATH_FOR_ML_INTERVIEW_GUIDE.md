# Math for Machine Learning — Interview Preparation Guide

> Comprehensive mathematical foundations for ML/AI roles. Covers linear algebra, calculus, probability, statistics, and optimization with derivations and interview questions.

---

## Table of Contents

1. [Linear Algebra](#1-linear-algebra)
2. [Calculus](#2-calculus)
3. [Probability](#3-probability)
4. [Statistics](#4-statistics)
5. [Optimization](#5-optimization)

---

## 1. Linear Algebra

### 1.1 Vectors

#### Dot Product (Inner Product)
For vectors $a, b \in \mathbb{R}^n$:
$$a \cdot b = \sum_{i=1}^n a_i b_i = a^T b = \|a\|\|b\|\cos\theta$$

**Properties:**
- Commutative: $a \cdot b = b \cdot a$
- Bilinear: $(\alpha a + \beta b) \cdot c = \alpha(a \cdot c) + \beta(b \cdot c)$
- Connection to cosine similarity: $\cos\theta = \frac{a \cdot b}{\|a\|\|b\|}$

**In ML:** Dot products measure similarity (cosine similarity in NLP, kernel methods, attention mechanisms).

#### Norms

**L2 norm (Euclidean):** $\|x\|_2 = \sqrt{\sum_i x_i^2}$

**L1 norm (Manhattan):** $\|x\|_1 = \sum_i |x_i|$

**Lp norm:** $\|x\|_p = (\sum_i |x_i|^p)^{1/p}$

**L0 "norm":** $\|x\|_0 = \text{number of non-zero elements}$ (not a true norm)

**L-infinity norm:** $\|x\|_\infty = \max_i |x_i|$

**Why L1 vs L2 in regularization?**
- L2 shrinks coefficients proportionally — encourages small weights but none go to exactly zero.
- L1 drives small coefficients to exactly zero — feature selection (sparsity inducing).
- Geometrically: L1 constraint is a diamond (corners hit axes), L2 is a sphere.

#### Orthogonality
Vectors $u, v$ are **orthogonal** if $u \cdot v = 0$.

**Orthonormal:** Orthogonal and unit norm: $u_i \cdot u_j = \delta_{ij}$.

**Gram-Schmidt process:** Converts a set of vectors to an orthonormal basis.

#### Linear Independence
A set $\{v_1, \ldots, v_k\}$ is **linearly independent** if:
$$\alpha_1 v_1 + \cdots + \alpha_k v_k = 0 \implies \alpha_1 = \cdots = \alpha_k = 0$$

### 1.2 Matrices

#### Matrix Multiplication
$(AB)_{ij} = \sum_k A_{ik} B_{kj}$

**Interpretations:**
- Linear combination of columns of $A$ using columns of $B$
- Linear combination of rows of $B$ using rows of $A$
- Outer product sum: $AB = \sum_k A_{:,k} B_{k,:}$

#### Rank
- **Column rank:** dimension of column space
- **Row rank:** dimension of row space (always equals column rank)
- **Full rank:** $\text{rank}(A) = \min(m, n)$ for $A \in \mathbb{R}^{m \times n}$
- **Rank deficiency:** indicates linear dependence, singular matrix, information loss

**Low-rank approximation:** Replace $A$ with rank-$k$ approximation $\tilde{A}$ (via SVD). Used in matrix completion, recommendation systems, compression.

#### Determinant
For square matrix $A \in \mathbb{R}^{n \times n}$:
- $\det(A) = 0 \iff A$ is singular (non-invertible)
- $\det(AB) = \det(A)\det(B)$
- $\det(A^T) = \det(A)$
- $\det(A^{-1}) = 1/\det(A)$
- $\det(cA) = c^n \det(A)$

**Geometric meaning:** Factor by which $A$ scales volumes.

#### Trace
$\text{Tr}(A) = \sum_i A_{ii}$

**Properties:**
- $\text{Tr}(ABC) = \text{Tr}(BCA) = \text{Tr}(CAB)$ (cyclic property)
- $\text{Tr}(A^T B) = \sum_{i,j} A_{ij} B_{ij}$ (Frobenius inner product)

#### Inverse
$A^{-1}$ exists iff $A$ is square and full rank.

$(AB)^{-1} = B^{-1} A^{-1}$

$(A^T)^{-1} = (A^{-1})^T$

**In ML:** Solving linear systems $Ax = b \implies x = A^{-1}b$ (linear regression closed form: $\hat{\beta} = (X^T X)^{-1} X^T y$). In practice, use $X^T X$ is often ill-conditioned — use SVD/pseudoinverse instead.

#### Matrix Norms

**Frobenius norm:** $\|A\|_F = \sqrt{\sum_{i,j} A_{ij}^2} = \sqrt{\text{Tr}(A^T A)}$

**Spectral norm:** $\|A\|_2 = \sigma_{\max}(A)$ (largest singular value)

**Nuclear norm:** $\|A\|_* = \sum_i \sigma_i(A)$ (sum of singular values) — convex relaxation of rank

### 1.3 Eigenvalues and Eigenvectors

**Definition:** $A v = \lambda v$, where $v \neq 0$.

**Characteristic equation:** $\det(A - \lambda I) = 0$

**Geometric interpretation:** $A$ scales eigenvector $v$ by factor $\lambda$ (no rotation along that direction).

**Properties:**
- $\text{Tr}(A) = \sum_i \lambda_i$
- $\det(A) = \prod_i \lambda_i$
- For symmetric $A$, eigenvalues are real, eigenvectors are orthogonal.
- For positive definite $A$, all eigenvalues $> 0$.

**Eigendecomposition:** $A = Q \Lambda Q^{-1}$ (if diagonalizable).

For **symmetric** $A$: $A = Q \Lambda Q^T$ where $Q$ is orthogonal.

#### Power Iteration
Algorithm to find dominant eigenvalue/eigenvector:
$$v^{(k+1)} = \frac{A v^{(k)}}{\|A v^{(k)}\|}$$

Converges to eigenvector of largest eigenvalue.

#### Rayleigh Quotient
$$R(A, x) = \frac{x^T A x}{x^T x}$$

For symmetric $A$: $\lambda_{\min} \leq R(A, x) \leq \lambda_{\max}$, with equality at corresponding eigenvectors.

### 1.4 Singular Value Decomposition (SVD)

**Full SVD:** For $A \in \mathbb{R}^{m \times n}$:
$$A = U \Sigma V^T$$

- $U \in \mathbb{R}^{m \times m}$: left singular vectors (orthogonal)
- $V \in \mathbb{R}^{n \times n}$: right singular vectors (orthogonal)
- $\Sigma \in \mathbb{R}^{m \times n}$: diagonal with singular values $\sigma_1 \geq \sigma_2 \geq \cdots \geq \sigma_{\min(m,n)} \geq 0$

**Connection to eigendecomposition:**
- $A^T A = V \Sigma^T \Sigma V^T$ (eigendecomposition of $A^T A$)
- $A A^T = U \Sigma \Sigma^T U^T$ (eigendecomposition of $A A^T$)
- Singular values are square roots of eigenvalues of $A^T A$.

**Truncated SVD (rank-$k$ approximation):**
$$A_k = U_k \Sigma_k V_k^T$$

**Eckart-Young-Mirsky theorem:** $A_k$ is the best rank-$k$ approximation to $A$ under both Frobenius and spectral norms.

**Applications:**
- **PCA:** $X = U \Sigma V^T$; principal components are columns of $V$, scores are $U \Sigma$.
- **Matrix completion:** Minimize $\|P_\Omega(A - UV^T)\|_F^2$ for known entries $\Omega$, with $U, V$ low-rank.
- **Recommendation:** User-item matrix factorization via truncated SVD.
- **Data compression:** Keep top $k$ singular values, discard rest.
- **Dimensionality reduction:** Project data onto top $k$ singular vectors.
- **Pseudoinverse:** $A^+ = V \Sigma^+ U^T$ where $\Sigma^+$ inverts non-zero singular values.

### 1.5 Matrix Calculus

#### Gradient
For $f: \mathbb{R}^n \to \mathbb{R}$, the gradient $\nabla f \in \mathbb{R}^n$:
$$(\nabla f(x))_i = \frac{\partial f}{\partial x_i}$$

**Direction of steepest ascent:** $\nabla f(x)$

#### Jacobian
For $f: \mathbb{R}^n \to \mathbb{R}^m$, the Jacobian $J \in \mathbb{R}^{m \times n}$:
$$J_{ij} = \frac{\partial f_i}{\partial x_j}$$

#### Hessian
For $f: \mathbb{R}^n \to \mathbb{R}$, the Hessian $H \in \mathbb{R}^{n \times n}$:
$$H_{ij} = \frac{\partial^2 f}{\partial x_i \partial x_j}$$

**For backpropagation:**
- Forward pass computes function values
- Backward pass applies chain rule: $\frac{\partial L}{\partial x} = \frac{\partial L}{\partial y} \cdot \frac{\partial y}{\partial x}$
- Key: $\frac{\partial}{\partial X} (W X + b) = W^T$, $\frac{\partial}{\partial W} (W X + b) = X^T$

#### Useful Matrix Derivatives

| Expression | Derivative |
|---|---|
| $\frac{\partial}{\partial x} (a^T x)$ | $a$ |
| $\frac{\partial}{\partial x} (x^T A x)$ | $(A + A^T)x$ |
| $\frac{\partial}{\partial X} \|X\|_F^2$ | $2X$ |
| $\frac{\partial}{\partial W} \|y - W x\|_2^2$ | $2(W x - y)x^T$ |

### 1.6 Positive Definiteness

A symmetric matrix $A \in \mathbb{R}^{n \times n}$ is:
- **Positive definite (PD):** $x^T A x > 0$ for all $x \neq 0$ (all eigenvalues $> 0$)
- **Positive semidefinite (PSD):** $x^T A x \geq 0$ for all $x$ (all eigenvalues $\geq 0$)
- **Negative definite:** $x^T A x < 0$ for all $x \neq 0$
- **Indefinite:** Has both positive and negative eigenvalues

**Why PD matters:**
- Hessian PD $\implies$ local minimum
- $X^T X$ is always PSD
- Covariance matrices are PSD
- Quadratic form $x^T A x$ is convex iff $A$ is PSD

### 1.7 Matrix Factorization

**Non-negative Matrix Factorization (NMF):** $A \approx WH$ with $W, H \geq 0$. Parts-based representation.

**Cholesky Decomposition:** $A = LL^T$ for PD $A$. Fast linear solves.

**QR Decomposition:** $A = QR$ ($Q$ orthogonal, $R$ upper triangular). Used for numerically stable least squares.

**LU Decomposition:** $A = LU$ ($L$ lower, $U$ upper triangular). Solve linear systems.

**Low-rank factorization:** $A \approx UV^T$ with $U \in \mathbb{R}^{m \times k}$, $V \in \mathbb{R}^{n \times k}$. Used in recommendation systems (matrix factorization for collaborative filtering).

### 1.8 Interview Questions — Linear Algebra

**Q1:** Prove that the covariance matrix is positive semidefinite.

> $\Sigma = \mathbb{E}[(X - \mu)(X - \mu)^T]$. For any $v \neq 0$:
> $$v^T \Sigma v = v^T \mathbb{E}[(X - \mu)(X - \mu)^T] v = \mathbb{E}[v^T (X - \mu)(X - \mu)^T v] = \mathbb{E}[((X - \mu)^T v)^2] \geq 0$$
> Since it's an expectation of a square, it's always $\geq 0$.

**Q2:** Show that $A^T A$ is always PSD.

> $x^T (A^T A) x = (Ax)^T (Ax) = \|Ax\|^2 \geq 0$ for all $x$.

**Q3:** Derive the closed-form solution for linear regression.

> Minimize $\|y - X\beta\|_2^2$. Gradient: $\nabla_\beta = -2X^T(y - X\beta) = 0$.
> $$X^T y = X^T X \beta \implies \hat{\beta} = (X^T X)^{-1} X^T y$$

**Q4:** What is the SVD and why is it useful for PCA?

> PCA finds directions of maximum variance. The covariance is $X^T X = V \Sigma^2 V^T$ via SVD of $X = U \Sigma V^T$. Principal components are columns of $V$, and projection onto top $k$ PCs gives best rank-$k$ approximation.

**Q5:** Why is the pseudoinverse $A^+$ important in ML?

> For $Ax = y$, if $A$ is not square or not full rank, $A^+ y$ gives the minimum-norm least-squares solution. $A^+ = V \Sigma^+ U^T$.

**Q6:** How are eigenvalues related to the convergence of gradient descent?

> For quadratic $f(x) = \frac{1}{2}x^T A x$ with $A$ symmetric PD, GD converges at rate $O((\kappa - 1)/(\kappa + 1))^k$ where $\kappa = \lambda_{\max}/\lambda_{\min}$ is the condition number. Larger $\kappa$ = slower convergence.

**Q7:** What is the trace trick and how is it used?

> $x^T A x = \text{Tr}(x^T A x) = \text{Tr}(A x x^T)$. Used in deriving MLE for multivariate Gaussian: the log-likelihood involves $(x_i - \mu)^T \Sigma^{-1} (x_i - \mu)$.

**Q8:** Derive the gradient of $f(W) = \|y - W x\|_2^2$ w.r.t. $W$.

> $f(W) = (y - W x)^T (y - W x) = y^T y - 2y^T W x + x^T W^T W x$.
> $\frac{\partial f}{\partial W} = -2 y x^T + 2 W x x^T = 2(W x - y)x^T$

---

## 2. Calculus

### 2.1 Derivatives

#### Chain Rule
For $f(g(x))$: $\frac{df}{dx} = \frac{df}{dg} \cdot \frac{dg}{dx}$

**For backpropagation:** If $L = f(z)$, $z = g(x)$, $y = h(z)$:
$$\frac{\partial L}{\partial x} = \frac{\partial L}{\partial y} \cdot \frac{\partial y}{\partial z} \cdot \frac{\partial z}{\partial x}$$

Key insight: backprop is repeated application of the chain rule, computing gradients from output back to input.

#### Partial Derivatives
For $f(x_1, \ldots, x_n)$: $\frac{\partial f}{\partial x_i}$ measures rate of change holding all other variables constant.

**Example:** For $f(x, y) = x^2 y + y^3$:
$$\frac{\partial f}{\partial x} = 2xy, \quad \frac{\partial f}{\partial y} = x^2 + 3y^2$$

#### Total Derivative
When variables depend on a common parameter $t$:
$$\frac{df}{dt} = \frac{\partial f}{\partial x} \frac{dx}{dt} + \frac{\partial f}{\partial y} \frac{dy}{dt}$$

### 2.2 Gradient

**Definition:** $\nabla f = \left( \frac{\partial f}{\partial x_1}, \ldots, \frac{\partial f}{\partial x_n} \right)$

**Properties:**
- Points in direction of steepest ascent
- Magnitude gives rate of change in that direction
- Orthogonal to level sets of $f$
- $-\nabla f$ is direction of steepest descent

#### Gradient Descent
$$x^{(k+1)} = x^{(k)} - \eta \nabla f(x^{(k)})$$

Converges to local minimum (for convex $f$, global minimum).

**Interpretation:** Negative gradient points downhill; we take a step of size $\eta$ in that direction.

### 2.3 Taylor Series

**First-order approximation:**
$$f(x + \Delta x) \approx f(x) + \nabla f(x)^T \Delta x$$

**Second-order approximation:**
$$f(x + \Delta x) \approx f(x) + \nabla f(x)^T \Delta x + \frac{1}{2} \Delta x^T H(x) \Delta x$$

**Uses:**
- First-order: gradient descent
- Second-order: Newton's method, understanding curvature, convergence analysis
- Optimality conditions: at minimum, $\nabla f = 0$ and $H$ is PSD

### 2.4 Convexity

A function $f$ is **convex** if:
$$f(\theta x + (1-\theta) y) \leq \theta f(x) + (1-\theta) f(y) \quad \forall \theta \in [0,1]$$

**Equivalent conditions (for differentiable $f$):**
- $f(y) \geq f(x) + \nabla f(x)^T (y - x)$ (first-order condition — function lies above tangent)
- $\nabla^2 f(x) \succeq 0$ (Hessian PSD for twice differentiable)
- Line restriction: $g(t) = f(x + tv)$ is convex in $t$ for any $v$

**Why convex optimization matters:**
- Every local minimum is global
- No saddle points (for strictly convex)
- Strong theoretical guarantees on convergence
- Efficient algorithms exist (gradient descent, Newton, etc.)

**Strong convexity:**
$$f(y) \geq f(x) + \nabla f(x)^T (y - x) + \frac{\mu}{2} \|y - x\|^2$$

Implies unique global minimum, faster convergence.

#### Jensen's Inequality
For convex $f$: $f(\mathbb{E}[X]) \leq \mathbb{E}[f(X)]$

For concave $f$: $f(\mathbb{E}[X]) \geq \mathbb{E}[f(X)]$

**Applications:**
- EM algorithm (E-step uses Jensen's inequality for lower bound)
- KL divergence non-negativity proof: $KL(p\|q) \geq 0$

### 2.5 Lagrange Multipliers

**Constrained optimization:** $\min f(x)$ subject to $g_i(x) = 0, h_j(x) \leq 0$

**Lagrangian:** $\mathcal{L}(x, \lambda, \mu) = f(x) + \sum_i \lambda_i g_i(x) + \sum_j \mu_j h_j(x)$

#### KKT Conditions (necessary for optimality):
1. **Stationarity:** $\nabla \mathcal{L} = 0$
2. **Primal feasibility:** $g_i(x^*) = 0$, $h_j(x^*) \leq 0$
3. **Dual feasibility:** $\mu_j \geq 0$
4. **Complementary slackness:** $\mu_j h_j(x^*) = 0$

**Example — SVM dual derivation:** Primal: $\min \frac{1}{2}\|w\|^2$ s.t. $y_i(w^T x_i + b) \geq 1$.

**Example — Maximum entropy:** Max $H(p)$ subject to $\sum p_i = 1$ and moment constraints.

### 2.6 Automatic Differentiation

#### Forward Mode
- Evaluates function and derivative simultaneously
- One forward pass per input variable
- Efficient for $f: \mathbb{R}^n \to \mathbb{R}$ with $n$ small

#### Reverse Mode (Backpropagation)
- Forward pass computes values
- Backward pass computes gradients
- $O(1)$ cost per gradient for $f: \mathbb{R}^n \to \mathbb{R}$ (vs $O(n)$ for forward)
- This is what PyTorch/TensorFlow/JAX use

**Computational graph:** DAG where nodes are operations, edges are data flow. Reverse mode traverses graph backwards.

### 2.7 Interview Questions — Calculus

**Q1:** Why can't we always use second-order methods (Newton's method) for deep learning?

> Newton's method requires computing and inverting the Hessian ($O(n^3)$) which is infeasible for millions of parameters. Hessian may not be PD (saddle points). Quasi-Newton methods (L-BFGS) approximate Hessian.

**Q2:** Derive the gradient for logistic regression loss.

> $L = -\sum_i [y_i \log \sigma(w^T x_i) + (1 - y_i) \log(1 - \sigma(w^T x_i))]$
> $\frac{\partial L}{\partial w} = \sum_i (\sigma(w^T x_i) - y_i) x_i$

**Q3:** What is the difference between GD, SGD, and mini-batch GD?

> GD: uses full dataset per step. SGD: uses one sample (high variance). Mini-batch: uses $m$ samples (trade-off).

**Q4:** Explain the role of the chain rule in backpropagation.

> Backprop computes $\frac{\partial L}{\partial w}$ for each weight by applying the chain rule backwards through the computational graph, reusing previously computed gradients.

**Q5:** How do you prove that cross-entropy loss is convex for logistic regression?

> Show Hessian is PSD. For logistic regression, $H = \sum_i \sigma(w^T x_i)(1 - \sigma(w^T x_i)) x_i x_i^T$, which is a sum of PSD matrices (since $x_i x_i^T$ is PSD and coefficients are positive).

**Q6:** What is a saddle point and why is it problematic for GD?

> Point where $\nabla f = 0$ but Hessian has both positive and negative eigenvalues. GD can slow down near saddle points. In high dimensions, saddle points are more common than local minima (the "saddle point problem" in deep learning).

---

## 3. Probability

### 3.1 Probability Distributions

#### Bernoulli
- **Support:** $x \in \{0, 1\}$
- **PMF:** $p(x) = p^x (1-p)^{1-x}$
- **Mean:** $p$
- **Variance:** $p(1-p)$
- **Used for:** Binary classification, dropout

#### Binomial
- **Support:** $k \in \{0, 1, \ldots, n\}$
- **PMF:** $p(k) = \binom{n}{k} p^k (1-p)^{n-k}$
- **Mean:** $np$
- **Variance:** $np(1-p)$
- **Interpretation:** Sum of $n$ independent Bernoulli trials

#### Poisson
- **Support:** $k \in \{0, 1, 2, \ldots\}$
- **PMF:** $p(k) = \frac{\lambda^k e^{-\lambda}}{k!}$
- **Mean = Variance:** $\lambda$
- **Used for:** Count data, rare events, modeling arrival rates

#### Uniform
- **PDF:** $p(x) = \frac{1}{b-a}$ for $x \in [a, b]$
- **Mean:** $\frac{a+b}{2}$
- **Variance:** $\frac{(b-a)^2}{12}$
- **Used for:** Random initialization, sampling

#### Normal (Gaussian)
- **PDF:** $p(x) = \frac{1}{\sqrt{2\pi\sigma^2}} \exp\left(-\frac{(x-\mu)^2}{2\sigma^2}\right)$
- **Mean:** $\mu$
- **Variance:** $\sigma^2$
- **Mode = Mean = Median**
- **Multivariate:** $p(x) = \frac{1}{(2\pi)^{d/2}|\Sigma|^{1/2}} \exp\left(-\frac{1}{2}(x-\mu)^T \Sigma^{-1} (x-\mu)\right)$
- **Used for:** Everywhere — noise models, residuals, initialization

#### Exponential
- **PDF:** $p(x) = \lambda e^{-\lambda x}$ for $x \geq 0$
- **Mean:** $1/\lambda$
- **Variance:** $1/\lambda^2$
- **Memoryless property:** $P(X > s + t | X > s) = P(X > t)$
- **Used for:** Waiting times, decay processes

#### Beta
- **PDF:** $p(x) = \frac{x^{\alpha-1}(1-x)^{\beta-1}}{B(\alpha, \beta)}$ for $x \in [0, 1]$
- **Mean:** $\frac{\alpha}{\alpha + \beta}$
- **Variance:** $\frac{\alpha\beta}{(\alpha+\beta)^2(\alpha+\beta+1)}$
- **Conjugate prior for Bernoulli/binomial**
- **Used for:** Modeling probabilities, Bayesian inference

#### Gamma
- **PDF:** $p(x) = \frac{\beta^\alpha}{\Gamma(\alpha)} x^{\alpha-1} e^{-\beta x}$ for $x \geq 0$
- **Mean:** $\alpha/\beta$
- **Variance:** $\alpha/\beta^2$
- **Special cases:** Exponential ($\alpha = 1$), Chi-squared ($\alpha = \nu/2, \beta = 1/2$)
- **Conjugate prior for Poisson rate $\lambda$**

#### Dirichlet
- **Support:** Probability simplex $\sum_i x_i = 1$, $x_i \geq 0$
- **PDF:** $p(x) = \frac{1}{B(\alpha)} \prod_i x_i^{\alpha_i - 1}$
- **Conjugate prior for categorical/multinomial**
- **Used for:** Topic models (LDA), mixture models

### 3.2 Key Properties

| Distribution | PDF/PMF | Mean | Variance | Mode |
|---|---|---|---|---|
| Bernoulli($p$) | $p^x(1-p)^{1-x}$ | $p$ | $p(1-p)$ | $0,1$ |
| Binomial($n,p$) | $\binom{n}{k}p^k(1-p)^{n-k}$ | $np$ | $np(1-p)$ | $\lfloor (n+1)p \rfloor$ |
| Poisson($\lambda$) | $\frac{\lambda^k e^{-\lambda}}{k!}$ | $\lambda$ | $\lambda$ | $\lfloor \lambda \rfloor$ |
| Normal($\mu,\sigma^2$) | $\frac{1}{\sqrt{2\pi}\sigma}e^{-(x-\mu)^2/(2\sigma^2)}$ | $\mu$ | $\sigma^2$ | $\mu$ |
| Exponential($\lambda$) | $\lambda e^{-\lambda x}$ | $1/\lambda$ | $1/\lambda^2$ | $0$ |
| Beta($\alpha,\beta$) | $\frac{x^{\alpha-1}(1-x)^{\beta-1}}{B(\alpha,\beta)}$ | $\frac{\alpha}{\alpha+\beta}$ | $\frac{\alpha\beta}{(\alpha+\beta)^2(\alpha+\beta+1)}$ | $\frac{\alpha-1}{\alpha+\beta-2}$ |
| Gamma($\alpha,\beta$) | $\frac{\beta^\alpha}{\Gamma(\alpha)} x^{\alpha-1} e^{-\beta x}$ | $\alpha/\beta$ | $\alpha/\beta^2$ | $(\alpha-1)/\beta$ |

### 3.3 Maximum Likelihood Estimation (MLE)

**Principle:** Choose parameters $\theta$ that maximize the likelihood of observed data:
$$\hat{\theta}_{MLE} = \arg\max_\theta \prod_{i=1}^n p(x_i | \theta)$$

**Log-likelihood:** $\ell(\theta) = \sum_{i=1}^n \log p(x_i | \theta)$

**Examples:**

**Bernoulli MLE:**
$$\ell(p) = \sum_i [x_i \log p + (1-x_i) \log(1-p)]$$
$$\frac{\partial \ell}{\partial p} = \sum_i \frac{x_i}{p} - \frac{1-x_i}{1-p} = 0 \implies \hat{p} = \frac{1}{n} \sum_i x_i$$

**Gaussian MLE:**
$$\hat{\mu} = \frac{1}{n} \sum_i x_i, \quad \hat{\sigma}^2 = \frac{1}{n} \sum_i (x_i - \hat{\mu})^2$$

Note: MLE variance is biased (divides by $n$, not $n-1$). Bessel's correction uses $n-1$.

**Properties of MLE:**
- Consistent: $\hat{\theta}_{MLE} \xrightarrow{p} \theta^*$
- Asymptotically normal: $\sqrt{n}(\hat{\theta}_{MLE} - \theta^*) \to N(0, I(\theta^*)^{-1})$
- Asymptotically efficient (attains Cramér-Rao lower bound)
- Invariant: if $\hat{\theta}$ is MLE, then $g(\hat{\theta})$ is MLE of $g(\theta)$

### 3.4 Maximum A Posteriori (MAP)

**Bayesian approach:** $\hat{\theta}_{MAP} = \arg\max_\theta p(\theta | x) = \arg\max_\theta p(x | \theta) p(\theta)$

Equivalently: $\hat{\theta}_{MAP} = \arg\max_\theta [\log p(x | \theta) + \log p(\theta)]$

**Connection to regularization:**
- Gaussian prior on weights $\implies$ L2 regularization
- Laplace prior on weights $\implies$ L1 regularization

**MLE vs MAP:**
- MLE: $\arg\max_\theta p(x | \theta)$ (no prior)
- MAP: $\arg\max_\theta p(\theta | x) \propto p(x | \theta) p(\theta)$ (with prior)
- As $n \to \infty$, MAP $\to$ MLE (data dominates)

### 3.5 Bayes' Theorem

$$P(A | B) = \frac{P(B | A) P(A)}{P(B)}$$

**Posterior $\propto$ Likelihood $\times$ Prior:**
$$p(\theta | x) = \frac{p(x | \theta) p(\theta)}{p(x)} \propto p(x | \theta) p(\theta)$$

**Bayesian updating:** Start with prior $p(\theta)$, observe data $x$, compute posterior $p(\theta | x)$. Postrior becomes new prior for next observation.

### 3.6 Conditional Probability & Chain Rule

**Conditional probability:** $P(A | B) = \frac{P(A \cap B)}{P(B)}$

**Law of total probability:** $P(B) = \sum_i P(B | A_i) P(A_i)$

**Chain rule:**
$$P(X_1, \ldots, X_n) = P(X_1) P(X_2 | X_1) P(X_3 | X_1, X_2) \cdots P(X_n | X_1, \ldots, X_{n-1})$$

### 3.7 Expectations, Variance, Covariance

**Expectation:**
$$\mathbb{E}[X] = \begin{cases} \sum_x x p(x) & \text{discrete} \\ \int x p(x) dx & \text{continuous} \end{cases}$$

$$\mathbb{E}[aX + bY] = a\mathbb{E}[X] + b\mathbb{E}[Y]$$

**Variance:**
$$\text{Var}(X) = \mathbb{E}[(X - \mu)^2] = \mathbb{E}[X^2] - \mathbb{E}[X]^2$$

$$\text{Var}(aX + b) = a^2 \text{Var}(X)$$

**Covariance:**
$$\text{Cov}(X, Y) = \mathbb{E}[(X - \mu_X)(Y - \mu_Y)] = \mathbb{E}[XY] - \mathbb{E}[X]\mathbb{E}[Y]$$

- $\text{Cov}(X, Y) = 0$ does NOT imply independence (only uncorrelated)
- $\text{Var}(X + Y) = \text{Var}(X) + \text{Var}(Y) + 2\text{Cov}(X, Y)$

**Correlation:**
$$\rho_{XY} = \frac{\text{Cov}(X, Y)}{\sigma_X \sigma_Y} \in [-1, 1]$$

### 3.8 Law of Large Numbers (LLN)

**Weak LLN:** $\bar{X}_n \xrightarrow{p} \mu$ (converges in probability)

**Strong LLN:** $\bar{X}_n \xrightarrow{a.s.} \mu$ (almost sure convergence)

**Why it matters:** Sample moments converge to population moments. MLE consistency relies on LLN.

### 3.9 Central Limit Theorem (CLT)

For i.i.d. $X_1, \ldots, X_n$ with mean $\mu$ and variance $\sigma^2$:
$$\sqrt{n}(\bar{X}_n - \mu) \xrightarrow{d} N(0, \sigma^2)$$

Equivalently:
$$\frac{\bar{X}_n - \mu}{\sigma / \sqrt{n}} \xrightarrow{d} N(0, 1)$$

**Why it matters:**
- Justifies normality assumptions in many statistical tests
- Confidence intervals: $\bar{x} \pm z_{\alpha/2} \cdot \frac{\sigma}{\sqrt{n}}$
- Central to hypothesis testing
- Explains why many real-world distributions are approximately normal

### 3.10 Information Theory

#### Entropy
$$H(X) = -\sum_x p(x) \log p(x) = \mathbb{E}[-\log p(X)]$$

- Measures uncertainty / average information content
- Maximum entropy: Uniform distribution (for discrete, finite support)
- For continuous: differential entropy $h(X) = -\int p(x) \log p(x) dx$

#### Cross-Entropy
$$H(p, q) = -\sum_x p(x) \log q(x) = H(p) + KL(p \| q)$$

- Used as loss for classification (cross-entropy loss = negative log-likelihood)
- $H(p, q) \geq H(p)$ with equality iff $p = q$

#### KL Divergence
$$KL(p \| q) = \sum_x p(x) \log \frac{p(x)}{q(x)}$$

- **Non-negative:** $KL(p \| q) \geq 0$, equality iff $p = q$ (proved via Jensen)
- **Not symmetric:** $KL(p \| q) \neq KL(q \| p)$ (not a metric)
- **Not a distance** (no triangle inequality)

**Forward KL** $KL(p \| q)$: "moment-matching" — $q$ must cover all modes of $p$ (mean-seeking)
**Reverse KL** $KL(q \| p)$: "mode-seeking" — $q$ picks one mode of $p$

**Used in:** Variational inference (minimizing $KL(q \| p)$), model distillation, GANs

#### Mutual Information
$$I(X; Y) = KL(p(x,y) \| p(x)p(y)) = H(X) - H(X | Y) = H(Y) - H(Y | X)$$

- Measures dependence between $X$ and $Y$
- $I(X; Y) = 0$ iff $X$ and $Y$ are independent

#### Jensen-Shannon Divergence
$$JS(p \| q) = \frac{1}{2} KL(p \| m) + \frac{1}{2} KL(q \| m)$$
where $m = \frac{p+q}{2}$

- Symmetric and bounded $[0, \log 2]$
- Square root of JS is a metric
- Used in GANs (original GAN loss approximates JS divergence)

### 3.11 Interview Questions — Probability

**Q1:** Derive the MLE for the mean of a Gaussian distribution.

> $\ell(\mu, \sigma^2) = -\frac{n}{2}\log(2\pi\sigma^2) - \frac{1}{2\sigma^2} \sum_i (x_i - \mu)^2$
> $\frac{\partial \ell}{\partial \mu} = \frac{1}{\sigma^2} \sum_i (x_i - \mu) = 0 \implies \hat{\mu} = \frac{1}{n} \sum_i x_i$

**Q2:** Show that the Bernoulli distribution is in the exponential family.

> $p(x | p) = \exp\{x \log p + (1-x) \log(1-p)\} = \exp\{x \log\frac{p}{1-p} + \log(1-p)\}$
> Natural parameter: $\eta = \log\frac{p}{1-p}$, sufficient statistic: $T(x) = x$, log-partition: $A(\eta) = \log(1 + e^\eta)$

**Q3:** Prove that KL divergence is non-negative.

> $KL(p \| q) = -\sum p(x) \log \frac{q(x)}{p(x)} \geq -\log \sum p(x) \frac{q(x)}{p(x)} = -\log \sum q(x) = 0$
> (Using Jensen's inequality since $-\log$ is convex)

**Q4:** What is the relationship between cross-entropy loss and MLE?

> Minimizing cross-entropy = maximizing log-likelihood. For classification, cross-entropy loss is exactly the negative log-likelihood under a categorical distribution.

**Q5:** How does MAP relate to regularization?

> MAP with Gaussian prior $\theta \sim N(0, \tau^2 I)$ gives $\ell_2$-regularized objective: $\min_\theta -\log p(x|\theta) + \frac{1}{2\tau^2} \|\theta\|_2^2$. MAP with Laplace prior gives L1 regularization.

**Q6:** Explain the Central Limit Theorem and why it's important.

> Sum/average of i.i.d. random variables converges to normal distribution, regardless of original distribution. Enables confidence intervals, hypothesis tests, and justifies normality assumptions.

**Q7:** What is the difference between MLE and MAP?

> MLE maximizes $p(x|\theta)$, MAP maximizes $p(\theta|x) \propto p(x|\theta)p(\theta)$. MAP includes a prior; as $n \to \infty$, they converge.

**Q8:** Derive the expectation and variance of a Beta distribution.

> $\mathbb{E}[X] = \frac{\alpha}{\alpha+\beta}$, $\text{Var}(X) = \frac{\alpha\beta}{(\alpha+\beta)^2(\alpha+\beta+1)}$

---

## 4. Statistics

### 4.1 Hypothesis Testing

#### Null and Alternative Hypotheses
- $H_0$: Null hypothesis (status quo, no effect)
- $H_a$: Alternative hypothesis (what we want to prove)

#### p-value
Probability of observing a test statistic at least as extreme as the observed one, assuming $H_0$ is true.

**Interpretation:** Smaller p-value = stronger evidence against $H_0$.

**Common threshold:** $\alpha = 0.05$. If $p < \alpha$, reject $H_0$.

**Misconception:** p-value is NOT the probability that $H_0$ is true. It's $P(\text{data} | H_0)$, not $P(H_0 | \text{data})$.

#### Type I and Type II Errors

| Decision | $H_0$ True | $H_0$ False |
|---|---|---|
| Fail to reject $H_0$ | Correct | Type II Error ($\beta$) |
| Reject $H_0$ | Type I Error ($\alpha$) | Correct (Power $= 1-\beta$) |

- **Type I ($\alpha$):** False positive — rejecting $H_0$ when it's true
- **Type II ($\beta$):** False negative — failing to reject $H_0$ when it's false
- **Power ($1-\beta$):** Probability of correctly rejecting $H_0$ when false

#### Significance Level
$\alpha$ is the maximum acceptable Type I error rate. Common values: 0.01, 0.05, 0.10.

### 4.2 Common Statistical Tests

#### t-test
- **One-sample:** Tests if mean differs from known value
- **Two-sample (independent):** Tests if means of two groups differ
- **Paired:** Tests if mean difference in paired observations differs from zero
- **Assumptions:** Normality (approximately), independence

**Test statistic:** $t = \frac{\bar{x} - \mu_0}{s / \sqrt{n}} \sim t_{n-1}$

#### Chi-Square Test
- **Goodness of fit:** Tests if observed frequencies match expected distribution
- **Test of independence:** Tests if two categorical variables are independent
- **Test statistic:** $\chi^2 = \sum \frac{(O_i - E_i)^2}{E_i} \sim \chi^2_{df}$

#### ANOVA (Analysis of Variance)
- Tests if means of 3+ groups are equal
- **One-way ANOVA:** One categorical factor
- **Two-way ANOVA:** Two categorical factors (with/without interaction)
- **F-statistic:** $F = \frac{\text{between-group variance}}{\text{within-group variance}} \sim F_{k-1, n-k}$

### 4.3 Confidence Intervals

**Definition:** An interval $[L, U]$ such that $P(L \leq \theta \leq U) = 1 - \alpha$ for 95% CI.

**Interpretation (Frequentist):** If we repeated the experiment many times, 95% of CIs would contain the true parameter. NOT "95% probability the parameter is in this interval."

**For mean (known $\sigma$):** $\bar{x} \pm z_{\alpha/2} \cdot \frac{\sigma}{\sqrt{n}}$

**For mean (unknown $\sigma$):** $\bar{x} \pm t_{\alpha/2, n-1} \cdot \frac{s}{\sqrt{n}}$

**Bootstrap CI:** Resample data with replacement, compute statistic each time, take percentiles.

### 4.4 Bias-Variance Tradeoff

**Decomposition of expected test error:**
$$\mathbb{E}[(y - \hat{f}(x))^2] = \text{Bias}(\hat{f}(x))^2 + \text{Var}(\hat{f}(x)) + \sigma^2$$

Where:
- **Bias:** $\mathbb{E}[\hat{f}(x)] - f(x)$ — error from incorrect assumptions
- **Variance:** $\mathbb{E}[(\hat{f}(x) - \mathbb{E}[\hat{f}(x)])^2]$ — sensitivity to training data
- **Irreducible error:** $\sigma^2 = \text{Var}(y | x)$

**Tradeoff:**
- **High bias, low variance:** Underfitting (e.g., linear model for complex data)
- **Low bias, high variance:** Overfitting (e.g., deep tree for small data)
- Goal: find sweet spot that minimizes total error

**Formal derivation:**
$$
\begin{aligned}
\mathbb{E}[(y - \hat{f})^2] &= \mathbb{E}[(f + \epsilon - \hat{f})^2] \\
&= \mathbb{E}[(\hat{f} - f)^2] + 2\mathbb{E}[(\hat{f} - f)\epsilon] + \mathbb{E}[\epsilon^2] \\
&= \text{Var}(\hat{f}) + \text{Bias}(\hat{f})^2 + \sigma^2
\end{aligned}
$$
(since $\mathbb{E}[\epsilon] = 0$ and $\epsilon$ independent of $\hat{f}$)

### 4.5 Overfitting / Underfitting — Regularization

#### L1 Regularization (Lasso)
$$\min_\beta \|y - X\beta\|_2^2 + \lambda \|\beta\|_1$$

- Encourages sparsity (feature selection)
- Non-differentiable at zero — requires subgradients
- MAP with Laplace prior

#### L2 Regularization (Ridge)
$$\min_\beta \|y - X\beta\|_2^2 + \lambda \|\beta\|_2^2$$

- Closed form: $\hat{\beta} = (X^T X + \lambda I)^{-1} X^T y$
- Shrinks coefficients proportionally
- MAP with Gaussian prior

#### ElasticNet
$$\min_\beta \|y - X\beta\|_2^2 + \lambda_1 \|\beta\|_1 + \lambda_2 \|\beta\|_2^2$$

- Combines L1 sparsity and L2 stability
- Useful when features are correlated

### 4.6 Sampling Methods

#### Random Sampling
Each sample has equal probability of being selected. Unbiased but can be impractical.

#### Stratified Sampling
Divide population into strata, sample proportionally from each. Reduces variance compared to random sampling. Important for imbalanced datasets.

#### Importance Sampling
$$E_{p}[f(X)] = \int f(x) p(x) dx = \int f(x) \frac{p(x)}{q(x)} q(x) dx \approx \frac{1}{n} \sum_i f(x_i) \frac{p(x_i)}{q(x_i)}$$
where $x_i \sim q(x)$.

- Used when $p$ is hard to sample from
- Weight $w_i = p(x_i)/q(x_i)$
- Variance depends on how well $q$ matches $p$

### 4.7 Bayesian vs Frequentist

| Aspect | Frequentist | Bayesian |
|---|---|---|
| Probability | Long-run frequency | Degree of belief |
| Parameters | Fixed (unknown) constants | Random variables with distributions |
| Inference | Point estimates, CIs, p-values | Posterior distributions, credible intervals |
| Prior | Not used | Required (can be informative or uninformative) |
| Interpretation of CI | 95% of CIs contain $\theta$ | 95% probability $\theta$ is in interval |

**Bayesian inference:**
$$p(\theta | x) = \frac{p(x | \theta) p(\theta)}{\int p(x | \theta) p(\theta) d\theta}$$

**Credible Interval:** Interval $[a, b]$ such that $P(a \leq \theta \leq b | x) = 0.95$. More intuitive than frequentist CI.

**Conjugate priors:**
- Beta is conjugate for Bernoulli/Binomial
- Gamma is conjugate for Poisson
- Normal (with known variance) is conjugate for Normal mean
- Dirichlet is conjugate for Multinomial

**Posterior predictive distribution:**
$$p(\tilde{x} | x) = \int p(\tilde{x} | \theta) p(\theta | x) d\theta$$

### 4.8 Interview Questions — Statistics

**Q1:** Derive the bias-variance decomposition.

> See derivation in section 4.4. Key steps: add/subtract $f(x)$, use $\mathbb{E}[\epsilon] = 0$, and $\mathbb{E}[\epsilon^2] = \sigma^2$, $\mathbb{E}[\hat{f}^2] - \mathbb{E}[\hat{f}]^2 = \text{Var}(\hat{f})$.

**Q2:** Explain the difference between L1 and L2 regularization geometrically.

> L1: diamond constraint region — intersections at axes (sparsity). L2: sphere constraint — smooth shrinkage. L1 yields exact zeros, L2 does not.

**Q3:** Why does ridge regression have a closed-form solution but lasso does not?

> Ridge: objective is differentiable and strictly convex; $\ell_2$ regularization adds a quadratic penalty that keeps $X^T X + \lambda I$ invertible. Lasso: $\ell_1$ penalty is non-differentiable at zero; no closed form; solved via coordinate descent or LARS.

**Q4:** What is the difference between a confidence interval and a credible interval?

> Confidence interval (frequentist): random interval covering fixed parameter 95% of repeated experiments. Credible interval (Bayesian): fixed interval containing 95% of posterior probability.

**Q5:** How would you test whether a new ML model is significantly better than the baseline?

> Paired t-test or McNemar's test on matched predictions. Or use 5x2 cross-validation paired t-test. Control for multiple testing if comparing many models.

**Q6:** Explain the p-value in simple terms.

> The probability of observing data at least as extreme as what we saw, assuming the null hypothesis is true. A small p-value suggests the null is unlikely.

**Q7:** What is the multiple testing problem and how do you correct for it?

> When testing many hypotheses simultaneously, Type I error rate inflates. Corrections: Bonferroni ($\alpha / m$), Benjamini-Hochberg (FDR control), Holm-Bonferroni.

---

## 5. Optimization

### 5.1 Convex vs Non-Convex Optimization

**Convex optimization:** Minimize convex function over convex set.
- Every local minimum is global
- Strong duality holds
- Efficient algorithms with guarantees

**Non-convex optimization:** Objective has multiple local minima, saddle points.
- Deep learning loss surfaces are highly non-convex
- No global convergence guarantees in general
- SGD often finds "good enough" solutions (deep learning works in practice)

**Why deep learning works despite non-convexity:**
- Overparameterization creates many "almost-global" minima
- SGD noise helps escape saddle points
- Loss landscape becomes more convex near minima
- Implicit regularization of SGD

### 5.2 Gradient Descent Variants

#### Batch Gradient Descent
$$x^{(k+1)} = x^{(k)} - \eta \nabla f(x^{(k)})$$

- Uses entire dataset per step
- Deterministic, low variance
- Slow for large datasets

#### Stochastic Gradient Descent (SGD)
$$x^{(k+1)} = x^{(k)} - \eta \nabla f_i(x^{(k)})$$

- Uses one random sample per step
- High variance but fast per iteration
- Noise helps escape saddle points
- Convergence: $O(1/\sqrt{k})$ for non-smooth, $O(1/k)$ for smooth strongly convex

#### Mini-Batch SGD
$$x^{(k+1)} = x^{(k)} - \eta \cdot \frac{1}{B} \sum_{i \in B} \nabla f_i(x^{(k)})$$

- Trade-off between batch and stochastic
- Batch size: typical 32, 64, 128, 256
- Larger batches = less variance, more computation per step
- Extreme large batch can hurt generalization

#### SGD with Momentum
$$v^{(k+1)} = \beta v^{(k)} + \nabla f(x^{(k)})$$
$$x^{(k+1)} = x^{(k)} - \eta v^{(k+1)}$$

- Accelerates convergence
- Smooths oscillations
- Typical $\beta = 0.9$

#### Adam (Adaptive Moment Estimation)
$$m_t = \beta_1 m_{t-1} + (1 - \beta_1) g_t$$
$$v_t = \beta_2 v_{t-1} + (1 - \beta_2) g_t^2$$
$$\hat{m}_t = m_t / (1 - \beta_1^t), \quad \hat{v}_t = v_t / (1 - \beta_2^t)$$
$$x_{t+1} = x_t - \eta \frac{\hat{m}_t}{\sqrt{\hat{v}_t} + \epsilon}$$

- Adaptive learning rate per parameter
- Combines momentum + RMSProp
- Default: $\beta_1 = 0.9$, $\beta_2 = 0.999$, $\epsilon = 10^{-8}$

### 5.3 First-Order vs Second-Order Methods

#### First-Order Methods
Use only gradient information. Examples: GD, SGD, Adam, AdaGrad.
- $O(n)$ per iteration ($n$ = number of parameters)
- Linear convergence for strongly convex

#### Second-Order Methods
Use Hessian information. Examples: Newton's method, quasi-Newton, L-BFGS.

**Newton's method:**
$$x^{(k+1)} = x^{(k)} - H(x^{(k)})^{-1} \nabla f(x^{(k)})$$

- Quadratic convergence near optimum
- $O(n^3)$ per iteration (inverting Hessian)
- Hessian may not be PD (use modified Newton)

**Quasi-Newton (BFGS):** Approximates Hessian using gradient differences, $O(n^2)$ per iteration.

**L-BFGS:** Limited memory BFGS — stores only last $m$ gradient differences, $O(mn)$ per iteration. Good for large problems.

### 5.4 Constrained Optimization

#### Lagrangian
$$\min f(x) \quad \text{s.t.} \quad g_i(x) \leq 0, \; h_j(x) = 0$$

$$\mathcal{L}(x, \lambda, \mu) = f(x) + \sum_i \lambda_i g_i(x) + \sum_j \mu_j h_j(x)$$

#### KKT Conditions
See Section 2.5.

#### Projected Gradient Descent
For $\min_{x \in C} f(x)$:
$$x^{(k+1)} = \text{Proj}_C(x^{(k)} - \eta \nabla f(x^{(k)}))$$

Where $\text{Proj}_C(y) = \arg\min_{x \in C} \|x - y\|_2^2$.

**Example:** $\ell_2$ ball projection, non-negativity constraints.

**Proximal gradient descent:** For $\min f(x) + g(x)$ where $g$ is non-smooth (e.g., L1):
$$x^{(k+1)} = \text{prox}_{\eta g}(x^{(k)} - \eta \nabla f(x^{(k)}))$$

Where $\text{prox}_{\eta g}(y) = \arg\min_x \frac{1}{2\eta} \|x - y\|_2^2 + g(x)$.

**For L1 ($g(x) = \lambda \|x\|_1$):** Soft thresholding:
$$\text{prox}_{\eta \lambda \|\cdot\|_1}(y)_i = \text{sign}(y_i) \max(|y_i| - \eta\lambda, 0)$$

### 5.5 Subgradients

For convex but non-differentiable $f$, subgradient $g$ satisfies:
$$f(y) \geq f(x) + g^T (y - x) \quad \forall y$$

**Subdifferential:** $\partial f(x)$ = set of all subgradients at $x$.

**For L1 norm:** $\partial \|x\|_1 = \begin{cases} \{1\} & x > 0 \\ \{-1\} & x < 0 \\ [-1, 1] & x = 0 \end{cases}$

**Subgradient method:** $x^{(k+1)} = x^{(k)} - \eta_k g^{(k)}$ where $g^{(k)} \in \partial f(x^{(k)})$.

Converges with diminishing step sizes: $\sum \eta_k = \infty$, $\sum \eta_k^2 < \infty$.

### 5.6 Convergence Rates

| Method | Smooth Strongly Convex | Smooth Convex | Non-Smooth Convex |
|---|---|---|---|
| GD | $O(c^k)$ (linear) | $O(1/k)$ | — |
| SGD | — | $O(1/\sqrt{k})$ | $O(1/\sqrt{k})$ |
| Accelerated GD | $O(c^{\sqrt{\kappa}})$ | $O(1/k^2)$ | — |
| Subgradient | — | — | $O(1/\sqrt{k})$ |
| Newton | $O(c^{2^k})$ (quadratic) | — | — |

**Linear convergence:** Error decreases exponentially with iterations.
**Sublinear convergence:** Error decreases as $O(1/k)$ or $O(1/\sqrt{k})$.

**Condition number $\kappa$:** Ratio of largest to smallest eigenvalue. Higher $\kappa$ = harder problem.

### 5.7 Saddle Points and Local Minima in Deep Learning

- In high-dimensional non-convex optimization, critical points are more likely saddle points than local minima (Bray & Dean, 2007; Dauphin et al., 2014).
- Hessian at saddle has both positive and negative eigenvalues.
- GD can slow near saddle points (plateau).
- SGD noise helps escape saddle points.
- Overparameterization tends to make local minima global.

**Escape analysis:**
- PGD (plain GD) requires $O(\log(1/\epsilon))$ iterations to escape strict saddle.
- SGD with noise can escape faster.
- Perturbation (adding noise) helps.

### 5.8 Interview Questions — Optimization

**Q1:** Why does SGD converge and how does the learning rate schedule affect convergence?

> SGD converges because the expected update direction equals the true gradient. Learning rate must decay: $\eta_k = O(1/k)$ typically. Too fast decay = slow progress; too slow = oscillates.

**Q2:** What is the condition number and why does it matter?

> $\kappa = \lambda_{\max} / \lambda_{\min}$ for Hessian at optimum. Large $\kappa$ means ill-conditioned problem — GD converges slowly (zig-zags). Preconditioning or adaptive methods help.

**Q3:** Explain the relationship between momentum and the condition number.

> Momentum accelerates GD by damping oscillations in high-curvature directions. Effectively reduces condition number for convergence.

**Q4:** When would you use L-BFGS over Adam?

> L-BFGS is better for small-to-medium problems where second-order information helps. Adam is better for large-scale deep learning, non-convex problems, and when gradient noise is high.

**Q5:** Prove that gradient descent with exact line search converges linearly for strongly convex quadratics.

> For $f(x) = \frac{1}{2}x^T A x$ with $A$ PD, exact step $\eta_k = (g_k^T g_k)/(g_k^T A g_k)$. Convergence factor depends on $\kappa$.

**Q6:** What is the difference between convergence in objective value and convergence in parameters?

> $f(x_k) - f(x^*)$ can converge even if $x_k$ is far from $x^*$ (flat region). Conversely, parameters can be close but objective difference large (steep region). Both matter.

**Q7:** How does batch size affect training?

> Small batch: high variance, regularization effect, can escape saddle points. Large batch: precise gradients, may converge to sharper minima (generalize worse), more parallelism. There is often a "critical batch size" beyond which returns diminish.

**Q8:** Explain why deep learning optimization is hard.

> Non-convex landscape with many local minima, saddle points, plateaus. High-dimensional (millions of parameters). Ill-conditioned curvature. Vanishing/exploding gradients. No global convergence guarantees.

---

## Quick Reference: Key Formulas

| Topic | Formula |
|---|---|
| Dot product | $a \cdot b = \|a\|\|b\|\cos\theta$ |
| Linear regression | $\hat{\beta} = (X^T X)^{-1} X^T y$ |
| SVD | $A = U \Sigma V^T$ |
| KL divergence | $KL(p \| q) = \sum p(x) \log(p(x)/q(x))$ |
| Cross-entropy | $H(p,q) = -\sum p(x) \log q(x)$ |
| Bias-variance | $\mathbb{E}[(y-\hat{f})^2] = \text{Bias}^2 + \text{Var} + \sigma^2$ |
| Bayes theorem | $p(\theta \mid x) \propto p(x \mid \theta) p(\theta)$ |
| Gradient descent | $x_{t+1} = x_t - \eta \nabla f(x_t)$ |
| Chain rule | $\frac{df}{dx} = \frac{df}{dg} \frac{dg}{dx}$ |
| CLT | $\sqrt{n}(\bar{X} - \mu) \xrightarrow{d} N(0, \sigma^2)$ |
| MLE | $\hat{\theta} = \arg\max \log p(x \mid \theta)$ |
| Ridge closed form | $\hat{\beta} = (X^T X + \lambda I)^{-1} X^T y$ |
| KKT stationarity | $\nabla f + \sum \lambda_i \nabla g_i + \sum \mu_j \nabla h_j = 0$ |
| Entropy | $H(X) = -\sum p(x) \log p(x)$ |
| Mutual information | $I(X;Y) = H(X) - H(X \mid Y)$ |

---

*Last updated: July 2026*
