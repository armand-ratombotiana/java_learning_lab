# Mathematical Foundations of Vectors

## 📐 Formal Definition
A column vector $\mathbf{v}$ in an $n$-dimensional Euclidean space $\mathbb{R}^n$ is represented as an $n \times 1$ matrix:

$$
\mathbf{v} = \begin{bmatrix} v_1 \\ v_2 \\ \vdots \\ v_n \end{bmatrix}
$$

## ➕ Vector Addition
Given two vectors $\mathbf{u}$ and $\mathbf{v}$ in $\mathbb{R}^n$:
$$
\mathbf{u} + \mathbf{v} = \begin{bmatrix} u_1 + v_1 \\ u_2 + v_2 \\ \vdots \\ u_n + v_n \end{bmatrix}
$$

## ✖️ Scalar Multiplication
Given a scalar $c \in \mathbb{R}$ and a vector $\mathbf{v}$:
$$
c\mathbf{v} = \begin{bmatrix} cv_1 \\ cv_2 \\ \vdots \\ cv_n \end{bmatrix}
$$

## 🎯 The Dot Product
The dot product (inner product) of two vectors $\mathbf{u}$ and $\mathbf{v}$ yields a scalar. It is defined algebraically as:
$$
\mathbf{u} \cdot \mathbf{v} = \sum_{i=1}^{n} u_i v_i = u_1v_1 + u_2v_2 + \dots + u_nv_n
$$

### Geometric Interpretation of Dot Product
The dot product is also defined geometrically as:
$$
\mathbf{u} \cdot \mathbf{v} = \|\mathbf{u}\| \|\mathbf{v}\| \cos(\theta)
$$
Where $\theta$ is the angle between the two vectors. 
- If $\mathbf{u} \cdot \mathbf{v} = 0$, the vectors are **orthogonal** (perpendicular).
- If $\mathbf{u} \cdot \mathbf{v} > 0$, the angle is acute (pointing in similar directions).
- If $\mathbf{u} \cdot \mathbf{v} < 0$, the angle is obtuse (pointing in opposite directions).

## 📏 Vector Norms (Magnitude)
The norm of a vector is a measure of its length or size.

### L2 Norm (Euclidean Norm)
The standard distance from the origin. Used heavily in Ridge Regression (L2 Regularization).
$$
\|\mathbf{v}\|_2 = \sqrt{\sum_{i=1}^{n} v_i^2}
$$

### L1 Norm (Manhattan Norm)
The sum of absolute values. Used in Lasso Regression (L1 Regularization) to induce sparsity.
$$
\|\mathbf{v}\|_1 = \sum_{i=1}^{n} |v_i|
$$