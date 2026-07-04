# Architecture of Trigonometry

## Java Math Library

```
java.lang.Math
├── sin(double)        → sine
├── cos(double)        → cosine
├── tan(double)        → tangent
├── asin(double)       → arc sine
├── acos(double)       → arc cosine
├── atan(double)       → arc tangent
├── atan2(double,double) → quadrant-aware arc tangent
├── sinh/cosh/tanh     → hyperbolic functions
└── toRadians/toDegrees → conversion
```

## Signal Processing Pipeline

```
Time Domain          Frequency Domain
┌─────────┐  FFT   ┌──────────────┐
│ samples │ ─────→ │ sin/cos bins │
└─────────┘        └──────────────┘
                        │
                   filter/analyze
                        │
                   inverse FFT
                        ↓
                  modified samples
```

## Fourier Transform Family

```
Fourier Series              (periodic, continuous)
     ↓
Fourier Transform          (aperiodic, continuous)
     ↓
Discrete Fourier Transform  (periodic, discrete)
     ↓
Fast Fourier Transform      (DFT in O(n log n))
```

## 3D Graphics Pipeline

```
Object (local coords)
    → Model matrix (rotation uses sin/cos)
    → View matrix (camera look-at uses atan2)
    → Projection matrix (frustum uses tan)
    → Screen
```
