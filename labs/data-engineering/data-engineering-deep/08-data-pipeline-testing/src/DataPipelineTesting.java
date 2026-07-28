package com.dataengineering.deep.lab08;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataPipelineTesting {

    public enum DataType { STRING, INTEGER, LONG, DOUBLE, BOOLEAN, TIMESTAMP }

    public record SchemaField(String name, DataType type, boolean nullable, String defaultValue) {}
    public record Schema(List<SchemaField> fields, int version) {}

    public record DataContract(String id, String producer, List<String> consumers, int version,
                               Schema schema, List<String> slas, Instant validFrom, Instant validUntil) {}

    public record ValidationResult(boolean passed, List<String> errors) {}

    public static class ContractValidator {
        public ValidationResult validate(DataContract contract, Schema actualSchema) {
            List<String> errors = new ArrayList<>();
            for (var field : contract.schema().fields()) {
                boolean exists = actualSchema.fields().stream().anyMatch(f -> f.name().equals(field.name()));
                if (!exists) errors.add("Missing field: " + field.name());
            }
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

    public enum CompatibilityMode { BACKWARD, FORWARD, FULL, NONE }

    public static class SchemaEvolutionTest {
        public boolean testCompatibility(Schema oldSchema, Schema newSchema, CompatibilityMode mode) {
            return switch (mode) {
                case BACKWARD -> canRead(newSchema, oldSchema);
                case FORWARD -> canRead(oldSchema, newSchema);
                case FULL -> canRead(newSchema, oldSchema) && canRead(oldSchema, newSchema);
                case NONE -> true;
            };
        }
        private boolean canRead(Schema reader, Schema writer) {
            for (var wf : writer.fields()) {
                var rf = reader.fields().stream().filter(f -> f.name().equals(wf.name())).findFirst();
                if (rf.isEmpty() && wf.defaultValue() == null) return false;
            }
            return true;
        }
    }

    public static class ContractRegistry {
        private final Map<String, DataContract> contracts = new ConcurrentHashMap<>();
        private final Map<String, Schema> latestSchemas = new ConcurrentHashMap<>();

        public void register(DataContract contract) {
            contracts.put(contract.id(), contract);
            latestSchemas.put(contract.producer(), contract.schema());
        }

        public DataContract getContract(String id) { return contracts.get(id); }

        public List<DataContract> findByConsumer(String consumerId) {
            return contracts.values().stream().filter(c -> c.consumers().contains(consumerId)).toList();
        }

        public List<DataContract> findByProducer(String producerId) {
            return contracts.values().stream().filter(c -> c.producer().equals(producerId)).toList();
        }

        public List<DataContract> findActive() {
            Instant now = Instant.now();
            return contracts.values().stream()
                .filter(c -> now.isBefore(c.validUntil()) && now.isAfter(c.validFrom())).toList();
        }

        public Schema getLatestSchema(String producer) { return latestSchemas.get(producer); }
    }

    public record CiEvent(String pipelineId, String branch, String commitHash,
                          Schema proposedSchema, Instant timestamp) {}

    public record ValidationReport(boolean passed, List<String> errors, List<String> warnings,
                                   List<String> affectedConsumers) {}

    public static class ContractCIIntegrator {
        private final ContractRegistry registry;
        private final SchemaEvolutionTest evolutionTest;

        public ContractCIIntegrator(ContractRegistry registry, SchemaEvolutionTest evolutionTest) {
            this.registry = registry; this.evolutionTest = evolutionTest;
        }

        public ValidationReport validate(CiEvent event, String producerId) {
            List<String> errors = new ArrayList<>();
            List<String> warnings = new ArrayList<>();
            Set<String> affectedConsumers = new HashSet<>();

            for (var contract : registry.findByProducer(producerId)) {
                var result = new ContractValidator().validate(contract, event.proposedSchema());
                if (!result.passed()) {
                    errors.addAll(result.errors());
                    affectedConsumers.addAll(contract.consumers());
                }
                var existingSchema = registry.getLatestSchema(contract.producer());
                if (existingSchema != null) {
                    boolean compatible = evolutionTest.testCompatibility(existingSchema, event.proposedSchema(), CompatibilityMode.BACKWARD);
                    if (!compatible) {
                        errors.add("Evolution breaks backward compatibility for contract " + contract.id());
                        affectedConsumers.addAll(contract.consumers());
                    }
                }
                warnings.addAll(result.errors().stream().filter(e -> e.startsWith("Warning")).toList());
            }
            return new ValidationReport(errors.isEmpty(), errors, warnings, List.copyOf(affectedConsumers));
        }
    }

    public static void main(String[] args) {
        var schemaV1 = new Schema(List.of(
            new SchemaField("id", DataType.STRING, false, null),
            new SchemaField("name", DataType.STRING, true, null)
        ), 1);
        var schemaV2 = new Schema(List.of(
            new SchemaField("id", DataType.STRING, false, null),
            new SchemaField("name", DataType.STRING, true, null),
            new SchemaField("email", DataType.STRING, true, null)
        ), 2);
        var contract = new DataContract("c1", "producer-payments", List.of("consumer-analytics", "consumer-ml"),
            1, schemaV1, List.of("latency < 5min"), Instant.now().minusSeconds(3600), Instant.now().plusSeconds(86400));
        var registry = new ContractRegistry();
        registry.register(contract);
        var evolution = new SchemaEvolutionTest();
        System.out.println("Backward compatible V1->V2: " + evolution.testCompatibility(schemaV1, schemaV2, CompatibilityMode.BACKWARD));
        var ciEvent = new CiEvent("pipeline-1", "main", "abc123", schemaV2, Instant.now());
        var integrator = new ContractCIIntegrator(registry, evolution);
        var report = integrator.validate(ciEvent, "producer-payments");
        System.out.println("CI report passed: " + report.passed() + ", errors: " + report.errors() + ", affected: " + report.affectedConsumers());
    }
}
