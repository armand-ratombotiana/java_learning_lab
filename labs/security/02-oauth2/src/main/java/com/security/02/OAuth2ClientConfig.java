package com.security02;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration model for an OAuth2 client.
 * 
 * SECURITY CONCEPT: OAuth2 is an authorization framework that enables
 * third-party applications to obtain limited access to user resources
 * without exposing credentials. This class demonstrates how to configure
 * an OAuth2 client with proper security settings.
 * 
 * Key security parameters:
 * - clientId / clientSecret: Registered credentials
 * - redirectUri: Must be pre-registered and validated
 * - scopes: Request only the minimum permissions needed
 * - responseType: "code" for authorization code flow (most secure for web apps)
 */
public class OAuth2ClientConfig {

    private final String clientId;
    private final String clientSecret;
    private final String authorizationUri;
    private final String tokenUri;
    private final String redirectUri;
    private final List<String> scopes;
    private final String responseType;
    private final boolean pkceEnabled;

    public OAuth2ClientConfig(Builder builder) {
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.authorizationUri = builder.authorizationUri;
        this.tokenUri = builder.tokenUri;
        this.redirectUri = builder.redirectUri;
        this.scopes = builder.scopes;
        this.responseType = builder.responseType;
        this.pkceEnabled = builder.pkceEnabled;
    }

    /**
     * Builds the authorization URL for redirecting the user agent.
     * SECURITY: The state parameter (not shown here) is critical to
     * prevent CSRF attacks on the OAuth callback.
     */
    public String buildAuthorizationUrl() {
        return authorizationUri + "?"
                + "response_type=" + responseType + "&"
                + "client_id=" + clientId + "&"
                + "redirect_uri=" + redirectUri + "&"
                + "scope=" + String.join("+", scopes);
    }

    public static class Builder {
        private String clientId;
        private String clientSecret;
        private String authorizationUri;
        private String tokenUri;
        private String redirectUri;
        private List<String> scopes = List.of("openid", "profile");
        private String responseType = "code";
        private boolean pkceEnabled = true;

        public Builder clientId(String clientId) { this.clientId = clientId; return this; }
        public Builder clientSecret(String clientSecret) { this.clientSecret = clientSecret; return this; }
        public Builder authorizationUri(String uri) { this.authorizationUri = uri; return this; }
        public Builder tokenUri(String uri) { this.tokenUri = uri; return this; }
        public Builder redirectUri(String uri) { this.redirectUri = uri; return this; }
        public Builder scopes(String... scopes) { this.scopes = Arrays.asList(scopes); return this; }
        public Builder responseType(String type) { this.responseType = type; return this; }
        public Builder pkceEnabled(boolean enabled) { this.pkceEnabled = enabled; return this; }
        public OAuth2ClientConfig build() { return new OAuth2ClientConfig(this); }
    }

    public String getClientId() { return clientId; }
    public String getClientSecret() { return clientSecret; }
    public String getAuthorizationUri() { return authorizationUri; }
    public String getTokenUri() { return tokenUri; }
    public String getRedirectUri() { return redirectUri; }
    public List<String> getScopes() { return scopes; }
    public String getResponseType() { return responseType; }
    public boolean isPkceEnabled() { return pkceEnabled; }

    @Override
    public String toString() {
        return "OAuth2ClientConfig{"
                + "clientId='" + clientId + '\''
                + ", authorizationUri='" + authorizationUri + '\''
                + ", redirectUri='" + redirectUri + '\''
                + ", scopes=" + scopes
                + ", responseType='" + responseType + '\''
                + ", pkceEnabled=" + pkceEnabled
                + '}';
    }

    public static void main(String[] args) {
        // Simulates a Spring Security OAuth2 client configuration
        OAuth2ClientConfig config = new OAuth2ClientConfig.Builder()
                .clientId("my-app-client")
                .clientSecret("client-secret-here") // Keep secret server-side
                .authorizationUri("https://provider.example.com/oauth2/authorize")
                .tokenUri("https://provider.example.com/oauth2/token")
                .redirectUri("https://my-app.example.com/login/oauth2/code/provider")
                .scopes("openid", "profile", "email")
                .responseType("code")
                .pkceEnabled(false) // Public clients (SPA/mobile) MUST enable PKCE
                .build();

        System.out.println("=== OAuth2 Client Configuration ===");
        System.out.println("Client ID: " + config.getClientId());
        System.out.println("Redirect URI: " + config.getRedirectUri());
        System.out.println("Scopes: " + config.getScopes());
        System.out.println("PKCE Enabled: " + config.isPkceEnabled());
        System.out.println("\nAuthorization URL:\n" + config.buildAuthorizationUrl());
    }
}
