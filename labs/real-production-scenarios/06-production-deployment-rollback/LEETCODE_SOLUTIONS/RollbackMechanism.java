package com.prod.solutions.deployment;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates an automated rollback mechanism that monitors error rates
 * and automatically reverts to the previous version if thresholds are exceeded.
 *
 * Production rollback strategies include:
 * - kubectl rollout undo
 * - Swap deployment slots (Azure App Service)
 * - Traffic shifting back to old version
 * - Database migration rollback
 */
public class RollbackMechanism {

    static class DeploymentVersion {
        final String version;
        final boolean hasBug;

        DeploymentVersion(String version, boolean hasBug) {
            this.version = version;
            this.hasBug = hasBug;
        }
    }

    static class RollingDeployer {
        private final Deque<DeploymentVersion> versionHistory = new ArrayDeque<>();
        private DeploymentVersion activeVersion;
        private int errorCount = 0;
        private int requestCount = 0;
        private static final double ERROR_THRESHOLD = 0.05; // 5% error rate
        private static final int MIN_SAMPLE_SIZE = 50;

        void deploy(DeploymentVersion version) {
            if (activeVersion != null) {
                versionHistory.push(activeVersion);
            }
            activeVersion = version;
            errorCount = 0;
            requestCount = 0;
            System.out.printf("Deployed %s%n", version.version);
        }

        void simulateRequest() {
            requestCount++;
            boolean isError = activeVersion.hasBug &&
                    ThreadLocalRandom.current().nextDouble() < 0.15; // 15% error rate

            if (isError) {
                errorCount++;
                System.out.printf("  [ERROR] Request failed on %s (%d/%d errors)%n",
                        activeVersion.version, errorCount, requestCount);

                // Check if auto-rollback is needed
                if (requestCount >= MIN_SAMPLE_SIZE &&
                        (double) errorCount / requestCount > ERROR_THRESHOLD) {
                    System.out.println("  >>> ERROR THRESHOLD EXCEEDED! Auto-rolling back...");
                    rollback();
                }
            }
        }

        void rollback() {
            if (!versionHistory.isEmpty()) {
                DeploymentVersion previous = versionHistory.pop();
                activeVersion = previous;
                errorCount = 0;
                requestCount = 0;
                System.out.printf("  <<< Rolled back to %s%n", previous.version);
            } else {
                System.out.println("  <<< No previous version to rollback to!");
            }
        }

        DeploymentVersion getActiveVersion() { return activeVersion; }
    }

    public static void main(String[] args) {
        System.out.println("=== Automated Rollback Mechanism Demo ===\n");

        RollingDeployer deployer = new RollingDeployer();

        // Start with stable version
        deployer.deploy(new DeploymentVersion("v1.0.0", false));
        simulateTraffic(deployer, 100);

        // Deploy buggy version
        System.out.println("\n--- Rolling out v2.0.0 (has bug) ---");
        deployer.deploy(new DeploymentVersion("v2.0.0", true));
        simulateTraffic(deployer, 200);

        System.out.printf("%nActive version after incident: %s%n",
                deployer.getActiveVersion().version);

        // Deploy fixed version
        System.out.println("\n--- Deploying fixed v2.0.1 ---");
        deployer.deploy(new DeploymentVersion("v2.0.1", false));
        simulateTraffic(deployer, 100);

        System.out.printf("%nFinal active version: %s%n",
                deployer.getActiveVersion().version);
        System.out.println("\n=== Automated rollback prevented extended outage ===");
    }

    static void simulateTraffic(RollingDeployer deployer, int count) {
        for (int i = 0; i < count; i++) {
            deployer.simulateRequest();
            try { Thread.sleep(5); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
