# Module 18: Security & Cryptography - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is a Salt, and why is it necessary for password hashing?
**Answer**:
A salt is a cryptographically strong random string of bytes added to a password before it is hashed.
Without a salt, two users with the same password (e.g., "password123") will have the exact same hash in the database. Attackers use precomputed hash tables (Rainbow Tables) to reverse-engineer common passwords instantly. By adding a unique, random salt to each user's password, the resulting hashes are unique, rendering Rainbow Tables useless.

### Q2: Why is symmetric encryption faster than asymmetric encryption?
**Answer**:
Symmetric encryption (like AES) uses simple mathematical operations (substitution, permutation, XOR) and a single, shared key, which requires very little CPU processing.
Asymmetric encryption (like RSA) relies on complex mathematical problems involving huge prime numbers and modular exponentiation using two mathematically linked keys. The math required to compute these pairs and encrypt/decrypt data is computationally intensive and very slow, which is why asymmetric encryption is typically only used to encrypt a small symmetric key, establishing a secure channel to then switch to symmetric encryption (e.g., the TLS handshake).

### Q3: What is the purpose of an Initialization Vector (IV)?
**Answer**:
In block cipher modes like CBC (Cipher Block Chaining), an Initialization Vector (IV) ensures that identical plaintexts encrypt to different ciphertexts. It acts similarly to a salt for encryption. The IV must be random and unique for each encryption operation, but it does not need to be secret; it is typically sent in plaintext alongside the ciphertext.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Spot the Vulnerability
**Problem**: The following Java method is used to verify a user's password. Identify the critical security flaw and explain how to fix it.

```java
public boolean verifyPassword(String inputPassword, String storedHash) {
    try {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(inputPassword.getBytes());
        String inputHash = Base64.getEncoder().encodeToString(hashBytes);
        
        return inputHash.equals(storedHash);
    } catch (Exception e) {
        return false;
    }
}
```

**Solution / Explanations**:
1. **Broken Algorithm**: MD5 is cryptographically broken and vulnerable to rapid collision attacks. It should never be used for passwords. (Fix: Use BCrypt, Argon2, or PBKDF2).
2. **Missing Salt**: The password is being hashed directly without a salt. It is vulnerable to Rainbow Table attacks.
3. **Timing Attack Vulnerability**: `String.equals()` compares strings character-by-character and returns `false` as soon as a mismatch is found. An attacker can measure the time it takes the method to return to guess the hash character-by-character. (Fix: Use `MessageDigest.isEqual()` which executes a constant-time byte array comparison).