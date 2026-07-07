# FFT — Debugging Guide

Test FFT with impulse signal [1,0,0,...0]: result should be all ones. Test with constant signal [1,1,...,1]: result should be [n,0,0,...,0]. Verify round-trip: IFFT(FFT(x)) should equal x within floating-point tolerance. For polynomial multiplication, verify against naive O(n^2) multiplication. For NTT, verify |a - INTT(NTT(a))| == 0 mod MOD exactly.