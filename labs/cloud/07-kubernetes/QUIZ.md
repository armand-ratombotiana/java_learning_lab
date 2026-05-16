# Kubernetes - QUIZ

## Section 1: Core Concepts

**Q1: What is the smallest deployable unit in Kubernetes?**
- A) Node
- B) Pod
- C) Container
- D) Cluster

**Q2: What component assigns pods to nodes?**
- A) kubelet
- B) kube-proxy
- C) kube-scheduler
- D) kube-apiserver

**Q3: What is the purpose of etcd in Kubernetes?**
- A) Container runtime
- B) Network proxy
- C) Key-value store for cluster state
- D) API server

**Q4: What does a Deployment manage?**
- A) Nodes
- B) ReplicaSets
- C) Services
- D) ConfigMaps

**Q5: What is the default network driver for Docker?**
- A) host
- B) bridge
- C) overlay
- D) none

## Section 2: Networking

**Q6: Which Service type exposes pods externally?**
- A) ClusterIP
- B) NodePort
- C) LoadBalancer
- D) Both B and C

**Q7: What does a Service provide?**
- A) Persistent storage
- B) Stable network endpoint
- C) CPU resources
- D) Image registry

**Q8: How do pods discover services?**
- A) Through ingress
- B) Through DNS
- C) Through configmaps
- D) Through secrets

**Q9: What is the purpose of kube-proxy?**
- A) Container runtime
- B) Network proxy for service traffic
- C) Scheduling pods
- D) Storing configuration

**Q10: What does NetworkPolicy control?**
- A) CPU allocation
- B) Traffic flow between pods
- C) Image pulls
- D) Volume mounting

## Section 3: Configuration

**Q11: What is ConfigMap used for?**
- A) Storing sensitive data
- B) Non-sensitive configuration
- C) Persistent storage
- D) Service discovery

**Q12: What is the difference between ConfigMap and Secret?**
- A) ConfigMap is for pods, Secret is for nodes
- B) Secret is for sensitive data, base64 encoded
- C) ConfigMap is encrypted, Secret is not
- D) No difference

**Q13: How do you inject a Secret into a pod?**
- A) As environment variable
- B) As volume mount
- C) Both A and B
- D) Cannot inject Secrets

**Q14: What does `kubectl apply` do?**
- A) Creates resources only
- B) Updates or creates resources (declarative)
- C) Deletes resources
- D) Scales resources

**Q15: What is the purpose of labels?**
- A) Authentication
- B) Organize and select resources
- C) Store configuration
- D) Define network policies

## Section 4: Scaling & Updates

**Q16: What does HorizontalPodAutoscaler do?**
- A) Scales nodes
- B) Scales pods based on metrics
- C) Creates new clusters
- D) Updates images

**Q17: What is a rolling update strategy?**
- A) Replace all pods at once
- B) Gradually replace pods with new version
- C) Add new pods without removing old
- D) Scale down then scale up

**Q18: How do you rollback a deployment?**
- A) kubectl delete deployment
- B) kubectl rollout undo
- C) kubectl scale
- D) kubectl apply

**Q19: What is the default upgrade strategy for Deployment?**
- A) Recreate
- B) RollingUpdate
- C) Blue-green
- D) Canary

**Q20: What does `kubectl rollout status` show?**
- A) Resource usage
- B) Progress of rolling update
- C) Pod logs
- D) Node status

## Section 5: Storage

**Q21: What is PersistentVolume (PV)?**
- A) Temporary storage
- B) Cluster-wide storage resource
- C) Pod-specific volume
- D) Network configuration

**Q22: What is PersistentVolumeClaim (PVC)?**
- A) Cluster backup
- B) Request for storage by pods
- C) Volume driver
- D) Network policy

**Q23: What does ReadWriteOnce access mode mean?**
- A) Can be mounted by many pods
- B) Can be mounted by single node
- C) Can be mounted by single pod
- D) Read-only access

**Q24: What is a StorageClass used for?**
- A) Classifying pods
- B) Dynamic volume provisioning
- C) Service classification
- D) Node selection

**Q25: What happens to PV data when PVC is deleted?**
- A) Data is always deleted
- B) Data persists based on reclaim policy
- C) Data moves to another PVC
- D) PV is also deleted

## Section 6: Security & Best Practices

**Q26: What does SecurityContext do?**
- A) Manages network security
- B) Defines pod/container security settings
- C) Encrypts data at rest
- D) Manages TLS certificates

**Q27: Why should containers run as non-root?**
- A) Performance
- B) Security - limit container breakout impact
- C) Required by Kubernetes
- D) Easier debugging

**Q28: What does readOnlyRootFilesystem achieve?**
- A) Faster container startup
- B) Prevents writing to container filesystem
- C) Makes container read-only for users
- D) Reduces image size

**Q29: What is PodDisruptionBudget used for?**
- A) Limit resource usage
- B) Ensure minimum pods during disruptions
- C) Schedule maintenance
- D) Backup data

**Q30: What is the purpose of ResourceQuota?**
- A) Limit node resources
- B) Limit namespace resource consumption
- C) Schedule pods
- D) Manage storage

---

## Answers

1. B
2. C
3. C
4. B
5. B
6. D
7. B
8. B
9. B
10. B
11. B
12. B
13. C
14. B
15. B
16. B
17. B
18. B
19. B
20. B
21. B
22. B
23. C
24. B
25. B
26. B
27. B
28. B
29. B
30. B