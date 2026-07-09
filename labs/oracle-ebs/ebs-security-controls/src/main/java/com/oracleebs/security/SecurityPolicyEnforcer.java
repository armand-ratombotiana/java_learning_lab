package com.oracleebs.security;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SecurityPolicyEnforcer {
    public enum AccessResult { GRANTED, DENIED_FUNCTION, DENIED_DATA, DENIED_FORM }

    public static class FunctionSecurity {
        private final String functionName;
        private final String responsibilityKey;
        private final boolean enabled;

        public FunctionSecurity(String func, String resp, boolean enabled) {
            this.functionName = func;
            this.responsibilityKey = resp;
            this.enabled = enabled;
        }

        public String getFunctionName() { return functionName; }
        public String getResponsibilityKey() { return responsibilityKey; }
        public boolean isEnabled() { return enabled; }
    }

    public static class DataSecurity {
        private final String tableName;
        private final String accessType;
        private final Set<String> allowedOrgs;
        private final boolean selectAllowed;
        private final boolean insertAllowed;
        private final boolean updateAllowed;
        private final boolean deleteAllowed;

        public DataSecurity(String table, String access, Set<String> orgs, boolean sel, boolean ins, boolean upd, boolean del) {
            this.tableName = table;
            this.accessType = access;
            this.allowedOrgs = orgs;
            this.selectAllowed = sel;
            this.insertAllowed = ins;
            this.updateAllowed = upd;
            this.deleteAllowed = del;
        }

        public String getTableName() { return tableName; }
        public String getAccessType() { return accessType; }
        public Set<String> getAllowedOrgs() { return allowedOrgs; }
        public boolean isSelectAllowed() { return selectAllowed; }
        public boolean isInsertAllowed() { return insertAllowed; }
        public boolean isUpdateAllowed() { return updateAllowed; }
        public boolean isDeleteAllowed() { return deleteAllowed; }
    }

    private final Map<String, List<FunctionSecurity>> functionGrants;
    private final Map<String, DataSecurity> dataGrants;
    private final Set<String> privilegedUsers;

    public SecurityPolicyEnforcer() {
        this.functionGrants = new ConcurrentHashMap<>();
        this.dataGrants = new ConcurrentHashMap<>();
        this.privilegedUsers = ConcurrentHashMap.newKeySet();
    }

    public void grantFunction(String functionName, String responsibilityKey, boolean enabled) {
        functionGrants.computeIfAbsent(functionName, k -> new ArrayList<>())
            .add(new FunctionSecurity(functionName, responsibilityKey, enabled));
    }

    public void grantData(DataSecurity ds) {
        dataGrants.put(ds.getTableName(), ds);
    }

    public void addPrivilegedUser(String user) {
        privilegedUsers.add(user);
    }

    public AccessResult checkFunctionAccess(String functionName, String responsibilityKey) {
        var grants = functionGrants.get(functionName);
        if (grants == null) return AccessResult.DENIED_FUNCTION;
        return grants.stream()
            .filter(g -> g.getResponsibilityKey().equals(responsibilityKey) && g.isEnabled())
            .findFirst()
            .map(g -> AccessResult.GRANTED)
            .orElse(AccessResult.DENIED_FUNCTION);
    }

    public AccessResult checkDataAccess(String tableName, String orgId, String operation) {
        DataSecurity ds = dataGrants.get(tableName);
        if (ds == null) return AccessResult.DENIED_DATA;
        if (!ds.getAllowedOrgs().contains(orgId)) return AccessResult.DENIED_DATA;
        return switch (operation.toUpperCase()) {
            case "SELECT" -> ds.isSelectAllowed() ? AccessResult.GRANTED : AccessResult.DENIED_DATA;
            case "INSERT" -> ds.isInsertAllowed() ? AccessResult.GRANTED : AccessResult.DENIED_DATA;
            case "UPDATE" -> ds.isUpdateAllowed() ? AccessResult.GRANTED : AccessResult.DENIED_DATA;
            case "DELETE" -> ds.isDeleteAllowed() ? AccessResult.GRANTED : AccessResult.DENIED_DATA;
            default -> AccessResult.DENIED_DATA;
        };
    }

    public boolean isPrivileged(String user) {
        return privilegedUsers.contains(user);
    }

    public static SecurityPolicyEnforcer createDefault() {
        SecurityPolicyEnforcer enforcer = new SecurityPolicyEnforcer();
        enforcer.grantFunction("GL_POST", "GL_RESPONSIBILITY", true);
        enforcer.grantFunction("AP_INVOICE_ENTRY", "AP_RESPONSIBILITY", true);
        enforcer.grantFunction("ADMIN_PANEL", "SYSADMIN", true);
        enforcer.grantFunction("ADMIN_PANEL", "AP_RESPONSIBILITY", false);
        enforcer.grantData(new DataSecurity("GL_BALANCES", "READ_ONLY", Set.of("101", "102", "103"), true, false, false, false));
        enforcer.grantData(new DataSecurity("AP_INVOICES", "FULL", Set.of("101", "102"), true, true, true, false));
        enforcer.addPrivilegedUser("SYSADMIN");
        enforcer.addPrivilegedUser("OPERATIONS");
        return enforcer;
    }
}
