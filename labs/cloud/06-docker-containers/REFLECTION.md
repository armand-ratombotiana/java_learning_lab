# Reflection — Docker

## Self-Assessment

| Skill | Know | Explain | Teach |
|-------|:----:|:-------:|:-----:|
| Dockerfile creation and optimization | ☐ | ☐ | ☐ |
| Multi-stage builds | ☐ | ☐ | ☐ |
| Docker Compose for multi-service apps | ☐ | ☐ | ☐ |
| Docker networking and port mapping | ☐ | ☐ | ☐ |
| Volumes and data persistence | ☐ | ☐ | ☐ |
| Container resource limits | ☐ | ☐ | ☐ |
| Docker security best practices | ☐ | ☐ | ☐ |
| Image tagging and registry push/pull | ☐ | ☐ | ☐ |

## Journal Prompts

1. How does Docker change your development workflow compared to running Java apps directly on your machine?

2. What's the most impactful optimization for reducing Docker image size?

3. How do you handle database schema migrations in containerized environments?

4. When would you NOT use Docker for a Java application?

5. How does Docker's isolation (namespaces, cgroups) compare to a full VM?

## Key Takeaways
- Container = isolated process (not a VM)
- Image layers enable caching, reuse, and small download sizes
- Multi-stage builds reduce production image size by 60-80%
- Docker Compose orchestrates multi-service local development
- Always set memory limits; JVM must respect container limits
- Run as non-root, use read-only filesystem, drop capabilities
- Use .dockerignore to speed up builds
- Pin base image versions; never use :latest in production
