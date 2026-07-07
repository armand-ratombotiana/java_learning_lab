# FFT — Common Mistakes

1. Not padding to the next power of two: FFT requires N to be a power of two. If the input length is not a power of two, the algorithm will produce incorrect results or array bounds errors.

2. Confusing forward and inverse transforms: forward FFT uses omega = exp(-2*pi*i/N), inverse uses omega = exp(2*pi*i/N) and divides by N. Using the wrong one gives results conjugated by N.

3. Forgetting to divide by N in inverse FFT: many implementations forget this final scaling step, producing values N times too large.

4. Bit-reversal permutation not matching the iterative butterfly structure: the standard Cooley-Tukey decimation-in-time FFT requires bit-reversed input order. Not reordering gives incorrect results.

5. Rounding errors in polynomial multiplication: after inverse FFT, coefficients should be rounded to the nearest integer. Without rounding, floating-point errors accumulate.

6. NTT modulus not meeting requirements: the modulus must be of the form c*2^k + 1 with 2^k >= N. Using a prime without this property will fail because a primitive N-th root of unity does not exist.

7. Integer overflow in NTT: (a * b) % MOD must be computed using long arithmetic when MOD ~ 10^9. Use (a % MOD) * (b % MOD) % MOD with long intermediate.

8. Primitive root computation: not all generators of the multiplicative group work for a given N. The root must satisfy g^{(p-1)/N} != 1 mod p.