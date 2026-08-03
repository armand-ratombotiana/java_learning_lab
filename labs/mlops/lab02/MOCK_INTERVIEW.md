# Lab 02: Mock Interview — Experiment Tracking with MLflow

**Role**: MLOps Engineer / Machine Learning Platform Engineer
**Duration**: 60 minutes
**Focus**: Experiment tracking, MLflow REST API, run/parameter/metric logging, reproducibility, tooling trade-offs

---

**Interviewer**: "Walk me through the tracking client in this lab. What does it actually do over the wire?"

**Candidate**: "The `MlflowTrackingClient` is a thin wrapper over MLflow's REST API. Every operation funnels through `postJson(path, json)`, which opens an `HttpURLConnection`, sets `Content-Type: application/json`, writes the body, and throws `RuntimeException("MLflow API error " + code + " for " + path)` on any non-2xx. From there the lab exposes the five verbs we actually need: `createExperiment(name)` POSTs to `/api/2.0/mlflow/experiments/create` and regexes the `experiment_id` out of the response; `createRun(experimentId)` does the same against `/api/2.0/mlflow/runs/create`; `logParam` and `logMetric` hit `runs/log-parameter` and `runs/log-metric`; and `setTerminated(runId, "FINISHED")` POSTs to `/api/2.0/mlflow/runs/update`. That's the whole contract — the client is stateless and re-created per process, which is exactly how you want a tracking client to behave in CI."

**Interviewer**: "The main loop creates 12 runs — three learning rates times two epoch counts times two batch sizes. Walk me through what gets logged per run."

**Candidate**: "Each run gets a fresh `runId` from `createRun`, then three parameters — `learning_rate`, `epochs`, `batch_size` — logged *before* training starts, per the guide's best practice of logging all hyperparameters upfront. Then the demo simulates per-epoch training: for each epoch it computes accuracy and loss and calls `logMetric(runId, "accuracy", accuracy, epoch)` and `logMetric(runId, "loss", loss, epoch)`, so the UI can render a learning curve. Finally `final_accuracy` is logged at step `epochs` and the run is terminated with status `FINISHED`. The console prints `Run <first 8 chars of runId>: lr=..., epochs=..., batch=...` so you can correlate what you see in the MLflow UI with the terminal."

**Interviewer**: "Why log accuracy every epoch *and* a final accuracy? Isn't that redundant?"

**Candidate**: "They serve different queries. The per-epoch series answers 'did training converge, and how noisy was it?' — you can spot divergence or oscillation at epoch three instead of at run completion. The `final_accuracy` metric at step `epochs` is a scalar landmark: it's what a model-selection job queries with `get_best_run`, what a registry gate compares against a threshold, and what the lab's next lab (Model Registry) reads when deciding to promote. In MLflow terms the epoch series is a metric with `step` granularity — that's why `logMetric` takes the `step` parameter — and the final one is the summary statistic. Same number, different access patterns."

**Interviewer**: "The lab calls `simulateTraining(lr, epochs, batchSize)` which seeds `new Random(42)`. Why does a deterministic simulation matter for an experiment tracking lab?"

**Candidate**: "Because tracking is only meaningful if a run is reproducible. With `Random(42)` every process gets the identical pseudo-random stream, so the same hyperparameters always produce the same accuracy — you can rerun the lab and get identical metric values in the UI, which lets you verify the tracking pipeline itself is correct rather than a moving target. In production this is the seed-logging practice: the interview notes explicitly say to log the random seed, input dataset hash, git commit, and conda environment. The demo makes the point visible: the `learning_rate` effect on final accuracy is a clean, repeatable pattern rather than noise."

**Interviewer**: "Design a tracking system for 50 data scientists. The lab's Q1 asks for this — what are your main building blocks?"

**Candidate**: "MLflow Tracking Server with PostgreSQL as the backend store instead of the default file store — you need a real DB once runs number in the thousands and 50 people query concurrently. Then: hierarchical experiment naming (team/project/run) so search doesn't devolve into a flat dump; role-based access control, which vanilla MLflow lacks, so a proxy or auth layer in front; and automated tagging from CI/CD — dataset name, git commit, environment. Artifacts — model binaries, scalers, plots — go to S3/GCS with lifecycle policies so you don't pay for 5,000 copies of the same base image. The lab's Java client is the thin edge of that platform; the server side is the reliability story."

**Interviewer**: "What exactly do you log to make a run reproducible, and where does Java fit in?"

**Candidate**: "Four things: the full environment — `conda.yaml` or `requirements.txt`; the source code pointer — git commit hash, ideally the exact hash, not the branch name; the input data version — dataset hash or version id from the feature store; and the random seed plus any environment-dependent settings like JVM version. The interview notes add a Java-specific trick: log `System.getProperties()` and the classpath as artifacts, because a Java training job's behavior can change with JDK patch versions or a dependency bump that silently lands in the classpath. In the lab's client, all of that is just more `logParam` / `logArtifact` calls on the same run."

**Interviewer**: "What are MLflow's limitations, and how do you work around them in a serious platform?"

**Candidate**: "Three big ones, per the lab's Q3: no native hyperparameter search — integrate Optuna or Ray Tune, with MLflow as the sink for trial results; no fine-grained access control — wrap the server with an auth proxy; and weak lineage across multi-step pipelines — a training run doesn't automatically know which data pipeline version produced its inputs, so you extend tracking with DAG run IDs: the orchestrator (Lab 01's `PipelineDAG`) stamps each run with its pipeline execution ID, and the registry ties model versions back to that ID. The pattern is: MLflow owns the run/experiment ledger; your platform owns the relationships."

**Interviewer**: "MLflow vs Weights & Biases vs Neptune — pick for three different org profiles."

**Candidate**: "MLflow: open-source, simple REST API, self-hostable — right for on-prem and regulated orgs that can't send training metadata to a SaaS. W&B: best-in-class visualization and collaboration — right for research teams doing rapid prototyping where interaction speed is the bottleneck. Neptune: strongest team management and metadata organization — right for enterprises running many concurrent teams who need structure. The lab's framing is exactly that: MLflow for self-hosted/on-prem, W&B for rapid prototyping, Neptune for enterprise teams. For the 50-data-scientist platform I'd start with MLflow on PostgreSQL and layer what's missing."

**Interviewer**: "The client does regex parsing on JSON responses — `resp.replaceAll(".*\"experiment_id\":\"(\\d+)\".*", "$1")`. Why is that a problem at scale?"

**Candidate**: "It's the classic demo trade-off: zero dependencies, but brittle against API changes and order-of-fields differences. At scale I'd swap it for a real JSON parser or a typed REST client — the endpoint contract (`/api/2.0/mlflow/...`) is stable, so a generated client gives compile-time safety. The error path matters more: `postJson` already distinguishes network failure from HTTP error codes, but with regex parsing a malformed 200 response silently yields garbage instead of a parse error. My rule: fine for a teaching client, unacceptable in a platform that 50 data scientists depend on — and worth a test suite that replays recorded MLflow responses."

**Interviewer**: "How do you compare runs to decide which model wins, given what the lab logs?"

**Candidate**: "Query the experiment's runs, fetch `final_accuracy` for each, and rank — the lab's 12-run sweep is small enough to eyeball in the UI, but the API pattern matters: select the best run, then read its parameters as the source of truth for the registry entry. The critical discipline is that comparison only works if the metric was computed the same way across runs — same evaluation set, same seed policy. That's why the lab logs `final_accuracy` at step `epochs` on every run unconditionally: a missing value means the run is disqualified rather than assumed worst. This is the seam where Lab 03's `ModelRegistry.registerVersion` consumes tracking output: `runId`, `metrics`, `parameters` all come straight from the tracking store."

**Interviewer**: "The interview notes mention logging the environment from CI/CD. What does automated tagging look like concretely?"

**Candidate**: "In the pipeline, immediately after `createRun`, read the environment and push it as parameters and tags: git commit from `$GITHUB_SHA` or `git rev-parse HEAD`, dataset version from the feature store, environment name (dev/staging/prod), and the CI build ID. The lab's client pattern extends naturally — `logParam(runId, "git_commit", sha)` is the same call that logs `learning_rate`. The payoff: an auditor or a DS can take any run in the UI, click it, and see not just `lr=0.01` but *which code and which data* produced that number. That's the reproducibility answer to 'I can't figure out which run this metric came from'."

**Interviewer**: "Tie the lab to its LeetCode references: 588, 981, and 635."

**Candidate**: "Design In-Memory File System (588) models artifact storage — `logArtifact` is a hierarchical path under the run's artifact URI, so thinking about mkdir/get semantics keeps the artifact layer sane. Time-Based Key-Value Store (981) is exactly the metric API: `logMetric(runId, key, value, step)` stores values keyed by step, and the UI's line chart is a timestamp/key lookup — the same 'most recent value at or before time T' pattern as the feature store's point-in-time join. Design Log Storage System (635) covers the run ledger itself: append-only entries with timestamps, queryable by time range — which is the experiment history every DS searches when asking 'what did we try last quarter?'"

**Interviewer**: "What would you change if this tracking client had to record 1,000 concurrent training jobs?"

**Candidate**: "Three changes. First, batching: `logMetric` per epoch per run becomes a per-step bulk call — log all 20 epoch metrics in one POST — cutting connection churn by 20x. Second, async delivery: the client should enqueue and flush in the background so a slow tracking server never blocks training; the lab's synchronous `postJson` is fine at demo scale but makes training latency hostage to the tracking service. Third, retry with backoff on the HTTP layer, reusing Lab 01's retry semantics — a transient tracking-server blip shouldn't fail an eight-hour training run. And I'd add a local file fallback: buffer metrics to disk and replay them, so no experiment data is lost when the server is down."
