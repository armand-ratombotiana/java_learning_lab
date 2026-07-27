# Mock Interview: Implement RoPE Positional Encoding from Scratch

## Scenario
You are interviewing for a NLP research role. The team uses Rotary Position Embeddings (RoPE) in their transformer models and wants to test your understanding.

## Interviewer Opening Question
"Implement Rotary Position Embedding (RoPE) from scratch and explain why it's preferred over absolute positional encoding."

## Candidate Response
"RoPE encodes position by rotating the query and key vectors by an angle proportional to the position index. Unlike absolute positional encodings that add position information to the input, RoPE directly modifies the attention score computation so that the dot product naturally decays with relative distance."

## Interviewer Probing Questions

**Q: How does RoPE achieve relative position awareness?**
"The rotation matrix R_theta(p) applied to Q and K means the dot product Q_p * K_q depends only on (p - q), not absolute positions. This gives the model a built-in inductive bias for relative positions."

**Q: What are the benefits over learned absolute embeddings?**
"RoPE generalizes to longer sequences than seen during training. It doesn't require learnable position parameters. It supports both linear and nonlinear interpolation for context extension."

**Q: How do you extend RoPE to longer contexts (e.g., 2x training length)?**
"NTK-aware scaling or YaRN: adjust the rotation frequency based on the ratio of extended to original context length. For 2x extension, reduce the theta base frequency by a scale factor."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
import torch.nn.functional as F
import numpy as np
import math

def precompute_freqs_cis(dim: int, seq_len: int, theta: float = 10000.0):
    """
    Precompute the frequency tensor for RoPE.
    Returns complex numbers in polar form (cos, sin).
    """
    # Compute frequencies for each dimension pair
    freqs = 1.0 / (theta ** (torch.arange(0, dim, 2).float() / dim))
    # Position indices
    t = torch.arange(seq_len, dtype=torch.float32)
    # Outer product: (seq_len, dim/2)
    freqs = torch.outer(t, freqs)
    # Convert to complex polar form
    freqs_cis = torch.polar(torch.ones_like(freqs), freqs)
    return freqs_cis  # (seq_len, dim/2)

def reshape_for_broadcast(freqs_cis, x):
    """Reshape frequency tensor for broadcasting with x."""
    ndim = x.ndim
    shape = [d if i == 1 or i == ndim - 1 else 1 for i, d in enumerate(x.shape)]
    # For attention: x is (batch, heads, seq_len, dim)
    # freqs_cis is (1, 1, seq_len, dim/2)
    return freqs_cis.view(*shape)

def apply_rotary_emb(xq, xk, freqs_cis):
    """
    Apply rotary embeddings to query and key tensors.
    xq, xk: (batch, num_heads, seq_len, dim)
    freqs_cis: (seq_len, dim/2) in complex form
    """
    # Convert to complex numbers
    # Reshape xq and xk to pair the last dimension
    xq_ = torch.view_as_complex(xq.reshape(*xq.shape[:-1], -1, 2))
    xk_ = torch.view_as_complex(xk.reshape(*xk.shape[:-1], -1, 2))

    # Reshape freq_cis for broadcasting
    freqs_cis = reshape_for_broadcast(freqs_cis, xq_)

    # Apply rotation via complex multiplication
    xq_out = torch.view_as_real(xq_ * freqs_cis).flatten(3)
    xk_out = torch.view_as_real(xk_ * freqs_cis).flatten(3)

    return xq_out.type_as(xq), xk_out.type_as(xk)

class RotaryAttention(nn.Module):
    """Self-attention with RoPE."""
    def __init__(self, d_model, num_heads, max_seq_len=4096, theta=10000.0):
        super().__init__()
        self.d_model = d_model
        self.num_heads = num_heads
        self.d_k = d_model // num_heads
        self.max_seq_len = max_seq_len

        self.W_q = nn.Linear(d_model, d_model, bias=False)
        self.W_k = nn.Linear(d_model, d_model, bias=False)
        self.W_v = nn.Linear(d_model, d_model, bias=False)
        self.W_o = nn.Linear(d_model, d_model, bias=False)

        freqs_cis = precompute_freqs_cis(self.d_k, max_seq_len * 2, theta)
        self.register_buffer("freqs_cis", freqs_cis)

    def forward(self, x, mask=None, start_pos=0):
        batch, seq_len, _ = x.shape

        Q = self.W_q(x).view(batch, seq_len, self.num_heads, self.d_k).transpose(1, 2)
        K = self.W_k(x).view(batch, seq_len, self.num_heads, self.d_k).transpose(1, 2)
        V = self.W_v(x).view(batch, seq_len, self.num_heads, self.d_k).transpose(1, 2)

        # Apply RoPE
        freqs_cis = self.freqs_cis[start_pos:start_pos + seq_len]
        Q, K = apply_rotary_emb(Q, K, freqs_cis)

        scores = torch.matmul(Q, K.transpose(-2, -1)) / math.sqrt(self.d_k)
        if mask is not None:
            scores = scores.masked_fill(mask == 0, -1e9)
        attn = F.softmax(scores, dim=-1)
        out = torch.matmul(attn, V).transpose(1, 2).contiguous().view(batch, seq_len, -1)
        return self.W_o(out)

def verify_rotation_property():
    """Verify that RoPE dot product depends only on relative position."""
    d_k = 64
    max_len = 10
    freqs_cis = precompute_freqs_cis(d_k, max_len)

    # Create the same vector at different positions
    vec = torch.randn(d_k)
    vecs = vec.unsqueeze(0).unsqueeze(0).repeat(1, 1, max_len, 1)  # (1, 1, max_len, d_k)
    # Apply RoPE
    q_rot, k_rot = apply_rotary_emb(vecs, vecs, freqs_cis)

    # Dot products should be position-dependent but translation invariant
    scores = torch.matmul(q_rot, k_rot.transpose(-2, -1))
    print("Attention score matrix (should be Toeplitz-like):")
    print(scores[0, 0].detach().numpy().round(3))

    # Verify: score[i, j] should equal score[i+k, j+k]
    for i in range(max_len - 3):
        for j in range(max_len - 3):
            d1 = scores[0, 0, i, j].item()
            d2 = scores[0, 0, i+1, j+1].item()
            assert abs(d1 - d2) < 1e-5, f"RoPE should be translation invariant"
    print("Translation invariance verified.")

def ntk_scaling(theta, scale_factor=2.0):
    """NTK-aware scaling for context extension."""
    return theta * (scale_factor ** (0.5 / (64 / 2 - 1)))
```

## Interviewer Feedback
"Excellent implementation using complex numbers for the rotation. You correctly handle the broadcasting and demonstrate the translation invariance property. The NTK scaling discussion shows practical understanding of context extension."

## Key Takeaways
- RoPE encodes position by rotating Q and K vectors in 2D subspaces
- The dot product becomes a function of relative position only
- RoPE generalizes to longer sequences than seen during training
- Implemented efficiently using complex number multiplication
- NTK-aware scaling enables context extension beyond training length
