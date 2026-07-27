# Mock Interview: Gradient & Vector Calculus

**Topic:** Derive backpropagation using matrix calculus

## Core Questions

### Q1: Set up the notation for backpropagation.

**Answer:**
Neural network: $L$ layers

Forward pass:
- $z^{(l)} = W^{(l)} a^{(l-1)} + b^{(l)}$
- $a^{(l)} = \sigma(z^{(l)})$
- $a^{(0)} = x$ (input)
- Loss $\mathcal{L}$ (e.g., cross-entropy)

Dimensions:
- $W^{(l)} \in \mathbb{R}^{n_l \times n_{l-1}}$
- $b^{(l)} \in \mathbb{R}^{n_l}$
- $z^{(l)}, a^{(l)} \in \mathbb{R}^{n_l}$

### Q2: Derive the backpropagation equations.

**Answer:**
Define error signal: $\delta^{(l)} = \frac{\partial \mathcal{L}}{\partial z^{(l)}}$

Using chain rule:

$\delta^{(L)} = \nabla_{a^{(L)}} \mathcal{L} \odot \sigma'(z^{(L)})$

For $l = L-1$ down to $1$:

$\delta^{(l)} = ((W^{(l+1)})^T \delta^{(l+1)}) \odot \sigma'(z^{(l)})$

Gradients:
- $\frac{\partial \mathcal{L}}{\partial W^{(l)}} = \delta^{(l)} (a^{(l-1)})^T$
- $\frac{\partial \mathcal{L}}{\partial b^{(l)}} = \delta^{(l)}$

**Intuition:** Error $\delta^{(l+1)}$ propagates backward through $W^{(l+1)T}$ (transpose of forward weight), then through derivative of activation.

### Q3: Explain the chain rule for vector-valued functions.

**Answer:**
For $f: \mathbb{R}^m \to \mathbb{R}^n$ and $g: \mathbb{R}^n \to \mathbb{R}^p$:

$\frac{\partial (g \circ f)}{\partial x} = \frac{\partial g}{\partial f} \cdot \frac{\partial f}{\partial x}$

In backprop, we never form the full Jacobian — we compute vector-Jacobian products (VJPs):

$\delta^{(l)} = \underbrace{\frac{\partial \mathcal{L}}{\partial z^{(l+1)}}}_{\text{row vector}} \cdot \underbrace{\frac{\partial z^{(l+1)}}{\partial a^{(l)}}}_{\text{Jacobian}} \cdot \underbrace{\frac{\partial a^{(l)}}{\partial z^{(l)}}}_{\text{Jacobian}}$

### Q4: Matrix calculus identities used in backprop.

**Answer:**
Let $y = Wx + b$:
- $\frac{\partial y}{\partial x} = W$ (Jacobian)
- $\frac{\partial y}{\partial W} = x^T \otimes I$ (Kronecker product form)
- Practical gradient: $\frac{\partial L}{\partial W} = \frac{\partial L}{\partial y} \cdot x^T$

Element-wise activation:
- $\frac{\partial \sigma(z)}{\partial z} = \text{diag}(\sigma'(z))$
- In practice: $\delta^{(l)} = ((W^{(l+1)})^T \delta^{(l+1)}) \odot \sigma'(z^{(l)})$ (Hadamard product avoids full diag)

### Q5: Common pitfalls and numerical considerations.

**Answer:**
- **Vanishing gradients:** Sigmoid/tanh saturate — $\sigma'(z) \to 0$. ReLU helps but can die.
- **Exploding gradients:** Deep networks accumulate large errors — gradient clipping.
- **Weight initialization matters:** He/Xavier init balances gradient variance across layers.
- **Batch normalization:** Controls first two moments of $z^{(l)}$, stabilizes gradient flow.

## Advanced

- **Automatic differentiation:** $\nabla_x f = (\text{sum of all paths from } f \to x)$ — chain rule sums over all paths
- **Hessian computation:** $\frac{\partial^2 L}{\partial w^2} = \sum \delta^{(l)} \cdot (\ldots)$ — used for curvature-aware optimization
- **Jacobian of softmax:** $\frac{\partial \text{softmax}(z)_i}{\partial z_j} = p_i(\delta_{ij} - p_j)$
