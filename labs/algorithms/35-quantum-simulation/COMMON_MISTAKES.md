# Quantum Simulation — Common Mistakes

1. Forgetting to normalize the state vector after gate application: quantum gates are unitary and preserve norm, so |alpha|^2 + |beta|^2 should remain 1. Floating-point errors can accumulate, requiring periodic renormalization.

2. Applying gates to the wrong qubit in multi-qubit simulation: the pair (i, i | (1<<q)) only works correctly when bit q of i is 0. Ensure the pairing logic is correct.

3. Measurement without proper probability: measurement outcome should be chosen with probability = |amplitude|^2. Using a uniform random choice is incorrect.

4. Grover's algorithm iteration count: pi/4 * sqrt(N) is the optimal number of iterations, but this must be an integer. Rounding to the nearest integer. Too many iterations cause the amplitude to decrease (overshooting).

5. QFT qubit ordering: QFT is typically defined with the most significant qubit as the target of the first Hadamard. The final qubit reversal step is crucial for correctness.

6. Oracle implementation: the oracle should flip the sign of the target state (multiply amplitude by -1) while leaving all other states unchanged. A common mistake is setting the target amplitude to 0 or 1 instead.

7. CNOT matrix: the CNOT matrix is identity except for the swap between |10> and |11>. Using a different ordering of basis states (qubit 0 as control vs qubit 1 as control) changes the matrix.

8. Complex number conjugation: when computing probabilities, use |alpha|^2 = real^2 + imag^2. Forgetting the imaginary part gives incorrect probabilities.

9. Tensor product order: |0> ⊗ |1> = |01>, not |10>. The convention is that the first qubit is the most significant bit.