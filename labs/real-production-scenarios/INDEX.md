# Real Production Scenarios Academy

A collection of real-world production troubleshooting labs drawn from FAANG, Oracle, Microsoft, Google SRE, and enterprise engineering incidents. Each lab presents a complete incident lifecycle: detection, diagnosis, root cause analysis, resolution, and prevention.

## Curriculum

| # | Lab | Scenario Source | Topic Area |
|---|-----|-----------------|------------|
| 01 | Java Memory Leak Debugging | Netflix Tech Blog | Java/JVM Performance |
| 02 | Thread Deadlock Analysis | Google SRE | Java Concurrency |
| 03 | High CPU Production Incident | Meta Engineering | Performance Profiling |
| 04 | Database Connection Pool Exhaustion | Amazon AWS RDS | Database/Backend |
| 05 | Slow Query / Deadlock Resolution | Oracle Support | Database/PL-SQL |
| 06 | Production Deployment Rollback | Microsoft Azure | DevOps/CD |
| 07 | Microservice Circuit Breaker Failure | Netflix Hystrix | Distributed Systems |
| 08 | Cache Stampede Mitigation | Meta/Facebook Memcache | Caching/Distributed |
| 09 | Disk Space / Capacity Incident | Google SRE | Infrastructure |
| 10 | Security Incident Response | Cloudflare/SolarWinds | Security |
| 11 | SSL/TLS Certificate Expiry | Let's Encrypt / Cloudflare | Networking/Security |
| 12 | Kubernetes Pod CrashLoop | Kubernetes SIG | Container/Orchestration |
| 13 | Kafka Consumer Lag Incident | Confluent / LinkedIn | Streaming/Messaging |
| 14 | API Rate Limiting Breach | GitHub / Twitter | API Gateway |
| 15 | Disaster Recovery Failover | AWS / Azure | Cloud/DR |

## Each lab contains:

| File | Description |
|------|-------------|
| README.md | Situation overview, severity, impact assessment |
| INCIDENT_REPORT.md | Full incident timeline (inspired by real incidents) |
| ROOT_CAUSE.md | Root cause analysis with 5 Whys |
| SOLUTION.md | Step-by-step fix with code examples |
| PREVENTION.md | How to prevent recurrence |
| MONITORING.md | Monitoring/alerts that should catch it |
| CHECKLIST.md | Incident response runbook checklist |
| DIAGRAMS/ | Architecture and sequence diagrams |
| src/ | Java source code reproducing / fixing the issue |

## How to use

1. Read the README.md for the situation context
2. Attempt to diagnose the issue without reading the solution
3. Review INCIDENT_REPORT.md for the full timeline
4. Study ROOT_CAUSE.md to understand what went wrong
5. Implement the SOLUTION.md in your own codebase patterns
6. Apply PREVENTION.md to your production systems
