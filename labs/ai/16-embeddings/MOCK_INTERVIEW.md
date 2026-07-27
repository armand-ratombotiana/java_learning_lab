# Mock Interview: Embeddings

## Question 1: Word2Vec (Skip-gram)
**Q**: Implement skip-gram Word2Vec with negative sampling.

**A**:
```python
class SkipGram:
    def __init__(self, vocab_size, embed_dim=100, lr=0.01):
        self.W_in = np.random.randn(vocab_size, embed_dim) * 0.01
        self.W_out = np.random.randn(embed_dim, vocab_size) * 0.01
        self.lr = lr

    def forward(self, target, context, negative_samples):
        h = self.W_in[target]                   # (embed_dim,)
        # Positive sample
        pos_score = h @ self.W_out[:, context]   # scalar
        pos_loss = -np.log(sigmoid(pos_score))

        # Negative samples
        neg_scores = h @ self.W_out[:, negative_samples]
        neg_loss = -np.sum(np.log(sigmoid(-neg_scores)))

        # Gradients
        d_pos = sigmoid(pos_score) - 1
        d_neg = sigmoid(neg_scores)
        dh = d_pos * self.W_out[:, context] + np.sum(d_neg * self.W_out[:, negative_samples], axis=1)

        self.W_in[target] -= self.lr * dh
        self.W_out[:, context] -= self.lr * d_pos * h
        self.W_out[:, negative_samples] -= self.lr * d_neg * h[:, None]

        return pos_loss + neg_loss
```

## Question 2: Embedding Properties
**Q**: What properties do word embeddings capture? Explain analogies.

**A**: Word embeddings capture semantic and syntactic relationships through vector arithmetic.

Examples:
- king - man + woman = queen
- Paris - France + Italy = Rome
- walking - walk + run = running

This emerges because embeddings encode relational structure in their vector space.

## Question 3: Contextual vs Static Embeddings
**Q**: Compare Word2Vec/GloVe (static) vs BERT (contextual) embeddings.

**A**:
| Aspect | Static (Word2Vec, GloVe) | Contextual (BERT, GPT) |
|--------|--------------------------|------------------------|
| Context | One embedding per word | Different per context |
| Polysemy | "bank" = same vector | "river bank" ≠ "money bank" |
| Out-of-vocab | Unknown tokens fail | Subword tokenization |
| Training | Unsupervised on co-occurrence | Masked LM / autoregressive |
| Size | Small (100-300d) | Large (768-4096d) |
| Use case | Baseline, efficiency | SOTA accuracy |

**Follow-up**: How would you use BERT embeddings in production?
Cache BERT output for frequent phrases. Use Sentence-BERT for efficient sentence embeddings.

## Question 4: Embedding for Non-text Data
**Q**: How do you create embeddings for categorical features, users, or graphs?

**A**: 
- **Categorical**: Embedding look-up table (nn.Embedding)
- **Users**: Collaborative filtering (matrix factorization, two-tower model)
- **Graphs**: Node2Vec, GraphSAGE, GNN encoders
- **Images**: CNN/CNN feature vectors
- **Multi-modal**: CLIP-style contrastive learning

## Question 5: Evaluation of Embeddings
**Q**: How do you evaluate embedding quality?

**A**: 
- **Intrinsic**: Word similarity/analogy benchmarks (WordSim353, Google Analogy)
- **Extrinsic**: Downstream task performance (classification, NER, retrieval)
- **Clustering**: Silhouette score on embeddings
- **Visualization**: t-SNE or UMAP (qualitative check)
- **Nearest neighbors**: Are neighbors semantically similar?
- **Bias detection**: WEAT (Word Embedding Association Test) for societal bias
