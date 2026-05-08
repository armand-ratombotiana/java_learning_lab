# Deep Dive: Computer Vision in Java

## Table of Contents
1. [Introduction](#introduction)
2. [JavaCV/OpenCV Basics](#opencv)
3. [Image Processing](#processing)
4. [Object Detection](#object-detection)
5. [Image Classification](#classification)
6. [OCR with Tesseract](#ocr)
7. [Video Processing](#video)
8. [Deep Learning Integration](#deep-learning)

---

## 1. Introduction

Computer vision enables machines to interpret visual information. In Java, several libraries provide computer vision capabilities:

- **JavaCV**: Java binding for OpenCV
- **Deep Java Library (DJL)**: AWS's deep learning framework
- **Tess4J**: Java wrapper for Tesseract OCR
- **BoofCV**: Pure Java computer vision library

---

## 2. JavaCV/OpenCV Basics

### Setup with Maven

```xml
<dependency>
    <groupId>org.bytedeco</groupId>
    <artifactId>javacv-platform</artifactId>
    <version>1.5.9</version>
</dependency>
```

### Loading and Displaying Images

```java
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;

public class BasicImageProcessing {
    
    public static void main(String[] args) throws Exception {
        // Load image
        Mat image = imread("image.jpg");
        
        if (image.empty()) {
            System.out.println("Failed to load image");
            return;
        }
        
        System.out.println("Image loaded: " + image.rows() + "x" + image.cols());
        
        // Display image
        imshow("Image", image);
        waitKey(0);
        
        // Save image
        imwrite("output.jpg", image);
    }
}
```

---

## 3. Image Processing

### 3.1 Color Conversion

```java
import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

public class ColorConversion {
    
    public static Mat toGrayscale(Mat colorImage) {
        Mat gray = new Mat();
        cvtColor(colorImage, gray, COLOR_BGR2GRAY);
        return gray;
    }
    
    public static Mat toHSV(Mat bgrImage) {
        Mat hsv = new Mat();
        cvtColor(bgrImage, hsv, COLOR_BGR2HSV);
        return hsv;
    }
    
    public static Mat binarize(Mat grayImage) {
        Mat binary = new Mat();
        threshold(grayImage, binary, 127, 255, THRESH_BINARY);
        return binary;
    }
}
```

### 3.2 Image Transformations

```java
public class Transformations {
    
    public static Mat rotate(Mat image, double angle) {
        Point2f center = new Point2f(image.cols() / 2.0, image.rows() / 2.0);
        Mat rotationMatrix = getRotationMatrix2D(center, angle, 1.0);
        
        Mat rotated = new Mat();
        warpAffine(image, rotated, rotationMatrix, image.size());
        
        return rotated;
    }
    
    public static Mat resize(Mat image, int width, int height) {
        Mat resized = new Mat();
        resize(image, resized, new Size(width, height));
        return resized;
    }
    
    public static Mat blur(Mat image) {
        Mat blurred = new Mat();
        GaussianBlur(image, blurred, new Size(5, 5), 0);
        return blurred;
    }
}
```

### 3.3 Edge Detection

```java
public class EdgeDetection {
    
    public static Mat canny(Mat grayImage) {
        Mat edges = new Mat();
        Canny(grayImage, edges, 50, 150);
        return edges;
    }
    
    public static Mat sobel(Mat grayImage) {
        Mat gradX = new Mat();
        Mat gradY = new Mat();
        
        Sobel(grayImage, gradX, CV_16S, 1, 0, 3);
        Sobel(grayImage, gradY, CV_16S, 0, 1, 3);
        
        Mat grad = new Mat();
        addWeighted(gradX, 0.5, gradY, 0.5, 0, grad);
        
        return grad;
    }
}
```

---

## 4. Object Detection

### 4.1 Haar Cascades

```java
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.Java2DFrameConverter;

public class FaceDetection {
    
    private CascadeClassifier faceCascade;
    
    public FaceDetection() throws Exception {
        faceCascade = new CascadeClassifier(
            "data/haarcascade_frontalface_default.xml"
        );
    }
    
    public Rect[] detectFaces(Mat image) {
        Mat gray = toGrayscale(image);
        
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(gray, faces);
        
        return faces.toArray();
    }
    
    public void drawFaces(Mat image, Rect[] faces) {
        for (Rect face : faces) {
            rectangle(image, face, new Scalar(0, 255, 0, 0), 2);
        }
    }
}
```

### 4.2 YOLO Object Detection

```java
// Using DJL for YOLO
import ai.djl.*;
import ai.djl.modality.cv.*;
import ai.djl.modality.cv.output.*;

public class YOLODetection {
    
    public static void main(String[] args) throws Exception {
        // Load YOLO model
        Model model = Model.newInstance("yolov5s");
        model.load(Paths.get("yolov5s.pt"));
        
        // Create predictor
        Translator<Image, DetectedObjects> translator = 
            new YoloTranslator();
        Predictor<Image, DetectedObjects> predictor = 
            model.newPredictor(translator);
        
        // Detect objects
        Image image = ImageFactory.getInstance().fromFile(
            Paths.get("image.jpg")
        );
        
        DetectedObjects result = predictor.predict(image);
        
        // Print results
        for (DetectedObjects.DetectedObject obj : result.items()) {
            System.out.println(obj.getClassName() + ": " + 
                obj.getProbability());
        }
    }
}
```

---

## 5. Image Classification

### 5.1 Using DJL

```java
import ai.djl.*;
import ai.djl.modality.cv.*;
import ai.djl.translate.*;

public class ImageClassification {
    
    public static void main(String[] args) throws Exception {
        // Load pre-trained model (ResNet50)
        Model model = Model.newInstance("resnet50");
        model.load(Paths.get("resnet50-0.1.0-symbol.json"));
        
        // Create image input
        Image image = ImageFactory.getInstance().fromFile(
            Paths.get("image.jpg")
        );
        
        // Create translator
        Translator<Image, Classifications> translator = 
            ImageClassificationTranslator.builder()
                .optTopK(5)
                .build();
        
        // Predict
        Predictor<Image, Classifications> predictor = 
            model.newPredictor(translator);
        
        Classifications result = predictor.predict(image);
        
        // Print top 5 predictions
        result.items().forEach(item -> 
            System.out.println(item.getClassName() + ": " + 
                item.getProbability())
        );
    }
}
```

---

## 6. OCR with Tesseract

### 6.1 Basic OCR

```java
import net.sourceforge.tess4j.*;
import java.io.File;

public class OCRExample {
    
    public static void main(String[] args) throws Exception {
        // Create Tesseract instance
        Tesseract tesseract = new Tesseract();
        
        // Set data path
        tesseract.setDatapath("tessdata");
        tesseract.setLanguage("eng");
        
        // Recognize text
        String text = tesseract.doOCR(new File("image.png"));
        
        System.out.println("Recognized text:");
        System.out.println(text);
    }
}
```

### 6.2 Advanced OCR with Regions

```java
public class AdvancedOCR {
    
    public static String extractRegion(
            File imageFile, 
            int x, int y, int width, int height) throws Exception {
        
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("tessdata");
        
        // Set ROI (Region of Interest)
        BufferedImage image = ImageIO.read(imageFile);
        BufferedImage region = image.getSubimage(x, y, width, height);
        
        return tesseract.doOCR(region);
    }
    
    public static void extractWithConfidence(File imageFile) 
            throws Exception {
        
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("tessdata");
        
        // Get with confidence
        Result result = tesseract.doOCR(imageFile);
        
        for (TextLine line : result.getLines()) {
            System.out.println(line.getText() + " [" + 
                line.getConfidence() + "%]");
        }
    }
}
```

---

## 7. Video Processing

### 7.1 Video Capture

```java
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;

public class VideoCapture {
    
    public static void main(String[] args) throws Exception {
        // Open webcam
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();
        
        // Create window
        CanvasFrame canvas = new CanvasFrame("Webcam");
        
        Frame frame;
        while ((frame = grabber.grabFrame()) != null) {
            canvas.showImage(frame);
            
            // Press 'q' to quit
            if (canvas.isVisible() == false) {
                break;
            }
            Thread.sleep(50);
        }
        
        grabber.stop();
        canvas.dispose();
    }
}
```

### 7.2 Video Recording

```java
public class VideoRecorder {
    
    public static void recordVideo(
            String outputFile, 
            int width, int height) throws Exception {
        
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.setImageWidth(width);
        grabber.setImageHeight(height);
        grabber.start();
        
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
            outputFile, width, height
        );
        recorder.setVideoCodec(AV_CODEC_ID_H264);
        recorder.start();
        
        Frame frame;
        while ((frame = grabber.grabFrame()) != null) {
            recorder.record(frame);
            
            // Stop after 60 seconds
            // Check time and break
        }
        
        recorder.stop();
        recorder.release();
        grabber.stop();
    }
}
```

---

## 8. Deep Learning Integration

### 8.1 Using DJL for Custom Models

```java
import ai.djl.*;
import ai.djl.modality.cv.*;
import ai.djl.nn.*;
import ai.djl.training.*;

public class CustomModelExample {
    
    public static void main(String[] args) throws Exception {
        // Create model
        Model model = Model.newInstance("custom-model");
        
        // Define network
        Block block = new SequentialBlock()
            .add(Conv2d.Builder()
                .setFilters(16)
                .setKernelShape(new Shape(3, 3))
                .build())
            .add(Activation::relu)
            .add(Pool2dAggregatorBuilder.pool2D(
                new Shape(2, 2), new Shape(2, 2)));
        
        model.setBlock(block);
        
        // Train
        DefaultTrainingConfig config = new DefaultTrainingConfig(
            new SoftmaxCrossEntropy("softmax", new Shape(10))
        );
        
        Trainer trainer = model.newTrainer(config);
        // Training loop...
    }
}
```

---

## Summary

Computer vision in Java:

| Library | Use Case |
|---------|----------|
| JavaCV | OpenCV bindings, image processing |
| DJL | Deep learning, pre-trained models |
| Tess4J | OCR text recognition |
| BoofCV | Pure Java vision algorithms |

Key capabilities: Image processing, object detection, classification, OCR

---

*Continue to QUIZZES.md and EDGE_CASES.md.*