# Mock Interview: Optimization Algorithms

**Topic:** Compare optimization algorithms for ML training

## Core Questions

### Q1: Compare SGD, Momentum, Adam, and L-BFGS.

**Answer:**
| Method | Per-step cost | Memory | Convergence | Use Case |
|--------|--------------|--------|-------------|----------|
| **SGD** | $O(d)$ | $O(d)$ | Slow, noisy | Baselines, simple models |
| **Momentum** | $O(d)$ | $O(2d)$ | Faster, damped oscillations | Vision CNNs |
| **Adam** | $O(d)$ | $O(3d)$ | Fast early, adaptive | Transformers, GANs, LLMs |
| **L-BFGS** | $O(d^2)$ | $O(kd)$ | Superlinear | Small $d$, convex, deterministic |

### Q2: When would you choose each?

**Answer:**
- **SGD + Momentum:** Best generalization with proper tuning; standard for ResNets, CNNs
- **Adam:** Default choice; robust, fast convergence, good for Transformers, RL, GANs
- **AdamW:** Improved Adam with decoupled weight decay; standard for modern LLMs
- **L-BFGS:** Only for small problems where Hessian approximation helps; rare in deep learning
- **AdaGrad:** Natural for sparse features (NLP, click-through rates)
- **RMSProp:** Good for non-stationary objectives (RL, online learning)

### Q3: Explain learning rate schedules.

**Answer:**
- **Step decay:** Reduce by factor every $k$ epochs
- **Exponential decay:** $\eta_t = \eta_0 \cdot \gamma^t$
- **Cosine annealing:** $\eta_t = \eta_{\min} + \frac{1}{2}(\eta_{\max} - \eta_{\min})(1 + \cos(\pi t/T))$
- **Warmup:** Linear increase from 0 to $\eta_0$ in first few thousand steps (crucial for Transformers)
- **Cyclical:** Oscillate between bounds, can escape sharp minima

Adam typically needs less schedule tuning than SGD.

## Advanced

- **Second-order methods:** K-FAC approximates Fisher info matrix; NG (natural gradient) is invariant to parameterization
- **Sharpness-Aware Minimization (SAM):** $\min \max_{\|\epsilon\| \le \rho} L(w+\epsilon)$ finds flat minima, better generalization
- **Lookahead:** Maintains slow weights, interpolates with inner-loop SGD updates
