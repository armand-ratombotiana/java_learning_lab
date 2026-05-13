# Optimization for Machine Learning - Complete Theory

## Table of Contents
1. [Optimization Fundamentals](#1-optimization-fundamentals)
2. [Gradient Descent](#2-gradient-descent)
3. [Stochastic Methods](#3-stochastic-methods)
4. [Adaptive Learning Rates](#4-adaptive-learning-rates)
5. [Momentum Methods](#5-momentum-methods)
6. [Second Order Methods](#6-second-order-methods)
7. [Constrained Optimization](#7-constrained-optimization)
8. [Regularization](#8-regularization)
9. [Advanced Algorithms](#9-advanced-algorithms)
10. [Practical Considerations](#10-practical-considerations)

---

## 1. Optimization Fundamentals

### 1.1 Problem Formulation

**Unconstrained optimization**:
$$\min_{\mathbf{x} \in \mathbb{R}^n} f(\mathbf{x})$$

**Constrained optimization**:
$$\min_{\mathbf{x}} f(\mathbf{x}) \quad \text{subject to} \quad g_i(\mathbf{x}) = 0, \quad h_j(\mathbf{x}) \leq 0$$

### 1.2 Types of Problems

| Classification | Description |
|---------------|-------------|
| Convex vs Non-convex | Shape of objective function |
| Constrained vs Unconstrained | Presence of constraints |
| Continuous vs Discrete | Domain of variables |
| Single vs Multi-objective | Number of objectives |

### 1.3 Convex Functions

Function f is convex if for all x, y and λ ∈ [0, 1]:
$$f(\lambda x + (1-\lambda)y) \leq \lambda f(x) + (1-\lambda)f(y)$$

**Properties**:
- Any local minimum is global minimum
- Hessian is positive semi-definite
- Epigraph is convex set

### 1.4 Optimality Conditions

**Necessary conditions** (for local minimum):
- Gradient zero: ∇f(x*) = 0
- Feasible (satisfies constraints)

**First order**: ∇f(x*) = 0

**Second order necessary**: Hessian positive semi-definite

**Second order sufficient**: Hessian positive definite

### 1.5 Convergence Rates

| Rate | Description | Example |
|------|-------------|---------|
| O(1/t) | Sublinear | Gradient descent on convex |
| O(1/t²) | Linear (fast) | Momentum, Newton |
| O(c^t) | Exponential | Strongly convex with proper step |
| O(e^{-t}) | Superlinear | Certain second-order methods |

---

## 2. Gradient Descent

### 2.1 Basic Algorithm

$$\mathbf{x}_{t+1} = \mathbf{x}_t - \alpha_t \nabla f(\mathbf{x}_t)$$

where α_t > 0 is learning rate (step size).

### 2.2 Interpretation

- Take step in direction of negative gradient
- Step size controls convergence speed
- Too small: slow convergence
- Too large: may diverge

### 2.3 Convergence Analysis

**For smooth convex f**:
$$f(\mathbf{x}_t) - f(\mathbf{x}^*) \leq \frac{L \|\mathbf{x}_0 - \mathbf{x}^*\|^2}{2t}$$

where L is Lipschitz constant of gradient: ||∇f(x) - ∇f(y)|| ≤ L||x - y||

**For smooth strongly convex f** (μ > 0):
$$f(\mathbf{x}_t) - f(\mathbf{x}^*) \leq \left(1 - \frac{\mu}{L}\right)^t (f(\mathbf{x}_0) - f(\mathbf{x}^*))$$

### 2.4 Step Size Selection

**Constant step**: α = c/L (theoretical guarantee)

**Line search**: Find α minimizing f(x_t - α∇f(x_t))

**Backtracking**: Start with α, reduce by factor β until:
$$f(\mathbf{x}_t - \alpha \nabla f) \leq f(\mathbf{x}_t) - c \alpha \|\nabla f\|^2$$

### 2.5 Variants

**Batch GD**: Use full dataset, stable but slow.

**Stochastic GD**: Use single sample, fast but noisy.

**Mini-batch GD**: Use batch of samples, balance speed and stability.

---

## 3. Stochastic Methods

### 3.1 Stochastic Gradient Descent (SGD)

$$\mathbf{x}_{t+1} = \mathbf{x}_t - \alpha_t \nabla f_i(\mathbf{x}_t)$$

where i is randomly selected sample.

### 3.2 Why Stochastic?

- Large datasets: computing full gradient is expensive
- Escaping local minima in non-convex problems
- Better generalization due to noise

### 3.3 Convergence Conditions

**For convex f**:
$$\sum_t \alpha_t = \infty, \quad \sum_t \alpha_t^2 < \infty$$

Common choice: α_t = O(1/t)

**For non-convex**:
Requires diminishing step sizes or averaging.

### 3.4 Challenges

1. **Noisy gradients**: High variance
2. **Learning rate tuning**: Critical
3. **Local minima**: May get stuck
4. **Saddle points**: Slower escape

### 3.5 Variance Reduction

**Mini-batch averaging**: Use larger batches

**Gradient averaging**:
$$\bar{\mathbf{g}}_t = \beta \bar{\mathbf{g}}_{t-1} + (1-\beta) \nabla f_i(\mathbf{x}_t)$$

---

## 4. Adaptive Learning Rates

### 4.1 Adagrad

$$G_t = \sum_{\tau=1}^t \nabla f_\tau \odot \nabla f_\tau$$
$$\mathbf{x}_{t+1} = \mathbf{x}_t - \frac{\alpha}{\sqrt{G_t} + \epsilon} \odot \nabla f_t$$

**Advantages**:
- Adapts to different feature scales
- Good for sparse data

**Disadvantage**: G_t grows unbounded → learning rate decays to zero.

### 4.2 RMSprop

$$E[g^2]_t = \beta E[g^2]_{t-1} + (1-\beta) \nabla f_t \odot \nabla f_t$$
$$\mathbf{x}_{t+1} = \mathbf{x}_t - \frac{\alpha}{\sqrt{E[g^2]_t} + \epsilon} \odot \nabla f_t$$

**Modification**: Use exponential moving average instead of cumulative sum.

### 4.3 Adam (Adaptive Moment Estimation)

**First moment (momentum)**:
$$\mathbf{m}_t = \beta_1 \mathbf{m}_{t-1} + (1-\beta_1) \nabla f_t$$

**Second moment (adaptive learning rate)**:
$$\mathbf{v}_t = \beta_2 \mathbf{v}_{t-1} + (1-\beta_2) \nabla f_t \odot \nabla f_t$$

**Bias correction**:
$$\hat{\mathbf{m}}_t = \frac{\mathbf{m}_t}{1-\beta_1^t}, \quad \hat{\mathbf{v}}_t = \frac{\mathbf{v}_t}{1-\beta_2^t}$$

**Update**:
$$\mathbf{x}_{t+1} = \mathbf{x}_t - \frac{\alpha \hat{\mathbf{m}}_t}{\sqrt{\hat{\mathbf{v}}_t} + \epsilon}$$

**Typical parameters**: β₁ = 0.9, β₂ = 0.999, α = 0.001

### 4.4 AdamW (Adam with Weight Decay)

Separates weight decay from gradient-based updates:
$$\mathbf{x}_{t+1} = \mathbf{x}_t - \alpha \left(\frac{\hat{\mathbf{m}}_t}{\sqrt{\hat{\mathbf{v}}_t} + \epsilon} + \lambda \mathbf{x}_t\right)$$

---

## 5. Momentum Methods

### 5.1 Classical Momentum

$$ \mathbf{v}_t = \beta \mathbf{v}_{t-1} + \nabla f(\mathbf{x}_t)$$
$$ \mathbf{x}_{t+1} = \mathbf{x}_t - \alpha \mathbf{v}_t$$

**Interpretation**: Accumulate velocity in consistent directions.

### 5.2 Nesterov Accelerated Gradient (NAG)

$$ \mathbf{v}_t = \beta \mathbf{v}_{t-1} + \nabla f(\mathbf{x}_t - \alpha \beta \mathbf{v}_{t-1})$$
$$ \mathbf{x}_{t+1} = \mathbf{x}_t - \alpha \mathbf{v}_t$$

**Look-ahead**: Compute gradient at predicted position.

### 5.3 Convergence

**For convex f**:
$$f(\mathbf{x}_t) - f^* \leq O\left(\frac{1}{t^2}\right)$$

Faster than vanilla gradient descent O(1/t).

### 5.4 Connection to Physics

Mimics momentum in physics:
- Velocity accumulates
- Friction coefficient β
- Gravity-like gradient force

---

## 6. Second Order Methods

### 6.1 Newton's Method

$$ \mathbf{x}_{t+1} = \mathbf{x}_t - H^{-1} \nabla f(\mathbf{x}_t)$$

where H = Hessian matrix.

**Advantages**:
- Quadratic convergence near optimum
- Adapts to curvature

**Disadvantages**:
- Expensive Hessian computation O(n²)
- O(n³) for matrix inversion
- May not be positive definite

### 6.2 Quasi-Newton Methods

Approximate Hessian instead of computing directly.

**BFGS** (Broyden-Fletcher-Goldfarb-Shanno):
- Approximate H^{-1} directly
- O(n²) per iteration
- Requires storing n×n matrix

**L-BFGS** (Limited memory):
- Store only m gradient vectors
- O(mn) per iteration, m << n
- Good for large-scale problems

### 6.3 Natural Gradient

$$ \mathbf{x}_{t+1} = \mathbf{x}_t - \alpha F^{-1} \nabla f$$

where F is Fisher information matrix.

**Interpretation**: Metric of parameter space, not Euclidean.

**Application**: Natural gradient descent in reinforcement learning.

### 6.4 Gauss-Newton and Levenberg-Marquardt

For nonlinear least squares:
$$\min \|f(\mathbf{x})\|^2$$

**Gauss-Newton**: H ≈ J^T J

**Levenberg-Marquardt**: H + λI (trust region)

---

## 7. Constrained Optimization

### 7.1 Projected Gradient Descent

$$ \mathbf{x}_{t+1} = P_{\mathcal{C}}(\mathbf{x}_t - \alpha \nabla f(\mathbf{x}_t))$$

where P_C is projection onto feasible set.

**Example**: Box constraints [l, u]

### 7.2 Penalty Methods

**Quadratic penalty**:
$$ \min f(\mathbf{x}) + \rho \sum g_i(\mathbf{x})^2$$

**Barrier methods** (for inequality):
- Log barrier: -μ log(-h(x))
- For small μ → exact solution

### 7.3 Augmented Lagrangian

$$ \mathcal{L}(\mathbf{x}, \lambda, \mu) = f(\mathbf{x}) + \lambda^T g(\mathbf{x}) + \frac{\mu}{2}\|g(\mathbf{x})\|^2$$

**ADMM** (Alternating Direction Method):
- Alternating minimization in blocks
- Good for distributed optimization

### 7.4 Frank-Wolfe Algorithm

For constrained convex optimization with linear constraints:
- No projection needed
- Iterative: x_{t+1} = (1-γ)x_t + γ*s_t
- s_t from linear minimization over constraints

---

## 8. Regularization

### 8.1 L2 Regularization (Ridge)

$$ \min f(\mathbf{x}) + \lambda \|\mathbf{x}\|_2^2$$

**Effect**: Shrinks weights toward zero, but not exactly zero.

**Gradient**: 2λx

**Solution**: (A^T A + λI)^{-1} A^T b (in least squares)

### 8.2 L1 Regularization (Lasso)

$$ \min f(\mathbf{x}) + \lambda \|\mathbf{x}\|_1$$

**Effect**: Promotes sparsity, exact zero weights.

**Subgradient**: λ sign(x) (with special handling at 0)

**Solution**: Sparse models, feature selection

### 8.3 Elastic Net

$$ \min f(\mathbf{x}) + \lambda_1 \|\mathbf{x}\|_1 + \lambda_2 \|\mathbf{x}\|_2^2$$

**Effect**: Combines benefits of L1 and L2

### 8.4 Group Sparsity

$$ \min f(\mathbf{x}) + \lambda \sum_g \|\mathbf{x}_g\|_2$$

**Effect**: Selects entire groups of features

### 8.5 Spectral Regularization

$$ \min f(\mathbf{x}) + \lambda \|\mathbf{x}\|_*$$

where ||·||_* is nuclear norm (sum of singular values).

**Effect**: Low-rank solutions

---

## 9. Advanced Algorithms

### 9.1 Coordinate Descent

Optimize one variable at a time while fixing others.

**For LASSO**: Closed-form solution for each coordinate.

### 9.2 Proximal Methods

**Proximal operator**: prox_f(x) = argmin_z (f(z) + (1/2)||x - z||²)

**Proximal gradient**:
$$ \mathbf{x}_{t+1} = \text{prox}_{\alpha g}(\mathbf{x}_t - \alpha \nabla f(\mathbf{x}_t))$$

**ISTA** (Iterative Shrinkage-Thresholding Algorithm):
For g = λ||x||₁: prox_{αg}(x) = soft_threshold(x, λα)

### 9.3 Trust Region Methods

At each iteration:
$$ \min_m \quad f(\mathbf{x}_t) + \nabla f^T \Delta + \frac{1}{2}\Delta^T B \Delta \quad \text{s.t.} \quad \|\Delta\| \leq \delta $$

where B approximates Hessian.

### 9.4 Line Search Methods

** Wolfe conditions**:
1. Sufficient decrease: f(x + αd) ≤ f(x) + c₁α∇f^T d
2. Curvature: ∇f(x + αd)^T d ≥ c₂∇f^T d

**Good step size**: Ensures progress without taking too small steps.

### 9.5 Stochastic Variance Reduction

**SVRG** (Stochastic Variance Reduced Gradient):
- Periodically compute full gradient
- Use variance-reduced stochastic gradient
- Faster convergence than SGD

**SAGA**:
- Maintain table of past gradients
- Unbiased estimate from single sample

---

## 10. Practical Considerations

### 10.1 Learning Rate Scheduling

**Step decay**:
$$\alpha_t = \alpha_0 \gamma^{\lfloor t/k \rfloor}$$

**Exponential decay**:
$$\alpha_t = \alpha_0 e^{-\gamma t}$$

**Inverse scaling**:
$$\alpha_t = \alpha_0 / (1 + \gamma t)$$

**Cosine annealing**:
$$\alpha_t = \frac{\alpha_{min}}{2} \left(1 + \cos\left(\frac{\pi t}{T}\right)\right)$$

### 10.2 Warmup

Gradually increase learning rate:
$$\alpha_t = \alpha_{base} \cdot \frac{t}{T_{warmup}}$$

**Benefits**:
- Stability in early training
- Better convergence

### 10.3 Gradient Clipping

$$ \mathbf{g} = \begin{cases} \mathbf{g} \cdot \frac{c}{\|\mathbf{g}\|} & \text{if } \|\mathbf{g}\| > c \\ \mathbf{g} & \text{otherwise} \end{cases}$$

**Prevents gradient explosion** in RNNs.

### 10.4 Early Stopping

Monitor validation loss:
- Stop when validation increases
- Prevents overfitting

### 10.5 Initialization

**Xavier/Glorot**:
$$W \sim U\left[-\sqrt{\frac{6}{n_{in} + n_{out}}}, \sqrt{\frac{6}{n_{in} + n_{out}}}\right]$$

**He initialization** (for ReLU):
$$W \sim U\left[-\sqrt{\frac{6}{n_{in}}}, \sqrt{\frac{6}{n_{in}}}\right]$$

### 10.6 Distributed Optimization

**Data parallelism**:
- Replicate model on multiple GPUs
- Average gradients

**Model parallelism**:
- Split model across GPUs
- Pipeline execution

**Parameter servers**:
- Central server holds parameters
- Workers compute gradients

---

## Summary

Key optimization concepts for ML:
1. Gradient descent variants (batch, stochastic, mini-batch)
2. Adaptive methods (Adagrad, RMSprop, Adam)
3. Momentum and acceleration
4. Second-order methods (Newton, quasi-Newton)
5. Constrained optimization (projection, penalties)
6. Regularization (L1, L2, elastic net)
7. Learning rate scheduling
8. Practical tricks (clipping, warmup, early stopping)

These enable training effective ML models.

---

## Further Reading

1. "Convex Optimization" - Boyd and Vandenberghe
2. "Optimization for Machine Learning" - Sra et al.
3. "Deep Learning" - Goodfellow et al. (Chapter on optimization)