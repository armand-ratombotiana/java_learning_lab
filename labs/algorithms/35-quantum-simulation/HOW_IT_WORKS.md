# How Quantum Simulation Works

## Qubit State Representation

A single qubit is represented as a 2-element complex array [alpha, beta] where |psi> = alpha|0> + beta|1>. Operations are 2x2 unitary matrices. Multi-qubit states are represented as complex arrays of size 2^n, where index i in binary represents the basis state |b_0 b_1 ... b_{n-1}>.

## Applying Quantum Gates

Single-qubit gates: compute the tensor product structure. To apply gate G to qubit q in an n-qubit system:
- For each basis state index i where qubit q is 0 (bit q = 0), pair with the state where qubit q is 1.
- Apply the 2x2 gate to the [alpha_i, alpha_{i|1<<q}] pair.
This requires O(2^n) operations per gate application.

## Implementing Grover's Algorithm

- Initialize n qubits to |0>, apply Hadamard to each to create uniform superposition.
- Repeat O(sqrt(2^n)) times:
  - Oracle: call applyOracle(), which multiplies the amplitude of the target state by -1.
  - Diffusion: apply diffusion operator (inversion about the mean):
    * Apply Hadamard to all qubits.
    * Apply X to all qubits.
    * Apply controlled-Z on last qubit (with all qubits as control).
    * Apply X to all qubits.
    * Apply Hadamard to all qubits.
- Measure: the most probable outcome is the target state.

## Quantum Fourier Transform

For n qubits:
- Apply Hadamard to qubit n-1.
- For j = n-2 down to 0:
  - Apply controlled rotation R_k where k = n-1-j.
  - Apply Hadamard to qubit j.
- Then reverse the order of qubits.
The rotation R_k = [[1, 0], [0, exp(2*pi*i/2^k)]]. The QFT circuit uses O(n^2) gates, compared to the O(2^n) classical DFT.

## Measurement

Measurement collapses the quantum state. To measure state |i>:
- Probability = |state[i]|^2.
- Generate a random number r in [0, 1). Iterate through the state array, accumulating |state[i]|^2. When accumulator >= r, return i as the measured state and set the state to the corresponding basis vector.