# Architecture — Number Theory

## Library Design

`
NumberTheory Library
├── GCD
│   ├── Euclidean
│   └── ExtendedEuclidean
├── Primality
│   ├── SimpleSieve
│   ├── SegmentedSieve
│   └── MillerRabin
├── Modular
│   ├── Exponentiation
│   ├── Inverse
│   └── Arithmetic
├── CRT
│   ├── Solver
│   └── Garner (mixed radix)
└── Totient
    └── EulerTotient
`

## Integration

- Used as foundation for cryptographic libraries
- Integrated into math competition and programming contest libraries
- Foundation for algebraic algorithms (polynomial GCD, finite fields)
