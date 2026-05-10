package com.learning.lab.module80;

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Module 80: Computer Vision with OpenCV");
        System.out.println("=".repeat(60));

        opencvOverview();
        imageBasics();
        pixelOperations();
        imageFiltering();
        morphologicalOps();
        edgeDetection();
        featureDetection();
        imageTransforms();
        colorSpaces();
        contours();
        objectDetection();
        deepLearningCV();
        videoProcessing();
        optimization();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Module 80 Lab Complete!");
        System.out.println("=".repeat(60));
    }

    static void opencvOverview() {
        System.out.println("\n--- OpenCV Overview ---");
        System.out.println("OpenCV (Open Computer Vision) is a popular computer vision library");

        System.out.println("\nCore Capabilities:");
        System.out.println("   - Image/video capture and processing");
        System.out.println("   - Feature detection and matching");
        System.out.println("   - Object detection and recognition");
        System.out.println("   - Camera calibration");
        System.out.println("   - Machine learning integration");

        System.out.println("\nJavaCV (Java Binding):");
        System.out.println("   - Wrapper for OpenCV native libraries");
        System.out.println("   - Uses FFmpeg for video processing");
        System.out.println("   - BYTEDECO maintained");

        System.out.println("\nModules:");
        String[] modules = {
            "core: Core data structures",
            "imgproc: Image processing",
            "objdetect: Object detection",
            "video: Video analysis",
            "calib3d: Camera calibration",
            "ml: Machine learning",
            "dnn: Deep neural networks"
        };
        for (String m : modules) {
            System.out.println("   - " + m);
        }

        System.out.println("\nIntegration:");
        System.out.println("   <dependency>");
        System.out.println("       <groupId>org.bytedeco</groupId>");
        System.out.println("       <artifactId>javacv-platform</artifactId>");
        System.out.println("       <version>8.1.0</version>");
        System.out.println("   </dependency>");
    }

    static void imageBasics() {
        System.out.println("\n--- Image Basics ---");
        System.out.println("Understanding digital images");

        System.out.println("\nImage Representation:");
        System.out.println("   - Images as 2D matrices");
        System.out.println("   - Each cell = pixel value");
        System.out.println("   - Row-major order (y, x)");

        System.out.println("\nImage Types:");
        System.out.println("   - Grayscale: 1 channel (0-255)");
        System.out.println("   - RGB: 3 channels (B, G, R)");
        System.out.println("   - RGBA: 4 channels (+ alpha)");
        System.out.println("   - HSV: Hue, Saturation, Value");

        System.out.println("\nCreating Images:");
        System.out.println("   // Create blank image");
        System.out.println("   Mat image = new Mat(480, 640, CV_8UC3, new Scalar(255, 255, 255));");
        System.out.println("   ");
        System.out.println("   // Create from file");
        System.out.println("   Mat image = Imgcodecs.imread(\"photo.jpg\");");
        System.out.println("   ");
        System.out.println("   // Create from byte array");
        System.out.println("   Mat image = Imgcodecs.imdecode(new MatOfByte(bytes), IMREAD_COLOR);");

        System.out.println("\nImage Properties:");
        int width = 1920, height = 1080, channels = 3;
        System.out.println("   Width: " + width);
        System.out.println("   Height: " + height);
        System.out.println("   Channels: " + channels);
        System.out.println("   Total pixels: " + (width * height));
        System.out.println("   Data size: " + (width * height * channels) + " bytes");

        System.out.println("\nSaving Images:");
        System.out.println("   Imgcodecs.imwrite(\"output.png\", image);");
        System.out.println("   Imgcodecs.imwrite(\"output.jpg\", image, compressionParams);");
    }

    static void pixelOperations() {
        System.out.println("\n--- Pixel Operations ---");
        System.out.println("Accessing and modifying individual pixels");

        System.out.println("\nDirect Pixel Access:");
        System.out.println("   // Using Mat.get()");
        System.out.println("   byte[] pixel = new byte[3];");
        System.out.println("   image.get(row, col, pixel);");
        System.out.println("   // pixel[0] = B, pixel[1] = G, pixel[2] = R");
        System.out.println("   ");
        System.out.println("   // Using Mat.put()");
        System.out.println("   byte[] newPixel = {(byte)255, (byte)0, (byte)0};");
        System.out.println("   image.put(row, col, newPixel);");

        System.out.println("\nPixel Iteration:");
        int[][] imageData = {
            {255, 250, 245},
            {240, 235, 230},
            {225, 220, 215}
        };
        System.out.println("   Image data (3x3):");
        for (int[] row : imageData) {
            System.out.print("   ");
            for (int val : row) {
                System.out.printf("%4d ", val);
            }
            System.out.println();
        }

        System.out.println("\nBrightness Adjustment:");
        int brightness = 50;
        int[][] brightened = new int[3][3];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                brightened[r][c] = Math.min(255, imageData[r][c] + brightness);
            }
        }
        System.out.println("   Brightness +50: " + brightened[0][0]);

        System.out.println("\nContrast Adjustment:");
        double contrast = 1.5;
        int[][] contrasted = new int[3][3];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                contrasted[r][c] = (int) Math.min(255, imageData[r][c] * contrast);
            }
        }
        System.out.println("   Contrast *1.5: " + contrasted[0][0]);

        System.out.println("\nEfficient Methods (Core Ops):");
        System.out.println("   Core.add(image, new Scalar(50), image);  // Brightness");
        System.out.println("   Core.multiply(image, new Scalar(1.5), image);  // Contrast");
    }

    static void imageFiltering() {
        System.out.println("\n--- Image Filtering ---");
        System.out.println("Applying filters to images");

        System.out.println("\n1. Blurring (Smoothing):");
        System.out.println("   // Gaussian blur");
        System.out.println("   Imgproc.GaussianBlur(src, dst, new Size(5, 5), 0);");
        int[][] blurred = {
            {252, 251, 250},
            {251, 250, 249},
            {250, 249, 248}
        };
        System.out.println("   After blur: ~" + Arrays.toString(blurred[0]));

        System.out.println("\n2. Median Blur:");
        System.out.println("   Imgproc.medianBlur(src, dst, 5);");
        System.out.println("   - Good for salt-and-pepper noise");

        System.out.println("\n3. Bilateral Filter:");
        System.out.println("   Imgproc.bilateralFilter(src, dst, 9, 75, 75);");
        System.out.println("   - Preserves edges while smoothing");

        System.out.println("\n4. Box Filter:");
        System.out.println("   Imgproc.boxFilter(src, dst, -1, new Size(3, 3));");
        System.out.println("   - Simple averaging");

        System.out.println("\n5. Sharpen:");
        double[][] kernel = {
            {0, -1, 0},
            {-1, 5, -1},
            {0, -1, 0}
        };
        System.out.println("   Sharpen kernel:");
        for (double[] row : kernel) {
            System.out.printf("     [%.1f, %.1f, %.1f]%n", row[0], row[1], row[2]);
        }
        System.out.println("   Mat kernel = new Mat(3, 3, CV_32F, kernelData);");
        System.out.println("   Imgproc.filter2D(src, dst, -1, kernel);");

        System.out.println("\nCustom Filter:");
        System.out.println("   Mat kernel = Imgproc.getGaussianKernel(5, 1.0);");
        System.out.println("   Imgproc.sepFilter2D(src, dst, -1, kernel, kernel);");
    }

    static void morphologicalOps() {
        System.out.println("\n--- Morphological Operations ---");
        System.out.println("Image transformations based on shape");

        System.out.println("\nBasic Operations:");

        System.out.println("\n1. Erosion:");
        System.out.println("   - Shrinks bright regions");
        System.out.println("   - Removes small noise");
        System.out.println("   Imgproc.erode(src, dst, kernel);");
        int[] erosionExample = {254, 253, 252};
        System.out.println("   Result: " + Arrays.toString(erosionExample));

        System.out.println("\n2. Dilation:");
        System.out.println("   - Expands bright regions");
        System.out.println("   - Fills small holes");
        System.out.println("   Imgproc.dilate(src, dst, kernel);");
        int[] dilationExample = {252, 253, 254};
        System.out.println("   Result: " + Arrays.toString(dilationExample));

        System.out.println("\n3. Opening:");
        System.out.println("   - Erosion followed by dilation");
        System.out.println("   - Removes noise, preserves shape");
        System.out.println("   Imgproc.morphologyEx(src, dst, MORPH_OPEN, kernel);");

        System.out.println("\n4. Closing:");
        System.out.println("   - Dilation followed by erosion");
        System.out.println("   - Fills holes, connects regions");
        System.out.println("   Imgproc.morphologyEx(src, dst, MORPH_CLOSE, kernel);");

        System.out.println("\n5. Gradient:");
        System.out.println("   - Difference between dilation and erosion");
        System.out.println("   - Extracts contours");
        System.out.println("   Imgproc.morphologyEx(src, dst, MORPH_GRADIENT, kernel);");

        System.out.println("\nKernel Creation:");
        System.out.println("   Mat kernel = Imgproc.getStructuringElement(");
        System.out.println("       Imgproc.MORPH_RECT, new Size(3, 3));");
        System.out.println("   // Or: MORPH_ELLIPSE, MORPH_CROSS");
    }

    static void edgeDetection() {
        System.out.println("\n--- Edge Detection ---");
        System.out.println("Finding edges in images");

        System.out.println("\n1. Sobel Operator:");
        System.out.println("   // Compute gradients");
        System.out.println("   Imgproc.Sobel(src, gradX, CV_16S, 1, 0, 3);");
        System.out.println("   Imgproc.Sobel(src, gradY, CV_16S, 0, 1, 3);");
        System.out.println("   Core.convertScaleAbs(gradX, gradX);");
        System.out.println("   Core.addWeighted(gradX, 0.5, gradY, 0.5, 0, dst);");

        double[] sobelExample = {0.5, 0.3, 0.8, 0.6, 0.2, 0.9, 0.7, 0.4, 0.1};
        System.out.println("   Gradient magnitude: " + String.format("%.2f", sobelExample[0]));

        System.out.println("\n2. Laplacian:");
        System.out.println("   // Second derivative");
        System.out.println("   Imgproc.Laplacian(src, dst, CV_16S, 3);");
        System.out.println("   - Detects fine details");
        System.out.println("   - Sensitive to noise");

        System.out.println("\n3. Canny Edge Detector:");
        System.out.println("   // Multi-stage algorithm");
        System.out.println("   Imgproc.Canny(src, edges, 50, 150);");
        System.out.println("   - Threshold 1: 50, Threshold 2: 150");
        System.out.println("   - Finds strong and weak edges");
        System.out.println("   - Links weak edges to strong ones");

        String[] cannySteps = {
            "1. Noise reduction (Gaussian)",
            "2. Gradient computation",
            "3. Non-maximum suppression",
            "4. Double threshold",
            "5. Edge tracking by hysteresis"
        };
        System.out.println("   Steps:");
        for (String step : cannySteps) {
            System.out.println("     " + step);
        }

        System.out.println("\n4. Prewitt Operator:");
        System.out.println("   // Similar to Sobel, no weighting");
        System.out.println("   - Horizontal: [-1,0,1]");
        System.out.println("   - Vertical: [1;0;1]");
    }

    static void featureDetection() {
        System.out.println("\n--- Feature Detection ---");
        System.out.println("Detecting key points in images");

        System.out.println("\nFeature Types:");

        System.out.println("\n1. Harris Corner Detection:");
        System.out.println("   // Corner detection");
        System.out.println("   Imgproc.cornerHarris(src, dst, 2, 3, 0.04);");
        System.out.println("   - Finds corners and interest points");
        int[] harrisExample = {120, 185, 220, 150};
        System.out.println("   Corner scores: " + Arrays.toString(harrisExample));

        System.out.println("\n2. SIFT (Scale-Invariant Feature Transform):");
        System.out.println("   MatOfKeyPoint keypoints = new MatOfKeyPoint();");
        System.out.println("   SIFT.create().detect(src, keypoints);");
        System.out.println("   - Scale invariant");
        System.out.println("   - Rotation invariant");
        System.out.println("   - 128-dimensional descriptors");

        System.out.println("\n3. ORB (Oriented FAST and Rotated BRIEF):");
        System.out.println("   ORB.create(1000).detectAndCompute(src, mask, keypoints, descriptors);");
        System.out.println("   - Fast and efficient");
        System.out.println("   - Binary descriptors");
        System.out.println("   - Good for real-time");

        System.out.println("\n4. FAST (Features from Accelerated Segment Test):");
        System.out.println("   FastFeatureDetector.create().detect(src, keypoints);");
        System.out.println("   - Very fast corner detection");

        System.out.println("\n5. BRIEF (Binary Robust Independent Elementary Features):");
        System.out.println("   BriefDescriptorExtractor.create().compute(src, keypoints, descriptors);");
        System.out.println("   - Binary descriptors");
        System.out.println("   - Fast matching");

        System.out.println("\nFeature Matching:");
        System.out.println("   DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);");
        System.out.println("   matcher.match(descriptors1, descriptors2, matches);");
    }

    static void imageTransforms() {
        System.out.println("\n--- Image Transforms ---");
        System.out.println("Geometric transformations of images");

        System.out.println("\n1. Resize:");
        System.out.println("   // Scale to 50%");
        System.out.println("   Imgproc.resize(src, dst, new Size(), 0.5, 0.5, INTER_AREA);");
        int[] resizeInput = {1920, 1080};
        int[] resizeOutput = {960, 540};
        System.out.println("   Input: " + resizeInput[0] + "x" + resizeInput[1]);
        System.out.println("   Output: " + resizeOutput[0] + "x" + resizeOutput[1]);

        System.out.println("\n2. Rotate:");
        System.out.println("   Mat rotMat = Imgproc.getRotationMatrix2D(center, angle, 1.0);");
        System.out.println("   Imgproc.warpAffine(src, dst, rotMat, src.size());");
        System.out.println("   - 90, 180, 270 degrees");

        System.out.println("\n3. Affine Transform:");
        System.out.println("   // Parallel lines preserved");
        System.out.println("   Mat M = Imgproc.getAffineTransform(srcPoints, dstPoints);");
        System.out.println("   Imgproc.warpAffine(src, dst, M, new Size(width, height));");

        System.out.println("\n4. Perspective Transform:");
        System.out.println("   // Any quadrilateral to any quadrilateral");
        System.out.println("   Mat M = Imgproc.getPerspectiveTransform(srcPoints, dstPoints);");
        System.out.println("   Imgproc.warpPerspective(src, dst, M, new Size(width, height));");

        System.out.println("\n5. Crop:");
        System.out.println("   Rect roi = new Rect(x, y, width, height);");
        System.out.println("   Mat cropped = new Mat(image, roi);");

        System.out.println("\n6. Translation:");
        System.out.println("   Mat M = new Mat(2, 3, CV_64F, new double[]{1, 0, dx, 0, 1, dy});");
        System.out.println("   Imgproc.warpAffine(src, dst, M, src.size());");
    }

    static void colorSpaces() {
        System.out.println("\n--- Color Spaces ---");
        System.out.println("Working with different color representations");

        System.out.println("\nCommon Color Spaces:");

        System.out.println("\n1. RGB (Red, Green, Blue):");
        System.out.println("   - Additive color");
        System.out.println("   - Screen display");
        int[] rgb = {255, 128, 64};
        System.out.println("   Example: R=" + rgb[0] + ", G=" + rgb[1] + ", B=" + rgb[2]);

        System.out.println("\n2. HSV (Hue, Saturation, Value):");
        System.out.println("   // Convert to HSV");
        System.out.println("   Imgproc.cvtColor(src, hsv, COLOR_BGR2HSV);");
        double[] hsv = {30.0, 0.8, 1.0};
        System.out.println("   H=" + hsv[0] + ", S=" + hsv[1] + ", V=" + hsv[2]);
        System.out.println("   - Hue: 0-180 (color)");
        System.out.println("   - Saturation: 0-255 (intensity)");
        System.out.println("   - Value: 0-255 (brightness)");

        System.out.println("\n3. Grayscale:");
        System.out.println("   Imgproc.cvtColor(src, gray, COLOR_BGR2GRAY);");
        int gray = (int)(0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);
        System.out.println("   Grayscale value: " + gray);

        System.out.println("\n4. LAB (CIELAB):");
        System.out.println("   - Perceptually uniform");
        System.out.println("   - Device-independent");
        System.out.println("   Imgproc.cvtColor(src, lab, COLOR_BGR2Lab);");

        System.out.println("\nColor-Based Operations:");
        System.out.println("   // Threshold in HSV for green");
        System.out.println("   Scalar lowerGreen = new Scalar(25, 50, 50);");
        System.out.println("   Scalar upperGreen = new Scalar(85, 255, 255);");
        System.out.println("   Core.inRange(hsv, lowerGreen, upperGreen, mask);");
    }

    static void contours() {
        System.out.println("\n--- Contours ---");
        System.out.println("Finding and analyzing shapes in images");

        System.out.println("\nContour Detection:");
        System.out.println("   List<MatOfPoint> contours = new ArrayList<>();");
        System.out.println("   Mat hierarchy = new Mat();");
        System.out.println("   Imgproc.findContours(binary, contours, hierarchy,");
        System.out.println("       Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);");

        System.out.println("\nContour Properties:");
        System.out.println("   // Area");
        System.out.println("   double area = Imgproc.contourArea(contour);");
        System.out.println("   ");
        System.out.println("   // Perimeter");
        System.out.println("   double perimeter = Imgproc.arcLength(contour, true);");
        System.out.println("   ");
        System.out.println("   // Bounding box");
        System.out.println("   Rect bounding = Imgproc.boundingRect(contour);");
        System.out.println("   ");
        System.out.println("   // Minimum enclosing circle");
        System.out.println("   Point center = new Point();");
        System.out.println("   float[] radius = new float[1];");
        System.out.println("   Imgproc.minEnclosingCircle(contour, center, radius);");

        System.out.println("\nContour Approximation:");
        System.out.println("   MatOfPoint2f approx = new MatOfPoint2f();");
        System.out.println("   double epsilon = 0.02 * perimeter;");
        System.out.println("   Imgproc.approxPolyDP(contour, approx, epsilon, true);");
        System.out.println("   // Reduce points while preserving shape");

        System.out.println("\nShape Detection:");
        String[] shapes = {"Triangle: 3 points", "Rectangle: 4 points (convex)",
                         "Circle: high points + circularity check", "Polygon: multiple points"};
        for (String shape : shapes) {
            System.out.println("   - " + shape);
        }

        System.out.println("\nConvex Hull:");
        System.out.println("   MatOfPoint hull = new MatOfPoint();");
        System.out.println("   Imgproc.convexHull(contour, hull);");
    }

    static void objectDetection() {
        System.out.println("\n--- Object Detection ---");
        System.out.println("Detecting objects in images");

        System.out.println("\nDetection Methods:");

        System.out.println("\n1. Haar Cascade Classifier:");
        System.out.println("   CascadeClassifier faceDetector = new CascadeClassifier(\"haarcascade_frontalface.xml\");");
        System.out.println("   MatOfRect faces = new MatOfRect();");
        System.out.println("   faceDetector.detectMultiScale(image, faces);");
        System.out.println("   - Pre-trained models available");
        System.out.println("   - Face, eye, car detection");

        System.out.println("\n2. Template Matching:");
        System.out.println("   Imgproc.matchTemplate(image, template, result, TM_CCOEFF_NORMED);");
        System.out.println("   Core.MinMaxLoc resultLoc = Core.minMaxLoc(result);");
        System.out.println("   - Find exact matches");
        System.out.println("   - Simple but limited");

        System.out.println("\n3. Color-Based Detection:");
        System.out.println("   // Convert to HSV and threshold");
        System.out.println("   Core.inRange(hsv, lower, upper, mask);");
        System.out.println("   // Find contours in mask");
        System.out.println("   - Good for colored objects");

        System.out.println("\n4. Motion Detection:");
        System.out.println("   Core.absdiff(frame1, frame2, diff);");
        System.out.println("   Imgproc.threshold(diff, thresh, 25, 255, THRESH_BINARY);");
        System.out.println("   - Frame difference method");
        System.out.println("   - Background subtraction");

        System.out.println("\n5. Deep Learning Detection:");
        System.out.println("   // Using DNN module");
        System.out.println("   Net net = Dnn.readNetFromCaffe(prototxt, weights);");
        System.out.println("   Dnn.blobFromImage(image, blob);");
        System.out.println("   net.setInput(blob);");
        System.out.println("   Mat detections = net.forward();");
        System.out.println("   - YOLO, SSD, Faster R-CNN");
    }

    static void deepLearningCV() {
        System.out.println("\n--- Deep Learning for CV ---");
        System.out.println("Using neural networks for computer vision");

        System.out.println("\nDNN Module (OpenCV):");
        System.out.println("   Dnn.Net network = Dnn.readNet(\"model.onnx\");");
        System.out.println("   Dnn.blobFromImage(img, blob, 1.0, size);");
        System.out.println("   network.setInput(blob);");
        System.out.println("   Mat output = network.forward();");

        System.out.println("\nPre-trained Models:");

        System.out.println("\n1. Image Classification:");
        System.out.println("   - ResNet, VGG, MobileNet");
        System.out.println("   - ImageNet trained");
        System.out.println("   - 1000 classes");

        System.out.println("\n2. Object Detection:");
        System.out.println("   - YOLO (You Only Look Once)");
        System.out.println("   - SSD (Single Shot Detection)");
        System.out.println("   - Faster R-CNN");
        System.out.println("   YOLO detected: [person: 0.95, car: 0.87, bicycle: 0.72]");

        System.out.println("\n3. Semantic Segmentation:");
        System.out.println("   - Fully Convolutional Networks");
        System.out.println("   - U-Net, Mask R-CNN");
        System.out.println("   - Pixel-level classification");

        System.out.println("\n4. Face Recognition:");
        System.out.println("   - FaceNet, DeepFace");
        System.out.println("   - Embedding-based verification");

        System.out.println("\n5. Pose Estimation:");
        System.out.println("   - OpenPose");
        System.out.println("   - Detect body keypoints");

        System.out.println("\nModel Formats:");
        System.out.println("   - ONNX: Interchange format");
        System.out.println("   - Caffe, TensorFlow, PyTorch");

        System.out.println("\nDL4J Integration:");
        System.out.println("   // Using DeepLearning4j");
        System.out.println("   ComputationGraph model = ModelSerializer.restoreModel(file);");
        System.out.println("   INDArray output = model.output(input);");
    }

    static void videoProcessing() {
        System.out.println("\n--- Video Processing ---");
        System.out.println("Working with video streams and files");

        System.out.println("\nVideo Capture:");
        System.out.println("   VideoCapture capture = new VideoCapture(0);  // Camera");
        System.out.println("   // Or from file:");
        System.out.println("   VideoCapture capture = new VideoCapture(\"video.mp4\");");
        System.out.println("   ");
        System.out.println("   // Properties");
        System.out.println("   double fps = capture.get(CAP_PROP_FPS);");
        System.out.println("   int width = (int)capture.get(CAP_PROP_FRAME_WIDTH);");
        System.out.println("   int height = (int)capture.get(CAP_PROP_FRAME_HEIGHT);");

        System.out.println("\nReading Frames:");
        System.out.println("   Mat frame = new Mat();");
        System.out.println("   while (capture.read(frame)) {");
        System.out.println("       // Process frame");
        System.out.println("       processFrame(frame);");
        System.out.println("       if (waitKey(30) >= 0) break;");
        System.out.println("   }");
        System.out.println("   capture.release();");

        System.out.println("\nVideo Writer:");
        System.out.println("   VideoWriter writer = new VideoWriter(\"output.mp4\",");
        System.out.println("       VideoWriter.fourcc('M','J','P','G'), fps, new Size(width, height));");
        System.out.println("   writer.write(frame);");
        System.out.println("   writer.release();");

        System.out.println("\nFrame Operations:");
        System.out.println("   // Resize");
        System.out.println("   Imgproc.resize(frame, resized, new Size(640, 480));");
        System.out.println("   ");
        System.out.println("   // Convert to grayscale");
        System.out.println("   Imgproc.cvtColor(frame, gray, COLOR_BGR2GRAY);");
        System.out.println("   ");
        System.out.println("   // Apply blur");
        System.out.println("   Imgproc.GaussianBlur(frame, blurred, new Size(5, 5), 0);");

        System.out.println("\nMotion Tracking:");
        System.out.println("   // Optical flow");
        System.out.println("   Video.calcOpticalFlowFarneback(prev, next, flow);");
        System.out.println("   - Dense optical flow");
    }

    static void optimization() {
        System.out.println("\n--- Performance Optimization ---");
        System.out.println("Optimizing computer vision applications");

        System.out.println("\nCode Optimization:");

        System.out.println("\n1. Use Native Operations:");
        System.out.println("   // Instead of pixel loops");
        System.out.println("   Core.add(src1, src2, dst);");
        System.out.println("   // Use optimized OpenCV functions");

        System.out.println("\n2. GPU Acceleration:");
        System.out.println("   // Use CUDA when available");
        System.out.println("   GpuMat gpuImg = new GpuMat();");
        System.out.println("   gpuImg.upload(image);");
        System.out.println("   GpuMat gpuResult = new GpuMat();");
        System.out.println("   gpuImg.cvtColor(gpuResult, COLOR_BGR2GRAY);");
        System.out.println("   gpuResult.download(result);");

        System.out.println("\n3. ROI (Region of Interest):");
        System.out.println("   // Process only relevant area");
        System.out.println("   Rect roi = new Rect(x, y, w, h);");
        System.out.println("   Mat region = new Mat(image, roi);");
        System.out.println("   process(region);");

        System.out.println("\n4. Multi-threading:");
        System.out.println("   ExecutorService executor = Executors.newFixedThreadPool(4);");
        System.out.println("   // Process frames in parallel");

        System.out.println("\nMemory Optimization:");
        System.out.println("   // Don't allocate in loop");
        System.out.println("   Mat reuse = new Mat();");
        System.out.println("   for (...) {");
        System.out.println("       result.copyTo(reuse);");
        System.out.println("   }");
        System.out.println("   // Release when done");
        System.out.println("   mat.release();");

        System.out.println("\nAlgorithm Optimization:");
        System.out.println("   // Use fast approximations");
        System.out.println("   Imgproc.resize(src, dst, size, 0, 0, INTER_NEAREST);");
        System.out.println("   // Reduce image size first");
        System.out.println("   // Process at lower resolution, refine later");
    }
}