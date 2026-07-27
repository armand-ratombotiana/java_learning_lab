# Google AI Interview Guide

Comprehensive interview preparation for ML/AI roles at Google (including DeepMind, Google Research, Gemini team).

---

## 1. Role Types at Google AI

### ML Engineer (L3-L8)
- Builds ML systems for Google products: Search, YouTube, Ads, Cloud AI, Assistant
- ML focus: recommendation, ranking, NLP, vision, speech
- Strong software engineering required
- Coding bar: LeetCode hard

### AI Research Scientist (DeepMind / Google Research)
- Fundamental research at DeepMind or Google Research (formerly Google Brain)
- Publish at NeurIPS, ICML, ICLR, CVPR, ICASSP
- Focus areas: transformers, RL, multimodal, scientific ML (AlphaFold)
- PhD or equivalent research track record

### Applied Scientist
- Bridge between research and product
- Adapt SOTA research for Google products
- Strong publication record expected
- Teams: Google Cloud AI, Workspace AI

### ML Infrastructure Engineer
- Builds training/serving infrastructure
- TPU/GPU cluster management
- ML pipelines, feature stores, data processing
- Systems + ML hybrid role

---

## 2. Interview Process

### Process Timeline

| Step | Duration | Format |
|------|----------|--------|
| Recruiter Screen | 30 min | Phone/Video |
| Technical Phone Screen | 45-60 min | Coding + ML basics |
| On-site (Virtual) | 4-5 hours | 4-5 rounds |
| Hiring Committee Review | 1-2 weeks | Paper review |
| Team Matching | 1-4 weeks | Conversation with teams |
| Offer | - | Negotiation |

### Round Breakdown

| Round | Duration | Focus |
|-------|----------|-------|
| ML Design | 45 min | End-to-end ML system |
| Coding 1 | 45 min | Algorithms (medium) |
| Coding 2 | 45 min | Algorithms (hard) |
| ML Coding | 45 min | ML from scratch |
| Googleyness | 30-45 min | Behavioral |
| Lunch | 30 min | Informal chat (not evaluated) |

---

## 3. ML Design Round

### Common Topics

| System | Key Components | Metrics |
|--------|---------------|---------|
| YouTube Recommendations | Two-tower, retrieval, ranking, re-ranking | Watch time, CTR |
| Google Search Ranking | Query understanding, document retrieval, ranking | NDCG, MAP, CTR |
| Google Smart Reply | Encoder-decoder, retrieval + generation | Acceptance rate |
| Google Photos | Image tagging, face recognition, clustering | Precision, recall |
| Gmail Spam Detection | Content + metadata features, ensemble | Precision, recall |
| Google Translate | Transformer (T5/PaLM), on-device | BLEU, human eval |
| Ad Prediction | CTR prediction, real-time bidding | AUC, revenue |
| Google Maps | ETA prediction, route optimization | MAE, latency |

### ML Design Framework

**1. Clarify Requirements (5 min)**
- What type of ML problem? (classification, regression, ranking, generation)
- Scale: How many users/items/requests?
- Metrics: What matters for business?
- Constraints: Latency SLO, cost, hardware?

**2. Data Pipeline (10 min)**
- Sources: user events, content metadata, context
- Features: offline batch + online streaming
- Storage: BigQuery, Spanner, Colossus
- Validation: TensorFlow Data Validation

**3. Model Design (15 min)**
- Model architecture selection
- Training: data split, loss function, optimizer
- Offline evaluation: metrics, validation strategy
- Baselines and ablation

**4. Serving & Deployment (10 min)**
- Batch vs real-time inference
- Model versioning and A/B testing
- Latency budget allocation
- Caching strategies

**5. Monitoring & Iteration (5 min)**
- Metrics to monitor
- Drift detection
- Retraining cadence
- Experimentation framework

### Sample ML Design: YouTube Recommendations

```
┌─────────────────────────────────────────────────────┐
│ User Request (video watched, search, browse)         │
└─────────┬───────────────────────────────────────────┘
          │
          ▼
┌─────────────────────┐    ┌─────────────────────────┐
│ Candidate Generation │◄───│ Feature Store            │
│ - Two-tower model    │    │ - User embeddings        │
│ - Collaborative filt │    │ - Video embeddings       │
│ - Popularity         │    │ - Context features       │
│ - Topic matching     │    └─────────────────────────┘
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│ Ranking (DNN)        │
│ - Deep cross features│
│ - Multi-task: watch, │
│   like, share, skip  │
│ - Position bias corr │
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│ Re-ranking           │
│ - Diversity          │
│ - Freshness boost    │
│ - User fatigue       │
│ - Business rules     │
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│ Recommended Videos   │
└─────────────────────┘
```

---

## 4. ML Coding Round

### Problems Frequently Asked

| Problem | Difficulty | Frequency |
|---------|------------|-----------|
| Linear regression (gradient descent) | Medium | Very High |
| Logistic regression with regularization | Medium | High |
| K-means clustering + initialization | Medium | High |
| K-nearest neighbors (various distances) | Medium | High |
| Decision tree (ID3 or CART) | Hard | Medium |
| Neural network forward/backward pass | Hard | High |
| PCA from scratch (SVD or eigendecomp) | Hard | Medium |
| CNN convolution (forward pass) | Hard | Medium |
| Attention mechanism (scaled dot-product) | Hard | High |
| Word2Vec skip-gram with negative sampling | Hard | Medium |
| Batch normalization forward/backward | Hard | Medium |
| ROC/AUC computation | Medium | Medium |

### Evaluation Criteria

- **Correctness**: Does the code produce correct output?
- **Completeness**: Are edge cases handled? (empty data, NaN, single class)
- **Efficiency**: Time/space complexity analysis
- **Vectorization**: Using numpy efficiently (avoid Python loops when possible)
- **Testing**: Demonstrating with sample data

### Example: Multi-Head Attention Implementation

```python
# Expected to implement in ~20-25 minutes
def scaled_dot_product_attention(Q, K, V, mask=None):
    d_k = Q.shape[-1]
    scores = np.matmul(Q, K.transpose(0, 2, 1)) / np.sqrt(d_k)
    if mask is not None:
        scores = np.where(mask, scores, -1e9)
    attention_weights = softmax(scores)
    return np.matmul(attention_weights, V), attention_weights

def multi_head_attention(Q, K, V, d_model, num_heads, W_Q, W_K, W_V, W_O):
    batch_size = Q.shape[0]
    d_k = d_model // num_heads

    Q_proj = np.dot(Q, W_Q)
    K_proj = np.dot(K, W_K)
    V_proj = np.dot(V, W_V)

    Q_heads = split_heads(Q_proj, batch_size, num_heads, d_k)
    K_heads = split_heads(K_proj, batch_size, num_heads, d_k)
    V_heads = split_heads(V_proj, batch_size, num_heads, d_k)

    output, attention = scaled_dot_product_attention(Q_heads, K_heads, V_heads)
    concat = combine_heads(output, batch_size, num_heads, d_k)
    return np.dot(concat, W_O), attention
```

---

## 5. Algorithm Coding Round

### High-Frequency Topics

| Topic | Frequency | Example Problems |
|-------|-----------|-----------------|
| Arrays & Strings | Very High | Two Sum, Longest Substring, Group Anagrams |
| Trees & Graphs | High | Binary Tree Max Path Sum, Number of Islands |
| Dynamic Programming | Very High | Longest Increasing Subsequence, Edit Distance |
| Hash Maps | Very High | LRU Cache, Top K Frequent |
| Binary Search | High | Search Rotated Array, Split Array Largest Sum |
| Heap/Priority Queue | Medium | Kth Largest, Merge K Sorted |
| Backtracking | Medium | Word Search, N-Queens |
| Design | High | Serialize Binary Tree, Autocomplete |

### Google Coding Style

- **Readable**: Clean, well-named variables, modular functions
- **Efficient**: Time and space complexity are discussed
- **Tested**: Run through sample tests and edge cases
- **No shortcuts**: Implement algorithms yourself, not library calls

```
# Example: Two Sum (optimized)
def two_sum(nums, target):
    seen = {}
    for i, num in enumerate(nums):
        complement = target - num
        if complement in seen:
            return [seen[complement], i]
        seen[num] = i
    return []
```

---

## 6. Googleyness (Behavioral)

### What They Evaluate

| Dimension | What They Look For | Red Flags |
|-----------|-------------------|-----------|
| Ambiguity | Comfort with undefined problems | Needs explicit specs |
| Collaboration | Cross-team, inclusive | Blames others |
| Learning | Growth from failure | Defensive about mistakes |
| Impact | Prioritization, results | Covers tasks, not impact |
| Intellectual Curiosity | Deep understanding | Surface-level knowledge |

### Sample Questions

**On Ambiguity**:
- "Tell me about a project with changing requirements"
- "Describe a time you had to figure out the right approach without guidance"

**On Collaboration**:
- "Describe a disagreement with a colleague and how you resolved it"
- "Tell me about a time you influenced a decision without authority"

**On Learning**:
- "Tell me about a technical mistake you made and what you learned"
- "Describe a skill you taught yourself for a project"

**On Impact**:
- "How did you prioritize features for an ML model?"
- "Tell me about a time you went above and beyond expectations"

**On Intellectual Curiosity**:
- "What's the most interesting ML paper you've read recently?"
- "How do you decide what to learn next?"

### Preparation Strategies

1. **Prepare 5-7 STAR stories** covering different dimensions
2. **Quantify impact** whenever possible
3. **Show humility** and willingness to learn
4. **Connect to Google's values** (focus on users, respect each other, etc.)

---

## 7. Research Deep Dive (Research Roles)

### Presentation Structure

**Slides (15 min presentation, 30 min Q&A)**:
1. Problem statement and motivation (2 slides)
2. Related work and limitations (1-2 slides)
3. Method: intuition, equations, architecture (3-4 slides)
4. Experiments: setup, baselines, results, ablations (3-4 slides)
5. Analysis: strengths, limitations, future work (1-2 slides)

### Q&A Preparation

Be ready to discuss:
- **Why did you choose this approach?** (alternatives considered)
- **What are the limitations?** (honest assessment)
- **How would you extend this work?** (new directions)
- **Reproducibility**: Specific experiment details
- **Theoretical grounding**: Assumptions, guarantees

### Papers to Know

**Classic Google Papers**:
- "Attention Is All You Need" (2017)
- "BERT: Pre-training of Deep Bidirectional Transformers" (2018)
- "Scaling Laws for Neural Language Models" (Kaplan et al., 2020)
- "Pathways: Asynchronous Distributed Dataflow for ML" (2022)
- "PaLM: Scaling Language Modeling with Pathways" (2022)
- "Gemini: A Family of Highly Capable Multimodal Models" (2023)
- "AlphaFold" series (2018-2024)

**DeepMind Papers**:
- "Mastering the Game of Go with Deep Neural Networks and Tree Search" (2016)
- "SILG: The Multi-domain Interactive Navigation" (2021)
- "Gato: A Generalist Agent" (2022)
- "Chinchilla: Training Compute-Optimal Large Language Models" (2022)

---

## 8. Preparation Timeline

### 12 Weeks Before

**Weeks 1-4: Foundation**
- Review ML fundamentals (bias-variance, regularization, loss functions)
- Practice ML coding (implement 10 algorithms from scratch)
- LeetCode fundamentals (50 problems)

**Weeks 5-8: System Design**
- Study ML system design patterns
- Practice 5 design problems (recommendation, ranking, moderation)
- Read 10 Google papers
- LeetCode medium (50 problems)

**Weeks 9-11: Deep Practice**
- Full mock interviews (ML design + coding + Googleyness)
- LeetCode hard (20 problems)
- Review ML coding implementations from memory
- Prepare STAR stories

**Week 12: Review**
- Light review of key concepts
- Questions to ask interviewers
- Rest and prepare logistics

---

## 9. Key Resources

### Books
- "Cracking the Coding Interview" (McDowell)
- "Designing Data-Intensive Applications" (Kleppmann)
- "Deep Learning" (Goodfellow, Bengio, Courville)

### Online Resources
- Google ML Crash Course
- TensorFlow Documentation
- JAX Tutorials
- DeepMind Publications

### Key Skills to Demonstrate
- Strong fundamentals over memorization
- Clear communication of complex ideas
- Intellectual curiosity and depth
- Collaborative problem-solving
