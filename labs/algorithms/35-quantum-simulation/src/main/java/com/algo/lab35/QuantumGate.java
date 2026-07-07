package com.algo.lab35;

public class QuantumGate {

    private static final double S2 = 1.0 / Math.sqrt(2);

    public static Qubit.ComplexNumber[][] hadamard() {
        return new Qubit.ComplexNumber[][]{
            {new Qubit.ComplexNumber(S2, 0), new Qubit.ComplexNumber(S2, 0)},
            {new Qubit.ComplexNumber(S2, 0), new Qubit.ComplexNumber(-S2, 0)}
        };
    }

    public static Qubit.ComplexNumber[][] pauliX() {
        return new Qubit.ComplexNumber[][]{
            {new Qubit.ComplexNumber(0, 0), new Qubit.ComplexNumber(1, 0)},
            {new Qubit.ComplexNumber(1, 0), new Qubit.ComplexNumber(0, 0)}
        };
    }

    public static Qubit.ComplexNumber[][] pauliY() {
        return new Qubit.ComplexNumber[][]{
            {new Qubit.ComplexNumber(0, 0), new Qubit.ComplexNumber(0, -1)},
            {new Qubit.ComplexNumber(0, 1), new Qubit.ComplexNumber(0, 0)}
        };
    }

    public static Qubit.ComplexNumber[][] pauliZ() {
        return new Qubit.ComplexNumber[][]{
            {new Qubit.ComplexNumber(1, 0), new Qubit.ComplexNumber(0, 0)},
            {new Qubit.ComplexNumber(0, 0), new Qubit.ComplexNumber(-1, 0)}
        };
    }

    public static Qubit.ComplexNumber[][] cnot() {
        Qubit.ComplexNumber[][] m = new Qubit.ComplexNumber[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                m[i][j] = new Qubit.ComplexNumber(0, 0);
        m[0][0] = new Qubit.ComplexNumber(1, 0);
        m[1][1] = new Qubit.ComplexNumber(1, 0);
        m[2][3] = new Qubit.ComplexNumber(1, 0);
        m[3][2] = new Qubit.ComplexNumber(1, 0);
        return m;
    }

    public static Qubit.ComplexNumber[][] toffoli() {
        Qubit.ComplexNumber[][] m = new Qubit.ComplexNumber[8][8];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                m[i][j] = new Qubit.ComplexNumber(0, 0);
        for (int i = 0; i < 6; i++) m[i][i] = new Qubit.ComplexNumber(1, 0);
        m[6][7] = new Qubit.ComplexNumber(1, 0);
        m[7][6] = new Qubit.ComplexNumber(1, 0);
        return m;
    }
}
