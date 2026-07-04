# Discrete Mathematics

The study of mathematical structures that are fundamentally discrete rather than continuous.

## Scope

- Propositional and predicate logic
- Set theory, relations, functions
- Proof techniques: induction, contradiction, contrapositive
- Modular arithmetic, number theory
- Boolean algebra, digital logic

## Java Implementation

```java
public class DiscreteMath {
    public static boolean isEven(int n) { return n % 2 == 0; }
    public static int modulo(int a, int b) { return ((a % b) + b) % b; }
    public static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++)
            if (n % i == 0) return false;
        return true;
    }
}
```
