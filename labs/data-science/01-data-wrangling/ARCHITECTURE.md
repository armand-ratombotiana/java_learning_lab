# Architecture: Data Wrangling Pipeline Design

## High-Level Architecture

```
┌─────────────┐    ┌──────────────┐    ┌──────────────┐    ┌─────────────┐
│  Data Sources│───>│  Wrangling   │───>│   Feature    │───>│  ML Model   │
│  (CSV, DB,   │    │  Pipeline    │    │   Store      │    │  Training    │
│   API, S3)   │    │  (Java App)  │    │  (Parquet)   │    │              │
└─────────────┘    └──────────────┘    └──────────────┘    └─────────────┘
                           │
                    ┌──────┴──────┐
                    │  Validation  │
                    │  Dashboard   │
                    └─────────────┘
```

## Component Responsibilities

### Data Loader
- Reads from heterogeneous sources (S3, JDBC, local FS)
- Implements retry with exponential backoff
- Validates source schemas before load

### Transformer Chain
- Each transformer implements `DataFrame → DataFrame`
- Transformers are composed via builder pattern
- Every transformer logs row-count delta

```java
public interface Transformer {
    DataFrame transform(DataFrame input);
}

public class Pipeline {
    private List<Transformer> stages = new ArrayList<>();
    
    public Pipeline addStage(Transformer t) {
        stages.add(t);
        return this;
    }
    
    public DataFrame run(DataFrame input) {
        DataFrame current = input;
        for (Transformer t : stages) {
            int before = current.rowCount();
            current = t.transform(current);
            logger.info("{}: {} → {} rows", t.getClass().getSimpleName(), before, current.rowCount());
        }
        return current;
    }
}
```

### Validator
- Schema checks: column presence, types, nullability
- Range checks: min/max/unique constraints
- Distribution checks: mean/std within expected bounds

### Writer
- Writes to Parquet (columnar, compressed) for downstream consumption
- Registers dataset in feature store with version hash
- Triggers downstream pipeline notification
