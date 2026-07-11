# Mathematical Foundation of Self-Attention

## 📐 Scaled Dot-Product Attention
The mathematical formula for Self-Attention is elegantly simple yet incredibly powerful.

Let:
- $Q$ be the matrix of Queries (shape: sequence_length $\times$ dimension_k)
- $K$ be the matrix of Keys (shape: sequence_length $\times$ dimension_k)
- $V$ be the matrix of Values (shape: sequence_length $\times$ dimension_v)
- $d_k$ be the dimension of the keys/queries.

The attention output is calculated as:
$$ \text{Attention}(Q, K, V) = \text{softmax}\left(\frac{Q K^T}{\sqrt{d_k}}\right) V $$

### Step-by-Step Breakdown:
1. **$Q K^T$ (The Scores)**: We take the dot product of every Query with every Key. This results in a square matrix (sequence_length $\times$ sequence_length) of raw attention scores (logits). High scores mean high similarity.
2. **$\frac{1}{\sqrt{d_k}}$ (The Scaling Factor)**: As the dimension $d_k$ gets larger, the dot products tend to grow very large in magnitude. Large values push the softmax function into regions where gradients are extremely small (vanishing gradients). We scale the scores down by the square root of the dimension to keep the variance stable.
3. **$\text{softmax}(\dots)$ (The Weights)**: The softmax function converts the raw scores into a probability distribution (values between 0 and 1 that sum to 1 across each row). These are the attention weights.
4. **$\times V$ (The Output)**: We multiply the attention weights by the Values matrix. This creates the final output: a weighted sum of the values, where the weights are determined by the query-key similarities.

## 🎭 Masking
In many applications, we need to prevent the attention mechanism from looking at certain tokens.

1. **Padding Mask**: Sentences in a batch have different lengths, so they are padded with `<PAD>` tokens to make them the same length. We don't want the model to pay attention to padding. We apply a mask that sets the attention scores for `<PAD>` tokens to $-\infty$ before the softmax. $\text{softmax}(-\infty) = 0$.
2. **Causal Mask (Look-Ahead Mask)**: In autoregressive language models (like GPT), predicting the next word must only depend on *past* words. If the model can look ahead at the future words, it's cheating. We apply a triangular mask that sets all scores above the diagonal to $-\infty$.