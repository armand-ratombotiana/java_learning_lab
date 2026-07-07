package com.algo.lab35;

import java.util.Random;

public class GroverSearch {

    private final int n;
    private final int N;
    private final int target;
    private final Qubit.ComplexNumber[] state;
    private static final Random random = new Random();

    public GroverSearch(int n, int target) {
        this.n = n;
        this.N = 1 << n;
        this.target = target;
        this.state = new Qubit.ComplexNumber[N];
        for (int i = 0; i < N; i++) {
            state[i] = new Qubit.ComplexNumber(1.0 / Math.sqrt(N), 0);
        }
    }

    public int search() {
        int iterations = (int) (Math.PI / 4 * Math.sqrt(N));
        for (int iter = 0; iter < iterations; iter++) {
            applyOracle();
            applyDiffusion();
        }
        double[] probs = new double[N];
        double total = 0;
        for (int i = 0; i < N; i++) {
            probs[i] = state[i].normSq();
            total += probs[i];
        }
        double r = random.nextDouble() * total;
        double cum = 0;
        for (int i = 0; i < N; i++) {
            cum += probs[i];
            if (r <= cum) return i;
        }
        return N - 1;
    }

    private void applyOracle() {
        state[target] = new Qubit.ComplexNumber(-state[target].re, -state[target].im);
    }

    private void applyDiffusion() {
        double sum = 0;
        for (Qubit.ComplexNumber c : state) sum += c.re;
        double mean = sum / N;
        for (int i = 0; i < N; i++) {
            state[i] = new Qubit.ComplexNumber(2 * mean - state[i].re, -state[i].im);
        }
    }
}
