# History of Exploratory Data Analysis

## 1960s: The Seeds

John Tukey, working at Bell Labs, argued that classical statistics (which focused on hypothesis testing) was insufficient for understanding real data. He advocated for graphical and numerical techniques that "let the data speak."

## 1977: EDA Is Born

Tukey published *Exploratory Data Analysis* — a book with no formulas, only graphs and practical techniques. He introduced:
- **Box plot** (visual summary of distribution)
- **Stem-and-leaf plot** (histogram with data preserved)
- **Scatterplot matrix** (all pairwise relationships)
- **Median polish** (robust smoothing for two-way tables)
- **Resistant statistics** (median, IQR) — not sensitive to outliers

## 1980s–1990s: Computing EDA

- Bell Labs' S language (precursor to R) built EDA into the language
- The concept of **data mining** emerged as EDA scaled to large databases
- Anscombe's quartet (1973) demonstrated that summary statistics alone are deeply misleading

## 2000s–2020s: Automated EDA

- Pandas profiling, Sweetviz, D-Tale (Python) automate EDA report generation
- Java: Tablesaw's `summary()`, Smile's descriptive stats, Spark's `describe()`
- Interactive EDA: Observable Plot, Vega-Lite

```java
// Modern Java EDA: automated profiling
public class DataProfiler {
    public static void profile(Table data) {
        System.out.println("=== DATA PROFILE ===");
        System.out.println("Rows: " + data.rowCount());
        System.out.println("Columns: " + data.columnCount());
        System.out.println("Memory: " + data.byteSize() / 1024 / 1024 + " MB");
        data.columnNames().forEach(col -> {
            Column<?> c = data.column(col);
            System.out.printf("  %s (%s): %d missing, %d unique%n",
                col, c.type(), c.countMissing(), c.countUnique());
        });
    }
}
```
