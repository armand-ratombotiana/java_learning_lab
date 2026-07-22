# Interview Questions: FFT (Fast Fourier Transform)

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 43 Multiply Strings | Medium | Google, Meta, Amazon, Microsoft | Schoolbook / FFT |
| LC 1922 Count Good Numbers | Medium | Google | Modular exponentiation |
| LC 1977 Number of Ways to Separate | Hard | Google | DP + LCP |

Note: FFT has few direct LeetCode problems. It appears in advanced algorithm interviews and signal processing roles.

## NeetCode Reference
Not covered in NeetCode 150. FFT is considered an advanced/hard interview topic.

## Company-Specific Questions
### Google
- Multiply two large integers using FFT (for roles requiring numeric computation)
- Explain the Cooley-Tukey algorithm and its O(n log n) complexity
- How would you use FFT for polynomial multiplication?
- Design a system for pattern matching with wildcards using FFT

### Microsoft
- How does FFT accelerate deep learning convolutions?
- Implement convolution using FFT for signal processing
- Explain the butterfly network and its hardware implementations
- How would you use FFT in Excel for Fourier analysis?

### Meta
- FFT for audio/video processing at scale
- How would you detect similar images using FFT-based hashing?
- Design a system for music identification (like Shazam) using FFT
- Not commonly asked in SWE roles; more for ML/signal processing

### Amazon
- FFT for Alexa voice processing
- How does product image comparison use frequency domain features?
- Design a system for audio fingerprinting

### Apple
- How does Core Audio use FFT for real-time audio processing?
- FFT-accelerated image processing on iOS devices (GPU vs CPU)
- Design a power-efficient FFT implementation for mobile
- How does the vDSP framework accelerate FFT on Apple Silicon?

### Oracle
- FFT for time-series analysis in Oracle Database
- How would you implement signal processing in PL/SQL?
- Not typically asked; appears in specialized data science roles

## Real Production Scenarios
- Scenario 1: Audio fingerprinting - using FFT to convert audio signals into frequency-domain fingerprints for music identification service serving millions of queries daily
- Scenario 2: Image deduplication - applying FFT-based perceptual hashing to detect near-duplicate images across a billion-image catalog with rotation and scale invariance
- Scenario 3: Network anomaly detection - debugging an FFT-based spectral analysis pipeline that produces false positives due to windowing artifacts in the frequency domain

## Interview Tips
- FFT converts O(n^2) convolution to O(n log n) using divide and conquer on roots of unity
- Know that Cooley-Tukey works for any n = 2^k (radix-2); mixed-radix for other sizes
- Convolution theorem: convolution in time domain = pointwise multiplication in frequency domain
- Common edge cases: non-power-of-two n (zero-padding), numerical precision (use double or NTT)

## Java-Specific Considerations
- `java.lang.Math` provides `sin`, `cos`, `PI` needed for complex roots of unity
- Implement complex numbers as `class Complex { double re, im; }` or use `double[][]` for real/imag
- For production: use optimized libraries like JTransforms or Apache Commons Math
- NTT (Number Theoretic Transform) avoids floating-point issues with modular arithmetic
- Pitfall: numerical instability with large FFT sizes due to floating-point accumulation errors
- Pitfall: recursion overhead in naive Cooley-Tukey; iterative in-place FFT is preferred
- Bit-reversal permutation is needed for in-place FFT: `int rev = (rev >> 1) | (i & 1) << (logN - 1)`
