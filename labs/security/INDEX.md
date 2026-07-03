# Security Academy — Complete Learning Path

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Keycloak](https://img.shields.io/badge/Keycloak-3B82F6?style=for-the-badge&logo=keycloak&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![Labs](https://img.shields.io/badge/Labs-8-blue?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Intermediate_to_Expert-purple?style=for-the-badge)

**Secure your Java applications — from authentication to authorization, encryption to OAuth2**

</div>

---

## Overview

The Security Academy provides a comprehensive curriculum covering application security in the Java ecosystem. You will learn authentication and authorization patterns, Spring Security, OAuth2 and OpenID Connect, Keycloak, JWT, encryption, and secure coding practices. Each lab combines theoretical security principles with hands-on Java implementations.

---

## Curriculum Map

### Level 1: Authentication & Authorization
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 01 | [Security Fundamentals](./01-security-fundamentals/) | Authentication vs authorization, hashing, salting, secure defaults | 3-4 hrs | Intermediate | [11-security](../../11-security/) |
| 02 | [Spring Security Basics](./02-spring-security-basics/) | Filter chains, security configuration, form login, logout | 4-5 hrs | Intermediate | [20-spring-security](../../20-spring-security/) |
| 03 | [JWT (JSON Web Tokens)](./03-jwt/) | Token structure, signing, verification, refresh tokens | 3-4 hrs | Intermediate | [20-spring-security](../../20-spring-security/) |

### Level 2: OAuth2 & Identity Management
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 04 | [OAuth2 & OpenID Connect](./04-oauth2-oidc/) | Authorization code, client credentials, PKCE, ID tokens | 4-5 hrs | Advanced | [11-security](../../11-security/) |
| 05 | [Keycloak Integration](./05-keycloak/) | Identity provider, realms, clients, users, roles | 4-5 hrs | Advanced | [37-keycloak](../../37-keycloak/) |
| 06 | [Method Security & RBAC](./06-method-security/) | @PreAuthorize, @PostAuthorize, role hierarchies, ACL | 3-4 hrs | Advanced | [20-spring-security](../../20-spring-security/) |

### Level 3: Advanced Security
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 07 | [Encryption & Cryptography](./07-encryption/) | Symmetric/asymmetric encryption, digital signatures, TLS | 4-5 hrs | Advanced | — |
| 08 | [API Security & Secure Coding](./08-api-security/) | CSRF, CORS, rate limiting, input validation, secrets management | 3-4 hrs | Advanced | [11-security](../../11-security/) |

**Total estimated time: 28-36 hours**

---

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 08
Fund    Spring  JWT     OAuth2  Keycloak Method  Encrypt  API
                          & OIDC          Security          Security
```

Labs 01–03 build foundational authentication and token knowledge. Labs 04–06 cover OAuth2 and identity providers. Labs 07–08 cover cryptography and API hardening.

---

## Prerequisites

- Java proficiency with Spring Boot experience
- Understanding of HTTP and REST APIs
- Basic knowledge of authentication flows (login, session)
- Docker for running Keycloak

---

## How to Use This Academy

### For Backend Developers
Work through Labs 01–06 for comprehensive authentication and authorization skills.

### For Security Engineers
Pay special attention to Labs 07–08 for cryptography and secure coding practices.

### For Architects
Focus on Labs 04–05 to understand identity provider integration and SSO patterns.

---

## Related Academies

- [Backend Academy](../backend/) — Spring Boot, REST APIs
- [Networking Academy](../networking/) — TLS, mTLS, service mesh security
- [DevOps Academy](../devops/) — Secrets management, container security
- [Cloud Academy](../cloud/) — IAM, security groups, compliance

---

## Resources

### Official Documentation
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [OAuth 2.0 Spec](https://datatracker.ietf.org/doc/html/rfc6749)
- [OpenID Connect Spec](https://openid.net/connect/)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [JWT.io](https://jwt.io/)

### Books
- *Spring Security in Action* — Laurentiu Spilca
- *OAuth 2 in Action* — Justin Richer
- *The Tangled Web* — Michal Zalewski
- *Cryptography Engineering* — Niels Ferguson, Bruce Schneier

### Tools
- [Keycloak](https://www.keycloak.org/)
- [jwt.io Debugger](https://jwt.io/)
- [OWASP ZAP](https://www.zaproxy.org/)
- [Burp Suite](https://portswigger.net/burp)

---

<div align="center">

**Secure Applications. Build Everything.**

</div>
