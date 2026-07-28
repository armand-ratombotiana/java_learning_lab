# Interview Questions: Switch Expressions

## Basic
1. What is the difference between a switch statement and a switch expression?
2. What is the arrow (`->`) syntax and how does it prevent fall-through?
3. What does `yield` do in a switch expression?

## Intermediate
4. When must a switch expression be exhaustive?
5. How do you handle null in a switch expression?
6. Can you mix colon and arrow syntax in the same switch?

## Advanced
7. How does the compiler check exhaustiveness for sealed types?
8. What happens if you use `break` instead of `yield` in a switch expression?
9. How do guarded patterns (`when`) interact with exhaustiveness checks?
10. Can a switch expression throw an exception? How would you handle it?

## Expert
11. Explain the bytecode difference between `tableswitch` and `lookupswitch` and how arrow syntax affects them.
12. How does the JIT compiler optimize pattern matching in switch expressions?
13. Can switch expressions be used with primitives? What are the boxing implications?
14. How does the dominance rule apply when `when` clauses overlap?
