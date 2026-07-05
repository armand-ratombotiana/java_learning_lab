package com.security05;

import java.util.*;

/**
 * Simulates Keycloak configuration for a Spring Boot application.
 * 
 * SECURITY CONCEPT: Keycloak is an open-source Identity and Access
 * Management (IAM) solution providing:
 * - Single Sign-On (SSO)
 * - User federation (LDAP, Active Directory)
 * - Social login (Google, GitHub, etc.)
 * - Fine-grained authorization
 * - User-managed access
 * 
 * Configuration parameters that affect security:
 * - sslRequired: Enforce TLS
 * - publicClient: true for SPAs/mobile (forces PKCE use)
 * - bearerOnly: true for microservices that don't initiate logins
 * - confidentialPort: Port for SSL-backed communication
 */
public class KeycloakConfig {

    // Simulates keycloak.json / application.yml config
    private final String authServerUrl;
    private final String realm;
    private final String resource; // client ID
    private final String secret;
    private final boolean publicClient;
    private final boolean bearerOnly;
    private final String sslRequired;
    private final int confidentialPort;
    private final List<String> allowedOrigins;

    private KeycloakConfig(Builder builder) {
        this.authServerUrl = builder.authServerUrl;
        this.realm = builder.realm;
        this.resource = builder.resource;
        this.secret = builder.secret;
        this.publicClient = builder.publicClient;
        this.bearerOnly = builder.bearerOnly;
        this.sslRequired = builder.sslRequired;
        this.confidentialPort = builder.confidentialPort;
        this.allowedOrigins = builder.allowedOrigins;
    }

    /**
     * Validates the configuration.
     * SECURITY: Refuse to start if TLS is not configured.
     */
    public boolean validate() {
        if (!"all".equals(sslRequired) && !"external".equals(sslRequired)) {
            System.err.println("SECURITY WARNING: SSL not enforced for Keycloak");
            return false;
        }
        if (!publicClient && (secret == null || secret.isEmpty())) {
            System.err.println("SECURITY ERROR: Confidential client needs a secret");
            return false;
        }
        return true;
    }

    public String getWellKnownUrl() {
        return authServerUrl + "/realms/" + realm + "/.well-known/openid-configuration";
    }

    public String getJwksUrl() {
        return authServerUrl + "/realms/" + realm + "/protocol/openid-connect/certs";
    }

    public String getTokenUrl() {
        return authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    /**
     * Builds the Keycloak spring-security adapter configuration.
     */
    public Map<String, Object> toAdapterConfig() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("realm", realm);
        config.put("auth-server-url", authServerUrl);
        config.put("resource", resource);
        config.put("ssl-required", sslRequired);
        config.put("public-client", publicClient);
        config.put("confidential-port", confidentialPort);
        config.put("bearer-only", bearerOnly);
        if (!publicClient) {
            config.put("credentials", Map.of("secret", "****"));
        }
        return config;
    }

    public static class Builder {
        private String authServerUrl = "http://localhost:8080";
        private String realm = "my-realm";
        private String resource;
        private String secret;
        private boolean publicClient = true;
        private boolean bearerOnly;
        private String sslRequired = "external";
        private int confidentialPort = 8443;
        private List<String> allowedOrigins = List.of("/*");

        public Builder authServerUrl(String url) { this.authServerUrl = url; return this; }
        public Builder realm(String realm) { this.realm = realm; return this; }
        public Builder resource(String resource) { this.resource = resource; return this; }
        public Builder secret(String secret) { this.secret = secret; return this; }
        public Builder publicClient(boolean pc) { this.publicClient = pc; return this; }
        public Builder bearerOnly(boolean bo) { this.bearerOnly = bo; return this; }
        public Builder sslRequired(String ssl) { this.sslRequired = ssl; return this; }
        public Builder confidentialPort(int port) { this.confidentialPort = port; return this; }
        public KeycloakConfig build() { return new KeycloakConfig(this); }
    }

    @Override
    public String toString() {
        return "KeycloakConfig{" +
                "authServerUrl='" + authServerUrl + '\'' +
                ", realm='" + realm + '\'' +
                ", resource='" + resource + '\'' +
                ", publicClient=" + publicClient +
                ", bearerOnly=" + bearerOnly +
                ", sslRequired='" + sslRequired + '\'' +
                '}';
    }

    public static void main(String[] args) {
        System.out.println("=== Keycloak Configuration ===\n");

        // Confidential client (server-side app with client secret)
        KeycloakConfig confidentialConfig = new KeycloakConfig.Builder()
                .authServerUrl("https://auth.example.com:8443")
                .realm("security-academy")
                .resource("spring-boot-app")
                .secret("your-client-secret")
                .publicClient(false)
                .bearerOnly(true)
                .sslRequired("all")
                .build();

        System.out.println("Confidential Client Config:");
        System.out.println(confidentialConfig);
        System.out.println("  Valid: " + confidentialConfig.validate());
        System.out.println("  Token URL: " + confidentialConfig.getTokenUrl());
        System.out.println("  JWKS URL: " + confidentialConfig.getJwksUrl());
        System.out.println("  OpenID Config: " + confidentialConfig.getWellKnownUrl());

        // Public client (SPA / mobile app)
        KeycloakConfig publicConfig = new KeycloakConfig.Builder()
                .authServerUrl("https://auth.example.com:8443")
                .realm("security-academy")
                .resource("spa-app")
                .publicClient(true)
                .sslRequired("external")
                .build();

        System.out.println("\nPublic Client (SPA) Config:");
        System.out.println(publicConfig);
        System.out.println("  Valid: " + publicConfig.validate());

        // Adapter configuration output
        System.out.println("\nAdapter Config (for application.yml):");
        confidentialConfig.toAdapterConfig().forEach(
                (k, v) -> System.out.println("  " + k + ": " + v));
    }
}
