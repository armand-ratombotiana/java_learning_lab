# Calculus for ML - EXERCISES

## Exercise 1: Compute Derivative
Find derivative of f(x) = 3x² + 2x - 5

**Answer**: f'(x) = 6x + 2

## Exercise 2: Gradient
Find gradient of f(x,y) = x² + xy + y²

**Answer**: ∇f = [2x + y, x + 2y]

## Exercise 3: Chain Rule
If z = f(u), u = g(x), find dz/dx when f(u) = u², g(x) = 3x + 1

**Answer**: dz/dx = 2(3x+1) × 3 = 18x + 6

## Exercise 4: Gradient Descent
Minimize f(x) = x² starting at x₀ = 5, lr = 0.1

```java
double x = 5;
for (int i = 0; i < 10; i++) {
    double grad = 2 * x;  // f'(x) = 2x
    x -= 0.1 * grad;
    System.out.println("x = " + x);
}
// Result: x will converge to 0
```

## Exercise 5: Hessian Matrix
Find Hessian of f(x,y) = x² + 2xy + y²

```java
// f_xx = 2, f_xy = 2, f_yx = 2, f_yy = 2
double[][] H = {{2, 2}, {2, 2}};
```

---

## Solutions

### Exercise 1:
```java
public double derivative(double x) {
    return 6 * x + 2;
}
```

### Exercise 2:
```java
public double[] gradient(double x, double y) {
    return new double[]{2 * x + y, x + 2 * y};
}
```

### Exercise 3:
```java
public double chainRule(double x) {
    // f(u) = u², u = 3x+1
    // dz/dx = f'(u) × u' = 2u × 3 = 6(3x+1) = 18x + 6
    return 18 * x + 6;
}
```

### Exercise 4:
```java
// x₀ = 5, lr = 0.1
// x₁ = 5 - 0.1 * 10 = 4
// x₂ = 4 - 0.1 * 8 = 3.2
// ...
// Converges to 0
```

### Exercise 5:
```java
public double[][] hessian() {
    return new double[][] {
        {2, 2},
        {2, 2}
    };
}
```