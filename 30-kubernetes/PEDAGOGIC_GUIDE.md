# Kubernetes Pedagogic Guide

## Teaching Strategy

### Phase 1: Fundamentals (Hours 1-2)
**Goal**: Understand K8s concepts

**Topics**:
- What is Kubernetes?
- Pods, Deployments, Services
- kubectl basics
- Architecture overview

**Activities**:
1. Create first pod
2. Deploy application
3. Expose service

**Exercises**:
- Exercise 1, 2

**Assessment**: Application runs in K8s

---

### Phase 2: Configuration (Hours 3-4)
**Goal**: Master K8s resources

**Topics**:
- ConfigMaps and Secrets
- Resource management
- Health checks
- Volume mounting

**Activities**:
1. Create ConfigMap and Secret
2. Add health checks
3. Configure resources

**Exercises**:
- Exercise 3, 6

**Assessment**: Resources configured

---

### Phase 3: Networking (Hours 5-6)
**Goal**: Understand K8s networking

**Topics**:
- Service types
- Ingress
- DNS
- Network policies

**Activities**:
1. Create different service types
2. Configure Ingress
3. Set up network policies

**Exercises**:
- Exercise 2, 5

**Assessment**: Services accessible

---

### Phase 4: Deployment Strategies (Hours 7-8)
**Goal**: Manage deployments

**Topics**:
- Rolling updates
- Rollbacks
- HPA
- PDB

**Activities**:
1. Configure rolling update
2. Set up HPA
3. Test rollback

**Exercises**:
- Exercise 4, 7, 9

**Assessment**: Deployments work correctly

---

### Phase 5: Advanced (Hours 9-10)
**Goal**: Use advanced patterns

**Topics**:
- Helm charts
- Security
- Multi-cluster

**Activities**:
1. Create Helm chart
2. Add security context
3. Explore operators

**Exercises**:
- Exercise 8, 10

**Assessment**: Complete setup works

---

## Teaching Techniques

### Code Review Questions
1. What are the key components here?
2. How would you scale this application?
3. What security is missing?
4. How would you debug this issue?

### Common Mistakes
| Mistake | Solution |
|---------|----------|
| No resource limits | Set requests and limits |
| No health checks | Add liveness and readiness |
| Using latest tag | Pin specific versions |
| No RBAC | Create proper roles |
| No monitoring | Set up metrics |

### Real-World Examples

**Example 1: Microservices Stack**
- Multiple services
- Ingress for routing
- ConfigMaps for configuration
- HPA for scaling

**Example 2: Production Setup**
- Security contexts
- Network policies
- Resource quotas
- Pod disruption budgets
```

**Example 3: CI/CD Integration**
- Deploy from pipeline
- Helm charts for packaging
- Image scanning
- Progressive delivery
```

---

## Hands-On Projects

### Project 1: Complete Application (5 hours)
Deploy complete application:
- Deployment with health checks
- Service configuration
- ConfigMap and Secrets
- Resource limits

**Requirements**:
- Application runs and is accessible
- Health checks work
- Resources limited

---

### Project 2: Production Setup (6 hours)
Production-ready deployment:
- Ingress with TLS
- HPA for scaling
- PDB for updates
- Security contexts

**Requirements**:
- HTTPS enabled
- Auto-scaling works
- Zero-downtime updates

---

### Project 3: Helm Packaging (5 hours)
Create Helm chart:
- Chart structure
- Templates
- Values configuration
- CI/CD integration

**Requirements**:
- Chart works
- Can be deployed via Helm
- CI/CD can deploy

---

## Evaluation Criteria

### Configuration (40%)
- Proper resource definitions
- Correct service types
- Health checks configured

### Deployment (30%)
- Rolling updates work
- Rollbacks function
- Scaling works

### Best Practices (30%)
- Security contexts
- Resource limits
- RBAC configured

---

## Resources

### Official Docs
- [Kubernetes Docs](https://kubernetes.io/docs/)
- [kubectl Cheat Sheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)
- [Helm Docs](https://helm.sh/docs/)

### Tools
- kubectl
- minikube / kind
- Helm
- k9s