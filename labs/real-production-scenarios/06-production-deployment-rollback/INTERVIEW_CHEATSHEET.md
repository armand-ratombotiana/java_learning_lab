# Interview Cheatsheet: Deployment Rollback

## Key Diagnostic Commands
- `kubectl rollout history deployment/<name>` — deploy history
- `kubectl rollout undo deployment/<name>` — rollback
- `git log --oneline -20` — recent changes
- `git diff <tag1> <tag2>` — what changed
- CI/CD pipeline logs (Jenkins, GitHub Actions, Azure DevOps)
- Feature flag system status (LaunchDarkly, Flagsmith)

## Common Metrics to Check
- Error rate before/after deployment
- Latency P50, P95, P99 before/after
- Request rate change
- Deployment success/failure rate
- Time to rollback

## Typical Root Causes
- Missing migration in deployment script
- Config error in new environment
- Incompatible API change (no versioning)
- Insufficient canary testing
- Race condition between deploy and traffic shift
- Secret/config missing in target environment

## Interview Question Patterns
- "Design a deployment pipeline with safety guarantees"
- "How do you decide to roll back vs. fix forward?"
- "What's the difference between blue-green and canary deployment?"
- "How do feature flags prevent deployment failures?"

## STAR Story Template
**S**: App Service started returning 500 errors after deployment
**T**: Restore service and prevent future deploy failures
**A**: Rolled back within 3 min, added canary deployment with 1%→10%→100%, automated health check gates
**R**: Zero-downtime deployments achieved, rollback time reduced to < 1 min
