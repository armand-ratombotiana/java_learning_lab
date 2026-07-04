# Debugging

## Missing Catalog Entries
Check auto-discovery scheduler is running.

## Broken Lineage
Verify OpenLineage Spark listener is configured:
```java
spark.conf().set("spark.extraListeners",
    "io.openlineage.spark.agent.OpenLineageSparkListener");
```

## Policy Not Working
```java
AccessDecision result = policyEngine.evaluate(
    AccessRequest.builder().user("test").resource("finance").build());
```
