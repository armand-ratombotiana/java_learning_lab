# Interview Questions — Serverless Architecture

## Q1: What causes a cold start and how do you mitigate it?
**A:** Cold start happens when a new execution environment is created (download code, init runtime). Mitigation: provisioned concurrency, snap-start, keep-warm pings, and optimizing dependency size.

## Q2: How do you manage state in serverless functions?
**A:** Externalize state to managed services (DynamoDB, Redis, S3). Functions should be stateless — any local state is lost after the execution environment is recycled.

## Q3: What is function composition in serverless?
**A:** Chaining multiple functions together via event sources. A function emits an event that triggers the next function. Patterns: sequential chains, fan-out (parallel), fan-in (aggregation).

## Q4: How does FaaS invocation lifecycle work?
**A:** Init: download code, start runtime, run init code. Invoke: run handler. After idle timeout (typically 5-15 min), the environment is frozen or shut down.
