# Docker Pedagogic Guide

## Teaching Strategy

### Phase 1: Fundamentals (Hours 1-2)
**Goal**: Understand Docker basics

**Topics**:
- What is Docker?
- Images and containers
- Basic commands
- Dockerfile basics

**Activities**:
1. Run first container
2. Write simple Dockerfile
3. Build and run image

**Exercises**:
- Exercise 1 (Multi-stage build)

**Assessment**: Container runs successfully

---

### Phase 2: Dockerfile Deep Dive (Hours 3-4)
**Goal**: Master Dockerfile patterns

**Topics**:
- Multi-stage builds
- Layer caching
- Security hardening
- Optimization

**Activities**:
1. Optimize existing Dockerfile
2. Add security options
3. Implement non-root user

**Exercises**:
- Exercise 1, 4

**Assessment**: Optimized Dockerfile

---

### Phase 3: Docker Compose (Hours 5-6)
**Goal**: Master multi-container apps

**Topics**:
- Docker Compose syntax
- Service dependencies
- Networks and volumes
- Health checks

**Activities**:
1. Create compose file
2. Configure dependencies
3. Set up networks

**Exercises**:
- Exercise 2, 5

**Assessment**: Complete compose file

---

### Phase 4: Advanced Patterns (Hours 7-8)
**Goal**: Apply advanced patterns

**Topics**:
- Resource management
- Secrets management
- Logging
- Security scanning

**Activities**:
1. Configure resource limits
2. Set up secrets
3. Configure logging

**Exercises**:
- Exercise 3, 6, 7

**Assessment**: All patterns working

---

## Teaching Techniques

### Code Review Questions
1. What layers can be cached here?
2. How can we reduce image size?
3. What security options are missing?
4. How would you debug this container?

### Common Mistakes
| Mistake | Solution |
|---------|----------|
| Using latest tag | Use specific versions |
| Running as root | Create non-root user |
| No health checks | Add health checks |
| No resource limits | Set memory/CPU limits |
| Not cleaning up | Use multi-stage builds |

### Real-World Examples

**Example 1: Microservices Stack**
- API gateway (nginx)
- Application services
- PostgreSQL
- Redis
- Monitoring (Prometheus)

**Example 2: Development Environment**
- Local code mounting
- Hot reload
- Debugging enabled

**Example 3: CI/CD Pipeline**
- Build in container
- Run tests in container
- Push to registry
- Deploy to orchestrator
```

---

## Hands-On Projects

### Project 1: Complete Application (4 hours)
Create complete setup with:
- Spring Boot app
- PostgreSQL
- Redis
- Nginx reverse proxy

**Requirements**:
- All services communicate
- Health checks work
- Proper resource limits

---

### Project 2: Production Setup (6 hours)
Configure production-ready setup:
- Security hardening
- Secrets management
- Log rotation
- Backup strategy

**Requirements**:
- Runs as non-root
- Secrets properly managed
- Logs configured

---

### Project 3: CI/CD Integration (5 hours)
Integrate with CI/CD:
- Build Docker image in pipeline
- Push to registry
- Deploy to orchestration

**Requirements**:
- Pipeline builds image
- Image scanned for vulnerabilities
- Deploys successfully

---

## Evaluation Criteria

### Dockerfile (40%)
- Proper structure
- Layer optimization
- Security hardening

### Docker Compose (30%)
- Complete configuration
- Proper dependencies
- Health checks

### Best Practices (30%)
- Resource limits
- Logging
- Security

---

## Resources

### Official Docs
- [Docker Docs](https://docs.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- [Best Practices](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/)

### Tools
- Docker Desktop
- Docker Compose
- BuildKit
- Hadolint