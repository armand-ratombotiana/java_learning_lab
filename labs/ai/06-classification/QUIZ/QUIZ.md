# Classification - Quiz

## Assessment Questions

### Question 1
What is the output of the sigmoid function?

A) A probability between 0 and 1
B) A value between -1 and 1
C) Any real number
D) An integer

**Answer: A** - The sigmoid function σ(z) = 1/(1+e^(-z)) outputs values in the range (0, 1), making it suitable for probabilities.

---

### Question 2
In logistic regression, what loss function is used?

A) Mean Squared Error
B) Cross-entropy loss
C) Hinge loss
D) Absolute loss

**Answer: B** - Cross-entropy loss (negative log-likelihood) is used for logistic regression, which encourages the model to output high probabilities for correct classes.

---

### Question 3
What is the main advantage of SVM over logistic regression?

A) Faster training
B) Works well with high-dimensional data and can use kernels
C) Always produces probabilistic outputs
D) Requires less data

**Answer: B** - SVM can use kernel functions to handle non-linear decision boundaries and works well in high-dimensional spaces.

---

### Question 4
What are "support vectors" in SVM?

A) The predicted class labels
B) The data points closest to the decision boundary
C) The features with highest importance
D) The bias term

**Answer: B** - Support vectors are the data points that lie on or within the margin, and they are the only points that determine the position of the decision boundary.

---

### Question 5
Which splitting criterion is used in decision trees for classification?

A) Variance
B) Gini impurity or Entropy
C) Mean absolute error
D) Mean squared error

**Answer: B** - Decision trees for classification use Gini impurity or entropy (which leads to information gain) to determine the best splits.

---

### Question 6
What is the purpose of the kernel trick in SVM?

A) To speed up training
B) To implicitly compute in higher-dimensional space without explicit transformation
C) To reduce the number of features
D) To handle missing data

**Answer: B** - The kernel trick allows SVM to compute the decision boundary in a high-dimensional (or infinite-dimensional) feature space without explicitly computing the transformation.

---

### Question 7
In Random Forest, what is bagging?

A) A type of decision tree
B) Bootstrap aggregating - training multiple trees on different bootstrap samples
C) A method for feature selection
D) A regularization technique

**Answer: B** - Bagging (Bootstrap Aggregating) creates multiple models on different bootstrap samples and combines their predictions to reduce variance.

---

### Question 8
What does precision measure?

A) Of all positive predictions, how many are correct
B) Of all actual positives, how many were predicted positive
C) The overall accuracy
D) The false positive rate

**Answer: A** - Precision = TP / (TP + FP) - it measures the accuracy of positive predictions.

---

### Question 9
What is the F1 score?

A) The average of precision and recall
B) The harmonic mean of precision and recall
C) The product of precision and recall
D) The sum of precision and recall

**Answer: B** - F1 = 2 * (Precision * Recall) / (Precision + Recall), which is the harmonic mean balancing both metrics.

---

### Question 10
What happens when you increase the C parameter in a soft-margin SVM?

A) More regularization, wider margin
B) Less regularization, stricter margin
C) Changes the kernel type
D) No effect

**Answer: B** - Higher C means less regularization, allowing fewer misclassifications and a tighter margin. Lower C allows more violations for a wider margin.

---

### Question 11 (Bonus)
What is the difference between hard margin and soft margin SVM?

A) Hard margin uses kernel, soft margin doesn't
B) Hard margin requires perfect separation, soft margin allows some errors
C) Hard margin is faster
D) There is no difference

**Answer: B** - Hard margin SVM requires data to be perfectly linearly separable, while soft margin allows some misclassifications to handle non-separable data.

---

### Question 12 (Bonus)
What is overfitting in the context of decision trees?

A) Using too few features
B) Creating a tree that is too deep, fitting training data too closely
C) Using too much data for training
D) Not using enough trees in the ensemble

**Answer: B** - Overfitting in decision trees occurs when the tree is made too deep, capturing noise in the training data rather than general patterns.