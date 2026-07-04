# Step-by-Step: Trigonometry in Java

## Convert Degrees to Radians

```java
public static double toRadians(double degrees) {
    return degrees * Math.PI / 180.0;
}
```

## Solve a Triangle (SAS)

```java
// Given two sides and included angle
public static Triangle solveSAS(double a, double b, double gamma) {
    double c = Math.sqrt(a * a + b * b - 2 * a * b * Math.cos(gamma));
    double alpha = Math.acos((b * b + c * c - a * a) / (2 * b * c));
    double beta = Math.PI - alpha - gamma;
    return new Triangle(a, b, c, alpha, beta, gamma);
}
```

## Compute DFT (Discrete Fourier Transform)

```java
public static Complex[] dft(double[] signal) {
    int n = signal.length;
    Complex[] result = new Complex[n];
    for (int k = 0; k < n; k++) {
        double real = 0, imag = 0;
        for (int t = 0; t < n; t++) {
            double angle = 2 * Math.PI * k * t / n;
            real += signal[t] * Math.cos(angle);
            imag -= signal[t] * Math.sin(angle);
        }
        result[k] = new Complex(real, imag);
    }
    return result;
}
```

## Rotate a 3D Point Around Y-Axis

```java
public static Point3D rotateY(Point3D p, double angle) {
    double cos = Math.cos(angle);
    double sin = Math.sin(angle);
    return new Point3D(
        p.x() * cos + p.z() * sin,
        p.y(),
        -p.x() * sin + p.z() * cos
    );
}
```
