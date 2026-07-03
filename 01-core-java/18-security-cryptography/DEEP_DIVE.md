# Module 18: Security & Cryptography - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-17  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Java Cryptography Architecture (JCA)](#jca)
2. [Hashing and Message Digests](#hashing)
3. [Symmetric Encryption](#symmetric)
4. [Asymmetric Encryption](#asymmetric)
5. [Digital Signatures](#signatures)

---

## 1. Java Cryptography Architecture (JCA) <a name="jca"></a>
JCA provides a framework and implementation for encryption, key generation and agreement, and Message Authentication Code (MAC) algorithms. It uses a provider-based architecture.

---

## 2. Hashing and Message Digests <a name="hashing"></a>
Hashing turns data into a fixed-size string of bytes. It is a one-way function used for verifying data integrity or storing passwords securely.

```java
import java.security.MessageDigest;

public class HashingExample {
    public static byte[] hash(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data.getBytes());
    }
}
```

---

## 3. Symmetric Encryption <a name="symmetric"></a>
Uses the same key for encryption and decryption. AES (Advanced Encryption Standard) is the standard algorithm.

```java
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SymmetricExample {
    public static void encrypt() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();
        
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal("Hello World".getBytes());
    }
}
```

---

## 4. Asymmetric Encryption <a name="asymmetric"></a>
Uses a public key for encryption and a private key for decryption. RSA is widely used.

```java
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import javax.crypto.Cipher;

public class AsymmetricExample {
    public static void rsaEncrypt() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair pair = keyPairGen.generateKeyPair();
        
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pair.getPublic());
        byte[] encrypted = cipher.doFinal("Secret Message".getBytes());
    }
}
```

---

## 5. Digital Signatures <a name="signatures"></a>
Signatures ensure authenticity and non-repudiation by encrypting a hash with a private key.

```java
import java.security.Signature;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class SignatureExample {
    public static byte[] sign(byte[] data) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        KeyPair kp = kpg.generateKeyPair();
        
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(kp.getPrivate());
        sig.update(data);
        return sig.sign();
    }
}
```