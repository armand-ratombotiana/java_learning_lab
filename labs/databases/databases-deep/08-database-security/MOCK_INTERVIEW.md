# Lab 08: Mock Interview — Database Security

**Role**: Senior Database / Security Engineer
**Duration**: 45 minutes
**Company style**: FAANG / fintech / healthcare (compliance-heavy stacks)

---

**Interviewer**: "Let's start broad. What are the layers of database security, bottom to top?"

**Candidate**: "Six layers, roughly: (1) **Network** — the database must not be reachable from the internet: VPC isolation, security groups, TLS-only connections, and ideally mTLS with client certs. (2) **Authentication** — who are you: passwords (with strong hashing and rotation), Kerberos, LDAP/SSO, client certificates, and now passkeys where supported; centralized IAM (e.g., IAM Auth for RDS) removes password management entirely. (3) **Authorization** — what can you do: roles, privileges (GRANT/REVOKE), and the finer-grained mechanisms — column-level grants, row-level security policies, and dynamic data masking. (4) **Audit** — what did you do: statement and connection logs, who queried which rows of sensitive tables, alerting on anomalies. (5) **Data protection at rest** — TDE (transparent data encryption) and KMS key management, plus application-level encryption for the most sensitive columns. (6) **Data protection in motion** — TLS and, at the application boundary, formats like envelope encryption. The mistake to avoid: jumping to fancy features while leaving the network open — I'd always sequence network isolation → authentication → authorization → audit."

**Interviewer**: "Focus on authorization. What is row-level security (RLS), and when is it the right tool?"

**Candidate**: "RLS lets you attach a *policy* to a table — a predicate that's implicitly ANDed into every query: `CREATE POLICY tenant_isolation ON orders USING (tenant_id = current_setting('app.tenant_id'))`. Every SELECT/INSERT/UPDATE/DELETE is automatically filtered or restricted to rows the predicate allows. It's right when you can't (or won't) build the predicate into every query — multi-tenant SaaS where the same table serves many tenants and you want a *defense-in-depth guarantee* rather than relying on each developer remembering `WHERE tenant_id = ?`. PostgreSQL has it (row security), Oracle has VPD (Virtual Private Database with policies + application contexts), SQL Server has row-level security with a security policy function. The key properties: policies apply even when the query omits the condition — that's the guarantee — and they compose with GRANTs (row-level is layered on top of column/table privileges)."

**Interviewer**: "How does the policy evaluation actually work — what's the performance model?"

**Candidate**: "The policy predicate is a normal SQL expression — `tenant_id = current_setting(...)` — so the planner treats it like any other filter: it's pushed into the plan (ideally into the scan node) and uses indexes. For tenant_id = constant, that's an index lookup per query — effectively free. Performance traps: (1) a predicate that forces a full scan (`age(created_at) < 90` — non-sargable) defeats indexing; (2) subqueries or functions in the predicate run per row — use `current_setting` / application context (session constants) instead, or `SECURITY DEFINER` views for the complex cases; (3) `FORCE ROW LEVEL SECURITY` with `BYPASSRLS` — superusers must still be able to bypass for maintenance, but every bypass role is an attack surface to minimize."

**Interviewer**: "What are the classic RLS failure modes and gotchas?"

**Candidate**: "The big five. (1) **Policy bypass via views/functions**: a `SECURITY DEFINER` function owned by a privileged role executes with the *owner's* rights — a user can call it to read data the policy would block. Fix: design definer functions carefully, and use `SECURITY INVOKER` by default. (2) **Privilege escalation via insert/update**: if you allow INSERT but the policy uses `WITH CHECK`, a user could insert a row whose `tenant_id` is *someone else's* — `WITH CHECK` clauses exist precisely to prevent that: every inserted/updated row must satisfy the policy. (3) **The `admin` account with `BYPASSRLS`** left in the application connection string — the policy is then decorative. (4) **Cache poisoning by session reuse**: connection pools reuse sessions; if the policy reads a session variable (`current_setting('app.tenant_id')`) that a previous connection set, rows leak across tenants. Fix: set the variable on checkout, never trust pre-existing session state, and prefer per-request prepared connections. (5) **Policy doesn't apply to superuser/table owner**: PostgreSQL applies RLS only to non-owners (unless `FORCE`); people forget and test as `postgres` — everything 'works' — then the app user hits the wall."

**Interviewer**: "Beyond rows — column-level protection. Dynamic data masking vs encryption: walk me through the tradeoff."

**Candidate**: "**Dynamic data masking** (SQL Server, Oracle 12c+, and app-level libraries): the engine rewrites the result — a masked column shows `xxx-xx-1234` or `NULL` to users without a mask permission. Pros: zero schema change, transparent to queries, easy to deploy; the data stays usable for aggregates. Cons: it's *display-time* — someone with direct access (backups, exports, a privileged DBA role) sees the real values; masking is security theater against anyone with data access. **Encryption**: the column stores ciphertext; only the app holding the key can read it. Pros: real confidentiality — backups, DBAs, and breaches yield nothing. Cons: querying requires decryption (indexes on encrypted columns are limited — equality hashing only), key management complexity, and app changes. The rule: masking for *least-privilege display* (support agents), encryption for *confidentiality* (PII/PCI at rest). They're complementary, not alternatives."

**Interviewer**: "How do you audit a database without writing an auditor's nightmare?"

**Candidate**: "Layered, purpose-driven logging. (1) **Connection logs** — who connected from where, when (the cheap always-on layer). (2) **Statement logs on sensitive tables** — `LOG` the SQL text (carefully — avoid logging the parameter values of passwords!) for any query touching `customers.pii`; PostgreSQL `pgaudit` or Oracle `FGA` (fine-grained auditing) can trigger on *rows returned*, not just statements. (3) **Alerting, not just logs** — the log is useless if nobody reads it: alert on first-time patterns — a role that never touches `payments` suddenly SELECTing it at 3 AM; a query returning > 10K rows from a PII table (bulk export signal). (4) **Immutable storage** — ship logs to an append-only, out-of-band sink (S3 object lock) so a compromised database can't scrub its own audit trail. The interview one-liner: *audit what changes and what leaves, alert on the first deviations, and make the trail tamper-proof.*"

**Interviewer**: "Now the design question: multi-tenant SaaS, one shared `orders` table, 500 tenants, and a compliance requirement that tenant A can never see tenant B's rows — even via a query bug. Design the security architecture."

**Candidate**: "Defense in depth, four layers. (1) **Application layer**: every query goes through a data-access layer that *always* injects `tenant_id = :current` — the primary access control. (2) **Database RLS as the guarantee**: `CREATE POLICY orders_tenant ON orders USING (tenant_id = current_setting('app.tenant_id'))` with `WITH CHECK (tenant_id = current_setting('app.tenant_id'))`, and `ALTER TABLE orders FORCE ROW LEVEL SECURITY`. The app's connection pool sets the tenant context at checkout (with a race-safe checkout hook); the application DB role has no `BYPASSRLS`. (3) **Column masking**: payment-card fields masked for all but a privileged finance role; encryption at rest via TDE for the whole cluster. (4) **Audit + alerts**: log and alert on any query against `orders` from a non-app role, any attempt to change the tenant context mid-session, and any connection with `BYPASSRLS` (which shouldn't exist in the app path). Then the acceptance test: a security suite that tries the classic attacks — cross-tenant SELECT by injecting `WHERE 1=1`, cross-tenant INSERT, reading via a view, changing `current_setting` mid-session — and asserts every attempt returns nothing or errors. That's the property that compliance actually cares about."

**Interviewer**: "If you had to pick the single most important defense for a database reachable by an app, what is it?"

**Candidate**: "Network isolation + least-privilege credentials, in that order. If the database is unreachable except from the app tier, and the app's credentials can only touch what it needs, then most attack paths die at the perimeter — before RLS, masking, or audit even matter. Every other control is a layer on top; this one is the floor."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| Layered model | Gave a complete network→auth→authz→audit→encryption stack |
| RLS mechanics | USING vs WITH CHECK, FORCE, BYPASSRLS, session-variable predicates |
| Failure modes | Named BYPASSRLS drift, session poisoning, definer-function bypass |
| Masking vs encryption | Knew masking is display-time, encryption is confidentiality |
| Audit design | Trigger-based alerting, tamper-proof out-of-band logs |
| Architecture | Four-layer multi-tenant design with an acceptance test |

### Candidate strengths
- The `WITH CHECK` distinction (preventing cross-tenant *inserts*) is the detail most candidates miss.
- Connection-pool session poisoning is a genuinely senior observation.
- Closed with the right priority: perimeter first.

### Gaps to work on
- Didn't mention **audit log writes under load** (synchronous audit logging adds latency; buffer + batching needed).
- Could have mentioned `REVOKE` on public schema and default privileges as hygiene.
- Missed **key management specifics** (KMS, key rotation, envelope encryption) in the masking answer.

## Follow-up study prompts
1. How does PostgreSQL's `ROW SECURITY` interact with `SECURITY DEFINER` views and `policy` on the *view* vs the *table*?
2. What is Oracle VPD's application context, and how does it differ from PostgreSQL's `current_setting` approach?
3. How do column-level masks compose with exports/backups — and what does that imply for "masked" data in a data warehouse?

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Let's go deep on authentication. What are the modern options for a database, and what would you choose for a 200-service microservice platform?"

**Candidate**: "The ladder, roughly by strength and operational cost: (1) **username/password** — the floor; must be strong-hashed (scrypt/argon2), rotated, and never in config files (inject via secrets manager); the risk is theft and reuse, not the hash. (2) **Kerberos** — mutual authentication, no password on the wire; used by older enterprise stacks; heavy to operate. (3) **Client TLS certificates (mTLS)** — the database verifies a CA-signed client cert; revocation and rotation are the operational burden. (4) **Identity-provider integration** — LDAP/SSO federation, or cloud IAM (AWS RDS IAM auth, GCP IAM): the database trusts the platform's identity system, credentials are short-lived (15 min tokens) and *can't be stolen from a config file*. For 200 services: cloud IAM or an OIDC/SPIFFE-style workload identity — each service gets its own identity, tokens are short-lived, rotation is automatic, and the DBA stops managing passwords entirely. The interview point: *auth is weakest where credentials are static — short-lived, machine-managed identity beats everything else*."

**Interviewer**: "Encryption at rest — TDE vs application-level encryption. When do you use which, and what's the key-management model?"

**Candidate**: "**TDE** (transparent data encryption): the engine encrypts data files/backups with a *tablespace/datafile key*, which is wrapped by a master key in the KMS. Zero app change, protects backups and stolen disks — but *not* the database process itself: anyone with DB access reads plaintext. **Application-level encryption**: the app encrypts sensitive columns before writing; the database stores ciphertext; even a DBA or a breached DB exposes nothing. Costs: app changes, and *queryability* — you can't range-scan or index plaintext; you get equality lookup via deterministic encryption (HMAC-style) and nothing else; joins on encrypted columns are limited. **Key management**: envelope encryption — a KMS master key wraps the data key; you must rotate master keys (cheap, re-wrap only) and data keys (expensive, re-encrypt). The model to name: *KMS holds the root, the database holds wrapped keys, and keys never leave the HSM/KMS in plaintext — every access is audited*. The rule: TDE for the whole cluster as a floor; app-level for the columns where confidentiality must survive *database compromise*, not just disk loss."

**Interviewer**: "SQL injection — is it a database security problem, and what's the defense stack?"

**Candidate**: "It's the app's bug, but the database must not be the second line of defense... actually it *is* part of the layered answer. The stack: (1) **parameterized statements everywhere** — the only real fix; the database receives values and structure separately, so no input can become syntax. (2) **least privilege** — the app's DB role must not be able to do what injection could exploit: no `DROP`, no `pg_read_server_files`, no cross-schema GRANTs; a `SECURITY DEFINER`-heavy schema is an injection amplifier. (3) **defense in depth in the database**: WAF-level input filtering, and for legacy systems — `pgaudit`-logged suspicious patterns, or row/column security so even a crafted query sees only what the policy allows. (4) **detection**: SQL-injection-pattern alerts (UNION SELECT, stacked queries, `information_schema` probes) on the query log. The interview point: parameterization kills 99% of it, least-privilege kills the blast radius of the rest, and audit catches the attempts — all three are database-side engineering, not just app discipline."

**Interviewer**: "Audit logging under load — synchronous audit writes add latency to every transaction. How do you keep audit without paying that tax?"

**Candidate**: "The engineering pattern: (1) **asynchronous, bounded**: the audit writer goes to an in-memory queue drained by a dedicated thread, so the transaction path only enqueues; the queue is bounded, and overflow policy is *fail-open-with-alert* or *fail-closed* depending on the compliance regime — for PCI, fail-closed is required (block the operation), for most others fail-open with a high-severity alert beats availability loss. (2) **batch and ship**: the auditor writes batched records (hundreds per batch) to append-only storage — log shipping is naturally batched; (3) **out-of-band sink**: audit goes to a *different* storage system (S3 object lock, SIEM) so the DB's own failure doesn't destroy the trail and the DB's own compromise can't erase it; (4) **sampling**: connection-level logs can be sampled; *row-access* logs for sensitive tables must not be. The one-liner: *audit asynchronously, ship in batches, store out-of-band, and know your overflow policy*."

**Interviewer**: "Final: your RLS-based multi-tenant design gets a compliance review. The reviewer asks: 'what happens if a developer runs a migration as the app role?' Walk me through the risk."

**Candidate**: "The scenario: a migration script runs as the app's connection role against the tenant-shared `orders` table — or worse, a developer session with elevated privileges. Risks: (1) if the migration runs as a `BYPASSRLS` role or table owner, the tenant policy is silently absent — and if the migration *touches* the policy (dropping/recreating the table, `ALTER TABLE ... DISABLE ROW SECURITY`), a window opens; (2) a bulk UPDATE in the migration runs *without* the tenant predicate — it would update every tenant's rows (the app role has no tenant context in a migration job), a cross-tenant write at scale; (3) the migration tool may set `current_setting('app.tenant_id')` from a shared config — one tenant's context applies to the whole migration. The defense: (a) migrations run under a *separate* deploy role with `BYPASSRLS` — but then the migration must be *reviewed as security-sensitive code* (it is part of the trust boundary); (b) require migrations to be `SECURITY INVOKER` and explicit about tenant scope — never allow implicit all-tenants writes; (c) the acceptance suite I mentioned runs *after every migration* — the cross-tenant probes are part of CI, so a policy-disabling migration fails the build. The one-liner: *RLS is only as strong as the deployment pipeline that maintains it — migrations are the #1 way policies silently rot*."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Prepare the key-management answer with a concrete envelope-encryption walk (KMS master key → wrapped data key → re-encrypt-on-rotate).
- Practice the audit-under-load answer with the fail-open/fail-closed decision matrix — it's the operational nuance interviewers probe for.
- Rehearse the migration-threat narrative — it reframes RLS as a *deployment* problem, which is the senior take.

### One-sentence takeaway
- "Database security is a layered contract: perimeter first, least-privilege always, RLS as the guarantee, audit as the tripwire — and the weakest layer is whichever one a process or migration bypasses silently."

### Self-check questions (run before the real interview)
1. Can I lay out the six security layers bottom-to-top and argue the deployment order?
2. Can I explain the difference between masking and encryption, including what each survives?
3. Can I walk through RLS policy evaluation, `USING` vs `WITH CHECK`, and `BYPASSRLS` risks?
4. Can I design an async audit pipeline with an explicit overflow policy?
5. Can I describe workload identity / IAM auth as the modern alternative to passwords?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** Order the six security layers by deployment priority.
**Hint.** Network → authn → authz → audit → encryption at rest → encryption in motion.

**Q2.** What does RLS add that an app-layer WHERE clause doesn't?
**Hint.** A guarantee: the policy applies even when the query forgets — defense in depth.

**Q3.** `USING` vs `WITH CHECK` in a policy — what does each prevent?
**Hint.** USING filters reads; WITH CHECK prevents inserting/updating rows that violate the policy.

**Q4.** Why is `BYPASSRLS` on the app role a silent killer?
**Hint.** The policy becomes decorative — every query bypasses tenant isolation.

**Q5.** Masking vs encryption — which survives a backup leak?
**Hint.** Encryption only; masking is display-time and reveals real values to anyone with data access.

**Q6.** What is the connection-pool RLS trap?
**Hint.** Reused sessions carry the previous tenant's `current_setting` — set context on checkout.

**Q7.** Name the three mechanisms of a good audit pipeline.
**Hint.** Async + batched shipping, out-of-band immutable sink, alerting on first-time deviations.

**Q8.** What kills 99% of SQL injection, and what caps the damage of the rest?
**Hint.** Parameterized statements; least-privilege DB roles shrink the blast radius.

**Q9.** TDE protects against what, exactly?
**Hint.** Disk loss/backup theft — not database compromise; app-level encryption covers that.

**Q10.** Why are migrations a threat to RLS?
**Hint.** They run with elevated roles and can disable/rebuild policies — CI must re-run the cross-tenant probes.

### Scoring
- **8-10 correct**: ready for the security loop.
- **5-7**: revise RLS mechanics and the masking/encryption distinction.
- **<5**: re-read the walkthrough before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab (`RowLevelSecurityEngine`) and pass the cross-tenant attack suite.
**Day 3**: Quick-Fire rounds; write the six-layer stack from memory.
**Day 4**: Rehearse the multi-tenant design answer (four layers + acceptance test).
**Day 5**: Drill the extended rounds (IAM auth, envelope encryption, audit under load, migration threats).
**Day 6**: Mock interview, 45 minutes, no notes.
**Day 7**: Score against the Debrief table; study the follow-up prompts.
