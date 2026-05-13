# Optimization for ML - Theory

## 1. Optimization Fundamentals

### Problem Formulation
```
minimize f(x)
subject to x ∈ ℝⁿ
```

### Types of Problems
- **Unconstrained**: No restrictions
- **Constrained**: g(x) ≤ 0, h(x) = 0
- **Convex**: Single global minimum
- **Non-convex**: Multiple local minima

### Optimality Conditions
- **First-order**: ∇f(x*) = 0
- **Second-order**: ∇²f(x*) ≽ 0 (for minimum)

## 2. Gradient Descent

### Basic Algorithm
```
x_{t+1} = x_t - η * ∇f(x_t)
```
where η is learning rate.

### Convergence Analysis
- **Convex**: O(1/√T) for non-smooth, O(1/T) for smooth
- **Non-convex**: No guarantees

### Learning Rate Schedules
- **Constant**: η_t = η
- **Decay**: η_t = η / (1 + decay * t)
- **Step**: η_t = η * γ^t at specific epochs

## 3. Advanced Gradient Methods

### Stochastic Gradient Descent (SGD)
```
x_{t+1} = x_t - η * ∇f(x_t, i_t)
```
Uses single sample or mini-batch.

### Momentum
```
v_{t+1} = β * v_t + (1-β) * ∇f(x_t)
x_{t+1} = x_t - η * v_{t+1}
```
Accelerates convergence, dampens oscillations.

### Nesterov Accelerated Gradient
```
v_{t+1} = β * v_t + (1-β) * ∇f(x_t - η * v_t)
x_{t+1} = x_t - η * v_{t+1}
```

### RMSprop
```
E[g²]_{t+1} = ρ * E[g²]_t + (1-ρ) * g²
x_{t+1} = x_t - η / sqrt(E[g²] + ε) * g
```

### Adam (Adaptive Moment Estimation)
```
m_t = β₁ * m_{t-1} + (1-β₁) * g
v_t = β₂ * v_{t-1} + (1-β₂) * g²
m̂ = m_t / (1-β₁^t)
v̂ = v_t / (1-β₂^t)
x_{t+1} = x_t - η * m̂ / (sqrt(v̂) + ε)
```

## 4. Regularization

### L1 (Lasso)
```
minimize ||y - Xw||² + α||w||₁
```
Sparsity, feature selection.

### L2 (Ridge)
```
minimize ||y - Xw||² + α||w||₂²
```
Shrinks weights, handles multicollinearity.

### Elastic Net
```
minimize ||y - Xw||² + α(ρ||w||₁ + (1-ρ)||w||₂²/2)
```
Combines L1 and L2.

### Dropout
- Randomly zero out neurons during training
- Reduces overfitting
- Acts as ensemble of many networks

## 5. Constrained Optimization

### Lagrange Multipliers
```
L(x, λ) = f(x) + λ * g(x)
∇L = 0
```

### KKT Conditions
- Stationarity
- Primal feasibility
- Dual feasibility
- Complementary slackness

### Penalty Methods
- Quadratic penalty: f(x) + ρ||g(x)||²
- Augmented Lagrangian

## 6. Practical Considerations

### Initialization
- Xavier/Glorot for tanh
- He for ReLU
- Small random values

### Early Stopping
- Monitor validation loss
- Stop when no improvement

### Gradient Clipping
- Clip by norm: ||g|| > threshold
- Clip by value: g > threshold