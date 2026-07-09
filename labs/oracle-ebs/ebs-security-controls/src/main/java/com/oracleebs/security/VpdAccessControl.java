package com.oracleebs.security;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VpdAccessControl {
    public static class VpdPolicy {
        private final String policyName;
        private final String tableName;
        private final String predicate;
        private final boolean enabled;
        private final String policyGroup;

        public VpdPolicy(String name, String table, String predicate, boolean enabled, String group) {
            this.policyName = name;
            this.tableName = table;
            this.predicate = predicate;
            this.enabled = enabled;
            this.policyGroup = group;
        }

        public String getPolicyName() { return policyName; }
        public String getTableName() { return tableName; }
        public String getPredicate() { return predicate; }
        public boolean isEnabled() { return enabled; }
        public String getPolicyGroup() { return policyGroup; }
    }

    public static class OrgContext {
        private final String userId;
        private final String responsibilityId;
        private final String orgId;
        private final boolean multiOrgView;

        public OrgContext(String user, String resp, String org, boolean multiOrg) {
            this.userId = user;
            this.responsibilityId = resp;
            this.orgId = org;
            this.multiOrgView = multiOrg;
        }

        public String getUserId() { return userId; }
        public String getResponsibilityId() { return responsibilityId; }
        public String getOrgId() { return orgId; }
        public boolean isMultiOrgView() { return multiOrgView; }
    }

    private final Map<String, VpdPolicy> policies;
    private final Map<String, List<OrgContext>> userContexts;

    public VpdAccessControl() {
        this.policies = new ConcurrentHashMap<>();
        this.userContexts = new ConcurrentHashMap<>();
    }

    public void addPolicy(VpdPolicy policy) {
        policies.put(policy.getPolicyName(), policy);
    }

    public void setUserContext(String userId, OrgContext context) {
        userContexts.computeIfAbsent(userId, k -> new ArrayList<>()).add(context);
    }

    public String resolvePredicate(String tableName, String userId) {
        OrgContext ctx = getCurrentContext(userId);
        if (ctx == null) return "1=0";

        StringBuilder predicate = new StringBuilder("1=1");

        for (VpdPolicy policy : policies.values()) {
            if (policy.getTableName().equals(tableName) && policy.isEnabled()) {
                if (!ctx.isMultiOrgView()) {
                    predicate.append(" AND org_id = '").append(ctx.getOrgId()).append("'");
                }
                predicate.append(" AND (").append(policy.getPredicate()).append(")");
            }
        }

        return predicate.toString();
    }

    public boolean canAccessOrg(String userId, String orgId) {
        OrgContext ctx = getCurrentContext(userId);
        return ctx != null && (ctx.isMultiOrgView() || ctx.getOrgId().equals(orgId));
    }

    private OrgContext getCurrentContext(String userId) {
        var contexts = userContexts.get(userId);
        return contexts == null || contexts.isEmpty() ? null : contexts.get(contexts.size() - 1);
    }

    public static VpdAccessControl createDefault() {
        VpdAccessControl vpd = new VpdAccessControl();
        vpd.addPolicy(new VpdPolicy("MOAC_POLICY", "GL_BALANCES", "org_id = SYS_CONTEXT('FND','ORG_ID')", true, "SYS_DEFAULT"));
        vpd.addPolicy(new VpdPolicy("HR_POLICY", "PER_PEOPLE_F", "business_group_id = SYS_CONTEXT('FND','BG_ID')", true, "SYS_DEFAULT"));
        vpd.setUserContext("ALICE", new OrgContext("ALICE", "GL_RESP", "101", false));
        vpd.setUserContext("BOB", new OrgContext("BOB", "MULTI_ORG_RESP", "101", true));
        return vpd;
    }
}
