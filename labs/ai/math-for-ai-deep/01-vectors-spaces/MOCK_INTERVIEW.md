# Mock Interview: Vector Spaces

**Topic:** Explain linear independence, span, basis — with ML applications

## Core Questions

### Q1: Define linear independence, span, and basis.

**Answer:**
**Linear independence:** Vectors $v_1, \ldots, v_k$ are linearly independent if $c_1 v_1 + \cdots + c_k v_k = 0$ implies $c_1 = \cdots = c_k = 0$. No vector can be written as a linear combination of the others.

**Span:** The set of all linear combinations of a set of vectors. $\text{span}\{v_1, \ldots, v_k\} = \{\sum c_i v_i \mid c_i \in \mathbb{R}\}$.

**Basis:** A set of linearly independent vectors that span the space. Every vector in the space has a unique representation in that basis.

**Dimension:** The number of vectors in any basis of the space.

### Q2: Applications in ML.

**Answer:**
1. **Feature representations:** Each feature vector $x \in \mathbb{R}^d$ lives in a $d$-dimensional vector space. The basis determines how we represent data. The canonical basis $\{e_1, \ldots, e_d\}$ is just one choice.

2. **PCA:** Finds an orthogonal basis aligned with directions of maximum variance. Transforms data to this new basis, then can discard low-variance dimensions.

3. **Word embeddings:** $v_{\text{king}} - v_{\text{man}} + v_{\text{woman}} \approx v_{\text{queen}}$ — vectors span a semantic space. Linear relationships capture analogies.

4. **Kernel methods:** Map data to higher-dimensional (possibly infinite) feature space via $\phi(x)$. The Representer Theorem shows optimal solutions lie in the span of training points.

5. **Neural network layers:** Each layer computes a linear transformation $Wx + b$. The rank of weight matrices determines the effective dimensionality of the feature space.

### Q3: Linear dependence in ML — why does it matter?

**Answer:**
- **Collinearity in regression:** Linearly dependent features make $(X^T X)$ singular, OLS breaks down. Regularization (Ridge) adds $\lambda I$ to fix invertibility.
- **Rank deficiency:** If data lies in a lower-dimensional subspace, many models degenerate.
- **Overparameterization:** Modern NNs have more parameters than data points — the model space is high-dim but the effective rank of the Hessian is low.

### Q4: What is the difference between linear and affine spaces?

**Answer:**
- **Linear subspace:** Contains origin, closed under linear combinations.
- **Affine subspace:** Translation of a linear subspace $v + S = \{v + s \mid s \in S\}$. Does not need to contain origin.

In ML: Decision boundary of a linear classifier is an affine set $\{x \mid w^T x + b = 0\}$.

## Advanced

- **Dual space:** Space of linear functionals. In kernel methods, $\phi(x)$ maps $x$ to a function $K(x, \cdot)$ in RKHS.
- **Orthogonal complement:** $S^\perp = \{u \mid u^T v = 0, \forall v \in S\}$. Used in projections.
- **Four fundamental subspaces:** Column space, nullspace, row space, left nullspace — key for understanding linear systems, SVD, and PCA.
