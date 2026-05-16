# Neural Networks Basics - EXERCISES

## Exercise 1: Perceptron Implementation
Implement a perceptron that can learn the AND function.

```java
public class PerceptronAND {
    public static void main(String[] args) {
        double[][] trainingData = {
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1}
        };
        int[] labels = {0, 0, 0, 1};

        Perceptron perceptron = new Perceptron(2, 0.1);
        perceptron.train(trainingData, labels, 100);

        for (double[] data : trainingData) {
            System.out.println(Arrays.toString(data) + " -> " + perceptron.predict(data));
        }
    }
}
// Expected: AND truth table outputs
```

**Answer**: Implement perceptron with weights that learn AND gate decision boundary at w1 + w2 > threshold.

## Exercise 2: Sigmoid Activation
Calculate sigmoid(0), sigmoid(2), and sigmoid(-2).

```java
public double sigmoid(double x) {
    return 1.0 / (1.0 + Math.exp(-x));
}
// sigmoid(0) = 0.5
// sigmoid(2) ≈ 0.8808
// sigmoid(-2) ≈ 0.1192
```

## Exercise 3: MLP Forward Pass
Manually compute the forward pass for a 2-3-2 MLP with ReLU:
- Input: [1.0, 0.5]
- Weights layer 1: [[0.5, 0.2], [0.3, 0.7], [0.6, 0.1]]
- Biases layer 1: [0.1, 0.2, 0.3]
- Weights layer 2: [[0.4, 0.3, 0.2], [0.5, 0.4, 0.1]]
- Biases layer 2: [0.1, 0.2]

```java
public double[] relu(double x) {
    return Math.max(0, x);
}
// z1 = 0.5*1 + 0.2*0.5 + 0.1 = 0.6 → a1 = 0.6
// z2 = 0.3*1 + 0.7*0.5 + 0.2 = 0.65 → a2 = 0.65
// z3 = 0.6*1 + 0.1*0.5 + 0.3 = 0.95 → a3 = 0.95
// Output: [0.4*0.6 + 0.3*0.65 + 0.2*0.95 + 0.1,
//          0.5*0.6 + 0.4*0.65 + 0.1*0.95 + 0.2]
```

## Exercise 4: Cross-Entropy Loss
Compute cross-entropy loss for prediction [0.7, 0.2, 0.1] with target class 0.

```java
public double crossEntropy(double[] prediction, int target) {
    return -Math.log(prediction[target] + 1e-10);
}
// -log(0.7) ≈ 0.3567
```

## Exercise 5: Xavier Initialization
Initialize weights for a layer with 100 inputs and 50 outputs.

```java
public double[][] xavierInit(int inputs, int outputs) {
    double limit = Math.sqrt(2.0 / (inputs + outputs));
    Random random = new Random();
    double[][] weights = new double[outputs][inputs];
    for (int i = 0; i < outputs; i++) {
        for (int j = 0; j < inputs; j++) {
            weights[i][j] = random.nextGaussian() * limit;
        }
    }
    return weights;
}
// limit ≈ 0.141
```

## Exercise 6: Leaky ReLU Derivative
Calculate derivative at x = -5 with alpha = 0.01.

```java
public double leakyReluDerivative(double x, double alpha) {
    return x > 0 ? 1.0 : alpha;
}
// leakyReluDerivative(-5, 0.01) = 0.01
```

---

## Solutions

### Exercise 1:
```java
class Perceptron {
    private double[] weights;
    private double bias;

    public Perceptron(int inputSize, double learningRate) {
        this.weights = new double[inputSize];
        this.bias = 0.0;
        Random random = new Random();
        for (int i = 0; i < weights.length; i++) {
            weights[i] = random.nextDouble() * 2 - 1;
        }
    }

    public int predict(double[] inputs) {
        double sum = bias;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * inputs[i];
        }
        return sum >= 0 ? 1 : 0;
    }

    public void train(double[][] data, int[] labels, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < data.length; i++) {
                int pred = predict(data[i]);
                int error = labels[i] - pred;
                for (int j = 0; j < weights.length; j++) {
                    weights[j] += 0.1 * error * data[i][j];
                }
                bias += 0.1 * error;
            }
        }
    }
}
```

### Exercise 2:
- sigmoid(0) = 0.5
- sigmoid(2) = 1 / (1 + e^-2) ≈ 0.8808
- sigmoid(-2) = 1 / (1 + e^2) ≈ 0.1192

### Exercise 3:
Hidden layer output: [0.6, 0.65, 0.95]
Final output: [0.715, 0.905]

### Exercise 4:
Cross-entropy = 0.3567

### Exercise 5:
Limit = 0.141, weights from N(0, 0.0199)

### Exercise 6:
Derivative = 0.01