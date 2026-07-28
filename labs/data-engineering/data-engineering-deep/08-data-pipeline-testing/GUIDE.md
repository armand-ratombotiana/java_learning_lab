# Lab 08: Data Pipeline Testing — Implementation Guide

## Step 1: Data Contract Model

```java
public record DataContract(String id, String producer, List<String> consumers, int version,
                           Schema schema, List<String> slas, Instant validFrom, Instant validUntil) {}
```

## Step 2: Contract Validation

```java
public class ContractValidator {
    public ValidationResult validate(DataContract contract, Schema actualSchema) {
        List<String> errors = new ArrayList<>();
        // Check all producer fields exist
        for (var field : contract.schema().fields()) {
            if (actualSchema.fields().stream().noneMatch(f -> f.name().equals(field.name()))) {
                errors.add("Missing field: " + field.name());
            }
        }
        // Check type compatibility
        for (var actual : actualSchema.fields()) {
            var contracted = contract.schema().fields().stream()
                .filter(f -> f.name().equals(actual.name())).findFirst();
            if (contracted.isPresent() && !contracted.get().type().equals(actual.type())) {
                errors.add("Type mismatch for " + actual.name() + ": expected " + contracted.get().type() + ", got " + actual.type());
            }
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }
}
```

## Step 3: Consumer-Driven Contract Test

```java
public class ConsumerContractTest {
    private final ContractRegistry registry;

    public void testConsumerContract(String consumerId, Dataset actualDataset) {
        var contracts = registry.findByConsumer(consumerId);
        for (var contract : contracts) {
            ValidationResult result = validate(contract, actualDataset.schema());
            if (!result.passed()) {
                throw new ContractViolationException("Contract violated for " + contract.id() + ": " + result.errors());
            }
        }
    }
}
```

## Step 4: Schema Evolution Test

```java
public class SchemaEvolutionTest {
    public boolean testCompatibility(Schema oldSchema, Schema newSchema, CompatibilityMode mode) {
        return switch (mode) {
            case BACKWARD -> canRead(newSchema, oldSchema);
            case FORWARD -> canRead(oldSchema, newSchema);
            case FULL -> canRead(newSchema, oldSchema) && canRead(oldSchema, newSchema);
        };
    }

    private boolean canRead(Schema reader, Schema writer) {
        // Reader must have at least all fields that writer has (with defaults for missing)
        for (var wf : writer.fields()) {
            var rf = reader.fields().stream().filter(f -> f.name().equals(wf.name())).findFirst();
            if (rf.isEmpty() && wf.defaultValue() == null) return false;
        }
        return true;
    }
}
```

## Step 5: Contract Registry

```java
public class ContractRegistry {
    private final Map<String, DataContract> contracts = new ConcurrentHashMap<>();

    public void register(DataContract contract) { contracts.put(contract.id(), contract); }
    public DataContract getContract(String id) { return contracts.get(id); }

    public List<DataContract> findByConsumer(String consumerId) {
        return contracts.values().stream()
            .filter(c -> c.consumers().contains(consumerId))
            .toList();
    }

    public List<DataContract> findActive() {
        return contracts.values().stream()
            .filter(c -> Instant.now().isBefore(c.validUntil())
                && Instant.now().isAfter(c.validFrom()))
            .toList();
    }
}
```
