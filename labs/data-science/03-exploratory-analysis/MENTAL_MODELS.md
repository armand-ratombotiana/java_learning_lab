# Mental Models for Exploratory Data Analysis

## 1. The Detective

EDA is detective work. You have clues (data points), leads (correlations), witnesses (domain experts), and red herrings (spurious correlations). The goal is to build a coherent story about what happened and why — not to convict (model), but to understand.

## 2. The Four Questions

Every EDA session answers four questions:
1. **What is the shape of the data?** (dimensions, types, uniqueness)
2. **What is normal?** (central tendency, typical values)
3. **What is wrong?** (missing, outliers, contradictions)
4. **What is interesting?** (patterns, clusters, anomalies)

## 3. The Funnel

EDA starts broad and narrows:
- **All columns** → summary stats → all pairs correlation
- **Interesting columns** → distribution plots → group comparisons
- **Interesting relationships** → detailed scatter/facet → transformation experiments
- **Verified patterns** → hypothesis formulation → model design

## 4. Anscombe's Quartet

Four datasets with identical summary statistics (mean x=9, mean y=7.5, correlation=0.82, regression line same) but wildly different distributions:

```
Dataset 1: Linear relationship (the "good" one)
Dataset 2: Curved relationship (wrong model form)
Dataset 3: Linear with one outlier (one point drives correlation)
Dataset 4: Vertical line with one outlier (no actual x/y relationship)
```

Moral: **Always visualize. Never trust summary statistics alone.**
