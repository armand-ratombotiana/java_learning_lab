# Code Deep Dive: Snowflake Data Cloud

See Java source files in src/main/java/com/dataeng/nine/ for complete implementations:

- SnowflakeConnector.java: JDBC connection management, query execution, warehouse CRUD
- WarehouseManager.java: Virtual warehouse lifecycle (create, resize, suspend, resume, list)
- ClusteringManager.java: Clustering key management, depth analysis, reclustering

Key patterns:
```java
// Connection
Properties props = new Properties();
props.put("user", user); props.put("password", password);
props.put("warehouse", warehouse);
Connection conn = DriverManager.getConnection(
    "jdbc:snowflake://account.snowflakecomputing.com", props);

// Time Travel
Statement stmt = conn.createStatement();
stmt.execute("SELECT * FROM table AT (TIMESTAMP => '2024-01-01'::TIMESTAMP)");

// Zero-Copy Clone
stmt.execute("CREATE TABLE clone_table CLONE source_table");
```
