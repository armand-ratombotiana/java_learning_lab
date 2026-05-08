# Exercises - Consul

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