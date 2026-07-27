# Mock Interview: Optimization Fundamentals

**Topic:** Explain convex optimization, Lagrange multipliers, KKT conditions

## Core Questions

### Q1: What makes an optimization problem convex?

**Answer:**
A problem $\min_x f(x)$ s.t. $g_i(x) \le 0$, $h_j(x) = 0$ is convex if:

1. **Objective $f(x)$** is convex: $f(\lambda x + (1-\lambda)y) \le \lambda f(x) + (1-\lambda) f(y)$
2. **Inequality constraints $g_i(x)$** are convex functions
3. **Equality constraints $h_j(x)$** are affine: $h_j(x) = a_j^T x + b_j$
4. **Domain** is a convex set

**Why convex?** Every local minimum is global. First-order condition: $\nabla f(x^*)^T (y - x^*) \ge 0$ for all feasible $y$.

**Examples in ML:**
- Convex: Linear/logistic regression, SVMs (without kernels), Lasso, Ridge
- Non-convex: Neural networks, deep learning, mixture models, matrix factorization with non-convex constraints

### Q2: Derive Lagrange multipliers for equality constraints.

**Answer:**
Problem: $\min f(x)$ s.t. $h(x) = 0$

**Lagrangian:** $\mathcal{L}(x, \nu) = f(x) + \nu^T h(x)$

**Necessary condition (first-order):** $\nabla \mathcal{L} = 0$

$\nabla_x \mathcal{L} = \nabla f(x^*) + \nu^T \nabla h(x^*) = 0$
$\nabla_\nu \mathcal{L} = h(x^*) = 0$

**Intuition:** At optimum, gradient of objective must be parallel to gradient of constraints (can't improve by moving along constraint surface).

### Q3: Derive KKT conditions for inequality constraints.

**Answer:**
Problem: $\min f(x)$ s.t. $g_i(x) \le 0$, $h_j(x) = 0$

Define Lagrangian: $\mathcal{L}(x, \mu, \nu) = f(x) + \sum \mu_i g_i(x) + \sum \nu_j h_j(x)$

**KKT conditions (necessary for optimality under constraint qualifications):**

1. **Stationarity:** $0 \in \partial f(x^*) + \sum \mu_i \partial g_i(x^*) + \sum \nu_j \nabla h_j(x^*)$
2. **Primal feasibility:** $g_i(x^*) \le 0$, $h_j(x^*) = 0$
3. **Dual feasibility:** $\mu_i \ge 0$
4. **Complementary slackness:** $\mu_i g_i(x^*) = 0$ (if $g_i(x^*) < 0$ then $\mu_i = 0$)

**Interpretation:** Active constraints ($g_i(x^*) = 0$) have $\mu_i \ge 0$ cost. Inactive constraints ($g_i(x^*) < 0$) can be ignored ($\mu_i = 0$).

### Q4: Applications in ML.

**Answer:**
1. **SVM dual:** Lagrange multipliers $\alpha_i$ become support vector weights. KKT conditions identify SVs ($\alpha_i > 0$).
2. **Lasso:** $\min \|y - Xw\|^2$ s.t. $\|w\|_1 \le t$. KKT gives subgradient condition: $2X^T (y - Xw) \in \lambda \partial \|w\|_1$
3. **Regularization as constraint:** Ridge = $\min \|y - Xw\|^2$ s.t. $\|w\|_2^2 \le t$ (equivalence via Lagrangian)
4. **Optimal transport:** KKT yields dual variables for Earth Mover's Distance

### Q5: Strong duality and Slater's condition.

**Answer:**
**Dual problem:** $g(\mu, \nu) = \min_x \mathcal{L}(x, \mu, \nu)$ provides lower bound: $g(\mu, \nu) \le f(x^*)$

**Strong duality:** $g(\mu^*, \nu^*) = f(x^*)$ (gap = 0)

**Slater's condition:** If there exists a strictly feasible point where $g_i(x) < 0$ for all non-linear $g_i$, then strong duality holds for convex problems.

**When weak duality happens:** Non-convex problems may have duality gap $> 0$.

## Advanced

- **Subgradients:** $\partial f(x) = \{g \mid f(y) \ge f(x) + g^T (y-x) \text{ for all } y\}$. For non-differentiable convex functions (e.g., L1).
- **Proximal operators:** $\text{prox}_f(v) = \arg\min_x (f(x) + \frac{1}{2}\|x - v\|^2)$. Key for L1 optimization.
- **ADMM:** Alternating Direction Method of Multipliers — combines dual ascent with method of multipliers for decomposable problems.
