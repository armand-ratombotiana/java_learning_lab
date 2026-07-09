package com.apex.apt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ApexSecurityManager {
    public record User(String id, String name, Set<String> roles) {}
    public record AuthScheme(String name, String type, boolean isCurrent) {}
    public record AuthResult(boolean success, String userId, String message) {}

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> rolePermissions = new ConcurrentHashMap<>();
    private AuthScheme currentAuthScheme = new AuthScheme("Application Express Accounts", "APEX_ACCOUNTS", true);
    private String sessionStateProtection = "UNRESTRICTED";

    public void registerUser(User u) { users.put(u.id(), u); }
    public void addRolePermission(String role, String permission) {
        rolePermissions.computeIfAbsent(role, r -> ConcurrentHashMap.newKeySet()).add(permission);
    }

    public AuthResult authenticate(String username, String password) {
        var user = users.get(username);
        if (user == null) return new AuthResult(false, null, "User not found");
        // Simulated authentication
        return new AuthResult(true, username, "Authenticated via " + currentAuthScheme.name());
    }

    public boolean authorize(String userId, String permission) {
        var user = users.get(userId);
        if (user == null) return false;
        for (var role : user.roles()) {
            var perms = rolePermissions.get(role);
            if (perms != null && (perms.contains(permission) || perms.contains("*")))
                return true;
        }
        return false;
    }

    public boolean checkSessionStateProtection(String pageId, String item, String value) {
        if ("UNRESTRICTED".equals(sessionStateProtection)) return true;
        if ("CHECKSUM".equals(sessionStateProtection)) return value != null && !value.isEmpty();
        return false;
    }

    public void setSessionStateProtection(String level) { this.sessionStateProtection = level; }
    public void setAuthScheme(AuthScheme s) { this.currentAuthScheme = s; }
    public AuthScheme getAuthScheme() { return currentAuthScheme; }

    public String generateCsrfToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public boolean validateCsrfToken(String token, String expected) {
        return token != null && token.equals(expected);
    }

    public String sanitizeHtml(String input) {
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
    }

    public static ApexSecurityManager createSample() {
        var mgr = new ApexSecurityManager();
        mgr.registerUser(new User("admin", "Admin User", Set.of("ADMIN")));
        mgr.registerUser(new User("editor", "Editor User", Set.of("EDITOR")));
        mgr.registerUser(new User("viewer", "Viewer User", Set.of("VIEWER")));
        mgr.addRolePermission("ADMIN", "*");
        mgr.addRolePermission("EDITOR", "DATA_EDIT");
        mgr.addRolePermission("EDITOR", "DATA_VIEW");
        mgr.addRolePermission("VIEWER", "DATA_VIEW");
        return mgr;
    }
}