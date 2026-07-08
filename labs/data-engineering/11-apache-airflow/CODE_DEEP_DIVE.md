# Code Deep Dive: Apache Airflow

See Java source files in src/main/java/com/dataeng/eleven/ for complete implementations:

- AirflowDagGenerator.java: Programmatic DAG construction with dependency management
- DagValidator.java: Cycle detection, topological sort, critical path estimation
- SlaMonitor.java: SLA definition registration, breach detection, alerting

Key patterns:
```java
// DAG with dependencies
dagGenerator.addTask(new Task("extract", "Extract from source", 15));
dagGenerator.addTask(new Task("load", "Load to warehouse", 30));
dagGenerator.addDependency("extract", "load");

// Cycle detection
DagValidator validator = new DagValidator();
validator.addEdge("A", "B");
if (validator.hasCycle()) { /* handle error */ }
```
