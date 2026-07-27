# LLM / GenAI Deep Interview Guide

> Comprehensive preparation for LLM & Generative AI interviews at top AI companies.
> Covers: Transformers, Pre-training, Fine-tuning, Inference, RAG, Agents, Evaluation & Safety.

---

## Table of Contents

1. [Transformer Architecture](#1-transformer-architecture)
2. [Pre-training](#2-pre-training)
3. [Fine-tuning](#3-fine-tuning)
4. [Inference](#4-inference)
5. [RAG (Retrieval-Augmented Generation)](#5-rag-retrieval-augmented-generation)
6. [Agents](#6-agents)
7. [Evaluation & Safety](#7-evaluation--safety)

---

## 1. Transformer Architecture

### 1.1 "Attention is All You Need" — Full Breakdown

The Transformer (Vaswani et al., 2017) replaced RNNs entirely with attention mechanisms.

**Core Innovation:** Eliminate recurrence entirely. Process all tokens in parallel using self-attention.

**Architecture Components:**
- **Input Embedding:** Map each token to a d_model-dimensional vector.
- **Positional Encoding:** Inject position information since attention is permutation-invariant.
- **Multi-Head Self-Attention:** Each token attends to all tokens.
- **Feed-Forward Network (FFN):** Two linear layers with a non-linearity.
- **Layer Normalization + Residual Connections:** Stabilize training.
- **Output Projection + Softmax:** Predict next token.

**Encoder:** Bidirectional self-attention (each token sees all tokens). 6 layers (base), 12 (big).

**Decoder:** Masked self-attention (causal — token cannot attend to future tokens) + cross-attention (attends to encoder output). 6 layers.

**Scaled Dot-Product Attention:**

```
Attention(Q, K, V) = softmax(QK^T / sqrt(d_k)) V
```

- Q, K, V: projections of input (or encoder output for cross-attention)
- Scale by 1/sqrt(d_k): prevents softmax from entering regions of extreme gradient

### 1.2 Multi-Head Attention

**Mechanism:**
1. Project input into h sets of Q, K, V using learned weight matrices W_Q, W_K, W_V
2. Each head: d_k = d_model / h
3. Compute attention per head independently
4. Concatenate all heads' outputs
5. Project through W_O

```
MultiHead(Q, K, V) = Concat(head_1, ..., head_h) W_O
head_i = Attention(Q W_Q_i, K W_K_i, V W_V_i)
```

**Why Multi-Head?** Allows the model to attend to different representation subspaces at different positions (e.g., syntax, semantics, coreference).

**Interview Q: Why scale by sqrt(d_k)?**
Without scaling, for large d_k, the dot products grow large in magnitude, pushing softmax into regions with extremely small gradients. Scaling keeps variance ~1.

**Interview Q: How does multi-head differ from using a single large head?**
Multi-head creates multiple lower-dimensional subspaces. This is computationally cheaper per head and empirically learns more diverse attention patterns.

**Interview Q: What's the computational complexity of self-attention?**
O(n^2 * d) for sequence length n — the quadratic bottleneck. This is the key limitation for long sequences.

### 1.3 Positional Encoding

**Sinusoidal (Vaswani et al.):**
```
PE(pos, 2i)   = sin(pos / 10000^(2i/d_model))
PE(pos, 2i+1) = cos(pos / 10000^(2i/d_model))
```
- Fixed, no learned parameters
- Allows extrapolation to longer sequences
- Each dimension is a sinusoid of different frequency

**Learned (BERT, GPT):** Learn an embedding table for each position up to max_seq_len.
- Cannot extrapolate beyond max_seq_len
- Works well in practice for fixed-length models

**RoPE (Rotary Position Embedding — used in Llama, GPT-NeoX):**
- Rotate Q and K vectors by angle proportional to position
- Only affects relative position (dot product depends on relative position difference)
- Benefits: relative position bias, enables extrapolation, decay with distance
- Applied before attention: q_m^T k_n = x_m^T W_Q^T R_{theta,m-n} W_K x_n

**ALiBi (Press et al., 2022):**
- No positional embeddings added to input
- Add a bias to attention scores: score_ij = q_i^T k_j - m * |i - j|
- m is a head-specific slope (geometric sequence: 2^(-8/h), etc.)
- Allows strong extrapolation to unseen lengths
- Used in BLOOM, MPT

**Interview Q: Why does RoPE enable better length extrapolation than learned embeddings?**
RoPE encodes relative position directly into the attention computation via rotation, so the model naturally generalizes to positions it hasn't seen. Learned embeddings provide no inductive bias for unseen positions.

**Interview Q: How does ALiBi avoid needing positional embeddings?**
By adding a position-dependent bias *after* the QK dot product, ALiBi directly tells attention where tokens are relative to each other, without polluting the token representations themselves.

### 1.4 Feed-Forward Networks

Standard FFN (Vaswani):
```
FFN(x) = max(0, x W_1 + b_1) W_2 + b_2
```
- Expands to d_ff (4x d_model), then projects back.

**GELU (GPT-3, BERT):**
```
GELU(x) = x * Phi(x) ≈ 0.5x(1 + tanh(sqrt(2/pi)(x + 0.044715x^3)))
```
- Smooth approximation of ReLU
- Non-convex, non-monotonic
- Better gradient flow than ReLU

**SwiGLU (PaLM, Llama, Llama 2, Mistral):**
```
SwiGLU(x, W, V) = Swish(x W) ⊗ (x V)
Swish(x) = x * sigmoid(beta * x)
```
- Gated variant: two linear projections, one gated by SiLU
- Typically uses d_ff * 2/3 to match parameter count
- Consistently outperforms ReLU/GELU on perplexity

**Interview Q: Why does SwiGLU outperform ReLU?**
The gating mechanism (element-wise product of two transformations) provides a richer, more expressive activation. Empirically shown to improve perplexity across model scales.

**Interview Q: How does FFN size relate to model quality?**
Larger d_ff increases model capacity but adds parameters. Modern LLMs often use d_ff ≈ 8/3 * d_model (SwiGLU) to 4 * d_model (ReLU).

### 1.5 Layer Normalization

**LayerNorm:**
```
LayerNorm(x) = gamma * (x - mu) / sqrt(sigma^2 + eps) + beta
```
- Normalizes across the feature dimension
- Learns affine parameters gamma (scale), beta (shift)

**Pre-Norm vs Post-Norm:**
- **Post-Norm (original Transformer):** LayerNorm after residual add → harder to train, requires warmup
- **Pre-Norm (GPT-2+):** LayerNorm *before* each sub-layer → much easier training, no warmup needed
  ```
  x = x + Sublayer(LayerNorm(x))  // Pre-Norm
  x = LayerNorm(x + Sublayer(x))  // Post-Norm
  ```

**RMSNorm (Llama, Llama 2):**
```
RMSNorm(x) = gamma * x / sqrt(mean(x^2) + eps)
```
- Removes the mean subtraction from LayerNorm
- Simpler, faster, empirically matches LayerNorm quality
- Based on observation that LayerNorm's mean centering is less important than scaling

**Interview Q: Why is Pre-Norm preferred over Post-Norm?**
Pre-Norm makes training more stable — gradients flow through the residual path without passing through normalization. Post-Norm requires careful learning rate warmup to prevent divergence.

**Interview Q: Does RMSNorm work as well as LayerNorm?**
In practice, yes — especially in large Transformers. The mean-centering step is redundant since the FFN's bias terms learn the shift anyway.

### 1.6 Residual Connections

**Purpose:**
- Enable training of very deep networks by allowing gradients to bypass sub-layers
- Preserve input information — the sub-layer only needs to learn the *residual* (delta)

**Path formulation in pre-norm:**
```
x_{l+1} = x_l + F_l(LayerNorm(x_l))
```
- The model can "fall back" to identity if sub-layer is unhelpful
- Critical for training 70B+ parameter models

**Interview Q: Why are residual connections critical for Transformers?**
Without them, gradients in very deep Transformers would vanish. Residuals create a direct gradient highway from output to input, making training 100+ layer models feasible.

### 1.7 Encoder-Decoder vs Decoder-Only vs Encoder-Only

**Encoder-Decoder (T5, BART, Flan-T5):**
- Ideal for seq2seq tasks (translation, summarization, text-to-SQL)
- Encoder: bidirectional attention; Decoder: causal + cross-attention
- Higher compute cost (need to run both encoder and decoder)

**Decoder-Only (GPT, Llama, Claude, Mistral):**
- Causal attention only — each token attends to previous tokens
- Simplest architecture, scalable to 100s of billions of parameters
- Can be used for any task via prompting (in-context learning)
- **The dominant architecture for modern LLMs**

**Encoder-Only (BERT, RoBERTa, DeBERTa):**
- Bidirectional attention — each token attends to all tokens
- Best for understanding tasks (classification, NER, QA)
- Cannot generate text natively

**Interview Q: Why did decoder-only win over encoder-decoder?**
Decoder-only models are simpler, more compute-efficient per token, scale better, and can handle arbitrary tasks through instruction following. The cross-attention in encoder-decoder adds parameters and compute without proportional gain at large scale.

**Interview Q: When would you still use encoder-decoder?**
Tasks that truly benefit from bidirectionally encoding a long input while generating — like document summarization or translation. T5 still performs competitively in these domains.

### 1.8 KV Cache

**What it is:** During autoregressive generation, cache the Key (K) and Value (V) matrices from previous time steps so they don't need to be recomputed.

**How it works:**
1. At step t, compute Q_t, K_t, V_t for the new token
2. Retrieve cached K_{1:t-1}, V_{1:t-1} from memory
3. Concatenate: K = [K_{1:t-1}, K_t], V = [V_{1:t-1}, V_t]
4. Compute attention with full K, V (but only Q_t)
5. Cache the new K_t, V_t for next step

**Memory Implication:** For a model with L layers, h heads, n tokens, d_k per head:
- Cache size = 2 * L * h * n * d_k * 2 bytes (FP16) = 4 * L * h * n * d_k bytes
- Example: Llama 70B (L=80, h=64, d_k=128) with 4096 tokens: ~2.7 GB per sequence
- For batch size B: multiply by B

**Optimizations from MHA → MQA → GQA:**

| Variant | Key-Value Heads | Parameters Saved | Quality Impact |
|---------|----------------|------------------|----------------|
| MHA (Multi-Head Attention) | h = Q heads | Baseline | Baseline |
| MQA (Multi-Query Attention) | 1 KV head shared | 30% KV cache | Mild degradation |
| GQA (Grouped Query Attention) | g groups (g < h) | ~25-50% KV cache | Near MHA quality |

**GQA (Llama 2 70B, CodeLlama, Mistral):** Divide Q heads into g groups, each group shares one KV head. Common: h=32, g=8 → 4x KV cache reduction.

**Interview Q: How does KV cache scale with batch size and sequence length?**
Linearly with both. For B batch and S sequence length, KV cache = 2 * L * h * d_k * B * S. This is often the bottleneck for large batch serving.

**Interview Q: Why does GQA preserve quality better than MQA?**
MQA forces all Q heads to share one KV set — too constrained for rich attention patterns. GQA gives each group of Q heads their own KV head, striking a balance between efficiency and expressiveness.

### 1.9 Flash Attention

**Problem:** Standard attention reads/writes the full SxS attention matrix from HBM (slow), O(n^2) memory.

**Key Ideas:**
1. **Tiling:** Compute attention in blocks that fit in fast SRAM (on-chip)
2. **Online Softmax:** Compute softmax incrementally without materializing the full matrix
3. **Recomputation:** Don't store the attention matrix for backward pass — recompute it in forward

**Online Softmax Algorithm (forward):**
```
For each block:
  1. Load Q block, K block into SRAM
  2. Compute partial S_ij = Q_i * K_j^T
  3. Update running max m, sum d
  4. Compute partial attention output
```

**IO-Aware Attention:** Flash Attention is designed to minimize HBM↔SRAM reads/writes, which are the true bottleneck.

**Flash Attention 2:** Further reduces non-matmul FLOPs, better parallelization across sequence dimension.

**Flash Attention 3 (Hopper):** Leverages WGMMA instructions on H100 GPUs for ~2x speedup.

**Interview Q: How does Flash Attention reduce memory from O(n^2) to O(n)?**
By not materializing the full SxS attention matrix. It computes attention in tiles and keeps only the final output in HBM. The backward pass recomputes (instead of storing) the attention matrix.

**Interview Q: What's the practical speedup of Flash Attention?**
2-4x end-to-end training speedup for Transformers with long sequences (4K+ tokens). The longer the sequence, the bigger the win.

**Interview Q: How does the online softmax algorithm work?**
It maintains running maximum and denominator statistics, processing blocks incrementally. Each new block may increase the max, requiring rescaling of previously computed probabilities — handled by tracking a correction factor.

---

## 2. Pre-training

### 2.1 Training Objectives

**Causal LM (GPT, Llama, Mistral):**
```
L = -sum_t log P(x_t | x_{<t})
```
- Standard autoregressive, left-to-right prediction
- Each token predicts the next token
- Used by all decoder-only models

**Masked LM (BERT):**
```
L = -sum_{m in M} log P(x_m | x_{M})
```
- Randomly mask ~15% of tokens; predict masked tokens
- Bidirectional context — sees both left and right
- Substitutes 80% [MASK], 10% random token, 10% unchanged

**Prefix LM (UniLM, GLM):**
- First segment: full bidirectional attention (prefix)
- Second segment: causal attention (generation)
- Blends encoder and decoder objectives

**T5 Denoising Objective:**
- Replace consecutive corrupted spans with sentinel tokens
- Model predicts the masked-out spans
- More efficient than BERT for text-to-text tasks
- Uses a noise rate of 15% with a span length of 3

**Interview Q: Why is causal LM better for generative tasks than masked LM?**
Causal LM learns the joint distribution over sequences, making it naturally good at generation. Masked LM only learns conditionals over masked positions — it doesn't know how to generate left-to-right.

**Interview Q: How does the T5 denoising objective differ from BERT?**
T5 masks contiguous spans (not individual tokens) and uses sentinel tokens to indicate *where* each span goes. The model must generate the full text including sentinel tokens, making it a true text-to-text format.

### 2.2 Tokenization

**BPE (Byte Pair Encoding — GPT, GPT-2, RoBERTa):**
1. Start with character vocabulary
2. Repeatedly merge most frequent adjacent pair
3. Merge until desired vocabulary size
4. Can split rare words into subwords, keeps common words intact

**WordPiece (BERT, DistilBERT):**
- Similar to BPE but merges based on likelihood increase, not frequency
- Uses a unigram language model to score merges
- Produces more linguistically meaningful subwords vs BPE's frequency-based approach

**Unigram (XLNet, ALBERT, T5):**
- Start with large vocabulary, iteratively prune lowest-likelihood tokens
- Trains a unigram LM, removes tokens that least reduce likelihood
- More principled — directly optimizes for encoding efficiency

**SentencePiece (T5, Llama, Gemma):**
- Treats input as raw bytes (no need for pre-tokenization)
- Can use BPE or Unigram algorithm internally
- Handles any language without language-specific preprocessing

**Vocabulary Construction Considerations:**
- **Vocabulary size:** 32K (efficient) to 250K (better coverage, but larger embeddings)
- **Coverage vs. compactness:** Larger vocab = fewer tokens per text = faster inference, but larger embedding table
- **Multilingual:** Need to ensure fair representation of all languages in the corpus

**Interview Q: How does BPE tokenization handle unseen words?**
BPE can always fall back to character-level representation since it starts from characters. Unseen words are expressed as a sequence of subword tokens.

**Interview Q: What's the trade-off of vocabulary size in LLMs?**
Larger vocabulary: fewer tokens per document (faster decode), but larger embedding and softmax matrices. Finding the optimal size is a Pareto optimization.

### 2.3 Training Pipeline

**Data Loading:**
- Shuffle at document level, pack into sequences (concatenate documents with EOS tokens)
- Use multi-GPU data loading with distributed samplers
- Data pipeline: SSD → CPU RAM → GPU: use memory-mapped files or dedicated loaders

**Gradient Accumulation:**
```
optimizer.step() every N micro-batches
effective_batch = micro_batch_size * N * num_gpus
```
- Enables large effective batch sizes with limited GPU memory
- Simulates large batch training without needing large GPUs

**Mixed Precision Training:**

| Precision | Range | Mantissa | Use Case |
|-----------|-------|----------|----------|
| FP32 | ~3e-38 to 3e38 | 23 bits | Accumulation, master weights |
| FP16 | ~6e-5 to 6e4 | 10 bits | Forward/backward but small range → overflow risk |
| BF16 | ~3e-38 to 3e38 | 7 bits | Larger range than FP16, good for gradients |

- **FP16:** Requires loss scaling to prevent underflow/overflow. Maintain FP32 master weights.
- **BF16:** No loss scaling needed. Larger range, lower precision. Standard on A100, H100.
- Typically: forward/backward in BF16/FP16, optimizer in FP32, master weights in FP32.

**Interview Q: Why is loss scaling needed for FP16 but not BF16?**
FP16 has a small dynamic range (~6e-5 to 6e4). Gradients can easily underflow (become 0) or overflow (NaN). BF16's exponent range matches FP32, so gradient magnitude is preserved, though mantissa precision is lower.

### 2.4 Distributed Training

**Data Parallelism (DP):**
- Each GPU has a full model copy, receives different data
- Gradients are all-reduced across GPUs after each step
- Communication: O(model_size) per step

**Tensor Parallelism (TP):**
- Split individual layers across GPUs
- Each GPU computes a portion of the matrix multiplication
- Communication: O(hidden_size) per layer
- Used intra-node (high-bandwidth NVLink)

**Pipeline Parallelism (PP):**
- Split layers across GPUs (different GPUs get different layers)
- GPUs pass activations forward, gradients backward
- Communication: O(activation_size) per micro-batch
- Can suffer from "bubble" inefficiency

**1D/2D/3D Parallelism:**
- **1D:** Data parallelism only
- **2D:** Data + Tensor parallelism (common for 13B-70B)
- **3D:** Data + Tensor + Pipeline parallelism (for 100B+ models)

**ZeRO (Zero Redundancy Optimizer):**
- ZeRO-1: Shard optimizer states across GPUs
- ZeRO-2: Shard optimizer states + gradients
- ZeRO-3: Shard optimizer states + gradients + parameters

**Interview Q: When should you use tensor parallelism vs pipeline parallelism?**
TP is best for intra-node (NVLink: 600GB/s+) where communication is fast. PP is better for inter-node (100GB/s) but has bubble inefficiency. TP requires more communication bandwidth.

**Interview Q: How does ZeRO-3 differ from model parallelism?**
ZeRO-3 shards the model parameters themselves across devices, gathering them on demand. Unlike model parallelism, each device processes different micro-batches, so it's a memory optimization for data parallelism.

### 2.5 Scaling Laws

**Kaplan et al. (2020):**
- Model performance depends primarily on scale: model size, dataset size, compute budget
- **Key finding:** For compute-optimal training, model size and data size should scale together
- Performance follows a power law with compute budget
- Larger models are more sample efficient (better performance per training token)

**Chinchilla (Hoffmann et al., 2022):**
- **Key finding:** Kaplan et al. overestimated the importance of model size relative to data size
- For optimal model, **double the data** when you **double the model size**
- Most existing LLMs are "undertrained" (model too large for the training data)
- Chinchilla optimal: 70B model should be trained on ~1.4T tokens
- Practical implication: many companies now train smaller models for longer

**Optimal Model Size / Data Ratio:**
```
L(N, D) = E + A/N^alpha + B/D^beta
```
- N = parameters, D = tokens
- Optimal: N_opt ∝ C^a, D_opt ∝ C^b — both scale with compute C

**Interview Q: What does the Chinchilla paper tell us about model design?**
Many models in 2022-2023 (GPT-3, Gopher) were undertrained. Chinchilla suggests using smaller models trained on more data yields better perplexity for the same compute budget. This drove the trend toward 7B-13B models trained for 2T+ tokens.

**Interview Q: How do scaling laws inform decisions about training a new model?**
Given a compute budget C, you can compute optimal N and D. If training is compute-bound, aim for the Chinchilla-optimal ratio. If inference cost matters, lean toward smaller N with more training tokens.

### 2.6 Learning Rate Schedules

**Cosine Decay:**
```
lr(t) = lr_min + 0.5 * (lr_max - lr_min) * (1 + cos(t/T * pi))
```
- Smooth decay, common in LLM training
- Often with a linear warmup period (first N steps)

**Linear Warmup (common in all schedules):**
- Start from ~0, linearly increase to lr_max over warmup_steps
- Prevents early divergence when the model is untrained

**Constant LR + Decay (used by some):**
- Train at lr_max for most of training, then decay
- Useful when you're unsure about total training steps

**Schedule Types:**
- **Inverse square root:** lr(t) = lr_max * sqrt(warmup / max(warmup, t))
- **Warmup-stable-decay:** Warmup → constant → cosine decay to 0

**Interview Q: Why do LLMs use a warmup phase?**
Early in training, the model parameters are random and gradients are noisy. High learning rates can cause divergence. Warmup allows the optimizer to accumulate gradient statistics (Adam momentum) before applying the full learning rate.

### 2.7 Stability Techniques

**Gradient Clipping:**
- Clip global gradient norm to a maximum value (typically 1.0)
- Prevents gradient explosion from destabilizing training
- Applied before optimizer step: g = g * min(1, threshold / ||g||)

**Loss Normalization:**
- Normalize the loss by sequence length or number of tokens
- Ensures consistent gradient scale across different batch sizes

**Z-Loss (PaLM):**
```
L_z = alpha * log(sum(exp(logits)))
```
- Added to the main loss to penalize excessively large logits
- Prevents logit growth, improving training stability
- Particularly important for long training runs

**Interview Q: Why does gradient clipping prevent loss spikes?**
When a batch contains an outlier, gradients can become very large. Without clipping, this causes an optimizer step that "overshoots" the optimum, potentially sending loss to NaN. Clipping bounds the step size.

---

## 3. Fine-tuning

### 3.1 Full Fine-tuning vs Parameter-Efficient Methods

**Full Fine-tuning:**
- Update all model parameters
- Requires full model gradient state (prohibitive for 70B+)
- Each task requires a separate full model copy
- Best quality for small models

**LoRA (Low-Rank Adaptation):**
```
W' = W + BA  where B in R^(dxr), A in R^(rxk), r << min(d,k)
```
- Freeze original weights, train low-rank adapter matrices
- Hugely memory efficient: only train ~0.1-1% of parameters
- Keeps original model intact, swap adapters at inference

**QLoRA:**
- LoRA + 4-bit quantization of base model
- Can fine-tune 65B model on single 48GB GPU
- Uses NF4 quantization, double quantization, paged optimizers

**IA3:**
- Learned vectors that rescale keys, values, and FFN activations
```
k' = l_k * k,  v' = l_v * v,  ff' = l_ff * ff
```
- Even fewer parameters than LoRA

**Adapters:**
- Insert small bottleneck MLP layers after each Transformer sub-layer
- More parameters than LoRA, but more expressive

**Interview Q: When would you choose full fine-tuning over LoRA?**
When quality is paramount and you have enough compute. If the downstream task is very different from pre-training (e.g., code → medical), full fine-tuning may capture domain-specific knowledge better than low-rank adaptation.

**Interview Q: Can LoRA achieve the same quality as full fine-tuning?**
For most instruction-following and downstream tasks, yes — especially with sufficient rank (r=64+). For tasks requiring massive domain shift (e.g., very specialized codebase), full fine-tuning may still win.

### 3.2 LoRA Deep Dive

**Rank Selection:**
- Typical ranks: r=8 (minimal), r=16 (standard), r=64 (high capacity)
- Higher rank = more expressiveness but more parameters and memory
- Rule of thumb: r=16 for most tasks, r=64 for complex tasks
- Can search over ranks using validation loss

**Scaling Factor:**
```
output = Wx + (alpha / r) * BAx
```
- `alpha`: scaling factor for the LoRA update
- Initialize alpha = r (update magnitude ~1)
- Higher alpha → stronger LoRA influence
- Can be adjusted post-training as an ensemble weight

**Weight Initialization:**
- A: Kaiming uniform (or normal with std ~0.02)
- B: Zeros
- Ensures BA = 0 at start → no disruption to pre-trained model
- Only introduces drift during training

**Weight Tying / No Weight Tying:**
- LoRA typically does NOT tie weights across layers
- Each layer gets its own independent A, B matrices

**Interview Q: Why initialize B as zeros and A with random values?**
So the LoRA update BA = 0 at initialization. The model output is unchanged. As training progresses, BA learns the task-specific update from zero. If both were random, you'd immediately change the model output.

**Interview Q: How do you choose between applying LoRA to Q, K, V, O, FFN layers?**
Apply to all projection layers for maximum capacity. For memory savings, apply to the most impactful layers (attention projections first). Many implementations default to Q and V.

### 3.3 QLoRA

**4-bit NormalFloat (NF4):**
- Information-theoretically optimal data type for normally distributed weights
- Normalize weights to N(0,1), then quantize to 16 equally probable values
- Better than uniform 4-bit quantization for normally distributed weights

```
Quantization: w_q = argmin |w - q_i| for i in 0..15
NF4 bins: equal mass under normal distribution
```

**Double Quantization:**
- Quantize the quantization constants themselves (FP8 → FP4)
- Saves additional ~0.5 bits per parameter
- Example: first quantize to NF4 per 64 weight block, then quantize fp32 scale factors to fp8

**Paged Optimizers:**
- Use CPU RAM for optimizer states during fine-tuning
- Move pages to GPU on demand when GPU memory is insufficient
- Prevents OOM errors on memory-constrained GPUs

**Interview Q: How does QLoRA fine-tune a 65B model on a single GPU?**
1. 4-bit NF4 quantization of base model (65B → ~32 GB)
2. LoRA adapters in FP16 (~0.5 GB)
3. Gradient checkpointing (trades compute for memory)
4. Paged optimizers (swap optimizer states to CPU)
Total: ~35 GB, fits in a 48GB GPU.

**Interview Q: Is QLoRA quality as good as 16-bit LoRA?**
Yes, surprisingly — within ~0.5% of full precision for most tasks. The key is that the pre-trained weights are quantized, but LoRA updates accumulate in FP16, providing the corrective delta.

### 3.4 P-Tuning, Prefix Tuning, Prompt Tuning

**Prompt Tuning:**
- Prepend learnable "virtual tokens" to the input
- Only these embeddings are trained (freeze entire model)
- Simple but less expressive than LoRA
- Must match prompt format for each task

**Prefix Tuning:**
- Learn key-value prefixes for each Transformer layer
- More expressive than prompt tuning (modifies all layers)
- Inserted into the key-value attention: [PREFIX_K, K], [PREFIX_V, V]

**P-Tuning (v2):**
- Use an LSTM/MLP to generate the prompt embeddings
- More parameter-efficient than prefix tuning
- Better optimization landscape

**Comparison:**

| Method | Parameters | Quality | Complexity |
|--------|-----------|---------|------------|
| Prompt Tuning | ~10K-100K | Low-Medium | Very Simple |
| Prefix Tuning | ~0.1M-1M | Medium | Medium |
| P-Tuning | ~10K-100K | Medium | Medium |
| LoRA | ~0.1M-100M | High | Simple |
| Full FT | 100M-100B | Highest | Complex |

**Interview Q: When would you use prompt tuning instead of LoRA?**
When you need extreme memory efficiency and the task is straightforward. Prompt tuning is also easier to deploy — just swap a small embedding vector. Good for task routing in multi-task systems.

### 3.5 RLHF (Reinforcement Learning from Human Feedback)

**Three-stage pipeline:**
1. **SFT:** Supervised fine-tuning on human demonstrations
2. **Reward Model:** Train a model to predict human preference
3. **PPO:** Optimize the policy (LLM) to maximize reward while staying close to SFT model

**Reward Model Training:**
```
L_RM = -E[log(sigma(r_better - r_worse))]
```
- Pairwise preference: given response A (better) and B (worse)
- Train reward model to assign higher reward to better response
- Typically ~6B parameters (smaller than policy)

**PPO Algorithm for LLMs:**
1. Sample responses from current policy (LLM)
2. Compute reward from RM + KL penalty
3. Update policy to maximize reward
4. Constraint: KL(pi || pi_ref) to prevent reward hacking

**PPO Objective:**
```
L = E[min(ratio * A, clip(ratio, 1-eps, 1+eps) * A)]
ratio = pi_theta(y|x) / pi_old(y|x)
A = reward - baseline
```

**KL Divergence Penalty:**
- Added to PPO reward: R = R_RM - beta * KL(pi || pi_ref)
- Beta controls how much the policy can deviate from SFT
- Prevents the model from exploiting reward model (reward hacking)
- Typical beta: 0.01 - 0.05

**Interview Q: Why do we need a KL penalty in RLHF?**
Without it, the policy can learn to generate text that scores high on the reward model but is nonsensical or unhelpful to humans (reward hacking). The KL penalty anchors the policy to the SFT model, preserving language quality.

**Interview Q: Why train a separate reward model instead of using human feedback directly?**
Human feedback is expensive and slow. The reward model learns to approximate human preferences, then provides fast, cheap reward signals for PPO training. It also smooths out noisy human judgments.

### 3.6 DPO (Direct Preference Optimization)

**Key Insight:** The RLHF reward + PPO optimization can be solved in closed form.

**DPO Loss:**
```
L_DPO = -E[log(sigma(beta * log(pi(y_w|x) / pi_ref(y_w|x))
                      - beta * log(pi(y_l|x) / pi_ref(y_l|x))))]
```
- Directly optimizes policy using preference pairs
- No reward model needed
- No PPO training loop

**Why It Works:**
- The optimal policy under KL constraint has a closed form: pi*(y|x) ∝ pi_ref(y|x) * exp(R(y)/beta)
- Rearranging shows that the reward is implicit in the policy ratio
- DPO eliminates the need for explicit reward model training

**Advantages over RLHF:**
- Much simpler (no reward model, no PPO loop)
- More stable training
- Lower compute cost
- Competitive quality

**Disadvantages:**
- Doesn't easily support multiple reward signals (helpfulness, harmlessness, honesty)
- Less explored at extreme scale (70B+)
- May not handle multi-turn RL as naturally

**Interview Q: How does DPO avoid training a reward model?**
DPO reparameterizes the reward function in terms of the policy itself: R(x,y) = beta * log(pi(y|x) / pi_ref(y|x)). The KL-constrained reward maximization has a known closed form, so we can directly optimize the policy on preference data.

**Interview Q: When would you choose RLHF over DPO?**
When you need to combine multiple reward signals (e.g., helpfulness + safety + factuality), RLHF's explicit reward model makes it easy to weigh or compose rewards. DPO's implicit reward is harder to decompose.

### 3.7 Instruction Tuning

**Process:** Fine-tune LLM on a diverse set of (instruction, response) pairs to make it follow instructions.

**Dataset Construction:**
- **Human-written:** OpenAssistant, ShareGPT, Dolly
- **Synthetic:** Self-Instruct, Alpaca (GPT-4 generated)
- **Augmented:** Evol-Instruct (WizardLM), Orca (GPT-4 explanations)
- **Quality filtering:** Include high-quality examples, remove noisy or toxic responses

**Template Formatting:**
```
### Instruction:
{instruction}

### Input:
{input}

### Response:
{response}
```
- Consistent format is critical
- Models can be sensitive to template changes
- Chat templates for multi-turn: [INST], [ASST], or <|im_start|>system

**Multi-task Learning:**
- Train on 1,000+ tasks simultaneously
- Benefits: better generalization, more robust instruction following
- Tasks: summarization, QA, coding, reasoning, creative writing
- Can use task balancing (temperature sampling) to avoid overfitting to high-resource tasks

**Interview Q: How does instruction tuning enable zero-shot generalization?**
By training on many tasks in a unified format, the model learns the meta-skill of "follow instructions." It can apply this to unseen tasks at inference time. The diversity of training tasks is more important than the volume of any single task.

**Interview Q: What is the data flywheel for instruction tuning?**
Use the model to generate responses → humans rate/correct them → use corrected data for fine-tuning → improved model → better responses. This creates a virtuous cycle of improvement.

---

## 4. Inference

### 4.1 Quantization

| Method | Precision | Quality | Speed | Key Technique |
|--------|-----------|---------|-------|---------------|
| GPTQ | 4-bit (or 3/2) | High | Fast (GPU) | Optimal Brain Quantization |
| AWQ | 4-bit | Very High | Fast (GPU) | Activation-aware scaling |
| GGUF | 2-8 bit variable | Medium-High | Medium (CPU/GPU) | Multiple quantization types |

**GPTQ (Frantar et al., 2022):**
- Post-training quantization based on Optimal Brain Quantizer (OBQ)
- Quantize weights one-by-one, adjust remaining weights to compensate
- Uses a calibration dataset (128-1024 samples)
- Layer-wise: for each layer, find weight w_q that minimizes output MSE
- Update remaining weights: delta = (w_q - q) * H^{-1}_{:,q} / H^{-1}_{q,q}

**AWQ (Lin et al., 2023):**
- Observation: a small fraction (~1%) of "salient" weight channels are much more important
- Salience determined by activation magnitude (not weight magnitude)
- Scale up salient channels before quantization to preserve them
- No backpropagation needed — faster than GPTQ

**GGUF (llama.cpp format):**
- Originally GGML format, now superseded by GGUF
- Support from 2-bit to 8-bit quantization
- Q4_K_M (4-bit medium): good quality/speed trade-off
- Q5_K_M (5-bit medium): near-lossless at ~5.5 bits per weight
- Runs on CPU, Apple Silicon (Metal), GPU (CUDA)

**Interview Q: How does GPTQ determine which weights to quantize?**
GPTQ uses the Hessian of the loss with respect to each weight. Weights with low Hessian (low curvature) are quantized first — they have less impact on output quality. Remaining weights are updated to compensate for the quantization error.

**Interview Q: Why does AWQ achieve better quality than GPTQ at the same bit width?**
AWQ identifies and protects salient channels (1% of weights) by scaling them up before quantization. GPTQ treats all weights uniformly. Preserving these channels is disproportionately important for output quality.

### 4.2 Speculative Decoding

**Problem:** Autoregressive decoding is slow — one token at a time, each requiring a full forward pass.

**Idea:** Use a small, fast "draft model" to propose K candidate tokens, then verify all K in a single forward pass of the large "target model."

**Process:**
1. Draft model (e.g., 1B params) generates K tokens autoregressively: y_1, ..., y_K
2. Target model (e.g., 70B params) runs one forward pass with draft tokens
3. Acceptance criterion: for each position i, a sample u ~ Uniform(0,1), accept if u < min(1, p_target(y_i) / p_draft(y_i))
4. If token rejected at position j, discard j..K, keep accepted prefix, resample from adjusted distribution

**Acceptance Criteria (Rejection Sampling):**
```
accept if p_target(y_i) / p_draft(y_i) >= 1  (target always agrees)
else accept with probability p_target / p_draft
```
- This guarantees the output distribution matches the target model's distribution
- You get the quality of the target model with latency of the draft model

**Tree-based Speculation:**
- Draft model generates a speculative tree (multiple branches)
- Target model verifies entire tree in one pass
- Increases acceptance rate by exploring multiple continuations

**Interview Q: Does speculative decoding change the output distribution?**
No. With proper rejection sampling (Metropolis-Hastings correction), the output distribution is *exactly* the target model's distribution. No quality loss.

**Interview Q: When is speculative decoding most beneficial?**
When the draft model is much faster than the target model (e.g., 1B draft, 70B target). Speedup = 1 / (1/K + latency_ratio). Empirically: 2-3x speedup in practice.

### 4.3 Caching

**KV Cache Optimization:**
- Cache K and V for all previous tokens
- Memory: ~1-2 GB per 1000 tokens for 7B model
- Optimization: use FP8 or INT8 for KV cache (quantize)

**vLLM Paged Attention:**
- Inspired by virtual memory paging in operating systems
- KV cache is stored in non-contiguous blocks ("pages")
- Each block holds KV for K tokens (e.g., block_size=16)
- Blocks are allocated on demand, avoiding pre-allocation
- Dramatically reduces memory waste (no need to allocate for max seq len)

**Paged Attention Key Mechanism:**
- Block table maps logical tokens → physical blocks
- Uses a block manager for allocation and eviction
- Supports copy-on-write for shared prefixes (caching common prefixes)

**Prefix Caching:**
- Cache KV cache entries for common prefix strings (e.g., system prompts)
- When a request starts with a cached prefix, skip recomputation
- Significant savings for batched serving of similar requests
- Implemented in vLLM, TGI, and SageMaker

**Interview Q: How does vLLM's paged attention reduce memory fragmentation?**
Standard KV cache pre-allocates for max sequence length (wasteful). Paged attention allocates in fixed-size blocks on demand, fitting exact needs. The block table handles mapping, so unused capacity from other sequences can be used.

**Interview Q: What's the hit rate for prefix caching in production?**
Depends on workload. Chatbots with shared system prompts: 60-90% cache hit rate. Ad-hoc queries: 0-10%. Reducing system prompt processing from 800 to 80 tokens per user message is a massive latency improvement.

### 4.4 Batching

**Continuous Batching (Orca, vLLM):**
- Instead of waiting for all sequences in a batch to finish generation, dynamically add/remove sequences
- When a sequence generates EOS, immediately insert a new sequence into the running batch
- Maximizes GPU utilization by always keeping it busy

**Dynamic Batching:**
- Collect requests for a fixed time window, then batch them together
- Simpler but can add latency (waiting for batch to fill)

**Inflight Batching / Iteration-level Batching:**
- After each decoding step, check for finished sequences
- Remove finished sequences, inject new ones
- Requires careful memory management (different sequences have different KV cache sizes)

**Interview Q: How does continuous batching improve throughput vs static batching?**
Static batching: if one sequence finishes early, the rest of the batch waits with idle slots. Continuous batching fills those slots immediately, increasing GPU utilization from ~60% to ~95%.

**Interview Q: What are the implementation challenges of continuous batching?**
Managing KV cache with variable-length sequences (vLLM's paged attention solves this). Handling different sequence lengths in attention masks. Ensuring fair scheduling across requests.

### 4.5 Serving

**vLLM:**
- Paged attention, continuous batching, high throughput
- Supports GPTQ, AWQ, SqueezeLLM quantization
- OpenAI-compatible API
- Best for: general-purpose LLM serving

**TensorRT-LLM (NVIDIA):**
- NVIDIA CUDA-optimized inference engine
- Supports all quantization types, speculative decoding, inflight batching
- Best performance on NVIDIA hardware (A100, H100, B200)
- More complex setup (requires model compilation)

**TGI (Text Generation Inference, Hugging Face):**
- Production-grade serving
- Continuous batching, watermarking, safety filtering
- Easy to use (one command deployment)
- Good for: quick deployment, moderate scale

**ONNX Runtime:**
- Cross-platform inference optimization
- Best for: multi-platform deployment (cloud + edge + mobile)
- Model must be converted to ONNX format

**Interview Q: How would you choose between vLLM, TensorRT-LLM, and TGI?**
vLLM for best ease/performance trade-off. TensorRT-LLM if you need maximum throughput on NVIDIA hardware and can manage setup complexity. TGI for quick deployment with safety features. Consider also: quantization support, custom kernels, and integration with your stack.

---

## 5. RAG (Retrieval-Augmented Generation)

### 5.1 RAG Architecture

**Components:**
1. **Indexing:** Prepare documents for retrieval (chunk → embed → store)
2. **Retrieval:** Find relevant passages for a query
3. **Augmentation:** Combine retrieved passages with original query
4. **Generation:** LLM generates answer using augmented prompt

**Pipeline:**
```
Query → Retriever → top-k passages → Augmenter → LLM → Response
                ↓
         Vector DB (pre-indexed)
```

**Naive RAG (Lewis et al., 2020):**
- Retrieve then read: single retrieval step, then generate
- Can miss context if the initial retrieval is poor

**Iterative RAG:**
- Multi-step: retrieve → generate partial answer → retrieve more based on what's needed
- Better for complex, multi-hop questions

**Self-RAG (Asai et al., 2023):**
- LLM generates "reflection tokens": whether to retrieve, which passages are relevant, whether output is supported
- Dynamic retrieval decisions at per-token level
- Significantly improves factuality

**CRAG (Corrective RAG, Yan et al., 2024):**
- Evaluate retrieval quality, take corrective actions:
  - Good: use retrieved passages
  - Bad: discard and use web search
  - Ambiguous: combine with web search

**RAPTOR (Sarthi et al., 2024):**
- Build a hierarchical document tree (cluster → summarize → repeat)
- Retrieve at different abstraction levels
- Better for questions requiring both detailed and high-level answers

**GraphRAG (Microsoft, 2024):**
- Build knowledge graph from documents
- Retrieve entities, relationships, and communities
- Excellent for multi-hop and global reasoning questions
- Used for enterprise document QA

**Interview Q: What failure modes does Self-RAG address that Naive RAG doesn't?**
Naive RAG always retrieves, even when the LLM already knows the answer (wasteful) or the retrieval is noisy (hurts quality). Self-RAG dynamically decides WHEN to retrieve and WHETHER the retrieved passages are actually relevant, reducing noise.

**Interview Q: How does GraphRAG differ from vector-based RAG?**
Vector RAG retrieves semantically similar document chunks. GraphRAG retrieves entities and relationships from a knowledge graph, enabling structured reasoning about connections between concepts. Better for questions like "what do X and Y have in common?"

### 5.2 Chunking Strategies

**Fixed-Size Chunking:**
- Split document by token or character count (e.g., 512 tokens with 128 overlap)
- Pros: simple, fast
- Cons: can split mid-sentence or mid-concept

**Semantic Chunking:**
- Split at natural boundaries (paragraphs, sections, sentences)
- Use embedding similarity between sentences to detect topic shifts
- Cons: more computationally expensive

**Recursive Chunking:**
- Try different separators (paragraph → sentence → word) until chunk fits
- Used by LangChain's RecursiveCharacterTextSplitter
- Ensures chunks respect document structure when possible

**Agentic Chunking:**
- Use an LLM to determine where to split
- Most accurate, most expensive
- Example: "Summarize this section, then summarize next section"

**Chunk Overlap:**
- Important to maintain context across chunk boundaries
- Typical overlap: 10-20% of chunk size
- Too little: lose context at boundaries
- Too much: increase indexing size and retrieval noise

**Interview Q: How do you determine the optimal chunk size for a RAG system?**
Depends on: (1) embedding model context length, (2) LLM context window, (3) document structure, (4) question type. Short chunks (128-256 tokens) for precise fact retrieval. Longer chunks (512-1024 tokens) for summarization and reasoning. Test with your specific use case.

**Interview Q: Why is chunk overlap important?**
Without overlap, a question that spans a chunk boundary will miss one side of the context. Overlap ensures both sides are represented in at least one chunk, improving retrieval recall.

### 5.3 Embedding Models

**Sentence-Transformers (SBERT):**
- BERT-derived models optimized for semantic similarity
- Bi-encoder: compute embeddings independently
- Cross-encoder: directly scores similarity (expensive, used for re-ranking)

**Popular Models:**
- `text-embedding-3-small` / `text-embedding-3-large` (OpenAI)
- `intfloat/e5-mistral-7b-instruct` (Mistral-based, 1024 dimensions)
- `BAAI/bge-large-en-v1.5` (BAAI, 1024 dimensions)
- `sentence-transformers/all-MiniLM-L6-v2` (small, 384 dimensions)

**How to Choose:**
| Factor | Consideration |
|--------|---------------|
| Dimensions | Higher = more expressive, slower, more storage |
| Context length | Longer = better for documents, slower |
| Domain | Pick model trained on similar data |
| Latency | Smaller models for real-time |
| Language | Multilingual if needed |

**Fine-tuning Embeddings:**
- Use contrastive learning: (query, positive_passage, negative_passage) triplets
- Loss: InfoNCE or triplet loss
- Negative mining: hard negatives improve discrimination
- Domain fine-tuning: train on in-domain (query, passage) pairs

**Interview Q: How do you evaluate which embedding model is best for your use case?**
Create a test set of (query, relevant_passage) pairs. Measure Recall@K (does the relevant passage appear in top-k?). Also check retrieval latency and storage requirements for your scale.

**Interview Q: What is hard negative mining for embedding fine-tuning?**
Hard negatives are passages that are semantically similar to the query but not relevant (e.g., same product, different model). Training with hard negatives forces the model to learn fine-grained distinctions, improving retrieval precision.

### 5.4 Vector Databases

**HNSW (Hierarchical Navigable Small World):**
- Multi-layer graph structure for approximate nearest neighbor search
- Top layers: coarse grain (long edges), bottom layers: fine grain (short edges)
- Search: start at top layer, greedily descend, switch to finer layers
- Recall: 95-99+% at 10-100x speed vs brute force
- Index build time: O(n log n)
- Memory: ~1.2-2x the raw data size

**IVF (Inverted File Index):**
- Cluster vectors into Voronoi cells (using k-means)
- Search: find nearest clusters, search only those
- IVF+PQ: add product quantization to reduce memory (compress vectors to ~8-32 bytes)
- Faster index build than HNSW
- Lower recall than HNSW at same speed

**Disk-ANN:**
- Graph-based index stored on SSD, not RAM
- Uses Vamana algorithm (similar to HNSW but disk-friendly)
- Can handle billions of vectors on a single machine
- Reads from disk in large contiguous chunks (SSD-optimized)

**Comparison:**

| Index | Speed | Recall | Memory | Build Time | Scale |
|-------|-------|--------|--------|------------|-------|
| HNSW | Fastest | Highest | High | Moderate | Millions |
| IVF+PQ | Fast | Moderate | Low | Fast | Hundreds of M |
| Disk-ANN | Moderate | High | Low (disk) | Slow | Billions |

**Interview Q: How does HNSW achieve both high speed and high recall?**
The hierarchical structure: top layers provide an approximate "GPS" to which neighborhood to search, bottom layers find the exact neighbors within that neighborhood. The probabilistic skip-list-like structure ensures logarithmic search time.

**Interview Q: When would you choose IVF+PQ over HNSW?**
When memory is constrained. IVF+PQ with product quantization reduces each vector to ~16 bytes — 50x compression of FP32 vectors. HNSW stores full-precision vectors. IVF+PQ is also faster for insertion-heavy workloads (add new vectors).

### 5.5 Retrieval

**Dense Retrieval:**
- Use embedding models for semantic search
- Strengths: captures meaning, works with paraphrases, handles vocabulary mismatch
- Weaknesses: needs training data, can be fooled by distribution shift, context length limit

**Sparse Retrieval (BM25):**
- TF-IDF based: term frequency * inverse document frequency with length normalization
- Strengths: no training needed, exact keyword matching, fast
- Weaknesses: vocabulary mismatch, doesn't capture meaning

**SPLADE (Sparse Lexical and Dense):**
- Learn to generate sparse vectors (term weighting)
- Each dimension = vocabulary token (like BM25)
- Advantages: combines interpretability of sparse + quality of dense
- Produces learned term importance scores

**Hybrid Search:**
```
score = alpha * dense_score + (1-alpha) * sparse_score
```
- Combine BM25 + dense retrieval scores
- Weighted sum with tunable alpha
- Best of both worlds: semantic + exact match
- Common: alpha=0.5 (balanced), tuned on validation

**Re-ranking:**
- Stage 1: fast retrieval (BM25 or bi-encoder) to get top-100 candidates
- Stage 2: slow re-ranker (cross-encoder) to score top-100
- Stage 3: return top-5 after re-ranking
- Cross-encoder: computes [CLS] query passage [SEP] query passage — expensive but accurate

**Interview Q: Why is hybrid search (dense + sparse) better than either alone?**
Dense retrieval excels at semantic similarity but can miss exact matches. BM25 hits exact keywords but misses meaning. They have complementary failure modes, so combining them improves both precision and recall.

**Interview Q: What's the latency budget for a two-stage retrieval system?**
Stage 1 (bi-encoder or BM25): ~10-50ms for 1M candidates. Stage 2 (cross-encoder): ~5-20ms per candidate. Total: 100ms-2s depending on candidate count. Use parallel re-ranking across GPUs for high throughput.

### 5.6 Augmentation

**Prompt Construction:**
```
Answer the question based ONLY on the provided context.
Question: {question}
Context: {retrieved_passages}
Answer:
```
- Clear instructions to use only provided context (reduces hallucination)
- Instruct the model to say "I cannot answer from the given context" if irrelevant

**Context Window Management:**
- Fit as many relevant passages as possible within context window
- Priorities: highest relevance scores, most recent passages
- Techniques: truncate less relevant passages, summarize multiple passages
- For very long contexts: use sliding window or hierarchical processing

**Position Bias in RAG:**
- LLMs tend to favor information at the beginning and end of the context
- Place the most relevant passage at the end (recency bias) or beginning
- Can also prompt "Pay attention to all passages equally"

**Interview Q: How do you handle cases where no retrieved passages are relevant?**
Prompt the model to only answer from provided context → model should output "No relevant information found." For production, add a fallback: web search, or retrieve from a different data source.

### 5.7 Evaluation

**Retrieval Metrics:**

| Metric | Meaning | Formula |
|--------|---------|---------|
| Recall@K | Fraction of relevant docs in top-K | relevant_in_top_k / total_relevant |
| MRR (Mean Reciprocal Rank) | Inverse rank of first relevant doc | mean(1/rank_first_relevant) |
| NDCG (Normalized Discounted Cumulative Gain) | Ranking quality with graded relevance | DCG / IDCG |

- **Recall@K:** Primary metric for RAG (did we retrieve what we need?).
- **MRR:** Good for QA when only one relevant doc exists.
- **NDCG:** Best for graded relevance (e.g., 0=irrelevant, 1=somewhat, 2=very).

**Generation Metrics:**

| Metric | What it measures | How |
|--------|------------------|-----|
| Correctness | Is the answer factually right? | Human eval or LLM-as-judge |
| Faithfulness | Is the answer supported by context? | NLI model or LLM-as-judge |
| Completeness | Does answer fully address the question? | Human eval |
| Hallucination rate | % of facts not in context | LLM-as-judge or factual consistency model |

- **LLM-as-judge:** Use a strong LLM (GPT-4, Claude) to score outputs. Cheap, fast, correlates with human eval.
- **Grounding check:** Ask LLM "is each claim in the answer supported by the context?"

**Interview Q: Why does Recall@K matter more than precision for RAG?**
Missing a key passage (low recall) means the LLM has no way to know the correct answer, likely causing hallucination. Some irrelevant passages (low precision) can be filtered by the LLM. Recall failures are catastrophic; precision failures are recoverable.

**Interview Q: How do you measure faithfulness automatically?**
Use a natural language inference (NLI) model: for each claim in the answer, check if the context entails it. If context entails the claim, faithful. If neutral or contradictory, unfaithful. This correlates well with human judgments.

### 5.8 Advanced RAG

**Iterative RAG:**
- Step 1: Retrieve → generate partial answer
- Step 2: Identify missing information
- Step 3: Generate new query → retrieve more
- Repeat until answer is sufficient

**Self-RAG:**
- LLM outputs special tokens: [Retrieve], [No Retrieve], [Relevant], [Irrelevant], [Supported], [Not Supported]
- Trained on these reflection tokens
- Dynamically decides retrieval strategy per segment

**RAPTOR:**
- Build hierarchical summaries of the document corpus:
  - Step 1: Cluster document chunks
  - Step 2: Summarize each cluster
  - Step 3: Cluster summaries, repeat
- Retrieve from multiple levels (leaf + summary)
- Better for questions needing overview + detail

**GraphRAG (Microsoft):**
- Extract entities, relationships, claims from documents
- Build community summaries using Leiden algorithm
- Query: find relevant entities → traverse graph → retrieve passages
- Excellence in multi-hop global sensemaking

**Interview Q: Compare Self-RAG and GraphRAG for enterprise document QA.**
Self-RAG excels when you need per-segment retrieval decisions and factuality checks — good for compliance-critical use cases. GraphRAG excels when you need to answer questions spanning multiple documents (e.g., "what is our R&D strategy across all business units?").

---

## 6. Agents

### 6.1 Agent Architectures

**ReAct (Reasoning + Acting, Yao et al., 2022):**
```
Thought: I need to find the weather in Tokyo
Action: search_weather["Tokyo"]
Observation: 22°C, sunny
Thought: The weather is 22°C and sunny
Answer: The weather in Tokyo is 22°C and sunny.
```
- Interleave reasoning (thoughts) with actions
- Observations are fed back as text
- Simple, works well, interpretable

**Plan-and-Execute:**
1. **Plan:** LLM decomposes task into sub-tasks
2. **Execute:** Execute sub-tasks sequentially (or parallel)
3. **Re-plan (optional):** Adjust plan based on execution results
- Better for complex multi-step tasks
- Can use different LLMs for planning vs execution

**Reflexion (Shinn et al., 2023):**
- Agent has short-term memory (current trajectory) and long-term memory (past experiences)
- After task completion, evaluate and store "lessons" in memory
- Future episodes retrieve relevant lessons
- Improves via self-reflection

**Tree-of-Thought (ToT, Yao et al., 2023):**
- Maintain a tree of reasoning paths
- At each step, generate K continuations from current state
- Evaluate each continuation (certainty, progress)
- Use BFS/DFS to explore promising branches
- More expensive but more powerful for complex reasoning

**Comparison:**

| Architecture | Reasoning | Memory | Best For |
|-------------|-----------|--------|----------|
| ReAct | Simple chain | None | Simple tool use |
| Plan & Execute | Hierarchical plan | Plan state | Multi-step tasks |
| Reflexion | Self-reflective | Episodic | Learning from mistakes |
| Tree-of-Thought | Tree search | Search tree | Complex reasoning |

**Interview Q: When should you use Tree-of-Thought instead of ReAct?**
When the task requires exploration (multiple possible paths) and the cost of wrong decisions is high. ToT is used for math puzzles, planning problems, and creative tasks where the optimal path isn't obvious from the start.

**Interview Q: How does Reflexion improve over ReAct?**
Reflexion stores successful strategies and common mistakes as episodic memories. In subsequent runs, it retrieves relevant experiences, avoiding mistakes it made before. This gives the agent an improving trajectory over time.

### 6.2 Tool Calling

**OpenAI Function Calling API:**
```json
{
  "name": "search_database",
  "description": "Search the internal database for information",
  "parameters": {
    "type": "object",
    "properties": {
      "query": {
        "type": "string",
        "description": "Search query"
      },
      "limit": {
        "type": "integer",
        "description": "Max results"
      }
    },
    "required": ["query"]
  }
}
```

**Process:**
1. Define tools with JSON Schema for parameters
2. LLM outputs a function call (tool name + arguments)
3. Execute the tool, get result
4. Feed result back to LLM
5. LLM either calls another tool or produces final answer

**Tool Description Best Practices:**
- Clear, detailed names and descriptions
- Explicit parameter schemas with types and descriptions
- Include examples in descriptions
- Limit the number of tools per prompt (performance degrades with too many)

**Parameter JSON Schema:**
- Type system: string, integer, number, boolean, array, object
- Enum constraints where applicable
- Nested objects for structured parameters
- Required/optional distinction

**Interview Q: How does the LLM decide which tool to call?**
The tool descriptions are concatenated in the system prompt. The LLM uses its pre-trained understanding of the descriptions to match the user's intent to the appropriate tool. Better descriptions → more accurate tool selection.

**Interview Q: What happens if the LLM generates an invalid tool call?**
Two strategies: (1) Parse the output, if invalid, return an error message as the tool's observation and let the LLM try again. (2) Use constrained decoding (grammar-based sampling) to force valid JSON output.

### 6.3 Memory

**Short-term Memory (Context Window):**
- The current conversation history
- Limited by context window size (4K-100K tokens)
- Managed via truncation, summarization, or sliding window
- Most agents use the context for immediate reasoning

**Long-term Memory (Vector Store):**
- Store past conversations, learned knowledge, task outcomes
- Retrieved via semantic similarity when relevant
- Enables learning from past experiences (Reflexion)
- Typically use same embedding model as RAG

**Episodic Memory:**
- Stores specific episodes: (state, action, outcome, reflection)
- Retrieved for similar states in the future
- Like a case library of past experiences
- Enables the agent to improve over time

**Memory Consolidation:**
- Short-term → consolidate → long-term (as the agent works)
- Periodically summarize conversation history
- Extract key facts, decisions, and their outcomes
- Prune redundant or outdated memories

**Interview Q: How do you prevent memory from growing unbounded?**
Use consolidation: periodically summarize conversation segments, prune low-importance memories, archive old episodes. Set a maximum token budget for each memory tier. Use recency, relevance, and importance scores for eviction.

**Interview Q: Compare the memory needs of a customer support agent vs a coding agent.**
Customer support: needs short-term (current conversation) + long-term (user history, preferences, past issues). Coding agent: needs short-term (current file/PR context) + episodic memory (past fixes, project conventions). Coding benefits from longer context windows for code files.

### 6.4 Multi-Agent Systems

**Orchestration:**
- One "manager" agent decomposes tasks, assigns to specialized agents
- Example: AutoGPT, Microsoft AutoGen
- Manager monitors progress, reassigns if stuck

**Communication:**
- Agents communicate via messages (structured or natural language)
- Common formats: JSON with sender, receiver, content, metadata
- Debate or discussion between agents with different roles

**Delegation:**
- Agent A determines it needs Agent B's expertise
- Sends a request with context
- Agent B processes and returns
- Agent A incorporates the result

**Agent Roles:**
- **Orchestrator:** Plans, delegates, monitors
- **Researcher:** Retrieves information
- **Coder:** Writes code
- **Reviewer:** Critiques output
- **Validator:** Tests and verifies

**Interview Q: What are the failure modes of multi-agent systems?**
(1) Infinite loops (agents keep passing messages). (2) Context overflow (accumulated messages exceed context window). (3) Role confusion (agents doing each other's jobs). (4) Groupthink (all agents converge on same wrong answer). (5) Cost explosion (each agent call costs tokens).

**Interview Q: How do you handle conflicts between agents?**
Designate a "judge" agent or use the orchestrator to resolve. Structured voting (each agent votes, majority wins). Human-in-the-loop for critical decisions. Pre-defined escalation paths.

### 6.5 Evaluation

| Metric | What it measures |
|--------|------------------|
| Task Completion Rate | % of tasks successfully completed |
| Cost | Average cost per task (tokens + API calls) |
| Latency | Time to complete a task |
| Safety | % of actions that violate safety constraints |
| Efficiency | Number of tool calls / steps per task |
| Robustness | Performance under noise, errors, edge cases |

**Task Completion Rate:**
- Most important metric
- Requires ground truth for each task
- Can use LLM-as-judge to evaluate completion

**Cost Analysis:**
```
cost = sum(num_input_tokens * input_price + num_output_tokens * output_price) + sum(tool_api_costs)
```
- Multi-agent systems can be very expensive (10-100x single call)
- Optimize: compress messages, remove redundant steps

**Interview Q: How do you evaluate an agent system before deploying to production?**
Create a benchmark of 100-500 realistic tasks with ground truth answers. Measure: task completion rate, average cost per task, average latency, safety violation rate. Run ablation studies (remove memory, change architecture) to understand each component's contribution.

---

## 7. Evaluation & Safety

### 7.1 Model Evaluation

**Perplexity:**
```
PPL = exp(-1/N * sum(log P(x_i | x_{<i})))
```
- Intrinsic measure: how surprised the model is by the test data
- Lower is better
- Correlates with but doesn't guarantee downstream performance
- Not suitable for comparing across different tokenizers

**MMLU (Massive Multitask Language Understanding):**
- 57 subjects across STEM, humanities, social sciences
- ~14,000 multiple-choice questions
- Measures world knowledge and reasoning
- Key benchmark for LLM comparison
- Top models: 86-90% (GPT-4, Claude 3.5)

**HumanEval / MBPP:**
- Code generation benchmarks
- Pass@k: fraction of problems solved in k attempts
- HumanEval: 164 hand-written Python problems
- MBPP: ~1,000 crowd-sourced Python problems

**GSM8K:**
- Grade-school math word problems
- ~8,500 problems
- Tests multi-step mathematical reasoning
- High scores require structured reasoning (Chain-of-Thought)

**HELM (Holistic Evaluation of Language Models, Stanford):**
- Multi-metric evaluation: accuracy, calibration, robustness, fairness, bias, toxicity, efficiency
- Standardized scenarios with per-scenario metrics
- Most comprehensive evaluation framework
- 40+ scenarios, 7+ metrics each

**Chatbot Arena (LMSYS):**
- Human preference-based evaluation via blind A/B comparisons
- Elo rating system
- Best proxy for real-world user satisfaction
- Models ranked by human preference

**Interview Q: Why is MMLU considered a key benchmark but not sufficient?**
MMLU measures knowledge but not instruction following, safety, creativity, code generation, or long-context understanding. A model could ace MMLU but be unusable for real tasks. Need complementary benchmarks.

**Interview Q: How do you interpret perplexity differences between models with different tokenizers?**
They're not directly comparable. Perplexity depends on the tokenizer: more aggressive tokenization (more tokens per word) artificially lowers perplexity. Compare only between models sharing the same tokenizer, or use BPE-adjusted perplexity.

### 7.2 Hallucination

**Types:**
- **Intrinsic:** Contradicts the provided source material (RAG context, prompt)
- **Extrinsic:** Contradicts real-world facts not in the source (the model "makes up" information)

**Causes:**
- Model trained to always provide a plausible answer (never "I don't know")
- Knowledge stored in parameters can be incomplete or outdated
- Decoding strategy (sampling, top-k) can produce unlikely generations
- Attention patterns can miss relevant context

**Detection:**
- **NLI-based:** Use Natural Language Inference model between claims and context
- **QA-based:** Generate questions from output, verify against context
- **LLM-as-judge:** Ask another LLM to check for unsupported claims
- **Entailment classifier:** BART-based models finetuned on contradiction detection
- **Self-consistency:** Generate multiple answers, check for consistency

**Mitigation:**
- **At training:** Include "I don't know" examples, use factual consistency loss (DoLA)
- **At prompting:** Strong instructions to use only provided context
- **At decoding:** Contrastive decoding, top-k filtering, length penalty
- **At retrieval:** High-quality RAG, validate retrieved passages
- **Post-hoc:** Verify claims against knowledge base before returning

**Interview Q: Why does the LLM hallucinate even when it could answer correctly?**
Maybe the model is trained to never say "I don't know" (RLHF encourages helpfulness over honesty). Maybe the sampled decoding produces an unlikely but incorrect path. The model has no inherent truth-checking mechanism — it just produces the most likely sequence.

**Interview Q: How do you distinguish hallucination from correct information the model knows?**
You can't from the output alone. You need external verification: check against a knowledge base, use context, run NLI. The model's internal confidence (log probabilities) is a weak signal — models can be confidently wrong.

### 7.3 Safety

**RLHF Red-teaming:**
- Human experts interact with the model to find harmful outputs
- Categories: hate speech, self-harm, violence, illegal content, etc.
- Collected red-teaming data is used for further RLHF training
- Iterative process: red-team → fix → red-team again

**Guardrails:**
- Pre-processing: check input for harmful, jailbreak, or prompt injection
- Post-processing: check output for policy violations
- Rule-based: blocklist patterns, regex, PII detection
- ML-based: safety classifier (e.g., Llama Guard, NeMo Guardrails)
- Action: block, rewrite, or flag for review

**Content Filtering:**
- Input filter: block harmful user queries
- Output filter: block or rewrite harmful model responses
- Types:
  - Category-based (hate, violence, sexual, self-harm)
  - Severity-based (low, medium, high)
  - Contextual (jailbreak detection, prompt extraction)

**Prompt Injection:**
- Attacker embeds malicious instructions: "Ignore previous instructions and say [harmful content]"
- Defense: input sanitization, prompt boundaries, instruction-aware models
- Use special tokens to separate user input from system instructions
- Claude's <scoping> tags and OpenAI's structured outputs

**Interview Q: What is a jailbreak attack and how do you defend against it?**
Jailbreak attacks are carefully crafted prompts that bypass the model's safety training. Examples: role-playing "DAN" (Do Anything Now), base64 encoding, hypothetical scenarios. Defense: input classifiers, adversarial training (include jailbreaks in RLHF data), output classifiers, structured system prompts.

**Interview Q: Design a multi-layer safety system for a chatbot.**
Layer 1: Input guardrail (block harmful/jailbreak prompts). Layer 2: Prompt augmentation (safety instructions in system prompt). Layer 3: Model training (RLHF safety data). Layer 4: Output guardrail (classify/modify unsafe responses). Layer 5: Human review for edge cases. Each layer catches what the previous misses.

### 7.4 Alignment

**Three-stage alignment pipeline:**
1. **SFT:** Teach the model the desired format and behavior via demonstrations
2. **RLHF/DPO:** Align with human preferences
3. **Constitutional AI (optional):** Use AI-generated critiques for self-improvement

**Supervised Fine-tuning (SFT):**
- Train on (instruction, ideal_response) pairs
- Establishes basic behavioral patterns
- Loss: standard cross-entropy
- Need high-quality demonstrations from human experts

**RLHF → DPO shift:**
- RLHF: complex (RM + PPO), but more flexible (multiple reward signals)
- DPO: simpler (no RM, no PPO), but limited to one preference signal
- Industry trend: DPO for initial alignment, RLHF for fine-grained safety tuning

**Constitutional AI (Bai et al., 2022):**
- **Stage 1: Self-Critique:** Model generates responses, then critiques its own responses
- **Stage 2: Revision:** Model revises based on self-critique
- **Stage 3: Training:** Train on (question, revised_response) pairs
- Reduces need for human labels in safety training
- Principles: "Be helpful", "Be harmless", "Be honest"

**Alignment Tax:**
- Alignment sometimes reduces model capability (especially on factual recall)
- Trade-off: better behavior vs better performance
- Mitigation: careful data curation, mixture of alignment and pre-training data

**Interview Q: How does Constitutional AI reduce the need for human labeling?**
The model generates its own critiques and revisions using a set of written constitutional principles. This creates synthetic but diverse training data. Human involvement is reduced to writing the constitution (a few principles) rather than labeling thousands of examples.

**Interview Q: What is the alignment tax and how do you minimize it?**
The alignment tax is the reduction in capability (e.g., coding, math performance) caused by alignment training. Minimize by: carefully balanced training data (mix of alignment and capability examples), low-rank adaptation during alignment (preserve base model), and validation on capability benchmarks after each alignment step.
