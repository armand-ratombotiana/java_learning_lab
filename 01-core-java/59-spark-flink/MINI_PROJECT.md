# Module 59: Apache Spark & Flink - Mini Project

**Project Name**: Spark SQL Log Analytics Engine  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Set up an Apache Spark local environment using Java. Load a massive unstructured text log file, convert it into a structured DataFrame, and perform complex SQL-like aggregations using the Spark SQL API.

## 📝 Requirements

### Core Features

1. **Project Setup**:
   - Create a Maven project and include dependencies for `spark-core` and `spark-sql`.
   - Ensure you are running Java 11 or 17 (Note: Spark has strict JDK compatibility rules; JDK 17 is recommended for Spark 3.3+).

2. **Data Generation**:
   - Write a simple Java loop to generate a `server_logs.txt` file containing 100,000 lines.
   - Format: `[TIMESTAMP] [LEVEL] [IP_ADDRESS] [ENDPOINT] "MESSAGE"`
   - Mix the levels (`INFO`, `WARN`, `ERROR`, `FATAL`).

3. **Spark Session Initialization**:
   - Initialize a `SparkSession` with `.master("local[*]")` to use all CPU cores on your local machine.

4. **Data Extraction & Transformation**:
   - Read the text file into a `Dataset<Row>`.
   - Use standard Spark String functions or Regex to parse the raw text lines and extract the `LEVEL`, `ENDPOINT`, and `MESSAGE` into separate columns.

5. **Analytical Queries**:
   - **Query A**: Group by `LEVEL` and count the occurrences. Sort descending by count.
   - **Query B**: Filter to include only `ERROR` or `FATAL` levels. Group by `ENDPOINT` to find the top 5 endpoints generating the most errors.
   - Use `.show()` to print the results of the DataFrames to the console.

6. **Output**:
   - Take the result of Query B and save it as a new JSON file using `.write().json("output/error_reports")`.

---

## 💡 Solution Blueprint

1. **Spark Session & Parsing**:
   ```java
   import org.apache.spark.sql.*;
   import static org.apache.spark.sql.functions.*;

   public class LogAnalyzer {
       public static void main(String[] args) {
           SparkSession spark = SparkSession.builder()
                   .appName("LogAnalyzer")
                   .master("local[*]")
                   .getOrCreate();

           // Read raw text
           Dataset<Row> rawLogs = spark.read().text("server_logs.txt");

           // Parse via Regex (assuming format "[2024] [ERROR] [192.168.1.1] /api/users ...")
           Dataset<Row> parsedLogs = rawLogs.select(
               regexp_extract(col("value"), "\\[(.*?)\\] \\[(.*?)\\] \\[(.*?)\\] (.*?) \"(.*?)\"", 2).alias("level"),
               regexp_extract(col("value"), "\\[(.*?)\\] \\[(.*?)\\] \\[(.*?)\\] (.*?) \"(.*?)\"", 4).alias("endpoint")
           );

           // Query A: Count by Level
           parsedLogs.groupBy("level")
                     .count()
                     .orderBy(desc("count"))
                     .show();

           // Query B: Top 5 Error Endpoints
           Dataset<Row> errorReport = parsedLogs
                     .filter(col("level").isin("ERROR", "FATAL"))
                     .groupBy("endpoint")
                     .count()
                     .orderBy(desc("count"))
                     .limit(5);
                     
           errorReport.show();
           
           // Save result
           errorReport.write().mode(SaveMode.Overwrite).json("output/top_errors");

           spark.stop();
       }
   }
   ```