# CI/CD Pipeline Performance

## Pipeline Optimization
- **Caching**: Cache node_modules, Maven .m2, pip packages between runs.
- **Parallel jobs**: Run independent jobs (lint, test, security) in parallel.
- **Test splitting**: Distribute test suite across multiple runners.
- **Incremental builds**: Only rebuild changed modules.
- **Conditional execution**: Skip stages based on changed paths (path filters).

## GitHub Actions Performance Tips
- Use `actions/cache` for dependency caching.
- Use matrix builds for parallel test execution across OS/versions.
- Choose `ubuntu-latest` over Windows/macOS when possible (faster startup).
- Use self-hosted runners for larger instances or GPU workloads.

## Metrics to Track
- **Pipeline duration**: Total time from trigger to completion.
- **Queue time**: Time job waits for available runner.
- **Test execution time**: Time to run all tests.
- **Resource utilization**: CPU/memory during builds.

## Common Bottlenecks
- Test execution (slowest stage typically)
- Docker image build/push
- Dependency download
- Deployment to remote environments
