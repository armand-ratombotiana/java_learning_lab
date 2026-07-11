# Derivatives Code Deep Dive

This lab provides a pure Java implementation of Numerical Differentiation, demonstrating how to approximate the derivative of any mathematical function using the formal limit definition.

## 💻 Pure Java Implementation

```java file="labs/math/02-calculus/derivatives/SOLUTION/NumericalDifferentiation.java"
package math.calculus;

import java.util.function.Function;

/**
 * A fundamental implementation of numerical differentiation.
 * In production ML systems, Automatic Differentiation (Autograd) is used instead,
 * as numerical differentiation is prone to floating-point errors and is computationally expensive.
 */
public class NumericalDifferentiation {

    // A very small step size (h) to simulate the limit approaching zero
    private static final double H = 1e-5;

    /**
     * Approximates the derivative of a single-variable function at a specific point x.
     * Uses the central difference formula for better accuracy than the forward difference.
     * Formula: (f(x + h) - f(x - h)) / (2h)
     */
    public static double computeDerivative(Function<Double, Double> func, double x) {
        double fPlusH = func.apply(x + H);
        double fMinusH = func.apply(x - H);
        return (fPlusH - fMinusH) / (2 * H);
    }

    /**
     * Approximates the partial derivative of a multivariable function with respect to a specific variable index.
     * 
     * @param func The multivariable function (takes an array of inputs)
     * @param variables The point at which to evaluate the derivative (e.g., [x, y, z])
     * @param targetVarIndex The index of the variable to differentiate with respect to
     */
    public static double computePartialDerivative(Function<double[], Double> func, double[] variables, int targetVarIndex) {
        // Create copies of the variable array to perturb
        double[] varsPlusH = variables.clone();
        double[] varsMinusH = variables.clone();
        
        // Perturb ONLY the target variable (holding all others constant)
        varsPlusH[targetVarIndex] += H;
        varsMinusH[targetVarIndex] -= H;
        
        double fPlusH = func.apply(varsPlusH);
        double fMinusH = func.apply(varsMinusH);
        
        return (fPlusH - fMinusH) / (2 * H);
    }

    public static void main(String[] args) {
        
        System.out.println("--- Single Variable Derivatives ---");
        
        // Function: f(x) = x^2
        Function<Double, Double> f1 = x -> x * x;
        double x1 = 3.0;
        // Analytical derivative: 2x. At x=3, f'(3) = 6
        double numerical1 = computeDerivative(f1, x1);
        System.out.printf("f(x) = x^2 at x=%.1f | Numerical: %.5f | Expected: 6.0\n", x1, numerical1);
        
        // Function: f(x) = sin(x)
        Function<Double, Double> f2 = x -> Math.sin(x);
        double x2 = Math.PI;
        // Analytical derivative: cos(x). At x=PI, cos(PI) = -1
        double numerical2 = computeDerivative(f2, x2);
        System.out.printf("f(x) = sin(x) at x=PI | Numerical: %.5f | Expected: -1.0\n", numerical2);
        
        System.out.println("\n--- Partial Derivatives ---");
        
        // Multivariable Function: f(x, y) = x^2 * y + y^3
        Function<double[], Double> multiFunc = vars -> {
            double x = vars[0];
            double y = vars[1];
            return (x * x * y) + (y * y * y);
        };
        
        double[] point = {2.0, 3.0}; // x=2, y=3
        
        // Partial w.r.t x: 2xy. At (2,3) -> 2*2*3 = 12
        double partialX = computePartialDerivative(multiFunc, point, 0);
        System.out.printf("df/dx at (2,3) | Numerical: %.5f | Expected: 12.0\n", partialX);
        
        // Partial w.r.t y: x^2 + 3y^2. At (2,3) -> 4 + 27 = 31
        double partialY = computePartialDerivative(multiFunc, point, 1);
        System.out.printf("df/dy at (2,3) | Numerical: %.5f | Expected: 31.0\n", partialY);
    }
}
```

## 🔍 Key Takeaways
1. **Central Difference**: Notice that instead of using the formal definition `(f(x+h) - f(x)) / h`, we use `(f(x+h) - f(x-h)) / (2h)`. This is mathematically proven to be a much more accurate approximation and reduces floating-point truncation errors in computer science.
2. **Partial Derivatives in Code**: Look at `computePartialDerivative`. We clone the input array and *only* add `H` to the specific variable we are differentiating against. All other variables remain exactly the same. This perfectly models the mathematical definition of holding other variables constant.