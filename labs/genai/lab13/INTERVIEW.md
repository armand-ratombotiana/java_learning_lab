# Lab 13: Interview Questions

## Q1: What problem does RoPE solve compared to absolute positional encoding?
**A:** RoPE encodes relative position through rotation, allowing the model to generalize to longer sequences than seen during training. It provides a natural decay for distant token pairs and works with linear attention.

## Q2: How does sliding window attention reduce memory complexity?
**A:** Standard attention is O(n^2). Sliding window with window W is O(n*W), where W << n. This makes it feasible to process very long sequences (e.g., 1M tokens).

## Q3: What is the "context extension" problem and how is it addressed?
**A:** Models trained on short contexts perform poorly on longer ones. Solutions: RoPE with interpolation (PI, NTK-aware), ALiBi, sliding window, and continued pretraining on long sequences.

## Q4: Explain ALiBi and its advantages.
**A:** ALiBi adds a linear bias to attention scores proportional to token distance. It eliminates the need for positional embeddings entirely and naturally extrapolates to longer sequences.

## Q5: How does context compression work in practice?
**A:** A separate model (or the same LLM) summarizes long context into condensed form. The compressed representation is prepended to subsequent prompts. Trade-off: lossy compression vs reduced KV cache.
