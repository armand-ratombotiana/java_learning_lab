# Code Deep Dive: Data Observability

See Java source files in src/main/java/com/dataeng/eighteen/ for:
- AnomalyDetector.java: Volume, freshness, and distribution anomaly detection
- DataProfiler.java: Automated profiling with statistical analysis

Key patterns:
```java
// Anomaly detection
AnomalyDetector detector = new AnomalyDetector();
VolumeAnomaly anomaly = detector.detectVolumeAnomaly(
    currentRowCount, historicalRowCounts);
if (anomaly.isAnomaly()) {
    alertService.sendAlert(anomaly);
}

// Data profiling
DataProfiler profiler = new DataProfiler();
ProfileResult result = profiler.profile(dataset);
System.out.println("Null rate: " + result.getNullRate());
System.out.println("Distinct rate: " + result.getDistinctRate());
```
