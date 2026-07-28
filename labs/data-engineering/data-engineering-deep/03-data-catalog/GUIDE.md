# Lab 03: Data Catalog — Implementation Guide

## Step 1: Dataset Model

```java
public record Dataset(String id, String name, String description, String owner, String location,
                      Schema schema, List<String> tags, Instant createdAt, Instant updatedAt) {}
```

## Step 2: Schema Model with Versioning

```java
public record Schema(List<SchemaField> fields, int version, String compatibilityMode) {
    public record SchemaField(String name, DataType type, String description, boolean nullable, String defaultValue) {}
    public enum DataType { STRING, INTEGER, LONG, DOUBLE, BOOLEAN, TIMESTAMP, ARRAY, MAP, STRUCT }
}
```

## Step 3: Schema Compatibility Check

```java
public enum CompatibilityCheck { BACKWARD, FORWARD, FULL, NONE }

public boolean checkCompatibility(Schema existing, Schema proposed, CompatibilityCheck mode) {
    return switch (mode) {
        case BACKWARD -> canRead(proposed, existing); // new reader can read old data
        case FORWARD -> canRead(existing, proposed);  // old reader can read new data
        case FULL -> canRead(proposed, existing) && canRead(existing, proposed);
        case NONE -> true;
    };
}
```

## Step 4: Catalog Service

```java
public class DataCatalog {
    private final Map<String, Dataset> datasets = new ConcurrentHashMap<>();
    private final Map<String, List<Schema>> schemaHistory = new ConcurrentHashMap<>();
    private final InvertedIndex index = new InvertedIndex();

    public void register(Dataset dataset) {
        datasets.put(dataset.id(), dataset);
        schemaHistory.computeIfAbsent(dataset.id(), k -> new ArrayList<>()).add(dataset.schema());
        index.index(dataset);
    }

    public List<Dataset> search(String query) { return index.search(query); }
    public List<Dataset> searchByTag(String tag) { return index.searchByTag(tag); }
}
```

## Step 5: Inverted Index

```java
public class InvertedIndex {
    private final Map<String, Set<String>> termToIds = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> tagToIds = new ConcurrentHashMap<>();

    public void index(Dataset ds) {
        String text = (ds.name() + " " + ds.description()).toLowerCase();
        for (String word : text.split("\\W+")) {
            termToIds.computeIfAbsent(word, k -> ConcurrentHashMap.newKeySet()).add(ds.id());
        }
        for (String tag : ds.tags()) {
            tagToIds.computeIfAbsent(tag, k -> ConcurrentHashMap.newKeySet()).add(ds.id());
        }
    }

    public List<Dataset> search(String query) {
        String[] terms = query.toLowerCase().split("\\s+");
        return Arrays.stream(terms)
            .map(termToIds::get)
            .filter(Objects::nonNull)
            .reduce((a, b) -> { var r = new HashSet<>(a); r.retainAll(b); return r; })
            .orElse(Set.of()).stream().map(id -> datasets.get(id)).filter(Objects::nonNull).toList();
    }
}
```
