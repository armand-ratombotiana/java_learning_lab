# Reflection — AWS Database

## Self-Assessment

| Skill | Know | Explain | Teach |
|-------|:----:|:-------:|:-----:|
| RDS provisioning and configuration | ☐ | ☐ | ☐ |
| Multi-AZ and read replicas | ☐ | ☐ | ☐ |
| DynamoDB table and index design | ☐ | ☐ | ☐ |
| DynamoDB capacity planning | ☐ | ☐ | ☐ |
| Aurora storage architecture | ☐ | ☐ | ☐ |
| ElastiCache Redis setup and usage | ☐ | ☐ | ☐ |
| Connection pooling best practices | ☐ | ☐ | ☐ |
| Database security (encryption, IAM auth) | ☐ | ☐ | ☐ |
| Database migration strategies | ☐ | ☐ | ☐ |

## Journal Prompts

1. Would you choose RDS or DynamoDB for your next Java project? What factors drive the decision?

2. How does Aurora's compute-storage separation change your thinking about database scaling?

3. At what point does adding ElastiCache make sense for a Java web application?

4. How would you design a multi-region database for an application with global users?

5. What database metrics would you monitor in production, and what thresholds trigger alerts?

## Key Takeaways
- RDS for relational (ACID, joins); DynamoDB for NoSQL (scale, latency)
- Multi-AZ provides HA; Read Replicas provide read scaling
- Aurora: 5x MySQL performance through log-storage separation
- Always use connection pooling (HikariCP for Java, RDS Proxy for Lambda)
- Redis cache-aside pattern reduces DB load by 80-95%
- Design DynamoDB keys for even distribution (avoid hot partitions)
- Encrypt everything at rest and in transit
