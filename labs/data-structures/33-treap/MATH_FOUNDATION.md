# Math Foundation: Treap (Randomized BST)

## Asymptotic Analysis

The mathematical foundation of the Treap (Randomized BST) rests on rigorous asymptotic analysis. Understanding the complexity bounds requires familiarity with several mathematical concepts.

## Time Complexity

### Best Case
In the best case, operations complete in constant or logarithmic time. This occurs when the structure is well-balanced and the operation aligns with the structure's strengths.

### Average Case
Average-case analysis assumes a uniform distribution of inputs or random behavior of the structure itself. For randomized variants, this provides high-probability guarantees.

### Worst Case
The worst-case behavior is bounded by careful design. Some variants guarantee logarithmic worst-case bounds while others rely on randomization for expected performance.

## Space Complexity

The space usage consists of:
- **Element storage**: The data itself
- **Structural overhead**: Pointers, metadata, balance information
- **Waste**: Unused capacity, fragmentation

## Recurrence Relations

Many operations can be analyzed using recurrence relations of the form:
T(n) = a T(n/b) + f(n)

Solving these recurrences using the Master Theorem or other techniques yields the asymptotic bounds.

## Probability Theory

Randomized variants rely on probability theory:
- Expected depth analysis
- High-probability bounds
- Randomization as an adversarial defense

## Amortized Analysis

Some operations have expensive individual costs but are cheap amortized over a sequence. Techniques include:
- Aggregate analysis
- Accounting method
- Potential function method
