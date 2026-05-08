# 37 - Keycloak Learning Module

## Overview
Keycloak is an open-source identity and access management solution. This module covers OAuth2/OIDC integration with Spring Boot and Keycloak.

## Module Structure
- `keycloak-auth/` - Spring Security OAuth2 implementation

## Technology Stack
- Spring Boot 3.x
- Spring Security OAuth2 Resource Server
- Keycloak Admin API Client
- Maven

## Prerequisites
- Keycloak server running on `localhost:8180`
- Default realm: `master`
- Admin credentials: `admin`/`admin`

## Key Features
- OAuth2 Authorization Code flow
- OpenID Connect (OIDC)
- SAML 2.0 support
- Role-based access control (RBAC)
- User federation (LDAP, Active Directory)
- Social login (Google, GitHub, etc.)

## Build & Run
```bash
cd keycloak-auth
mvn clean install
mvn spring-boot:run
```

## Default Configuration
- Auth server URL: `http://localhost:8180`
- Realm: `learning-realm`
- Client ID: `spring-client`

## Related Modules
- 11-security (security patterns)
- 35-consul (service discovery)