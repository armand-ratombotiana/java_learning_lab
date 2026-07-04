# Architecture: Migration Tools in CI/CD

```
┌────────────────┐
│  Developer     │
│  writes V3.sql │
└───────┬────────┘
        │ git push
        ▼
┌────────────────┐
│  Git Repository│
│  main branch   │
└───────┬────────┘
        │ CI pipeline
        ▼
┌─────────────────────────────┐
│  CI/CD Pipeline             │
│  ┌───────────────────────┐  │
│  │ 1. Build              │  │
│  │ 2. Run tests          │  │
│  │ 3. flyway migrate     │  │  ← Run on test DB
│  │ 4. Integration tests  │  │
│  └───────────────────────┘  │
└───────┬─────────────────────┘
        │ deploy
        ▼
┌─────────────────────────────┐
│  Production Rollout         │
│  1. Pre-prod smoke test     │
│  2. flyway migrate          │
│  3. Health check            │
│  4. Route traffic           │
└─────────────────────────────┘
```

## Multi-Environment Strategy
- **Dev**: `flyway clean` + migrate frequently
- **Staging**: Mirror of production, full migration run
- **Production**: Ordered, gated, monitored rollout
- **DR**: Rebuild from all migrations

## Migration as Code
- Store migrations alongside application code (same repo)
- Version migrations match application releases
- Never deploy code without its corresponding migrations
