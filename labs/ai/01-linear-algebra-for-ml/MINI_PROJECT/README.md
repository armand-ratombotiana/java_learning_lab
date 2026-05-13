# Linear Algebra - Mini Project: Image Compression with SVD

## Project Overview
Build an image compression system using Singular Value Decomposition.

## Objectives
1. Compress grayscale images using truncated SVD
2. Analyze quality vs compression ratio
3. Visualize the compression process

## Implementation Steps

### Step 1: Load and Preprocess Image
```python
from PIL import Image
import numpy as np

# Load image and convert to grayscale
img = Image.open('image.jpg').convert('L')
img_array = np.array(img, dtype=float)
```

### Step 2: Apply SVD
```python
U, S, Vt = np.linalg.svd(img_array, full_matrices=False)
```

### Step 3: Compress with Different k Values
```python
def compress_image(U, S, Vt, k):
    """Compress using top k singular values"""
    return U[:, :k] @ np.diag(S[:k]) @ Vt[:k, :]

# Test different compression levels
for k in [5, 10, 20, 50, 100]:
    compressed = compress_image(U, S, Vt, k)
    # Save and compare
```

### Step 4: Calculate Metrics
```python
def calculate_metrics(original, compressed):
    mse = np.mean((original - compressed) ** 2)
    psnr = 20 * np.log10(255 / np.sqrt(mse))
    compression_ratio = original.size / (compressed.shape[0]*k + k + k*compressed.shape[1])
    return mse, psnr, compression_ratio
```

### Step 5: Visualization
- Show original vs compressed images
- Plot singular value spectrum
- Display reconstruction error vs k

## Deliverables
1. Python script with compression functions
2. Comparison visualization
3. Analysis report with metrics