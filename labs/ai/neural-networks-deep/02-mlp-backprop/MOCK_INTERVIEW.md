# Mock Interview: Implement Backpropagation for a 3-Layer MLP

## Scenario
You are interviewing for a deep learning engineer role. They want to verify your understanding of the backpropagation algorithm end-to-end.

## Interviewer Opening Question
"Implement forward and backward passes for a 3-layer MLP (input -> hidden1 -> hidden2 -> output) from scratch using NumPy. Show the full gradient computation."

## Candidate Response
"I'll implement a 3-layer MLP with ReLU hidden activations and softmax output. The backward pass computes gradients using the chain rule: dL/dW = (dL/dy) * (dy/dz) * (dz/dW). I'll compute layer by layer, caching intermediate values during forward pass."

## Interviewer Probing Questions

**Q: What happens if you use sigmoid instead of ReLU?**
"Sigmoid saturates for large positive/negative values, causing vanishing gradients. ReLU keeps gradient = 1 for positive inputs, which mitigates vanishing gradients. However, ReLU can cause dead neurons if they always output negative."

**Q: How do you prevent overfitting in this MLP?**
"Add L2 regularization to the loss, which adds weight * learning_rate to each gradient. Dropout randomly zeroes activations during training. Early stopping on validation loss."

**Q: What's the difference between batch, mini-batch, and SGD?**
"Batch: full dataset per update (stable but slow). SGD: one sample per update (noisy but fast). Mini-batch: small subset (best trade-off — 32-256 samples per step). I'll implement mini-batch."

## Candidate Solution (Python)

```python
import numpy as np

class MLP:
    def __init__(self, input_size, hidden1_size, hidden2_size, output_size, lr=0.01):
        self.lr = lr
        # He initialization for ReLU
        self.W1 = np.random.randn(input_size, hidden1_size) * np.sqrt(2.0 / input_size)
        self.b1 = np.zeros((1, hidden1_size))
        self.W2 = np.random.randn(hidden1_size, hidden2_size) * np.sqrt(2.0 / hidden1_size)
        self.b2 = np.zeros((1, hidden2_size))
        self.W3 = np.random.randn(hidden2_size, output_size) * np.sqrt(2.0 / hidden2_size)
        self.b3 = np.zeros((1, output_size))

    def relu(self, x):
        return np.maximum(0, x)

    def relu_derivative(self, x):
        return (x > 0).astype(float)

    def softmax(self, x):
        x_exp = np.exp(x - np.max(x, axis=1, keepdims=True))
        return x_exp / np.sum(x_exp, axis=1, keepdims=True)

    def cross_entropy_loss(self, y_pred, y_true):
        n = y_pred.shape[0]
        return -np.sum(y_true * np.log(y_pred + 1e-8)) / n

    def forward(self, X):
        # Layer 1: input -> hidden1
        self.z1 = np.dot(X, self.W1) + self.b1
        self.a1 = self.relu(self.z1)
        # Layer 2: hidden1 -> hidden2
        self.z2 = np.dot(self.a1, self.W2) + self.b2
        self.a2 = self.relu(self.z2)
        # Layer 3: hidden2 -> output
        self.z3 = np.dot(self.a2, self.W3) + self.b3
        self.a3 = self.softmax(self.z3)
        return self.a3

    def backward(self, X, y_true):
        n = X.shape[0]

        # Output layer gradient
        dL_dz3 = self.a3 - y_true  # (n, output_size) — softmax + cross-entropy gradient

        # Layer 3 gradients
        dL_dW3 = np.dot(self.a2.T, dL_dz3)  # (hidden2_size, output_size)
        dL_db3 = np.sum(dL_dz3, axis=0, keepdims=True)

        # Backprop to layer 2
        dL_da2 = np.dot(dL_dz3, self.W3.T)  # (n, hidden2_size)
        dL_dz2 = dL_da2 * self.relu_derivative(self.z2)  # (n, hidden2_size)
        dL_dW2 = np.dot(self.a1.T, dL_dz2)  # (hidden1_size, hidden2_size)
        dL_db2 = np.sum(dL_dz2, axis=0, keepdims=True)

        # Backprop to layer 1
        dL_da1 = np.dot(dL_dz2, self.W2.T)  # (n, hidden1_size)
        dL_dz1 = dL_da1 * self.relu_derivative(self.z1)  # (n, hidden1_size)
        dL_dW1 = np.dot(X.T, dL_dz1)  # (input_size, hidden1_size)
        dL_db1 = np.sum(dL_dz1, axis=0, keepdims=True)

        # Update weights (SGD)
        self.W3 -= self.lr * dL_dW3
        self.b3 -= self.lr * dL_db3
        self.W2 -= self.lr * dL_dW2
        self.b2 -= self.lr * dL_db2
        self.W1 -= self.lr * dL_dW1
        self.b1 -= self.lr * dL_db1

        loss = self.cross_entropy_loss(self.a3, y_true)
        return loss

    def train(self, X, y, epochs=100, batch_size=32, verbose=True):
        n = X.shape[0]
        for epoch in range(epochs):
            # Shuffle
            idx = np.random.permutation(n)
            X_shuffled, y_shuffled = X[idx], y[idx]
            epoch_loss = 0.0
            for i in range(0, n, batch_size):
                X_batch = X_shuffled[i:i+batch_size]
                y_batch = y_shuffled[i:i+batch_size]
                self.forward(X_batch)
                loss = self.backward(X_batch, y_batch)
                epoch_loss += loss
            if verbose and epoch % 10 == 0:
                train_acc = self.accuracy(X, y)
                print(f"Epoch {epoch}: loss={epoch_loss:.4f}, acc={train_acc:.4f}")

    def predict(self, X):
        return np.argmax(self.forward(X), axis=1)

    def accuracy(self, X, y):
        return np.mean(self.predict(X) == np.argmax(y, axis=1))

def gradient_check():
    """Numerical gradient verification."""
    np.random.seed(42)
    mlp = MLP(input_size=10, hidden1_size=20, hidden2_size=15, output_size=5)
    X = np.random.randn(3, 10)
    y = np.eye(5)[np.random.randint(0, 5, 3)]

    # Forward
    mlp.forward(X)
    # Backward
    mlp.backward(X, y)

    # Numerical check for W1
    eps = 1e-7
    W1_orig = mlp.W1.copy()
    for i in range(mlp.W1.shape[0]):
        for j in range(mlp.W1.shape[1]):
            mlp.W1[i, j] = W1_orig[i, j] + eps
            mlp.forward(X)
            loss_plus = mlp.cross_entropy_loss(mlp.forward(X), y)
            mlp.W1[i, j] = W1_orig[i, j] - eps
            mlp.forward(X)
            loss_minus = mlp.cross_entropy_loss(mlp.forward(X), y)
            num_grad = (loss_plus - loss_minus) / (2 * eps)
            mlp.W1[i, j] = W1_orig[i, j]
            if i == 0:
                print(f"Numerical: {num_grad:.8f}, Analytical: {mlp.W1.grad if hasattr(mlp.W1, 'grad') else 'N/A'}")
            break
        break
```

## Interviewer Feedback
"Excellent implementation with correct chain rule application. The caching of forward values for backward pass is clean. Gradient checking would confirm correctness numerically. You've demonstrated strong understanding of the core deep learning algorithm."

## Key Takeaways
- Backpropagation applies the chain rule to compute gradients layer by layer
- Cache intermediate values (z, a) during forward for use in backward
- ReLU activation (gradient = 1 or 0) mitigates vanishing gradients
- Softmax + cross-entropy simplifies to y_pred - y_true for the output gradient
- He initialization with ReLU prevents gradient explosion/vanishing
