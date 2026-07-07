# Flashcards — FFT

Q: FFT time complexity?
A: O(N log N)

Q: Naive DFT complexity?
A: O(N^2)

Q: Butterfly operation?
A: a' = a + w*b; b' = a - w*b

Q: Convolution theorem?
A: DFT(f * g) = DFT(f) . DFT(g)

Q: What requires N to be power of two?
A: Radix-2 Cooley-Tukey FFT

Q: NTT replaces complex roots with?
A: Primitive roots of unity modulo a prime

Q: Common NTT modulus?
A: 998244353 = 119 * 2^23 + 1

Q: Bit-reversal permutation?
A: Reorder input array by reversed bit indices