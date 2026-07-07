package com.algo.lab35;

public class QuantumFourierTransform {

    private final int n;
    private final int N;

    public QuantumFourierTransform(int n) {
        this.n = n;
        this.N = 1 << n;
    }

    public Qubit.ComplexNumber[] apply(Qubit.ComplexNumber[] input) {
        Qubit.ComplexNumber[] state = input.clone();
        for (int qubit = n - 1; qubit >= 0; qubit--) {
            applyHadamard(state, qubit);
            for (int j = n - 1; j > qubit; j--) {
                int k = j - qubit;
                double angle = 2 * Math.PI / (1 << (k + 1));
                applyControlledRotation(state, j, qubit, angle);
            }
        }
        state = reverseQubits(state);
        return state;
    }

    private void applyHadamard(Qubit.ComplexNumber[] state, int qubit) {
        for (int i = 0; i < N; i++) {
            if ((i & (1 << qubit)) == 0) {
                int j = i | (1 << qubit);
                Qubit.ComplexNumber a = state[i];
                Qubit.ComplexNumber b = state[j];
                state[i] = new Qubit.ComplexNumber(
                    (a.re + b.re) / Math.sqrt(2),
                    (a.im + b.im) / Math.sqrt(2)
                );
                state[j] = new Qubit.ComplexNumber(
                    (a.re - b.re) / Math.sqrt(2),
                    (a.im - b.im) / Math.sqrt(2)
                );
            }
        }
    }

    private void applyControlledRotation(Qubit.ComplexNumber[] state, int control, int target, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        for (int i = 0; i < N; i++) {
            if ((i & (1 << control)) != 0 && (i & (1 << target)) == 0) {
                int j = i | (1 << target);
                Qubit.ComplexNumber a = state[j];
                state[j] = new Qubit.ComplexNumber(
                    a.re * cos - a.im * sin,
                    a.re * sin + a.im * cos
                );
            }
        }
    }

    private Qubit.ComplexNumber[] reverseQubits(Qubit.ComplexNumber[] state) {
        Qubit.ComplexNumber[] result = new Qubit.ComplexNumber[N];
        for (int i = 0; i < N; i++) {
            int rev = Integer.reverse(i) >>> (32 - n);
            result[i] = state[rev];
        }
        return result;
    }
}
