# Incident Response Runbook Checklist: Kafka Consumer Lag

## Incident ID: ____________________
## Date: ____________________
## Responder: ____________________

## Severity Classification

- [ ] P0: Lag > 1M messages, revenue-impacting, real-time processing critical
- [ ] P1: Lag > 100K, SEV2, significant data freshness impact
- [ ] P2: Lag > 10K, non-critical, investigation needed
- [ ] P3: Lag > 1K, informational, monitoring anomaly

## Immediate Response (First 5 Minutes)

### Detection
- [ ] Confirm consumer lag via CLI:
  ```bash
  kafka-consumer-groups.sh --bootstrap-server <broker> --group <group> --describe
  ```
- [ ] Check production rate vs consumption rate (Grafana)
- [ ] Identify affected topics and partitions
- [ ] Check consumer group state (STABLE / REBALANCING / DEAD)
- [ ] Check consumer count vs expected count

### Declaration
- [ ] Declare incident in PagerDuty if lag > 100K or rebalance storm
- [ ] Create Slack channel: #inc-<incident-short-name>
- [ ] Post initial situation report:
  ```
  INC-XXXX | SEV[X] | Kafka Consumer Lag
  Consumer group: [name]
  Topic: [topic]
  Lag: [X] messages
  Growth rate: [X] msg/min
  Consumer count: [X]/[Y] expected
  Started at: [time]
  ```
- [ ] Notify Data Platform team
- [ ] Notify Application team

## Assessment (5-15 Minutes)

### Consumer Diagnostics
- [ ] Check consumer application logs:
  ```bash
  kubectl logs -n <namespace> -l app=<consumer-app> --tail=200
  ```
- [ ] Check for errors (DB connection, API timeouts, processing exceptions)
- [ ] Check consumer configuration:
  ```bash
  kubectl exec <pod> -n <namespace> -- cat /etc/config/application.yml | grep -A 20 kafka
  ```
- [ ] Verify `group.instance.id` is set
- [ ] Verify `partition.assignment.strategy` is CooperativeStickyAssignor

### Downstream Dependency Check
- [ ] Check database connection pool:
  ```bash
  kubectl exec <pod> -n <namespace> -- curl -s localhost:8080/actuator/metrics/hikaricp.connections.active
  ```
- [ ] Check database query performance (slow queries, locks, migration running)
- [ ] Check API dependency health (downstream HTTP services)
- [ ] Check for recent schema migrations or deployments

### Rebalance Analysis
- [ ] Check rebalance count:
  ```bash
  # PromQL
  rate(kafka_consumer_group_rebalance_count{group="<group>"}[1h])
  ```
- [ ] Check broker logs for rebalance events:
  ```bash
  grep "Rebalance" /var/log/kafka/server.log | tail -50
  ```
- [ ] Identify rebalance trigger (session timeout, max.poll.interval, member departure)
- [ ] Check each consumer's last heartbeat time

## Rapid Remediation (5-20 Minutes)

### Path A: Scale Out Consumers (Fast — Add Capacity)
- [ ] Increase consumer replicas:
  ```bash
  kubectl scale deployment <consumer> --replicas=<2x current>
  ```
- [ ] Verify new consumers join group
- [ ] Monitor lag decrease rate
- [ ] Re-assess after 5 minutes

### Path B: Increase Batch Size (If Processing Throttled)
- [ ] Update consumer config:
  ```yaml
  max.poll.records: 5000
  max.poll.interval.ms: 600000
  ```
- [ ] Rolling restart of consumer pods
- [ ] Monitor processing rate increase

### Path C: Fix Downstream Dependency (If DB/API Bottleneck)
- [ ] Kill stuck queries/migrations:
  ```sql
  SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE state = 'active' AND query LIKE 'CREATE INDEX%';
  ```
- [ ] Scale database connections:
  ```sql
  ALTER SYSTEM SET max_connections = 200;
  ```
- [ ] Verify connection pool recovers

### Path D: Adjust Session Timeout (If Rebalance Storm)
- [ ] Update consumer config:
  ```yaml
  session.timeout.ms: 60000
  max.poll.interval.ms: 600000
  ```
- [ ] OR add static group membership:
  ```yaml
  group.instance.id: ${HOSTNAME}
  partition.assignment.strategy: org.apache.kafka.clients.consumer.CooperativeStickyAssignor
  ```

## Investigation (20-60 Minutes)

### Deep Dive — Lag Cause
- [ ] Compare production vs consumption rate:
  ```bash
  kafka-run-class.sh kafka.tools.JmxTool \
    --object-name kafka.consumer:type=consumer-fetch-manager-metrics,client-id=* \
    --attributes records-consumed-rate
  ```
- [ ] Check each partition's lag distribution (unbalanced?)
- [ ] Check consumer processing time histogram
- [ ] Review application code for blocking operations in poll thread
- [ ] Check for thread pool exhaustion

### Deep Dive — Rebalance Storm Root Cause
- [ ] Are session timeouts happening?
- [ ] Is max.poll.interval.ms exceeded?
- [ ] Are consumers crashing (OOM, error)?
- [ ] Is network connectivity stable?
- [ ] Are heartbeats being processed?

### Deep Dive — Data Integrity
- [ ] Check for duplicate processing (at-least-once semantics)
- [ ] Check for poison pill messages (unparseable events)
- [ ] Check for offset commit failures
- [ ] Verify last committed offsets are correct

## Recovery Verification

- [ ] Lag decreasing below 10K:
  ```bash
  kafka-consumer-groups.sh --bootstrap-server <broker> \
    --group <group> --describe | awk '{sum+=$5} END {print sum}'
  ```
- [ ] Rebalance rate below 1/hour
- [ ] Consumer count matches expected
- [ ] Processing time < 100ms average
- [ ] DB connection pool < 80% usage
- [ ] No errors in consumer logs
- [ ] Alert resets in Prometheus
- [ ] Grafana dashboard shows green

## Post-Incident Actions

### Immediate Fixes
- [ ] Add static group membership if missing
- [ ] Switch to cooperative rebalancing
- [ ] Increase max.poll.records
- [ ] Add async processing pattern
- [ ] Reduce lag alert threshold (10K)

### Configuration Standardization
- [ ] Add consumer config validation to CI/CD
- [ ] Create consumer configuration template
- [ ] Add mandatory group.instance.id policy
- [ ] Add mandatory CooperativeStickyAssignor policy
- [ ] Create consumer health check runbook

### Monitoring Improvements
- [ ] Add lag growth rate alert
- [ ] Add rebalance rate alert
- [ ] Add DB connection pool to Kafka dashboard
- [ ] Add consumer processing time histogram
- [ ] Create consumer health scoreboard

## Long-Term Preventive Actions

### Architecture
- [ ] Implement async processing for all Kafka consumers
- [ ] Add circuit breaker for downstream dependencies
- [ ] Implement consumer auto-scaling based on lag
- [ ] Add consumer-side rate limiting for graceful degradation
- [ ] Implement dead letter queue (DLQ) for poison pill messages

### Testing
- [ ] Add load testing for consumer groups at peak traffic + 20%
- [ ] Add chaos testing for consumer failures (rebalance scenarios)
- [ ] Add DB failure testing (connection pool exhaustion)
- [ ] Add rebalance storm testing

## Escalation Contacts

| Role | Name | Phone | Email |
|------|------|-------|-------|
| Data Platform Lead | | | |
| App Team Lead | | | |
| DB Admin | | | |
| SRE Lead | | | |
| Confluent Support | | | |
| VP Engineering | | | |

## Lessons Learned

### What went well:
- _________________________________________________________________

### What went wrong:
- _________________________________________________________________

### What we will improve:
- _________________________________________________________________

## Sign-Off

- [ ] Incident report completed: ____________________
- [ ] Root cause analysis completed: ____________________
- [ ] All action items assigned: ____________________
- [ ] Post-mortem scheduled: ____________________
- [ ] Final approval by SRE Director: ____________________

---

*This checklist references Confluent Blog, LinkedIn Engineering Blog, Netflix Tech Blog, and Apache Kafka documentation.*
