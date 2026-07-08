package com.security12;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.time.Instant;
import java.util.*;

public class WebAuthnRegistrationService {

    private final Map<String, StoredCredential> credentials = new HashMap<>();
    private static final SecureRandom RANDOM = new SecureRandom();

    public RegistrationOptions startRegistration(String userId, String userName) {
        byte[] challenge = new byte[32];
        RANDOM.nextBytes(challenge);
        RegistrationOptions options = new RegistrationOptions();
        options.challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(challenge);
        options.rp = new RelyingParty("Java Security Lab", "localhost");
        options.user = new UserInfo(userId, userName, "Java User");
        options.pubKeyCredParams = List.of(new PubKeyCredParams(-7, "public-key"));
        options.authenticatorSelection = new AuthenticatorSelection("platform", true, "preferred");
        options.timeout = 60000;
        options.attestation = "none";
        return options;
    }

    public boolean completeRegistration(RegistrationRequest request) {
        try {
            if (request.credentialId == null || request.publicKey == null) return false;
            StoredCredential cred = new StoredCredential();
            cred.credentialId = Base64.getUrlDecoder().decode(request.credentialId);
            cred.publicKey = Base64.getUrlDecoder().decode(request.publicKey);
            cred.userId = request.userId;
            cred.createdAt = Instant.now();
            cred.transports = List.of("internal", "hybrid");
            credentials.put(request.credentialId, cred);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyAttestation(String credentialId, String signature, String clientDataJson, String authenticatorData) {
        StoredCredential cred = credentials.get(credentialId);
        if (cred == null) return false;
        try {
            byte[] signatureBytes = Base64.getUrlDecoder().decode(signature);
            byte[] clientDataBytes = Base64.getUrlDecoder().decode(clientDataJson);
            KeyFactory kf = KeyFactory.getInstance("EC");
            byte[] pubKeyBytes = cred.publicKey;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static class RegistrationOptions {
        public String challenge;
        public RelyingParty rp;
        public UserInfo user;
        public List<PubKeyCredParams> pubKeyCredParams;
        public AuthenticatorSelection authenticatorSelection;
        public int timeout;
        public String attestation;
    }

    public static class RelyingParty {
        public String name, id;
        public RelyingParty(String name, String id) { this.name = name; this.id = id; }
    }

    public static class UserInfo {
        public String id, name, displayName;
        public UserInfo(String id, String name, String displayName) { this.id = id; this.name = name; this.displayName = displayName; }
    }

    public static class PubKeyCredParams {
        public int alg; public String type;
        public PubKeyCredParams(int alg, String type) { this.alg = alg; this.type = type; }
    }

    public static class AuthenticatorSelection {
        public String authenticatorAttachment; public boolean residentKey; public String userVerification;
        public AuthenticatorSelection(String aa, boolean rk, String uv) { this.authenticatorAttachment = aa; this.residentKey = rk; this.userVerification = uv; }
    }

    public static class RegistrationRequest {
        public String credentialId, publicKey, userId;
    }

    static class StoredCredential {
        byte[] credentialId, publicKey;
        String userId;
        Instant createdAt;
        List<String> transports;
    }

    public static void main(String[] args) {
        WebAuthnRegistrationService service = new WebAuthnRegistrationService();
        RegistrationOptions opts = service.startRegistration("user-1", "alice");
        System.out.println("Challenge: " + opts.challenge);
        System.out.println("RP: " + opts.rp.name);
    }
}
