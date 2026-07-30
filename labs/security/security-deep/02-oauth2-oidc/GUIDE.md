# OAuth2 & OIDC — Study Guide

## Core Concepts

### OAuth2 Grant Types
- **Authorization Code**: most secure for web apps; exchanges code for tokens
- **Authorization Code + PKCE**: required for public clients (SPA, mobile)
- **Client Credentials**: machine-to-machine, no user involved
- **Refresh Token**: long-lived token to obtain new access tokens

### JWT Structure
- Header: {"alg":"RS256","typ":"JWT"}
- Payload: {"sub":"user123","exp":1700000000,"iss":"https://auth.example.com"}
- Signature: HMAC-SHA256 or RSA-SHA256 of base64(header).base64(payload)

### OIDC
- Extends OAuth2 with id_token (JWT)
- Claims: sub, name, email, email_verified
- UserInfo endpoint returns claims about authenticated user
- Discovery: /.well-known/openid-configuration

## Implementation Checklist
1. Always validate JWT signature, expiration (exp), issuer (iss), audience (aud)
2. Use PKCE for SPA/mobile clients (never client_secret in public clients)
3. Store refresh tokens securely (HTTP-only cookies or secure storage)
4. Keep access tokens short-lived (15-60 minutes)

## Common Pitfalls
- Not validating JWT signature leads to token forgery
- Exposing client_secret in client-side code
- Storing tokens in localStorage (XSS vulnerability)
- Not rotating refresh tokens
