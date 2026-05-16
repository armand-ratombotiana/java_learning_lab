# Dimensionality Reduction - Real World Project

## Production-Ready Image Feature Extraction and Visualization System

### Project Overview
Build a system for extracting features from image datasets, reducing dimensionality for visualization and efficient storage, and enabling downstream ML tasks.

---

## Project Architecture

```
image-feature-system/
├── src/main/java/com/ml/image/
│   ├── ImageFeatureExtractor.java
│   ├── pipeline/
│   │   ├── ImagePreprocessor.java
│   │   ├── FeatureExtractor.java
│   │   └── DimensionalityReducer.java
│   ├── dimred/
│   │   ├── PCAReducer.java
│   │   ├── LDAReducer.java
│   │   └── KernelPCAReducer.java
│   ├── visualization/
│   │   ├── EmbeddingPlotter.java
│   │   └── TSNEVisualizer.java
│   └── api/
│       └── ImageFeatureController.java
└── build.gradle
```

---

## Core Components

### 1. Image Preprocessor

```java
package com.ml.image.pipeline;

import java.awt.image.*;
import java.util.*;

public class ImagePreprocessor {

    public static BufferedImage loadImage(String path) {
        // Implementation using Java ImageIO
        return null;
    }

    public static double[] flattenImage(BufferedImage img, int targetSize) {
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage resized = new BufferedImage(targetSize, targetSize,
            BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g = resized.createGraphics();
        g.drawImage(img, 0, 0, targetSize, targetSize, null);

        double[] pixels = new double[targetSize * targetSize];
        for (int y = 0; y < targetSize; y++) {
            for (int x = 0; x < targetSize; x++) {
                int rgb = resized.getRGB(x, y);
                int gray = (rgb >> 16) & 0xFF;
                pixels[y * targetSize + x] = gray / 255.0;
            }
        }

        return pixels;
    }

    public static double[][] preprocessDataset(List<String> imagePaths, int targetSize) {
        double[][] features = new double[imagePaths.size()][targetSize * targetSize];

        for (int i = 0; i < imagePaths.size(); i++) {
            BufferedImage img = loadImage(imagePaths.get(i));
            features[i] = flattenImage(img, targetSize);
        }

        return features;
    }
}
```

### 2. Dimensionality Reducer Factory

```java
package com.ml.image.pipeline;

import com.ml.dimred.PCA;
import com.ml.dimred.LDA;

public class DimensionalityReducer {

    public enum ReductionType { PCA, LDA, KERNEL_PCA }

    public static double[][] reduce(double[][] features, int[] labels,
                                   ReductionType type, int targetDim) {
        return switch (type) {
            case PCA -> reduceWithPCA(features, targetDim);
            case LDA -> reduceWithLDA(features, labels, targetDim);
            case KERNEL_PCA -> reduceWithKernelPCA(features, targetDim);
        };
    }

    private static double[][] reduceWithPCA(double[][] features, int targetDim) {
        PCA pca = new PCA();
        pca.fit(features);
        return pca.transform(features, targetDim);
    }

    private static double[][] reduceWithLDA(double[][] features, int[] labels, int targetDim) {
        LDA lda = new LDA();
        lda.fit(features, labels);
        return lda.transform(features);
    }

    private static double[][] reduceWithKernelPCA(double[][] features, int targetDim) {
        // Implement kernel PCA
        return features;
    }
}
```

### 3. REST API

```java
package com.ml.image.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/images")
public class ImageFeatureController {

    private final ImageFeatureService service;

    @PostMapping("/extract")
    public FeatureExtractionResponse extract(
            @RequestParam String imagePath,
            @RequestParam(defaultValue = "64") int size,
            @RequestParam(defaultValue = "PCA") String method,
            @RequestParam(defaultValue = "2") int targetDim) {

        double[] features = service.extractFeatures(imagePath, size);
        double[][] reduced = service.reduceDimensions(
            new double[][]{features}, method, targetDim);

        return new FeatureExtractionResponse(reduced[0], features.length);
    }

    @PostMapping("/visualize")
    public VisualizationResponse visualize(
            @RequestBody List<String> imagePaths,
            @RequestParam(defaultValue = "2") int dimensions) {

        double[][] features = service.preprocessAll(imagePaths);
        double[][] embedding = service.reduceDimensions(features, "PCA", dimensions);

        return new VisualizationResponse(embedding, imagePaths.size());
    }
}

record FeatureExtractionResponse(double[] reducedFeatures, int originalDim) {}
record VisualizationResponse(double[][] embedding, int numImages) {}
```

This system provides comprehensive dimensionality reduction for image data with visualization and API capabilities.