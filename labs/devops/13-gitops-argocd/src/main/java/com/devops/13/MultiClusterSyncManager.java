package com.devops.thirteen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MultiClusterSyncManager {
    private final String name;
    private final Map<String, List<ArgoCDApplication>> clusterApps = new ConcurrentHashMap<>();

    public MultiClusterSyncManager(String name) {
        this.name = name;
    }

    public void registerCluster(String clusterName, String serverUrl) {
        clusterApps.putIfAbsent(clusterName, new ArrayList<>());
        System.out.printf("Registered cluster '%s' at %s%n", clusterName, serverUrl);
    }

    public void deployToCluster(String clusterName, ArgoCDApplication app) {
        clusterApps.computeIfAbsent(clusterName, k -> new ArrayList<>()).add(app);
        System.out.printf("Added app '%s' to cluster '%s'%n", app.getName(), clusterName);
    }

    public SyncPolicy createSyncPolicy(String clusterName) {
        return new SyncPolicy(clusterName, true, true, false);
    }

    public SyncResult syncCluster(String clusterName) {
        List<ArgoCDApplication> apps = clusterApps.get(clusterName);
        if (apps == null || apps.isEmpty()) {
            return new SyncResult(clusterName, false, "No applications found");
        }
        System.out.printf("=== Syncing cluster '%s' with %d applications ===%n", clusterName, apps.size());
        List<CompletableFuture<Boolean>> futures = apps.stream()
            .map(app -> CompletableFuture.supplyAsync(app::sync))
            .toList();
        boolean allSuccess = futures.stream().allMatch(f -> {
            try { return f.get(); } catch (Exception e) { return false; }
        });
        String status = allSuccess ? "All apps synced" : "Some apps failed";
        System.out.printf("Cluster '%s' sync result: %s%n", clusterName, status);
        return new SyncResult(clusterName, allSuccess, status);
    }

    public void syncAllClusters() {
        System.out.printf("=== Multi-cluster sync for '%s' with %d clusters ===%n", name, clusterApps.size());
        clusterApps.keySet().forEach(this::syncCluster);
    }

    public static class SyncPolicy {
        public final String clusterName;
        public final boolean autoSync;
        public final boolean prune;
        public final boolean selfHeal;

        public SyncPolicy(String clusterName, boolean autoSync, boolean prune, boolean selfHeal) {
            this.clusterName = clusterName;
            this.autoSync = autoSync;
            this.prune = prune;
            this.selfHeal = selfHeal;
        }
    }

    public static class SyncResult {
        public final String clusterName;
        public final boolean success;
        public final String message;

        public SyncResult(String clusterName, boolean success, String message) {
            this.clusterName = clusterName;
            this.success = success;
            this.message = message;
        }
    }

    public static void main(String[] args) {
        MultiClusterSyncManager manager = new MultiClusterSyncManager("global-deploy");
        manager.registerCluster("prod-us-east", "https://kubernetes.us-east-1.example.com");
        manager.registerCluster("staging-eu-west", "https://kubernetes.eu-west-1.example.com");

        ArgoCDApplication app1 = new ArgoCDApplication(
            "frontend", "https://github.com/org/config.git", "k8s/frontend", "main", "frontend-ns");
        ArgoCDApplication app2 = new ArgoCDApplication(
            "backend", "https://github.com/org/config.git", "k8s/backend", "main", "backend-ns");

        manager.deployToCluster("prod-us-east", app1);
        manager.deployToCluster("prod-us-east", app2);
        manager.deployToCluster("staging-eu-west", app1);

        manager.syncAllClusters();
    }
}
