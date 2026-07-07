# History of Quantum Algorithm Simulation

1981: Richard Feynman proposed the idea of quantum computers, arguing that quantum systems cannot be efficiently simulated by classical computers. This motivated the development of quantum computing.

1985: David Deutsch published the first quantum algorithm (Deutsch's algorithm), demonstrating quantum speedup for a simple problem. He also described the universal quantum computer.

1994: Peter Shor published Shor's algorithm for integer factorization, showing exponential speedup over classical methods. This sparked intense interest in quantum computing. Classical simulation of Shor's algorithm for small numbers (15 = 3 * 5) became the standard verification method.

1996: Lov Grover published Grover's search algorithm, demonstrating quadratic speedup for unstructured search. Simulation of Grover's algorithm on classical computers became common for education.

1998: The first quantum computer simulators were developed (e.g., libquantum by Hendrik Weimer). These could simulate up to about 20 qubits.

2000: The Quantum Fourier Transform was described by several authors as a generalization of the classical FFT. Cleve and Watrous showed efficient QFT simulation requires exponential classical resources.

2007: Quantum circuit simulators like QuEST and ProjectQ scaled to 35-40 qubits using high-performance computing techniques.

2016: IBM Quantum Experience launched, providing cloud-accessible quantum computers alongside simulators. This made quantum simulation widely available for education.

2020s: Tensor network methods (matrix product states) allowed simulating specific quantum circuits with 50+ qubits. Classical simulation of quantum algorithms remains a critical verification and educational tool, despite being fundamentally limited by the exponential growth of the state vector.