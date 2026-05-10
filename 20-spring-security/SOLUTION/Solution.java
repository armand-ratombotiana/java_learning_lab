package com.learning.lab.module20.solution;

import java.util.*;
import java.security.MessageDigest;
import java.util.Base64;

public class Solution {

    // JWT Token
    public static class JwtToken {
        private final String token;
        private final String subject;
        private final long issuedAt;
        private final long expiration;

        public JwtToken(String token, String subject, long issuedAt, long expiration) {
            this.token = token;
            this.subject = subject;
            this.issuedAt = issuedAt;
            this.expiration = expiration;
        }

        public String getToken() { return token; }
        public String getSubject() { return subject; }
        public long getIssuedAt() { return issuedAt; }
        public long getExpiration() { return expiration; }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiration;
        }
    }

    // JWT Builder
    public static class JwtBuilder {
        private String subject;
        private long issuedAt = System.currentTimeMillis();
        private long expiration = issuedAt + 3600000;
        private Map<String, Object> claims = new HashMap<>();

        public JwtBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public JwtBuilder expiration(long millis) {
            this.expiration = millis;
            return this;
        }

        public JwtBuilder claim(String key, Object value) {
            claims.put(key, value);
            return this;
        }

        public JwtToken build() {
            String token = Base64.getEncoder().encodeToString(
                (subject + "." + issuedAt + "." + expiration).getBytes()
            );
            return new JwtToken(token, subject, issuedAt, expiration);
        }
    }

    // JWT Parser
    public static class JwtParser {
        public JwtToken parse(String token) {
            try {
                String decoded = new String(Base64.getDecoder().decode(token));
                String[] parts = decoded.split("\\.");
                if (parts.length >= 3) {
                    return new JwtToken(token, parts[0], Long.parseLong(parts[1]), Long.parseLong(parts[2]));
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }

        public boolean validate(String token, String secret) {
            JwtToken jwt = parse(token);
            return jwt != null && !jwt.isExpired();
        }
    }

    // Password Encoder
    public interface PasswordEncoder {
        String encode(CharSequence rawPassword);
        boolean matches(CharSequence rawPassword, String encodedPassword);
    }

    public static class BCryptPasswordEncoder implements PasswordEncoder {
        private final int strength;

        public BCryptPasswordEncoder(int strength) {
            this.strength = strength;
        }

        @Override
        public String encode(CharSequence rawPassword) {
            String hash = rawPassword.toString() + "_bcrypt_" + strength;
            return Base64.getEncoder().encodeToString(hash.getBytes());
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return encode(rawPassword).equals(encodedPassword);
        }
    }

    public static class SHA256PasswordEncoder implements PasswordEncoder {
        @Override
        public String encode(CharSequence rawPassword) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(rawPassword.toString().getBytes());
                return Base64.getEncoder().encodeToString(hash);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return encode(rawPassword).equals(encodedPassword);
        }
    }

    // OAuth2
    public static class OAuth2Provider {
        private final String clientId;
        private final String clientSecret;
        private final String authorizationUri;
        private final String tokenUri;

        public OAuth2Provider(String clientId, String clientSecret, String authUri, String tokenUri) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.authorizationUri = authUri;
            this.tokenUri = tokenUri;
        }

        public String getAuthorizationUrl(String redirectUri, String state) {
            return authorizationUri + "?client_id=" + clientId + "&redirect_uri=" + redirectUri + "&state=" + state;
        }

        public String getClientId() { return clientId; }
        public String getTokenUri() { return tokenUri; }
    }

    public static class OAuth2Token {
        private final String accessToken;
        private final String refreshToken;
        private final long expiresIn;
        private final String tokenType;

        public OAuth2Token(String accessToken, String refreshToken, long expiresIn, String tokenType) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresIn = expiresIn;
            this.tokenType = tokenType;
        }

        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public long getExpiresIn() { return expiresIn; }
        public String getTokenType() { return tokenType; }
    }

    // Security Config
    public static class SecurityConfig {
        private boolean jwtEnabled = true;
        private boolean oauth2Enabled = false;
        private String[] permittedPaths = {"/public/**", "/auth/login"};
        private String[] securedPaths = {"/api/**"};

        public void setJwtEnabled(boolean enabled) { this.jwtEnabled = enabled; }
        public void setOAuth2Enabled(boolean enabled) { this.oauth2Enabled = enabled; }
        public void setPermittedPaths(String[] paths) { this.permittedPaths = paths; }
        public void setSecuredPaths(String[] paths) { this.securedPaths = paths; }

        public boolean isJwtEnabled() { return jwtEnabled; }
        public boolean isOAuth2Enabled() { return oauth2Enabled; }
        public String[] getPermittedPaths() { return permittedPaths; }
        public String[] getSecuredPaths() { return securedPaths; }
    }

    // Authentication
    public interface Authentication {
        Object getPrincipal();
        Object getCredentials();
        boolean isAuthenticated();
    }

    public static class UsernamePasswordAuthentication implements Authentication {
        private final String username;
        private final String password;
        private final boolean authenticated;

        public UsernamePasswordAuthentication(String username, String password) {
            this.username = username;
            this.password = password;
            this.authenticated = true;
        }

        @Override
        public Object getPrincipal() { return username; }
        @Override
        public Object getCredentials() { return password; }
        @Override
        public boolean isAuthenticated() { return authenticated; }
    }

    // Authorization
    public static class AuthorizationManager {
        private final Map<String, Set<String>> rolePermissions = new HashMap<>();

        public void addRolePermission(String role, String permission) {
            rolePermissions.computeIfAbsent(role, k -> new HashSet<>()).add(permission);
        }

        public boolean hasPermission(String role, String permission) {
            Set<String> perms = rolePermissions.get(role);
            return perms != null && perms.contains(permission);
        }
    }

    // User Details
    public interface UserDetails {
        String getUsername();
        String getPassword();
        List<String> getAuthorities();
        boolean isAccountNonExpired();
        boolean isAccountNonLocked();
    }

    public static class DefaultUserDetails implements UserDetails {
        private final String username;
        private final String password;
        private final List<String> authorities;

        public DefaultUserDetails(String username, String password, List<String> authorities) {
            this.username = username;
            this.password = password;
            this.authorities = authorities;
        }

        @Override
        public String getUsername() { return username; }
        @Override
        public String getPassword() { return password; }
        @Override
        public List<String> getAuthorities() { return authorities; }
        @Override
        public boolean isAccountNonExpired() { return true; }
        @Override
        public boolean isAccountNonLocked() { return true; }
    }

    // Filter Chain
    public static class SecurityFilterChain {
        private final List<SecurityFilter> filters = new ArrayList<>();

        public void addFilter(SecurityFilter filter) {
            filters.add(filter);
        }

        public void applyFilters(SecurityContext context) {
            for (SecurityFilter filter : filters) {
                filter.doFilter(context);
            }
        }
    }

    public interface SecurityFilter {
        void doFilter(SecurityContext context);
    }

    public static class JwtAuthenticationFilter implements SecurityFilter {
        private final JwtParser jwtParser;

        public JwtAuthenticationFilter(JwtParser jwtParser) {
            this.jwtParser = jwtParser;
        }

        @Override
        public void doFilter(SecurityContext context) {
            String token = context.getRequestHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                JwtToken jwt = jwtParser.parse(token);
                if (jwt != null && !jwt.isExpired()) {
                    context.setAuthentication(new UsernamePasswordAuthentication(jwt.getSubject(), ""));
                }
            }
        }
    }

    public static class SecurityContext {
        private Authentication authentication;
        private String requestHeader(String header) {
            return "";
        }

        public void setAuthentication(Authentication auth) {
            this.authentication = auth;
        }

        public Authentication getAuthentication() {
            return authentication;
        }
    }

    // CSRF Protection
    public static class CsrfToken {
        private final String token;
        private final String headerName;

        public CsrfToken(String token, String headerName) {
            this.token = token;
            this.headerName = headerName;
        }

        public String getToken() { return token; }
        public String getHeaderName() { return headerName; }
    }

    public static class CsrfProtection {
        private final Set<String> protectedMethods = new HashSet<>(Set.of("POST", "PUT", "DELETE"));

        public boolean isProtected(String method) {
            return protectedMethods.contains(method.toUpperCase());
        }
    }

    public static void demonstrateSecurity() {
        System.out.println("=== JWT Token ===");
        JwtToken jwt = new JwtBuilder()
            .subject("user123")
            .expiration(System.currentTimeMillis() + 3600000)
            .claim("role", "ADMIN")
            .build();
        System.out.println("Token: " + jwt.getToken());
        System.out.println("Subject: " + jwt.getSubject());
        System.out.println("Expired: " + jwt.isExpired());

        System.out.println("\n=== JWT Parser ===");
        JwtParser parser = new JwtParser();
        String token = "dXNlcjEyMy4xNjQwMDAwMDAwMC4xNjkwMDAwMDAwMA==";
        JwtToken parsed = parser.parse(token);
        if (parsed != null) {
            System.out.println("Parsed subject: " + parsed.getSubject());
        }

        System.out.println("\n=== Password Encoder ===");
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(10);
        String encoded = bcrypt.encode("password123");
        System.out.println("BCrypt encoded: " + encoded.substring(0, 20) + "...");
        System.out.println("Matches: " + bcrypt.matches("password123", encoded));

        SHA256PasswordEncoder sha256 = new SHA256PasswordEncoder();
        String shaEncoded = sha256.encode("password123");
        System.out.println("SHA256 encoded: " + shaEncoded);

        System.out.println("\n=== OAuth2 ===");
        OAuth2Provider oauth = new OAuth2Provider("clientId", "clientSecret",
            "https://oauth.provider.com/authorize", "https://oauth.provider.com/token");
        String authUrl = oauth.getAuthorizationUrl("http://localhost:8080/callback", "state123");
        System.out.println("Auth URL: " + authUrl);

        System.out.println("\n=== Security Config ===");
        SecurityConfig config = new SecurityConfig();
        config.setJwtEnabled(true);
        config.setOAuth2Enabled(true);
        System.out.println("JWT enabled: " + config.isJwtEnabled());
        System.out.println("Secured paths: " + String.join(", ", config.getSecuredPaths()));
    }

    public static void main(String[] args) {
        demonstrateSecurity();
    }
}