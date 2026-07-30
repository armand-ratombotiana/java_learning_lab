# Interview: Number Theory

## Q1: Conceptual Understanding
**Q**: Explain the significance of Euler's totient in RSA.
**A**: φ(n) = (p-1)(q-1) for n=pq. RSA relies on the fact that a^φ(n) ≡ 1 mod n (Euler's theorem). The public exponent e and private exponent d satisfy ed ≡ 1 mod φ(n).

## Q2: Implementation
**Q**: How would you implement fast modular exponentiation?
**A**: Use binary exponentiation (exponentiation by squaring). Process bits of exponent from MSB to LSB: square result each step, multiply by base if bit is 1. O(log exponent) multiplications.

## Q3: Numerical Analysis
**Q**: Why is the Miller-Rabin test preferred over Fermat's test?
**A**: Fermat test has Carmichael numbers (e.g., 561 = 3×11×17) that pass for all bases coprime to n. Miller-Rabin adds square root detection and has no such pseudoprime problem.

## Coding Challenge
Implement the extended Euclidean algorithm returning gcd and Bézout coefficients.
