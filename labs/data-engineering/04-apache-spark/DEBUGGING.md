# Debugging

## OOM
- Check Spark UI Executors tab
- Increase executor memory
- Enable off-heap memory

## Skew
- Check task time distribution
- Add salting: key + "_" + salt
- Enable AQE: spark.sql.adaptive.skewJoin.enabled=true

## Plan Inspection
```java
dataset.explain("formatted");
dataset.explain("cost");
```
