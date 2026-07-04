# Internals of Discrete Mathematics

## Boolean Algebra Axioms

| Law | Expression |
|-----|-----------|
| Identity | $A \lor 0 = A$, $A \land 1 = A$ |
| Annulment | $A \lor 1 = 1$, $A \land 0 = 0$ |
| Idempotence | $A \lor A = A$, $A \land A = A$ |
| Complement | $A \lor \lnot A = 1$, $A \land \lnot A = 0$ |
| De Morgan | $\lnot(A \lor B) = \lnot A \land \lnot B$ |

## Normal Forms

### CNF (Conjunctive Normal Form)

$$
(A \lor B) \land (C \lor \lnot D) \land \dots
$$

### DNF (Disjunctive Normal Form)

$$
(A \land B) \lor (C \land \lnot D) \lor \dots
```

Every boolean expression can be expressed in both forms.

## Truth Table Generation

```java
public static void generateTruthTable(int variables, String expression) {
    int rows = 1 << variables;
    for (int i = 0; i < rows; i++) {
        boolean[] vals = new boolean[variables];
        for (int j = 0; j < variables; j++)
            vals[j] = (i & (1 << (variables - 1 - j))) != 0;
        boolean result = evaluate(expression, vals);
        System.out.println(Arrays.toString(vals) + " → " + result);
    }
}
```
