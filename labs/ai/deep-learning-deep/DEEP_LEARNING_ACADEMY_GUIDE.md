# Deep Learning Academy — Sub-Academy Guide

> Companion guide to the `deep-learning-deep` micro-lab series.
> Each section covers interview questions, company focus areas, code snippets, and connections to real ML system design.

---

## Table of Contents

- [01 — CNN Fundamentals](#01---cnn-fundamentals)
- [02 — CNN Architectures](#02---cnn-architectures)
- [03 — RNN / LSTM / GRU](#03---rnn--lstm--gru)
- [04 — Seq2Seq with Attention](#04---seq2seq-with-attention)
- [05 — Transformer from Scratch](#05---transformer-from-scratch)
- [06 — Attention Variants](#06---attention-variants)
- [07 — Positional Encoding](#07---positional-encoding)
- [08 — Normalization in Transformers](#08---normalization-in-transformers)
- [09 — KV Cache](#09---kv-cache)
- [10 — Inference Optimization](#10---inference-optimization)

---

## 01 — CNN Fundamentals

**Folder:** `01-cnn-fundamentals/`

### Key Interview Questions

- Explain convolution 2D with a 3×3 kernel, stride 1, padding 1. What is the output size?
  - **A:** `O = (W - K + 2P) / S + 1 = (W - 3 + 2) / 1 + 1 = W`. Same-size output for same padding.
- How does dilated convolution increase the receptive field without increasing parameters?
  - **A:** Dilated (or atrous) convolution inserts zeros between kernel elements. A 3×3 kernel with dilation rate 2 has the same receptive field as 5×5 but only 9 parameters. The output size shrinks as `O = (W - d·(K-1) - 1) / S + 1`.
- What is the receptive field of a stack of three 3×3 convolutions?
  - **A:** Using the formula `RF_out = RF_in + (K - 1) · Π(stride_prev)`: after three 3×3 convs with stride 1, the effective RF is 7×7. In general: `RF = 1 + Σ_{i=1}^{L} (K_i - 1) · Π_{j=1}^{i-1} S_j`.
- How do you compute the number of parameters in a Conv2D layer?
  - **A:** `C_in · C_out · K_h · K_w + C_out` (biases).

### Company Focus

- **FAANG (computer vision roles):** Receptive field calculations, parameter counting, memory estimation for feature maps.
- **Autonomous driving (Waymo, Tesla):** Real-time constraints — how striding and depthwise convolutions reduce computation.
- **Medical imaging:** Understanding dilation for multi-scale feature extraction in segmentation (UNet variants).

### Code Example — PyTorch Conv2D Manual Forward Pass

```python
import torch
import torch.nn.functional as F

def conv2d_manual(x, weight, bias=None, stride=1, padding=0):
    """
    x:      [batch, C_in, H, W]
    weight: [C_out, C_in, K, K]
    """
    B, C_in, H, W = x.shape
    C_out, _, K, _ = weight.shape

    # Apply padding
    x_pad = F.pad(x, (padding, padding, padding, padding))
    H_pad, W_pad = x_pad.shape[2], x_pad.shape[3]

    # Compute output spatial dims
    H_out = (H_pad - K) // stride + 1
    W_out = (W_pad - K) // stride + 1

    # Unfold: extract sliding patches
    patches = x_pad.unfold(2, K, stride).unfold(3, K, stride)
    # patches shape: [B, C_in, H_out, W_out, K, K]

    # Weighted sum over kernel and input channels
    out = torch.einsum("bcijkl,oikl->boij", patches, weight)
    if bias is not None:
        out += bias.view(1, -1, 1, 1)
    return out
```

### Real ML System Design

- **EfficientNet at the edge:** CNN fundamentals directly impact mobile deployment (depthwise separable convs).
- **Feature pyramid networks:** Multi-scale feature extraction with varying strides — understanding receptive fields is essential.
- **Memory optimization:** Feature map sizes dominate memory in CNNs. Knowing the tradeoff between spatial dims, channels, and batch size is critical.

---

## 02 — CNN Architectures

**Folder:** `02-cnn-architectures/`

### Key Interview Questions

- Compare ResNet and DenseNet from a gradient flow perspective.
  - **A:** ResNet uses additive skip connections: `y = F(x) + x`. Gradients flow via identity path (multiplicative factor 1). DenseNet uses concatenation: each layer sees all previous features. Gradient flows through multiple shorter paths. DenseNet is more parameter-efficient (feature reuse). ResNet is more memory-efficient.
- Why does EfficientNet compound-scale depth, width, and resolution?
  - **A:** These dimensions are interdependent: a larger image needs more layers (depth) and channels (width) to capture fine-grained details. Compound scaling with ϕ uniformly increases all three under a FLOPS constraint: `depth = α^ϕ, width = β^ϕ, resolution = γ^ϕ`, where `α·β²·γ² ≈ 2`.
- What's the difference between MobileNet V1 and V2?
  - **A:** V1: depthwise separable convs (depthwise + pointwise). V2: inverted residuals (expansion → depthwise → projection) with linear bottlenecks (no ReLU in the final projection layer). The inverted residual is thin→wide→thin, preserving information in low-dimensional space.

### Company Focus

- **FAANG ML:** ResNet variants (ResNeXt, ResNeSt), depthwise convs in efficient backbones.
- **Apple / Mobile:** MobileNet V3, EfficientNet-Lite for on-device.
- **Meta AI:** ConvNeXt (modernized ResNet with GELU, LayerNorm, inverted bottleneck).

### Code Example — ResNet Bottleneck Block

```python
import torch
import torch.nn as nn

class Bottleneck(nn.Module):
    expansion = 4  # output channels = planes * 4

    def __init__(self, in_planes, planes, stride=1, downsample=None):
        super().__init__()
        width = planes  # bottleneck width
        self.conv1 = nn.Conv2d(in_planes, width, 1, bias=False)
        self.bn1 = nn.BatchNorm2d(width)
        self.conv2 = nn.Conv2d(width, width, 3, stride=stride,
                                padding=1, bias=False)
        self.bn2 = nn.BatchNorm2d(width)
        self.conv3 = nn.Conv2d(width, planes * 4, 1, bias=False)
        self.bn3 = nn.BatchNorm2d(planes * 4)
        self.relu = nn.ReLU(inplace=True)
        self.downsample = downsample

    def forward(self, x):
        identity = x
        if self.downsample is not None:
            identity = self.downsample(x)

        out = self.relu(self.bn1(self.conv1(x)))
        out = self.relu(self.bn2(self.conv2(out)))
        out = self.bn3(self.conv3(out))
        out += identity
        out = self.relu(out)
        return out
```

### Real ML System Design

- **ImageNet training on TPU pods:** Scaling CNN training across accelerators — large batch training with learning rate scaling.
- **Serving backbone for object detection:** Choosing between ResNet-50 (fast, 25M params) and ResNet-101 (accurate, 44M) for real-time detection.
- **Model parallelism:** Splitting ResNet stages across GPUs to handle large images or videos.

---

## 03 — RNN / LSTM / GRU

**Folder:** `03-rnn-lstm-gru/`

### Key Interview Questions

- Derive the vanishing gradient in a vanilla RNN. Show the math.
  - **A:** The gradient of the loss at step T with respect to the hidden state at step t:
    ```
    ∂L/∂h_t = ∂L/∂h_T · Π_{k=t}^{T-1} diag(f'(h_k)) · W_hh^T
    ```
    The norm `||W_hh||` multiplied repeatedly causes exponential growth (> 1) or decay (< 1). With Tanh (|f'| ≤ 0.25), even `||W_hh|| = 1` still causes vanishing.
- Why does the LSTM solve vanishing gradients?
  - **A:** The cell state uses a linear self-loop: `C_t = f_t ⊙ C_{t-1} + i_t ⊙ C̃_t`. The gradient wrt previous cell state is `∂C_t/∂C_{t-1} = diag(f_t)`, which is gated (not multiplied by a weight matrix). The forget gate can be learned to be close to 1, allowing gradients to flow for thousands of steps.
- Compare LSTM and GRU parameter counts.
  - **A:** LSTM has 4 gates × (hidden_dim² + hidden_dim·input_dim + hidden_dim) = 4 gates × weight matrices. GRU has 3 gates. For hidden_dim = 512, input_dim = 256: LSTM ≈ 4·(512² + 512·256 + 512) = 4·(262K + 131K + 512) = 4·393K ≈ 1.57M. GRU ≈ 3·(262K + 131K + 512) ≈ 1.18M. GRU is ~25% smaller.

### Company Focus

- **Google (Translate):** LSTM-based seq2seq before Transformers. Still used in low-resource settings.
- **Apple (Siri):** LSTMs for speech recognition (hybrid CTC-attention).
- **Finance / trading:** GRU for time-series forecasting (faster training, simpler).

### Code Example — LSTM from Scratch

```python
import torch
import torch.nn as nn

class LSTMCell(nn.Module):
    def __init__(self, input_size, hidden_size):
        super().__init__()
        self.hidden_size = hidden_size
        self.fc = nn.Linear(input_size + hidden_size, 4 * hidden_size)

    def forward(self, x, state):
        h, c = state
        gates = self.fc(torch.cat([x, h], dim=-1))
        f, i, o, g = gates.chunk(4, dim=-1)

        f = torch.sigmoid(f)
        i = torch.sigmoid(i)
        o = torch.sigmoid(o)
        g = torch.tanh(g)

        c_new = f * c + i * g
        h_new = o * torch.tanh(c_new)
        return h_new, c_new


class LSTM(nn.Module):
    def __init__(self, input_size, hidden_size, num_layers=1, bidirectional=False):
        super().__init__()
        self.num_layers = num_layers
        self.bidirectional = bidirectional
        num_directions = 2 if bidirectional else 1

        self.cells = nn.ModuleList()
        for layer in range(num_layers):
            for direction in range(num_directions):
                inp = input_size if layer == 0 else hidden_size * num_directions
                self.cells.append(LSTMCell(inp, hidden_size))

    def forward(self, x):
        batch, seq_len, _ = x.shape
        num_directions = 2 if self.bidirectional else 1
        h_0 = x.new_zeros(self.num_layers * num_directions, batch, self.hidden_size)
        c_0 = x.new_zeros(self.num_layers * num_directions, batch, self.hidden_size)

        # Process sequence
        h, c = h_0, c_0
        outputs = []
        for t in range(seq_len):
            h_new, c_new = [], []
            for layer in range(self.num_layers):
                idx = layer * num_directions
                h_fwd, c_fwd = self.cells[idx](x[:, t] if layer == 0 else ...)
                h_new.append(h_fwd)
                c_new.append(c_fwd)
                if self.bidirectional:
                    h_bwd, c_bwd = self.cells[idx + 1](x[:, seq_len-1-t] if layer == 0 else ...)
                    h_new.append(h_bwd)
                    c_new.append(c_bwd)
            outputs.append(torch.stack(h_new, dim=-1).mean(-1))
        return torch.stack(outputs, dim=1)
```

### Real ML System Design

- **Real-time ASR:** Bidirectional LSTMs are non-causal (look ahead). For streaming ASR, use unidirectional LSTM with attention on a fixed look-ahead window.
- **Time-series anomaly detection:** LSTM autoencoders with reconstruction loss. Choosing number of layers (2–3 max) and hidden size based on sequence length.
- **Training at scale:** Gradient clipping (norm = 5.0) is non-negotiable for stable LSTM training.

---

## 04 — Seq2Seq with Attention

**Folder:** `04-seq2seq-attention/`

### Key Interview Questions

- Compare Bahdanau and Luong attention mathematically.
  - **A:**
    - Bahdanau: `e_{t,i} = v^T · tanh(W·s_{t-1} + U·h_i)`. Additive, uses previous decoder state `s_{t-1}`, O(d²) computation per pair.
    - Luong (dot): `e_{t,i} = s_t^T · h_i`. Multiplicative, uses current decoder state `s_t`, O(d) per pair.
    - Luong (general): `e_{t,i} = s_t^T · W·h_i`. More flexible than dot.
- What is teacher forcing and why use it?
  - **A:** During training, the decoder receives the ground-truth previous token as input instead of its own prediction. Speeds convergence and stabilizes training. Problem: exposure bias — at inference, the model sees its own errors. Solution: scheduled sampling (mix ground truth and predictions with decaying probability).
- Explain beam search decoding.
  - **A:** Maintains `k` candidate hypotheses at each step. At step t, expand each hypothesis with all vocab tokens, keep top `k` by cumulative log-probability. Stops when hypotheses reach end-of-sequence or max length. Final: score all complete hypotheses and pick the best. `k=1` = greedy decoding. Increasing k improves quality at computation cost.

### Company Focus

- **Google Translate:** LSTM seq2seq with attention (pre-Transformer, still in production for some language pairs).
- **Meta (M2M-100):** Multilingual seq2seq for 100 languages.
- **TTS (Tacotron 2):** Seq2seq with location-sensitive attention for speech synthesis.

### Code Example — Bahdanau Attention

```python
import torch
import torch.nn as nn
import torch.nn.functional as F

class BahdanauAttention(nn.Module):
    def __init__(self, enc_dim, dec_dim, attn_dim):
        super().__init__()
        self.W_e = nn.Linear(enc_dim, attn_dim, bias=False)
        self.W_d = nn.Linear(dec_dim, attn_dim, bias=False)
        self.v = nn.Linear(attn_dim, 1, bias=False)

    def forward(self, dec_hidden, enc_outputs, mask=None):
        # dec_hidden: [batch, dec_dim]
        # enc_outputs: [batch, seq_len, enc_dim]
        # Returns: context [batch, enc_dim], weights [batch, seq_len]

        score = self.v(torch.tanh(
            self.W_e(enc_outputs) + self.W_d(dec_hidden).unsqueeze(1)
        )).squeeze(-1)  # [batch, seq_len]

        if mask is not None:
            score = score.masked_fill(mask == 0, -1e9)

        weights = F.softmax(score, dim=-1)
        context = torch.bmm(weights.unsqueeze(1), enc_outputs).squeeze(1)
        return context, weights
```

### Real ML System Design

- **Neural Machine Translation serving:** Batching requests, managing KV cache differences (seq2seq vs decoder-only), latency vs throughput.
- **Exposure bias mitigation:** Use reinforcement learning (REINFORCE, MIXER) or minimum risk training.
- **Edge deployment:** Quantize attention weights to int8, use beam search with small beam sizes (k=2–4).

---

## 05 — Transformer from Scratch

**Folder:** `05-transformer-from-scratch/`

### Key Interview Questions

- Derive the Transformer attention formula and explain why we scale by `√d_k`.
  - **A:** `Attention(Q,K,V) = softmax(Q·K^T / √d_k)·V`. If `q, k ~ N(0, 1)`, then `q·k ~ N(0, d_k)`. Without scaling, the variance pushes softmax into regions with vanishing gradients. Scaling by `√d_k` normalizes the variance to 1, maintaining smooth gradients.
- What is the purpose of multi-head attention?
  - **A:** Each head projects Q, K, V into lower-dimensional subspaces, computing attention in parallel. Different heads learn different aspects (syntax, semantics, position). Concatenating them gives the model the ability to jointly attend to information from different representation subspaces.
- Explain the Transformer FFN (position-wise feed-forward network).
  - **A:** `FFN(x) = max(0, x·W₁ + b₁)·W₂ + b₂`. Two linear layers with ReLU (or GELU). The inner dimension is typically 4× the model dimension (e.g., 512 → 2048 → 512). It's "position-wise": the same FFN applied independently to each position.

### Company Focus

- **OpenAI (GPT series):** Decoder-only Transformer with causal masking.
- **Google (BERT, T5, PaLM):** Encoder-only (BERT) and encoder-decoder (T5) Transformers.
- **Hugging Face:** Transformer inference infrastructure, seamless integration of all architectures.

### Code Example — Scaled Dot-Product Attention

```python
import torch
import torch.nn as nn
import torch.nn.functional as F

class MultiHeadAttention(nn.Module):
    def __init__(self, d_model, n_heads):
        super().__init__()
        assert d_model % n_heads == 0
        self.d_model = d_model
        self.n_heads = n_heads
        self.d_k = d_model // n_heads

        self.W_q = nn.Linear(d_model, d_model)
        self.W_k = nn.Linear(d_model, d_model)
        self.W_v = nn.Linear(d_model, d_model)
        self.W_o = nn.Linear(d_model, d_model)

    def forward(self, query, key, value, mask=None):
        B = query.size(0)

        # [B, seq_len, d_model] → [B, n_heads, seq_len, d_k]
        Q = self.W_q(query).view(B, -1, self.n_heads, self.d_k).transpose(1, 2)
        K = self.W_k(key).view(B, -1, self.n_heads, self.d_k).transpose(1, 2)
        V = self.W_v(value).view(B, -1, self.n_heads, self.d_k).transpose(1, 2)

        # Scaled dot-product
        scores = torch.matmul(Q, K.transpose(-2, -1)) / (self.d_k ** 0.5)
        if mask is not None:
            scores = scores.masked_fill(mask == 0, float('-inf'))

        attn = F.softmax(scores, dim=-1)
        out = torch.matmul(attn, V)

        # Concatenate heads
        out = out.transpose(1, 2).contiguous().view(B, -1, self.d_model)
        return self.W_o(out)
```

### Real ML System Design

- **Training large transformers:** Data parallelism, tensor parallelism (splitting attention across GPUs), pipeline parallelism.
- **Inference optimization:** KV cache reduces compute but increases memory. See [09 — KV Cache](#09---kv-cache).
- **Mixed precision training:** FP16/BF16 for Q, K, V projections; FP32 for softmax (precision-critical).

---

## 06 — Attention Variants

**Folder:** `06-attention-variants/`

### Key Interview Questions

- Explain self-attention vs cross-attention vs causal attention.
  - **A:**
    - **Self-attention:** Q, K, V all from the same sequence. Captures intra-sequence dependencies.
    - **Cross-attention:** Q from one sequence (e.g., decoder), K, V from another (e.g., encoder). Captures inter-sequence dependencies.
    - **Causal attention:** Q, K, V from same sequence, but with a triangular mask preventing positions from attending to future positions. Used in decoder-only LMs.
- Compare RoPE and ALiBi for position encoding in attention.
  - **A:** Both are relative position methods. RoPE (Rotary Position Embedding): rotates Q and K vectors by an angle proportional to position. Attention Q·K becomes a function of relative position. ALiBi: adds a position-dependent bias to the attention score (linearly decreasing with distance). ALiBi is simpler and enables length extrapolation better than RoPE.
- What is Flash Attention and why is it faster?
  - **A:** IO-aware exact attention algorithm. Instead of writing large intermediate matrices (S, P) to HBM, it tiles the computation in fast SRAM, recomputes attention weights during backward pass. Results in 2–4× speedup and O(n²) memory reduction. Key components: tiling, online softmax rescaling, and recomputation.

### Company Focus

- **OpenAI (GPT-4):** Flash Attention in training and inference.
- **Anthropic (Claude):** Sliding window attention for long contexts (100K+ tokens).
- **Google (PaLM):** RoPE for position encoding, multi-query attention.

### Code Example — Causal Self-Attention

```python
def causal_attention(Q, K, V, mask=None):
    # Q, K, V: [batch, seq_len, d_k]
    d_k = Q.size(-1)

    scores = torch.matmul(Q, K.transpose(-2, -1)) / (d_k ** 0.5)

    # Causal mask: triangular matrix, upper right is -inf
    seq_len = Q.size(-2)
    causal = torch.triu(
        torch.full((seq_len, seq_len), float('-inf'), device=Q.device),
        diagonal=1
    )
    scores = scores + causal.unsqueeze(0).unsqueeze(0)

    if mask is not None:
        scores = scores.masked_fill(mask == 0, float('-inf'))

    attn = F.softmax(scores, dim=-1)
    return attn @ V
```

### Real ML System Design

- **Long-context models (128K–1M tokens):** Sparse + Flash Attention combination. BigBird or Longformer patterns for pretraining, Flash Attention for fine-tuning.
- **Streaming / real-time inference:** Causal attention with limited look-ahead. Sliding window attention reduces memory proportional to window size.
- **Multimodal models (Flamingo, LLaVA):** Cross-attention between visual features (K, V) and text tokens (Q).

---

## 07 — Positional Encoding

**Folder:** `07-positional-encoding/`

### Key Interview Questions

- How do sinusoidal positional encodings encode position?
  - **A:** `PE(pos, 2i) = sin(pos / 10000^{2i/d}), PE(pos, 2i+1) = cos(pos / 10000^{2i/d})`. Each dimension has a different frequency (geometric progression). The encoding allows the model to learn relative positions because `PE(pos+k)` can be expressed as a linear function of `PE(pos)` via trigonometric identities.
- What is the advantage of learned positional embeddings (GPT-style)?
  - **A:** The model can learn task-specific position representations. The embedding matrix is [max_seq_len, d_model], looked up by position index. Flexible but cannot extrapolate beyond max_seq_len.
- How does RoPE encode position and enable length extrapolation?
  - **A:** RoPE applies a rotation matrix to Q and K:
    ```
    q_m = R(Θ, m) · W_q · x_m
    k_n = R(Θ, n) · W_k · x_n
    q_m^T · k_n = (W_q·x_m)^T · R(Θ, m-n) · (W_k·x_n)
    ```
    The attention score depends only on relative position `m-n`. Because the rotation is parameterized by frequencies, RoPE can theoretically handle any sequence length.

### Company Focus

- **Meta AI (LLaMA, LLaMA 2/3):** RoPE for position encoding.
- **Mistral AI:** RoPE + sliding window attention.
- **Google (T5):** Relative position bias (T5 bias) — learnable scalar added to attention score based on relative distance.

### Code Example — Sinusoidal Positional Encoding

```python
class SinusoidalPE(nn.Module):
    def __init__(self, d_model, max_len=2048):
        super().__init__()
        pe = torch.zeros(max_len, d_model)
        position = torch.arange(0, max_len).unsqueeze(1).float()
        div_term = torch.exp(
            torch.arange(0, d_model, 2).float()
            * (-torch.log(torch.tensor(10000.0)) / d_model)
        )
        pe[:, 0::2] = torch.sin(position * div_term)
        pe[:, 1::2] = torch.cos(position * div_term)
        self.register_buffer('pe', pe.unsqueeze(0))

    def forward(self, x):
        return x + self.pe[:, :x.size(1)]
```

### Real ML System Design

- **Extending context length:** From 2048 → 8192 → 128K tokens. Positional encoding design is critical. NTK-aware scaling and YaRN for RoPE allow context extension without retraining from scratch.
- **Training stability:** Incorrect position encoding scale can cause training instability (especially RoPE with high frequencies at early positions).
- **Decoding speed:** Positional encoding computation is negligible but needs to handle the batch with varying sequence lengths.

---

## 08 — Normalization in Transformers

**Folder:** `08-normalization-transformers/`

### Key Interview Questions

- Why LayerNorm instead of BatchNorm in Transformers?
  - **A:** LayerNorm normalizes across features (per token), BatchNorm normalizes across batch dimension. In NLP, sequence lengths vary, making BatchNorm statistics unstable. LayerNorm is sequence-length independent and works well for recurrent and transformer architectures.
- Compare pre-LN (pre-norm) and post-LN (post-norm) in Transformers.
  - **A:**
    - **Post-LN (original Transformer):** `LayerNorm(x + Sublayer(x))`. Norm after residual addition. More unstable (gradients can vanish in early layers), requires warmup.
    - **Pre-LN (modern standard):** `x + Sublayer(LayerNorm(x))`. Norm before sublayer. More stable, doesn't need warmup, allows higher learning rates.
- What is RMSNorm and why use it?
  - **A:** `RMSNorm(x) = x / sqrt(mean(x²) + ε)`. A simpler version of LayerNorm without the mean subtraction and learnable bias. Saves ~5% compute while maintaining performance. Used in LLaMA, Mistral.

### Company Focus

- **All large LM labs (OpenAI, Meta, Google, Anthropic, Mistral):** Pre-norm with RMSNorm is the standard.
- **DeepMind (Chinchilla):** Detailed study of normalization choices in scaling laws.
- **Hugging Face Transformers:** Default implementation uses pre-norm with LayerNorm.

### Code Example — Pre-LN Transformer Block

```python
class PreLNBlock(nn.Module):
    def __init__(self, d_model, n_heads, d_ff, dropout=0.1):
        super().__init__()
        self.self_attn = MultiHeadAttention(d_model, n_heads)
        self.ffn = nn.Sequential(
            nn.Linear(d_model, d_ff),
            nn.GELU(),
            nn.Dropout(dropout),
            nn.Linear(d_ff, d_model),
            nn.Dropout(dropout),
        )
        self.norm1 = nn.LayerNorm(d_model)
        self.norm2 = nn.LayerNorm(d_model)
        self.dropout = nn.Dropout(dropout)

    def forward(self, x, mask=None):
        # Pre-norm: norm before attention
        x = x + self.dropout(self.self_attn(self.norm1(x), mask))
        # Pre-norm: norm before FFN
        x = x + self.dropout(self.ffn(self.norm2(x)))
        return x
```

### Real ML System Design

- **FP8 training:** Normalization statistics computed in FP32 for precision, then cast to FP8 for computation. Critical for stable low-precision training.
- **Distributed training:** LayerNorm and RMSNorm are element-wise — no synchronization needed across devices. This is a significant advantage over BatchNorm.
- **Long sequences:** Pre-norm is numerically preferable for very deep transformers (32+ layers). Post-norm requires careful learning rate tuning at scale.

---

## 09 — KV Cache

**Folder:** `09-kv-cache/`

### Key Interview Questions

- What is the KV cache and why is it needed?
  - **A:** During autoregressive generation, each step attends to all previous tokens. Without caching, we'd recompute K and V for all previous tokens at every step. The KV cache stores these (Key, Value) tensors from past attention layers, reducing FLOPs from O(n²·d) to O(n·d) per step (just compute for the new token).
- What is the memory overhead of the KV cache?
  - **A:** For each transformer layer: `2 (K,V) × n_heads × d_head × seq_len × batch_size × precision_bytes`. For GPT-3 175B: 96 layers, n_heads=96, d_head=128, seq_len=2048, batch_size=64, FP16: `2 × 96 × 96 × 128 × 2048 × 64 × 2 bytes ≈ 3.3 TB`. This dwarfs the model weights (~350GB).
- Explain Grouped Query Attention (GQA) and Multi-Query Attention (MQA).
  - **A:**
    - **MQA (Shazeer, 2019):** Single K, V head shared across all Q heads. K cache reduces from `n_heads` to 1. Large memory savings, slight quality loss.
    - **GQA (Ainslie et al., 2023):** Compromise between MQA and full MHA. Uses `g` KV heads (e.g., 8), each shared by `n_heads/g` Q heads. Used in LLaMA 2 70B, LLaMA 3.

### Company Focus

- **OpenAI (GPT-4):** GQA or similar for memory-efficient inference.
- **Google (PaLM, Gemini):** MQA in PaLM, GQA in Gemini.
- **vLLM team (UC Berkeley):** PagedAttention for flexible KV cache management.

### Code Example — KV Cache Update

```python
def update_kv_cache(kv_cache, key, value, layer_idx):
    """
    kv_cache: dict with 'k' and 'v' tensors per layer
    key, value: [batch, n_heads, 1, d_head]  (new token only)
    Returns full K, V for attention computation.
    """
    if kv_cache is None:
        return key, value

    # Concatenate new tokens to cache
    k_cached = torch.cat([kv_cache['k'][layer_idx], key], dim=-2)
    v_cached = torch.cat([kv_cache['v'][layer_idx], value], dim=-2)

    # Update cache
    kv_cache['k'][layer_idx] = k_cached
    kv_cache['v'][layer_idx] = v_cached

    return k_cached, v_cached
```

### Real ML System Design

- **Prefill vs decode phase:** In the prefill phase (first token), the entire prompt is processed in parallel, populating the KV cache. The decode phase (subsequent tokens) is memory-bound — limited by KV cache reads.
- **Continuous batching:** Instead of waiting for all sequences in a batch to finish, dynamically add/remove sequences from the batch. vLLM's PagedAttention manages KV cache in non-contiguous pages (like virtual memory). Reduces fragmentation and increases throughput.
- **KV cache quantization:** Cache often stored in FP16 or INT8. INT4 quantization of KV cache can reduce memory by 4× with minimal quality loss (KIVI, KVQuant).

---

## 10 — Inference Optimization

**Folder:** `10-inference-optimization/`

### Key Interview Questions

- Explain speculative decoding.
  - **A:** Use a small, fast draft model (e.g., 1.3B) to generate `k` candidate tokens. The large target model (e.g., 70B) verifies them in parallel (one forward pass). Accepted tokens are kept; rejected tokens are corrected. Speedup depends on acceptance rate (typically 2–3×). Works because verification is parallel (attention on all k tokens), while the draft model is cheap.
- Compare GPTQ, AWQ, and GGUF for model quantization.
  - **A:**
    - **GPTQ:** Post-training quantization using approximate second-order information (Hessian-based). Best for GPU inference. Typically INT4 with minimal degradation.
    - **AWQ:** Activation-aware weight quantization. Scales weight channels based on activation magnitudes. Faster than GPTQ, often better quality at INT4.
    - **GGUF:** File format for CPU-friendly quantization (llama.cpp ecosystem). Supports 2–8 bit quantization. Best for local/edge inference without GPUs.
- What is continuous batching and how does vLLM implement it?
  - **A:** Traditional static batching: wait for all sequences in the batch to finish before accepting new ones. Continuous batching: add new sequences to the batch as soon as others finish (iteration-level scheduling). vLLM combines this with PagedAttention (KV cache in blocks/pages) to minimize fragmentation and maximize GPU utilization.

### Company Focus

- **Hugging Face (Text Generation Inference):** Continuous batching, tensor parallelism, and quantization for high-throughput serving.
- **Anthropic (Claude):** Long-context inference with KV cache optimization, speculative decoding.
- **Groq:** LPU (Language Processing Unit) for ultra-low-latency inference.

### Code Example — vLLM PagedAttention Concept

```python
class PagedAttention:
    """
    Conceptual illustration of PagedAttention's block table.
    KV cache is stored in fixed-size blocks (pages).
    Each sequence has a logical-to-physical block mapping.
    """
    def __init__(self, block_size=16, num_blocks=1024):
        self.block_size = block_size
        # Physical KV cache storage: [num_blocks, block_size, n_heads, d_head]
        self.k_cache = torch.zeros(num_blocks, block_size, ...)
        self.v_cache = torch.zeros(num_blocks, block_size, ...)
        self.free_blocks = set(range(num_blocks))
        self.block_tables = {}  # seq_id -> [block_id, ...]

    def allocate_blocks(self, seq_id, num_blocks_needed):
        blocks = list(self.free_blocks)[:num_blocks_needed]
        self.free_blocks.difference_update(blocks)
        self.block_tables[seq_id] = blocks

    def write(self, seq_id, pos, key, value):
        block_idx = pos // self.block_size
        offset = pos % self.block_size
        physical_block = self.block_tables[seq_id][block_idx]
        self.k_cache[physical_block, offset] = key
        self.v_cache[physical_block, offset] = value
```

### Real ML System Design

- **Latency vs throughput:** For chatbots (low latency): use small KV cache, low batch size, tensor parallelism on fewer GPUs. For batch processing (high throughput): maximize batch size with continuous batching, use model parallelism across more GPUs.
- **Serving stack components:**
  1. **Model server:** vLLM, TensorRT-LLM, TGI, llama.cpp
  2. **Scheduler:** Continuous batching engine
  3. **Quantization:** INT4 weights + FP16 KV cache
  4. **Parallelism:** Tensor parallelism (within node) + pipeline parallelism (across nodes)
  5. **Scheduling:** Dynamic batching with priority queues
- **Benchmarking:** Key metrics: Time To First Token (TTFT), Inter-Token Latency (ITL), tokens per second per user, tokens per second per GPU, max supported context length.
- **Memory budget for LLM inference:**
  ```
  Total GPU memory = Model weights (quantized)
                   + KV cache (per seq × max seq len)
                   + Activations (during decode: O(batch × d_model))
                   + Overhead (CUDA kernels, framework)
  ```
  Typically: for a 70B model at INT4, each GPU needs ~40GB for weights + KV cache memory proportional to batch size × seq length.

---

> *End of DEEP_LEARNING_ACADEMY_GUIDE.md — Use alongside each micro-lab for focused interview prep and system design connections.*
