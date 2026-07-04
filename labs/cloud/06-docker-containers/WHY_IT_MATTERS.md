# Why Docker Matters

## Business Impact
- **Shipping speed**: Deploy 10x more frequently with containers
- **Resource efficiency**: 5-10x more apps per server vs VMs
- **Standardization**: One artifact (image) moves through pipeline
- **Cost savings**: Higher density = fewer servers = lower cloud bills

## Technical Impact
- **Isolation**: Each container has its own filesystem, network, process space
- **Immutable infrastructure**: Images are versioned, never modified in place
- **Orchestration**: Docker is the foundation for Kubernetes, ECS, Swarm
- **Ecosystem**: 8M+ public images on Docker Hub; vast tooling landscape

## For Java Developers
- Multi-stage builds: compile with JDK, run with JRE (smaller images)
- Layered images: dependencies layer changes less frequently than app layer
- Health checks: Docker checks Java app health via HTTP endpoint
- Docker Compose: Spring Boot + PostgreSQL + Redis in one command
- Memory limits: -Xmx set within container limits
