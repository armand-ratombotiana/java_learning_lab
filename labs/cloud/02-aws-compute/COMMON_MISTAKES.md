# Common Mistakes — AWS Compute

## Lambda Mistakes

### 1. Java Cold Starts Too Slow
**Mistake**: Using default Lambda settings (128MB) for Java.
**Effect**: Cold start = 5-8 seconds, timeout during initialization.
**Fix**: Allocate 512MB-1GB RAM (more CPU = faster JVM startup). Use SnapStart.

### 2. No Connection Pooling in Static Initializers
**Mistake**: Creating DB connections in each invocation.
**Effect**: Connection pool created per request, exhausting RDS connections.
**Fix**: Use static initializer for connection pool (reused across warm starts).

### 3. Large Deployment Packages
**Mistake**: Fat JAR with all dependencies, unmodified.
**Effect**: >50MB package → slow deployment, long cold starts.
**Fix**: Use Lambda layers for shared dependencies (AWS SDK, common libs).

### 4. Ignoring Lambda /tmp Limits
**Mistake**: Writing >512MB to /tmp.
**Effect**: Disk full error at runtime.
**Fix**: Stream to S3 for large temporary data; configure ephemeral storage.

## ECS Mistakes

### 1. No Task Memory Limits
**Mistake**: Not setting `memory` in task definition.
**Effect**: Tasks consume all instance memory, OOM kills.
**Fix**: Always set `memory` and `memoryReservation` in task definition.

### 2. Hardcoding Environment Variables
**Mistake**: DB passwords in task definition.
**Effect**: Secrets visible in AWS Console, API, CI logs.
**Fix**: Use Secrets Manager or Parameter Store; reference in task definition.

### 3. Not Setting Health Checks
**Mistake**: No container health check configured.
**Effect**: ALB routes traffic to unhealthy containers.
**Fix**: Add health check command:
```json
"healthCheck": {
    "command": ["CMD-SHELL", "curl -f http://localhost:8080/actuator/health"],
    "interval": 30,
    "timeout": 5,
    "retries": 3
}
```

## EC2 Auto Scaling Mistakes

### 1. Insufficient Warm-Up Time
**Mistake**: New instances immediately receive full traffic.
**Effect**: Tomcat/Spring Boot not ready; 502 errors.
**Fix**: Use ALB health check grace period (300s+). Configure lifecycle hooks.

### 2. Scaling Based on CPU Only
**Mistake**: Only CPU metric for scaling decisions.
**Risk**: Memory leak or request queue depth ignored.
**Fix**: Use composite metrics: CPU + memory + request count per target.

### 3. Spot Instance Interruption
**Mistake**: All instances on Spot; no fallback.
**Effect**: Interruption terminates all capacity.
**Fix**: Mix OD + Spot (at least 1 OD per AZ). Use capacity-optimized Spot.
