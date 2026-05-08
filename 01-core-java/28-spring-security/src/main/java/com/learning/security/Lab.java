package com.learning.security;

import java.security.*;
import java.util.*;
import java.util.concurrent.*;

public class Lab {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Spring Security Lab (Conceptual) ===\n");

        authentication();
        authorization();
        passwordHashing();
        jwtConcept();
        csrfProtection();
    }

    static void authentication() {
        System.out.println("--- Authentication ---");

        record User(String name, String pwdHash, List<String> roles) {}

        class AuthManager {
            final Map<String, User> users = new ConcurrentHashMap<>();
            void register(User u) { users.put(u.name(), u); }

            Optional<User> auth(String name, String pwd) {
                var u = users.get(name);
                if (u == null) { System.out.println("  FAILED: unknown user " + name); return Optional.empty(); }
                String hash = sha256(pwd);
                if (!u.pwdHash().equals(hash)) { System.out.println("  FAILED: wrong password for " + name); return Optional.empty(); }
                System.out.println("  SUCCESS: " + name + " authenticated");
                return Optional.of(u);
            }

            String sha256(String s) {
                try { return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(s.getBytes())); }
                catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
            }
        }

        var am = new AuthManager();
        am.register(new User("alice", am.sha256("pass123"), List.of("ROLE_USER")));
        am.register(new User("admin", am.sha256("admin123"), List.of("ROLE_ADMIN")));
        am.auth("alice", "pass123");
        am.auth("alice", "wrong");
        am.auth("bob", "pass123");

        System.out.println("\n  Providers: DaoAuthenticationProvider, JwtAuthenticationProvider, LdapAuthenticationProvider, OAuth2LoginAuthenticationProvider");
    }

    static void authorization() {
        System.out.println("\n--- Authorization ---");

        class ACL {
            final Map<String, List<String>> perms = new HashMap<>();
            void grant(String role, String perm) { perms.computeIfAbsent(role, k -> new ArrayList<>()).add(perm); }
            boolean check(List<String> roles, String perm) { return roles.stream().anyMatch(r -> perms.getOrDefault(r, List.of()).contains(perm)); }
        }

        var acl = new ACL();
        acl.grant("ROLE_ADMIN", "user:delete");
        acl.grant("ROLE_ADMIN", "report:view");
        acl.grant("ROLE_USER", "report:view");

        record User(String name, List<String> roles) {}
        var alice = new User("alice", List.of("ROLE_USER"));
        var admin = new User("admin", List.of("ROLE_ADMIN"));

        System.out.println("  alice can delete: " + acl.check(alice.roles(), "user:delete"));
        System.out.println("  admin can delete: " + acl.check(admin.roles(), "user:delete"));
        System.out.println("  alice can view report: " + acl.check(alice.roles(), "report:view"));

        System.out.println("\n  Spring annotations: @PreAuthorize, @PostAuthorize, @Secured, @RolesAllowed");
        System.out.println("  Security matchers: .hasRole(), .hasAuthority(), .authenticated(), .permitAll()");
    }

    static void passwordHashing() throws Exception {
        System.out.println("\n--- Password Hashing ---");

        var md = MessageDigest.getInstance("SHA-256");
        long start = System.nanoTime();
        String sha = Base64.getEncoder().encodeToString(md.digest("password".getBytes()));
        long shaTime = (System.nanoTime() - start) / 1_000_000;

        var spec = new javax.crypto.spec.PBEKeySpec("password".toCharArray(), "salt".getBytes(), 10000, 256);
        var factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        start = System.nanoTime();
        String pbkdf2 = Base64.getEncoder().encodeToString(factory.generateSecret(spec).getEncoded());
        long pbkdf2Time = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("  SHA-256:  %s... (%dms)%n", sha.substring(0, 20), shaTime);
        System.out.printf("  PBKDF2:   %s... (%dms)%n", pbkdf2.substring(0, 20), pbkdf2Time);

        System.out.println("\n  PasswordEncoders:");
        for (var e : List.of("NoOpPasswordEncoder (plain, DO NOT USE)",
                "BCryptPasswordEncoder (recommended, adaptive)",
                "Pbkdf2PasswordEncoder (configurable iterations)",
                "SCryptPasswordEncoder (memory-hard, future-proof)"))
            System.out.println("  " + e);
    }

    static void jwtConcept() {
        System.out.println("\n--- JWT Concept ---");

        var header = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        var payload = Base64.getUrlEncoder().withoutPadding().encodeToString(
            ("{\"sub\":\"user-42\",\"name\":\"Alice\",\"roles\":[\"ROLE_USER\"]," +
             "\"iat\":" + (System.currentTimeMillis() / 1000) + ",\"exp\":" + (System.currentTimeMillis() / 1000 + 3600) + "}").getBytes());
        var sig = Base64.getUrlEncoder().withoutPadding().encodeToString("hs256-signature".getBytes());
        var jwt = header + "." + payload + "." + sig;

        System.out.println("  JWT: " + jwt.substring(0, 50) + "...");

        System.out.println("\n  Validation steps:");
        for (var s : List.of("1. Parse Base64URL-encoded parts", "2. Verify HMAC signature",
                "3. Check exp claim (not expired)", "4. Check issuer/audience if present",
                "5. Extract authorities from roles claim"))
            System.out.println("  " + s);

        System.out.println("\n  SecurityFilterChain config example:");
        System.out.println("""
            http.authorizeHttpRequests(auth -> auth
              .requestMatchers("/api/public/**").permitAll()
              .requestMatchers("/api/admin/**").hasRole("ADMIN")
              .anyRequest().authenticated())
              .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
              .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS));""");
    }

    static void csrfProtection() {
        System.out.println("\n--- CSRF Protection ---");

        class CSRF {
            final Map<String, String> tokens = new ConcurrentHashMap<>();
            String generate(String session) {
                String t = UUID.randomUUID().toString(); tokens.put(session, t); return t;
            }
            boolean validate(String session, String token) {
                return token.equals(tokens.get(session));
            }
        }

        var csrf = new CSRF();
        String sess = "session-123";
        String token = csrf.generate(sess);
        System.out.println("  Wrong token: " + csrf.validate(sess, "bad") + " (403)");
        System.out.println("  Correct token: " + csrf.validate(sess, token) + " (200)");

        System.out.println("\n  CSRF strategies:");
        for (var s : List.of("Synchronizer Token Pattern",
                "SameSite Cookie (Lax/Strict)",
                "Origin/Referer header validation",
                "Double Submit Cookie"))
            System.out.println("  " + s);

        System.out.println("\n  Security concepts: Principal, GrantedAuthority, SecurityContext, SecurityContextHolder, FilterChain");
        System.out.println("  OAuth2 grants: Authorization Code, Client Credentials, Refresh Token, Device Code");
    }
}
