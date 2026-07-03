# Module 18: Security & Cryptography - Edge Cases & Pitfalls

---

## Pitfall 1: Hardcoding Secrets

### ❌ Wrong
Storing encryption keys, passwords, or salts directly in source code.
```java
// Terrible security practice
private static final String SECRET_KEY = "my_super_secret_key";
```

### ✅ Correct
Use environment variables, secure key management systems (KMS), or Java Keystores.

---

## Pitfall 2: Using Weak Algorithms

### ❌ Wrong
Using deprecated or broken algorithms like MD5 or DES.
```java
MessageDigest md = MessageDigest.getInstance("MD5"); // Broken, prone to collision attacks
```

### ✅ Correct
Use industry-standard, strong algorithms like SHA-256 (or higher) for hashing and AES-256 for symmetric encryption.
```java
MessageDigest md = MessageDigest.getInstance("SHA-256");
```

---

## Pitfall 3: Not Using IVs or Salts

### ❌ Wrong
Encrypting the same plaintext multiple times yielding the same ciphertext (e.g., AES in ECB mode), or hashing passwords without salts.

### ✅ Correct
Always use an Initialization Vector (IV) for symmetric encryption (e.g., AES/CBC/PKCS5Padding or AES/GCM) and unique salts when hashing passwords to prevent rainbow table attacks.