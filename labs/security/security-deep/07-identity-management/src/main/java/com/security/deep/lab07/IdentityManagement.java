package com.security.deep.lab07;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IdentityManagement {

    public static class LdapDirectory {
        private final Map<String, LdapEntry> entries = new ConcurrentHashMap<>();

        public record LdapEntry(String dn, String cn, String uid, String mail, String passwordHash) {}

        public void addEntry(String dn, String cn, String uid, String mail, String password) {
            entries.put(dn, new LdapEntry(dn, cn, uid, mail, sha256(password)));
        }

        public boolean bind(String dn, String password) {
            LdapEntry entry = entries.get(dn);
            if (entry == null) return false;
            return entry.passwordHash().equals(sha256(password));
        }

        public List<LdapEntry> search(String baseDn, String filter) {
            List<LdapEntry> results = new ArrayList<>();
            String searchTerm = filter.replaceAll(".*=", "").replaceAll("[()]", "");
            for (LdapEntry entry : entries.values()) {
                if (entry.dn().contains(baseDn) && entry.uid().contains(searchTerm)) {
                    results.add(entry);
                }
            }
            return results;
        }

        private String sha256(String input) {
            try {
                var md = java.security.MessageDigest.getInstance("SHA-256");
                return Base64.getEncoder().encodeToString(md.digest(input.getBytes()));
            } catch (Exception e) { throw new RuntimeException(e); }
        }
    }

    public static String createSamlAssertion(String subject, String issuer, String audience,
                                               int expirationMinutes) {
        String assertionId = "_" + UUID.randomUUID();
        long now = System.currentTimeMillis();
        long notBefore = now - 60000;
        long notOnOrAfter = now + expirationMinutes * 60000L;

        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <saml2:Assertion xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion"
                ID="%s" IssueInstant="%d">
              <saml2:Issuer>%s</saml2:Issuer>
              <saml2:Subject>
                <saml2:NameID>%s</saml2:NameID>
                <saml2:SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:bearer">
                  <saml2:SubjectConfirmationData NotOnOrAfter="%d" Audience="%s"/>
                </saml2:SubjectConfirmation>
              </saml2:Subject>
              <saml2:Conditions NotBefore="%d" NotOnOrAfter="%d">
                <saml2:AudienceRestriction>
                  <saml2:Audience>%s</saml2:Audience>
                </saml2:AudienceRestriction>
              </saml2:Conditions>
              <saml2:AuthnStatement AuthnInstant="%d">
                <saml2:AuthnContext>
                  <saml2:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Password</saml2:AuthnContextClassRef>
                </saml2:AuthnContext>
              </saml2:AuthnStatement>
            </saml2:Assertion>
            """.formatted(assertionId, now, issuer, subject, notOnOrAfter, audience, notBefore, notOnOrAfter, audience, now);
    }

    public static class ScimUserManager {
        private final Map<String, Map<String, Object>> users = new ConcurrentHashMap<>();

        public Map<String, Object> createUser(Map<String, Object> userData) {
            String id = UUID.randomUUID().toString();
            Map<String, Object> user = new LinkedHashMap<>(userData);
            user.put("id", id);
            user.put("meta", Map.of("resourceType", "User", "created", System.currentTimeMillis()));
            users.put(id, user);
            return user;
        }

        public Map<String, Object> getUser(String id) {
            return users.get(id);
        }

        public Map<String, Object> listUsers(int startIndex, int count) {
            List<Map<String, Object>> items = new ArrayList<>(users.values());
            int from = Math.min(startIndex - 1, items.size());
            int to = Math.min(from + count, items.size());
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("totalResults", items.size());
            result.put("startIndex", startIndex);
            result.put("itemsPerPage", count);
            result.put("Resources", items.subList(from, to));
            return result;
        }

        public Map<String, Object> patchUser(String id, Map<String, Object> updates) {
            Map<String, Object> user = users.get(id);
            if (user == null) return null;
            user.putAll(updates);
            return user;
        }

        public void deleteUser(String id) { users.remove(id); }
    }

    public static class TotpGenerator {
        public static String generateSecret() {
            SecureRandom sr = new SecureRandom();
            byte[] bytes = new byte[20];
            sr.nextBytes(bytes);
            return Base32.encode(bytes);
        }

        public static String generateCode(String secret, long timeStepSeconds) throws Exception {
            long counter = System.currentTimeMillis() / (timeStepSeconds * 1000);
            byte[] key = Base32.decode(secret);
            byte[] counterBytes = ByteBuffer.allocate(8).putLong(counter).array();
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(counterBytes);
            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24)
                       | ((hash[offset + 1] & 0xFF) << 16)
                       | ((hash[offset + 2] & 0xFF) << 8)
                       | (hash[offset + 3] & 0xFF);
            int otp = binary % 1000000;
            return String.format("%06d", otp);
        }

        public static boolean validateCode(String secret, String code, long timeStepSeconds) throws Exception {
            String expected = generateCode(secret, timeStepSeconds);
            return expected.equals(code);
        }
    }

    static class Base32 {
        private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        public static String encode(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i += 5) {
                int buf = 0;
                int bits = 0;
                for (int j = i; j < i + 5 && j < bytes.length; j++) {
                    buf = (buf << 8) | (bytes[j] & 0xFF);
                    bits += 8;
                }
                while (bits > 0) {
                    sb.append(ALPHABET.charAt((buf >> (bits - 5)) & 0x1F));
                    bits -= 5;
                }
            }
            return sb.toString();
        }
        public static byte[] decode(String s) {
            byte[] bytes = new byte[s.length() * 5 / 8];
            int pos = 0;
            for (int i = 0; i < s.length(); i += 8) {
                int buf = 0;
                int bits = 0;
                for (int j = i; j < i + 8 && j < s.length(); j++) {
                    buf = (buf << 5) | ALPHABET.indexOf(s.charAt(j));
                    bits += 5;
                }
                while (bits >= 8) {
                    bytes[pos++] = (byte) ((buf >> (bits - 8)) & 0xFF);
                    bits -= 8;
                }
            }
            return bytes;
        }
    }
}
