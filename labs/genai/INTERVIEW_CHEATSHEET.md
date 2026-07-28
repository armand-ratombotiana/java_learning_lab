# GenAI Interview Cheatsheet

## Transformer at a Glance
```
Attention(Q,K,V) = softmax(QK^T / sqrt(d_k)) V
MultiHead(Q,K,V) = Concat(head_1,...,head_h) W_O
FFN(x) = max(0, xW_1 + b_1)W_2 + b_2
LayerNorm(x) = (x - μ) / σ ⊙ γ + β
```

## Common Pitfalls
- Forgetting the `√d_k` scaling factor in attention.
- Confusing encoder-decoder (T5) vs decoder-only (GPT).
- Mixing training vs inference: KV cache, teacher forcing.

## Key Numbers
| Concept | Value |
|---------|-------|
| d_model (GPT-3) | 12288 |
| d_k (GPT-3) | 128 |
| Vocabulary size (GPT-3) | 50257 |
| Context window (GPT-3) | 2048 |
| Context window (GPT-4) | 8192 / 32768 / 128K |
| Typical LoRA rank | 8–64 |
| INT8 speedup vs FP16 | ~2x |

## RAG Flow
```
Query → Embed → Retrieve (ANN) → Augment (prompt) → Generate → Verify
```

## Agent Loop
```
Observe → Think → Act → Observe (ReAct loop)
```

## Evaluation Metrics
| Metric | Use Case |
|--------|----------|
| Perplexity | Language modeling |
| BLEU | Translation |
| ROUGE | Summarization |
| F1 (QA) | Question answering |
| Toxicity score | Safety |
| BERTScore | Semantic similarity |
