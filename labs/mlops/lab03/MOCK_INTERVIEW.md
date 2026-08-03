# Lab 03: Mock Interview — Model Registry & Versioning

**Role**: MLOps Engineer / ML Platform Engineer
**Duration**: 60 minutes
**Focus**: Model versioning, stage lifecycle, champion/challenger, lineage, rollback, registry architecture

---

**Interviewer**: "Walk me through the registry in this lab. What is a model version, and what lifecycle does it follow?"

**Candidate**: "A `ModelVersion` is the unit of everything: it carries `version`, `modelName`, the originating `runId` from experiment tracking, the `metrics` map, `parameters`, an `artifactPath` pointing at the binary, and a `stage` drawn from the enum `NONE, STAGING, PRODUCTION, ARCHIVED`. `ModelRegistry` owns a `Map<String, List<ModelVersion>>` keyed by model name and hands out versions via `registerVersion(modelName, runId, metrics, parameters, artifactPath)`, which assigns the next `versionCounter` number. Lifecycle is strictly staged: `promoteToStaging` moves NONE → STAGING, `promoteToProduction` moves STAGING → PRODUCTION, `archiveVersion` parks retired models. The demo's `fraud_detector` walks the full path: v1 goes staging → production, v2 and v3 stage, v3 takes production from v1, v4 becomes the new challenger, and v2 is manually archived."

**Interviewer**: "In the demo, promoting v3 to production automatically archives v1. Why is that the right default?"

**Candidate**: "Because a model name should have exactly one production version at a time — two production pointers means the serving layer can't answer 'which model is live?'. `promoteToProduction` encodes it: it looks up `getProductionModel(modelName)` and, if the incumbent differs from the incoming version, calls `transitionTo(ARCHIVED)` on it before promoting the new one. So the invariant is maintained by the registry, not by human discipline. The demo prints it clearly: `Model fraud_detector v1: PRODUCTION → ARCHIVED` right before `Model fraud_detector v3: STAGING → PRODUCTION`. This is exactly MLflow's `transition_stage` semantics — and it makes rollback trivial, which is the interview question everyone asks."

**Interviewer**: "So how does rollback actually work in this design?"

**Candidate**: "Rollback is not mutation — it's re-tagging an immutable version. The registry never deletes or edits a version; every `ModelVersion` is append-only after registration, which is why `registerVersion` always returns a new version number instead of overwriting. To roll back from v3 to v1, you call `promoteToProduction("fraud_detector", 1)`: the current champion v3 is archived, v1 moves from ARCHIVED to PRODUCTION, and the immutable history still shows both promotions and the archive. The interview notes add the production-grade requirement: run validation on the rollback target against the current data schema before re-tagging, because a model retired six months ago may not accept today's features. The key principle: version history is the source of truth; stage is just a pointer."

**Interviewer**: "The registry prints `★ CHAMPION` and `☆ CHALLENGER` tags. Explain the champion/challenger pattern as this lab implements it."

**Candidate**: "The champion is the production model — `getProductionModel` filters `stage == PRODUCTION`; the challenger is the candidate being evaluated — `getStagingModel` filters `stage == STAGING`. The demo ends with v3 as champion (`★`) and v4 as challenger (`☆`). What the registry doesn't do is route traffic — the interview notes describe the serving-side rule: a routing layer sends, say, 95% of traffic to the champion and 5% to the challenger, compares online metrics over a window, and only then promotes. The registry's job is narrower and more important: it guarantees there is exactly one champion and one challenger to route between, and that the promotion happens through the audited transition path rather than ad-hoc config."

**Interviewer**: "The demo registers four versions with accuracies 0.923, 0.935, 0.947, 0.941 — and v4, the *lowest* of the top three, is the challenger. Why not just promote the highest-accuracy version?"

**Candidate**: "Because offline accuracy is necessary but not sufficient. v4 uses a different architecture (`NeuralNet` vs the `XGBoost` of v2/v3) and different hyperparameters, so its 0.941 is a point estimate on one evaluation set — promoting it directly would skip the online evidence. The lab deliberately shows the process over the number: v3 with 0.947 is the champion because it beat v1's 0.923 in the registry's staged progression; v4 with 0.941 enters as challenger to prove itself against v3 with real traffic. The registry encodes the judgment call as a workflow — stage, compare, promote — instead of a max() over accuracy. If we optimized only accuracy, we'd skip the challenger phase and get surprised by online regression."

**Interviewer**: "How would you scale this registry to thousands of models across many teams, per the lab's Q1?"

**Candidate**: "Split the design like the interview notes: PostgreSQL for metadata — model names, versions, stages, lineage — and S3 for the binaries, because metadata is relational and hot, while artifacts are blobs. Add hierarchical namespaces — team/project/model — so `fraud_detector` becomes `payments/fraud/fraud_detector` and search stays sane at scale. Then microservice it: a registry API service, a versioning service, a lineage service. Cache the active production models in Redis so the serving layer's `getProductionModel` answers from memory, not the DB. And add webhook notifications on stage transitions so the serving platform and monitoring can react to promotions asynchronously."

**Interviewer**: "What is lineage in this model, and what must you store to make it audit-proof?"

**Candidate**: "Lineage is the answer to 'what produced this version?' — every field the lab's `ModelVersion` already carries plus a few more: the `runId` links back to the MLflow run, `parameters` record the hyperparameters, `artifactPath` records where the binary lives. For audit compliance the interview notes add: git commit of the training code, dataset version hash, training script path, evaluation metrics, the CI/CD run ID, timestamp, and *who* performed each promotion. And critically, every stage transition must land in an immutable event log — the demo's `transitionTo` printout is the event stream in miniature. If you can't replay every transition of a version after the fact, an auditor will find you out."

**Interviewer**: "Semantic versioning is mentioned as a best practice. How does it fit the registry's integer versions?"

**Candidate**: "The registry's internal `version` is a monotonically increasing integer — perfect for immutability and ordering, but opaque to humans. Semantic versioning adds meaning on top: bump `minor` when you retrain with new data but keep the architecture (v2 → v3 in the demo, both XGBoost), bump `major` for architecture or interface changes (v3 XGBoost → v4 NeuralNet). The guide's best practice is to tag versions with semver in the model name or description field while keeping the integer as the machine key. The two coexist: `major.minor.patch` tells reviewers how risky a promotion is; the integer tells the registry which immutable record to transition."

**Interviewer**: "What happens to models in the registry when you want to serve them? How does Lab 03 connect to Lab 05's serving layer?"

**Candidate**: "The registry is the source of truth for deployment. The serving pipeline calls `getProductionModel("fraud_detector")`, reads `artifactPath` — e.g. `s3://models/fraud_detector/v3/model.pkl` — and loads that binary into the model server. Because stages are the only thing that changes, a promotion is a data update, not a code deploy: the server polls or is notified of the new production pointer and hot-reloads the artifact. That separation is why auto-archiving the incumbent matters — if the registry ever returned two production versions, the server wouldn't know which artifact to load. This is the MLflow model: `get_latest_versions(name, stages=["Production"])` maps 1:1 to the lab's `getProductionModel`."

**Interviewer**: "What would you do if `getVersion` returned null in production?"

**Candidate**: "Fail closed. `promoteToStaging` and `promoteToProduction` already guard with `throw new IllegalArgumentException("Version not found")` — production code should never silently no-op a promotion request, because a null champion means the serving layer would route to nothing. The deeper lesson: the registry API must be total — every operation either changes state and returns the version, or throws — so callers can't confuse 'no change' with 'success'. And `getProductionModel` returning null should be a P0 alert: it means the model name has no live version, which is a configuration disaster, not a runtime oddity."

**Interviewer**: "Tie the registry to the LeetCode references: LRU Cache, file systems, version control."

**Candidate**: "LRU Cache (146) is the Redis caching layer for active versions — the interview notes' design: cache the hot production models with eviction, because the registry is a small metadata DB compared to serving QPS. Design File System (588) models hierarchical namespaces — team/project/model paths and their lookups. And a version control system is the registry itself: immutable versions, branch-like stages, and a history that can be diffed and rolled back. The unifying idea: all three are about *addressable, immutable state* — a cache, a tree, or a version chain — which is precisely what makes model management auditable."

**Interviewer**: "What's the most dangerous thing about the lab's `archiveVersion` being callable by anyone?"

**Candidate**: "It's a no-guard operation — it doesn't check whether the version is the production champion or whether another team depends on it. Archive a live champion and you've just broken serving. Production hardening: refuse to archive the current production version (or require an explicit `force=true` with a logged reason), require permissions per model namespace, and emit the webhook so downstream consumers learn before the artifact disappears. The interview notes' access-control gap applies here exactly — fine-grained ACLs are what the registry needs before teams beyond the demo can touch it. Immutability protects the data; authorization protects the operations."
