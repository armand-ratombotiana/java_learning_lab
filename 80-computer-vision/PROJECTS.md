# Computer Vision Projects - Module 80

This module covers computer vision concepts using Java with OpenCV, DeepLearning4j, and related libraries.

## Mini-Projects per Concept

### 1. Image Loading and Display (2 hours)

#### Overview
Learn to load, display, and save images using OpenCV (JavaCV) and understand basic image properties.

#### Project Structure
```
image-loading/
├── pom.xml
├── src/main/java/com/learning/cv/
│   ├── ImageLoadingLab.java
│   └── ImageUtils.java
└── src/main/resources/images/
    └── sample.jpg
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>image-loading</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <javacv.version>8.1.0</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv-platform</artifactId>
            <version>${javacv.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### ImageLoadingLab.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgcodecs::*;
import org.bytedeco.opencv.opencv_highgui.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_highgui.*;

public class ImageLoadingLab {
    
    public static void main(String[] args) {
        String imagePath = "src/main/resources/images/sample.jpg";
        
        Mat image = imread(imagePath);
        
        if (image.empty()) {
            System.out.println("Error: Could not load image");
            return;
        }
        
        System.out.println("Image loaded successfully");
        System.out.println("Width: " + image.cols());
        System.out.println("Height: " + image.rows());
        System.out.println("Channels: " + image.channels());
        System.out.println("Depth: " + image.depth());
        System.out.println("Type: " + image.type());
        
        namedWindow("Sample Image", WINDOW_NORMAL);
        imshow("Sample Image", image);
        
        waitKey(0);
        destroyWindow("Sample Image");
        
        String outputPath = "src/main/resources/images/output.jpg";
        imwrite(outputPath, image);
        System.out.println("Image saved to: " + outputPath);
    }
}
```

#### ImageUtils.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

public class ImageUtils {
    
    public static Mat loadImage(String path) {
        Mat image = imread(path);
        if (image.empty()) {
            throw new RuntimeException("Failed to load image: " + path);
        }
        return image;
    }
    
    public static boolean saveImage(Mat image, String path) {
        return imwrite(path, image);
    }
    
    public static Mat resizeImage(Mat image, int width, int height) {
        Mat resized = new Mat();
        org.bytedeco.opencv.global.opencv_imgproc.resize(
            image, resized, 
            new Size(width, height)
        );
        return resized;
    }
    
    public static Mat cropImage(Mat image, int x, int y, int width, int height) {
        return image.apply(new Rect(x, y, width, height));
    }
    
    public static void printImageInfo(Mat image, String name) {
        System.out.println("=== " + name + " ===");
        System.out.println("Size: " + image.cols() + "x" + image.rows());
        System.out.println("Channels: " + image.channels());
        System.out.println("Type: " + image.type());
    }
}
```

#### Build and Run
```bash
cd image-loading
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.cv.ImageLoadingLab"
```

---

### 2. Image Preprocessing (2 hours)

#### Overview
Learn image preprocessing techniques: grayscale conversion, smoothing, sharpening, and morphological operations.

#### Project Structure
```
image-preprocessing/
├── pom.xml
└── src/main/java/com/learning/cv/
    ├── ImagePreprocessingLab.java
    └── Preprocessor.java
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>image-preprocessing</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <javacv.version>8.1.0</javacv.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv-platform</artifactId>
            <version>${javacv.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### ImagePreprocessingLab.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class ImagePreprocessingLab {
    
    public static void main(String[] args) {
        Preprocessor preprocessor = new Preprocessor();
        
        String imagePath = "src/main/resources/images/sample.jpg";
        Mat image = preprocessor.loadImage(imagePath);
        
        Mat gray = preprocessor.toGrayscale(image);
        System.out.println("Converted to grayscale");
        
        Mat blurred = preprocessor.gaussianBlur(image, 5);
        System.out.println("Applied Gaussian blur");
        
        Mat median = preprocessor.medianBlur(image, 5);
        System.out.println("Applied median blur");
        
        Mat bilateral = preprocessor.bilateralFilter(image, 9, 75, 75);
        System.out.println("Applied bilateral filter");
        
        Mat sharpened = preprocessor.sharpen(image);
        System.out.println("Applied sharpening");
        
        Mat normalized = preprocessor.normalize(image);
        System.out.println("Normalized image");
        
        Mat equalized = preprocessor.equalizeHistogram(gray);
        System.out.println("Equalized histogram");
        
        preprocessor.showImages(image, gray, blurred, sharpened);
    }
}
```

#### Preprocessor.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class Preprocessor {
    
    public Mat loadImage(String path) {
        return org.bytedeco.opencv.opencv_imgcodecs.imread(path);
    }
    
    public Mat toGrayscale(Mat image) {
        Mat gray = new Mat();
        if (image.channels() == 3) {
            cvtColor(image, gray, COLOR_BGR2GRAY);
        } else {
            gray = image.clone();
        }
        return gray;
    }
    
    public Mat gaussianBlur(Mat image, int kernelSize) {
        Mat blurred = new Mat();
        GaussianBlur(image, blurred, new Size(kernelSize, kernelSize), 0);
        return blurred;
    }
    
    public Mat medianBlur(Mat image, int kernelSize) {
        Mat blurred = new Mat();
        medianBlur(image, blurred, kernelSize);
        return blurred;
    }
    
    public Mat bilateralFilter(Mat image, int d, double sigmaColor, double sigmaSpace) {
        Mat filtered = new Mat();
        bilateralFilter(image, filtered, d, sigmaColor, sigmaSpace);
        return filtered;
    }
    
    public Mat sharpen(Mat image) {
        Mat gray = toGrayscale(image);
        Mat sharpened = new Mat();
        
        Mat kernel = new Mat(3, 3, CV_32F);
        float[] data = {
            0, -1, 0,
            -1, 5, -1,
            0, -1, 0
        };
        kernel.put(0, 0, data);
        
        filter2D(gray, sharpened, -1, kernel);
        return sharpened;
    }
    
    public Mat normalize(Mat image) {
        Mat normalized = new Mat();
        normalize(image, normalized, 0, 255, NORM_MINMAX, CV_8U);
        return normalized;
    }
    
    public Mat equalizeHistogram(Mat grayImage) {
        Mat equalized = new Mat();
        equalizeHist(grayImage, equalized);
        return equalized;
    }
    
    public Mat adjustBrightness(Mat image, double alpha, int beta) {
        Mat adjusted = new Mat();
        image.convertTo(adjusted, -1, alpha, beta);
        return adjusted;
    }
    
    public Mat adjustContrast(Mat image, double alpha) {
        Mat adjusted = new Mat();
        image.convertTo(adjusted, -1, alpha, 0);
        return adjusted;
    }
    
    public void showImages(Mat... images) {
        for (int i = 0; i < images.length; i++) {
            org.bytedeco.opencv.opencv_highgui.namedWindow("Image " + i);
            org.bytedeco.opencv.opencv_highgui.imshow("Image " + i, images[i]);
        }
        org.bytedeco.opencv.opencv_highgui.waitKey(0);
    }
}
```

---

### 3. Color Space Conversion (2 hours)

#### Overview
Understand different color spaces (RGB, HSV, LAB, YCrCb) and learn to convert between them for various computer vision tasks.

#### Project Structure
```
color-space/
├── pom.xml
└── src/main/java/com/learning/cv/
    ├── ColorSpaceLab.java
    └── ColorSpaceConverter.java
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>color-space</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <javacv.version>8.1.0</javacv.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv-platform</artifactId>
            <version>${javacv.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### ColorSpaceLab.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class ColorSpaceLab {
    
    public static void main(String[] args) {
        ColorSpaceConverter converter = new ColorSpaceConverter();
        
        String imagePath = "src/main/resources/images/sample.jpg";
        Mat image = converter.loadImage(imagePath);
        
        Mat rgb = converter.toRGB(image);
        System.out.println("RGB channels: " + rgb.channels());
        
        Mat hsv = converter.toHSV(image);
        System.out.println("Converted to HSV");
        
        Mat lab = converter.toLAB(image);
        System.out.println("Converted to LAB");
        
        Mat ycrcb = converter.toYCrCb(image);
        System.out.println("Converted to YCrCb");
        
        Mat[] hsvChannels = converter.splitHSV(hsv);
        Mat h = hsvChannels[0];
        Mat s = hsvChannels[1];
        Mat v = hsvChannels[2];
        
        Mat colorMask = converter.createColorMask(image, new Scalar(100, 150, 50, 0), new Scalar(140, 255, 255, 0));
        System.out.println("Created color mask");
        
        converter.showResult(image, hsv, colorMask);
    }
}
```

#### ColorSpaceConverter.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import java.util.Arrays;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class ColorSpaceConverter {
    
    public Mat loadImage(String path) {
        return org.bytedeco.opencv.opencv_imgcodecs.imread(path);
    }
    
    public Mat toRGB(Mat image) {
        Mat rgb = new Mat();
        if (image.channels() == 1) {
            cvtColor(image, rgb, COLOR_GRAY2BGR);
        } else if (image.channels() == 4) {
            cvtColor(image, rgb, COLOR_BGRA2BGR);
        } else {
            rgb = image.clone();
        }
        return rgb;
    }
    
    public Mat toHSV(Mat image) {
        Mat hsv = new Mat();
        cvtColor(image, hsv, COLOR_BGR2HSV);
        return hsv;
    }
    
    public Mat toLAB(Mat image) {
        Mat lab = new Mat();
        cvtColor(image, lab, COLOR_BGR2LAB);
        return lab;
    }
    
    public Mat toYCrCb(Mat image) {
        Mat ycrcb = new Mat();
        cvtColor(image, ycrcb, COLOR_BGR2YCrCb);
        return ycrcb;
    }
    
    public Mat toGray(Mat image) {
        Mat gray = new Mat();
        cvtColor(image, gray, COLOR_BGR2GRAY);
        return gray;
    }
    
    public Mat[] splitHSV(Mat hsv) {
        Mat[] channels = new Mat[3];
        split(hsv, channels);
        return channels;
    }
    
    public Mat createColorMask(Mat image, Scalar lower, Scalar upper) {
        Mat hsv = toHSV(image);
        Mat mask = new Mat();
        inRange(hsv, lower, upper, mask);
        return mask;
    }
    
    public Mat applyMask(Mat image, Mat mask) {
        Mat result = new Mat();
        core.bitwise_and(image, image, result, mask);
        return result;
    }
    
    public void showResult(Mat... images) {
        for (int i = 0; i < images.length; i++) {
            org.bytedeco.opencv.opencv_highgui.namedWindow("Result " + i);
            org.bytedeco.opencv.opencv_highgui.imshow("Result " + i, images[i]);
        }
        org.bytedeco.opencv.opencv_highgui.waitKey(0);
    }
}
```

---

### 4. Edge Detection (Canny, Sobel) (2 hours)

#### Overview
Learn edge detection algorithms including Canny edge detector, Sobel operator, and Laplacian for feature extraction.

#### Project Structure
```
edge-detection/
├── pom.xml
└── src/main/java/com/learning/cv/
    ├── EdgeDetectionLab.java
    └── EdgeDetector.java
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>edge-detection</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <javacv.version>8.1.0</javacv.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv-platform</artifactId>
            <version>${javacv.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### EdgeDetectionLab.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class EdgeDetectionLab {
    
    public static void main(String[] args) {
        EdgeDetector detector = new EdgeDetector();
        
        String imagePath = "src/main/resources/images/sample.jpg";
        Mat image = detector.loadImage(imagePath);
        Mat gray = detector.toGrayscale(image);
        
        Mat canny = detector.canny(gray, 50, 150);
        System.out.println("Canny edge detection complete");
        
        Mat sobelX = detector.sobelX(gray);
        Mat sobelY = detector.sobelY(gray);
        Mat sobel = detector.sobel(gray);
        System.out.println("Sobel edge detection complete");
        
        Mat laplacian = detector.laplacian(gray);
        System.out.println("Laplacian edge detection complete");
        
        Mat prewitt = detector.prewitt(gray);
        System.out.println("Prewitt edge detection complete");
        
        detector.showImages(image, gray, canny, sobel);
    }
}
```

#### EdgeDetector.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class EdgeDetector {
    
    public Mat loadImage(String path) {
        return org.bytedeco.opencv.opencv_imgcodecs.imread(path);
    }
    
    public Mat toGrayscale(Mat image) {
        Mat gray = new Mat();
        cvtColor(image, gray, COLOR_BGR2GRAY);
        return gray;
    }
    
    public Mat canny(Mat grayImage, double threshold1, double threshold2) {
        Mat edges = new Mat();
        Canny(grayImage, edges, threshold1, threshold2);
        return edges;
    }
    
    public Mat sobelX(Mat grayImage) {
        Mat sobelX = new Mat();
        Sobel(grayImage, sobelX, CV_32F, 1, 0, 3);
        convertScaleAbs(sobelX, sobelX);
        return sobelX;
    }
    
    public Mat sobelY(Mat grayImage) {
        Mat sobelY = new Mat();
        Sobel(grayImage, sobelY, CV_32F, 0, 1, 3);
        convertScaleAbs(sobelY, sobelY);
        return sobelY;
    }
    
    public Mat sobel(Mat grayImage) {
        Mat gradX = sobelX(grayImage);
        Mat gradY = sobelY(grayImage);
        Mat sobel = new Mat();
        core.addWeighted(gradX, 0.5, gradY, 0.5, 0, sobel);
        return sobel;
    }
    
    public Mat laplacian(Mat grayImage) {
        Mat laplacian = new Mat();
        Laplacian(grayImage, laplacian, CV_64F);
        convertScaleAbs(laplacian, laplacian);
        return laplacian;
    }
    
    public Mat prewitt(Mat grayImage) {
        Mat kernelX = new Mat(3, 3, CV_32F);
        float[] kx = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
        kernelX.put(0, 0, kx);
        
        Mat kernelY = new Mat(3, 3, CV_32F);
        float[] ky = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
        kernelY.put(0, 0, ky);
        
        Mat resultX = new Mat();
        Mat resultY = new Mat();
        filter2D(grayImage, resultX, -1, kernelX);
        filter2D(grayImage, resultY, -1, kernelY);
        
        Mat prewitt = new Mat();
        core.addWeighted(resultX, 0.5, resultY, 0.5, 0, prewitt);
        return prewitt;
    }
    
    public void showImages(Mat... images) {
        for (int i = 0; i < images.length; i++) {
            org.bytedeco.opencv.opencv_highgui.namedWindow("Image " + i);
            org.bytedeco.opencv.opencv_highgui.imshow("Image " + i, images[i]);
        }
        org.bytedeco.opencv.opencv_highgui.waitKey(0);
    }
}
```

---

### 5. Contour Detection (2 hours)

#### Overview
Learn contour detection, hierarchy, and shape analysis for object identification and segmentation.

#### Project Structure
```
contour-detection/
├── pom.xml
└── src/main/java/com/learning/cv/
    ├── ContourDetectionLab.java
    └── ContourAnalyzer.java
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>contour-detection</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <javacv.version>8.1.0</javacv.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv-platform</artifactId>
            <version>${javacv.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### ContourDetectionLab.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import java.util.List;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class ContourDetectionLab {
    
    public static void main(String[] args) {
        ContourAnalyzer analyzer = new ContourAnalyzer();
        
        String imagePath = "src/main/resources/images/shapes.jpg";
        Mat image = analyzer.loadImage(imagePath);
        
        List<MatOfPoint> contours = analyzer.findContours(image);
        System.out.println("Found " + contours.size() + " contours");
        
        for (int i = 0; i < contours.size(); i++) {
            double area = analyzer.getContourArea(contours.get(i));
            double perimeter = analyzer.getContourPerimeter(contours.get(i));
            Rect boundingBox = analyzer.getBoundingBox(contours.get(i));
            Point center = analyzer.getCentroid(contours.get(i));
            
            System.out.println("Contour " + i + ": area=" + area + 
                ", perimeter=" + perimeter + 
                ", center=(" + center.x() + "," + center.y() + ")");
        }
        
        Mat output = analyzer.drawContours(image, contours);
        
        analyzer.showImage(output);
    }
}
```

#### ContourAnalyzer.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import java.util.ArrayList;
import java.util.List;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class ContourAnalyzer {
    
    public Mat loadImage(String path) {
        Mat image = org.bytedeco.opencv.opencv_imgcodecs.imread(path);
        if (image.channels() == 3) {
            Mat gray = new Mat();
            cvtColor(image, gray, COLOR_BGR2GRAY);
            Mat edges = new Mat();
            Canny(gray, edges, 50, 150);
            return edges;
        }
        return image;
    }
    
    public List<MatOfPoint> findContours(Mat image) {
        Mat contours = new Mat();
        Mat hierarchy = new Mat();
        List<MatOfPoint> contourList = new ArrayList<>();
        
        int mode = RETR_EXTERNAL;
        int method = CHAIN_APPROX_SIMPLE;
        findContours(image, contours, hierarchy, mode, method);
        
        for (int i = 0; i < contours.rows(); i++) {
            MatOfPoint contour = new MatOfPoint(contours.row(i));
            contourList.add(contour);
        }
        
        return contourList;
    }
    
    public double getContourArea(MatOfPoint contour) {
        return contourArea(contour);
    }
    
    public double getContourPerimeter(MatOfPoint contour) {
        return arcLength(contour, true);
    }
    
    public Rect getBoundingBox(MatOfPoint contour) {
        return boundingRect(contour);
    }
    
    public RotatedRect getBoundingRect(MatOfPoint contour) {
        return minAreaRect(new MatOfPoint2f(contour.toArray()));
    }
    
    public Point getCentroid(MatOfPoint contour) {
        Moments moments = moments(new MatOfPoint2f(contour.toArray()));
        double x = moments.get_m10() / moments.get_m00();
        double y = moments.get_m01() / moments.get_m00();
        return new Point(x, y);
    }
    
    public MatOfPoint2f getConvexHull(MatOfPoint contour) {
        MatOfPoint2f points = new MatOfPoint2f(contour.toArray());
        MatOfPoint2f hull = new MatOfPoint2f();
        convexHull(points, hull);
        return hull;
    }
    
    public Mat drawContours(Mat image, List<MatOfPoint> contours) {
        Mat output = image.clone();
        if (image.channels() == 1) {
            cvtColor(output, output, COLOR_GRAY2BGR);
        }
        
        for (int i = 0; i < contours.size(); i++) {
            Scalar color = new Scalar(0, 255, 0);
            drawContours(output, contours, i, color, 2);
        }
        
        return output;
    }
    
    public void showImage(Mat image) {
        org.bytedeco.opencv.opencv_highgui.namedWindow("Contours");
        org.bytedeco.opencv.opencv_highgui.imshow("Contours", image);
        org.bytedeco.opencv.opencv_highgui.waitKey(0);
    }
}
```

---

### 6. Image Segmentation (2 hours)

#### Overview
Learn image segmentation techniques including thresholding, k-means clustering, and watershed algorithm.

#### Project Structure
```
image-segmentation/
├── pom.xml
└── src/main/java/com/learning/cv/
    ├── ImageSegmentationLab.java
    └── Segmenter.java
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>image-segmentation</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <javacv.version>8.1.0</javacv.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv-platform</artifactId>
            <version>${javacv.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### ImageSegmentationLab.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class ImageSegmentationLab {
    
    public static void main(String[] args) {
        Segmenter segmenter = new Segmenter();
        
        String imagePath = "src/main/resources/images/sample.jpg";
        Mat image = segmenter.loadImage(imagePath);
        
        Mat binary = segmenter.threshold(image, 127);
        System.out.println("Binary thresholding complete");
        
        Mat otsu = segmenter.otsuThreshold(image);
        System.out.println("Otsu thresholding complete");
        
        Mat adaptive = segmenter.adaptiveThreshold(image);
        System.out.println("Adaptive thresholding complete");
        
        Mat[] kMeansResult = segmenter.kMeans(image, 3);
        System.out.println("K-means segmentation complete");
        
        Mat watershed = segmenter.watershed(image);
        System.out.println("Watershed segmentation complete");
        
        segmenter.showImages(image, binary, otsu, kMeansResult[0]);
    }
}
```

#### Segmenter.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import java.util.ArrayList;
import java.util.List;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class Segmenter {
    
    public Mat loadImage(String path) {
        return org.bytedeco.opencv.opencv_imgcodecs.imread(path);
    }
    
    public Mat toGrayscale(Mat image) {
        Mat gray = new Mat();
        if (image.channels() > 1) {
            cvtColor(image, gray, COLOR_BGR2GRAY);
        } else {
            gray = image.clone();
        }
        return gray;
    }
    
    public Mat threshold(Mat image, double thresh) {
        Mat gray = toGrayscale(image);
        Mat binary = new Mat();
        threshold(gray, binary, thresh, 255, THRESH_BINARY);
        return binary;
    }
    
    public Mat otsuThreshold(Mat image) {
        Mat gray = toGrayscale(image);
        Mat otsu = new Mat();
        threshold(gray, otsu, 0, 255, THRESH_BINARY + THRESH_OTSU);
        return otsu;
    }
    
    public Mat adaptiveThreshold(Mat image) {
        Mat gray = toGrayscale(image);
        Mat adaptive = new Mat();
        adaptiveThreshold(gray, adaptive, 255, ADAPTIVE_THRESH_GAUSSIAN_C, 
            THRESH_BINARY, 11, 2);
        return adaptive;
    }
    
    public Mat[] kMeans(Mat image, int k) {
        Mat samples = image.reshape(1, image.rows() * image.cols());
        samples.convertTo(samples, CV_32F);
        
        Mat labels = new Mat();
        Mat centers = new Mat();
        
        TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 100, 0.2);
        
        core.kmeans(samples, k, labels, criteria, 10, KMEANS_PP_CENTERS, centers);
        
        Mat clustered = labels.reshape(0, image.rows());
        clustered.convertTo(clustered, CV_8U);
        
        return new Mat[]{clustered, centers};
    }
    
    public Mat watershed(Mat image) {
        Mat gray = toGrayscale(image);
        Mat binary = new Mat();
        threshold(gray, binary, 0, 255, THRESH_BINARY_INV + THRESH_OTSU);
        
        Mat kernel = getStructuringElement(MORPH_RECT, new Size(3, 3));
        Mat opened = new Mat();
        morphologyEx(binary, opened, MORPH_OPEN, kernel);
        
        Mat distTransform = new Mat();
        distanceTransform(opened, distTransform, DIST_L2, 3);
        
        Mat foregrounde = new Mat();
        threshold(distTransform, foregrounde, 0.7 * core.maxElem(distTransform).get(0), 255, THRESH_BINARY);
        
        Mat markers = new Mat();
        foregrounde.convertTo(markers, CV_32S);
        
       watershed(image, markers);
        
        Mat result = new Mat();
        markers.convertTo(result, CV_8U);
        
        return result;
    }
    
    public void showImages(Mat... images) {
        for (int i = 0; i < images.length; i++) {
            org.bytedeco.opencv.opencv_highgui.namedWindow("Image " + i);
            org.bytedeco.opencv.opencv_highgui.imshow("Image " + i, images[i]);
        }
        org.bytedeco.opencv.opencv_highgui.waitKey(0);
    }
}
```

---

### 7. Object Detection (YOLO, SSD) (3 hours)

#### Overview
Learn deep learning-based object detection using YOLO and SSD models with DeepLearning4j.

#### Project Structure
```
object-detection/
├── pom.xml
├── src/main/java/com/learning/cv/
│   ├── ObjectDetectionLab.java
│   ├── YOLODetector.java
│   └── SSDDetector.java
└── src/main/resources/models/
    └── yolov3.weights
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>object-detection</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <javacv.version>8.1.0</javacv.version>
        <dl4j.version>1.0.0-beta</dl4j.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv-platform</artifactId>
            <version>${javacv.version}</version>
        </dependency>
        <dependency>
            <groupId>org.deeplearning4j</groupId>
            <artifactId>deeplearning4j-core</artifactId>
            <version>${dl4j.version}</version>
        </dependency>
        <dependency>
            <groupId>org.nd4j</groupId>
            <artifactId>nd4j-native-platform</artifactId>
            <version>${dl4j.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### ObjectDetectionLab.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;

public class ObjectDetectionLab {
    
    public static void main(String[] args) {
        String imagePath = "src/main/resources/images/street.jpg";
        
        YOLODetector yolo = new YOLODetector();
        System.out.println("YOLO detector initialized");
        
        Mat image = yolo.loadImage(imagePath);
        
        var detections = yolo.detect(image);
        System.out.println("Detected " + detections.size() + " objects");
        
        for (var det : detections) {
            System.out.println("  - " + det.getLabel() + ": " + 
                String.format("%.2f", det.getConfidence()) + 
                " at (" + det.getX() + "," + det.getY() + ")");
        }
        
        Mat output = yolo.drawDetections(image, detections);
        yolo.showImage(output);
        
        SSDDetector ssd = new SSDDetector();
        var ssdDetections = ssd.detect(image);
        System.out.println("SSD detected " + ssdDetections.size() + " objects");
    }
}
```

#### YOLODetector.java
```java
package com.learning.cv;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_dnn.*;
import java.io.*;
import java.util.*;

public class YOLODetector {
    
    private Net net;
    private List<String> classNames;
    private static final String[] cocoNames = {
        "person", "bicycle", "car", "motorcycle", "airplane", "bus", "train", "truck", "boat",
        "traffic light", "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat",
        "dog", "horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe", "backpack",
        "umbrella", "handbag", "tie", "suitcase", "frisbee", "skis", "snowboard", "sports ball",
        "kite", "baseball bat", "baseball glove", "skateboard", "surfboard", "tennis racket", "bottle",
        "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple", "sandwich",
        "orange", "broccoli", "carrot", "hot dog", "pizza", "donut", "cake", "chair", "couch",
        "potted plant", "bed", "dining table", "toilet", "tv", "laptop", "mouse", "remote", "keyboard",
        "cell phone", "microwave", "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase",
        "scissors", "teddy bear", "hair drier", "toothbrush"
    };
    
    public YOLODetector() {
        classNames = Arrays.asList(cocoNames);
        
        String configPath = "src/main/resources/models/yolov3.cfg";
        String weightsPath = "src/main/resources/models/yolov3.weights";
        
        net = Dnn.readNetFromDarknet(configPath, weightsPath);
        System.out.println("YOLO network loaded");
    }
    
    public Mat loadImage(String path) {
        return org.bytedeco.opencv.opencv_imgcodecs.imread(path);
    }
    
    public List<Detection> detect(Mat image) {
        Mat blob = Dnn.blobFromImage(image, 1.0 / 255.0, new Size(416, 416), 
            new Scalar(0, 0, 0), true, false);
        
        net.setInput(blob);
        
        MatVector outputs = new MatVector();
        net.forward(outputs);
        
        List<Detection> detections = new ArrayList<>();
        
        for (int i = 0; i < outputs.size(); i++) {
            Mat output = outputs.get(i);
            for (int j = 0; j < output.rows(); j++) {
                Mat row = output.row(j);
                float[] data = new float[5 + classNames.size()];
                row.get(0, 0, data);
                
                float confidence = data[4];
                if (confidence > 0.5f) {
                    float maxConf = 0;
                    int maxIdx = 0;
                    for (int k = 0; k < classNames.size(); k++) {
                        if (data[5 + k] > maxConf) {
                            maxConf = data[5 + k];
                            maxIdx = k;
                        }
                    }
                    
                    float conf = confidence * maxConf;
                    if (conf > 0.5f) {
                        float x = data[0] * image.cols();
                        float y = data[1] * image.rows();
                        float w = data[2] * image.cols();
                        float h = data[3] * image.rows();
                        
                        detections.add(new Detection(classNames.get(maxIdx), conf, x, y, w, h));
                    }
                }
            }
        }
        
        detections = nonMaxSuppression(detections, 0.4f);
        
        return detections;
    }
    
    private List<Detection> nonMaxSuppression(List<Detection> detections, float iouThreshold) {
        detections.sort((a, b) -> Float.compare(b.getConfidence(), a.getConfidence()));
        
        List<Detection> result = new ArrayList<>();
        Set<Integer> removed = new HashSet<>();
        
        for (int i = 0; i < detections.size(); i++) {
            if (removed.contains(i)) continue;
            
            Detection current = detections.get(i);
            result.add(current);
            
            for (int j = i + 1; j < detections.size(); j++) {
                if (removed.contains(j)) continue;
                
                Detection other = detections.get(j);
                float iou = computeIOU(current, other);
                if (iou > iouThreshold) {
                    removed.add(j);
                }
            }
        }
        
        return result;
    }
    
    private float computeIOU(Detection a, Detection b) {
        float x1 = Math.max(a.getX(), b.getX());
        float y1 = Math.max(a.getY(), b.getY());
        float x2 = Math.min(a.getX() + a.getWidth(), b.getX() + b.getWidth());
        float y2 = Math.min(a.getY() + a.getHeight(), b.getY() + b.getHeight());
        
        float intersection = Math.max(0, x2 - x1) * Math.max(0, y2 - y1);
        float union = a.getWidth() * a.getHeight() + b.getWidth() * b.getHeight() - intersection;
        
        return intersection / union;
    }
    
    public Mat drawDetections(Mat image, List<Detection> detections) {
        Mat output = image.clone();
        
        for (var det : detections) {
            int x = (int) det.getX();
            int y = (int) det.getY();
            int w = (int) det.getWidth();
            int h = (int) det.getHeight();
            
            org.bytedeco.opencv.opencv_core.rectangle(output, 
                new Point(x, y), new Point(x + w, y + h), 
                new Scalar(0, 255, 0));
            
            String label = det.getLabel() + " " + String.format("%.2f", det.getConfidence());
            org.bytedeco.opencv.opencv_imgproc.putText(output, label, 
                new Point(x, y - 5), 
                org.bytedeco.opencv.opencv_imgproc.FONT_HERSHEY_SIMPLEX, 
                0.5, new Scalar(0, 255, 0));
        }
        
        return output;
    }
    
    public void showImage(Mat image) {
        org.bytedeco.opencv.opencv_highgui.namedWindow("Detections");
        org.bytedeco.opencv.opencv_highgui.imshow("Detections", image);
        org.bytedeco.opencv.opencv_highgui.waitKey(0);
    }
    
    public static class Detection {
        private String label;
        private float confidence;
        private float x, y, width, height;
        
        public Detection(String label, float confidence, float x, float y, float width, float height) {
            this.label = label;
            this.confidence = confidence;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        public String getLabel() { return label; }
        public float getConfidence() { return confidence; }
        public float getX() { return x; }
        public float getY() { return y; }
        public float getWidth() { return width; }
        public float getHeight() { return height; }
    }
}
```

#### SSDDetector.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_dnn.*;
import java.util.*;

public class SSDDetector {
    
    private Net net;
    private List<String> classNames;
    
    public SSDDetector() {
        classNames = Arrays.asList(
            "aeroplane", "bicycle", "bird", "boat", "bottle", "bus", "car", "cat", 
            "chair", "cow", "diningtable", "dog", "horse", "motorbike", "person", 
            "pottedplant", "sheep", "sofa", "train", "tvmonitor"
        );
        
        String prototxt = "src/main/resources/models/mobilenet_ssd.prototxt";
        String caffemodel = "src/main/resources/models/mobilenet_ssd.caffemodel";
        
        net = Dnn.readNetFromCaffe(prototxt, caffemodel);
    }
    
    public List<YOLODetector.Detection> detect(Mat image) {
        Mat blob = Dnn.blobFromImage(image, 0.007843, new Size(300, 300), 
            new Scalar(127.5, 127.5, 127.5), false);
        
        net.setInput(blob);
        Mat detections = net.forward();
        
        List<YOLODetector.Detection> results = new ArrayList<>();
        
        for (int i = 0; i < detections.rows(); i++) {
            float confidence = detections.get(i, 2, 0);
            if (confidence > 0.5) {
                int classId = (int) detections.get(i, 1, 0);
                float x = detections.get(i, 3, 0) * image.cols();
                float y = detections.get(i, 4, 0) * image.rows();
                float w = (detections.get(i, 5, 0) - detections.get(i, 3, 0)) * image.cols();
                float h = (detections.get(i, 6, 0) - detections.get(i, 4, 0)) * image.rows();
                
                results.add(new YOLODetector.Detection(
                    classNames.get(classId), confidence, x, y, w, h));
            }
        }
        
        return results;
    }
}
```

---

### 8. Face Detection (2 hours)

#### Overview
Learn face detection using Haar cascades and DNN modules, with landmark detection.

#### Project Structure
```
face-detection/
├── pom.xml
└── src/main/java/com/learning/cv/
    ├── FaceDetectionLab.java
    └── FaceDetector.java
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>face-detection</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <javacv.version>8.1.0</javacv.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv-platform</artifactId>
            <version>${javacv.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### FaceDetectionLab.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import java.util.*;

public class FaceDetectionLab {
    
    public static void main(String[] args) {
        FaceDetector detector = new FaceDetector();
        
        String imagePath = "src/main/resources/images/faces.jpg";
        Mat image = detector.loadImage(imagePath);
        
        List<Rect> faces = detector.detectHaar(image);
        System.out.println("Haar detected " + faces.size() + " faces");
        
        for (Rect face : faces) {
            System.out.println("  Face: (" + face.x() + "," + face.y() + 
                ") " + face.width() + "x" + face.height());
        }
        
        List<Rect> dnnFaces = detector.detectDNN(image);
        System.out.println("DNN detected " + dnnFaces.size() + " faces");
        
        Mat output = detector.drawFaces(image, faces);
        detector.showImage(output);
    }
}
```

#### FaceDetector.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.*;
import org.bytedeco.opencv.opencv_dnn.*;
import java.util.*;

public class FaceDetector {
    
    private CascadeClassifier haarCascade;
    private Net dnnNet;
    
    public FaceDetector() {
        String cascadePath = "src/main/resources/cascades/haarcascade_frontalface_default.xml";
        haarCascade = new CascadeClassifier(cascadePath);
        
        String modelPath = "src/main/resources/models/face_detection_yunet.onnx";
        dnnNet = Dnn.readNetFromONNX(modelPath);
    }
    
    public Mat loadImage(String path) {
        return org.bytedeco.opencv.opencv_imgcodecs.imread(path);
    }
    
    public List<Rect> detectHaar(Mat image) {
        Mat gray = new Mat();
        org.bytedeco.opencv.opencv_imgproc.cvtColor(image, gray, 
            org.bytedeco.opencv.opencv_imgproc.COLOR_BGR2GRAY);
        
        MatOfRect faces = new MatOfRect();
        haarCascade.detectMultiScale(gray, faces);
        
        return faces.toList();
    }
    
    public List<Rect> detectDNN(Mat image) {
        Mat blob = Dnn.blobFromImage(image, 1.0 / 255.0, new Size(320, 320), 
            new Scalar(0, 0, 0), false, false);
        
        dnnNet.setInput(blob);
        Mat output = dnnNet.forward();
        
        List<Rect> faces = new ArrayList<>();
        
        for (int i = 0; i < output.rows(); i++) {
            float confidence = output.get(i, 0, 2);
            if (confidence > 0.5) {
                float x = output.get(i, 0, 3) * image.cols();
                float y = output.get(i, 0, 4) * image.rows();
                float w = output.get(i, 0, 5) * image.cols();
                float h = output.get(i, 0, 6) * image.rows();
                
                faces.add(new Rect((int)x, (int)y, (int)w, (int)h));
            }
        }
        
        return faces;
    }
    
    public Mat drawFaces(Mat image, List<Rect> faces) {
        Mat output = image.clone();
        
        for (Rect face : faces) {
            org.bytedeco.opencv.opencv_core.rectangle(output, 
                new Point(face.x(), face.y()), 
                new Point(face.x() + face.width(), face.y() + face.height()), 
                new Scalar(0, 255, 0), 2);
        }
        
        return output;
    }
    
    public void showImage(Mat image) {
        org.bytedeco.opencv.opencv_highgui.namedWindow("Faces");
        org.bytedeco.opencv.opencv_highgui.imshow("Faces", image);
        org.bytedeco.opencv.opencv_highgui.waitKey(0);
    }
}
```

---

### 9. Image Classification (2 hours)

#### Overview
Learn image classification using pre-trained deep learning models with DeepLearning4j.

#### Project Structure
```
image-classification/
├── pom.xml
└── src/main/java/com/learning/cv/
    ├── ImageClassificationLab.java
    └── ImageClassifier.java
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>image-classification</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <dl4j.version>1.0.0-beta</dl4j.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.deeplearning4j</groupId>
            <artifactId>deeplearning4j-core</artifactId>
            <version>${dl4j.version}</version>
        </dependency>
        <dependency>
            <groupId>org.deeplearning4j</groupId>
            <artifactId>deeplearning4j-keras</artifactId>
            <version>${dl4j.version}</version>
        </dependency>
        <dependency>
            <groupId>org.nd4j</groupId>
            <artifactId>nd4j-native-platform</artifactId>
            <version>${dl4j.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### ImageClassificationLab.java
```java
package com.learning.cv;

import org.deeplearning4j.nn.modelimport.keras.*;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.graph.*;
import org.nd4j.linalg.api.ndarray.*;
import org.nd4j.linalg.factory.*;

public class ImageClassificationLab {
    
    public static void main(String[] args) throws Exception {
        ImageClassifier classifier = new ImageClassifier();
        
        String imagePath = "src/main/resources/images/cat.jpg";
        INDArray features = classifier.loadImage(imagePath);
        
        var predictions = classifier.predict(features);
        
        System.out.println("Top 5 predictions:");
        for (int i = 0; i < 5; i++) {
            System.out.println("  " + predictions.get(i).getKey() + ": " + 
                String.format("%.4f", predictions.get(i).getValue()));
        }
    }
}
```

#### ImageClassifier.java
```java
package com.learning.cv;

import org.deeplearning4j.nn.modelimport.keras.*;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.graph.*;
import org.nd4j.linalg.api.ndarray.*;
import org.nd4j.linalg.factory.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;

public class ImageClassifier {
    
    private ComputationGraph model;
    private List<String> classLabels;
    
    public ImageClassifier() throws Exception {
        String modelPath = "src/main/resources/models/resnet50.keras";
        
        KerasModelImport kerasModel = new KerasModelImport();
        model = kerasModel.importComputationGraph(modelPath);
        
        classLabels = loadLabels("src/main/resources/models/imagenet_labels.txt");
    }
    
    public INDArray loadImage(String path) throws Exception {
        BufferedImage img = javax.imageio.ImageIO.read(new File(path));
        
        int width = 224;
        int height = 224;
        int channels = 3;
        
        INDArray input = Nd4j.create(1, channels, height, width);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                input.putScalar(0, 0, y, x, r / 255.0);
                input.putScalar(0, 1, y, x, g / 255.0);
                input.putScalar(0, 2, y, x, b / 255.0);
            }
        }
        
        return input;
    }
    
    public List<Map.Entry<String, Float>> predict(INDArray input) {
        INDArray[] outputs = model.output(false, input);
        INDArray probabilities = outputs[0];
        
        INDArray sorted = probabilities.dup();
        int[] args = argsort(sorted);
        
        List<Map.Entry<String, Float>> results = new ArrayList<>();
        
        for (int i = 0; i < Math.min(5, args.length); i++) {
            int idx = args[args.length - 1 - i];
            float prob = probabilities.getFloat(idx);
            results.add(Map.entry(classLabels.get(idx), prob));
        }
        
        return results;
    }
    
    private int[] argsort(INDArray arr) {
        double[] values = new double[(int) arr.length()];
        for (int i = 0; i < values.length; i++) {
            values[i] = arr.getDouble(i);
        }
        
        Integer[] indices = new Integer[values.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        
        Arrays.sort(indices, (a, b) -> Double.compare(values[a], values[b]));
        
        int[] result = new int[indices.length];
        for (int i = 0; i < indices.length; i++) {
            result[i] = indices[i];
        }
        
        return result;
    }
    
    private List<String> loadLabels(String path) throws IOException {
        List<String> labels = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        
        String line;
        while ((line = reader.readLine()) != null) {
            labels.add(line.trim());
        }
        
        reader.close();
        return labels;
    }
}
```

---

### 10. Feature Matching (2 hours)

#### Overview
Learn feature detection and matching using SIFT, ORB, and BRIEF algorithms.

#### Project Structure
```
feature-matching/
├── pom.xml
└── src/main/java/com/learning/cv/
    ├── FeatureMatchingLab.java
    └── FeatureMatcher.java
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>feature-matching</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <javacv.version>8.1.0</javacv.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv-platform</artifactId>
            <version>${javacv.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### FeatureMatchingLab.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;

public class FeatureMatchingLab {
    
    public static void main(String[] args) {
        FeatureMatcher matcher = new FeatureMatcher();
        
        String queryPath = "src/main/resources/images/object.jpg";
        String trainPath = "src/main/resources/images/scene.jpg";
        
        Mat queryImage = matcher.loadImage(queryPath);
        Mat trainImage = matcher.loadImage(trainPath);
        
        var orbMatches = matcher.matchORB(queryImage, trainImage);
        System.out.println("ORB matches found: " + orbMatches.size());
        
        var siftMatches = matcher.matchSIFT(queryImage, trainImage);
        System.out.println("SIFT matches found: " + siftMatches.size());
        
        var briefMatches = matcher.matchBRIEF(queryImage, trainImage);
        System.out.println("BRIEF matches found: " + briefMatches.size());
        
        Mat output = matcher.drawMatches(queryImage, trainImage, orbMatches);
        matcher.showImage(output);
    }
}
```

#### FeatureMatcher.java
```java
package com.learning.cv;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_features2d.*;
import java.util.*;
import static org.bytedeco.opencv.global.opencv_features2d.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class FeatureMatcher {
    
    public Mat loadImage(String path) {
        return org.bytedeco.opencv.opencv_imgcodecs.imread(path);
    }
    
    public List<DMatch> matchORB(Mat queryImage, Mat trainImage) {
        Mat queryKeypoints = new Mat();
        Mat queryDescriptors = new Mat();
        Mat trainKeypoints = new Mat();
        Mat trainDescriptors = new Mat();
        
        ORB detector = ORB.create();
        detector.detectAndCompute(queryImage, new Mat(), queryKeypoints, queryDescriptors);
        detector.detectAndCompute(trainImage, new Mat(), trainKeypoints, trainDescriptors);
        
        BFMatcher bfMatcher = new BFMatcher(NORM_HAMMING, true);
        
        MatOfDMatch matches = new MatOfDMatch();
        bfMatcher.match(queryDescriptors, trainDescriptors, matches);
        
        List<DMatch> goodMatches = new ArrayList<>();
        for (DMatch match : matches.toArray()) {
            if (match.distance() < 50) {
                goodMatches.add(match);
            }
        }
        
        return goodMatches;
    }
    
    public List<DMatch> matchSIFT(Mat queryImage, Mat trainImage) {
        Mat queryKeypoints = new Mat();
        Mat queryDescriptors = new Mat();
        Mat trainKeypoints = new Mat();
        Mat trainDescriptors = new Mat();
        
        SIFT detector = SIFT.create();
        detector.detectAndCompute(queryImage, new Mat(), queryKeypoints, queryDescriptors);
        detector.detectAndCompute(trainImage, new Mat(), trainKeypoints, trainDescriptors);
        
        FlannBasedMatcher flannMatcher = FlannBasedMatcher.create();
        
        MatOfDMatch matches = new MatOfDMatch();
        flannMatcher.match(queryDescriptors, trainDescriptors, matches);
        
        List<DMatch> goodMatches = new ArrayList<>();
        double maxDist = 0;
        
        for (DMatch match : matches.toArray()) {
            maxDist = Math.max(maxDist, match.distance());
        }
        
        for (DMatch match : matches.toArray()) {
            if (match.distance() < 0.7 * maxDist) {
                goodMatches.add(match);
            }
        }
        
        return goodMatches;
    }
    
    public List<DMatch> matchBRIEF(Mat queryImage, Mat trainImage) {
        Mat queryKeypoints = new Mat();
        Mat queryDescriptors = new Mat();
        Mat trainKeypoints = new Mat();
        Mat trainDescriptors = new Mat();
        
        BriefDescriptorExtractor briefExtractor = BriefDescriptorExtractor.create();
        
        Feature2D orb = ORB.create();
        orb.detect(queryImage, queryKeypoints);
        orb.compute(queryImage, queryKeypoints, queryDescriptors);
        orb.detect(trainImage, trainKeypoints);
        orb.compute(trainImage, trainKeypoints, trainDescriptors);
        
        BFMatcher bfMatcher = new BFMatcher(NORM_HAMMING, true);
        
        MatOfDMatch matches = new MatOfDMatch();
        bfMatcher.match(queryDescriptors, trainDescriptors, matches);
        
        return Arrays.asList(matches.toArray());
    }
    
    public Mat drawMatches(Mat queryImage, Mat trainImage, List<DMatch> matches) {
        Mat queryKeypoints = new Mat();
        Mat trainKeypoints = new Mat();
        
        ORB detector = ORB.create();
        detector.detect(queryImage, queryKeypoints);
        detector.detect(trainImage, trainKeypoints);
        
        MatOfKeyPoint queryKps = new MatOfKeyPoint();
        MatOfKeyPoint trainKps = new MatOfKeyPoint();
        
        KeyPoint.convert(Arrays.asList(queryKeypoints.toArray()), queryKps.toArray());
        KeyPoint.convert(Arrays.asList(trainKeypoints.toArray()), trainKps.toArray());
        
        Mat output = new Mat();
        drawMatches(queryImage, queryKps, trainImage, trainKps, 
            new MatOfDMatch(matches.toArray(new DMatch[0])), output);
        
        return output;
    }
    
    public void showImage(Mat image) {
        org.bytedeco.opencv.opencv_highgui.namedWindow("Matches");
        org.bytedeco.opencv.opencv_highgui.imshow("Matches", image);
        org.bytedeco.opencv.opencv_highgui.waitKey(0);
    }
}
```

---

## Real-World Projects

### 1. Traffic Monitoring System

#### Overview
Build a real-time traffic monitoring system that detects and tracks vehicles, counts traffic, and estimates speeds.

#### Project Structure
```
traffic-monitoring/
├── pom.xml
├── src/main/java/com/learning/cv/
│   ├── TrafficMonitoringApplication.java
│   ├── vehicle/
│   │   ├── VehicleDetector.java
│   │   ├── VehicleTracker.java
│   │   └── SpeedEstimator.java
│   ├── camera/
│   │   └── VideoProcessor.java
│   └── stats/
│       └── TrafficStatistics.java
├── src/main/resources/
│   ├── models/
│   │   └── yolov4.weights
│   └── config.yaml
├── Dockerfile
└── docker-compose.yml
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>traffic-monitoring</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <javacv.version>8.1.0</javacv.version>
        <spring.boot.version>3.1.0</spring.boot.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv-platform</artifactId>
            <version>${javacv.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${spring.boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
            <version>${spring.boot.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### TrafficMonitoringApplication.java
```java
package com.learning.cv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrafficMonitoringApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TrafficMonitoringApplication.class, args);
    }
}
```

#### VehicleDetector.java
```java
package com.learning.cv.vehicle;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_dnn.*;
import java.util.*;

public class VehicleDetector {
    
    private Net yoloNet;
    private Map<String, Integer> vehicleClasses;
    
    public VehicleDetector() {
        vehicleClasses = new HashMap<>();
        vehicleClasses.put("car", 2);
        vehicleClasses.put("motorcycle", 3);
        vehicleClasses.put("bus", 5);
        vehicleClasses.put("truck", 7);
        
        String config = "src/main/resources/models/yolov4.cfg";
        String weights = "src/main/resources/models/yolov4.weights";
        
        yoloNet = Dnn.readNetFromDarknet(config, weights);
    }
    
    public List<VehicleDetection> detect(Mat frame) {
        Mat blob = Dnn.blobFromImage(frame, 1.0/255.0, new Size(416, 416), 
            new Scalar(0, 0, 0), true, false);
        
        yoloNet.setInput(blob);
        
        List<VehicleDetection> detections = new ArrayList<>();
        
        // Process detections...
        return detections;
    }
    
    public static class VehicleDetection {
        private String type;
        private Rect boundingBox;
        private float confidence;
        private long trackId;
        
        public VehicleDetection(String type, Rect box, float conf) {
            this.type = type;
            this.boundingBox = box;
            this.confidence = conf;
        }
        
        public String getType() { return type; }
        public Rect getBoundingBox() { return boundingBox; }
        public float getConfidence() { return confidence; }
        public long getTrackId() { return trackId; }
        public void setTrackId(long id) { this.trackId = id; }
    }
}
```

#### TrafficStatistics.java
```java
package com.learning.cv.stats;

import org.springframework.data.redis.core.*;
import java.time.*;
import java.util.concurrent.atomic.*;

public class TrafficStatistics {
    
    private final RedisTemplate<String, Object> redis;
    private final AtomicInteger carsPerHour = new AtomicInteger(0);
    private final AtomicInteger trucksPerHour = new AtomicInteger(0);
    private final Map<String, AtomicInteger> hourlyCounts = new java.util.HashMap<>();
    
    public TrafficStatistics(RedisTemplate<String, Object> redis) {
        this.redis = redis;
        
        for (int i = 0; i < 24; i++) {
            hourlyCounts.put(String.format("hour_%02d", i), new AtomicInteger(0));
        }
    }
    
    public void recordVehicle(String vehicleType) {
        String hourKey = String.format("hour_%02d", LocalTime.now().getHour());
        hourlyCounts.get(hourtyKey).incrementAndGet();
        
        String key = "traffic:" + LocalDate.now() + ":" + hourKey;
        redis.opsForValue().increment(key);
        
        if ("car".equals(vehicleType)) {
            carsPerHour.incrementAndGet();
        } else if ("truck".equals(vehicleType)) {
            trucksPerHour.incrementAndGet();
        }
    }
    
    public int getTotalVehicles() {
        return carsPerHour.get() + trucksPerHour.get();
    }
    
    public Map<String, Integer> getHourlyBreakdown() {
        Map<String, Integer> result = new java.util.HashMap<>();
        hourlyCounts.forEach((k, v) -> result.put(k, v.get()));
        return result;
    }
}
```

#### Dockerfile
```dockerfile
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apk add --no-cache maven

RUN mvn clean package -DskipTests

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "target/traffic-monitoring-1.0.0.jar"]
```

#### docker-compose.yml
```yaml
version: '3.8'

services:
  traffic-monitoring:
    build: .
    ports:
      - "8080:8080"
    volumes:
      - ./videos:/app/videos
    environment:
      - SPRING_REDIS_HOST=redis
      - SPRING_REDIS_PORT=6379
    depends_on:
      - redis

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
```

---

### 2. Face Recognition Attendance

#### Overview
Build a face recognition-based attendance system with database storage and web dashboard.

#### Project Structure
```
face-attendance/
├── pom.xml
├── src/main/java/com/learning/cv/
│   ├── FaceAttendanceApplication.java
│   ├── recognition/
│   │   ├── FaceRecognizer.java
│   │   ├── FaceEmbeddings.java
│   │   └── AttendanceService.java
│   ├── controller/
│   │   └── AttendanceController.java
│   └── entity/
│       └── AttendanceRecord.java
├── src/main/resources/
│   ├── models/
│   │   └── facenet.onnx
│   └── known-faces/
├── Dockerfile
└── docker-compose.yml
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>face-attendance</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <javacv.version>8.1.0</javacv.version>
        <spring.boot.version>3.1.0</spring.boot.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv-platform</artifactId>
            <version>${javacv.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${spring.boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <version>${spring.boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.6.0</version>
        </dependency>
    </dependencies>
</project>
```

#### FaceRecognizer.java
```java
package com.learning.cv.recognition;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_dnn.*;
import org.bytedeco.opencv.opencv_objdetect.*;
import java.util.*;
import java.nd4j.linalg.api.ndarray.*;
import java.nd4j.linalg.factory.*;

public class FaceRecognizer {
    
    private CascadeClassifier faceCascade;
    private Net embeddingNet;
    private Map<String, INDArray> knownEmbeddings;
    private Map<String, String> studentNames;
    
    public FaceRecognizer() {
        String cascadePath = "src/main/resources/cascades/haarcascade_frontalface_default.xml";
        faceCascade = new CascadeClassifier(cascadePath);
        
        String modelPath = "src/main/resources/models/facenet.onnx";
        embeddingNet = Dnn.readNetFromONNX(modelPath);
        
        knownEmbeddings = new HashMap<>();
        studentNames = new HashMap<>();
    }
    
    public void registerFace(String studentId, String name, Mat faceImage) {
        INDArray embedding = extractEmbedding(faceImage);
        knownEmbeddings.put(studentId, embedding);
        studentNames.put(studentId, name);
    }
    
    public String recognize(Mat faceImage) {
        INDArray queryEmbedding = extractEmbedding(faceImage);
        
        double minDistance = Double.MAX_VALUE;
        String bestMatch = null;
        
        for (Map.Entry<String, INDArray> entry : knownEmbeddings.entrySet()) {
            double distance = computeDistance(queryEmbedding, entry.getValue());
            
            if (distance < minDistance && distance < 0.6) {
                minDistance = distance;
                bestMatch = entry.getKey();
            }
        }
        
        return bestMatch;
    }
    
    private INDArray extractEmbedding(Mat face) {
        Mat blob = Dnn.blobFromImage(face, 1.0/255.0, new Size(160, 160), 
            new Scalar(0, 0, 0), true, false);
        
        embeddingNet.setInput(blob);
        
        // Extract embeddings...
        return null;
    }
    
    private double computeDistance(INDArray a, INDArray b) {
        INDArray diff = a.sub(b);
        return diff.norm2Number().doubleValue();
    }
}
```

#### AttendanceRecord.java
```java
package com.learning.cv.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "attendance")
public class AttendanceRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String studentId;
    
    @Column(nullable = false)
    private String studentName;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column
    private String location;
    
    @Column
    private double confidence;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
}
```

---

### 3. Quality Inspection System

#### Overview
Build an automated quality inspection system for manufacturing defects detection.

#### Project Structure
```
quality-inspection/
├── pom.xml
└── src/main/java/com/learning/cv/
    ├── QualityInspectionApplication.java
    ├── inspection/
    │   ├── DefectDetector.java
    │   ├── QualityGrader.java
    │   └── InspectionService.java
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>quality-inspection</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <javacv.version>8.1.0</javacv.version>
        <spring.boot.version>3.1.0</spring.boot.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv-platform</artifactId>
            <version>${javacv.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${spring.boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
            <version>${spring.boot.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### DefectDetector.java
```java
package com.learning.cv.inspection;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_dnn.*;
import java.util.*;

public class DefectDetector {
    
    private Net defectNet;
    
    public DefectDetector() {
        String modelPath = "src/main/resources/models/defect_detection.onnx";
        defectNet = Dnn.readNetFromONNX(modelPath);
    }
    
    public List<Defect> detectDefects(Mat image) {
        Mat blob = Dnn.blobFromImage(image, 1.0/255.0, new Size(512, 512), 
            new Scalar(0, 0, 0), true, false);
        
        defectNet.setInput(blob);
        
        List<Defect> defects = new ArrayList<>();
        
        // Process defect detection...
        return defects;
    }
    
    public QualityGrade grade(QualityGrade.Type type, List<Defect> defects) {
        double score = 100.0;
        
        for (Defect defect : defects) {
            score -= defect.getSeverity() * 10;
        }
        
        QualityGrade.Grade grade;
        if (score >= 95) grade = QualityGrade.Grade.A;
        else if (score >= 85) grade = QualityGrade.Grade.B;
        else if (score >= 70) grade = QualityGrade.Grade.C;
        else grade = QualityGrade.Grade.REJECT;
        
        return new QualityGrade(type, score, grade, defects);
    }
    
    public static class Defect {
        private String type;
        private Rect location;
        private double severity;
        
        public Defect(String type, Rect location, double severity) {
            this.type = type;
            this.location = location;
            this.severity = severity;
        }
        
        public String getType() { return type; }
        public Rect getLocation() { return location; }
        public double getSeverity() { return severity; }
    }
}
```

---

### 4. Autonomous Navigation

#### Overview
Build a computer vision system for autonomous robot navigation using path detection and obstacle avoidance.

#### Project Structure
```
autonomous-navigation/
├── pom.xml
└── src/main/java/com/learning/cv/
    ├── AutonomousNavigationApplication.java
    ├── navigation/
    │   ├── PathDetector.java
    │   ├── ObstacleAvoider.java
    │   └── NavigationController.java
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>autonomous-navigation</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <javacv.version>8.1.0</javacv.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv-platform</artifactId>
            <version>${javacv.version}</version>
        </dependency>
    </dependencies>
</project>
```

---

### 5. Medical Image Analysis

#### Overview
Build a medical image analysis system for X-ray and MRI analysis with disease detection.

#### Project Structure
```
medical-image-analysis/
├── pom.xml
└── src/main/java/com/learning/cv/
    ├── MedicalImageAnalysisApplication.java
    ├── analysis/
    │   ├── MedicalImageAnalyzer.java
    │   ├── Segmentation.java
    │   └── Classifier.java
```

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>medical-image-analysis</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <javacv.version>8.1.0</javacv.version>
        <dl4j.version>1.0.0-beta</dl4j.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.bytedeco</groupId>
            <artifactId>javacv-platform</artifactId>
            <version>${javacv.version}</version>
        </dependency>
        <dependency>
            <groupId>org.deeplearning4j</groupId>
            <artifactId>deeplearning4j-core</artifactId>
            <version>${dl4j.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>3.1.0</version>
        </dependency>
    </dependencies>
</project>
```

#### MedicalImageAnalyzer.java
```java
package com.learning.cv.analysis;

import org.bytedeco.opencv.opencv_core.*;
import org.deeplearning4j.nn.graph.*;
import org.nd4j.linalg.api.ndarray.*;

public class MedicalImageAnalyzer {
    
    private ComputationGraph segmentationModel;
    private ComputationGraph classificationModel;
    
    public MedicalImageAnalyzer() throws Exception {
        segmentationModel = loadModel("src/main/resources/models/segmentation.onnx");
        classificationModel = loadModel("src/main/resources/models/classification.onnx");
    }
    
    public AnalysisResult analyze(Mat medicalImage) {
        Mat preprocessed = preprocessImage(medicalImage);
        
        INDArray segmentationInput = prepareForModel(preprocessed);
        INDArray[] segmentationOutput = segmentationModel.output(false, segmentationInput);
        Mat segmentedMask = convertToMask(segmentationOutput[0]);
        
        INDArray classificationInput = prepareForModel(preprocessed);
        INDArray[] classificationOutput = classificationModel.input(false, classificationInput);
        INDArray probabilities = classificationOutput[0];
        
        return new AnalysisResult(segmentedMask, extractFindings(probabilities));
    }
    
    private Mat preprocessImage(Mat image) {
        Mat normalized = new Mat();
        image.convertTo(normalized, CV_32F, 1.0 / 255.0);
        return normalized;
    }
    
    private AnalysisResult extractFindings(INDArray probabilities) {
        String[] conditions = {
            "normal", "pneumonia", "tuberculosis", "COVID-19", 
            "lung_nodule", "fracture"
        };
        
        Map<String, Double> findings = new HashMap<>();
        for (int i = 0; i < conditions.length; i++) {
            findings.put(conditions[i], probabilities.getDouble(i));
        }
        
        return new AnalysisResult(findings);
    }
    
    public static class AnalysisResult {
        private Mat segmentedMask;
        private Map<String, Double> findings;
        
        public AnalysisResult(Mat mask, Map<String, Double> findings) {
            this.segmentedMask = mask;
            this.findings = findings;
        }
        
        public Map<String, Double> getFindings() { return findings; }
        public Mat getSegmentedMask() { return segmentedMask; }
    }
}
```

---

## Build and Run Instructions

```bash
# Build all mini-projects
cd 80-computer-vision
mvn clean install

# Run a specific project
cd object-detection
mvn exec:java -Dexec.mainClass="com.learning.cv.ObjectDetectionLab"

# Run with Docker
docker-compose up -d
```

## Learning Outcomes

- Image loading and preprocessing with OpenCV (JavaCV)
- Color space conversion and manipulation
- Edge detection using Canny, Sobel, Laplacian
- Contour detection and shape analysis
- Image segmentation techniques
- Object detection with YOLO and SSD
- Face detection with Haar cascades and DNN
- Image classification with DeepLearning4j
- Feature matching with SIFT, ORB, BRIEF
- Real-world application development