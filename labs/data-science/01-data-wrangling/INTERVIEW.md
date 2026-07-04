# Interview Questions: Data Wrangling

## Conceptual

1. **Explain the difference between ETL and ELT. When would you choose each?**
   - ETL: Transform before load (schema-on-write). Better for structured analytics.
   - ELT: Load raw, transform in warehouse (schema-on-read). Better for exploration.

2. **What is tidy data? Why does it matter?**
   - Each variable is a column, each observation is a row, each value is a cell. It's the standard form that most analytical tools expect.

3. **How do you handle missing data? What's the trade-off between imputation and deletion?**
   - Deletion is simpler but loses information. Imputation preserves sample size but introduces bias if done naively.

## Coding

4. **Implement a function in Java that removes outliers by IQR.**

```java
public static Table removeOutliersIQR(Table data, String column) {
    DoubleColumn col = data.doubleColumn(column);
    double q1 = col.quartile(1);
    double q3 = col.quartile(3);
    double iqr = q3 - q1;
    return data.where(
        col.isGreaterThan(q1 - 1.5 * iqr)
            .and(col.isLessThan(q3 + 1.5 * iqr))
    );
}
```

5. **Write Java code to one-hot encode a categorical column of US states.**

```java
public static Table oneHotEncode(Table data, String column) {
    StringColumn cat = data.stringColumn(column);
    Table encoded = data.copy();
    for (String val : cat.unique()) {
        encoded.addColumn(BooleanColumn.create("is_" + val, 
            cat.isEqualTo(val)));
    }
    return encoded.dropColumns(column);
}
```

6. **How would you detect that a salary column has values in both "USD/year" and "USD/month" and normalize to yearly?**
   - Check column for values > 20000. If present, ask domain expert. Use column metadata. Or check distribution: bimodal at known yearly/monthly points suggests mixed units.
