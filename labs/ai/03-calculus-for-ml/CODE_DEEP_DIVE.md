# Calculus for ML - CODE DEEP DIVE

## Automatic Differentiation

```java
public class AutoDiff {
    public static class DualNumber {
        double value;
        double derivative;
        
        public DualNumber(double value, double derivative) {
            this.value = value;
            this.derivative = derivative;
        }
        
        public static DualNumber constant(double x) {
            return new DualNumber(x, 0);
        }
        
        public static DualNumber variable(double x) {
            return new DualNumber(x, 1);
        }
        
        public static DualNumber add(DualNumber a, DualNumber b) {
            return new DualNumber(a.value + b.value, a.derivative + b.derivative);
        }
        
        public static DualNumber multiply(DualNumber a, DualNumber b) {
            return new DualNumber(
                a.value * b.value,
                a.value * b.derivative + a.derivative * b.value
            );
        }
        
        public static DualNumber sin(DualNumber x) {
            return new DualNumber(
                Math.sin(x.value),
                x.derivative * Math.cos(x.value)
            );
        }
        
        public static DualNumber cos(DualNumber x) {
            return new DualNumber(
                Math.cos(x.value),
                -x.derivative * Math.sin(x.value)
            );
        }
        
        public static DualNumber exp(DualNumber x) {
            return new DualNumber(
                Math.exp(x.value),
                x.derivative * Math.exp(x.value)
            );
        }
    }
    
    // Example: f(x) = x² + 2*sin(x)
    public static void example() {
        DualNumber x = DualNumber.variable(0.5);
        DualNumber result = DualNumber.add(
            DualNumber.multiply(x, x),  // x²
            DualNumber.multiply(DualNumber.constant(2), DualNumber.sin(x))  // 2*sin(x)
        );
        
        System.out.println("f(0.5) = " + result.value);
        System.out.println("f'(0.5) = " + result.derivative);  // Should be 2*0.5 + 2*cos(0.5)
    }
}
```

## Gradient Computation for ML

```java
public class MLGradient {
    // Mean Squared Error gradient
    public double[] mseGradient(double[] predictions, double[] targets) {
        double[] gradient = new double[predictions.length];
        for (int i = 0; i < predictions.length; i++) {
            gradient[i] = 2 * (predictions[i] - targets[i]);
        }
        return gradient;
    }
    
    // Cross-entropy gradient
    public double[] crossEntropyGradient(double[] predictions, double[] targets) {
        double[] gradient = new double[predictions.length];
        for (int i = 0; i < predictions.length; i++) {
            gradient[i] = -targets[i] / (predictions[i] + 1e-10) 
                       + (1 - targets[i]) / (1 - predictions[i] + 1e-10);
        }
        return gradient;
    }
    
    // Numerical gradient check
    public boolean checkGradient(Function<double[], Double> f, 
                                 double[] params, double[] gradient) {
        double eps = 1e-5;
        double[] numericalGrad = new double[params.length];
        
        for (int i = 0; i < params.length; i++) {
            double[] paramsPlus = params.clone();
            double[] paramsMinus = params.clone();
            
            paramsPlus[i] += eps;
            paramsMinus[i] -= eps;
            
            numericalGrad[i] = (f.apply(paramsPlus) - f.apply(paramsMinus)) / (2 * eps);
        }
        
        // Compare
        double maxDiff = 0;
        for (int i = 0; i < gradient.length; i++) {
            maxDiff = Math.max(maxDiff, Math.abs(gradient[i] - numericalGrad[i]));
        }
        
        return maxDiff < 1e-5;
    }
}
```

## Hessian-Free Optimization

```java
public class NewtonMethod {
    public double[] minimize(Function<double[], Double> f, double[] init) {
        double[] x = init.clone();
        double tol = 1e-8;
        
        for (int iter = 0; iter < 100; iter++) {
            // Compute gradient
            double[] grad = computeGradient(f, x);
            
            if (norm(grad) < tol) break;
            
            // Compute Hessian approximation
            double[][] hessian = approximateHessian(f, x);
            
            // Solve H * p = -g
            double[] direction = solveLinearSystem(hessian, grad);
            
            // Line search
            double alpha = lineSearch(f, x, direction);
            
            // Update
            for (int i = 0; i < x.length; i++) {
                x[i] += alpha * direction[i];
            }
        }
        
        return x;
    }
    
    private double lineSearch(Function<double[], Double> f, 
                              double[] x, double[] d) {
        double alpha = 1.0;
        double c = 0.1;
        double rho = 0.5;
        
        double fx = f.apply(x);
        double[] grad = computeGradient(f, x);
        double slope = dot(grad, d);
        
        while (alpha > 1e-10) {
            double[] xNew = new double[x.length];
            for (int i = 0; i < x.length; i++) {
                xNew[i] = x[i] + alpha * d[i];
            }
            
            if (f.apply(xNew) <= fx + c * alpha * slope) {
                return alpha;
            }
            alpha *= rho;
        }
        
        return alpha;
    }
    
    private double norm(double[] v) {
        return Math.sqrt(dot(v, v));
    }
    
    private double dot(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }
}
```