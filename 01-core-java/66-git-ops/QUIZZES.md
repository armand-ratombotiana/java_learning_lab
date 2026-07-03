# Module 66: GitOps - Quizzes

---

## Q1: Pull-Based Deployments
What is the primary security advantage of a Pull-based GitOps deployment over a traditional Push-based CI/CD deployment?

A) Pull-based deployments are faster.
B) Pull-based deployments don't require Docker.
C) In a Push model, the CI server needs admin credentials to the production cluster, creating a massive attack vector. In a Pull model, the cluster reaches out to Git; the cluster's credentials never leave the cluster.
D) Pull-based deployments automatically encrypt code.

**Answer**: C
**Explanation**: Pull-based GitOps controllers (like ArgoCD) run inside the secure boundaries of the Kubernetes cluster. They only need read access to a Git repository, completely removing the need to expose cluster API credentials to third-party CI tools.

---

## Q2: Configuration Drift
What happens when "Configuration Drift" is detected in a strict GitOps environment?

A) The server crashes.
B) The GitOps controller (like ArgoCD) detects that the live cluster state no longer matches the desired state in Git, and it automatically overwrites the manual cluster changes to restore synchronization with Git.
C) The Git repository is deleted.
D) The developer is locked out of their account.

**Answer**: B
**Explanation**: Git is the single source of truth. Any manual `kubectl edit` commands will be swiftly reverted by the controller, enforcing that all changes must go through the proper PR and code review process.

---

## Q3: Secret Management
Why can't standard Kubernetes Secrets be used natively in a GitOps workflow?

A) Kubernetes Secrets do not support passwords.
B) Kubernetes Secrets are just Base64 encoded, not actually encrypted. Committing them to Git exposes the plaintext passwords to anyone with repo access.
C) Kubernetes Secrets cannot be read by ArgoCD.
D) Git rejects YAML files containing the word "Secret".

**Answer**: B
**Explanation**: Base64 is an encoding format, not an encryption cipher. To safely store secrets in Git, you must use tools like Sealed Secrets or Mozilla SOPS, which use asymmetric encryption.