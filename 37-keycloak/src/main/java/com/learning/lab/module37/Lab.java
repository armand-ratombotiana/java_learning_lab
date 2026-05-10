package com.learning.lab.module37;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.*;

import java.util.*;
import java.util.stream.Collectors;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 37: Keycloak Lab ===\n");

        System.out.println("1. OAuth2 Configuration:");
        System.out.println("   - Authorization Endpoint: http://localhost:8080/auth/realms/{realm}/protocol/openid-connect/auth");
        System.out.println("   - Token Endpoint: http://localhost:8080/auth/realms/{realm}/protocol/openid-connect/token");
        System.out.println("   - Userinfo Endpoint: http://localhost:8080/auth/realms/{realm}/protocol/openid-connect/userinfo");
        System.out.println("   - Logout Endpoint: http://localhost:8080/auth/realms/{realm}/protocol/openid-connect/logout");

        System.out.println("\n2. SSO Configuration:");
        System.out.println("   - Grant Type: authorization_code");
        System.out.println("   - Response Type: code");
        System.out.println("   - Scopes: openid, profile, email");
        System.out.println("   - PKCE: S256");

        System.out.println("\n3. Realm Configuration:");
        realmConfigurationDemo();

        System.out.println("\n4. Client Configuration:");
        clientConfigurationDemo();

        System.out.println("\n5. User Management:");
        userManagementDemo();

        System.out.println("\n6. Role-Based Access Control:");
        roleBasedAccessDemo();

        System.out.println("\n=== Keycloak Lab Complete ===");
    }

    static void realmConfigurationDemo() {
        System.out.println("   Realm Settings:");
        System.out.println("   - Realm Name: my-realm");
        System.out.println("   - Enabled: true");
        System.out.println("   - Registration Allowed: false");
        System.out.println("   - Login With Email: true");
        System.out.println("   - Duplicate Emails: false");
        System.out.println("   - Password Policy: length(8) and digits(1) and specialChars(1)");
        System.out.println("   - Access Token Lifespan: 300 seconds");
        System.out.println("   - SSO Session Lifespan: 3600 seconds");
    }

    static void clientConfigurationDemo() {
        System.out.println("   Client Settings:");
        System.out.println("   - Client ID: my-app");
        System.out.println("   - Client Protocol: openid-connect");
        System.out.println("   - Access Type: confidential");
        System.out.println("   - Valid Redirect URIs: http://localhost:8080/*");
        System.out.println("   - Web Origins: http://localhost:8080");
        System.out.println("   - Consent Required: true");
        System.out.println("   - Service Accounts Enabled: true");
    }

    static void userManagementDemo() {
        System.out.println("   User Operations:");
        System.out.println("   - Create User: POST /admin/realms/{realm}/users");
        System.out.println("   - Get Users: GET /admin/realms/{realm}/users");
        System.out.println("   - Update User: PUT /admin/realms/{realm}/users/{id}");
        System.out.println("   - Delete User: DELETE /admin/realms/{realm}/users/{id}");
        System.out.println("   - Reset Password: PUT /admin/realms/{realm}/users/{id}/reset-password");
        System.out.println("   - Get User Sessions: GET /admin/realms/{realm}/users/{id}/sessions");
    }

    static void roleBasedAccessDemo() {
        System.out.println("   Role Management:");
        System.out.println("   - Realm Roles: admin, user, guest");
        System.out.println("   - Client Roles: my-app/viewer, my-app/editor, my-app/admin");
        System.out.println("   - Composite Roles: combining multiple roles");
        System.out.println("   - Role Mappings: assigned to users");
        System.out.println("   - Permission Evaluation: based on roles and attributes");
    }
}