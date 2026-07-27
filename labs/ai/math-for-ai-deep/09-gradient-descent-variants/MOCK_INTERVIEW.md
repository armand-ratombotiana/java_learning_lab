# Mock Interview: Gradient Descent Variants

**Topic:** Compare all gradient descent methods — convergence analysis

## Core Questions

### Q1: Compare Batch, Stochastic, and Mini-batch GD.

**Answer:**
| Variant | Update | Per-iteration Cost | Convergence |
|---------|--------|-------------------|-------------|
| **Batch GD** | $w_{t+1} = w_t - \eta \nabla L(w_t)$ | $O(n)$ — full dataset | Linear, monotonic |
| **SGD** | $w_{t+1} = w_t - \eta \nabla L_i(w_t)$ | $O(1)$ — one sample | Sublinear, noisy |
| **Mini-batch** | $w_{t+1} = w_t - \eta \frac{1}{b} \sum \nabla L_i(w_t)$ | $O(b)$ — batch | Smooth trade-off |

**Trade-offs:**
- Batch GD: Accurate gradient, slow per iteration, can get stuck in sharp minima
- SGD: Fast per iteration, noise helps escape local minima, but variance slows convergence
- Mini-batch: Best compromise — can parallelize (GPU), variance reduced, converges fastest in wall-clock time

### Q2: Compare optimizers.

**Answer:**
| Method | Update Rule | Key Feature |
|--------|------------|-------------|
| **SGD** | $w_{t+1} = w_t - \eta g_t$ | Base method |
| **Momentum** | $v_{t+1} = \beta v_t + g_t$, $w_{t+1} = w_t - \eta v_{t+1}$ | Accelerates along consistent directions, damps oscillations |
| **NAG** | $v_{t+1} = \beta v_t + \nabla L(w_t - \beta v_t)$, $w_{t+1} = w_t - \eta v_{t+1}$ | "Look ahead" correction, more stable |
| **AdaGrad** | $G_{t+1} = G_t + g_t^2$, $w_{t+1} = w_t - \frac{\eta}{\sqrt{G_{t+1}+\epsilon}} g_t$ | Adaptive per-parameter learning rates |
| **RMSProp** | $v_{t+1} = \beta v_t + (1-\beta)g_t^2$, $w_{t+1} = w_t - \frac{\eta}{\sqrt{v_{t+1}+\epsilon}} g_t$ | Fixes AdaGrad's aggressive decay |
| **Adam** | $m_t = \beta_1 m_{t-1} + (1-\beta_1)g_t$, $v_t = \beta_2 v_{t-1} + (1-\beta_2)g_t^2$, $\hat{m}_t$, $\hat{v}_t$ bias-corrected | Momentum + adaptive LR. Default in deep learning. |
| **AdamW** | Adam + decoupled weight decay | Fixes L2 regularization interaction, better generalization |

### Q3: Analyzing Adam update.

**Answer:**
```
m = beta1 * m + (1 - beta1) * g        # First moment (mean)
v = beta2 * v + (1 - beta2) * g**2     # Second moment (uncentered variance)
m_hat = m / (1 - beta1**t)             # Bias correction
v_hat = v / (1 - beta2**t)
w -= lr * m_hat / (sqrt(v_hat) + eps)
```

**Interpretation:**
- $m$ smooths gradient (momentum-like)
- $v$ adapts step size per parameter (RMSProp-like)
- $\sqrt{v}$ normalizes by gradient magnitude
- Bias correction handles initialization at $t=0$
- Effective step size: $\eta / (\sqrt{v} + \epsilon)$

### Q4: Convergence analysis.

**Answer:**
For convex $L$ with Lipschitz gradient $\| \nabla L(x) - \nabla L(y) \| \le L\|x-y\|$:

| Method | Convergence (convex) | Convergence (strongly convex) |
|--------|---------------------|------------------------------|
| **Batch GD** | $O(1/t)$ | $O(\exp(-t/\kappa))$ linear |
| **SGD** | $O(1/\sqrt{t})$ | $O(1/t)$ |
| **Momentum** | $O(1/t^2)$ | $O(\exp(-t/\sqrt{\kappa}))$ |
| **AdaGrad** | $O(1/\sqrt{t})$ | $O(1/t)$ |
| **Adam** | $O(1/\sqrt{t})$ | — (non-convergent in some cases) |

**For non-convex (deep learning):**
- All methods reach $\epsilon$-stationary point ($\|\nabla L\| \le \epsilon$) in $O(1/\epsilon^4)$ iterations for SGD
- Adam often converges faster in practice despite weaker theoretical guarantees

### Q5: Practical recommendations.

**Answer:**
- **SGD + Momentum:** If you can tune well, often generalizes best
- **Adam:** Default for transformers, GANs, RL — robust across hyperparameters
- **AdamW:** Prefer over Adam for vision transformers, LLMs
- **Cosine LR schedule / warmup:** Standard for large models
- **Gradient clipping:** Essential for RNNs, transformers
- **Second-order (K-FAC, L-BFGS):** Still rare in deep learning (expensive $O(d^2)$ memory)

## Advanced

- **Polyak-Ruppert averaging:** $\bar{w}_t = \frac{1}{t} \sum w_i$ improves SGD convergence rate
- **Lookahead:** Maintains slow weights, interpolates with fast SGD updates
- **Sharp/SGD:** Finds flatter minima than Adam (generalization benefit)
- **SAM (Sharpness-Aware Minimization):** Minimizes worst-case loss in neighborhood, finds flat minima
