# Security

## SASL/SSL
```java
props.put(SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
props.put(SASL_MECHANISM, "SCRAM-SHA-512");
props.put(SASL_JAAS_CONFIG, "...");
```

## ACLs
Grant read/write permissions for source/sink topics and changelog topics.
