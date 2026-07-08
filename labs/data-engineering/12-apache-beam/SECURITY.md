# Security for Apache Beam

## Credentials
```java
pipeline.getOptions().as(DataflowPipelineOptions.class)
    .setGcpCredential(credentials);
```

## Encryption
- CMEK for disk encryption on Dataflow
- KMS integration for encrypted connections
- TLS for all data in transit

## Network
- VPC Service Controls for Dataflow
- Private IP for workers (no public internet access)
- Subnet configuration for data isolation
