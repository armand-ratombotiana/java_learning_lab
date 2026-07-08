# Math Foundation: Payment System Design

## 1. Probability and Statistics

### Basic Probability
- P(A ⋃ B) = P(A) + P(B) - P(A ⋂ B)
- P(A|B) = P(A ⋂ B) / P(B)
- Bayes' Theorem: P(A|B) = P(B|A) * P(A) / P(B)

### Key Distributions
- **Poisson**: P(X=k) = (λ^k * e^(-λ)) / k! for arrival rates
- **Exponential**: f(x) = λ * e^(-λx) for service times
- **Normal**: f(x) = (1/σ√2π) * e^(-(x-μ)²/(2σ²))

## 2. Queueing Theory

### Little's Law
L = λ * W (L=items in system, λ=arrival rate, W=time in system)

### M/M/1 Queue
- Utilization: ρ = λ/μ
- Queue length: Lq = ρ²/(1-ρ)
- Wait time: Wq = ρ/(μ(1-ρ))

### M/M/c Queue
- Erlang C formula for probability of queueing
- Wq = C(c, ρ) * (1/(cμ - λ))

## 3. Algorithm Analysis

### Time Complexity
- O(1): Constant (hash lookup)
- O(log n): Logarithmic (binary search)
- O(n): Linear (sequential scan)
- O(n log n): Linearithmic (sorting)
- O(n²): Quadratic (nested loops)

## 4. Applied Math for Payment System Design

### Load Balancing Distribution
P(server i selected) = wᵢ / Σ wⱼ

### Performance Percentiles
- p50: Median latency
- p95: 95% of requests within this time
- p99: 99% of requests within this time

### Availability Calculations
- 99.9%: 8.77 hours downtime/year
- 99.99%: 52.56 minutes/year
- 99.999%: 5.26 minutes/year

### Capacity Planning
Required capacity = (peak RPS * avg latency) / (1 - utilization target)
