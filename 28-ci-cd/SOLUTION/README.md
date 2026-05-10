# CI/CD Solution

## Concepts Covered

### Jenkins Pipeline
- Declarative pipeline syntax
- Agent allocation
- Environment variables
- Stages: Build, Test, Docker Build, Deploy
- Post actions for cleanup and notifications

### GitHub Actions
- Workflow triggers (push, pull_request)
- Jobs and steps
- Actions marketplace integration
- Artifact upload/download
- Conditional deployment

### GitLab CI/CD
- Stages definition
- Docker-in-Docker (dind) service
- Artifact management
- Coverage reporting

### Docker Multi-stage Builds
- Build stage for compilation
- Runtime stage for deployment
- Image size optimization

## Files Generated

- `Jenkinsfile` - Jenkins declarative pipeline
- `.github/workflows/ci.yml` - GitHub Actions workflow
- `.gitlab-ci.yml` - GitLab CI configuration
- `Dockerfile` - Multi-stage build
- `settings.xml` - Maven configuration

## Running Tests

```bash
mvn test -Dtest=CICDSolutionTest
```