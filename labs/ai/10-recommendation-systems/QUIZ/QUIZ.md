# Recommendation Systems - Quiz

### Question 1
What is collaborative filtering?

A) Using item features to make recommendations
B) Using user-item interactions to find similar users/items
C) Content-based filtering
D) Matrix factorization only

**Answer: B** - Collaborative filtering recommends items based on similar users' preferences or similar items.

---

### Question 2
What is the user-item matrix in recommendation systems?

A) A matrix of item features
B) Matrix with users as rows, items as columns, ratings as values
C) A diagonal matrix
D) A sparse matrix that should be dense

**Answer: B** - The user-item matrix has users on one axis, items on another, and ratings/interactions as cell values.

---

### Question 3
What is the "cold start" problem?

A) The system is too slow
B) New users/items have no historical data, making recommendations difficult
C) The data is too cold to process
D) Matrix factorization fails

**Answer: B** - Cold start occurs when new users or items lack enough interactions for effective recommendations.

---

### Question 4
What does matrix factorization do?

A) Decomposes user-item matrix into lower-dimensional user and item matrices
B) Increases the matrix size
C) Adds more ratings
D) Filters content

**Answer: A** - Matrix factorization decomposes the rating matrix into latent factor matrices representing users and items.

---

### Question 5
What is the difference between user-based and item-based collaborative filtering?

A: User-based uses similar users, item-based uses similar items
B: No difference
C: Item-based is always better
D: User-based is unsupervised

**Answer: A** - User-based CF finds similar users and recommends what they liked. Item-based CF finds similar items to what user already liked.

---

### Question 6
What is the purpose of regularization in matrix factorization?

A) To increase prediction accuracy
B) To prevent overfitting by penalizing large parameters
C) To speed up computation
D) To handle missing values

**Answer: B** - Regularization prevents overfitting by adding a penalty for large parameter values in the loss function.

---

### Question 7
What is a latent factor in matrix factorization?

A) An observed feature
B) An unobserved hidden feature representing user/item characteristics
C) The bias term
D) The rating

**Answer: B** - Latent factors are hidden features (like "genre preference" or "style") learned from the data.

---

### Question 8
How do you handle missing values in the user-item matrix?

A) Ignore them completely
B) Fill with zeros or mean values during training
C) Remove users/items with missing values
D) Use only complete matrices

**Answer: B** - During training, missing values are typically ignored or filled. Matrix factorization naturally handles missing values by only learning from observed ratings.

---

### Question 9
What is cosine similarity used for in recommendations?

A) Computing matrix factors
B) Measuring similarity between user or item vectors
C) Calculating the loss function
D) Normalizing ratings

**Answer: B** - Cosine similarity measures the angle between vectors to determine similarity for CF.

---

### Question 10
What is the evaluation metric for recommendations?

A) Only accuracy
B) Precision, Recall, F1, MAP, NDCG, RMSE
C) Only RMSE
D) Only precision

**Answer: B** - Recommendations use multiple metrics: RMSE for rating prediction, precision/recall/MAP/NDCG for ranking.

---

### Question 11 (Bonus)
What is hybrid recommendation?

A) Combining collaborative filtering and content-based methods
B) Using multiple CF methods
C) Using matrix factorization only
D) Using only one user

**Answer: A** - Hybrid systems combine collaborative filtering (user behavior) with content-based methods (item features).

---

### Question 12 (Bonus)
What is the bias term in matrix factorization?

A) Always zero
B) Captures global average rating and user/item biases
C) The regularization parameter
D) The number of factors

**Answer: B** - Bias term accounts for user and item average deviations from global mean, improving predictions.