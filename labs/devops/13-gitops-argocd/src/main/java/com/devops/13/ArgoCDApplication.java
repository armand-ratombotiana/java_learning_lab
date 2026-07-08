package com.devops.thirteen;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArgoCDApplication {
    private final String name;
    private final String repoUrl;
    private final String path;
    private final String targetRevision;
    private final String namespace;
    private final Map<String, String> parameters = new ConcurrentHashMap<>();
    private volatile SyncStatus syncStatus = SyncStatus.UNKNOWN;
    private volatile HealthStatus healthStatus = HealthStatus.UNKNOWN;
    private volatile String syncStartedAt;
    private volatile String revision;

    public enum SyncStatus { UNKNOWN, SYNCED, OUT_OF_SYNC, SYNC_FAILED, SYNCING }
    public enum HealthStatus { UNKNOWN, HEALTHY, DEGRADED, PROGRESSING, SUSPENDED }

    public ArgoCDApplication(String name, String repoUrl, String path, String targetRevision, String namespace) {
        this.name = name;
        this.repoUrl = repoUrl;
        this.path = path;
        this.targetRevision = targetRevision;
        this.namespace = namespace;
    }

    public void setParameter(String key, String value) { parameters.put(key, value); }
    public String getParameter(String key) { return parameters.get(key); }
    public String getName() { return name; }
    public String getRepoUrl() { return repoUrl; }
    public String getPath() { return path; }
    public String getTargetRevision() { return targetRevision; }
    public String getNamespace() { return namespace; }
    public SyncStatus getSyncStatus() { return syncStatus; }
    public HealthStatus getHealthStatus() { return healthStatus; }

    public boolean sync() {
        syncStartedAt = Instant.now().toString();
        syncStatus = SyncStatus.SYNCING;
        System.out.printf("[%s] ArgoCD Application '%s' sync started%n", syncStartedAt, name);
        System.out.printf("  Source: %s/%s@%s%n", repoUrl, path, targetRevision);
        System.out.printf("  Destination: namespace=%s, server=https://kubernetes.default.svc%n", namespace);
        System.out.printf("  Parameters: %s%n", parameters);
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        revision = "abc" + Instant.now().toEpochMilli();
        syncStatus = SyncStatus.SYNCED;
        healthStatus = HealthStatus.HEALTHY;
        System.out.printf("[%s] Application '%s' synced successfully to revision %s%n", Instant.now(), name, revision);
        return true;
    }

    public boolean refresh() {
        System.out.printf("Refreshing application '%s' from repo %s%n", name, repoUrl);
        syncStatus = SyncStatus.SYNCING;
        try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        syncStatus = SyncStatus.SYNCED;
        healthStatus = HealthStatus.HEALTHY;
        return true;
    }

    public Map<String, Object> status() {
        Map<String, Object> status = new HashMap<>();
        status.put("name", name);
        status.put("syncStatus", syncStatus);
        status.put("healthStatus", healthStatus);
        status.put("revision", revision);
        status.put("repoUrl", repoUrl);
        status.put("namespace", namespace);
        status.put("syncStartedAt", syncStartedAt);
        return status;
    }

    public boolean rollback(String targetRevision) {
        System.out.printf("Rolling back '%s' to revision %s%n", name, targetRevision);
        syncStatus = SyncStatus.SYNCING;
        try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        this.revision = targetRevision;
        syncStatus = SyncStatus.SYNCED;
        healthStatus = HealthStatus.HEALTHY;
        System.out.printf("Rollback of '%s' to %s completed%n", name, targetRevision);
        return true;
    }

    public static void main(String[] args) {
        ArgoCDApplication app = new ArgoCDApplication(
            "my-service", "https://github.com/org/config.git", "k8s/overlays/prod", "main", "production");
        app.setParameter("replicaCount", "5");
        app.setParameter("image.tag", "1.2.3");
        app.sync();
        System.out.println("Status: " + app.status());
        app.refresh();
        app.rollback("abc1700000000000");
    }
}
