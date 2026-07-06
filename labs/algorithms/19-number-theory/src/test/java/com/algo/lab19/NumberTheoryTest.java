package com.algo.lab19;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class NumberTheoryTest {

    @Test
    void testGCDBasic() {
        assertEquals(6, GCD.gcd(12, 18));
        assertEquals(1, GCD.gcd(17, 5));
        assertEquals(12, GCD.gcd(0, 12));
    }

    @Test
    void testGCDNegative() {
        assertEquals(5, GCD.gcd(-10, 15));
        assertEquals(5, GCD.gcd(10, -15));
    }

    @Test
    void testLCM() {
        assertEquals(36, GCD.lcm(12, 18));
        assertEquals(0, GCD.lcm(0, 5));
    }

    @Test
    void testExtendedGcd() {
        GCD.ExtendedGcdResult r = GCD.extendedGcd(30, 20);
        assertEquals(10, r.gcd);
        assertEquals(10, r.x * 30 + r.y * 20);
    }

    @Test
    void testExtendedGcdCoPrime() {
        GCD.ExtendedGcdResult r = GCD.extendedGcd(17, 5);
        assertEquals(1, r.gcd);
    }

    @Test
    void testPrimeSieveSmall() {
        List<Integer> primes = PrimeSieve.sieve(10);
        assertEquals(List.of(2, 3, 5, 7), primes);
    }

    @Test
    void testPrimeSieveEmpty() {
        assertTrue(PrimeSieve.sieve(1).isEmpty());
    }

    @Test
    void testPrimeSieveLarge() {
        List<Integer> primes = PrimeSieve.sieve(100);
        assertEquals(25, primes.size());
        assertTrue(primes.contains(97));
    }

    @Test
    void testSegmentedSieve() {
        List<Integer> primes = PrimeSieve.segmentedSieve(100);
        assertEquals(25, primes.size());
    }

    @Test
    void testIsPrime() {
        assertTrue(PrimeSieve.isPrime(2));
        assertTrue(PrimeSieve.isPrime(17));
        assertFalse(PrimeSieve.isPrime(1));
        assertFalse(PrimeSieve.isPrime(4));
        assertFalse(PrimeSieve.isPrime(21));
    }

    @Test
    void testMillerRabinInt() {
        assertTrue(MillerRabin.isPrime(2));
        assertTrue(MillerRabin.isPrime(3));
        assertTrue(MillerRabin.isPrime(7919));
        assertFalse(MillerRabin.isPrime(1));
        assertFalse(MillerRabin.isPrime(4));
        assertFalse(MillerRabin.isPrime(100));
    }

    @Test
    void testMillerRabinLong() {
        assertTrue(MillerRabin.isPrime(2147483647L));
        assertFalse(MillerRabin.isPrime(2147483645L));
    }

    @Test
    void testMillerRabinLargeComposite() {
        assertFalse(MillerRabin.isPrime(999999999989L));
    }

    @Test
    void testModPow() {
        assertEquals(1, ModularArithmetic.modPow(2, 0, 7));
        assertEquals(8, ModularArithmetic.modPow(2, 3, 10));
        assertEquals(3, ModularArithmetic.modPow(5, 3, 13));
    }

    @Test
    void testModInverse() {
        assertEquals(3, ModularArithmetic.modInverse(3, 8));
        assertEquals(7, ModularArithmetic.modInverse(3, 10));
    }

    @Test
    void testModInverseThrows() {
        assertThrows(ArithmeticException.class, () -> ModularArithmetic.modInverse(2, 4));
    }

    @Test
    void testChineseRemainderTwo() {
        assertEquals(5, ModularArithmetic.crt2(1, 2, 2, 3));
    }

    @Test
    void testCRTSolve() {
        long[] remainders = {2, 3, 2};
        long[] moduli = {3, 5, 7};
        long x = ChineseRemainder.solve(remainders, moduli);
        assertEquals(23, x);
        assertEquals(2, x % 3);
        assertEquals(3, x % 5);
        assertEquals(2, x % 7);
    }

    @Test
    void testCRTThrows() {
        assertThrows(IllegalArgumentException.class, () -> ChineseRemainder.solve(new long[]{1}, new long[]{}));
    }

    @Test
    void testCRTSolveTwo() {
        assertEquals(23, ChineseRemainder.solveTwo(2, 3, 3, 5));
    }
}
