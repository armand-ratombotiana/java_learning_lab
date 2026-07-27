# Mock Interview: Numerical Computing

**Topic:** Numerical stability in ML — log-sum-exp trick, softmax, underflow/overflow

## Core Questions

### Q1: What is the log-sum-exp trick and why is it needed?

**Answer:**
**Problem:** Computing $\log(\sum e^{x_i})$ directly causes overflow/underflow.

Example: $x = [1000, 1001, 1002]$ → $e^{1000}$ overflows double precision ($\approx 10^{434}$ vs max $10^{308}$).

**Trick:** $\log(\sum e^{x_i}) = c + \log(\sum e^{x_i - c})$ where $c = \max(x_i)$

With $c = 1002$: $\log(e^{-2} + e^{-1} + 1) + 1002 \approx \log(1.367) + 1002 \approx 0.313 + 1002$

**Why it works:** After subtracting max, the largest exponent is 0 (so $e^0 = 1$). All others are $\le 0$, safe from overflow.

### Q2: How to compute softmax safely?

**Answer:**
$\text{softmax}(x_i) = \frac{e^{x_i}}{\sum_j e^{x_j}}$

**Numerically stable version:**
```
def softmax(x):
    c = np.max(x)
    exp_x = np.exp(x - c)      # max exp is 1
    return exp_x / np.sum(exp_x)
```

**For log-softmax (used in cross-entropy loss):**
```
def log_softmax(x):
    c = np.max(x)
    return (x - c) - np.log(np.sum(np.exp(x - c)))
```

**Why log-softmax + NLLLoss is better:** Avoids computing both exp and log separately. $-\log(\text{softmax}(x)_i) = -(x_i - \max) + \log(\sum e^{x_j - \max})$.

### Q3: Common numerical issues in ML.

**Answer:**
| Issue | Example | Fix |
|-------|---------|-----|
| **Overflow** | $e^{1000}$ | Shift via max subtraction |
| **Underflow** | $e^{-1000} \approx 0$ | Work in log space |
| **NaN gradients** | Loss → NaN | Gradient clipping, lower LR |
| **Division by zero** | $x / \text{std}$ when std=0 | Add $\epsilon$ (1e-8) |
| **Log of zero** | $\log(0)$ | Add $\epsilon$, use log1p |
| **Catastrophic cancellation** | $1 - \text{small\_value}$ close to 1 | Use log1p, expm1 |
| **Ill-conditioned matrices** | $(X^T X)^{-1}$ | Add $\lambda I$, use SVD pseudoinverse |

### Q4: Explain the softmax + cross-entropy gradient cancellation.

**Answer:**
Combined gradient simplifies nicely:

$\frac{\partial}{\partial z_k} \left( -\log\frac{e^{z_y}}{\sum e^{z_j}} \right) = \frac{\partial}{\partial z_k} \left( -z_y + \log\sum e^{z_j} \right)$

$= -\mathbb{1}[k = y] + \frac{e^{z_k}}{\sum e^{z_j}}$

$= \text{softmax}(z)_k - \mathbb{1}[k = y]$

This is exactly $\hat{y} - y$ in vector form! Numerically, this avoids computing $\log(\text{softmax})$ directly.

### Q5: Practical numerical tips for ML engineering.

**Answer:**
- **Use `logsumexp`** from `scipy.special` or `torch.logsumexp`
- **Use `log_softmax`** + `NLLLoss` instead of `softmax` + `CrossEntropy`
- **Gradient clipping:** Clip norm (global) or value (per parameter) to range $[-c, c]$
- **Weight initialization:** Xavier/He init prevents exponential blow-up in forward/backward pass
- **Batch normalization:** Keeps activations in stable range
- **Mixed precision (FP16):** Need loss scaling to avoid underflow of small gradients
- **Use `double` for sensitive computations** (e.g., log determinants, matrix inverses)
- **Check for NaNs:** Periodically inspect weights/gradients during training

## Advanced

- **Condition number:** $\kappa(A) = \|A\|\cdot\|A^{-1}\|$ — measures sensitivity to perturbations. Large $\kappa$ means numerical instability.
- **Kahan summation:** Compensated summation reduces floating point errors when adding many numbers.
- **Stable sigmoid:** $\sigma(x) = \begin{cases} \frac{e^x}{1+e^x} & x \ge 0 \\ \frac{1}{1+e^{-x}} & x < 0 \end{cases}$ avoids overflow for large negative $x$.
- **Log1p and expm1:** $\log(1+x)$ and $e^x - 1$ computed with high precision for small $x$.
