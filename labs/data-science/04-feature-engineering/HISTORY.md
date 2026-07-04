# History of Feature Engineering

## 1990s: Manual Crafting

Before ML went mainstream, feature engineering was done manually by domain experts. Credit scoring used features like "number of late payments in last 12 months" crafted by bankers.

## 2000s: Feature Selection

The rise of SVMs and decision trees brought feature selection (finding the best subset) into focus. Algorithms like Relief, LASSO, and Random Forest feature importance reduced reliance on manual selection.

## 2010s: Automated Feature Engineering

- **Featuretools** (2017) introduced Deep Feature Synthesis — automated relational feature engineering
- **t-SNE and UMAP** automated feature extraction for visualization
- **Deep learning** automated feature learning for images, audio, text

## 2020s: Transformer-Based Features

- Tabular transformers (TabNet, FT-Transformer) attempt to learn features end-to-end
- Foundation models generate embeddings that serve as features for downstream tasks
- Feature stores (Feast, Tecton) platformized feature engineering

## Java Timeline

```java
// Smile library provides automated feature extraction (2014+)
// PCA for dimensionality reduction
double[][] features = Smile.pca(data, 10).project(data);

// Feature scaling
double[][] scaled = Smile.normalize(data);
```
