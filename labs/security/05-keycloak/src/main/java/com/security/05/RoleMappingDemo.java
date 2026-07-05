package com.security05;

import java.util.*;

/**
 * Demonstrates Keycloak role mapping and permission resolution.
 * 
 * SECURITY CONCEPT: Keycloak provides flexible role management:
 * 
 * Realm Roles — global roles applicable across all clients
 *   e.g., "admin", "user", "manager"
 * 
 * Client Roles — roles scoped to a specific client application
 *   e.g., "order-manager" for the "orders-app" client
 * 
 * Composite Roles — roles that include other roles
 *   e.g., "super-admin" includes "admin" + "auditor"
 * 
 * Role mapping associates users with roles, which are then
 * translated into Spring Security GrantedAuthorities.
 */
public class RoleMappingDemo {

    enum RoleType { REALM_ROLE, CLIENT_ROLE }

    static class Role {
        final String name;
        final RoleType type;
        final String clientId;
        final Set<String> includes; // composite: included role names

        Role(String name, RoleType type) {
            this(name, type, null, Set.of());
        }

        Role(String name, RoleType type, String clientId, Set<String> includes) {
            this.name = name;
            this.type = type;
            this.clientId = clientId;
            this.includes = includes;
        }

        @Override
        public String toString() {
            String prefix = type == RoleType.REALM_ROLE ? "REALM:" : "CLIENT:" + clientId + ":";
            if (!includes.isEmpty()) {
                return prefix + name + " [includes=" + includes + "]";
            }
            return prefix + name;
        }
    }

    // Role definitions
    private final Map<String, Role> realmRoles = new LinkedHashMap<>();
    private final Map<String, Map<String, Role>> clientRoles = new LinkedHashMap<>();

    // User to role mapping
    private final Map<String, Set<String>> userRoleMappings = new LinkedHashMap<>();

    public RoleMappingDemo() {
        initRoles();
        initUsers();
    }

    private void initRoles() {
        // Realm roles
        realmRoles.put("user", new Role("user", RoleType.REALM_ROLE));
        realmRoles.put("admin", new Role("admin", RoleType.REALM_ROLE));
        realmRoles.put("manager", new Role("manager", RoleType.REALM_ROLE));
        // Composite role: super-admin includes admin + manager
        realmRoles.put("super-admin", new Role("super-admin", RoleType.REALM_ROLE,
                null, Set.of("admin", "manager")));

        // Client roles for "orders-app"
        clientRoles.put("orders-app", new LinkedHashMap<>());
        clientRoles.get("orders-app").put("order-viewer",
                new Role("order-viewer", RoleType.CLIENT_ROLE, "orders-app", Set.of()));
        clientRoles.get("orders-app").put("order-creator",
                new Role("order-creator", RoleType.CLIENT_ROLE, "orders-app", Set.of()));
        clientRoles.get("orders-app").put("order-admin",
                new Role("order-admin", RoleType.CLIENT_ROLE, "orders-app",
                        Set.of("order-viewer", "order-creator")));

        // Client roles for "reports-app"
        clientRoles.put("reports-app", new LinkedHashMap<>());
        clientRoles.get("reports-app").put("report-viewer",
                new Role("report-viewer", RoleType.CLIENT_ROLE, "reports-app", Set.of()));
        clientRoles.get("reports-app").put("report-exporter",
                new Role("report-exporter", RoleType.CLIENT_ROLE, "reports-app", Set.of()));
    }

    private void initUsers() {
        userRoleMappings.put("alice", Set.of("admin", "order-admin"));
        userRoleMappings.put("bob", Set.of("user", "order-viewer"));
        userRoleMappings.put("charlie", Set.of("super-admin", "report-exporter"));
    }

    /**
     * Resolves all roles for a user, expanding composite role includes.
     */
    public Set<String> resolveRoles(String username) {
        Set<String> assigned = userRoleMappings.get(username);
        if (assigned == null) return Set.of();

        Set<String> resolved = new LinkedHashSet<>(assigned);
        // Expand composite roles
        for (String roleName : assigned) {
            Role role = realmRoles.get(roleName);
            if (role != null) {
                resolved.addAll(role.includes);
            }
            // Check client roles
            for (Map.Entry<String, Map<String, Role>> entry : clientRoles.entrySet()) {
                Role clientRole = entry.getValue().get(roleName);
                if (clientRole != null) {
                    resolved.addAll(clientRole.includes);
                }
            }
        }
        return resolved;
    }

    /**
     * Returns roles scoped to a specific client (for token claims).
     */
    public Map<String, Set<String>> getClientScopedRoles(String username) {
        Set<String> userRoles = userRoleMappings.getOrDefault(username, Set.of());
        Map<String, Set<String>> scoped = new LinkedHashMap<>();

        for (Map.Entry<String, Map<String, Role>> clientEntry : clientRoles.entrySet()) {
            Set<String> matched = new LinkedHashSet<>();
            for (String userRole : userRoles) {
                if (clientEntry.getValue().containsKey(userRole)) {
                    matched.add(userRole);
                }
            }
            if (!matched.isEmpty()) {
                scoped.put(clientEntry.getKey(), matched);
            }
        }
        return scoped;
    }

    /**
     * Simulates the Keycloak role mapping in a JWT access token.
     */
    public String simulateTokenRoles(String username) {
        Set<String> roles = resolveRoles(username);
        Map<String, Set<String>> clientScoped = getClientScopedRoles(username);
        return "Roles for " + username + ": realm=" + roles
                + ", client_roles=" + clientScoped;
    }

    public static void main(String[] args) {
        RoleMappingDemo demo = new RoleMappingDemo();

        System.out.println("=== Keycloak Role Mapping ===\n");

        for (String user : List.of("alice", "bob", "charlie")) {
            System.out.println(user + ":");
            System.out.println("  Assigned roles: " + demo.userRoleMappings.get(user));
            System.out.println("  Resolved roles: " + demo.resolveRoles(user));
            System.out.println("  Client scoped:  " + demo.getClientScopedRoles(user));
            System.out.println();
        }

        System.out.println("--- Token Claims Simulation ---");
        System.out.println(demo.simulateTokenRoles("alice"));
        System.out.println(demo.simulateTokenRoles("charlie"));
    }
}
