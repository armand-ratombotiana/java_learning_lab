# Optimization - Flashcards

## Quick Review Cards

### Card 1: Gradient Descent Formula
**Q:** What is the update rule for basic gradient descent?
**A:** `x_{t+1} = x_t - α * ∇f(x_t)` where α is the learning rate.

---

### Card 2: Learning Rate Too High
**Q:** What happens when learning rate exceeds the optimum?
**A:** The optimizer overshoots the minimum, potentially diverging or oscillating indefinitely.

---

### Card 3: Adam Components
**Q:** What two components does Adam combine?
**A:** Momentum (first moment estimate) and adaptive learning rates (second moment estimate).

---

### Card 4: Momentum Update
**Q:** How does momentum accelerate optimization?
**A:** It accumulates velocity in consistent gradient directions: `v_t = βv_{t-1} + ∇f(x_t)`

---

### Card 5: L1 vs L2 Regularization
**Q:** What is the key difference between L1 and L2 regularization?
**A:** L1 promotes sparsity (exact zeros), L2 shrinks weights toward zero but rarely to exactly zero.

---

### Card 6: Bias Correction in Adam
**Q:** Why does Adam use bias correction?
**A:** Both m and v are initialized as zero, causing initial bias; correction divides by (1 - β^t).

---

### Card 7: Gradient Clipping
**Q:** When is gradient clipping necessary?
**A:** In deep networks (RNNs) where gradients can grow exponentially, causing numerical overflow.

---

### Card 8: Learning Rate Schedules
**Q:** What is cosine annealing?
**A:** A schedule that reduces learning rate following a cosine curve: α_t = (α_max/2)(1 + cos(π*t/T))

---

### Card 9: Convergence Rate
**Q:** What is the convergence rate of gradient descent on convex functions?
**A:** O(1/t) - sublinear, meaning slower as you approach the optimum.

---

### Card 10: Newton's Method
**Q:** What makes Newton's method converge quadratically?
**A:** It uses second-order curvature information: x_{t+1} = x_t - H^{-1}∇f(x_t)

---

### Card 11: Stochastic Gradient Descent
**Q:** Why is SGD preferred for large datasets?
**A:** It computes gradient on a single sample instead of entire dataset, making each iteration O(1) instead of O(n).

---

### Card 12: Adagrad Limitation
**Q:** What is the main limitation of Adagrad?
**A:** The accumulated sum of squared gradients grows unbounded, causing learning rate to decay toward zero.