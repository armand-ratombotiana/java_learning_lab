# Transformers - Theory

## 1. Attention Mechanism

### Scaled Dot-Product Attention
```
Attention(Q, K, V) = softmax(QK^T / √d_k) V
```

### Multi-Head Attention
```
MultiHead(Q, K, V) = Concat(head_1, ..., head_h) W^O
head_i = Attention(QW_i^Q, KW_i^K, VW_i^V)
```

## 2. Architecture Components

### Positional Encoding
```
PE(pos, 2i) = sin(pos/10000^(2i/d))
PE(pos, 2i+1) = cos(pos/10000^(2i/d))
```

### Feed-Forward Network
```
FFN(x) = max(0, xW₁ + b₁)W₂ + b₂
```

### Layer Norm
- Normalize across features
- Per-example normalization

### Residual Connection
```
output = LayerNorm(x + Sublayer(x))
```

## 3. Encoder-Decoder

### Encoder
- Self-attention layers
- Processes input sequence

### Decoder
- Self-attention + Encoder-Decoder attention
- Autoregressive generation

## 4. Pre-training

### Masked Language Modeling (BERT)
- Mask 15% tokens
- Predict masked tokens

### Next Sentence Prediction
- Predict if B follows A

### Causal Language Modeling (GPT)
- Predict next token
- Autoregressive

## 5. BERT vs GPT

### BERT
- Bidirectional
- MLM + NSP
- Classification, QA

### GPT
- Unidirectional
- CLM
- Text generation

## 6. Modern Transformers

### GPT-4, LLaMA, Claude
- Massive scale
- Instruction tuning
- RLHF

### Efficiency
- Flash Attention
- Mixture of Experts
- Quantization