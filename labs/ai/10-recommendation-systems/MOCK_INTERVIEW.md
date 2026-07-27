# Mock Interview: Recommendation Systems

## Question 1: Collaborative Filtering
**Q**: Implement matrix factorization for collaborative filtering.

**A**:
```python
class MatrixFactorization:
    def __init__(self, n_factors=20, lr=0.01, reg=0.02, n_iter=100):
        self.n_factors = n_factors
        self.lr = lr
        self.reg = reg
        self.n_iter = n_iter

    def fit(self, ratings, n_users, n_items):
        # ratings: list of (user, item, rating) tuples
        self.P = np.random.normal(0, 0.1, (n_users, self.n_factors))
        self.Q = np.random.normal(0, 0.1, (n_items, self.n_factors))
        self.bu = np.zeros(n_users)
        self.bi = np.zeros(n_items)
        self.global_mean = np.mean([r for _,_,r in ratings])

        for _ in range(self.n_iter):
            for u, i, r in ratings:
                pred = self._predict(u, i)
                err = r - pred
                self.P[u] += self.lr * (err * self.Q[i] - self.reg * self.P[u])
                self.Q[i] += self.lr * (err * self.P[u] - self.reg * self.Q[i])
                self.bu[u] += self.lr * (err - self.reg * self.bu[u])
                self.bi[i] += self.lr * (err - self.reg * self.bi[i])

    def _predict(self, u, i):
        return self.global_mean + self.bu[u] + self.bi[i] + self.P[u] @ self.Q[i]
```

## Question 2: Cold Start Problem
**Q**: How do you handle the cold start problem for new users and new items?

**A**: New users:
- Request preferences/questions during onboarding
- Use demographic/popularity-based recommendations
- Contextual bandits for exploration
- Content-based features (browser, device, location)

New items:
- Content-based similarity to existing items
- Metadata-based (title, description, category)
- Human-curated initial tags
- Promote with exploration/exploit strategy

## Question 3: Two-Tower Model
**Q**: Design a two-tower neural network for retrieval.

**A**: 
- **Query tower**: User features (history, demographics, context) -> embedding
- **Item tower**: Item features (ID, metadata, content) -> embedding
- Train with dot product similarity and negative sampling

```python
class TwoTowerModel(nn.Module):
    def __init__(self, n_users, n_items, dim=64):
        super().__init__()
        self.user_tower = nn.Sequential(
            nn.Embedding(n_users, 128),
            nn.Linear(128, dim))
        self.item_tower = nn.Sequential(
            nn.Embedding(n_items, 128),
            nn.Linear(128, dim))

    def forward(self, users, items):
        u_emb = self.user_tower(users)
        i_emb = self.item_tower(items)
        return (u_emb * i_emb).sum(dim=-1)
```

## Question 4: Evaluation
**Q**: How do you evaluate a recommendation system offline?

**A**: 
- **Hit Rate**: Is the relevant item in the top-K?
- **MRR (Mean Reciprocal Rank)**: Rank of first relevant item
- **NDCG**: Position-weighted relevance (discounted cumulative gain)
- **Coverage**: % of items recommended
- **Diversity**: Average pairwise dissimilarity of recommendations
- **Serendipity**: Unexpected but relevant recommendations
- **AUC**: Ranking quality (relevant vs non-relevant items)
