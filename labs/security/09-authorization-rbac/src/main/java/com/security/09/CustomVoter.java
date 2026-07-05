package com.security09;

import java.util.*;

/**
 * Demonstrates a custom Access Decision Voter for Spring Security.
 * 
 * SECURITY CONCEPT: Spring Security's Access Decision Manager uses
 * voters to decide whether to grant or deny access to a resource.
 * Each voter votes GRANT, DENY, or ABSTAIN, and the manager
 * makes the final decision based on the configured strategy:
 * 
 * - AffirmativeBased: GRANT if any voter votes GRANT
 * - ConsensusBased: GRANT if more GRANT than DENY votes
 * - UnanimousBased: GRANT only if all voters vote GRANT
 * 
 * Custom voters allow domain-specific authorization logic
 * that goes beyond simple role checking.
 */
public class CustomVoter {

    public enum Vote { GRANT, DENY, ABSTAIN }

    // Simulated resource permissions (resource → required permission)
    private static final Map<String, RbacAuthorizationDemo.Permission> resourcePermissions
            = new LinkedHashMap<>();

    static {
        resourcePermissions.put("/api/users", RbacAuthorizationDemo.Permission.READ_USER);
        resourcePermissions.put("/api/users/create", RbacAuthorizationDemo.Permission.CREATE_USER);
        resourcePermissions.put("/api/users/delete", RbacAuthorizationDemo.Permission.DELETE_USER);
        resourcePermissions.put("/api/reports", RbacAuthorizationDemo.Permission.READ_REPORT);
        resourcePermissions.put("/api/reports/export", RbacAuthorizationDemo.Permission.EXPORT_REPORT);
        resourcePermissions.put("/api/admin/roles", RbacAuthorizationDemo.Permission.MANAGE_ROLES);
    }

    private final RbacAuthorizationDemo rbacDemo;

    public CustomVoter(RbacAuthorizationDemo rbacDemo) {
        this.rbacDemo = rbacDemo;
    }

    /**
     * Votes on whether a user can access a resource.
     * This is a simplified version of AccessDecisionVoter.supports()
     * and vote() combined.
     * 
     * SECURITY: The voter is parameterized to support different
     * resource types and security configurations.
     */
    public Vote vote(String username, String resourceUri, String httpMethod) {
        // Determine required permission for this resource
        RbacAuthorizationDemo.Permission required = resourcePermissions.get(resourceUri);

        if (required == null) {
            // No specific permission required — abstain (let other voters decide)
            return Vote.ABSTAIN;
        }

        // Special rules based on HTTP method (GET is less sensitive)
        if ("GET".equalsIgnoreCase(httpMethod)) {
            // For read access, map to the read version of the permission
            String readPermission = required.name().replace("CREATE_", "READ_")
                    .replace("DELETE_", "READ_")
                    .replace("EXPORT_", "READ_")
                    .replace("MANAGE_", "READ_");
            try {
                required = RbacAuthorizationDemo.Permission.valueOf(readPermission);
            } catch (IllegalArgumentException e) {
                // Keep original if mapping fails
            }
        }

        // SECURITY: Time-based access restriction (weekend / after-hours)
        if (isAfterHours() && requiresElevatedAccess(required)) {
            System.out.println("  [Voter] After-hours restriction for sensitive operation");
            return Vote.DENY;
        }

        // Check permission against the RBAC system
        if (rbacDemo.hasPermission(username, required)) {
            System.out.println("  [Voter] GRANT — user has " + required);
            return Vote.GRANT;
        } else {
            System.out.println("  [Voter] DENY — user lacks " + required);
            return Vote.DENY;
        }
    }

    /**
     * Simulates after-hours check (outside 9 AM – 6 PM).
     */
    private boolean isAfterHours() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int hour = cal.get(java.util.Calendar.HOUR_OF_DAY);
        return hour < 9 || hour >= 18;
    }

    /**
     * Determines if an operation requires elevated (admin-level) access.
     */
    private boolean requiresElevatedAccess(RbacAuthorizationDemo.Permission permission) {
        return permission == RbacAuthorizationDemo.Permission.DELETE_USER
                || permission == RbacAuthorizationDemo.Permission.MANAGE_ROLES
                || permission == RbacAuthorizationDemo.Permission.MANAGE_SYSTEM
                || permission == RbacAuthorizationDemo.Permission.EXPORT_REPORT;
    }

    /**
     * Demonstrates the AffirmativeBased strategy — GRANT if any voter votes GRANT.
     */
    public static class AffirmativeBasedManager {
        private final List<CustomVoter> voters;

        public AffirmativeBasedManager(List<CustomVoter> voters) {
            this.voters = voters;
        }

        public boolean decide(String username, String resource, String method) {
            for (CustomVoter voter : voters) {
                Vote vote = voter.vote(username, resource, method);
                if (vote == Vote.GRANT) {
                    return true;
                }
                if (vote == Vote.DENY) {
                    // Continue checking — AffirmativeBased allows others to GRANT
                }
            }
            return false; // No voter granted access
        }
    }

    public static void main(String[] args) {
        RbacAuthorizationDemo rbac = new RbacAuthorizationDemo();
        CustomVoter voter = new CustomVoter(rbac);

        System.out.println("=== Custom Access Decision Voter ===\n");

        // Test access scenarios
        String[][] testCases = {
            {"alice", "/api/users", "GET"},
            {"alice", "/api/admin/roles", "POST"},
            {"bob", "/api/reports/export", "POST"},
            {"charlie", "/api/reports/export", "POST"},
            {"dave", "/api/users/delete", "DELETE"},
            {"dave", "/api/reports", "GET"}
        };

        for (String[] test : testCases) {
            System.out.println("User: " + test[0] + ", Resource: " + test[1]
                    + ", Method: " + test[2]);
            Vote vote = voter.vote(test[0], test[1], test[2]);
            System.out.println("  Result: " + vote);
            // AffirmativeBased decision
            boolean decided = new AffirmativeBasedManager(List.of(voter))
                    .decide(test[0], test[1], test[2]);
            System.out.println("  Access: " + (decided ? "GRANTED" : "DENIED") + "\n");
        }
    }
}
