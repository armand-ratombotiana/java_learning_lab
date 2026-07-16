# Mathematical Foundation of Entropy

## 📐 Shannon Entropy
For a discrete random variable $X$ with possible outcomes $x_1, \dots, x_n$, which occur with probability $P(x_i)$, the entropy $H(X)$ is:

$$ H(X) = -\sum_{i=1}^{n} P(x_i) \log_2 P(x_i) $$

- **Intuition**: If an event is certain ($P=1$), its entropy is 0 (no information gained). If an event is highly unlikely, its occurrence provides a lot of information (high surprise).

## 📉 Cross-Entropy
Used as a loss function in classification. It measures the difference between two probability distributions: the true labels ($P$) and the model's predictions ($Q$).

$$ H(P, Q) = -\sum_{i} P(x_i) \log Q(x_i) $$

In binary classification (Logistic Regression), this simplifies to the **Log Loss**:
$$ L = -(y \log(\hat{y}) + (1-y) \log(1-\hat{y})) $$