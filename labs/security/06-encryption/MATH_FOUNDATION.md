# Math Foundation for 06-encryption

## Cryptographic Primitives

### Hash Functions

Hash functions are one-way mathematical functions essential for password storage:

```
H(x) = y
- Given x, computing y is easy
- Given y, finding x is computationally infeasible
- Small change in x produces completely different y
```

**Properties:**
- **Preimage resistance**: Given y, cannot find x such that H(x) = y
- **Second preimage resistance**: Given x, cannot find x' != x with H(x) = H(x')
- **Collision resistance**: Cannot find any x1 != x2 with H(x1) = H(x2)

### Entropy and Password Strength

Password strength is measured in bits of entropy:

```
E = log2(R^L)
Where:
- E = entropy in bits
- R = size of character set
- L = length of password
```

### Encryption Mathematics

#### Symmetric Encryption (AES)

AES operates on a 4x4 column-major order matrix of bytes. Each round applies:
SubBytes -> ShiftRows -> MixColumns -> AddRoundKey

#### Asymmetric Encryption (RSA)

RSA relies on the difficulty of factoring the product of two large prime numbers:

```
Key generation:
1. Choose p, q (large primes)
2. Compute n = p * q
3. Compute phi(n) = (p-1)(q-1)
4. Choose e such that 1 < e < phi(n) and gcd(e, phi(n)) = 1
5. Compute d = e^(-1) (mod phi(n))

Public key: (e, n)
Private key: (d, n)

Encryption: c = m^e (mod n)
Decryption: m = c^d (mod n)
```

## Java Cryptography Architecture (JCA)

Java provides cryptographic operations through JCA:

```java
// MessageDigest (hashing)
MessageDigest md = MessageDigest.getInstance("SHA-256");
byte[] hash = md.digest(input.getBytes());

// Key generation (AES)
KeyGenerator kg = KeyGenerator.getInstance("AES");
kg.init(256);
SecretKey key = kg.generateKey();

// Cipher (encryption/decryption)
Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
cipher.init(Cipher.ENCRYPT_MODE, key);
byte[] ciphertext = cipher.doFinal(plaintext);
```
