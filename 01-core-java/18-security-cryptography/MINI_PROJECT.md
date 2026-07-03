# Module 18: Security & Cryptography - Mini Project

**Project Name**: Password Manager Core  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Implement secure password hashing, salting, and symmetric encryption utilizing the Java Cryptography Architecture (JCA).

## 📝 Requirements

### Core Features
1. **Password Hashing & Salting**:
   - Create a `SecureHashUtils` class.
   - Write a method `public static byte[] generateSalt()` that returns a 16-byte random salt using `SecureRandom`.
   - Write a method `public static String hashPassword(String password, byte[] salt)` that uses `MessageDigest` and the SHA-256 algorithm to securely hash the password combined with the salt. Return the result as a Hex or Base64 encoded string.

2. **Symmetric Encryption (AES)**:
   - Create a `CryptoUtils` class.
   - Write a method to generate an AES `SecretKey` (256-bit).
   - Write an `encrypt(String plainText, SecretKey key)` method that uses `Cipher.getInstance("AES/CBC/PKCS5Padding")`. Ensure you generate a random Initialization Vector (IV) and prepend it to the ciphertext.
   - Write a `decrypt(byte[] cipherTextWithIv, SecretKey key)` method that extracts the IV, initializes the Cipher in decrypt mode, and returns the plaintext string.

3. **Putting it Together**:
   - In your `main` method, simulate a user registering: Generate a salt, hash their password, and "store" the hash and salt.
   - Simulate a user storing a secret note: Encrypt a string using the AES key.
   - Simulate a user logging in: Ask for the password, hash it using the stored salt, verify it matches the stored hash, and if so, decrypt and print the secret note.

---

## 💡 Solution Blueprint

1. **Hashing Setup**:
   ```java
   public static String hashPassword(String password, byte[] salt) throws Exception {
       MessageDigest md = MessageDigest.getInstance("SHA-256");
       md.update(salt);
       byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
       return Base64.getEncoder().encodeToString(hashedPassword);
   }
   ```

2. **Encryption Setup**:
   ```java
   public static byte[] encrypt(String plaintext, SecretKey key) throws Exception {
       Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
       byte[] iv = new byte[16];
       new SecureRandom().nextBytes(iv);
       IvParameterSpec ivSpec = new IvParameterSpec(iv);
       
       cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
       byte[] encrypted = cipher.doFinal(plaintext.getBytes());
       
       // Prepend IV to the encrypted data for use in decryption
       ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encrypted.length);
       byteBuffer.put(iv);
       byteBuffer.put(encrypted);
       return byteBuffer.array();
   }
   ```