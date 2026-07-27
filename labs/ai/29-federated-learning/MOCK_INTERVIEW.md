# Mock Interview: Federated Learning

**Topic:** Design a federated learning system for healthcare

## Core Questions

### Q1: What is federated learning and why for healthcare?

**Answer:**
Federated learning trains a shared model across decentralized data without raw data leaving clients.

**Key motivation for healthcare:**
- **Privacy regulations:** HIPAA, GDPR prevent sharing patient data
- **Data silos:** Hospitals, clinics cannot pool data centrally
- **Data heterogeneity:** Different patient populations, equipment, protocols
- **Rare diseases:** Aggregated learning from many sites improves rare disease models

### Q2: Design a healthcare federated learning system.

**Answer:**
```
Architecture:

Central Server
  ├── Model aggregation (FedAvg)
  ├── Model versioning & distribution
  ├── Encryption key management
  ├── Monitoring & compliance logging
  └── Differential privacy budget tracking

Hospital Nodes (clients)
  ├── Local EHR database
  ├── Secure enclave for training
  ├── DP mechanism (per training step)
  ├── Model update (gradients) → encrypted → server
  └── Audit trail

Communication
  ├── TLS 1.3 for transport
  ├── Homomorphic encryption (optional)
  ├── Secure aggregation (SecAgg protocol)
  └── Asynchronous rounds (handles stragglers)
```

### Q3: What is FedAvg and its variations?

**Answer:**
**FedAvg (Federated Averaging):**
```
Server:
  Initialize w_0
  For each round t:
    Sample subset S_t of clients
    Broadcast w_t to all selected clients
    For each client k in parallel:
      w_{t+1}^k = LocalTrain(w_t, D_k)  # SGD on local data
    Aggregate: w_{t+1} = Σ (n_k / N) · w_{t+1}^k
```

**Variations:**
| Method | Key Idea | Use Case |
|--------|----------|----------|
| **FedAvg** | Weighted average of client updates | Baseline |
| **FedProx** | Add proximal term $\|w - w_t\|^2$ to loss | Heterogeneous data |
| **SCAFFOLD** | Control variates to correct client drift | High heterogeneity |
| **FedNova** | Normalize updates by local steps | Unbalanced computation |
| **FedMA** | Match and average neurons (matching layer) | Non-IID extreme |
| **q-FedAvg** | Fairness-aware, weighted by loss | Health equity |

### Q4: How do you handle data heterogeneity?

**Answer:**
**Types of heterogeneity:**
- **Feature distribution shift:** Different patient demographics
- **Label distribution shift:** Different disease prevalence
- **Concept drift:** Different ICD coding practices

**Strategies:**
- **Personalized FL:** Each hospital gets a personalized model (multi-task learning, meta-learning)
- **Clustered FL:** Group hospitals by data similarity, train cluster-specific models
- **Adaptive aggregation:** Weight clients by data quality, not just size
- **Domain adaptation:** Align feature representations across sites via adversarial training
- **Regularization:** FedProx limits local model divergence from global model

### Q5: Privacy considerations beyond FL.

**Answer:**
FL alone is not sufficient — gradients can leak information (gradient inversion attacks reconstruct images from gradients).

**Additional protections:**

1. **Differential Privacy (DP):** Add calibrated noise to gradients (or weights)
   - $\epsilon$ budget tracking across rounds
   - Typically $\epsilon = 1-10$ for healthcare
   - Trade-off: more noise = less accuracy

2. **Secure Aggregation (SecAgg):** Encrypt individual updates so server only sees aggregate
   - Uses secret sharing or threshold encryption
   - Server cannot attribute updates to specific clients

3. **Homomorphic Encryption:** Server computes on encrypted data (heavy, rarely practical)

4. **Trusted Execution Environments (TEE):** Train inside SGX enclave

5. **Differential Privacy + Secure Aggregation:** Combined best practice

### Q6: How do you evaluate and monitor?

**Answer:**
- **Global model metrics:** Accuracy, AUC, F1 on held-out centralized test set (if available)
- **Per-site metrics:** Monitor each hospital's performance separately
- **Fairness metrics:** Ensure model doesn't perform worse on minority populations
- **Drift detection:** Monitor per-round performance changes
- **Communication efficiency:** Bytes sent, rounds to convergence
- **Privacy budget:** Track $\epsilon$ consumption per hospital

## Advanced

- **Client selection:** Stratified sampling ensures diverse representation each round
- **Compression:** Gradient quantization, sparsification, or sketching to reduce communication
- **Asynchronous FL:** Handle stragglers without blocking global aggregation
- **Verifiable FL:** Use zero-knowledge proofs to verify client training was performed correctly
- **Cross-silo vs. cross-device:** Healthcare is cross-silo (few clients, reliable connection, large data)
