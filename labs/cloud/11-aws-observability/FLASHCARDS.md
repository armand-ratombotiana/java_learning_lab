# Flashcards - AWS Observability

## Card 1
**Front**: What is 
**Back**: AWS Observability is a cloud computing pattern for building scalable, maintainable distributed systems through abstraction, modularity, and best practices.

## Card 2
**Front**: Key design principles?
**Back**: Single responsibility, loose coupling, high cohesion, interface segregation, dependency inversion.

## Card 3
**Front**: How to make AWS Observability fault-tolerant?
**Back**: Circuit breakers, retries with backoff, bulkheads, graceful degradation, health checks.

## Card 4
**Front**: What metrics should AWS Observability expose?
**Back**: RED metrics: Rate (requests/sec), Errors (error rate), Duration (latency distribution).

## Card 5
**Front**: Library vs service difference?
**Back**: Library is in-process; service is separate process with network communication.

## Card 6
**Front**: How to handle configuration?
**Back**: Externalize using environment variables, config files, or config servers.

## Card 7
**Front**: Testing strategies for 
**Back**: Unit tests for logic, integration tests for interactions, contract tests for APIs, E2E for workflows.

## Card 8
**Front**: How to ensure observability?
**Back**: Structured logging, metrics export (Prometheus), distributed tracing (OpenTelemetry), health checks.

## Card 9
**Front**: Role of a service registry?
**Back**: Service discovery: services find each other by logical name instead of hardcoded addresses.

## Card 10
**Front**: How to deploy to production?
**Back**: Containerize with Docker, orchestrate with Kubernetes, use blue/green or canary deployments, CI/CD pipelines.
