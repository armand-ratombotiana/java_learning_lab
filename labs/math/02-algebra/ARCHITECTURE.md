# Architecture of Algebra

## Algebraic Structure Hierarchy

```
Group          (one operation, inverses)
  ├─ Abelian Group  (commutative)
  ├─ Ring      (two operations: +, ×)
  │    └─ Field     (division exists)
  └─ Module    (vector space over ring)
       └─ Vector Space (over field)
```

## Java Analogies

| Math Structure | Java Concept |
|---------------|-------------|
| Group | `interface Group<T>` with `identity()`, `combine(T,T)`, `inverse(T)` |
| Ring | `BigInteger` — add, multiply, but no general division |
| Field | `double` — add, multiply, divide (but with precision limits) |
| Vector Space | `double[]` with vector add and scalar multiply |
| Homomorphism | `Function<T,R>` preserving structure |

## Polynomial Ring

```java
public record Polynomial(double[] coeffs) {
    public Polynomial add(Polynomial other) {
        int n = Math.max(coeffs.length, other.coeffs.length);
        double[] result = new double[n];
        for (int i = 0; i < coeffs.length; i++) result[i] += coeffs[i];
        for (int i = 0; i < other.coeffs.length; i++) result[i] += other.coeffs[i];
        return new Polynomial(result);
    }
}
```
