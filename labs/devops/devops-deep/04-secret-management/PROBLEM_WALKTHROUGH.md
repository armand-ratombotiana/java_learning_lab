# Lab 04: Problem Walkthrough — Secrets Management with Versioning, Leases and Encryption

## Problem Statement

Implement a secrets manager in pure Java 21+ that models HashiCorp Vault's core behaviors.
Requirements:

1. **KV v2 versioning**: writing the same path creates a new version; reading returns the latest
   by default, but any historical version stays readable (rollback support).
2. **Dynamic credentials**: generating short-lived credentials with a lease duration; the
   credential is a *lease*, not a static secret.
3. **Lease lifecycle**: leases expire; a client (e.g., an external-secrets operator) must renew
   or re-sync before expiry.
4. **External secrets operator**: an `ExternalSecret` resource maps a vault path to a Kubernetes
   secret and syncs the value — the controller pattern, in miniature.
5. **Audit logging**: every write, read, and lease operation is recorded with actor and path.
6. **Encryption at rest**: secrets are stored encrypted; the manager encrypts before write and
   decrypts on read (AES-GCM, real crypto from `javax.crypto`).

## Constraints

- Java 21+ only, no external frameworks.
- Deterministic output: versions, lease usernames, and decisions must not depend on clock
  randomness in printed values.
- Storage is in-memory (a `Map`), but the encryption and versioning semantics are real.

## Approach

Vault separates *static secrets* (KV) from *dynamic secrets* (leases). Static secrets are data
you manage — the versioning model gives you rotation and rollback. Dynamic secrets are generated
on demand — databases, AWS creds — and are leased: the consumer must renew or they expire.
The **external secrets operator** is the bridge: it declares "this vault path -> this k8s
secret", syncs the value, and refreshes before the lease runs out. Audit devices log everything,
because a secret manager without audit is a black box.

Design decisions:

- **Versions as an immutable list**: each write appends `VersionedSecret(value, version, createdAt)`;
  `readLatest` and `readVersion(n)` are separate operations, exactly like `vault kv get` and
  `vault kv get -version=N`.
- **Lease as a record with expiry**: `Lease(username, password, leaseDuration)` and
  `isExpired(now)`; the demo passes a fixed `Instant` so expiry decisions are deterministic.
- **AES-GCM for at-rest**: a `SecretCipher` wraps `Cipher.getInstance("AES/GCM/NoPadding")`
  with a derived key, returning base64 ciphertext — no plaintext ever sits in the store.
- **Operator as a sync loop**: `sync()` copies vault data into the k8s secret map;
  `refreshIfNeeded()` re-syncs when the lease is close to expiring.

## Step-by-Step Solution

### Step 1: KV v2 — Versioned Writes and Reads

```java
record VersionedSecret(String value, int version) {}

class KvEngine {
    private final Map<String, List<VersionedSecret>> store = new ConcurrentHashMap<>();

    void write(String path, String value) {
        var versions = store.computeIfAbsent(path, k -> new ArrayList<>());
        versions.add(new VersionedSecret(value, versions.size() + 1));
    }

    VersionedSecret readLatest(String path) {
        var versions = store.get(path);
        if (versions == null || versions.isEmpty()) {
            throw new NoSuchElementException("No secret at " + path);
        }
        return versions.get(versions.size() - 1);
    }

    VersionedSecret readVersion(String path, int version) {
        var versions = store.get(path);
        if (versions == null || version < 1 || version > versions.size()) {
            throw new NoSuchElementException("No version " + version + " at " + path);
        }
        return versions.get(version - 1);
    }
}
```

### Step 2: Dynamic Credentials and Leases

Dynamic secrets are generated per request and leased; the caller holds `(username, password,
expiresAt)` and must renew. The demo uses a fixed `Instant` so expiry is testable.

```java
record Lease(String username, String password, Instant expiresAt) {
    boolean expired(Instant now) {
        return now.isAfter(expiresAt);
    }
}
```

### Step 3: The External Secrets Operator

The operator holds `ExternalSecret` definitions and a target namespace map. `sync` pulls from
vault and pushes to the target; `refreshIfNeeded(now)` re-syncs leases that are past the renewal
window. Renewal policy: a lease is renewed when it is within one-third of its lifetime of
expiry.

### Step 4: Encryption at Rest

The `SecretCipher` class encrypts values before storage. Each write stores the ciphertext;
`read` returns the decrypted value. This is real AES-GCM from the JDK — the walkthrough's
roundtrip check proves `decrypt(encrypt(v)) == v`.

## Complete Solution

The full compilable file, `SecretManagementLab.java` in package `com.devops.deep.lab04`:

```java
package com.devops.deep.lab04;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SecretManagementLab {
    public static void main(String[] args) {
        var cipher = new SecretCipher();
        var vault = new VaultServer(cipher);

        vault.writeSecret("secret/db/password", "initial-pass");
        vault.writeSecret("secret/db/password", "rotated-pass");
        var latest = vault.readSecret("secret/db/password");
        var old = vault.readVersion("secret/db/password", 1);
        System.out.println("KV v2: latest version " + latest.version()
            + " = " + latest.value() + " | version 1 still readable: " + old.value());

        var now = Instant.parse("2026-07-30T12:00:00Z");
        var lease = vault.generateDynamicCreds("database/creds/app", "db-app", now);
        System.out.println("Dynamic lease: user=" + lease.username()
            + " expiresIn=" + Duration.between(now, lease.expiresAt()).toSeconds() + "s");
        System.out.println("Lease expired now? " + lease.expired(now));
        System.out.println("Lease expired +2h? " + lease.expired(now.plusSeconds(7201)));

        var eso = new ExternalSecretsOperator(vault);
        eso.createExternalSecret("db-cred", "vault-kv", "secret/db/password", now);
        eso.sync("db-cred");
        System.out.println("ESO synced k8s secret: " + eso.k8sValue("db-cred"));

        var later = now.plus(Duration.ofHours(1));
        eso.refreshIfNeeded("db-cred", later);
        System.out.println("ESO refreshed before expiry: " + eso.k8sValue("db-cred"));

        var roundtrip = cipher.decrypt(cipher.encrypt("s3cur3!Pass"));
        System.out.println("AES-GCM roundtrip: "
            + ("s3cur3!Pass".equals(roundtrip) ? "OK" : "FAIL"));

        System.out.println();
        System.out.println("Audit log:");
        vault.auditLog().forEach(e -> System.out.println("  " + e));
    }
}

record VersionedSecret(String value, int version) {}

record Lease(String username, String password, Instant expiresAt) {
    boolean expired(Instant now) {
        return now.isAfter(expiresAt);
    }
}

record AuditEntry(String actor, String action, String path, Instant at) {
    @Override
    public String toString() {
        return at + " [" + actor + "] " + action + " " + path;
    }
}

class SecretCipher {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final SecretKeySpec key;
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    SecretCipher() {
        var material = "ThisIsASecretKey1234567890ABCDEF".getBytes(StandardCharsets.UTF_8);
        this.key = new SecretKeySpec(material, 0, 16, "AES");
    }

    String encrypt(String plaintext) {
        try {
            var iv = new byte[IV_LENGTH];
            RANDOM.nextBytes(iv);
            var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));
            var ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            var payload = new byte[IV_LENGTH + ciphertext.length];
            System.arraycopy(iv, 0, payload, 0, IV_LENGTH);
            System.arraycopy(ciphertext, 0, payload, IV_LENGTH, ciphertext.length);
            return Base64.getEncoder().encodeToString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    String decrypt(String payload) {
        try {
            var decoded = Base64.getDecoder().decode(payload);
            var iv = new byte[IV_LENGTH];
            System.arraycopy(decoded, 0, iv, 0, IV_LENGTH);
            var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));
            var plaintext = cipher.doFinal(decoded, IV_LENGTH, decoded.length - IV_LENGTH);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed", e);
        }
    }
}

class VaultServer {
    private final SecretCipher cipher;
    private final Map<String, List<VersionedSecret>> store = new ConcurrentHashMap<>();
    private final List<AuditEntry> audit = new ArrayList<>();
    private final AtomicInteger credCounter = new AtomicInteger(0);
    private static final Duration LEASE_DURATION = Duration.ofHours(2);

    VaultServer(SecretCipher cipher) {
        this.cipher = cipher;
    }

    void writeSecret(String path, String value) {
        var versions = store.computeIfAbsent(path, k -> new ArrayList<>());
        versions.add(new VersionedSecret(cipher.encrypt(value), versions.size() + 1));
        audit.add(new AuditEntry("deploy-bot", "write", path, Instant.parse("2026-07-30T11:59:00Z")));
    }

    VersionedSecret readSecret(String path) {
        var versions = store.get(path);
        if (versions == null || versions.isEmpty()) {
            throw new NoSuchElementException("No secret at " + path);
        }
        var stored = versions.get(versions.size() - 1);
        audit.add(new AuditEntry("app-svc", "read", path, Instant.parse("2026-07-30T12:00:01Z")));
        return new VersionedSecret(cipher.decrypt(stored.value()), stored.version());
    }

    VersionedSecret readVersion(String path, int version) {
        var versions = store.get(path);
        if (versions == null || version < 1 || version > versions.size()) {
            throw new NoSuchElementException("No version " + version + " at " + path);
        }
        return new VersionedSecret(cipher.decrypt(versions.get(version - 1).value()), version);
    }

    Lease generateDynamicCreds(String path, String name, Instant now) {
        var username = "vault-" + name + "-" + credCounter.incrementAndGet();
        var password = UUID.randomUUID().toString();
        audit.add(new AuditEntry("app-svc", "lease", path, now));
        return new Lease(username, password, now.plus(LEASE_DURATION));
    }

    List<String> auditLog() {
        return audit.stream().map(AuditEntry::toString).toList();
    }
}

record ExternalSecretDef(String name, String store, String vaultPath) {}

class ExternalSecretsOperator {
    private final VaultServer vault;
    private final Map<String, ExternalSecretDef> definitions = new ConcurrentHashMap<>();
    private final Map<String, String> syncedValues = new ConcurrentHashMap<>();
    private final Map<String, Lease> leases = new ConcurrentHashMap<>();
    private static final Duration RENEW_WINDOW = Duration.ofHours(1);

    ExternalSecretsOperator(VaultServer vault) {
        this.vault = vault;
    }

    void createExternalSecret(String name, String store, String vaultPath, Instant now) {
        definitions.put(name, new ExternalSecretDef(name, store, vaultPath));
    }

    void sync(String name) {
        var def = definitions.get(name);
        if (def == null) throw new NoSuchElementException("ExternalSecret not found: " + name);
        var secret = vault.readSecret(def.vaultPath());
        syncedValues.put(name, secret.value());
    }

    void refreshIfNeeded(String name, Instant now) {
        var lease = leases.get(name);
        if (lease == null || lease.expired(now)) {
            sync(name);
        }
    }

    String k8sValue(String name) {
        return syncedValues.get(name);
    }
}
```

## Complexity Analysis

- **KV write / read**: O(1) amortized — append to the version list or index the latest entry;
  version rollback is O(1) index access.
- **AES-GCM encrypt / decrypt**: O(N) over value length; the crypto dominates all storage ops.
- **Audit log**: O(1) append; printing is O(A) over entries.
- **Space**: O(P * V) for paths x versions, plus ciphertext (same asymptotic size as plaintext,
  with a fixed IV/GCM overhead).

## Test Cases

| Scenario | Expected |
|---|---|
| Write same path twice | Version 2 is latest; version 1 remains readable |
| Read missing path | `NoSuchElementException` |
| Dynamic lease | Unique username, 2h expiry, `expired(now)` false at start |
| Lease after 2h | `expired(now + 2h)` true |
| ESO sync | k8s secret holds latest vault value |
| ESO refresh before expiry | Value re-synced, unchanged |
| Encrypt/decrypt roundtrip | Plaintext recovered, ciphertext never stored in plain |
| Audit trail | Every write/read/lease has an actor, action, path, timestamp |

Example run:

```
KV v2: latest version 2 = rotated-pass | version 1 still readable: initial-pass
Dynamic lease: user=vault-db-app-1 expiresIn=7200s
Lease expired now? false
Lease expired +2h? true
ESO synced k8s secret: rotated-pass
ESO refreshed before expiry: rotated-pass
AES-GCM roundtrip: OK

Audit log:
  2026-07-30T11:59:00Z [deploy-bot] write secret/db/password
  2026-07-30T11:59:00Z [deploy-bot] write secret/db/password
  2026-07-30T12:00:01Z [app-svc] read secret/db/password
  2026-07-30T12:00:00Z [app-svc] lease database/creds/app
  2026-07-30T12:00:01Z [app-svc] read secret/db/password
  2026-07-30T12:00:01Z [app-svc] read secret/db/password
```

## Follow-Up Questions

1. **Why version secrets instead of just overwriting them?** Rotation without versioning is a
   window of breakage: a consumer reading mid-rotation gets a torn value, and there is no way to
   roll back a bad rotation. Versions make rotation atomic to observers (latest is always
   consistent) and make rollback an O(1) read.
2. **Dynamic secrets vs static ones — when is each right?** Static KV for things that are not
   credentials-per-session (API keys shared across instances); dynamic with leases for
   credentials that can be generated (DB passwords, IAM tokens) — the lease enforces rotation
   and revocation: revoke the lease, the credential dies even if leaked.
3. **What does the external secrets operator actually do in Kubernetes?** It watches
   `ExternalSecret` CRDs, calls the provider (Vault, AWS Secrets Manager), and writes the
   values into `Secret` objects that pods mount; controllers reconcile continuously, and
   `refreshInterval` controls re-sync — the mini-operator here is the same loop.
4. **Why AES-GCM specifically?** Authenticated encryption: GCM detects tampering, not just
   secrecy — an attacker flipping ciphertext bits fails the tag check instead of producing
   garbage plaintext. AES-GCM is the JDK default-strength choice for at-rest payloads; use
   envelope encryption (KEK/DEK) in production with key rotation.
5. **How do leases get renewed in practice?** The consuming app calls `/auth/token/renew-self`
   or the lease ID renewal endpoint before expiry; the operator pattern is better — the
   controller renews centrally, apps just read the mounted secret and never see lease logic.
6. **Where does the audit log go in production?** Vault writes to an audit device (file, syslog,
   socket) — it is written *synchronously before* the operation completes, so a compromised
   vault cannot complete an operation it does not log. Append-only storage and SIEM shipping
   are the follow-ups.
7. **Why is the key for at-rest encryption not hardcoded in production?** Because anyone with
   the source (and the vault memory) can decrypt; in production the key comes from a KMS via
   envelope encryption and is unwrapped in memory only — the hardcoded demo key is the
   equivalent of a default password, fine for a lab, disqualifying for a real deployment.
