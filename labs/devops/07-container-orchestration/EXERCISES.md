# Container Orchestration Exercises

## Exercise 1: HPA Setup
Deploy an application with resource requests. Create an HPA targeting 70% CPU. Generate load and observe scaling.

## Exercise 2: Rolling Update
Perform a rolling update. Watch the rollout status. Roll back to the previous version.

## Exercise 3: Probes
Add startup, liveness, and readiness probes. Cause a liveness failure and observe restart.

## Exercise 4: Pod Disruption Budget
Create a PDB with minAvailable=2. Attempt to drain a node. Observe PDB prevents drain.

## Exercise 5: Resource Quotas
Create a namespace with ResourceQuota. Try to deploy pods that exceed the quota.

## Exercise 6: Canary Deployment
Deploy v1 (3 pods). Deploy v2 (1 pod) alongside. Use a service mesh or ingress weight for traffic splitting.

## Exercise 7: Blue/Green Deployment
Set up two services (blue, green). Switch traffic by updating the active service selector.

## Exercise 8: Cluster Autoscaler (if cloud cluster)
Configure cluster autoscaler. Create pending pods that require more nodes. Observe node scaling.
