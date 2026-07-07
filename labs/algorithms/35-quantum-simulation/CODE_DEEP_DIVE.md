# Quantum Simulation — Code Deep Dive

The Qubit class represents a single qubit with a Complex[2] state. The applyGate method: Complex[] result = new Complex[2]; result[0] = gate[0][0].times(state[0]).plus(gate[0][1].times(state[1])); result[1] = gate[1][0].times(state[0]).plus(gate[1][1].times(state[1])); state = result. The measure method: double prob0 = state[0].normSq(); if (Math.random() < prob0) { state = new Complex[]{new Complex(1,0), new Complex(0,0)}; return 0; } else { state = new Complex[]{new Complex(0,0), new Complex(1,0)}; return 1; }.

The QuantumGate class provides static methods returning 2x2 or NxN Complex[][]. hadamard returns [[1/sqrt2, 1/sqrt2], [1/sqrt2, -1/sqrt2]]. cnot returns a 4x4 matrix: identity except swap last two rows: [[1,0,0,0],[0,1,0,0],[0,0,0,1],[0,0,1,0]].

The multi-qubit simulator (used in GroverSearch and QFT) maintains a state vector of size 2^n. Applying a gate to qubit q: for each basis state i where bit q is 0: j = i | (1<<q); new_i = gate00 * state[i] + gate01 * state[j]; new_j = gate10 * state[i] + gate11 * state[j]; state[i] = new_i; state[j] = new_j.

The GroverSearch class: initialize state = uniform superposition (each amplitude = 1/sqrt(N)). For iter = 0 to O(sqrt(N)): applyOracle: flip sign of solution state; applyDiffusion: compute mean amplitude, for each i: state[i] = 2 * mean - state[i]. Measure and return the most likely state.

The QuantumFourierTransform class: for qubit = n-1 down to 0: apply H to qubit. For j = n-1 down to qubit+1: apply controlled rotation R_{j-qubit+1} controlled by qubit j, targeted on qubit. After all gates, reverse qubit order. This is iterative (non-recursive) for clarity.