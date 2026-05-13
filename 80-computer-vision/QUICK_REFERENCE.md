# Computer Vision Quick Reference

## JavaCV (OpenCV Java Bindings)
```java
Mat image = imread("photo.jpg");
imshow("Window", image);
waitKey(0);
```

## Image Processing

### Grayscale
```java
Mat gray = new Mat();
cvtColor(image, gray, COLOR_BGR2GRAY);
```

### Gaussian Blur
```java
Mat blurred = new Mat();
GaussianBlur(gray, blurred, new Size(5, 5), 0);
```

### Edge Detection (Canny)
```java
Mat edges = new Mat();
Canny(blurred, edges, 50, 150);
```

### Thresholding
```java
Mat threshold = new Mat();
threshold(gray, threshold, 127, 255, THRESH_BINARY);
```

### Morphological Operations
```java
Mat kernel = getStructuringElement(MORPH_RECT, new Size(3, 3));
morphologyEx(image, result, MORPH_OPEN, kernel);
```

## Object Detection

### Haar Cascade (Face Detection)
```java
CascadeClassifier faceDetector = new CascadeClassifier(
    "haarcascade_frontalface_default.xml");
MatOfRect faceDetections = new MatOfRect();
faceDetector.detectMultiScale(image, faceDetections);
```

### YOLO
```java
Net net = Dnn.readNetFromDarknet("yolov3.cfg", "yolov3.weights");
Mat blob = Dnn.blobFromImage(image, 1/255.0, new Size(416, 416), swapRB);
net.setInput(blob);
List<Mat> outputs = new ArrayList<>();
net.forward(outputs, net.getUnconnectedOutLayersNames());
```

## Image Classification (DJL)
```java
Criteria<Image, Classifications> criteria = Criteria.builder()
    .optApplication(Application.CV.IMAGE_CLASSIFICATION)
    .setTypes(Image.class, Classifications.class)
    .optFilter("dataset", "imagenet")
    .build();
ZooModel<Image, Classifications> model = criteria.loadModel();
Image img = ImageFactory.getInstance().fromUrl("http://image.jpg");
Classifications result = model.predict(img);
```

## OCR (Tesseract/Tess4J)
```java
ITesseract tesseract = new Tesseract();
tesseract.setDatapath("tessdata");
tesseract.setLanguage("eng");
String text = tesseract.doOCR(new File("document.png"));
```

## Video Processing
```java
VideoCapture capture = new VideoCapture(0); // Webcam
Mat frame = new Mat();
while (capture.read(frame)) {
    processFrame(frame);
    imshow("Video", frame);
    if (waitKey(30) >= 0) break;
}
capture.release();
```

## Applications
- Face recognition and verification
- License plate recognition
- Medical imaging analysis
- Autonomous vehicle perception
- Document digitization and OCR
- Augmented reality markers
- Quality inspection in manufacturing