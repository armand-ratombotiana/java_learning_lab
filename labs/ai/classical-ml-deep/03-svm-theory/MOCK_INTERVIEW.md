# Mock Interview: SVM Theory

**Topic:** Explain SVM with kernel trick — derive dual formulation

## Core Questions

### Q1: What is the SVM objective?

**Answer:**
Hard-margin SVM finds the hyperplane $w^T x + b = 0$ that maximizes the margin $\frac{2}{\|w\|}$.

Primal problem: $\min_{w,b} \frac{1}{2}\|w\|^2$ subject to $y_i(w^T x_i + b) \ge 1$ for all $i$.

Soft-margin (allow misclassifications):
$\min_{w,b,\xi} \frac{1}{2}\|w\|^2 + C\sum_{i=1}^n \xi_i$
s.t. $y_i(w^T x_i + b) \ge 1 - \xi_i,\; \xi_i \ge 0$

$C$ trades off margin width vs. training error.

### Q2: Derive the dual formulation.

**Answer:**
Lagrangian: $\mathcal{L} = \frac{1}{2}\|w\|^2 + C\sum \xi_i - \sum \alpha_i[y_i(w^T x_i + b) - 1 + \xi_i] - \sum \mu_i \xi_i$

KKT conditions: $\frac{\partial \mathcal{L}}{\partial w} = 0 \Rightarrow w = \sum \alpha_i y_i x_i$
$\frac{\partial \mathcal{L}}{\partial b} = 0 \Rightarrow \sum \alpha_i y_i = 0$
$\frac{\partial \mathcal{L}}{\partial \xi_i} = 0 \Rightarrow \alpha_i = C - \mu_i$

Substitute back to get dual:
$\max_\alpha \sum \alpha_i - \frac{1}{2}\sum_i\sum_j \alpha_i \alpha_j y_i y_j x_i^T x_j$
s.t. $0 \le \alpha_i \le C,\; \sum \alpha_i y_i = 0$

**Key insight:** Dual depends only on inner products $x_i^T x_j$ — enables the kernel trick.

### Q3: Explain the kernel trick.

**Answer:**
Replace inner product $\langle x_i, x_j \rangle$ with kernel $K(x_i, x_j) = \phi(x_i)^T \phi(x_j)$ without computing $\phi$ explicitly.

Common kernels:
- **Linear:** $K(x, z) = x^T z$
- **Polynomial:** $K(x, z) = (x^T z + 1)^d$
- **RBF:** $K(x, z) = \exp(-\gamma\|x - z\|^2)$
- **Sigmoid:** $K(x, z) = \tanh(ax^T z + b)$

Decision function: $f(x) = \text{sign}(\sum \alpha_i y_i K(x_i, x) + b)$

Only support vectors ($\alpha_i > 0$) affect the decision boundary.

## Advanced

- **Mercer's theorem:** Conditions for valid kernel (positive semi-definite Gram matrix)
- **SMO algorithm:** Sequential minimal optimization for solving dual
- **Hinge loss vs. logistic loss:** SVM minimizes hinge loss; logistic regression uses log loss
- **Nu-SVM:** Parameterizes margin with $\nu \in [0,1]$ controlling fraction of SVs
