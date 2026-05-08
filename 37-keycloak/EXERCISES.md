# Exercises - Keycloak

## Exercise 1: OAuth2 Resource Server
Implement secure API with Keycloak:

1. Configure Spring Boot as OAuth2 resource server
2. Validate JWT tokens from Keycloak
3. Protect endpoints with `@PreAuthorize`
4. Extract user info from token claims

## Exercise 2: Client Credentials Flow
Implement machine-to-machine authentication:

1. Register service account client in Keycloak
2. Obtain access token using client credentials
3. Use token to call protected service
4. Handle token expiration and refresh

## Exercise 3: Role-Based Access Control
Implement RBAC with Keycloak:

1. Create roles (admin, user, moderator) in Keycloak
2. Assign roles to users
3. Configure role-based endpoint access
4. Test with different user accounts

## Exercise 4: User Management API
Manage users programmatically:

1. Use Keycloak Admin API to create users
2. Add custom attributes to user profiles
3. Implement user search and listing
4. Handle password reset and email verification

## Exercise 5: Multi-Realm Support
Implement multi-tenant security:

1. Configure multiple realms (dev, staging, prod)
2. Dynamically select realm based on request
3. Implement realm-specific validation
4. Handle cross-realm token validation

## Bonus Challenge
Implement a custom Keycloak extension (SPI) that validates corporate email domains during registration and automatically assigns users to appropriate departments.