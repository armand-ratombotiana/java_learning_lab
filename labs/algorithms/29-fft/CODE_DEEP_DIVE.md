# FFT — Code Deep Dive

The FFT class uses a Complex inner class for complex arithmetic. The fft method takes an array of Complex and a boolean invert. First, it applies bit-reversal permutation: for each i, if i < rev[i], swap a[i] and a[rev[i]].

The iterative butterfly loops: for (int len = 2; len <= n; len <<= 1) { double ang = 2 * Math.PI / len * (invert ? -1 : 1); Complex wlen = new Complex(Math.cos(ang), Math.sin(ang)); for (int i = 0; i < n; i += len) { Complex w = new Complex(1, 0); for (int j = 0; j < len/2; j++) { Complex u = a[i+j]; Complex v = a[i+j+len/2].times(w); a[i+j] = u.plus(v); a[i+j+len/2] = u.minus(v); w = w.times(wlen); } } }

After the loops, if invert, divide all elements by n.

The PolynomialMultiply class: pad a and b to size >= a.length + b.length - 1, next power of 2. Convert to Complex arrays, call FFT.fft on both, multiply pointwise, call FFT.fft with invert=true, round real parts.

The NTT class uses long[] arrays for modular arithmetic. The ntt method uses the same iterative structure but with modular arithmetic: long wlen = modPow(primitiveRoot, (MOD-1)/len, MOD); for invert, wlen = modPow(wlen, MOD-2, MOD). The butterfly uses: long u = a[i+j]; long v = a[i+j+len/2] * w % MOD; a[i+j] = (u + v) % MOD; a[i+j+len/2] = (u - v + MOD) % MOD.

Primitive root computation: factor (MOD-1), for candidate g = 2,3,5,... check g^{(MOD-1)/p} != 1 mod MOD for all prime factors p.