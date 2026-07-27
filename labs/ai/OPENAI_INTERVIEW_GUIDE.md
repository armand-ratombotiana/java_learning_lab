# OpenAI Interview Guide

Interview preparation for AI/ML roles at OpenAI. Covers research scientist, applied ML engineer, and MTS positions.

---

## 1. Role Types

### Research Scientist
- Pushes the frontier of AI capabilities
- Works on foundation models (GPT-4, GPT-4V, DALL-E, Whisper)
- Strong publication record required
- Focus: scaling, alignment, reasoning, multimodality

### Applied ML Engineer
- Builds production systems around OpenAI APIs
- Focus: reliability, safety, latency optimization
- Strong engineering background + ML knowledge
- Works with enterprise customers and developer platform

### Member of Technical Staff (MTS)
- Generalist role spanning research and engineering
- Works across multiple teams and projects
- Expects breadth + depth in ML
- Highly selective (smallest hiring group)

### Safety Researcher
- Alignment, interpretability, robustness
- RLHF, red-teaming, capability evaluations
- Safety-first research methodology

---

## 2. Interview Process

### Process Timeline

| Step | Duration | Format |
|------|----------|--------|
| Recruiter Screen | 30 min | Values + background |
| Technical Screen | 45-60 min | Coding or research discussion |
| Virtual On-site | 4-5 hours | 4-5 rounds |
| References | - | 3-5 references checked |
| Offer | - | Negotiation |

### Round Breakdown

| Round | Duration | Focus |
|-------|----------|-------|
| Research Deep Dive | 60 min | Paper/project presentation |
| ML Coding | 45 min | Implementation from scratch |
| System Design | 45 min | ML infrastructure/serving |
| General Coding | 45 min | Python algorithms |
| Values | 45 min | Safety, culture, motivations |
| Lunch | 30 min | Informal chat |

---

## 3. Research Deep Dive

### Presentation Requirements

**Structure (20 min + 40 min Q&A)**:
1. Problem motivation and significance
2. Detailed methodology with key equations
3. Experimental setup and results
4. Analysis of strengths and limitations
5. Extensions and open questions

### What They Evaluate

- **Depth of understanding**: Can you explain why the method works, not just how?
- **Critical thinking**: What are the failure modes? How would you improve it?
- **Creativity**: Can you propose novel extensions?
- **Rigor**: Are your experiments well-designed? Statistical significance?
- **Communication**: Can you make complex ideas accessible?

### Sample Research Questions

```
Q: "Why did you choose this architecture over alternatives?"
A: Should discuss specific trade-offs (compute, accuracy, training stability)

Q: "What's the most significant limitation?"
A: Honest assessment with potential solutions

Q: "How would you extend this to a different domain?"
A: Concrete proposal with expected challenges
```

### Papers to Know

**GPT Series**:
- "Improving Language Understanding by Generative Pre-Training" (GPT-1, 2018)
- "Language Models are Unsupervised Multitask Learners" (GPT-2, 2019)
- "Language Models are Few-Shot Learners" (GPT-3, 2020)
- "Training Language Models to Follow Instructions" (InstructGPT, 2022)
- "GPT-4 Technical Report" (2023)

**Other Key Papers**:
- "Scaling Laws for Neural Language Models" (Kaplan et al., 2020)
- "Training Compute-Optimal Large Language Models" (Chinchilla, 2022)
- "Constitutional AI: Harmlessness from AI Feedback" (Bai et al., 2022)
- "Let's Verify Step by Step" (Process reward models, 2023)

---

## 4. ML Coding Round

### High-Frequency Problems

| Category | Problems | Importance |
|----------|----------|------------|
| Transformer | Multi-head attention, transformer block, positional encoding | Critical |
| Training Loop | Complete train loop: forward, loss, backward, update | Very High |
| Attention Variants | Flash attention, grouped query, sliding window | High |
| RLHF Components | Reward model, PPO loss, KL penalty | High |
| Optimizers | Adam, AdamW, SGD with momentum | High |
| Tokenization | BPE tokenizer training, encoding | Medium |
| Mixture of Experts | Sparse MoE routing, load balancing | Medium |
| LoRA Fine-tuning | Low-rank adaptation, weight merging | High |

```python
# Example: Transformer Block (from scratch)
import numpy as np

class TransformerBlock:
    def __init__(self, d_model, n_heads, d_ff, dropout=0.1):
        self.attention = MultiHeadAttention(d_model, n_heads)
        self.ffn = FeedForward(d_model, d_ff)
        self.norm1 = LayerNorm(d_model)
        self.norm2 = LayerNorm(d_model)
        self.dropout = dropout

    def forward(self, x, mask=None):
        # Pre-norm architecture
        attn_out = self.attention(self.norm1.forward(x), mask=mask)
        x = x + attn_out

        ffn_out = self.ffn.forward(self.norm2.forward(x))
        x = x + ffn_out

        return x

    def backward(self, grad_output):
        grad_x = ...

class LayerNorm:
    def __init__(self, d_model, eps=1e-5):
        self.gamma = np.ones(d_model)
        self.beta = np.zeros(d_model)
        self.eps = eps

    def forward(self, x):
        mean = x.mean(axis=-1, keepdims=True)
        var = x.var(axis=-1, keepdims=True)
        self.x_norm = (x - mean) / np.sqrt(var + self.eps)
        return self.gamma * self.x_norm + self.beta

class MultiHeadAttention:
    def __init__(self, d_model, n_heads):
        assert d_model % n_heads == 0
        self.d_model = d_model
        self.n_heads = n_heads
        self.d_k = d_model // n_heads
        self.W_q = np.random.randn(d_model, d_model) / np.sqrt(d_model)
        self.W_k = np.random.randn(d_model, d_model) / np.sqrt(d_model)
        self.W_v = np.random.randn(d_model, d_model) / np.sqrt(d_model)
        self.W_o = np.random.randn(d_model, d_model) / np.sqrt(d_model)

    def forward(self, x, mask=None):
        B, T, D = x.shape
        Q = x @ self.W_q
        K = x @ self.W_k
        V = x @ self.W_v

        # Reshape to multi-head
        Q = Q.reshape(B, T, self.n_heads, self.d_k).transpose(0, 2, 1, 3)
        K = K.reshape(B, T, self.n_heads, self.d_k).transpose(0, 2, 1, 3)
        V = V.reshape(B, T, self.n_heads, self.d_k).transpose(0, 2, 1, 3)

        # Scaled dot-product attention
        scores = Q @ K.transpose(0, 1, 3, 2) / np.sqrt(self.d_k)
        if mask is not None:
            scores = scores.masked_fill(mask == 0, -1e9)
        attn = self.softmax(scores)
        out = attn @ V

        # Reshape back
        out = out.transpose(0, 2, 1, 3).reshape(B, T, D)
        return out @ self.W_o

    def softmax(self, x):
        x = x - x.max(axis=-1, keepdims=True)
        exp = np.exp(x)
        return exp / exp.sum(axis=-1, keepdims=True)
```

---

## 5. System Design Round

### Common Topics

| Topic | Key Considerations | OpenAI Context |
|-------|-------------------|----------------|
| LLM Training Infrastructure | Distributed training, data pipeline, checkpointing | GPT-4 scale training |
| Inference Serving | Throughput, latency, batching, quantization | API serving |
| Safety Evaluation Pipeline | Red-teaming, benchmarks, automated testing | Model readiness |
| RLHF Data Pipeline | Preference collection, quality control | InstructGPT |
| API Platform Design | Rate limiting, authentication, monitoring | ChatGPT/API |
| Fine-tuning Infrastructure | LoRA serving, model customization | Fine-tuning API |

### Sample Design Questions

**Design an LLM inference serving system**:

Requirements:
- Serve a 70B parameter model
- 1000 requests/second
- p99 latency < 2s
- Support streaming

Key Decisions:
1. **Model parallelism**: Tensor parallelism across 8 GPUs per node
2. **Batching**: Continuous/in-flight batching
3. **KV cache**: PagedAttention for memory efficiency
4. **Quantization**: FP16 serving, optional INT4 for lower cost
5. **Scaling**: Horizontal scaling behind load balancer
6. **Caching**: Response caching for identical prompts
7. **Monitoring**: Token throughput, latency, error rates

**Design a model evaluation pipeline**:

Requirements:
- Evaluate new model versions before release
- Automated + human evaluation
- Safety, capability, and alignment metrics

Key Decisions:
1. **Automated benchmarks**: MMLU, HumanEval, GSM8K, HELM
2. **Adversarial evaluation**: Red team prompts, jailbreak attempts
3. **Human evaluation**: Preference comparisons, rating scales
4. **Safety filters**: Toxicity, bias, harmful content detection
5. **Regression testing**: Compare against previous versions
6. **Statistical significance**: Confidence intervals, multiple seeds

---

## 6. General Coding Round

### Focus Areas

| Topic | Importance | Notes |
|-------|-----------|-------|
| Python internals | High | Generators, decorators, context managers |
| Algorithms | Medium | Graphs, DP, trees (not as heavy as Google/Meta) |
| Data structures | Medium | Hash maps, heaps, trees |
| Systems programming | Medium | Concurrency, async, I/O |
| Math/Numerical | High | Linear algebra, probability in code |

### Sample Python Questions

```python
# Question: Implement a generator that yields batches from a dataset
def batch_generator(data, batch_size, shuffle=True):
    indices = list(range(len(data)))
    if shuffle:
        random.shuffle(indices)
    for i in range(0, len(data), batch_size):
        batch_idx = indices[i:i + batch_size]
        yield [data[j] for j in batch_idx]

# Question: Implement LRU cache with O(1) operations
class LRUCache:
    def __init__(self, capacity):
        self.capacity = capacity
        self.cache = {}
        self.order = []

    def get(self, key):
        if key not in self.cache:
            return -1
        self.order.remove(key)
        self.order.append(key)
        return self.cache[key]

    def put(self, key, value):
        if key in self.cache:
            self.order.remove(key)
        elif len(self.cache) >= self.capacity:
            oldest = self.order.pop(0)
            del self.cache[oldest]
        self.cache[key] = value
        self.order.append(key)
```

---

## 7. Values / Behavioral Round

### Core Values at OpenAI

| Value | What It Means | Interview Focus |
|-------|--------------|-----------------|
| AGI Focus | Work on the most important AI problems | "What problem do you think is most important?" |
| Safety First | Responsible development | "How do you think about AI risk?" |
| Rigor | Scientific excellence | "How do you ensure your results are correct?" |
| Collaboration | Work with the best | "How do you handle disagreement?" |
| Scale | Think big | "What would you do with unlimited compute?" |

### Key Questions to Prepare

```
1. Why do you want to work at OpenAI specifically?
2. How do you think about the risks from advanced AI?
3. Describe a time you had to prioritize safety over speed
4. What's the most important unsolved problem in AI?
5. How do you stay current with research?
6. Describe a research result you were skeptical of
7. How do you think about research reproducibility?
8. What's your process for evaluating new ideas?
```

### Answering Safety Questions

Show that you:
1. Take AI safety seriously (not dismissive)
2. Have nuanced thinking (not alarmist or overly optimistic)
3. Can articulate specific risks and mitigation strategies
4. Consider both near-term and long-term implications

---

## 8. Key Preparation Areas

### Technical Must-Knows

1. **Transformer Architecture** - deep understanding of every component
2. **Scaling Laws** - for compute, data, model size
3. **RLHF** - reward modeling, PPO, KL divergence
4. **Alignment** - Constitutional AI, reinforcement learning from AI feedback
5. **Training Efficiency** - data parallelism, model parallelism, ZeRO
6. **Inference Optimization** - quantization, speculation decoding, KV cache
7. **Evaluation** - benchmarks, human eval, adversarial testing
8. **Multimodal** - vision-language, audio models
9. **Reasoning** - chain-of-thought, self-consistency, tool use
10. **Safety** - red-teaming, jailbreaks, monitoring

### Research Area Deep Dives

Choose 2-3 areas to develop deep expertise in:
- **Scaling**: Laws, efficiency, large-scale training
- **Alignment**: RLHF, constitutional AI, interpretability
- **Reasoning**: Chain-of-thought, planning, verification
- **Multimodality**: Vision-language, audio, video generation
- **Efficiency**: Quantization, distillation, sparse models

---

## 9. Preparation Timeline

### 12 Weeks

**Weeks 1-4: Foundations**
- Read all GPT papers (1-4, InstructGPT, RLHF)
- Implement transformer from scratch
- Study scaling laws and Chinchilla

**Weeks 5-8: Deep Dive**
- Read recent research (reasoning, alignment, multimodality)
- Practice ML coding (attention variants, training loops, optimizers)
- Prepare research presentation with deep analysis

**Weeks 9-11: Interview Practice**
- Mock interviews (coding + research + system design)
- Practice STAR stories aligned with OpenAI values
- Read safety and alignment literature

**Week 12: Final Preparation**
- Review key formulas and concepts
- Prepare questions for interviewers
- Rest and prepare logistics

---

## 10. Key Resources

### Papers (Must Read)
- GPT-1, GPT-2, GPT-3, InstructGPT, GPT-4
- Scaling Laws (Kaplan et al., Chinchilla)
- RLHF (PPO, DPO, KTO)
- Constitutional AI (Anthropic)
- "Let's Verify Step by Step" (OpenAI)

### Learning Resources
- Andrej Karpathy's "Zero to Hero" and "nanoGPT"
- "The Annotated Transformer" (Harvard NLP)
- "3Blue1Brown" neural network series
- Lilian Weng's blog (lilianweng.github.io)

### Code Repos
- nanoGPT (karpathy)
- minRLHF (open-source RLHF implementations)
- GPT-2 (openai/gpt-2)
- OpenAI Evals (evaluation framework)
