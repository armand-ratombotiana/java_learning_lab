# Data Science Academy

![Status](https://img.shields.io/badge/status-active-success.svg)
![Labs](https://img.shields.io/badge/labs-15-blue)
![Difficulty](https://img.shields.io/badge/difficulty-intermediate-red)
![Java](https://img.shields.io/badge/Java-21+-red)

## Overview

The Data Science Academy teaches end-to-end data analysis in Java. Starting from raw data ingestion and cleaning, you will progress through visualization, exploratory analysis, feature engineering, time-series modeling, causal inference, experimentation, and advanced topics including Bayesian statistics, dimensionality reduction, clustering, NLP, ensemble methods, model evaluation, unsupervised learning, and production data pipelines. Labs use pure Java implementations with JUnit 5 testing.

## Curriculum

### Level 1: Core Data Science

| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 01 | [data-wrangling](./01-data-wrangling) | DataFrame/Series operations, loading CSV/JSON/DB, cleaning, reshaping | 4h | * |
| 02 | [visualization](./02-visualization) | Line/bar/scatter/histogram/heatmap charts, JFreeChart, XChart | 3h | * |
| 03 | [exploratory-analysis](./03-exploratory-analysis) | Descriptive stats, distributions, correlation, outlier detection | 3h | ** |
| 04 | [feature-engineering](./04-feature-engineering) | Encoding, scaling, binning, text features, date extraction, PCA | 4h | ** |
| 05 | [time-series](./05-time-series) | Trend/seasonality, stationarity, ARIMA, exponential smoothing | 4h | *** |
| 06 | [causal-inference](./06-causal-inference) | ATE/ATT, confounding, matching, IV, DAGs, do-calculus | 4h | *** |
| 07 | [experimentation](./07-experimentation) | A/B testing, hypothesis tests, power analysis, Bayesian methods | 4h | *** |

### Level 2: Advanced Data Science

| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 08 | [bayesian-statistics](./08-bayesian-statistics) | Bayesian inference, conjugate priors, MCMC sampling, PyMC3 concepts in Java | 5h | *** |
| 09 | [dimension-reduction](./09-dimension-reduction) | PCA, t-SNE, UMAP, feature selection, variance threshold | 4h | *** |
| 10 | [clustering-advanced](./10-clustering-advanced) | DBSCAN, HDBSCAN, Gaussian Mixture Models, hierarchical clustering | 5h | *** |
| 11 | [nlp-advanced](./11-nlp-advanced) | Tokenization, TF-IDF, word embeddings, LDA topic modeling, text classification | 5h | *** |
| 12 | [ensemble-methods](./12-ensemble-methods) | Random Forest, Gradient Boosting, XGBoost concepts, stacking, bagging | 5h | **** |
| 13 | [model-evaluation](./13-model-evaluation) | Cross-validation, ROC/AUC, confusion matrix, bias-variance tradeoff, hyperparameter tuning | 4h | *** |

### Level 3: Production Data Science

| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 14 | [unsupervised-learning](./14-unsupervised-learning) | Autoencoders, manifold learning, anomaly detection, clustering metrics | 5h | **** |
| 15 | [data-pipelines](./15-data-pipelines) | Feature engineering pipelines, data transforms, imputation, encoding, scaling | 5h | **** |

**Total estimated time: 64 hours**

## How to Use

Each lab contains:
- **THEORY.md** — In-depth concept explanations with formulas
- **MATH_FOUNDATION.md** — Detailed mathematical derivations
- **CODE_DEEP_DIVE.md** — Annotated Java implementation walkthroughs
- **EXERCISES.md** — Practice problems with solutions
- **ARCHITECTURE.md** — Software design documentation
- **SECURITY.md** — Security considerations
- **PERFORMANCE.md** — Performance analysis and optimization
- **REFACTORING.md** — Code improvement strategies
- **DEBUGGING.md** — Common bugs and troubleshooting
- **COMMON_MISTAKES.md** — Frequent pitfalls
- **STEP_BY_STEP.md** — Procedural implementation guide
- **VISUAL_GUIDE.md** — Diagrams and visual explanations
- **INTERNALS.md** — Deep dive into mechanisms
- **HOW_IT_WORKS.md** — High-level explanations
- **MENTAL_MODELS.md** — Conceptual frameworks
- **HISTORY.md** — Historical development
- **WHY_IT_MATTERS.md** — Real-world importance
- **WHY_IT_EXISTS.md** — Problem context
- **REFERENCES.md** — Academic papers and resources
- **REFLECTION.md** — Guided reflection questions
- **INTERVIEW.md** — Common interview questions
- **FLASHCARDS.md** — Spaced-repetition review cards
- **QUIZ.md** — Knowledge check questions
- **MINI_PROJECT/** — Guided project directory
- **REAL_WORLD_PROJECT/** — Full production scenario directory

Work sequentially. Start with Level 1 (Labs 01-07), then proceed to Level 2 (Labs 08-13), and finally Level 3 (Labs 14-15). Each lab builds on concepts from previous labs.

## Prerequisites

- Java 21+ and Maven/Gradle
- Familiarity with basic statistics (mean, variance, distributions)
- Understanding of CSV/JSON data formats
- Familiarity with JVM ecosystem
- Linear algebra and probability fundamentals

## Learning Path

```
Level 1: Core
01 -- 02 -- 03 -- 04 -- 05 -- 06 -- 07
Wrlng Viz  EDA  Feat  Time  Caus  Expt
                 Eng   Ser   Inf   Des

Level 2: Advanced
08 -- 09 -- 10 -- 11 -- 12 -- 13
Bayes  Dim   Clus  NLP   Ens   Eval
Stats  Red   ter   Adv   ble   uatn

Level 3: Production
14 -- 15
Unsup Data
Learn Pipes
```

## Related Academies

- [Math Academy](../math) — Probability, statistics, combinatorics (foundational)
- [AI Academy](../ai) — Machine learning, deep learning, NLP
- [Databases Academy](../databases) — SQL, NoSQL, data warehousing
- [DevOps Academy](../devops) — MLOps, pipeline automation

## Resources

- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Apache Commons Math](https://commons.apache.org/proper/commons-math)
- [Smile - Statistical Machine Intelligence](https://haifengl.github.io/)
- [JSAT - Java Statistical Analysis Tool](https://github.com/EdwardRaff/JSAT)
