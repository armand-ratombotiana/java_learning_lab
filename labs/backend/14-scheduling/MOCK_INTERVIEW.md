# Mock Interview: Scheduling (Lab 14)

**Role:** Backend Engineer (Mid-Level)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** How does Spring's `@Scheduled` annotation work?

**Candidate:** `@Scheduled` enables method-level scheduling using a `TaskScheduler`. It supports:
- `fixedRate`: Invoke every N milliseconds (overlap allowed if execution exceeds interval)
- `fixedDelay`: Invoke N milliseconds after previous execution completes
- `cron`: Cron expression for complex schedules: `@Scheduled(cron = "0 0 * * * *")` for hourly
- `initialDelay`: Wait N milliseconds before first execution

Enable scheduling with `@EnableScheduling` on a configuration class. Spring creates a `ThreadPoolTaskScheduler` with default pool size of 1.

**Interviewer:** What's the difference between `fixedRate` and `fixedDelay`?

**Candidate:** 
- `fixedRate`: Method is invoked at fixed intervals, regardless of execution time. If execution exceeds interval, multiple invocations run concurrently.
- `fixedDelay`: Next invocation waits for the previous to complete plus the delay. No concurrent executions.

Use `fixedDelay` for tasks that must not overlap (data consistency). Use `fixedRate` for tasks that must maintain regular cadence (heartbeats, health checks).

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you handle distributed scheduling across multiple application instances?

**Candidate:** `@Scheduled` on each instance leads to duplicate executions in multi-instance deployments. Solutions:

1. **ShedLock:** Database-backed lock for scheduled tasks
```java
@Scheduled(cron = "0 0 * * * *")
@SchedulerLock(name = "cleanupExpiredSessions", lockAtLeastFor = "5m")
public void cleanupExpiredSessions() { ... }
```
Uses a database table to track lock ownership. Only one instance executes.

2. **Quartz with JDBC JobStore:** Persistent scheduling with cluster support
3. **Kubernetes CronJob:** Let K8s handle scheduling at the platform level
4. **Leader election:** Only the leader node runs scheduled tasks (Spring Integration, Hazelcast)
5. **Distributed task queue:** Publish tasks to RabbitMQ/Kafka, consumers compete for execution

**Interviewer:** How do you gracefully stop long-running scheduled tasks during shutdown?

**Candidate:** 
1. **TaskScheduler's `awaitTermination`:** Configure `ThreadPoolTaskScheduler` with `setAwaitTerminationSeconds` and `setWaitForTasksToCompleteOnShutdown(true)`.
2. **PreDestroy hook:** Add `@PreDestroy` method to signal tasks to stop.
3. **Grace period:** `spring.lifecycle.timeout-per-shutdown-phase=30s` for graceful shutdown.
4. **Health check integration:** During shutdown, mark instance unhealthy so traffic stops, then wait for tasks to complete.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a distributed job scheduling system for data pipelines that must process 10K jobs/day with no duplicates.

**Candidate:** 

**Architecture:**
```
Job Definition → PostgreSQL (job_store)
                     ↓
              Scheduler (Quartz clustered)
                     ↓
           ┌─────────┴─────────┐
           │                   │
      Job Queue           Dead Letter
      (Kafka topic)       Queue
           │
    ┌──────┴──────┐
    │             │
 Worker Pool   Worker Pool
  (K8s Pods)   (K8s Pods)
```

**Implementation:**
1. **Quartz with JDBC JobStore** for persistent, clustered scheduling
2. **Quartz's misfire handling** — `MISFIRE_INSTRUCTION_DO_NOTHING` for non-critical, `MISFIRE_INSTRUCTION_FIRE_ONCE_NOW` for time-sensitive
3. **Kafka for job distribution** — each scheduled job publishes a Kafka event, workers compete
4. **Idempotency key** — each job has `jobId + scheduledTime` to prevent duplicate processing
5. **Monitoring** — Prometheus metrics for job duration, failure rate, queue depth

**SQL for job store tables:**
```sql
CREATE TABLE scheduled_jobs (
    id UUID PRIMARY KEY,
    job_type VARCHAR(100) NOT NULL,
    cron_expression VARCHAR(100),
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE job_executions (
    id UUID PRIMARY KEY,
    job_id UUID REFERENCES scheduled_jobs(id),
    status VARCHAR(20) NOT NULL, -- PENDING, RUNNING, SUCCESS, FAILED
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    error_message TEXT,
    UNIQUE(job_id, started_at) -- prevent duplicates
);
```

**Interviewer:** How do you handle a cron job that needs to run at exactly midnight but the processing takes 3 hours?

**Candidate:** The issue is overlap — if the next midnight arrives while the previous run is still processing. Solutions:
1. **`fixedDelay` equivalent for cron:** Use Quartz with `MISFIRE_INSTRUCTION_DO_NOTHING` — skip if previous is still running
2. **State machine in job:** Check `job_executions` table for pending/running status before starting
3. **Distributed lock:** Use Redisson `RSemaphore` or `RLock` around the job execution
4. **Abort previous run:** Before starting new execution, send cancel signal to previous (works for batch jobs with checkpoints)
5. **Incremental processing:** Split the 3-hour job into smaller chunks processed throughout the day

---

## Interviewer Feedback

**Strengths:** Good distributed scheduling knowledge, practical Quartz usage  
**Areas to Improve:** Could discuss Airflow vs Quartz for complex DAG scheduling  
**Verdict:** Hire

---

*Lab 14 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
