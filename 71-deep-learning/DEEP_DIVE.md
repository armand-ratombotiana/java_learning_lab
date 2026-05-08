# Deep Dive: Deep Learning Fundamentals

A comprehensive guide to neural networks, from basics to advanced architectures.

---

## 1. Neural Network Foundations

### 1.1 What is Deep Learning?

Deep Learning is a subset of Machine Learning that uses **artificial neural networks** with multiple layers (hence "deep") to learn representations of data.

```
Traditional ML:  Feature Engineering → Model → Prediction
Deep Learning:   Raw Data → Neural Network → Automatic Feature Learning → Prediction
```

### 1.2 The Neuron (Perceptron)

The fundamental unit of a neural network:

```python
# Mathematical representation of a neuron
y = activation(w1*x1 + w2*x2 + ... + wn*xn + bias)
```

Where:
- `x` = input features
- `w` = weights (learnable parameters)
- `bias` = learned offset
- `activation` = non-linear function

### 1.3 Activation Functions

#### Sigmoid
```
σ(x) = 1 / (1 + e^(-x))
```
- Range: (0, 1)
- Used in: Output layer for binary classification
- Problem: Vanishing gradients

#### Tanh
```
tanh(x) = (e^x - e^(-x)) / (e^x + e^(-x))
```
- Range: (-1, 1)
- Zero-centered
- Problem: Still has vanishing gradient

#### ReLU (Rectified Linear Unit)
```
f(x) = max(0, x)
```
- Range: [0, ∞)
- Most popular activation
- Problem: Dying ReLU (neurons die during training)

#### Leaky ReLU
```
f(x) = max(0.01*x, x)
```
- Allows small gradient when x < 0

#### Softmax
```
σ(x)i = e^(xi) / Σ(e^(xj))
```
- Used in output layer for multi-class classification
- Outputs sum to 1 (probabilities)

---

## 2. Network Architecture

### 2.1 Feedforward Neural Network (FNN)

```
Input Layer → Hidden Layer(s) → Output Layer
```

```python
class FeedforwardNN(nn.Module):
    def __init__(self, input_size, hidden_size, output_size):
        super().__init__()
        self.layer1 = nn.Linear(input_size, hidden_size)
        self.layer2 = nn.Linear(hidden_size, output_size)
        self.relu = nn.ReLU()
    
    def forward(self, x):
        x = self.relu(self.layer1(x))
        x = self.layer2(x)
        return x
```

### 2.2 Convolutional Neural Network (CNN)

Specialized for processing grid-like data (images):

```
Input → Conv Layer → Pooling → Conv → Pooling → Flatten → FC → Output
```

#### Key Components:

**Convolutional Layer:**
- Filter/Kernel slides over input
- Performs dot product at each position
- Creates feature map

**Pooling Layer:**
- Reduces spatial dimensions
- Max pooling: takes maximum value in window
- Average pooling: takes average value

**Common Architectures:**
- LeNet-5 (1998): First CNN
- AlexNet (2012): Deep CNN breakthrough
- VGG-16 (2014): Very deep network
- ResNet (2015): Skip connections
- EfficientNet (2019): Efficient scaling

### 2.3 Recurrent Neural Network (RNN)

Processes sequential data:

```
x1 → [RNN] → h1 → [RNN] → h2 → [RNN] → h3 → Output
 |         |         |
 x2        x3        x4
```

**Problems:**
- Vanishing gradients
- Can't handle long sequences

**Solutions:**
- LSTM (Long Short-Term Memory)
- GRU (Gated Recurrent Unit)

### 2.4 Transformer Architecture

The foundation of modern NLP:

```
Input Embedding → Positional Encoding → Encoder/Decoder → Output
```

Key components:
- Self-Attention mechanism
- Multi-head attention
- Feed-forward networks
- Residual connections & LayerNorm

---

## 3. Training Neural Networks

### 3.1 Loss Functions

**Mean Squared Error (MSE):**
```python
MSE = (1/n) * Σ(y_true - y_pred)²
```
Used for regression

**Cross-Entropy Loss:**
```python
CE = -Σ y_true * log(y_pred)
```
Used for classification

**Binary Cross-Entropy:**
```python
BCE = -[y * log(p) + (1-y) * log(1-p)]
```
Used for binary classification

### 3.2 Optimization Algorithms

#### Gradient Descent
```
θ = θ - α * ∇θ J(θ)
```
Where α = learning rate

#### Stochastic Gradient Descent (SGD)
```python
# Update after each sample
for i in range(n_samples):
    loss = compute_loss(x[i], y[i])
    loss.backward()
    optimizer.step()
```

#### Adam (Adaptive Moment Estimation)
```python
optimizer = torch.optim.Adam(model.parameters(), lr=0.001)
```
Combines:
- Momentum (accelerates)
- RMSProp (adaptive learning rates)

### 3.3 Backpropagation

The algorithm for training neural networks:

```
1. Forward pass: Compute predictions
2. Compute loss: Compare predictions to ground truth
3. Backward pass: Compute gradients
4. Update weights: θ = θ - η * gradient
```

### 3.4 Hyperparameters

| Parameter | Typical Values | Effect |
|-----------|---------------|--------|
| Learning Rate | 0.001, 0.01 | Step size in gradient descent |
| Batch Size | 32, 64, 128 | Samples per update |
| Epochs | 10, 100, 1000 | Complete dataset passes |
| Hidden Layers | 1-10 | Network depth |
| Hidden Units | 64-1024 | Layer width |
| Dropout | 0.1-0.5 | Regularization rate |

---

## 4. Regularization

### 4.1 L1/L2 Regularization

**L1 (Lasso):** Adds sum of absolute weights to loss
**L2 (Ridge):** Adds sum of squared weights to loss

```python
# L2 in PyTorch
optimizer = torch.optim.Adam(model.parameters(), 
                               lr=0.001, 
                               weight_decay=0.01)
```

### 4.2 Dropout

Randomly "drops" neurons during training:

```python
self.dropout = nn.Dropout(p=0.5)
```

### 4.3 Early Stopping

Stop training when validation loss increases.

### 4.4 Data Augmentation

Increase training data variety:
- Image: rotation, flip, crop, color jitter
- Text: synonym replacement, back-translation
- Audio: noise addition, time stretching

---

## 5. Advanced Architectures

### 5.1 Autoencoders

Unsupervised learning for dimensionality reduction:

```
Input → Encoder → Latent Space → Decoder → Output
```

Types:
- Vanilla Autoencoder
- Variational Autoencoder (VAE)
- Denoising Autoencoder
- Sparse Autoencoder

### 5.2 Generative Adversarial Networks (GANs)

```
Generator: Creates fake samples
Discriminator: Distinguishes real vs fake

Training: Min-max game until Generator fool Discriminator
```

### 5.3 Attention Mechanisms

Self-attention computes relationship between all positions:

```python
# Simplified attention
attention_scores = Q @ K.transpose() / sqrt(d_k)
attention_weights = softmax(attention_scores)
output = attention_weights @ V
```

### 5.4 Transfer Learning

Use pre-trained models for new tasks:

```python
# Using pre-trained ResNet
model = torchvision.models.resnet50(pretrained=True)
# Replace final layer for new classification
model.fc = nn.Linear(2048, num_classes)
```

---

## 6. Computer Vision

### 6.1 Image Classification

```python
# CNN for image classification
class ImageClassifier(nn.Module):
    def __init__(self, num_classes=10):
        super().__init__()
        self.conv1 = nn.Conv2d(3, 32, 3, padding=1)
        self.pool = nn.MaxPool2d(2, 2)
        self.fc = nn.Linear(32 * 16 * 16, num_classes)
    
    def forward(self, x):
        x = self.pool(torch.relu(self.conv1(x)))
        x = x.view(-1, 32 * 16 * 16)
        return self.fc(x)
```

### 6.2 Object Detection

**Two-stage detectors:**
- R-CNN series: Slower but more accurate

**One-stage detectors:**
- YOLO: Fast, real-time
- SSD: Balance of speed and accuracy

### 6.3 Semantic Segmentation

Pixel-level classification:

```
U-Net: Encoder-Decoder with Skip Connections
```

### 6.4 Face Recognition

- FaceNet: Embedding-based
- ArcFace: Additive Angular Margin Loss

---

## 7. Natural Language Processing

### 7.1 Word Embeddings

**Word2Vec:**
- CBOW: Predict word from context
- Skip-gram: Predict context from word

**GloVe:** Global Vectors (count-based + prediction)

**FastText:** Subword embeddings

### 7.2 Sequence-to-Seq Models

```
Encoder → Context Vector → Decoder
```

### 7.3 BERT (Bidirectional Encoder Representations)

Pre-trained language model:

```python
from transformers import BertModel
model = BertModel.from_pretrained('bert-base-uncased')
```

### 7.4 GPT (Generative Pre-trained Transformer)

Autoregressive language model:

- GPT-1, GPT-2, GPT-3, GPT-4
- ChatGPT: Fine-tuned for conversation

---

## 8. Reinforcement Learning

### 8.1 Agent-Environment Interaction

```
Agent takes Action → Environment gives Reward & New State → Agent learns
```

### 8.2 Q-Learning

```python
# Q-learning update
Q(s, a) = Q(s, a) + α * (reward + γ * max(Q(s', a')) - Q(s, a))
```

### 8.3 Deep Q-Network (DQN)

```python
# Neural network approximates Q-function
class DQN(nn.Module):
    def forward(self, state):
        return self.fc(torch.relu(self.conv(state)))
```

### 8.4 Policy Gradients

Optimize policy directly:
- REINFORCE
- Actor-Critic (A2C/A3C)
- PPO (Proximal Policy Optimization)

---

## 9. Implementation Examples

### 9.1 PyTorch CNN for MNIST

```python
import torch
import torch.nn as nn
import torch.optim as optim

class CNN(nn.Module):
    def __init__(self):
        super().__init__()
        self.conv1 = nn.Conv2d(1, 32, 3, padding=1)
        self.conv2 = nn.Conv2d(32, 64, 3, padding=1)
        self.pool = nn.MaxPool2d(2, 2)
        self.fc1 = nn.Linear(64 * 7 * 7, 128)
        self.fc2 = nn.Linear(128, 10)
        self.dropout = nn.Dropout(0.5)
    
    def forward(self, x):
        x = self.pool(torch.relu(self.conv1(x)))
        x = self.pool(torch.relu(self.conv2(x)))
        x = x.view(-1, 64 * 7 * 7)
        x = self.dropout(torch.relu(self.fc1(x)))
        return self.fc2(x)

# Training
model = CNN()
criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(model.parameters(), lr=0.001)

for epoch in range(10):
    for images, labels in train_loader:
        outputs = model(images)
        loss = criterion(outputs, labels)
        optimizer.zero_grad()
        loss.backward()
        optimizer.step()
```

### 9.2 TensorFlow Keras CNN

```python
import tensorflow as tf
from tensorflow.keras import layers, models

model = models.Sequential([
    layers.Conv2D(32, (3, 3), activation='relu', input_shape=(28, 28, 1)),
    layers.MaxPooling2D((2, 2)),
    layers.Conv2D(64, (3, 3), activation='relu'),
    layers.MaxPooling2D((2, 2)),
    layers.Flatten(),
    layers.Dense(64, activation='relu'),
    layers.Dense(10, activation='softmax')
])

model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])

model.fit(train_images, train_labels, epochs=10)
```

### 9.3 Using Pre-trained Model

```python
# Transfer learning with PyTorch
from torchvision import models

# Load pre-trained ResNet
model = models.resnet50(pretrained=True)

# Freeze all layers
for param in model.parameters():
    param.requires_grad = False

# Replace final layer
model.fc = nn.Linear(512, num_classes)

# Fine-tune
optimizer = optim.Adam(model.fc.parameters(), lr=0.001)
```

---

## 10. Best Practices

### 10.1 Data Preparation
- Normalize/standardize inputs
- Handle missing values
- Data augmentation for more training data
- Proper train/validation/test split

### 10.2 Model Selection
- Start simple, then increase complexity
- Use pre-trained models when possible
- Consider computational constraints

### 10.3 Training
- Monitor training/validation loss
- Use learning rate scheduling
- Implement early stopping
- Save best model checkpoint

### 10.4 Evaluation
- Use appropriate metrics (accuracy, F1, AUC, etc.)
- Cross-validation for robust estimates
- Confusion matrix analysis
- ROC curves for classification

### 10.5 Debugging
- Check data pipeline
- Verify input/output shapes
- Gradient flow monitoring
- Learning rate search

---

## 11. Key Concepts Summary Table

| Concept | Description |
|---------|-------------|
| Neuron | Basic unit that computes weighted sum + activation |
| Layer | Collection of neurons |
| Forward Pass | Computing output from input |
| Backpropagation | Computing gradients for weight updates |
| Loss Function | Measures prediction error |
| Optimizer | Updates weights based on gradients |
| Epoch | One full pass through training data |
| Batch | Subset of data for one update |
| Learning Rate | Step size in gradient descent |
| Regularization | Techniques to prevent overfitting |
| Dropout | Randomly disabling neurons during training |
| Convolution | Sliding filter over input |
| Pooling | Downsampling spatial dimensions |
| Attention | Focusing on relevant parts of input |
| Transformer | Architecture based on self-attention |
| Transfer Learning | Using pre-trained models |
| GAN | Generator vs Discriminator training |

---

## 12. Framework Comparison

| Framework | Pros | Cons |
|-----------|------|------|
| PyTorch | Dynamic graph, Pythonic, research-friendly | Less deployment tools |
| TensorFlow/Keras | Production-ready, TensorBoard | Static graph (eager mode helps) |
| JAX | Functional, fast, auto-differentiation | Less mature ecosystem |
| MXNet | Efficient, multi-language | Smaller community |
| CNTK | Microsoft integration | Less popular |

---

## 13. Hardware & GPU

### CUDA Basics
```python
# Check GPU availability
import torch
print(torch.cuda.is_available())
print(torch.cuda.get_device_name(0))

# Move to GPU
device = torch.device('cuda')
model = model.to(device)
data = data.to(device)
```

### Mixed Precision Training
```python
# FP16 for faster training
scaler = torch.cuda.amp.GradScaler()
with torch.cuda.amp.autocast():
    outputs = model(data)
```

---

## 14. Troubleshooting

| Problem | Solution |
|---------|----------|
| Loss not decreasing | Check learning rate, data normalization |
| Overfitting | Add dropout, regularization, more data |
| Underfitting | Increase model capacity, train longer |
| NaN loss | Check for division by zero, reduce learning rate |
| GPU out of memory | Reduce batch size, gradient accumulation |
| Slow training | Use GPU, mixed precision, optimize data loading |

---

## 15. Next Steps

After mastering these concepts:

1. **Specialize**: Choose a domain (NLP, CV, RL)
2. **Read Papers**: Stay current with arXiv
3. **Implement**: Build projects from scratch
4. **Deploy**: Learn MLOps (MLflow, Kubeflow)
5. **Research**: Contribute to open source

---

**End of Deep Dive** - Continue to [QUIZZES.md](./QUIZZES.md) for assessment