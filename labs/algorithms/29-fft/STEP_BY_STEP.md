# FFT — Step by Step Guide

## Step 1: Implement Complex Numbers

Create Complex class with real and imag fields. Implement plus, minus, times, conjugate methods. Implement toString for debugging.

## Step 2: Implement Bit-Reversal

Compute rev array: rev[0] = 0; for (int i = 1; i < n; i++) rev[i] = rev[i>>1] >> 1 | (i & 1) << (logN-1). Reorder input array: for i = 0..n-1: if i < rev[i], swap a[i] and a[rev[i]].

## Step 3: Implement FFT Butterfly

For len = 2,4,8,...,n: compute wlen = exp(-2*pi*i/len) (or exp(2*pi*i/len) for inverse). For i = 0..n-1 step len: initialize w = 1. For j = 0..len/2-1: u = a[i+j]; v = a[i+j+len/2] * w; a[i+j] = u + v; a[i+j+len/2] = u - v; w = w * wlen.

## Step 4: Implement Inverse FFT

Same loop, but wlen uses positive exponent. After the loop, divide all elements by n.

## Step 5: Test FFT with Known Signals

FFT of [1,0,0,0] should give all ones. FFT of [1,1,1,1] should be [4,0,0,0]. Test round-trip: IFFT(FFT(x)) should equal x.

## Step 6: Implement Polynomial Multiplication

Pad polynomials, FFT, multiply pointwise, IFFT, round. Test: multiply (1+2x+3x^2) by (4+5x+6x^2) should give (4+13x+28x^2+27x^3+18x^4).

## Step 7: Implement NTT

Replace Complex with long mod operations. Choose prime 998244353. Find primitive root 3. Verify ntt/Intt round-trip with integer arrays.