# Feature Engineering Lab

## Overview
Feature engineering is the process of using domain knowledge to create features that make machine learning algorithms work better. It involves transforming raw data into meaningful inputs for models.

## 1. Feature Extraction

### Numeric Features
- Raw numerical values
- Mathematical transformations
- Statistical aggregations

### Categorical Features
- One-hot encoding
- Label encoding
- Target encoding
- Frequency encoding

### Text Features
- Bag of words
- TF-IDF
- Word embeddings
- N-grams

### Date/Time Features
- Year, month, day
- Day of week
- Hour, minute
- Time since reference

## 2. Feature Transformation

### Scaling
- Standardization (z-score)
- Min-Max scaling
- Robust scaling
- Log transformation

### Imputation
- Mean/median imputation
- Mode imputation
- Forward/backward fill
- K-Nearest Neighbors

### Discretization
- Equal width bins
- Equal frequency bins
- Domain-based bins

## 3. Feature Selection

### Filter Methods
- Correlation threshold
- Variance threshold
- Statistical tests

### Wrapper Methods
- Recursive feature elimination
- Forward selection
- Backward elimination

### Embedded Methods
- L1 regularization (Lasso)
- Feature importance from tree models

## 4. Dimensionality Reduction

### PCA
- Linear projection
- Maximizes variance
- Unsupervised

### t-SNE
- Non-linear
- Preserves local structure
- Visualization

### UMAP
- Fast
- Preserves global structure
- Configurable distance metric