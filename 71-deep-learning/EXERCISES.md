# Deep Learning Exercises

## Exercise 1: Basic Neural Network
Build a simple MLP (Multi-Layer Perceptron) with:
- Input layer: 784 features (MNIST-style)
- Hidden layer: 128 units with ReLU activation
- Dropout: 20% rate
- Output layer: 10 units with Softmax

## Exercise 2: CNN Image Classifier
Create a CNN for CIFAR-10 classification:
- Conv2D with 32 filters (3x3 kernel)
- MaxPooling (2x2)
- Flatten layer
- Dense output (10 classes)

## Exercise 3: Transfer Learning
Use a pre-trained ResNet model to:
- Load ImageNet weights
- Replace final layer for custom classification
- Fine-tune on your dataset

## Exercise 4: Model Training Pipeline
Implement complete training:
- Data loading with DataLoader
- Loss function selection
- Optimizer configuration (Adam)
- Training loop with epoch tracking

## Exercise 5: Model Evaluation
Evaluate trained model:
- Calculate accuracy on test set
- Generate confusion matrix
- Measure precision, recall, F1-score

## Exercise 6: Save and Load Models
- Save trained model to disk
- Load model for inference
- Export to ONNX format