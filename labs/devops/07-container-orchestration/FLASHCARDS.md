# Container Orchestration Flashcards

**Q: What is HPA?**
A: Horizontal Pod Autoscaler — automatically scales replicas based on metrics.

**Q: What is VPA?**
A: Vertical Pod Autoscaler — adjusts CPU/memory requests/limits automatically.

**Q: What is a liveness probe?**
A: Checks if container is alive; restarts on failure.

**Q: What is a readiness probe?**
A: Checks if pod is ready to serve traffic; removes from Service on failure.

**Q: What is a startup probe?**
A: Delays liveness/readiness checks during container startup.

**Q: What is a PodDisruptionBudget?**
A: Ensures minimum available pods during voluntary disruptions.

**Q: What determines Guaranteed QoS?**
A: All containers have limits = requests for both CPU and memory.

**Q: What is maxSurge?**
A: Maximum extra pods allowed during rolling update (absolute or percentage).

**Q: What is a canary deployment?**
A: Gradually shift traffic percentage to the new version.

**Q: What is cluster autoscaler?**
A: Automatically adds/removes nodes based on pending pod resource needs.
