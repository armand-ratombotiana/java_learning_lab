# Gradient Descent Code Deep Dive

This lab provides a pure Java implementation of Batch Gradient Descent, applied to solve a simple Linear Regression problem (predicting a line of best fit).

## 💻 Pure Java Implementation

```java file="labs/math/05-optimization/gradient-descent/SOLUTION/LinearRegressionGD.java"
package math.optimization;

/**
 * A fundamental implementation of Batch Gradient Descent for Linear Regression.
 */
public class LinearRegressionGD {

    // Model parameters: y = theta1 * x + theta0
    private double theta0 = 0.0; // y-intercept (bias)
    private double theta1 = 0.0; // slope (weight)

    private final double learningRate;
    private final int iterations;

    public LinearRegressionGD(double learningRate, int iterations) {
        this.learningRate = learningRate;
        this.iterations = iterations;
    }

    /**
     * The prediction function (Hypothesis).
     */
    public double predict(double x) {
        return theta1 * x + theta0;
    }

    /**
     * Calculates the Mean Squared Error (MSE) Cost.
     */
    public double calculateCost(double[] x, double[] y) {
        int m = x.length;
        double sumError = 0.0;
        
        for (int i = 0; i < m; i++) {
            double prediction = predict(x[i]);
            double error = prediction - y[i];
            sumError += (error * error);
        }
        
        return sumError / (2 * m);
    }

    /**
     * Trains the model using Batch Gradient Descent.
     */
    public void train(double[] x, double[] y) {
        int m = x.length;

        for (int iter = 0; iter < iterations; iter++) {
            
            double sumGradient0 = 0.0;
            double sumGradient1 = 0.0;

            // 1. Calculate the gradients across the ENTIRE dataset (Batch)
            for (int i = 0; i < m; i++) {
                double prediction = predict(x[i]);
                double error = prediction - y[i];
                
                // Partial derivative w.r.t theta0
                sumGradient0 += error; 
                
                // Partial derivative w.r.t theta1
                sumGradient1 += error * x[i]; 
            }

            // Average the gradients
            double gradient0 = sumGradient0 / m;
            double gradient1 = sumGradient1 / m;

            // 2. Update the parameters simultaneously
            theta0 = theta0 - (learningRate * gradient0);
            theta1 = theta1 - (learningRate * gradient1);

            // Print progress every 100 iterations
            if (iter % 100 == 0) {
                double cost = calculateCost(x, y);
                System.out.printf("Iteration %4d | Cost: %.4f | theta0: %.4f | theta1: %.4f%n", 
                                  iter, cost, theta0, theta1);
            }
        }
    }

    public static void main(String[] args) {
        // Training Data: A simple linear relationship y = 2x + 1 (with slight noise)
        double[] x = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] y = {3.1, 4.9, 7.2, 8.8, 11.1};

        // Initialize model with a learning rate of 0.01 and 1000 iterations
        LinearRegressionGD model = new LinearRegressionGD(0.01, 1000);
        
        System.out.println("Starting Training...");
        model.train(x, y);
        
        System.out.println("\nTraining Complete!");
        System.out.printf("Final Equation: y = %.4f * x + %.4f%n", model.theta1, model.theta0);
        
        // Test prediction
        double testX = 6.0;
        System.out.printf("Prediction for x = %.1f is %.4f (Expected ~13.0)%n", testX, model.predict(testX));
    }
}
```

## 🔍 Key Takeaways
1. **Simultaneous Update**: Notice that we calculate `sumGradient0` and `sumGradient1` *before* we update `theta0` and `theta1`. If we updated `theta0` immediately inside the loop, the calculation for `sumGradient1` would use the *new* `theta0`, which is mathematically incorrect for standard Gradient Descent.
2. **The Learning Rate**: If you change the `learningRate` in the `main` method to `1.5`, the model will "blow up". The cost will print as `NaN` or `Infinity`. This is divergence—the steps were too large, and the model stepped over the valley completely.