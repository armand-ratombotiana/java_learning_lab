package com.algo.lab39;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigInteger;
import java.util.List;

class NumberTheoryAdvancedTest {

    @Test
    void testPollardRhoPrime() {
        assertTrue(PollardRho.isPrime(17));
        assertTrue(PollardRho.isPrime(7919));
        assertFalse(PollardRho.isPrime(15));
    }

    @Test
    void testPollardRhoFactorization() {
        List<Long> factors = PollardRho.factorize(12);
        long product = 1;
        for (long f : factors) product *= f;
        assertEquals(12, product);
        assertTrue(factors.size() >= 3);
    }

    @Test
    void testPollardRhoLargeSemiPrime() {
        long n = 100003L * 100019L;
        List<Long> factors = PollardRho.factorize(n);
        long product = 1;
        for (long f : factors) product *= f;
        assertEquals(n, product);
    }

    @Test
    void testEllipticCurveAddition() {
        EllipticCurve ec = new EllipticCurve(
            BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.valueOf(17));
        EllipticCurve.Point p = EllipticCurve.Point.of(BigInteger.valueOf(3), BigInteger.valueOf(1));
        assertTrue(ec.isOnCurve(p));
        EllipticCurve.Point sum = ec.add(p, p);
        assertNotNull(sum);
    }

    @Test
    void testEllipticCurveMultiply() {
        EllipticCurve ec = new EllipticCurve(
            BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.valueOf(97));
        EllipticCurve.Point g = EllipticCurve.Point.of(BigInteger.valueOf(3), BigInteger.valueOf(6));
        assertTrue(ec.isOnCurve(g));
        EllipticCurve.Point result = ec.multiply(g, BigInteger.valueOf(5));
        assertNotNull(result);
    }

    @Test
    void testContinuedFractionSqrt2() {
        List<Long> exp = ContinuedFraction.sqrtExpansion(2);
        assertTrue(exp.get(0) == 1);
        assertTrue(exp.size() > 1);
        assertTrue(exp.get(exp.size() - 1) == 2);
    }

    @Test
    void testPellEquationD2() {
        BigInteger[] sol = PellEquation.solve(2);
        assertEquals(BigInteger.valueOf(3), sol[0]);
        assertEquals(BigInteger.valueOf(2), sol[1]);
    }

    @Test
    void testPellEquationD3() {
        BigInteger[] sol = PellEquation.solve(3);
        assertEquals(BigInteger.valueOf(2), sol[0]);
        assertEquals(BigInteger.valueOf(1), sol[1]);
    }
}
