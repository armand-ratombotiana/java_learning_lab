# Lab 05: Interview Questions

## FAANG-Level Questions

### Q1: Design a model serving system that handles 100K QPS with P99 latency < 50ms.
**Answer**: Use a horizontally scalable microservice architecture. Deploy model servers behind a load balancer (NLB/ALB). Each server runs a lightweight Java/Spring Boot app with in-process model loading. Use Redis for feature caching. Implement request batching for GPU utilization. Auto-scale based on CPU/request queue depth using K8s HPA.

### Q2: Compare model serving frameworks: TorchServe, Triton, BentoML, Seldon.
**Answer**: TorchServe is PyTorch-native with model versioning APIs. Triton Inference Server supports multiple frameworks (TensorRT, ONNX, PyTorch) with GPU optimizations and dynamic batching. BentoML focuses on Python-first deployment with Docker/MLflow integration. Seldon Core is a K8s-native ML deployment operator with canary/blue-green support.

### Q3: How do you handle model warm-up and cold starts?
**Answer**: Implement pre-loading of models on startup (not lazy load). Use Kubernetes preStop hooks for graceful shutdown. For serverless, use provisioned concurrency (AWS Lambda) or keep-warm requests. For Java, ensure JIT compilation is warmed up with a traffic ramp-up period.

### Q4: What security considerations are important for model serving?
**Answer**: Input validation and sanitization, rate limiting (per-user/IP), authentication/authorization (OAuth2, API keys), HTTPS/TLS termination, model access control (only authorized models served), monitoring for adversarial inputs, and container image scanning.

## LeetCode / NeetCode References
- **Design HTTP Server** — Web server patterns, request handling
- **Design Rate Limiter (LeetCode 359)** — Throttling prediction requests
- **Design Web Crawler** — Concurrent request handling patterns
