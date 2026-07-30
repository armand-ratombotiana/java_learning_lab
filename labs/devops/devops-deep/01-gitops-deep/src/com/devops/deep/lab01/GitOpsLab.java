package com.devops.deep.lab01;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GitOpsLab {
    public static void main(String[] args) {
        var gitRepo = new GitRepository();
        gitRepo.commit("main", "Deployment", List.of(
            new Resource("Deployment", "web-app", "nginx:1.25", 3),
            new Resource("Service", "web-svc", null, 0)
        ));

        var argoCD = new ArgoCD(gitRepo);
        argoCD.createApplication("web-app", "main", "Deployment");

        argoCD.reconcile();

        System.out.println("After initial reconcile:");
        System.out.println(argoCD.status());

        // Simulate drift
        argoCD.injectDrift("web-app", new Resource("Deployment", "web-app", "nginx:1.26", 3));

        System.out.println("After drift injection:");
        System.out.println(argoCD.status());

        argoCD.reconcile();
        System.out.println("After drift correction:");
        System.out.println(argoCD.status());
    }
}

record Resource(String kind, String name, String image, int replicas) {}

class GitRepository {
    private final Map<String, Map<String, List<Resource>>> branches = new ConcurrentHashMap<>();
    private final List<String> history = new CopyOnWriteArrayList<>();

    void commit(String branch, String path, List<Resource> resources) {
        branches.computeIfAbsent(branch, k -> new ConcurrentHashMap<>()).put(path, resources);
        history.add("[" + Instant.now() + "] Commit to " + branch + "/" + path);
    }

    List<Resource> getDesiredState(String branch, String path) {
        return branches.getOrDefault(branch, Map.of()).getOrDefault(path, List.of());
    }

    List<String> history() { return List.copyOf(history); }
}

class ArgoCD {
    private final GitRepository repo;
    private final Map<String, GitOpsApplication> apps = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Resource>> liveState = new ConcurrentHashMap<>();

    ArgoCD(GitRepository repo) { this.repo = repo; }

    void createApplication(String name, String branch, String path) {
        apps.put(name, new GitOpsApplication(name, branch, path));
    }

    void reconcile() {
        for (var entry : apps.entrySet()) {
            var app = entry.getValue();
            var desired = repo.getDesiredState(app.branch(), app.path());
            reconcileApp(app.name(), desired);
        }
    }

    private void reconcileApp(String appName, List<Resource> desired) {
        for (var res : desired) {
            liveState.computeIfAbsent(appName, k -> new ConcurrentHashMap<>())
                .put(res.name(), res);
        }
        System.out.println("Reconciled " + appName + " — desired state applied.");
    }

    void injectDrift(String appName, Resource driftedResource) {
        liveState.computeIfAbsent(appName, k -> new ConcurrentHashMap<>())
            .put(driftedResource.name(), driftedResource);
    }

    String status() {
        var sb = new StringBuilder();
        for (var entry : liveState.entrySet()) {
            sb.append("App: ").append(entry.getKey()).append("\n");
            for (var res : entry.getValue().values()) {
                var repoResource = findDesired(entry.getKey(), res.name());
                var outOfSync = repoResource != null && !repoResource.equals(res);
                sb.append("  ").append(res.kind()).append("/").append(res.name());
                sb.append(" image=").append(res.image());
                sb.append(outOfSync ? " [OUT OF SYNC]" : " [SYNCED]");
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private Resource findDesired(String appName, String resName) {
        var app = apps.get(appName);
        if (app == null) return null;
        return repo.getDesiredState(app.branch(), app.path()).stream()
            .filter(r -> r.name().equals(resName)).findFirst().orElse(null);
    }
}

record GitOpsApplication(String name, String branch, String path) {}
