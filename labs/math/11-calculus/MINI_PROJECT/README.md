# Mini Project: Function Plotter

```java
package com.mathacademy.calculus.mini;

public class FunctionPlotter {
    public double[][] plotPoints(Function f, double xMin, double xMax, int points) {
        double step = (xMax - xMin) / points;
        double[][] data = new double[points][2];
        for (int i = 0; i < points; i++) {
            double x = xMin + i * step;
            data[i][0] = x;
            data[i][1] = f.evaluate(x);
        }
        return data;
    }
    
    public interface Function {
        double evaluate(double x);
    }
}
```