# Fast Fourier Transform — Theoretical Foundation

## Discrete Fourier Transform

The DFT transforms a sequence of N complex numbers x_0, ..., x_{N-1} into another sequence X_0, ..., X_{N-1} where X_k = sum_{j=0}^{N-1} x_j * omega_N^{-jk}, with omega_N = e^{2 pi i / N}. The DFT is a linear transformation representable as an N x N Vandermonde matrix. Direct computation takes O(N^2) operations.

## Cooley-Tukey FFT

The Cooley-Tukey algorithm reduces DFT complexity to O(N log N) by exploiting the structure of the DFT matrix. For N = N1 * N2, the algorithm rearranges the 1D array into a 2D array of shape N1 x N2, computes DFTs along one dimension, multiplies by twiddle factors, and then computes DFTs along the other dimension. The most common variant is the radix-2 decimation-in-time FFT, which recursively splits the input into even and odd indices.

## Polynomial Multiplication via FFT

To multiply two polynomials A(x) and B(x) of degree n, evaluate A and B at 2n + 1 points using FFT, multiply pointwise, and then inverse FFT to recover coefficients. The total complexity is O(n log n), compared to O(n^2) for naive multiplication. This is the foundation of efficient polynomial arithmetic in computer algebra systems.

## Number Theoretic Transform

NTT replaces complex roots of unity with primitive roots modulo a prime p. NTT requires p = c * 2^k + 1 (Fermat primes like 998244353 = 119 * 2^23 + 1). This avoids floating-point errors entirely, producing exact integer results. The NTT uses the same divide-and-conquer structure as FFT but operates in a finite field.

## Convolution Theorem

Convolution in the time domain corresponds to pointwise multiplication in the frequency domain: if h = f * g, then H = F . G (where . denotes elementwise multiplication). This allows O(n log n) convolution, which is used in signal processing (filtering), image processing (blur, edge detection), and probability (sum of random variables).

## Applications in Signal Processing

FFT enables frequency analysis of signals: decompose a signal into its constituent frequencies, remove noise by zeroing out certain frequency components, and reconstruct using inverse FFT. This is used in audio compression (MP3), image compression (JPEG), and communications (OFDM).