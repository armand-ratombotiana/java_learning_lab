# Mock Interview: Compare MHA, MQA, GQA — Implementation and Memory Analysis

## Scenario
You are interviewing for a ML performance engineer role. They want to optimize transformer inference and need you to compare attention variants.

## Interviewer Opening Question
"Compare Multi-Head Attention (MHA), Multi-Query Attention (MQA), and Grouped Query Attention (GQA). Implement all three and analyze their memory and compute trade-offs."

## Candidate Response
"MHA uses separate K, V projections per head. MQA shares a single K, V across all heads, dramatically reducing KV cache size. GQA is a middle ground: N groups of query heads sharing one K, V head. MQA saves the most memory but can hurt quality; GQA is Pareto-optimal."

## Interviewer Probing Questions

**Q: How much memory does the KV cache save?**
"For a model with 32 layers, 32 heads, d_k=128, sequence length 4096, batch 1: MHA cache = 32 * 2 * 4096 * 128 * 2 bytes = 64MB per layer. MQA reduces to 2MB per layer. GQA with 8 groups: 8MB per layer."

**Q: When would you choose GQA over MQA?**
"GQA (as used in Llama 2 70B) preserves more model capacity while still reducing KV cache by 4-8x. MQA can degrade quality on complex reasoning tasks. GQA with 8 groups for 32 heads is a sweet spot."

**Q: What about training vs inference trade-offs?**
"MQA/GQA were designed for inference efficiency. During training, MHA is standard. You'd train from scratch with MQA/GQA or use cross-architecture distillation."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
import torch.nn.functional as F
import numpy as np

class MultiHeadAttention(nn.Module):
    def __init__(self, d_model, num_heads):
        super().__init__()
        assert d_model % num_heads == 0
        self.d_model = d_model
        self.num_heads = num_heads
        self.d_k = d_model // num_heads
        self.W_q = nn.Linear(d_model, d_model)
        self.W_k = nn.Linear(d_model, d_model)
        self.W_v = nn.Linear(d_model, d_model)
        self.W_o = nn.Linear(d_model, d_model)

    def forward(self, x, mask=None, use_cache=False):
        batch, seq_len, _ = x.shape
        Q = self.W_q(x).view(batch, seq_len, self.num_heads, self.d_k).transpose(1, 2)
        K = self.W_k(x).view(batch, seq_len, self.num_heads, self.d_k).transpose(1, 2)
        V = self.W_v(x).view(batch, seq_len, self.num_heads, self.d_k).transpose(1, 2)
        scores = torch.matmul(Q, K.transpose(-2, -1)) / (self.d_k ** 0.5)
        if mask is not None:
            scores = scores.masked_fill(mask == 0, -1e9)
        attn = F.softmax(scores, dim=-1)
        out = torch.matmul(attn, V).transpose(1, 2).contiguous().view(batch, seq_len, -1)
        return self.W_o(out)

class MultiQueryAttention(nn.Module):
    def __init__(self, d_model, num_heads):
        super().__init__()
        self.d_model = d_model
        self.num_heads = num_heads
        self.d_k = d_model // num_heads
        self.W_q = nn.Linear(d_model, d_model)
        self.W_k = nn.Linear(d_model, self.d_k)  # single K head
        self.W_v = nn.Linear(d_model, self.d_k)  # single V head
        self.W_o = nn.Linear(d_model, d_model)

    def forward(self, x, mask=None, use_cache=False):
        batch, seq_len, _ = x.shape
        Q = self.W_q(x).view(batch, seq_len, self.num_heads, self.d_k).transpose(1, 2)
        K = self.W_k(x).view(batch, seq_len, 1, self.d_k).transpose(1, 2)  # 1 head
        V = self.W_v(x).view(batch, seq_len, 1, self.d_k).transpose(1, 2)
        # Broadcast K, V across heads
        scores = torch.matmul(Q, K.transpose(-2, -1)) / (self.d_k ** 0.5)
        if mask is not None:
            scores = scores.masked_fill(mask == 0, -1e9)
        attn = F.softmax(scores, dim=-1)
        out = torch.matmul(attn, V).transpose(1, 2).contiguous().view(batch, seq_len, -1)
        return self.W_o(out)

class GroupedQueryAttention(nn.Module):
    def __init__(self, d_model, num_heads, num_groups):
        super().__init__()
        assert num_heads % num_groups == 0
        self.d_model = d_model
        self.num_heads = num_heads
        self.num_groups = num_groups
        self.d_k = d_model // num_heads
        self.heads_per_group = num_heads // num_groups
        self.W_q = nn.Linear(d_model, d_model)
        self.W_k = nn.Linear(d_model, num_groups * self.d_k)
        self.W_v = nn.Linear(d_model, num_groups * self.d_k)
        self.W_o = nn.Linear(d_model, d_model)

    def forward(self, x, mask=None, use_cache=False):
        batch, seq_len, _ = x.shape
        Q = self.W_q(x).view(batch, seq_len, self.num_heads, self.d_k).transpose(1, 2)
        K = self.W_k(x).view(batch, seq_len, self.num_groups, self.d_k).transpose(1, 2)
        V = self.W_v(x).view(batch, seq_len, self.num_groups, self.d_k).transpose(1, 2)
        # Repeat K, V for each head in the group
        K = K.repeat_interleave(self.heads_per_group, dim=1)
        V = V.repeat_interleave(self.heads_per_group, dim=1)
        scores = torch.matmul(Q, K.transpose(-2, -1)) / (self.d_k ** 0.5)
        if mask is not None:
            scores = scores.masked_fill(mask == 0, -1e9)
        attn = F.softmax(scores, dim=-1)
        out = torch.matmul(attn, V).transpose(1, 2).contiguous().view(batch, seq_len, -1)
        return self.W_o(out)

def memory_analysis():
    d_model = 4096
    num_heads = 32
    num_groups = 8
    seq_len = 4096
    n_layers = 32
    batch = 1

    mha_kv = 2 * batch * n_layers * num_heads * seq_len * (d_model // num_heads) * 4
    mqa_kv = 2 * batch * n_layers * 1 * seq_len * (d_model // num_heads) * 4
    gqa_kv = 2 * batch * n_layers * num_groups * seq_len * (d_model // num_heads) * 4

    print(f"KV Cache Memory (GB):")
    print(f"  MHA: {mha_kv / 1e9:.2f}")
    print(f"  MQA: {mqa_kv / 1e9:.2f}")
    print(f"  GQA: {gqa_kv / 1e9:.2f}")
    print(f"  MQA saves: {(1 - mqa_kv/mha_kv) * 100:.1f}%")
    print(f"  GQA saves: {(1 - gqa_kv/mha_kv) * 100:.1f}%")
```

## Interviewer Feedback
"Excellent analysis. The implementations are correct and the memory analysis demonstrates practical understanding. Your recommendation of GQA as the Pareto-optimal choice aligns with industry practice (Llama 2, Mistral)."

## Key Takeaways
- MHA: separate K, V per head — maximum quality, highest KV cache
- MQA: single K, V shared across heads — best memory efficiency
- GQA: N groups of query heads sharing K, V — Pareto-optimal trade-off
- KV cache is the dominant memory cost during autoregressive decoding
- GQA with 8 groups for 32 heads is the recommended configuration
