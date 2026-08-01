# Lab 04: Mock Interview — Secrets Management Deep Dive

**Role**: Platform / DevOps Engineer
**Duration**: 60 minutes
**Focus**: Vault architecture, dynamic secrets and leases, external secrets operator, encryption

---

**Interviewer**: "Why do teams end up with a secrets problem, and what's the fix?"

**Candidate**: "Secrets get scattered because each team solves the same problem in isolation:
credentials in config files, in CI variables, in Kubernetes secrets, in a spreadsheet
nobody
updates. The fix is centralization plus discipline: one secrets manager — Vault is the
standard
— with a defined access model, audit logging, and rotation. But the tool alone is not
the fix:
the real change is the policy — no secrets in git, no secrets in images, secrets
injected at
runtime, and access scoped by identity. Centralizing without auditing and rotating just
moves
the chaos into one place with a worse blast radius."

**Interviewer**: "Walk me through Vault's architecture at a high level."

**Candidate**: "Four pieces. The storage backend — the state, encrypted at rest, typically a
consul/raft cluster. The seal/unseal mechanism — keys decrypt the storage before
anything
serves; in production that's auto-unseal with a KMS, so no human typing keys. The
secrets
engines — pluggable modules: KV for static secrets, database engines for dynamic
credentials,
PKI, transit, and so on. And the auth methods — how identities authenticate: tokens,
Kubernetes
service accounts, AppRole, OIDC. Everything flows through the same ACL layer: identity
-> policy
-> capabilities on paths. The architecture point I'd stress: the storage is encrypted
and the
engines and auth are all in one binary — it's a control plane, not a data store for
application data."

**Interviewer**: "What's the difference between a static and a dynamic secret?"

**Candidate**: "A static secret is a value you manage — a password you wrote down, rotated
manually. A dynamic secret is generated on demand and leased: the database engine
creates a
real username and password with exactly the privileges the role defines, returns them
with a
lease, and the credential is revoked — or its underlying user deleted — when the lease
expires
or is revoked explicitly. The practical difference: with static secrets, a leaked
credential
is valid forever until someone notices; with dynamic ones, the credential has a built-in
expiry, and rotation becomes 'revoke and re-issue' instead of a coordinated change
ceremony.
That's why database and cloud credential engines are the biggest Vault wins."

**Interviewer**: "How does lease renewal work, and what breaks in practice?"

**Candidate**: "The consuming application — or better, the operator — calls the renew endpoint
with the lease ID within the lease duration. Vault can extend the lease, or refuse if
the
policy's max TTL is hit. What breaks in practice: apps that cache credentials and never
renew —
the lease expires mid-run and the app dies at an unpredictable time; apps that renew
without
checking the response, so they keep renewing an expired lease; and the big one —
long-lived
processes that treat dynamic credentials like static ones and stop renewing after a
deploy.
The robust pattern is the renew-with-monitoring loop: renew, verify the response, and
alert
when renewals stop succeeding, rather than discovering it at the next deploy."

**Interviewer**: "What exactly does the External Secrets Operator do?"

**Candidate**: "It's a Kubernetes controller that bridges the secret manager and the cluster.
You declare an ExternalSecret resource: which provider (Vault, AWS, etc.), which remote
key or
path, and which target — a Secret object plus how to refresh it. The controller
reconciles the
CRD: it reads the remote secret, writes the Kubernetes Secret, and re-syncs on the
refresh
interval. The win: application pods never talk to Vault — they read a normal Kubernetes
Secret,
which is what they were doing already. So adoption doesn't change the app; it changes
where the
value comes from and who refreshes it. The failure mode to design for: the controller
stopping
refresh, which shows up as the 'renewal stopped' problem at the Kubernetes layer."

**Interviewer**: "How do applications authenticate to Vault on Kubernetes?"

**Candidate**: "The Kubernetes auth method: each service account gets a signed JWT, and Vault
validates it against the cluster API — the service account, its namespace, and its
annotations. The mapping: service account -> Vault role -> a policy with paths and
capabilities. So the request is authorized twice: Kubernetes says the pod is who it
claims to
be, and Vault's policy says what that identity may read. The thing people get wrong: the
Vault
role and policy are the actual security boundary — a role that grants broad access to a
broad
service-account match is just secret sprawl with a JWT in front. Least privilege has to
be
defined per workload, which is real work."

**Interviewer**: "How do you rotate the PKI certificates Vault issues?"

**Candidate**: "Vault makes rotation boring, which is the point. Each issued certificate has a
TTL; the issuing application renews before expiry through the same endpoint that issued
it.
Because the CA lives in Vault, revocation is centralized: revoke a cert, add it to a
CRL, and
it dies immediately. The operational loop is the same as lease renewal — monitor that
renewal
is actually happening, and alert on certificates older than some fraction of their TTL,
because
the way incidents happen is a service that stopped renewing and now every client fails
the
handshake. The deeper question is short TTLs: hours instead of years, so even a leaked
key has
a small window."

**Interviewer**: "Your developers want a shared secrets spreadsheet instead. How do you win the
argument?"

**Candidate**: "Don't argue policy — argue consequences, then remove the friction. The
consequences are concrete: a spreadsheet has no audit trail, no revocation, no
per-person
access, and one leaked cell compromises everything on the page. The friction argument
matters
more: if getting a secret from Vault takes a ticket and three days, of course teams use
spreadsheets. So the winning move is making Vault *easier*: self-service roles scoped by
default, clear documentation, examples for their stack, and the operator syncing secrets
to
their namespace automatically. And enforce the hard boundary in CI — scans that block
secrets
in repos, which every modern platform has. People follow the path of least resistance;
make
the safe path the easy path."

**Interviewer**: "Where does encryption at rest fit — the storage backend is already encrypted?"

**Candidate**: "Layered, and each layer answers a different question. The storage backend
encryption protects Vault's own state at rest — that's the seal/unseal mechanism, keyed
by the
master key, auto-unsealed via KMS in production. But the secrets themselves should be
wrapped
at the point where they're produced: the transit engine encrypts application data so
Vault
never sees plaintext it doesn't need to, and app-level encryption (AES-GCM, envelope
encryption with a KEK/DEK split) protects data in databases. The principle: defense in
depth —
at-rest encryption is not one switch, it's encryption at each boundary, and the keys for
each
layer are separate, so compromising one layer doesn't decrypt everything."

**Interviewer**: "What's your plan if the secrets manager itself goes down?"

**Candidate**: "Three fronts. Availability: run it highly available — multiple replicas, raft
consensus, auto-unseal so recovery doesn't need humans. Degradation: design apps to
survive
it — they should read secrets at startup and cache them with a lease, so a brief outage
doesn't kill running services; fail-closed only where security demands it. And recovery:
documented unseal and restore procedure, backed up storage, tested by a game-day at
least
once a quarter. The uncomfortable truth: an outage of the secrets manager becomes an
outage of
everything if apps re-read secrets on every request — so the caching/lease pattern is
the
resilience story, and that's the same pattern that makes rotation work."
