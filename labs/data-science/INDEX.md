# 📊 Data Science Academy

![Status](https://img.shields.io/badge/status-active-success.svg)
![Labs](https://img.shields.io/badge/labs-7-blue)
![Difficulty](https://img.shields.io/badge/difficulty-intermediate-yellow)
![Java](https://img.shields.io/badge/Java-11+-red)
![Tablesaw](https://img.shields.io/badge/Tablesaw-orange)

## Overview

The Data Science Academy teaches end-to-end data analysis in Java. Starting from raw data ingestion and cleaning, you will progress through visualization, exploratory analysis, feature engineering, time-series modeling, causal inference, and experimentation. Labs use Tablesaw, JFreeChart, XChart, and custom Java implementations to mirror the Python data science stack.

## Curriculum

| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 01 | [data-wrangling](./01-data-wrangling) | DataFrame/Series operations, loading CSV/JSON/DB, cleaning, reshaping | 4h | ★☆☆ |
| 02 | [visualization](./02-visualization) | Line/bar/scatter/histogram/heatmap charts, JFreeChart, XChart | 3h | ★☆☆ |
| 03 | [exploratory-analysis](./03-exploratory-analysis) | Descriptive stats, distributions, correlation, outlier detection | 3h | ★★☆ |
| 04 | [feature-engineering](./04-feature-engineering) | Encoding, scaling, binning, text features, date extraction, PCA | 4h | ★★☆ |
| 05 | [time-series](./05-time-series) | Trend/seasonality, stationarity, ARIMA, exponential smoothing | 4h | ★★★ |
| 06 | [causal-inference](./06-causal-inference) | ATE/ATT, confounding, matching, IV, DAGs, do-calculus | 4h | ★★★ |
| 07 | [experimentation](./07-experimentation) | A/B testing, hypothesis tests, power analysis, Bayesian methods | 4h | ★★★ |

**Total estimated time: 26 hours**

## How to Use

Each lab contains:
- **THEORY.md** — In-depth concept explanations with formulas
- **CODE_DEEP_DIVE.md** — Annotated Java implementation walkthroughs
- **EXERCISES.md** — Practice problems with solutions
- **MINI_PROJECT/** — Guided project directory with starter code
- **REAL_WORLD_PROJECT/** — Full production scenario directory
- **QUIZ.md** — Knowledge check questions
- **FLASHCARDS.md** — Spaced-repetition review cards

Work sequentially. After reading THEORY.md, follow CODE_DEEP_DIVE.md, then build MINI_PROJECT and REAL_WORLD_PROJECT. Use data from the provided datasets or bring your own.

## Prerequisites

- Java 11+ and Maven/Gradle
- Familiarity with basic statistics (mean, variance, distributions)
- Understanding of CSV/JSON data formats
- (Optional) Familiarity with JVM ecosystem

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07
Wrangle  Viz    EDA    Feat    Time    Causal  Expt
                            Eng     Series  Inf     Design
```

Labs 01–04 form the core data science workflow. Lab 05 adds temporal analysis. Labs 06–07 add rigorous statistical reasoning for decision-making.

## Related Academies

- [Math Academy](../math) — Probability, statistics, combinatorics (foundational)
- [AI Academy](../ai) — Machine learning, deep learning, NLP
- [Databases Academy](../databases) — SQL, NoSQL, data warehousing
- [DevOps Academy](../devops) — MLOps, pipeline automation

## Resources

- [Tablesaw Documentation](https://jtablesaw.github.io/tablesaw)
- [JFreeChart Guide](https://www.jfree.org/jfreechart)
- [XChart GitHub](https://github.com/knowm/XChart)
- [Apache Commons Math](https://commons.apache.org/proper/commons-math)
- [Java Machine Learning (JSAT)](https://github.com/EdwardRaff/JSAT)
