# Lab 01: Mock Interview — GitOps Deep Dive

**Role**: Platform Engineer / DevOps Lead
**Duration**: 60 minutes
**Focus**: GitOps principles, drift detection, sync strategies, ArgoCD vs Flux

---

**Interviewer**: "We deploy to Kubernetes with a mix of helm charts applied manually from laptops. I
want GitOps. What does that actually change?"

**Candidate**: "Three things: declarative, versioned, automated. Declarative — the entire desired
state of the cluster lives as manifests in git; nothing is applied by hand. Versioned — git history
is the audit trail; every change has a commit, an author, and a review. Automated — a reconciler in
the cluster continuously compares git's desired state with the cluster's actual state and converges
them. The laptop is removed from the critical path entirely."

**Interviewer**: "Walk me through the reconciliation loop."

**Candidate**: "The operator runs a loop with three phases. Observe: pull the desired state from git
and the actual state from the cluster API. Diff: classify every difference — a resource in git but
not in the cluster is ADDED; in the cluster but not git is MISSING; both but different spec is
MODIFIED. Act: apply or delete according to the sync policy, and emit a report. Then sleep and
repeat — that's why it's called a loop, not a job. The loop makes drift self-healing: someone edits
a deployment by hand, the operator detects it within seconds and reverts to git."

**Interviewer**: "What does the drift report look like in practice?"

**Candidate**: "Per resource: kind, name, namespace, the drift type, and ideally the actual diff —
'replicas: 3 -> 1'. Aggregated: drift count by kind, last successful sync, commit at head. The
important operational metric: time-to-convergence — how long between a git commit and the cluster
matching it. And a pageable signal: persistent drift — the loop has tried N times and the cluster
still disagrees, which usually means an admission webhook or a controller is fighting it."

**Interviewer**: "ArgoCD or Flux — how do you choose?"

**Candidate**: "Both are excellent; the honest answer is organizational fit. ArgoCD: richer UI, SSO,
multi-cluster management, project scoping, sync waves — great for larger orgs and for teams who want
approval workflows in the tool. Flux: lighter, Kustomize-native, strong GitOps Toolkit philosophy,
SOPS encryption built in, controller-based extension — great for teams that want to compose their
own operators. The key point: the reconciliation loop is the same idea in both — they differ in
orchestration and polish, not fundamentals."

**Interviewer**: "When do you use Helm vs Kustomize in a GitOps repo?"

**Candidate**: "Helm when the chart is the unit of distribution — third-party charts like
ingress-nginx, or charts your team maintains with templating needs. Kustomize when you want pure
overlays — base manifests plus environment overlays with no templating logic. My default: Helm
charts in a HelmRepository (Flux) or managed by ArgoCD's chart support, Kustomize for in-repo
overlays. The trap to avoid: deep Helm templating with values files per environment stacked five
deep — that becomes unreadable declarative code."

**Interviewer**: "Automatic sync or manual?"

**Candidate**: "Hybrid, and it's the standard industry answer: automatic sync on the main branch for
the staging environment — speed of iteration; manual sync with approvals for production — change
control. And critically: even with automatic sync, a *diff* stage runs first. Prune — deleting
resources no longer in git — is the most dangerous sync operation, so prune-on-sync is usually off
for production or restricted to namespaces owned by the app."

**Interviewer**: "What are sync waves, and why do they exist?"

**Candidate**: "Ordering, because Kubernetes apply order matters. CRDs must exist before custom
resources of that type; namespaces before the deployments in them; services after the deployments
they target — or at least their selectors. ArgoCD solves it with the `sync-wave` annotation: wave 0
first, then 1, 2, 3. I use waves for the CRD -> namespace -> app pattern and keep the total wave
count under five — beyond that, you're modeling an orchestrator inside a declarative tool, and you
should question the design."

**Interviewer**: "How do you handle secrets in git?"

**Candidate**: "Never plaintext. Three legitimate patterns: SOPS-encrypted manifests — encrypted at
rest in git, decrypted at apply time with keys from the cluster or KMS; sealed secrets — sealed by a
controller in-cluster so only the cluster can decrypt; and external secret references — the manifest
says 'secret from Vault: db-password', and an operator syncs it. My preference: SOPS for
GitOps-native, External Secrets Operator when secrets are already centralized in Vault or cloud
secret managers."

**Interviewer**: "Your team bypasses git to hotfix an incident — `kubectl edit deployment` at 3am.
What happens?"

**Candidate**: "The reconciler treats it as drift and reverts it. That's either the system working —
the cluster returns to the declared state — or an operational emergency where the fix must hold
until the manifest is updated. My rule: in a true SEV, `kubectl edit` with intent to commit within
minutes; the reconciler's revert is then a feature, not a bug. The dangerous failure is the
opposite: hotfix by hand, and it *works* for days because nobody updated git — that's how the drift
memory of the team goes stale. The fix is cultural: 'the cluster is not the truth, git is', enforced
by making the revert fast and visible."

**Interviewer**: "How do you test a GitOps change before it hits production?"

**Candidate**: "The pipeline is the test harness: render the manifests (helm template/kustomize
build), validate with kubeconform or `kubectl diff --server-side`, run conftest/OPA policy checks,
deploy to staging via GitOps, run smoke tests, then promote to prod by merging to the production
branch — the promotion is itself a git commit, reviewable and revertible. The deepest benefit:
rollback is `git revert`, not `kubectl apply` of the old YAML. You get a full audit trail of every
environment change."

**Interviewer**: "What are the failure modes of the reconciler itself?"

**Candidate**: "Five. Git connectivity failure — reconciler runs on stale state; detect and alert,
don't act. Cluster API throttling — the loop hammering the API server; exponential backoff is
mandatory. Malformed manifests — the app fails to sync and *every* environment fails together;
validate early in CI, not in the cluster. Permission drift — the operator's service account loses a
permission and all syncs fail silently. And the philosophical one: fighting controllers — an HPA
scaling replicas while the reconciler pins replicas in git; exclude live-managed fields from
comparison."

**Interviewer**: "How do you do canary deployments with GitOps?"

**Candidate**: "GitOps manages the manifests, but the traffic split is runtime state: use Flagger or
Argo Rollouts on top — the canary analysis engine adjusts weights, while the git state stays the
source for the *versions*. The pattern: git holds the canary revision; the progressive delivery tool
shifts traffic and runs analysis; promotion is a git merge of the rollout resource. This split —
declarative source of truth for versions, live adjustment for traffic — is the modern standard."

**Interviewer**: "Monorepo or separate repo per environment?"

**Candidate**: "Monorepo with folder structure — `apps/payments/base`,
`apps/payments/overlays/staging`, `apps/payments/overlays/prod` — is my default: cross-cutting
changes (a shared configmap) land in one PR, reviewable together, and the promotion is a single
merge. Separate repos win when environments are owned by different teams with separate access
control — but then you duplicate manifests and pay a sync tax. Either way: one directory per app,
explicit environment overlays, and an ownership file."

**Interviewer**: "How do you enforce 'no direct kubectl changes'?"

**Candidate**: "The reconciler's revert is the enforcement — combined with an audit alert: when
drift is detected and repaired, emit an event with the diff; repeated drift from the same actor
becomes a conversation. Tooling: `kubectl` audit via the API server's audit log, and delete/patch
webhooks on sensitive namespaces that require a token minted from git CI. But the strongest
enforcement is cultural, and it comes from trust in the loop: when engineers see the system fix
drift faster than they can, the bypass habit dies."

**Interviewer**: "Final question: when is GitOps *not* the right answer?"

**Candidate**: "Three cases. Stateless, ephemeral workloads that never need reconciliation — plain
batch jobs. Teams without the maturity to keep git as the single source of truth — if the team can't
stop editing prod by hand, GitOps tools just add a second fight. And Kubernetes itself isn't
required: GitOps is a pattern, not a K8s feature — it applies to Terraform (Atlantis), to edge
configs, anywhere declarative state meets continuous enforcement. But honestly, for any K8s platform
with more than one cluster, not using GitOps in 2026 is a deliberate choice with real costs."

---

## Interviewer Feedback

**Strengths**:
- Clear loop-first explanation (observe/diff/act) that grounds the whole interview.
- Nuanced ArgoCD vs Flux, Helm vs Kustomize, auto vs manual sync answers.
- Honest treatment of hotfixes, controller fights, and enforcement.

**Improvements**:
- Could have sketched a concrete `sync-wave` annotation example.
- Could have discussed GitOps for multi-cluster fleet management (ArgoCD ApplicationSets) more concretely.
- Could have mentioned git-based secrets rotation strategy in more depth.

**Score**: Strong Hire
