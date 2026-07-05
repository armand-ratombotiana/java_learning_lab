package com.security02;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * Simulates the OAuth2 Authorization Code flow.
 * 
 * SECURITY CONCEPT: The Authorization Code flow is the most secure
 * OAuth2 grant for server-side web applications. Three parties interact:
 * 
 * 1. Client (our app) — initiates the flow
 * 2. Authorization Server — authenticates user and issues code
 * 3. Resource Server — validates token and serves protected data
 * 
 * Flow: User → Auth Request → Login → Code → Token → API Call
 * 
 * The 'state' parameter prevents CSRF on the callback.
 * The 'code' is a one-time use credential exchanged for tokens server-side.
 */
public class AuthorizationCodeFlow {

    private final OAuth2ClientConfig clientConfig;
    private final HttpClient httpClient;
    private final SecureRandom secureRandom;
    private String state;
    private String authorizationCode;
    private String accessToken;

    public AuthorizationCodeFlow(OAuth2ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.httpClient = HttpClient.newHttpClient();
        this.secureRandom = new SecureRandom();
    }

    /**
     * Step 1: Generate and store the state parameter.
     * SECURITY: State binds the authorization request to the callback,
     * preventing CSRF attacks on the OAuth redirect flow.
     */
    public String generateState() {
        byte[] stateBytes = new byte[32];
        secureRandom.nextBytes(stateBytes);
        this.state = Base64.getUrlEncoder().withoutPadding().encodeToString(stateBytes);
        return this.state;
    }

    /**
     * Step 2: Build the authorization request URL.
     * The user's browser is redirected to this URL at the authorization server.
     */
    public String buildAuthorizationRequest() {
        String stateParam = generateState();
        // SECURITY: code_challenge and code_challenge_method for PKCE
        String pkceParams = "";
        if (clientConfig.isPkceEnabled()) {
            pkceParams = "&code_challenge_method=S256&code_challenge=" + generateCodeChallenge();
        }
        return clientConfig.buildAuthorizationUrl()
                + "&state=" + stateParam
                + pkceParams;
    }

    /**
     * Step 3: Handle the callback (simulated).
     * Validates state matches, then exchanges code for tokens.
     */
    public boolean handleCallback(String receivedState, String receivedCode) {
        // SECURITY: Validate state to prevent CSRF
        if (!this.state.equals(receivedState)) {
            System.out.println("CSRF DETECTED: State mismatch!");
            return false;
        }
        this.authorizationCode = receivedCode;
        System.out.println("Authorization code received (validated state)");
        return true;
    }

    /**
     * Step 4: Exchange the authorization code for tokens.
     * SECURITY: This exchange happens server-to-server (back-channel).
     * The client_secret is NEVER exposed to the user agent.
     */
    public String exchangeCodeForToken() {
        if (authorizationCode == null) {
            throw new IllegalStateException("No authorization code to exchange");
        }
        // In production: POST to tokenUri with grant_type=authorization_code
        System.out.println("Exchanging code for token at: "
                + clientConfig.getTokenUri());
        System.out.println("grant_type=authorization_code");
        System.out.println("code=" + authorizationCode);
        System.out.println("redirect_uri=" + clientConfig.getRedirectUri());
        System.out.println("client_id=" + clientConfig.getClientId());
        System.out.println("client_secret=[omitted - server-side only]");

        // Simulated token response
        this.accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9."
                + "eyJzdWIiOiJ1c2VyMTIzIiwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSJ9."
                + "fakesignature";
        return this.accessToken;
    }

    /**
     * Step 5: Call protected resource with the access token.
     * SECURITY: Token sent in Authorization header as Bearer token.
     * Must use HTTPS to prevent token interception.
     */
    public String callProtectedResource(String resourceUri) {
        if (accessToken == null) {
            throw new IllegalStateException("No access token available");
        }
        // In production: actual HTTP call
        System.out.println("GET " + resourceUri);
        System.out.println("Authorization: Bearer " + accessToken.substring(0, 50) + "...");
        return "{\"message\": \"Protected resource accessed successfully\"}";
    }

    /**
     * Generates a code challenge for PKCE using S256.
     * NOTE: This is a placeholder; PKCEDemo.java has the full implementation.
     */
    private String generateCodeChallenge() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String verifier = UUID.randomUUID().toString();
            byte[] challenge = md.digest(verifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(challenge);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static void main(String[] args) {
        OAuth2ClientConfig config = new OAuth2ClientConfig.Builder()
                .clientId("demo-client")
                .authorizationUri("https://auth.example.com/authorize")
                .tokenUri("https://auth.example.com/token")
                .redirectUri("https://app.example.com/callback")
                .scopes("openid", "profile")
                .build();

        AuthorizationCodeFlow flow = new AuthorizationCodeFlow(config);

        System.out.println("=== OAuth2 Authorization Code Flow ===\n");

        // Step 1-2: Build auth request
        String authUrl = flow.buildAuthorizationRequest();
        System.out.println("1. Redirect user to:\n" + authUrl + "\n");

        // Step 3: User authenticates and is redirected back with code
        String stateFromCallback = flow.state; // Simulates provider sending state back
        String codeFromCallback = "auth_code_x7k2m9n4";
        boolean valid = flow.handleCallback(stateFromCallback, codeFromCallback);
        System.out.println("2. Callback handled: " + valid + "\n");

        // Step 4: Exchange code for token
        String token = flow.exchangeCodeForToken();
        System.out.println("3. Access token obtained\n");

        // Step 5: Use token
        String response = flow.callProtectedResource("https://api.example.com/userinfo");
        System.out.println("4. API response: " + response);
    }
}
