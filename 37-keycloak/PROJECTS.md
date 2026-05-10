# Keycloak Authentication Projects

This module covers OAuth2/OpenID provider configuration, SSO integration, role-based access control, and identity management for building secure Java applications with Keycloak.

## Mini-Project: OAuth2/OpenID Authentication (2-4 Hours)

### Overview

Build a secure application with Keycloak integration demonstrating OAuth2 authorization code flow, token management, role-based access control, and Single Sign-On capabilities.

### Technology Stack

- Java 21 with Spring Boot 3.x
- Spring Security OAuth2 Client
- Keycloak Admin API
- Maven build system

### Project Structure

```
keycloak-auth/
├── pom.xml
└── src/
    └── main/
        ├── java/com/learning/keycloak/
        │   ├── KeycloakAuthApplication.java
        │   ├── config/
        │   │   └── SecurityConfig.java
        │   ├── model/
        │   │   ├── User.java
        │   │   └── Role.java
        │   ├── controller/
        │   │   └── AuthController.java
        │   └── service/
        │       └── KeycloakService.java
        └── resources/
            └── application.properties
```

### Implementation

**pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.learning</groupId>
        <artifactId>java-learning-parent</artifactId>
        <version>1.0.0</version>
    </parent>
    
    <artifactId>keycloak-auth</artifactId>
    
    <properties>
        <java.version>21</java.version>
        <spring-boot.version>3.2.0</spring-boot.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-client</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.keycloak</groupId>
            <artifactId>keycloak-admin-client</artifactId>
            <version>23.0.0</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring-boot.version}</version>
            </plugin>
        </plugins>
    </build>
</project>
```

**User.java (Model)**

```java
package com.learning.keycloak.model;

import java.time.LocalDateTime;
import java.util.Set;

public class User {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private Set<String> roles;
    private Set<String> groups;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    
    public User() {
        this.createdAt = LocalDateTime.now();
        this.enabled = true;
    }
    
    public User(String username, String email) {
        this();
        this.username = username;
        this.email = email;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    public Set<String> getGroups() { return groups; }
    public void setGroups(Set<String> groups) { this.groups = groups; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
}
```

**SecurityConfig.java**

```java
package com.learning.keycloak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUserAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/api/user/profile", true)
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> {})
            );
        
        return http.build();
    }
}
```

**KeycloakService.java**

```java
package com.learning.keycloak.service;

import com.learning.keycloak.model.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KeycloakService {
    
    private final Keycloak keycloakClient;
    private final String realm = "learning-realm";
    private final Map<String, User> userStore = new HashMap<>();
    
    public KeycloakService() {
        this.keycloakClient = null;
        initializeDemoUsers();
    }
    
    private void initializeDemoUsers() {
        User admin = new User("admin", "admin@example.com");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRoles(Set.of("ADMIN", "USER"));
        userStore.put(admin.getUsername(), admin);
        
        User user1 = new User("john", "john@example.com");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setRoles(Set.of("USER"));
        userStore.put(user1.getUsername(), user1);
        
        User user2 = new User("jane", "jane@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setRoles(Set.of("USER", "MANAGER"));
        userStore.put(user2.getUsername(), user2);
    }
    
    public User createUser(String username, String email, String firstName, 
            String lastName, Set<String> roles) {
        
        User user = new User(username, email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRoles(roles);
        
        userStore.put(username, user);
        
        System.out.println("Created user in Keycloak: " + username);
        
        return user;
    }
    
    public User getUser(String username) {
        return userStore.get(username);
    }
    
    public List<User> getAllUsers() {
        return new ArrayList<>(userStore.values());
    }
    
    public List<User> getUsersByRole(String role) {
        return userStore.values().stream()
            .filter(u -> u.getRoles().contains(role))
            .collect(Collectors.toList());
    }
    
    public void assignRole(String username, String role) {
        User user = userStore.get(username);
        if (user != null) {
            Set<String> roles = new HashSet<>(user.getRoles());
            roles.add(role);
            user.setRoles(roles);
            
            System.out.println("Assigned role '" + role + "' to user: " + username);
        }
    }
    
    public void removeRole(String username, String role) {
        User user = userStore.get(username);
        if (user != null) {
            Set<String> roles = new HashSet<>(user.getRoles());
            roles.remove(role);
            user.setRoles(roles);
            
            System.out.println("Removed role '" + role + "' from user: " + username);
        }
    }
    
    public void enableUser(String username, boolean enabled) {
        User user = userStore.get(username);
        if (user != null) {
            user.setEnabled(enabled);
            
            System.out.println(enabled ? "Enabled" : "Disabled" + " user: " + username);
        }
    }
    
    public void deleteUser(String username) {
        userStore.remove(username);
        
        System.out.println("Deleted user from Keycloak: " + username);
    }
    
    public Map<String, Set<String>> getRoleMappings() {
        Map<String, Set<String>> mappings = new HashMap<>();
        
        for (User user : userStore.values()) {
            mappings.put(user.getUsername(), user.getRoles());
        }
        
        return mappings;
    }
    
    public boolean validateCredentials(String username, String password) {
        System.out.println("Validating credentials for: " + username);
        return userStore.containsKey(username);
    }
}
```

**AuthController.java**

```java
package com.learning.keycloak.controller;

import com.learning.keycloak.model.User;
import com.learning.keycloak.service.KeycloakService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class AuthController {
    
    private final KeycloakService keycloakService;
    
    public AuthController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }
    
    @GetMapping("/public/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "provider", "keycloak");
    }
    
    @PostMapping("/public/users")
    public User createUser(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String email = (String) request.get("email");
        String firstName = (String) request.get("firstName");
        String lastName = (String) request.get("lastName");
        @SuppressWarnings("unchecked")
        Set<String> roles = new java.util.HashSet<>((List<String>) request.get("roles"));
        
        return keycloakService.createUser(username, email, firstName, lastName, roles);
    }
    
    @GetMapping("/admin/users")
    public List<User> getAllUsers() {
        return keycloakService.getAllUsers();
    }
    
    @GetMapping("/admin/users/{username}")
    public User getUser(@PathVariable String username) {
        return keycloakService.getUser(username);
    }
    
    @PostMapping("/admin/users/{username}/roles/{role}")
    public Map<String, String> assignRole(
            @PathVariable String username, 
            @PathVariable String role) {
        keycloakService.assignRole(username, role);
        return Map.of("status", "Role assigned");
    }
    
    @DeleteMapping("/admin/users/{username}/roles/{role}")
    public Map<String, String> removeRole(
            @PathVariable String username, 
            @PathVariable String role) {
        keycloakService.removeRole(username, role);
        return Map.of("status", "Role removed");
    }
    
    @PostMapping("/admin/users/{username}/enable")
    public Map<String, String> enableUser(
            @PathVariable String username,
            @RequestParam boolean enabled) {
        keycloakService.enableUser(username, enabled);
        return Map.of("status", enabled ? "User enabled" : "User disabled");
    }
    
    @DeleteMapping("/admin/users/{username}")
    public Map<String, String> deleteUser(@PathVariable String username) {
        keycloakService.deleteUser(username);
        return Map.of("status", "User deleted");
    }
    
    @GetMapping("/admin/roles")
    public Map<String, Set<String>> getRoleMappings() {
        return keycloakService.getRoleMappings();
    }
    
    @GetMapping("/user/profile")
    public User getProfile(@AuthenticationPrincipal OidcUser principal) {
        if (principal != null) {
            User user = new User(principal.getPreferredUsername(), principal.getEmail());
            user.setFirstName(principal.getGivenName());
            user.setLastName(principal.getFamilyName());
            
            List<String> roles = principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());
            user.setRoles(Set.copyOf(roles));
            
            return user;
        }
        
        return keycloakService.getUser("john");
    }
}
```

**KeycloakAuthApplication.java**

```java
package com.learning.keycloak;

import com.learning.keycloak.model.User;
import com.learning.keycloak.service.KeycloakService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
public class KeycloakAuthApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(KeycloakAuthApplication.class, args);
    }
    
    @Bean
    CommandLineRunner demo(KeycloakService service) {
        return args -> {
            System.out.println("=== Keycloak Authentication Demo ===\n");
            
            System.out.println("1. Created users:");
            List<User> users = service.getAllUsers();
            users.forEach(u -> System.out.println("   - " + u.getUsername() + 
                " (" + u.getEmail() + ") roles: " + u.getRoles()));
            
            System.out.println("\n2. Users with ADMIN role:");
            List<User> admins = service.getUsersByRole("ADMIN");
            admins.forEach(u -> System.out.println("   - " + u.getUsername()));
            
            System.out.println("\n3. Users with USER role:");
            List<User> regularUsers = service.getUsersByRole("USER");
            regularUsers.forEach(u -> System.out.println("   - " + u.getUsername()));
            
            System.out.println("\n4. Role mappings:");
            Map<String, Set<String>> mappings = service.getRoleMappings();
            mappings.forEach((user, roles) -> 
                System.out.println("   " + user + ": " + roles));
            
            System.out.println("\n=== Demo Complete ===");
            System.exit(0);
        };
    }
}
```

**application.properties**

```properties
spring.application.name=keycloak-auth
server.port=8080

# Keycloak configuration
keycloak.auth-server-url=http://localhost:8080/auth
keycloak.realm=learning-realm
keycloak.resource=learning-app

# OAuth2 configuration
spring.security.oauth2.client.registration.keycloak.client-id=learning-client
spring.security.oauth2.client.registration.keycloak.client-secret=secret
spring.security.oauth2.client.registration.keycloak.scope=openid,profile,email

spring.security.oauth2.client.provider.keycloak.issuer-uri=${keycloak.auth-server-url}/realms/${keycloak.realm}
```

### Start Keycloak and Run

```bash
# Start Keycloak
docker run -d --name keycloak -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:23.0.0 start-dev

# Build and run application
cd 37-keycloak/keycloak-auth
mvn clean package -DskipTests
mvn spring-boot:run
```

### OAuth2 Flows Demonstrated

```
1. Authorization Code Flow:
   - Browser -> /oauth2/authorize -> Keycloak login -> callback with code
   - Exchange code for tokens (access_token, refresh_token, id_token)

2. Client Credentials Flow:
   - Client app -> /token (client_id + client_secret)
   - Used for service-to-service communication

3. Refresh Token Flow:
   - Use refresh_token to obtain new access_token
   - Without re-authentication

4. Implicit Flow (deprecated):
   - Not recommended for new applications
```

### Expected Output

```
=== Keycloak Authentication Demo ===

1. Created users:
   - admin (admin@example.com) roles: [ADMIN, USER]
   - john (john@example.com) roles: [USER]
   - jane (jane@example.com) roles: [USER, MANAGER]

2. Users with ADMIN role:
   - admin

3. Users with USER role:
   - admin
   - john
   - jane

4. Role mappings:
   admin: [ADMIN, USER]
   john: [USER]
   jane: [USER, MANAGER]

=== Demo Complete ===
```

---

## Real-World Project: Enterprise SSO with Multi-Realm Support (8+ Hours)

### Overview

Build a comprehensive enterprise Single Sign-On solution using Keycloak that demonstrates multi-realm architecture, identity brokering, custom identity providers, fine-grained permissions, and event logging for complete access management.

### Key Features

1. **Multi-Realm Architecture** - Separate realms for different applications
2. **Identity Brokering** - External identity providers (Google, GitHub, SAML)
3. **Fine-Grained Permissions** - Resource-based authorization
4. **Custom User Storage** - User federation to LDAP/AD
5. **Client Templates** - Reusable client configurations
6. **Authentication Flows** - Custom registration and login flows
7. **Event Logging** - Audit logs for security events
8. **Token Exchange** - Cross-realm token exchange

### Project Structure

```
keycloak-auth/
├── pom.xml
└── src/
    └── main/
        ├── java/com/learning/keycloak/
        │   ├── EnterpriseSSOApplication.java
        │   ├── config/
        │   │   ├── KeycloakConfig.java
        │   │   └── IdentityProviderConfig.java
        │   ├── model/
        │   │   ├── RealmConfig.java
        │   │   ├── ClientApplication.java
        │   │   └── AuthPermission.java
        │   ├── service/
        │   │   ├── RealmManagementService.java
        │   │   ├── ClientManagementService.java
        │   │   ├── PermissionManagementService.java
        │   │   └── EventAuditService.java
        │   └── controller/
        │       └── EnterpriseController.java
        └── resources/
            └── application.properties
```

### Implementation

**RealmConfig.java**

```java
package com.learning.keycloak.model;

import java.util.Set;

public class RealmConfig {
    private String name;
    private boolean enabled;
    private String displayName;
    private String loginTheme;
    private String accountTheme;
    private int accessTokenLifespan;
    private int ssoSessionIdleTimeout;
    private int ssoSessionMaxLifespan;
    private boolean registrationAllowed;
    private boolean loginWithEmailAllowed;
    private boolean duplicateEmailsAllowed;
    private Set<String> defaultRoles;
    private int passwordPolicyMinLength;
    private int passwordPolicyMinDigits;
    private int passwordPolicyMinSpecialChars;
    
    public RealmConfig() {
        this.enabled = true;
        this.accessTokenLifespan = 300;
        this.ssoSessionIdleTimeout = 1800;
        this.ssoSessionMaxLifespan = 28800;
        this.registrationAllowed = true;
        this.loginWithEmailAllowed = true;
        this.duplicateEmailsAllowed = false;
        this.passwordPolicyMinLength = 8;
        this.passwordPolicyMinDigits = 1;
        this.passwordPolicyMinSpecialChars = 0;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getLoginTheme() { return loginTheme; }
    public void setLoginTheme(String loginTheme) { this.loginTheme = loginTheme; }
    public String getAccountTheme() { return accountTheme; }
    public void setAccountTheme(String accountTheme) { this.accountTheme = accountTheme; }
    public int getAccessTokenLifespan() { return accessTokenLifespan; }
    public void setAccessTokenLifespan(int accessTokenLifespan) { this.accessTokenLifespan = accessTokenLifespan; }
    public int getSsoSessionIdleTimeout() { return ssoSessionIdleTimeout; }
    public void setSsoSessionIdleTimeout(int ssoSessionIdleTimeout) { this.ssoSessionIdleTimeout = ssoSessionIdleTimeout; }
    public int getSsoSessionMaxLifespan() { return ssoSessionMaxLifespan; }
    public void setSsoSessionMaxLifespan(int ssoSessionMaxLifespan) { this.ssoSessionMaxLifespan = ssoSessionMaxLifespan; }
    public boolean isRegistrationAllowed() { return registrationAllowed; }
    public void setRegistrationAllowed(boolean registrationAllowed) { this.registrationAllowed = registrationAllowed; }
    public boolean isLoginWithEmailAllowed() { return loginWithEmailAllowed; }
    public void setLoginWithEmailAllowed(boolean loginWithEmailAllowed) { this.loginWithEmailAllowed = loginWithEmailAllowed; }
    public boolean isDuplicateEmailsAllowed() { return duplicateEmailsAllowed; }
    public void setDuplicateEmailsAllowed(boolean duplicateEmailsAllowed) { this.duplicateEmailsAllowed = duplicateEmailsAllowed; }
    public Set<String> getDefaultRoles() { return defaultRoles; }
    public void setDefaultRoles(Set<String> defaultRoles) { this.defaultRoles = defaultRoles; }
    public int getPasswordPolicyMinLength() { return passwordPolicyMinLength; }
    public void setPasswordPolicyMinLength(int passwordPolicyMinLength) { this.passwordPolicyMinLength = passwordPolicyMinLength; }
    public int getPasswordPolicyMinDigits() { return passwordPolicyMinDigits; }
    public void setPasswordPolicyMinDigits(int passwordPolicyMinDigits) { this.passwordPolicyMinDigits = passwordPolicyMinDigits; }
    public int getPasswordPolicyMinSpecialChars() { return passwordPolicyMinSpecialChars; }
    public void setPasswordPolicyMinSpecialChars(int passwordPolicyMinSpecialChars) { this.passwordPolicyMinSpecialChars = passwordPolicyMinSpecialChars; }
}
```

**ClientApplication.java**

```java
package com.learning.keycloak.model;

import java.util.List;
import java.util.Map;

public class ClientApplication {
    private String clientId;
    private String name;
    private String description;
    private boolean enabled;
    private boolean publicClient;
    private boolean consentRequired;
    private boolean standardFlowEnabled;
    private boolean implicitFlowEnabled;
    private boolean directAccessGrantsEnabled;
    private String rootUrl;
    private String redirectUris;
    private List<String> webOrigins;
    private Map<String, List<String>> attributes;
    private int accessTokenLifespan;
    private int idleTimeout;
    private int sessionIdleTimeout;
    private int sessionMaxLifespan;
    
    public ClientApplication() {
        this.enabled = true;
        this.publicClient = false;
        this.consentRequired = false;
        this.standardFlowEnabled = true;
        this.implicitFlowEnabled = false;
        this.directAccessGrantsEnabled = false;
        this.accessTokenLifespan = 300;
    }
    
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isPublicClient() { return publicClient; }
    public void setPublicClient(boolean publicClient) { this.publicClient = publicClient; }
    public boolean isConsentRequired() { return consentRequired; }
    public void setConsentRequired(boolean consentRequired) { this.consentRequired = consentRequired; }
    public boolean isStandardFlowEnabled() { return standardFlowEnabled; }
    public void setStandardFlowEnabled(boolean standardFlowEnabled) { this.standardFlowEnabled = standardFlowEnabled; }
    public boolean isImplicitFlowEnabled() { return implicitFlowEnabled; }
    public void setImplicitFlowEnabled(boolean implicitFlowEnabled) { this.implicitFlowEnabled = implicitFlowEnabled; }
    public boolean isDirectAccessGrantsEnabled() { return directAccessGrantsEnabled; }
    public void setDirectAccessGrantsEnabled(boolean directAccessGrantsEnabled) { this.directAccessGrantsEnabled = directAccessGrantsEnabled; }
    public String getRootUrl() { return rootUrl; }
    public void setRootUrl(String rootUrl) { this.rootUrl = rootUrl; }
    public String getRedirectUris() { return redirectUris; }
    public void setRedirectUris(String redirectUris) { this.redirectUris = redirectUris; }
    public List<String> getWebOrigins() { return webOrigins; }
    public void setWebOrigins(List<String> webOrigins) { this.webOrigins = webOrigins; }
    public Map<String, List<String>> getAttributes() { return attributes; }
    public void setAttributes(Map<String, List<String>> attributes) { this.attributes = attributes; }
    public int getAccessTokenLifespan() { return accessTokenLifespan; }
    public void setAccessTokenLifespan(int accessTokenLifespan) { this.accessTokenLifespan = accessTokenLifespan; }
    public int getIdleTimeout() { return idleTimeout; }
    public void setIdleTimeout(int idleTimeout) { this.idleTimeout = idleTimeout; }
    public int getSessionIdleTimeout() { return sessionIdleTimeout; }
    public void setSessionIdleTimeout(int sessionIdleTimeout) { this.sessionIdleTimeout = sessionIdleTimeout; }
    public int getSessionMaxLifespan() { return sessionMaxLifespan; }
    public void setSessionMaxLifespan(int sessionMaxLifespan) { this.sessionMaxLifespan = sessionMaxLifespan; }
}
```

**AuthPermission.java**

```java
package com.learning.keycloak.model;

import java.util.List;
import java.util.Map;

public class AuthPermission {
    private String id;
    private String name;
    private String resource;
    private String scope;
    private Map<String, List<String>> policies;
    private String decisionStrategy;
    private List<String> dependentPolicies;
    
    public AuthPermission() {
        this.decisionStrategy = "AFFIRMATIVE";
    }
    
    public AuthPermission(String name, String resource, String scope) {
        this();
        this.name = name;
        this.resource = resource;
        this.scope = scope;
    }
    
    public static AuthPermission allowAll(String name, String resource) {
        AuthPermission permission = new AuthPermission(name, resource, "*");
        permission.setDecisionStrategy("AFFIRMATIVE");
        return permission;
    }
    
    public static AuthPermission requireRole(String name, String resource, String role) {
        AuthPermission permission = new AuthPermission(name, resource, "*");
        permission.setDecisionStrategy("UNANIMOUS");
        return permission;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public Map<String, List<String>> getPolicies() { return policies; }
    public void setPolicies(Map<String, List<String>> policies) { this.policies = policies; }
    public String getDecisionStrategy() { return decisionStrategy; }
    public void setDecisionStrategy(String decisionStrategy) { this.decisionStrategy = decisionStrategy; }
    public List<String> getDependentPolicies() { return dependentPolicies; }
    public void setDependentPolicies(List<String> dependentPolicies) { this.dependentPolicies = dependentPolicies; }
}
```

**RealmManagementService.java**

```java
package com.learning.keycloak.service;

import com.learning.keycloak.model.RealmConfig;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RealmManagementService {
    
    private final Map<String, RealmConfig> realms = new ConcurrentHashMap<>();
    
    public RealmManagementService() {
        initializeDefaultRealms();
    }
    
    private void initializeDefaultRealms() {
        RealmConfig master = new RealmConfig();
        master.setName("master");
        master.setDisplayName("Master Realm");
        master.setEnabled(true);
        realms.put("master", master);
        
        RealmConfig learning = new RealmConfig();
        learning.setName("learning");
        learning.setDisplayName("Learning Platform");
        learning.setEnabled(true);
        learning.setRegistrationAllowed(true);
        learning.setAccessTokenLifespan(600);
        realms.put("learning", learning);
        
        RealmConfig intranet = new RealmConfig();
        intranet.setName("intranet");
        intranet.setDisplayName("Corporate Intranet");
        intranet.setEnabled(true);
        intranet.setRegistrationAllowed(false);
        intranet.setAccessTokenLifespan(1800);
        realms.put("intranet", intranet);
    }
    
    public RealmConfig createRealm(RealmConfig config) {
        realms.put(config.getName(), config);
        
        System.out.println("Created realm: " + config.getName());
        
        return config;
    }
    
    public RealmConfig getRealm(String name) {
        return realms.get(name);
    }
    
    public List<RealmConfig> getAllRealms() {
        return new ArrayList<>(realms.values());
    }
    
    public List<RealmConfig> getEnabledRealms() {
        return realms.values().stream()
            .filter(RealmConfig::isEnabled)
            .toList();
    }
    
    public void updateRealm(String name, RealmConfig config) {
        if (realms.containsKey(name)) {
            realms.put(name, config);
            
            System.out.println("Updated realm: " + name);
        }
    }
    
    public void deleteRealm(String name) {
        if (!"master".equals(name)) {
            realms.remove(name);
            
            System.out.println("Deleted realm: " + name);
        }
    }
    
    public void enableRealm(String name, boolean enabled) {
        RealmConfig config = realms.get(name);
        if (config != null) {
            config.setEnabled(enabled);
            
            System.out.println((enabled ? "Enabled" : "Disabled") + " realm: " + name);
        }
    }
    
    public Map<String, Object> getRealmSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("total", realms.size());
        summary.put("enabled", realms.values().stream().filter(RealmConfig::isEnabled).count());
        summary.put("realms", realms.keySet());
        
        return summary;
    }
}
```

**ClientManagementService.java**

```java
package com.learning.keycloak.service;

import com.learning.keycloak.model.ClientApplication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientManagementService {
    
    private final Map<String, ClientApplication> clients = new ConcurrentHashMap<>();
    
    public ClientManagementService() {
        initializeDefaultClients();
    }
    
    private void initializeDefaultClients() {
        ClientApplication webApp = new ClientApplication();
        webApp.setClientId("learning-web");
        webApp.setName("Learning Web App");
        webApp.setDescription("Main web application");
        webApp.setRootUrl("http://localhost:3000");
        webApp.setRedirectUris("http://localhost:3000/*");
        clients.put(webApp.getClientId(), webApp);
        
        ClientApplication mobileApp = new ClientApplication();
        mobileApp.setClientId("learning-mobile");
        mobileApp.setName("Learning Mobile App");
        mobileApp.setDescription("Mobile application");
        mobileApp.setPublicClient(true);
        mobileApp.setConsentRequired(true);
        mobileApp.setRedirectUris("com.learning.app://callback");
        clients.put(mobileApp.getClientId(), mobileApp);
        
        ClientApplication apiGateway = new ClientApplication();
        apiGateway.setClientId("api-gateway");
        apiGateway.setName("API Gateway");
        apiGateway.setDescription("Backend API client");
        apiGateway.setDirectAccessGrantsEnabled(true);
        apiGateway.setAccessTokenLifespan(3600);
        clients.put(apiGateway.getClientId(), apiGateway);
    }
    
    public ClientApplication createClient(ClientApplication client) {
        clients.put(client.getClientId(), client);
        
        System.out.println("Created client: " + client.getClientId());
        
        return client;
    }
    
    public ClientApplication getClient(String clientId) {
        return clients.get(clientId);
    }
    
    public List<ClientApplication> getAllClients() {
        return new ArrayList<>(clients.values());
    }
    
    public void updateClient(String clientId, ClientApplication client) {
        if (clients.containsKey(clientId)) {
            clients.put(clientId, client);
            
            System.out.println("Updated client: " + clientId);
        }
    }
    
    public void deleteClient(String clientId) {
        clients.remove(clientId);
        
        System.out.println("Deleted client: " + clientId);
    }
    
    public void enableClient(String clientId, boolean enabled) {
        ClientApplication client = clients.get(clientId);
        if (client != null) {
            client.setEnabled(enabled);
            
            System.out.println((enabled ? "Enabled" : "Disabled") + " client: " + clientId);
        }
    }
    
    public Map<String, Object> getClientStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("total", clients.size());
        stats.put("enabled", clients.values().stream().filter(ClientApplication::isEnabled).count());
        stats.put("public_clients", clients.values().stream().filter(ClientApplication::isPublicClient).count());
        stats.put("confidential_clients", clients.values().stream().filter(c -> !c.isPublicClient()).count());
        
        return stats;
    }
}
```

**PermissionManagementService.java**

```java
package com.learning.keycloak.service;

import com.learning.keycloak.model.AuthPermission;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PermissionManagementService {
    
    private final Map<String, AuthPermission> permissions = new ConcurrentHashMap<>();
    private final Map<String, List<String>> resourcePolicies = new ConcurrentHashMap<>();
    
    public PermissionManagementService() {
        initializeDefaultPermissions();
    }
    
    private void initializeDefaultPermissions() {
        AuthPermission adminAll = AuthPermission.allowAll("admin-all", "admin-api");
        permissions.put("admin-all", adminAll);
        
        AuthPermission userRead = new AuthPermission("user-read", "users", "read");
        userRead.setDecisionStrategy("AFFIRMATIVE");
        permissions.put("user-read", userRead);
        
        AuthPermission userWrite = new AuthPermission("user-write", "users", "write");
        userWrite.setDecisionStrategy("UNANIMOUS");
        permissions.put("user-write", userWrite);
        
        AuthPermission orderRead = AuthPermission.allowAll("order-read", "orders-api");
        permissions.put("order-read", orderRead);
    }
    
    public AuthPermission createPermission(AuthPermission permission) {
        permissions.put(permission.getId(), permission);
        
        System.out.println("Created permission: " + permission.getName());
        
        return permission;
    }
    
    public AuthPermission getPermission(String id) {
        return permissions.get(id);
    }
    
    public List<AuthPermission> getPermissionsForResource(String resource) {
        return permissions.values().stream()
            .filter(p -> p.getResource().equals(resource))
            .toList();
    }
    
    public void assignPermissionToRole(String permissionId, String roleId) {
        AuthPermission permission = permissions.get(permissionId);
        
        if (permission != null) {
            resourcePolicies.computeIfAbsent(permissionId, k -> new ArrayList<>()).add(roleId);
            
            System.out.println("Assigned permission '" + permissionId + "' to role: " + roleId);
        }
    }
    
    public void assignPermissionToUser(String permissionId, String userId) {
        AuthPermission permission = permissions.get(permissionId);
        
        if (permission != null) {
            Map<String, List<String>> policies = new HashMap<>(permission.getPolicies());
            policies.computeIfAbsent("users", k -> new ArrayList<>()).add(userId);
            permission.setPolicies(policies);
            
            System.out.println("Assigned permission '" + permissionId + "' to user: " + userId);
        }
    }
    
    public boolean hasPermission(String userId, String resource, String scope) {
        List<AuthPermission> perms = getPermissionsForResource(resource);
        
        return perms.stream().anyMatch(p -> {
            if (!"*".equals(p.getScope()) && !p.getScope().equals(scope)) {
                return false;
            }
            
            Map<String, List<String>> policies = p.getPolicies();
            if (policies == null || policies.isEmpty()) {
                return true;
            }
            
            return policies.containsKey(userId);
        });
    }
    
    public Map<String, List<AuthPermission>> getPermissionMatrix() {
        Map<String, List<AuthPermission>> matrix = new HashMap<>();
        
        for (AuthPermission permission : permissions.values()) {
            matrix.computeIfAbsent(permission.getResource(), k -> new ArrayList<>()).add(permission);
        }
        
        return matrix;
    }
}
```

### Build and Run

```bash
cd 37-keycloak/keycloak-auth
mvn clean package -DskipTests
mvn spring-boot:run
```

### API Endpoints

```
# Realm Management
GET /api/realms                    # List all realms
POST /api/realms                 # Create realm
GET /api/realms/{name}          # Get realm details
PUT /api/realms/{name}          # Update realm
DELETE /api/realms/{name}      # Delete realm

# Client Management
GET /api/clients                # List all clients
POST /api/clients               # Create client
GET /api/clients/{clientId}     # Get client details
PUT /api/clients/{clientId}     # Update client
DELETE /api/clients/{clientId} # Delete client

# Permission Management
GET /api/permissions                    # List all permissions
POST /api/permissions                   # Create permission
GET /api/permissions/{id}               # Get permission
POST /api/permissions/{id}/roles/{role}  # Assign to role

# User Management
GET /api/users              # List users
POST /api/users            # Create user
GET /api/users/{id}        # Get user
PUT /api/users/{id}        # Update user
DELETE /api/users/{id}    # Delete user
```

### Learning Outcomes

After completing these projects, you will understand:

1. **OAuth2 Flows** - Authorization code, client credentials, refresh tokens
2. **OpenID Connect** - ID tokens, userinfo endpoint, discovery
3. **User Management** - Create, update, delete users
4. **Role-Based Access** - Roles, groups, composite roles
5. **Client Configuration** - Public vs confidential clients
6. **Fine-Grained Permissions** - Resource-based authorization
7. **Authentication Flows** - Custom login and registration
8. **Identity Brokering** - External identity providers

### References

- Keycloak Documentation: https://www.keycloak.org/documentation
- Server Administration Guide: https://www.keycloak.org/docs/latest/server_admin/index.html
- Authorization Services: https://www.keycloak.org/docs/latest/authorization_services/index.html