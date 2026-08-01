# Lab 08: Mock Interview — Senior Security/Compliance Engineer

**Role**: Senior Security Engineer | **Topic**: Compliance Audit Engine | **Duration**: 45 minutes

---

## Interview Transcript

**Interviewer**: "Design a compliance audit engine with continuous control monitoring. The company must evidence SOC 2, PCI-DSS, and HIPAA across a cloud estate. What's your architecture?"

**Candidate**: "Let me decompose it into four planes. First, the **control catalog**: the mapping from regulatory frameworks to individual controls — each control has an ID, a framework source, a severity, and a *definition of compliance* that is executable. Second, the **evidence pipeline**: how we collect the raw signals — cloud configuration APIs, activity logs, vulnerability scans, IAM policies — continuously. Third, the **evaluation engine**: the logic that turns evidence into a pass/fail/unknown verdict per control, with a confidence level. Fourth, the **reporting and remediation layer**: audit reports per framework, violation tracking, and the ticketing workflow. The two design decisions that dominate everything else: controls must be *executable checks* rather than prose checklists, and evidence must be *immutable and complete* — because the artifact of an audit is the evidence trail, not the green checkmarks."

**Interviewer**: "What does 'executable control' mean concretely? Give me an example."

**Candidate**: "Take PCI-DSS requirement 10.2 — audit logging must capture all events. A prose control is 'implement audit logging'; an executable control is: 'for every account in scope, the CloudTrail-like activity log must be enabled with management events, data events for S3, and the log bucket must be encrypted, and the retention must be ≥ 180 days, and the log delivery must be checked for gaps.' Each clause maps to a *check* — a function over collected evidence. So the control is a small policy, written in a declarative policy language or as a composable check in code: `enablement(check(cloudTrail)), coverage(check(dataEvents)), integrity(check(logBucketEncryption)), retention(check(180d)), delivery(check(gapReport))`. Executable controls give you three superpowers: the audit can be automated (no manual evidence collection weeks before an audit), the verdict is *reproducible* (same evidence, same verdict — auditors trust that), and the control can be continuously monitored instead of snapshotted annually."

**Interviewer**: "How do you collect evidence continuously? Cloud config changes every minute."

**Candidate**: "Two complementary collectors. The **snapshot collector** polls the cloud APIs on a schedule — every 15-60 minutes per account — and records the full configuration state: every resource, its attributes, its IAM policy, its tags. Snapshots go into an immutable evidence store — object storage with WORM (write-once-read-many) semantics, because auditors will later ask 'show me the configuration as of March 3rd'. The **event collector** consumes the activity stream (CloudTrail, Azure Monitor, GCP audit logs) and appends to the same store. Together they answer two different questions: 'what was the configuration at time T' (snapshot) and 'what changed and who did it' (events). Both are *append-only*: nothing is ever edited or deleted — the evidence store is the single source of truth for every verdict ever produced."

**Interviewer**: "Now the evaluation engine. How do you handle the fact that a verdict is time-dependent — a control may have been failing for 3 hours and then fixed? And how do you handle 'unknown'?"

**Candidate**: "Every verdict is a *time-series value*, not a point: control X over framework Y for resource R at hour H is PASS/FAIL/UNKNOWN. The engine evaluates each check per evaluation cycle (hourly is my default) and stores the verdict series. The interesting engineering is the aggregation semantics: a control is FAIL at time T if *any* in-scope resource fails, and the control's 'compliance over the last 30 days' is the ratio of time it was passing — that's the **compliance score**. 'Unknown' is the honest third state: evidence missing, collector broken, or check not yet executed. Unknown must never be silently treated as PASS — that's the classic audit fraud pattern; unknown feeds its own metric (evidence coverage) and an unknown for more than a defined grace period pages the platform team. The failure that people overlook: *freshness* — the verdict must carry the evidence timestamp; a PASS from a collector that stopped 6 days ago is meaningless, so the score is computed over verdicts weighted by evidence freshness."

**Interviewer**: "Continuous monitoring means continuous false positives. How do you keep the noise down while staying strict?"

**Candidate**: "The verdict pipeline has three layers: detection (the check), classification (is this a real violation or an accepted exception?), and escalation (ticket → ticket with SLA → page). The exception mechanism is critical: a **suppression** with a time-bound waiver, an owner, and an approver — e.g., 'this dev account has encryption disabled for 7 days because the key migration is in progress, approved by the security lead'. Suppressions are themselves audited and expire automatically; a control can also have *scope exclusions* (a bucket tagged `data-classification=public` legitimately skips the public-read check). The scoring must reflect this: suppressed time counts as compliant (the risk is accepted, not forgotten), but the report shows the suppression count so the audit trail is transparent. And there's a feedback loop: any check that fires more than, say, 5% of the time is reviewed — either the environment is badly misconfigured or the check needs tuning, and both are worth knowing."

**Interviewer**: "How do you map framework requirements to controls when the frameworks overlap? PCI 10.2 and SOC 2 CC7.3 both cover audit logging."

**Candidate**: "The control catalog is a **mapping layer, not a copy of each framework**. We maintain a canonical set of *technical controls* — 'audit-logging-enabled', 'encryption-at-rest', 'mfa-required', 'least-privilege-iam' — each with one implementation, and each framework requirement *references* canonical controls plus framework-specific ones (PCI adds 'protect stored cardholder data', HIPAA adds 'BAA in place'). The evidence is collected once; the verdict is computed once per canonical control; framework compliance is a *view* over the canonical verdicts. This avoids the nightmare of evaluating the same check 30 times with 30 slightly different definitions and getting 30 inconsistent answers. The framework-specific bits — scoping (which systems are in scope for PCI vs HIPAA) and documentation evidence (policies, sign-offs, DR test results) — live in the mapping layer as attachment requirements."

**Interviewer**: "Scoping is a great point — the PCI scope isn't the whole cloud estate. How does the engine know what's in scope?"

**Candidate**: "Scoping is declared, not inferred: every resource carries scope tags — `pci:in-scope`, `hipaa:covered-entity` — and the evaluator only runs PCI controls against PCI-scoped resources. But there's a subtlety auditors hammer on: **scope creep detection**. If a resource is tagged in-scope but nothing about it is evaluated (a new service type the engine doesn't have a check for), the engine must flag it as 'in-scope but unevaluated' — that's the gap that becomes an audit finding. Conversely, an asset that *should* be in scope but isn't tagged — a bucket containing cardholder data with no scope tag — is detected by data classification heuristics: the engine scans object metadata and flags untagged assets with sensitive content. Scoping drift is where real audits fail, so the engine treats scope as evidence to be continuously verified, not a static config."

**Interviewer**: "How do you report to auditors? What artifacts do you produce?"

**Candidate**: "Three artifacts. The **live dashboard** for engineers: per-framework compliance score, failing controls ranked by severity and blast radius. The **executive report**: monthly compliance score trends, new violations, open waivers, remediation velocity — this is what the board sees. And the **audit evidence pack**: for every control, the evidence the verdict is based on — the snapshots, the events, the check execution logs, the suppression records — exported as an immutable bundle with a signed manifest (hash chain), so the auditor can replay any verdict back to its evidence. The hash-chain manifest is the detail that makes the engine credible: if the evidence can't be verified as unmodified, the audit is meaningless, so we cryptographically link every evidence write to the previous one."

**Interviewer**: "How would you test an audit engine? The whole point is that it's trustworthy."

**Candidate**: "Test the checks against a **fault-injection environment**: deploy deliberately non-compliant configurations — a public S3 bucket, an unencrypted RDS instance, a user with no MFA — and assert each check fires with the right verdict and the right severity. Then golden tests for the aggregation semantics: craft verdict series and assert the time-weighted scores, the unknown handling, and the waiver expiry behavior. Then the critical test: **replay determinism** — re-run the entire evaluation over a frozen evidence bundle and assert byte-identical reports; if evaluation isn't deterministic, you can't audit it. And production-shadow evaluation: run the new check version in parallel with the old one for a week and diff the verdicts on real data before promoting."

---

## Wrap-Up

**What the interviewer is looking for**:
- Controls as executable checks with a definition of compliance, not prose
- Immutable, append-only evidence with freshness tracking
- The PASS/FAIL/UNKNOWN trichotomy and time-weighted compliance scores
- Waiver/suppression mechanics with expiry and auditability
- Canonical controls mapped to frameworks, not per-framework duplicates
- Scoping as continuously verified evidence (scope creep detection)
- Determinism and replayability of evaluations

**Common mistakes candidates make**:
- Treating 'unknown' as pass
- Point-in-time verdicts with no time-series aggregation
- Duplicating checks per framework instead of mapping to canonical controls
- No freshness check on evidence
- Suppressions without expiry and owner
- Reports without a reproducible evidence trail
