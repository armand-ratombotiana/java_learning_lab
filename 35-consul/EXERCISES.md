# Exercises - Consul

## Exercise 1: Basic Service Discovery
Implement core service discovery:

1. Register a service with health check endpoint
2. Query service catalog via REST API
3. Use DNS SRV records for service discovery
4. Implement client-side load balancing
5. Configure health check intervals and timeouts

## Exercise 2: Advanced Health Checks
Configure comprehensive health monitoring:

1. Set up HTTP health check with expected response
2. Implement TCP health check for port availability
3. Create script-based health check for custom logic
4. Configure TTL-based health checks
5. Implement distributed health awareness

## Exercise 3: Key-Value Operations
Manage configuration with KV store:

1. Store and retrieve simple key-value pairs
2. Create hierarchical configuration structure
3. Implement configuration change notifications
4. Use transactions for atomic multi-key updates
5. Configure prefix-based queries with blocking

## Exercise 4: Service Mesh (Consul Connect)
Implement service-to-service security:

1. Configure Consul Connect for service communication
2. Set up intentions for allowed service communication
3. Implement mTLS between services
4. Create service identity with certificates
5. Monitor service mesh traffic

## Exercise 5: Distributed Coordination
Handle coordination patterns:

1. Implement leader election with Sessions
2. Use distributed locks for resource management
3. Create semaphore pattern for rate limiting
4. Implement configuration refresh with watches
5. Handle network partition recovery

## Bonus Challenge
Build a self-healing microservice system that:
- Automatically registers/deregisters with Consul
- Implements circuit breaker using health status
- Uses KV store for dynamic configuration
- Performs leader election for master tasks
- Monitors all services with health dashboards

## Exercise 1: Service Registration
Implement automatic service registration:

1. Configure application to register with Consul
2. Add custom metadata to service registration
3. Set up HTTP health check endpoint
4. Verify registration in Consul UI

## Exercise 2: Service Discovery
Implement client-side service discovery:

1. Use RestTemplate with Consul load balancer
2. Use Spring Cloud OpenFeign with Consul integration
3. Implement dynamic service URL resolution
4. Test failover to healthy instance

## Exercise 3: Key-Value Store
Use Consul's KV store for configuration:

1. Store application configuration in Consul KV
2. Implement dynamic configuration reload
3. Create namespace paths for environment separation
4. Use transactions for atomic updates

## Exercise 4: Health Checks
Configure comprehensive health monitoring:

1. Add TCP health check (port availability)
2. Add script-based health check
3. Configure TTL-based health checks
4. View health status in Consul UI

## Exercise 5: Distributed Locks
Implement coordination using Consul:

1. Use Spring Session with Consul backend
2. Implement distributed locking with Consul
3. Handle lock contention and timeout
4. Build leader election mechanism

## Bonus Challenge
Implement a circuit breaker pattern where services query Consul for healthy instances and automatically route to alternative services when primary is unhealthy.