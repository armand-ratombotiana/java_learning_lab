# ML Algorithm Flowchart

## Decision Tree: Algorithm Selection

```
Start
  │
  ▼
Is target variable categorical?
  │
  ├─ YES → Is dataset small? (< 10k)
  │         │
  │         ├─ YES → Decision Tree, Naive Bayes
  │         │
  │         └─ NO → Is accuracy critical?
  │                   │
  │                   ├─ YES → Gradient Boosting, Random Forest
  │                   └─ NO → SVM, Logistic Regression
  │
  └─ NO → Is clustering needed?
            │
            ├─ YES → K-Means, DBSCAN
            │
            └─ NO → Linear Regression, Ridge, Lasso
```

## Algorithm Categories

### Classification
| Algorithm | Use Case |
|-----------|----------|
| Decision Tree | Interpretable, categorical/numeric |
| Random Forest | High accuracy, handles noise |
| Naive Bayes | Fast, text classification |
| SVM | Complex boundaries |
| Neural Network | Complex patterns |

### Regression
| Algorithm | Use Case |
|-----------|----------|
| Linear Regression | Simple relationships |
| Ridge/Lasso | Feature selection, regularization |
| Decision Tree | Non-linear, numeric |
| Random Forest | Robust predictions |

### Clustering
| Algorithm | Use Case |
|-----------|----------|
| K-Means | Known k, spherical clusters |
| DBSCAN | Arbitrary shapes, outlier detection |
| Hierarchical | Dendrogram visualization |

## Weka Workflow
1. Load ARFF/CSV data
2. Apply filters (normalization, feature selection)
3. Choose classifier
4. Evaluate with cross-validation
5. Export model for deployment