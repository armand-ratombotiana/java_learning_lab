# Calculus - REAL WORLD PROJECT

Build automatic differentiation engine supporting:
- Basic operations (add, mul, sin, cos, exp)
- Reverse mode AD for neural networks

```java
public class AutoDiffEngine {
    // Forward mode AD
    public DualNumber forward(DoubleUnaryOperator f, double x) {
        // Compute f(x) and f'(x) simultaneously
    }
    
    // Reverse mode for neural networks
    public Matrix reverseMode(Matrix output, List<Operation> ops) {
        // Backpropagate gradients
    }
}
```