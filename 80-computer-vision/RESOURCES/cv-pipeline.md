# CV Pipeline Diagram

## End-to-End Pipeline

```
┌──────────┐    ┌─────────────┐    ┌───────────────┐    ┌────────────┐
│  Input   │───▶│ Preprocess  │───▶│ Feature Extract│───▶│  Model     │
│  Image   │    │             │    │               │    │  Inference │
└──────────┘    └─────────────┘    └───────────────┘    └────────────┘
                     │                     │                    │
                     ▼                     ▼                    ▼
               ┌─────────┐          ┌───────────┐        ┌──────────┐
               │ Resize  │          │  CNN/ViT  │        │ Class/   │
               │ Normalize│          │  Features  │        │ Detect   │
               │ Augment │          │  HOG/SIFT  │        │ Segment  │
               └─────────┘          └───────────┘        └──────────┘
```

## Preprocessing

| Step | Purpose |
|------|---------|
| Resize | Standard input size |
| Normalize | Scale pixel values |
| Color conversion | Grayscale/RGB/HSV |
| Augmentation | Increase data variety |

## Feature Extraction

### Traditional
- **HOG**: Histogram of Oriented Gradients
- **SIFT/SURF**: Scale-invariant keypoints
- **Haar Cascades**: Edge-based features

### Deep Learning
- **CNN**: Convolutional Neural Networks
- **ViT**: Vision Transformers

## Common Tasks

| Task | Description |
|------|-------------|
| Image Classification | Assign label to entire image |
| Object Detection | Find and locate objects (bbox) |
| Semantic Segmentation | Pixel-level classification |
| Instance Segmentation | Distinguish individual objects |
| Face Recognition | Identify faces |
| Pose Estimation | Detect body keypoints |