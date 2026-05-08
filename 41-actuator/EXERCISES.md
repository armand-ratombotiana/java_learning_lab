# Exercises - Actuator

## Exercise 1: Built-in Endpoints
Explore actuator endpoints:

1. Access `/actuator/health` and check component status
2. View `/actuator/metrics` for available metrics
3. Explore `/actuator/info` endpoint
4. Access `/actuator/env` for configuration properties

## Exercise 2: Custom Health Indicator
Create application-specific health checks:

1. Implement `HealthIndicator` for database connectivity
2. Check external service (mock) availability
3. Return custom status with details
4. View composite health in `/actuator/health`

## Exercise 3: Custom Endpoint
Build custom management operations:

1. Create custom GET endpoint via `@Endpoint`
2. Implement diagnostic operation
3. Add parameter support with `@Selector`
4. Test endpoint with cURL

## Exercise 4: Security Configuration
Secure actuator endpoints:

1. Configure endpoint exposure (show/hide)
2. Implement custom security for sensitive endpoints
3. Set up role-based access control
4. Test unauthorized access scenarios

## Exercise 5: Integration with Monitoring
Connect actuator to monitoring stack:

1. Enable Prometheus metrics endpoint
2. Configure info contributor with build info
3. Add custom metrics via Micrometer
4. Verify metrics in Prometheus/Grafana

## Bonus Challenge
Create a shutdown endpoint that: requires authentication, gracefully stops accepting new requests, waits for in-flight requests to complete, then terminates.