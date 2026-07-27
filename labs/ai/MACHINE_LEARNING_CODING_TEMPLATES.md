# Machine Learning Coding Templates

Common ML coding problems with implementations in Python (numpy) and Java. Each includes interview context, complexity analysis, and common follow-up questions.

---

## Table of Contents
1. [Linear Regression from Scratch](#1-linear-regression-from-scratch)
2. [Logistic Regression](#2-logistic-regression)
3. [K-Means Clustering](#3-k-means-clustering)
4. [K-Nearest Neighbors](#4-k-nearest-neighbors)
5. [Decision Tree (CART)](#5-decision-tree-cart)
6. [Neural Network Forward/Backward Pass](#6-neural-network-forwardbackward-pass)
7. [Transformer Attention Mechanism](#7-transformer-attention-mechanism)
8. [Loss Functions](#8-loss-functions)
9. [Evaluation Metrics](#9-evaluation-metrics)
10. [Gradient Descent Variants](#10-gradient-descent-variants)

---

## 1. Linear Regression from Scratch

### Interview Context
**Frequency**: Very High (most common first question)
**Time**: 15-20 min
**Follow-ups**: Regularization (Ridge/Lasso), feature scaling, matrix inversion vs gradient descent

### Python Implementation

```python
import numpy as np

class LinearRegression:
    def __init__(self, learning_rate=0.01, n_iterations=1000):
        self.lr = learning_rate
        self.n_iterations = n_iterations
        self.weights = None
        self.bias = None

    def fit(self, X, y):
        n_samples, n_features = X.shape
        self.weights = np.zeros(n_features)
        self.bias = 0

        for _ in range(self.n_iterations):
            y_predicted = np.dot(X, self.weights) + self.bias

            dw = (1 / n_samples) * np.dot(X.T, (y_predicted - y))
            db = (1 / n_samples) * np.sum(y_predicted - y)

            self.weights -= self.lr * dw
            self.bias -= self.lr * db

    def predict(self, X):
        return np.dot(X, self.weights) + self.bias

    def mse(self, y_true, y_pred):
        return np.mean((y_true - y_pred) ** 2)

    def r2_score(self, y_true, y_pred):
        ss_res = np.sum((y_true - y_pred) ** 2)
        ss_tot = np.sum((y_true - np.mean(y_true)) ** 2)
        return 1 - (ss_res / ss_tot)
```

### Java Implementation

```java
public class LinearRegression {
    private double[] weights;
    private double bias;
    private double learningRate;
    private int iterations;

    public LinearRegression(double learningRate, int iterations) {
        this.learningRate = learningRate;
        this.iterations = iterations;
    }

    public void fit(double[][] X, double[] y) {
        int n = X.length;
        int m = X[0].length;
        weights = new double[m];
        bias = 0.0;

        for (int iter = 0; iter < iterations; iter++) {
            double[] yPred = predict(X);
            double[] dw = new double[m];
            double db = 0.0;

            for (int j = 0; j < m; j++) {
                for (int i = 0; i < n; i++) {
                    dw[j] += (yPred[i] - y[i]) * X[i][j];
                }
                dw[j] /= n;
            }
            for (int i = 0; i < n; i++) {
                db += (yPred[i] - y[i]);
            }
            db /= n;

            for (int j = 0; j < m; j++) {
                weights[j] -= learningRate * dw[j];
            }
            bias -= learningRate * db;
        }
    }

    public double[] predict(double[][] X) {
        int n = X.length;
        double[] predictions = new double[n];
        for (int i = 0; i < n; i++) {
            double pred = bias;
            for (int j = 0; j < weights.length; j++) {
                pred += X[i][j] * weights[j];
            }
            predictions[i] = pred;
        }
        return predictions;
    }
}
```

### Key Points to Discuss
- Closed-form solution: `w = (X^T X)^{-1} X^T y` (O(n^3) complexity)
- Gradient descent preferred for large n
- Feature scaling affects convergence
- Normal equation fails when X^T X is singular

---

## 2. Logistic Regression

### Interview Context
**Frequency**: High
**Time**: 20-25 min
**Follow-ups**: Multiclass (softmax), regularization, handling imbalanced data

### Python Implementation

```python
import numpy as np

class LogisticRegression:
    def __init__(self, learning_rate=0.01, n_iterations=1000):
        self.lr = learning_rate
        self.n_iterations = n_iterations
        self.weights = None
        self.bias = None

    def sigmoid(self, z):
        return 1 / (1 + np.exp(-np.clip(z, -250, 250)))

    def fit(self, X, y):
        n_samples, n_features = X.shape
        self.weights = np.zeros(n_features)
        self.bias = 0

        for _ in range(self.n_iterations):
            linear_pred = np.dot(X, self.weights) + self.bias
            y_predicted = self.sigmoid(linear_pred)

            dw = (1 / n_samples) * np.dot(X.T, (y_predicted - y))
            db = (1 / n_samples) * np.sum(y_predicted - y)

            self.weights -= self.lr * dw
            self.bias -= self.lr * db

    def predict(self, X, threshold=0.5):
        linear_pred = np.dot(X, self.weights) + self.bias
        y_predicted = self.sigmoid(linear_pred)
        return (y_predicted >= threshold).astype(int)

    def predict_proba(self, X):
        linear_pred = np.dot(X, self.weights) + self.bias
        return self.sigmoid(linear_pred)

    def log_loss(self, y_true, y_pred):
        epsilon = 1e-15
        y_pred = np.clip(y_pred, epsilon, 1 - epsilon)
        return -np.mean(y_true * np.log(y_pred) + (1 - y_true) * np.log(1 - y_pred))
```

### Multiclass (Softmax) Extension

```python
class SoftmaxRegression:
    def __init__(self, learning_rate=0.01, n_iterations=1000):
        self.lr = learning_rate
        self.n_iterations = n_iterations
        self.W = None
        self.b = None

    def softmax(self, z):
        z -= np.max(z, axis=1, keepdims=True)
        exp_z = np.exp(z)
        return exp_z / np.sum(exp_z, axis=1, keepdims=True)

    def fit(self, X, y):
        n_samples, n_features = X.shape
        n_classes = len(np.unique(y))
        self.W = np.random.randn(n_features, n_classes) * 0.01
        self.b = np.zeros(n_classes)

        y_onehot = np.eye(n_classes)[y]

        for _ in range(self.n_iterations):
            scores = np.dot(X, self.W) + self.b
            probs = self.softmax(scores)

            dW = (1 / n_samples) * np.dot(X.T, (probs - y_onehot))
            db = (1 / n_samples) * np.sum(probs - y_onehot, axis=0)

            self.W -= self.lr * dW
            self.b -= self.lr * db
```

### Key Points to Discuss
- Sigmoid outputs probability (not logit)
- Cross-entropy loss is convex for logistic regression
- Decision boundary is linear
- No closed-form solution (unlike linear regression)

---

## 3. K-Means Clustering

### Interview Context
**Frequency**: High
**Time**: 20 min
**Follow-ups**: K-means++, initialization, choosing K (elbow, silhouette), limitations

### Python Implementation

```python
import numpy as np

class KMeans:
    def __init__(self, K=3, max_iters=100, init_method='random'):
        self.K = K
        self.max_iters = max_iters
        self.init_method = init_method
        self.centroids = None
        self.labels = None

    def _initialize_centroids(self, X):
        if self.init_method == 'random':
            indices = np.random.choice(len(X), self.K, replace=False)
            return X[indices]
        elif self.init_method == 'kmeans++':
            centroids = [X[np.random.randint(len(X))]]
            for _ in range(1, self.K):
                dists = np.min([np.sum((X - c) ** 2, axis=1) for c in centroids], axis=0)
                probs = dists / np.sum(dists)
                centroids.append(X[np.random.choice(len(X), p=probs)])
            return np.array(centroids)

    def _assign_clusters(self, X):
        X_norm = np.sum(X ** 2, axis=1, keepdims=True)
        centroids_norm = np.sum(self.centroids ** 2, axis=1)
        distances = X_norm + centroids_norm - 2 * np.dot(X, self.centroids.T)
        return np.argmin(distances, axis=1)

    def fit(self, X):
        self.centroids = self._initialize_centroids(X)

        for _ in range(self.max_iters):
            old_centroids = self.centroids.copy()
            self.labels = self._assign_clusters(X)

            for k in range(self.K):
                if np.sum(self.labels == k) > 0:
                    self.centroids[k] = np.mean(X[self.labels == k], axis=0)

            if np.allclose(old_centroids, self.centroids):
                break

        return self.labels

    def inertia(self, X):
        return sum(np.sum((X[self.labels == k] - self.centroids[k]) ** 2)
                   for k in range(self.K))

    def silhouette_score(self, X):
        n = len(X)
        a = np.zeros(n)
        b = np.full(n, np.inf)

        for i in range(n):
            same_cluster = X[self.labels == self.labels[i]]
            a[i] = np.mean(np.sum((same_cluster - X[i]) ** 2, axis=1))

            for k in range(self.K):
                if k != self.labels[i]:
                    other_cluster = X[self.labels == k]
                    dist = np.mean(np.sum((other_cluster - X[i]) ** 2, axis=1))
                    b[i] = min(b[i], dist)

        s = (b - a) / np.maximum(a, b)
        return np.mean(s)
```

### Key Points to Discuss
- Lloyd's algorithm: assignment + update steps
- K-means++ initialization prevents poor local optima
- Assumes spherical clusters (fails for non-convex shapes)
- Sensitive to outliers and scaling

---

## 4. K-Nearest Neighbors

### Interview Context
**Frequency**: Medium
**Time**: 15-20 min
**Follow-ups**: Curse of dimensionality, weighted KNN, KD-tree vs brute force

### Python Implementation

```python
import numpy as np
from collections import Counter

class KNN:
    def __init__(self, K=5, distance_metric='euclidean'):
        self.K = K
        self.distance_metric = distance_metric
        self.X_train = None
        self.y_train = None

    def _distance(self, a, b):
        if self.distance_metric == 'euclidean':
            return np.sqrt(np.sum((a - b) ** 2))
        elif self.distance_metric == 'manhattan':
            return np.sum(np.abs(a - b))
        elif self.distance_metric == 'cosine':
            dot = np.dot(a, b)
            norm_a = np.linalg.norm(a)
            norm_b = np.linalg.norm(b)
            return 1 - (dot / (norm_a * norm_b + 1e-10))

    def fit(self, X, y):
        self.X_train = X
        self.y_train = y

    def predict(self, X):
        predictions = [self._predict(x) for x in X]
        return np.array(predictions)

    def _predict(self, x):
        distances = [self._distance(x, x_train) for x_train in self.X_train]
        k_indices = np.argsort(distances)[:self.K]
        k_labels = [self.y_train[i] for i in k_indices]
        return Counter(k_labels).most_common(1)[0][0]

    def predict_proba(self, X):
        probas = []
        for x in X:
            distances = [self._distance(x, x_train) for x_train in self.X_train]
            k_indices = np.argsort(distances)[:self.K]
            k_labels = [self.y_train[i] for i in k_indices]

            weighted_distances = 1.0 / (np.array([distances[i] for i in k_indices]) + 1e-10)
            weighted_sum = {label: 0 for label in set(self.y_train)}
            for idx, label in zip(k_indices, k_labels):
                weighted_sum[self.y_train[idx]] += weighted_distances[len(weighted_sum)]
            total = sum(weighted_sum.values())
            probas.append({k: v/total for k, v in weighted_sum.items()})
        return probas
```

### Key Points to Discuss
- Lazy learner: no training phase, O(1) fit, O(nd) predict
- Distance metric choice is problem-dependent
- Feature scaling is critical (min-max or z-score)
- Curse of dimensionality: distance becomes meaningless in high dimensions

---

## 5. Decision Tree (CART)

### Interview Context
**Frequency**: High
**Time**: 25-30 min
**Follow-ups**: Pruning, impurity measures, categorical features, regression trees

### Python Implementation

```python
import numpy as np

class DecisionTree:
    class Node:
        def __init__(self):
            self.feature = None
            self.threshold = None
            self.left = None
            self.right = None
            self.value = None
            self.is_leaf = False

    def __init__(self, max_depth=10, min_samples_split=2, min_impurity_decrease=0.0):
        self.max_depth = max_depth
        self.min_samples_split = min_samples_split
        self.min_impurity_decrease = min_impurity_decrease
        self.root = None

    def _gini(self, y):
        classes = np.unique(y)
        impurity = 1.0
        for c in classes:
            p = np.sum(y == c) / len(y)
            impurity -= p ** 2
        return impurity

    def _entropy(self, y):
        classes = np.unique(y)
        entropy = 0.0
        for c in classes:
            p = np.sum(y == c) / len(y)
            if p > 0:
                entropy -= p * np.log2(p)
        return entropy

    def _mse(self, y):
        return np.mean((y - np.mean(y)) ** 2)

    def _information_gain(self, y, y_left, y_right, criterion='gini'):
        if criterion == 'gini':
            impurity = self._gini
        elif criterion == 'entropy':
            impurity = self._entropy
        else:
            impurity = self._mse

        parent_impurity = impurity(y)
        n = len(y)
        n_left = len(y_left)
        n_right = len(y_right)
        weighted_impurity = (n_left/n) * impurity(y_left) + (n_right/n) * impurity(y_right)
        return parent_impurity - weighted_impurity

    def _best_split(self, X, y):
        best_gain = 0
        best_feature = None
        best_threshold = None
        n_samples, n_features = X.shape

        for feature in range(n_features):
            values = np.unique(X[:, feature])
            if len(values) <= 1:
                continue
            thresholds = (values[:-1] + values[1:]) / 2

            for threshold in thresholds:
                left_mask = X[:, feature] <= threshold
                y_left = y[left_mask]
                y_right = y[~left_mask]

                if len(y_left) < self.min_samples_split or len(y_right) < self.min_samples_split:
                    continue

                gain = self._information_gain(y, y_left, y_right)
                if gain > best_gain:
                    best_gain = gain
                    best_feature = feature
                    best_threshold = threshold

        return best_feature, best_threshold, best_gain

    def _build_tree(self, X, y, depth=0):
        node = self.Node()

        if (depth >= self.max_depth or len(y) < self.min_samples_split or
            len(np.unique(y)) == 1):
            node.is_leaf = True
            node.value = np.mean(y) if self._task == 'regression' else np.bincount(y.astype(int)).argmax()
            return node

        feature, threshold, gain = self._best_split(X, y)

        if feature is None or gain <= self.min_impurity_decrease:
            node.is_leaf = True
            node.value = np.mean(y) if self._task == 'regression' else np.bincount(y.astype(int)).argmax()
            return node

        node.feature = feature
        node.threshold = threshold

        left_mask = X[:, feature] <= threshold
        node.left = self._build_tree(X[left_mask], y[left_mask], depth + 1)
        node.right = self._build_tree(X[~left_mask], y[~left_mask], depth + 1)

        return node

    def fit(self, X, y):
        self._task = 'regression' if y.dtype in [np.float64, np.float32] else 'classification'
        self.root = self._build_tree(X, y)

    def _traverse(self, x, node):
        if node.is_leaf:
            return node.value
        if x[node.feature] <= node.threshold:
            return self._traverse(x, node.left)
        return self._traverse(x, node.right)

    def predict(self, X):
        return np.array([self._traverse(x, self.root) for x in X])
```

### Key Points to Discuss
- CART uses binary splits (Gini for classification, MSE for regression)
- ID3 uses multi-way splits (information gain)
- Pruning prevents overfitting (cost-complexity pruning)
- Feature importance calculated from impurity reduction
- Decision surfaces are axis-aligned

---

## 6. Neural Network Forward/Backward Pass

### Interview Context
**Frequency**: Very High
**Time**: 30-40 min
**Follow-ups**: Vanishing gradients, batch normalization, dropout, initialization

### Python Implementation

```python
import numpy as np

class NeuralNetwork:
    def __init__(self, layer_dims, activations):
        self.layer_dims = layer_dims
        self.activations = activations
        self.parameters = self._initialize_parameters()
        self.caches = {}
        self.grads = {}

    def _initialize_parameters(self):
        parameters = {}
        for l in range(1, len(self.layer_dims)):
            parameters[f'W{l}'] = np.random.randn(
                self.layer_dims[l], self.layer_dims[l-1]) * np.sqrt(2.0 / self.layer_dims[l-1])
            parameters[f'b{l}'] = np.zeros((self.layer_dims[l], 1))
        return parameters

    def _activation(self, Z, activation_type):
        if activation_type == 'sigmoid':
            A = 1 / (1 + np.exp(-np.clip(Z, -500, 500)))
            cache = A
            return A, cache
        elif activation_type == 'relu':
            A = np.maximum(0, Z)
            cache = Z
            return A, cache
        elif activation_type == 'tanh':
            A = np.tanh(Z)
            cache = A
            return A, cache
        elif activation_type == 'softmax':
            Z_shifted = Z - np.max(Z, axis=0, keepdims=True)
            exp_Z = np.exp(Z_shifted)
            A = exp_Z / np.sum(exp_Z, axis=0, keepdims=True)
            cache = A
            return A, cache
        elif activation_type == 'linear':
            return Z, Z

    def _activation_derivative(self, dA, cache, activation_type):
        if activation_type == 'sigmoid':
            s = cache
            return dA * s * (1 - s)
        elif activation_type == 'relu':
            Z = cache
            dZ = dA.copy()
            dZ[Z <= 0] = 0
            return dZ
        elif activation_type == 'tanh':
            t = cache
            return dA * (1 - t ** 2)
        elif activation_type == 'linear':
            return dA

    def forward(self, X):
        caches = {}
        A = X

        for l in range(1, len(self.layer_dims)):
            A_prev = A
            W = self.parameters[f'W{l}']
            b = self.parameters[f'b{l}']
            Z = np.dot(W, A_prev) + b
            A, activation_cache = self._activation(Z, self.activations[l-1])
            caches[f'cache{l}'] = (A_prev, W, b, Z, activation_cache, self.activations[l-1])

        self.caches = caches
        return A

    def compute_loss(self, AL, Y, loss_type='binary_crossentropy'):
        m = Y.shape[1]
        if loss_type == 'binary_crossentropy':
            AL = np.clip(AL, 1e-15, 1 - 1e-15)
            loss = -(1/m) * np.sum(Y * np.log(AL) + (1 - Y) * np.log(1 - AL))
        elif loss_type == 'categorical_crossentropy':
            AL = np.clip(AL, 1e-15, 1)
            loss = -(1/m) * np.sum(Y * np.log(AL))
        elif loss_type == 'mse':
            loss = (1/m) * np.sum((Y - AL) ** 2)
        return loss

    def backward(self, AL, Y, loss_type='binary_crossentropy'):
        m = AL.shape[1]
        grads = {}

        if loss_type == 'binary_crossentropy':
            dAL = -(np.divide(Y, AL + 1e-15) - np.divide(1 - Y, 1 - AL + 1e-15))
        elif loss_type == 'categorical_crossentropy':
            dAL = AL - Y
        elif loss_type == 'mse':
            dAL = (2/m) * (AL - Y)

        for l in reversed(range(1, len(self.layer_dims))):
            A_prev, W, b, Z, activation_cache, activation_type = self.caches[f'cache{l}']

            if l == len(self.layer_dims) - 1:
                dA = dAL
            else:
                W_next = self.parameters[f'W{l+1}']
                dA = np.dot(W_next.T, grads[f'dZ{l+1}'])

            dZ = self._activation_derivative(dA, activation_cache, activation_type)

            grads[f'dW{l}'] = (1/m) * np.dot(dZ, A_prev.T)
            grads[f'db{l}'] = (1/m) * np.sum(dZ, axis=1, keepdims=True)
            grads[f'dZ{l}'] = dZ

        self.grads = grads

    def update(self, learning_rate=0.01):
        for l in range(1, len(self.layer_dims)):
            self.parameters[f'W{l}'] -= learning_rate * self.grads[f'dW{l}']
            self.parameters[f'b{l}'] -= learning_rate * self.grads[f'db{l}']
```

### Key Points to Discuss
- Backpropagation uses chain rule from calculus
- Gradient checking via numerical approximation
- Xavier/Glorot initialization for tanh/sigmoid
- He initialization for ReLU
- Vanishing/exploding gradient problem

---

## 7. Transformer Attention Mechanism

### Interview Context
**Frequency**: Very High
**Time**: 25-30 min
**Follow-ups**: Multi-head attention, masking, KV cache, causal vs bidirectional

### Python Implementation

```python
import numpy as np

class ScaledDotProductAttention:
    def forward(self, Q, K, V, mask=None, scale=None):
        d_k = Q.shape[-1]
        scale = scale or 1.0 / np.sqrt(d_k)

        scores = np.matmul(Q, K.transpose(0, 2, 1)) * scale

        if mask is not None:
            scores = np.where(mask == 0, -1e9, scores)

        attention_weights = self._softmax(scores)
        output = np.matmul(attention_weights, V)

        self.cache = (Q, K, V, attention_weights, mask, scale)
        return output, attention_weights

    def backward(self, d_output):
        Q, K, V, attention_weights, mask, scale = self.cache

        d_attention_weights = np.matmul(d_output, V.transpose(0, 2, 1))
        d_scores = attention_weights * (d_attention_weights - np.sum(
            d_attention_weights * attention_weights, axis=-1, keepdims=True))

        if scale != 1.0:
            d_scores *= scale

        d_Q = np.matmul(d_scores, K)
        d_K = np.matmul(d_scores.transpose(0, 2, 1), Q)
        d_V = np.matmul(attention_weights.transpose(0, 2, 1), d_output)

        return d_Q, d_K, d_V

    def _softmax(self, x):
        x -= np.max(x, axis=-1, keepdims=True)
        exp_x = np.exp(x)
        return exp_x / np.sum(exp_x, axis=-1, keepdims=True)
```

### Multi-Head Attention

```python
class MultiHeadAttention:
    def __init__(self, d_model, num_heads):
        assert d_model % num_heads == 0
        self.d_model = d_model
        self.num_heads = num_heads
        self.d_k = d_model // num_heads

        self.W_Q = np.random.randn(d_model, d_model) * np.sqrt(2.0 / d_model)
        self.W_K = np.random.randn(d_model, d_model) * np.sqrt(2.0 / d_model)
        self.W_V = np.random.randn(d_model, d_model) * np.sqrt(2.0 / d_model)
        self.W_O = np.random.randn(d_model, d_model) * np.sqrt(2.0 / d_model)

        self.attention = ScaledDotProductAttention()

    def _split_heads(self, x, batch_size):
        x = x.reshape(batch_size, -1, self.num_heads, self.d_k)
        return x.transpose(0, 2, 1, 3)

    def _combine_heads(self, x, batch_size):
        x = x.transpose(0, 2, 1, 3)
        return x.reshape(batch_size, -1, self.d_model)

    def forward(self, Q, K, V, mask=None):
        batch_size = Q.shape[0]

        Q_proj = np.dot(Q, self.W_Q)
        K_proj = np.dot(K, self.W_K)
        V_proj = np.dot(V, self.W_V)

        Q_heads = self._split_heads(Q_proj, batch_size)
        K_heads = self._split_heads(K_proj, batch_size)
        V_heads = self._split_heads(V_proj, batch_size)

        attention_output, attention_weights = self.attention.forward(
            Q_heads, K_heads, V_heads, mask)

        concat_attention = self._combine_heads(attention_output, batch_size)
        output = np.dot(concat_attention, self.W_O)

        return output, attention_weights
```

### Transformer Block (for reference)

```python
class TransformerBlock:
    def __init__(self, d_model, num_heads, d_ff, dropout_rate=0.1):
        self.attention = MultiHeadAttention(d_model, num_heads)
        self.layer_norm1 = LayerNorm(d_model)
        self.layer_norm2 = LayerNorm(d_model)
        self.ffn = FeedForward(d_model, d_ff)
        self.dropout_rate = dropout_rate

    def forward(self, x, mask=None):
        # Pre-norm architecture (common in modern transformers)
        attn_output, _ = self.attention.forward(
            self.layer_norm1.forward(x),
            self.layer_norm1.forward(x),
            self.layer_norm1.forward(x),
            mask
        )
        x = x + attn_output  # Residual connection

        ffn_output = self.ffn.forward(self.layer_norm2.forward(x))
        x = x + ffn_output  # Residual connection

        return x

class LayerNorm:
    def __init__(self, d_model, eps=1e-5):
        self.gamma = np.ones(d_model)
        self.beta = np.zeros(d_model)
        self.eps = eps

    def forward(self, x):
        mean = np.mean(x, axis=-1, keepdims=True)
        var = np.var(x, axis=-1, keepdims=True)
        self.x_normalized = (x - mean) / np.sqrt(var + self.eps)
        return self.gamma * self.x_normalized + self.beta

class FeedForward:
    def __init__(self, d_model, d_ff):
        self.W1 = np.random.randn(d_model, d_ff) * np.sqrt(2.0 / d_model)
        self.b1 = np.zeros(d_ff)
        self.W2 = np.random.randn(d_ff, d_model) * np.sqrt(2.0 / d_ff)
        self.b2 = np.zeros(d_model)

    def forward(self, x):
        self.x = x
        self.hidden = np.maximum(0, np.dot(x, self.W1) + self.b1)
        return np.dot(self.hidden, self.W2) + self.b2
```

### Key Points to Discuss
- Attention is O(n^2) in sequence length
- Multi-head attention allows attending to different subspaces
- Masking prevents attending to future tokens (causal)
- Positional encoding: sinusoidal vs learned vs RoPE
- KV cache speeds up autoregressive decoding

---

## 8. Loss Functions

### Interview Context
**Frequency**: Medium
**Time**: 10-15 min per function
**Follow-ups**: Gradient derivation, numerical stability

### Python Implementation

```python
import numpy as np

class LossFunctions:
    @staticmethod
    def mse(y_true, y_pred):
        loss = np.mean((y_true - y_pred) ** 2)
        grad = 2 * (y_pred - y_true) / len(y_true)
        return loss, grad

    @staticmethod
    def mae(y_true, y_pred):
        loss = np.mean(np.abs(y_true - y_pred))
        grad = np.sign(y_pred - y_true) / len(y_true)
        return loss, grad

    @staticmethod
    def huber(y_true, y_pred, delta=1.0):
        error = y_true - y_pred
        is_small_error = np.abs(error) <= delta
        squared_loss = 0.5 * error ** 2
        linear_loss = delta * (np.abs(error) - 0.5 * delta)
        loss = np.mean(np.where(is_small_error, squared_loss, linear_loss))

        grad = np.where(is_small_error, -error, -delta * np.sign(error))
        grad /= len(y_true)
        return loss, grad

    @staticmethod
    def binary_crossentropy(y_true, y_pred, epsilon=1e-15):
        y_pred = np.clip(y_pred, epsilon, 1 - epsilon)
        loss = -np.mean(y_true * np.log(y_pred) + (1 - y_true) * np.log(1 - y_pred))
        grad = -(y_true / y_pred - (1 - y_true) / (1 - y_pred)) / len(y_true)
        return loss, grad

    @staticmethod
    def categorical_crossentropy(y_true, y_pred, epsilon=1e-15):
        y_pred = np.clip(y_pred, epsilon, 1.0)
        loss = -np.mean(np.sum(y_true * np.log(y_pred), axis=-1))
        grad = -(y_true / y_pred) / len(y_true)
        return loss, grad

    @staticmethod
    def hinge(y_true, y_pred):
        margins = 1 - y_true * y_pred
        loss = np.mean(np.maximum(0, margins))
        grad = -y_true * (margins > 0).astype(float) / len(y_true)
        return loss, grad

    @staticmethod
    def contrastive(y_true, distance, margin=1.0):
        square_dist = distance ** 2
        loss = np.mean(y_true * square_dist + (1 - y_true) * np.maximum(0, margin - square_dist))
        # gradient depends on distance computation structure
        return loss

    @staticmethod
    def triplet(anchor, positive, negative, margin=0.2):
        pos_dist = np.sum((anchor - positive) ** 2, axis=-1)
        neg_dist = np.sum((anchor - negative) ** 2, axis=-1)
        loss = np.mean(np.maximum(0, pos_dist - neg_dist + margin))
        return loss
```

---

## 9. Evaluation Metrics

### Interview Context
**Frequency**: Medium
**Time**: 10-15 min per metric
**Follow-ups**: When to use which metric, multi-class extensions

### Python Implementation

```python
import numpy as np

class EvaluationMetrics:
    @staticmethod
    def accuracy(y_true, y_pred):
        return np.mean(y_true == y_pred)

    @staticmethod
    def precision(y_true, y_pred):
        tp = np.sum((y_true == 1) & (y_pred == 1))
        fp = np.sum((y_true == 0) & (y_pred == 1))
        return tp / (tp + fp + 1e-15)

    @staticmethod
    def recall(y_true, y_pred):
        tp = np.sum((y_true == 1) & (y_pred == 1))
        fn = np.sum((y_true == 1) & (y_pred == 0))
        return tp / (tp + fn + 1e-15)

    @staticmethod
    def f1_score(y_true, y_pred):
        p = EvaluationMetrics.precision(y_true, y_pred)
        r = EvaluationMetrics.recall(y_true, y_pred)
        return 2 * p * r / (p + r + 1e-15)

    @staticmethod
    def confusion_matrix(y_true, y_pred, num_classes):
        cm = np.zeros((num_classes, num_classes), dtype=int)
        for t, p in zip(y_true, y_pred):
            cm[t][p] += 1
        return cm

    @staticmethod
    def roc_curve(y_true, y_scores, num_thresholds=100):
        thresholds = np.linspace(0, 1, num_thresholds)
        tpr = np.zeros(num_thresholds)
        fpr = np.zeros(num_thresholds)

        for i, thresh in enumerate(thresholds):
            y_pred = (y_scores >= thresh).astype(int)
            tp = np.sum((y_true == 1) & (y_pred == 1))
            fn = np.sum((y_true == 1) & (y_pred == 0))
            fp = np.sum((y_true == 0) & (y_pred == 1))
            tn = np.sum((y_true == 0) & (y_pred == 0))
            tpr[i] = tp / (tp + fn + 1e-15)
            fpr[i] = fp / (fp + tn + 1e-15)

        return fpr, tpr, thresholds

    @staticmethod
    def auc(fpr, tpr):
        return -np.trapz(tpr, fpr)

    @staticmethod
    def log_loss(y_true, y_pred, epsilon=1e-15):
        y_pred = np.clip(y_pred, epsilon, 1 - epsilon)
        return -np.mean(y_true * np.log(y_pred) + (1 - y_true) * np.log(1 - y_pred))

    @staticmethod
    def mean_average_precision(y_true, y_scores):
        desc_order = np.argsort(-y_scores)
        y_true_sorted = y_true[desc_order]
        n_relevant = np.sum(y_true)

        precisions = []
        n_correct = 0
        for i, rel in enumerate(y_true_sorted):
            if rel == 1:
                n_correct += 1
                precisions.append(n_correct / (i + 1))

        return np.mean(precisions) if precisions else 0.0

    @staticmethod
    def ndcg(y_true, y_scores, k=10):
        desc_order = np.argsort(-y_scores)
        y_true_sorted = y_true[desc_order][:k]

        dcg = sum((2**rel - 1) / np.log2(i + 2) for i, rel in enumerate(y_true_sorted))

        ideal_order = np.argsort(-y_true)[:k]
        y_true_ideal = y_true[ideal_order]
        idcg = sum((2**rel - 1) / np.log2(i + 2) for i, rel in enumerate(y_true_ideal))

        return dcg / idcg if idcg > 0 else 0.0

    @staticmethod
    def silhouette_score(X, labels):
        n = len(X)
        a = np.zeros(n)
        b = np.full(n, np.inf)

        for i in range(n):
            same_cluster = X[labels == labels[i]]
            a[i] = np.mean(np.sum((same_cluster - X[i]) ** 2, axis=1))

            for k in np.unique(labels):
                if k != labels[i]:
                    other_cluster = X[labels == k]
                    dist = np.mean(np.sum((other_cluster - X[i]) ** 2, axis=1))
                    b[i] = min(b[i], dist)

        s = (b - a) / np.maximum(a, b)
        return np.mean(s)
```

---

## 10. Gradient Descent Variants

### Interview Context
**Frequency**: High
**Time**: 15-20 min
**Follow-ups**: Adaptive methods, learning rate scheduling, convergence proofs

### Python Implementation

```python
import numpy as np

class GradientDescent:
    def __init__(self, learning_rate=0.01, beta=0.9, beta1=0.9, beta2=0.999, epsilon=1e-8):
        self.lr = learning_rate
        self.beta = beta
        self.beta1 = beta1
        self.beta2 = beta2
        self.epsilon = epsilon

    def sgd(self, grad, params):
        return params - self.lr * grad

    def momentum(self, grad, velocity):
        velocity = self.beta * velocity + (1 - self.beta) * grad
        return velocity

    def nesterov(self, grad, params, velocity):
        look_ahead = params - self.beta * velocity
        velocity = self.beta * velocity + (1 - self.beta) * grad(look_ahead)
        return velocity

    def adagrad(self, grad, cache):
        cache += grad ** 2
        return self.lr * grad / (np.sqrt(cache) + self.epsilon)

    def rmsprop(self, grad, cache):
        cache = self.beta * cache + (1 - self.beta) * grad ** 2
        return self.lr * grad / (np.sqrt(cache) + self.epsilon)

    def adam(self, grad, m, v, t):
        m = self.beta1 * m + (1 - self.beta1) * grad
        v = self.beta2 * v + (1 - self.beta2) * grad ** 2

        m_hat = m / (1 - self.beta1 ** t)
        v_hat = v / (1 - self.beta2 ** t)

        return self.lr * m_hat / (np.sqrt(v_hat) + self.epsilon), m, v


class Optimizer:
    @staticmethod
    def learning_rate_decay(lr, t, decay_type='step', **kwargs):
        if decay_type == 'step':
            return lr * (kwargs.get('decay_rate', 0.1) ** (t // kwargs.get('step_size', 30)))
        elif decay_type == 'exponential':
            return lr * np.exp(-kwargs.get('decay_rate', 0.001) * t)
        elif decay_type == 'cosine':
            T_max = kwargs.get('T_max', 100)
            return lr * 0.5 * (1 + np.cos(np.pi * t / T_max))
        elif decay_type == 'linear':
            return lr * max(0, 1 - t / kwargs.get('T_max', 100))
        return lr

    @staticmethod
    def cyclical_lr(lr_min, lr_max, step_size, t):
        cycle = np.floor(1 + t / (2 * step_size))
        x = np.abs(t / step_size - 2 * cycle + 1)
        return lr_min + (lr_max - lr_min) * max(0, 1 - x)

    @staticmethod
    def warmup(lr, t, warmup_steps=5):
        if t < warmup_steps:
            return lr * (t + 1) / warmup_steps
        return lr
```

### Key Points to Discuss
- SGD with momentum helps escape local minima
- Adam combines momentum + adaptive learning rates
- Learning rate scheduling is crucial for convergence
- Nesterov momentum corrects overshooting
- Batch normalization reduces dependence on learning rate
