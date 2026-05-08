package com.learning.gitops;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {

    static class GitRepository {
        private final String name;
        private final String defaultBranch;
        private final List<Commit> commits = new CopyOnWriteArrayList<>();

        record Commit(String id, String message, String author, long timestamp) {}

        GitRepository(String name, String defaultBranch) {
            this.name = name; this.defaultBranch = defaultBranch;
        }

        Commit commit(String message, String author) {
            var id = UUID.randomUUID().toString().substring(0, 7);
            var commit = new Commit(id, message, author, System.currentTimeMillis());
            commits.add(commit);
            return commit;
        }

        List<Commit> log() { return List.copyOf(commits); }
    }

    static class Pipeline {
        private final String name;
        private final List<Stage> stages = new ArrayList<>();

        record Stage(String name, Runnable action) {}

        Pipeline(String name) { this.name = name; }

        Pipeline stage(String name, Runnable action) {
            stages.add(new Stage(name, action));
            return this;
        }

        boolean execute() {
            System.out.println("  Pipeline: " + name);
            for (var stage : stages) {
                System.out.print("    " + stage.name() + "... ");
                try {
                    stage.action().run();
                    System.out.println("OK");
                } catch (Exception e) {
                    System.out.println("FAILED (" + e.getMessage() + ")");
                    return false;
                }
            }
            return true;
        }
    }

    static class Environment {
        private final String name;
        private String currentVersion;

        Environment(String name, String initialVersion) {
            this.name = name;
            this.currentVersion = initialVersion;
        }

        void deploy(String version) {
            currentVersion = version;
            System.out.println("    Deployed v" + version + " to " + name);
        }

        String getVersion() { return currentVersion; }
    }

    public static void main(String[] args) {
        System.out.println("=== GitOps Lab ===\n");

        gitOpsPrinciples();
        gitAsSingleSource();
        ciCdPipeline();
        environments();
        argoCd();
    }

    static void gitOpsPrinciples() {
        System.out.println("--- GitOps Principles ---");
        System.out.println("""
  1. Declarative configuration - entire system defined in Git
  2. Git as single source of truth - desired state is in Git
  3. Automated reconciliation - operator ensures live state matches Git
  4. Pull-based deployment - operator pulls from Git, no CI/CD push

  Benefits:
  - Full audit trail (every change is a Git commit)
  - Easy rollback (revert commit / checkout previous)
  - Standard development workflow (PRs, reviews, approvals)
  - Disaster recovery (recreate cluster from Git)

  Tooling:
  - ArgoCD (Kubernetes)
  - Flux CD (Kubernetes)
  - Terraform Cloud / Atlantis (infrastructure)
  - Crossplane (control plane)
    """);
    }

    static void gitAsSingleSource() {
        System.out.println("\n--- Git as Single Source of Truth ---");
        var repo = new GitRepository("my-app-config", "main");

        var repoStructure = """
  my-app-config/
  ├── apps/
  │   ├── payment/
  │   │   ├── deployment.yaml
  │   │   ├── service.yaml
  │   │   └── kustomization.yaml
  │   └── web/
  │       ├── deployment.yaml
  │       └── ingress.yaml
  ├── infrastructure/
  │   ├── terraform/
  │   │   ├── main.tf
  │   │   └── variables.tf
  │   └── monitoring/
  │       ├── prometheus.yaml
  │       └── grafana.yaml
  ├── environments/
  │   ├── dev/
  │   │   └── kustomization.yaml
  │   ├── staging/
  │   │   └── kustomization.yaml
  │   └── prod/
  │       └── kustomization.yaml
  └── README.md
  """;

        System.out.println(repoStructure);

        repo.commit("Increase payment service replicas to 5", "Alice");
        repo.commit("Add canary deployment config", "Bob");
        repo.commit("Update PostgreSQL version to 16", "Alice");

        System.out.println("  Git log:");
        repo.log().forEach(c -> System.out.printf("    %s %s - %s%n", c.id(), c.message(), c.author()));
    }

    static void ciCdPipeline() {
        System.out.println("\n--- CI/CD Pipeline ---");
        var repo = new GitRepository("app", "main");
        repo.commit("feat: add new API endpoint", "dev-user");

        var pipeline = new Pipeline("Build & Deploy")
            .stage("Checkout", () -> {})
            .stage("Build", () -> {
                if (Math.random() > 0.9) throw new RuntimeException("Compilation error");
            })
            .stage("Unit Tests", () -> {
                if (Math.random() > 0.9) throw new RuntimeException("Test failure");
            })
            .stage("Docker Build", () -> {})
            .stage("Push to Registry", () -> {})
            .stage("Deploy to Dev", () -> System.out.print(""))
            .stage("Integration Tests", () -> {
                if (Math.random() > 0.9) throw new RuntimeException("Integration test failed");
            })
            .stage("Deploy to Staging", () -> System.out.print(""))
            .stage("Smoke Tests", () -> {})
            .stage("Deploy to Production", () -> System.out.print(""));

        boolean success = pipeline.execute();
        System.out.println("  Result: " + (success ? "PASSED" : "FAILED"));

        System.out.println("""
  CI/CD workflow:
  PR -> CI checks (build + test) -> merge to main
  -> build container image -> push to registry
  -> update manifests in config repo (new image tag)
  -> GitOps operator detects drift -> deploys
    """);
    }

    static void environments() {
        System.out.println("\n--- Environment Promotion ---");
        var dev = new Environment("dev", "1.2.0");
        var staging = new Environment("staging", "1.1.0");
        var prod = new Environment("prod", "1.0.0");

        System.out.println("  Current versions:");
        for (var env : List.of(dev, staging, prod))
            System.out.println("    " + env.name + ": v" + env.getVersion());

        System.out.println("\n  Promotion workflow:");
        System.out.println("    PR merges to main -> CI builds v1.2.0");

        String version = "1.2.0";
        dev.deploy(version);

        System.out.println("    Dev tests pass -> create PR to promote to staging");
        staging.deploy(version);

        System.out.println("    Staging tests pass -> create PR to promote to prod");
        prod.deploy(version);

        System.out.println("""
  Promotion via Git: each environment is a directory/branch
  dev:   commit image tag v1.2.0   -> operator syncs dev
  stg:   PR: update tag -> merge   -> operator syncs staging
  prod:  PR: update tag -> merge   -> operator syncs prod
  Rollback: revert the commit in Git
    """);
    }

    static void argoCd() {
        System.out.println("\n--- ArgoCD ---");
        System.out.println("""
  ArgoCD = declarative GitOps for Kubernetes

  Architecture:
  - ArgoCD runs in cluster
  - Watches Git repository for changes
  - Compares desired state (Git) vs live state (cluster)
  - Auto-syncs (or manual sync with approval)

  App definition:
    apiVersion: argoproj.io/v1alpha1
    kind: Application
    metadata:
      name: my-app
    spec:
      source:
        repoURL: https://github.com/team/config
        path: apps/payment
        targetRevision: main
      destination:
        server: https://kubernetes.default.svc
        namespace: prod
      syncPolicy:
        automated:
          prune: true
          selfHeal: true

  Sync strategies:
  - Auto: automatically apply changes from Git
  - Manual: require human approval for each sync
  - Hook: run jobs before/after sync (DB migration)

  Health checks: ArgoCD understands K8s resource health
  Rollback: sync to previous commit in Git
    """);
    }
}
