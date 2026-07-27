# Meta AI Interview Guide

Interview preparation for ML/AI roles at Meta (including FAIR, Llama, GenAI, and product ML teams).

---

## 1. Role Types at Meta AI

### ML Engineer (SWE, ML)
- Applied ML on Facebook, Instagram, WhatsApp, Messenger
- Focus: ranking, recommendation, ads, integrity, content understanding
- Strong both in ML and software engineering
- Coding bar is very high (LeetCode hard)

### Research Scientist (FAIR - Fundamental AI Research)
- Publish at top ML venues (NeurIPS, ICML, ICLR, CVPR)
- Open source contributions (PyTorch, Llama, Detectron2)
- PhD or equivalent publication record
- Areas: NLP, vision, speech, RL, multimodal

### Applied Research Scientist
- Combine research with product impact
- Bridge between FAIR and product teams
- PhD + strong engineering skills

### ML Infrastructure Engineer
- Core ML frameworks: PyTorch, ONNX, Caffe2
- Distributed training infrastructure
- GPU cluster scheduling and optimization

---

## 2. Interview Process

### Process Timeline

| Step | Duration | Format |
|------|----------|--------|
| Recruiter Screen | 15-20 min | Phone |
| Technical Screen | 45 min | Coding + ML basics |
| Virtual On-site | 4-5 hours | 4-5 rounds |
| Team Fit / Wrap-up | 45 min | Cross-functional chat |
| Offer | - | Negotiation |

### Round Breakdown

| Round | Duration | Focus |
|-------|----------|-------|
| ML System Design | 45 min | Large-scale ML systems |
| Coding 1 | 45 min | LeetCode (medium) |
| Coding 2 | 45 min | LeetCode (medium-hard) |
| ML Coding | 45 min | ML implementation |
| Behavioral / ML Deep Dive | 45 min | Project deep dive |

---

## 3. ML System Design Round

### Meta-Specific Systems

| System | Unique Considerations | Scale |
|--------|----------------------|-------|
| News Feed Ranking | Real-time, multi-objective, billions of users | 2B+ DAU |
| Instagram Explore | Visual content, discovery, virality | 1B+ users |
| Ads Delivery & Auction | Real-time bidding, budget pacing, relevance | Billions/day |
| Friend Suggestions | Social graph, PagaRank, embeddings | 3B+ users |
| Marketplace Recommendations | Two-sided marketplace, trust | 1B+ users |
| Content Integrity | Misinformation, hate speech, bullying | Billions/day |
| Reels Recommendation | Short-form video, trends, engagement | 1B+ users |
| Groups Recommendations | Community detection, interest modeling | 1B+ users |

### System Design Framework

**1. Clarify Scope (5 min)**
- What are we building? (ranking, recommendation, classification)
- Key metrics: engagement, quality, safety, latency
- Scale: users, items, requests/second
- Constraints: latency budget, compute budget

**2. Data & Features (10 min)**
- Data sources: user actions, content metadata, social graph
- Real-time vs batch features
- Feature engineering approach
- Feature store design

**3. Model Architecture (15 min)**
- Retrieval vs ranking stage separation
- Model selection and rationale
- Training: data, loss, optimization
- Multi-objective / multi-task approach

**4. Serving & Evaluation (10 min)**
- Inference infrastructure
- A/B testing framework
- Online metrics and instrumentation
- Latency optimization

**5. Operations (5 min)**
- Retraining strategy
- Monitoring and alerting
- Experimentation platform

---

## 4. ML Coding Round

### Common Problems

| Algorithm | Variants | Meta Frequency |
|-----------|----------|----------------|
| Linear Regression | GD, closed-form, ridge | High |
| Logistic Regression | Binary, multinomial, regularized | Very High |
| Neural Network | MLP with backprop, batch norm | Very High |
| K-Means | K-means++, initialization strategies | High |
| KNN | Various distance metrics, KD-tree | Medium |
| Decision Tree | Classification, regression | Medium |
| PCA | SVD approach, explained variance | Medium |
| Attention | Scaled dot-product, multi-head | Very High |
| CNN Convolution | Forward pass, im2col | High |
| RNN/LSTM Cell | Forward pass | Medium |

### Meta-Specific Expectations

- **PyTorch-style**: Interviewers often expect code compatible with PyTorch conventions
- **Efficiency**: numpy vectorization, avoid Python loops
- **Clean**: Well-structured, modular code
- **Testing**: Edge cases, sample data validation

```python
# Example: Logistic Regression (Meta-style)
import numpy as np

class LogisticRegression:
    def __init__(self, lr=0.01, lambda_l2=0.0, n_iter=1000):
        self.lr = lr
        self.lambda_l2 = lambda_l2
        self.n_iter = n_iter
        self.w = None
        self.b = 0.0

    def sigmoid(self, z):
        z = np.clip(z, -500, 500)
        return 1 / (1 + np.exp(-z))

    def fit(self, X, y):
        n, d = X.shape
        self.w = np.zeros(d)

        for i in range(self.n_iter):
            logits = X @ self.w + self.b
            probs = self.sigmoid(logits)

            # Gradient with L2 regularization
            grad_w = (X.T @ (probs - y)) / n + (self.lambda_l2 * self.w) / n
            grad_b = np.mean(probs - y)

            self.w -= self.lr * grad_w
            self.b -= self.lr * grad_b

            if i % 100 == 0:
                loss = self._log_loss(y, probs)
                # Can print loss for monitoring

    def predict_proba(self, X):
        return self.sigmoid(X @ self.w + self.b)

    def predict(self, X, threshold=0.5):
        return (self.predict_proba(X) >= threshold).astype(int)

    def _log_loss(self, y, p):
        eps = 1e-15
        p = np.clip(p, eps, 1 - eps)
        return -np.mean(y * np.log(p) + (1 - y) * np.log(1 - p))
```

---

## 5. Algorithm Coding Round

### Meta's Coding Patterns

| Pattern | Frequency | Examples |
|---------|-----------|----------|
| Two Pointers | Very High | Container Most Water, 3Sum |
| Sliding Window | Very High | Longest Substring, Min Window |
| DFS/BFS | Very High | Number Islands, Clone Graph |
| DP | Very High | Coin Change, Decode Ways |
| Binary Search | High | Search Rotated Array, Find Peak |
| Graph (topological sort) | High | Course Schedule, Alien Dict |
| Heap | Medium | K Closest Points, Merge K Lists |
| Tree | High | Binary Tree Paths, Max Path Sum |
| Strings | High | Group Anagrams, Palindromes |
| Design | High | LRU Cache, Serialize Tree |

### Preparation Strategy

- **Focus on Graphs and DP**: Meta asks these more than other companies
- **Handle scale**: Code should handle large inputs efficiently
- **Object-oriented**: Write clean, well-structured code
- **Testing mindset**: Think about edge cases (empty, single element, duplicates)

```
Meta-Specific Favorites:
1. LC 200: Number of Islands (DFS)
2. LC 238: Product of Array Except Self
3. LC 124: Binary Tree Maximum Path Sum
4. LC 56: Merge Intervals
5. LC 146: LRU Cache
6. LC 269: Alien Dictionary (Topological Sort)
7. LC 297: Serialize and Deserialize Binary Tree
8. LC 339: Nested List Weight Sum
9. LC 560: Subarray Sum Equals K
10. LC 102: Binary Tree Level Order Traversal
```

---

## 6. Behavioral / ML Project Deep Dive

### What They Want to Understand

**1. Technical Depth**
- Did you really understand the ML techniques?
- Can you explain why you made specific choices?
- Do you know the math behind the models?

**2. Impact**
- What was the measurable outcome?
- How did you prioritize what to work on?
- What was your personal contribution?

**3. Leadership**
- How did you influence decisions?
- Did you mentor others?
- How do you handle disagreements?

**4. Growth Mindset**
- What did you learn?
- What would you do differently?
- How do you respond to failure?

### Sample Questions

```
"Tell me about the most impactful ML project you worked on"

STAR Response Structure:
S: "Our Ads CTR model had plateaued at 2.1%..."
T: "I needed to redesign the ranking architecture to improve CTR"
A: "I implemented a two-tower architecture with real-time features,
    conducted A/B tests with 5% traffic, iterated based on results..."
R: "CTR improved 8%, revenue increased $50M/year. The model serves
    100K QPS at 50ms p99 latency."
```

### Meta Leadership Principles

| Principle | ML Application |
|-----------|---------------|
| Move Fast | "I shipped model v1 in 2 weeks, iterated weekly" |
| Be Open | "I shared my approach on the ML mailing list, got feedback" |
| Focus on Impact | "I prioritized models by expected revenue impact" |
| Build Trust | "I added comprehensive monitoring and alerting" |
| Iterate | "We ran 5 experiment iterations before shipping" |

---

## 7. Understanding Meta's AI Stack

### PyTorch Knowledge

Be prepared to discuss:
- **Autograd**: How gradients are computed and accumulated
- **DDP (DistributedDataParallel)**: All-reduce gradient synchronization
- **FSDP (Fully Sharded Data Parallel)**: ZeRO-style sharding
- **TorchScript / torch.compile**: Graph optimization
- **CUDA graphs**: Reducing kernel launch overhead

### Llama Architecture

- **Grouped Query Attention (GQA)**: Reduces KV cache size
- **Rotary Position Embedding (RoPE)**: Relative position encoding
- **SwiGLU Activation**: Gated activation function
- **RMSNorm**: Simplified layer normalization
- **Pre-norm architecture**: More stable training
- **Vocabulary**: 32K-128K tokens (trained with SentencePiece)

### Large-Scale Training at Meta

- **16K+ GPUs** for Llama 3 training
- **FSDP + Tensor Parallelism** combination
- **NVIDIA H100** clusters with InfiniBand
- **NCCL** for GPU communication
- **Optimizer**: AdamW with cosine learning rate schedule
- **Batch size**: 4M+ tokens per batch
- **Precision**: BF16 mixed precision

---

## 8. Research Expectations (FAIR Roles)

### Research Presentation

**Structure (15 min + 30 min Q&A)**:
1. Problem and motivation
2. Related work
3. Method with mathematical formulation
4. Experimental results and analysis
5. Discussion and future work

**Key Evaluation Points**:
- Novelty and significance of contribution
- Experimental rigor
- Understanding of strengths and limitations
- Ability to discuss future directions

### Meta-FAIR Papers to Know

**Classic**:
- "DeepFace: Closing the Gap to Human-Level Performance" (2014)
- "Detectron" series
- "Fairseq" sequence modeling toolkit

**Recent**:
- "LLaMA: Open and Efficient Foundation Language Models" (2023)
- "Llama 2: Open Foundation and Fine-Tuned Chat Models" (2023)
- "Segment Anything" (2023)
- "DINOv2" (2023)
- "ImageBind" (2023)
- "Llama 3" (2024)

---

## 9. Preparation Timeline

### 8-12 Weeks

**Foundation (Weeks 1-4)**:
- ML fundamentals: deep understanding of 30 core concepts
- ML coding: implement 10+ algorithms from scratch
- LeetCode: 50 problems (medium focus)

**System Design (Weeks 5-8)**:
- Study Meta's ML systems (News Feed, Ads, Instagram)
- Practice 5 design problems with Meta-specific constraints
- Read recent Meta AI papers

**Intensive (Weeks 9-11)**:
- Mock interviews (2x per week)
- LeetCode: 30 problems (hard focus)
- STAR story preparation (5 stories)
- ML code from memory (no reference)

**Final Week**:
- Light review of concepts
- Prepare questions for interviewers
- Logistics: setup, rest, mindset

---

## 10. Key Resources

### Books
- "Recommender Systems Handbook" (Ricci et al.)
- "Deep Learning" (Goodfellow et al.)
- "Cracking the Coding Interview" (McDowell)

### Meta Resources
- **PyTorch Documentation**: DDP, FSDP, torch.compile
- **FAIR Publications**: research.facebook.com
- **Meta Engineering Blog**: engineering.fb.com
- **PyTorch GitHub**: source code reading

### Online Courses
- Stanford CS229 (ML theory)
- Stanford CS231n (CNN/vision)
- Stanford CS224n (NLP/transformers)

### Practice Platforms
- LeetCode (Meta tagged questions)
- Kaggle (ML coding practice)
- HackerRank (algorithm practice)
