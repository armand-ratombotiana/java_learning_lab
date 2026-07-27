# Neural Networks Interview Guide — AI Academy

> Comprehensive interview preparation for neural networks: theory, mathematics, implementation, and system design.

---

# 1. Perceptron & MLP

## 1.1 The Perceptron Algorithm

The perceptron is the simplest form of a neural network: a single linear binary classifier.

**Mathematical formulation:**

Given input vector `x ∈ ℝⁿ`, weights `w ∈ ℝⁿ`, bias `b ∈ ℝ`, the perceptron computes:

```
z = w·x + b = Σᵢ wᵢ xᵢ + b
ŷ = sign(z) = { +1 if z ≥ 0, -1 otherwise }
```

**Learning algorithm (Rosenblatt, 1958):**

1. Initialize `w = 0`, `b = 0`
2. For each misclassified sample `(xᵢ, yᵢ)`:
   - `w ← w + η · yᵢ · xᵢ`
   - `b ← b + η · yᵢ`
3. Repeat until convergence or max epochs

Where `η` is the learning rate (typically 1). Only misclassified examples trigger updates.

**Perceptron Convergence Theorem (Novikoff, 1962):**

If the training data is linearly separable, the perceptron algorithm converges in a finite number of updates. The bound on the number of mistakes is `(R/γ)²` where:
- `R` = radius of the smallest ball containing the data
- `γ` = margin (minimum distance from any point to the separating hyperplane)

**Limitations — XOR Problem (Minsky & Papert, 1969):**

The perceptron cannot learn the XOR function because it is not linearly separable. A single hyperplane cannot separate the four points of XOR:

```
0 XOR 0 = 0    0 XOR 1 = 1
1 XOR 0 = 1    1 XOR 1 = 0
```

This limitation was a major factor in the first AI winter. The solution requires a hidden layer — leading to the MLP.

## 1.2 Multi-Layer Perceptron (MLP)

An MLP adds one or more hidden layers between input and output, with non-linear activation functions.

**Architecture:**

```
Input → [Linear + Activation] → Hidden₁ → [Linear + Activation] → ... → Hiddenₖ → [Linear] → Output
```

Each layer computes:

```
h⁽ˡ⁾ = σ(W⁽ˡ⁾ · h⁽ˡ⁻¹⁾ + b⁽ˡ⁾)
```

Where `σ` is a non-linear activation function.

**Universal Approximation Theorem (Cybenko, 1989; Hornik, 1991):**

A feedforward network with a single hidden layer containing a finite number of neurons can approximate any continuous function on a compact subset of ℝⁿ, given a non-constant, bounded, and continuous activation function (e.g., sigmoid).

Key points:
- Width, not depth, is sufficient for approximation
- The theorem does NOT guarantee learnability (optimization may fail)
- It does not provide bounds on the number of neurons required
- Deeper networks can be exponentially more parameter-efficient

**Width vs Depth:**

| Aspect | Wide (shallow) | Deep |
|--------|---------------|------|
| Parameters | May require exponentially more neurons | More parameter-efficient |
| Expressivity | Universal approximator in theory | Requires fewer total units |
| Optimization | Easier to train | Prone to vanishing gradients |
| Generalization | Can overfit more easily | Often generalizes better |
| Modern practice | Rarely used alone | Preferred architecture |

Key insight: Depth creates hierarchical feature representations. Early layers learn simple features (edges), deeper layers learn complex abstractions (objects).

## 1.3 Forward Pass — Matrix Multiplication

For a batch of `m` samples, the forward pass is computed as matrix operations:

```
X: (m, d₁) — input batch
W⁽¹⁾: (d₁, d₂) — weight matrix for layer 1
b⁽¹⁾: (d₂,) — bias for layer 1
Z⁽¹⁾ = X · W⁽¹⁾ + b⁽¹⁾  — (m, d₂)
A⁽¹⁾ = σ(Z⁽¹⁾)            — (m, d₂)

W⁽²⁾: (d₂, d₃) — weight matrix for layer 2
Z⁽²⁾ = A⁽¹⁾ · W⁽²⁾ + b⁽²⁾  — (m, d₃)
A⁽²⁾ = σ(Z⁽²⁾)            — (m, d₃)

Continue until output layer:
ŷ = A⁽ᴸ⁾ = σ(Z⁽ᴸ⁾)        — (m, d_out)
```

Vectorization via matrix multiplication enables GPU acceleration.

## 1.4 Backpropagation — Chain Rule & Gradient Computation

Backpropagation computes gradients of the loss `L` with respect to all parameters using the chain rule.

**Chain rule application:**

For a single path `x → z → L`:

```
∂L/∂x = (∂L/∂z) · (∂z/∂x)
```

**Three-step process per layer:**

1. **Compute output error:** `δ⁽ᴸ⁾ = ∂L/∂Z⁽ᴸ⁾ = ∂L/∂ŷ ⊙ σ'(Z⁽ᴸ⁾)` (output layer)
2. **Backpropagate error:** `δ⁽ˡ⁾ = (δ⁽ˡ⁺¹⁾ · W⁽ˡ⁺¹⁾ᵀ) ⊙ σ'(Z⁽ˡ⁾)` (hidden layer)
3. **Compute gradients:**
   - `∂L/∂W⁽ˡ⁾ = A⁽ˡ⁻¹⁾ᵀ · δ⁽ˡ⁾`
   - `∂L/∂b⁽ˡ⁾ = Σ δ⁽ˡ⁾` (sum over batch)

**Computational graph:**

A directed acyclic graph where:
- Nodes = tensors (activations, weights, biases)
- Edges = operations (matmul, addition, activation)
- Forward pass: compute nodes in topological order
- Backward pass: apply chain rule in reverse topological order

Automatic differentiation (autodiff) frameworks (PyTorch, TensorFlow) build this graph dynamically or statically.

## 1.5 Activation Functions

### Sigmoid

```
σ(x) = 1 / (1 + e⁻ˣ)
σ'(x) = σ(x) · (1 - σ(x))
```

- Range: (0, 1) — ideal for binary classification output
- Saturation: gradients near 0 for `|x| > 5` — vanishing gradient
- Not zero-centered — causes zigzagging in optimization
- Output not zero-centered — can cause biased gradients
- Use: output layer for binary classification (with BCE loss)

### Tanh

```
tanh(x) = (eˣ - e⁻ˣ) / (eˣ + e⁻ˣ)
tanh'(x) = 1 - tanh²(x)
```

- Range: (-1, 1) — zero-centered
- Still saturates — vanishing gradient in deep networks
- Stronger gradients than sigmoid near zero
- Use: hidden layers in RNNs/LSTMs, older architectures

### ReLU (Rectified Linear Unit)

```
ReLU(x) = max(0, x)
ReLU'(x) = { 1 if x > 0, 0 if x ≤ 0 }
```

- Range: [0, ∞) — non-saturating for positive values
- Solves vanishing gradient for positive activations
- Computationally cheap
- Dying ReLU: if weights push neuron to always output ≤ 0, gradient is 0 forever
- Not zero-centered
- Use: default for hidden layers in CNNs and MLPs

### Leaky ReLU

```
LeakyReLU(x) = { x if x > 0, αx if x ≤ 0 }  where α = 0.01 typically
```

- Addresses dying ReLU by allowing small negative gradient
- `α` is a fixed hyperparameter
- Use: when dying ReLU is observed

### PReLU (Parametric ReLU)

```
PReLU(x) = { x if x > 0, αx if x ≤ 0 }
```

- Same as Leaky ReLU but `α` is learned during training
- Can adapt per channel or globally
- Use: when adaptive negative slope is beneficial

### ELU (Exponential Linear Unit)

```
ELU(x) = { x if x > 0, α(eˣ - 1) if x ≤ 0 }
```

- Smooth for negative values — better gradient flow
- Mean activations closer to zero — faster convergence
- More computationally expensive than ReLU
- Use: when mean-shift toward zero helps convergence

### Swish (SiLU — Sigmoid Linear Unit)

```
Swish(x) = x · σ(x) = x / (1 + e⁻ˣ)
```

- Discovered via automated search (Ramachandran et al., 2017)
- Smooth, non-monotonic, self-gated
- Empirically matches or outperforms ReLU in deep networks
- Use: modern architectures, EfficientNet, Transformer variants

### GELU (Gaussian Error Linear Unit)

```
GELU(x) = x · Φ(x)  where Φ is the CDF of standard normal
GELU(x) ≈ 0.5x · [1 + tanh(√(2/π) · (x + 0.044715x³))]
```

- Used in BERT, GPT, ViT, and most modern transformers
- Smooth approximation of ReLU with probabilistic gating
- Similar to Swish but with different theoretical motivation
- Use: default for transformer-based architectures

## 1.6 Vanishing / Exploding Gradients

**Causes:**

- Vanishing: gradients shrink exponentially with depth due to saturating activations (sigmoid, tanh) or repeated multiplication of small weights
- Exploding: gradients grow exponentially due to large weight initialization or unstable dynamics

**Mathematically:** For an `L`-layer network, gradient at layer 1 involves product of `L` Jacobians:

```
∂L/∂W⁽¹⁾ = δ⁽ᴸ⁾ · W⁽ᴸ⁾ · σ'(Z⁽ᴸ⁻¹⁾) · ... · W⁽²⁾ · σ'(Z⁽¹⁾)
```

If each term has spectral norm `> 1`, gradient explodes; if `< 1`, it vanishes.

**Solutions:**

| Solution | Addresses | Mechanism |
|----------|-----------|-----------|
| ReLU/GELU | Vanishing | Non-saturating activations |
| Batch Normalization | Both | Stabilizes layer input distribution |
| Residual connections | Vanishing | Gradient highway (identity path) |
| Proper initialization | Both | Keeps variance constant across layers |
| Gradient clipping | Exploding | Caps gradient norm |
| LSTM gates | Vanishing | Gated gradient flow |
| Layer-wise pretraining | Vanishing | Better starting point |

## 1.7 Weight Initialization

### Xavier/Glorot Initialization

For activation functions with symmetry around zero (tanh, sigmoid):

```
W ~ Uniform(-√(6/(n_in + n_out)), √(6/(n_in + n_out)))
— or —
W ~ N(0, √(2/(n_in + n_out)))
```

**Goal:** `Var(z⁽ˡ⁾) = Var(z⁽ˡ⁻¹⁾)` and `Var(∂L/∂z⁽ˡ⁾) = Var(∂L/∂z⁽ˡ⁺¹⁾)`

**Derivation:** Under the assumption of linear activations and i.i.d. inputs/weights, the variance of the forward propagation signal is preserved when:

```
Var(W) = 2 / (n_in + n_out)
```

### He Initialization

For ReLU and variants:

```
W ~ N(0, √(2/n_in))
W ~ Uniform(-√(6/n_in), √(6/n_in))
```

**Why:** ReLU zeroes half the activations, halving the variance. He initialization compensates by doubling the variance compared to Xavier.

**Derivation:** With ReLU activation, `E[ReLU(x)²] = E[x²]/2`, so to preserve variance:

```
Var(W) = 2 / n_in
```

### LeCun Initialization

For sigmoid/tanh in fully connected networks:

```
W ~ N(0, √(1/n_in))
```

**Use:** Original LeNet and older fully-connected architectures.

## 1.8 Interview Questions — Perceptron & MLP

1. **Q:** Prove the perceptron convergence theorem.
   **A:** Let `w*` be the optimal weight vector with `||w*|| = 1` and margin `γ > 0`. Each update `w ← w + ηyx` increases `w · w*` by at least `ηγ`. The norm `||w||²` grows at most `η²R²` per update. After `k` updates, Cauchy-Schwarz gives `k ≤ (R/γ)²`.

2. **Q:** Why can't a single perceptron learn XOR? What is the minimal architecture that can?
   **A:** XOR is not linearly separable. A 2-layer MLP with 2 hidden neurons (or a 2-2-1 network) can solve it by learning hidden representations that make the problem linearly separable.

3. **Q:** Derive backpropagation for a 2-layer network with ReLU activation.
   **A:** [Walk through forward pass, compute loss, apply chain rule backward, derive gradients for W¹, W², b¹, b².]

4. **Q:** Compare ReLU vs GELU vs Swish. When would you choose each?
   **A:** ReLU for speed and simplicity; GELU for transformers (BERT, GPT); Swish for deep CNNs (EfficientNet). Consider dying ReLU risk with very deep networks.

5. **Q:** What happens if you initialize all weights to zero?
   **A:** All neurons in a layer compute the same output, gradients are identical, and the network cannot break symmetry — effectively collapsing to a single neuron per layer.

6. **Q:** Explain the bias-variance tradeoff in the context of MLP width.
   **A:** Wider networks have more capacity (lower bias) but risk overfitting (higher variance). Depth adds hierarchical abstraction with less parameter cost.

7. **Q:** How does the universal approximation theorem apply to modern deep learning?
   **A:** It guarantees existence of a network that approximates any function, but doesn't guarantee we can learn it via gradient descent. Modern depth helps optimization and generalization.

---

# 2. Regularization

## 2.1 L1 / L2 Regularization

**Mathematical formulation:**

Regularized loss function:

```
L_reg(w) = L_data(w) + λ · Ω(w)
```

**L2 (Ridge / Weight Decay):**

```
Ω(w) = (1/2) · ||w||₂² = (1/2) · Σⱼ wⱼ²
```

Gradient update becomes:

```
w ← w - η · (∂L_data/∂w + λw) = (1 - ηλ)w - η · ∂L_data/∂w
```

Effect: L2 shrinks weights proportionally to their magnitude (all weights decay toward zero but none reaches exactly zero).

**L1 (Lasso):**

```
Ω(w) = ||w||₁ = Σⱼ |wⱼ|
```

Gradient update:

```
wⱼ ← wⱼ - η · (∂L_data/∂wⱼ + λ · sign(wⱼ))
```

Effect: L1 drives weights exactly to zero (sparsity). The subgradient of |w| at 0 is [-1, 1], encouraging zero weights.

**Why L1 induces sparsity:**
The non-differentiability at zero creates a "dead zone" where the regularized gradient pulls weights to exactly zero. L2's gradient is linear in w, so near-zero weights have vanishingly small pull toward zero.

**Elastic Net:** Combines L1 + L2:

```
Ω(w) = λ₁||w||₁ + λ₂||w||₂²
```

## 2.2 Dropout

**Standard dropout (Srivastava et al., 2014):**

During training, randomly drop neurons with probability `p`:

```
rⱼ ~ Bernoulli(1 - p)    — mask
h'ⱼ = rⱼ · hⱼ            — apply mask
```

**Inverted dropout (commonly used):**

Scale activations at training time to maintain expected value at inference:

```
h'ⱼ = (rⱼ · hⱼ) / (1 - p)
```

At inference: no dropout, no scaling — the network is used as-is.

**Why it works as ensemble:**
- Dropout trains an exponential number of sub-networks (2ⁿ for n neurons)
- At inference, the full network approximates the geometric mean of ensemble predictions
- Prevents co-adaptation: neurons cannot rely on specific other neurons
- Similar to data augmentation in feature space

**Effect on gradients:**
Dropout adds noise to gradients, acting as a Bayesian approximation (Gal & Ghahramani, 2015). Monte Carlo Dropout uses dropout at inference with multiple forward passes to estimate uncertainty.

## 2.3 Batch Normalization

**Algorithm (Ioffe & Szegedy, 2015):**

For a mini-batch `B = {x₁, ..., xₘ}` with `m` samples:

```
μ_B = (1/m) · Σᵢ xᵢ            — batch mean
σ²_B = (1/m) · Σᵢ (xᵢ - μ_B)²  — batch variance
ẋᵢ = (xᵢ - μ_B) / √(σ²_B + ε)  — normalize
yᵢ = γ · ẋᵢ + β                 — scale and shift
```

`γ` and `β` are learnable parameters that restore representational power.

**Training vs Inference:**

| Phase | Mean & Variance | γ, β |
|-------|----------------|------|
| Training | Batch statistics | Learned via gradient |
| Inference | Running average of training statistics | Frozen |

During inference, use the running mean/variance accumulated during training:

```
y = γ · (x - μ_running) / √(σ²_running + ε) + β
```

**Why it works (debated):**

Original claim: reduces "internal covariate shift" (distribution change in layer inputs during training).

Later findings (Santurkar et al., 2018): BatchNorm smooths the optimization landscape (Lipschitz constant), making gradients more predictive and allowing larger learning rates. The internal covariate shift reduction is a side effect, not the primary benefit.

**Position in network:**

Common patterns:
- Pre-activation: `Conv → BN → ReLU` (He et al.)
- Post-activation: `Conv → ReLU → BN` (original)

Pre-activation often performs better for deep networks because BN normalizes before activation, avoiding distribution distortion.

**Limitations:**
- Small batch size → noisy statistics → training instability
- Works poorly with RNNs/LSTMs (different sequence lengths)
- Difference between train and inference behavior can cause issues at test time

## 2.4 Layer Normalization

**Algorithm (Ba et al., 2016):**

Normalize across the feature dimension (for each sample independently):

```
μ = (1/d) · Σⱼ xⱼ             — mean over features
σ² = (1/d) · Σⱼ (xⱼ - μ)²     — variance over features
ẋⱼ = (xⱼ - μ) / √(σ² + ε)
yⱼ = γ · ẋⱼ + β
```

**Key difference from BatchNorm:**

| Aspect | Batch Normalization | Layer Normalization |
|--------|-------------------|-------------------|
| Normalization dim | Batch dimension | Feature dimension |
| Batch dependency | Yes | No |
| Train vs inference | Different | Same |
| RNN compatibility | Poor | Good |
| Used in | CNNs, MLPs | Transformers, RNNs |

**Why used in Transformers:**
- No dependency on batch size — works for single samples
- Consistent behavior at train and inference
- Sequence models benefit from per-token normalization
- Works well with self-attention mechanisms

## 2.5 Early Stopping

**Algorithm:**

1. Track validation loss after each epoch
2. If validation loss does not improve for `patience` epochs:
   - Stop training
   - Restore model weights from the best epoch

**Why it works:**
- Acts as regularization by limiting model capacity (number of effective gradient steps)
- Prevents overfitting before the model memorizes noise
- The best validation checkpoint often corresponds to the optimal point in the bias-variance tradeoff

**Implementation details:**
- `patience`: typically 5–20 epochs (depends on dataset)
- `min_delta`: minimum change to qualify as improvement (e.g., 1e-4)
- `restore_best_weights`: always set to True in production
- Save best model weights in memory/disk: `if val_loss < best_val_loss: save_checkpoint()`

## 2.6 Data Augmentation

**Common techniques:**

| Type | Techniques | Use case |
|------|-----------|----------|
| Image | Random flip, rotation, crop, color jitter, mixup, CutMix, RandAugment | Computer vision |
| Text | Back-translation, synonym replacement, word dropout, EDA | NLP |
| Audio | SpecAugment, noise addition, time stretch, pitch shift | Speech |
| Tabular | SMOTE, additive noise, feature swapping | Tabular data |

**When it helps:**
- Limited dataset size
- High capacity model prone to overfitting
- Domain invariance is desired (e.g., object detection needs viewpoint invariance)

**When it hurts:**
- Task requires sensitivity to augmentation (e.g., medical diagnosis — flipping changes organ position)
- Augmentations destroy label information (e.g., extreme cropping removes the object)
- Already sufficient data — augmentation adds negligible benefit

## 2.7 Label Smoothing

**Mathematical formulation:**

Replace hard labels with a mixture of the hard label and a uniform distribution:

```
q'(k) = (1 - ε) · δ_{k,y} + ε / K
```

Where:
- `δ_{k,y}` = 1 if `k = y`, 0 otherwise
- `ε` = smoothing parameter (typically 0.1)
- `K` = number of classes

**Effect on loss:**

```
L = -Σₖ q'(k) · log(p(k))
  = (1 - ε) · (-log(p(y))) + (ε/K) · (-Σₖ log(p(k)))
```

**Why it works:**
- Prevents overconfidence — reduces the gap between logits of correct and incorrect classes
- Improves calibration: model's confidence better matches accuracy
- Acts as regularization: prevents the model from pushing logits to infinity
- Used in: Inception-v3, Transformer, BERT, EfficientNet

**When to use:**
- Large number of classes
- Noisy labels (label smoothing provides robustness)
- Knowledge distillation — smoother teacher probabilities

**When to avoid:**
- When true hard labels are critical (e.g., exact classification targets)
- Knowledge distillation student training (soft targets from teacher are already smoothed)

## 2.8 Advanced Regularization

**Stochastic Depth (Huang et al., 2016):**
- Randomly drop entire residual blocks during training
- Survival probability decays linearly with depth: `p_l = 1 - l/L · (1 - p_L)`
- Effectively trains an ensemble of shallower networks
- Improves gradient flow in very deep ResNets (1202 layers)

**DropConnect (Wan et al., 2013):**
- Drop individual weights (connections) rather than neurons
- Each weight is dropped with probability `p`
- More fine-grained than dropout, higher variance
- Less commonly used due to implementation complexity

**Shake-Shake (Gastaldi, 2017):**
- Applies to residual networks with multiple branches
- During training, randomly interpolate between branches: `α·branch₁ + (1-α)·branch₂`
- During inference, use average: `(branch₁ + branch₂)/2`
- Regularizes by forcing features to be useful even when randomly interpolated

**Cutout / Random Erasing:**
- Mask random square regions in input images to zero
- Forces network to use multiple features, not just the most discriminative region
- Simple but effective for vision tasks

## 2.9 Interview Questions — Regularization

1. **Q:** Derive L1 and L2 gradients. Why does L1 produce sparse weights?
   **A:** L2 gradient is `λw` (linear), L1 gradient is `λ·sign(w)` (constant magnitude). L1's constant pull near zero pushes weights exactly to zero; L2's diminishing pull near zero never reaches exactly zero.

2. **Q:** Explain inverted dropout and why scaling is necessary.
   **A:** At training, activations are scaled by `1/(1-p)` to maintain expected value. At inference, no scaling is applied. Without scaling, the expected activation during training would be `(1-p)·E[h]`, lower than inference.

3. **Q:** Why does Batch Normalization use learnable γ and β parameters?
   **A:** Normalization constrains activations to zero mean and unit variance, which may reduce representational power. γ and β allow the layer to learn the optimal scale and shift, restoring the identity transformation if needed.

4. **Q:** Compare Batch Normalization with Layer Normalization for transformers.
   **A:** LayerNorm is independent of batch size, consistent at train/test, and normalizes across features — ideal for attention-based models. BatchNorm would need padding masks and behaves differently at train vs test.

5. **Q:** What is label smoothing and how does it affect the loss landscape?
   **A:** Label smoothing replaces hard targets `[0, 1, 0]` with `[ε/K, 1-ε+ε/K, ε/K]`. This prevents the model from pushing logits to ±∞, creating a softer decision boundary and better calibration.

6. **Q:** How does early stopping act as regularization?
   **A:** Early stopping limits the number of gradient steps, constraining the effective model capacity. The optimization trajectory starts near initialization (small weights) and early stopping keeps weights closer to zero, similar to L2 regularization.

---

# 3. Optimizers

## 3.1 Stochastic Gradient Descent (SGD)

**Basic formulation:**

```
θ_{t+1} = θ_t - η · ∇L(θ_t; xᵢ, yᵢ)
```

Where `θ` are parameters, `η` is learning rate, and gradient is computed on a single random sample (or mini-batch).

**Mini-batch SGD:**

```
θ_{t+1} = θ_t - η · (1/m) · Σᵢ ∇L(θ_t; xᵢ, yᵢ)
```

Mini-batch size: typically 32–512. Trade-off: larger batches give more accurate gradients but less noise for escaping local minima.

**Momentum (Polyak, 1964):**

```
v_{t+1} = μ · v_t + ∇L(θ_t)
θ_{t+1} = θ_t - η · v_{t+1}
```

- `μ` = momentum coefficient (typically 0.9)
- Accumulates gradient history — accelerates in consistent directions, dampens oscillations
- Analogous to physical momentum: a ball rolling down the loss surface

**Nesterov Accelerated Gradient (NAG):**

```
v_{t+1} = μ · v_t + ∇L(θ_t - η · μ · v_t)
θ_{t+1} = θ_t - η · v_{t+1}
```

- Gradient computed at approximate future position `θ_t - η·μ·v_t`
- "Look ahead" correction reduces overshooting
- Faster convergence than standard momentum in theory and practice

**Convergence analysis:**
- Convex functions: SGD converges at `O(1/√T)` rate (sublinear)
- Strongly convex: `O(1/T)` rate with appropriate step sizes
- Non-convex: converges to stationary point (gradient ≈ 0)

## 3.2 AdaGrad (Duchi et al., 2011)

**Algorithm:**

```
g_t = ∇L(θ_t)
G_t = G_{t-1} + g_t²          — accumulation of squared gradients
θ_{t+1} = θ_t - η · g_t / (√(G_t) + ε)
```

**Key properties:**
- Per-parameter learning rates: frequently updated parameters get smaller effective learning rates
- No manual learning rate scheduling needed (learning rate naturally decays)
- `ε` for numerical stability (typically 1e-8)

**Strength:**
- Good for sparse data (e.g., word embeddings, NLP) — infrequent features get larger updates

**Weakness:**
- Accumulation of squared gradients grows monotonically → effective learning rate goes to zero
- Cannot recover from aggressive decay — eventually stops learning
- Unsuitable for dense, deep neural networks

## 3.3 RMSProp (Hinton, 2012)

**Algorithm:**

```
g_t = ∇L(θ_t)
v_t = β · v_{t-1} + (1-β) · g_t²    — moving average of squared gradients
θ_{t+1} = θ_t - η · g_t / (√(v_t) + ε)
```

- `β` = decay rate (typically 0.9)
- Fixes AdaGrad's monotonically decaying learning rate
- Adapts learning rate based on recent gradient magnitudes
- Works well for non-stationary objectives and RNNs

## 3.4 Adam (Kingma & Ba, 2015)

**Algorithm:**

```
g_t = ∇L(θ_t)

— Biased first moment estimate (mean):
m_t = β₁ · m_{t-1} + (1-β₁) · g_t

— Biased second moment estimate (uncentered variance):
v_t = β₂ · v_{t-1} + (1-β₂) · g_t²

— Bias correction:
m̂_t = m_t / (1 - β₁ᵗ)
v̂_t = v_t / (1 - β₂ᵗ)

— Update:
θ_{t+1} = θ_t - η · m̂_t / (√(v̂_t) + ε)
```

**Default hyperparameters:**
- `η = 0.001` — step size
- `β₁ = 0.9` — exponential decay for first moment
- `β₂ = 0.999` — exponential decay for second moment
- `ε = 1e-8` — numerical stability

**Bias correction rationale:**
At early timesteps, `m_t` and `v_t` are biased toward zero (initialized as zero). Bias correction compensates by dividing by `(1-βᵗ)`, which starts at `(1-β)` for `t=1` and approaches 1 as `t` increases.

**Interpretation:**
- First moment: gradient momentum (direction)
- Second moment: adaptive per-parameter learning rate (step size)
- Effective step size: `η · (√(v̂_t) + ε)⁻¹ · m̂_t`

**Strengths:**
- Works well out of the box with default hyperparameters
- Handles sparse gradients, noisy gradients, non-stationary objectives
- Combines momentum with per-parameter adaptation

**Weaknesses:**
- May not generalize as well as SGD with momentum (generalization gap)
- Can fail to converge in some cases (Reddi et al., 2018 — AMSGrad fixes this)
- Second moment estimate can be noisy near convergence

## 3.5 AdamW — Decoupled Weight Decay (Loshchilov & Hutter, 2019)

**Key insight:** In Adam, L2 regularization is not equivalent to weight decay when adaptive learning rates are per-parameter. The gradient of L2 `λw` is normalized by the per-parameter second moment, reducing regularization for frequently updated parameters.

**AdamW formulation:**

```
— Gradient step (no weight decay):
θ_t' = θ_t - η · m̂_t / (√(v̂_t) + ε)

— Decoupled weight decay:
θ_{t+1} = θ_t' - η · λ · θ_t
```

**Effect:**
- Weight decay is applied after the adaptive gradient step, independently of the gradient normalization
- Improves generalization compared to Adam with L2 regularization
- Matches or exceeds SGD with momentum on many benchmarks (especially ImageNet)

**Why it improves generalization:**
- Decouples regularization from the optimization process
- Prevents weight decay from being distorted by adaptive learning rates
- Cleaner separation of loss function and optimization

## 3.6 Learning Rate Schedules

**Step Decay:**
```
η_t = η₀ · γ^{⌊t / step_size⌋}
```
- Drop by factor `γ` (e.g., 0.1) every `step_size` epochs
- Common in pre-ResNet architectures

**Exponential Decay:**
```
η_t = η₀ · e^{-k·t}
```
- Smooth, continuous decay
- `k` controls decay rate

**Cosine Annealing (Loshchilov & Hutter, 2017):**
```
η_t = η_min + (1/2) · (η₀ - η_min) · (1 + cos(t · π / T))
```
- Smooth decay following cosine curve from `η₀` to `η_min`
- Reduces to warm restart when combined with periodic resets (SGDR)

**SGDR — Stochastic Gradient Descent with Warm Restarts:**
```
η_t = η_min + (1/2) · (η₀ - η_min) · (1 + cos(t_mod_T · π / T))
```
- Periodically reset learning rate to `η₀`
- Each restart allows escaping sharp minima
- T can increase geometrically: `T_i = T₀ · γⁱ`

**OneCycle (Smith & Topin, 2019):**
- Learning rate increases from `η_max / div_factor` to `η_max` then decreases to `η_max / div_factor / final_div_factor`
- Typically has warmup phase, then annealing phase
- Can be combined with cosine annealing
- Often achieves better results in fewer epochs

## 3.7 Warmup

**Why needed for Adam:**
- Adam initializes `v_t = 0` for second moment
- Early updates have low `v_t` → learning rate is artificially high
- Can cause large initial updates that destabilize training
- More severe in large batch training

**Linear warmup:**
```
η_t = η_target · (t / T_warmup)  for t ≤ T_warmup
η_t = η_target                   for t > T_warmup
```

**Exponential warmup:**
```
η_t = η_target · (1 - e^{-t / τ})
```

**Gradual warmup (Goyal et al., 2017):**
- Used in large-batch training (8k–32k batch size)
- Scale learning rate linearly with batch size
- Warmup for 5 epochs is common

## 3.8 Gradient Clipping

**Global norm clipping:**

```
g = ∇L(θ)
if ||g||₂ > threshold:
    g = g · (threshold / ||g||₂)
```

**Per-parameter clipping:**
```
gᵢ = max(-threshold, min(threshold, gᵢ))
```

**Threshold selection:**
- Typical values: 0.5–10.0 for global norm
- Start with 1.0 and adjust based on gradient norm distribution
- Monitor gradient norm histogram in TensorBoard

**When to use:**
- RNNs/LSTMs (prone to exploding gradients)
- GAN training (unstable dynamics)
- Large batch training (gradient norms increase with batch size)
- Training with mixed precision (FP16 gradients can overflow)

## 3.9 Second-Order Methods

**Newton's Method:**
```
θ_{t+1} = θ_t - H⁻¹ · ∇L(θ_t)
```
- Uses Hessian matrix `H` for curvature information
- Quadratic convergence near optima
- Prohibitive cost: `O(n³)` for computing/ inverting Hessian with `n` parameters

**L-BFGS (Limited-memory BFGS):**
- Approximates inverse Hessian using recent gradient history
- Uses last `m` gradient differences (memory efficient)
- Works well for small to medium-sized problems
- Not suitable for stochastic optimization (requires full batch)
- Used in: logistic regression, SVM, small neural networks

**When second-order methods are used:**
- Convex optimization with full batch
- Fine-tuning small models where curvature matters
- Neural network hyperparameter optimization (e.g., KFAC in some research)

## 3.10 Interview Questions — Optimizers

1. **Q:** Derive the Adam update rule from first principles. Explain the purpose of each term.
   **A:** [Walk through momentum, adaptive learning rate, bias correction. m = first moment (momentum), v = second moment (adaptive step), bias correction compensates zero initialization.]

2. **Q:** Why does AdamW decouple weight decay from the gradient update?
   **A:** In Adam, L2 regularization gradient is normalized by `√(v̂_t)`, reducing regularization for frequently updated params. AdamW applies weight decay after the adaptive step, keeping it independent of gradient magnitude.

3. **Q:** What is the generalization gap between Adam and SGD with momentum? How can we close it?
   **A:** Adam often finds sharper minima with worse generalization. Solutions: use AdamW, cosine annealing, SGD fine-tuning after Adam, or SWA (Stochastic Weight Averaging).

4. **Q:** Explain how gradient clipping prevents exploding gradients.
   **A:** Clipping caps the gradient norm to a threshold. If `||g|| > threshold`, rescale to `threshold / ||g||`. This bounds the update magnitude to `η·threshold` at most.

5. **Q:** Compare RMSProp and AdaGrad. When would you use each?
   **A:** AdaGrad accumulates squared gradients monotonically, eventually stopping. RMSProp uses a moving average, adapting to recent dynamics. RMSProp is preferred for deep networks; AdaGrad for sparse features.

6. **Q:** Derive the convergence rate of SGD for convex functions.
   **A:** For convex `L` with `||∇L|| ≤ G` and `||θ₁ - θ*|| ≤ D`, choose `η_t = D/(G√T)`. Then `E[L(θ̄_T) - L(θ*)] ≤ GD/√T`.

7. **Q:** Why is learning rate warmup necessary for large-batch training?
   **A:** Large batches produce more accurate gradients with lower variance, allowing larger updates. Without warmup, the large initial learning rate combined with strong gradients causes divergence. Warmup gradually increases LR, stabilizing early training.

---

# 4. Architecture Design

## 4.1 Width vs Depth Trade-off

**Depth benefits:**
- Hierarchical feature learning: edges → shapes → objects
- More parameter-efficient: depth adds capacity with O(depth) parameters
- Deeper networks can represent certain functions with exponentially fewer units (e.g., parity function needs O(2^n) with 1 hidden layer but O(n) with O(log n) depth)

**Width benefits:**
- Easier optimization: wider networks have fewer local minima
- More robust to initialization: less sensitive to bad initialization
- Wider networks with linear bottlenecks benefit from parallelization

**Design heuristics:**
- Increase depth first, then width
- Common ratio: hidden layer width ~2x–4x input dimension
- Very wide layers (>4096) may overfit and are computationally expensive
- Very deep networks (>100 layers) need residual connections

## 4.2 Skip Connections (ResNet — He et al., 2016)

**Formulation:**

```
y = F(x, {Wᵢ}) + x
```

Where `F` is a residual mapping (e.g., two Conv layers) and `x` is the identity shortcut connection.

**Gradient highway:**
```
∂L/∂x = ∂L/∂y · ∂F/∂x + ∂L/∂y
         ↑                ↑
      residual path    identity path
```

The identity path (`∂L/∂y` term) allows gradients to flow directly to earlier layers, bypassing the residual blocks. This prevents vanishing gradients in very deep networks (100+ layers).

**Ensemble interpretation (Veit et al., 2016):**
- ResNets behave as ensembles of many shallower networks
- During training, different subsets of layers are effectively active
- Dropping residual blocks during inference only mildly degrades performance
- The identity path creates an implicit ensemble of 2ᴺ paths for N residual blocks

**Bottleneck design:**
```
1×1 Conv (reduce channels) → 3×3 Conv → 1×1 Conv (expand channels)
```

Reduces computation: `256×256×3×3` → `64×1×1 + 64×3×3 + 256×1×1`

**Pre-activation vs post-activation:**
- Post-activation (original): `Conv → BN → ReLU → addition`
- Pre-activation (He et al., 2016): `BN → ReLU → Conv → addition`
- Pre-activation improves gradient flow and regularization

## 4.3 Dense Connections (DenseNet — Huang et al., 2017)

**Formulation:**

```
x_l = H_l([x₀, x₁, ..., x_{l-1}])
```

Each layer receives feature maps from ALL preceding layers via concatenation.

**Feature reuse:**
- Earlier features are available at all later layers
- Reduces redundant feature learning
- Gradients flow directly through concatenation paths

**Parameter efficiency:**
- DenseNet requires fewer parameters than ResNet for comparable accuracy
- Each layer adds only "growth rate" `k` new feature maps (typically 12–32)
- Total parameters: `O(k · L²)` vs ResNet's `O(k² · L)`

**Transition layers:**
Between dense blocks: `BN → 1×1 Conv → 2×2 AvgPool`
- Reduce number of feature maps (compression factor θ = 0.5)
- Reduce spatial dimension

**Comparison:**
| Aspect | ResNet | DenseNet |
|--------|--------|----------|
| Connection | Summation | Concatenation |
| Feature reuse | Implicit (gradient flow) | Explicit (direct access) |
| Parameters | More per layer | Fewer (small growth rate) |
| Memory | Lower (no storage of all features) | Higher (store all features for backward pass) |
| Computation | Efficient | Can be memory-bound |

## 4.4 Attention Mechanisms

**Self-attention (Vaswani et al., 2017):**

```
Attention(Q, K, V) = softmax(QKᵀ / √d_k) · V
```

- `Q` (queries), `K` (keys), `V` (values)
- `√d_k` scaling prevents dot products from growing too large, avoiding softmax saturation
- Each position can attend to all other positions

**Multi-head attention:**

```
MultiHead(Q, K, V) = Concat(head₁, ..., head_h) · W_O
head_i = Attention(Q·W_Qⁱ, K·W_Kⁱ, V·W_Vⁱ)
```

- `h` heads (typically 8–16) learn different attention patterns
- Each head operates in a lower-dimensional subspace

**As architectural component:**
- Self-attention replaces fixed-size receptive fields (CNNs) with dynamic content-based attention
- Transformers use attention as the primary building block (no convolution/recurrence)
- Attention enables global context modeling at any depth
- Computational cost: `O(n² · d)` for sequence length `n` — quadratic in sequence length

**Efficient variants:**
- Linear attention: approximate softmax with kernel trick → `O(n·d²)`
- Sparse attention: attend only to local or strided positions
- Longformer / BigBird: combine local + global attention

## 4.5 Normalization Layers

**Batch Normalization (BN):**
- Normalizes across batch and spatial dimensions
- Best for CNNs with large batch sizes
- Behavior differs at train vs inference

**Layer Normalization (LN):**
- Normalizes across features
- Used in transformers, RNNs
- Batch-size independent

**Instance Normalization (IN):**
- Normalizes across spatial dimensions only
- Used in style transfer (per-instance statistics)
- Removes instance-specific contrast information

**Group Normalization (GN — Wu & He, 2019):**
- Normalizes within groups of channels
- Compromise between LN (1 group) and IN (C groups)
- Performs well with small batch sizes (2–16)
- Used in object detection and segmentation (batch size limited)

**Switchable Normalization (SN — Luo et al., 2018):**
- Learns to combine BN, LN, and IN with learnable weights
- Adapts to different tasks automatically

**Selection guide:**

| Condition | Recommended |
|-----------|-------------|
| Batch size ≥ 32 | BatchNorm |
| Batch size small (2–16) | GroupNorm |
| Transformers / RNNs | LayerNorm |
| Style transfer | InstanceNorm |
| Small batch + vision | GroupNorm |

## 4.6 Activation Function Decision Tree

```
Is it a transformer-based architecture?
├── Yes → GELU
└── No → Is it a very deep network (>50 layers)?
     ├── Yes → Swish / GELU
     └── No → Is dying ReLU observed?
          ├── Yes → Leaky ReLU / PReLU / ELU
          └── No → ReLU (default)
                   ├── Output binary classification → Sigmoid
                   └── Output multi-class → Softmax
```

## 4.7 Model Scaling — EfficientNet Compound Scaling

**Observation (Tan & Le, 2019):** Scaling network width, depth, or resolution individually gives diminishing returns.

**Compound scaling:**

```
depth: d = α^φ
width: w = β^φ
resolution: r = γ^φ
s.t. α · β² · γ² ≈ 2
```

Where `φ` controls the scaling budget and `α, β, γ` are determined by a small grid search.

**Intuition:**
- Depth (d): more layers for richer features
- Width (w): more channels for finer-grained features
- Resolution (r): larger input for more detailed patterns
- They interact: higher resolution needs deeper networks and wider layers

**Constraint derivation:**
- FLOPs ~ d · w² · r² (approximately)
- Doubling resolution quadruples FLOPs (2D input)
- Doubling width quadruples FLOPs
- Doubling depth doubles FLOPs
- Constraint: `α · β² · γ² ≈ 2` ensures total FLOPs scale by `2^φ`

---

# 5. Model Compression

## 5.1 Pruning

**Magnitude pruning (Han et al., 2015):**
- Remove weights with smallest absolute values
- Simple, effective, computationally cheap
- Can reduce parameters by 90%+ without significant accuracy loss

**Iterative vs one-shot:**

| Approach | Description | Effectiveness |
|----------|-------------|---------------|
| One-shot | Prune all weights at once | Simple but can cause large accuracy drop |
| Iterative | Prune incrementally, retrain between rounds | Better accuracy recovery, more flexible |

**Structured vs unstructured:**

| Aspect | Unstructured | Structured |
|--------|--------------|------------|
| Granularity | Individual weights | Channels, filters, layers |
| Hardware speedup | Requires sparse hardware | Dense hardware compatible |
| Compression ratio | Higher (90%+) | Lower (30–50%) |
| Use case | Storage compression | Inference acceleration on GPUs |

**Lottery Ticket Hypothesis (Frankle & Carbin, 2019):**
- A randomly initialized network contains a subnetwork (winning ticket) that can train to comparable accuracy
- Winning tickets are found by: train → prune → reset remaining weights to original initialization
- Winning tickets are smaller and train faster
- Implications: overparameterization provides a pool of good subnetworks; SGD naturally finds a lottery ticket during training

**Pruning at initialization:**
- SNIP (Lee et al., 2019): prune based on connection sensitivity
- GraSP (Wang et al., 2020): prune based on gradient signal preservation
- Can prune before training, reducing training cost

## 5.2 Quantization

**Post-Training Quantization (PTQ):**
- Convert weights and activations to lower precision after training
- Calibration: run a few batches to determine activation ranges
- May cause accuracy drop for very low precision

**Quantization-Aware Training (QAT):**
- Simulate quantization during training (fake quantization)
- Model learns to adapt to quantization noise
- Better accuracy than PTQ, especially at INT4
- Straight-through estimator (STE): approximate gradient through quantization

**Symmetric vs Asymmetric:**

```
Symmetric:  x_q = round(x / scale)    — range symmetric around zero
Asymmetric: x_q = round(x / scale + zero_point)  — can offset
```

- Symmetric: simpler, good for weights (symmetric distribution)
- Asymmetric: better for activations (often positive after ReLU)

**Per-tensor vs Per-channel:**

```
Per-tensor: single scale for entire tensor
Per-channel: separate scale for each output channel
```

- Per-channel is more accurate but more complex
- Prefer per-channel for weights, per-tensor for activations

**Precision levels:**

| Precision | Bits | Range | Use case |
|-----------|------|-------|----------|
| FP32 | 32 | ~1e-38 to ~3e38 | Training default |
| FP16 | 16 | ~5.5e-8 to 65504 | Mixed precision training |
| BF16 | 16 | ~1e-38 to ~3e38 | Google TPUs, stable training |
| INT8 | 8 | -128 to 127 | Inference, GPU-friendly |
| INT4 | 4 | -8 to 7 | Edge devices, aggressive compression |

## 5.3 Knowledge Distillation (Hinton et al., 2015)

**Architecture:**

```
Teacher (large) ─── predictions (soft targets) ───┐
                                                    ├──→ Student loss
Student (small) ─── predictions ───────────────────┘
                                ↑
                           hard targets (labels)
```

**Soft labels with temperature:**

```
pᵢ = exp(zᵢ / T) / Σⱼ exp(zⱼ / T)
```

- `T` = temperature (higher → softer distribution)
- High temperature reveals inter-class relationships (e.g., "cat" is more like "dog" than "car")
- Training typically uses T > 1 for the teacher, then student is trained at T = 1

**Loss function:**

```
L = α · L_hard(y, σ(z_student)) + (1-α) · T² · L_KL(σ(z_teacher/T), σ(z_student/T))
```

- `α` balances hard label and soft label components
- `T²` scaling ensures gradient magnitude is consistent across temperatures

**Why distillation works:**
- Soft targets contain more information than hard labels (dark knowledge)
- Teacher provides a smoothed label distribution that regularizes the student
- The student learns the teacher's generalization patterns, not just training data

**Student-teacher design patterns:**
- Same architecture, smaller size (fewer layers/channels)
- Different architecture (e.g., CNN teacher → transformer student)
- Self-distillation: student = teacher (same architecture, train on teacher's soft labels)
- Distillation can be combined with pruning and quantization

## 5.4 Weight Sharing

**Concept:** Multiple parts of a network share the same weight parameters.

**Applications:**
- Convolution: weight sharing via kernel convolution across spatial locations
- RNN: same weights applied to each timestep
- Siamese networks: identical weights for parallel subnetworks
- Super-resolution: shared upsampling layers

**Benefits:**
- Drastically reduces parameter count
- Enables translation invariance (CNNs)
- Sequence-length independence (RNNs)
- Regularization via parameter sharing

## 5.5 Interview Questions — Model Compression

1. **Q:** What is the lottery ticket hypothesis and how do you find winning tickets?
   **A:** Subnetworks exist within random networks that can train to full accuracy. Find by: train → prune p% weights → reset retained weights to original init → retrain. Repeat iteratively.

2. **Q:** Compare PTQ and QAT. When would you use each?
   **A:** PTQ is faster (no retraining) and good for INT8 on large models. QAT is needed for INT4 or when accuracy drop from PTQ exceeds threshold. PTQ first; if accuracy drops > 1%, use QAT.

3. **Q:** Derive the knowledge distillation loss. Why is temperature important?
   **A:** [Write the standard KD loss with KL divergence. Temperature T smooths the softmax — higher T reveals relationships between classes. T² scaling compensates for gradient magnitude change.]

4. **Q:** What is the difference between structured and unstructured pruning? When does each give hardware speedup?
   **A:** Unstructured prunes individual weights (needs sparse hardware). Structured prunes entire channels/filters — directly reduces computation on standard hardware (GPUs/TPUs).

5. **Q:** How can pruning, quantization, and distillation be combined?
   **A:** Typical pipeline: 1) Train teacher, 2) Distill to student, 3) Prune student weights, 4) Quantize to INT8/INT4. Each step compounds compression with graceful degradation.

---

# 6. Transfer Learning

## 6.1 Feature Extraction vs Fine-Tuning

**Feature extraction (frozen backbone):**
- Pre-trained model weights are frozen
- Only the new classification head is trained
- Suitable when: small target dataset, target similar to source domain
- Advantages: fast training, low risk of overfitting, minimal computation

**Fine-tuning:**
- Pre-trained weights are used as initialization
- All (or some) layers are updated during training
- Suitable when: sufficient target data, domain shift, improved performance needed

**When to freeze layers:**

| Scenario | Freeze strategy | Rationale |
|----------|----------------|-----------|
| Very small dataset (<1k samples) | Freeze all but head | Prevent overfitting |
| Medium dataset (1k–10k) | Freeze early layers, fine-tune later layers | Early layers learn generic features |
| Large dataset (>10k) | Fine-tune all layers | Enough data to adapt all features |
| Domain shift (e.g., natural → medical) | Fine-tune all layers, use small LR | All layers need adaptation |
| Source close to target | Only fine-tune last few layers | No need to change early features |

**Learning rate strategy:**
- Lower learning rate for pre-trained layers (1e-5 to 5e-5)
- Higher learning rate for randomly initialized layers (1e-4 to 1e-3)
- Use differential learning rates: decayed by `discriminative_factor` per layer (ULMFiT approach)

**Common pitfalls:**
- Forgetting: fine-tuning with high LR destroys pre-trained features
- Overfitting: fine-tuning large models on small datasets
- Catastrophic forgetting when fine-tuning for too long
- Not adjusting batch norm layers (set to train mode or freeze statistics)

## 6.2 Domain Adaptation

**Covariate shift:**
- Training (source) and test (target) distributions differ: `P_source(x, y) ≠ P_target(x, y)`
- In covariate shift, only `P(x)` changes, `P(y|x)` remains the same
- Domain adaptation aims to align feature distributions

**Domain adversarial training (Ganin et al., 2016):**

```
Feature extractor (G_f) → [Label predictor (G_y): classification loss]
                         → [Domain classifier (G_d): domain confusion loss]
```

- Gradient reversal layer: flip gradient sign for domain classifier
- Feature extractor learns domain-invariant features
- Domain classifier cannot distinguish source from target
- Result: features that work well on both domains

**Distribution alignment methods:**
- MMD (Maximum Mean Discrepancy): minimizes distance between source and target feature distributions
- CORAL: align second-order statistics (covariance) of features
- Deep CORAL: deep network variant of CORAL alignment

**Self-training for domain adaptation:**
- Train on source, pseudo-label target, retrain with high-confidence target predictions
- Iterative process with confidence thresholding
- Can be combined with consistency regularization

## 6.3 Multi-Task Learning

**Architecture:**
- Shared backbone (encoder): learns representations useful for all tasks
- Task-specific heads: separate output layers for each task

**Loss weighting:**

```
L_total = Σᵗ wᵗ · Lᵗ
```

Where `wᵃ` weights each task's contribution. Common approaches:
- Equal weighting: all tasks equally important
- Uncertainty weighting (Kendall et al., 2018): `wᵗ = 1 / (2 · σ²ᵗ)` where `σᵗ` is learned task uncertainty
- Dynamic weighting: adjust weights based on gradient magnitude or task difficulty
- PCGrad (Yu et al., 2020): project conflicting gradients to avoid interference

**When multi-task learning helps:**
- Tasks share lower-level features (e.g., object detection + segmentation)
- Auxiliary tasks provide useful inductive bias (e.g., pose estimation + action recognition)
- Low-resource tasks benefit from high-resource task data
- Model must handle multiple related tasks efficiently

**When it hurts (negative transfer):**
- Tasks have conflicting objectives
- One task dominates the shared representation
- Insufficient model capacity for all tasks
- Different data distributions across tasks

## 6.4 Best Practices for Fine-Tuning

1. **Use pre-trained weights from the same domain if possible** (e.g., medical images → medical pre-training)
2. **Replace and re-initialize the output layer** — it must match the new task's number of classes
3. **Start with a small learning rate** (1e-5 to 5e-5 for Adam, 1e-3 for SGD)
4. **Gradual unfreezing** (Howard & Ruder, 2018):
   - First epoch: only head trains
   - Second epoch: unfreeze last third of layers
   - Third epoch: unfreeze all layers
5. **Use discriminative learning rates**: lower LR for earlier layers
6. **Monitor validation loss closely** — stop when it begins to increase
7. **Data augmentation** is especially important when fine-tuning on small datasets
8. **Freeze batch norm** statistics when fine-tuning with very small batches
9. **Use weight decay** (1e-4 to 1e-5) to prevent overfitting to small target data
10. **Consider re-initializing the classifier head and training from scratch for a few epochs** before unfreezing the backbone

---

# 7. Debugging Neural Networks

## 7.1 Loss Not Decreasing

**Checklist:**

1. **Learning rate too high or too low:**
   - Too high: loss oscillates or diverges (NaN)
   - Too low: loss decreases very slowly
   - Fix: try LR range test (Smith, 2017) — increase LR exponentially from 1e-7 to 10, plot loss

2. **Gradient issues:**
   - Vanishing gradients: all layers have near-zero gradients
   - Exploding gradients: gradient norms exceed 1e3
   - Fix: check gradient histograms, add clipping, adjust initialization

3. **Data normalization:**
   - Input features should be zero mean, unit variance
   - Check for extreme values or NaN in input data
   - Label distribution: class imbalance can cause loss stagnation

4. **Model architecture:**
   - Dead ReLU: all activations are zero (check activation histograms)
   - Wrong output activation (e.g., using tanh for multi-class instead of softmax)
   - Incorrect loss function (e.g., BCE for multi-class)

5. **Implementation bugs:**
   - Wrong gradient computation
   - Parameters not being updated (check after optimizer step)
   - Accumulated gradients not zeroed
   - Data loading errors (e.g., always returning same batch)

## 7.2 Overfitting

**Signs:**
- Training loss decreases but validation loss increases
- Large gap between train and validation metrics
- Model memorizes training data

**Solutions in order of effectiveness:**

| Solution | Effect |
|----------|--------|
| More data / augmentation | Best first step |
| Reduce model capacity | Smaller network |
| Increase regularization | L2, dropout, label smoothing |
| Early stopping | Stops before overfitting |
| Reduce training epochs | Direct control |
| Ensemble models | Robust predictions |

## 7.3 NaN Loss

**Common causes:**

1. **Gradient explosion:** weight values exceed numerical range
   - Fix: gradient clipping, reduce learning rate, add batch norm

2. **Learning rate too high:** weights oscillate out of control
   - Fix: reduce LR, add warmup, use cosine schedule

3. **Numerical instability:**
   - Log of zero: `log(softmax)` → `logsumexp` trick
   - Division by zero: add epsilon in denominators
   - Check for `x/0`, `x**2` overflow, etc.

4. **Data issues:**
   - NaN or Inf in input data
   - Labels out of valid range

**Debugging steps:**
- Use `torch.autograd.set_detect_anomaly(True)`
- Check gradient norms after backward pass
- Add gradient clipping and observe
- Reduce learning rate by 10x
- Use FP32 instead of mixed precision

## 7.4 Saturation

**Dead ReLU (Dying ReLU):**
- Neuron always outputs 0 for all training samples
- Gradient through dead ReLU is 0 — neuron is permanently dead
- Causes: bad initialization, high learning rate, large negative bias shift

**Fixes:**
- Use LeakyReLU, PReLU, ELU, or GELU instead
- Reduce learning rate
- Check weight initialization (use He init for ReLU)
- Batch norm before activation (pre-activation design)

**Activation saturation (sigmoid/tanh):**
- Neurons operate in the saturating regime (|x| > 5 for sigmoid)
- Gradients are near zero → no learning
- Fix: switch to ReLU family, add batch norm, proper initialization

## 7.5 Visualization with TensorBoard

**Key metrics to log:**

```
Loss:
  ├── Training loss (per step/epoch)
  └── Validation loss (per epoch)
Accuracy:
  ├── Training accuracy
  └── Validation accuracy
Learning rate:
  └── Current learning rate
```

**Weight histograms:**
- Distribution of weight values per layer over time
- Healthy: centered around zero, some spread
- Dead: all weights near zero
- Exploding: wide distribution with outliers

**Gradient histograms:**
- Distribution of gradient values per layer
- Healthy: ~1e-3 to 1e-1 range
- Vanishing: all near zero (especially early layers)
- Exploding: order of magnitude larger than activations

**Activation histograms:**
- Distribution of layer outputs
- Dead ReLU: spike at exactly 0
- Saturated tanh: spikes at -1 and 1
- Healthy ReLU: spike at 0 + right-skewed distribution

**Network graph:**
- Verify the computational graph matches the intended architecture
- Check parameter shapes and connections

## 7.6 Gradient Checking

**Numerical gradient verification:**

```
∂L/∂θ ≈ (L(θ + ε) - L(θ - ε)) / (2ε)
```

- `ε` = 1e-4 to 1e-6 (typically 1e-5)
- Compare with analytical gradient: `relative_error = |numerical - analytical| / max(|numerical|, |analytical|, ε)`

**Thresholds:**
- Relative error < 1e-7: perfect
- Relative error < 1e-5: likely correct
- Relative error > 1e-3: WRONG — debug

**Best practices:**
- Use double precision (FP64) for gradient checking
- Test with a small model and few parameters
- Test before full training starts
- Use a small number of random training samples
- Enable gradient checking module in PyTorch: `torch.autograd.gradcheck`

**Common gradient check failures:**
- Wrong loss function gradient
- Activation derivative error
- Incorrect parameter sharing
- Non-differentiable operations (argmax, sorting)
- Numerical instability in custom operations

---

*End of Neural Networks Interview Guide*
