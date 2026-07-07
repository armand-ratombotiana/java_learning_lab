# How FFT Works

## Cooley-Tukey Radix-2 FFT

Given an array a of length N (power of 2):
- If N == 1, return a.
- Split: even = a[0], a[2], ..., a[N-2]; odd = a[1], a[3], ..., a[N-1].
- Recursively compute evenFFT = FFT(even), oddFFT = FFT(odd).
- For k = 0 to N/2 - 1:
  - wk = omega^(-k) = exp(-2*pi*i*k/N) = cos(theta) - i*sin(theta).
  - t = wk * oddFFT[k].
  - result[k] = evenFFT[k] + t.
  - result[k + N/2] = evenFFT[k] - t.
- Return result.

The inverse FFT differs only by using omega^k instead of omega^(-k) and dividing by N at the end.

## Polynomial Multiplication

To multiply A(x) and B(x) of degree n:
- Pad A and B to length L = next power of 2 >= 2n + 1.
- Compute FFT(A) and FFT(B).
- Multiply pointwise: C[i] = A_fft[i] * B_fft[i].
- Compute inverse FFT: result = IFFT(C).
- Round real parts to nearest integer (coefficients).

## Number Theoretic Transform

NTT replaces complex primitive root omega with a primitive root g mod prime p:
- w = g^((p-1)/N) mod p.
- p must be a prime of the form c*2^k + 1 (e.g., 998244353 = 119*2^23 + 1).
- Everything else is identical to FFT but using modular arithmetic.
- Inverse NTT uses inverse of N modulo p and the inverse primitive root.

## Bit-Reversal Permutation

Before iterative (non-recursive) FFT, elements must be rearranged in bit-reversed order. For index i, the bit-reversed index rev[i] is computed by reversing the binary representation of i. The iterative FFT then processes the array in-place, combining pairs at each level.