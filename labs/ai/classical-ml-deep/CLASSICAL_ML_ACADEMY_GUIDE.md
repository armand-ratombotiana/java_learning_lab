# Classical ML — Deep Dive Sub-Academy Guide

> Companion guide for 01-linear-regression through 10-anomaly-detection micro-labs

---

## How to Use This Guide

Each micro-lab section contains:
- **Key Interview Questions** — what top companies ask
- **Company Focus** — which companies emphasize this topic
- **Code Examples** — scikit-learn snippets
- **Math Derivations** — the essential equations

---

## Micro-Lab 01: Linear Regression

### Key Interview Questions

1. Derive the OLS estimator from scratch. What assumptions are needed?
2. What is the Gauss-Markov theorem and what does it guarantee?
3. How do you interpret a coefficient in multiple linear regression?
4. What is multicollinearity and how do you detect it?
5. Explain the difference between confidence intervals and prediction intervals.
6. What happens if we omit an important variable?
7. What is heteroscedasticity and how does it affect inference?

### Company Focus

| Company | Focus Area |
|---------|-----------|
| **Amazon** | Interpretability, multicollinearity in pricing models |
| **JPMorgan** | Linear models for credit scoring, regulatory compliance |
| **Google** | Large-scale OLS via normal equations vs gradient descent |
| **Uber** | Demand forecasting, time-series with linear models |
| **Netflix** | Baseline models for comparison with deep learning |

### Code Examples

```python
import numpy as np
from sklearn.linear_model import LinearRegression
from sklearn.metrics import mean_squared_error, r2_score

# OLS closed-form
X = np.random.randn(100, 3)
y = X @ np.array([1.5, -2.0, 3.0]) + np.random.randn(100) * 0.5

model = LinearRegression()
model.fit(X, y)

print(f"Coefficients: {model.coef_}")
print(f"Intercept: {model.intercept_}")
print(f"R²: {r2_score(y, model.predict(X))}")

# Manual OLS
X_with_bias = np.c_[np.ones(100), X]
beta_hat = np.linalg.inv(X_with_bias.T @ X_with_bias) @ X_with_bias.T @ y
```

### Math Derivations

**OLS closed form:**
```
RSS = (y - Xβ)ᵀ(y - Xβ) = yᵀy - 2βᵀXᵀy + βᵀXᵀXβ
∂RSS/∂β = -2Xᵀy + 2XᵀXβ = 0
β̂ = (XᵀX)⁻¹Xᵀy
```

**Variance of β̂:**
```
Var(β̂) = σ²(XᵀX)⁻¹
```

**Gauss-Markov:** Under assumptions 1-5 (linearity, independence, exogeneity, homoscedasticity, no perfect collinearity), OLS is the Best Linear Unbiased Estimator (BLUE).

---

## Micro-Lab 02: Regularized Regression (Ridge, Lasso, ElasticNet)

### Key Interview Questions

1. Compare L1 and L2 regularization — why does L1 induce sparsity?
2. How do you tune λ (alpha)?
3. What is the bias-variance tradeoff in regularization?
4. Why does ridge have a closed form while lasso doesn't?
5. How does ElasticNet handle groups of correlated features?
6. What happens to ridge coefficients as λ → ∞? As λ → 0?
7. Why standardize features before regularization?

### Company Focus

| Company | Focus Area |
|---------|-----------|
| **Goldman Sachs** | Ridge for multicollinearity in financial models |
| **Airbnb** | Lasso for feature selection in pricing models |
| **Facebook/Meta** | ElasticNet for ad click prediction (sparse + groups) |
| **Zillow** | Regularized regression for home value estimation |
| **Capital One** | Risk modeling with L1 for interpretability |

### Code Examples

```python
from sklearn.linear_model import Ridge, Lasso, ElasticNet
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline

# Ridge
ridge = Ridge(alpha=1.0)
ridge.fit(X_train, y_train)

# Lasso (feature selection)
lasso = Lasso(alpha=0.01)
lasso.fit(X_train, y_train)
print(f"Zeroed features: {np.sum(lasso.coef_ == 0)}")

# ElasticNet
en = ElasticNet(alpha=0.01, l1_ratio=0.5)  # l1_ratio = 0 → ridge, 1 → lasso
en.fit(X_train, y_train)

# Cross-validated
from sklearn.linear_model import RidgeCV, LassoCV, ElasticNetCV
ridge_cv = RidgeCV(alphas=[0.1, 1.0, 10.0])
ridge_cv.fit(X_train, y_train)
print(f"Best alpha: {ridge_cv.alpha_}")
```

### Math Derivations

**Ridge shrinkage effect:**
```
β̂_ridge = (XᵀX + λI)⁻¹Xᵀy
```
- Adding λI to singular values → all singular values become non-zero → invertible.
- If XᵀX = I (orthonormal design): β̂_ridge = β̂_ols / (1 + λ).

**Lasso (subgradient condition):**
- For orthonormal design: β̂_j = sign(β̂_j_ols) × max(0, |β̂_j_ols| - λ/2).
- Soft-thresholding operator: S(z, γ) = sign(z)(|z| - γ)_+.

**Lasso path property:** As λ decreases, variables enter the model one by one.

---

## Micro-Lab 03: Logistic Regression

### Key Interview Questions

1. Derive the logistic regression loss function from maximum likelihood.
2. Why use log-loss instead of MSE for classification?
3. How is the decision boundary determined?
4. What is the odds ratio interpretation?
5. Explain multinomial logistic regression and the softmax function.
6. Why can't logistic regression have a closed-form solution?
7. How do you handle multi-class with logistic regression? (OvR vs softmax)

### Company Focus

| Company | Focus Area |
|---------|-----------|
| **Google** | Ad click prediction (logistic regression baseline) |
| **LinkedIn** | Feed ranking, recommendation calibrations |
| **Spotify** | Playlist classification, content filtering |
| **Wells Fargo** | Loan default prediction (interpretability required) |
| **Twitter/X** | Abuse detection, content moderation |

### Code Examples

```python
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import log_loss

# Binary
lr = LogisticRegression(C=1.0, penalty='l2')
lr.fit(X_train, y_train)
probs = lr.predict_proba(X_test)[:, 1]

# Multinomial (softmax)
lr_multi = LogisticRegression(multi_class='multinomial', solver='lbfgs')
lr_multi.fit(X_train, y_train_multi)

# Loss comparison
print(f"Log-loss: {log_loss(y_test, probs)}")
print(f"Accuracy: {lr.score(X_test, y_test)}")

# Manual log-odds
log_odds = X_test @ lr.coef_.T + lr.intercept_
probs_manual = 1 / (1 + np.exp(-log_odds))
```

### Math Derivations

**MLE formulation:**
```
L(β) = Π P(yᵢ|xᵢ)^yᵢ (1-P(yᵢ|xᵢ))^(1-yᵢ)
log L = Σ [yᵢ log pᵢ + (1-yᵢ) log(1-pᵢ)]
```

**Gradient:**
```
∂logL/∂βⱼ = Σ (yᵢ - pᵢ) xᵢⱼ
```

**Hessian (for IRLS):**
```
∂²logL/∂βⱼ∂βₖ = -Σ pᵢ(1-pᵢ) xᵢⱼ xᵢₖ
```

**No closed form:** The gradient equations are nonlinear in β (pᵢ depends on β through sigmoid). Solved via IRLS or gradient descent.

**Multinomial softmax:**
```
P(y=k|x) = exp(xβₖ) / Σⱼ exp(xβⱼ)
```
- One set of coefficients per class.
- Identifiability: fix one class as reference (β_ref = 0).

---

## Micro-Lab 04: Decision Trees

### Key Interview Questions

1. Compare Gini impurity vs entropy vs misclassification rate.
2. How does a decision tree split a continuous feature?
3. Explain cost-complexity pruning and how α is chosen.
4. Why are decision trees prone to overfitting?
5. How does max_depth prevent overfitting vs underfitting?
6. What makes a split "good" in a regression tree?
7. How do decision trees handle missing values?

### Company Focus

| Company | Focus Area |
|---------|-----------|
| **JPMorgan** | Credit risk decision rules (interpretable) |
| **Amazon** | Customer segmentation, A/B test analysis |
| **Instacart** | Product substitution logic |
| **Palantir** | Explainable AI / rule extraction |
| **Allstate** | Insurance risk stratification |

### Code Examples

```python
from sklearn.tree import DecisionTreeClassifier, DecisionTreeRegressor, plot_tree
from sklearn.tree import export_text

# Classification tree
dt = DecisionTreeClassifier(max_depth=3, min_samples_split=10, criterion='gini')
dt.fit(X_train, y_train)

# Regression tree
dt_reg = DecisionTreeRegressor(max_depth=5, min_samples_leaf=5)
dt_reg.fit(X_train, y_train)

# Inspect tree
rules = export_text(dt, feature_names=feature_names)
print(rules)

# Cost-complexity pruning
path = dt.cost_complexity_pruning_path(X_train, y_train)
ccp_alphas, impurities = path.ccp_alphas, path.impurities
```

### Math Derivations

**Gini impurity:**
```
Gini(t) = 1 - Σⱼ pⱼ²
```
- Maximized at 0.5 (two classes, equal), minimized at 0.

**Entropy:**
```
H(t) = -Σⱼ pⱼ log₂(pⱼ)
```
- Maximized at 1 (two classes), minimized at 0.

**MSE reduction (regression):**
```
Δ = MSE(parent) - (n_l/nₚ MSE(left) + n_r/nₚ MSE(right))
```

**Cost-complexity pruning:**
```
Cα(T) = R(T) + α|T|
```
- R(T) = misclassification rate (or MSE), |T| = number of terminal nodes.
- Weakest-link: prune the node that minimizes ΔR / Δ|T|.

---

## Micro-Lab 05: Random Forest

### Key Interview Questions

1. How does Random Forest reduce variance compared to a single tree?
2. What is the effect of the number of trees (n_estimators)?
3. How does feature subsampling (max_features) work and why is it important?
4. What is OOB error and how does it relate to cross-validation?
5. How does Random Forest handle imbalanced data?
6. Compare Random Forest vs Gradient Boosting.
7. Can Random Forest overfit?

### Company Focus

| Company | Focus Area |
|---------|-----------|
| **Kaggle** | Competition-winning baseline (tabular data) |
| **Airbnb** | Search ranking, price prediction |
| **Uber** | ETD (estimated time of delivery) prediction |
| **Capital One** | Fraud detection with Random Forest ensembles |
| **Procter & Gamble** | Supply chain demand forecasting |

### Code Examples

```python
from sklearn.ensemble import RandomForestClassifier, RandomForestRegressor
from sklearn.metrics import classification_report
import matplotlib.pyplot as plt

# Random Forest Classifier
rf = RandomForestClassifier(
    n_estimators=200,
    max_depth=10,
    min_samples_leaf=4,
    max_features='sqrt',  # sqrt(p) for classification
    oob_score=True,
    random_state=42
)
rf.fit(X_train, y_train)

print(f"OOB score: {rf.oob_score_:.4f}")
print(f"Test accuracy: {rf.score(X_test, y_test):.4f}")

# Feature importance
importances = rf.feature_importances_
indices = np.argsort(importances)[::-1]
for i, idx in enumerate(indices[:5]):
    print(f"{i+1}. {feature_names[idx]}: {importances[idx]:.4f}")

# OOB as validation monitor
errors = []
for n in range(1, 301):
    rf_n = RandomForestClassifier(n_estimators=n, oob_score=True, n_jobs=-1)
    rf_n.fit(X_train, y_train)
    errors.append(1 - rf_n.oob_score_)
```

### Math Derivations

**Variance of ensemble:**
```
Var(f_ensemble(x)) = ρ(x)σ²(x) + (1-ρ(x))σ²(x)/B
```
- ρ = pairwise correlation between tree predictions.
- As B → ∞, variance → ρσ².
- Feature subsampling (max_features < p) reduces ρ.

**OOB error estimate:**
- Each tree uses ~63.2% of data (1 - e⁻¹ ≈ 0.632).
- For each sample, predict using trees where it was OOB.
- OOB error is nearly unbiased (like leave-one-out CV but faster).

---

## Micro-Lab 06: Gradient Boosting (XGBoost, LightGBM, CatBoost)

### Key Interview Questions

1. How does gradient boosting differ from bagging?
2. Explain the role of the learning rate (η) and n_estimators.
3. What innovations does XGBoost bring over traditional boosting?
4. How does LightGBM achieve faster training than XGBoost?
5. What is CatBoost's ordered boosting and why is it needed?
6. How do you tune a gradient boosting model?
7. Explain early stopping and its relationship to overfitting.

### Company Focus

| Company | Focus Area |
|---------|-----------|
| **Kaggle** | Dominant algorithm for tabular competitions |
| **Booking.com** | Hotel ranking, price optimization |
| **Citi** | Credit risk, default prediction |
| **Yandex** | CatBoost origin — search relevance |
| **Microsoft** | LightGBM origin — ranking, ads |
| **DoorDash** | ETD, demand forecasting |

### Code Examples

```python
import xgboost as xgb
import lightgbm as lgb
import catboost as cb

# XGBoost
xgb_model = xgb.XGBRegressor(
    n_estimators=500,
    learning_rate=0.05,
    max_depth=6,
    subsample=0.8,
    colsample_bytree=0.8,
    reg_lambda=1.0,
    reg_alpha=0.0,
    early_stopping_rounds=50
)
xgb_model.fit(X_train, y_train, eval_set=[(X_val, y_val)], verbose=False)

# LightGBM
lgb_model = lgb.LGBMRegressor(
    n_estimators=500,
    learning_rate=0.05,
    num_leaves=31,
    subsample=0.8,
    colsample_bytree=0.8,
    reg_lambda=1.0,
    min_child_samples=20
)
lgb_model.fit(X_train, y_train, eval_set=[(X_val, y_val)], callbacks=[lgb.early_stopping(50)])

# CatBoost
cb_model = cb.CatBoostRegressor(
    iterations=500,
    learning_rate=0.05,
    depth=6,
    l2_leaf_reg=3.0,
    cat_features=categorical_indices,
    verbose=False
)
cb_model.fit(X_train, y_train, eval_set=(X_val, y_val))
```

### Math Derivations

**General gradient boosting:**
```
Fₘ(x) = F_{m-1}(x) + η · hₘ(x)
hₘ = argmin Σ (rᵢₘ - h(xᵢ))², where rᵢₘ = -∂L(yᵢ, F_{m-1}(xᵢ))/∂F
```

**XGBoost objective:**
```
L = Σ [gᵢ fₘ(xᵢ) + ½ hᵢ fₘ(xᵢ)²] + γT + ½λ||w||²
```
- Optimal leaf weight: wⱼ* = -Σ gᵢ / (Σ hᵢ + λ).

**LightGBM GOSS:**
- Keep top a × 100% of samples with largest gradients.
- Randomly sample b × 100% of samples with small gradients.
- Multiply sampled small-gradient samples by (1 - a)/b.

**CatBoost ordered boosting:**
- For each step, fit hₘ on a subsample, compute residuals on the rest.
- Uses random permutations: for i-th sample, model trained on first i-1 samples in permutation.

---

## Micro-Lab 07: Support Vector Machines

### Key Interview Questions

1. Derive the SVM dual optimization problem.
2. Explain the kernel trick — why can we replace dot products with kernels?
3. How does the C parameter affect the margin?
4. When would you use RBF vs linear vs polynomial kernel?
5. What are support vectors and why are they important?
6. How does SVR differ from standard regression?
7. Why is SVM sensitive to feature scaling?

### Company Focus

| Company | Focus Area |
|---------|-----------|
| **Google** | Image classification (before deep learning) |
| **Palantir** | Anomaly detection, cybersecurity |
| **JPMorgan** | Document classification, text analysis |
| **Siemens** | Industrial fault detection |
| **Pfizer** | Drug discovery (molecule classification) |

### Code Examples

```python
from sklearn.svm import SVC, SVR, LinearSVC
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline

# Always scale for SVM
svm_pipeline = Pipeline([
    ('scaler', StandardScaler()),
    ('svm', SVC(kernel='rbf', C=1.0, gamma='scale'))
])
svm_pipeline.fit(X_train, y_train)

# Linear SVM (faster for high-d)
linear_svm = LinearSVC(C=1.0, penalty='l2', loss='squared_hinge')
linear_svm.fit(X_train, y_train)

# SVR
svr = SVR(kernel='rbf', C=1.0, epsilon=0.1)
svr.fit(X_train, y_train)

# Grid search for C and gamma
from sklearn.model_selection import GridSearchCV
param_grid = {'C': [0.1, 1, 10], 'gamma': ['scale', 'auto', 0.1, 0.01]}
grid = GridSearchCV(SVC(), param_grid, cv=5)
grid.fit(X_train, y_train)
```

### Math Derivations

**Primal:**
```
min ½||w||²  s.t. yᵢ(wᵀxᵢ + b) ≥ 1
```

**Lagrangian:**
```
L = ½||w||² - Σ αᵢ[yᵢ(wᵀxᵢ + b) - 1], αᵢ ≥ 0
```

**KKT conditions:**
- ∂L/∂w = 0 → w = Σ αᵢ yᵢ xᵢ
- ∂L/∂b = 0 → Σ αᵢ yᵢ = 0
- αᵢ[yᵢ(wᵀxᵢ + b) - 1] = 0 (complementary slackness)

**Dual:**
```
max Σ αᵢ - ½ Σ Σ αᵢ αⱼ yᵢ yⱼ xᵢᵀxⱼ
s.t. αᵢ ≥ 0, Σ αᵢ yᵢ = 0
```

**With kernel:**
```
max Σ αᵢ - ½ Σ Σ αᵢ αⱼ yᵢ yⱼ K(xᵢ, xⱼ)
```

**Decision function:**
```
f(x) = Σ αᵢ yᵢ K(xᵢ, x) + b
```
Only points with αᵢ > 0 (support vectors) contribute.

**Soft margin with C:**
- 0 ≤ αᵢ ≤ C.
- C large → hard margin (few SVs, complex).
- C small → soft margin (many SVs, simple).

---

## Micro-Lab 08: Dimensionality Reduction (PCA, t-SNE, UMAP)

### Key Interview Questions

1. Derive PCA — show it's equivalent to finding eigenvectors of the covariance matrix.
2. How do you choose the number of principal components?
3. Compare PCA vs t-SNE vs UMAP.
4. When would you use feature selection vs feature extraction?
5. Explain the perplexity parameter in t-SNE.
6. How does UMAP achieve better global structure preservation?
7. What is the "curse of dimensionality" and how does PCA help?

### Company Focus

| Company | Focus Area |
|---------|-----------|
| **Spotify** | Audio feature compression, playlist visualization |
| **23andMe** | Genetic data dimensionality reduction |
| **Amazon** | Product embeddings (PCA), recommendation viz |
| **OpenAI** | Embedding visualization (t-SNE/UMAP) |
| **Palantir** | High-dimensional data exploration |
| **Netflix** | User/movie embedding analysis |

### Code Examples

```python
from sklearn.decomposition import PCA
from sklearn.manifold import TSNE
import umap

# PCA
pca = PCA(n_components=0.95)  # keep 95% variance
X_pca = pca.fit_transform(X_scaled)
print(f"Explained variance ratio: {pca.explained_variance_ratio_}")
print(f"Components kept: {X_pca.shape[1]}")

# Scree plot
plt.plot(np.cumsum(pca.explained_variance_ratio_))
plt.xlabel('Number of components')
plt.ylabel('Cumulative explained variance')

# PCA whitening
pca_white = PCA(whiten=True)
X_white = pca_white.fit_transform(X_scaled)
print(f"Covariance after whitening:\n{np.cov(X_white.T)}")

# t-SNE
tsne = TSNE(n_components=2, perplexity=30, random_state=42)
X_tsne = tsne.fit_transform(X_scaled)

# UMAP
reducer = umap.UMAP(n_neighbors=15, min_dist=0.1, n_components=2)
X_umap = reducer.fit_transform(X_scaled)
```

### Math Derivations

**PCA — covariance matrix eigendecomposition:**
```
Σ = (1/(n-1)) XᵀX
Σw = λw
```
- First PC: w₁ = eigenvector with largest eigenvalue λ₁.
- Variance along w₁: w₁ᵀΣw₁ = λ₁.

**PCA via SVD:**
```
X = UDVᵀ
- Columns of V = eigenvectors of XᵀX = principal directions
- UD = principal component scores
```

**t-SNE — KL divergence:**
```
KL(P||Q) = Σᵢ Σⱼ p_{j|i} log(p_{j|i} / q_{j|i})
```
- p_{j|i} ∝ exp(-||xᵢ - xⱼ||² / 2σᵢ²) (Gaussian in high-d).
- q_{j|i} ∝ (1 + ||yᵢ - yⱼ||²)^{-1} (t-distribution in low-d).

**UMAP — cross-entropy:**
```
CE(P, Q) = Σ Σ p_{ij} log(p_{ij}/q_{ij}) + (1 - p_{ij}) log((1-p_{ij})/(1-q_{ij}))
```
- First term: attractive forces (like t-SNE).
- Second term: repulsive forces (captures global structure).

---

## Micro-Lab 09: Clustering (K-Means, DBSCAN, GMM)

### Key Interview Questions

1. Why does K-means converge but only to a local minimum?
2. How does k-means++ initialization work?
3. Compare DBSCAN and K-means — when to use each?
4. What is the silhouette score and how is it interpreted?
5. Explain the EM algorithm for GMM.
6. How do you choose eps in DBSCAN?
7. Compare single-linkage vs Ward linkage in hierarchical clustering.
8. What is the adjusted Rand Index and why use it?

### Company Focus

| Company | Focus Area |
|---------|-----------|
| **Uber** | Geospatial clustering, surge pricing zones |
| **Spotify** | Music recommendation (user clustering) |
| **Amazon** | Customer segmentation (K-means) |
| **LinkedIn** | Member community detection (DBSCAN/SNN) |
| **Twitter/X** | Bot detection (anomaly via clustering) |
| **Pandora** | Music genome project (GMM) |

### Code Examples

```python
from sklearn.cluster import KMeans, DBSCAN, AgglomerativeClustering
from sklearn.mixture import GaussianMixture
from sklearn.metrics import silhouette_score, adjusted_rand_score

# K-Means
kmeans = KMeans(n_clusters=5, init='k-means++', n_init=10, random_state=42)
labels_kmeans = kmeans.fit_predict(X_scaled)
print(f"Silhouette: {silhouette_score(X_scaled, labels_kmeans):.4f}")

# Elbow / silhouette method
scores = []
for k in range(2, 15):
    km = KMeans(n_clusters=k, n_init=10, random_state=42)
    labels = km.fit_predict(X_scaled)
    scores.append(silhouette_score(X_scaled, labels))

# DBSCAN
dbscan = DBSCAN(eps=0.5, min_samples=5)
labels_dbscan = dbscan.fit_predict(X_scaled)
n_clusters = len(set(labels_dbscan)) - (1 if -1 in labels_dbscan else 0)
n_noise = list(labels_dbscan).count(-1)
print(f"DBSCAN clusters: {n_clusters}, noise: {n_noise}")

# GMM
gmm = GaussianMixture(n_components=5, covariance_type='full', random_state=42)
labels_gmm = gmm.fit_predict(X_scaled)
probs = gmm.predict_proba(X_scaled)  # soft assignments
print(f"Log-likelihood: {gmm.score(X_scaled):.2f}")

# Hierarchical
hc = AgglomerativeClustering(n_clusters=5, linkage='ward')
labels_hc = hc.fit_predict(X_scaled)
```

### Math Derivations

**K-means objective:**
```
J = Σₖ Σ_{i∈Cₖ} ||xᵢ - μₖ||²
```

**EM for GMM — E-step:**
```
γᵢₖ = πₖ N(xᵢ | μₖ, Σₖ) / Σⱼ πⱼ N(xᵢ | μⱼ, Σⱼ)
```

**EM for GMM — M-step:**
```
Nₖ = Σᵢ γᵢₖ
πₖ = Nₖ / N
μₖ = (1/Nₖ) Σᵢ γᵢₖ xᵢ
Σₖ = (1/Nₖ) Σᵢ γᵢₖ (xᵢ - μₖ)(xᵢ - μₖ)ᵀ
```

**Silhouette score:**
```
s(i) = (b(i) - a(i)) / max(a(i), b(i))
```
- a(i) = mean intra-cluster distance.
- b(i) = mean distance to nearest neighbor cluster.

**Adjusted Rand Index:**
```
ARI = (RI - E[RI]) / (max(RI) - E[RI])
```

---

## Micro-Lab 10: Anomaly Detection

### Key Interview Questions

1. Compare Isolation Forest, One-Class SVM, and LOF.
2. When would you use autoencoder-based anomaly detection?
3. How does the contamination parameter affect model behavior?
4. Explain the Isolation Forest anomaly score formula.
5. What is local anomaly detection and when is it needed?
6. How do you evaluate anomaly detection without labels?
7. Compare novelty detection vs outlier detection.

### Company Focus

| Company | Focus Area |
|---------|-----------|
| **PayPal** | Fraud detection (Isolation Forest + rules) |
| **Datadog** | Infrastructure monitoring (metric anomalies) |
| **Splunk** | Log anomaly detection (autoencoders) |
| **Cloudflare** | Network intrusion detection |
| **Stripe** | Payment fraud, chargeback prediction |
| **Tesla** | Battery anomaly detection (manufacturing) |

### Code Examples

```python
from sklearn.ensemble import IsolationForest
from sklearn.svm import OneClassSVM
from sklearn.neighbors import LocalOutlierFactor
import numpy as np

# Isolation Forest
iso_forest = IsolationForest(
    n_estimators=100,
    contamination=0.05,  # expected proportion of anomalies
    random_state=42
)
preds_if = iso_forest.fit_predict(X)  # 1 = normal, -1 = anomaly
scores_if = iso_forest.decision_function(X)  # lower = more anomalous

# One-Class SVM
oc_svm = OneClassSVM(kernel='rbf', nu=0.05, gamma='scale')
preds_svm = oc_svm.fit_predict(X)

# LOF
lof = LocalOutlierFactor(n_neighbors=20, contamination=0.05)
preds_lof = lof.fit_predict(X)
scores_lof = -lof.negative_outlier_factor_  # higher = more anomalous

# Autoencoder (PyTorch)
import torch
class AnomalyAE(torch.nn.Module):
    def __init__(self, input_dim):
        super().__init__()
        self.encoder = torch.nn.Sequential(
            torch.nn.Linear(input_dim, 16),
            torch.nn.ReLU(),
            torch.nn.Linear(16, 8),
        )
        self.decoder = torch.nn.Sequential(
            torch.nn.Linear(8, 16),
            torch.nn.ReLU(),
            torch.nn.Linear(16, input_dim),
        )
    def forward(self, x):
        return self.decoder(self.encoder(x))

# Reconstruction error as anomaly score
model = AnomalyAE(X.shape[1])
criterion = torch.nn.MSELoss()
# ... train ...
recon_errors = torch.mean((X_tensor - model(X_tensor))**2, dim=1).detach().numpy()
```

### Math Derivations

**Isolation Forest anomaly score:**
```
s(x, n) = 2^{-E[h(x)] / c(n)}
c(n) = 2H(n-1) - 2(n-1)/n,  H(i) = ln(i) + 0.577
```
- h(x) = path length, c(n) = average BST path length for n nodes.
- E[h(x)] near c(n) → s ≈ 0.5 (ambiguous).
- E[h(x)] → 0 → s → 1 (anomaly).
- E[h(x)] → n-1 → s → 0 (normal).

**One-Class SVM dual:**
```
min ½||w||² + (1/νn) Σ ξᵢ - ρ
s.t. wᵀφ(xᵢ) ≥ ρ - ξᵢ, ξᵢ ≥ 0
```
- ν bounds fraction of outliers (upper) and support vectors (lower).

**LOF:**
```
LOFₖ(a) = (1/|Nₖ(a)|) Σ LRDₖ(b) / LRDₖ(a)
```
- LRDₖ(a) = 1 / (Σ reach-distₖ(a,b) / |Nₖ(a)|).
- LOF > 1 → local anomaly; LOF ≈ 1 → normal.

---

## Appendix — Comparison Tables

### When to Use Which Algorithm

| Task | Best First Try | If That Fails |
|------|---------------|---------------|
| Regression (tabular) | Linear Regression → Ridge | XGBoost, Random Forest |
| Classification (tabular) | Logistic Regression → Random Forest | XGBoost, LightGBM |
| High-dimensional sparse | Linear SVM, Logistic Regression | Lasso, XGBoost |
| Non-linear boundary | RBF SVM → Random Forest | XGBoost, CatBoost |
| Large data (100k+) | LightGBM (GOSS) | Linear models, subsampling |
| Small data (< 1k) | Ridge, Lasso, simple DT | SVM (RBF), bagging |
| Interpretability needed | Linear/Logistic Regression | Decision Tree (depth ≤ 3) |
| Clustering (known K) | K-means | GMM |
| Clustering (unknown K) | DBSCAN | Hierarchical (dendrogram) |
| Anomaly detection (fast) | Isolation Forest | LOF (local anomalies) |
| Anomaly detection (complex) | Autoencoder | One-Class SVM |
| Feature reduction (viz) | PCA → t-SNE | UMAP |

### Interview Quick-Cheat

| Question | Key Phrase |
|----------|-----------|
| "Derive OLS" | ∂RSS/∂β = 0 → (XᵀX)⁻¹Xᵀy |
| "Why L1 sparsity?" | Diamond constraint hits axis |
| "Bias-variance tradeoff" | Bias² + Var + irreducible error |
| "Kernel trick" | Replace xᵢᵀxⱼ with K(xᵢ,xⱼ) = ⟨φ(xᵢ),φ(xⱼ)⟩ |
| "SVM support vectors" | αᵢ > 0 → on/beyond margin |
| "Gradient boosting" | Fit to pseudo-residuals |
| "XGBoost innovation" | 2nd order, regularization, sparsity, column block, quantile sketch |
| "LightGBM speed" | GOSS (sampling) + EFB (feature bundling) |
| "CatBoost vs others" | Ordered boosting + native categorical |
| "t-SNE vs UMAP" | KL vs cross-entropy; local vs global |
| "Isolation Forest" | Fewer splits to isolate anomalies |
| "SMOTE" | Interpolate minority neighbors |
| "Focal loss" | (1-pₜ)^γ focuses on hard examples |
| "MCC" | TP×TN - FP×FN / sqrt(...) — all 4 cells |

---

> Created for the AI Academy — Java Learning Lab