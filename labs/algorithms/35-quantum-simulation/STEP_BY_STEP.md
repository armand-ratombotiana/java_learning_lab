# Quantum Simulation — Step by Step Guide

## Step 1: Implement Complex Number Class

Create ComplexNumber with real and imag fields (doubles). Methods: add, subtract, multiply, conjugate, magnitude, normSq(). Override toString for debugging.

## Step 2: Implement Single Qubit

Create Qubit class with Complex[2] state. Constructor initializes to |0> or custom state. applyGate(Complex[][]) applies 2x2 unitary matrix to state. measure() collapses state to |0> or |1> with appropriate probability.

## Step 3: Implement Quantum Gates

Create static methods in QuantumGate class: hadamard(), pauliX(), pauliY(), pauliZ(), cnot(), toffoli(). Return appropriate 2x2, 4x4, or 8x8 complex matrices.

## Step 4: Test Gates Individually

Apply H to |0>: should get (|0>+|1>)/sqrt(2). Apply X to |1>: should get |0>. Apply H twice: should get original. Apply CNOT to |10>: should get |11>.

## Step 5: Implement Multi-Qubit Simulator

Create QuantumState class with size = 2^n complex array. Provide applyGate(int qubit, Complex[][] gate) method. Use bit manipulation to pair states.

## Step 6: Implement Grover's Algorithm

Initialize n-qubit uniform superposition. Loop sqrt(2^n) times: apply oracle (flip target amplitude sign), apply diffusion operator (inversion about mean). Measure.

## Step 7: Implement QFT

For n qubits: apply H to last qubit. For each qubit from n-2 down to 0: apply controlled rotations from higher qubits, then H. Reverse qubit order. Test: QFT|000> = uniform superposition of all basis states with equal amplitude.