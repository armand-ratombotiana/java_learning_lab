# How Data Wrangling Works

Data wrangling follows a pipeline of discrete stages. Each stage consumes a DataFrame and produces a transformed DataFrame.

## Pipeline Stages

### 1. Discovery — profile the data
```java
Table t = Table.read().csv("raw.csv");
System.out.println(t.shape());        // 50000 x 34
System.out.println(t.missingCount());
System.out.println(t.summary());
```

### 2. Structuring — parse and type columns
```java
t.replaceColumn("date", t.dateColumn("date").asLocalDate());
t.replaceColumn("zip", t.stringColumn("zip").padStart(5, '0'));
```

### 3. Cleaning — fix or remove bad values
```java
t = t.dropRowsWithMissingValues(t.columnNames().toArray(new String[0]));
t = t.dropDuplicateRows();
```

### 4. Enriching — add derived columns
```java
t.addColumn("age_at_enroll", 
    t.dateColumn("enroll_date").year().subtract(t.dateColumn("birth_date").year()));
```

### 5. Validating — assert quality constraints
```java
Preconditions.checkState(t.rowCount() == expectedRows);
Preconditions.checkState(t.doubleColumn("salary").min() >= 0);
```

### 6. Publishing — write clean output
```java
t.write().csv("clean_data.csv");
```

## Key Concept: Immutability

Modern Java wrangling pipelines treat DataFrames as immutable — each operation creates a new copy. This prevents side effects and makes pipelines debuggable by allowing snapshots at any stage.
