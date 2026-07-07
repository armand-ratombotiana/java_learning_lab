# Quantum Simulation — Mathematical Foundation

## Qubit Representation

A pure qubit state is a unit vector in C^2: |psi> = alpha|0> + beta|1>, where alpha, beta in C and |alpha|^2 + |beta|^2 = 1. The Bloch sphere representation: |psi> = cos(theta/2)|0> + e^{i phi} sin(theta/2)|1> where theta in [0, pi], phi in [0, 2pi). Each qubit operation is a rotation on the Bloch sphere.

## Unitary Matrices

A matrix U is unitary if U^dagger * U = U * U^dagger = I, where ^dagger is conjugate transpose. Unitary matrices preserve inner products and thus maintain the normalization of quantum states. All quantum gates are unitary. Common gates: Hadamard H = (1/sqrt(2))[[1,1],[1,-1]], Pauli X = [[0,1],[1,0]], Pauli Z = [[1,0],[0,-1]].

## Tensor Product

Multi-qubit states are constructed using tensor product (Kronecker product). For |psi> = a|0> + b|1> and |phi> = c|0> + d|1>, the joint state is |psi> ⊗ |phi> = ac|00> + ad|01> + bc|10> + bd|11>. The tensor product distributes over superposition.

## Measurement Postulate

A measurement in the computational basis projects the state onto one of the basis states. Outcome |i> occurs with probability |<i|psi>|^2. After measurement, the state collapses to |i>. The measurement is irreversible: all information about the original amplitudes is lost.

## Grover Iteration

Let N = 2^n. The Grover iteration operator G = D * O where O is the oracle (flips the sign of the target state) and D = 2|s><s| - I is the diffusion operator (inversion about the mean), with |s> = (1/sqrt(N)) sum_i |i> being the uniform superposition. After k iterations, the amplitude of the target state is sin((2k+1)theta) where sin(theta) = 1/sqrt(N). Optimal k = floor(pi/4 * sqrt(N)).

## Quantum Fourier Transform

QFT on n qubits: QFT|j> = (1/sqrt(N)) sum_{k=0}^{N-1} omega_N^{jk} |k> where omega_N = e^{2pi i/N}. This is the unitary analog of the classical DFT. The circuit uses O(n^2) Hadamard and controlled-phase gates.