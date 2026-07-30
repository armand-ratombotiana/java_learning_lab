# Interview: Numerical Methods

## Q1: Conceptual Understanding
**Q**: Compare Newton-Raphson and bisection for root finding.
**A**: Bisection guarantees linear convergence given a sign change but is slow. Newton-Raphson converges quadratically near the root but requires a derivative and a good initial guess. Bisection is robust; Newton-Raphson is fast when it works.

## Q2: Implementation
**Q**: How would you implement adaptive numerical integration?
**A**: Apply Simpson's rule on the whole interval and on two halves. If the difference exceeds a tolerance, recurse on each half. This concentrates evaluations where the function varies rapidly.

## Q3: Numerical Analysis
**Q**: Why does choosing h too small in finite differences increase error?
**A**: Truncation error decreases with h, but round-off error grows as h shrinks because subtracting nearly equal floating-point numbers causes catastrophic cancellation. Optimal h balances truncation and round-off.

## Coding Challenge
Implement a numerically stable Newton-Raphson solver with fallback to bisection.
