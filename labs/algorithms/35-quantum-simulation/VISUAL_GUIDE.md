# Quantum Simulation — Visual Guide

## Bloch Sphere

A single qubit is represented on the Bloch sphere. |0> is the north pole (theta=0), |1> is the south pole (theta=pi). Hadamard gate rotates from |0> to the equator: (|0>+|1>)/sqrt(2). Pauli-X flips between poles (theta -> pi-theta). Pauli-Z reflects through the equator (flips phase, not population).

## 2-Qubit Basis States

4 basis states: |00>, |01>, |10>, |11>. Each has an amplitude (complex number). The state vector is [alpha_00, alpha_01, alpha_10, alpha_11] with sum of squared magnitudes = 1. CNOT gate swaps |10> and |11> amplitudes (controlled-X on qubit 1, controlled by qubit 0).

## Grover's Amplitude Amplification

4 items (2 qubits). Uniform superposition: each amplitude = 1/2. Oracle marks target (say |11>): amplitude becomes [-1/2, -1/2, -1/2, +1/2]. Diffusion: inversion about mean (mean = 0). After diffusion: amplitudes [+1/2, +1/2, +1/2, -1/2]... wait, not correct. After first Grover iteration: target amplitude ≈ 1, others ≈ 0. Visually: amplitudes grow toward target.

## QFT Circuit

For 3 qubits: H on q2, controlled-R2 on q1, controlled-R3 on q0, H on q1, controlled-R2 on q0, H on q0. Then swap q0 and q2. The controlled rotations: R_k applies phase e^{2pi i/2^k} when control=1.

## Measurement Probability Distribution

After Grover with 4 items and target |11>: measurement probabilities after 1 iteration: P(|00>) ≈ 0, P(|01>) ≈ 0, P(|10>) ≈ 0, P(|11>) ≈ 1. Two iterations would overshoot (amplitudes would decrease). Optimal iterations = floor(pi/4 * sqrt(2^n)).