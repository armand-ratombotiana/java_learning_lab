# Why FFT Matters

FFT matters because it is one of the most important numerical algorithms of the 20th century. It revolutionized signal processing, making real-time audio and video processing possible. Without FFT, we would not have MP3, JPEG, or modern telecommunications.

In audio processing, FFT decomposes sound into frequencies. Noise cancellation headphones analyze ambient noise and generate anti-noise signals. Speech recognition extracts frequency features (MFCCs) for classification. Music streaming services use FFT for visualization and compression.

In image processing, FFT enables filtering operations. Convolution with a Gaussian kernel (blurring) becomes multiplication in frequency space. JPEG compression uses the Discrete Cosine Transform (a close relative of FFT). Medical imaging (MRI, CT scans) relies on FFT for image reconstruction.

In telecommunications, OFDM (used in WiFi, 4G/5G, DVB) multiplexes data onto multiple orthogonal subcarriers. The modulation and demodulation are implemented with FFT. This enables high-bandwidth wireless communication.

In computational mathematics, FFT accelerates polynomial and integer multiplication. Schönhage-Strassen algorithm multiplies n-bit integers in O(n log n log log n). The GNU Multiple Precision Arithmetic Library uses FFT for large integer arithmetic. Computer algebra systems multiply polynomials in seconds that would take hours with naive methods.

In competitive programming, FFT enables solving problems that would otherwise be impossible: counting matching patterns with wildcards, computing correlations, summing convolutions of arrays, and multiplying large numbers exactly. FFT appears regularly in high-level contests like IOI and ICPC.