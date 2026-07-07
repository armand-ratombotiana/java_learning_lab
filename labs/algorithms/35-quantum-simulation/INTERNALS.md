# Quantum Simulation — Internal Implementation Details

The Qubit class represents a single qubit as a ComplexNumber[2] array. State is normalized (|alpha|^2 + |beta|^2 = 1). Operations: applyGate(ComplexNumber[][] gate) multiplies the state vector by a 2x2 unitary matrix. Measure() collapses the state: picks 0 with probability |alpha|^2 or 1 with probability |beta|^2, then sets the state to |0> or |1> accordingly.

Multi-qubit simulation uses the full state vector approach (exponential in qubit count). For n qubits, the state is a ComplexNumber[1<<n] array. Index i (binary) corresponds to basis state |b_0 b_1 ... b_{n-1}>. Applying a single-qubit gate to qubit q iterates over all basis states, processing pairs (i, i | (1<<q)) where bit q of i is 0.

The QuantumGate class provides static factory methods:
- hadamard(): returns [[1, 1], [1, -1]] / sqrt(2).
- pauliX(): returns [[0, 1], [1, 0]].
- pauliY(): returns [[0, -i], [i, 0]].
- pauliZ(): returns [[1, 0], [0, -1]].
- cnot(): returns a 4x4 matrix [[1,0,0,0],[0,1,0,0],[0,0,0,1],[0,0,1,0]].
- toffoli(): returns an 8x8 matrix (controlled-controlled-NOT).

The GroverSearch class simulates Grover's algorithm. It initializes all qubits to uniform superposition, then applies the oracle and diffusion operator O(sqrt(N)) times. The oracle marks the solution by flipping the amplitude sign. The diffusion operator is H^⊗n * (2|0><0| - I) * H^⊗n.

The QuantumFourierTransform class implements QFT for small qubit counts (up to 6-8 qubits, since full simulation of n qubits requires O(2^n) memory). The circuit uses Hadamard gates followed by controlled phase rotations (R_k gates). After all gates, qubits are reversed to produce the correct QFT output order.