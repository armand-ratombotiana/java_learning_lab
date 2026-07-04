# Security — AWS Database

## RDS Security

### Encryption at Rest
```powershell
# Enable encryption at launch only (cannot encrypt existing unencrypted DB)
aws rds create-db-instance --engine mysql --master-username admin `
    --master-password ChangeMe1! --allocated-storage 20 `
    --db-instance-class db.t3.micro --db-instance-identifier secure-db `
    --storage-encrypted --kms-key-id alias/rds-key
```

### IAM Database Authentication
```java
// Connect using IAM credentials token (no password in code)
AuthTokenGenerator tokenGenerator = AuthTokenGenerator.builder()
    .jdbcUrl("jdbc:mysql://mydb.xxx.us-east-1.rds.amazonaws.com:3306")
    .username("iam_user")
    .region(Region.US_EAST_1).build();

String token = tokenGenerator.generateToken();

// Use token as password in JDBC connection
// Token expires in 15 minutes, auto-refreshes
```

## DynamoDB Security

### Fine-Grained Access Control
```json
{
  "Effect": "Allow",
  "Action": ["dynamodb:GetItem", "dynamodb:PutItem"],
  "Resource": "arn:aws:dynamodb:us-east-1:xxx:table/Orders",
  "Condition": {
    "ForAllValues:StringEquals": {
      "dynamodb:LeadingKeys": ["${cognito-identity.amazonaws.com:sub}"]
    }
  }
}
```
- Users can only access items where partition key matches their identity
- No application-level filtering — enforced at DynamoDB level

### DAX Encryption in Transit
```java
// DAX cluster with encryption
AmazonDaxClient.builder()
    .region(Region.US_EAST_1)
    .endpoint("dax-cluster.xxx.clustercfg.dax.use1.amazonaws.com:8111")
    .clientConfiguration(ClientConfiguration.builder()
        .useInsecureTLSUrl(false)  // TLS encryption
        .build())
    .build();
```

## ElastiCache Security

### Redis Auth Token
```powershell
# Create cluster with auth token
aws elasticache create-cache-cluster --cache-cluster-id secure-cache `
    --engine redis --cache-node-type cache.t3.micro --num-cache-nodes 1 `
    --auth-token MySecretToken123!
```

```java
// Java client with auth
Jedis jedis = new Jedis("secure-cache.xxx.cache.amazonaws.com", 6379);
jedis.auth("MySecretToken123!");
```

### VPC Isolation
- ElastiCache should never be publicly accessible
- Security group: allow port 6379 (Redis) from application SG only
- Same VPC as application servers
- No internet gateway access
