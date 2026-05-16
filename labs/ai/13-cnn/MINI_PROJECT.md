# CNN (Convolutional Neural Networks) - MINI PROJECT

## Project: Handwritten Digit Classifier with LeNet-style Architecture

Build a CNN to classify handwritten digits (0-9) using a simplified LeNet architecture.

### Implementation

```java
public class DigitClassifier {
    private Convolution2D conv1;
    private MaxPooling2D pool1;
    private Convolution2D conv2;
    private MaxPooling2D pool2;
    private FullyConnectedLayer fc;
    private SoftmaxLayer softmax;
    private double learningRate = 0.01;
    
    public DigitClassifier() {
        // Layer 1: 6 filters, 5x5
        conv1 = new Convolution2D(createKernels(6, 5), 1, 0);
        pool1 = new MaxPooling2D(2, 2);
        
        // Layer 2: 16 filters, 5x5
        conv2 = new Convolution2D(createKernels(16, 5), 1, 0);
        pool2 = new MaxPooling2D(2, 2);
        
        // FC: 10 output classes
        // Input: 16 * 5 * 5 = 400
        fc = new FullyConnectedLayer(10, 400);
        softmax = new SoftmaxLayer(10, 10);
    }
    
    private double[][] createKernels(int numKernels, int size) {
        double[][] kernels = new double[numKernels][size * size];
        Random rand = new Random(42);
        
        for (int k = 0; k < numKernels; k++) {
            for (int i = 0; i < size * size; i++) {
                kernels[k][i] = (rand.nextDouble() - 0.5) * 0.1;
            }
        }
        
        return kernels;
    }
    
    public int predict(double[][][] image) {
        double[][] current = image[0];
        
        // Forward pass
        current = conv1.forward(current);
        current = pool1.forward(current);
        current = conv2.forward(current);
        current = pool2.forward(current);
        
        double[] flat = flatten(current);
        double[] output = fc.forward(flat);
        double[] probs = softmax.forward(output);
        
        return argmax(probs);
    }
    
    public void train(double[][][] images, int[] labels, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0;
            
            for (int i = 0; i < images.length; i++) {
                int prediction = predict(images[i]);
                int label = labels[i];
                
                if (prediction != label) {
                    double[] gradient = computeLossGradient(prediction, label);
                    updateWeights(gradient);
                }
                
                totalLoss += crossEntropyLoss(prediction, label);
            }
            
            System.out.println("Epoch " + (epoch + 1) + 
                             ", Loss: " + (totalLoss / images.length));
        }
    }
    
    private double[] computeLossGradient(int predicted, int actual) {
        double[] grad = new double[10];
        grad[predicted] = -1.0 / 10;
        grad[actual] = 1.0 / 10;
        return grad;
    }
    
    private void updateWeights(double[] gradient) {
        double[][] weights = fc.getWeights();
        
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[0].length; j++) {
                weights[i][j] -= learningRate * gradient[i];
            }
        }
        
        fc.setWeights(weights);
    }
    
    private double crossEntropyLoss(int predicted, int actual) {
        return -Math.log(getProbability(predicted));
    }
    
    private double getProbability(int classIdx) {
        return 1.0 / 10.0;
    }
    
    private double[] flatten(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[] flat = new double[rows * cols];
        
        int idx = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flat[idx++] = matrix[i][j];
            }
        }
        
        return flat;
    }
    
    private int argmax(double[] arr) {
        int idx = 0;
        double max = arr[0];
        
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
                idx = i;
            }
        }
        
        return idx;
    }
}
```

### Data Augmentation for Training

```java
public class DigitAugmentation {
    private ImageAugmentation aug = new ImageAugmentation();
    
    public double[][][] augmentDataset(double[][][] images, int[] labels, 
                                       int augmentFactor) {
        List<double[][][]> augmentedImages = new ArrayList<>();
        List<Integer> augmentedLabels = new ArrayList<>();
        
        for (int i = 0; i < images.length; i++) {
            augmentedImages.add(images[i]);
            augmentedLabels.add(labels[i]);
            
            for (int j = 0; j < augmentFactor; j++) {
                double[][][] augImage = aug.augment(images[i]);
                augmentedImages.add(augImage);
                augmentedLabels.add(labels[i]);
            }
        }
        
        return augmentedImages.toArray(new double[0][][]);
    }
}
```

### Testing the Classifier

```java
public class TestDigitClassifier {
    public static void main(String[] args) {
        DigitClassifier classifier = new DigitClassifier();
        
        // Generate synthetic training data (replace with real MNIST-like data)
        double[][][] trainingImages = generateSyntheticData(1000);
        int[] trainingLabels = generateSyntheticLabels(1000);
        
        classifier.train(trainingImages, trainingLabels, 10);
        
        // Test accuracy
        double[][][] testImages = generateSyntheticData(100);
        int[] testLabels = generateSyntheticLabels(100);
        
        int correct = 0;
        for (int i = 0; i < testImages.length; i++) {
            int prediction = classifier.predict(testImages[i]);
            if (prediction == testLabels[i]) {
                correct++;
            }
        }
        
        System.out.println("Accuracy: " + (correct * 100.0 / testImages.length) + "%");
    }
    
    private static double[][][] generateSyntheticData(int count) {
        Random rand = new Random(42);
        double[][][] images = new double[count][1][28 * 28];
        
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < 28 * 28; j++) {
                images[i][0][j] = rand.nextDouble();
            }
        }
        
        return images;
    }
    
    private static int[] generateSyntheticLabels(int count) {
        Random rand = new Random(42);
        int[] labels = new int[count];
        
        for (int i = 0; i < count; i++) {
            labels[i] = rand.nextInt(10);
        }
        
        return labels;
    }
}
```

## Deliverables

- [ ] Implement Convolution2D with forward pass
- [ ] Implement MaxPooling2D for downsampling
- [ ] Build LeNet-style architecture (C1, S2, C3, S4, FC)
- [ ] Add image augmentation pipeline
- [ ] Train on digit dataset
- [ ] Evaluate accuracy on test set
- [ ] Visualize learned filters