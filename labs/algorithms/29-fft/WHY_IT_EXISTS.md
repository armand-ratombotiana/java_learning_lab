# Why FFT Exists

The Fast Fourier Transform exists because the Discrete Fourier Transform is too slow in practice. The naive DFT requires O(n^2) operations, which for a 1024-point signal means over a million complex multiplications. The Cooley-Tukey FFT reduces this to O(n log n), making real-time signal processing feasible.

FFT exists because many problems become simpler in the frequency domain. Convolution, which naively requires O(n^2), becomes pointwise multiplication in the frequency domain. Correlation, cross-correlation, and polynomial multiplication all benefit from this transformation.

FFT exists because of the structure of roots of unity. The DFT matrix has a special recursive structure that can be exploited. The Cooley-Tukey algorithm divides the problem size in half recursively, reusing computations. This divide-and-conquer pattern is a direct consequence of the algebraic properties of complex roots of unity.

The Number Theoretic Transform (NTT) exists because floating-point arithmetic introduces rounding errors. In exact computation contexts (polynomial arithmetic, competitive programming), we need exact integer results. NTT uses primitive roots modulo a prime, achieving the same algorithmic structure as FFT but in a finite field where operations are exact.