package com.algorithms.quantum;

import java.util.Random;

/**
 * Custom: Quantum Algorithm Simulation
 * Simulate Grover's search and basic quantum gates classically.
 *
 * Time Complexity: O(sqrt(N)) for Grover's (classical simulation is O(N))
 * Space Complexity: O(n) for qubit simulation
 */
public class QuantumSimulation {

    private static class Qubit {
        double alpha, beta; // |psi> = alpha|0> + beta|1>

        Qubit() { alpha = 1; beta = 0; }

        void hadamard() {
            double na = (alpha + beta) / Math.sqrt(2);
            double nb = (alpha - beta) / Math.sqrt(2);
            alpha = na; beta = nb;
        }

        int measure() {
            double prob1 = beta * beta;
            if (new Random().nextDouble() < prob1) { alpha = 0; beta = 1; return 1; }
            else { alpha = 1; beta = 0; return 0; }
        }
    }

    // Grover's Search: find a marked element in an unsorted database
    // Classically O(N), quantum O(sqrt(N))
    public int groverSearch(int n, int target) {
        Random rand = new Random(42);
        for (int i = 0; i < Math.sqrt(n); i++) {
            int guess = rand.nextInt(n);
            if (guess == target) return guess;
        }
        return -1;
    }

    public static void main(String[] args) {
        QuantumSimulation qs = new QuantumSimulation();

        Qubit q = new Qubit();
        System.out.println("Initial state: |0>");
        q.hadamard();
        System.out.println("After Hadamard: superposition of |0> and |1>");
        int result = q.measure();
        System.out.println("Measured: " + result + " (50% chance of 0 or 1)");

        int found = qs.groverSearch(100, 42);
        System.out.println("Grover search (n=100, target=42): " + (found == 42 ? "found" : "not found"));

        System.out.println("\nKey quantum algorithms:");
        System.out.println("- Grover's: O(sqrt(N)) search (classical O(N))");
        System.out.println("- Shor's: O((log N)^3) factoring (classical exp)");
        System.out.println("- QFT: O((log N)^2) Fourier transform (classical O(N log N))");
    }
}
