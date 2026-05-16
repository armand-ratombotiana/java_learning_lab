# CNN (Convolutional Neural Networks) - FLASHCARDS

### Card 1
**Q:** What is the purpose of convolution in CNN?
**A:** To extract local features by applying learnable filters that detect edges, textures, patterns

### Card 2
**Q:** Formula for convolution output size?
**A:** O = (W - K + 2P) / S + 1, where W=input, K=kernel, P=padding, S=stride

### Card 3
**Q:** Why use padding in convolution?
**A:** Preserves spatial dimensions, enables deeper networks, avoids information loss at edges

### Card 4
**Q:** What does max pooling do?
**A:** Downsamples by taking max value in each window, provides translation invariance, reduces computation

### Card 5
**Q:** Why is ReLU preferred over sigmoid in CNN?
**A:** Avoids vanishing gradient, faster computation, sparse activation

### Card 6
**Q:** What problem does ResNet solve?
**A:** Degradation problem - adding more layers increases training error; residual connections enable training of very deep networks

### Card 7
**Q:** What is a skip connection in ResNet?
**A:** Direct connection from input to output: F(x) + x, allowing gradient flow through the network

### Card 8
**Q:** What is batch normalization and why use it?
**A:** Normalizes layer inputs to have zero mean and unit variance; stabilizes training, enables higher learning rates, reduces internal covariate shift

### Card 9
**Q:** How does data augmentation help CNN training?
**A:** Increases effective dataset size, reduces overfitting, makes model robust to variations (rotation, brightness, etc.)

### Card 10
**Q:** What is the difference between valid and same padding?
**A:** Valid: no padding, output shrinks. Same: padding to keep output same size as input

### Card 11
**Q:** What is a feature map in CNN?
**A:** Output of a convolution layer - a 2D grid of neuron activations from one filter

### Card 12
**Q:** Why are CNNs translation invariant?
**A:** Pooling and shared weights allow detection of features regardless of position

**Total: 12 flashcards**