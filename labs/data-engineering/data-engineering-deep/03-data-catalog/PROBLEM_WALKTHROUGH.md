# Lab 03: Problem Walkthrough — Schema Evolution Manager

## Problem

Build a `SchemaEvolutionManager` that tracks schema versions, implements BACKWARD and FORWARD compatibility checks, and suggests migration steps when breaking changes are detected.

## Walkthrough

### Step 1: Schema Diff

```java
public record SchemaDiff(List<FieldAdded> added, List<FieldRemoved> removed, List<FieldChanged> changed) {
    public record FieldAdded(String name, SchemaField field) {}
    public record FieldRemoved(String name, SchemaField field) {}
    public record FieldChanged(String name, SchemaField oldField, SchemaField newField) {}
}
```

### Step 2: Compute Diff

```java
public SchemaDiff diff(Schema oldSchema, Schema newSchema) {
    var oldFields = oldSchema.fields().stream().collect(Collectors.toMap(SchemaField::name, f -> f));
    var newFields = newSchema.fields().stream().collect(Collectors.toMap(SchemaField::name, f -> f));
    List<FieldAdded> added = new ArrayList<>();
    List<FieldRemoved> removed = new ArrayList<>();
    List<FieldChanged> changed = new ArrayList<>();
    for (var entry : newFields.entrySet()) {
        if (!oldFields.containsKey(entry.getKey())) added.add(new FieldAdded(entry.getKey(), entry.getValue()));
        else if (!oldFields.get(entry.getKey()).equals(entry.getValue()))
            changed.add(new FieldChanged(entry.getKey(), oldFields.get(entry.getKey()), entry.getValue()));
    }
    for (var entry : oldFields.entrySet()) {
        if (!newFields.containsKey(entry.getKey())) removed.add(new FieldRemoved(entry.getKey(), entry.getValue()));
    }
    return new SchemaDiff(added, removed, changed);
}
```

### Step 3: Compatibility Check

```java
public boolean isBackwardCompatible(Schema oldSchema, Schema newSchema) {
    // BACKWARD: new schema can read old data
    // New fields must have defaults, removed fields must be optional
    var diff = diff(oldSchema, newSchema);
    for (var added : diff.added()) {
        if (!added.field().nullable() && added.field().defaultValue() == null) return false;
    }
    return true;
}
```

### Step 4: Migration Suggestions

```java
public List<String> suggestMigrations(SchemaDiff diff) {
    List<String> suggestions = new ArrayList<>();
    for (var removed : diff.removed()) {
        suggestions.add("DROP column '" + removed.name() + "' — this is a BREAKING change.");
    }
    for (var changed : diff.changed()) {
        suggestions.add("ALTER column '" + changed.name() + "' — verify downstream consumers.");
    }
    return suggestions;
}
```

## Complexity

- **Time**: O(F) where F = number of fields in the schema
- **Space**: O(F) for storing field maps
