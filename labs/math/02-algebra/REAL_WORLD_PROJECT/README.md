# Real World Project: Signal Processing & Fourier Transform Simulator

## Project Overview
Build a signal processing system demonstrating practical applications of algebra, polynomials, and complex numbers in signal analysis and frequency decomposition.

## Project Structure
```
REAL_WORLD_PROJECT/
├── ComplexNumber.java
├── SignalProcessor.java
├── FourierTransform.java
├── PolynomialFilter.java
└── SignalProcessingDemo.java
```

## Implementation

### ComplexNumber.java
```java
package com.mathacademy.algebra.realworld;

import java.util.Objects;

public class ComplexNumber {
    private final double real;
    private final double imaginary;
    
    public ComplexNumber(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }
    
    public static ComplexNumber fromPolar(double r, double theta) {
        return new ComplexNumber(r * Math.cos(theta), r * Math.sin(theta));
    }
    
    public static ComplexNumber exp(ComplexNumber z) {
        double expReal = Math.exp(z.real);
        return new ComplexNumber(expReal * Math.cos(z.imaginary), 
                               expReal * Math.sin(z.imaginary));
    }
    
    public ComplexNumber add(ComplexNumber other) {
        return new ComplexNumber(this.real + other.real, 
                               this.imaginary + other.imaginary);
    }
    
    public ComplexNumber subtract(ComplexNumber other) {
        return new ComplexNumber(this.real - other.real, 
                               this.imaginary - other.imaginary);
    }
    
    public ComplexNumber multiply(ComplexNumber other) {
        double newReal = this.real * other.real - this.imaginary * other.imaginary;
        double newImag = this.real * other.imaginary + this.imaginary * other.real;
        return new ComplexNumber(newReal, newImag);
    }
    
    public ComplexNumber multiply(double scalar) {
        return new ComplexNumber(real * scalar, imaginary * scalar);
    }
    
    public ComplexNumber divide(ComplexNumber other) {
        double denominator = other.real * other.real + other.imaginary * other.imaginary;
        if (denominator == 0) {
            throw new ArithmeticException("Division by zero");
        }
        double newReal = (this.real * other.real + this.imaginary * other.imaginary) / denominator;
        double newImag = (this.imaginary * other.real - this.real * other.imaginary) / denominator;
        return new ComplexNumber(newReal, newImag);
    }
    
    public ComplexNumber conjugate() {
        return new ComplexNumber(real, -imaginary);
    }
    
    public double modulus() {
        return Math.sqrt(real * real + imaginary * imaginary);
    }
    
    public double modulusSquared() {
        return real * real + imaginary * imaginary;
    }
    
    public double argument() {
        return Math.atan2(imaginary, real);
    }
    
    public ComplexNumber power(int n) {
        double r = modulus();
        double theta = argument();
        double newR = Math.pow(r, n);
        double newTheta = theta * n;
        return ComplexNumber.fromPolar(newR, newTheta);
    }
    
    public ComplexNumber sqrt() {
        double r = Math.sqrt(modulus());
        double theta = argument() / 2;
        return ComplexNumber.fromPolar(r, theta);
    }
    
    @Override
    public String toString() {
        if (Math.abs(imaginary) < 1e-10) {
            return String.format("%.4f", real);
        }
        if (Math.abs(real) < 1e-10) {
            return String.format("%.4fi", imaginary);
        }
        String sign = imaginary >= 0 ? "+" : "-";
        return String.format("%.4f %s %.4fi", real, sign, Math.abs(imaginary));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof ComplexNumber) {
            ComplexNumber other = (ComplexNumber) obj;
            return Math.abs(real - other.real) < 1e-10 &&
                   Math.abs(imaginary - other.imaginary) < 1e-10;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(Math.round(real * 1e6), Math.round(imaginary * 1e6));
    }
    
    public double getReal() { return real; }
    public double getImaginary() { return imaginary; }
}
```

### SignalProcessor.java
```java
package com.mathacademy.algebra.realworld;

import java.util.ArrayList;
import java.util.List;

public class SignalProcessor {
    
    public static class Signal {
        private double[] samples;
        private double sampleRate;
        
        public Signal(double[] samples, double sampleRate) {
            this.samples = samples;
            this.sampleRate = sampleRate;
        }
        
        public Signal(int length, double sampleRate) {
            this.samples = new double[length];
            this.sampleRate = sampleRate;
        }
        
        public double getSample(int i) {
            return samples[i];
        }
        
        public void setSample(int i, double value) {
            samples[i] = value;
        }
        
        public int length() {
            return samples.length;
        }
        
        public double getSampleRate() {
            return sampleRate;
        }
        
        public double[] getSamples() {
            return samples;
        }
        
        public Signal copy() {
            return new Signal(samples.clone(), sampleRate);
        }
    }
    
    public static Signal generateSineWave(double frequency, double duration, double sampleRate) {
        int numSamples = (int) (duration * sampleRate);
        double[] samples = new double[numSamples];
        for (int i = 0; i < numSamples; i++) {
            double t = i / sampleRate;
            samples[i] = Math.sin(2 * Math.PI * frequency * t);
        }
        return new Signal(samples, sampleRate);
    }
    
    public static Signal generateSquareWave(double frequency, double duration, double sampleRate) {
        int numSamples = (int) (duration * sampleRate);
        double[] samples = new double[numSamples];
        for (int i = 0; i < numSamples; i++) {
            double t = i / sampleRate;
            samples[i] = Math.signum(Math.sin(2 * Math.PI * frequency * t));
        }
        return new Signal(samples, sampleRate);
    }
    
    public static Signal generateSawtoothWave(double frequency, double duration, double sampleRate) {
        int numSamples = (int) (duration * sampleRate);
        double[] samples = new double[numSamples];
        for (int i = 0; i < numSamples; i++) {
            double t = i / sampleRate;
            samples[i] = 2 * (frequency * t % 1) - 1;
        }
        return new Signal(samples, sampleRate);
    }
    
    public static Signal addSignals(Signal a, Signal b) {
        int length = Math.min(a.length(), b.length());
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = a.getSample(i) + b.getSample(i);
        }
        return new Signal(result, a.getSampleRate());
    }
    
    public static Signal multiplySignals(Signal a, Signal b) {
        int length = Math.min(a.length(), b.length());
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = a.getSample(i) * b.getSample(i);
        }
        return new Signal(result, a.getSampleRate());
    }
    
    public static Signal scalarMultiply(Signal s, double scalar) {
        double[] result = new double[s.length()];
        for (int i = 0; i < s.length(); i++) {
            result[i] = s.getSample(i) * scalar;
        }
        return new Signal(result, s.getSampleRate());
    }
    
    public static Signal convolve(Signal a, Signal b) {
        int n = a.length() + b.length() - 1;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            result[i] = 0;
            for (int j = 0; j < a.length(); j++) {
                if (i - j >= 0 && i - j < b.length()) {
                    result[i] += a.getSample(j) * b.getSample(i - j);
                }
            }
        }
        return new Signal(result, a.getSampleRate());
    }
    
    public static double[] autocorrelation(Signal s) {
        int n = s.length();
        double[] result = new double[n];
        for (int lag = 0; lag < n; lag++) {
            double sum = 0;
            for (int i = 0; i < n - lag; i++) {
                sum += s.getSample(i) * s.getSample(i + lag);
            }
            result[lag] = sum;
        }
        return result;
    }
    
    public static Signal lowPassFilter(Signal input, double cutoffFreq) {
        double sampleRate = input.getSampleRate();
        int n = input.length();
        double[] filtered = new double[n];
        double rc = 1.0 / (2 * Math.PI * cutoffFreq);
        double dt = 1.0 / sampleRate;
        double alpha = dt / (rc + dt);
        filtered[0] = input.getSample(0);
        for (int i = 1; i < n; i++) {
            filtered[i] = filtered[i - 1] + alpha * (input.getSample(i) - filtered[i - 1]);
        }
        return new Signal(filtered, sampleRate);
    }
    
    public static Signal highPassFilter(Signal input, double cutoffFreq) {
        Signal lowPassed = lowPassFilter(input, cutoffFreq);
        double[] result = new double[input.length()];
        for (int i = 0; i < input.length(); i++) {
            result[i] = input.getSample(i) - lowPassed.getSample(i);
        }
        return new Signal(result, input.getSampleRate());
    }
    
    public static double[] computeFFT(double[] signal) {
        int n = signal.length;
        if (n <= 1) return signal;
        double[] even = new double[n / 2];
        double[] odd = new double[n / 2];
        for (int i = 0; i < n / 2; i++) {
            even[i] = signal[2 * i];
            odd[i] = signal[2 * i + 1];
        }
        even = computeFFT(even);
        odd = computeFFT(odd);
        double[] result = new double[n];
        for (int i = 0; i < n / 2; i++) {
            double theta = -2 * Math.PI * i / n;
            ComplexNumber t = ComplexNumber.fromPolar(1, theta);
            double realPart = t.getReal() * odd[i] - t.getImaginary() * odd[i];
            result[i] = even[i] + realPart;
            result[i + n / 2] = even[i] - realPart;
        }
        return result;
    }
    
    public static ComplexNumber[] complexFFT(ComplexNumber[] signal) {
        int n = signal.length;
        if (n <= 1) return signal;
        ComplexNumber[] even = new ComplexNumber[n / 2];
        ComplexNumber[] odd = new ComplexNumber[n / 2];
        for (int i = 0; i < n / 2; i++) {
            even[i] = signal[2 * i];
            odd[i] = signal[2 * i + 1];
        }
        even = complexFFT(even);
        odd = complexFFT(odd);
        ComplexNumber[] result = new ComplexNumber[n];
        for (int i = 0; i < n / 2; i++) {
            double theta = -2 * Math.PI * i / n;
            ComplexNumber t = ComplexNumber.fromPolar(1, theta).multiply(odd[i]);
            result[i] = even[i].add(t);
            result[i + n / 2] = even[i].subtract(t);
        }
        return result;
    }
    
    public static double[] powerSpectrum(Signal signal) {
        double[] samples = signal.getSamples();
        int n = samples.length;
        ComplexNumber[] complexSignal = new ComplexNumber[n];
        for (int i = 0; i < n; i++) {
            complexSignal[i] = new ComplexNumber(samples[i], 0);
        }
        ComplexNumber[] fft = complexFFT(complexSignal);
        double[] spectrum = new double[n / 2];
        for (int i = 0; i < n / 2; i++) {
            spectrum[i] = fft[i].modulusSquared() / n;
        }
        return spectrum;
    }
    
    public static double rms(Signal signal) {
        double sumSquares = 0;
        for (int i = 0; i < signal.length(); i++) {
            sumSquares += signal.getSample(i) * signal.getSample(i);
        }
        return Math.sqrt(sumSquares / signal.length());
    }
    
    public static double meanSquareError(Signal a, Signal b) {
        int length = Math.min(a.length(), b.length());
        double sum = 0;
        for (int i = 0; i < length; i++) {
            double diff = a.getSample(i) - b.getSample(i);
            sum += diff * diff;
        }
        return sum / length;
    }
}
```

### FourierTransform.java
```java
package com.mathacademy.algebra.realworld;

import java.util.ArrayList;
import java.util.List;

public class FourierTransform {
    
    public static class FrequencyComponent {
        private double frequency;
        private double amplitude;
        private double phase;
        
        public FrequencyComponent(double frequency, double amplitude, double phase) {
            this.frequency = frequency;
            this.amplitude = amplitude;
            this.phase = phase;
        }
        
        public double getFrequency() { return frequency; }
        public double getAmplitude() { return amplitude; }
        public double getPhase() { return phase; }
        
        @Override
        public String toString() {
            return String.format("f=%.2fHz, A=%.4f, phi=%.4f rad", frequency, amplitude, phase);
        }
    }
    
    public static ComplexNumber[] discreteFT(double[] signal) {
        int n = signal.length;
        ComplexNumber[] result = new ComplexNumber[n];
        for (int k = 0; k < n; k++) {
            double realSum = 0;
            double imagSum = 0;
            for (int t = 0; t < n; t++) {
                double angle = -2 * Math.PI * k * t / n;
                realSum += signal[t] * Math.cos(angle);
                imagSum += signal[t] * Math.sin(angle);
            }
            result[k] = new ComplexNumber(realSum, imagSum);
        }
        return result;
    }
    
    public static double[] inverseDFT(ComplexNumber[] freqDomain) {
        int n = freqDomain.length;
        double[] result = new double[n];
        for (int t = 0; t < n; t++) {
            double sum = 0;
            for (int k = 0; k < n; k++) {
                double angle = 2 * Math.PI * k * t / n;
                sum += freqDomain[k].getReal() * Math.cos(angle) - 
                       freqDomain[k].getImaginary() * Math.sin(angle);
            }
            result[t] = sum / n;
        }
        return result;
    }
    
    public static List<FrequencyComponent> analyzeFrequencies(SignalProcessor.Signal signal) {
        double[] samples = signal.getSamples();
        double sampleRate = signal.getSampleRate();
        int n = samples.length;
        ComplexNumber[] dft = discreteFT(samples);
        List<FrequencyComponent> components = new ArrayList<>();
        for (int k = 0; k < n / 2; k++) {
            double frequency = k * sampleRate / n;
            double amplitude = dft[k].modulus() * 2.0 / n;
            double phase = dft[k].argument();
            if (amplitude > 0.01) {
                components.add(new FrequencyComponent(frequency, amplitude, phase));
            }
        }
        return components;
    }
    
    public static Signal reconstructFromComponents(List<FrequencyComponent> components, 
                                                   double duration, double sampleRate) {
        int numSamples = (int) (duration * sampleRate);
        double[] samples = new double[numSamples];
        for (int i = 0; i < numSamples; i++) {
            double t = i / sampleRate;
            double sum = 0;
            for (FrequencyComponent comp : components) {
                sum += comp.getAmplitude() * Math.cos(2 * Math.PI * comp.getFrequency() * t + comp.getPhase());
            }
            samples[i] = sum;
        }
        return new SignalProcessor.Signal(samples, sampleRate);
    }
    
    public static double[] windowFunction(int n, String type) {
        double[] window = new double[n];
        switch (type.toLowerCase()) {
            case "hamming":
                for (int i = 0; i < n; i++) {
                    window[i] = 0.54 - 0.46 * Math.cos(2 * Math.PI * i / (n - 1));
                }
                break;
            case "hanning":
                for (int i = 0; i < n; i++) {
                    window[i] = 0.5 - 0.5 * Math.cos(2 * Math.PI * i / (n - 1));
                }
                break;
            default:
                for (int i = 0; i < n; i++) {
                    window[i] = 1.0;
                }
        }
        return window;
    }
    
    public static Signal applyWindow(Signal signal, String windowType) {
        double[] window = windowFunction(signal.length(), windowType);
        double[] windowed = new double[signal.length()];
        for (int i = 0; i < signal.length(); i++) {
            windowed[i] = signal.getSample(i) * window[i];
        }
        return new SignalProcessor.Signal(windowed, signal.getSampleRate());
    }
    
    public static double[] lowPassKernel(int size, double cutoff) {
        double[] kernel = new double[size];
        int center = size / 2;
        for (int i = 0; i < size; i++) {
            int n = i - center;
            if (n == 0) {
                kernel[i] = 2 * Math.PI * cutoff;
            } else {
                kernel[i] = Math.sin(2 * Math.PI * cutoff * n) / n;
            }
            kernel[i] *= 0.5 + 0.5 * Math.cos(Math.PI * n / center);
        }
        double sum = 0;
        for (double k : kernel) {
            sum += k;
        }
        for (int i = 0; i < size; i++) {
            kernel[i] /= sum;
        }
        return kernel;
    }
}
```

### PolynomialFilter.java
```java
package com.mathacademy.algebra.realworld;

public class PolynomialFilter {
    
    public static class FilterCoefficients {
        double[] a;
        double[] b;
        
        public FilterCoefficients(double[] a, double[] b) {
            this.a = a;
            this.b = b;
        }
    }
    
    public static FilterCoefficients designLowPass(int order, double cutoffFreq, double sampleRate) {
        double[] a = new double[order + 1];
        double[] b = new double[order + 1];
        double wc = 2 * Math.PI * cutoffFreq / sampleRate;
        for (int n = 0; n <= order; n++) {
            a[n] = Math.cos(n * wc) / (n + 1);
            b[n] = 2 * Math.cos(n * wc) / (n + 1);
        }
        a[0] = 1;
        return new FilterCoefficients(a, b);
    }
    
    public static SignalProcessor.Signal applyFilter(SignalProcessor.Signal input, FilterCoefficients filter) {
        int outputLength = input.length();
        double[] output = new double[outputLength];
        for (int n = 0; n < outputLength; n++) {
            double y = 0;
            for (int i = 0; i < filter.b.length && i <= n; i++) {
                y += filter.b[i] * input.getSample(n - i);
            }
            output[n] = y / filter.a[0];
        }
        return new SignalProcessor.Signal(output, input.getSampleRate());
    }
    
    public static double[] polynomialSmoothing(double[] data, int windowSize, int degree) {
        int n = data.length;
        double[] smoothed = new double[n];
        for (int i = 0; i < n; i++) {
            int start = Math.max(0, i - windowSize / 2);
            int end = Math.min(n - 1, i + windowSize / 2);
            double[][] X = new double[end - start + 1][degree + 1];
            double[] y = new double[end - start + 1];
            for (int j = 0; j <= end - start; j++) {
                y[j] = data[start + j];
                for (int p = 0; p <= degree; p++) {
                    X[j][p] = Math.pow(j, p);
                }
            }
            double[] coeffs = leastSquares(X, y);
            int centerIdx = i - start;
            double value = 0;
            for (int p = 0; p <= degree; p++) {
                value += coeffs[p] * Math.pow(centerIdx, p);
            }
            smoothed[i] = value;
        }
        return smoothed;
    }
    
    private static double[] leastSquares(double[][] X, double[] y) {
        int m = X.length;
        int n = X[0].length;
        double[][] XtX = new double[n][n];
        double[] Xty = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                XtX[i][j] = 0;
                for (int k = 0; k < m; k++) {
                    XtX[i][j] += X[k][i] * X[k][j];
                }
            }
        }
        for (int i = 0; i < n; i++) {
            Xty[i] = 0;
            for (int k = 0; k < m; k++) {
                Xty[i] += X[k][i] * y[k];
            }
        }
        return solveLinearSystem(XtX, Xty);
    }
    
    private static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = b.length;
        double[][] aug = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                aug[i][j] = A[i][j];
            }
            aug[i][n] = b[i];
        }
        for (int i = 0; i < n; i++) {
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(aug[k][i]) > Math.abs(aug[maxRow][i])) {
                    maxRow = k;
                }
            }
            double[] temp = aug[i];
            aug[i] = aug[maxRow];
            aug[maxRow] = temp;
            if (Math.abs(aug[i][i]) < 1e-10) continue;
            for (int k = i + 1; k < n; k++) {
                double factor = aug[k][i] / aug[i][i];
                for (int j = i; j <= n; j++) {
                    aug[k][j] -= factor * aug[i][j];
                }
            }
        }
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = aug[i][n];
            for (int j = i + 1; j < n; j++) {
                x[i] -= aug[i][j] * x[j];
            }
            x[i] /= aug[i][i];
        }
        return x;
    }
}
```

### SignalProcessingDemo.java
```java
package com.mathacademy.algebra.realworld;

public class SignalProcessingDemo {
    
    public static void main(String[] args) {
        System.out.println("SIGNAL PROCESSING & FOURIER TRANSFORM DEMO");
        demonstrateWaveGeneration();
        demonstrateFFT();
        demonstrateFiltering();
        demonstrateSpectralAnalysis();
    }
    
    private static void demonstrateWaveGeneration() {
        System.out.println("\n1. WAVE GENERATION");
        double sampleRate = 1000;
        SignalProcessor.Signal sine = SignalProcessor.generateSineWave(50, 0.1, sampleRate);
        System.out.println("Generated 50Hz sine wave: " + sine.length() + " samples");
        System.out.printf("  RMS: %.4f%n", SignalProcessor.rms(sine));
    }
    
    private static void demonstrateFFT() {
        System.out.println("\n2. FAST FOURIER TRANSFORM");
        double sampleRate = 1000;
        SignalProcessor.Signal signal = SignalProcessor.generateSineWave(100, 0.1, sampleRate);
        ComplexNumber[] dft = FourierTransform.discreteFT(signal.getSamples());
        int peakIndex = 0;
        double peakMag = 0;
        for (int i = 1; i < dft.length / 2; i++) {
            double mag = dft[i].modulus();
            if (mag > peakMag) {
                peakMag = mag;
                peakIndex = i;
            }
        }
        double peakFreq = peakIndex * sampleRate / dft.length;
        System.out.printf("  Peak frequency: %.2f Hz%n", peakFreq);
    }
    
    private static void demonstrateFiltering() {
        System.out.println("\n3. SIGNAL FILTERING");
        double sampleRate = 1000;
        SignalProcessor.Signal signal = SignalProcessor.generateSineWave(50, 0.5, sampleRate);
        for (int i = 200; i < 300; i++) {
            signal.setSample(i, signal.getSample(i) + 0.5);
        }
        System.out.printf("  Original RMS: %.4f%n", SignalProcessor.rms(signal));
        SignalProcessor.Signal filtered = SignalProcessor.lowPassFilter(signal, 100);
        System.out.printf("  Filtered RMS: %.4f%n", SignalProcessor.rms(filtered));
    }
    
    private static void demonstrateSpectralAnalysis() {
        System.out.println("\n4. SPECTRAL ANALYSIS");
        double sampleRate = 1000;
        SignalProcessor.Signal signal1 = SignalProcessor.generateSineWave(60, 0.2, sampleRate);
        SignalProcessor.Signal signal2 = SignalProcessor.generateSineWave(140, 0.2, sampleRate);
        SignalProcessor.Signal combined = SignalProcessor.addSignals(signal1, signal2);
        java.util.List<FourierTransform.FrequencyComponent> components = 
            FourierTransform.analyzeFrequencies(combined);
        System.out.println("  Detected components:");
        for (FourierTransform.FrequencyComponent comp : components) {
            System.out.printf("    %.2f Hz, amplitude %.4f%n", comp.getFrequency(), comp.getAmplitude());
        }
    }
}
```

## Running the Project

```bash
cd labs/math/02-algebra/REAL_WORLD_PROJECT
javac -d bin *.java
java com.mathacademy.algebra.realworld.SignalProcessingDemo
```

## Real-World Applications
1. Audio processing (MP3, AAC compression)
2. Image compression (JPEG)
3. telecommunications (modems)
4. Medical imaging (MRI, CT)
5. Vibration analysis in engineering