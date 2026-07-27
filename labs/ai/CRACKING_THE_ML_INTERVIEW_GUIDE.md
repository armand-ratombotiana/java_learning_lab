# Cracking the ML Interview Guide

Complete interview preparation covering ML fundamentals, coding, system design, research, statistics, and role-specific strategies.

---

## Part 1: ML Fundamentals - 30 Essential Concepts

### 1. Bias-Variance Trade-off

**Definition**: Error decomposition into bias (error from incorrect assumptions) and variance (error from sensitivity to training data).

**Total Error = Bias^2 + Variance + Irreducible Error**

| Model | Bias | Variance | Typical Performance |
|-------|------|----------|-------------------|
| Linear Regression | High | Low | Underfits complex problems |
| Decision Trees | Low | High | Overfits without pruning |
| Random Forest | Medium | Medium | Good balance |
| Neural Networks | Very Low | Very High | Needs regularization |

**Interview Question**: "How do you diagnose and fix high bias vs high variance?"
- **High Bias**: Underfitting → Increase model complexity, add features, reduce regularization
- **High Variance**: Overfitting → More data, reduce features, increase regularization, early stopping

### 2. Regularization

**L1 (Lasso)**: `Loss + lambda * sum(|w|)` - Produces sparse weights (feature selection)
**L2 (Ridge)**: `Loss + lambda * sum(w^2)` - Shrinks weights but doesn't zero them
**Elastic Net**: Combination of L1 + L2

**Why L1 produces sparsity**: The constraint region has corners on axes, probability of hitting a corner increases with dimension.

### 3. Loss Functions

| Problem | Loss Function | Formula | Properties |
|---------|--------------|---------|------------|
| Regression | MSE | (y - y_hat)^2 | Sensitive to outliers |
| Regression | MAE | |y - y_hat| | Robust to outliers |
| Regression | Huber | Squared for small, Linear for large | Best of both |
| Binary classification | Binary Cross-Entropy | -(y log(p) + (1-y) log(1-p)) | Probabilistic interpretation |
| Multi-class | Categorical Cross-Entropy | -sum(y_i log(p_i)) | Softmax + CE |
| Ranking | Pairwise Hinge | max(0, margin - (s_pos - s_neg)) | Ranking margin |
| Contrastive | Contrastive Loss | y*d^2 + (1-y)*max(0, m-d)^2 | Siamese networks |

### 4. Gradient Descent

**Algorithm**:
```
w_{t+1} = w_t - lr * gradient(L(w_t))
```

**Variants**:
| Variant | Update Rule | Pros | Cons |
|---------|-------------|------|------|
| Batch GD | Full dataset gradient | Stable, guaranteed convergence | Slow for large datasets |
| SGD | Single sample | Fast updates, escapes local minima | Noisy, may not converge |
| Mini-batch SGD | Small batch | Balance of speed + stability | Learning rate tuning needed |
| Momentum | v = beta*v + grad; w -= lr*v | Faster, dampens oscillations | Extra hyperparameter |
| Adam | Adaptive lr per parameter | Works well out of box | May not generalize as well as SGD |

### 5. Backpropagation

**Chain Rule in Neural Networks**:
```
dL/dw = dL/dy * dy/dz * dz/dw
```

**Steps**:
1. Forward pass: Compute output
2. Compute loss
3. Backward pass: Compute gradients (output→input)
4. Update weights

**Common gradient issues**:
- **Vanishing gradients**: Sigmoid/tanh saturate → use ReLU, batch norm, residual connections
- **Exploding gradients**: Large weight updates → gradient clipping, proper initialization

### 6. Attention Mechanism

**Scaled Dot-Product Attention**:
```
Attention(Q, K, V) = softmax(QK^T / sqrt(d_k)) * V
```

**Why scale by sqrt(d_k)?**: Prevents dot products from growing large, which would push softmax into regions with extremely small gradients.

**Multi-Head Attention**: Project Q/K/V into h subspaces, compute attention in each, concatenate and project.

### 7. Transformers

**Architecture Components**:
1. **Self-Attention**: Each token attends to all tokens
2. **Feed-Forward Network**: Two linear layers with ReLU/GELU
3. **Layer Normalization**: Normalize across feature dimension
4. **Residual Connections**: Skip connections for gradient flow
5. **Positional Encoding**: Sinusoidal or learned position information

**Key Innovations**:
- Parallelizable (unlike RNNs)
- O(n^2) complexity in sequence length
- Pre-norm vs Post-norm architecture
- Causal masking for autoregressive generation

### 8. Convolutional Neural Networks (CNNs)

**Key Operations**:
- **Convolution**: Filter slides over input, computing dot products
- **Pooling**: Downsampling (max, average)
- **Stride**: Step size of filter movement
- **Padding**: Adding border to preserve dimensions

**Properties**:
- Translation invariance
- Local connectivity
- Parameter sharing
- Hierarchical feature learning

### 9. Recurrent Neural Networks (RNNs)

**Vanishing Gradient Problem**: Gradients in long sequences become very small through repeated multiplication by W < 1.

**LSTM**:
- Forget gate: What to discard from cell state
- Input gate: What new info to store
- Output gate: What to output from cell state
- Cell state: Long-term memory (carries gradient better)

**GRU**: Simplified LSTM (merge forget + input gates)

### 10. Ensemble Methods

**Bagging** (Bootstrap Aggregating):
- Train models on bootstrap samples
- Average predictions (regression) or majority vote (classification)
- Reduces variance (Random Forest)

**Boosting**:
- Sequentially train models focusing on previous errors
- AdaBoost: Increase weights on misclassified samples
- Gradient Boosting: Fit new model to residuals (XGBoost, LightGBM, CatBoost)

**Stacking**: Train meta-model on predictions of base models

### 11. Dimensionality Reduction

**PCA (Principal Component Analysis)**:
1. Center the data
2. Compute covariance matrix
3. Eigendecomposition
4. Select top k eigenvectors

**t-SNE**: Non-linear, preserves local structure, good for visualization
**UMAP**: Faster than t-SNE, better global structure preservation

### 12. Clustering

**K-Means**: Minimizes within-cluster variance. Iterative: assign→update→repeat.
**DBSCAN**: Density-based, handles arbitrary shapes, detects noise. Parameters: epsilon, minPts.
**Hierarchical**: Agglomerative (bottom-up) or divisive (top-down). Dendrogram visualization.

### 13. Model Evaluation

**Classification Metrics**:
- Accuracy: (TP+TN)/(TP+TN+FP+FN) — sensitive to class imbalance
- Precision: TP/(TP+FP) — "how many positive predictions were correct"
- Recall: TP/(TP+FN) — "how many actual positives were found"
- F1: 2*P*R/(P+R) — harmonic mean of precision and recall
- ROC-AUC: Probability that positive ranks higher than negative
- PR-AUC: Better for imbalanced datasets

**Regression Metrics**:
- MSE, RMSE, MAE, MAPE, R-squared, Adjusted R-squared

**Probabilistic Metrics**:
- Log Loss, Brier Score, Calibration Error

### 14. Cross-Validation

| Type | Use Case | Pros | Cons |
|------|----------|------|------|
| K-fold | General purpose | Low bias | Non-independent folds |
| Stratified K-fold | Imbalanced data | Preserves class distribution | Requires labels |
| Group K-fold | Non-i.i.d. data | Keeps groups together | Smaller training sets |
| Time Series CV | Temporal data | Respects time order | No future→past leakage |
| Leave-One-Out | Very small data | Maximum training data | Expensive for large n |

### 15. Imbalanced Data

**Techniques**:
1. **Resampling**: Oversample minority (SMOTE), undersample majority
2. **Cost-sensitive learning**: Higher penalty for minority errors
3. **Ensemble methods**: Balanced Random Forest, EasyEnsemble
4. **Anomaly detection approach**: Treat minority as anomalies
5. **Synthetic data**: GANs, autoencoders for minority class
6. **Loss function modification**: Focal loss, weighted loss

### 16. Feature Scaling

| Method | Formula | When to Use |
|--------|---------|-------------|
| Standardization | (x - mean)/std | Data not bounded, outliers handled |
| Min-Max | (x - min)/(max - min) | Bounded data, no outliers |
| Robust | (x - median)/IQR | Outliers present |
| Unit Vector | x/||x|| | Cosine similarity important |

**Why scale matters**: Gradient descent converges faster, distance-based algorithms (KNN, SVM) are sensitive, regularization treats features equally.

### 17. Activation Functions

| Function | Formula | Range | Pros | Cons |
|----------|---------|-------|------|------|
| Sigmoid | 1/(1+e^-x) | (0,1) | Probabilistic output | Vanishing gradient, not zero-centered |
| Tanh | (e^x-e^-x)/(e^x+e^-x) | (-1,1) | Zero-centered | Vanishing gradient |
| ReLU | max(0,x) | [0,∞) | No vanishing gradient, fast | Dying ReLU |
| Leaky ReLU | max(0.01x,x) | (-∞,∞) | Fixes dying ReLU | Extra parameter |
| ELU | x if x>0, a(e^x-1) if x≤0 | (-a,∞) | Smooth, negative values | More compute |
| GELU | x * Phi(x) | (-∞,∞) | Used in transformers | Complex |
| Swish/SiLU | x * sigmoid(x) | (-∞,∞) | Smooth, self-gated | More compute |

### 18. Normalization Layers

| Layer | Normalizes Across | When to Use |
|-------|-------------------|-------------|
| Batch Norm | Batch dimension | CNNs, higher batch sizes |
| Layer Norm | Feature dimension | Transformers, RNNs |
| Instance Norm | Single sample | Style transfer |
| Group Norm | Groups of channels | Small batch sizes (detection) |

### 19. Weight Initialization

| Method | Distribution | Used With |
|--------|-------------|-----------|
| Xavier/Glorot | U[-sqrt(6/(in+out)), sqrt(6/(in+out))] | Sigmoid, Tanh |
| He | N(0, sqrt(2/in)) | ReLU |
| LeCun | N(0, sqrt(1/in)) | SELU |
| Orthogonal | Orthogonal matrix | RNNs, LSTMs |

### 20. Hyperparameter Tuning

| Method | Description | Budget Required |
|--------|-------------|-----------------|
| Grid Search | Exhaustive over parameter grid | Very high |
| Random Search | Random sampling from distribution | Medium |
| Bayesian Opt | Gaussian process + acquisition function | Low-Medium |
| Hyperband | Successive halving with adaptive budgets | Very low |
| Population-based | Evolutionary search | Low |

### 21. Transfer Learning

**Strategies**:
1. **Feature Extraction**: Freeze pre-trained weights, train new classifier
2. **Fine-tuning**: Unfreeze some/all layers, train with small learning rate
3. **Progressive unfreezing**: Gradually unfreeze layers from top

**When to use**:
- Small dataset
- Similar domain to pre-training
- Limited compute

### 22. Data Augmentation

**Image**: Rotation, flip, crop, color jitter, cutout, mixup, CutMix
**Text**: Synonym replacement, back-translation, random insertion, EDA
**Audio**: SpecAugment, noise addition, time stretch
**Tabular**: SMOTE, noise injection, feature-wise augmentation

### 23. Model Compression

| Technique | Compression Ratio | Quality Loss | Use Case |
|-----------|------------------|--------------|----------|
| Pruning | 2-10x | Low-Medium | Reduce parameters |
| Quantization | 2-4x | Low | Reduce precision |
| Distillation | Variable | Low | Smaller student model |
| Weight sharing | 2-5x | Medium | K-means on weights |
| Low-rank factorization | 2-3x | Low | Decompose weight matrices |

### 24. Few-Shot Learning

**Approaches**:
1. **Metric-based**: Siamese networks, Prototypical networks
2. **Model-based**: MAML (Model-Agnostic Meta-Learning)
3. **Data augmentation**: Synthetic data generation
4. **Transfer learning**: Pre-train, fine-tune on few examples
5. **In-context learning**: LLMs, prompt engineering

### 25. Reinforcement Learning

**Key Concepts**:
- Agent, Environment, State, Action, Reward
- Policy (π): Maps state to action
- Value function (V): Expected return from state
- Q-function: Expected return from state-action pair

**Algorithms**:
| Type | Algorithm | Description |
|------|-----------|-------------|
| Value-based | DQN | Learn Q-function, derive policy |
| Policy-based | REINFORCE, PPO | Directly learn policy |
| Actor-Critic | A2C, SAC | Learn both policy + value |
| Model-based | MuZero | Learn environment model |

### 26. Generative Models

**VAE**: Encoder→Latent→Decoder. KL divergence + reconstruction loss.
**GAN**: Generator (creates fake) vs Discriminator (detects fake). Min-max game.
**Diffusion**: Forward process (add noise) → Reverse process (denoise). U-Net architecture.
**Autoregressive**: GPT, PixelCNN. Generate token by token conditioning on previous.

### 27. Optimization Beyond SGD

**Learning Rate Schedules**:
- Step decay: Reduce by factor every N epochs
- Cosine annealing: Smooth cosine decay, with restarts (SGDR)
- Warm-up: Linear increase, then decay
- Cyclical: Oscillate between bounds

**Second-order methods**:
- Newton's method: Uses Hessian (too expensive)
- Quasi-Newton: L-BFGS approximates Hessian
- Natural gradient: KL-divergence based

### 28. Model Interpretability

| Method | Type | Output | Use Case |
|--------|------|--------|----------|
| Feature Importance | Global | Feature ranking | Tabular models |
| SHAP | Local+Global | Shapley values | Model-agnostic |
| LIME | Local | Linear approximation | Interpretable local |
| Integrated Gradients | Local | Attribution scores | Deep learning |
| Grad-CAM | Local | Heatmap | CNN visualizations |
| Attention Weights | Local | Token importance | Transformers |
| Partial Dependence | Global | Feature effect | Understanding relationships |

### 29. MLOps & Production ML

**Key Components**:
- **Pipeline**: Data→Feature→Train→Evaluate→Deploy→Monitor
- **Reproducibility**: Version control for data, code, model, environment
- **Monitoring**: Data drift, concept drift, performance degradation
- **CI/CD**: Automated training, testing, deployment
- **Governance**: Model registry, audit trail, compliance

**Common Issues**:
- Training-serving skew
- Data drift over time
- Concept drift
- Pipeline failures
- Resource contention

### 30. Ethical AI

**Key Considerations**:
- **Fairness**: Demographic parity, equal opportunity, equalized odds
- **Accountability**: Who is responsible for model decisions?
- **Transparency**: Interpretable models, documentation, explainability
- **Privacy**: Data minimization, differential privacy, federated learning
- **Robustness**: Adversarial robustness, distribution shift
- **Safety**: Alignment, containment, monitoring

---

## Part 2: ML Coding - NumPy from Scratch

### Essential Implementations to Practice

1. **Linear Regression** (closed form + gradient descent)
2. **Logistic Regression** (binary + multinomial)
3. **K-Means Clustering** (with K-means++ initialization)
4. **K-Nearest Neighbors** (brute force + KD-tree)
5. **Decision Tree** (classification + regression)
6. **PCA** (eigendecomposition + SVD approach)
7. **Neural Network** (MLP with forward/backward)
8. **CNN Convolution** (naive + im2col)
9. **RNN/LSTM Cell** (forward pass)
10. **Transformer Attention** (scaled dot-product + multi-head)
11. **Word2Vec Skip-gram** (negative sampling)
12. **Naive Bayes** (Gaussian + Multinomial)
13. **Gradient Descent** (SGD, Momentum, Adam, RMSProp)
14. **Evaluation Metrics** (accuracy, precision, recall, F1, AUC)
15. **Cross-Validation** (K-fold, stratified, temporal)

### Coding Interview Patterns

```
Pattern 1: "Implement from scratch, then optimize"
  - Start with naive implementation
  - Add vectorization (numpy)
  - Discuss further optimization (parallel, GPU)

Pattern 2: "Implement and analyze"
  - Working implementation
  - Time/space complexity
  - Compare with alternatives

Pattern 3: "Fix this broken implementation"
  - Debug existing code
  - Identify bugs
  - Add test cases

Pattern 4: "Design and implement a solution"
  - Clarify requirements
  - Design architecture
  - Implement key components
```

---

## Part 3: ML System Design - 10 Common Problems

### Problems to Master

| # | Problem | Key Concepts |
|---|---------|--------------|
| 1 | Recommendation System | Two-tower, candidate generation, ranking, re-ranking |
| 2 | Search Ranking | Query understanding, retrieval, ranking, personalization |
| 3 | Fraud Detection | Real-time features, GBDT, online learning, graph features |
| 4 | Content Moderation | Multi-modal, tiered pipeline, active learning |
| 5 | Personalization Engine | User profiling, contextual bandits, session models |
| 6 | Ad Prediction System | CTR prediction, real-time bidding, budget pacing |
| 7 | LLM Serving | vLLM, KV cache, quantization, continuous batching |
| 8 | RAG System | Retrieval, embedding, reranking, generation |
| 9 | Model Training Platform | Data pipeline, distributed training, experiment tracking |
| 10 | Model Monitoring System | Drift detection, alerting, dashboard, auto-remediation |

### Design Framework

**Step 1: Requirements Clarification (5 min)**
- What is the problem? (classification, ranking, generation, etc.)
- Scale: data volume, QPS, number of users/models
- Latency requirements (p50, p95, p99)
- Accuracy requirements
- Constraints: budget, team size, infrastructure

**Step 2: Data Pipeline (5 min)**
- Data sources: batch, streaming, both
- Feature engineering: offline + online features
- Feature store: Feast, Tecton, custom
- Data validation: quality checks, schema

**Step 3: Model Architecture (10 min)**
- Model selection: based on data size, latency, problem type
- Training: loss function, optimizer, regularization
- Evaluation: offline metrics, validation strategy
- Experimentation: A/B testing framework

**Step 4: Serving Infrastructure (10 min)**
- Deployment: batch vs real-time
- Model servers: TF Serving, TorchServe, Triton
- Scaling: horizontal, caching, CDN
- Monitoring: metrics, alerts, dashboards

**Step 5: Iteration and Maintenance (5 min)**
- Retraining strategy: scheduled, trigger-based, continuous
- Model updates: A/B test, canary, rollback
- Feedback loop: data collection for improvement

---

## Part 4: Research - How to Present Papers

### Paper Presentation Structure

1. **Problem Statement (2 min)**
   - What problem does the paper address?
   - Why is it important?
   - What were previous approaches and their limitations?

2. **Method (5 min)**
   - Core idea/intuition
   - Mathematical formulation (key equations)
   - Architecture diagram
   - Training/inference procedure
   - Key design decisions and rationale

3. **Experiments (3 min)**
   - Datasets and metrics
   - Baselines compared
   - Ablation studies
   - Key results (table with best numbers)

4. **Analysis (3 min)**
   - Strengths of the approach
   - Limitations and failure cases
   - Reproducibility considerations
   - How would you improve it?

5. **Discussion (2 min)**
   - Impact on field
   - Connections to your work
   - Open questions

### What Interviewers Look For

- **Depth**: Do you understand the math and implementation?
- **Critical thinking**: Can you identify strengths and weaknesses?
- **Creativity**: Can you suggest extensions or improvements?
- **Communication**: Can you explain complex ideas clearly?
- **Connections**: Can you relate the paper to broader ML concepts?

### Expected Paper Preparation

**For Research Roles**: Be ready to deeply discuss 3-5 papers
- Your own publications (if any)
- Recent influential papers (from the last 1-2 years)
- Classic papers in your area

**For Applied Roles**: 
- 2-3 projects with technical depth
- Related work discussion
- Architecture decisions and trade-offs

---

## Part 5: Statistics and Probability

### Key Concepts

1. **Probability Distributions**
   - Normal, Bernoulli, Binomial, Poisson, Exponential, Beta, Dirichlet
   - Know: PDF/PMF, mean, variance, MLE estimation

2. **Bayesian Thinking**
   - P(A|B) = P(B|A)P(A)/P(B)
   - Prior → Likelihood → Posterior
   - Conjugate priors
   - MAP vs MLE estimation

3. **Hypothesis Testing**
   - Null vs Alternative hypothesis
   - Type I error (false positive) vs Type II error (false negative)
   - p-value interpretation (NOT probability that null is true)
   - Confidence intervals
   - Statistical power

4. **Sampling Methods**
   - Simple random, stratified, cluster
   - Reservoir sampling
   - Importance sampling
   - MCMC (Metropolis-Hastings, Gibbs)

5. **Information Theory**
   - Entropy: H(X) = -sum(p(x) log p(x))
   - Cross-entropy: H(p,q) = -sum(p(x) log q(x))
   - KL Divergence: D_KL(p||q) = sum(p(x) log(p(x)/q(x)))
   - Mutual Information: I(X;Y) = H(X) - H(X|Y)

### Common Interview Questions

```
1. You flip a fair coin 3 times. What's the probability of at least 2 heads?
   Answer: 4/8 = 0.5 (HHH, HHT, HTH, THH)

2. Given a biased coin with P(H) = p, how many flips to estimate p within 0.01?
   Answer: Use normal approximation: n = (z*sigma/ME)^2

3. Two teams play best-of-7. Team A has P(win each game) = 0.6. P(A wins series)?
   Answer: Binomial: P(>=4 wins in 7 games)

4. You have two features with correlation 0.9. What issues might arise?
   Answer: Multicollinearity - unstable coefficient estimates, interpretability issues

5. How do you test if a new model is significantly better than the old one?
   Answer: Paired t-test or McNemar's test on matched predictions
```

---

## Part 6: Per-Role Differences

### ML Engineer vs Applied Scientist vs Research Scientist

| Dimension | ML Engineer | Applied Scientist | Research Scientist |
|-----------|-------------|-------------------|-------------------|
| **Focus** | Production systems | Applied ML research | Fundamental research |
| **Coding** | LeetCode heavy | ML coding + algorithms | Implementation + experiments |
| **ML Depth** | Practical, tools | Deep theoretical | Cutting-edge, new methods |
| **System Design** | High (training + serving) | Medium (ML pipeline) | Low (research infra) |
| **Research** | Read applied papers | Adapt research to product | Publish at top venues |
| **Math** | Linear algebra, statistics | Calculus, probability, stats | Advanced math, proofs |
| **PhD Required** | No | Often preferred | Usually required |
| **Publications** | Nice to have | Expected | Required |
| **Data Skills** | Data pipelines, SQL | Data analysis, visualization | Data generation & labeling |
| **Production** | Deployment, monitoring, SRE | Model validation, A/B tests | Prototype, proof-of-concept |
| **Key Interview** | Coding, System Design | ML Design, Research | Research, Math |

### Interview Weight by Role

```
ML Engineer:
  Coding: 40%  |  ML Fundamentals: 25%  |  System Design: 25%  |  Behavioral: 10%

Applied Scientist:
  ML Fundamentals: 35%  |  Research: 25%  |  Coding: 20%  |  System Design: 15%  |  Behavioral: 5%

Research Scientist:
  Research: 45%  |  ML Fundamentals: 25%  |  Math: 15%  |  Coding: 10%  |  Behavioral: 5%
```

### Preparation Strategy by Role

**ML Engineer**:
- Focus on: LeetCode, ML system design, coding ML from scratch
- Prepare: Production ML experience stories
- Study: MLOps, feature stores, model serving
- Practice: End-to-end ML pipeline design

**Applied Scientist**:
- Focus on: ML fundamentals depth, paper discussion, system design
- Prepare: Publications research, project presentations
- Study: Recent SOTA techniques in your domain
- Practice: Explaining complex ML concepts simply

**Research Scientist**:
- Focus on: Research presentation, mathematical depth, implementation
- Prepare: Deep understanding of 3+ papers
- Study: Advanced math (information theory, optimization theory)
- Practice: Deriving equations, proving properties

---

## Part 7: Interview Day Checklist

### Before the Interview

- [ ] Research the company's recent AI/ML work
- [ ] Review 3-5 relevant papers from the team
- [ ] Prepare 3 project stories (STAR format)
- [ ] Practice 5 ML coding problems
- [ ] Review ML fundamentals (concept cards)
- [ ] Test your coding environment (camera, mic, screen share)
- [ ] Prepare questions to ask interviewers

### During the Interview

- [ ] Listen carefully and ask clarifying questions
- [ ] Think out loud (communicate your thought process)
- [ ] Start with simple solution, then optimize
- [ ] Test your code with examples
- [ ] Discuss alternatives and trade-offs
- [ ] Be honest about what you don't know
- [ ] Take notes on feedback

### After the Interview

- [ ] Send thank-you notes
- [ ] Note down questions you struggled with
- [ ] Reflect on what you'd do differently
- [ ] Continue practicing for next round

---

## Part 8: Quick Reference Formulas

### Core ML Formulas

```python
# Linear Regression (Closed Form)
w = (X^T X)^{-1} X^T y

# Logistic Regression
P(y=1|x) = 1 / (1 + e^{-w^T x})

# Softmax
P(y=k|x) = e^{w_k^T x} / sum_j e^{w_j^T x}

# Cross-Entropy Loss
L = -1/n * sum(y_i * log(p_i) + (1-y_i)*log(1-p_i))

# SVM (Primal)
min ||w||^2/2 + C * sum(max(0, 1 - y_i(w^T x_i + b)))

# K-Means Objective
min sum_k sum_{x in C_k} ||x - mu_k||^2

# PCA Objective
max_ w Var(Xw) subject to ||w|| = 1

# Attention
Attention(Q,K,V) = softmax(QK^T / sqrt(d_k)) V

# Layer Norm
y = gamma * (x - mean(x)) / sqrt(var(x) + eps) + beta

# Adam Update
m_t = beta1 * m_{t-1} + (1-beta1) * g_t
v_t = beta2 * v_{t-1} + (1-beta2) * g_t^2
w_{t+1} = w_t - lr * m_t / (sqrt(v_t) + eps) * sqrt(1-beta2^t)/(1-beta1^t)
```

### Statistics Formulas

```python
# Bayes' Theorem
P(A|B) = P(B|A) * P(A) / P(B)

# Normal Distribution
f(x) = 1 / (sigma * sqrt(2*pi)) * exp(-(x-mu)^2 / (2*sigma^2))

# Bernoulli Distribution
P(X=1) = p, P(X=0) = 1-p, E[X] = p, Var(X) = p(1-p)

# KL Divergence
D_KL(P||Q) = sum P(x) * log(P(x)/Q(x))

# Confidence Interval (mean)
CI = x_bar +/- z * sigma / sqrt(n)
```
