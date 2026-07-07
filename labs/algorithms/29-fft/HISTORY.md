# History of FFT

1805: Carl Friedrich Gauss discovered the Fast Fourier Transform to interpolate asteroid orbits, but his work went unpublished and unrecognized.

1965: James Cooley and John Tukey published the modern FFT algorithm, demonstrating O(n log n) computation of the DFT. Their paper, "An algorithm for the machine calculation of complex Fourier series," is one of the most cited scientific papers.

1967: IBM released the first FFT subroutine library. The algorithm quickly became essential in digital signal processing.

1977: The NTT (Number Theoretic Transform) was introduced by Rader and independently by Pollard, replacing complex roots of unity with modular arithmetic for exact computation.

1987: Schönhage and Strassen used NTT for integer multiplication, achieving O(n log n log log n) time. This remains the fastest asymptotic integer multiplication algorithm.

1990s: Split-radix FFT variants reduced operation counts by combining radix-2 and radix-4 steps.

2000s: FFTW (Fastest Fourier Transform in the West) library by Frigo and Johnson used adaptive planning to choose optimal algorithms for each hardware configuration.

2010s: GPU-based FFT implementations (cuFFT, clFFT) made tera-scale FFTs routine for deep learning and scientific computing.

2020s: FFT remains essential in 5G/6G communication (OFDM), AI accelerators (convolution via FFT), and computational science.