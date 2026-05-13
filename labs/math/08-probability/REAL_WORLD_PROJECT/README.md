# Real World Project: Monte Carlo Simulation

```java
package com.mathacademy.probability.realworld;

public class MonteCarloPi {
    public double estimatePi(int points) {
        int inside = 0;
        Random random = new Random();
        for (int i = 0; i < points; i++) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            if (x*x + y*y <= 1) inside++;
        }
        return 4.0 * inside / points;
    }
}
```