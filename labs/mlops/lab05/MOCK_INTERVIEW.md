# Lab 05: Mock Interview — Model Serving with Docker

**Role**: MLOps Engineer / ML Infrastructure Engineer
**Duration**: 60 minutes
**Focus**: REST model serving, health checks, Docker multi-stage builds, serving frameworks, scaling, security, cold starts

---

**Interviewer**: "Walk me through the model server in this lab. What is it, and how does it serve predictions?"

**Candidate**: "It's a zero-dependency Java REST service built on `com.sun.net.httpserver.HttpServer` — no Spring, no servlet container. The `Model` class holds a weight vector and bias — the demo loads `{0.5, -0.2, 0.8, 0.1}` with bias `0.3` — and `predict(double[] features)` computes the dot product, throwing `IllegalArgumentException` if the feature count mismatches. The server binds three contexts: `POST /predict`, `GET /healthz`, and `GET /readyz`. `/predict` reads the request body, runs it through `parseFeatures` — a deliberately simplistic JSON parser that extracts the `[...]` array — calls `model.predict`, and returns `{"prediction": ...}` via `sendResponse`, which sets `Content-Type: application/json`. Everything funnels through the lab's shared `sendResponse` helper so the status code and body contract is uniform."

**Interviewer**: "Why does `/predict` reject GET with 405 while health checks use GET?"

**Candidate**: "Because prediction is a state-changing, side-effecting operation over the wire: it costs compute, it should be cache-safe by design, and it carries a body — all the reasons HTTP reserves POST for non-idempotent operations. The handler checks `!"POST".equals(exchange.getRequestMethod())` and returns `405 {"error":"Method not allowed"}`. Health checks, by contrast, are pure reads that load balancers and Kubernetes probes issue as GET — that's the universal convention. This matches how Triton and TorchServe structure their endpoints: liveness/readiness as GET, inference as POST."

**Interviewer**: "The lab serves `/healthz` and `/readyz` as separate endpoints. What's the difference, and how do orchestrators use them?"

**Candidate**: "Liveness answers 'is the process alive?' — if `/healthz` fails, the orchestrator restarts the container. Readiness answers 'is this instance ready to receive traffic?' — if `/readyz` fails, the orchestrator stops routing requests to it but doesn't kill it. The separation matters for ML because model loading is slow: a pod can be alive (JVM booted) but not ready (model still loading into memory). The lab's Kubernetes manifests wire these up precisely: the Deployment in Lab 06 uses a `livenessProbe` on `/healthz` with `initialDelaySeconds: 10` and a `readinessProbe` on `/readyz` with `initialDelaySeconds: 5` — so K8s starts checking readiness before it decides to kill anything."

**Interviewer**: "The model is loaded inline at startup: `new Model(new double[]{...}, 0.3)` in `main`. What's wrong with that for production, per the lab's cold-start discussion?"

**Candidate**: "Nothing for a demo — the weights live in the class — but production models load from disk or object storage, and lazy loading is the classic mistake: the first request pays a multi-second load, the readiness probe fails, or worse, concurrent first requests all trigger the same load. The interview notes give the production playbook: pre-load the model at startup — this server already does that, which is why `/readyz` can honestly report 'ready' only after `model` is constructed; warm up the JIT with a ramp period so the first real traffic doesn't pay compilation cost; and use Kubernetes `preStop` hooks for graceful shutdown. For serverless, provisioned concurrency or keep-warm requests replace the startup penalty."

**Interviewer**: "How would you scale this to 100K QPS with P99 < 50ms, per the lab's Q1?"

**Candidate**: "Horizontal scaling, not bigger servers: deploy the container behind an ALB/NLB with a Kubernetes HPA scaling on CPU and request-queue depth. Each replica is stateless — the model lives in the process — so the pool is trivially elastic. Then attack latency: batch requests on the serving layer to keep GPU utilization high if there's a GPU, add a Redis feature cache in front of feature retrieval (connecting to Lab 04's online store), and keep the hot path allocation-light. The lab's `Executors.newFixedThreadPool(4)` already shows the right instinct — bounded concurrency so a slow client can't exhaust the JVM — production would just tune the pool to the instance's cores and let HPA handle the rest."

**Interviewer**: "TorchServe vs Triton vs BentoML vs Seldon — when do you reach for each, given you have a working Java server?"

**Candidate**: "The lab's Q2 framing: TorchServe is PyTorch-native with versioning APIs — right when the model zoo is PyTorch and you want its ecosystem. Triton wins when you have mixed frameworks — TensorRT, ONNX, PyTorch — because it's the multi-framework engine with dynamic batching and GPU optimizations; it's the throughput king. BentoML is Python-first with Docker/MLflow integration — fast path from notebook to container. Seldon Core is Kubernetes-native ML deployment with canary and blue-green as first-class features — the choice when the platform is K8s and rollout strategy matters more than raw throughput. My Java server is closest in spirit to a hand-rolled Triton-lite: single-model, in-process, simple."

**Interviewer**: "Walk me through the Dockerfile anatomy — why multi-stage, and what does each stage do?"

**Candidate**: "Two stages per the guide. The builder stage starts from `eclipse-temurin:21-jdk` — it needs the compiler, so it's fat: it copies `src/com/mlops/lab05/*.java` and runs `javac`. The runtime stage starts from `eclipse-temurin:21-jre` — no compiler, just the JVM — and copies the compiled `.class` files from the builder via `COPY --from=builder /app .`. The win: the shipped image contains a JRE, not a JDK, cutting the image by hundreds of megabytes and shrinking the attack surface — there's no `javac` in the container for an attacker to abuse. The `EXPOSE 8080` is documentation; the real contract is the `CMD ["java", "com.mlops.lab05.ModelServingLab"]`, and the server honors the `PORT` env var so the container can be run anywhere."

**Interviewer**: "What security considerations apply to a model-serving endpoint? The lab's Q4."

**Candidate**: "Five layers. Input validation and sanitization — the lab's `parseFeatures` is a teachable moment: it's brittle, and production needs a real schema check (Lab 09's validator) so a malicious body can't reach the model or crash the parser. Rate limiting per user/IP — the classic LeetCode 359 problem, implemented as a sliding window so a scraper can't burn your GPU. Authentication — OAuth2 or API keys at the edge, TLS termination at the ingress. Model access control — the server should only load artifacts from the registry's approved paths (Lab 03). And container hygiene — scan images with Trivy/Snyk, run as a non-root user, and keep the JRE-only runtime so there's less to exploit."

**Interviewer**: "The lab returns `400 {"error":"Expected 4 features, got 2"}` for wrong-length input. Why is a precise error message a security and debuggability feature?"

**Candidate**: "Because the contract failure is the model's contract: the feature count is part of the API schema. Returning the exact expected count turns a silent wrong-answer bug into an immediate, actionable error — the caller fixes the payload instead of shipping garbage predictions. From a debugging view, it's the difference between 'request failed' and 'you sent 2 features, this model needs 4'. The lab logs `Prediction error: ...` at `WARNING` via `java.util.logging` before responding, so the error has a server-side trail too. And from a security view, bounded, explicit failures avoid the alternative — a parser crash, an OOM, or a stack trace leaking internals."

**Interviewer**: "The lab's JSON handling — `toJson`, `parseFeatures` with regex — is a simplification. What's the production-grade replacement, and why does it matter at 100K QPS?"

**Candidate**: "Replace regex parsing with a streaming or schema-bound parser — Jackson or Gson with a DTO — because `parseFeatures`'s `replaceAll(".*\\[", ...)` breaks on whitespace, nested arrays, or extra fields, and it double-parses. At high QPS, allocations matter: a Jackson `byte[]` pass with a pre-allocated buffer beats string-building per request. The deeper point is contract: a DTO plus a schema validator enforces the `features` array's length and range before the model sees it, which is the serving-side version of Lab 09's `expectColumnValuesBetween`. The lab's `sendResponse`/`toJson` shape stays — you're just replacing the parser internals, keeping the endpoint contract stable."

**Interviewer**: "How does this server prove it's healthy in Kubernetes? What do the probes need to be careful about?"

**Candidate**: "The probes must not be able to kill a healthy service or keep an unhealthy one alive. Liveness on `/healthz` is cheap and always-true while the JVM runs — the lab's handler returns `{"status":"ok"}` unconditionally. Readiness on `/readyz` returns `{"status":"ready"}` — and production readiness should incorporate the model's loaded state: if the model failed to load, readiness should fail so traffic routes away while the pod is restarted. The `initialDelaySeconds` values in Lab 06's manifest (10s liveness, 5s readiness) exist because the JVM takes seconds to boot — a probe that starts too early fails spuriously. And with `periodSeconds` of 10-15, probes are cheap HTTP GETs, exactly the pattern this server implements."

**Interviewer**: "The lab's guide mentions `-Xmx`/`-Xms` JVM flags in the container. What does the container need beyond `java -jar`?"

**Candidate**: "Memory limits must match the JVM heap config, or you get OOM-killed JVMs: if the container limit is 1Gi and the JVM grows to 2Gi heap, cgroup kills the process — the classic 'K8s killed my healthy app' incident. The pattern: `-Xms` sets the starting heap, `-Xmx` caps it, and you leave headroom for metaspace, thread stacks, and JIT. The K8s manifest's resource block (requests 256m/512Mi, limits 1/1Gi) is the other half: requests guarantee scheduling, limits bound the blast radius. And in Java 21+, don't forget the option to let the JVM use container awareness (`-XX:MaxRAMPercentage`), so heap sizing follows the cgroup limit automatically."

**Interviewer**: "Tie the lab to its LeetCode references — rate limiter, crawler, HTTP server design."

**Candidate**: "Design Rate Limiter (359) is the serving-layer problem: a sliding-window counter per API key on `/predict`, which is what protects the endpoint before you pay for compute. Design Web Crawler (1242) covers the concurrency patterns — the lab's thread pool with bounded workers is the same shape as a polite crawler that limits parallelism per host. And HTTP server design in general: routing, status codes, body handling, keep-alive — which is literally what `com.sun.net.httpserver` forces you to think about, since there's no framework sugar. The through-line: serving is systems engineering, and the lab makes every byte visible."

**Interviewer**: "What's the failure mode you'd most expect in production with this server, and how would you catch it in testing?"

**Candidate**: "The contract mismatch: a client sends `{"features": [...]}` where the count or scale differs from training, and the server returns a plausible-but-wrong prediction. The lab catches the count error via the model's guard, but scale errors sail through — a feature logged as dollars instead of cents. The fix is a validation gate (Lab 09) between parsing and predicting, plus contract tests that replay real payloads. The second most likely failure: process exits when the pool is saturated — `newFixedThreadPool(4)` queues unboundedly, so a traffic spike becomes a memory spike. Both are caught by load testing the container in Docker Compose before it ever reaches Kubernetes — which is exactly the lab's progression from Lab 05 (container) to Lab 06 (orchestrator)."
