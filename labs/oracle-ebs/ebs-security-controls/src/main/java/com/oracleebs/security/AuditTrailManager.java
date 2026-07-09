package com.oracleebs.security;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuditTrailManager {
    public enum AuditAction { INSERT, UPDATE, DELETE, LOGIN, LOGOUT }

    public static class AuditRecord {
        private final long auditId;
        private final String tableName;
        private final String recordKey;
        private final AuditAction action;
        private final String user;
        private final String oldValue;
        private final String newValue;
        private final Instant timestamp;
        private final String ipAddress;

        public AuditRecord(long id, String table, String key, AuditAction action, String user, String oldVal, String newVal, String ip) {
            this.auditId = id;
            this.tableName = table;
            this.recordKey = key;
            this.action = action;
            this.user = user;
            this.oldValue = oldVal;
            this.newValue = newVal;
            this.timestamp = Instant.now();
            this.ipAddress = ip;
        }

        public long getAuditId() { return auditId; }
        public String getTableName() { return tableName; }
        public String getRecordKey() { return recordKey; }
        public AuditAction getAction() { return action; }
        public String getUser() { return user; }
        public String getOldValue() { return oldValue; }
        public String getNewValue() { return newValue; }
        public Instant getTimestamp() { return timestamp; }
        public String getIpAddress() { return ipAddress; }
    }

    public static class AuditPolicy {
        private final String tableName;
        private final boolean enabled;
        private final boolean trackOldValue;
        private final boolean trackNewValue;

        public AuditPolicy(String table, boolean enabled, boolean oldVal, boolean newVal) {
            this.tableName = table;
            this.enabled = enabled;
            this.trackOldValue = oldVal;
            this.trackNewValue = newVal;
        }

        public String getTableName() { return tableName; }
        public boolean isEnabled() { return enabled; }
        public boolean isTrackOldValue() { return trackOldValue; }
        public boolean isTrackNewValue() { return trackNewValue; }
    }

    private final Map<String, AuditPolicy> policies;
    private final List<AuditRecord> records;
    private long nextId;

    public AuditTrailManager() {
        this.policies = new ConcurrentHashMap<>();
        this.records = new ArrayList<>();
        this.nextId = 1;
    }

    public void setPolicy(AuditPolicy policy) {
        policies.put(policy.getTableName(), policy);
    }

    public AuditRecord record(String tableName, String recordKey, AuditAction action, String user, String oldVal, String newVal, String ip) {
        AuditPolicy policy = policies.get(tableName);
        if (policy == null || !policy.isEnabled()) return null;

        String oldValTracked = policy.isTrackOldValue() ? oldVal : null;
        String newValTracked = policy.isTrackNewValue() ? newVal : null;

        AuditRecord record = new AuditRecord(nextId++, tableName, recordKey, action, user, oldValTracked, newValTracked, ip);
        records.add(record);
        return record;
    }

    public List<AuditRecord> queryByUser(String user) {
        return records.stream().filter(r -> r.getUser().equals(user)).toList();
    }

    public List<AuditRecord> queryByTable(String table) {
        return records.stream().filter(r -> r.getTableName().equals(table)).toList();
    }

    public List<AuditRecord> queryByAction(AuditAction action) {
        return records.stream().filter(r -> r.getAction() == action).toList();
    }

    public int getTotalRecords() {
        return records.size();
    }

    public static AuditTrailManager createDefault() {
        AuditTrailManager mgr = new AuditTrailManager();
        mgr.setPolicy(new AuditPolicy("GL_BALANCES", true, true, true));
        mgr.setPolicy(new AuditPolicy("AP_INVOICES", true, true, true));
        mgr.setPolicy(new AuditPolicy("PO_HEADERS", true, false, true));
        mgr.setPolicy(new AuditPolicy("FND_USER", true, true, true));
        mgr.record("FND_USER", "USER001", AuditAction.UPDATE, "SYSADMIN", "EMAIL=old@test.com", "EMAIL=new@test.com", "10.0.0.1");
        mgr.record("AP_INVOICES", "INV001", AuditAction.INSERT, "AP_CLERK", null, "AMOUNT=5000", "10.0.0.2");
        return mgr;
    }
}
