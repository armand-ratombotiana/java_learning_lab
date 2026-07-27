# Neural Networks Academy Guide — 10 Micro-Labs

> Sub-academy guide for the `neural-networks-deep` micro-lab sequence.
> Each lab builds on the previous; complete them in order.

---

## Lab 01: Perceptron

**Objective:** Implement the Rosenblatt perceptron algorithm from scratch.

**Code Output Expected:**
```python
class Perceptron:
    def __init__(self, lr=1.0, max_epochs=100):
        self.lr = lr
        self.max_epochs = max_epochs
        self.w = None
        self.b = 0

    def fit(self, X, y):
        n_samples, n_features = X.shape
        self.w = np.zeros(n_features)
        self.b = 0
        for epoch in range(self.max_epochs):
            errors = 0
            for xi, yi in zip(X, y):
                if yi * (np.dot(xi, self.w) + self.b) <= 0:
                    self.w += self.lr * yi * xi
                    self.b += self.lr * yi
                    errors += 1
            if errors == 0:
                break
        return self

    def predict(self, X):
        return np.sign(np.dot(X, self.w) + self.b)
```

**Key Interview Questions:**
1. Prove the perceptron convergence theorem. What bound does it give?
2. Why does the perceptron fail on XOR? Draw the decision boundary.
3. What happens if the data is not linearly separable? The algorithm does not converge.
4. How does normalization affect convergence speed?

**Company Focus:** Foundational — asked at every company (Google, Meta, Amazon, Microsoft) as ML basics screening.

**Common Pitfalls:**
- Initializing weights randomly instead of zero (unnecessary — perceptron does not need symmetry breaking)
- Not checking for convergence (early stopping)
- Forgetting the `sign` activation during prediction
- Using `w += -lr * gradient` instead of the perceptron update rule

---

## Lab 02: MLP — Multi-Layer Perceptron

**Objective:** Build a fully-connected neural network with one hidden layer.

**Code Output Expected:**
```python
class MLP:
    def __init__(self, input_size, hidden_size, output_size, lr=0.01):
        self.W1 = np.random.randn(input_size, hidden_size) * 0.01
        self.b1 = np.zeros(hidden_size)
        self.W2 = np.random.randn(hidden_size, output_size) * 0.01
        self.b2 = np.zeros(output_size)
        self.lr = lr

    def forward(self, X):
        self.z1 = X @ self.W1 + self.b1
        self.a1 = np.maximum(self.z1, 0)  # ReLU
        self.z2 = self.a1 @ self.W2 + self.b2
        exp_z = np.exp(self.z2 - np.max(self.z2, axis=1, keepdims=True))
        self.a2 = exp_z / np.sum(exp_z, axis=1, keepdims=True)  # Softmax
        return self.a2

    def backward(self, X, y):
        m = X.shape[0]
        dz2 = self.a2 - y
        dW2 = self.a1.T @ dz2 / m
        db2 = np.sum(dz2, axis=0) / m
        da1 = dz2 @ self.W2.T
        dz1 = da1 * (self.z1 > 0)  # ReLU derivative
        dW1 = X.T @ dz1 / m
        db1 = np.sum(dz1, axis=0) / m
        self.W1 -= self.lr * dW1
        self.b1 -= self.lr * db1
        self.W2 -= self.lr * dW2
        self.b2 -= self.lr * db2
```

**Key Interview Questions:**
1. Derive backpropagation for this 2-layer network step by step.
2. Explain why we use cross-entropy loss with softmax.
3. What is the role of the hidden layer size? How does it affect bias-variance tradeoff?
4. Why use ReLU instead of sigmoid? Show the gradient comparison.
5. Explain the universal approximation theorem. What are its limitations?

**Company Focus:** Meta (ML system design), Amazon (applied scientist screen), Apple (ML engineer).

**Common Pitfalls:**
- Using sigmoid/tanh in hidden layers of deep networks (vanishing gradient)
- Forgetting to divide gradients by batch size
- Initializing weights too large (exploding activations) or too small (vanishing)
- No softmax stabilization (`np.exp` can overflow without max subtraction)

---

## Lab 03: Backpropagation

**Objective:** Implement backpropagation with computational graphs and manual gradient computation.

**Code Output Expected:**
```python
class ComputationalGraph:
    class Variable:
        def __init__(self, value, parents=None):
            self.value = value
            self.grad = 0
            self.parents = parents or []

        def backward(self, grad=1):
            self.grad += grad
            for parent, local_grad in self.parents:
                parent.backward(grad * local_grad)

def matmul(A, B):
    value = A.value @ B.value
    def local_grad_A(grad): return grad @ B.value.T
    def local_grad_B(grad): return A.value.T @ grad
    return Variable(value, [(A, local_grad_A), (B, local_grad_B)])

def relu(X):
    value = np.maximum(X.value, 0)
    def local_grad(grad): return grad * (X.value > 0)
    return Variable(value, [(X, local_grad)])
```

**Key Interview Questions:**
1. Derive the chain rule for a 3-layer network with ReLU and cross-entropy.
2. Explain automatic differentiation: forward mode vs reverse mode. Why is reverse mode preferred for deep learning?
3. What is the computational graph for a residual connection? Show forward and backward.
4. How does softmax backward work? Show the Jacobian derivation.

**Company Focus:** DeepMind, OpenAI, Anthropic, Tesla (autopilot) — deep ML understanding required.

**Common Pitfalls:**
- Incorrectly computing the softmax Jacobian (it is a full matrix, not per-element)
- Accumulating gradients instead of resetting them each batch
- Off-by-one in layer indexing
- Not handling the batch dimension in matrix gradient computations

---

## Lab 04: Regularization

**Objective:** Implement L1, L2, dropout, and batch normalization.

**Code Output Expected:**
```python
class Dropout:
    def __init__(self, p=0.5):
        self.p = p
        self.mask = None

    def forward(self, X, training=True):
        if training:
            self.mask = np.random.binomial(1, 1 - self.p, X.shape)
            return X * self.mask / (1 - self.p)
        return X

    def backward(self, dout):
        return dout * self.mask / (1 - self.p)

class BatchNorm:
    def __init__(self, dim, eps=1e-5, momentum=0.9):
        self.gamma = np.ones(dim)
        self.beta = np.zeros(dim)
        self.eps = eps
        self.momentum = momentum
        self.running_mean = np.zeros(dim)
        self.running_var = np.ones(dim)

    def forward(self, X, training=True):
        if training:
            mean = X.mean(axis=0)
            var = X.var(axis=0)
            self.running_mean = self.momentum * self.running_mean + (1 - self.momentum) * mean
            self.running_var = self.momentum * self.running_var + (1 - self.momentum) * var
        else:
            mean = self.running_mean
            var = self.running_var
        X_norm = (X - mean) / np.sqrt(var + self.eps)
        return self.gamma * X_norm + self.beta
```

**Key Interview Questions:**
1. Why does L1 regularization induce sparsity but L2 does not? Derive the gradient.
2. Explain inverted dropout. Why divide by `1-p` during training?
3. What is the mathematical justification for batch normalization? How does it help optimization?
4. Why does batch norm behave differently during training vs inference?
5. Compare layer norm and batch norm. Why is layer norm preferred in transformers?

**Company Focus:** Google (JAX/Flax systems), NVIDIA (CUDA optimization), Apple (on-device ML).

**Common Pitfalls:**
- Forgetting to scale by `1/(1-p)` in inverted dropout
- Not accumulating running statistics in batch norm
- Applying batch norm to RNNs without proper handling
- Using batch norm with very small batch sizes (noisy statistics)

---

## Lab 05: Optimizers

**Objective:** Implement SGD, momentum, RMSProp, Adam, AdamW from scratch.

**Code Output Expected:**
```python
class Adam:
    def __init__(self, lr=0.001, beta1=0.9, beta2=0.999, eps=1e-8):
        self.lr = lr
        self.beta1 = beta1
        self.beta2 = beta2
        self.eps = eps
        self.t = 0
        self.m = {}
        self.v = {}

    def step(self, params, grads):
        self.t += 1
        for key in params:
            if key not in self.m:
                self.m[key] = np.zeros_like(params[key])
                self.v[key] = np.zeros_like(params[key])
            self.m[key] = self.beta1 * self.m[key] + (1 - self.beta1) * grads[key]
            self.v[key] = self.beta2 * self.v[key] + (1 - self.beta2) * grads[key] ** 2
            m_hat = self.m[key] / (1 - self.beta1 ** self.t)
            v_hat = self.v[key] / (1 - self.beta2 ** self.t)
            params[key] -= self.lr * m_hat / (np.sqrt(v_hat) + self.eps)
```

**Key Interview Questions:**
1. Derive Adam from first principles. Explain each term and hyperparameter.
2. Compare Adam and SGD with momentum — why does SGD sometimes generalize better?
3. What is the generalization gap and how does AdamW address it?
4. Explain bias correction in Adam. Why is it necessary?
5. Compare learning rate schedules: cosine annealing vs step decay vs OneCycle.
6. What is gradient clipping and when is it necessary? Show the algorithm.

**Company Focus:** DeepMind, OpenAI (training large models), Tesla (autopilot training), Microsoft (Azure ML).

**Common Pitfalls:**
- Not resetting first/second moment estimates between training runs
- Using default Adam LR (0.001) for every problem
- Not implementing bias correction (convergence is worse)
- Confusing AdamW weight decay with L2 regularization in Adam
- Using the same LR for all layers (discriminative LR needed)

---

## Lab 06: Architecture Design

**Objective:** Implement ResNet-style skip connections and DenseNet-style dense blocks.

**Code Output Expected:**
```python
class ResidualBlock:
    def __init__(self, in_channels, out_channels, stride=1):
        self.conv1 = Conv2D(in_channels, out_channels, 3, stride=stride, padding=1)
        self.bn1 = BatchNorm(out_channels)
        self.conv2 = Conv2D(out_channels, out_channels, 3, stride=1, padding=1)
        self.bn2 = BatchNorm(out_channels)
        if stride != 1 or in_channels != out_channels:
            self.shortcut = Conv2D(in_channels, out_channels, 1, stride=stride)
        else:
            self.shortcut = Identity()

    def forward(self, x):
        residual = self.shortcut(x)
        out = np.maximum(self.bn1(self.conv1(x)), 0)
        out = self.bn2(self.conv2(out))
        return np.maximum(out + residual, 0)
```

**Key Interview Questions:**
1. Why do skip connections prevent vanishing gradients? Show the gradient flow.
2. Explain the ensemble interpretation of ResNets.
3. Compare DenseNet and ResNet — parameter efficiency, memory usage, feature reuse.
4. How does the EfficientNet compound scaling formula `α·β²·γ² ≈ 2` arise?
5. Show the activation function decision tree for architecture design.

**Company Focus:** Meta (FAIR), Google Brain, NVIDIA, Qualcomm (edge AI).

**Common Pitfalls:**
- Wrong shortcut dimension for the `1x1` convolution when stride > 1
- Not using pre-activation order (BN → ReLU → Conv)
- Doubling channels at the wrong position (should be at stride-2 block)
- Forgetting batch norm before activation in pre-activation design

---

## Lab 07: Fashion MNIST

**Objective:** Train an MLP on Fashion MNIST, apply all optimizers and regularizations.

**Code Output Expected:**
```python
def train_with_config(X_train, y_train, X_val, y_val, config):
    model = MLP(
        input_size=784,
        hidden_sizes=config['hidden_sizes'],
        output_size=10,
        activation=config.get('activation', 'relu')
    )
    optimizer = Adam(lr=config['lr'])
    scheduler = CosineAnnealingLR(T_max=config['epochs'])
    dropout = Dropout(p=config.get('dropout', 0.0))

    for epoch in range(config['epochs']):
        for X_batch, y_batch in get_batches(X_train, y_train, 128):
            logits = model.forward(X_batch)
            logits = dropout.forward(logits, training=True)
            loss = cross_entropy(logits, y_batch)
            loss += config['weight_decay'] * (sum(np.sum(w**2) for w in model.params()))
            grads = model.backward(X_batch, y_batch)
            optimizer.step(model.params(), grads)
        scheduler.step()

        val_acc = evaluate(model, X_val, y_val)
        if config.get('early_stop') and early_stop.should_stop(val_loss):
            break
```

**Key Interview Questions:**
1. How do you choose the architecture for this dataset? Why 784 → 256 → 128 → 10?
2. Which optimizer converges fastest on Fashion MNIST?
3. How does dropout interact with other regularization methods?
4. What is the best learning rate and schedule for this dataset?
5. How does early stopping choose the best model?

**Company Focus:** All companies (standard ML benchmark). Apple, Amazon, Google, Microsoft.

**Common Pitfalls:**
- Not flattening 28×28 images to 784 correctly
- Using softmax with ReLU in hidden layers but forgetting the output activation
- Not one-hot encoding labels for cross-entropy
- Training too long without early stopping
- Not normalizing pixel values to [0, 1] or z-score

---

## Lab 08: CIFAR-10

**Objective:** Train a CNN on CIFAR-10 with data augmentation and batch normalization.

**Code Output Expected:**
```python
def train_cifar():
    # Data augmentation
    transform = [
        RandomHorizontalFlip(p=0.5),
        RandomCrop(32, padding=4),
        ColorJitter(brightness=0.2, contrast=0.2),
        Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
    ]

    # Architecture
    model = Sequential([
        Conv2D(3, 64, 3, padding=1), BN(64), ReLU(),
        Conv2D(64, 64, 3, padding=1), BN(64), ReLU(), MaxPool2D(2),
        Conv2D(64, 128, 3, padding=1), BN(128), ReLU(),
        Conv2D(128, 128, 3, padding=1), BN(128), ReLU(), MaxPool2D(2),
        Flatten(),
        Dense(512), BN(512), ReLU(), Dropout(0.5),
        Dense(10)
    ])

    optimizer = AdamW(lr=1e-3, weight_decay=5e-4)
    scheduler = CosineAnnealingLR(T_max=200)

    for epoch in range(200):
        for X, y in augmented_data_loader:
            logits = model(X)
            loss = cross_entropy(logits, y)
            loss.backward()
            grad_clip_norm(model.parameters(), max_norm=1.0)
            optimizer.step()
            scheduler.step()
```

**Key Interview Questions:**
1. Why use data augmentation for CIFAR-10? Which augmentations help most?
2. Explain the design pattern: Conv → BN → ReLU → Conv → BN → ReLU → Pool.
3. Why add dropout after the dense layer but not after convolutions?
4. How does AdamW with weight decay compare to Adam + L2 on CIFAR-10?
5. Why use gradient clipping with CNNs?

**Company Focus:** Meta, Google, Apple (computer vision roles), Tesla (vision).

**Common Pitfalls:**
- Using augmented data incorrectly (applying normalization before instead of after random transforms)
- Not padding before random crop (crops go out of bounds)
- Training with too large a learning rate (CIFAR-10 CNNs need 1e-3 with warmup)
- Using ReLU before BN (should be BN → ReLU in pre-activation)
- Evaluating on non-normalized test images

---

## Lab 09: Transfer Learning

**Objective:** Fine-tune a pre-trained model on a custom dataset.

**Code Output Expected:**
```python
def fine_tune(model, train_loader, val_loader, num_classes):
    # Replace classifier head
    model.fc = Linear(2048, num_classes)  # ResNet50 has 2048-dim features

    # Freeze early layers
    for name, param in model.named_parameters():
        if 'fc' not in name:
            param.requires_grad = False

    # Differential learning rates
    fc_params = model.fc.parameters()
    base_params = [p for n, p in model.named_parameters() if 'fc' not in n]

    optimizer = AdamW([
        {'params': base_params, 'lr': 1e-5},
        {'params': fc_params, 'lr': 1e-3}
    ], weight_decay=1e-4)

    # Gradually unfreeze
    for epoch in range(epochs):
        if epoch == 3:
            unfreeze_layer(model, 'layer4')
        if epoch == 6:
            unfreeze_all(model)
```

**Key Interview Questions:**
1. When do you freeze layers vs fine-tune all layers?
2. Why use differential learning rates? How do you set them?
3. What is catastrophic forgetting? How does gradual unfreezing help?
4. Explain domain adaptation: how do you handle covariate shift?
5. How does multi-task learning with a shared backbone work?

**Company Focus:** Google (Cloud AI), Microsoft (Azure AI), Amazon (SageMaker), DeepMind.

**Common Pitfalls:**
- Fine-tuning all layers with the same LR (destroys pre-trained features)
- Not freezing batch norm statistics during fine-tuning with small batches
- Using pre-trained weights from non-matching input dimensions
- Training the classification head for too few epochs before unfreezing
- Forgetting to add dropout/weight decay for the new head

---

## Lab 10: Model Compression

**Objective:** Prune, quantize, and distill a neural network.

**Code Output Expected:**
```python
# Magnitude pruning
def magnitude_prune(model, sparsity=0.5):
    for name, param in model.named_parameters():
        if 'weight' in name:
            threshold = np.percentile(np.abs(param), sparsity * 100)
            mask = np.abs(param) > threshold
            param.data *= mask

# Quantization (symmetric INT8)
def quantize_weights(weights):
    max_val = np.max(np.abs(weights))
    scale = max_val / 127.0
    quantized = np.round(weights / scale).astype(np.int8)
    return quantized, scale

# Knowledge distillation
def distill_loss(student_logits, teacher_logits, labels, T=4.0, alpha=0.7):
    soft_targets = F.softmax(teacher_logits / T, dim=1)
    student_soft = F.log_softmax(student_logits / T, dim=1)
    distill = F.kl_div(student_soft, soft_targets, reduction='batchmean') * (T ** 2)
    student_hard = F.log_softmax(student_logits, dim=1)
    hard_loss = F.nll_loss(student_hard, labels)
    return alpha * distill + (1 - alpha) * hard_loss
```

**Key Interview Questions:**
1. Compare structured vs unstructured pruning. When does each give speedup?
2. Explain PTQ vs QAT. Why does QAT produce better INT4 results?
3. Derive the knowledge distillation loss. Why multiply by T²?
4. What is the lottery ticket hypothesis? How do you find winning tickets?
5. How can pruning, quantization, and distillation be combined in a deployment pipeline?

**Company Focus:** Apple (on-device ML, CoreML), Qualcomm (SNPE), Google (TensorFlow Lite, MediaPipe), NVIDIA (TensorRT).

**Common Pitfalls:**
- Pruning too aggressively in one shot (accuracy may drop permanently)
- Not fine-tuning after quantization to recover accuracy
- Forgetting to calibrate quantization ranges with representative data
- Using temperature T that is too high (distribution becomes uniform, loses information)
- Applying distillation before pruning (distillation should be part of training, not post-hoc)

---

## Interview Prep Summary Table

| Lab | Topic | Key Algorithm | Most Asked Question |
|-----|-------|---------------|-------------------|
| 01 | Perceptron | Rosenblatt update | Convergence theorem proof |
| 02 | MLP | Forward/backward pass | Derive backpropagation |
| 03 | Backprop | Chain rule, autodiff | Reverse mode vs forward mode |
| 04 | Regularization | Dropout, BatchNorm | L1 vs L2 sparsity |
| 05 | Optimizers | Adam, AdamW | Derive Adam update |
| 06 | Architecture | ResNet, DenseNet | Why skip connections work |
| 07 | Fashion MNIST | Full pipeline | Architecture selection |
| 08 | CIFAR-10 | CNN + augmentation | Data augmentation strategies |
| 09 | Transfer Learning | Fine-tuning | Freeze vs fine-tune decisions |
| 10 | Model Compression | Pruning, Quantization | PTQ vs QAT comparison |

---

## Recommended Study Order

1. Complete all 10 micro-labs sequentially
2. For each lab, implement the core algorithm from scratch (no PyTorch/TF)
3. After implementing from scratch, re-implement using PyTorch/TF
4. Practice interview questions aloud — explain concepts as if to a colleague
5. Run experiments: change hyperparameters, observe effects, document findings
6. Read 1–2 original papers per lab topic (linked above)

## Company-Specific Focus

| Company | Focus Areas |
|---------|------------|
| Google | Batch norm, architecture design, transfer learning |
| Meta/FAIR | Skip connections, normalization, optimizers |
| OpenAI | Large model training, AdamW, knowledge distillation |
| DeepMind | Backpropagation theory, gradient analysis |
| Tesla | Quantization, pruning, inference optimization |
| Apple | Model compression, on-device deployment |
| NVIDIA | Quantization, kernel optimization, mixed precision |
| Microsoft | Transfer learning, multi-task learning, Azure deployment |
| Amazon | ML pipeline design, model serving, regularization |
| Anthropic | Optimization theory, gradient analysis, safety |

---

*End of Neural Networks Academy Guide*
