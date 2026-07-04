# Mental Models for Kubernetes

## 1. Ship Fleet Analogy
- **Cluster** = Shipping port
- **Node** = Cargo ship
- **Pod** = Shipping container
- **Deployment** = Shipping manifest (how many containers of each type)
- **Service** = Lighthouse (stable address to find containers)
- **Scheduler** = Harbor master (decides which ship gets which container)

## 2. Desired State Reconciliation
Kubernetes is a control loop: you specify desired state (3 replicas, image v2), controllers continuously work to make actual state match. Like a thermostat — set temperature to 72°F, system runs until it reaches 72.

## 3. API Server as Brain
Everything goes through the API server. It's the central nervous system — no component talks directly to another. This provides audit logging, authorization, and a consistent communication pattern.

## 4. Cattle vs Pets
Treat nodes and pods as cattle, not pets. If one fails, replace it. Never SSH into a node to fix it — delete and recreate.
