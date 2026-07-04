# Debugging

## Lag
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group app-id --describe
```

## Topology
```java
System.out.println(builder.build().describe());
```

## Error Handling
```java
props.put(DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
    LogAndContinueExceptionHandler.class);
```
