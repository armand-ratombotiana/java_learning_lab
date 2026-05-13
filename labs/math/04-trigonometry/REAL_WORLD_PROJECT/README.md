# Real World Project: Wave Calculator

```java
package com.mathacademy.trigonometry.realworld;

public class WaveCalculator {
    public double[] generateSineWave(double amplitude, double frequency, double phase, int samples) {
        double[] wave = new double[samples];
        for (int i = 0; i < samples; i++) {
            wave[i] = amplitude * Math.sin(2 * Math.PI * frequency * i + phase);
        }
        return wave;
    }
    
    public double[] addWaves(double[] wave1, double[] wave2) {
        int len = Math.min(wave1.length, wave2.length);
        double[] result = new double[len];
        for (int i = 0; i < len; i++) result[i] = wave1[i] + wave2[i];
        return result;
    }
}
```