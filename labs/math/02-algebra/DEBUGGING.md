# Debugging Algebra in Java

## Common Issues

### NaN from Dividing Zero by Zero

```java
double slope = (y2 - y1) / (x2 - x1); // 0/0 = NaN
```

### Loss of Significance

Subtracting nearly equal numbers loses precision:

```java
// BAD: catastrophic cancellation
double root1 = (-b + sqrtD) / (2 * a);
double root2 = (-b - sqrtD) / (2 * a);

// BETTER: compute one root, derive other from product
double root1 = (-b + Math.copySign(sqrtD, b)) / (2 * a);
double root2 = c / (a * root1);
```

## Debugging Checklist

- [ ] Are signs correct when moving terms across `=`?
- [ ] Did you factor before dividing by a variable?
- [ ] Discriminant computed correctly?
- [ ] Matrix dimensions compatible for multiplication?
- [ ] Are you using `==` for floating-point comparison?
- [ ] Did you handle the case $a = 0$ in quadratic?
