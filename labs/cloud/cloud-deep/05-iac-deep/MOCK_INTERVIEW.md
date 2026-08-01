# Lab 05: Mock Interview — Senior Platform Engineer (IaC)

**Role**: Senior Platform Engineer | **Topic**: Terraform-like IaC Engine | **Duration**: 45 minutes

---

## Interview Transcript

**Interviewer**: "Design a Terraform-like infrastructure-as-code engine. The core loop: refresh, plan, apply. Walk me through what each phase does and where the tricky parts are."

**Candidate**: "Let me define the three phases precisely, because people blur them. **Refresh** reads the current state of the world — what actually exists in the cloud right now — and merges it into our known state. **Plan** computes a diff between the desired state (from the code/config) and the current state (from refresh): a three-way diff, really — desired vs actual vs the previous run's recorded state, because you need the previous record to detect drift correctly. The output of plan is a set of actions: `create`, `update`, `delete`, or `no-op`, with the precise attribute-level changes. **Apply** executes those actions in dependency order and updates the state file. The heart of the whole system — and the thing most people get wrong — is the **state**: where it lives, how it's locked, and how it stays consistent when two engineers run `apply` simultaneously."

**Interviewer**: "Let's start with state. Why is state so important in IaC, and what are the failure modes?"

**Candidate**: "Terraform is not a 'desired-state daemon' — it doesn't watch the world and reconcile. It's an imperative engine driven by a state file: each resource in code has an ID that comes from state, and the plan diff is state-vs-code. If state is lost or corrupted, the next plan sees every resource as 'to create' — and since the resource names in code match real resources, the provider API would try to create something that already exists, or worse, the engine deletes and re-creates. That's why state is stored remotely with **locking**: a lock table (DynamoDB for AWS, etc.) that every `plan`/`apply` takes, with the owner ID and a TTL; stale locks need manual break, so the lock should carry an expiry. The second failure mode is concurrent apply from CI and a laptop — the lock must be held from plan through apply (actually Terraform re-acquires it for apply) so you can't plan against a state that changed underneath you. The third failure mode is partial apply failure: a resource created but state never updated, or updated state but the resource actually failed — that's why apply is journaled and why `refresh` after a failed apply is essential."

**Interviewer**: "Walk me through the plan phase in detail — how does the diff actually work at the attribute level?"

**Candidate**: "Each resource type has a schema — a map of attribute names to types and constraints — and a provider that implements read/create/update/delete. Plan works in three layers. Layer one, identity: match code resources to state resources by address (`aws_instance.web[0]`) and by the `id` attribute. Layer two, action decision: no state entry → create; state entry but code deleted → destroy; both exist → compare attributes. Layer three, attribute diff: for each attribute in the schema, compare the config value against the state value — if they differ and the attribute is not `computed` or `forceNew`-guarded, it's an update; if `forceNew`, it's replace (delete + create). Computed attributes — things the cloud assigns, like a public IP — are excluded from the diff unless the config explicitly sets them. The output is a structured `PlanFile` with per-resource actions and per-attribute changes, and `plan` must be a read-only operation that never mutates the world — that guarantee is what makes code review of plans possible."

**Interviewer**: "What about ordering? You can't just apply actions in any order — dependencies matter."

**Candidate**: "The engine builds a dependency graph from references: if resource B's config references resource A's attribute (`${aws_instance.web.id}` or, in modern HCL, direct references), there's an edge A→B. The graph is also enriched with provider-required implicit dependencies — a security group rule depends on the security group, an IAM policy attachment depends on both the policy and the role. Apply walks the graph in topological order: creates and updates first, destroys last (and within destroys, reverse topological order — delete children before parents). The critical failure mode is a dependency cycle: two resources referencing each other — the plan must detect cycles and fail with a clear error rather than deadlock. There's also the practical subtlety: `depends_on` is the escape hatch for dependencies the engine can't infer — and the graph must be conservative: when in doubt, order by reference, because the cost of wrong ordering is a partial apply."

**Interviewer**: "How do you handle concurrent plans and the read-modify-write race at the state level? Say two engineers both plan against the same state, then both apply."

**Candidate**: "This is the classic IaC race, and the answer is a three-part protocol. First, the lock: both applies try to acquire the state lock; the second one fails fast with 'state locked by X since time T' — no blocking, because long-blocked applies turn into zombie jobs. Second, optimistic concurrency at the state store: the state file carries a `serial` number; an apply that writes state must provide the serial it read, and the store rejects the write if the serial moved (compare-and-swap semantics). Third, and this is the part that catches the eager engineer: plan must be re-run right before apply when the state has changed — `plan` output is a promise, not a contract; if the state serial moved between plan and apply, the plan is invalid and must be regenerated. The demo version of this race — two threads planning and applying against the same serial — is exactly what I'd implement in the walkthrough."

**Interviewer**: "What about drift? The cloud always drifts — someone clicks in the console, a lambda deletes an instance, a scale event changes the ASG. How does your engine behave?"

**Candidate**: "Drift is handled by refresh and by **detection, not prevention** — IaC can't prevent console clickers, it can only notice them. The engine has two modes: refresh-before-plan (read current state, diff) — the default in the demo — and a drift *check* command that reports resources whose state differs from the real world *without* proposing to fix them, so you can distinguish 'drift we caused' from 'drift the world caused'. There's also the deeper question of policy: some drift should be accepted and then *imported* into config (a scale event that reflects real demand), other drift should be *reverted* (an unauthorized change to a security group). Modern IaC handles this with policy-as-code gates in the CI pipeline rather than in the engine itself. The engine's job is only to be honest: never silently overwrite state you didn't read."

**Interviewer**: "How does the engine model provider backends? The providers are where all the API differences live."

**Candidate**: "Providers are plugins behind a stable interface: `ResourceProvider` with `read`, `create`, `update`, `delete` for each registered resource type, plus schema introspection. The engine never knows AWS from Azure — it knows only resource types and attributes, and it delegates every world-mutating call to the provider. Provider versions are pinned in config because schema changes between provider versions are the #1 source of surprise diffs — an attribute that became computed, a renamed field — so the plan file records which provider versions produced it. The registry pattern — providers declare their resource types and schemas at load time — lets the engine validate config before planning: unknown resource type or unknown attribute is a compile-time-ish error, not a runtime surprise."

**Interviewer**: "How do you test an IaC engine? This seems hard because it mutates real infrastructure."

**Candidate**: "The trick is a fake provider: an in-memory resource store implementing the same provider interface with realistic semantics — create returns an ID, update validates attributes, delete removes. The entire plan/apply/diff logic is testable against the fake without touching a cloud. Golden-file tests assert plan output for tricky cases: forceNew replacements, computed attributes, dependency reordering, cycle errors. Then contract tests run the same suite against the real provider — recording the actual API calls — to catch the classic bug where the fake and the real diverge. The demo walkthrough uses exactly this pattern: a fake 'FakeCloudProvider' with VMs, buckets, and networks, and we assert plans and applies against it."

**Interviewer**: "What's the most dangerous design decision in an IaC engine, in your opinion?"

**Candidate**: "Auto-destroy. The moment an engine deletes resources *it wasn't explicitly told to delete in the current diff*, you're one bug away from destroying production. My rule: destroy is always explicit in the plan, always reviewed, and always gated — `apply -auto-approve` should never be the default for destroys. The second-most-dangerous: state corruption on partial failure. Between the API call succeeding and the state write succeeding, there's a window where the world has changed but state hasn't — and the crash-recovery protocol (journal the intent before acting, then reconcile after restart) is what separates a toy from an engine people trust with production."

---

## Wrap-Up

**What the interviewer is looking for**:
- Precise separation of refresh / plan / apply and what each phase reads and writes
- Deep state-awareness: remote state, locking, serial numbers, optimistic concurrency
- Attribute-level diff semantics: computed, forceNew, replace vs update
- Dependency graph reasoning: topological order, cycles, destroys-last
- Crash-safety: journaling and reconcile-on-restart
- Testability via fake providers and golden files

**Common mistakes candidates make**:
- Treating plan as 'diff config vs cloud' instead of 'diff config vs state'
- No locking or concurrency protocol on state
- Ignoring computed attributes and forceNew semantics in the diff
- Deleting resources before their dependents in apply ordering
- No mention of drift detection or crash recovery
