package com.algo.lab35;

import java.util.Random;

public class Qubit {

    private ComplexNumber[] state;
    private static final Random random = new Random();

    public Qubit() {
        state = new ComplexNumber[]{new ComplexNumber(1, 0), new ComplexNumber(0, 0)};
    }

    public Qubit(ComplexNumber alpha, ComplexNumber beta) {
        state = new ComplexNumber[]{alpha, beta};
        normalize();
    }

    public void applyGate(ComplexNumber[][] gate) {
        ComplexNumber[] result = new ComplexNumber[2];
        result[0] = gate[0][0].multiply(state[0]).add(gate[0][1].multiply(state[1]));
        result[1] = gate[1][0].multiply(state[0]).add(gate[1][1].multiply(state[1]));
        state = result;
    }

    public int measure() {
        double prob0 = state[0].normSq();
        if (random.nextDouble() < prob0) {
            state = new ComplexNumber[]{new ComplexNumber(1, 0), new ComplexNumber(0, 0)};
            return 0;
        } else {
            state = new ComplexNumber[]{new ComplexNumber(0, 0), new ComplexNumber(1, 0)};
            return 1;
        }
    }

    public ComplexNumber[] getState() {
        return state;
    }

    private void normalize() {
        double norm = Math.sqrt(state[0].normSq() + state[1].normSq());
        state[0] = new ComplexNumber(state[0].re / norm, state[0].im / norm);
        state[1] = new ComplexNumber(state[1].re / norm, state[1].im / norm);
    }

    static class ComplexNumber {
        final double re, im;

        ComplexNumber(double re, double im) {
            this.re = re;
            this.im = im;
        }

        ComplexNumber add(ComplexNumber o) {
            return new ComplexNumber(re + o.re, im + o.im);
        }

        ComplexNumber multiply(ComplexNumber o) {
            return new ComplexNumber(re * o.re - im * o.im, re * o.im + im * o.re);
        }

        double normSq() {
            return re * re + im * im;
        }
    }
}
