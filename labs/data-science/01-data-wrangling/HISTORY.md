# History of Data Wrangling

## 1960s–1980s: Manual Cleaning

Data entered via punch cards and magnetic tape. Cleaning meant re-punching cards. The term "data wrangling" did not exist; it was called "data cleaning" or "data preparation."

## 1990s: ETL Emerges

Data warehousing popularized **Extract, Transform, Load** (ETL). Tools like Informatica and DataStage automated data movement. Wrangling lived in SQL statements layered inside ETL pipelines.

## 2000s: Python & R Enter

Pandas (2008) and dplyr (2014) made interactive wrangling accessible to data scientists. The term "data wrangling" was popularized by Hadley Wickham's 2014 paper *Tidy Data*.

## 2010s: Visual Wrangling

Tools like Trifacta and Alteryx offered GUI-based wrangling. Google's OpenRefine (2010) gave analysts spreadsheet-like cleaning without code.

## 2020s: Automated & Scala/Java Native

Spark's DataFrame API, Tablesaw, and Smile bring wrangling to the JVM with lazy evaluation and in-memory columnar storage. Automated data validation libraries like Great Expectations and Deequ (AWS, built on Spark) codify wrangling as testable assertions.

```java
// Modern Java wrangling with Tablesaw (2020s)
Table sales = Table.read().csv("sales_2024.csv");
Table clean = sales
    .dropRowsWithMissingValues()
    .where(sales.stringColumn("region").isNotEqualTo("NULL"))
    .sortOn("date");
```
