# Anomaly Detection - Theory

## 1. Statistical Methods

### Z-Score / Modified Z-Score
- Points beyond threshold are anomalies

### IQR (Interquartile Range)
- Points outside Q1-1.5*IQR and Q3+1.5*IQR

### Grubbs' Test
- Detects single outlier

### Chi-Squared
- For multivariate normal data

## 2. Isolation Forest

### Key Idea
- Anomalies are easily isolated
- Random partitioning
- Short path = anomaly

### Algorithm
1. Build random decision trees
2. Average path length
3. Score = 2^(-mean_depth/c(n))

## 3. One-Class SVM

### Support Vector Domain Description
- Find smallest enclosing hypersphere
- Points outside are anomalies

### Kernel trick for non-linear boundaries

## 4. Autoencoders

### Reconstruction Error
- Train on normal data
- High reconstruction error = anomaly

### Variants
- Denoising autoencoder
- Sparse autoencoder
- Variational autoencoder

## 5. Local Outlier Factor (LOF)

### Density-Based
- Compare local density to neighbors
- LOF > 1 = outlier

## 6. Evaluation

### Precision-Recall Curve
### F1-Score at k