# Deep Learning Interview Guide — AI Academy

> Comprehensive interview preparation covering all major deep learning topics.
> Each section includes formulations, diagrams (ASCII), code snippets, and interview Q&A.

---

## Table of Contents

1. [CNN Architectures](#1-cnn-architectures)
2. [RNN / LSTM / GRU](#2-rnn--lstm--gru)
3. [Attention Mechanisms](#3-attention-mechanisms)
4. [GANs](#4-gans)
5. [Diffusion Models](#5-diffusion-models)
6. [VAEs](#6-vaes)
7. [Graph Neural Networks](#7-graph-neural-networks)
8. [Loss Functions](#8-loss-functions)
9. [Optimization](#9-optimization)

---

## 1. CNN Architectures

### Overview

CNNs have evolved from simple stacked convolutions to sophisticated multi-path designs. Understanding the lineage is essential for any ML interview.

---

### 1.1 LeNet-5 (1998)

**Architecture:**

```
Input (32x32)
    ↓
Conv1 (6 filters, 5x5) → Tanh → AvgPool (2x2)
    ↓
Conv2 (16 filters, 5x5) → Tanh → AvgPool (2x2)
    ↓
FC(120) → Tanh → FC(84) → Tanh → FC(10) → Softmax
```

**Key innovation:** First successful CNN for handwritten digit recognition (MNIST). Introduced the concept of alternating convolution and subsampling layers.

**Parameters:** ~60k (tiny by modern standards).

**Interview Q:** Why did LeNet use `tanh` instead of `ReLU`?

**A:** ReLU was popularized later (2010). Tanh was the default activation because it saturates and is zero-centered, which helped with the smaller datasets available at the time.

---

### 1.2 AlexNet (2012)

**Architecture:**

```
Input (227x227x3)
    ↓
Conv(96, 11x11, s4) → ReLU → LocalNorm → MaxPool(3x3, s2)
    ↓
Conv(256, 5x5) → ReLU → LocalNorm → MaxPool(3x3, s2)
    ↓
Conv(384, 3x3) → ReLU
    ↓
Conv(384, 3x3) → ReLU
    ↓
Conv(256, 3x3) → ReLU → MaxPool(3x3, s2)
    ↓
FC(4096) → ReLU → Dropout(0.5)
    ↓
FC(4096) → ReLU → Dropout(0.5)
    ↓
FC(1000) → Softmax
```

**Key innovation:** Won ImageNet 2012 by a large margin. First to use ReLU (faster training), Dropout (regularization), and data augmentation at scale. Trained on two GPUs with model parallelism.

**Parameters:** ~60M.

**Interview Q:** Why did AlexNet use 11x11 kernels in the first layer?

**A:** Large receptive field to capture low-level features (edges, blobs) at high resolution. Modern networks use smaller kernels (3x3) stacked deeper for the same effective receptive field with fewer parameters.

**Interview Q:** How did AlexNet handle the 224x224 vs 227x227 discrepancy?

**A:** The actual input was 227x227. Many papers incorrectly cite 224x224. 227 = 224 + 2 (padding) + 1 (extra from 11x11 conv with stride 4).

---

### 1.3 VGGNet (2014)

**Architecture:**

```
Input (224x224x3)
    ↓
[Conv2D(64, 3x3) → ReLU] × 2 → MaxPool(2x2, s2)        # 112x112x64
    ↓
[Conv2D(128, 3x3) → ReLU] × 2 → MaxPool(2x2, s2)       # 56x56x128
    ↓
[Conv2D(256, 3x3) → ReLU] × 3 → MaxPool(2x2, s2)       # 28x28x256
    ↓
[Conv2D(512, 3x3) → ReLU] × 3 → MaxPool(2x2, s2)       # 14x14x512
    ↓
[Conv2D(512, 3x3) → ReLU] × 3 → MaxPool(2x2, s2)       # 7x7x512
    ↓
FC(4096) → ReLU → Dropout → FC(4096) → ReLU → Dropout → FC(1000) → Softmax
```

**Key innovation:** Showed that _depth_ matters. Used only 3×3 convolutions stacked deeper (VGG16 = 16 weight layers, VGG19 = 19). Very simple, uniform architecture.

**Parameters:** VGG16: ~138M (mostly in FC layers).

**Interview Q:** Why use two 3×3 convolutions instead of one 5×5?

**A:** Two 3×3 layers have the same effective receptive field (5×5) but with fewer parameters: 2 × (3×3×C²) = 18C² vs 1 × (5×5×C²) = 25C². Also, two ReLU activations add more non-linearity.

**Interview Q:** What's the main downside of VGG?

**A:** Extremely parameter-heavy (138M), slow to train, large model files (>500MB). The FC layers alone account for ~100M parameters.

---

### 1.4 Inception (GoogLeNet, 2014)

**Architecture (Inception module):**

```
          Input (prev layer)
               |
    ┌────┬─────┼─────┬────┐
   1x1  1x1   1x1   3x3  3x3 MaxPool
    ↓    ↓     ↓     ↓     ↓
   3x3  5x5   1x1   1x1   1x1
    ↓    ↓     ↓     ↓     ↓
         └───┬──┴─────┴─────┘
           Filter Concatenation
```

**Key innovation:** Network-in-network concept with 1×1 convolutions for dimensionality reduction. Multiple filter sizes at the same level capture multi-scale features. Auxiliary classifiers for gradient flow.

**Parameters:** ~7M (1/10th of AlexNet).

**Interview Q:** What is the purpose of the 1×1 convolution in Inception?

**A:** Dimensionality reduction (bottleneck). A 1×1 conv with fewer filters than input channels reduces channels before expensive 3×3/5×5 convs, saving computation. E.g., 256 → 64 → 256 vs 256 → 256 directly.

**Interview Q:** Why use auxiliary classifiers?

**A:** Combat vanishing gradients in deep networks. They provide additional gradient signals at intermediate layers. Inception v2/v3 showed they aren't strictly necessary but help regularization.

---

### 1.5 ResNet (2015)

**Residual Block:**

```
    x
    |
[Conv → BN → ReLU → Conv → BN]
    |                    |
    └────────────────────┘ (+)
           |
          ReLU
          ↓
       F(x) + x
```

**Bottleneck Block (ResNet-50/101/152):**

```
    x (256-d)
    |
Conv2D(64, 1×1) → BN → ReLU      # bottleneck: 256→64
    ↓
Conv2D(64, 3×3) → BN → ReLU      # spatial features
    ↓
Conv2D(256, 1×1) → BN            # expand: 64→256
    ↓
    └────────────────────┐ (+)
          ReLU            x (1×1 conv for dim match)
```

**Key innovation:** Skip connections (identity shortcuts) solve the _degradation problem_ — deeper networks perform _worse_ due to optimization difficulty, not overfitting. Residual mapping: `H(x) = F(x) + x` makes it easier to learn identity.

**Why skip connections work:**

1. **Gradient flow:** Gradients can flow directly through the skip path during backpropagation (`∂L/∂x = ∂L/∂y × (1 + ∂F/∂x)`), avoiding vanishing gradients.
2. **Ensemble interpretation:** ResNets behave like an ensemble of shallow networks (Veit et al., 2016).
3. **Better optimization:** Residual functions are easier to optimize than unreferenced mappings.

**Parameters:** ResNet-50: 25.6M, ResNet-101: 44.5M, ResNet-152: 60.2M.

**Interview Q:** What if the skip connection dimensions don't match (e.g., 256→512)?

**A:** Use a 1×1 convolution with stride 2 (for spatial downsampling) on the shortcut to project `x` to match `F(x)` dimensions: `y = F(x) + Wx`.

**Interview Q:** Why does ResNet use BN after every conv and before ReLU?

**A:** Batch Normalization stabilizes training by normalizing layer inputs, allowing higher learning rates and reducing sensitivity to initialization. Placing ReLU _after_ the addition (post-activation) or _before_ (pre-activation, ResNet v2) is a design choice — v2 shows pre-activation works better.

**Interview Q:** Does ResNet actually solve vanishing gradients?

**A:** Yes, mathematically. With skip connections, the gradient wrt input is `∂L/∂x = ∂L/∂y × (1 + ∂F/∂x)`. The `1` term ensures the gradient never vanishes. Even if `∂F/∂x → 0`, the gradient is still `∂L/∂y`.

---

### 1.6 DenseNet (2017)

**Dense Block:**

```
x0 → [BN→ReLU→Conv] → x1
                     ↓
x0,x1 → [BN→ReLU→Conv] → x2
                        ↓
x0,x1,x2 → [BN→ReLU→Conv] → x3
```

**Key innovation:** Each layer receives _all_ previous feature maps as input (concatenation, not addition). Growth rate `k` controls how many new channels each layer produces. Requires fewer parameters than ResNet because feature maps are reused.

**Transition layer:** BN → 1×1 Conv (compression) → 2×2 AvgPool.

**Interview Q:** DenseNet vs ResNet — when would you choose each?

**A:** DenseNet is more parameter-efficient (better feature reuse) and has smoother gradient flow. ResNet is more memory-efficient (DenseNet stores all intermediate features for concatenation). DenseNet tends to overfit less on small data. ResNet is more widely deployed in production.

---

### 1.7 EfficientNet (2019)

**Key innovation:** _Compound scaling_ — uniformly scale depth, width, and resolution using a compound coefficient ϕ:

```
depth:     d = α^ϕ
width:     w = β^ϕ
resolution: r = γ^ϕ
where α·β²·γ² ≈ 2 (FLOPS constraint)
```

**Base architecture:** EfficientNet-B0 uses MBConv blocks (MobileNetV2 inverted bottleneck + SE attention).

| Model | Top-1 Acc | Params | FLOPS |
|-------|-----------|--------|-------|
| B0    | 77.1%     | 5.3M   | 0.39B |
| B3    | 81.1%     | 12M    | 1.8B  |
| B7    | 84.3%     | 66M    | 37B   |

**Interview Q:** Why does compound scaling work better than scaling a single dimension?

**A:** Depth, width, and resolution are interdependent. A larger image needs more layers (depth) to capture larger patterns and more channels (width) to capture fine-grained details. Compound scaling balances all three.

**Interview Q:** What is an MBConv block?

**A:** MobilenetV2-style inverted bottleneck: expand (1×1, 6×), depthwise conv (3×3), squeeze-and-excitation, project (1×1). The "inverted" part means the bottleneck is wide-narrow-wide instead of narrow-wide-narrow.

---

### 1.8 Depthwise Separable Convolutions

**Standard conv:**
- One filter processes _all_ input channels simultaneously.
- Computation: `K × K × C_in × C_out × H × W`

**Depthwise separable:**
1. **Depthwise:** One filter per input channel (no cross-channel mixing). `K × K × C_in × H × W`
2. **Pointwise:** 1×1 conv on depthwise output. `1 × 1 × C_in × C_out × H × W`

**Computation ratio:**
```
(Depthwise + Pointwise) / Standard
= (K²·C_in·H·W + C_in·C_out·H·W) / (K²·C_in·C_out·H·W)
= 1/C_out + 1/K²
≈ 1/K²  (when C_out is large)
```

For 3×3: ~1/9 the computation!

**MobileNet:** Uses depthwise separable convs. MobileNetV2 adds inverted residuals (thin→wide→thin) with linear bottlenecks.

**Xception:** Extreme Inception — replaces all Inception modules with depthwise separable convs.

**Interview Q:** Why does depthwise separation work despite losing cross-channel interaction?

**A:** The pointwise (1×1) conv handles cross-channel mixing efficiently. The spatial conv and channel mixing are decoupled, which is a cheaper factorization of the full convolution.

**Interview Q:** When would you NOT use depthwise separable convs?

**A:** On hardware without good depthwise conv kernel optimization (e.g., some older GPUs). Depthwise convs have lower compute intensity and are memory-bandwidth bound. Also, very small models may lose capacity.

---

## 2. RNN / LSTM / GRU

### 2.1 Vanilla RNN

**Formulation:**

```
h_t = tanh(W_hh · h_{t-1} + W_xh · x_t + b_h)
y_t = softmax(W_hy · h_t + b_y)
```

**Unfolded:**

```
y_1      y_2      y_3      y_T
↑        ↑        ↑        ↑
h_0 → h_1 → h_2 → h_3 → ... → h_T
↑        ↑        ↑        ↑
x_1      x_2      x_3      x_T
```

**Vanishing Gradient Problem:**

During backpropagation through time (BPTT):

```
∂L/∂h_t = ∂L/∂h_T × Π_{k=t}^{T-1} diag(f'(h_k)) × W_hh
```

The product of Jacobians causes gradients to vanish (or explode) exponentially with sequence length because `||W_hh|| < 1` leads to decay and `||W_hh|| > 1` leads to explosion.

**Interview Q:** How does tanh make vanishing gradients worse in vanilla RNNs?

**A:** tanh derivatives are ≤ 0.25 (max at 0). Repeated multiplication causes gradient decay. ReLU doesn't help here because unbounded activations cause explosion instead.

**Interview Q:** How do you handle vanishing gradients in vanilla RNNs?

**A:** Gradient clipping (norm clipping to 5 or 10), careful initialization (identity matrix for W_hh), ReLU activation, and most importantly — switching to LSTM/GRU with gating mechanisms.

---

### 2.2 LSTM (Hochreiter & Schmidhuber, 1997)

**Formulation:**

```
Forget gate:    f_t = σ(W_f · [h_{t-1}, x_t] + b_f)
Input gate:     i_t = σ(W_i · [h_{t-1}, x_t] + b_i)
Candidate:      C̃_t = tanh(W_C · [h_{t-1}, x_t] + b_C)
Cell state:     C_t = f_t ⊙ C_{t-1} + i_t ⊙ C̃_t
Output gate:    o_t = σ(W_o · [h_{t-1}, x_t] + b_o)
Hidden state:   h_t = o_t ⊙ tanh(C_t)
```

**Architecture diagram:**

```
C_{t-1} ──────┬───────┬────────────── C_t
              |       |
    f_t ────×(forget) |
              |       |
    i_t ────×─────────┘(input)
              |       |
    C̃_t ────+─────────┘(candidate)
              |
              |         o_t
              └────tanh──×──── h_t
```

**Why the gradient doesn't vanish:**

The cell state has a linear self-loop weighted by the forget gate:

```
∂C_t / ∂C_{t-1} = f_t
```

Gradients flow through the cell state via multiplication by `f_t`, not by a weight matrix. The forget gate can be learned to be close to 1 (remember) or 0 (forget). This additive + gated structure is the key innovation.

**Interview Q:** Walk through the purpose of each gate.

**A:**
- **Forget gate** `f_t`: Decides what to discard from the previous cell state. Sigmoid outputs [0,1]; 1 = keep, 0 = forget.
- **Input gate** `i_t`: Decides which values to update in the cell state.
- **Candidate** `C̃_t`: New candidate values (via tanh) to add.
- **Output gate** `o_t`: Decides what part of the cell state to output as the hidden state.

**Interview Q:** Why does LSTM use sigmoid for gates and tanh for candidate/output?

**A:** Sigmoid outputs [0,1] — natural for gating (on/off). Tanh outputs [-1,1] — natural for candidate values that can add or subtract information. Also, tanh is zero-centered, which helps gradient flow.

**Code — LSTM forward pass:**

```python
def lstm_step(x, h_prev, c_prev, params):
    W_f, W_i, W_c, W_o, b_f, b_i, b_c, b_o = params

    concat = np.concatenate([h_prev, x])

    f = sigmoid(W_f @ concat + b_f)
    i = sigmoid(W_i @ concat + b_i)
    c_tilde = np.tanh(W_c @ concat + b_c)
    c = f * c_prev + i * c_tilde
    o = sigmoid(W_o @ concat + b_o)
    h = o * np.tanh(c)
    return h, c
```

---

### 2.3 GRU (Cho et al., 2014)

**Formulation:**

```
Update gate:    z_t = σ(W_z · [h_{t-1}, x_t] + b_z)
Reset gate:     r_t = σ(W_r · [h_{t-1}, x_t] + b_r)
Candidate:      h̃_t = tanh(W_h · [r_t ⊙ h_{t-1}, x_t] + b_h)
Hidden state:   h_t = (1 - z_t) ⊙ h_{t-1} + z_t ⊙ h̃_t
```

**GRU vs LSTM — architectural comparison:**

| Feature | LSTM | GRU |
|---------|------|-----|
| Gates | forget, input, output (3) | update, reset (2) |
| Cell state | Separate `c_t` | No separate cell state |
| Parameters | ~4× hidden_dim² | ~3× hidden_dim² |
| Computation | More | Less (~25% fewer params) |
| Performance | Often better on long sequences | Competitive, faster training |

**Interview Q:** When would you choose GRU over LSTM?

**A:** GRU is simpler, faster to train, and needs less data. If you have limited compute or small datasets, start with GRU. LSTM with its explicit cell state can capture longer dependencies and is often preferred for sequence lengths > 100.

**Interview Q:** How does GRU's update gate compare to LSTM's gates?

**A:** The update gate `z_t` simultaneously controls both forgetting (like LSTM's forget gate) and input integration (like LSTM's input gate). When `z_t ≈ 1`, new information is kept; when `z_t ≈ 0`, previous state is fully retained.

---

### 2.4 Bidirectional RNNs

**Architecture:**

```
Output:          y_1      y_2      y_3      y_T
                ↗  ↖     ↗  ↖     ↗  ↖     ↗  ↖
Forward:  → h_1_f → h_2_f → h_3_f → ... → h_T_f
Backward: h_1_b ← h_2_b ← h_3_b ← ... ← h_T_b ←
                ↖  ↗     ↖  ↗     ↖  ↗     ↖  ↗
Input:           x_1      x_2      x_3      x_T
```

**Interview Q:** When do you use bidirectional vs unidirectional RNNs?

**A:** Bidirectional: when you have access to the entire sequence (e.g., text classification, NER, translation). Unidirectional: when the task is online/real-time (e.g., speech recognition, stock prediction) where future tokens are unavailable.

---

### 2.5 Stacked (Deep) RNNs

```
y_t
↑
h_t^(L) = RNN_L(h_{t-1}^(L), h_t^(L-1))
↑
...
↑
h_t^(2) = RNN_2(h_{t-1}^(2), h_t^(1))
↑
h_t^(1) = RNN_1(h_{t-1}^(1), x_t)
↑
x_t
```

**Interview Q:** What's the tradeoff with stacking RNN layers?

**A:** More layers → higher capacity, can learn hierarchical temporal features. But: (1) vanishing gradients get worse with depth, (2) slower training, (3) more prone to overfitting. Typically 2–3 layers max in practice.

**Interview Q:** How does stacked LSTM differ from a single LSTM with more hidden units?

**A:** Stacked LSTMs learn hierarchical temporal representations (faster-changing features in lower layers, slower in upper layers). A single wider LSTM captures more complex single-level dynamics but not hierarchical ones.

---

## 3. Attention Mechanisms

### 3.1 Bahdanau Attention (Additive, 2014)

**Motivation:** In seq2seq models, the encoder compresses the entire source sequence into a single fixed-size vector (context). This is a bottleneck — Bahdanau attention allows the decoder to "look at" different source positions at each step.

**Formulation:**

```
Energy:     e_{t,i} = v_a^T · tanh(W_a · s_{t-1} + U_a · h_i)
Weights:    α_{t,i} = softmax(e_{t,i})
Context:    c_t = Σ_i α_{t,i} · h_i
Decoder:    s_t = f(s_{t-1}, y_{t-1}, c_t)
Output:     p(y_t | y_{<t}, x) = softmax(W_s · s_t)
```

Where `s_t` is decoder hidden state, `h_i` are encoder hidden states.

**Key properties:**
- **Additive attention:** Energy is computed via a learned MLP (tanh).
- **Soft alignment:** Differentiable (softmax), allows end-to-end training.
- **Context is dynamic:** Different `c_t` at each decoding step.

**Interview Q:** Why is it called "additive" attention?

**A:** The energy function combines `s_{t-1}` and `h_i` through an MLP with a `tanh` non-linearity. The learned weight `v_a` then takes the inner product, effectively computing a learned additive combination.

---

### 3.2 Luong Attention (Multiplicative, 2015)

**Formulation (global):**

```
Score (dot):        score(s_t, h_i) = s_t^T · h_i
Score (general):    score(s_t, h_i) = s_t^T · W_a · h_i
Score (concat):     score(s_t, h_i) = v_a^T · tanh(W_a · [s_t; h_i])

Weights:    α_{t,i} = softmax(score(s_t, h_i))
Context:    c_t = Σ_i α_{t,i} · h_i
Output:     h̃_t = tanh(W_c · [c_t; s_t])
```

**Bahdanau vs Luong:**

| Aspect | Bahdanau | Luong |
|--------|----------|-------|
| Energy | Additive (MLP) | Multiplicative (dot/general) |
| Computation | O(d²) for MLP | O(d) for dot, O(d²) for general |
| Decoder alignment | Uses `s_{t-1}` | Uses `s_t` (current decoder state) |
| Speed | Slower to compute | Faster (matrix ops) |
| Context usage | Concatenated with `s_{t-1}` | Concatenated with `s_t`, then projected |

**Interview Q:** In practice, which performs better?

**A:** Both perform similarly with enough capacity. Luong dot-product is more efficient (just matrix multiplication). Luong general adds capacity with the learned `W_a`. Bahdanau can be more expressive but is slower.

**Code — Luong dot-product attention:**

```python
def dot_product_attention(query, keys, values):
    # query: [batch, d_k], keys: [batch, seq_len, d_k]
    scores = query @ keys.transpose(0, 2, 1)  # [batch, seq_len]
    weights = softmax(scores / sqrt(d_k))
    return weights @ values  # [batch, d_v]
```

---

### 3.3 Self-Attention (Vaswani et al., 2017)

**Scaled Dot-Product Attention:**

```
Attention(Q, K, V) = softmax(Q · K^T / √d_k) · V
```

**Why scaling by √d_k?**

Without scaling, when `d_k` is large, the dot products have large variance:

```
Var(q·k) = d_k  (if q, k ~ N(0,1))
```

Large values push the softmax into regions of extremely small gradients (nearly 0 or 1). Scaling by `√d_k` normalizes the variance to ~1, keeping the softmax in regions with meaningful gradients.

**Proof sketch:**

If `q_i, k_i ~ N(0, 1)` i.i.d., then `q·k = Σ q_i·k_i` is a sum of `d_k` i.i.d. terms with mean 0, var 1. So `Var(q·k) = d_k`. Dividing by `√d_k` gives `Var = 1`.

**Multi-Head Attention:**

```
MultiHead(Q, K, V) = Concat(head_1, ..., head_h) · W_O
where head_i = Attention(Q·W_Q_i, K·W_K_i, V·W_V_i)
```

Each head learns different aspects of the relationship (syntax, semantics, position).

**Interview Q:** Why multi-head instead of one large attention?

**A:** Multiple heads allow the model to attend to information from different representation subspaces. One head might focus on syntax, another on long-range dependencies, etc. The different projections prevent the averaging effect of a single softmax.

**Interview Q:** What's the computational complexity of self-attention?

**A:** O(n² · d) for sequence length n and dimension d. Every token attends to every other token. This is the quadratic bottleneck that sparse/longformer/linear attention addresses.

---

### 3.4 Cross-Attention

**In encoder-decoder:**

```
Q = decoder_state (or derived from decoder)
K = encoder_outputs
V = encoder_outputs
```

The decoder queries the encoder's representation to retrieve relevant information. This is how Transformers handle seq2seq tasks.

**In cross-modal models (e.g., CLIP, Flamingo):**

```
Q = text_embeddings
K = image_features
V = image_features
```

Text queries image content. This is the core of multimodal alignment.

**Interview Q:** How is cross-attention different from self-attention?

**A:** In self-attention, Q, K, V all come from the same sequence. In cross-attention, K and V come from one source (e.g., encoder) while Q comes from another (e.g., decoder). This creates an information bottleneck — the decoder cannot access anything the encoder hasn't provided.

---

### 3.5 Attention Variants

**Sparse Attention (Child et al., 2019):**

- **Fixed patterns:** Attend to a fixed set of positions (strided, local).
- **Strided:** Attend to positions `i - k·l` for stride `l`.
- **Local:** Attend to a sliding window around each position.
- **Combination:** Alternating strided and local layers give full coverage.

**Linear Attention (Katharopoulos et al., 2020):**

Replace softmax with a kernel feature map:

```
Attention = softmax(QK^T)V  →  Attention ≈ φ(Q) · (φ(K)^T · V)
```

Where `φ` is a feature map (e.g., elu+1). Complexity drops from O(n²) to O(n).

**Sliding Window Attention (Beltagy et al., 2020 — Longformer):**

Each token attends only to `w` neighbors on each side. O(n·w) instead of O(n²). Combined with global attention on special tokens.

**Global + Dilated Attention (BigBird, Zaheer et al., 2020):**

- **Global tokens:** Few special tokens attend to all (and vice versa).
- **Sliding window:** Local window attention.
- **Random:** Random connections for information flow.

Combined, BigBird provably approximates full attention with O(n) complexity.

**Interview Q:** When would you use sparse attention over full attention?

**A:** Long sequences (document-level NLP, long video, genomics). Full attention is O(n²) memory, which becomes prohibitive beyond ~4K tokens. Sparse attention (Longformer, BigBird) extends to 32K+ tokens.

**Interview Q:** What's the limitation of linear attention?

**A:** The kernel trick means attention is no longer a convex combination (softmax). It can be less expressive, and performance often degrades on long-range tasks. Also, causal masking is more complex.

---

### 3.6 Flash Attention (Dao et al., 2022)

**Core idea:** Fused, IO-aware attention that's 2–4× faster by minimizing reads/writes to HBM (high-bandwidth memory).

**Key techniques:**

1. **Tiling:** Compute attention by blocks that fit in SRAM (on-chip). The softmax is computed incrementally with rescaling.

   ```
   Given Q, K, V in HBM:
   For each block Q_j in SRAM:
       Load K_block, V_block to SRAM
       Compute S = Q_j · K_block^T
       Compute row-wise softmax online (with rescaling)
       Accumulate output
       Write final O_j back to HBM
   ```

2. **Online softmax rescaling:**

   Standard softmax: `softmax(x) = exp(x - max(x)) / Σ exp(x - max(x))`

   Flash Attention computes softmax incrementally over blocks:
   - Track `m_new = max(m_old, rowmax(x_block))`
   - Update numerator `ℓ_new = ℓ_old · exp(m_old - m_new) + exp(x_block - m_new)`
   - Update output with rescaling

3. **Recomputation:** During backward pass, recompute attention weights from Q, K, V in SRAM rather than storing them. This trades extra computation for dramatically less memory.

**Interview Q:** Why is Flash Attention faster despite recomputing during backward?

**A:** The bottleneck is HBM bandwidth, not FLOPs. Full attention reads Q, K, V from HBM once and writes O once. Standard attention reads/writes intermediate S and P matrices to HBM. Flash Attention avoids all intermediate HBM reads/writes by keeping everything in fast SRAM. The recomputation is on-chip (fast) and saves HBM bandwidth.

**Interview Q:** What's the memory savings?

**A:** Standard attention: O(n² + n·d) for storing S, P matrices. Flash Attention: O(n·d) for Q, K, V, O. No quadratic intermediate storage. For n=4096, d=64, this is ~512MB vs ~2MB per layer.

---

## 4. GANs

### 4.1 Original GAN (Goodfellow et al., 2014)

**Min-max game:**

```
min_G max_D V(D, G) = E_{x~p_data}[log D(x)] + E_{z~p_z}[log(1 - D(G(z)))]
```

**Generator:** Maps noise `z` to fake data `G(z)`, tries to fool discriminator.

**Discriminator:** Classifies real vs fake, outputs probability.

**Alternating training:**
1. Update D: maximize `log D(x) + log(1 - D(G(z)))`
2. Update G: minimize `log(1 - D(G(z)))` (or equivalently maximize `log D(G(z))`)

**Training instability issues:**

- **Non-convergence:** The min-max game may oscillate. No guarantee of Nash equilibrium.
- **Mode collapse:** Generator produces only a few modes of the data distribution.
- **Vanishing gradients:** When D is too strong (classifies perfectly), G's gradient vanishes because `log(1 - D(G(z)))` saturates.
- **Sensitivity:** Hyperparameters, architecture, and initialization are very delicate.

**Interview Q:** Why does the generator use `maximize log D(G(z))` instead of `minimize log(1 - D(G(z)))`?

**A:** `log(1 - D(G(z)))` saturates when D easily rejects G(z) (early in training). The gradient vanishes. `log D(G(z))` provides stronger gradients because D(G(z)) is small early on, so the gradient `d/dθ log D(G(z))` = (1/D)·(dD/dθ) is large.

---

### 4.2 DCGAN (Radford et al., 2015)

**Key architectural guidelines:**
- Replace pooling with strided conv (D) / fractional-strided conv (G)
- Use BatchNormalization in both G and D (except G output, D input)
- Remove FC hidden layers
- ReLU in G (except output tanh), LeakyReLU in D
- Adam optimizer (lr=0.0002, β1=0.5)

---

### 4.3 Conditional GAN (CGAN)

**Formulation:** Both G and D receive a condition `y` (e.g., class label, text, image):

```
min_G max_D V(D, G) = E_{x~p_data}[log D(x|y)] + E_{z~p_z}[log(1 - D(G(z|y)|y))]
```

Conditioning is typically done via concatenation (e.g., one-hot label embedded and concatenated to z or to feature maps).

---

### 4.4 CycleGAN (Zhu et al., 2017)

**Task:** Unpaired image-to-image translation (e.g., photo ↔ Monet painting).

**Key idea:** Cycle consistency loss — translating A→B→A should recover the original image.

```
L_cycle(G, F) = E_{x~A}[||F(G(x)) - x||_1] + E_{y~B}[||G(F(y)) - y||_1]
```

**Full objective:**
```
L(G, F, D_A, D_B) = L_GAN(G, D_B, A, B)
                   + L_GAN(F, D_A, B, A)
                   + λ · L_cycle(G, F)
```

**Interview Q:** Why L1 for cycle loss instead of L2?

**A:** L1 produces sharper images. L2 encourages blurring (penalizes outliers less aggressively, so the model averages over multiple plausible outputs).

---

### 4.5 StyleGAN / StyleGAN2 (Karras et al., 2019–2020)

**Key innovations:**
- **Mapping network:** Noise → intermediate latent space W (not the input layer). This disentangles attributes.
- **Adaptive Instance Normalization (AdaIN):** Style injection via AdaIN: `AdaIN(x, y) = y_s · (x - μ(x))/σ(x) + y_b`
- **Style mixing:** Mixing two latent codes at different layers creates hybrid images.
- **Stochastic variation:** Noise injection per pixel for fine details.

**StyleGAN2 improvements:** Removed artifacts (droplets), improved FID with normalization changes and path length regularization.

**Interview Q:** Why use a mapping network instead of feeding z directly?

**A:** The mapping network `f: Z → W` disentangles the latent space. In Z space, features are correlated. W space is learned to be more linear and factorized (moving along one direction changes one attribute). This enables style mixing and interpolation.

---

### 4.6 GAN vs Diffusion Models — Interview Comparison

| Aspect | GAN | Diffusion |
|--------|-----|-----------|
| Training | Adversarial (min-max) | Surrogate denoising objective |
| Stability | Unstable, mode collapse | Stable, defined loss |
| Diversity | Lower (mode dropping) | Higher (covers all modes) |
| Speed | Fast (single forward pass) | Slow (50–1000 steps) |
| Likelihood | No tractable likelihood | Variational lower bound |
| FID | Often better | Competitive (DDPM, IDDPM) |
| Samples | Sharp, may have artifacts | High quality, more faithful |

**Interview Q:** Will diffusion models replace GANs?

**A:** For image generation, largely yes (DALL-E 3, Imagen, Stable Diffusion). For video, 3D, and interactive applications (real-time), GANs are still faster. GANs also remain useful for domain adaptation, super-resolution (real-time), and any application requiring a single forward pass.

---

### 4.7 Training Tricks

**Label Smoothing:** Replace targets 0/1 with soft values (e.g., 0.1/0.9). Prevents discriminator from being overconfident, improving gradient quality for generator.

**Feature Matching:** Minimize L2 distance between real and fake feature statistics (from intermediate D layers). Stabilizes GAN by providing a more structured objective.

**Gradient Penalty (WGAN-GP):**

```
L = E[D(fake)] - E[D(real)] + λ · E[(||∇D(x̂)||_2 - 1)²]
```

Where `x̂` is sampled uniformly along lines between real and fake pairs. Enforces 1-Lipschitz constraint on discriminator, stabilizing WGAN training.

**Interview Q:** Why does gradient penalty work better than weight clipping (original WGAN)?

**A:** Weight clipping pushes weights to extreme values (-c, c), which biases the critic towards simple functions (loss of capacity). Gradient penalty directly enforces the Lipschitz constraint smoothly and maintains model capacity.

---

## 5. Diffusion Models

### 5.1 Forward Process (Noising)

Given data `x₀ ~ q(x)`, gradually add Gaussian noise over `T` steps:

```
q(x_t | x_{t-1}) = N(x_t; √(1 - β_t) · x_{t-1}, β_t · I)
```

In closed form (reparameterization trick):

```
x_t = √(ᾱ_t) · x₀ + √(1 - ᾱ_t) · ε
where α_t = 1 - β_t,  ᾱ_t = Π_{s=1}^{t} α_s
```

As `T → ∞`, `x_T ~ N(0, I)` (isotropic Gaussian).

**Variance schedule:** `β_t` is typically small (1e-4 → 0.02). Linear, cosine, or learned.

---

### 5.2 Reverse Process (Denoising)

Learn `p_θ(x_{t-1} | x_t)` to reverse the forward process:

```
p_θ(x_{t-1} | x_t) = N(x_{t-1}; μ_θ(x_t, t), Σ_θ(x_t, t))
```

**Training objective (simplified, Ho et al., 2020):**

```
L_simple = E_{t, x₀, ε} [ || ε - ε_θ(√(ᾱ_t)·x₀ + √(1-ᾱ_t)·ε, t) ||² ]
```

The model learns to predict the noise `ε` added at step `t`.

**Sampling (reverse):**
```
x_T ~ N(0, I)
for t = T, ..., 1:
    z ~ N(0, I) if t > 1 else 0
    x_{t-1} = (1/√α_t) · (x_t - (β_t/√(1-ᾱ_t)) · ε_θ(x_t, t)) + σ_t · z
```

**DDPM (Denoising Diffusion Probabilistic Models, Ho et al., 2020):**

- Architecture: U-Net with self-attention at multiple resolutions.
- Loss: MSE on predicted noise (L_simple).
- Equivalent to weighted variational lower bound on log-likelihood.
- T = 1000 steps, linear variance schedule.
- 35.4 FID on CIFAR-10 (unconditional).

**Interview Q:** How many steps does DDPM need to generate a sample?

**A:** 1000 steps. Each step requires a neural network forward pass. This is ~1000× slower than a GAN. DDIM reduces this to 20–100 steps.

---

### 5.3 DDIM (Song et al., 2020) — Faster Sampling

**Key insight:** The forward process doesn't have to be Markovian. DDIM uses a _non-Markovian_ forward process that preserves the same marginals `q(x_t | x₀)` as DDPM.

**Sampling (deterministic, T steps → S steps):**

```
x_{τ_S} ~ N(0, I)
for t = τ_S, ..., τ_1:
    x_{τ_{t-1}} = √(ᾱ_{τ_{t-1}}) · (x_{τ_t} - √(1-ᾱ_{τ_t}) · ε_θ(x_{τ_t}, t)) / √(ᾱ_{τ_t})
                  + √(1 - ᾱ_{τ_{t-1}}) · ε_θ(x_{τ_t}, t)
```

This is implicit — not a learned model, just a different sampling procedure for the same trained DDPM.

**DDIM properties:**
- **Deterministic:** Same noise → same sample (useful for interpolation).
- **Fast:** 20–100 steps vs 1000 (10–50× speedup).
- **Consistency:** Different step schedules produce the same marginal distributions.
- **Interpolation:** Linear interpolation in latent space produces smooth transitions.

**Interview Q:** What's the tradeoff between DDPM and DDIM?

**A:** DDPM (stochastic): higher sample quality at many steps. DDIM (deterministic): controllable speed-quality tradeoff, interpolation ability, consistency. DDIM has slightly lower quality in few-step regimes (< 10 steps).

---

### 5.4 Score-Based Modeling & Langevin Dynamics

**Connection to diffusion:** The noise prediction network `ε_θ(x_t, t)` is equivalent to a _score function_:

```
ε_θ(x_t, t) = -√(1 - ᾱ_t) · ∇_{x_t} log p(x_t)
```

**Score matching:** Learn `s_θ(x) ≈ ∇_x log p(x)` without knowing the partition function.

**Langevin dynamics sampling:**

```
x_{t+1} = x_t + (η/2) · s_θ(x_t) + √η · z,  z ~ N(0, I)
```

**Noise-conditional score networks (NCSN):** Multiple noise levels for better coverage of low-density regions (where scores are unreliable).

**Song et al. unified framework:** Diffusion models = score matching + Langevin dynamics. DDPM is a specific discretization of a stochastic differential equation (SDE).

---

### 5.5 Stable Diffusion (Rombach et al., 2022)

**Architecture:**

```
┌─────────────────────────────────────────────┐
│             TEXT CONDITIONING                │
│  Text → CLIP Text Encoder → cross-attn K,V  │
└──────────────────────┬──────────────────────┘
                       │
┌──────────────────────┴──────────────────────┐
│            LATENT DIFFUSION                  │
│   z ~ N(0, I) → U-Net → denoised z          │
│   U-Net: ResNet+Attn blocks                 │
│   cross-attn: Q=unet_feat, K,V=text_emb     │
└──────────────────────┬──────────────────────┘
                       │
┌──────────────────────┴──────────────────────┐
│            VAE DECODER                       │
│   z → Decoder → image (H, W, 3)             │
└─────────────────────────────────────────────┘
```

**Key components:**

1. **VAE:** Encoder compresses image to latent space (8× downsampling). Decoder reconstructs. Operating in latent space reduces computation by ~factor of 64 (spatial dimensions reduced 8×).

2. **U-Net:** Predicts noise in latent space. Uses ResNet blocks with self-attention and cross-attention (for text conditioning).

3. **Text conditioning:** CLIP text encoder provides text embeddings. Cross-attention in U-Net merges image and text modalities.

**Why latent diffusion?**

- **Computational efficiency:** Diffusion in pixel space (256×256×3) requires 256²×3 ≈ 200k dimensions. Latent space (32×32×4) ≈ 4k dimensions. That's a 50× reduction in compute per step.
- **Semantic compression:** The VAE encoder removes imperceptible high-frequency details, letting the diffusion model focus on the semantic content.
- **Perceptual quality:** The VAE decoder adds back realistic textures, leveraging the inductive bias of convolutional decoders.

**Interview Q:** Why is Stable Diffusion called "latent" diffusion?

**A:** The diffusion process operates on latent representations (from a VAE encoder), not directly on pixels. This makes training and sampling significantly faster while maintaining quality.

**Interview Q:** How does text conditioning work in cross-attention?

**A:** In each U-Net block, the spatial features are projected to Q, and text embeddings are projected to K and V. Cross-attention computes `softmax(Q·K^T/√d)·V`, producing spatially-aware text-weighted features.

---

### 5.6 Diffusion Model Interview Questions

**Q1:** Why does the diffusion loss function predict noise rather than the image directly?

**A:** Predicting noise is equivalent to predicting the score function `∇log p(x)`. The noise prediction loss simplifies to MSE on the added noise, which is well-conditioned (unit variance). Predicting `x₀` directly would require a more complex scaling with `t` and often leads to worse performance (Ho et al., 2020).

**Q2:** How do you handle different resolutions in diffusion models?

**A:** Most models are trained at a fixed resolution. For higher resolutions, you can: (1) super-resolution cascade (SR3, Cascaded Diffusion), (2) patch-based generation, (3) shifted window attention (for Transformers). Stable Diffusion can generate 2× training resolution by tiling.

**Q3:** What is classifier-free guidance (CFG)?

**A:** During training, the model is trained both with and without conditioning (10% drop rate). During sampling, the output is extrapolated: `ε̃_θ = ε_θ(x_t, t, ∅) + w·(ε_θ(x_t, t, c) - ε_θ(x_t, t, ∅))`. The guidance scale `w > 1` pushes samples toward conditioned modes, improving alignment with text at the cost of diversity.

**Q4:** What's the relationship between DDPM and score matching?

**A:** DDPM's noise prediction `ε_θ(x_t, t)` is proportional to the score: `∇_{x_t} log p(x_t) ≈ -ε_θ(x_t, t) / √(1 - ᾱ_t)`. The denoising objective corresponds to denoising score matching. This connection unifies the DDPM and score-based SDE frameworks.

---

## 6. VAEs

### 6.1 Variational Autoencoder

**Architecture:**

```
Input x
   ↓
Encoder q_φ(z | x) → μ, σ
   ↓
z = μ + σ · ε,  ε ~ N(0, I)   [Reparameterization]
   ↓
Decoder p_θ(x | z) → reconstructed x̂
```

**ELBO Derivation:**

```
log p(x) = KL(q_φ(z|x) || p_θ(z|x)) + ELBO

ELBO = E_{z~q_φ}[log p_θ(x|z)] - KL(q_φ(z|x) || p(z))
```

- **Reconstruction term:** `E[log p_θ(x|z)]` — how well the decoder reconstructs x from z.
- **KL divergence:** `KL(q_φ(z|x) || p(z))` — regularizes the latent distribution toward the prior `p(z) = N(0, I)`.

**Reparameterization Trick:**

```
z = μ + σ · ε,  ε ~ N(0, I)
```

Enables backpropagation through stochastic sampling by moving the randomness to an external variable `ε`. The gradient flows through `μ` and `σ` deterministically.

**Interview Q:** Why can't we directly maximize `log p(x)`?

**A:** `log p(x) = log ∫ p(x|z)·p(z) dz` is intractable — requires integrating over all possible z. The ELBO provides a tractable lower bound that we can optimize via SGD.

**Interview Q:** What happens if the KL term dominates the ELBO?

**A:** The encoder maps all inputs to the same latent distribution (posterior collapse). The decoder ignores z and relies only on autoregressive decoding. Common in high-capacity decoders. Solutions: KL annealing, free bits, β-VAE.

---

### 6.2 β-VAE (Higgins et al., 2017)

```
ELBO_β = E[log p(x|z)] - β · KL(q(z|x) || p(z))
```

With `β > 1`, the KL term is weighted higher, encouraging more disentangled latent representations. The model learns independent factors of variation (rotation, scale, color) in separate latent dimensions.

**Interview Q:** What's the tradeoff with β > 1?

**A:** Higher β → more disentanglement but worse reconstruction quality. The original paper uses β = 4 as a sweet spot. There's an information bottleneck effect: the latent code is constrained, so it must encode only the most salient factors.

---

### 6.3 VQ-VAE (van den Oord et al., 2017)

**Architecture:**

```
Input x → Encoder → z_e(x)
                          ↓ (nearest neighbor lookup in codebook {e_k})
                      z_q(x) = e_k where k = argmin_j ||z_e(x) - e_j||
                          ↓
                      Decoder → x̂
```

**Loss:**

```
L = ||x - D(z_q(x))||² + ||sg[z_e(x)] - e||² + β · ||z_e(x) - sg[e]||²
```

- First term: reconstruction
- Second term: codebook learning (move codes toward encoder outputs)
- Third term: commitment loss (encoder commits to codes)

**VQ-VAE + prior = powerful generative model.** The discrete latent codes are modeled by PixelCNN (VQ-VAE) or a Transformer (VQ-VAE-2, DALL-E).

**Interview Q:** What's the advantage of discrete latents (VQ-VAE) over continuous (standard VAE)?

**A:** Discrete representations are a natural fit for language, music, and categorical data. They avoid the "holes" problem in continuous VAEs (regions of latent space with high prior probability but poor reconstructions). They also enable autoregressive priors (Transformers) over the discrete codes.

---

### 6.4 VAE vs GAN vs Diffusion

| Criterion | VAE | GAN | Diffusion |
|-----------|-----|-----|-----------|
| Likelihood | ELBO (lower bound) | No | Variational bound |
| Sample quality | Blurry | Sharp (best per-step) | Very good |
| Diversity | High | Mode collapse | High |
| Speed | Fast | Fastest | Slow |
| Training | Stable | Unstable | Stable |
| Latent space | Structured | Unstructured | Interpretable (semantic) |

**Interview Q:** When would you pick a VAE over a GAN?

**A:** When you need a structured latent space (disentangled representation), when training stability is critical, or when you need an encoder (inference network). Common applications: anomaly detection, representation learning, compression.

---

## 7. Graph Neural Networks

### 7.1 Message Passing Framework

The general GNN layer for node `v` at layer `k`:

```
h_v^{(k)} = UPDATE^{(k)}(h_v^{(k-1)}, AGGREGATE^{(k)}({h_u^{(k-1)} : u ∈ N(v)}))
```

or equivalently:

```
m_v^{(k)} = AGGREGATE({h_u^{(k-1)} : u ∈ N(v)})
h_v^{(k)} = UPDATE(h_v^{(k-1)}, m_v^{(k)})
```

---

### 7.2 Graph Convolutional Network (GCN, Kipf & Welling, 2017)

**Layer update:**

```
H^{(l+1)} = σ(D̃^{-1/2} · Ã · D̃^{-1/2} · H^{(l)} · W^{(l)})
```

Where `Ã = A + I` (self-loops), `D̃` is the degree matrix of `Ã`.

**Per-node formulation:**

```
h_v^{(l+1)} = ReLU( Σ_{u ∈ N(v) ∪ {v}} (1/√(d̃_v · d̃_u)) · W^{(l)} · h_u^{(l)} )
```

**Key ideas:**
- Symmetric normalization: `1/√(d_v·d_u)` prevents degree-related feature scale explosion.
- Self-loops: Include the node itself in the aggregation.
- Spectral motivation: First-order approximation of spectral graph convolution.

**Interview Q:** Why does GCN use symmetric normalization?

**A:** Without normalization, high-degree nodes would have much larger feature magnitudes. Symmetric normalization ensures features are scaled by the square root of degrees, stabilizing training. It also relates to the normalized graph Laplacian.

**Limitation:** GCN is transductive — requires the full graph Laplacian during inference. Graphsage addresses this.

---

### 7.3 GraphSAGE (Hamilton et al., 2017)

**Inductive learning:** Uses learned aggregator functions instead of relying on the full Laplacian.

**Aggregators:**
- Mean: element-wise mean of neighbor features
- LSTM: sample neighbors, apply LSTM (order-invariant via random ordering)
- Pool: MLP on each neighbor + max/mean pooling

**Layer update:**

```
h_v^{(k)} = σ(W · CONCAT(h_v^{(k-1)}, AGG({h_u^{(k-1)}, u ∈ N(v)})))
```

**Key:** Neighborhood is sampled (fixed size), enabling minibatch training on large graphs.

---

### 7.4 Graph Attention Network (GAT, Veličković et al., 2018)

**Attention coefficients:**

```
e_{vu} = a(W·h_v || W·h_u)
α_{vu} = softmax_u(LeakyReLU(e_{vu}))
h_v' = σ( Σ_{u ∈ N(v)} α_{vu} · W · h_u )
```

**Multi-head attention:**

```
h_v' = ||_{k=1}^{K} σ( Σ_{u ∈ N(v)} α_{vu}^{(k)} · W^{(k)} · h_u )
```

**Key advantages over GCN:**
- **Implicit edge weights:** Learned attention assigns different importance to different neighbors.
- **Inductive:** No dependency on full Laplacian.
- **More expressive:** Can model degree-invariant importance.

**Interview Q:** How does GAT differ from Transformer attention?

**A:** GAT uses the graph adjacency as the attention mask (only adjacent nodes attend). Transformer uses full (dense) attention — all tokens attend to all others. GAT computes attention on each edge separately; Transformer computes all pairwise dot products.

---

### 7.5 Graph Isomorphism Network (GIN, Xu et al., 2019)

**Theoretical motivation:** Maximally powerful GNN under the WL graph isomorphism test.

**Layer:**

```
h_v^{(k)} = MLP^{(k)}((1 + ε^{(k)}) · h_v^{(k-1)} + Σ_{u ∈ N(v)} h_u^{(k-1)})
```

Where `ε` is a learnable parameter (or fixed to 0).

**Key insight:** Sum aggregator is strictly more powerful than mean or max aggregators for distinguishing graph structures.

**Interview Q:** Why is sum aggregation more powerful than mean or max?

**A:** Mean and max can map different multisets to the same representation. Example: `{1, 1, 1, 1, 1}` vs `{1, 2}` have the same mean (1) and max (1) but different sums. Sum uniquely captures the full multiset (with the MLP providing injectivity).

---

### 7.6 GNN Applications

| Task | Description | Common Approach |
|------|-------------|-----------------|
| Node classification | Predict label per node | GCN/GAT + CE loss |
| Link prediction | Predict edge existence | GNN encoder + dot product decoder |
| Graph classification | Predict label per graph | GNN + global pooling (sum/mean) + FC |
| Graph generation | Generate new graphs | VAE/GAN/Diffusion over graphs |

**Interview Q:** How do you handle link prediction in GNNs?

**A:** Encode node representations with GNN, then compute pairwise score (e.g., dot product between node embeddings). Train with negative sampling (random non-edges). Encode local structure with techniques like SEAL (subgraph extraction).

---

## 8. Loss Functions

### 8.1 Classification Losses

**Cross-Entropy Loss (Multi-class):**

```
L = - Σ_{c=1}^{C} y_c · log(p_c)
```

Where `y_c` is one-hot ground truth, `p_c = softmax(z_c)`.

**Binary Cross-Entropy (BCE):**

```
L = -[y · log(p) + (1 - y) · log(1 - p)]
```

**Focal Loss (Lin et al., 2017):**

```
L_focal = -α_t · (1 - p_t)^γ · log(p_t)
```

- `γ ≥ 0`: focusing parameter. Higher γ down-weights easy examples.
- `α_t`: class balancing weight.
- Purpose: Address extreme class imbalance (e.g., object detection where most boxes are background).

**Interview Q:** When do you use focal loss over cross-entropy?

**A:** Extreme class imbalance where easy negatives overwhelm the loss. Prime example: one-stage object detectors (RetinaNet). Cross-entropy gives too much gradient from easy examples; focal loss down-weights them.

---

### 8.2 Contrastive / Metric Losses

**Contrastive Loss:**

```
L = (1 - y) · D² + y · max(0, m - D)²
```

Where `y = 1` for similar pairs, `y = 0` for dissimilar, `D = ||f(a) - f(b)||₂`.

- Similar pairs: minimize distance.
- Dissimilar pairs: push apart if distance < margin `m`.

**Triplet Loss (Schroff et al., 2015 — FaceNet):**

```
L = max(0, ||f(a) - f(p)||² - ||f(a) - f(n)||² + α)
```

Where `a` = anchor, `p` = positive (same identity), `n` = negative (different identity), `α` = margin.

**Triplet mining strategies:**
- **Easy:** `d(a,p) + α < d(a,n)` — loss is 0, no learning signal.
- **Semi-hard:** `d(a,p) < d(a,n) < d(a,p) + α` — useful training pairs.
- **Hard:** `d(a,n) < d(a,p)` — violates margin, strongest signal but can cause collapse.

**InfoNCE (NT-Xent, used in SimCLR, CLIP):**

```
L_i = -log( exp(sim(z_i, z_j)/τ) / Σ_{k=1}^{2N} exp(sim(z_i, z_k)/τ) )
```

Where `(i, j)` is a positive pair (augmented views of same image), others are negatives.

**Interview Q:** Why use InfoNCE over triplet loss in contrastive learning?

**A:** InfoNCE naturally handles many negatives (batch size of 4096+). Triplet loss only compares one negative per triplet. InfoNCE provides more efficient learning by contrasting against all other examples in the batch.

---

### 8.3 Perceptual / Style / Adversarial Losses

**Perceptual Loss:** L2 (or L1) distance between VGG feature maps, not pixels:

```
L_perceptual(x, y) = Σ_l ||φ_l(x) - φ_l(y)||₂²
```

Where `φ_l` is the output of layer `l` of a pretrained VGG network.

- **Why it works:** Feature space captures perceptual similarity (texture, structure, content) rather than pixel-wise agreement. Makes images look more natural.
- **Application:** Super-resolution (SRGAN, ESRGAN), style transfer, image generation.

**Style Loss (Gatys et al., 2016):**

```
L_style(x, y) = Σ_l ||G(φ_l(x)) - G(φ_l(y))||_F²
```

Where `G(φ) = φ·φ^T` (Gram matrix) captures correlations between feature channels.

**Adversarial Loss:**

```
L_GAN = E[log D(x)] + E[log(1 - D(G(z)))]
```

Used in combination with perceptual (e.g., SRGAN) to make outputs perceptually realistic.

**Interview Q:** How do you decide which loss to combine?

**A:** Rule of thumb:
- **Pixel-level accuracy** (PSNR): L1 or L2
- **Perceptual quality**: L_perceptual (VGG)
- **Texture/style transfer**: L_style (Gram)
- **Realism / sharpness**: L_adversarial
- **Content preservation**: L_perceptual + L1

---

## 9. Optimization

### 9.1 Optimizers

**SGD (Stochastic Gradient Descent):**

```
θ_{t+1} = θ_t - η · ∇L(θ_t)
```

**SGD + Momentum:**

```
v_{t+1} = μ·v_t + ∇L(θ_t)
θ_{t+1} = θ_t - η·v_{t+1}
```

- Accelerates convergence in directions of consistent gradient.
- Helps navigate ravines (sharp curvature in one direction).
- Typical μ = 0.9.

**Nesterov Accelerated Gradient (NAG):**

```
v_{t+1} = μ·v_t + ∇L(θ_t - μ·v_t)
θ_{t+1} = θ_t - η·v_{t+1}
```

"Look ahead" — computes gradient at the approximate future position. Provides more responsive updates than standard momentum.

**AdaGrad (Duchi et al., 2011):**

```
G_t = G_{t-1} + ∇L(θ_t)²
θ_{t+1} = θ_t - η·∇L(θ_t) / √(G_t + ε)
```

- Per-parameter learning rates based on historical gradients.
- Good for sparse features (NLP, recommender systems).
- **Problem:** Accumulated sum keeps growing → learning rate → 0.

**RMSProp (Hinton, 2012):**

```
G_t = γ·G_{t-1} + (1-γ)·∇L(θ_t)²
θ_{t+1} = θ_t - η·∇L(θ_t) / √(G_t + ε)
```

- AdaGrad + exponential moving average (no monotonic decay).
- Good for non-stationary objectives (RNN training).
- Typical γ = 0.9.

**Adam (Kingma & Ba, 2014):**

```
m_t = β₁·m_{t-1} + (1-β₁)·∇L(θ_t)
v_t = β₂·v_{t-1} + (1-β₂)·∇L(θ_t)²
m̂_t = m_t / (1 - β₁^t)        # bias correction
v̂_t = v_t / (1 - β₂^t)
θ_{t+1} = θ_t - η·m̂_t / (√v̂_t + ε)
```

- **Momentum** (first moment) + **adaptive learning rates** (second moment).
- Bias correction handles initialization at t=0.
- Default: β₁=0.9, β₂=0.999, ε=1e-8.

**AdamW (Loshchilov & Hutter, 2017):**

Decouples weight decay from gradient update:

```
θ_{t+1} = θ_t - η·(m̂_t/(√v̂_t+ε) + λ·θ_t)
```

Standard Adam applies L2 regularization (adding `λ·θ_t` to gradient), which interacts poorly with adaptive learning rates. AdamW applies weight decay directly to parameters, leading to better generalization.

**Interview Q:** Adam vs SGD — which is better?

**A:** Adam converges faster, works well out-of-the-box, and is robust to hyperparameters. SGD + momentum generalizes better in many cases (especially CV), especially with a well-tuned learning rate schedule. Common practice: start with Adam for prototyping, switch to SGD with cosine schedule for final training.

---

### 9.2 Learning Rate Scheduling

| Schedule | Formula | Use Case |
|----------|---------|----------|
| **Step decay** | `η = η₀ · γ^{⌊epoch/step_size⌋}` | Classic CV training |
| **Cosine** | `η = η_min + (η₀ - η_min)·[1 + cos(π·t/T)]/2` | Stable, no tuning of decay points |
| **Cosine with warmup** | Linear warmup for `W` steps, then cosine | Prevents early training instability |
| **OneCycle** | Linear increase to max_lr, then cosine decay | Fast convergence (superconvergence) |
| **ReduceLROnPlateau** | Reduce when metric stops improving | Non-uniform schedule |

**Interview Q:** Why use warmup?

**A:** At the start of training, the model weights are random, so gradients have high variance. A large learning rate can cause early divergence (especially with Adam's adaptive rates). Warmup allows the optimizer to accumulate stable gradient statistics before applying the full learning rate.

---

### 9.3 Gradient Clipping

**Norm clipping:**

```
if ||g|| > threshold:
    g = g · threshold / ||g||
```

**Value clipping:** Clip each element to `[-threshold, threshold]`.

**Interview Q:** When do you use gradient clipping?

**A:** Essential for RNNs/LSTMs where recurrent gradients can explode. Used in Transformers (especially during early training). Any situation with unstable gradient norms. Typical threshold: 0.5–10.0.

---

### 9.4 Weight Decay

```
θ_{t+1} = θ_t - η·(∇L(θ_t) + λ·θ_t)
```

- Equivalent to L2 regularization.
- Prevents weights from growing too large.
- Improves generalization.
- **AdamW** decouples it from adaptive updates (essential for good Transformer training).

**Which optimizer when — decision chart:**

```
New problem?
├── Small data, prototyping → Adam (0.001)
├── NLP / Transformer → AdamW (0.0001–0.0003) + linear warmup + cosine decay
├── CV, ImageNet-scale → SGD (0.1, Nesterov momentum 0.9) + cosine schedule
├── GAN training → Adam (0.0002, β₁=0.5, β₂=0.999)
├── Sparse features (NLP, recsys) → Adam or AdaGrad
├── RNN/LSTM → RMSProp or SGD with momentum + gradient clipping
└── RL → Adam (stable), clipped gradients
```

### 9.5 Gradient Accumulation

Simulates larger batch sizes when GPU memory is limited:

```
for micro_step in range(accumulation_steps):
    loss = compute_loss(micro_batch)
    loss.backward()  # accumulates gradients

optimizer.step()    # update with accumulated gradients
optimizer.zero_grad()
```

**Effective batch size = micro_batch_size × accumulation_steps.**

**Interview Q:** Is gradient accumulation identical to a larger batch?

**A:** Nearly identical, but BatchNorm statistics per micro-batch differ (unless synchronized across micro-batches). Gradient accumulation also doesn't affect learning rate scheduling (step counting differs).

---

> *End of DEEP_LEARNING_INTERVIEW_GUIDE.md — Happy interviewing!*
