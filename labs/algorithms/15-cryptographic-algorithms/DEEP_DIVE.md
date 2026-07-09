# Deep Dive: Cryptographic Algorithms

## 1. RSA Proofs and Foundations

### Euler's Theorem

RSA security relies on Euler's theorem from number theory.

**Euler's Theorem**: For any integers a and n where gcd(a, n) = 1:
a^φ(n) ≡ 1 (mod n)

where φ(n) is Euler's totient function (the count of integers 1 ≤ k ≤ n where gcd(k, n) = 1).

```java
public class EulersTheorem {
    // φ(n) for n = p·q (product of two primes):
    // φ(p·q) = φ(p)·φ(q) = (p-1)(q-1)
    //
    // Proof: φ(p) = p-1 for prime p
    // φ(p·q) counts numbers not divisible by p or q
    // Total numbers: pq
    // Divisible by p: q numbers
    // Divisible by q: p numbers  
    // Both divisible by p and q: 1 number (p·q itself)
    // φ(pq) = pq - p - q + 1 = (p-1)(q-1)
    
    public static long totient(long p, long q) {
        return (p - 1) * (q - 1); // For n = p·q
    }
    
    // Euler's theorem proof for RSA:
    // C = M^e mod n (encryption)
    // M = C^d mod n (decryption)
    // 
    // Need: M^(e·d) ≡ M (mod n)
    // Since e·d ≡ 1 (mod φ(n)):
    //   e·d = k·φ(n) + 1 for some integer k
    // 
    // Therefore:
    //   M^(e·d) = M^(k·φ(n) + 1) = (M^φ(n))^k · M
    // 
    // By Euler's theorem: M^φ(n) ≡ 1 (mod n) if gcd(M, n) = 1
    // So: M^(e·d) ≡ 1^k · M ≡ M (mod n)
    //
    // If gcd(M, n) ≠ 1 (M is multiple of p or q):
    //   M = h·p for some h
    //   By Fermat's little theorem: M^(q-1) ≡ 1 (mod q)
    //   M^(k·φ(n)) ≡ 1 (mod q)
    //   M^(k·φ(n)+1) ≡ M (mod q)
    //   M^(k·φ(n)+1) ≡ M (mod p) [both sides ≡ 0 mod p]
    //   By CRT: M^(e·d) ≡ M (mod p·q)
}
```

### Extended Euclidean Algorithm

The extended Euclidean algorithm finds the modular inverse d of e modulo φ(n).

```java
public class ExtendedEuclidean {
    // Extended Euclidean Algorithm:
    // Finds x, y such that ax + by = gcd(a, b)
    // For RSA: find d where e·d + k·φ(n) = 1 (since gcd(e, φ(n)) = 1)
    // So d = x mod φ(n) is the private exponent
    
    public static long[] egcd(long a, long b) {
        // Returns [gcd, x, y] where ax + by = gcd(a, b)
        if (b == 0) return new long[]{a, 1, 0};
        long[] prev = egcd(b, a % b);
        long gcd = prev[0];
        long x = prev[2];
        long y = prev[1] - (a / b) * prev[2];
        return new long[]{gcd, x, y};
    }
    
    // Proof of correctness (by induction):
    // Base: b = 0 → gcd(a, 0) = a, a·1 + 0·0 = a → correct
    // Step: Assume for (b, a mod b) we have:
    //   b·x' + (a mod b)·y' = gcd(b, a mod b) = gcd(a, b)
    //   a mod b = a - ⌊a/b⌋·b
    //   b·x' + (a - ⌊a/b⌋·b)·y' = a·y' + b·(x' - ⌊a/b⌋·y')
    //   So x = y', y = x' - ⌊a/b⌋·y' → correct
    
    public static long modInverse(long e, long phi) {
        long[] result = egcd(e, phi);
        if (result[0] != 1) throw new ArithmeticException("Not invertible");
        return (result[1] % phi + phi) % phi;
    }
    
    // RSA key generation:
    public static class RSAKeyPair {
        public final long n, e, d;
        
        public RSAKeyPair(long p, long q) {
            n = p * q;
            long phi = (p - 1) * (q - 1);
            e = 65537; // Common public exponent
            if (gcd(e, phi) != 1) throw new IllegalArgumentException("e not coprime to phi");
            d = modInverse(e, phi);
        }
    }
    
    private static long gcd(long a, long b) {
        while (b != 0) { long t = b; b = a % b; a = t; }
        return a;
    }
    
    // Fast modular exponentiation (square-and-multiply)
    public static long modPow(long base, long exp, long mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = (result * base) % mod;
            base = (base * base) % mod;
            exp >>= 1;
        }
        return result;
    }
    
    public static void main(String[] args) {
        // Example with small primes (DO NOT USE IN PRODUCTION)
        long p = 61, q = 53; // n = 3233, φ(n) = 3120
        RSAKeyPair keys = new RSAKeyPair(p, q);
        
        long m = 42; // message
        long c = modPow(m, keys.e, keys.n); // encrypt
        long dec = modPow(c, keys.d, keys.n); // decrypt
        
        System.out.printf("RSA Example: m=%d, c=%d, dec=%d%n", m, c, dec);
        System.out.printf("n=%d, e=%d, d=%d%n", keys.n, keys.e, keys.d);
    }
}
```

### RSA Security Proof

```java
public class RSASecurity {
    // RSA security reduces to the factoring problem:
    // If an attacker can factor n = p·q, they can compute φ(n) and d
    // 
    // No polynomial-time algorithm is known for factoring large semiprimes
    // (RSA-2048 has 617 decimal digits, factoring takes >10^15 years)
    //
    // The RSA problem: Given (n, e, C), find M such that C = M^e mod n
    // This is conjectured to be as hard as factoring
    //
    // Attacks on RSA:
    // 1. Wiener's attack (small d): if d < n^0.25, d can be found via continued fractions
    // 2. Coppersmith's attack (small e, related messages): if e is small and 
    //    the same message is encrypted with related moduli
    // 3. Timing attack: measure decryption time to recover d
    //    → Mitigation: blinding (multiply by random r^e before decryption)
    // 4. Chosen ciphertext attack: RSA is not IND-CCA secure without padding
    //    → Mitigation: OAEP padding (Optimal Asymmetric Encryption Padding)
    
    // The Rabin cryptosystem is provably as hard as factoring:
    // Encryption: C = M² mod n
    // Decryption requires square roots mod p and mod q (via CRT)
    // Security = factoring (proven: breaking Rabin → factoring n)
    
    // Padding is essential:
    // RSA-OAEP (PKCS#1 v2.1) provides IND-CCA2 security
    // Without padding, RSA is deterministic (eavesdropper can verify guesses)
    public static class RSAOAEP {
        // OAEP padding:
        // 1. Pad message with random salt
        // 2. Apply Feistel-like network with hash function
        // 3. Result is as long as the modulus
        
        // Security proof (Bellare-Rogaway, 1994):
        // RSA-OAEP is IND-CCA2 secure in the random oracle model
        // Assumption: RSA is one-way (≈ factoring)
    }
}
```

## 2. Diffie-Hellman Key Exchange

Diffie-Hellman allows two parties to agree on a shared secret over an insecure channel.

### The Protocol

```java
public class DiffieHellman {
    // Public parameters: (g, p) where p is a large prime, g is a generator of Z_p*
    // g is a primitive root modulo p (its multiplicative order is p-1)
    
    // Alice generates:
    //   a = random (1 < a < p-1) — private key
    //   A = g^a mod p — public key, sent to Bob
    //
    // Bob generates:
    //   b = random (1 < b < p-1) — private key
    //   B = g^b mod p — public key, sent to Alice
    //
    // Shared secret (both compute the same):
    //   Alice: s = B^a mod p = (g^b)^a = g^{ab} mod p
    //   Bob:   s = A^b mod p = (g^a)^b = g^{ab} mod p
    
    // Computational Diffie-Hellman (CDH) assumption:
    // Given (g, g^a, g^b), it's computationally infeasible to compute g^{ab}
    //
    // Decisional Diffie-Hellman (DDH) assumption:
    // The triple (g^a, g^b, g^{ab}) is indistinguishable from (g^a, g^b, g^c)
    // for random c
    
    static class DHParty {
        private final long privateKey;
        public final long publicKey;
        private final long prime, generator;
        
        public DHParty(long prime, long generator) {
            this.prime = prime;
            this.generator = generator;
            this.privateKey = new SecureRandom().nextLong(prime - 2) + 2;
            this.publicKey = modPow(generator, privateKey, prime);
        }
        
        public long computeSharedSecret(long otherPublicKey) {
            return modPow(otherPublicKey, privateKey, prime);
        }
    }
    
    public static void main(String[] args) {
        // Small example (DO NOT USE IN PRODUCTION)
        long p = 23L; // prime
        long g = 5L;  // primitive root mod 23
        
        DHParty alice = new DHParty(p, g);
        DHParty bob = new DHParty(p, g);
        
        long sAlice = alice.computeSharedSecret(bob.publicKey);
        long sBob = bob.computeSharedSecret(alice.publicKey);
        
        System.out.printf("Alice's secret: %d%n", sAlice);
        System.out.printf("Bob's secret:   %d%n", sBob);
        System.out.printf("Match: %b%n", sAlice == sBob);
    }
    
    // Attacks:
    // 1. Man-in-the-Middle: without authentication, Eve can intercept
    //    → Mitigation: digital signatures (STS protocol, sig pub keys)
    // 2. Small subgroup attack: if p-1 has small factors
    //    → Mitigation: use safe primes (p = 2q + 1 where q is prime)
    // 3. Logjam attack: if weak export-grade DH parameters
    //    → Mitigation: use ≥ 2048-bit moduli
    
    // ElGamal encryption — based on DH:
    // Public key: (p, g, y = g^x)
    // Private key: x
    // Encrypt M: choose k, compute c1 = g^k, c2 = M·y^k
    // Decrypt: M = c2 / (c1^x)
    // Security: IND-CPA under DDH assumption
}
```

## 3. Elliptic Curve Cryptography Math

ECC provides equivalent security with much smaller key sizes than RSA.

### Elliptic Curve Group Law

```java
public class EllipticCurveMath {
    // Elliptic curve over finite field F_p:
    // E: y² = x³ + ax + b (mod p)
    // where 4a³ + 27b² ≠ 0 (mod p) — ensures no singular points
    
    // Point addition: P + Q = R
    // Let P = (x₁, y₁), Q = (x₂, y₂), P ≠ -Q
    // If P ≠ Q: λ = (y₂ - y₁) / (x₂ - x₁)
    // If P = Q (doubling): λ = (3x₁² + a) / (2y₁)
    // x₃ = λ² - x₁ - x₂
    // y₃ = λ(x₁ - x₃) - y₁
    
    // Point at infinity O = identity element
    // P + O = P
    // -P = (x, -y)
    // P + (-P) = O
    
    static class ECPoint {
        final long x, y;
        final boolean atInfinity;
        
        ECPoint() { x = 0; y = 0; atInfinity = true; }
        ECPoint(long x, long y) { this.x = x; this.y = y; atInfinity = false; }
        
        static ECPoint INFINITY = new ECPoint();
    }
    
    static class EllipticCurve {
        private final long a, b, p;
        
        EllipticCurve(long a, long b, long p) {
            this.a = a; this.b = b; this.p = p;
            long disc = (4 * a * a * a + 27 * b * b) % p;
            if (disc == 0) throw new IllegalArgumentException("Singular curve");
        }
        
        boolean isOnCurve(ECPoint pt) {
            if (pt.atInfinity) return true;
            long lhs = (pt.y * pt.y) % p;
            long rhs = (pt.x * pt.x * pt.x + a * pt.x + b) % p;
            return (lhs - rhs) % p == 0;
        }
        
        ECPoint add(ECPoint P, ECPoint Q) {
            if (P.atInfinity) return Q;
            if (Q.atInfinity) return P;
            if (P.x == Q.x && (P.y + Q.y) % p == 0) return ECPoint.INFINITY;
            
            long lambda;
            if (P.x == Q.x && P.y == Q.y) {
                // Point doubling
                long num = (3 * P.x * P.x + a) % p;
                long den = (2 * P.y) % p;
                lambda = num * modInverse(den, p) % p;
            } else {
                long num = (Q.y - P.y + p) % p;
                long den = (Q.x - P.x + p) % p;
                lambda = num * modInverse(den, p) % p;
            }
            
            long x3 = (lambda * lambda - P.x - Q.x + 2 * p) % p;
            long y3 = (lambda * (P.x - x3 + p) % p - P.y + p) % p;
            return new ECPoint(x3, y3);
        }
        
        // Scalar multiplication: n·P
        ECPoint multiply(long n, ECPoint P) {
            if (n == 0 || P.atInfinity) return ECPoint.INFINITY;
            if (n < 0) return multiply(-n, new ECPoint(P.x, (-P.y + p) % p));
            
            ECPoint result = ECPoint.INFINITY;
            ECPoint addend = P;
            
            while (n > 0) {
                if ((n & 1) == 1) result = add(result, addend);
                addend = add(addend, addend); // double
                n >>= 1;
            }
            return result;
        }
    }
    
    // ECC security:
    // Elliptic Curve Discrete Logarithm Problem (ECDLP):
    // Given curve E, point P, point Q = n·P, find n
    // Best known attack: Pollard's rho, O(√n) — exponential in key size
    //
    // Key size comparison:
    // RSA 1024 ≈ ECC 160 ≈ AES 80 (security level = 80 bits)
    // RSA 2048 ≈ ECC 224 ≈ AES 112
    // RSA 3072 ≈ ECC 256 ≈ AES 128
    // RSA 7680 ≈ ECC 384 ≈ AES 192
    // RSA 15360 ≈ ECC 521 ≈ AES 256
}
```

### ECDH (Elliptic Curve Diffie-Hellman)

```java
public class ECDH {
    // Same as DH but using elliptic curve group instead of Z_p*
    // 
    // Public parameters: curve E over F_p, base point G of order n
    // 
    // Alice generates: a (random) → A = a·G (EC point)
    // Bob generates: b (random) → B = b·G (EC point)
    // Shared secret: a·B = a·(b·G) = b·(a·G) = b·A = ab·G (x-coordinate)
    //
    // Benefits over DH:
    // - Shorter keys (256-bit ECC ≈ 3072-bit RSA)
    // - Faster computation
    // - Less bandwidth
    
    // Standards:
    // - P-256 (secp256r1): NIST standard
    // - P-384 (secp384r1): higher security
    // - Curve25519 (X25519): Daniel Bernstein's curve, constant-time
    //   Avoids shared-secret bias, faster than NIST curves
    
    // ECDSA (ECDSA signature):
    // Sign: s = k^(-1)(H(m) + r·d) mod n
    //   where k = random nonce, r = (k·G).x, d = private key
    // Verify: u₁·G + u₂·Q = R where Q = d·G (public key)
    // Critical: k must be truly random and unique!
    //   Sony PS3: used same k → private key recovered
    //   ← k use HW RNG or RFC 6979 (deterministic k from HMAC)
}
```

## 4. AES S-Box Construction

The AES S-box is the only non-linear component of AES and is mathematically constructed.

### S-Box Math

```java
public class AESSBox {
    // AES S-box is constructed via:
    // 1. Multiplicative inverse in GF(2⁸)
    // 2. Affine transformation over GF(2)
    
    // GF(2⁸) is the finite field with 2⁸ elements
    // Represented as polynomials of degree ≤ 7 with coefficients in {0,1}
    // Irreducible polynomial: m(x) = x⁸ + x⁴ + x³ + x + 1 (0x11B)
    //
    // The multiplicative inverse of a in GF(2⁸) is b such that:
    // a(x) · b(x) ≡ 1 mod m(x)
    //
    // If a = 0, the inverse is 0 (special case for S-box)
    
    private static final int IRREDUCIBLE = 0x11B; // x⁸ + x⁴ + x³ + x + 1
    
    // Multiplication in GF(2⁸)
    private static int gfMult(int a, int b) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            if ((b & 1) == 1) result ^= a;
            boolean high = (a & 0x80) != 0;
            a = (a << 1) & 0xFF;
            if (high) a ^= IRREDUCIBLE;
            b >>= 1;
        }
        return result;
    }
    
    // Extended Euclidean in GF(2⁸)
    private static int gfInverse(int a) {
        if (a == 0) return 0;
        int r0 = IRREDUCIBLE, r1 = a;
        int s0 = 1, s1 = 0;
        
        while (r1 != 1) {
            // Polynomial division
            int degreeDiff = degree(r0) - degree(r1);
            int quotient = 0;
            
            if (degreeDiff >= 0) {
                // Shift divisor to match degree of dividend
                int shifted = r1;
                for (int i = 0; i < degreeDiff; i++) shifted <<= 1;
                quotient = 1 << degreeDiff;
                r0 ^= shifted;
            }
            
            int temp = r0; r0 = r1; r1 = temp;
            // Update s similarly (simplified for clarity)
            throw new UnsupportedOperationException("Full GF inverse requires proper poly division");
        }
        return s0;
    }
    
    private static int degree(int poly) {
        return 31 - Integer.numberOfLeadingZeros(poly);
    }
    
    // The affine transformation:
    // b'_i = b_i ⊕ b_(i+4 mod 8) ⊕ b_(i+5 mod 8) ⊕ b_(i+6 mod 8) ⊕ b_(i+7 mod 8) ⊕ c_i
    // where c = 0x63 (01100011)
    private static int affineTransform(int b) {
        int result = 0;
        int c = 0x63;
        for (int i = 0; i < 8; i++) {
            int bit = ((b >> i) & 1) ^ ((b >> ((i + 4) % 8)) & 1)
                    ^ ((b >> ((i + 5) % 8)) & 1) ^ ((b >> ((i + 6) % 8)) & 1)
                    ^ ((b >> ((i + 7) % 8)) & 1) ^ ((c >> i) & 1);
            result |= (bit << i);
        }
        return result;
    }
    
    // Full S-box computation
    public static int[] computeSBox() {
        int[] sbox = new int[256];
        for (int i = 0; i < 256; i++) {
            int inv = gfInverse(i);
            sbox[i] = affineTransform(inv);
        }
        return sbox;
    }
    
    // Precomputed AES S-box:
    public static final int[] SBOX = {
        0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
        0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
        0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
        0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
        0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
        0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
        0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
        0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
        0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
        0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
        0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
        0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
        0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
        0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
        0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
        0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16
    };
}
```

## 5. SHA-256 Merkle-Damgård Construction

SHA-256 uses the Merkle-Damgård construction to hash arbitrary-length messages.

### Construction

```java
public class SHA256MerkleDamgard {
    // Merkle-Damgård construction:
    // 1. Pad message to multiple of block size (512 bits for SHA-256)
    // 2. Split into blocks M₁, M₂, ..., Mₙ
    // 3. Iterate: H₀ = IV (initial vector)
    //    Hᵢ = compress(Hᵢ₋₁, Mᵢ)
    // 4. Output Hₙ is the hash
    
    // Padding: append 1 bit, then zeros, then 64-bit length
    // Original message: |M|
    // Padding: |M| + 1 + zeros + 64 bits ≡ 0 mod 512 bits
    
    // Compression function f(H, M):
    // 1. Prepare message schedule W₀...W₆₃ from M
    // 2. Initialize working variables from H
    // 3. Apply 64 rounds of the compression function
    // 4. Add working variables to H
    
    // Initial values (IV):
    private static final int[] H0 = {
        0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
        0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
    };
    
    // Round constants (first 32 bits of fractional parts of cube roots of primes):
    private static final int[] K = {
        0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
        0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
        0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
        0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
        0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
        0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
        0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
        0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
        0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
        0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
        0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
        0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };
    
    // SHA-256 round function:
    private static int ch(int x, int y, int z) { return (x & y) ^ (~x & z); }
    private static int maj(int x, int y, int z) { return (x & y) ^ (x & z) ^ (y & z); }
    private static int sigma0(int x) { return Integer.rotateRight(x, 2) ^ Integer.rotateRight(x, 13) ^ Integer.rotateRight(x, 22); }
    private static int sigma1(int x) { return Integer.rotateRight(x, 6) ^ Integer.rotateRight(x, 11) ^ Integer.rotateRight(x, 25); }
    private static int gamma0(int x) { return Integer.rotateRight(x, 7) ^ Integer.rotateRight(x, 18) ^ (x >>> 3); }
    private static int gamma1(int x) { return Integer.rotateRight(x, 17) ^ Integer.rotateRight(x, 19) ^ (x >>> 10); }
    
    // Process a single 512-bit block
    private static int[] compress(int[] H, byte[] block) {
        int[] W = new int[64];
        
        // Prepare message schedule
        for (int t = 0; t < 16; t++) {
            W[t] = ((block[4*t] & 0xFF) << 24) |
                   ((block[4*t+1] & 0xFF) << 16) |
                   ((block[4*t+2] & 0xFF) << 8) |
                   (block[4*t+3] & 0xFF);
        }
        for (int t = 16; t < 64; t++) {
            W[t] = gamma1(W[t-2]) + W[t-7] + gamma0(W[t-15]) + W[t-16];
        }
        
        // Initialize working variables
        int a = H[0], b = H[1], c = H[2], d = H[3];
        int e = H[4], f = H[5], g = H[6], h = H[7];
        
        // Main loop
        for (int t = 0; t < 64; t++) {
            int T1 = h + sigma1(e) + ch(e, f, g) + K[t] + W[t];
            int T2 = sigma0(a) + maj(a, b, c);
            h = g; g = f; f = e;
            e = d + T1;
            d = c; c = b; b = a;
            a = T1 + T2;
        }
        
        // Update hash values
        return new int[] {
            H[0] + a, H[1] + b, H[2] + c, H[3] + d,
            H[4] + e, H[5] + f, H[6] + g, H[7] + h
        };
    }
    
    // Full SHA-256 hash
    public static byte[] sha256(byte[] message) {
        // Calculate padding
        int originalLength = message.length;
        long bitLength = (long) originalLength * 8;
        int padLen = (56 - (originalLength + 1) % 64 + 64) % 64;
        byte[] padded = new byte[originalLength + 1 + padLen + 8];
        
        System.arraycopy(message, 0, padded, 0, originalLength);
        padded[originalLength] = (byte) 0x80; // Append 1 bit
        
        // Append length in bits (big-endian, 64-bit)
        for (int i = 0; i < 8; i++) {
            padded[padded.length - 1 - i] = (byte) (bitLength >> (8 * i));
        }
        
        int[] H = H0.clone();
        for (int i = 0; i < padded.length; i += 64) {
            byte[] block = new byte[64];
            System.arraycopy(padded, i, block, 0, 64);
            H = compress(H, block);
        }
        
        byte[] result = new byte[32];
        for (int i = 0; i < 8; i++) {
            result[4*i] = (byte) (H[i] >> 24);
            result[4*i+1] = (byte) (H[i] >> 16);
            result[4*i+2] = (byte) (H[i] >> 8);
            result[4*i+3] = (byte) H[i];
        }
        return result;
    }
    
    // Security properties:
    // 1. Preimage resistance: given h, find m with hash(m) = h
    //    → 2²⁵⁶ operations required (birthday: 2¹²⁸)
    // 2. Second preimage resistance: given m₁, find m₂ ≠ m₁ with hash(m₁) = hash(m₂)
    //    → 2²⁵⁶ operations required
    // 3. Collision resistance: find any m₁ ≠ m₂ with hash(m₁) = hash(m₂)
    //    → 2¹²⁸ operations required (birthday bound)
    //
    // Length extension attack:
    // Given hash(M), attacker can compute hash(M || padding || extension)
    // without knowing M. This is why HMAC exists:
    // HMAC(K, M) = hash((K ⊕ opad) || hash((K ⊕ ipad) || M))
    //
    // SHA-3 (Keccak) uses sponge construction, not Merkle-Damgård
    // Sponge is not vulnerable to length extension attacks
}
```

## 6. Cryptographic Security Summary

```java
public class CryptoSummary {
    // Algorithm | Type     | Security Level | Key Size   | Performance
    // ==========|==========|================|============|=============
    // RSA       | Asymmetric | Factoring    | 2048-4096  | Slow (exponentiation)
    // ECC       | Asymmetric | ECDLP        | 256-521    | Fast (scalar mult)
    // DH        | Asymmetric | CDH/DDH      | 2048-4096  | Slow
    // ECDH      | Asymmetric | ECDLP        | 256-521    | Fast
    // AES       | Symmetric  | Best: 2^key  | 128-256    | Very fast
    // SHA-256   | Hash      | Collision    | 256-bit    | Fast
    // SHA-3     | Hash      | Collision    | 256-512    | Fast (sponge)
    
    // Best practice recommendations (2024+):
    // - Key exchange: X25519 (Curve25519)
    // - Signatures: Ed25519 (EdDSA)
    // - Encryption: AES-256-GCM (authenticated encryption)
    // - Hashing: SHA-256 or SHA-3-256
    // - Key agreement: ECDH with P-256 or X25519
    // - Password hashing: Argon2id (not SHA-256!)
    // - TLS 1.3: uses AEAD + ECDHE key exchange
    
    // Quantum threat:
    // Shor's algorithm breaks RSA, ECC, DH in polynomial time
    // Grover's algorithm halves symmetric key security
    // Post-quantum candidates: CRYSTALS-Kyber (KEM), CRYSTALS-Dilithium (signatures)
    // NIST selected Kyber and Dilithium for standardization (2024)
}
```

The mathematical foundations (modular arithmetic, group theory, finite fields) are essential for understanding cryptographic security. These proofs show why breaking these schemes reduces to unsolved mathematical problems.
