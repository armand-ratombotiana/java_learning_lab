package com.math10;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NumberTheoryTest {

    @ParameterizedTest
    @CsvSource({"12,8,4", "17,5,1", "100,25,25", "0,5,5", "-12,8,4"})
    void testGcd(long a, long b, long expected) {
        assertEquals(expected, NumberTheory.gcd(a, b));
    }

    @ParameterizedTest
    @CsvSource({"12,8,24", "17,5,85", "6,10,30"})
    void testLcm(long a, long b, long expected) {
        assertEquals(expected, NumberTheory.lcm(a, b));
    }

    @ParameterizedTest
    @CsvSource({"12,8,4,1,-1", "17,5,1,-2,7", "240,46,2,-9,47"})
    void testExtendedEuclid(long a, long b, long d, long x, long y) {
        long[] result = NumberTheory.extendedEuclid(a, b);
        assertEquals(d, result[0]);
        assertEquals(x, result[1]);
        assertEquals(y, result[2]);
    }

    @ParameterizedTest
    @CsvSource({"3,7,5", "5,11,9", "1,7,1"})
    void testModularInverse(long a, long m, long expected) {
        assertEquals(expected, NumberTheory.modularInverse(a, m));
    }

    @Test
    void testModularInverseFails() {
        assertThrows(ArithmeticException.class, () -> NumberTheory.modularInverse(2, 4));
    }

    @ParameterizedTest
    @CsvSource({"2,10,1000,24", "3,5,7,5", "5,0,7,1"})
    void testModPow(long base, long exp, long mod, long expected) {
        assertEquals(expected, NumberTheory.modPow(base, exp, mod));
    }

    @ParameterizedTest
    @ValueSource(longs = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 97, 101, 103, 7919, 104729})
    void testIsPrimeTrue(long n) {
        assertTrue(NumberTheory.isPrime(n));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 4, 6, 8, 9, 10, 12, 15, 21, 25, 27, 100, 1000})
    void testIsPrimeFalse(long n) {
        assertFalse(NumberTheory.isPrime(n));
    }

    @Test
    void testMillerRabin() {
        assertTrue(NumberTheory.millerRabin(BigInteger.valueOf(104729), 10));
        assertTrue(NumberTheory.millerRabin(BigInteger.valueOf(7919), 10));
        assertFalse(NumberTheory.millerRabin(BigInteger.valueOf(1), 10));
        assertFalse(NumberTheory.millerRabin(BigInteger.valueOf(4), 10));
        assertFalse(NumberTheory.millerRabin(BigInteger.valueOf(100), 10));
    }

    @Test
    void testPrimeFactors() {
        assertEquals(List.of(2L, 2L, 5L), NumberTheory.primeFactors(20));
        assertEquals(List.of(97L), NumberTheory.primeFactors(97));
        assertEquals(List.of(2L, 3L, 5L, 7L), NumberTheory.primeFactors(210));
    }

    @ParameterizedTest
    @CsvSource({"10,4", "7,6", "100,40", "17,16"})
    void testTotient(long n, long expected) {
        assertEquals(expected, NumberTheory.totient(n));
    }

    @Test
    void testChineseRemainder() {
        long[] remainders = {2, 3, 2};
        long[] moduli = {3, 5, 7};
        assertEquals(23, NumberTheory.chineseRemainder(remainders, moduli));
    }

    @Test
    void testRSA() {
        var key = NumberTheory.generateRSAKeyPair(512);
        BigInteger plaintext = BigInteger.valueOf(42);
        BigInteger cipher = NumberTheory.rsaEncrypt(plaintext, key.n(), key.e());
        BigInteger decrypted = NumberTheory.rsaDecrypt(cipher, key.n(), key.d());
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testLargeRSAMessage() {
        var key = NumberTheory.generateRSAKeyPair(1024);
        BigInteger msg = new BigInteger("12345678901234567890");
        BigInteger enc = NumberTheory.rsaEncrypt(msg, key.n(), key.e());
        BigInteger dec = NumberTheory.rsaDecrypt(enc, key.n(), key.d());
        assertEquals(msg, dec);
    }

    @Test
    void testPollardRho() {
        long factor = NumberTheory.pollardRho(8051);
        assertTrue(8051 % factor == 0);
        assertTrue(factor > 1 && factor < 8051);
    }
}
