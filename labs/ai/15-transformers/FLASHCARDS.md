# Transformers - FLASHCARDS

### Card 1
**Q:** What is self-attention?
**A:** Mechanism where every position attends to all positions in the sequence, computing relationship between tokens

### Card 2
**Q:** Why scale attention by sqrt(d_k)?
**A:** Prevents gradient explosion when d_k is large; keeps softmax in regime where gradients are not too small

### Card 3
**Q:** What is the purpose of positional encoding?
**A:** Inject order information since attention is order-invariant; uses sin/cos functions at different frequencies

### Card 4
**Q:** Difference between encoder and decoder attention?
**A:** Encoder: bidirectional (full attention). Decoder: causal (masked) for prefix, cross-attention to encoder output

### Card 5
**Q:** What does multi-head attention do?
**A:** Runs attention in parallel with different learned projections, capturing different types of relationships

### Card 6
**Q:** Why use residual connections in transformer?
**A:** Enables gradient flow in deep networks, helps with training stability

### Card 7
**Q:** What is layer normalization doing?
**A:** Normalizes activations to zero mean and unit variance per feature, helps training convergence

### Card 8
**Q:** How does GPT differ from BERT?
**A:** GPT: auto-regressive (predicts next token), decoder-only. BERT: masked language model, encoder-only, bidirectional

### Card 9
**Q:** What is the feed-forward network in transformer?
**A:** Two linear layers with ReLU: projects from d_model to d_ff and back; applies per position independently

### Card 10
**Q:** Why transformer more parallelizable than RNN?
**A:** No sequential dependency - all positions computed simultaneously, unlike RNN which must compute sequentially

### Card 11
**Q:** What is cross-attention in decoder?
**A:** Decoder attends to encoder output - queries from decoder, keys/values from encoder

### Card 12
**Q:** Complexity of self-attention?
**A:** O(n² × d) where n=sequence length, d=model dimension; quadratic in sequence length

**Total: 12 flashcards**