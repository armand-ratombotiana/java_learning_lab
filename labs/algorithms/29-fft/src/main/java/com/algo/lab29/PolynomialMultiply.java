package com.algo.lab29;

public class PolynomialMultiply {

    public static long[] multiply(long[] a, long[] b) {
        int resultLen = a.length + b.length - 1;
        int n = 1;
        while (n < resultLen) n <<= 1;
        FFT.Complex[] ca = new FFT.Complex[n];
        FFT.Complex[] cb = new FFT.Complex[n];
        for (int i = 0; i < n; i++) {
            ca[i] = new FFT.Complex(i < a.length ? a[i] : 0, 0);
            cb[i] = new FFT.Complex(i < b.length ? b[i] : 0, 0);
        }
        FFT.fft(ca, false);
        FFT.fft(cb, false);
        for (int i = 0; i < n; i++) ca[i] = ca[i].times(cb[i]);
        FFT.fft(ca, true);
        long[] result = new long[resultLen];
        for (int i = 0; i < resultLen; i++) {
            result[i] = Math.round(ca[i].re);
        }
        return result;
    }
}
