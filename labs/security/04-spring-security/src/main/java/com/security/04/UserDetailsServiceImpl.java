package com.security04;

import java.util.*;

/**
 * Simulates Spring Security's UserDetailsService.
 * 
 * SECURITY CONCEPT: UserDetailsService is the bridge between
 * your user storage (database, LDAP, etc.) and Spring Security.
 * 
 * Key responsibilities:
 * - Load users by username
 * - Return a UserDetails object with authorities (roles/permissions)
 * - Handle user not found gracefully (don't reveal which field is wrong)
 * - Support account locking, expiration, and credential expiration
 */
public class UserDetailsServiceImpl {

    // Simulated authorities/roles
    public static class GrantedAuthority {
        public final String authority;

        GrantedAuthority(String authority) {
            this.authority = authority;
        }

        @Override
        public String toString() { return authority; }
    }

    // Simulated UserDetails
    public static class UserDetails {
        public final String username;
        public final String password; // Hashed in production
        public final Set<GrantedAuthority> authorities;
        public final boolean accountNonExpired;
        public final boolean accountNonLocked;
        public final boolean credentialsNonExpired;
        public final boolean enabled;

        public UserDetails(String username, String password,
                           Set<GrantedAuthority> authorities,
                           boolean accountNonExpired, boolean accountNonLocked,
                           boolean credentialsNonExpired, boolean enabled) {
            this.username = username;
            this.password = password;
            this.authorities = authorities;
            this.accountNonExpired = accountNonExpired;
            this.accountNonLocked = accountNonLocked;
            this.credentialsNonExpired = credentialsNonExpired;
            this.enabled = enabled;
        }

        public boolean isAccountNonLocked() {
            return accountNonLocked;
        }

        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public String toString() {
            return "UserDetails{username='" + username + "', authorities="
                    + authorities + ", locked=" + !accountNonLocked
                    + ", enabled=" + enabled + "}";
        }
    }

    // In-memory user store — simulates a database lookup
    private final Map<String, UserDetails> userStore = new LinkedHashMap<>();

    public UserDetailsServiceImpl() {
        // SECURITY: Passwords are hashed in production (BCrypt).
        // The "password" field here represents the stored hash.
        initTestUsers();
    }

    private void initTestUsers() {
        userStore.put("admin", new UserDetails("admin",
                "$2a$12$hashed_admin_password_placeholder",
                Set.of(new GrantedAuthority("ROLE_ADMIN"),
                       new GrantedAuthority("ROLE_USER"),
                       new GrantedAuthority("PERMISSION_DELETE_USER"),
                       new GrantedAuthority("PERMISSION_VIEW_REPORTS")),
                true, true, true, true));

        userStore.put("user", new UserDetails("user",
                "$2a$12$hashed_user_password_placeholder",
                Set.of(new GrantedAuthority("ROLE_USER"),
                       new GrantedAuthority("PERMISSION_VIEW_REPORTS")),
                true, true, true, true));

        // Disabled account
        userStore.put("disabled", new UserDetails("disabled",
                "$2a$12$hashed_disabled_placeholder",
                Set.of(new GrantedAuthority("ROLE_USER")),
                true, true, true, false));

        // Locked account (after too many failed attempts)
        userStore.put("locked", new UserDetails("locked",
                "$2a$12$hashed_locked_placeholder",
                Set.of(new GrantedAuthority("ROLE_USER")),
                true, false, true, true));
    }

    /**
     * Loads a user by username — equivalent to loadUserByUsername().
     * SECURITY: Returns a generic "Bad credentials" error regardless
     * of whether the user exists. Never reveal "user not found" vs
     * "wrong password" — that lets attackers enumerate valid usernames.
     */
    public UserDetails loadUserByUsername(String username) {
        UserDetails user = userStore.get(username);
        if (user == null) {
            // Generic message prevents username enumeration
            throw new NoSuchElementException("Bad credentials");
        }

        // SECURITY: Check account status
        if (!user.isEnabled()) {
            throw new IllegalStateException("Account is disabled");
        }
        if (!user.isAccountNonLocked()) {
            throw new IllegalStateException("Account is locked");
        }

        return user;
    }

    /**
     * Demonstrates the authentication flow.
     * SECURITY: Always check account status BEFORE checking password.
     * This prevents leaking information about why authentication failed.
     */
    public boolean authenticate(String username, String rawPassword) {
        try {
            UserDetails user = loadUserByUsername(username);
            // In production: passwordEncoder.matches(rawPassword, user.password)
            System.out.println("User found: " + user);
            return true; // Simulating password match
        } catch (NoSuchElementException e) {
            System.out.println("Authentication failed for: " + username
                    + " — " + e.getMessage());
            return false;
        } catch (IllegalStateException e) {
            System.out.println("Authentication blocked for: " + username
                    + " — " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        UserDetailsServiceImpl service = new UserDetailsServiceImpl();

        System.out.println("=== UserDetailsService Simulation ===\n");

        // Load and display users
        for (String username : List.of("admin", "user", "disabled", "locked")) {
            try {
                UserDetails user = service.loadUserByUsername(username);
                System.out.println("Loaded: " + user);
            } catch (Exception e) {
                System.out.println("Error loading '" + username + "': " + e.getMessage());
            }
        }

        // Test non-existent user
        System.out.println("\n--- Authentication Attempts ---");
        service.authenticate("admin", "password123");
        service.authenticate("nonexistent", "password123");
        service.authenticate("disabled", "password123");
        service.authenticate("locked", "password123");
    }
}
