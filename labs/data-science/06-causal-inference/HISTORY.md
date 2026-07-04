# History of Causal Inference

## 1920s: Randomized Experiments

- Ronald Fisher (1926) formalized randomized controlled trials (RCTs) in agriculture
- Randomization ensures treatment and control groups are exchangeable — the gold standard for causal inference

## 1970s: The Causal Revolution Begins

- Donald Rubin (1974) introduced the **potential outcomes framework** (Rubin Causal Model)
- Each unit has two potential outcomes: Y(1) if treated, Y(0) if not treated
- The causal effect = Y(1) − Y(0), but we only observe one — this is the **fundamental problem of causal inference**

## 1980s–1990s: Observational Methods

- Heckman (1979) developed selection bias correction
- Rosenbaum & Rubin (1983) introduced **propensity score matching**
- Angrist, Imbens, Rubin (1996) developed instrumental variables for causal inference

## 1990s–2000s: Graphical Models

- Judea Pearl (2000) published *Causality* — formalized **Directed Acyclic Graphs** (DAGs) for causal reasoning
- Introduced the **do-calculus**: a complete set of rules for identifying causal effects from observational data
- Pearl's framework makes causal assumptions explicit and testable via d-separation

## 2010s–2020s: ML for Causal Inference

- Athey & Imbens (2016) introduced causal forests
- Double ML (Chernozhukov et al., 2018) uses ML to estimate causal effects in high dimensions
- Causal graphs + ML: the unification of Pearl's structural approach with modern ML

```java
// Modern causal inference: Double ML in Java (conceptual)
double[] causalEffect = DoubleML.estimate(
    outcome,       // Y
    treatment,     // T
    covariates,    // X (high-dimensional confounders)
    new RandomForest(),  // ML model for outcome
    new RandomForest()   // ML model for treatment
);
```
