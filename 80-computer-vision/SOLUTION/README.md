# Computer Vision - Solution

## Overview

This module provides comprehensive examples for computer vision using JavaCV (OpenCV bindings). It covers image loading, image processing, feature detection, object detection, color detection, video capture, and morphological operations.

## Dependencies

```xml
<dependency>
    <groupId>org.bytedeco</groupId>
    <artifactId>javacv-platform</artifactId>
    <version>1.5.10</version>
</dependency>
<dependency>
    <groupId>org.bytedeco</groupId>
    <artifactId>opencv-platform</artifactId>
    <version>1.5.10</version>
</dependency>
```

## Key Concepts

### 1. Image Loading

The `ImageLoadingExample` class demonstrates:
- Image loading from file
- Grayscale conversion
- Image dimension retrieval

### 2. Image Processing

The `ImageProcessingExample` class covers:
- Image resizing
- Image rotation
- Gaussian blur
- Median blur
- Edge detection (Canny)
- Thresholding (binary and Otsu)
- Brightness and contrast adjustment

### 3. Feature Detection

The `FeatureDetectionExample` class provides:
- Harris corner detection
- SIFT feature detection
- ORB feature detection
- Contour detection and drawing

### 4. Object Detection

The `ObjectDetectionExample` class implements:
- Face detection using Haar cascades
- Eye detection
- Rectangle drawing for detected objects

### 5. Color Detection

The `ColorDetectionExample` class demonstrates:
- Color range detection
- HSV color space conversion
- Red, blue, green color detection

### 6. Video Capture

The `VideoCaptureExample` class provides:
- Camera opening
- Video file reading
- Frame properties retrieval

### 7. Video Writer

The `VideoWriterExample` class covers:
- Video writer creation
- Frame writing

### 8. Morphological Operations

The `MorphologicalOperationsExample` class implements:
- Erosion
- Dilation
- Opening
- Closing

## Classes Overview

| Class | Description |
|-------|-------------|
| `ImageLoadingExample` | Image loading and conversion |
| `ImageProcessingExample` | Image transformation operations |
| `FeatureDetectionExample` | Feature and corner detection |
| `ObjectDetectionExample` | Face and eye detection |
| `ColorDetectionExample` | Color-based detection |
| `VideoCaptureExample` | Video camera/file handling |
| `VideoWriterExample` | Video writing |
| `MorphologicalOperationsExample` | Morphological image operations |

## Running Tests

```bash
cd 80-computer-vision
mvn test -Dtest=Test
```

## Examples

### Image Loading

```java
Mat image = loadImage("path/to/image.jpg");
int width = getImageWidth(image);
int height = getImageHeight(image);
```

### Edge Detection

```java
Mat edges = detectEdges(image);
```

### Face Detection

```java
CascadeClassifier faceDetector = createFaceDetector("haarcascade_frontalface_default.xml");
RectVector faces = detectFaces(faceDetector, image);
drawFaceRectangles(image, faces);
```

### Color Detection

```java
Mat hsv = convertToHSV(image);
Mat redMask = detectRedColor(hsv);
```

## Best Practices

1. **Memory Management**: Release resources after use
2. **Image Formats**: Use supported image formats (JPEG, PNG)
3. **Camera Access**: Handle camera availability gracefully
4. **Performance**: Process frames in separate threads for video
5. **Cascade Files**: Use proper cascade files for detection

## Further Reading

- [JavaCV Documentation](https://github.com/bytedeco/javacv)
- [OpenCV Java Tutorial](https://docs.opencv.org/4.5.0/d2/d77/tutorial_table_of_content_java.html)
- [OpenCV API Reference](https://docs.opencv.org/4.5.0/)