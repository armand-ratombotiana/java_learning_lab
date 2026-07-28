# Lab 08: Problem Walkthrough — Data Contract CI/CD Integration

## Problem

Build a `ContractCIIntegrator` that runs contract validation as a step in a CI/CD pipeline, preventing breaking changes from being deployed and notifying affected consumers.

## Walkthrough

### Step 1: CI Event Model

```java
public record CiEvent(String pipelineId, String branch, String commitHash,
                      Schema proposedSchema, Instant timestamp) {}

public record ValidationReport(boolean passed, List<String> errors,
                               List<String> warnings, List<String> affectedConsumers) {}
```

### Step 2: Run Full Validation Suite

```java
public class ContractCIIntegrator {
    private final ContractRegistry registry;
    private final SchemaEvolutionTest evolutionTest;

    public ValidationReport validate(CiEvent event) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        Set<String> affectedConsumers = new HashSet<>();

        for (var contract : registry.findActive()) {
            // 1. Schema existence check
            var result = new ContractValidator().validate(contract, event.proposedSchema());
            if (!result.passed()) {
                errors.addAll(result.errors());
                affectedConsumers.addAll(contract.consumers());
            }

            // 2. Evolution compatibility
            var existingSchema = registry.getLatestSchema(contract.producer());
            if (existingSchema != null) {
                boolean compatible = evolutionTest.testCompatibility(
                    existingSchema, event.proposedSchema(), CompatibilityMode.BACKWARD);
                if (!compatible) {
                    errors.add("Schema evolution breaks backward compatibility for contract " + contract.id());
                    affectedConsumers.addAll(contract.consumers());
                }
            }
        }
        return new ValidationReport(errors.isEmpty(), errors, warnings, List.copyOf(affectedConsumers));
    }
}
```

### Step 3: Notify Affected Consumers

```java
public class ConsumerNotifier {
    public void notify(ValidationReport report, CiEvent event) {
        if (!report.affectedConsumers().isEmpty()) {
            for (String consumer : report.affectedConsumers()) {
                sendNotification(consumer, "Pipeline " + event.pipelineId()
                    + " has a breaking change affecting your data contract.");
            }
        }
    }
}
```

### Step 4: Block Deployment if Critical

```java
public class DeploymentGuard {
    public boolean canDeploy(ValidationReport report) {
        if (!report.passed()) {
            System.err.println("ERROR: Deployment blocked — " + report.errors().size() + " contract violations");
            return false;
        }
        return true;
    }
}
```

## Complexity

- **Time**: O(C * F) where C = contracts, F = fields per schema
- **Space**: O(C + E) for contract loads + error storage
