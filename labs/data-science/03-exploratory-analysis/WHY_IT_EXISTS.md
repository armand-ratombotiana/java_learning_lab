# Why Exploratory Data Analysis Exists

EDA exists because you cannot model what you do not understand. Before fitting any model, a data scientist must understand the data's shape, distribution, missingness patterns, relationships, and quirks. John Tukey formalized EDA in 1977 as an approach that prioritizes **discovery over confirmation** — letting the data speak before imposing assumptions.

## The Gap It Bridges

- **Formal statistics** tests hypotheses you already have; EDA surfaces hypotheses you did not know to ask
- **ML models** are black boxes; EDA builds the mental model of the data before the model trains
- **Data quality checks** find errors; EDA finds meaning

```java
// EDA in Java: first 5 minutes of any dataset
Table t = Table.read().csv("dataset.csv");
System.out.println(t.summary());        // count, mean, std, min, max per column
System.out.println(t.structure());      // column names, types, unique counts
System.out.println(t.missingCount());   // nulls per column
```
