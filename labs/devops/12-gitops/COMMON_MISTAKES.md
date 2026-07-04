# Common GitOps Mistakes

1. **Auto-pruning without careful review** — deletes resources unintentionally.
2. **No self-healing** — drift goes undetected; use `selfHeal: true`.
3. **Committing secrets to Git** — even encrypted, avoid secrets in Git; use External Secrets Operator.
4. **Too many apps in one ApplicationSet** — hard to manage; split by service/team.
5. **No sync waves** — resources created in wrong order (CRD before CR).
6. **Ignoring health checks** — app shown as synced but not actually healthy.
7. **Overly permissive sync** — allow any namespace; security risk.
8. **No multi-cluster strategy** — repeating same config per cluster manually.
9. **Webhook notifications missing** — long poll intervals delay deployments.
10. **Complex repo structure** — deep nesting, many directories, hard to navigate.
11. **Not testing sync policies** — prune and selfHeal can cause production incidents.
12. **Mixing GitOps with imperative commands** — kubectl create/edit breaks GitOps model.
