# Pedagogic Guide - Keycloak

## Learning Path

### Phase 1: Authentication Fundamentals
1. OAuth2 flows (Authorization Code, Client Credentials, etc.)
2. OpenID Connect architecture
3. JWT structure and claims
4. Token validation

### Phase 2: Keycloak Setup
1. Realm and client configuration
2. User management and roles
3. Identity providers configuration
4. Role mapper setup

### Phase 3: Spring Security Integration
1. OAuth2 Resource Server setup
2. JWT validation and decoding
3. Security filter chain configuration
4. Method-level security

### Phase 4: Advanced Features
1. Custom claims and attributes
2. Permission-based access (Keycloak Authorization)
3. Token exchange and refresh
4. Multi-factor authentication

### Phase 5: Operations
1. User federation providers
2. Theme customization
3. Event logging and auditing
4. Performance tuning

## OAuth2 Flows

| Flow | Use Case |
|------|----------|
| Authorization Code | Web apps with user login |
| Client Credentials | Service-to-service |
| Device Code | CLI tools, TVs |
| Refresh Token | Long-lived sessions |

## JWT Claims

| Claim | Description |
|-------|-------------|
| `sub` | Subject (user ID) |
| `iss` | Issuer (Keycloak URL) |
| `aud` | Audience (client ID) |
| `exp` | Expiration time |
| `iat` | Issued at time |
| `realm_access.roles` | User roles |

## Interview Topics
- OAuth2 vs. SAML comparison
- When to use which OAuth flow
- Token security and validation
- Refresh token rotation
- Keycloak vs. alternatives (Auth0, Okta)

## Next Steps
- Explore Keycloak Authorization Services
- Learn about fine-grained permissions
- Study Token Exchange for delegated access