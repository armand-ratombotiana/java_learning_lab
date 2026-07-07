# FFT — Mathematical Foundation

## Roots of Unity

An n-th root of unity is a complex number omega such that omega^n = 1. The primitive n-th root is omega_n = e^{2pi i / n} = cos(2pi/n) + i*sin(2pi/n). Properties: omega_n^k = e^{2pi i k / n}; omega_n^{-k} = omega_n^{n-k}. The sum of all n-th roots of unity is zero for n > 1.

## DFT Matrix

The DFT matrix F_N has entries F_N[j][k] = omega_N^{-jk}. F_N is unitary up to scaling: F_N * conj(F_N) = N * I. The inverse DFT matrix uses omega_N^{jk} instead and divides by N.

## Convolution Theorem

Given sequences a and b of length n: DFT(a * b) = DFT(a) . DFT(b) where . denotes elementwise multiplication and * is cyclic convolution (a * b)[k] = sum_{j=0}^{n-1} a[j] * b[(k-j) mod n]. For linear convolution (polynomial multiplication), pad both sequences to size >= n+m-1 before applying the theorem.

## NTT Over Finite Fields

NTT requires: (1) modulus p is prime, (2) p = c * 2^k + 1 where 2^k >= N, (3) there exists a primitive n-th root of unity g^{(p-1)/n} mod p. Common choices: 998244353 = 119 * 2^23 + 1 (primitive root 3), 167772161 = 5 * 2^25 + 1 (primitive root 3).

## Computational Complexity

The number of floating-point operations in FFT for N = 2^k: each of log N stages does N/2 butterflies, each butterfly has 1 complex multiplication and 2 complex additions = 6 real operations + 4 real multiplications. Total: about 5N log N real operations. This is orders of magnitude faster than the N^2 operations of the naive DFT.