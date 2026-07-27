# Mock Interview: Neural Networks Basics

## Question 1: Perceptron & MLP
**Q**: Implement a single perceptron. Then extend to a multi-layer perceptron.

**A**:
```python
class Perceptron:
    def __init__(self, lr=0.01, n_iter=100):
        self.lr = lr
        self.n_iter = n_iter

    def fit(self, X, y):
        n, d = X.shape
        self.w = np.zeros(d)
        self.b = 0
        for _ in range(self.n_iter):
            for xi, yi in zip(X, y):
                linear = np.dot(xi, self.w) + self.b
                y_pred = np.where(linear >= 0, 1, 0)
                update = self.lr * (yi - y_pred)
                self.w += update * xi
                self.b += update

class MLP:
    def __init__(self, layers, lr=0.01):
        self.layers = layers  # list of layer sizes
        self.lr = lr
        self.params = {}
        for i in range(1, len(layers)):
            self.params[f'W{i}'] = np.random.randn(layers[i-1], layers[i]) * 0.01
            self.params[f'b{i}'] = np.zeros(layers[i])

    def forward(self, X):
        self.A = [X]
        for i in range(1, len(self.layers)):
            z = self.A[-1] @ self.params[f'W{i}'] + self.params[f'b{i}']
            self.A.append(np.maximum(0, z))  # ReLU
        return self.A[-1]
```

## Question 2: Activation Functions
**Q**: Compare sigmoid, tanh, ReLU, and GELU activation functions.

**A**: 
| Function | Output | Vanishing Gradient? | Zero-Centered? | Used In |
|----------|--------|--------------------|----------------|---------|
| Sigmoid | (0,1) | Yes | No | Output layer |
| Tanh | (-1,1) | Yes | Yes | RNNs |
| ReLU | [0,inf) | No | No | CNNs, MLPs |
| GELU | (-inf,inf) | No | Approx | Transformers |

ReLU can cause "dead neurons" (gradient = 0 for negative inputs). Leaky ReLU and ELU address this.

## Question 3: Initialization
**Q**: Why does weight initialization matter? Compare Xavier vs He initialization.

**A**: Poor initialization causes: vanishing/exploding gradients, slow convergence.

- **Xavier/Glorot**: Var(w) = 2/(fan_in + fan_out). For sigmoid/tanh.
- **He**: Var(w) = 2/fan_in. For ReLU.
- **LeCun**: Var(w) = 1/fan_in. For SELU.

Key principle: maintain variance of activations and gradients across layers.

## Question 4: Universal Approximation Theorem
**Q**: Explain the Universal Approximation Theorem. What are its practical implications and limitations?

**A**: A feedforward network with one hidden layer (with sufficient neurons and non-linear activation) can approximate any continuous function to arbitrary accuracy.

**Practical limitations**:
- Number of neurons may need to be exponentially large
- Finding the right weights is not guaranteed (optimization)
- Training data is finite
- Overfitting risk

## Question 5: Vanishing/Exploding Gradients
**Q**: What causes vanishing/exploding gradients in deep networks? How do you mitigate?

**A**: **Causes**: 
- Deep networks with saturating activations (sigmoid/tanh) -> gradients multiply by <1
- Deep networks -> product of many partial derivatives (chain rule)

**Mitigations**:
- ReLU instead of sigmoid/tanh
- Proper initialization (He/Xavier)
- Batch normalization
- Residual connections (skip connections)
- Gradient clipping (for exploding gradients)
- Layer normalization (Transformers)
