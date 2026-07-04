# Why Trigonometry Matters

Trigonometric functions model every periodic phenomenon in nature and engineering.

## Applications

| Domain | Use |
|--------|-----|
| Signal Processing | Fourier transform, audio compression (MP3) |
| Computer Graphics | 3D rotations, camera look-at, lighting |
| Physics | Simple harmonic motion, wave propagation, AC circuits |
| Navigation | GPS trilateration, bearing calculations |
| Medical Imaging | CT scan reconstruction (Radon transform) |
| Electrical Eng. | AC phase analysis, impedance |

## In Java

```java
// Any periodic computation uses trig
double[] fft(List<Double> signal) { ... } // Fourier transform
double rotation = Math.atan2(y, x);       // angle from components
```
