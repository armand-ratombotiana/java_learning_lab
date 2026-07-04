# Debugging Discrete Math in Java

## Common Issues

### Integer Division in Sieve

```java
// WRONG: double comparison
for (int i = 2; i <= Math.sqrt(n); i++)

// RIGHT: integer comparison
for (int i = 2; i * i <= n; i++)
```

### Boolean Expression Short-Circuit

```java
// WRONG: NullPointerException
if (list != null & list.size() > 0) // single & evaluates both

// RIGHT: short-circuit &&
if (list != null && list.size() > 0)
```

## Debugging Checklist

- [ ] Induction: base case correct? Step proven correctly?
- [ ] Logic: implication direction correct? Quantifiers properly negated?
- [ ] Sets: overlap considered in cardinality?
- [ ] Mod: negative numbers handled?
- [ ] Proof: every statement justified?
