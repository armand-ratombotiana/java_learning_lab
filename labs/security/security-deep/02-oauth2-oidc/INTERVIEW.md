# Interview: OAuth2 & OIDC

## Q1: Conceptual Understanding
**Q**: Explain the OAuth2 authorization code flow with PKCE.
**A**: Client generates code_verifier (random) and code_challenge=SHA256(verifier). Redirects user to auth server. Auth server returns code. Client exchanges code + verifier for tokens. PKCE prevents interception of the authorization code.

## Q2: Implementation
**Q**: How do you validate a JWT access token?
**A**: 1) Verify signature using JWKS from authorization server. 2) Check exp (not expired). 3) Validate iss matches expected issuer. 4) Validate aud includes client ID. 5) Check nbf (not before) if present.

## Q3: System Design
**Q**: Design a microservice authentication architecture.
**A**: Centralized OAuth2 authorization server (Keycloak/Okta). Each service validates tokens locally (stateless) using JWKS. Use scopes for fine-grained permissions. Redis for token revocation blacklist.

## Coding Challenge
Implement a JWT validator that checks signature, expiration, issuer, and audience claims.
