# Mock Interview: Optimize Inference for a 70B Model on a Single GPU

## Scenario
You are interviewing for a ML infrastructure engineer role. The team needs to serve a 70B parameter model on a single A100-80GB GPU.

## Interviewer Opening Question
"A 70B model in FP16 requires 140GB of memory. How do you fit it on a single 80GB GPU while maintaining reasonable throughput?"

## Candidate Response
"I'd use a combination of techniques: 4-bit quantization (GPTQ/AWQ) to reduce memory to ~35GB, speculative decoding to reduce latency, and KV cache optimization (GQA + quantization). With INT4 weight-only quantization, the model fits in ~40GB leaving room for KV cache and activations."

## Interviewer Probing Questions

**Q: How does 4-bit quantization work and what's the quality impact?**
"INT4 quantization maps FP16 weights to 4-bit integers using group-wise scaling factors (groups of 32-128 weights). GPTQ uses a calibration set to optimize quantization to minimize output perturbation. Quality loss is typically < 1% on perplexity benchmarks."

**Q: What's the optimal batch size for a single GPU?**
"With INT4 weights, available memory = 80 - 35 (model) = 45GB for KV cache + activations. With GQA, KV cache per token is ~2MB. So max batch size is ~min(45GB / (2MB * seq_len), compute-bound). For seq_len=4096, that's about batch=8-16."

**Q: What about tensor parallelism on a single GPU?**
"Not needed — tensor parallelism splits across GPUs. On a single GPU, use fused kernels (FlashAttention, xformers) and torch.compile for operator fusion."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
from dataclasses import dataclass
from typing import Optional, Tuple
import time

@dataclass
class OptimizationConfig:
    quantization: str = "int4"  # int4, int8, fp8, fp16
    use_flash_attention: bool = True
    use_speculative_decoding: bool = True
    kv_cache_quantization: bool = True
    batch_size: int = 8
    max_seq_len: int = 4096

class INT4Linear(nn.Module):
    """Simulated INT4 quantized linear layer."""
    def __init__(self, in_features, out_features, group_size=128):
        super().__init__()
        self.in_features = in_features
        self.out_features = out_features
        self.group_size = group_size
        # Store FP16 weights (simulated — real impl would store INT4)
        self.weight = nn.Parameter(torch.randn(out_features, in_features))
        self.scales = nn.Parameter(torch.randn(out_features, in_features // group_size))
        self.zeros = nn.Parameter(torch.zeros(out_features, in_features // group_size))

    def quantize(self):
        """Quantize weights to INT4 (simulated)."""
        w = self.weight.data
        num_groups = w.shape[1] // self.group_size
        w_reshaped = w.view(w.shape[0], num_groups, self.group_size)
        # Compute scales and zero points
        max_val = w_reshaped.abs().max(dim=-1, keepdim=True).values
        self.scales.data = max_val.squeeze(-1) / 7.0  # INT4 range is -7 to 7
        self.zeros.data = torch.zeros_like(self.scales)
        # Quantize
        w_int = torch.round(w_reshaped / self.scales.unsqueeze(-1))
        w_int = torch.clamp(w_int, -7, 7)
        # Dequantize (simulated)
        self.weight.data = (w_int * self.scales.unsqueeze(-1)).view(w.shape)
        return self

    def forward(self, x):
        return F.linear(x, self.weight)

class MemoryEstimator:
    def __init__(self, config: OptimizationConfig):
        self.config = config

    def estimate_usage(self, num_layers=80, num_heads=64, d_model=8192, d_k=128):
        model_params = 70e9
        bytes_per_param = {"int4": 0.5, "int8": 1, "fp16": 2, "fp8": 1}
        bpw = bytes_per_param[self.config.quantization]
        model_memory = model_params * bpw

        # KV cache (with GQA — 8 KV heads)
        kv_heads = 8
        kv_cache_per_token = 2 * num_layers * kv_heads * d_k * bpw
        total_kv_cache = kv_cache_per_token * self.config.max_seq_len * self.config.batch_size

        # Activations
        activation_memory = self.config.batch_size * self.config.max_seq_len * d_model * 2 * 4

        total = model_memory + total_kv_cache + activation_memory
        return {
            "model_gb": model_memory / 1e9,
            "kv_cache_gb": total_kv_cache / 1e9,
            "activations_gb": activation_memory / 1e9,
            "total_gb": total / 1e9,
            "fits_on_a100": (total / 1e9) < 80
        }

class DecodeOptimizer:
    def __init__(self, config: OptimizationConfig):
        self.config = config
        self.use_flash = config.use_flash_attention

    def flash_attention(self, Q, K, V, mask=None):
        """FlashAttention: tiling-based attention that avoids O(N^2) memory."""
        if self.use_flash:
            # Use Flashattention kernel if available
            try:
                from flash_attn import flash_attn_func
                return flash_attn_func(Q, K, V, causal=True)
            except ImportError:
                pass
        # Fallback to standard attention with memory-efficient computation
        chunk_size = 512
        batch, heads, seq_len, d_k = Q.shape
        output = torch.zeros_like(Q)
        for i in range(0, seq_len, chunk_size):
            Q_chunk = Q[:, :, i:i+chunk_size, :]
            scores = torch.matmul(Q_chunk, K.transpose(-2, -1)) / (d_k ** 0.5)
            if mask is not None:
                scores = scores + mask[:, :, i:i+chunk_size, :]
            attn = torch.softmax(scores, dim=-1)
            output[:, :, i:i+chunk_size, :] = torch.matmul(attn, V)
        return output

    def speculative_decoding(self, model, draft_model, prompt, num_draft=5):
        """
        Speculative decoding using a smaller draft model.
        Draft model generates k tokens speculatively; target model validates.
        """
        accepted_tokens = []
        while True:
            # Draft: generate k tokens
            draft_tokens = draft_model.generate(prompt, max_new_tokens=num_draft)
            # Target: verify in a single forward pass
            target_logits = model.forward(draft_tokens)
            # Rejection sampling: accept tokens where target agrees
            for i in range(num_draft):
                if torch.argmax(target_logits[i]) == draft_tokens[i]:
                    accepted_tokens.append(draft_tokens[i])
                else:
                    # Resample at the first rejection
                    accepted_tokens.append(torch.argmax(target_logits[i]))
                    break
            if len(accepted_tokens) >= num_draft:
                break
        return accepted_tokens

@torch.compile
def fused_decoder_step(x, embedding, layers, output_layer):
    """Fused decoding step using torch.compile."""
    x = embedding(x)
    for layer in layers:
        x = layer(x)
    return output_layer(x)

def throughput_benchmark(config: OptimizationConfig):
    """Estimate throughput with given optimization config."""
    estimated_tokens_per_second = {
        "fp16_no_opt": 2,
        "int4_no_opt": 8,
        "int4_flash": 15,
        "int4_flash_spec": 25,
    }
    key = f"{config.quantization}_{'flash' if config.use_flash_attention else 'no_opt'}"
    if config.use_speculative_decoding:
        key += "_spec"
    tps = estimated_tokens_per_second.get(key, 5)
    latency_per_token_ms = 1000.0 / tps
    print(f"Configuration: {key}")
    print(f"Throughput: {tps} tokens/sec")
    print(f"Latency: {latency_per_token_ms:.1f}ms per token")
    print(f"Memory: {MemoryEstimator(config).estimate_usage()['total_gb']:.1f}GB")
    return tps
```

## Interviewer Feedback
"Comprehensive and practical. You covered quantization, memory budgeting, FlashAttention, speculative decoding, and torch.compile. The memory estimation shows you know exactly where every byte goes. This is a production-ready answer."

## Key Takeaways
- INT4 quantization reduces 70B model from 140GB to ~35GB
- GQA with 8 KV heads reduces KV cache by 8x
- FlashAttention eliminates O(N^2) memory bottleneck in attention
- Speculative decoding gives 2-3x latency improvement
- torch.compile fuses operations for additional throughput gains
