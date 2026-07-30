# Lab 06 Interview: Naive Bayes Classifier

## Q1: Why is it called "naive"?
Because it assumes conditional independence of features — a naive assumption rarely true in practice.

## Q2: When is Naive Bayes preferred despite the naive assumption?
It works well with high-dimensional data (text classification, spam filtering) and small datasets.

## Q3: What is the difference between Gaussian, Multinomial, and Bernoulli NB?
- Gaussian: continuous features, normal distribution
- Multinomial: discrete counts (e.g., word frequencies)
- Bernoulli: binary features (word presence/absence)

## Q4: Why add Laplace smoothing?
To handle zero probabilities for unseen feature values in the training data.

## Q5: Is Naive Bayes linear?
Yes — the decision boundary is linear in the log space.
