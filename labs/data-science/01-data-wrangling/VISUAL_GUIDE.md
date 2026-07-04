# Visual Guide to Data Wrangling

## Pipeline Diagram

```
  [Raw Data]          [Discovery]         [Clean]            [Enrich]        [Publish]
     |                    |                   |                  |                |
  dirty.csv  --->  shape(), summary()  --->  dropNA, dedup  --> addColumn() --> clean.csv
     |                    |                   |                  |                |
  missing vals     profile stats         type fixes         derived cols     validated
  wrong types      column counts         outlier removal    feature eng.     schema OK
```

## Before & After: A Typical Wrangling Flow

### Before (raw)

| id | name | age | salary | joined |
|---|---|---|---|---|
| 1 | "John" | "28" | "50000" | "01/15/2022" |
| 2 | "Jane" | "thirty-five" | ? | 2022-03-01 |
| 3 | "Bob" | 42 | "90k" | "Jan 5, 2023" |
| 3 | "Bob" | 42 | "90000" | "2023-01-05" |

### After (clean)

| id | name | age | salary | joined |
|---|---|---|---|---|
| 1 | John | 28 | 50000.0 | 2022-01-15 |
| 2 | Jane | 35 | 75000.0 | 2022-03-01 |
| 3 | Bob | 42 | 90000.0 | 2023-01-05 |

## Visual Encoding Decisions

| Decision | Visualization |
|---|---|
| Missing value pattern | Heatmap (rows x columns, missing in red) |
| Outlier detection | Box plot per column |
| Distribution before/after norm | Histogram overlay |
| Duplicate rows | Bar chart by count |
| Data type summary | Color-coded table |

```java
// Generate a missing-value heatmap flag (conceptual)
Table missingFlags = Table.create("missing");
for (String col : table.columnNames()) {
    IntColumn flags = IntColumn.create(col, table.column(col).isMissing());
    missingFlags.addColumn(flags);
}
// Pass flags to a plotting library to render the heatmap
```
