# Interview Questions: PL/SQL Advanced (Oracle Focus)

## Oracle-Specific Questions
- Explain pipelined table functions in PL/SQL — how do they improve performance for ETL processes?
- How does the PL/SQL result cache work? Explain `RESULT_CACHE` pragma and `RELIES_ON` clause.
- What is Virtual Private Database (VPD) in PL/SQL? How do you implement `DBMS_RLS` policies?
- Explain Oracle Scheduler: `DBMS_SCHEDULER` jobs, schedules, programs, and chains.
- How does Oracle Advanced Queuing (AQ) work? Explain `ENQUEUE`, `DEQUEUE`, and propagation.
- Explain ORDS AutoREST with PL/SQL: how to expose PL/SQL procedures as REST endpoints.
- What is Oracle SODA (Simple Oracle Document Access) and how does it work with PL/SQL?
- How do you profile and tune PL/SQL? Explain `DBMS_PROFILER`, `DBMS_HPROF`, and `DBMS_TRACE`.
- How does PL/SQL unit testing work? Explain `utPLSQL` v3 and code coverage.
- What is Oracle's DBMS_PREPROCESSOR and DBMS_METADATA for PL/SQL source analysis?

## Google Cloud / Technical
- Cloud SQL PostgreSQL vs Oracle for stored procedure logic
- Cloud Functions vs Oracle PL/SQL for event-driven processing
- Pub/Sub vs Oracle AQ for message queuing

## Microsoft / Azure
- Azure SQL elastic jobs vs Oracle Scheduler
- Service Bus vs Oracle Advanced Queuing
- Azure Functions vs Oracle PL/SQL for business logic

## Amazon / AWS
- AWS Lambda vs Oracle PL/SQL scheduled jobs
- Amazon SQS vs Oracle AQ for message queuing
- AWS Step Functions vs Oracle Scheduler chains

## Apple
- PL/SQL for Apple enterprise data processing pipelines
- Advanced PL/SQL for Apple supplier compliance systems

## LeetCode-Style Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| Pipeline | Data Transformation | Hard | Pipelined Function |
| Caching | Result Set Cache | Medium | RESULT_CACHE |
| Scheduling | Job Automation | Medium | DBMS_SCHEDULER |
| Queuing | Message Processing | Hard | AQ Enqueue/Dequeue |

## Production Scenarios
- Scenario 1: "Pipelined table function causing memory exhaustion — not using PIPELINED correctly"
- Scenario 2: "Result cache returning stale data — RELIES_ON not tracking dependencies"
- Scenario 3: "AQ queue growing unbounded — dequeuer not keeping up"
- Scenario 4: "VPD policy function blocking all queries — policy function bug"

## Interview Patterns & Tips
- Advanced PL/SQL interviews test pipelined functions, result cache, AQ, and profiling
- Know Oracle's enterprise features: Scheduler, AQ, VPD, and when to use each
- OCP Advanced PL/SQL certification covers these enterprise features
- PL/SQL architects: $140K-$200K
- Experience with Oracle enterprise features is rare and highly valued
