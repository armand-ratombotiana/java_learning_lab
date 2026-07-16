# Multi-Head Attention Theory & Intuition

## 💡 The Problem: Single-Head Bottleneck
In a single-head attention mechanism, the model averages all the context into one representation. 
Imagine the sentence: "The bank of the river was flooded."
- To understand "bank", the model might need to attend to "river" (semantic meaning).
- But it might *also* need to attend to "flooded" (syntactic/causal relationship).

With only one head, the model is forced to pick one or average them together, potentially losing the nuance of both.

## 🚀 The Solution: Representation Subspaces
**Multi-Head Attention** allows the model to jointly attend to information from different representation subspaces at different positions.

Instead of performing a single attention function with $d_{model}$-dimensional keys, values, and queries, we linearly project the queries, keys, and values $h$ times with different, learned linear projections to $d_k, d_k,$ and $d_v$ dimensions.

### The Analogy: A Committee of Experts
Imagine a committee reviewing a document:
- **Expert 1 (Head 1)**: Focuses on grammar and syntax.
- **Expert 2 (Head 2)**: Focuses on the logical flow of arguments.
- **Expert 3 (Head 3)**: Focuses on the emotional tone.

Each expert (Head) looks at the same input but "sees" different things. By combining their reports (Concatenation), the committee gets a much more comprehensive understanding of the document than any single expert could provide.

## 🏗️ The Multi-Head Workflow
1. **Linear Projection**: Transform the input embeddings into $h$ sets of Q, K, and V matrices using learned weights.
2. **Parallel Attention**: Calculate Scaled Dot-Product Attention for each head independently and in parallel.
3. **Concatenation**: Join the outputs of all heads back into a single matrix.
4. **Final Linear Transformation**: Project the concatenated result back to the original $d_{model}$ dimension.