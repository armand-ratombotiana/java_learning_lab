# CI/CD Theory

## Core Concepts
- **Continuous Integration (CI)**: Automatically build and test every code commit. Detect integration issues early.
- **Continuous Delivery (CD)**: Automatically deploy to staging; manual approval for production.
- **Continuous Deployment**: Automatically deploy to production after all tests pass.

## Pipeline Stages (Typical)
1. **Source**: Checkout code from version control.
2. **Build**: Compile, bundle, package (Docker image, JAR, etc.).
3. **Test**: Unit, integration, end-to-end, and smoke tests.
4. **Security Scan**: SAST, DAST, dependency scanning, container scanning.
5. **Quality Gate**: Code coverage, linting, static analysis.
6. **Artifact**: Publish build artifacts to registry.
7. **Deploy (Staging)**: Deploy to staging environment.
8. **Integration Tests**: Full end-to-end tests against staging.
9. **Deploy (Production)**: Deploy to production (manual or automated).
10. **Verify**: Smoke tests and monitoring checks post-deployment.

## Key Principles
- **Fail Fast**: Stop pipeline immediately on failure.
- **Idempotent**: Same commit always produces same result.
- **Immutable Artifacts**: Build once, deploy everywhere with same artifact.
- **Fast Feedback**: Pipeline should complete in < 15 minutes.
