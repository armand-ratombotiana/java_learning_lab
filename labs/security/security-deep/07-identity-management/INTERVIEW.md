# Interview: Identity Management

## Q1: Conceptual Understanding
**Q**: Compare SAML 2.0 and OIDC for SSO.
**A**: SAML uses XML assertions, HTTP-Redirect/POST bindings, and is common in enterprise. OIDC uses JWTs, REST/JSON, and is common in modern apps. OIDC is simpler to implement, works better with mobile/SPA, and is the modern choice.

## Q2: Implementation
**Q**: How does TOTP work for multi-factor authentication?
**A**: TOTP uses HMAC-SHA1 with time as counter: TOTP = Truncate(HMAC-SHA1(K, T)). T is time in 30-second windows from epoch. Server and client share a secret K. The 6-8 digit code is valid for one window.

## Q3: System Design
**Q**: Design an identity management system for a multinational enterprise.
**A**: Central IdP (Azure AD/Keycloak) with federation to regional IdPs. SCIM for automated user provisioning. SAML/OIDC for SSO. LDAP for directory sync. MFA with TOTP + WebAuthn. RBAC with attribute-based policies. Audit logging for compliance.

## Coding Challenge
Implement TOTP code generation and validation with a shared secret.
