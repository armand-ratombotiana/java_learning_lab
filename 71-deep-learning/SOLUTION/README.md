# Deep Learning with DL4J - Solution

## Overview

This module provides comprehensive deep learning examples using DeepLearning4j (DL4J) and ND4J. It covers neural networks, convolutional neural networks (CNNs), and recurrent neural networks (RNNs/LSTMs).

## Dependencies

```xml
<dependency>
    <groupId>org.deeplearning4j</groupId>
    <artifactId>deeplearning4j-core</artifactId>
    <version>1.0.0-beta3</version>
</dependency>
<dependency>
    <groupId>org.nd4j</groupId>
    <artifactId>nd4j-native-platform</artifactId>
    <version>1.0.0-beta3</version>
</dependency>
```

## Key Concepts

### 1. Neural Networks (Feed-Forward)

The `NeuralNetworkExample` class demonstrates:
- Multi-layer perceptron construction
- Dense layers with activation functions
- Output layers with loss functions
- Model training and evaluation

```java
MultiLayerNetwork network = new NeuralNetworkExample()
    .createSimpleFeedForwardNetwork(inputSize, outputSize);
INDArray output = neuralNetworkExample.forwardPass(network, input);
```

### 2. Convolutional Neural Networks (CNN)

The `ConvolutionalNeuralNetworkExample` class covers:
- Convolutional layers for feature extraction
- MaxPooling layers for downsampling
- Image preprocessing and augmentation
- Feature map analysis

### 3. Recurrent Neural Networks (LSTM)

The `RecurrentNeuralNetworkExample` class provides:
- LSTM layer configuration for sequence modeling
- Time series data generation
- Sequence prediction utilities

## Classes Overview

| Class | Description |
|-------|-------------|
| `NeuralNetworkExample` | Feed-forward neural network examples |
| `ConvolutionalNeuralNetworkExample` | CNN implementation for image tasks |
| `RecurrentNeuralNetworkExample` | LSTM for sequential data |
| `ModelTrainingExample` | Training, evaluation, model persistence |
| `DataProcessingExample` | Data normalization, one-hot encoding, augmentation |

## Running Tests

```bash
cd 71-deep-learning
mvn test -Dtest=Test
```

## Examples

### Creating a Simple Network

```java
MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
    .weightInit(WeightInit.XAVIER)
    .updater(new Adam(0.001))
    .list()
    .layer(0, new DenseLayer.Builder()
        .nIn(inputSize)
        .nOut(128)
        .activation(Activation.RELU)
        .build())
    .layer(1, new OutputLayer.Builder()
        .nIn(128)
        .nOut(outputSize)
        .activation(Activation.SOFTMAX)
        .lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
        .build())
    .build();
```

### Data Normalization

```java
INDArray normalized = dataProc.normalizeData(data);
// Z-score normalization: (x - mean) / std
```

### Model Persistence

```java
model.save(new File("model.zip"));
MultiLayerNetwork loaded = MultiLayerNetwork.load(new File("model.zip"), true);
```

## Best Practices

1. **Weight Initialization**: Use Xavier or He initialization for better convergence
2. **Learning Rate**: Start with 0.001 and adjust based on validation performance
3. **Batch Size**: Use appropriate batch sizes (32, 64, 128) based on available memory
4. **Early Stopping**: Prevent overfitting with early stopping patience
5. **Data Augmentation**: Increase dataset diversity for better generalization

## Further Reading

- [DL4J Documentation](https://deeplearning4j.konduit.ai/)
- [ND4J Linear Algebra](https://nd4j.org/)
- [DL4J Examples](https://github.com/deeplearning4j/dl4j-examples)