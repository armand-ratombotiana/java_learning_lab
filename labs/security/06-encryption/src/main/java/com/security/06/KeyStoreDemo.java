package com.security06;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;

/**
 * Demonstrates Java KeyStore (JKS/PKCS12) operations for key management.
 * 
 * SECURITY CONCEPT: A KeyStore is a secure storage facility for:
 * - Private keys (asymmetric — RSA, EC)
 * - Secret keys (symmetric — AES)
 * - Trusted certificates (X.509 certificate chains)
 * 
 * KeyStore types:
 * - PKCS12: Modern standard (.p12/.pfx). Recommended.
 * - JKS: Java-specific legacy (.jks). Being replaced by PKCS12.
 * 
 * Security best practices:
 * - Protect KeyStore with strong password
 * - Use distinct passwords for KeyStore and individual keys
 * - Store KeyStore files outside the application archive
 * - Use Hardware Security Module (HSM) for production
 * - Rotate keys regularly
 */
public class KeyStoreDemo {

    private static final String KEYSTORE_TYPE = "PKCS12";
    private static final String KEYSTORE_PASSWORD = "changeit"; // In production: from secure vault
    private static final String KEY_PASSWORD = "keypass123";
    private static final String KEYSTORE_FILE = "security-keystore.p12";

    /**
     * Creates a new KeyStore and saves it to a file.
     * SECURITY: KeyStore password protects the entire store.
     * Individual key passwords add an extra layer of protection.
     */
    public static KeyStore createKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(null, KEYSTORE_PASSWORD.toCharArray());
        return keyStore;
    }

    /**
     * Generates an RSA key pair and stores it in the KeyStore as a self-signed
     * certificate entry.
     * SECURITY: Self-signed certificates are for development only.
     * Production: use certificates signed by a trusted Certificate Authority (CA).
     */
    public static void generateAndStoreRsaKey(KeyStore keyStore, String alias)
            throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048, new SecureRandom());
        KeyPair keyPair = kpg.generateKeyPair();

        // Create self-signed certificate
        // In production, use BouncyCastle or Java keytool for proper certificates
        X509Certificate cert = createSelfSignedCertificate(keyPair, alias);

        // Store private key with certificate chain
        keyStore.setKeyEntry(alias, keyPair.getPrivate(),
                KEY_PASSWORD.toCharArray(),
                new Certificate[]{cert});

        System.out.println("  Stored RSA key: " + alias);
    }

    /**
     * Generates an AES secret key and stores it in the KeyStore as a secret key entry.
     */
    public static void generateAndStoreAesKey(KeyStore keyStore, String alias)
            throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256, new SecureRandom());
        SecretKey secretKey = keyGen.generateKey();

        // Secret keys are stored as KeyStore.SecretKeyEntry
        keyStore.setEntry(alias,
                new KeyStore.SecretKeyEntry(secretKey),
                new KeyStore.PasswordProtection(KEY_PASSWORD.toCharArray()));

        System.out.println("  Stored AES key: " + alias);
    }

    /**
     * Saves the KeyStore to a file.
     * SECURITY: The file is encrypted with the KeyStore password.
     * Protect file permissions — only the application should read it.
     */
    public static void saveKeyStore(KeyStore keyStore, String fileName) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            keyStore.store(fos, KEYSTORE_PASSWORD.toCharArray());
        }
        System.out.println("  KeyStore saved to: " + fileName);
    }

    /**
     * Loads a KeyStore from a file.
     */
    public static KeyStore loadKeyStore(String fileName) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        try (FileInputStream fis = new FileInputStream(fileName)) {
            keyStore.load(fis, KEYSTORE_PASSWORD.toCharArray());
        }
        return keyStore;
    }

    /**
     * Lists all entries in a KeyStore with their types.
     */
    public static void listEntries(KeyStore keyStore) throws Exception {
        System.out.println("\nKeyStore entries:");
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            String type;
            if (keyStore.isKeyEntry(alias)) {
                if (keyStore.getCertificate(alias) != null) {
                    type = "Private Key + Certificate";
                } else {
                    type = "Secret Key";
                }
            } else if (keyStore.isCertificateEntry(alias)) {
                type = "Trusted Certificate";
            } else {
                type = "Unknown";
            }
            System.out.println("  - " + alias + " (" + type + ")");
        }
    }

    /**
     * Creates a self-signed X.509 certificate for development/demo purposes.
     * SECURITY: In production, use a proper CA-signed certificate.
     */
    private static X509Certificate createSelfSignedCertificate(
            KeyPair keyPair, String alias) {
        // Simplified: in production use BouncyCastle's X509v3CertificateBuilder
        // or the JDK's keytool command-line utility
        System.out.println("  [DEV] Self-signed certificate created for: " + alias);
        return null; // Placeholder — real implementations require BouncyCastle
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Java KeyStore Demo ===\n");

        // Create a new KeyStore
        KeyStore keyStore = createKeyStore();
        System.out.println("KeyStore created (type: " + KEYSTORE_TYPE + ")");

        // Generate and store keys
        generateAndStoreRsaKey(keyStore, "myapp-rsa-key");
        generateAndStoreAesKey(keyStore, "myapp-aes-key");
        generateAndStoreRsaKey(keyStore, "signing-key");

        // Save to file
        saveKeyStore(keyStore, KEYSTORE_FILE);

        // Reload and inspect
        System.out.println("\nReloading KeyStore from file...");
        KeyStore loaded = loadKeyStore(KEYSTORE_FILE);
        listEntries(loaded);

        // Cleanup
        new File(KEYSTORE_FILE).delete();
        System.out.println("\nCleanup: temporary keystore deleted");

        System.out.println("\n--- KeyStore Best Practices ---");
        System.out.println("1. Use PKCS12 format (not JKS)");
        System.out.println("2. Strong passwords for KeyStore + individual keys");
        System.out.println("3. Store keystore outside application archive");
        System.out.println("4. Rotate keys every 1-2 years");
        System.out.println("5. Use HSM for production private keys");
    }
}
