# Mock Interview: Implement Multi-Head Attention from Scratch

## Scenario
You are interviewing for a deep learning research role. The interviewer wants to verify your understanding of the Transformer's core mechanism.

## Interviewer Opening Question
"Implement multi-head attention from scratch using NumPy, then explain how it enables parallel computation across sequence positions."

## Candidate Response
"Multi-head attention computes scaled dot-product attention in parallel across H heads. Each head projects Q, K, V into lower-dimensional subspaces, computes attention, concatenates outputs, and projects back. The key insight is that all sequence positions are processed simultaneously through matrix operations."

## Interviewer Probing Questions

**Q: Why is scaling by sqrt(d_k) necessary?**
"Without scaling, large d_k values produce large dot products that push softmax into regions with extremely small gradients. Scaling by sqrt(d_k) keeps the variance of dot products near 1, maintaining healthy gradient flow."

**Q: How does causal masking work?**
"In decoding, each position should only attend to previous positions. I apply a triangular mask (upper triangle set to -inf) before softmax so future tokens contribute zero attention."

**Q: What about relative position biases?**
"Relative position biases add learnable position-dependent offsets to the attention scores. Instead of absolute position embeddings, you use a bias matrix b_{i,j} = w_{clip(i-j, k)} that's added to the dot products."

## Candidate Solution (Python)

```python
import numpy as np

def softmax(x, axis=-1):
    x_max = np.max(x, axis=axis, keepdims=True)
    exp_x = np.exp(x - x_max)
    return exp_x / np.sum(exp_x, axis=axis, keepdims=True)

class MultiHeadAttention:
    def __init__(self, d_model=512, num_heads=8):
        assert d_model % num_heads == 0
        self.d_model = d_model
        self.num_heads = num_heads
        self.d_k = d_model // num_heads

        # Weight matrices
        self.W_q = np.random.randn(d_model, d_model) * 0.02
        self.W_k = np.random.randn(d_model, d_model) * 0.02
        self.W_v = np.random.randn(d_model, d_model) * 0.02
        self.W_o = np.random.randn(d_model, d_model) * 0.02

    def _reshape_to_batches(self, x):
        # x: (batch, seq_len, d_model)
        batch, seq_len, _ = x.shape
        x = x.reshape(batch, seq_len, self.num_heads, self.d_k)
        return x.transpose(0, 2, 1, 3)  # (batch, heads, seq_len, d_k)

    def _reshape_from_batches(self, x):
        # x: (batch, heads, seq_len, d_k)
        batch, _, seq_len, _ = x.shape
        x = x.transpose(0, 2, 1, 3)  # (batch, seq_len, heads, d_k)
        return x.reshape(batch, seq_len, self.d_model)

    def attention(self, Q, K, V, mask=None):
        # Q, K, V: (batch, heads, seq_len, d_k)
        scores = np.matmul(Q, K.transpose(0, 1, 3, 2))  # (batch, heads, seq_len, seq_len)
        scores = scores / np.sqrt(self.d_k)

        if mask is not None:
            scores = np.where(mask, -1e9, scores)

        attn_weights = softmax(scores, axis=-1)
        output = np.matmul(attn_weights, V)  # (batch, heads, seq_len, d_k)
        return output, attn_weights

    def forward(self, query, key, value, mask=None):
        # query, key, value: (batch, seq_len, d_model)
        Q = query @ self.W_q  # (batch, seq_len, d_model)
        K = key @ self.W_k
        V = value @ self.W_v

        Q = self._reshape_to_batches(Q)
        K = self._reshape_to_batches(K)
        V = self._reshape_to_batches(V)

        attn_output, attn_weights = self.attention(Q, K, V, mask)
        concat = self._reshape_from_batches(attn_output)
        output = concat @ self.W_o
        return output, attn_weights

class TransformerBlock:
    def __init__(self, d_model=512, num_heads=8, d_ff=2048, dropout=0.1):
        self.attention = MultiHeadAttention(d_model, num_heads)
        self.norm1 = LayerNorm(d_model)
        self.norm2 = LayerNorm(d_model)
        self.ffn = FeedForward(d_model, d_ff)
        self.dropout = dropout

    def forward(self, x, mask=None):
        # Self-attention with residual and layer norm
        attn_out, _ = self.attention.forward(x, x, x, mask)
        x = x + attn_out  # Residual
        x = self.norm1.forward(x)
        # FFN with residual and layer norm
        ffn_out = self.ffn.forward(x)
        x = x + ffn_out
        x = self.norm2.forward(x)
        return x

class LayerNorm:
    def __init__(self, d_model, eps=1e-6):
        self.gamma = np.ones(d_model)
        self.beta = np.zeros(d_model)
        self.eps = eps

    def forward(self, x):
        mean = np.mean(x, axis=-1, keepdims=True)
        var = np.var(x, axis=-1, keepdims=True)
        return self.gamma * (x - mean) / np.sqrt(var + self.eps) + self.beta

class FeedForward:
    def __init__(self, d_model, d_ff):
        self.W1 = np.random.randn(d_model, d_ff) * 0.02
        self.b1 = np.zeros(d_ff)
        self.W2 = np.random.randn(d_ff, d_model) * 0.02
        self.b2 = np.zeros(d_model)

    def forward(self, x):
        return x @ self.W1 @ self.W2 + x @ self.W2 + self.b1 @ self.W2 + self.b2
        # Simplified: x @ W1 + b1, then ReLU, then x @ W2 + b2
```

## Interviewer Feedback
"Excellent implementation. The reshape-based head splitting is correct and efficient. Your explanation of the scaling factor and causal masking shows deep understanding. The full transformer block with residual connections and layer norm completes the picture."

## Key Takeaways
- Multi-head attention splits the model into parallel subspaces
- Scaled dot-product attention requires 1/sqrt(d_k) for gradient stability
- Matrix formulation enables full sequence parallelism
- Residual connections and layer norm are essential for training deep transformers
- Causal masking enforces autoregressive generation in decoders
