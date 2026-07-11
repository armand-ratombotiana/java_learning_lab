# Mathematical Foundation of PCA

## 📐 The Goal of PCA
Given a dataset $X$ of shape $n \times d$ (where $n$ is the number of samples and $d$ is the number of features/dimensions), we want to find a transformation matrix $W$ that projects $X$ into a lower-dimensional space $k$ ($k < d$), such that the variance of the projected data is maximized.

## 🧮 Step-by-Step Derivation

### 1. Standardization
PCA is extremely sensitive to scale. We must center the data by subtracting the mean of each column so that the dataset has a mean of 0.
$$ X_{centered} = X - \mu $$

### 2. The Covariance Matrix
We need to understand how each feature varies with every other feature. We compute the $d \times d$ covariance matrix $\Sigma$:
$$ \Sigma = \frac{1}{n-1} X_{centered}^T X_{centered} $$

- The diagonal elements represent the variance of individual features.
- The off-diagonal elements represent the covariance between two features.

### 3. Eigen Decomposition
We decompose the covariance matrix into its **Eigenvectors** and **Eigenvalues**.
$$ \Sigma v = \lambda v $$
Where:
- $v$ is an eigenvector (a direction in the feature space).
- $\lambda$ is the corresponding eigenvalue (a scalar representing the magnitude of variance in that direction).

Because $\Sigma$ is a symmetric matrix, its eigenvectors are orthogonal (perpendicular) to each other. These eigenvectors are the **Principal Components**.

### 4. Sorting and Selection
We sort the eigenvectors in descending order based on their eigenvalues. 
- The eigenvector with the highest eigenvalue is the 1st Principal Component (the direction of maximum variance).
- We select the top $k$ eigenvectors to form a projection matrix $W$ of shape $d \times k$.

### 5. Projection
Finally, we project the original centered data $X_{centered}$ onto the new $k$-dimensional subspace:
$$ X_{reduced} = X_{centered} W $$

$X_{reduced}$ now has shape $n \times k$. We have successfully reduced the dimensionality from $d$ to $k$ while mathematically guaranteeing we retained the maximum possible variance.