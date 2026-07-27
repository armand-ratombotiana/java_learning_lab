# Mock Interview: Implement KV Cache for Autoregressive Decoding

## Scenario
You are interviewing for a inference optimization engineer role. They want to optimize LLM decoding latency.

## Interviewer Opening Question
"Implement a KV cache for autoregressive decoding. Explain how it reduces computation and memory requirements."

## Candidate Response
"The KV cache stores the key and value tensors from previous decoding steps, avoiding recomputation. During each step, we only compute Q for the current token and K, V for the current token, then append to the cache. This reduces attention complexity from O(N^2) per step to O(N) per step."

## Interviewer Probing Questions

**Q: What's the memory complexity of KV cache?**
"O(batch * num_layers * num_heads * seq_len * d_k). For a 70B model with 80 layers, 64 heads, d_k=128, seq_len=4096, batch=1: ~2.5GB per request."

**Q: How does the cache affect batching?**
"Continuous batching requires each sequence to maintain its own KV cache. Preemption requires storing cache to CPU. The total cache size limits the maximum batch size."

**Q: What optimizations can you apply?**
"KV cache quantization (FP8, INT8, INT4), cache eviction policies (H2O), sliding window cache (Mistral), and multi-query/grouped-query attention to reduce per-head cache."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
import torch.nn.functional as F
import math
from typing import Optional, Tuple

class KVCache:
    """Key-Value cache for autoregressive decoding."""
    def __init__(self, max_batch_size=1, max_seq_len=4096, num_layers=32,
                 num_heads=32, d_k=128):
        self.max_batch_size = max_batch_size
        self.max_seq_len = max_seq_len
        self.num_layers = num_layers
        self.num_heads = num_heads
        self.d_k = d_k
        # Cache shape: (num_layers, batch, heads, max_seq_len, d_k)
        self.k_cache = torch.zeros(num_layers, max_batch_size, num_heads,
                                   max_seq_len, d_k)
        self.v_cache = torch.zeros(num_layers, max_batch_size, num_heads,
                                   max_seq_len, d_k)
        self.seq_lens = torch.zeros(max_batch_size, dtype=torch.long)

    def reset(self):
        """Reset cache for new generation."""
        self.k_cache.zero_()
        self.v_cache.zero_()
        self.seq_lens.zero_()

    def update(self, layer_idx: int, k: torch.Tensor, v: torch.Tensor,
               batch_indices=None):
        """
        Append new K, V to cache.
        k, v: (batch, num_heads, 1, d_k) — current step
        """
        batch = k.shape[0]
        if batch_indices is None:
            batch_indices = torch.arange(batch)
        pos = self.seq_lens[batch_indices]  # current length for each seq
        self.k_cache[layer_idx, batch_indices, :, pos, :] = k.squeeze(2)
        self.v_cache[layer_idx, batch_indices, :, pos, :] = v.squeeze(2)
        self.seq_lens[batch_indices] += 1

    def get(self, layer_idx: int, batch_indices=None) -> Tuple[torch.Tensor, torch.Tensor]:
        """Get full cached K, V up to current length."""
        if batch_indices is None:
            batch_indices = torch.arange(self.max_batch_size)
        seq_len = self.seq_lens[batch_indices].max().item()
        k = self.k_cache[layer_idx, batch_indices, :, :seq_len, :]
        v = self.v_cache[layer_idx, batch_indices, :, :seq_len, :]
        return k, v

class AttentionWithCache(nn.Module):
    """Multi-head attention layer with KV cache support."""
    def __init__(self, d_model, num_heads, layer_idx=0):
        super().__init__()
        self.d_model = d_model
        self.num_heads = num_heads
        self.d_k = d_model // num_heads
        self.layer_idx = layer_idx

        self.W_q = nn.Linear(d_model, d_model, bias=False)
        self.W_k = nn.Linear(d_model, d_model, bias=False)
        self.W_v = nn.Linear(d_model, d_model, bias=False)
        self.W_o = nn.Linear(d_model, d_model, bias=False)

    def forward(self, x, kv_cache: Optional[KVCache] = None,
                start_pos: int = 0) -> torch.Tensor:
        batch, seq_len, _ = x.shape
        is_prefill = seq_len > 1

        Q = self.W_q(x).view(batch, seq_len, self.num_heads, self.d_k).transpose(1, 2)
        K = self.W_k(x).view(batch, seq_len, self.num_heads, self.d_k).transpose(1, 2)
        V = self.W_v(x).view(batch, seq_len, self.num_heads, self.d_k).transpose(1, 2)

        if kv_cache is not None:
            if is_prefill:
                # Prefill: store all at once
                kv_cache.update(self.layer_idx, K, V)
            else:
                # Decode step: store current, get cached
                kv_cache.update(self.layer_idx, K, V)
                K, V = kv_cache.get(self.layer_idx)

        effective_seq_len = K.shape[-2]
        scores = torch.matmul(Q, K.transpose(-2, -1)) / math.sqrt(self.d_k)

        if seq_len > 1:
            # Causal mask for prefill
            mask = torch.triu(torch.full((seq_len, seq_len), -1e9,
                              device=x.device), diagonal=1)
            scores = scores + mask

        attn = F.softmax(scores.float(), dim=-1).type_as(scores)
        out = torch.matmul(attn, V).transpose(1, 2).contiguous()
        out = out.view(batch, seq_len, self.d_model)
        return self.W_o(out)

class DecoderWithCache(nn.Module):
    """A decoder with KV cache for efficient decoding."""
    def __init__(self, vocab_size, d_model, num_heads, num_layers):
        super().__init__()
        self.embedding = nn.Embedding(vocab_size, d_model)
        self.layers = nn.ModuleList([
            AttentionWithCache(d_model, num_heads, layer_idx=i)
            for i in range(num_layers)
        ])
        self.output = nn.Linear(d_model, vocab_size)
        self.kv_cache = KVCache(num_layers=num_layers, num_heads=num_heads, d_k=d_model // num_heads)

    @torch.no_grad()
    def generate(self, prompt_tokens, max_new_tokens=100):
        self.kv_cache.reset()
        # Prefill
        x = self.embedding(prompt_tokens)
        for layer in self.layers:
            x = layer(x, self.kv_cache)
        logits = self.output(x[:, -1, :])
        next_token = logits.argmax(dim=-1, keepdim=True)

        generated = [next_token]
        for _ in range(max_new_tokens - 1):
            x = self.embedding(next_token)
            for layer in self.layers:
                x = layer(x, self.kv_cache)
            logits = self.output(x[:, -1, :])
            next_token = logits.argmax(dim=-1, keepdim=True)
            generated.append(next_token)
        return torch.cat([prompt_tokens] + generated, dim=1)

def compute_savings():
    """Compute theoretical compute savings from KV cache."""
    N = 4096  # sequence length
    i = 3000  # current decode step
    fwd_no_cache = lambda n: 2 * n * (n + 1) / 2  # O(N^2/2) FLOPs per step
    fwd_with_cache = lambda: N  # O(N) per step with cache after prefill
    total_no_cache = sum(fwd_no_cache(i) for i in range(1, N))
    total_with_cache = fwd_no_cache(N) + sum(N for i in range(N+1, N+100))
    print(f"Without cache (prefill): {fwd_no_cache(N):.0f} FLOPs")
    print(f"With cache (prefill):    {N:.0f} FLOPs per decode step")
    print(f"Total without cache:     {total_no_cache:.0f}")
    print(f"Total with cache:        {total_with_cache:.0f}")
```

## Interviewer Feedback
"Excellent implementation. You correctly separated prefill and decode phases. The cache update and retrieval logic is clean. The computational savings analysis shows you understand why KV cache is essential for efficient decoding."

## Key Takeaways
- KV cache avoids recomputing K, V for all previous tokens at each step
- Prefill phase: compute K, V for all prompt tokens and cache them
- Decode phase: compute only for one new token, append to cache
- Attention complexity reduces from O(N^2) to O(N) per decode step
- Cache memory is the primary constraint on batch size during generation
