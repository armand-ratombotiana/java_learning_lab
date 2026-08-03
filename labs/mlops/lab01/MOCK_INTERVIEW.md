# Lab 01: Mock Interview — ML Pipeline Orchestration

**Role**: MLOps Engineer / Platform Engineer
**Duration**: 60 minutes
**Focus**: DAG-based orchestration, topological sort, retry semantics, idempotency, Airflow/Prefect/Dagster comparison

---

**Interviewer**: "Walk me through how you would design an ML training pipeline orchestrator from scratch."

**Candidate**: "I'd start with a DAG abstraction: a `PipelineDAG` that owns a map of named `PipelineTask` nodes, where each task carries its `dependencies`, an execution `action`, and a `maxRetries` budget. The DAG resolves a valid execution order via `topologicalSort()` — the lab implements Kahn's algorithm with an in-degree map and adjacency list, and it throws `IllegalStateException("Cycle detected in pipeline DAG")` if the sorted output is smaller than the task set, so you can't ship a cyclic pipeline. Execution then walks that order, checks that every dependency reached `SUCCESS`, and only then runs the task. That's exactly how Airflow's scheduler behaves: static DAG definition in code, dynamic resolution at runtime."

**Interviewer**: "Your `PipelineTask.execute()` handles retries inside the task itself. What are the semantics?"

**Candidate**: "Each task owns its retry state: `retryCount`, `status`, and `errorMessage`. `execute()` sets status to `RUNNING`, prints `[name] Starting (attempt X/Y)` where Y is `maxRetries + 1`, and runs the action. On exception it increments `retryCount`; if the count is still within `maxRetries` it prints `Failed (attempt ...) — retrying...` and the caller loops the task again; otherwise status becomes `FAILED` and it prints `Failed after N retries`. A critical detail: the loop only re-invokes `execute()` while status is neither `SUCCESS` nor `FAILED`, so a task that eventually succeeds leaves a clean terminal state with its retry count preserved — the summary then shows `data_ingest: OK (retried 1 time(s))`."

**Interviewer**: "In the lab demo, `data_ingest` has a 0.2 failure probability and `deploy_model` has `maxRetries = 0`. Why would you give a deployment task zero retries?"

**Candidate**: "Because retries are for transient failures, and deployment should be exactly-once-ish. `data_ingest` hits S3 — throttling and network blips are transient, so two retries at a 0.2 per-attempt failure rate makes a double failure vanishingly rare. `deploy_model` runs `simulatedTask("Deploying to staging", 500, 0.0)` with zero fail probability and zero retries: blindly retrying a deployment can double-promote a model or re-trigger side effects like cache invalidation. That's the same reasoning behind Airflow operators with `retries=0` on side-effecting steps — you want deterministic failures surfaced immediately, not masked by retry storms. The lab encodes this: `evaluate_model` and `deploy_model` have fail probability 0.0, while the data-heavy steps are flaky by design."

**Interviewer**: "What happens to downstream tasks when a dependency fails mid-pipeline?"

**Candidate**: "The DAG refuses to run them. In `execute()`, before submitting a task the code checks `task.dependencies.stream().allMatch(d -> tasks.get(d).status == TaskStatus.SUCCESS)`; if any dependency failed, the task is marked `FAILED` with `errorMessage = "Dependency failed"` and prints `[name] Skipped — dependency failed`. So a failed `train_model` means `evaluate_model` and `deploy_model` never touch a half-baked artifact — nothing silently runs against stale or corrupt outputs. In production this maps to Airflow's `trigger_rule=all_success` versus the dangerous default of running anyway."

**Interviewer**: "The lab runs tasks with `Executors.newFixedThreadPool(4)`. Walk me through the concurrency model and its correctness."

**Candidate**: "The thread pool gives bounded parallelism — four worker threads — matching a 4-core serving node. Tasks are submitted in topological order, each as a runnable that loops `task.execute()` until terminal state. Since dependencies are guaranteed completed before a task is submitted, the ordering is safe even with concurrent execution. The design is conservative: it only parallelizes tasks whose dependencies already succeeded, which is correct but leaves throughput on the table — independent branches could run concurrently. In production I'd use a proper scheduler that submits a task only when its in-degree reaches zero, like Airflow's scheduler, instead of the sequential submission this lab uses for clarity."

**Interviewer**: "How do you guarantee idempotency across pipeline reruns? Your demo pipeline reruns every night."

**Candidate**: "Idempotency is enforced outside the DAG, at the artifact layer. The lab's INTERVIEW notes the standard toolkit: deterministic task IDs derived from input hashes, write-once storage with versioning, timestamp-partitioned outputs, and upserts for feature tables. In our Java pipeline, each task should write to a key like `s3://features/dt=2026-08-03/` so a re-run overwrites the same partition instead of duplicating rows; retries then become safe because the second attempt writes the same content. And the orchestration layer itself is idempotent: re-running `PipelineDAG.execute()` on a fresh DAG yields the same topological order and the same terminal summary, because sorting depends only on the dependency graph, not on execution history."

**Interviewer**: "Map your Java classes to Airflow concepts, and tell me what Airflow gives you that your code doesn't."

**Candidate**: "The guide's mapping table is: `PipelineTask` → `BaseOperator`, `PipelineDAG` → `DAG`, `topologicalSort()` → the scheduler, and `execute()` → operator `execute()`. What Airflow adds: a persistent scheduler with heartbeat and backfill, rich trigger rules beyond all-success, SLA timers, UI, and an operator ecosystem. What my Java DAG adds that Airflow's static DAGs lack: nothing — but Prefect-style dynamic DAGs would generate tasks from config at runtime. The lab's `TaskFactory` idea in the INTERVIEW notes exactly that: dynamic task generation by reading YAML or a DB, which Airflow only gets with `generate_dag_bag` tricks."

**Interviewer**: "Explain exponential backoff as described in the lab's interview notes, and how you'd add it to this code."

**Candidate**: "The lab's `execute()` retries immediately — attempt 2 starts the moment attempt 1 throws. The INTERVIEW notes call for exponential backoff on transient failures: wait `2^retryCount * baseDelay` (plus jitter) before the next attempt, so a thrashing S3 service gets time to recover instead of being hammered. I'd insert `Thread.sleep` with the backoff formula between `execute()` calls in the retry loop, and cap the delay. Contrast with deterministic failures — schema mismatch in `data_validate` — where backoff is useless: fail fast, alert immediately, and don't waste the retry budget."

**Interviewer**: "How do you handle a long-running pipeline that dies at hour three? What's checkpointing in this context?"

**Candidate**: "Checkpointing at task boundaries: each task writes its output as a versioned artifact, so a crashed pipeline restarts from the last `SUCCESS` task rather than from the top. In this Java model, the terminal statuses `SUCCESS`/`FAILED` per task form exactly the resume record — persist that state map, and on restart, skip tasks already `SUCCESS`. The retry loop is already naturally resumable because `execute()` re-enters cleanly. Airflow does the same with `max_active_runs` and task state in its metadata DB. The key design rule: a task must be able to re-run after a partial failure without corrupting downstream inputs — that's where partition-based writes save you."

**Interviewer**: "Airflow vs Prefect vs Dagster — the lab's Q3 asks for a comparison. Which would you pick for a team of 20 ML engineers?"

**Candidate**: "Airflow: mature, huge operator ecosystem, but static DAGs and weak data lineage. Prefect: dynamic DAGs, native retries with backoff, cloud-native caching — strong for ML where task graphs change per dataset. Dagster: asset-centric with lineage and data-quality hooks — best when the platform's job is data governance. For an ML-heavy team I'd pick Prefect for its dynamic graphs and caching, and Dagster if lineage is a hard requirement; Airflow only if the org already runs it. The lab's answer and mine agree: for ML, Prefect's dynamic nature and caching are the differentiators."

**Interviewer**: "The summary prints `OK`, `FAIL`, `RUN`, `PEND`. Why is a printed summary a bad production notification mechanism?"

**Candidate**: "It's a demo artifact, not production telemetry. `printSummary()` iterates the task map and formats a status line per task — fine for the classroom, useless for paging. In production each task transition should emit structured events: task name, attempt, latency, error class — pushed to Prometheus/Slack/PagerDuty. The INTERVIEW notes say alerting integrates with PagerDuty/Slack via webhooks; the Java seam is the `Consumer<PipelineTask> action` — swap it for a wrapper that publishes metrics before and after `execute()`. Also, `TaskStatus.RUNNING` and `PENDING` should never survive a finished pipeline; a `RUN` in the summary means the executor was killed mid-task — that's exactly when you'd resume from checkpoint."

**Interviewer**: "How would you scale this orchestrator to 500 scheduled pipelines with overlapping dependencies?"

**Candidate**: "Three moves: separate DAG definition from execution — every `PipelineDAG` is code in a repo, versioned, tested on PR with a cycle-detection test that reuses `topologicalSort()`; give each pipeline its own retry/backoff profile and SLAs; and run a distributed executor with a queue between scheduler and workers so pipelines share a pool instead of each owning threads. The concurrency primitive stays the same — the lab's `ExecutorService` becomes a pool per pipeline tier. And scheduling metadata (cron, data-availability triggers) goes in config, like Airflow's `schedule_interval`, so adding a pipeline is a config change plus a DAG class."

**Interviewer**: "Last question — the LeetCode connections: Course Schedule and Task Scheduler. How do they map?"

**Candidate**: "Course Schedule (207) is exactly `topologicalSort()`: detect whether a dependency graph is acyclic — our Kahn's implementation answers both 207 and 210 (returning the order). Alien Dictionary (269) builds a DAG from ordering constraints, which models inferring task order from event logs. Task Scheduler (621) is the scheduling-angle: with cooldowns between same-task runs, which our dependency graph generalizes — a cooldown is just an edge to a sleep task. If I had one takeaway: topological sort is the heart of orchestration, and the lab implements it in ~30 lines."

**Interviewer**: "What's the most common production failure you've seen in ML orchestrators, and how would this design prevent it?"

**Candidate**: "Silent partial success — three of six tasks run, the pipeline 'finishes', and nobody notices the model wasn't refreshed. This design prevents it structurally: downstream skipping is explicit (`Skipped — dependency failed`), the summary lists every task with a terminal status, and retries are bounded and visible. The second failure mode is unbounded retry loops — a task with `retries=-1` hammering a dead dependency. The lab's `maxRetries` budget, applied at task level with a per-attempt printout, makes runaway retries impossible and debuggable from the logs."
