# Arithmetic

Basic operations on numbers: addition, subtraction, multiplication, division, and their properties.

## Scope

- Integer & floating-point arithmetic
- Order of operations (PEMDAS/BODMAS)
- Modular arithmetic
- Number theory fundamentals (GCD, LCM, prime factorization)

## Java Implementation

```java
public class Arithmetic {
    public static int add(int a, int b) { return a + b; }
    public static int subtract(int a, int b) { return a - b; }
    public static int multiply(int a, int b) { return a * b; }
    public static double divide(double a, double b) {
        if (b == 0) throw new ArithmeticException("Division by zero");
        return a / b;
    }
}
```
