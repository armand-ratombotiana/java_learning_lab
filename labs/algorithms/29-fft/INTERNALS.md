# FFT — Internal Implementation Details

The FFT class implements the Cooley-Tukey radix-2 decimation-in-time FFT. Internal:
- Complex class: immutable complex number with real and imag parts. Methods: plus, minus, times, conjugate, exp (static factory for roots of unity).
- fft(Complex[] a, boolean invert): core method. Uses bit-reversal permutation first (reorder array). Then iterative combining: for each length len = 2, 4, 8, ..., N, compute wlen = exp(2*pi*i/len) (or inverse). For each block i = 0 to N-1 step len, for j = 0 to len/2-1, compute the butterfly: u = a[i+j]; v = a[i+j+len/2] * w; a[i+j] = u + v; a[i+j+len/2] = u - v; w = w * wlen.

The polynomial multiplication pads inputs to size >= n+m+1 (next power of 2), performs FFT on both, pointwise multiplies, performs inverse FFT, and rounds.

The NTT class uses a prime modulus (998244353, which is 119*2^23+1). The primitive root is 3. Internal:
- ntt(long[] a, boolean invert): similar structure to FFT but with modular arithmetic. Uses modular exponentiation for powers of the primitive root. The modular inverse of N is precomputed via pow(N, mod-2, mod).

Key difference from FFT: all operations are modulo p, using long arithmetic to avoid overflow (since p * p fits in 64 bits). The butterfly uses: u = a[i+j]; v = a[i+j+len/2] * w % MOD; a[i+j] = (u+v) % MOD; a[i+j+len/2] = (u - v + MOD) % MOD.

The iterative FFT eliminates recursion overhead and allows in-place computation. Bit-reversal precomputation creates an index array rev: for each i, rev[i] = rev[i>>1] >> 1 | (i & 1) << (logN-1).