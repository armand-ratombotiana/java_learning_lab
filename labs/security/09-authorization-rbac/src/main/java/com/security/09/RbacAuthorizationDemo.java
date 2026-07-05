package com.security09;

import java.util.*;

/**
 * Demonstrates Role-Based Access Control (RBAC) authorization.
 * 
 * SECURITY CONCEPT: RBAC assigns permissions to users based on
 * their roles within an organization. Compared to ACLs (per-user
 * permissions), RBAC is more scalable and manageable.
 * 
 * RBAC principles:
 * - Role assignment: users are assigned roles
 * - Role authorization: roles are granted permissions
 * - Permission authorization: users exercise permissions via roles
 * - Separation of duties: conflicting roles cannot be held simultaneously
 * - Least privilege: users get minimum roles needed
 * 
 * Common RBAC patterns:
 * - Flat RBAC: simple user ↔ role mapping
 * - Hierarchical RBAC: role hierarchies (senior roles inherit from junior)
 * - Constrained RBAC: separation of duties constraints
 * - Symmetric RBAC: permission ↔ role ↔ user relationships
 */
public class RbacAuthorizationDemo {

    // Permission definitions
    public enum Permission {
        READ_USER, CREATE_USER, UPDATE_USER, DELETE_USER,
        READ_REPORT, CREATE_REPORT, EXPORT_REPORT,
        MANAGE_ROLES, MANAGE_SYSTEM, VIEW_AUDIT_LOG
    }

    // Role definitions with assigned permissions
    private final Map<String, Set<Permission>> rolePermissions = new LinkedHashMap<>();
    // Role hierarchy (senior → inherits from junior)
    private final Map<String, Set<String>> roleHierarchy = new LinkedHashMap<>();
    // User to role assignments
    private final Map<String, Set<String>> userRoles = new LinkedHashMap<>();

    public RbacAuthorizationDemo() {
        initRoles();
        initUsers();
    }

    private void initRoles() {
        // Viewer role — read-only
        rolePermissions.put("ROLE_VIEWER", Set.of(
                Permission.READ_USER, Permission.READ_REPORT, Permission.VIEW_AUDIT_LOG));

        // Operator role — can create and update
        rolePermissions.put("ROLE_OPERATOR", Set.of(
                Permission.READ_USER, Permission.CREATE_USER, Permission.UPDATE_USER,
                Permission.READ_REPORT, Permission.CREATE_REPORT));

        // Manager role — can do everything Operator can, plus export
        rolePermissions.put("ROLE_MANAGER", Set.of(
                Permission.READ_REPORT, Permission.CREATE_REPORT, Permission.EXPORT_REPORT,
                Permission.READ_USER, Permission.VIEW_AUDIT_LOG));

        // Admin role — full access
        rolePermissions.put("ROLE_ADMIN", Set.of(
                Permission.READ_USER, Permission.CREATE_USER, Permission.UPDATE_USER,
                Permission.DELETE_USER,
                Permission.READ_REPORT, Permission.CREATE_REPORT, Permission.EXPORT_REPORT,
                Permission.MANAGE_ROLES, Permission.MANAGE_SYSTEM, Permission.VIEW_AUDIT_LOG));

        // Role hierarchy: ADMIN > MANAGER > OPERATOR > VIEWER
        roleHierarchy.put("ROLE_ADMIN", Set.of("ROLE_MANAGER"));
        roleHierarchy.put("ROLE_MANAGER", Set.of("ROLE_OPERATOR"));
        roleHierarchy.put("ROLE_OPERATOR", Set.of("ROLE_VIEWER"));
        roleHierarchy.put("ROLE_VIEWER", Set.of());
    }

    private void initUsers() {
        userRoles.put("alice", Set.of("ROLE_ADMIN"));
        userRoles.put("bob", Set.of("ROLE_MANAGER"));
        userRoles.put("charlie", Set.of("ROLE_OPERATOR"));
        userRoles.put("dave", Set.of("ROLE_VIEWER"));
        userRoles.put("eve", Set.of("ROLE_OPERATOR", "ROLE_MANAGER")); // Multiple roles
    }

    /**
     * Resolves effective permissions for a user, considering:
     * - Direct role permissions
     * - Inherited permissions from role hierarchy
     * - Permissions from all assigned roles
     */
    public Set<Permission> getUserPermissions(String username) {
        Set<String> roles = userRoles.get(username);
        if (roles == null) return Set.of();

        Set<Permission> permissions = new HashSet<>();
        Set<String> allRoles = new HashSet<>();

        // Resolve roles including hierarchy
        for (String role : roles) {
            allRoles.add(role);
            resolveRoleHierarchy(role, allRoles);
        }

        // Collect permissions from all roles
        for (String role : allRoles) {
            Set<Permission> rolePerms = rolePermissions.get(role);
            if (rolePerms != null) {
                permissions.addAll(rolePerms);
            }
        }

        return Collections.unmodifiableSet(permissions);
    }

    private void resolveRoleHierarchy(String role, Set<String> accumulator) {
        Set<String> inherited = roleHierarchy.get(role);
        if (inherited != null) {
            for (String parentRole : inherited) {
                if (accumulator.add(parentRole)) {
                    resolveRoleHierarchy(parentRole, accumulator);
                }
            }
        }
    }

    /**
     * Checks if a user has a specific permission.
     */
    public boolean hasPermission(String username, Permission permission) {
        return getUserPermissions(username).contains(permission);
    }

    /**
     * Checks if a user has all the specified permissions.
     */
    public boolean hasAllPermissions(String username, Permission... permissions) {
        Set<Permission> userPerms = getUserPermissions(username);
        return Arrays.stream(permissions).allMatch(userPerms::contains);
    }

    /**
     * Performs an access check — simulates @PreAuthorize behavior.
     */
    public boolean checkAccess(String username, String action, Object resource) {
        System.out.println("Access check: " + username + " wants to " + action
                + " " + resource);

        Permission required = switch (action.toUpperCase()) {
            case "CREATE_USER" -> Permission.CREATE_USER;
            case "DELETE_USER" -> Permission.DELETE_USER;
            case "EXPORT_REPORT" -> Permission.EXPORT_REPORT;
            case "VIEW_REPORT" -> Permission.READ_REPORT;
            case "MANAGE_ROLES" -> Permission.MANAGE_ROLES;
            default -> null;
        };

        if (required == null) {
            System.out.println("  Unknown action: " + action);
            return false;
        }

        boolean granted = hasPermission(username, required);
        System.out.println("  Required: " + required + " → "
                + (granted ? "GRANTED" : "DENIED"));
        return granted;
    }

    public static void main(String[] args) {
        RbacAuthorizationDemo rbac = new RbacAuthorizationDemo();

        System.out.println("=== RBAC Authorization Demo ===\n");

        // Display effective permissions
        for (String user : List.of("alice", "bob", "charlie", "dave", "eve")) {
            System.out.println(user + " (" + rbac.userRoles.get(user) + "):");
            System.out.println("  Permissions: " + rbac.getUserPermissions(user));
            System.out.println();
        }

        // Access check scenarios
        System.out.println("--- Access Control Checks ---");
        rbac.checkAccess("dave", "VIEW_REPORT", "Report-123");
        rbac.checkAccess("dave", "DELETE_USER", "User-456");
        rbac.checkAccess("bob", "EXPORT_REPORT", "Report-789");
        rbac.checkAccess("charlie", "CREATE_USER", null);
        rbac.checkAccess("alice", "MANAGE_ROLES", null);
        rbac.checkAccess("eve", "EXPORT_REPORT", "Report-999");
    }
}
