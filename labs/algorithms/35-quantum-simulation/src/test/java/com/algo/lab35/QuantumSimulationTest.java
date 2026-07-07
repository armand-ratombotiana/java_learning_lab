package com.algo.lab35;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuantumSimulationTest {

    @Test
    void testQubitInitialState() {
        Qubit q = new Qubit();
        assertEquals(1.0, q.getState()[0].re, 1e-9);
        assertEquals(0.0, q.getState()[1].re, 1e-9);
    }

    @Test
    void testHadamardGate() {
        Qubit q = new Qubit();
        q.applyGate(QuantumGate.hadamard());
        double p0 = q.getState()[0].normSq();
        double p1 = q.getState()[1].normSq();
        assertEquals(0.5, p0, 1e-9);
        assertEquals(0.5, p1, 1e-9);
    }

    @Test
    void testPauliXGate() {
        Qubit q = new Qubit(new Qubit.ComplexNumber(0, 0), new Qubit.ComplexNumber(1, 0));
        q.applyGate(QuantumGate.pauliX());
        assertEquals(1.0, q.getState()[0].re, 1e-9);
    }

    @Test
    void testGroverSearch() {
        GroverSearch grover = new GroverSearch(2, 3);
        int result = grover.search();
        assertEquals(3, result);
    }

    @Test
    void testQFT() {
        int n = 2;
        int N = 1 << n;
        Qubit.ComplexNumber[] input = new Qubit.ComplexNumber[N];
        input[0] = new Qubit.ComplexNumber(1, 0);
        for (int i = 1; i < N; i++) input[i] = new Qubit.ComplexNumber(0, 0);

        QuantumFourierTransform qft = new QuantumFourierTransform(n);
        Qubit.ComplexNumber[] output = qft.apply(input);

        double sum = 0;
        for (Qubit.ComplexNumber c : output) sum += c.normSq();
        assertEquals(1.0, sum, 1e-6);
    }

    @Test
    void testToffoliGate() {
        Qubit.ComplexNumber[][] toffoli = QuantumGate.toffoli();
        assertEquals(8, toffoli.length);
        assertEquals(8, toffoli[0].length);
    }

    @Test
    void testCNOTGate() {
        Qubit.ComplexNumber[][] cnot = QuantumGate.cnot();
        assertEquals(4, cnot.length);
    }
}
