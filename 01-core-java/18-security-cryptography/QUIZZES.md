# Module 18: Security & Cryptography - Quizzes

---

## Q1: Symmetric vs Asymmetric Encryption
What is the main difference between symmetric and asymmetric encryption?

A) Symmetric encryption is faster but uses different keys.
B) Asymmetric encryption uses the same key for both encryption and decryption.
C) Symmetric uses the same key for encryption/decryption, while asymmetric uses a public/private key pair.
D) Asymmetric is only used for hashing.

**Answer**: C
**Explanation**: Symmetric cryptography relies on a shared secret key, while asymmetric uses mathematically linked public and private keys.

---

## Q2: Secure Hashing
Which of the following is considered a secure algorithm for hashing passwords in modern applications?

A) MD5
B) SHA-1
C) SHA-256 (preferably with a salt) or Argon2 / bcrypt
D) DES

**Answer**: C
**Explanation**: MD5 and SHA-1 are cryptographically broken. SHA-256 with a salt, or specialized password hashing algorithms like bcrypt or Argon2, are recommended.

---

## Q3: Digital Signatures
What is the primary purpose of a Digital Signature?

A) To encrypt large amounts of data securely.
B) To ensure data authenticity and non-repudiation.
C) To compress data for faster network transmission.
D) To hide the sender's identity.

**Answer**: B
**Explanation**: A digital signature is created using the sender's private key, proving the data originated from them (authenticity) and preventing them from denying it (non-repudiation).