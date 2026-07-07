package com.algo.lab29;

public class FFT {

    public static class Complex {
        public final double re, im;

        public Complex(double re, double im) {
            this.re = re;
            this.im = im;
        }

        public Complex plus(Complex b) {
            return new Complex(re + b.re, im + b.im);
        }

        public Complex minus(Complex b) {
            return new Complex(re - b.re, im - b.im);
        }

        public Complex times(Complex b) {
            return new Complex(re * b.re - im * b.im, re * b.im + im * b.re);
        }

        public static Complex exp(double theta) {
            return new Complex(Math.cos(theta), Math.sin(theta));
        }
    }

    public static void fft(Complex[] a, boolean invert) {
        int n = a.length;
        int logN = Integer.numberOfLeadingZeros(0) - Integer.numberOfLeadingZeros(n);
        for (int i = 1, j = 0; i < n; i++) {
            int bit = n >> 1;
            for (; (j & bit) != 0; bit >>= 1) j ^= bit;
            j ^= bit;
            if (i < j) {
                Complex tmp = a[i];
                a[i] = a[j];
                a[j] = tmp;
            }
        }
        for (int len = 2; len <= n; len <<= 1) {
            double ang = 2 * Math.PI / len * (invert ? -1 : 1);
            Complex wlen = Complex.exp(ang);
            for (int i = 0; i < n; i += len) {
                Complex w = new Complex(1, 0);
                for (int j = 0; j < len / 2; j++) {
                    Complex u = a[i + j];
                    Complex v = a[i + j + len / 2].times(w);
                    a[i + j] = u.plus(v);
                    a[i + j + len / 2] = u.minus(v);
                    w = w.times(wlen);
                }
            }
        }
        if (invert) {
            for (int i = 0; i < n; i++) {
                a[i] = new Complex(a[i].re / n, a[i].im / n);
            }
        }
    }
}
