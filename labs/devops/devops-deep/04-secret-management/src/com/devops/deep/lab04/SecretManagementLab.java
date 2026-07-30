package com.devops.deep.lab04;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SecretManagementLab {
    public static void main(String[] args) {
        var vault = new VaultServer();
        vault.enableSecretsEngine("kv-v2", "secret/");

        vault.writeSecret("secret/db/password", Map.of("password", "s3cur3!Pass"));
        var dbPassword = vault.readSecret("secret/db/password");
        System.out.println("Vault secret: " + dbPassword);

        var dynamicCreds = vault.generateDynamicCreds("database/creds/app", "db-app");
        System.out.println("Dynamic creds: user=" + dynamicCreds.get("username") + " pass=" + dynamicCreds.get("password"));

        var eso = new ExternalSecretsOperator(vault);
        eso.createExternalSecret("db-cred", "vault-kv", "secret/db/password", "db-password");

        var k8sSecret = eso.sync("db-cred");
        System.out.println("K8s Secret synced: " + k8sSecret);

        var tls = new TransportEncryption();
        tls.enableTLS("1.3", "TLS_AES_256_GCM_SHA384");

        var encryptor = new AtRestEncryption();
        var encrypted = encryptor.encrypt("s3cur3!Pass");
        var decrypted = encryptor.decrypt(encrypted);
        System.out.println("At-rest encrypt/decrypt: " + (Objects.equals("s3cur3!Pass", decrypted) ? "OK" : "FAIL"));
    }
}

class VaultServer {
    private final Map<String, Map<String, Object>> kvStore = new ConcurrentHashMap<>();
    private final List<String> dynamicCredentials = new CopyOnWriteArrayList<>();

    void enableSecretsEngine(String engine, String path) {
        System.out.println("Vault: enabled " + engine + " at " + path);
    }

    void writeSecret(String path, Map<String, Object> data) {
        kvStore.put(path, data);
    }

    Map<String, Object> readSecret(String path) {
        return kvStore.getOrDefault(path, Map.of());
    }

    Map<String, String> generateDynamicCreds(String role, String name) {
        var creds = new HashMap<String, String>();
        creds.put("username", "vault-" + name + "-" + Instant.now().getEpochSecond());
        creds.put("password", UUID.randomUUID().toString());
        creds.put("lease_duration", "3600s");
        dynamicCredentials.add(creds.get("username"));
        return creds;
    }
}

class ExternalSecretsOperator {
    private final VaultServer vault;
    private final Map<String, ExternalSecretDef> secrets = new ConcurrentHashMap<>();
    private final Map<String, String> syncedSecrets = new ConcurrentHashMap<>();

    ExternalSecretsOperator(VaultServer vault) { this.vault = vault; }

    void createExternalSecret(String name, String store, String vaultPath, String k8sKey) {
        secrets.put(name, new ExternalSecretDef(name, store, vaultPath, k8sKey));
    }

    String sync(String name) {
        var def = secrets.get(name);
        if (def == null) throw new IllegalArgumentException("ExternalSecret not found: " + name);
        var vaultData = vault.readSecret(def.vaultPath());
        var value = vaultData.getOrDefault(def.k8sKey(), "unknown").toString();
        syncedSecrets.put(name, value);
        return value;
    }
}

record ExternalSecretDef(String name, String store, String vaultPath, String k8sKey) {}

class TransportEncryption {
    void enableTLS(String version, String cipherSuite) {
        System.out.println("TLS " + version + " enabled with " + cipherSuite);
    }
}

class AtRestEncryption {
    private final byte[] key = "ThisIsASecretKey1234567890ABCDEF".getBytes(); // In practice: key management service

    byte[] encrypt(String plaintext) {
        var data = plaintext.getBytes();
        var result = new byte[data.length];
        for (int i = 0; i < data.length; i++) result[i] = (byte) (data[i] ^ key[i % key.length]);
        return result;
    }

    String decrypt(byte[] ciphertext) {
        var result = new byte[ciphertext.length];
        for (int i = 0; i < ciphertext.length; i++) result[i] = (byte) (ciphertext[i] ^ key[i % key.length]);
        return new String(result);
    }
}
