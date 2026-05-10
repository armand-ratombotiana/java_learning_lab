# Kubernetes Quizzes

## Quiz 1: Basic Concepts

**Question 1**: What is the smallest deployable unit in Kubernetes?
- A) Container
- B) Pod
- C) Deployment
- D) Service

**Answer**: B

---

**Question 2**: Which controller manages pods?
- A) ReplicaSet
- B) Service
- C) ConfigMap
- D) Namespace

**Answer**: A

---

**Question 3**: What does a Service provide?
- A) Storage
- B) Network endpoint
- C) Authentication
- D) Authorization

**Answer**: B

---

**Question 4**: Which is NOT a valid Service type?
- A) ClusterIP
- B) NodePort
- C) Internal
- D) LoadBalancer

**Answer**: C

---

**Question 5**: What is the default namespace?
- A) default
- B) system
- C) kube-system
- D) None

**Answer**: A

---

## Quiz 2: Deployments

**Question 1**: What is the purpose of replicas in a Deployment?
- A) Manage storage
- B) Ensure high availability
- C) Configure networking
- D) Set security

**Answer**: B

---

**Question 2**: What does maxSurge: 1 mean?
- A) One pod can be unavailable
- B) One extra pod above desired
- C) One pod updated at a time
- D) None of the above

**Answer**: B

---

**Question 3**: How do you rollback a deployment?
- A) kubectl undo deployment
- B) kubectl rollout undo deployment
- C) kubectl revert deployment
- D) kubectl back deployment

**Answer**: B

---

**Question 4**: What is the purpose of selector in a Deployment?
- A) Choose nodes
- B) Select which pods to manage
- C) Filter logs
- D) Network selection

**Answer**: B

---

**Question 5**: Which command shows deployment history?
- A) kubectl history deployment
- B) kubectl rollout history deployment
- C) kubectl show deployment
- D) kubectl deployment history

**Answer**: B

---

## Quiz 3: Configuration

**Question 1**: ConfigMap stores what type of data?
- A) Encrypted data
- B) Non-sensitive configuration
- C) Secrets
- D) Certificates

**Answer**: B

---

**Question 2**: Secret is used for what?
- A) Large files
- B) Sensitive data
- C) Images
- D) Volumes

**Answer**: B

---

**Question 3**: How do you mount a ConfigMap as a volume?
- A) volumeMount with configMapRef
- B) volume with configMap
- C) Both A and B
- D) None

**Answer**: C

---

**Question 4**: What does envFrom do?
- A) Sets single env variable
- B) Imports all vars from ConfigMap/Secret
- C) Exports env variables
- D) None

**Answer**: B

---

**Question 5**: Which is NOT a way to use ConfigMap?
- A) Environment variable
- B) Volume mount
- C) Command argument
- D) All are valid

**Answer**: C

---

## Quiz 4: Health Checks

**Question 1**: What does liveness probe check?
- A) Container is started
- B) Container can handle requests
- C) Application is ready
- D) None

**Answer**: A

---

**Question 2**: What does readiness probe check?
- A) Container is running
- B) Can receive traffic
- C) Health endpoint exists
- D) All of above

**Answer**: B

---

**Question 3**: Which is NOT a valid probe type?
- A) httpGet
- B) tcpSocket
- C) exec
- D) grpc

**Answer**: D (gRPC is valid in newer versions)

---

**Question 4**: What is initialDelaySeconds?
- A) Time before first check
- B) Time between checks
- C) Check timeout
- D) Number of failures

**Answer**: A

---

**Question 5**: What happens when liveness probe fails?
- A) Pod is restarted
- B) Pod is removed
- C) Service stops sending traffic
- D) Nothing

**Answer**: A

---

## Quiz 5: Resources

**Question 1**: What does requests.memory define?
- A) Maximum memory
- B) Guaranteed memory
- C) Initial memory
- D) None

**Answer**: B

---

**Question 2**: What does limits.memory define?
- A) Guaranteed memory
- B) Maximum memory
- C) Initial memory
- D) None

**Answer**: B

---

**Question 3**: ResourceQuota applies to?
- A) Individual pods
- B) Namespace
- C) Cluster
- D) Node

**Answer**: B

---

**Question 4**: LimitRange applies to?
- A) Cluster
- B) Namespace
- C) Node
- D) Pod

**Answer**: B

---

**Question 5**: What triggers pod eviction?
- A) Using less than requests
- B) Exceeding limits
- C) Node failure
- D) All of above

**Answer**: D

---

## Answer Key

| Quiz | Q1 | Q2 | Q3 | Q4 | Q5 |
|------|-----|-----|-----|-----|-----|
| 1    | B  | A  | B  | C  | A  |
| 2    | B  | B  | B  | B  | B  |
| 3    | B  | B  | C  | B  | C  |
| 4    | A  | B  | D  | A  | A  |
| 5    | B  | B  | B  | B  | D  |