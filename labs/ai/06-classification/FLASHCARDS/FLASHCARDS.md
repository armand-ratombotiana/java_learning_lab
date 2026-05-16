# Classification - Flashcards

## Quick Review Cards

### Card 1: Sigmoid Function
**Q:** What is the formula for sigmoid activation?
**A:** σ(z) = 1 / (1 + e^(-z)) - Maps any real number to (0, 1).

---

### Card 2: Logistic Regression Output
**Q:** How do you get a probability prediction from logistic regression?
**A:** P(y=1|x) = σ(w^T x + b) where σ is the sigmoid function.

---

### Card 3: Cross-Entropy Loss
**Q:** What is the cross-entropy loss for binary classification?
**A:** L = -[y log(ŷ) + (1-y) log(1-ŷ)]

---

### Card 4: SVM Decision Boundary
**Q:** What is the decision boundary in a linear SVM?
**A:** w^T x + b = 0 - a hyperplane that maximizes the margin between classes.

---

### Card 5: Support Vectors
**Q:** What are support vectors in SVM?
**A:** The data points on or within the margin; they alone define the decision boundary.

---

### Card 6: Kernel Trick
**Q:** What does the kernel trick accomplish?
**A:** It computes dot products in high-dimensional space implicitly, enabling non-linear boundaries without explicit feature transformation.

---

### Card 7: Gini Impurity
**Q:** What is Gini impurity formula for a node?
**A:** G = 1 - Σ(p_i)^2 where p_i is the proportion of class i in the node.

---

### Card 8: Information Gain
**Q:** How is information gain calculated in decision trees?
**A:** IG = Entropy(parent) - Weighted_Avg_Entropy(children)

---

### Card 9: Random Forest Randomness
**Q:** What two sources of randomness does Random Forest use?
**A:** 1) Bootstrap sampling of data rows, 2) Random feature subset at each split.

---

### Card 10: Precision vs Recall
**Q:** What does high precision but low recall indicate?
**A:** The model is conservative - it only predicts positive when very confident, missing many actual positives.

---

### Card 11: F1 Score
**Q:** Why is F1 score better than accuracy for imbalanced data?
**A:** F1 considers both precision and recall, ignoring true negatives - more informative when classes are unbalanced.

---

### Card 12: Soft Margin SVM C Parameter
**Q:** What does a small C value mean in soft margin SVM?
**A:** More regularization, wider margin, allows more misclassifications - better generalization.