# Scalability - HISTORY

## Timeline

### 1960s-1980s: Mainframe Era
- Single massive machines handle all processing
- Scaling = buy a bigger mainframe
- IBM System/360 dominated

### 1990s: Client-Server & Vertical Scaling
- Scaling meant upgrading server hardware
- Sun Microsystems "big iron" servers
- Load balancers introduced for web farms

### 2000s: Horizontal Scaling Emerges
- 2001: Google's MapReduce paper — scale-out paradigm
- 2004: Amazon Dynamo paper — distributed key-value store
- 2006: Amazon EC2 launches — elastic compute
- Memcached popularizes distributed caching

### 2010s: Cloud-Native Scaling
- 2010: Netflix moves to AWS cloud
- 2013: Docker containers enable reproducible scaling
- 2015: Kubernetes orchestrates container scaling
- Auto-scaling groups become standard

### 2020s: Serverless & Edge
- Lambda/Functions scale to zero
- Edge computing reduces latency
- Database scaling: Vitess, CockroachDB, PlanetScale
- Predictive auto-scaling using ML

## Java's Scalability Evolution
- 2004: J2EE clustering and session replication
- 2014: Spring Boot + Spring Cloud for cloud-native apps
- 2017: Spring WebFlux (reactive, non-blocking)
- 2022: Virtual threads (Project Loom) for lightweight concurrency
