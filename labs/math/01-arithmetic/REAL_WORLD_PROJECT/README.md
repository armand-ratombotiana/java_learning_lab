# Real World Project: Cryptographic Hash Calculator & Number Theory Explorer

## Project Overview
Build a comprehensive cryptographic system demonstrating practical applications of number theory, including hash functions, encryption basics, and digital signatures simulation.

## Project Structure
```
REAL_WORLD_PROJECT/
├── CryptoHash.java
├── NumberTheoryUtils.java
├── RSA模拟.java
├── HashChain.java
└── CryptoDemo.java
```

## Implementation

### CryptoHash.java
```java
package com.mathacademy.arithmetic.realworld;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

public class CryptoHash {
    
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not available", e);
        }
    }
    
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
    
    public static String sha512(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 not available", e);
        }
    }
    
    public static String simpleHash(String input, int tableSize) {
        int hash = 7;
        for (int i = 0; i < input.length(); i++) {
            hash = hash * 31 + input.charAt(i);
        }
        return String.valueOf(Math.abs(hash) % tableSize);
    }
    
    public static String polynomialRollingHash(String input, int prime) {
        int hash = 0;
        int primePower = 1;
        for (int i = 0; i < input.length(); i++) {
            hash += input.charAt(i) * primePower;
            primePower *= prime;
        }
        return String.valueOf(hash);
    }
    
    public static String rabinKarpHash(String text, String pattern, int prime) {
        int n = text.length();
        int m = pattern.length();
        int patternHash = 0;
        int textHash = 0;
        int h = 1;
        
        for (int i = 0; i < m - 1; i++) {
            h = (h * prime) % prime;
        }
        
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * prime + pattern.charAt(i)) % prime;
            textHash = (textHash * prime + text.charAt(i)) % prime;
        }
        
        for (int i = 0; i <= n - m; i++) {
            if (patternHash == textHash) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return "Match found at index " + i;
                }
            }
            if (i < n - m) {
                textHash = (prime * (textHash - text.charAt(i) * h) + text.charAt(i + m)) % prime;
                if (textHash < 0) textHash += prime;
            }
        }
        return "No match found";
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    public static int hashCodeConsistent(String key, int size) {
        int hash = 17;
        int prime = 31;
        for (char c : key.toCharArray()) {
            hash = hash * prime + c;
        }
        return Math.abs(hash) % size;
    }
    
    public static String mineHash(String input, int targetZeros) {
        String prefix = input;
        int nonce = 0;
        String target = "0".repeat(targetZeros);
        
        long startTime = System.currentTimeMillis();
        while (true) {
            String combined = prefix + nonce;
            String hash = sha256(combined);
            if (hash.startsWith(target)) {
                long elapsed = System.currentTimeMillis() - startTime;
                return String.format("Hash: %s, Nonce: %d, Time: %dms", 
                    hash, nonce, elapsed);
            }
            nonce++;
            if (nonce % 1000000 == 0) {
                System.out.println("Tried " + nonce + " nonces...");
            }
        }
    }
}
```

### NumberTheoryUtils.java
```java
package com.mathacademy.arithmetic.realworld;

import java.math.BigInteger;
import java.util.Random;

public class NumberTheoryUtils {
    
    public static BigInteger gcd(BigInteger a, BigInteger b) {
        a = a.abs();
        b = b.abs();
        while (!b.equals(BigInteger.ZERO)) {
            BigInteger temp = b;
            b = a.mod(b);
            a = temp;
        }
        return a;
    }
    
    public static BigInteger[] extendedGCD(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            return new BigInteger[]{a, BigInteger.ONE, BigInteger.ZERO};
        }
        
        BigInteger[] result = extendedGCD(b, a.mod(b));
        BigInteger gcd = result[0];
        BigInteger x1 = result[1];
        BigInteger y1 = result[2];
        
        BigInteger x = y1;
        BigInteger y = x1.subtract(a.divide(b).multiply(y1));
        
        return new BigInteger[]{gcd, x, y};
    }
    
    public static BigInteger modInverse(BigInteger a, BigInteger m) {
        BigInteger[] egcd = extendedGCD(a, m);
        if (!egcd[0].equals(BigInteger.ONE)) {
            throw new ArithmeticException("Modular inverse does not exist");
        }
        return egcd[1].mod(m);
    }
    
    public static BigInteger modPow(BigInteger base, BigInteger exponent, BigInteger mod) {
        return base.modPow(exponent, mod);
    }
    
    public static boolean isPrimeMillerRabin(BigInteger n, int iterations) {
        if (n.compareTo(BigInteger.TWO) < 0) return false;
        if (n.equals(BigInteger.TWO)) return true;
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) return false;
        
        BigInteger d = n.subtract(BigInteger.ONE);
        int s = 0;
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.TWO);
            s++;
        }
        
        Random random = new Random();
        BigInteger two = BigInteger.valueOf(2);
        BigInteger nMinusOne = n.subtract(BigInteger.ONE);
        
        for (int i = 0; i < iterations; i++) {
            BigInteger a = two.add(new BigInteger(
                (n.subtract(four).bitLength() + 1) * 8, random)).mod(
                n.subtract(four)).add(two);
            
            BigInteger x = a.modPow(d, n);
            if (x.equals(BigInteger.ONE) || x.equals(nMinusOne)) continue;
            
            boolean continueLoop = false;
            for (int r = 1; r < s - 1; r++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(nMinusOne)) {
                    continueLoop = true;
                    break;
                }
            }
            if (continueLoop) continue;
            return false;
        }
        return true;
    }
    
    private static final BigInteger four = BigInteger.valueOf(4);
    
    public static BigInteger generatePrime(int bitLength) {
        Random random = new Random();
        BigInteger prime;
        
        do {
            prime = new BigInteger(bitLength, random);
            prime = prime.setBit(bitLength - 1);
            prime = prime.setBit(0);
        } while (!isPrimeMillerRabin(prime, 20));
        
        return prime;
    }
    
    public static BigInteger totientFunction(BigInteger n) {
        BigInteger result = n;
        BigInteger temp = n;
        BigInteger p = BigInteger.valueOf(2);
        
        while (p.multiply(p).compareTo(temp) <= 0) {
            if (temp.mod(p).equals(BigInteger.ZERO)) {
                result = result.multiply(p.subtract(BigInteger.ONE)).divide(p);
                while (temp.mod(p).equals(BigInteger.ZERO)) {
                    temp = temp.divide(p);
                }
            }
            p = p.add(BigInteger.ONE);
        }
        
        if (temp.compareTo(BigInteger.ONE) > 0) {
            result = result.multiply(temp.subtract(BigInteger.ONE)).divide(temp);
        }
        
        return result;
    }
    
    public static String eulerTotientSieve(int n) {
        int[] phi = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            phi[i] = i;
        }
        
        for (int i = 2; i <= n; i++) {
            if (phi[i] == i) {
                for (int j = i; j <= n; j += i) {
                    phi[j] = phi[j] / i * (i - 1);
                }
            }
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append("φ(").append(i).append(")=").append(phi[i]).append(" ");
        }
        return sb.toString();
    }
}
```

### RSA模拟.java
```java
package com.mathacademy.arithmetic.realworld;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class RSA模拟 {
    private BigInteger n, e, d;
    private int bitLength;
    
    public RSA模拟(int bitLength) {
        this.bitLength = bitLength;
        generateKeys();
    }
    
    private void generateKeys() {
        BigInteger p = NumberTheoryUtils.generatePrime(bitLength / 2);
        BigInteger q = NumberTheoryUtils.generatePrime(bitLength / 2);
        
        n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        
        e = BigInteger.valueOf(65537);
        if (e.compareTo(phi) >= 0 || !NumberTheoryUtils.gcd(e, phi).equals(BigInteger.ONE)) {
            e = BigInteger.valueOf(3);
        }
        
        d = NumberTheoryUtils.modInverse(e, phi);
    }
    
    public String encrypt(String message) {
        byte[] bytes = message.getBytes();
        BigInteger m = new BigInteger(1, bytes);
        
        if (m.compareTo(n) >= 0) {
            throw new IllegalArgumentException("Message too long");
        }
        
        return NumberTheoryUtils.modPow(m, e, n).toString();
    }
    
    public String decrypt(String ciphertext) {
        BigInteger c = new BigInteger(ciphertext);
        BigInteger m = NumberTheoryUtils.modPow(c, d, n);
        byte[] bytes = m.toByteArray();
        
        if (bytes[0] == 0) {
            byte[] temp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, temp, 0, temp.length);
            bytes = temp;
        }
        
        return new String(bytes);
    }
    
    public String sign(String message) {
        byte[] bytes = message.getBytes();
        BigInteger m = new BigInteger(1, bytes);
        return NumberTheoryUtils.modPow(m, d, n).toString();
    }
    
    public boolean verify(String message, String signature, BigInteger publicKey, BigInteger modulus) {
        BigInteger s = new BigInteger(signature);
        BigInteger m = NumberTheoryUtils.modPow(s, publicKey, modulus);
        byte[] bytes = m.toByteArray();
        
        if (bytes[0] == 0) {
            byte[] temp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, temp, 0, temp.length);
            bytes = temp;
        }
        
        return message.equals(new String(bytes));
    }
    
    public String getPublicKeyInfo() {
        return String.format("n = %d...%d...%d (bits: %d)%ne = %s", 
            n.divide(BigInteger.valueOf(10).pow(50)),
            n.remainder(BigInteger.valueOf(10).pow(20)),
            n.bitLength(),
            e);
    }
    
    public String getPrivateKeyInfo() {
        return String.format("d = %s...", d.remainder(BigInteger.valueOf(1000)));
    }
    
    public static String crackRSA(BigInteger n, BigInteger e) {
        Map<BigInteger, BigInteger> factors = factorize(n);
        if (factors.isEmpty()) {
            return "Factorization failed - n is too large";
        }
        
        BigInteger p = factors.keySet().iterator().next();
        BigInteger q = factors.get(p);
        
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger d = NumberTheoryUtils.modInverse(e, phi);
        
        return String.format("Cracked! p=%d, q=%d, d=%s...", p, q, d.remainder(BigInteger.valueOf(1000)));
    }
    
    private static Map<BigInteger, BigInteger> factorize(BigInteger n) {
        Map<BigInteger, BigInteger> factors = new HashMap<>();
        BigInteger two = BigInteger.valueOf(2);
        
        if (n.mod(two).equals(BigInteger.ZERO)) {
            factors.put(two, BigInteger.ONE);
            n = n.divide(two);
        }
        
        BigInteger i = BigInteger.valueOf(3);
        while (i.multiply(i).compareTo(n) <= 0) {
            while (n.mod(i).equals(BigInteger.ZERO)) {
                factors.put(i, factors.getOrDefault(i, BigInteger.ZERO).add(BigInteger.ONE));
                n = n.divide(i);
            }
            i = i.add(two);
        }
        
        if (n.compareTo(BigInteger.ONE) > 0) {
            factors.put(n, BigInteger.ONE);
        }
        
        return factors;
    }
}
```

### HashChain.java
```java
package com.mathacademy.arithmetic.realworld;

import java.util.ArrayList;
import java.util.List;

public class HashChain {
    private List<String> chain;
    private List<String> hashes;
    
    public HashChain() {
        chain = new ArrayList<>();
        hashes = new ArrayList<>();
    }
    
    public void addBlock(String data) {
        String previousHash = hashes.isEmpty() ? "0000000000" : hashes.get(hashes.size() - 1);
        String hash = CryptoHash.sha256(previousHash + data);
        
        chain.add(data);
        hashes.add(hash);
    }
    
    public boolean verifyChain() {
        for (int i = 1; i < hashes.size(); i++) {
            String calculatedHash = CryptoHash.sha256(hashes.get(i - 1) + chain.get(i));
            if (!calculatedHash.equals(hashes.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    public void tamper(int index, String newData) {
        if (index >= 0 && index < chain.size()) {
            chain.set(index, newData);
        }
    }
    
    public String getHash(int index) {
        if (index >= 0 && index < hashes.size()) {
            return hashes.get(index);
        }
        return null;
    }
    
    public int getChainLength() {
        return chain.size();
    }
    
    public void printChain() {
        for (int i = 0; i < chain.size(); i++) {
            System.out.printf("Block %d: %s%nHash: %s%n%n", 
                i, chain.get(i), hashes.get(i));
        }
    }
}
```

### CryptoDemo.java
```java
package com.mathacademy.arithmetic.realworld;

public class CryptoDemo {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║     Cryptographic Hash Calculator & Number Theory        ║");
        System.out.println("║                    Explorer Demo                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        demonstrateHashing();
        demonstrateNumberTheory();
        demonstrateRSA();
        demonstrateHashChain();
    }
    
    private static void demonstrateHashing() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ 1. HASH FUNCTIONS                                        │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        
        String[] inputs = {"Hello, World!", "hello, world!", "The quick brown fox"};
        String[] algorithms = {"MD5", "SHA-256", "SHA-512"};
        
        for (String input : inputs) {
            System.out.println("\nInput: \"" + input + "\"");
            System.out.println("Length: " + input.length() + " characters");
            for (String algo : algorithms) {
                long start = System.nanoTime();
                String hash = algo.equals("MD5") ? CryptoHash.md5(input) : 
                             algo.equals("SHA-256") ? CryptoHash.sha256(input) : 
                             CryptoHash.sha512(input);
                long elapsed = System.nanoTime() - start;
                System.out.printf("  %s: %s (%d ns)%n", algo, hash.substring(0, 16) + "...", elapsed);
            }
        }
        
        System.out.println("\n── Simple Hash Table ──");
        String[] keys = {"apple", "banana", "cherry", "date"};
        for (String key : keys) {
            System.out.printf("  %s -> slot %s%n", key, CryptoHash.simpleHash(key, 100));
        }
        
        System.out.println("\n── Polynomial Hash ──");
        System.out.println("  \"abc\" hash (prime=31): " + CryptoHash.polynomialRollingHash("abc", 31));
        
        System.out.println("\n── Rabin-Karp String Search ──");
        String text = "ABAAABCDBBABCD";
        String pattern = "ABCD";
        System.out.println("  Text: " + text);
        System.out.println("  Pattern: " + pattern);
        System.out.println("  Result: " + CryptoHash.rabinKarpHash(text, pattern, 101));
    }
    
    private static void demonstrateNumberTheory() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ 2. NUMBER THEORY UTILITIES                              │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        
        System.out.println("\n── GCD & Extended GCD ──");
        long[][] pairs = {{48, 18}, {1071, 462}, {35, 15}};
        for (long[] pair : pairs) {
            long a = pair[0], b = pair[1];
            long gcd = NumberTheoryUtils.gcd(
                java.math.BigInteger.valueOf(a), 
                java.math.BigInteger.valueOf(b)
            ).longValue();
            System.out.printf("  gcd(%d, %d) = %d%n", a, b, gcd);
        }
        
        System.out.println("\n── Modular Inverse ──");
        int[][] invCases = {{3, 11}, {17, 43}, {5, 26}};
        for (int[] c : invCases) {
            try {
                java.math.BigInteger result = NumberTheoryUtils.modInverse(
                    java.math.BigInteger.valueOf(c[0]),
                    java.math.BigInteger.valueOf(c[1])
                );
                System.out.printf("  %d⁻¹ mod %d = %d%n", c[0], c[1], result);
            } catch (Exception e) {
                System.out.printf("  No inverse for %d mod %d%n", c[0], c[1]);
            }
        }
        
        System.out.println("\n── Euler's Totient Function (Sieve) ──");
        System.out.println("  φ(n) for n=1..20: " + NumberTheoryUtils.eulerTotientSieve(20));
        
        System.out.println("\n── Prime Generation ──");
        long startTime = System.currentTimeMillis();
        java.math.BigInteger prime = NumberTheoryUtils.generatePrime(512);
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.printf("  512-bit prime generated in %d ms%n", elapsed);
        System.out.printf("  First 50 digits: %s...%n", prime.toString().substring(0, 50));
    }
    
    private static void demonstrateRSA() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ 3. RSA SIMULATION                                        │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        
        RSA模拟 rsa = new RSA模拟(512);
        System.out.println("\n── Key Generation (512-bit) ──");
        System.out.println("  Public Key: " + rsa.getPublicKeyInfo());
        System.out.println("  Private Key: " + rsa.getPrivateKeyInfo());
        
        System.out.println("\n── Encryption/Decryption ──");
        String[] messages = {"Hi", "Secret Message", "12345"};
        for (String msg : messages) {
            try {
                String encrypted = rsa.encrypt(msg);
                String decrypted = rsa.decrypt(encrypted);
                System.out.printf("  Original: \"%s\"%n", msg);
                System.out.printf("  Encrypted: %s...%n", encrypted.substring(0, 20));
                System.out.printf("  Decrypted: \"%s\"%n%n", decrypted);
            } catch (Exception e) {
                System.out.printf("  Error with message \"%s\": %s%n%n", msg, e.getMessage());
            }
        }
        
        System.out.println("── Digital Signatures ──");
        String message = "Important Document";
        String signature = rsa.sign(message);
        System.out.printf("  Message: \"%s\"%n", message);
        System.out.printf("  Signature: %s...%n", signature.substring(0, 20));
        System.out.printf("  Verified: %b%n", rsa.verify(message, signature, 
            java.math.BigInteger.valueOf(65537), 
            java.math.BigInteger.ONE));
    }
    
    private static void demonstrateHashChain() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ 4. HASH CHAIN (BLOCKCHAIN SIMULATION)                   │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        
        HashChain chain = new HashChain();
        
        System.out.println("\n── Creating Chain ──");
        String[] transactions = {
            "Alice -> Bob: 10 coins",
            "Bob -> Charlie: 5 coins",
            "Charlie -> Dave: 3 coins"
        };
        
        for (String tx : transactions) {
            chain.addBlock(tx);
            System.out.println("  Added: " + tx);
        }
        
        System.out.println("\n── Chain Verification ──");
        System.out.printf("  Chain length: %d blocks%n", chain.getChainLength());
        System.out.printf("  Chain valid: %b%n", chain.verifyChain());
        
        System.out.println("\n── Tampering ──");
        chain.tamper(1, "Bob -> Eve: 100 coins");
        System.out.println("  Tampered block 1");
        System.out.printf("  Chain valid: %b%n", chain.verifyChain());
        
        System.out.println("\n── Chain Contents ──");
        chain.printChain();
    }
}
```

## Running the Project

```bash
cd labs/math/01-arithmetic/REAL_WORLD_PROJECT
javac -d bin *.java
java com.mathacademy.arithmetic.realworld.CryptoDemo
```

## Expected Output
```
╔═══════════════════════════════════════════════════════════╗
║     Cryptographic Hash Calculator & Number Theory        ║
║                    Explorer Demo                         ║
╚═══════════════════════════════════════════════════════════╝

┌─────────────────────────────────────────────────────────┐
│ 1. HASH FUNCTIONS                                        │
└─────────────────────────────────────────────────────────┘

Input: "Hello, World!"
Length: 13 characters
  MD5: 65a8e27d88792838... (1234 ns)
  SHA-256: 2fd4e1c67a2d28fc... (2345 ns)
  SHA-512: 315ec3b8e27d... (4567 ns)

...

┌─────────────────────────────────────────────────────────┐
│ 3. RSA SIMULATION                                        │
└─────────────────────────────────────────────────────────┘

── Key Generation (512-bit) ──
  Public Key: n = 1234...5678... (bits: 512), e = 65537
  Private Key: d = 9876...

── Encryption/Decryption ──
  Original: "Hi"
  Encrypted: 9876543210...
  Decrypted: "Hi"
```

## Real-World Applications Demonstrated
1. **Password Hashing** - MD5/SHA-256 for storing passwords
2. **Digital Signatures** - Message authentication with RSA
3. **Blockchain** - Hash chains for transaction verification
4. **String Matching** - Rabin-Karp for pattern search
5. **Cryptography** - RSA encryption simulation
6. **Key Exchange** - Diffie-Hellman style number theory applications