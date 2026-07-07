# Quantum Algorithm Simulation — Theoretical Foundation

## Quantum Bits (Qubits)

A qubit is a two-level quantum system represented as a linear combination of basis states |0&gt; and |1&gt;: |psi&gt; = alpha|0&gt; + beta|1&gt;, where alpha and beta are complex numbers satisfying |alpha|^2 + |beta|^2 = 1. Upon measurement, the qubit collapses to |0&gt; with probability |alpha|^2 and |1&gt; with probability |beta|^2. This superposition property distinguishes quantum from classical computation.

## Quantum Gates

Quantum gates are unitary operators (U such that U^dagger U = I) that act on qubits. Common single-qubit gates: Pauli-X (NOT gate, flips |0&gt; to |1&gt;), Pauli-Y and Pauli-Z, the Hadamard gate H = 1/sqrt(2) * [[1,1],[1,-1]] which creates superposition. Multi-qubit gates include CNOT (controlled-X, flips target if control is |1&gt;) and Toffoli (CCNOT, flips target if both controls are |1&gt;).

## Quantum Circuit Model

A quantum computation is a sequence of quantum gates acting on a set of qubits. The composition of gates is represented as tensor products. For simulation, we maintain a complex vector of size 2^n for n qubits, where each basis state |b_0 b_1 ... b_{n-1}&gt; corresponds to an element. The tensor product structure allows efficient gate application.

## Grover's Search Algorithm

Grover's algorithm searches an unsorted database of N items in O(sqrt(N)) time, providing a quadratic speedup over classical O(N). The algorithm uses amplitude amplification: (1) apply Hadamard gates to create uniform superposition; (2) apply the oracle (marks the target state by flipping its amplitude sign); (3) apply the diffusion operator (inversion about the mean). Steps 2-3 are repeated approximately pi/4 * sqrt(N) times.

## Shor's Period Finding

Shor's algorithm factors integers in polynomial time using quantum period finding. The period finding subroutine uses the quantum Fourier transform to find the period r of the function f(x) = a^x mod N. If r is even and a^{r/2} != -1 mod N, then gcd(a^{r/2} - 1, N) and gcd(a^{r/2} + 1, N) are non-trivial factors. Classical simulation of Shor's algorithm is exponential.

## Quantum Fourier Transform

QFT is the quantum analog of the DFT. It maps the computational basis to the Fourier basis: QFT|j&gt; = 1/sqrt(N) sum_{k=0}^{N-1} omega_N^{jk} |k&gt;. QFT can be implemented with O(log^2 N) gates using controlled rotations and Hadamard gates. It is a key subroutine in Shor's algorithm, quantum phase estimation, and many other quantum algorithms.