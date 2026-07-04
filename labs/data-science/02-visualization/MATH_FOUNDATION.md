# Math Foundation: Scales and Coordinates

## 1. Linear Scale

Maps domain [d₀, d₁] to range [r₀, r₁].

$$ y = r_0 + \frac{x - d_0}{d_1 - d_0} \times (r_1 - r_0) $$

```java
public class LinearScale {
    private final double d0, d1, r0, r1;
    
    public double apply(double x) {
        return r0 + (x - d0) / (d1 - d0) * (r1 - r0);
    }
    
    public double invert(double y) {
        return d0 + (y - r0) / (r1 - r0) * (d1 - d0);
    }
}
```

## 2. Log Scale

$$ y = \log_{10}(x) $$

```java
// Log scale tick calculation
public double[] logTicks(double min, double max) {
    int expMin = (int) Math.floor(Math.log10(min));
    int expMax = (int) Math.ceil(Math.log10(max));
    double[] ticks = new double[expMax - expMin + 1];
    for (int i = 0; i < ticks.length; i++) {
        ticks[i] = Math.pow(10, expMin + i);
    }
    return ticks;
}
```

## 3. Histogram Bin Width

**Sturges' rule**: $k = \lceil \log_2 n + 1 \rceil$

**Freedman-Diaconis rule**: $h = 2 \times \frac{IQR}{\sqrt[3]{n}}$

```java
public int freedmanDiaconisBins(double[] data) {
    double iqr = quartile(data, 0.75) - quartile(data, 0.25);
    double h = 2.0 * iqr / Math.cbrt(data.length);
    if (h == 0) return 1;
    return (int) Math.ceil((max(data) - min(data)) / h);
}
```

## 4. Smoothing (LOESS)

Locally estimated scatterplot smoothing fits low-degree polynomials to localized subsets. The span parameter controls smoothness — smaller span = more wiggly.

The weight for point (xᵢ, yᵢ) when estimating at x₀:

$$ w_i = \left(1 - \left|\frac{x_i - x_0}{\text{span}} \times \frac{1}{\max\_dist}\right|^3\right)^3 $$

## 5. Color Interpolation

Continuous color scales interpolate in LAB color space (perceptually uniform), not RGB.

```java
public Color interpolateColor(Color c1, Color c2, double t) {
    int r = (int) (c1.getRed() + t * (c2.getRed() - c1.getRed()));
    int g = (int) (c1.getGreen() + t * (c2.getGreen() - c1.getGreen()));
    int b = (int) (c1.getBlue() + t * (c2.getBlue() - c1.getBlue()));
    return new Color(r, g, b);
}
```
