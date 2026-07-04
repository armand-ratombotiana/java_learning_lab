# CI/CD Debugging Guide

## GitHub Actions Debugging
```powershell
# Enable debug logging (repo secrets)
# Set ACTIONS_STEP_DEBUG: true
# Set ACTIONS_RUNNER_DEBUG: true

# Rerun failed jobs
gh run rerun <run-id>

# View logs via CLI
gh run view <run-id> --log

# SSH into runner (tmate)
- name: Debug with tmate
  uses: mxschmitt/action-tmate@v3
```

## Jenkins Debugging
```groovy
// Pipeline: Replay (modify pipeline without commit)
// Pipeline Syntax Generator (Snippet Generator)
// Check Jenkins logs: /var/log/jenkins/jenkins.log
```

## Common Issues
- **Runner offline**: Check GitHub Actions runner service or self-hosted runner connectivity.
- **Permission denied**: Check GitHub token permissions or Jenkins credential scope.
- **Docker build fails**: Check Dockerfile syntax, build context, registry auth.
- **Test failures**: Enable verbose test output, check test environment, flaky tests.
- **Deployment failures**: Check kubectl context, namespace, service account permissions.
