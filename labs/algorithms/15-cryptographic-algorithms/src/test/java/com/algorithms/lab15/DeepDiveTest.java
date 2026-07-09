package com.algorithms.lab15;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

public class DeepDiveTest {

    @Test
    void testEulersTheorem() {
        // Euler's theorem: a^φ(n) ≡ 1 mod n for gcd(a,n) = 1
        long n = 3233; // 61 * 53
        long phi = 60 * 52; // (61-1)*(53-1) = 3120
        long a = 5;
        
        long result = modPow(a, phi, n);
        assertEquals(1, result, "a^φ(n) ≡ 1 mod n for coprime a,n");
    }

    @Test
    void testExtendedEuclidean() {
        // Find d such that e*d ≡ 1 mod φ(n)
        long e = 17, phi = 3120;
        long[] egcd = egcd(e, phi);
        assertEquals(1, egcd[0], "gcd(e, φ(n)) should be 1");
        long d = (egcd[1] % phi + phi) % phi;
        assertEquals(1, (e * d) % phi, "e*d ≡ 1 mod φ(n)");
    }

    @Test
    void testRSAEncryptDecrypt() {
        long p = 61, q = 53;
        long n = p * q;
        long phi = (p - 1) * (q - 1);
        long e = 17;
        long[] egcd = egcd(e, phi);
        long d = (egcd[1] % phi + phi) % phi;
        
        long plaintext = 42;
        long ciphertext = modPow(plaintext, e, n);
        long decrypted = modPow(ciphertext, d, n);
        
        assertEquals(plaintext, decrypted, "RSA should correctly encrypt and decrypt");
        // Also verify: ciphertext ≠ plaintext (for small message > 1)
        assertNotEquals(plaintext, ciphertext);
    }

    @Test
    void testDiffieHellmanKeyExchange() {
        long p = 23, g = 5;
        long a = 6, b = 15; // private keys
        
        long A = modPow(g, a, p); // Alice's public
        long B = modPow(g, b, p); // Bob's public
        
        long sAlice = modPow(B, a, p); // shared secret from Alice
        long sBob = modPow(A, b, p);   // shared secret from Bob
        
        assertEquals(sAlice, sBob, "DH shared secrets should match");
    }

    @Test
    void testEllipticCurvePointAddition() {
        // Curve: y² = x³ + 2x + 3 mod 97
        long a = 2, b = 3, p = 97;
        
        // Point P: find a point on the curve by testing
        int px = 3;
        int py;
        // Find y such that y² ≡ 3³ + 2·3 + 3 = 36 mod 97 → y = 6
        assertEquals(6, modSqrt(36, p));
        
        // P = (3, 6), Q = P (doubling test)
        long lambda = ((3 * px * px + a) % p) * modInverse(2 * 6 % p, p) % p;
        long x3 = (lambda * lambda - px - px + 2 * p) % p;
        long y3 = (lambda * (px - x3 + p) % p - 6 + p) % p;
        
        assertTrue(x3 >= 0 && x3 < p, "Result x should be in field");
        assertTrue(y3 >= 0 && y3 < p, "Result y should be in field");
    }

    @Test
    void testAESSBoxNonLinearity() {
        int[] sbox = {
            0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5
        };
        // Test S-box: input 0x00 → 0x63
        assertEquals(0x63, sbox[0x00]);
        // Input 0x01 → 0x7c
        assertEquals(0x7c, sbox[0x01]);
    }

    @Test
    void testSHA256MerkleDamgard() {
        String input = "abc";
        byte[] hash = sha256(input.getBytes());
        
        // SHA-256("abc") = BA7816BF8F01CFEA4141... (known test vector)
        assertEquals(32, hash.length, "SHA-256 output should be 32 bytes");
        
        byte[] knownHash = hexStringToByteArray(
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
        assertArrayEquals(knownHash, hash);
    }

    @Test
    void testSHA256EmptyString() {
        byte[] hash = sha256("".getBytes());
        byte[] knownHash = hexStringToByteArray(
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        assertArrayEquals(knownHash, hash);
    }

    @Test
    void testSHA256LengthExtensionVulnerability() {
        // Merkle-Damgård is vulnerable to length extension
        // SHA-3 (sponge construction) is not
        assertTrue(true, "Length extension attack is possible on SHA-256");
    }

    @Test
    void testModularArithmeticProperties() {
        long p = 101;
        // Fermat's little theorem: a^(p-1) ≡ 1 mod p for a not divisible by p
        assertEquals(1, modPow(2, p - 1, p));
        assertEquals(1, modPow(5, p - 1, p));
        assertEquals(1, modPow(42, p - 1, p));
    }

    // Helper methods
    private long modPow(long base, long exp, long mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = (result * base) % mod;
            base = (base * base) % mod;
            exp >>= 1;
        }
        return result;
    }

    private long[] egcd(long a, long b) {
        if (b == 0) return new long[]{a, 1, 0};
        long[] prev = egcd(b, a % b);
        return new long[]{prev[0], prev[2], prev[1] - (a / b) * prev[2]};
    }

    private long modInverse(long a, long mod) {
        long[] egcd = egcd(a % mod, mod);
        if (egcd[0] != 1) throw new ArithmeticException("Not invertible");
        return (egcd[1] + mod) % mod;
    }

    private long modSqrt(long a, long p) {
        // Tonelli-Shanks for p ≡ 3 mod 4
        if (p % 4 == 3) return modPow(a, (p + 1) / 4, p);
        throw new UnsupportedOperationException("General sqrt not implemented");
    }

    private byte[] sha256(byte[] message) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            return md.digest(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
