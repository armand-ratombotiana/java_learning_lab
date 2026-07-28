# Lab 01: Interview Questions

## Q1: Explain scaled dot-product attention. Why the sqrt(d_k) scaling?
**A:** Without scaling, for large d_k the dot products grow large, pushing softmax into regions with extremely small gradients. Scaling by `1/sqrt(d_k)` keeps variance ~1.

## Q2: Why does the Transformer use multi-head attention instead of one large head?
**A:** Multiple heads allow the model to attend to different representation subspaces (e.g., syntax, semantics, position) in parallel.

## Q3: What is the purpose of positional encoding?
**A:** Self-attention is permutation-invariant; positional encoding injects sequence order information.

## Q4: Compare encoder-decoder vs decoder-only architectures.
**A:** Encoder-decoder (T5) uses bidirectional context in encoder + causal in decoder. Decoder-only (GPT) uses causal masking throughout, suitable for autoregressive generation.

## Q5: Why are residual connections and layer normalization important in Transformers?
**A:** Residuals mitigate vanishing gradients in deep stacks; layer norm stabilizes training by normalizing activations.
