# Architecture

```
[Flink Client] -> [JobManager] -> [TaskManager 1] [TaskManager 2]
                                  [Task Slots]    [Task Slots]
```

## Spring Boot Integration
```java
@Bean
public StreamExecutionEnvironment flinkEnv() {
    return StreamExecutionEnvironment.getExecutionEnvironment();
}
```
