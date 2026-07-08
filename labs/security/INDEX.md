# Security Academy — Complete Learning Path

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Keycloak](https://img.shields.io/badge/Keycloak-3B82F6?style=for-the-badge&logo=keycloak&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![Labs](https://img.shields.io/badge/Labs-20-blue?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Beginner_to_Expert-purple?style=for-the-badge)

**Secure your Java applications — from authentication to advanced enterprise security**

</div>

---

## Overview

The Security Academy provides a comprehensive curriculum covering application security in the Java ecosystem. You will learn authentication and authorization patterns, Spring Security, OAuth2 and OpenID Connect, Keycloak, JWT, encryption, secure coding, SAML federation, WebAuthn passkeys, secrets management, container security, API security testing, zero trust architecture, blockchain security, supply chain security, SIEM monitoring, and incident response. Each lab combines theoretical security principles with hands-on Java implementations.

---

## Curriculum Map

### Level 1: Authentication & Authorization
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 01 | [Security Fundamentals](./01-authentication-basics/) | Authentication vs authorization, hashing, salting, secure defaults | 3-4 hrs | Intermediate | [11-security](../../11-security/) |
| 02 | [Spring Security Basics](./02-oauth2/) | Filter chains, security configuration, form login, logout | 4-5 hrs | Intermediate | [20-spring-security](../../20-spring-security/) |
| 03 | [JWT (JSON Web Tokens)](./03-jwt/) | Token structure, signing, verification, refresh tokens | 3-4 hrs | Intermediate | [20-spring-security](../../20-spring-security/) |

### Level 2: OAuth2 & Identity Management
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 04 | [Spring Security](./04-spring-security/) | Spring Security configuration, authorization, method security | 4-5 hrs | Advanced | [11-security](../../11-security/) |
| 05 | [Keycloak](./05-keycloak/) | Identity provider, realms, clients, users, roles | 4-5 hrs | Advanced | [37-keycloak](../../37-keycloak/) |
| 06 | [Encryption](./06-encryption/) | Symmetric/asymmetric encryption, digital signatures, TLS | 4-5 hrs | Advanced | [20-spring-security](../../20-spring-security/) |

### Level 3: Advanced Security
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 07 | [CSRF & XSS Protection](./07-csrf-xss-protection/) | CSRF tokens, XSS prevention, content security policy | 3-4 hrs | Advanced | — |
| 08 | [CORS Headers](./08-cors-headers/) | Cross-origin resource sharing, preflight, allowed origins | 3-4 hrs | Advanced | [11-security](../../11-security/) |
| 09 | [Authorization RBAC](./09-authorization-rbac/) | Role-based access control, permissions, role hierarchies | 3-4 hrs | Advanced | [11-security](../../11-security/) |
| 10 | [Spring Security Advanced](./10-spring-security-advanced/) | OAuth2 resource server, Reactive security, testing | 4-5 hrs | Expert | [20-spring-security](../../20-spring-security/) |

### Level 4: Enterprise Security & Advanced Topics
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 11 | [SAML Federated Identity](./11-saml-federated-identity/) | SAML 2.0, SSO, identity federation, SP/IdP | 4-5 hrs | Advanced | — |
| 12 | [WebAuthn & Passkeys](./12-webauthn-passkeys/) | WebAuthn API, FIDO2, passkeys, biometric auth | 4-5 hrs | Advanced | — |
| 13 | [Secrets Management](./13-secrets-management/) | Vault, encrypted configs, secret rotation | 4-5 hrs | Advanced | — |
| 14 | [Container Security](./14-container-security/) | Docker security, image scanning, K8s pod security | 4-5 hrs | Advanced | — |
| 15 | [API Security Testing](./15-api-security-testing/) | OWASP Top 10, penetration testing, SAST/DAST | 4-5 hrs | Expert | — |
| 16 | [Zero Trust Architecture](./16-zero-trust-architecture/) | Zero trust, micro-segmentation, BeyondCorp | 4-5 hrs | Expert | — |
| 17 | [Blockchain Security](./17-blockchain-security/) | Smart contract vulns, consensus attacks, DeFi | 4-5 hrs | Expert | — |
| 18 | [Supply Chain Security](./18-supply-chain-security/) | SBOM, dependency scanning, Sigstore, SLSA | 4-5 hrs | Expert | — |
| 19 | [SIEM Monitoring](./19-siem-monitoring/) | Log aggregation, correlation, threat detection | 4-5 hrs | Expert | — |
| 20 | [Incident Response & Forensics](./20-incident-response-forensics/) | IR framework, containment, forensic imaging | 4-5 hrs | Expert | — |

**Total estimated time: 75-100 hours**

---

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 08 ──→ 09 ──→ 10
Fund    Spring  JWT     Spring  Keycloak Encrypt CSRF   CORS   RBAC   Adv
                        Security                          
                                                                         
11 ──→ 12 ──→ 13 ──→ 14 ──→ 15 ──→ 16 ──→ 17 ──→ 18 ──→ 19 ──→ 20
SAML    WebAuthn Secret Contain API     Zero   Block  Supply SIEM   IR
                Mgt    Sec     Test    Trust  Chain  Chain
```

Labs 01–03 build foundational authentication and token knowledge. Labs 04–06 cover Spring Security and encryption. Labs 07–10 cover web security and advanced Spring Security. Labs 11–20 cover enterprise security topics including federation, secrets, containers, testing, zero trust, blockchain, supply chain, SIEM, and incident response.

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
