# Lab 15 — Disaster Recovery Failover: Code Examples

## Automated Failover Orchestrator

```java
import java.net.http.*;
import java.net.URI;
import java.time.*;
import java.util.concurrent.*;

public class FailoverOrchestrator {
    private final String primaryRegion;
    private final String secondaryRegion;
    private final HealthChecker healthChecker;
    private final DnsManager dnsManager;
    private final DatabaseFailover dbFailover;
    private volatile boolean failedOver = false;

    public FailoverOrchestrator(String primary, String secondary,
                                HealthChecker hc, DnsManager dns, DatabaseFailover db) {
        this.primaryRegion = primary;
        this.secondaryRegion = secondary;
        this.healthChecker = hc;
        this.dnsManager = dns;
        this.dbFailover = db;
    }

    public FailoverResult executeFailover() {
        System.out.println("=== FAILOVER ORCHESTRATOR ===");
        System.out.println("Primary: " + primaryRegion);
        System.out.println("Secondary: " + secondaryRegion);

        long startTime = System.currentTimeMillis();

        try {
            // Step 1: Verify primary is indeed down
            if (healthChecker.isHealthy(primaryRegion)) {
                System.out.println("Primary region appears healthy. Verifying...");
                if (!healthChecker.isHealthy(primaryRegion)) {
                    return new FailoverResult(false, "Primary still healthy", 0);
                }
            }

            // Step 2: Notify stakeholders
            notifyStakeholders("INITIATING_FAILOVER", "Primary region " + primaryRegion + " is down");

            // Step 3: Database failover
            System.out.println("Step 1/5: Failing over database...");
            boolean dbSuccess = dbFailover.promoteToPrimary(secondaryRegion);
            if (!dbSuccess) throw new RuntimeException("Database failover failed");

            // Step 4: Update DNS
            System.out.println("Step 2/5: Updating DNS to secondary region...");
            boolean dnsSuccess = dnsManager.failoverTo(secondaryRegion);
            if (!dnsSuccess) throw new RuntimeException("DNS failover failed");

            // Step 5: Scale up secondary
            System.out.println("Step 3/5: Scaling up secondary region...");
            scaleUpSecondary();

            // Step 6: Verify secondary is healthy
            System.out.println("Step 4/5: Verifying secondary health...");
            if (!healthChecker.isHealthy(secondaryRegion)) {
                throw new RuntimeException("Secondary region not healthy after failover");
            }

            // Step 7: Run data integrity check
            System.out.println("Step 5/5: Running data integrity checks...");
            boolean dataOk = verifyDataIntegrity();
            if (!dataOk) {
                notifyStakeholders("DATA_INTEGRITY_WARNING",
                        "Data inconsistency detected during failover");
            }

            long duration = System.currentTimeMillis() - startTime;
            failedOver = true;
            System.out.println("=== FAILOVER COMPLETE in " + duration + "ms ===");

            notifyStakeholders("FAILOVER_COMPLETE",
                    "Traffic now served from " + secondaryRegion);

            return new FailoverResult(true, "Failover successful", duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            System.err.println("FAILOVER FAILED after " + duration + "ms: " + e.getMessage());
            notifyStakeholders("FAILOVER_FAILED", e.getMessage());
            return new FailoverResult(false, e.getMessage(), duration);
        }
    }

    private void scaleUpSecondary() {
        // Scale up logic — API call to cloud provider
    }

    private boolean verifyDataIntegrity() {
        return true; // Implement data verification
    }

    private void notifyStakeholders(String status, String message) {
        System.out.println("[NOTIFICATION] " + status + ": " + message);
    }

    public boolean isFailedOver() { return failedOver; }

    interface HealthChecker { boolean isHealthy(String region); }
    interface DnsManager { boolean failoverTo(String region); }
    interface DatabaseFailover { boolean promoteToPrimary(String region); }

    record FailoverResult(boolean success, String message, long durationMs) {}
}
```

## Health Checker with Cross-Region Verification

```java
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public class HealthCheckerImpl implements FailoverOrchestrator.HealthChecker {
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Override
    public boolean isHealthy(String region) {
        String healthUrl = "https://" + region + ".example.com/healthz";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(healthUrl))
                    .GET()
                    .timeout(Duration.ofSeconds(5))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200
                    && response.body().contains("\"status\":\"ok\"");
        } catch (Exception e) {
            System.err.println("Health check failed for " + region + ": " + e.getMessage());
            return false;
        }
    }
}
```

## DR Drill Automation

```java
import java.time.*;
import java.util.*;

public class DrDrillRunner {
    private final FailoverOrchestrator orchestrator;
    private final List<String> drillSteps = new ArrayList<>();

    public DrDrillRunner(FailoverOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public DrDrillResult runDrill() {
        System.out.println("=== DR DRILL INITIATED ===");
        System.out.println("Date: " + Instant.now());

        long startTime = System.currentTimeMillis();

        try {
            // Step 1: Document starting state
            recordStep("Documenting pre-drill state");

            // Step 2: Simulate primary region failure
            recordStep("Simulating primary region failure");

            // Step 3: Execute failover
            recordStep("Executing failover");
            var result = orchestrator.executeFailover();
            if (!result.success()) {
                return new DrDrillResult(false, result.message(), result.durationMs(), drillSteps);
            }

            // Step 4: Test application functionality
            recordStep("Testing application in secondary region");
            boolean appWorks = testApplication();
            if (!appWorks) {
                return new DrDrillResult(false, "Application not working in secondary", 
                        System.currentTimeMillis() - startTime, drillSteps);
            }

            // Step 5: Failback to primary
            recordStep("Failing back to primary region");
            // Execute failback...

            // Step 6: Verify primary works
            recordStep("Verifying primary region");
            boolean primaryWorks = failbackVerification();
            if (!primaryWorks) {
                return new DrDrillResult(false, "Primary not working after failback",
                        System.currentTimeMillis() - startTime, drillSteps);
            }

            long totalDuration = System.currentTimeMillis() - startTime;
            System.out.println("=== DRILL COMPLETED SUCCESSFULLY in " + totalDuration + "ms ===");
            return new DrDrillResult(true, "All steps passed", totalDuration, drillSteps);

        } catch (Exception e) {
            long totalDuration = System.currentTimeMillis() - startTime;
            System.err.println("DRILL FAILED: " + e.getMessage());
            return new DrDrillResult(false, e.getMessage(), totalDuration, drillSteps);
        }
    }

    private void recordStep(String description) {
        drillSteps.add(Instant.now() + ": " + description);
        System.out.println("  → " + description);
    }

    boolean testApplication() { return true; }
    boolean failbackVerification() { return true; }

    record DrDrillResult(boolean success, String message, long durationMs, List<String> steps) {}
}
```

## Unit Tests

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FailoverOrchestratorTest {
    @Test
    void testFailoverWhenPrimaryDown() {
        var hc = new FailoverOrchestrator.HealthChecker() {
            public boolean isHealthy(String r) { return r.equals("secondary"); }
        };
        var dns = r -> { System.out.println("DNS → " + r); return true; };
        var db = () -> true;

        var orchestrator = new FailoverOrchestrator("primary", "secondary", hc,
                (FailoverOrchestrator.DnsManager) dns,
                (FailoverOrchestrator.DatabaseFailover) db);

        var result = orchestrator.executeFailover();
        assertTrue(result.success(), "Failover should succeed");
    }

    @Test
    void testRTOIsMet() {
        // Verify RTO < 5 minutes
        assertTimeoutPreemptively(
            java.time.Duration.ofMinutes(5),
            () -> {
                var orchestrator = createMockOrchestrator();
                var result = orchestrator.executeFailover();
                assertTrue(result.success());
                assertTrue(result.durationMs() < 300000,
                        "RTO: Failover completed in " + result.durationMs() + "ms");
            }
        );
    }

    private FailoverOrchestrator createMockOrchestrator() {
        return new FailoverOrchestrator(
            "primary", "secondary",
            r -> r.equals("secondary"),
            r -> true,
            () -> true
        );
    }
}
```
