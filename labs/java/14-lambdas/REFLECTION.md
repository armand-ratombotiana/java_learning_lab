# Reflection — Lambdas

## Why This Lab Matters
Lambdas fundamentally changed how Java developers write code, enabling functional patterns that were previously impractical.

## What I Learned
- Lambda syntax and conversion from anonymous classes
- The `java.util.function` package and how to compose functions
- How the JVM handles lambdas via `invokedynamic`

## Questions I Still Have
- How do method handles compose under the hood?
- What future enhancements to lambdas are planned (value types, pattern matching in lambdas)?

## Personal Application
- Replace anonymous listeners in UI code
- Use functional composition for business rules
- Use `Supplier` for lazy initialisation

## Key Takeaways
1. Lambdas are about behaviour parameterisation
2. Prefer method references for readability
3. Non-capturing lambdas are effectively free
4. Keep lambda bodies small and extract complex logic
