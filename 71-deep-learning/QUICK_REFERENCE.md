# 📌 Deep Learning - Quick Reference

---

## Activation Functions

| Function | Formula | Range | Use Case |
|----------|---------|-------|----------|
| **Sigmoid** | σ(x) = 1/(1+e^(-x)) | (0,1) | Binary output |
| **Tanh** | (e^x-e^(-x))/(e^x+e^(-x)) | (-1,1) | Hidden layers |
| **ReLU** | max(0,x) | [0,∞) | Most common |
| **Leaky ReLU** | max(0.01x, x) | (-∞,∞) | Prevent dying ReLU |
| **Softmax** | e^(xᵢ)/Σe^(xⱼ) | (0,1), Σ=1 | Multi-class output |
| **Swish** | x·σ(x) | (-∞,∞) | Modern choice |

---

## Network Architectures

```
FNN:    Input → FC → FC → Output
CNN:    Input → Conv → Pool → Conv → FC → Output
RNN:    Input → (RNN)ⁿ → Output
LSTM:   Input → (LSTM)ⁿ → Output
Transformer: Input → Embedding → (MultiHead + FF)ⁿ → Output
```

---

## Loss Functions

| Task | Loss Function |
|------|---------------|
| Regression | MSE, MAE, Huber |
| Binary Classification | Binary Cross-Entropy |
| Multi-class | Cross-Entropy |
| Multi-label | Sigmoid BCE |

---

## Optimizers

| Optimizer | Update Rule | Best For |
|-----------|-------------|----------|
| **SGD** | θ = θ - η∇J | Classic, large datasets |
| **Momentum** | v = βv + η∇J; θ = θ - v | Faster convergence |
| **Adam** | m̂ = m/(1-β₁ᵗ), v̂ = v/(1-β₂ᵗ) | Default choice |
| **RMSprop** | E[g²] = γE[g²] + (1-γ)g² | RNNs |

---

## Regularization Techniques

| Technique | Purpose | Typical Value |
|-----------|---------|----------------|
| **L2 (Weight Decay)** | Penalize large weights | 1e-4 to 1e-2 |
| **Dropout** | Prevent co-adaptation | 0.1 to 0.5 |
| **Early Stopping** | Prevent overfitting | patience=5-10 |
| **Batch Norm** | Stabilize training | - |
| **Data Augmentation** | Increase diversity | task-specific |

---

## Hyperparameters

| Parameter | Typical Range | Effect |
|-----------|---------------|--------|
| **Learning Rate** | 1e-4 to 1e-2 | Step size |
| **Batch Size** | 16 to 512 | GPU memory, convergence |
| **Epochs** | 10 to 1000 | Training duration |
| **Hidden Units** | 32 to 1024 | Model capacity |
| **Dropout** | 0.1 to 0.5 | Regularization |
| **Weight Decay** | 1e-5 to 1e-2 | L2 penalty |

---

## CNN Components

```
Convolution:    Output = W - K + 2P / S + 1
Pooling:       Max (extract features) / Average (smooth)
BN:             (x - μ) / √(σ² + ε) * γ + β
```

---

## RNN Variants

| Variant | Feature | Use Case |
|---------|---------|----------|
| **Basic RNN** | Simple recurrence | Short sequences |
| **LSTM** | Cell, gate mechanisms | Long sequences |
| **GRU** | Simplified LSTM | Efficient, similar performance |

---

## Transformer Components

- **Self-Attention**: Q·K^T / √d · V
- **Multi-Head**: Parallel attention
- **Positional Encoding**: Sin/Cos or learned
- **LayerNorm**: Normalize over features

---

## Training Checklist

```
1. ✓ Data normalized (0 mean, 1 std)
2. ✓ Right input shape (batch, ...)
3. ✓ Loss matches task
4. ✓ LR reasonable (1e-3 default)
5. ✓ Model size appropriate
6. ✓ Gradient clipping (max_norm=1)
7. ✓ Train/eval mode correct
8. ✓ Zero gradients before backward
9. ✓ Validation monitoring
10. ✓ Model saving
```

---

## Debugging

| Problem | Solution |
|---------|----------|
| **NaN loss** | Reduce LR, check data, clip gradients |
| **No learning** | Increase LR, check gradient flow |
| **Overfitting** | Add dropout, regularize, augment data |
| **Slow** | Use GPU, mixed precision, optimize data loading |
| **GPU OOM** | Reduce batch size |

---

## Framework Quick Commands

### PyTorch
```python
model = nn.Sequential(nn.Linear(10, 5), nn.ReLU())
optimizer = torch.optim.Adam(model.parameters(), lr=0.001)
loss = nn.CrossEntropyLoss()
```

### TensorFlow/Keras
```python
model = tf.keras.Sequential([tf.keras.layers.Dense(5)])
model.compile(optimizer='adam', loss='categorical_crossentropy')
```

---

## Model Evaluation Metrics

| Task | Metrics |
|------|---------|
| **Classification** | Accuracy, Precision, Recall, F1, AUC |
| **Regression** | MSE, RMSE, MAE, R² |
| **Segmentation** | IoU, Dice |
| **Detection** | mAP |

---

## Key Formulas

**Forward Pass**:
```
y = activation(Wx + b)
```

**Gradient Descent**:
```
θ = θ - η∇J(θ)
```

**Cross-Entropy**:
```
L = -Σy*log(ŷ)
```

**Attention**:
```
Attention(Q,K,V) = softmax(QK^T/√d_k)V
```

---

## Memory Optimization

- **Mixed Precision**: torch.cuda.amp
- **Gradient Accumulation**: Simulate large batch
- **Gradient Checkpointing**: Recompute activations
- **Model Parallelism**: Split across GPUs

---

## Pre-trained Models

| Model | Task | ImageNet Top-1 |
|-------|------|----------------|
| ResNet-50 | Classification | 76.2% |
| VGG-16 | Classification | 71.5% |
| EfficientNet-B7 | Classification | 84.3% |
| BERT-base | NLP | - |
| GPT-2 | Language | - |

---

## Learning Rate Schedulers

- **Step**: LR = LR × γ every N epochs
- **Plateau**: Reduce on plateau
- **Cosine**: LR = 0.5 × (1 + cos(π × t/T))
- **Warmup**: Gradually increase then decay

---

## Common Architectures

```
ImageNet Timeline:
LeNet (1998) → AlexNet (2012) → VGG (2014) → 
ResNet (2015) → DenseNet (2016) → EfficientNet (2019)

NLP Timeline:
Word2Vec (2013) → LSTM/GRU (2014) → 
Attention (2015) → Transformer (2017) → 
BERT (2018) → GPT (2018-2020)
```

---

## Useful Libraries

```python
# Data
torchvision, tf.keras.preprocessing, albumentations

# Training
pytorch-lightning, catalyst, ignite

# Hugging Face
transformers, diffusers, accelerate

# Experiment Tracking
MLflow, Weights & Biases, TensorBoard
```

---

## Glossary

| Term | Definition |
|------|------------|
| **Epoch** | One full pass through data |
| **Batch** | Samples processed together |
| **Gradient** | Derivative of loss w.r.t. weights |
| **Backprop** | Computing gradients |
| **Tensor** | Multi-dimensional array |
| **Convolution** | Sliding filter operation |
| **Attention** | Weighted sum based on relevance |
| **Fine-tuning** | Continuing training on new task |
| **Transfer** | Using pre-trained model features |

---

**Remember**: "Deep learning is 10% deep, 90% debugging!"