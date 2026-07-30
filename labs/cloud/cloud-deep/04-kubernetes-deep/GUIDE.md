# GUIDE — Kubernetes Deep

## Step 1: Pod Lifecycle Model
```java
public enum PodPhase { PENDING, RUNNING, SUCCEEDED, FAILED, CRASH_LOOP_BACK_OFF, UNKNOWN }
public record Pod(String name, String namespace, PodPhase phase, String node, Map<String,String> labels) {}
```

## Step 2: Custom Scheduler
- Implement predicate filtering (resource fit, ports, node selector)
- Implement priority scoring (least requested, balanced, affinity)
- Bind pod to highest-scoring node

## Step 3: Controller/Operator Pattern
```java
public interface Controller {
    void reconcile(String key);
}
Operator operator = new Operator(crdClient, controller);
operator.start(); // watches CRD events and calls reconcile
```

## Step 4: Admission Webhook Simulator
- Mutating webhook: inject sidecar container, add labels
- Validating webhook: enforce pod security policies, resource limits

## Step 5: Network Policy Engine
- Evaluate ingress/egress rules against pod labels and namespaces
- Default deny, allow-all, and micro-segmentation rules

## Step 6: Exercises
1. Implement a ReplicaSet controller that maintains desired pod count
2. Build a validating webhook for required resource limits
3. Create a network policy simulator with allow/deny rules
