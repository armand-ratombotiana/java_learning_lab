# Keycloak Solution

## Overview
This module covers OAuth2, SSO, and realm configuration with Keycloak.

## Key Features

### OAuth2 & OpenID Connect
- Token management
- Authorization code flow
- Client credentials flow

### SSO Configuration
- Identity providers
- Authentication flows
- Session management

### Realm Management
- Creating realms
- Client configuration
- User management

## Usage

```java
Keycloak keycloak = Keycloak.getInstance(serverUrl, realm, clientId, username, password);
KeycloakSolution solution = new KeycloakSolution(keycloak);

// Create realm
solution.createRealm("my-realm");

// Create client
solution.createClient("my-realm", clientRep);

// Create user
solution.createUser("my-realm", "user", "user@test.com", "password");

// Get users
List<UserRepresentation> users = solution.getUsers("my-realm");
```

## Dependencies
- Keycloak Admin Client
- JUnit 5 for testing
- Mockito for mocking