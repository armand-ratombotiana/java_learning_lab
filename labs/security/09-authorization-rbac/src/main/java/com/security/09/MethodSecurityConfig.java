package com.security09;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Demonstrates Spring Security method-level security annotations.
 * 
 * SECURITY CONCEPT: Method security provides fine-grained access control
 * at the service/controller method level using annotations:
 * 
 * @PreAuthorize — Check authorization BEFORE method execution
 * @PostAuthorize — Check authorization AFTER method execution (access result)
 * @PreFilter / @PostFilter — Filter collections before/after method execution
 * @Secured — Simple role-based (less flexible than @PreAuthorize)
 * @RolesAllowed — JSR-250 equivalent of @Secured
 * 
 * Benefits:
 * - Declarative security (no boilerplate if/else checks)
 * - SpEL expressions for complex rules
 * - Method arguments and return values available in expressions
 * - Works with AOP proxies
 * 
 * Must enable with @EnableMethodSecurity (Spring Security 6+)
 * or @EnableGlobalMethodSecurity (older versions).
 */
public class MethodSecurityConfig {

    // Simulated annotation
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface PreAuthorize {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface PostAuthorize {
        String value();
    }

    // Simulated method security interceptor
    public static class MethodSecurityInterceptor {

        private final RbacAuthorizationDemo rbac;

        public MethodSecurityInterceptor(RbacAuthorizationDemo rbac) {
            this.rbac = rbac;
        }

        /**
         * Evaluates @PreAuthorize expression before method execution.
         * Supported SpEL expressions:
         * - hasRole('ROLE_ADMIN')
         * - hasAuthority('PERMISSION_DELETE_USER')
         * - hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')
         * - isAuthenticated()
         * - isAnonymous()
         * - permitAll / denyAll
         * - #argumentName (method arguments)
         * - @beanName.method() (Spring bean references)
         */
        public boolean checkPreAuthorize(String expression, String username) {
            System.out.println("  @PreAuthorize(\"" + expression + "\")");

            if (expression.startsWith("hasRole('") && expression.endsWith("')")) {
                String role = expression.substring(9, expression.length() - 2);
                Set<String> userRoles = getUserRoles(username);
                boolean hasRole = userRoles.contains(role);
                System.out.println("    hasRole check: " + role + " → "
                        + (hasRole ? "PASS" : "FAIL"));
                return hasRole;
            }

            if (expression.startsWith("hasAuthority('") && expression.endsWith("')")) {
                String authority = expression.substring(14, expression.length() - 2);
                try {
                    RbacAuthorizationDemo.Permission perm =
                            RbacAuthorizationDemo.Permission.valueOf(authority);
                    boolean hasPerm = rbac.hasPermission(username, perm);
                    System.out.println("    hasAuthority check: " + authority + " → "
                            + (hasPerm ? "PASS" : "FAIL"));
                    return hasPerm;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }

            if ("isAuthenticated()".equals(expression)) {
                return username != null && !"anonymous".equals(username);
            }

            if ("permitAll".equals(expression)) return true;
            if ("denyAll".equals(expression)) return false;

            System.out.println("    Unknown expression — DENY by default");
            return false; // Deny by default for unknown expressions
        }

        @SuppressWarnings("unchecked")
        private Set<String> getUserRoles(String username) {
            // Reflection-based access to private field for demo
            try {
                var field = RbacAuthorizationDemo.class.getDeclaredField("userRoles");
                field.setAccessible(true);
                Map<String, Set<String>> userRoles =
                        (Map<String, Set<String>>) field.get(rbac);
                return userRoles.getOrDefault(username, Set.of());
            } catch (Exception e) {
                return Set.of();
            }
        }

        /**
         * Evaluates @PostAuthorize expression after method execution.
         * Can access the return value via 'returnObject'.
         */
        public boolean checkPostAuthorize(String expression, Object returnValue) {
            System.out.println("  @PostAuthorize(\"" + expression + "\")");
            // PostAuthorize can check properties of the return value
            if (expression.contains("returnObject") && returnValue != null) {
                System.out.println("    Return object: " + returnValue);
                return true;
            }
            return true;
        }
    }

    // Simulated service methods with security annotations
    static class UserService {
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        void deleteUser(String userId, String currentUser) {
            System.out.println("    Executing: deleteUser(" + userId + ")\n");
        }

        @PreAuthorize("hasAuthority('DELETE_USER')")
        void deleteUserByPermission(String userId, String currentUser) {
            System.out.println("    Executing: deleteUserByPermission(" + userId + ")\n");
        }

        @PreAuthorize("isAuthenticated()")
        @PostAuthorize("returnObject != null")
        String getUserProfile(String userId, String currentUser) {
            System.out.println("    Executing: getUserProfile(" + userId + ")");
            return "Profile data for: " + userId;
        }

        @PreAuthorize("permitAll")
        String getPublicInfo() {
            return "Public information";
        }
    }

    public static void main(String[] args) throws Exception {
        RbacAuthorizationDemo rbac = new RbacAuthorizationDemo();
        MethodSecurityInterceptor interceptor = new MethodSecurityInterceptor(rbac);
        UserService userService = new UserService();

        System.out.println("=== Method Security (Annotation Simulation) ===\n");

        // Execute methods with security interception
        for (Method method : UserService.class.getDeclaredMethods()) {
            PreAuthorize preAuth = method.getAnnotation(PreAuthorize.class);
            PostAuthorize postAuth = method.getAnnotation(PostAuthorize.class);

            System.out.println("Method: " + method.getName());

            String testUser = "alice"; // admin user
            String targetUser = "bob";

            // Pre-authorization check
            boolean preAuthPassed = true;
            if (preAuth != null) {
                preAuthPassed = interceptor.checkPreAuthorize(preAuth.value(), testUser);
            }

            if (preAuthPassed) {
                // Execute the method
                Object result = method.invoke(userService, targetUser, testUser);

                // Post-authorization check
                if (postAuth != null) {
                    interceptor.checkPostAuthorize(postAuth.value(), result);
                }
            } else {
                System.out.println("    ACCESS DENIED by @PreAuthorize\n");
            }
        }

        // Test unauthorized access
        System.out.println("\n--- Unauthorized Access Attempt ---");
        System.out.println("Method: deleteUser (as 'dave' — viewer role)");
        boolean allowed = interceptor.checkPreAuthorize("hasRole('ROLE_ADMIN')", "dave");
        if (!allowed) {
            System.out.println("    ACCESS DENIED — dave is not an admin\n");
        }

        System.out.println("--- Method Security Benefits ---");
        System.out.println("- Declarative: no scattered security checks");
        System.out.println("- Reusable: same rules apply across callers");
        System.out.println("- Auditable: security decisions are centralized");
        System.out.println("- Expressive: SpEL for complex conditions");
    }
}
