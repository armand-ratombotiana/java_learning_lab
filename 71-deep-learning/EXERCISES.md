# Exercises: Deep Learning Fundamentals

Hands-on exercises from beginner to advanced.

---

## Exercise 1: Your First Neural Network (Beginner)

**Goal**: Build a simple FNN to classify MNIST digits.

**Steps**:
1. Load MNIST dataset using torchvision
2. Build a simple network: 784 → 256 → 10
3. Train for 5 epochs
4. Evaluate accuracy

**Template**:
```python
import torch
import torch.nn as nn
import torch.optim as optim
from torchvision import datasets, transforms
from torch.utils.data import DataLoader

# 1. Load data
transform = transforms.Compose([transforms.ToTensor()])
train_data = datasets.MNIST('./data', train=True, download=True, transform=transform)
train_loader = DataLoader(train_data, batch_size=64, shuffle=True)

# 2. Define model
class SimpleNN(nn.Module):
    def __init__(self):
        super().__init__()
        self.fc1 = nn.Linear(784, 256)
        self.fc2 = nn.Linear(256, 10)
    
    def forward(self, x):
        x = x.view(-1, 784)  # Flatten
        x = torch.relu(self.fc1(x))
        return self.fc2(x)

# 3. Training loop
model = SimpleNN()
criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(model.parameters(), lr=0.001)

for epoch in range(5):
    for images, labels in train_loader:
        outputs = model(images)
        loss = criterion(outputs, labels)
        optimizer.zero_grad()
        loss.backward()
        optimizer.step()
    print(f"Epoch {epoch+1} complete")
```

**Bonus**: Add dropout and see effect on training.

---

## Exercise 2: Build a CNN (Beginner)

**Goal**: Classify CIFAR-10 with CNN.

**Architecture**:
```
Conv(32, 3) → ReLU → Pool(2)
Conv(64, 3) → ReLU → Pool(2)
Conv(128, 3) → ReLU → Pool(2)
Flatten → FC(128) → FC(10)
```

**Expected accuracy**: >70%

---

## Exercise 3: Implement Backpropagation (Intermediate)

**Goal**: Manually compute gradients for simple network.

**Network**: 2 inputs → 3 hidden → 1 output

```python
# Forward pass
x = [1.0, 0.5]  # Input
w1 = [[0.1, 0.2, 0.3], [0.4, 0.5, 0.6]]  # Input to hidden
w2 = [0.1, 0.2, 0.3]  # Hidden to output
b1 = [0.1, 0.1, 0.1]
b2 = 0.1

# Compute forward pass and manually calculate backprop gradients
# Compare with PyTorch's autograd
```

---

## Exercise 4: Data Augmentation (Intermediate)

**Goal**: Improve model with data augmentation.

**Tasks**:
1. Add random horizontal flip
2. Add random rotation
3. Add random crop
4. Add color jitter
5. Train with and without augmentation
6. Compare validation accuracy

```python
train_transform = transforms.Compose([
    transforms.RandomHorizontalFlip(),
    transforms.RandomRotation(15),
    transforms.RandomCrop(32, padding=4),
    transforms.ColorJitter(brightness=0.2, contrast=0.2),
    transforms.ToTensor(),
    transforms.Normalize([0.5], [0.5])
])
```

---

## Exercise 5: Implement Optimizer (Advanced)

**Goal**: Implement Adam optimizer from scratch.

**Steps**:
1. Initialize: m = 0, v = 0, t = 0
2. For each parameter:
   - g = gradient
   - t += 1
   - m = β₁*m + (1-β₁)*g  (first moment)
   - v = β₂*v + (1-β₂)*g²  (second moment)
   - m_hat = m / (1-β₁^t)
   - v_hat = v / (1-β₂^t)
   - θ = θ - α * m_hat / (√v_hat + ε)

```python
class MyAdam:
    def __init__(self, params, lr=0.001, betas=(0.9, 0.999)):
        self.params = list(params)
        self.lr = lr
        self.beta1, self.beta2 = betas
        self.m = [torch.zeros_like(p) for p in self.params]
        self.v = [torch.zeros_like(p) for p in self.params]
        self.t = 0
    
    def step(self):
        self.t += 1
        for i, p in enumerate(self.params):
            if p.grad is None:
                continue
            g = p.grad
            self.m[i] = self.beta1 * self.m[i] + (1-self.beta1) * g
            self.v[i] = self.beta2 * self.v[i] + (1-self.beta2) * g**2
            m_hat = self.m[i] / (1-self.beta1**self.t)
            v_hat = self.v[i] / (1-self.beta2**self.t)
            p.data -= self.lr * m_hat / (v_hat**0.5 + 1e-8)
```

---

## Exercise 6: Build Autoencoder (Intermediate)

**Goal**: Compress and reconstruct images.

**Architecture**:
- Encoder: 784 → 128 → 32 (bottleneck)
- Decoder: 32 → 128 → 784

```python
class Autoencoder(nn.Module):
    def __init__(self):
        super().__init__()
        self.encoder = nn.Sequential(
            nn.Linear(784, 128),
            nn.ReLU(),
            nn.Linear(128, 32)
        )
        self.decoder = nn.Sequential(
            nn.Linear(32, 128),
            nn.ReLU(),
            nn.Linear(128, 784),
            nn.Sigmoid()
        )
    
    def forward(self, x):
        compressed = self.encoder(x)
        reconstructed = self.decoder(compressed)
        return reconstructed
```

**Task**: Train and visualize reconstruction quality.

---

## Exercise 7: Implement Dropout (Intermediate)

**Goal**: Implement dropout from scratch.

```python
class MyDropout(nn.Module):
    def __init__(self, p=0.5):
        super().__init__()
        self.p = p
    
    def forward(self, x):
        if self.training:
            mask = (torch.rand(x.size()) > self.p) / (1 - self.p)
            return x * mask
        return x
```

---

## Exercise 8: Build RNN from Scratch (Advanced)

**Goal**: Implement basic RNN cell.

```python
class RNNCell(nn.Module):
    def __init__(self, input_size, hidden_size):
        super().__init__()
        self.wh = nn.Linear(hidden_size, hidden_size)
        self.wx = nn.Linear(input_size, hidden_size)
    
    def forward(self, x, h_prev):
        h = torch.tanh(self.wx(x) + self.wh(h_prev))
        return h
```

**Task**: Build multi-step RNN and train on sequence data.

---

## Exercise 9: Implement Attention (Advanced)

**Goal**: Build self-attention mechanism.

```python
class SelfAttention(nn.Module):
    def __init__(self, embed_size):
        super().__init__()
        self.query = nn.Linear(embed_size, embed_size)
        self.key = nn.Linear(embed_size, embed_size)
        self.value = nn.Linear(embed_size, embed_size)
    
    def forward(self, x):
        Q = self.query(x)
        K = self.key(x)
        V = self.value(x)
        
        scores = torch.matmul(Q, K.transpose(-2, -1))
        attention = torch.softmax(scores / (K.size(-1)**0.5), dim=-1)
        return torch.matmul(attention, V)
```

---

## Exercise 10: Transfer Learning (Intermediate)

**Goal**: Use pre-trained ResNet for new task.

```python
import torchvision.models as models

# Load pre-trained model
model = models.resnet18(pretrained=True)

# Freeze all layers
for param in model.parameters():
    param.requires_grad = False

# Replace final layer
model.fc = nn.Linear(512, 10)

# Fine-tune only final layer
optimizer = optim.Adam(model.fc.parameters(), lr=0.001)
```

---

## Exercise 11: Build GAN (Advanced)

**Goal**: Generate fake MNIST digits.

```python
class Generator(nn.Module):
    def __init__(self):
        super().__init__()
        self.net = nn.Sequential(
            nn.Linear(100, 256),
            nn.ReLU(),
            nn.Linear(256, 784),
            nn.Tanh()
        )
    
    def forward(self, z):
        return self.net(z)

class Discriminator(nn.Module):
    def __init__(self):
        super().__init__()
        self.net = nn.Sequential(
            nn.Linear(784, 256),
            nn.LeakyReLU(0.2),
            nn.Linear(256, 1),
            nn.Sigmoid()
        )
    
    def forward(self, x):
        return self.net(x)
```

**Task**: Train both networks adversarially.

---

## Exercise 12: Distributed Training (Advanced)

**Goal**: Train across multiple GPUs.

```python
# Data Parallel
model = nn.DataParallel(model)
output = model(input)

# Or Distributed Data Parallel
torch.distributed.init_process_group("nccl")
model = nn.parallel.DistributedDataParallel(model)
```

---

## Exercise 13: Custom Loss Function (Intermediate)

**Goal**: Implement custom loss for specific task.

```python
class CustomLoss(nn.Module):
    def __init__(self):
        super().__init__()
    
    def forward(self, pred, target):
        # Example: Weighted MSE with L1 regularization
        mse = (pred - target).pow(2).mean()
        l1 = pred.abs().sum()
        return mse + 0.01 * l1
```

---

## Exercise 14: Learning Rate Scheduler (Intermediate)

**Goal**: Implement various schedulers.

```python
# Step decay
scheduler = optim.lr_scheduler.StepLR(optimizer, step_size=10, gamma=0.1)

# Reduce on plateau
scheduler = optim.lr_scheduler.ReduceLROnPlateau(optimizer, mode='min')

# Cosine annealing
scheduler = optim.lr_scheduler.CosineAnnealingLR(optimizer, T_max=50)
```

---

## Exercise 15: Build Transformer Encoder (Advanced)

**Goal**: Build complete transformer block.

```python
class TransformerBlock(nn.Module):
    def __init__(self, embed_size, heads):
        super().__init__()
        self.attention = MultiHeadAttention(embed_size, heads)
        self.norm1 = nn.LayerNorm(embed_size)
        self.norm2 = nn.LayerNorm(embed_size)
        self.ff = nn.Sequential(
            nn.Linear(embed_size, 4*embed_size),
            nn.ReLU(),
            nn.Linear(4*embed_size, embed_size)
        )
    
    def forward(self, x):
        attn_out = self.attention(x)
        x = self.norm1(x + attn_out)
        ff_out = self.ff(x)
        return self.norm2(x + ff_out)
```

---

## Solutions

### Exercise 1: Basic training loop
```python
# Run the template code - expected to work with basic corrections
# Key: proper tensor shapes, correct loss function
```

### Exercise 4: Compare accuracy
With augmentation: expect ~5-10% improvement in validation accuracy

### Exercise 5: Compare with Adam
Your Adam should match PyTorch's Adam within numerical tolerance

---

## Next Steps

- Review [QUICK_REFERENCE.md](./QUICK_REFERENCE.md) for formulas
- Study [DEEP_DIVE.md](./DEEP_DIVE.md) for theory
- Practice with real datasets