package com.algo.lab29;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FFTTest {

    @Test
    void testFFTRoundTrip() {
        int n = 4;
        FFT.Complex[] a = new FFT.Complex[n];
        for (int i = 0; i < n; i++) a[i] = new FFT.Complex(i + 1, 0);
        FFT.Complex[] original = new FFT.Complex[n];
        for (int i = 0; i < n; i++) original[i] = a[i];
        FFT.fft(a, false);
        FFT.fft(a, true);
        for (int i = 0; i < n; i++) {
            assertEquals(original[i].re, a[i].re, 1e-9);
        }
    }

    @Test
    void testPolynomialMultiply() {
        long[] a = {1, 2, 3};
        long[] b = {4, 5, 6};
        long[] c = PolynomialMultiply.multiply(a, b);
        assertArrayEquals(new long[]{4, 13, 28, 27, 18}, c);
    }

    @Test
    void testNTT() {
        int n = 8;
        long[] a = new long[n];
        for (int i = 0; i < n; i++) a[i] = i + 1;
        long[] original = a.clone();
        NTT.ntt(a, false);
        NTT.ntt(a, true);
        assertArrayEquals(original, a);
    }

    @Test
    void testFFTImpulse() {
        int n = 4;
        FFT.Complex[] a = new FFT.Complex[n];
        a[0] = new FFT.Complex(1, 0);
        for (int i = 1; i < n; i++) a[i] = new FFT.Complex(0, 0);
        FFT.fft(a, false);
        for (int i = 0; i < n; i++) {
            assertEquals(1.0, a[i].re, 1e-9);
            assertEquals(0.0, a[i].im, 1e-9);
        }
    }

    @Test
    void testNTTModPow() {
        assertEquals(1, NTT.modPow(3, 998244352));
    }
}
