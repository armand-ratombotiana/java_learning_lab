# Docker Solution

## Concepts Covered

### Multi-stage Docker Builds
- Build stage with Maven for compilation
- Runtime stage with minimal JRE image
- Layer optimization with dependency caching
- COPY --from for artifact transfer

### Docker Compose
- Service definitions (app, db, redis, nginx)
- Environment variables and port mapping
- Volume mounting for data persistence
- Health checks for dependency management
- Network configuration

### Security
- Non-root user creation
- Proper file ownership
- Security scanning (Trivy, Docker Scout)

### Development Workflow
- Volume mounting for hot reload
- Debug configuration with port 5005
- Maven cache for faster builds

## Commands

```bash
# Build
docker build -t myapp:latest .

# Run
docker-compose up -d

# Run with override
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up

# Push
./build.sh latest docker.io/myuser
```

## Running Tests

```bash
mvn test -Dtest=DockerSolutionTest
```