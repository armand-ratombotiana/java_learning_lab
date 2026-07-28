# Lab 02: Interview Questions

## Q1: Why does GPT use a decoder-only architecture?
**A:** Decoder-only simplifies the model (single stack), scales well, and is naturally suited for autoregressive generation. Bidirectional context is not needed for left-to-right language modeling.

## Q2: What is the KV cache and how does it work?
**A:** During autoregressive generation, each step recomputes K and V for all previous tokens. The KV cache stores these, reducing computation from O(n^2) to O(n) per step.

## Q3: How does BPE tokenization work?
**A:** Start with individual characters, iteratively merge the most frequent adjacent pair, add as a new token. Repeat until vocabulary reaches target size.

## Q4: What is the difference between greedy decoding, beam search, and sampling?
**A:** Greedy picks argmax each step. Beam search keeps top-k sequences. Sampling draws from the probability distribution (with temperature).

## Q5: Explain the purpose of the causal mask.
**A:** Prevents each token from attending to future tokens, ensuring the prediction for position i depends only on positions 0..i-1.
