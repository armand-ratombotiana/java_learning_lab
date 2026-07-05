package com.devops.gitops;

import java.util.Map;

public class ArgoCDSimulation {
    public enum SyncPolicy { AUTOMATIC, MANUAL }

    private final String appName;
    private final String repoUrl;
    private final String targetRevision;
    private SyncPolicy syncPolicy;

    public ArgoCDSimulation(String appName, String repoUrl, String targetRevision) {
        this.appName = appName;
        this.repoUrl = repoUrl;
        this.targetRevision = targetRevision;
    }

    public void setSyncPolicy(SyncPolicy policy) {
        this.syncPolicy = policy;
    }

    public void sync() {
        System.out.println("ArgoCD syncing application: " + appName);
        System.out.println("  Repo: " + repoUrl + "@" + targetRevision);
        System.out.println("  Policy: " + syncPolicy);
        System.out.println("  Health: Healthy");
        System.out.println("  Sync: Synced");
    }

    public void status() {
        System.out.println(appName + " - Synced to " + targetRevision + " (Healthy)");
    }

    public static void main(String[] args) {
        ArgoCDSimulation argocd = new ArgoCDSimulation(
            "my-app", "https://github.com/org/config.git", "main"
        );
        argocd.setSyncPolicy(SyncPolicy.AUTOMATIC);
        argocd.sync();
        argocd.status();
    }
}
