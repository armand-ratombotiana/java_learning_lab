# Mock Interview: Transformers

## Question 1: Scaled Dot-Product Attention
**Q**: Implement scaled dot-product attention from scratch.

**A**:
```python
def scaled_dot_product_attention(Q, K, V, mask=None):
    d_k = Q.shape[-1]
    scores = Q @ K.transpose(-2, -1) / np.sqrt(d_k)
    if mask is not None:
        scores = np.where(mask, scores, -1e9)
    weights = softmax(scores, axis=-1)
    return weights @ V

def softmax(x, axis=-1):
    x = x - np.max(x, axis=axis, keepdims=True)
    exp = np.exp(x)
    return exp / np.sum(exp, axis=axis, keepdims=True)
```

**Why scale by sqrt(d_k)?**: Prevents dot products from growing large (variance ~ d_k). Large values push softmax into regions with tiny gradients, slowing training.

## Question 2: Multi-Head Attention
**Q**: Why multi-head attention? How does it work?

**A**: Multi-head attention projects Q/K/V into h subspaces, computes attention in each, concatenates.

Benefits:
- Each head can focus on different relationships (syntax, semantics, position)
- Provides multiple representation subspaces
- Increases model capacity without increasing per-head dimension

```
output = Concat(head_1, ..., head_h) * W_O
where head_i = Attention(Q*W_Q_i, K*W_K_i, V*W_V_i)
```

## Question 3: Transformer Architecture
**Q**: Design a complete transformer block. Explain each component.

**A**: 
```python
class TransformerBlock:
    def __init__(self, d_model, n_heads, d_ff, dropout=0.1):
        self.attention = MultiHeadAttention(d_model, n_heads)
        self.ffn = nn.Sequential(
            nn.Linear(d_model, d_ff), nn.GELU(), nn.Linear(d_ff, d_model))
        self.norm1 = nn.LayerNorm(d_model)
        self.norm2 = nn.LayerNorm(d_model)
        self.dropout = nn.Dropout(dropout)

    def forward(self, x, mask=None):
        x = x + self.dropout(self.attention(self.norm1(x), mask=mask))
        x = x + self.dropout(self.ffn(self.norm2(x)))
        return x
```

Key design choices: Pre-norm (more stable training), GELU activation (smoother than ReLU), residual connections (gradient flow).

## Question 4: Positional Encoding
**Q**: Compare sinusoidal vs learned positional encodings. What about RoPE?

**A**:
- **Sinusoidal**: Fixed, uses sin/cos frequencies. Can extrapolate to longer sequences. No learned parameters.
- **Learned**: Embedding layer. More flexible. Cannot extrapolate beyond max length seen in training.
- **RoPE (Rotary Position Embedding)**: Rotates Q and K based on position. Preserves relative positions through attention dot product. Used in Llama, Mistral. Best of both worlds.

## Question 5: Transformer Optimization
**Q**: How would you optimize transformer training/serving?

**A**: Training:
- Flash Attention (fused kernel, no NxN attention matrix)
- Mixed precision (BF16)
- Activation checkpointing (trade compute for memory)
- Gradient accumulation
- ZeRO optimization (sharded optimizer/gradients/params)

Serving:
- KV cache (cache K,V for each token in generation)
- Grouped query attention (GQA) or MQA (fewer KV heads)
- Quantization (INT8, INT4 with AWQ/GPTQ)
- PagedAttention (vLLM, reduce KV cache fragmentation)
- Speculative decoding (use draft model for faster generation)
- Continuous batching (add/remove sequences dynamically)
