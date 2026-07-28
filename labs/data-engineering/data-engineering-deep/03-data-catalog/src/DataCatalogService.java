package com.dataengineering.deep.lab03;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataCatalogService {

    public enum DataType { STRING, INTEGER, LONG, DOUBLE, BOOLEAN, TIMESTAMP, ARRAY, MAP, STRUCT }

    public record SchemaField(String name, DataType type, String description, boolean nullable, String defaultValue) {}

    public record Schema(List<SchemaField> fields, int version, String compatibilityMode) {}

    public record Dataset(String id, String name, String description, String owner, String location,
                          Schema schema, List<String> tags, Instant createdAt, Instant updatedAt) {}

    public enum CompatibilityCheck { BACKWARD, FORWARD, FULL, NONE }

    public record SchemaDiff(List<FieldAdded> added, List<FieldRemoved> removed, List<FieldChanged> changed) {
        public record FieldAdded(String name, SchemaField field) {}
        public record FieldRemoved(String name, SchemaField field) {}
        public record FieldChanged(String name, SchemaField oldField, SchemaField newField) {}
    }

    public static class SchemaEvolutionManager {
        public SchemaDiff diff(Schema oldSchema, Schema newSchema) {
            var oldFields = oldSchema.fields().stream().collect(Collectors.toMap(SchemaField::name, f -> f));
            var newFields = newSchema.fields().stream().collect(Collectors.toMap(SchemaField::name, f -> f));
            List<SchemaDiff.FieldAdded> added = new ArrayList<>();
            List<SchemaDiff.FieldRemoved> removed = new ArrayList<>();
            List<SchemaDiff.FieldChanged> changed = new ArrayList<>();
            for (var entry : newFields.entrySet()) {
                if (!oldFields.containsKey(entry.getKey())) added.add(new SchemaDiff.FieldAdded(entry.getKey(), entry.getValue()));
                else if (!oldFields.get(entry.getKey()).equals(entry.getValue()))
                    changed.add(new SchemaDiff.FieldChanged(entry.getKey(), oldFields.get(entry.getKey()), entry.getValue()));
            }
            for (var entry : oldFields.entrySet()) {
                if (!newFields.containsKey(entry.getKey())) removed.add(new SchemaDiff.FieldRemoved(entry.getKey(), entry.getValue()));
            }
            return new SchemaDiff(added, removed, changed);
        }

        public boolean checkCompatibility(Schema existing, Schema proposed, CompatibilityCheck mode) {
            return switch (mode) {
                case BACKWARD -> isBackwardCompatible(existing, proposed);
                case FORWARD -> isForwardCompatible(existing, proposed);
                case FULL -> isBackwardCompatible(existing, proposed) && isForwardCompatible(existing, proposed);
                case NONE -> true;
            };
        }

        private boolean isBackwardCompatible(Schema oldSchema, Schema newSchema) {
            var diff = diff(oldSchema, newSchema);
            for (var added : diff.added()) {
                if (!added.field().nullable() && added.field().defaultValue() == null) return false;
            }
            return true;
        }

        private boolean isForwardCompatible(Schema oldSchema, Schema newSchema) {
            var diff = diff(oldSchema, newSchema);
            for (var removed : diff.removed()) {
                if (!removed.field().nullable() && removed.field().defaultValue() == null) return false;
            }
            return true;
        }

        public List<String> suggestMigrations(SchemaDiff diff) {
            List<String> suggestions = new ArrayList<>();
            for (var removed : diff.removed()) {
                suggestions.add("BREAKING: DROP column '" + removed.name() + "' — verify all consumers.");
            }
            for (var changed : diff.changed()) {
                suggestions.add("NOTICE: ALTER column '" + changed.name() + "' type from " + changed.oldField().type() + " to " + changed.newField().type());
            }
            return suggestions;
        }
    }

    public static class InvertedIndex {
        private final Map<String, DataCatalogService.Dataset> datasets = new ConcurrentHashMap<>();
        private final Map<String, Set<String>> termToIds = new ConcurrentHashMap<>();
        private final Map<String, Set<String>> tagToIds = new ConcurrentHashMap<>();

        public void index(DataCatalogService.Dataset ds) {
            datasets.put(ds.id(), ds);
            String text = (ds.name() + " " + ds.description()).toLowerCase();
            for (String word : text.split("\\W+")) {
                if (!word.isEmpty()) termToIds.computeIfAbsent(word, k -> ConcurrentHashMap.newKeySet()).add(ds.id());
            }
            for (String tag : ds.tags()) {
                tagToIds.computeIfAbsent(tag.toLowerCase(), k -> ConcurrentHashMap.newKeySet()).add(ds.id());
            }
        }

        public List<DataCatalogService.Dataset> search(String query) {
            String[] terms = query.toLowerCase().split("\\s+");
            return Arrays.stream(terms).map(termToIds::get).filter(Objects::nonNull)
                .reduce((a, b) -> { var r = new HashSet<>(a); r.retainAll(b); return r; })
                .orElse(Set.of()).stream().map(datasets::get).filter(Objects::nonNull).toList();
        }

        public List<DataCatalogService.Dataset> searchByTag(String tag) {
            return tagToIds.getOrDefault(tag.toLowerCase(), Set.of()).stream().map(datasets::get)
                .filter(Objects::nonNull).toList();
        }
    }

    public static class DataCatalog {
        private final Map<String, DataCatalogService.Dataset> datasets = new ConcurrentHashMap<>();
        private final Map<String, List<Schema>> schemaHistory = new ConcurrentHashMap<>();
        private final InvertedIndex index = new InvertedIndex();

        public void register(DataCatalogService.Dataset dataset) {
            datasets.put(dataset.id(), dataset);
            schemaHistory.computeIfAbsent(dataset.id(), k -> new ArrayList<>()).add(dataset.schema());
            index.index(dataset);
        }

        public Optional<DataCatalogService.Dataset> getById(String id) { return Optional.ofNullable(datasets.get(id)); }
        public List<DataCatalogService.Dataset> search(String query) { return index.search(query); }
        public List<DataCatalogService.Dataset> searchByTag(String tag) { return index.searchByTag(tag); }
        public List<Schema> getSchemaHistory(String datasetId) { return schemaHistory.getOrDefault(datasetId, List.of()); }

        public DataCatalogService.Dataset updateSchema(String datasetId, Schema newSchema) {
            var existing = datasets.get(datasetId);
            if (existing == null) throw new IllegalArgumentException("Dataset not found: " + datasetId);
            var updated = new DataCatalogService.Dataset(existing.id(), existing.name(), existing.description(),
                existing.owner(), existing.location(), newSchema, existing.tags(), existing.createdAt(), Instant.now());
            datasets.put(datasetId, updated);
            schemaHistory.get(datasetId).add(newSchema);
            return updated;
        }
    }

    public static void main(String[] args) {
        var schema = new Schema(List.of(
            new SchemaField("id", DataType.STRING, "Unique identifier", false, null),
            new SchemaField("name", DataType.STRING, "User name", true, null),
            new SchemaField("age", DataType.INTEGER, "User age", true, "0")
        ), 1, "BACKWARD");

        var ds = new Dataset("ds_users", "users", "User profile data", "data-team",
            "s3://data/warehouse/users", schema, List.of("pii", "core"), Instant.now(), Instant.now());

        var catalog = new DataCatalog();
        catalog.register(ds);

        var newSchema = new Schema(List.of(
            new SchemaField("id", DataType.STRING, "Unique identifier", false, null),
            new SchemaField("name", DataType.STRING, "User full name", true, null),
            new SchemaField("email", DataType.STRING, "Email address", true, null)
        ), 2, "BACKWARD");

        var evolution = new SchemaEvolutionManager();
        var diff = evolution.diff(schema, newSchema);
        System.out.println("Schema diff: " + diff);
        System.out.println("Backward compatible: " + evolution.checkCompatibility(schema, newSchema, CompatibilityCheck.BACKWARD));
        System.out.println("Suggestions: " + evolution.suggestMigrations(diff));
    }
}
