package com.algorithms.fft;

/**
 * Custom: Fast Fourier Transform (Cooley-Tukey)
 * Efficient polynomial multiplication in O(n log n).
 *
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 */
public class FFT {

    public static class Complex {
        double re, im;
        Complex(double re, double im) { this.re = re; this.im = im; }
        Complex add(Complex o) { return new Complex(re + o.re, im + o.im); }
        Complex sub(Complex o) { return new Complex(re - o.re, im - o.im); }
        Complex mul(Complex o) { return new Complex(re * o.re - im * o.im, re * o.im + im * o.re); }
    }

    public void fft(Complex[] a, boolean invert) {
        int n = a.length;
        for (int i = 1, j = 0; i < n; i++) {
            int bit = n >> 1;
            for (; (j & bit) != 0; bit >>= 1) j ^= bit;
            j ^= bit;
            if (i < j) { Complex t = a[i]; a[i] = a[j]; a[j] = t; }
        }
        for (int len = 2; len <= n; len <<= 1) {
            double ang = 2 * Math.PI / len * (invert ? -1 : 1);
            Complex wlen = new Complex(Math.cos(ang), Math.sin(ang));
            for (int i = 0; i < n; i += len) {
                Complex w = new Complex(1, 0);
                for (int j = 0; j < len / 2; j++) {
                    Complex u = a[i + j];
                    Complex v = a[i + j + len / 2].mul(w);
                    a[i + j] = u.add(v);
                    a[i + j + len / 2] = u.sub(v);
                    w = w.mul(wlen);
                }
            }
        }
        if (invert) for (int i = 0; i < n; i++) { a[i].re /= n; a[i].im /= n; }
    }

    public int[] multiply(int[] a, int[] b) {
        int n = 1;
        while (n < a.length + b.length) n <<= 1;
        Complex[] fa = new Complex[n], fb = new Complex[n];
        for (int i = 0; i < n; i++) {
            fa[i] = new Complex(i < a.length ? a[i] : 0, 0);
            fb[i] = new Complex(i < b.length ? b[i] : 0, 0);
        }
        fft(fa, false); fft(fb, false);
        for (int i = 0; i < n; i++) fa[i] = fa[i].mul(fb[i]);
        fft(fa, true);
        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = (int) Math.round(fa[i].re);
        return result;
    }

    public static void main(String[] args) {
        FFT fft = new FFT();
        // (1 + 2x) * (3 + 4x) = 3 + 10x + 8x^2
        int[] a = { 1, 2 };
        int[] b = { 3, 4 };
        int[] r = fft.multiply(a, b);
        System.out.print("Polynomial multiplication: ");
        for (int i = 0; i < r.length && i < 5; i++) System.out.print(r[i] + " ");
        System.out.println("(expected first 3: 3 10 8)");
    }
}
