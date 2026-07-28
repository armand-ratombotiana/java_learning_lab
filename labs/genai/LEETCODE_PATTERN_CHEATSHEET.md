# LeetCode Pattern Cheatsheet for GenAI Engineers

## ML-related Coding Patterns
| Pattern | Description | Example |
|---------|-------------|---------|
| Matrix / Grid | Attention score computation, convolution | Scaled dot-product attention |
| Two Pointers | Sliding window context | Sliding window attention |
| Prefix Sum | Cumulative distributions, softmax | Prefix softmax |
| Heap / Priority Queue | Top-k sampling, beam search | Top-k token selection |
| HashMap / Counter | Token counting, n-gram stats | Vocabulary frequencies |
| Backtracking | Beam search, constrained decoding | Sequence generation |
| Dynamic Programming | Viterbi, alignment | CTC decoding |

## Recommended Practice
- Implement `softmax`, `layer_norm`, `attention` from scratch.
- Write a tokenizer (BPE) and a trie for vocabulary matching.
- Implement beam search with configurable beam width.
- Simulate a RAG retrieval step with cosine similarity.
