package com.dsacademy.lab26.hyperloglog;

public class HyperLogLog {

    private final int precision;
    private final int m;
    private final HllRegister register;
    private static final double ALPHA_16 = 0.673;
    private static final double ALPHA_32 = 0.697;
    private static final double ALPHA_64 = 0.709;

    public HyperLogLog(int precision) {
        if (precision < 4 || precision > 16) {
            throw new IllegalArgumentException("Precision must be between 4 and 16");
        }
        this.precision = precision;
        this.m = 1 << precision;
        this.register = new HllRegister(precision);
    }

    public void add(int hash) {
        int idx = hash >>> (32 - precision);
        int w = hash << precision | (1 << (precision - 1));
        w = w >>> (precision + 1);
        int leadingZeros = Integer.numberOfLeadingZeros(w) + 1;
        register.set(idx, (byte) Math.min(leadingZeros, 31));
    }

    public void add(Object obj) {
        add(obj.hashCode());
    }

    public long cardinality() {
        double sum = 0;
        for (int i = 0; i < m; i++) {
            sum += 1.0 / (1 << register.get(i));
        }
        double estimate = alpha(m) * m * m / sum;
        if (estimate < 2.5 * m) {
            int zeros = 0;
            for (int i = 0; i < m; i++) {
                if (register.get(i) == 0) zeros++;
            }
            if (zeros > 0) {
                estimate = m * Math.log((double) m / zeros);
            }
        }
        return Math.round(estimate);
    }

    public void merge(HyperLogLog other) {
        if (other.precision != this.precision) {
            throw new IllegalArgumentException("Precision mismatch");
        }
        register.merge(other.register);
    }

    private static double alpha(int m) {
        if (m == 16) return ALPHA_16;
        if (m == 32) return ALPHA_32;
        if (m == 64) return ALPHA_64;
        return 0.7213 / (1 + 1.079 / m);
    }

    public int getPrecision() { return precision; }
    public int getM() { return m; }
}
