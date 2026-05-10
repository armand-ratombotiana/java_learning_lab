# CI/CD Edge Cases and Error Scenarios

## Build Failures

### Out of Memory Errors

```yaml
# GitHub Actions - Increase memory
- name: Build with more memory
  run: MAVEN_OPTS="-Xmx4g" mvn package

# Jenkins
steps {
    sh 'MAVEN_OPTS="-Xmx4g" mvn package'
}
```

### Network Timeouts

```yaml
# GitHub Actions - Retry with longer timeout
- name: Build with timeout
  run: |
    timeout 600 mvn package || echo "Timeout or failure"

# Retry action
- name: Build with retry
  uses: nickFields/retry-action@master
  with:
    retry_limit: 3
    command: mvn package
```

### Dependency Resolution Failures

```yaml
# Use mirrors or caches
- name: Configure Maven settings
  run: |
    mkdir -p ~/.m2
    cat > ~/.m2/settings.xml <<EOF
    <settings>
      <mirrors>
        <mirror>
          <id>central-mirror</id>
          <mirrorOf>central</mirrorOf>
          <url>https://repo.example.com/maven</url>
        </mirror>
      </mirrors>
    </settings>
    EOF
```

---

## Test Failures

### Flaky Tests

```yaml
# Run flaky tests with retry
- name: Run tests with retry
  run: |
    for i in {1..3}; do
      mvn test && exit 0
    done
    exit 1

# Jenkins flaky test strategy
steps {
    catchError(buildResult: 'FAILURE', stageResult: 'UNSTABLE') {
        sh 'mvn test'
    }
}
```

### Test Timeout Issues

```yaml
# Set timeout in Maven
- name: Run tests with timeout
  run: |
    mvn test -Dtimeout=300 \
      -Dtest=IntegrationTest \
      -Dsurefire.timeout=300
```

### Test Data Conflicts

```yaml
# Use unique test data
- name: Run tests with unique data
  env:
    TEST_DB_SUFFIX: ${{ github.run_id }}
  run: |
    mvn test -Ddatabase.suffix=$TEST_DB_SUFFIX
```

---

## Docker Build Issues

### Multi-Stage Build Failures

```dockerfile
# Multi-stage with error handling
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn dependency:go-offline || true
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN ls -la app.jar || echo "No jar found"
```

### Layer Caching Issues

```yaml
# Optimize for cache
- name: Build with cache optimization
  run: |
    # Copy dependency files first
    COPY pom.xml .
    RUN mvn dependency:go-offline
    
    # Copy source code
    COPY src ./src
    RUN mvn package -DskipTests
```

### Large Image Sizes

```dockerfile
# Use minimal base image
FROM eclipse-temurin:17-jre-alpine

# Remove unnecessary files
RUN rm -rf /var/cache/* \
  /tmp/* \
  /var/log/*

# Use multi-stage to minimize
FROM eclipse-temurin:17-jre-alpine-slim
```

---

## Deployment Failures

### Database Migration Issues

```bash
# Backup before migration
pg_dump -h db -U user mydb > backup_$(date +%Y%m%d).sql

# Run migration with backup
mvn flyway:migrate || (psql -h db -U user mydb < backup.sql && exit 1)
```

### Service Discovery Issues

```yaml
# Health check retry
- name: Deploy with health check
  run: |
    kubectl apply -f deployment.yaml
    for i in {1..30}; do
      if curl -sf http://myapp/health; then
        echo "Health check passed"
        exit 0
      fi
      echo "Waiting for health check... attempt $i"
      sleep 2
    done
    echo "Health check failed"
    kubectl rollout undo deployment/myapp
    exit 1
```

### Insufficient Resources

```yaml
# Request more resources
resources:
  requests:
    memory: "1Gi"
    cpu: "1000m"
  limits:
    memory: "2Gi"
    cpu: "2000m"
```

---

## Concurrency Issues

### Concurrent Builds

```groovy
// Jenkins - prevent concurrent builds
pipeline {
    options {
        disableConcurrentBuilds()
    }
}

// Or use locks
options {
    lock(resource: 'database')
}
```

### Race Conditions in Tests

```yaml
# Use unique ports
- name: Run integration tests
  run: |
    TEST_PORT=$(shuf -i 8000-9000 -n 1)
    mvn verify -Dserver.port=$TEST_PORT
```

---

## Security Edge Cases

### Expired Credentials

```yaml
# Rotate credentials before expiry
- name: Check credential expiry
  run: |
    if [ $(date -d "${{ secrets.EXPIRY_DATE }}" +%s) -lt $(date -d "+7 days" +%s) ]; then
      echo "Credentials expiring soon"
      exit 1
    fi
```

### Secret Leakage Detection

```yaml
# Scan for secrets
- name: Detect secrets
  run: |
    git diff --cached | grep -E "(API_KEY|PASSWORD|SECRET)" && exit 1
    echo "No secrets detected"
```

---

## Network Issues

### Timeout Configuration

```yaml
# GitHub Actions - increase timeout
- name: Long running task
  timeout-minutes: 60
  run: mvn verify

# Maven - increase timeout
mvn verify -Dmaven.surefire.timeout=600
```

### Proxy Configuration

```yaml
# Configure proxy
- name: Configure proxy
  run: |
    export HTTP_PROXY=http://proxy:8080
    export HTTPS_PROXY=http://proxy:8080
    mvn package
```

---

## Artifact Management

### Large Artifact Issues

```yaml
# Upload in chunks
- name: Upload large artifact
  run: |
    split -b 100M artifact.jar artifact-part-
    for part in artifact-part-*; do
      curl -X POST -F "file=@$part" $UPLOAD_URL
    done

# Or use artifact retention
- name: Build
  uses: actions/upload-artifact@v4
  with:
    name: build-artifact
    path: target/*.jar
    retention-days: 7
```

### Missing Artifacts

```yaml
# Check artifact existence
- name: Verify artifact exists
  run: |
    if [ ! -f target/app.jar ]; then
      echo "Artifact not found!"
      exit 1
    fi
    ls -la target/app.jar
```

---

## Best Practices

1. **Implement Proper Logging**: Log all significant steps
2. **Add Health Checks**: Verify deployment success
3. **Have Rollback Plans**: Always have a way to revert
4. **Use Idempotent Operations**: Make operations repeatable
5. **Handle Timeouts**: Set appropriate timeouts for all operations
6. **Monitor Resources**: Track resource usage in pipelines
7. **Version Everything**: Use semantic versioning
8. **Document Failures**: Record what went wrong and how to fix it