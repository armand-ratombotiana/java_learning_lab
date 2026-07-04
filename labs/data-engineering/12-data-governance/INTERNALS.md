# Governance Internals

## Data Catalog Model
```java
@Entity
public class DataAsset {
    @Id private String id;
    private String name; private String domain; private String owner;
    private String schemaJson; private String classification;
    private int qualityScore; private long recordCount;
}
```

## Lineage Database
```java
@Entity
public class LineageEdge {
    @Id private String id;
    private String sourceDataset; private String targetDataset;
    private String transformation; private Instant timestamp;
}
```
