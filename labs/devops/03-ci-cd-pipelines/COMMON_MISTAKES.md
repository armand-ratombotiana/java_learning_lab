# Common CI/CD Mistakes

1. **Slow pipelines** (>20 min) — developers bypass them or context-switch.
2. **Flaky tests** — reduces trust in pipeline; invest in test stability.
3. **Building artifacts multiple times** — build once, promote through environments.
4. **Hardcoding secrets** in pipeline config — use secret stores/env vars.
5. **Inconsistent environments** — test on different OS/node versions than production.
6. **Not testing deployments** — dry-run or deploy to staging first.
7. **Overly complex pipelines** — hard to maintain and debug.
8. **Ignoring failed pipelines** — "build is red but it's not my code."
9. **Not caching dependencies** — wastes time rebuilding node_modules every run.
10. **No pipeline feedback** — Slack/email notifications for failures.
11. **Manual steps** — defeats CI/CD purpose; automate everything.
12. **Merge conflicts from long-lived branches** — use short-lived feature branches.
