# Lab 11: Model Governance & Compliance — Guide

## Step 1: Understand Model Governance

Key components:
- **Model Cards**: Standardized documentation (model details, intended use, fairness evaluation)
- **Audit Trail**: Immutable log of all model versions, decisions, and transitions
- **Bias Detection**: Statistical parity, equal opportunity, disparate impact
- **Compliance Report**: Regulatory documentation for auditors

## Step 2: Implement ModelCard

The `ModelCard` class captures:
- Model metadata (name, version, type, framework)
- Intended use and limitations
- Training data description
- Evaluation results by demographic group
- Fairness metrics

## Step 3: Implement BiasDetector

The `BiasDetector` computes:
- Demographic parity difference
- Equal opportunity difference
- Disparate impact ratio (80% rule)

## Step 4: Compile and Run

```bash
cd lab11/src
javac com/mlops/lab11/*.java
java com.mlops.lab11.ModelGovernanceLab
```

## Fairness Metrics

| Metric | Formula | Threshold |
|--------|---------|-----------|
| Demographic Parity | P(ŷ=1|A=a) - P(ŷ=1|A=b) | < 0.1 |
| Equal Opportunity | TPR(a) - TPR(b) | < 0.1 |
| Disparate Impact | min(P(ŷ=1|A=a)/P(ŷ=1|A=b), ...) | > 0.8 |

## Best Practices
- Document models before deployment
- Run bias detection as part of CI/CD pipeline
- Maintain immutable audit logs for all model transitions
- Conduct regular fairness reviews with stakeholders
- Implement human-in-loop for high-risk decisions
