# Recommendation Systems - Flashcards

### Card 1: Collaborative Filtering
**Q:** What is collaborative filtering?
**A:** Recommends items based on similar users' preferences (user-based) or similar items (item-based).

### Card 2: User-Item Matrix
**Q:** What is the user-item matrix?
**A:** Matrix with users as rows, items as columns, and ratings/interactions as values.

### Card 3: Matrix Factorization
**Q:** What does matrix factorization do?
**A:** Decomposes the user-item matrix R ≈ U × V^T where U and V are latent factor matrices.

### Card 4: Cold Start Problem
**Q:** What is the cold start problem?
**A:** New users/items with no interaction history cannot get personalized recommendations.

### Card 5: Latent Factors
**Q:** What are latent factors?
**A:** Hidden features representing user preferences and item characteristics learned from data.

### Card 6: RMSE in Recommendations
**Q:** What does RMSE measure in recommendations?
**A:** Root Mean Square Error - measures accuracy of rating predictions.

### Card 7: Precision@K and Recall@K
**Q:** What do precision@K and recall@K measure?
**A:** Among top K recommendations, what fraction are relevant (precision) and what fraction of relevant items are retrieved (recall).

### Card 8: SVD vs SVD++
**Q:** What is SVD++ over SVD?
**A:** SVD++ adds implicit feedback - considers which items users have rated, not just the ratings.

### Card 9: Content-Based Filtering
**Q:** How does content-based filtering work?
**A:** Uses item features to recommend similar items to what user liked, doesn't need other users' data.

### Card 10: Cosine Similarity
**Q:** How is cosine similarity used in CF?
**A:** Measures similarity between user or item vectors to find nearest neighbors.

### Card 11: Bias in Matrix Factorization
**Q:** What does the bias capture in SVD?
**A:** Global average, user bias (user's avg vs global), item bias (item's avg vs global).

### Card 12: MAP and NDCG
**Q:** What do MAP and NDCG measure?
**A:** Mean Average Precision - avg precision at different recall levels. NDCG - normalized discounted cumulative gain, considers ranking quality.