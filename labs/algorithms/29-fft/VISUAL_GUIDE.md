# FFT — Visual Guide

## Complex Unit Circle

Roots of unity for N=8: points on unit circle at angles 0, 45, 90, 135, 180, 225, 270, 315 degrees. Each root omega_8^k = e^{2pi i k/8}. The primitive root omega_8 = e^{pi i/4} = cos(45) + i sin(45) = (sqrt(2)/2)(1 + i).

## Butterfly Diagram

The FFT butterfly: two inputs a and b, one output a + w*b, the other a - w*b. Visually: lines cross from left to right, with w applied to the lower line. At each stage, pairs are combined. For N=8: stage 1 combines (0,4), (1,5), (2,6), (3,7). Stage 2 combines (0,2), (1,3), (4,6), (5,7). Stage 3 combines (0,1), (2,3), (4,5), (6,7).

## Bit-Reversal Permutation

Input order for iterative FFT: not 0,1,2,3,4,5,6,7 but 0,4,2,6,1,5,3,7 (bit-reversed indices). 0=000->000=0, 1=001->100=4, 2=010->010=2, 3=011->110=6, 4=100->001=1, 5=101->101=5, 6=110->011=3, 7=111->111=7.

## Convolution Example

a=[1,2,3], b=[4,5,6]. Pad to size 4 (next power of 2 >= 5). FFT both, multiply pointwise, IFFT: c[0]=1*4=4, c[1]=1*5+2*4=13, c[2]=1*6+2*5+3*4=28, c[3]=2*6+3*5=27, c[4]=3*6=18.

## NTT Mod 998244353

Primitive root 3. For N=8: g = 3^{119*2^{20}} mod 998244353. Roots of unity: w_k = g^k mod MOD. Inverse NTT uses invG = 3^{-1} mod MOD and divides by N using modular inverse.