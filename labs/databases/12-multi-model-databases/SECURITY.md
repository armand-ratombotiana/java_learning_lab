# Security: Multi-Model & Polyglot

## Multi-Database Security Challenges

### Increased Attack Surface
Each database is a potential entry point. Secure each:
- PostgreSQL: SSL, pg_hba.conf, role-based access
- MongoDB: authentication, IP whitelist, SCRAM
- Redis: `requirepass`, rename commands, network isolation
- Neo4j: RBAC, SSL, bolt encryption
- Elasticsearch: X-Pack security, TLS

### Credential Management
```java
// BAD: hardcoded credentials for each DB
// GOOD: centralized secrets management
@Configuration
public class DatabaseSecrets {
    @Value("${db.mongo.uri}")  // from Vault/env
    private String mongoUri;

    @Value("${db.redis.password}")
    private String redisPassword;
}
```

### Network Segmentation
```yaml
# Docker Compose – isolate database networks
networks:
  rdbms_net:  # PostgreSQL accessible only by app
  cache_net:  # Redis accessible only by app
  search_net: # Elasticsearch accessible only by app

services:
  postgres:
    networks: [rdbms_net]
  redis:
    networks: [cache_net]
  app:
    networks: [rdbms_net, cache_net, search_net]
```

### Data Classification
| Data Type | Database | Encryption |
|---|---|---|
| Payments | PostgreSQL (encrypted) | Column-level + TLS |
| Sessions | Redis (in-memory) | TLS only |
| Products | MongoDB | TLS |
| Logs | Elasticsearch | TLS + index-level encryption |
