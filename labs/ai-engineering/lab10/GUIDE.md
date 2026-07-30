# AI Deployment & CI/CD — Deep Dive Guide

## Deployment Strategies

### Blue-Green Deployment
Two identical environments (blue = current, green = new):

1. Deploy new model version to the green environment
2. Run health checks against the green environment
3. If healthy, switch traffic from blue to green instantly
4. Blue remains as a fallback for immediate rollback

Pros: Instant switch, easy rollback, no version mixing
Cons: 2x resource cost during deployment

### Canary Release
Gradually shift traffic from old to new version:

1. Start with 5% traffic on new version
2. Monitor metrics (latency, errors, quality)
3. Gradually increase to 25%, 50%, 100%
4. Rollback immediately if metrics degrade

Pros: Limits blast radius, real-world validation
Cons: Longer deployment time, two versions in production

## Code Walkthrough: DeploymentManager

The `DeploymentManager` demonstrates:

- `blueGreenDeploy()` — registers new version, runs health check, switches traffic atomically
- `canaryRelease()` — registers version, sets 95/5 traffic split
- `promoteCanary()` — moves canary to 100% traffic
- `rollback()` — restores the previous deployment
- `previousDeployment` field enables one-step rollback

## Traffic Routing

The `TrafficRouter` implements weight-based routing:

- `setWeight(modelVersion, percentage)`: Configures traffic splits
- `route(requestId)`: Deterministic routing via hash of request ID
- Hash-based routing ensures consistent routing for the same request

## CI/CD Pipeline

The `CiCdPipeline` simulates a three-stage pipeline:

1. **Build**: Compile/package the model artifact
2. **Test**: Run evaluation suite (minimum accuracy threshold = 0.7)
3. **Deploy**: Execute canary release if tests pass

## Model Registry

The `ModelRegistry` maintains version history:

- Each model has a list of `ModelVersion` records (id, version, accuracy, health)
- `getLatest()` returns the most recent version
- `getHistory()` returns the full version list

## Production Considerations

- Automate health checks with canary metric thresholds
- Store model artifacts in a versioned registry (MLflow, S3 with versioning)
- Implement feature flags for gradual model rollout
- Monitor deployment metrics in real-time dashboards
- Automate rollback on metric threshold breaches
- Test rollback procedure regularly in staging