$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\15-cryptographic-algorithms"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Cryptographic Algorithms — Overview

Covers hashing, encryption basics, and digital signatures in Java.

## Learning Objectives
- Understand cryptographic hash functions and their properties
- Implement and use hash-based data structures
- Understand symmetric and asymmetric encryption
- Implement digital signatures with Java Cryptography Architecture

## Prerequisites
- Basic number theory (modular arithmetic)
- Java I/O and file handling
- Understanding of byte arrays and encoding

## Estimated Time
- **Total**: 5–6 hours
"@

wf "THEORY.md" @"
# Cryptographic Algorithms — Theoretical Foundation

## Hash Functions
Map arbitrary-sized input to fixed-size output.
- Preimage resistance: Given h, hard to find m with h(m)
- Second preimage resistance: Given m₁, hard to find m₂ ≠ m₁ with h(m₁) = h(m₂)
- Collision resistance: Hard to find any m₁ ≠ m₂ with h(m₁) = h(m₂)

## Symmetric Encryption
Same key for encryption and decryption.
- AES: Advanced Encryption Standard (128/256-bit key)
- DES/3DES: Data Encryption Standard (legacy)
- Stream ciphers: XOR-based (RC4, ChaCha20)
- Block ciphers: Operate on blocks (AES)

## Asymmetric Encryption
Different keys for encryption (public) and decryption (private).
- RSA: Based on factoring large primes
- ECC: Elliptic Curve Cryptography
- Diffie-Hellman: Key exchange

## Digital Signatures
- Sign with private key, verify with public key
- Provides authentication, non-repudiation, integrity
- DSA, ECDSA, RSA-PSS
"@

wf "WHY_IT_EXISTS.md" @"
# Why Cryptographic Algorithms Exist

Cryptography enables secure communication over insecure channels. It provides confidentiality (encryption), integrity (hashing), authentication (signatures), and non-repudiation. Modern digital infrastructure — e-commerce, banking, messaging — depends entirely on cryptographic algorithms.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Cryptographic Algorithms Matter

- Data Security: Protect sensitive data at rest and in transit
- Authentication: Verify identity of parties in communication
- Integrity: Detect tampering of data
- Non-Repudiation: Prevent denial of actions
- Privacy: Enable confidential communication
- Blockchain: Cryptographic hash functions power cryptocurrencies
- Password Storage: Hashing (never store plaintext passwords)
- Digital Certificates: TLS/SSL (HTTPS) authentication
"@

wf "HISTORY.md" @"
# History of Cryptography

- 1900s BC: Egyptian hieroglyph substitution (first documented cipher)
- 50 BC: Caesar cipher (shift cipher)
- 1586: Vigenère cipher (polyalphabetic)
- 1918: Enigma machine (Germany WWII)
- 1949: Shannon's "Communication Theory of Secrecy Systems"
- 1976: Diffie-Hellman key exchange
- 1977: RSA algorithm (Rivest, Shamir, Adleman)
- 1991: DSA (Digital Signature Algorithm)
- 2001: AES standard (Rijndael)
- 2009: Bitcoin (SHA-256 hash function)
- 2010s+: Post-quantum cryptography development
"@

wf "MENTAL_MODELS.md" @"
# Mental Models

## Hash Function — "Digital Fingerprint"
Like a human fingerprint uniquely identifies a person, a hash uniquely identifies data (with extremely high probability). Even a tiny change produces a completely different hash.

## Encryption — "Lockbox with a Key"
Symmetric: Same key locks and unlocks the box.
Asymmetric: Anyone can lock (public key), only you can unlock (private key).

## Digital Signature — "Wax Seal"
A wax seal on a letter proves it came from you (authenticity) and hasn't been opened (integrity). A digital signature provides the same guarantees.
"@

wf "HOW_IT_WORKS.md" @"
# How Cryptographic Algorithms Work

## Hashing with SHA-256
```java
import java.security.MessageDigest;

public String hash(String input) throws Exception {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] digest = md.digest(input.getBytes());
    StringBuilder sb = new StringBuilder();
    for (byte b : digest) sb.append(String.format("%02x", b));
    return sb.toString();
}
```

## AES Encryption
```java
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public byte[] encryptAES(byte[] data, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return cipher.doFinal(data);
}
```

## RSA Digital Signature
```java
import java.security.Signature;

public byte[] sign(byte[] data, PrivateKey key) throws Exception {
    Signature sig = Signature.getInstance("SHA256withRSA");
    sig.initSign(key);
    sig.update(data);
    return sig.sign();
}
```
"@

wf "INTERNALS.md" @"
# Cryptographic Algorithms — Internal Mechanics

## Password Hashing with BCrypt
```java
import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordHasher {
    // Hash password with salt
    public static String hashPassword(char[] password) {
        return BCrypt.withDefaults()
            .hashToString(12, password); // cost factor 12
    }
    
    // Verify password against hash
    public static boolean verifyPassword(char[] password, String hash) {
        return BCrypt.verifyer()
            .verify(password, hash).verified;
    }
}
```

## HMAC (Keyed-Hash Message Authentication Code)
```java
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public String hmacSha256(String data, String key) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
    mac.init(keySpec);
    byte[] result = mac.doFinal(data.getBytes());
    StringBuilder sb = new StringBuilder();
    for (byte b : result) sb.append(String.format("%02x", b));
    return sb.toString();
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Cryptography

## Modular Arithmetic
- a ≡ b (mod n) means n divides (a - b)
- Encryption/decryption relies on modular exponentiation

## RSA Math
- Choose p, q (large primes)
- n = p × q
- φ(n) = (p-1)(q-1)
- Choose e with gcd(e, φ(n)) = 1
- d = e⁻¹ mod φ(n)
- Encrypt: c = mᵉ mod n
- Decrypt: m = cᵈ mod n

## Security of RSA
Based on the difficulty of factoring n = p × q.
No polynomial-time factoring algorithm is known (for classical computers).

## Elliptic Curve Cryptography
Based on the difficulty of the elliptic curve discrete logarithm problem.
Smaller key sizes than RSA for equivalent security (256-bit ECC ≈ 3072-bit RSA).
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Cryptography

## Symmetric Encryption Flow
```
Plaintext → [Encrypt] → Ciphertext → [Decrypt] → Plaintext
                ↑                          ↑
           Same Key                   Same Key
```

## Asymmetric Encryption Flow
```
Plaintext → [Encrypt] → Ciphertext → [Decrypt] → Plaintext
                ↑                          ↑
          Public Key                  Private Key
```

## Digital Signature Flow
```
Sender:
Document → [Hash] → Hash → [Sign with Private Key] → Signature

Recipient:
Document → [Hash] → Hash₁
Signature → [Verify with Public Key] → Hash₂
Compare Hash₁ == Hash₂
```
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Cryptography

## RSA Key Generation and Encryption
```java
import java.security.*;

public class RSAExample {
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        return gen.generateKeyPair();
    }

    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }
}
```

## Consistent Hashing (for distributed systems)
```java
public class ConsistentHash<T> {
    private final HashFunction hashFunction;
    private final int numberOfReplicas;
    private final SortedMap<Integer, T> circle = new TreeMap<>();

    public ConsistentHash(HashFunction hashFunction, int numberOfReplicas) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;
    }

    public void add(T node) {
        for (int i = 0; i < numberOfReplicas; i++)
            circle.put(hashFunction.hash(node.toString() + i), node);
    }

    public T get(Object key) {
        if (circle.isEmpty()) return null;
        int hash = hashFunction.hash(key.toString());
        if (!circle.containsKey(hash)) {
            SortedMap<Integer, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }
}
```
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Cryptography

## Hashing Passwords
1. Generate random salt (16+ bytes)
2. Combine password + salt
3. Apply bcrypt/scrypt/PBKDF2 (with cost factor)
4. Store salt + hash together
5. To verify: retrieve salt from stored hash, re-hash password, compare

## Using Digital Signatures
1. Generate key pair (PrivateKey, PublicKey)
2. Sign: hash message, encrypt hash with private key
3. Verify: decrypt signature with public key, compare hash

## Secure Communication (TLS)
1. Client connects, requests secure channel
2. Server sends certificate (with public key)
3. Client verifies certificate against trusted CAs
4. Client generates session key, encrypts with server's public key
5. Both use session key for symmetric encryption (AES)
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes

- Using MD5 or SHA-1 for security (broken — use SHA-256+)
- Not using salt for password hashing
- Using ECB mode for AES (leaks patterns — use GCM)
- Hardcoding encryption keys in source code
- Rolling your own cryptography — DON'T! Use standard libraries
- Not validating certificates properly in TLS
- Using constant-time comparison for sensitive data
- Storing passwords in plaintext or with fast hashes (MD5, SHA-256)
- Not updating cryptographic algorithms as standards evolve
"@

wf "DEBUGGING.md" @"
# Debugging — Cryptography

## Verify Hash Consistency
```java
String hash1 = hash("hello");
String hash2 = hash("hello");
assert hash1.equals(hash2) : "Hashing not deterministic";

String hash3 = hash("Hello"); // capital H
assert !hash1.equals(hash3) : "Different input should give different hash";
```

## Decryption Roundtrip Test
```java
byte[] original = "Secret message".getBytes();
byte[] encrypted = encrypt(original, publicKey);
byte[] decrypted = decrypt(encrypted, privateKey);
assert Arrays.equals(original, decrypted) : "Roundtrip failed";
```

## HMAC Verification
```java
String hmac1 = hmacSha256(data, key);
String hmac2 = hmacSha256(data, key);
assert hmac1.equals(hmac2) : "HMAC should match";
```
"@

wf "REFACTORING.md" @"
# Refactoring — Cryptography

## Using Java Cryptography Architecture
```java
// Provider-agnostic — change algorithms easily
Cipher cipher = Cipher.getInstance(algorithm);
// algorithm = "AES/GCM/NoPadding"
// algorithm = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
```

## Encapsulate Cryptography
```java
public interface CryptoService {
    String hash(String data);
    byte[] encrypt(byte[] data);
    byte[] decrypt(byte[] data);
    byte[] sign(byte[] data);
    boolean verify(byte[] data, byte[] signature);
}
```
"@

wf "PERFORMANCE.md" @"
# Performance — Cryptography

| Algorithm | Operation | Throughput | Key Size | Notes |
|-----------|-----------|------------|----------|-------|
| SHA-256 | Hash | ~500 MB/s | N/A | Hardware accelerated |
| SHA-3 | Hash | ~300 MB/s | N/A | Newer standard |
| AES-128-GCM | Encrypt | ~1 GB/s | 128-bit | Hardware accelerated |
| AES-256-GCM | Encrypt | ~800 MB/s | 256-bit | Hardware accelerated |
| RSA-2048 | Encrypt | ~1 MB/s | 2048-bit | Slow |
| RSA-2048 | Decrypt | ~50 KB/s | 2048-bit | Very slow |
| ECDSA | Sign | ~10,000/s | 256-bit | Fast |
| bcrypt | Hash | ~1,000/s | cost=10 | Deliberately slow |

## Guidelines
- Hash: SHA-256 or SHA-3
- Symmetric: AES-256-GCM
- Asymmetric: ECC (ECDH for key exchange)
- Signatures: ECDSA or EdDSA
- Passwords: bcrypt/argon2 (deliberately slow)
"@

wf "SECURITY.md" @"
# Security — Cryptographic Algorithms

- Key Management: Store keys securely (HSM, key vault, secure enclave)
- Randomness: Use SecureRandom, not Random, for cryptographic operations
- Timing Attacks: Use constant-time comparison for sensitive data
- Side Channels: Cache timing, power analysis, electromagnetic leakage
- Protocol Weaknesses: Even strong algorithms can be weak in bad protocols
- Forward Secrecy: Use ephemeral keys (DHE, ECDHE) in TLS
- Quantum Threats: RSA and ECC vulnerable to quantum computers (Shor's algorithm)
- Post-Quantum: NIST is standardizing PQC (CRYSTALS-Kyber, Dilithium)
- Algorithm Agility: Design systems that can switch algorithms
"@

wf "ARCHITECTURE.md" @"
# Architecture — Cryptography

## Java Cryptography Architecture (JCA)
```java
// Provider architecture: SunJCE, Bouncy Castle, etc.
Security.addProvider(new BouncyCastleProvider());
MessageDigest md = MessageDigest.getInstance("SHA-256", "BC");
```

## Real-World Applications
- TLS/SSL: HTTPS (OpenSSL, Java SSLSocket)
- SSH: Secure shell (OpenSSH)
- PGP/GPG: Email encryption
- Blockchain: SHA-256 (Bitcoin), Keccak-256 (Ethereum)
- Password Managers: Master password → derived key → encrypted vault
- File Encryption: AES-256-GCM for file-level encryption
- JWT: JSON Web Tokens (signed with HMAC or RSA/ECDSA)
- Digital Certificates: X.509 certificates (used in TLS)
"@

wf "EXERCISES.md" @"
# Exercises — Cryptography

## Beginner
1. Implement SHA-256 hasher
2. Compute HMAC-SHA256 of a message
3. Generate RSA key pair (2048-bit)
4. Encrypt/decrypt a string with AES-GCM

## Intermediate
5. Implement password hashing with bcrypt + salt
6. Create digital signature for a file (sign + verify)
7. Implement consistent hashing for distributed cache
8. Implement Diffie-Hellman key exchange

## Advanced
9. Implement a simple blockchain (hash chain)
10. Implement Merkle tree for data verification
11. Implement secure file encryption utility
12. Implement JWT creation and verification
"@

wf "QUIZ.md" @"
# Quiz — Cryptography

1. What three properties must a cryptographic hash have?
2. What is the difference between symmetric and asymmetric encryption?
3. Why is ECB mode insecure for AES?
4. What problem does RSA rely on for security?
5. Why must passwords be salted before hashing?
6. What is forward secrecy in TLS?
7. How do digital signatures provide non-repudiation?
"@

wf "FLASHCARDS.md" @"
# Flashcards

- Q: Hash properties? → A: Preimage, 2nd preimage, collision resistance
- Q: AES key sizes? → A: 128, 192, 256 bits
- Q: RSA based on? → A: Integer factorization
- Q: Digital signature algorithm? → A: DSA, ECDSA, RSA-PSS
- Q: bcrypt purpose? → A: Password hashing (deliberately slow)
- Q: TLS purpose? → A: Secure communication over internet
- Q: ECC advantage? → A: Smaller keys than RSA for same security
"@

wf "INTERVIEW.md" @"
# Interview Questions

1. "How would you store passwords securely?" — Salt + bcrypt/argon2
2. "Explain how TLS handshake works." — Certificate verification, key exchange
3. "What is the difference between hashing and encryption?" — One-way vs two-way
4. "Design a secure file sharing system." — End-to-end encryption
5. "How do digital signatures work?" — Hash + encrypt with private key
6. "Explain the concept of forward secrecy." — Ephemeral key exchange
7. "What is a timing attack and how do you prevent it?" — Constant-time operations
"@

wf "REFLECTION.md" @"
# Reflection

- Why is it dangerous to implement your own cryptographic algorithms?
- How does the security margin of an algorithm affect its adoption?
- What are the trade-offs between RSA and ECC?
- Why does password hashing need to be deliberately slow?
- How will quantum computing change cryptography?
- What is the role of cryptographic agility in system design?
"@

wf "REFERENCES.md" @"
# References

- Ferguson, Schneier & Kohno "Cryptography Engineering"
- Stallings, W. "Cryptography and Network Security"
- JCA Reference Guide (Oracle)
- NIST SP 800-175B (Guideline for Cryptographic Algorithms)
- Boneh & Shoup "A Graduate Course in Applied Cryptography"
- Bouncy Castle Java Library Documentation
"@

Write-Host "15-cryptographic-algorithms: All 24 files created"
