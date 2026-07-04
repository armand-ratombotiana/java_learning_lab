# Mental Models for GitOps

## 1. Git as System of Record
Git is the source of truth — like a constitution. The cluster is the "actual state" — like current government. The GitOps operator is the judicial branch — ensuring actual state matches constitutional law.

## 2. Pull vs Push
- **Push model**: CI/CD server pushes changes to cluster (like delivery truck delivering packages).
- **Pull model**: GitOps operator pulls changes from Git (like checking your mailbox).
- The pull model is more secure: no cluster credentials in CI/CD, no inbound connections.

## 3. Loop of Reconciliation
```
[Git] ──watch──▶ [Operator] ──diff──▶ [Cluster]
   ▲                                    │
   │                                    │ drift
   │    (manual change detected)        │
   └────────────────────────────────────┘
```

## 4. GitOps as Cruise Control
Set your desired speed (Git state), and the car's computer (operator) maintains that speed regardless of hills, wind, or road conditions. If you're pushed downhill (drift), the computer corrects back to the set speed.
