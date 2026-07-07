# FFT — Mental Models

## Prism Separating Light

The Fourier Transform is like a prism splitting white light into its component colors. A time-domain signal (like a sound wave) is the white light. The frequency-domain output is the rainbow of colors, each representing a different frequency component. The inverse transform reassembles the colors back into white light, just like a second prism.

## Divide and Conquer on Roots

The Cooley-Tukey FFT exploits the symmetry of roots of unity on the complex unit circle. Imagine all points on a circle at equal angles (like pizza slices). The FFT recursively splits the pizza into even-numbered slices and odd-numbered slices, processes them separately, and combines. The root-of-unity symmetry means that rotating by half a circle gives -1, and the sums in the DFT can be decomposed.

## Convolution as Polynomial Coefficients

Multiplying two polynomials corresponds to convolving their coefficient sequences. Each coefficient in the result is a sum of products of coefficients from the two input polynomials at positions that sum to the output index. This is like multiplying two numbers digit by digit (convolution of digits). FFT makes this O(n log n) instead of O(n^2).

## NTT as Modular Clock Arithmetic

NTT replaces complex rotations with modular arithmetic. The primitive n-th root of unity in a finite field is like a clock where multiplying by the root steps through all possible values before returning to 1 after n steps. The modular prime field ensures exact integer arithmetic with no floating-point errors.

## Convolution as Overlapping Waveforms

In signal processing, convolution is like sliding one waveform past another and measuring the overlap area at each offset. If you record an echo, the original sound is convolved with the room's impulse response. FFT enables real-time convolution for audio effects: convert to frequency domain, multiply, and convert back.