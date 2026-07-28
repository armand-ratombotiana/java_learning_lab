# Lab 14: Interview Questions

## FAANG-Level Questions

### Q1: Design an AutoML system that can handle 10,000+ trials per day.
**Answer**: Use a distributed architecture: (1) Scheduler — manages trial queue with priority, (2) Worker pool — K8s pods that execute trials, (3) Result DB — stores hyperparameters and metrics, (4) Search algorithm — Bayesian optimization with early stopping (Hyperband/ASHA). Use a suggestion service that runs the acquisition function and proposes new trials. Implement warm-starting from previous studies.

### Q2: Compare Bayesian optimization vs random search vs grid search.
**Answer**: Grid search is exhaustive but suffers from curse of dimensionality (exponential scaling). Random search is more efficient in high dimensions — it explores the space uniformly and finds good regions faster. Bayesian optimization builds a probabilistic model (GP or TPE) to focus on promising regions, requiring fewer trials but with higher overhead per trial. Use grid for ≤2 dims, random for 3-10 dims, Bayesian for expensive evaluations (10+ hours per trial).

### Q3: How do you implement early stopping in AutoML?
**Answer**: Use Successive Halving: allocate a small budget to many trials, then keep the top 1/η and allocate more budget. ASHA (Asynchronous Successive Halving Algorithm) extends this for asynchronous, distributed settings. Also implement learning curve prediction — stop trials where predicted improvement is below a threshold.

### Q4: Explain neural architecture search. How does it differ from hyperparameter tuning?
**Answer**: NAS searches over network architectures (layer types, connectivity, activation functions) while hyperparameter tuning optimizes numerical parameters of a fixed architecture. NAS uses RL, evolution, or gradient-based methods (DARTS). It's computationally expensive (1000s of GPU days) but can discover novel architectures. Cells-based search (NASNet) reduces cost by searching over building blocks rather than full architectures.

## LeetCode / NeetCode References
- **Design a Search Engine** — Search/optimization algorithms
- **Find Peak Element (LeetCode 162)** — Optimization landscape analogy
- **Kth Smallest Element in a Sorted Matrix (LeetCode 378)** — Multi-dimensional search
