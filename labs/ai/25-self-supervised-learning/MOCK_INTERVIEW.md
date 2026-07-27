# Mock Interview: Self-Supervised Learning

**Topic:** Explain contrastive learning, SimCLR, and self-supervised pretraining

## Core Questions

### Q1: What is self-supervised learning?

**Answer:**
SSL learns representations from unlabeled data by designing a pretext task that produces supervision signals from the data itself.

**Paradigm:** Pretrain on unlabeled data → Finetune on labeled downstream task.

**Types of pretext tasks:**
- **Contrastive:** Pull positive pairs together, push negatives apart
- **Generative:** Predict masked parts of input (MAE, BERT)
- **Predictive:** Predict rotation, jigsaw puzzles, relative patch position

### Q2: Explain contrastive learning and SimCLR.

**Answer:**
**Core idea:** Learn an embedding space where similar (positive) pairs are close, dissimilar (negative) pairs are far apart.

**SimCLR framework:**
1. Take a batch of $N$ images
2. Apply two random augmentations to each → $2N$ augmented views
3. Encode with ResNet → projection head → 128-dim embeddings
4. **Loss (NT-Xent / InfoNCE):**

$\mathcal{L}_{i,j} = -\log \frac{\exp(\text{sim}(z_i, z_j)/\tau)}{\sum_{k=1}^{2N} \mathbb{1}_{k \ne i} \exp(\text{sim}(z_i, z_k)/\tau)}$

Where $\text{sim}(u, v) = u^T v / (\|u\|\|v\|)$ (cosine similarity).

**Key components:**
- **Data augmentation:** Random crop, color jitter, Gaussian blur, horizontal flip — crucial for good representations
- **Temperature $\tau$:** Controls sharpness of distribution; lower $\tau$ focuses on hard negatives
- **Projection head:** MLP before contrastive loss significantly improves downstream performance
- **Large batch size:** $N > 256$ to have enough negatives

### Q3: What makes contrastive learning work?

**Answer:**
**Alignment + Uniformity:**
- **Alignment:** $\mathbb{E}_{(x,x^+) \sim p_\text{pos}} \|f(x) - f(x^+)\|^2$ — positive pairs should be close
- **Uniformity:** $\log \mathbb{E}_{x,y \sim p_\text{data}} e^{-t\|f(x)-f(y)\|^2}$ — embeddings should be spread on hypersphere

Contrastive loss optimizes both simultaneously.

**Theoretical connection:** InfoNCE maximizes a lower bound on mutual information $I(X; T)$ between data and representations.

### Q4: Compare SSL methods.

| Method | Type | Key Idea | Pros | Cons |
|--------|------|----------|------|------|
| **SimCLR** | Contrastive | Aug + NT-Xent | Simple, effective | Needs large batch, many negatives |
| **MoCo** | Contrastive | Momentum encoder + queue | Memory efficient | Momentum tuning |
| **BYOL** | Non-contrastive | Bootstrap, no negatives | No negative mining | Collapse risk |
| **SimSiam** | Non-contrastive | Stop-gradient, predictor | Simplest | Sensitive to augmentation |
| **MAE** | Generative | Mask random patches, reconstruct | Scales well | Longer training |
| **Barlow Twins** | Redundancy reduction | Cross-correlation → identity | No negatives | Needs careful tuning |

### Q5: When to use SSL vs. supervised pretraining?

**Answer:**
**Use SSL when:**
- Lots of unlabeled data, limited labeled data
- Domain-specific data (medical, satellite, etc.) without labels
- Learning general-purpose representations
- Downstream task has few labels

**SSL limitations:**
- Pretraining is computationally expensive
- Needs careful augmentation design
- Benefits diminish with enough labeled data
- Can underperform supervised pretraining on in-distribution tasks

## Advanced

- **CURL:** Contrastive learning for RL — augments consecutive frames as positive pairs
- **CLIP:** Contrastive language-image pretraining — uses text as natural supervision
- **CIFAR10 accuracy with SimCLR:** ~85% linear probe (vs. ~95% supervised), but uses much less label data
- **VICReg:** Variance-Invariance-Covariance Regularization — explicitly enforces variance and decorrelation
