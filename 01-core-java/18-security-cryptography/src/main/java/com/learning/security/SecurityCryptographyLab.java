package com.learning.security;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.util.*;

public class SecurityCryptographyLab {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Security & Cryptography Lab ===\n");

        System.out.println("1. Hashing (SHA-256):");
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest("Hello World".getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        System.out.println("   SHA-256: " + sb);

        System.out.println("\n2. Base64 Encoding:");
        String original = "Java Security Lab";
        String encoded = Base64.getEncoder().encodeToString(original.getBytes());
        String decoded = new String(Base64.getDecoder().decode(encoded));
        System.out.println("   Original: " + original);
        System.out.println("   Encoded: " + encoded);
        System.out.println("   Decoded: " + decoded);

        System.out.println("\n3. Symmetric Encryption (AES):");
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey aesKey = keyGen.generateKey();
        System.out.println("   AES Key: " + Base64.getEncoder().encodeToString(aesKey.getEncoded()));

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] iv = cipher.getIV();
        byte[] encrypted = cipher.doFinal("Secret Data".getBytes());
        System.out.println("   Encrypted: " + Base64.getEncoder().encodeToString(encrypted));

        cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(128, iv));
        byte[] decrypted = cipher.doFinal(encrypted);
        System.out.println("   Decrypted: " + new String(decrypted));

        System.out.println("\n4. Asymmetric Encryption (RSA):");
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        System.out.println("   Public Key: " + Base64.getEncoder().encodeToString(kp.getPublic().getEncoded()).substring(0, 40) + "...");
        System.out.println("   Private Key: " + Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded()).substring(0, 40) + "...");

        cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, kp.getPublic());
        byte[] rsaEncrypted = cipher.doFinal("RSA works!".getBytes());
        System.out.println("   RSA Encrypted: " + Base64.getEncoder().encodeToString(rsaEncrypted).substring(0, 40) + "...");

        cipher.init(Cipher.DECRYPT_MODE, kp.getPrivate());
        byte[] rsaDecrypted = cipher.doFinal(rsaEncrypted);
        System.out.println("   RSA Decrypted: " + new String(rsaDecrypted));

        System.out.println("\n5. Digital Signatures:");
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(kp.getPrivate());
        sign.update("Important document".getBytes());
        byte[] signature = sign.sign();
        System.out.println("   Signature: " + Base64.getEncoder().encodeToString(signature).substring(0, 40) + "...");

        sign.initVerify(kp.getPublic());
        sign.update("Important document".getBytes());
        boolean verified = sign.verify(signature);
        System.out.println("   Verified: " + verified);

        System.out.println("\n6. KeyStore Example:");
        System.out.println("   KeyStore keyStore = KeyStore.getInstance(\"PKCS12\");");
        System.out.println("   keyStore.load(null, null);");
        System.out.println("   keyStore.setKeyEntry(\"mykey\", privateKey, password, certChain);");
        System.out.println("   keyStore.store(new FileOutputStream(\"keystore.p12\"), password);");

        System.out.println("\n7. SSL/TLS Context:");
        System.out.println("   SSLContext sslContext = SSLContext.getInstance(\"TLSv1.3\");");
        System.out.println("   sslContext.init(keyManagers, trustManagers, secureRandom);");
        System.out.println("   SSLServerSocketFactory factory = sslContext.getServerSocketFactory();");

        System.out.println("\n8. SecureRandom:");
        SecureRandom sr = SecureRandom.getInstanceStrong();
        byte[] randomBytes = new byte[16];
        sr.nextBytes(randomBytes);
        System.out.println("   Random bytes: " + Base64.getEncoder().encodeToString(randomBytes));

        System.out.println("\n9. Security Best Practices:");
        System.out.println("   - Use TLS 1.3 for transport security");
        System.out.println("   - Store passwords with bcrypt/argon2/scrypt");
        System.out.println("   - Use authenticated encryption (AES-GCM, not ECB)");
        System.out.println("   - Keep dependencies updated (OWASP Dependency-Check)");
        System.out.println("   - Follow OWASP Top 10 guidelines");

        System.out.println("\n=== Security & Cryptography Lab Complete ===");
    }
}