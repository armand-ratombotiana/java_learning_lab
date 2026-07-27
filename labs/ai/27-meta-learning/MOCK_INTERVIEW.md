# Mock Interview: Meta-Learning

**Topic:** Explain MAML and few-shot learning

## Core Questions

### Q1: What is meta-learning (learning to learn)?

**Answer:**
Meta-learning trains a model on a distribution of tasks so it can quickly adapt to new tasks with few examples.

**Key idea:** Learn the **learning algorithm itself** — the model learns to generalize from small amounts of data by leveraging experience from related tasks.

**Formulation:**
- Task distribution $p(\mathcal{T})$
- Each task $\mathcal{T}_i$ has support set $D_i^{\text{tr}}$ (few-shot) and query set $D_i^{\text{test}}$
- Meta-objective: $\min_\theta \mathbb{E}_{\mathcal{T} \sim p(\mathcal{T})} [\mathcal{L}(D^{\text{test}} ; \text{Adapt}(\theta, D^{\text{tr}}))]$

### Q2: Explain MAML (Model-Agnostic Meta-Learning).

**Answer:**
MAML learns an initialization $\theta$ such that a few gradient steps on a new task yields good performance.

**Algorithm:**

```
Initialize θ randomly
Loop (meta-iterations):
  Sample batch of tasks {T_i}
  For each task T_i:
    Compute adapted params: θ_i' = θ - α ∇_θ L(D_tr_i; θ)
    (can be multiple gradient steps)
  Meta-update: θ = θ - β ∇_θ Σ L(D_test_i; θ_i')
```

**Key insight:** MAML optimizes for **fast adaptation** — the gradient update must be computed through the inner loop, requiring Hessian-vector products (second-order gradients).

**First-order MAML (FOMAML):** Ignore second-order terms — often works almost as well (Reptile).

### Q3: What is few-shot learning?

**Answer:**
**$N$-way $K$-shot classification:** Classify among $N$ classes, with $K$ labeled examples per class.

**Standard splits:**
- 5-way 1-shot: 5 test classes, 1 example each for training
- 5-way 5-shot: 5 classes, 5 examples each

**Episodic training:** Sample episodes (tasks) from base classes, each with support and query set. Train on episodes, test on novel classes.

### Q4: Compare meta-learning approaches.

| Category | Method | Key Idea | Pros | Cons |
|----------|--------|----------|------|------|
| **Optimization-based** | MAML | Learn initialization for fast gradient adaptation | Works with any model, principled | Second-order costly, inner loop instability |
| **Optimization-based** | Reptile | First-order approximation of MAML | Simpler, faster | Less theoretical grounding |
| **Metric-based** | Prototypical Networks | Learn embedding; classify by distance to class prototypes | Simple, effective | Limited to classification |
| **Metric-based** | Siamese Networks | Learn similarity function | Works for verification | Pairwise training expensive |
| **Metric-based** | Relation Networks | Learn non-linear distance metric | More expressive | Overfitting |
| **Model-based** | Memory-Augmented NN | Store examples in external memory | Can handle complex tasks | Memory bound |
| **Black-box** | LSTM Meta-Learner | RNN reads/generates gradients | Flexible | Hard to train |

### Q5: Derive the MAML gradient update.

**Answer:**
Inner loop (one gradient step): $\theta_i' = \theta - \alpha \nabla_\theta \mathcal{L}_i^{\text{tr}}(\theta)$

Meta-objective: $\min_\theta \sum_i \mathcal{L}_i^{\text{test}}(\theta_i') = \sum_i \mathcal{L}_i^{\text{test}}(\theta - \alpha \nabla_\theta \mathcal{L}_i^{\text{tr}}(\theta))$

Meta-gradient:
$\nabla_\theta \mathcal{L}_i^{\text{test}}(\theta_i') = \nabla_{\theta_i'} \mathcal{L}_i^{\text{test}}(\theta_i') \cdot \nabla_\theta \theta_i'$

$\nabla_\theta \theta_i' = \underbrace{I}_{\text{first-order}} - \underbrace{\alpha \nabla_\theta^2 \mathcal{L}_i^{\text{tr}}(\theta)}_{\text{second-order}}$

For multiple inner steps, chain rule through all updates.

### Q6: When does MAML work well vs. poorly?

**Answer:**
**Works well:**
- Tasks share common structure (e.g., all sinusoid fitting with different phases)
- Few-shot regime (1-10 examples)
- Rapid adaptation is critical
- Tasks are diverse enough to learn useful prior

**Fails when:**
- Tasks are too dissimilar (meta-overfitting)
- Inner loop optimization is unstable (unbalanced data, poor LR)
- Distribution shift between meta-training and meta-test tasks
- Very small base dataset (cannot learn good initialization)

## Advanced

- **ANIL (Almost No Inner Loop):** Only adapt last layer(s), freeze feature extractor — matches MAML often
- **CAVIA (Context Adaptation):** Adapt only context parameters, keep shared parameters fixed
- **Meta-learning for RL:** MAML learns policy initialization that adapts to new environments quickly
- **Bayesian MAML:** Learn distribution over initializations (variational inference)
- **Cross-domain few-shot:** Meta-test classes come from different domain than meta-train (e.g., natural images → medical images)
