# Mock Interview: Implement Perceptron from Scratch and Prove Convergence

## Scenario
You are interviewing for a ML research role. They want to test your understanding of the foundations of neural networks.

## Interviewer Opening Question
"Implement the perceptron algorithm from scratch and prove the perceptron convergence theorem."

## Candidate Response
"The perceptron is a binary linear classifier: f(x) = sign(w * x + b). I'll implement the iterative update rule: for each misclassified point, update w = w + y*x, b = b + y. The perceptron convergence theorem states that if the data is linearly separable with margin gamma, the algorithm converges in at most R^2 / gamma^2 steps."

## Interviewer Probing Questions

**Q: What are the assumptions of the convergence theorem?**
"Two assumptions: (1) The data is linearly separable — there exists a unit-norm w* such that y_i * (w* * x_i) > 0 for all i. (2) All inputs are bounded: ||x_i|| <= R for all i."

**Q: What happens if the data is not linearly separable?**
"The perceptron will not converge — it will oscillate. The Pocket algorithm keeps the best weight vector seen so far, guaranteeing the best solution on the training set even for non-separable data."

**Q: How does the perceptron differ from logistic regression?**
"Perceptron uses a hard threshold (sign) with 0/1 loss. Logistic regression uses a sigmoid with cross-entropy loss. Perceptron updates only on misclassified points; logistic regression uses gradient descent on all points."

## Candidate Solution (Python)

```python
import numpy as np

class Perceptron:
    def __init__(self, learning_rate=1.0, max_epochs=1000):
        self.lr = learning_rate
        self.max_epochs = max_epochs
        self.w = None
        self.b = None
        self.converged_at = None

    def fit(self, X, y):
        """
        X: (n_samples, n_features)
        y: (n_samples,) — labels in {-1, +1}
        """
        n_samples, n_features = X.shape
        self.w = np.zeros(n_features)
        self.b = 0.0
        self.converged_at = None

        for epoch in range(self.max_epochs):
            misclassified = 0
            for i in range(n_samples):
                # Perceptron decision: sign(w * x + b)
                decision = y[i] * (np.dot(self.w, X[i]) + self.b)
                if decision <= 0:
                    # Misclassified: update weights
                    self.w += self.lr * y[i] * X[i]
                    self.b += self.lr * y[i]
                    misclassified += 1

            if misclassified == 0:
                self.converged_at = epoch + 1
                return self  # Converged

        return self  # Did not converge

    def predict(self, X):
        return np.sign(np.dot(X, self.w) + self.b)

    def score(self, X, y):
        return np.mean(self.predict(X) == y)

def perceptron_convergence_theorem(X, y):
    """
    Prove convergence bound: steps <= R^2 / gamma^2
    R = max ||x_i|| (radius of data)
    gamma = min |w* * x_i| (margin)
    """
    # Find a separating w* via SVM (for demonstration)
    from sklearn.svm import LinearSVC
    svm = LinearSVC(C=1e6, max_iter=10000, dual=True)
    svm.fit(X, y)
    w_star = svm.coef_[0]
    w_star = w_star / np.linalg.norm(w_star)

    # Compute margin
    margins = y * (np.dot(X, w_star) + svm.intercept_[0])
    gamma = margins.min()

    # Compute radius
    R = np.max(np.linalg.norm(X, axis=1))

    bound = (R ** 2) / (gamma ** 2)
    print(f"R = {R:.4f}, gamma = {gamma:.4f}")
    print(f"Convergence bound: <= {bound:.0f} steps")
    return bound

# Empirical demonstration
def demonstrate_convergence():
    np.random.seed(42)
    # Generate linearly separable data
    X1 = np.random.randn(100, 2) + np.array([2, 2])
    X2 = np.random.randn(100, 2) + np.array([-2, -2])
    X = np.vstack([X1, X2])
    y = np.hstack([np.ones(100), -np.ones(100)])

    p = Perceptron()
    p.fit(X, y)

    print(f"Converged: {p.converged_at is not None}")
    print(f"Epochs to converge: {p.converged_at}")
    print(f"Accuracy: {p.score(X, y):.2f}")
    print(f"Weights: {p.w}, Bias: {p.b}")

    perceptron_convergence_theorem(X, y)

class PocketPerceptron(Perceptron):
    """Pocket algorithm: keeps best weights when data is not separable."""
    def fit(self, X, y):
        n_samples, n_features = X.shape
        self.w = np.zeros(n_features)
        self.b = 0.0
        best_w = self.w.copy()
        best_b = self.b
        best_acc = 0.0

        for epoch in range(self.max_epochs):
            for i in range(n_samples):
                if y[i] * (np.dot(self.w, X[i]) + self.b) <= 0:
                    self.w += self.lr * y[i] * X[i]
                    self.b += self.lr * y[i]

            acc = self.score(X, y)
            if acc > best_acc:
                best_w = self.w.copy()
                best_b = self.b
                best_acc = acc

            if best_acc == 1.0:
                self.converged_at = epoch + 1
                break

        self.w = best_w
        self.b = best_b
        return self
```

## Interviewer Feedback
"Clean implementation and you correctly stated the convergence theorem with its assumptions. The Pocket extension shows practical awareness. The margin and radius analysis is exactly right."

## Key Takeaways
- Perceptron converges in O(R^2/gamma^2) steps for linearly separable data
- Update rule: w += y*x, b += y for each misclassified point
- The convergence depends on the margin and radius of the data
- Pocket algorithm handles non-separable cases
- Perceptron is the foundation for all modern neural networks
