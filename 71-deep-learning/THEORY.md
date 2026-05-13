# Deep Learning Theory

## First Principles

### What is Deep Learning?

Deep Learning is a subset of machine learning that uses artificial neural networks with multiple layers ("deep" architectures) to learn representations of data. It automatically discovers features from raw data rather than requiring manual feature engineering.

**Relationship**:
```
Artificial Intelligence (AI)
    └── Machine Learning (ML)
            └── Deep Learning (DL)
```

**Why Deep Learning?**
- Handles complex patterns traditional algorithms cannot
- Works well with unstructured data (images, text, audio)
- Scales with data - more data = better performance
- End-to-end learning from raw inputs

---

## Neural Network Fundamentals

### The Biological Inspiration

Neurons: Receive signals → Process → Transmit signal

Artificial neuron (perceptron):
```
Inputs (x₁, x₂, ..., xₙ) ──┐
                           ├─→ Σ + activation → Output
Weights (w₁, w₂, ..., wₙ) ──┘
                         ↑
                      Bias (b)
```

**Mathematical Model**:
```
output = activation(Σ(wᵢ × xᵢ) + b)
```

### Activation Functions

Non-linearities enabling networks to learn complex patterns:

| Function | Formula | Use Case |
|----------|---------|----------|
| Sigmoid | σ(x) = 1/(1+e⁻ˣ) | Binary output (0-1) |
| Tanh | tanh(x) | Hidden layers (-1 to 1) |
| ReLU | max(0, x) | Hidden layers (default) |
| Softmax | e^xᵢ/Σe^xⱼ | Multi-class output |

**Why Non-linear?**: Without non-linear activations, stacking layers collapses to single linear transformation - no benefit.

### Network Architecture

```
Input Layer → Hidden Layers → Output Layer
     ↓            ↓              ↓
  Features    Computation    Prediction
```

**Dense (Fully Connected)**:
- Every neuron connects to every neuron in next layer
- Good for tabular data, final layers

**Convolutional**:
- Local connections (kernels/filters)
- Preserves spatial structure
- Good for images

**Recurrent**:
- Connections form cycles
- Sequential data (time-series, text)
- LSTM, GRU variants

---

## Learning: Backpropagation

### The Learning Problem

Network has weights (parameters) to adjust. How do we know which direction to adjust?

**Loss Function**: Measures difference between prediction and actual

| Problem Type | Loss Function |
|--------------|---------------|
| Regression | Mean Squared Error (MSE) |
| Classification | Cross-Entropy |

### Gradient Descent

```
weight = weight - learning_rate × gradient
```

**Intuition**: Like a ball rolling downhill to find minimum (lowest loss):

1. Calculate gradient (direction of steepest increase)
2. Move opposite direction (downhill)
3. Repeat until convergence

### Backpropagation Algorithm

Chain rule for computing gradients through network:

```
Forward Pass:
  Input → (compute) → Hidden → (compute) → Output

Backward Pass (backprop):
  Output Error → (gradient) → Hidden → (gradient) → Input

Update weights using gradients
```

**Key Insight**: Efficiently compute gradient for each weight using chain rule - O(weights) not O(weights²) or worse.

---

## Convolutional Neural Networks (CNN)

### Why CNNs for Images?

**Problem with dense layers for images**:
- 224×224 RGB image = 150,528 inputs
- Dense layer to 4096 neurons = 600M+ parameters
- Overfitting, computationally expensive

**Solution**: Exploit spatial structure with local connections

### Convolution Operation

```
Input Image                    Filter (kernel)
┌────┬────┬────┐             ┌───┬───┐
│ 1  │ 0  │ 1  │        ×    │ 1 │ 0 │  = dot product = 1×1 + 0×0 + 1×1 + ...
├────┼────┼────┤             ├───┼───┤          ┌───┬────┬───┬────┐
│ 0  │ 1  │ 0  │             │ 0 │ 1 │          │ 4 │ 3  │ 4 │ 3  │ ← Feature Map
├────┼────┼────┤             └───┴───┘          ├───┼────┼───┼────┤
│ 1  │ 0  │ 1  │             Stride=1             │ 3 │ 4  │ 3 │ 4  │
└────┴────┴────┘                                  └───┴────┴───┘
```

**What convolutions learn**:
- Early layers: edges, colors, textures
- Middle layers: parts (eyes, wheels)
- Late layers: objects (face, car)

### CNN Components

1. **Convolutional Layer**: Apply learned filters
2. **Pooling**: Reduce spatial size (max, average)
3. **Batch Normalization**: Stabilize training
4. **Dropout**: Prevent overfitting

---

## Transfer Learning

### The Problem

Training large networks from scratch requires:
- Massive datasets (millions of examples)
- Extensive compute (days/weeks on GPUs)
- Expert tuning

### The Solution

Transfer learning leverages pre-trained models:

```
Pre-trained Model (trained on ImageNet)
         ↓
    Remove final layers
         ↓
    Add new layers for your task
         ↓
    Fine-tune on your data (much smaller dataset)
```

**Why it works**: Early layers learn general features (edges, shapes) that transfer across tasks.

### Common Approaches

1. **Feature Extraction**: Use frozen CNN as feature extractor + train classifier
2. **Fine-tuning**: Unfreeze top layers, retrain with low learning rate
3. **Full Fine-tuning**: Retrain entire network (requires more data)

---

## Optimization Theory

### Challenges in Training

**Vanishing Gradients**: Gradients shrink through layers → early layers train slowly
- Solution: Better activations (ReLU), skip connections (ResNet), batch norm

**Overfitting**: Network memorizes training data, fails on new data
- Solutions: More data, dropout, regularization, early stopping

**Local Minima**: Stuck in suboptimal solution
- Solutions: Random initialization, momentum, adaptive learning rates

### Optimizers

| Optimizer | Update Rule | When to Use |
|----------|-------------|-------------|
| SGD | w = w - lr × grad | Classic, interpretable |
| Adam | Adaptive moment | Default choice |
| RMSprop | Adaptive learning rate | Non-stationary objectives |

---

## Why It Works This Way

### Universal Approximation

A neural network with enough neurons can approximate any continuous function. This theoretical result explains why deep learning can solve diverse problems.

### Feature Learning

Traditional ML requires manual feature engineering. Deep learning learns features automatically - from raw pixels to high-level concepts - through gradient-based optimization.

### Scaling Laws

Performance improves with:
- More data
- More compute (bigger models)
- More parameters

This empirical observation drives modern AI - "scale up everything."

---

## Common Misconceptions

1. **"AI is magic"**: It's gradient-based optimization on large-scale linear algebra - mathematically tractable
2. **"More layers always better"**: Diminishing returns, potential overfitting, more compute
3. **"Neural networks think like brains"**: Loose inspiration, actual operation is different
4. **"Deep learning doesn't need data"**: Still requires lots of data, just learns from examples

---

## Frameworks

### Deep Java Library (DJL)

Java's deep learning framework:
- **Multi-engine**: PyTorch, TensorFlow, MXNet, ONNX
- **Intuitive APIs**: Similar to Python frameworks
- **Native Java**: No Python dependency

### Workflow with DJL

```java
// 1. Load model
Model model = Model.newInstance("resnet");
model.setBlock(new ResNetV1(10));

// 2. Prepare data
Image img = ImageFactory.getInstance().fromFile(imagePath);

// 3. Predict
Predictor<Image, Classifications> predictor = model.newPredictor();
Classifications result = predictor.predict(img);
```

---

## Further Theory

### From Here

- **Module 72 (LangChain4j)**: LLMs and AI integration
- **Module 73 (Spring AI)**: AI in Spring applications
- **Module 74 (OpenNLP)**: Classical NLP

### Deep Dive Resources

- **Deep Learning** (Goodfellow, Bengio, Courville): Definitive text
- **CS231n**: Stanford CNN course
- **D2L (Dive into Deep Learning)**: Interactive book