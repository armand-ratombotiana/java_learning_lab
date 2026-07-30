# Interview Questions — Serverless Deep

## Beginner

Q: What is a cold start in serverless functions?
A: The delay when a new execution environment is initialized before handling a request.

Q: What is provisioned concurrency?
A: Pre-initialized environments kept warm to avoid cold starts for predictable traffic.

## Intermediate

Q: How does Lambda's execution environment lifecycle work?
A: Init (download code, start runtime, run init code), Invoke (handle requests), Shutdown (freeze/terminate after idle timeout).

Q: How would you reduce cold start latency for Java functions?
A: Use SnapStart (CRaC), minimize deployment package, use tiered compilation, reduce dependency count.

## Advanced

Q: How do Lambda Extensions work and what can they do?
A: Extensions run as child processes, register via Extensions API, subscribe to Telemetry/Logs API for observability, secrets retrieval, or config loading.

Q: Design a high-throughput event filtering system for Lambda.
A: Use SQS message filtering with attribute-based filtering, S3 event notification filtering by prefix/suffix, Kinesis fan-out with enhanced fan-out.
