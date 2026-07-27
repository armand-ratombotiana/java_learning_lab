# Math for AI — Interview Guide (Sub-Academy)

> Targeted interview prep for the 10 micro-labs in `math-for-ai-deep`. Each micro-lab includes key interview questions, company focus areas, relevance statements, and code examples.

---

## Table of Contents

1. [01-vectors-spaces](#01-vectors-spaces)
2. [02-linear-transformations](#02-linear-transformations)
3. [03-eigen-decomposition](#03-eigen-decomposition)
4. [04-svd-applications](#04-svd-applications)
5. [05-calculus-for-ml](#05-calculus-for-ml)
6. [06-probability-distributions](#06-probability-distributions)
7. [07-inference-bayes](#07-inference-bayes)
8. [08-optimization-gd](#08-optimization-gd)
9. [09-regularization-complexity](#09-regularization-complexity)
10. [10-numerical-computing](#10-numerical-computing)

---

## 01-vectors-spaces

### Key Interview Questions

**Q1:** Explain the geometric interpretation of dot product and its role in attention mechanisms.

> Dot product $q \cdot k = \|q\|\|k\|\cos\theta$ measures alignment. In attention, query and key vectors with high dot product = high attention weight. Scaled dot-product attention: $\text{softmax}(QK^T / \sqrt{d_k})$.

**Q2:** Why does L1 regularization induce sparsity while L2 does not?

> L1 ball is a diamond (constraint region has corners on axes). L2 ball is spherical. Optimal solution for L1 hits corner = zero coefficients. Mathematically: subgradient of $|w|$ includes 0 at $w=0$, allowing exact zeros.

**Q3:** What is the curse of dimensionality and how do norms behave in high dimensions?

> In high dimensions, the volume of space grows exponentially. Most points are far apart. The ratio of nearest-to-farthest distance approaches 1 — distance metrics become meaningless. L2 norm concentrates: $\|x\|_2 \approx \sqrt{d}\sigma$ for i.i.d. entries.

**Q4:** Explain linear independence and why it matters for feature matrices.

> If features are linearly dependent, $X^T X$ is singular (not invertible). Linear regression has no unique solution. Regularization ($X^T X + \lambda I$) fixes this.

### Company Focus

| Company | Focus Area |
|---|---|
| OpenAI | Embedding spaces, cosine similarity for search/retrieval |
| Google DeepMind | Representation learning, manifold learning |
| Meta AI | High-dimensional recommender systems |
| Anthropic | Sparse features in interpretability |

### Relevance to ML/DL

- Word embeddings (Word2Vec, GloVe): semantic spaces, cosine similarity
- Attention: dot product between query and key vectors
- Feature engineering: multicollinearity detection
- Dimensionality reduction: PCA, t-SNE, UMAP

### Code Example

```python
import numpy as np

def cosine_similarity(a, b):
    return np.dot(a, b) / (np.linalg.norm(a) * np.linalg.norm(b))

# Embedding similarity example
query = np.array([0.2, 0.8, 0.1, 0.5])
key1 = np.array([0.3, 0.7, 0.2, 0.4])
key2 = np.array([0.9, 0.1, 0.8, 0.2])

print(f"Similarity with key1: {cosine_similarity(query, key1):.3f}")
print(f"Similarity with key2: {cosine_similarity(query, key2):.3f}")
```

---

## 02-linear-transformations

### Key Interview Questions

**Q1:** How do linear transformations compose and what does this mean for neural network layers?

> Composition of linear transformations = matrix multiplication. Two linear layers without activation collapse to one: $W_2(W_1 x) = (W_2 W_1)x$. Activations break linearity, enabling deep networks to learn non-linear functions.

**Q2:** Explain the relationship between matrix rank and information content.

> Rank = number of independent dimensions. Low-rank = redundancy. Low-rank approximation removes noise, captures latent structure. Used in matrix factorization for recommendations.

**Q3:** How does a linear layer in PyTorch relate to matrix multiplication?

> `nn.Linear(in_features, out_features)` stores weight $W \in \mathbb{R}^{out \times in}$ and bias $b$. Forward: $y = Wx + b$.

**Q4:** What is the null space of a matrix and why should ML practitioners care?

> Null space = $\{x : Ax = 0\}$. Non-trivial null space means information loss: different inputs map to same output. In linear autoencoders with bottleneck, null space of decoder indicates unreconstructable directions.

### Company Focus

| Company | Focus Area |
|---|---|
| Nvidia | GPU-optimized matrix operations, cuBLAS, Tensor Cores |
| Apple | Neural Engine, efficient matrix multiplication |
| Tesla (Dojo) | Custom hardware for matrix ops in vision |

### Relevance to ML/DL

- Every linear layer is a matrix multiplication
- Convolutions are special structured linear transformations
- Transformer MLP layers: two linear transformations with GELU activation
- Batch normalization: learnable affine transform

### Code Example

```python
import torch
import torch.nn as nn

# Linear layer as matrix transformation
layer = nn.Linear(128, 64)
x = torch.randn(32, 128)  # batch of 32
y = layer(x)

# Manual equivalent
W, b = layer.weight, layer.bias
y_manual = x @ W.T + b
assert torch.allclose(y, y_manual, atol=1e-6)
print(f"Output shape: {y.shape}")  # (32, 64)
```

---

## 03-eigen-decomposition

### Key Interview Questions

**Q1:** How are eigenvectors of the covariance matrix related to PCA?

> PCA finds eigenvectors of $\Sigma = X^T X$ (assuming centered data). Top eigenvectors = directions of maximum variance. Projection onto top-$k$ eigenvectors = dimensionality reduction.

**Q2:** Explain the condition number and its impact on gradient descent.

> Condition number $\kappa = \lambda_{\max}/\lambda_{\min}$ (ratio of extreme eigenvalues of Hessian). Large $\kappa$ = ill-conditioned = slow GD convergence (zig-zag behavior). Preconditioning rescales to reduce $\kappa$.

**Q3:** What does the eigenvalue spectrum tell us about a dataset?

> Rapidly decaying spectrum = data lies near low-dimensional subspace. Slow decay = high intrinsic dimensionality. Plateau = possible noise floor.

**Q4:** How does spectral normalization work in GANs?

> Spectral normalization constrains spectral norm (largest singular value) of each weight matrix to 1. Stabilizes GAN training by controlling Lipschitz constant. Applied via power iteration to estimate $\sigma_{\max}$.

### Company Focus

| Company | Focus Area |
|---|---|
| DeepMind | Eigenvalue analysis of neural network Hessians |
| OpenAI | Spectral analysis of transformers, grokking |
| Microsoft | Preconditioned optimization for large-scale training |

### Relevance to ML/DL

- PCA preprocessing
- Hessian analysis for optimization
- Graph neural networks: Laplacian eigenmaps
- Spectral normalization in GANs
- Understanding loss landscape curvature

### Code Example

```python
import numpy as np
from sklearn.decomposition import PCA

# PCA via eigendecomposition of covariance
X = np.random.randn(100, 20) - 0.5
X_centered = X - X.mean(axis=0)
cov = X_centered.T @ X_centered / (X.shape[0] - 1)
eigenvalues, eigenvectors = np.linalg.eigh(cov)

# Sort descending
idx = np.argsort(eigenvalues)[::-1]
eigenvalues = eigenvalues[idx]
eigenvectors = eigenvectors[:, idx]

# Variance explained
var_explained = eigenvalues / eigenvalues.sum()
print(f"Top 3 variance ratios: {var_explained[:3].round(3)}")
print(f"Using sklearn: {PCA().fit(X).explained_variance_ratio_[:3].round(3)}")
```

---

## 04-svd-applications

### Key Interview Questions

**Q1:** Derive the SVD-based solution for matrix completion (recommendation systems).

> Minimize $\sum_{(i,j) \in \Omega} (A_{ij} - u_i^T v_j)^2 + \lambda(\|U\|_F^2 + \|V\|_F^2)$. Solved via alternating least squares (ALS) or SGD. Netflix Prize was won with SVD-based models.

**Q2:** How is truncated SVD used for data compression?

> For $A = U \Sigma V^T$, keep only top $k$ singular values: $A_k = U_k \Sigma_k V_k^T$. Storage reduces from $mn$ to $k(m + n + 1)$. For images, this is a standard compression technique.

**Q3:** Explain the connection between SVD and PCA.

> For centered data matrix $X$: $X = U \Sigma V^T$. Principal components = columns of $V$. Projection = $U_k \Sigma_k$ (scores). SVD avoids computing $X^T X$ explicitly (numerically stable).

**Q4:** What is the pseudoinverse and when is it used?

> $A^+ = V \Sigma^+ U^T$. Solves $Ax = y$ for non-square / rank-deficient $A$. Gives minimum-norm least-squares solution. Used in linear regression when $X^T X$ is singular.

### Company Focus

| Company | Focus Area |
|---|---|
| Netflix/Spotify | Matrix factorization for recommendations |
| Google | PageRank via SVD-like eigenvector computation |
| Pinterest | SVD for image embedding compression |
| Amazon | Product recommendation via low-rank models |

### Relevance to ML/DL

- Collaborative filtering (recommendation systems)
- Image compression and denoising
- PCA / dimensionality reduction
- Latent semantic analysis (LSA) in NLP
- Low-rank adaptation (LoRA) for fine-tuning LLMs

### Code Example

```python
import numpy as np

# Image compression via truncated SVD
def compress_svd(A, k):
    U, s, Vt = np.linalg.svd(A, full_matrices=False)
    return U[:, :k] @ np.diag(s[:k]) @ Vt[:k, :]

# Matrix completion via alternating least squares (simplified)
def als_completion(R, k=10, lambda_reg=0.1, max_iter=20):
    n_users, n_items = R.shape
    U = np.random.randn(n_users, k)
    V = np.random.randn(n_items, k)
    for _ in range(max_iter):
        for i in range(n_users):
            idx = np.where(~np.isnan(R[i, :]))[0]
            V_i = V[idx, :]
            U[i] = np.linalg.solve(V_i.T @ V_i + lambda_reg * np.eye(k), V_i.T @ R[i, idx])
        for j in range(n_items):
            idx = np.where(~np.isnan(R[:, j]))[0]
            U_j = U[idx, :]
            V[j] = np.linalg.solve(U_j.T @ U_j + lambda_reg * np.eye(k), U_j.T @ R[idx, j])
    return U, V

# LoRA-style low-rank weight update
def lora_update(W, rank=4):
    # W_updated = W + BA where B in R^{dxr}, A in R^{rxd}
    d = W.shape[0]
    B = np.random.randn(d, rank) * 0.01
    A = np.random.randn(rank, d) * 0.01
    return W + B @ A

print("SVD compression ratio:", (10 + 10 + 1) / (10 * 10))
```

---

## 05-calculus-for-ml

### Key Interview Questions

**Q1:** Derive the backpropagation update for a 2-layer neural network.

> Forward: $z_1 = W_1 x$, $a_1 = \sigma(z_1)$, $z_2 = W_2 a_1$, $\hat{y} = z_2$.
> Backward: $\frac{\partial L}{\partial z_2} = \hat{y} - y$, $\frac{\partial L}{\partial W_2} = \frac{\partial L}{\partial z_2} a_1^T$,
> $\frac{\partial L}{\partial a_1} = W_2^T \frac{\partial L}{\partial z_2}$, $\frac{\partial L}{\partial z_1} = \frac{\partial L}{\partial a_1} \odot \sigma'(z_1)$,
> $\frac{\partial L}{\partial W_1} = \frac{\partial L}{\partial z_1} x^T$.

**Q2:** Explain the vanishing gradient problem.

> With sigmoid/tanh, derivatives are bounded ($\sigma'(z) \leq 0.25$). In deep networks, repeated multiplication causes gradients to vanish exponentially with depth. Solutions: ReLU, residual connections, batch normalization, careful initialization.

**Q3:** What is the Hessian and how does it relate to second-order optimization?

> Hessian encodes curvature. Newton's method: $x_{t+1} = x_t - H^{-1} \nabla f$. Converges faster but $O(n^3)$ per step. Hessian-vector products can be computed in $O(n)$ via Pearlmutter's trick.

**Q4:** Derive the gradient of the softmax with cross-entropy loss.

> Let $p_i = e^{z_i} / \sum_j e^{z_j}$, $L = -\sum y_i \log p_i$.
> $\frac{\partial L}{\partial z_i} = p_i - y_i$ (clean gradient = prediction - target).

### Company Focus

| Company | Focus Area |
|---|---|
| PyTorch Team (Meta) | Autograd, reverse-mode AD |
| Google (JAX) | Forward/reverse-mode AD, `grad`, `vmap`, `jit` |
| OpenAI | Efficient backprop for large models |
| DeepMind | Hessian-free optimization |

### Relevance to ML/DL

- Core of all neural network training (backpropagation)
- Understanding gradient flow problems
- Designing new activation functions, normalization layers
- Second-order optimization methods

### Code Example

```python
import torch

# Automatic differentiation example
x = torch.tensor([2.0, 3.0], requires_grad=True)
y = torch.tensor([1.0, 0.0])

# Simple 2-layer net
W1 = torch.randn(3, 2, requires_grad=True)
W2 = torch.randn(2, 3, requires_grad=True)

z1 = x @ W1.T
a1 = torch.relu(z1)
z2 = a1 @ W2.T
loss = ((z2 - y) ** 2).sum()

loss.backward()  # reverse-mode autograd
print(f"dL/dW1 shape: {W1.grad.shape}")
print(f"dL/dW2 shape: {W2.grad.shape}")

# Manual verification: gradient of squared error
# dL/dz2 = 2 * (z2 - y)
grad_z2 = 2 * (z2 - y)
print(f"Gradient check: {torch.allclose(W2.grad, grad_z2.T[:, None] * a1[None, :], atol=1e-6)}")
```

---

## 06-probability-distributions

### Key Interview Questions

**Q1:** Why is the Gaussian distribution so fundamental in ML?

> Central Limit Theorem: sums of i.i.d. variables are approximately normal. Used for: noise models, weight initialization (He, Glorot), variational autoencoders (reparameterization trick), Gaussian processes, Brownian motion in diffusion models.

**Q2:** Explain the reparameterization trick for the Normal distribution.

> Instead of sampling $z \sim N(\mu, \sigma^2)$ (non-differentiable), write $z = \mu + \sigma \cdot \epsilon$ with $\epsilon \sim N(0, 1)$. Gradients can flow through $\mu$ and $\sigma$. Essential for training VAEs.

**Q3:** Compare Bernoulli, Categorical, and Gaussian output distributions for neural networks.

> Bernoulli: binary classification (sigmoid output). Categorical: multi-class (softmax output). Gaussian: regression (linear output with learned variance). Each corresponds to a different likelihood in MLE.

**Q4:** What distribution does Gumbel-Softmax approximate and why is it useful?

> Gumbel-Softmax approximates the Categorical distribution while being differentiable. Enables gradient-based learning of discrete latent variables. Temperature parameter controls relaxation.

### Company Focus

| Company | Focus Area |
|---|---|
| OpenAI | Diffusion models (Gaussian noise scheduling) |
| DeepMind | Gaussian processes, Bayesian deep learning |
| Stability AI | Latent diffusion models, VAE distributions |
| Uber | Probabilistic programming (Pyro) |

### Relevance to ML/DL

- Loss functions correspond to negative log-likelihood under specific distributions
- Initialization schemes are distribution-dependent
- Bayesian neural networks, VAEs, normalizing flows
- Diffusion models (forward/reverse processes)
- Uncertainty quantification

### Code Example

```python
import torch
import torch.distributions as dist

# VAE reparameterization trick
def vae_sample(mu, logvar):
    std = torch.exp(0.5 * logvar)
    eps = torch.randn_like(std)
    return mu + eps * std

# Gumbel-Softmax for discrete outputs
def gumbel_softmax(logits, temperature=1.0):
    gumbel = -torch.log(-torch.log(torch.rand_like(logits) + 1e-20) + 1e-20)
    return torch.softmax((logits + gumbel) / temperature, dim=-1)

# Negative log-likelihood = loss
def gaussian_nll(y_pred, y_true, logvar):
    return 0.5 * (torch.exp(-logvar) * (y_pred - y_true)**2 + logvar + torch.log(2 * torch.tensor(torch.pi)))

# Mixture of Gaussians (for MDN)
def mixture_density_loss(y, pi, mu, sigma, n_components):
    component_log_prob = dist.Normal(mu, sigma).log_prob(y.unsqueeze(-1))
    return -torch.logsumexp(torch.log(pi + 1e-8) + component_log_prob, dim=-1).mean()

print("Reparameterization enables gradient flow through random nodes")
```

---

## 07-inference-bayes

### Key Interview Questions

**Q1:** Derive the evidence lower bound (ELBO) for variational inference.

> $$
> \begin{aligned}
> \log p(x) &= \log \int p(x, z) dz \\
> &= \log \int q(z) \frac{p(x, z)}{q(z)} dz \\
> &\geq \int q(z) \log \frac{p(x, z)}{q(z)} dz \quad \text{(Jensen's inequality)} \\
> &= \mathbb{E}_{q}[\log p(x, z) - \log q(z)] \\
> &= \mathbb{E}_{q}[\log p(x | z)] - KL(q(z) \| p(z)) = \text{ELBO}
> \end{aligned}
> $$

**Q2:** How does Bayesian inference help with uncertainty quantification?

> Bayesian methods provide: epistemic uncertainty (model uncertainty — captured by posterior), aleatoric uncertainty (data noise — captured by likelihood). Useful for: active learning, safe RL, medical diagnosis.

**Q3:** Explain MCMC and when it's preferred over variational inference.

> MCMC generates samples from the posterior via a Markov chain. Asymptotically exact but slow. Preferred when: posterior is complex, accuracy matters more than speed, dimensionality is moderate.

**Q4:** What is a conjugate prior and why are they useful?

> Prior $p(\theta)$ is conjugate to likelihood $p(x|\theta)$ if posterior is same family as prior. Enables closed-form Bayesian updating. Examples: Beta-Bernoulli, Normal-Normal, Gamma-Poisson.

### Company Focus

| Company | Focus Area |
|---|---|
| DeepMind | Bayesian RL, Bayesian optimization |
| OpenAI | Bayesian deep learning for safe AI |
| Google | Bayesian methods for ads, recommender systems |
| Secondmind | Bayesian optimization for hyperparameter tuning |

### Relevance to ML/DL

- Bayesian neural networks for uncertainty
- Variational autoencoders (VAE)
- Bayesian optimization for hyperparameter search
- Thompson sampling in bandits / RL
- Posterior inference for interpretability

### Code Example

```python
import torch
import torch.distributions as dist

# Variational Autoencoder ELBO
def elbo_loss(x, x_recon, mu, logvar, prior=dist.Normal(0, 1)):
    recon_loss = dist.Bernoulli(logits=x_recon).log_prob(x).sum(dim=-1)
    # or for continuous: dist.Normal(x_recon, 1).log_prob(x).sum(dim=-1)
    kl_loss = -0.5 * (1 + logvar - mu**2 - logvar.exp()).sum(dim=-1)
    return (recon_loss - kl_loss).mean()  # ELBO

# Bayesian linear regression closed form (conjugate)
def bayesian_linear_regression(X, y, alpha=1.0, beta=1.0):
    """Posterior for w: N(w | m_N, S_N) with conjugate Normal-Gamma prior"""
    S_N_inv = alpha * np.eye(X.shape[1]) + beta * X.T @ X
    S_N = np.linalg.inv(S_N_inv)
    m_N = beta * S_N @ X.T @ y
    return m_N, S_N

# MC Dropout for approximate Bayesian inference
def mc_dropout_predict(model, x, n_samples=100):
    model.train()  # keep dropout on
    preds = torch.stack([model(x) for _ in range(n_samples)])
    mean = preds.mean(dim=0)
    var = preds.var(dim=0)
    return mean, var

print("Variational inference trades exactness for scalability")
```

---

## 08-optimization-gd

### Key Interview Questions

**Q1:** Derive the convergence rate of gradient descent for strongly convex functions.

> For $f$ $\mu$-strongly convex and $L$-smooth with step $\eta = 1/L$:
> $$f(x_{k+1}) - f(x^*) \leq (1 - \frac{\mu}{L}) (f(x_k) - f(x^*))$$
> Linear convergence with rate $1 - 1/\kappa$ where $\kappa = L/\mu$.

**Q2:** Explain why Adam works well in practice.

> Adam combines: (1) momentum for acceleration, (2) adaptive per-parameter learning rates via gradient variance, (3) bias correction for early steps. Robust to hyperparameters, works well with minimal tuning.

**Q3:** What is the learning rate warmup and why is it needed?

> Large LR at start can cause divergence (especially with Adam because early gradient estimates are noisy). Warmup gradually increases LR from 0, allowing stable training. Common in transformer training.

**Q4:** How does weight decay differ from L2 regularization in Adam?

> In SGD, weight decay = L2 regularization ($w_{t+1} = w_t - \eta(\nabla L + \lambda w_t)$). In Adam, L2 adds $\lambda w_t$ to gradient before adaptive scaling, while decoupled weight decay (AdamW) applies decay after adaptive scaling. AdamW generally works better.

### Company Focus

| Company | Focus Area |
|---|---|
| OpenAI | Large-scale optimization for GPT, DALL-E |
| Google (Brain) | Optimizers for TPU training, Adafactor, Lion |
| Meta AI | Distributed optimization, ZeRO, FSDP |
| Microsoft (DeepSpeed) | Memory-efficient optimization |

### Relevance to ML/DL

- Every neural network training loop
- Hyperparameter tuning (LR, batch size, schedule)
- Distributed training optimization
- Mixed precision training

### Code Example

```python
import torch
import torch.nn as nn
import torch.optim as optim

# SGD with momentum
optimizer = optim.SGD(model.parameters(), lr=0.01, momentum=0.9, weight_decay=1e-4)

# AdamW (decoupled weight decay)
optimizer = optim.AdamW(model.parameters(), lr=3e-4, weight_decay=0.01)

# Cosine annealing schedule
scheduler = optim.lr_scheduler.CosineAnnealingLR(optimizer, T_max=100)

# Gradient clipping
torch.nn.utils.clip_grad_norm_(model.parameters(), max_norm=1.0)

# Learning rate warmup (manual)
def get_lr(step, warmup=4000, d_model=512):
    return d_model ** (-0.5) * min(step ** (-0.5), step * warmup ** (-1.5))

# Check convergence (linear rate for strongly convex)
def check_convergence(losses):
    ratios = [losses[i+1] / losses[i] for i in range(len(losses)-5)]
    print(f"Convergence ratio (last 5): {np.mean(ratios[-5:]):.4f}")

print("Optimizer choice significantly impacts training dynamics")
```

---

## 09-regularization-complexity

### Key Interview Questions

**Q1:** Derive the expected test error decomposition (bias-variance).

> See section 4.4 in the main guide. Formal: $\mathbb{E}[(y - \hat{f})^2] = \text{Bias}(\hat{f})^2 + \text{Var}(\hat{f}) + \sigma^2$.

**Q2:** How does dropout act as regularization?

> Dropout randomly zeros neurons with probability $p$. This: (1) creates an ensemble of sub-networks, (2) prevents co-adaptation of features, (3) is equivalent to approximate Bayesian inference. At test time, weights are scaled by $p$ (or inverted dropout).

**Q3:** Explain the double descent phenomenon.

> As model complexity increases, test error first decreases, then increases (classical U-shape), then **decreases again** as model becomes overparameterized. Modern understanding: interpolation threshold (where model just memorizes) is the peak. Beyond it, models generalize.

**Q4:** Compare early stopping, weight decay, and data augmentation as regularizers.

> All reduce effective model capacity: Early stopping limits number of GD steps (controls "optimization budget"). Weight decay penalizes large weights (controls Lipschitz constant). Data augmentation increases effective dataset size (reduces generalization gap).

### Company Focus

| Company | Focus Area |
|---|---|
| Google | Double descent, scaling laws |
| OpenAI | Scaling laws for GPT, Chinchilla |
| Anthropic | Interpretability, sparse autoencoders for regularization |
| DeepMind | Regularization in reinforcement learning |

### Relevance to ML/DL

- Preventing overfitting in all ML pipelines
- Understanding scaling laws (compute, data, parameters)
- Model selection and cross-validation
- Modern regularizers: label smoothing, MixUp, CutMix

### Code Example

```python
import torch
import torch.nn as nn

# Dropout
model = nn.Sequential(
    nn.Linear(784, 256),
    nn.ReLU(),
    nn.Dropout(0.5),  # 50% dropout
    nn.Linear(256, 128),
    nn.ReLU(),
    nn.Dropout(0.3),
    nn.Linear(128, 10)
)
model.train()  # dropout active
model.eval()   # dropout disabled (scaled)

# Label smoothing
def label_smoothing_loss(logits, targets, smoothing=0.1):
    n_classes = logits.size(-1)
    log_probs = torch.log_softmax(logits, dim=-1)
    smooth_targets = (1 - smoothing) * torch.nn.functional.one_hot(targets, n_classes)
    smooth_targets += smoothing / n_classes
    return -(smooth_targets * log_probs).sum(dim=-1).mean()

# MixUp augmentation
def mixup(x1, x2, y1, y2, alpha=1.0):
    lam = torch.distributions.Beta(alpha, alpha).sample()
    x_mixed = lam * x1 + (1 - lam) * x2
    y_mixed = lam * y1 + (1 - lam) * y2  # soft labels
    return x_mixed, y_mixed

# Early stopping
class EarlyStopping:
    def __init__(self, patience=5, min_delta=0):
        self.patience = patience
        self.min_delta = min_delta
        self.counter = 0
        self.best_loss = float('inf')
    def step(self, val_loss):
        if val_loss < self.best_loss - self.min_delta:
            self.best_loss = val_loss
            self.counter = 0
        else:
            self.counter += 1
        return self.counter >= self.patience

print("Regularization is about controlling model capacity, not just reducing parameters")
```

---

## 10-numerical-computing

### Key Interview Questions

**Q1:** Explain numerical stability issues in softmax computation.

> Naive softmax: $e^{z_i} / \sum e^{z_j}$ can overflow for large $z_i$ (e.g., $e^{1000}$). Solution: subtract max: $e^{z_i - \max(z)} / \sum e^{z_j - \max(z)}$. Uses identity $\text{softmax}(z) = \text{softmax}(z - c)$.

**Q2:** What is mixed precision training and why does it work?

> Uses float16 for forward/backward and float32 for master weights. FP16 halves memory, doubles throughput on Tensor Cores. Loss scaling prevents underflow of small gradients. BF16 (bfloat16) has same exponent range as FP32 — more stable.

**Q3:** How do you compute log-sum-exp safely?

> $\log\sum_i e^{z_i} = a + \log\sum_i e^{z_i - a}$ where $a = \max(z)$. This avoids overflow.

**Q4:** What causes NaN / Inf in training and how do you debug?

> Causes: division by zero, log(0), overflow (exp of large values), gradient explosion, learning rate too high. Debug: gradient clipping, gradient norm monitoring, NaN detection hooks, reducing LR.

**Q5:** Explain the numerical issues with computing $(X^T X)^{-1}$ directly.

> If $X$ is ill-conditioned, $X^T X$ squares the condition number, making inversion numerically unstable. Better: use SVD: $\hat{\beta} = V \Sigma^+ U^T y$. Or QR decomposition.

### Company Focus

| Company | Focus Area |
|---|---|
| Nvidia | Tensor Cores, FP8, numerical precision in CUDA |
| Google (Brain) | BF16, TPU numerics |
| AMD | ROCm numerical libraries |
| Apple | ANE, FP16 in Core ML |
| OpenAI | FP8 training for GPT-4 |

### Relevance to ML/DL

- Training stability: gradient scaling, NaN prevention
- Hardware utilization: mixed precision, memory bandwidth
- Reproducibility: deterministic algorithms, floating-point determinism
- Quantization: FP32 → FP16 → INT8 → FP4
- Safe operations: log, exp, division

### Code Example

```python
import torch
import torch.nn as nn
import numpy as np

# Safe softmax
def safe_softmax(logits, dim=-1):
    logits = logits - logits.max(dim=dim, keepdim=True)[0]  # subtract max
    return torch.softmax(logits, dim=dim)

# Log-sum-exp (stable)
def logsumexp_stable(x, dim=-1):
    c = x.max(dim=dim, keepdim=True)[0]
    return c + torch.log(torch.exp(x - c).sum(dim=dim, keepdim=True))

# Mixed precision training
def mixed_precision_step(model, x, y, optimizer, scaler):
    with torch.cuda.amp.autocast():
        logits = model(x)
        loss = nn.CrossEntropyLoss()(logits, y)
    scaler.scale(loss).backward()
    scaler.step(optimizer)
    scaler.update()
    optimizer.zero_grad()
    return loss

# Gradient clipping to avoid explosion
torch.nn.utils.clip_grad_norm_(model.parameters(), max_norm=1.0)

# Debug NaN
def detect_nan_hook(grad):
    if torch.isnan(grad).any():
        print("NaN detected in gradient!")
        return torch.nan_to_num(grad, nan=0.0)

# Solve linear system safely -- never invert directly
# Bad: beta = np.linalg.inv(X.T @ X) @ X.T @ y
# Good:
def safe_linear_solve(X, y):
    return np.linalg.lstsq(X, y, rcond=None)[0]  # uses SVD

# Check condition number
def check_condition(X):
    _, s, _ = np.linalg.svd(X, full_matrices=False)
    cond = s[0] / s[-1]
    print(f"Condition number: {cond:.2f}")
    if cond > 1e12:
        print("WARNING: Ill-conditioned matrix!")
    return cond

print("Numerical stability is critical for reliable ML training")
```

---

## Quick Reference: Micro-Lab Summary

| # | Micro-Lab | Core Topic | Key Companies | Key Skill |
|---|---|---|---|---|
| 01 | vectors-spaces | Dot product, norms, linear independence, orthogonality | OpenAI, Google, Meta | Embedding spaces |
| 02 | linear-transformations | Matrix multiplication, rank, linear layers | Nvidia, Apple, Tesla | NN layer math |
| 03 | eigen-decomposition | Eigenvectors, eigenvalues, PCA, condition number | DeepMind, OpenAI, Microsoft | PCA, optimization |
| 04 | svd-applications | SVD, low-rank approx, matrix factorization | Netflix, Google, Amazon | Recommenders, LoRA |
| 05 | calculus-for-ml | Chain rule, backprop, gradient, Hessian | Meta, Google, OpenAI | Backprop, autograd |
| 06 | probability-distributions | Distributions, reparameterization, likelihoods | OpenAI, DeepMind, Stability AI | VAEs, diffusion |
| 07 | inference-bayes | ELBO, Bayesian inference, MCMC, conjugate priors | DeepMind, Google, Secondmind | UQ, Bayesian DL |
| 08 | optimization-gd | GD, SGD, Adam, convergence, LR schedules | OpenAI, Google, Meta | Training recipes |
| 09 | regularization-complexity | Bias-variance, dropout, double descent, early stopping | Google, OpenAI, Anthropic | Preventing overfitting |
| 10 | numerical-computing | Mixed precision, numerics, safe compute, NaN debugging | Nvidia, Google, AMD | Stable training |

---

*Last updated: July 2026*
