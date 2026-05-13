# Dimensionality Reduction - Code Deep Dive

```python
import numpy as np
from sklearn.decomposition import PCA, LDA
from sklearn.manifold import TSNE, MDS
import umap

# === PCA ===

pca = PCA(n_components=2)
X_pca = pca.fit_transform(X)

# Variance explained
print(f"Variance explained: {pca.explained_variance_ratio_}")
print(f"Total variance: {sum(pca.explained_variance_ratio_):.2f}")

# Full PCA then truncate
pca_full = PCA()
pca_full.fit(X)
cumsum = np.cumsum(pca_full.explained_variance_ratio_)
n_components = np.argmax(cumsum >= 0.95) + 1

# === LDA ===

lda = LDA(n_components=2)
X_lda = lda.fit_transform(X, y)

# === T-SNE ===

tsne = TSNE(n_components=2, perplexity=30, n_iter=1000)
X_tsne = tsne.fit_transform(X)

# === UMAP ===

reducer = umap.UMAP(n_components=2, n_neighbors=15, min_dist=0.1)
X_umap = reducer.fit_transform(X)

# === MDS ===

mds = MDS(n_components=2, random_state=42)
X_mds = mds.fit_transform(X)

# === KERNEL PCA ===

from sklearn.decomposition import KernelPCA

kpca = KernelPCA(n_components=2, kernel='rbf', gamma=0.1)
X_kpca = kpca.fit_transform(X)

# === AUTOENCODER (Neural) ===

from tensorflow.keras.layers import Dense, Input
from tensorflow.keras.models import Model

input_dim = X.shape[1]
encoding_dim = 2

input_layer = Input(shape=(input_dim,))
encoded = Dense(encoding_dim, activation='relu')(input_layer)
decoded = Dense(input_dim, activation='sigmoid')(encoded)

autoencoder = Model(input_layer, decoded)
encoder = Model(input_layer, encoded)

autoencoder.compile(optimizer='adam', loss='mse')
autoencoder.fit(X, X, epochs=50, batch_size=32, verbose=0)

X_encoded = encoder.predict(X)
```