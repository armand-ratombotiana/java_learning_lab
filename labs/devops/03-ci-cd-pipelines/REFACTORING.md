# CI/CD Pipeline Refactoring

## Before (Monolithic Workflow)
```yaml
name: Pipeline
on: [push]
jobs:
  all:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: npm ci && npm test && npm run build
      - run: docker build -t app .
      - run: kubectl apply -f k8s/
```

## After (Modular, Optimized)
```yaml
name: CI/CD
on: [push, pull_request]

jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: npm ci && npm run lint

  test:
    needs: lint
    runs-on: ubuntu-latest
    strategy:
      matrix:
        node: [18, 20, 22]
    steps:
      - uses: actions/checkout@v4
      - run: npm ci && npm test

  build:
    needs: test
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: docker build -t app .
      - run: docker push app

  deploy:
    needs: build
    runs-on: ubuntu-latest
    environment: ${{ github.ref == 'refs/heads/main' && 'production' || 'staging' }}
    steps:
      - run: kubectl apply -f k8s/
```

## Gains
- Matrix testing catches Node version issues
- Parallel lint/test stages provide faster feedback
- Environment-specific deployment gates
- Clear dependency chain
