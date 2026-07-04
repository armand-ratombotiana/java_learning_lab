# Distributed Locking: Architecture

## Lock Service Architecture

```
┌─────────────────────────────────────────────┐
│           Application Services               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ Service A │  │ Service B │  │ Service C │  │
│  └─────┬────┘  └─────┬────┘  └─────┬────┘  │
│        │              │              │       │
└────────┼──────────────┼──────────────┼───────┘
         │              │              │
    ┌────┴──────────────┴──────────────┴────┐
    │           Lock Service                │
    │     (etcd / ZooKeeper / Redis)        │
    └───────────────────────────────────────┘
```

## Leader Election Architecture

```
Service Instance 1 ──▶ /election/leader
Service Instance 2 ──▶ /election/candidate-001 (watching leader)
Service Instance 3 ──▶ /election/candidate-002 (watching candidate-001)

If leader crashes:
  candidate-001 becomes leader
  candidate-002 watches candidate-001
```
