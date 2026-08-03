# Lab 06: Mock Interview — Kubernetes for ML

**Role**: Platform Engineer / MLOps Engineer
**Duration**: 60 minutes
**Focus**: Deployments, Services, HPA, rolling updates, probes, ConfigMaps/Secrets, canary/blue-green, GPU scheduling, multi-tenancy

---

**Interviewer**: "Walk me through what the lab's Java program actually produces — it's not a server, so what does it do?"

**Candidate**: "It's a manifest generator: instead of hand-editing YAML, the Java code renders Kubernetes manifests from typed methods — `generateDeployment`, `generateService`, `generateHPA`, `generateConfigMap` — and writes them to `k8s-manifests/` for `kubectl apply`. The deployment for the Lab 05 model server: 3 replicas with a `RollingUpdate` strategy, resource requests of 256m CPU / 512Mi memory and limits of 1 CPU / 1Gi, plus `livenessProbe` on `/healthz` (initialDelay 10s, period 15s) and `readinessProbe` on `/readyz` (initialDelay 5s, period 10s). The service exposes port 80 → container port 8080 as `ClusterIP`, and the HPA scales 2-10 replicas on 60% CPU utilization. The point: manifests become reviewable, testable artifacts generated from code, not copy-pasted text."

**Interviewer**: "Why `maxSurge: 1` and `maxUnavailable: 0` in the rolling update strategy? What behavior does that buy you?"

**Candidate**: "It's the zero-downtime, capacity-safe update contract. `maxUnavailable: 0` means the Deployment never removes an old pod before a new one is ready — at no point are you below desired capacity. `maxSurge: 1` means it's allowed to spin up one extra pod above the desired count during the transition, which gives the new version a warm slot before the first old pod is drained. For a model server this matters more than for a web app: replacing a pod means loading the model into memory — which can take tens of seconds — and if you allowed maxUnavailable 1, you'd serve 2/3 traffic while the third is loading. The RollingUpdate strategy is the default that trades simplicity for safety; the interview notes add blue-green and canary for when you need instant rollback or metric-gated rollout."

**Interviewer**: "Liveness vs readiness again, in the K8s context — how do the probes in this manifest use the Lab 05 endpoints?"

**Candidate**: "Liveness pings `/healthz` — if the process is alive the JVM answers `{"status":"ok"}`, and if liveness fails repeatedly K8s restarts the container. Readiness pings `/readyz` — and here the K8s design intent is that readiness should fail *before* the pod receives traffic: the 5s initial delay acknowledges JVM+model load time, and once ready the Service routes to it. The important interplay for ML: a pod that is alive but not ready sits harmless; a pod that is ready but unhealthy serves bad predictions. That's why the probe paths matter and why the lab's server deliberately implements both endpoints — an orchestrator can't distinguish 'starting up' from 'crashed' without them."

**Interviewer**: "Resource requests vs limits — the manifest has both, at 256m/512Mi and 1/1Gi. What happens if you omit one side?"

**Candidate**: "Requests are the scheduling promise: the scheduler only places the pod on a node that can reserve 256m CPU and 512Mi memory, and this pod's share in the node's usage accounting. Limits are the enforcement ceiling: CPU is throttled above the limit, memory above the limit means OOM-kill. If you set requests without limits, a memory leak grows unchecked until the node dies. If you set limits without requests, the scheduler may over-commit the node and the pod gets throttled unfairly. For ML, the guide's warning is specific: memory limits below the JVM's heap growth — the classic `-Xmx` mismatch — produce mysterious OOMKilled restarts. The manifest's 1Gi limit is a backstop; the JVM flags from Lab 05 (`-Xmx`) keep the heap inside it."

**Interviewer**: "The ConfigMap holds MODEL_THRESHOLD, BATCH_SIZE, CACHE_TTL_SECONDS, LOG_LEVEL. Why ConfigMap and not environment variables baked into the image?"

**Candidate**: "Separation of config from artifact. The image is immutable — same binary everywhere — and the ConfigMap overlays environment-specific settings at deploy time, so you can change the fraud threshold from 0.85 to 0.88 by editing one manifest and rolling, without rebuilding the image. The guide's table pairs ConfigMap with feature flags and model config, and Secret with API keys and model encryption keys — same mechanism, but Secrets are base64-encoded and RBAC-scoped. The discipline: config that differs between dev/staging/prod belongs in ConfigMaps/Secrets; config that is constant belongs in the image. Mixing them means either rebuilds for trivial changes or secrets drifting into image layers."

**Interviewer**: "Design a serving platform for 500+ models across teams — the lab's Q1."

**Candidate**: "Multi-tenant by construction: a namespace per team with ResourceQuota and LimitRange, so one team's training spike can't starve another's serving. Then the serving pattern: each model gets Deployment + Service + HPA, and a model router — Seldon or a custom router — dispatches `/predict` requests to the right model pod by model name and version from the path or header, resolving the champion through the registry (Lab 03). Istio on top gives canary traffic splitting, mTLS between services, and unified observability. Admission controllers enforce the platform contracts — model validation, resource quotas — so teams can't deploy a manifest that violates policy. The lab's manifest set is the unit of that architecture; the platform is the machinery around it."

**Interviewer**: "Canary vs blue-green vs rolling for ML models — the lab's Q3."

**Candidate**: "Rolling is simplest but mixes two versions during transition — fine when you trust the new version. Blue-green keeps both environments and flips the service selector — instant rollback, but you pay double capacity, and for ML you double the GPU bill. Canary routes X% of traffic to the new version — the interview notes call it the ML-preferred option because you can compare *model metrics* between versions on live traffic before committing: drift, latency, and business metrics from Lab 08, not just the offline accuracy from the registry. Istio virtual services do the splitting; a metric gate promotes or rolls back. The lab's HPA manifest is the shared substrate — all three strategies run on the same Deployment + Service plumbing."

**Interviewer**: "The HPA is `autoscaling/v2` targeting CPU at 60%. Why might CPU be the wrong scaling metric for model serving, and what's the alternative?"

**Candidate**: "CPU utilization tracks compute, but model serving's scarce resource is often memory (model size), inference queue depth, or end-to-end latency — CPU can look idle while requests pile up in the thread pool, especially with GPU inference where the bottleneck is elsewhere. `autoscaling/v2` supports custom and external metrics, and the production move is to scale on request queue depth or per-request latency — the interview notes explicitly say 'auto-scale based on CPU/request queue depth'. For GPUs, CPU-based HPA is badly wrong: you'd want GPU utilization. The lab's 60% CPU target is the teaching baseline; production replaces it with the metric that actually predicts saturation."

**Interviewer**: "A model pod is CrashLoopBackOff. Walk me through the debugging sequence — the lab's Q4."

**Candidate**: "Systematically: `kubectl logs <pod>` first — the JVM prints the stack trace. If logs are empty or truncated, `kubectl describe pod` shows events: image pull failures, probe failures, OOMKilled. If it's OOMKilled, the memory limit is below the JVM's needs — raise the limit or fix `-Xmx`. If it's the probe failing, the model loads after the readiness/liveness initial delay — the classic fix is raising `initialDelaySeconds`, which this manifest sets at 5/10. If the model file is missing, check the volume mount and ConfigMap. Then `kubectl exec` into the pod and test the loading path manually. The interview notes list exactly these four: OOM, model file not found, missing dependency — and the discipline: change one thing at a time, because CrashLoopBackOff restarts with backoff are slow."

**Interviewer**: "The guide recommends pod disruption budgets for critical serving. What do they protect against?"

**Candidate**: "PDBs cap how many pods can be voluntarily evicted at once — by node drains, autoscaler scale-down, or cluster maintenance. Without a PDB, a node drain during a rolling upgrade can evict every replica of the fraud model simultaneously, and the Service has zero ready endpoints — an outage caused by maintenance, not by the model. For a 3-replica deployment, `minAvailable: 2` says 'never let voluntary disruption take more than one pod'. The ML-specific twist: model pods are slow to re-ready (model load), so an evicted pod takes minutes to return to the Service — PDBs prevent compounding that with parallel evictions. It's the K8s-native version of Lab 08's availability thinking."

**Interviewer**: "How do you schedule GPU models in this cluster — the lab's Q2?"

**Candidate**: "GPUs are special-cased resources: the GPU nodes get a taint — `nvidia.com/gpu` — and GPU workloads get the matching toleration plus a node affinity, so only GPU-bound pods land there and CPU workloads never squat on GPU memory. The request/limit pattern applies: `nvidia.com/gpu: 1` as the request. The interview notes add the orchestration side: gang scheduling for distributed training — Volcano or Kubeflow — so an 8-worker training job doesn't start 7 pods and wait (or fail) for the 8th; and LimitRanges per namespace to stop one team from grabbing all GPUs. Serving GPUs and training GPUs usually want separate node pools too — a long training job shouldn't be preempted by serving autoscaling."

**Interviewer**: "Tie the lab to its LeetCode references — load balancer, autoscaler, distributed cache."

**Candidate**: "Design a Load Balancer is the Service/Ingress layer: stable virtual IP over a changing pod set — the Service's selector and endpoints are the LB's backend table. Design Autoscaler is the HPA controller: observe the metric (CPU), compare to target (60%), compute `ceil(current/desired) × current replicas`, bounded by min/max (2-10) — and with the lab's values you can work through the arithmetic by hand. Design Distributed Cache maps to multi-replica model serving: the registry's production model pointer (Lab 03) plus per-pod model copies is a replicated cache with a versioned key. The through-line: K8s primitives are distributed-systems problems with opinionated solutions."

**Interviewer**: "What's the most common way a manifest like this fails in production, despite everything looking right?"

**Candidate**: "The probe/timing mismatch — the model takes 40 seconds to load, the liveness initial delay is 10, the pod gets killed on a loop: alive-and-healthy process, dead because the orchestrator gave up too early. It's silent and looks like 'unstable model loading'. The fix is making readiness honest (Lab 05's `/readyz` reflecting model-loaded state) and sizing `initialDelaySeconds` from a measured load time, not a guess. The second classic: the HPA and the resource limits disagree — CPU limit 1 core, HPA target 60% — so the HPA scales on a fraction of the limit and over-provisions, or the pod throttles before the HPA reacts. Both failures are config-synchronization problems — which is exactly why generating all the manifests from one codebase, as this lab does, keeps the contract consistent."
