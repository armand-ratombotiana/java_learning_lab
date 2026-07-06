package com.algo.lab19;

/**
 * Modular arithmetic operations: exponentiation, inverse, and CRT.
 * All operations are performed modulo a given value.
 */
public class ModularArithmetic {

    private ModularArithmetic() {}

    public static long modPow(long base, long exp, long mod) {
        if (mod == 0) throw new ArithmeticException("Modulus cannot be zero");
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = (result * base) % mod;
            base = (base * base) % mod;
            exp >>= 1;
        }
        return result;
    }

    public static long modInverse(long a, long mod) {
        if (mod <= 0) throw new ArithmeticException("Modulus must be positive");
        GCD.ExtendedGcdResult result = GCD.extendedGcd((int) (a % mod), (int) mod);
        if (result.gcd != 1) {
            throw new ArithmeticException("Modular inverse does not exist");
        }
        return (result.x % mod + mod) % mod;
    }

    public static long crt2(long a1, long m1, long a2, long m2) {
        GCD.ExtendedGcdResult egcd = GCD.extendedGcd((int) m1, (int) m2);
        if (egcd.gcd != 1) {
            throw new ArithmeticException("Moduli are not coprime");
        }
        long mod = m1 * m2;
        long result = a1 * m2 % mod * egcd.y % mod + a2 * m1 % mod * egcd.x % mod;
        return (result % mod + mod) % mod;
    }

    public static long gcd(long a, long b) {
        return GCD.gcd(a, b);
    }
}
