package com.databases.security;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RbacController {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Role> roles = new ConcurrentHashMap<>();
    private final Map<String, Permission> permissions = new ConcurrentHashMap<>();

    public record Permission(String resource, String action, String description) {}
    public record Role(String name, String description, Set<String> permissionIds) {}
    public record User(String id, String name, Set<String> roleNames) {}

    public void addPermission(String id, String resource, String action, String desc) {
        permissions.put(id, new Permission(resource, action, desc));
    }

    public void addRole(String name, String desc, String... permIds) {
        roles.put(name, new Role(name, desc, new HashSet<>(Arrays.asList(permIds))));
    }

    public void addUser(String id, String name, String... roleNames) {
        users.put(id, new User(id, name, new HashSet<>(Arrays.asList(roleNames))));
    }

    public boolean checkAccess(String userId, String resource, String action) {
        var user = users.get(userId);
        if (user == null) return false;
        for (var roleName : user.roleNames()) {
            var role = roles.get(roleName);
            if (role == null) continue;
            for (var permId : role.permissionIds()) {
                var perm = permissions.get(permId);
                if (perm != null && perm.resource().equals(resource) && perm.action().equals(action)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<String> getUserPermissions(String userId) {
        var user = users.get(userId);
        if (user == null) return Set.of();
        var result = new HashSet<String>();
        for (var roleName : user.roleNames()) {
            var role = roles.get(roleName);
            if (role == null) continue;
            for (var permId : role.permissionIds()) {
                var perm = permissions.get(permId);
                if (perm != null) result.add(perm.resource() + ":" + perm.action());
            }
        }
        return result;
    }

    public void removeUser(String id) { users.remove(id); }
    public void removeRole(String name) {
        roles.remove(name);
        users.values().forEach(u -> u.roleNames().remove(name));
    }

    public static RbacController createDefault() {
        var rbac = new RbacController();
        rbac.addPermission("perm_read", "orders", "read", "Read orders");
        rbac.addPermission("perm_write", "orders", "write", "Write orders");
        rbac.addPermission("perm_delete", "orders", "delete", "Delete orders");
        rbac.addPermission("perm_admin", "*", "admin", "Admin access");
        rbac.addRole("admin", "Administrator", "perm_read", "perm_write", "perm_delete", "perm_admin");
        rbac.addRole("writer", "Can read and write", "perm_read", "perm_write");
        rbac.addRole("reader", "Read-only", "perm_read");
        rbac.addUser("user1", "Alice", "admin");
        rbac.addUser("user2", "Bob", "writer");
        rbac.addUser("user3", "Charlie", "reader");
        return rbac;
    }
}
