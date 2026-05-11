# Deep Learning Resources

Reference materials for deep learning fundamentals.

## Contents

- [Neural Network Architecture](./nn-architecture.md) - Visual diagrams of NN types
- [Official Documentation](#official-documentation)
- [Key Concepts](#key-concepts)

---

## Official Documentation

| Topic | Link |
|-------|------|
| Deep Learning Book | https://www.deeplearningbook.org/ |
| Stanford CS231n | http://cs231n.stanford.edu/ |
| PyTorch Docs | https://pytorch.org/docs/ |
| TensorFlow Guide | https://www.tensorflow.org/guide |
| NeurIPS Papers | https://papers.nips.cc/ |

---

## Key Concepts

### Network Architectures
| Type | Best For |
|------|----------|
| Feedforward (MLP) | Tabular data, simple patterns |
| CNN | Images, spatial data |
| RNN/LSTM/GRU | Sequences, time series, NLP |
| Transformer | Long sequences, NLP, attention |
| GAN | Generative tasks |
| Graph NN | Graph-structured data |

### Activation Functions
| Function | Formula | Use Case |
|----------|---------|----------|
| Sigmoid | 1/(1+e⁻ˣ) | Binary output |
| Tanh | (eˣ-e⁻ˣ)/(eˣ+e⁻ˣ) | Hidden layers |
| ReLU | max(0,x) | Default hidden |
| Leaky ReLU | max(0.01x,x) | Dying ReLU |
| Softmax | eˣⁱ/Σeˣʲ | Multi-class output |

### Loss Functions
| Task | Loss |
|------|------|
| Regression | MSE, MAE |
| Binary Classification | Binary Cross-Entropy |
| Multi-class | Categorical Cross-Entropy |
| Generative | GAN Loss, VAE Loss |

### Optimizers
| Optimizer | Pros | Cons |
|-----------|------|------|
| SGD | Simple, good generalization | Slow, sensitive to LR |
| Adam | Fast convergence | May overfit |
| RMSprop | Good for RNNs | Requires tuning |

### Key Techniques
1. **Batch Normalization** - Stabilize training
2. **Dropout** - Regularization
3. **Learning Rate Scheduling** - Adaptive LR
4. **Early Stopping** - Prevent overfitting
5. **Gradient Clipping** - Prevent exploding gradients

### Java Frameworks
- **Deeplearning4j (DL4J)** - Main Java DL framework
- **Tribuo** - ML library (Oracle)
- **DJL (Deep Java Library)** - AWS, supports PyTorch/TensorFlow backends
