package com.learning.cv;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_objdetect.*;
import org.bytedeco.opencv.opencv_videoio.*;

import java.io.File;
import java.util.*;

public class Solution {

    public static class ImageLoadingExample {

        public Mat loadImage(String filePath) {
            return OpenCVFrameConverter.ToMat().convert(
                new Java2DFrameConverter().convert(
                    new File(filePath)
                )
            );
        }

        public Mat loadImageAsGrayscale(String filePath) {
            Mat image = loadImage(filePath);
            Mat gray = new Mat();
            cvtColor(image, gray, COLOR_BGR2GRAY);
            return gray;
        }

        public int getImageWidth(Mat image) {
            return image.cols();
        }

        public int getImageHeight(Mat image) {
            return image.rows();
        }

        public int getImageChannels(Mat image) {
            return image.channels();
        }
    }

    public static class ImageProcessingExample {

        public Mat resizeImage(Mat image, int width, int height) {
            Mat resized = new Mat();
            resize(image, resized, new Size(width, height));
            return resized;
        }

        public Mat rotateImage(Mat image, double angle) {
            Point2f center = new Point2f(image.cols() / 2.0f, image.rows() / 2.0f);
            Mat rotationMatrix = getRotationMatrix2D(center, angle, 1.0);
            Mat rotated = new Mat();
            warpAffine(image, rotated, rotationMatrix, image.size());
            return rotated;
        }

        public Mat applyGaussianBlur(Mat image, int kernelSize) {
            Mat blurred = new Mat();
            GaussianBlur(image, blurred, new Size(kernelSize, kernelSize), 0);
            return blurred;
        }

        public Mat applyMedianBlur(Mat image, int kernelSize) {
            Mat blurred = new Mat();
            medianBlur(image, blurred, kernelSize);
            return blurred;
        }

        public Mat detectEdges(Mat image) {
            Mat gray = new Mat();
            if (image.channels() > 1) {
                cvtColor(image, gray, COLOR_BGR2GRAY);
            } else {
                gray = image;
            }

            Mat edges = new Mat();
            Canny(gray, edges, 50, 150);
            return edges;
        }

        public Mat applyThreshold(Mat grayImage, double threshold) {
            Mat binary = new Mat();
            threshold(grayImage, binary, threshold, 255, THRESH_BINARY);
            return binary;
        }

        public Mat applyOtsuThreshold(Mat grayImage) {
            Mat binary = new Mat();
            threshold(grayImage, binary, 0, 255, THRESH_BINARY + THRESH_OTSU);
            return binary;
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
    }

    public static class FeatureDetectionExample {

        public Mat detectHarrisCorners(Mat grayImage) {
            Mat corners = new Mat();
            Mat dst = new Mat();
            cornerHarris(grayImage, dst, 2, 3, 0.04);
            normalize(dst, corners, 0, 255, NORM_MINMAX, CV_8UC);
            return corners;
        }

        public void detectSIFT(Mat image) {
            SIFT sift = SIFT.create();
            System.out.println("SIFT detector created");
        }

        public void detectORB(Mat image) {
            ORB orb = ORB.create();
            System.out.println("ORB detector created");
        }

        public Mat detectContours(Mat binaryImage) {
            Mat contours = new Mat();
            Mat hierarchy = new Mat();
            findContours(binaryImage, contours, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
            return contours;
        }

        public void drawContours(Mat image, Mat contours) {
            drawContours(image, contours, -1, new Scalar(0, 255, 0, 0), 2);
        }
    }

    public static class ObjectDetectionExample {

        public CascadeClassifier createFaceDetector(String cascadeFile) {
            return new CascadeClassifier(cascadeFile);
        }

        public RectVector detectFaces(CascadeClassifier faceDetector, Mat image) {
            RectVector faces = new RectVector();
            faceDetector.detectMultiScale(image, faces);
            return faces;
        }

        public void drawFaceRectangles(Mat image, RectVector faces) {
            for (int i = 0; i < faces.size(); i++) {
                Rect face = faces.get(i);
                rectangle(image,
                    new Point(face.x(), face.y()),
                    new Point(face.x() + face.width(), face.y() + face.height()),
                    new Scalar(0, 255, 0, 0), 2);
            }
        }

        public CascadeClassifier createEyeDetector(String cascadeFile) {
            return new CascadeClassifier(cascadeFile);
        }

        public RectVector detectEyes(CascadeClassifier eyeDetector, Mat image) {
            RectVector eyes = new RectVector();
            eyeDetector.detectMultiScale(image, eyes);
            return eyes;
        }
    }

    public static class ColorDetectionExample {

        public Mat detectColorRange(Mat image, Scalar lower, Scalar upper) {
            Mat mask = new Mat();
            inRange(image, lower, upper, mask);
            return mask;
        }

        public Mat detectRedColor(Mat image) {
            Scalar lowerRed1 = new Scalar(0, 100, 100, 0);
            Scalar upperRed1 = new Scalar(10, 255, 255, 0);
            Scalar lowerRed2 = new Scalar(160, 100, 100, 0);
            Scalar upperRed2 = new Scalar(180, 255, 255, 0);

            Mat mask1 = new Mat();
            Mat mask2 = new Mat();
            inRange(image, lowerRed1, upperRed1, mask1);
            inRange(image, lowerRed2, upperRed2, mask2);

            Mat result = new Mat();
            bitwiseOr(mask1, mask2, result);
            return result;
        }

        public Mat detectBlueColor(Mat image) {
            Scalar lowerBlue = new Scalar(100, 50, 50, 0);
            Scalar upperBlue = new Scalar(140, 255, 255, 0);
            return detectColorRange(image, lowerBlue, upperBlue);
        }

        public Mat detectGreenColor(Mat image) {
            Scalar lowerGreen = new Scalar(40, 50, 50, 0);
            Scalar upperGreen = new Scalar(80, 255, 255, 0);
            return detectColorRange(image, lowerGreen, upperGreen);
        }

        public Mat convertToHSV(Mat image) {
            Mat hsv = new Mat();
            cvtColor(image, hsv, COLOR_BGR2HSV);
            return hsv;
        }
    }

    public static class VideoCaptureExample {

        public VideoCapture openCamera(int cameraIndex) {
            VideoCapture capture = new VideoCapture(cameraIndex);
            return capture;
        }

        public VideoCapture openVideoFile(String filePath) {
            VideoCapture capture = new VideoCapture(filePath);
            return capture;
        }

        public boolean isOpened(VideoCapture capture) {
            return capture.isOpened();
        }

        public Mat readFrame(VideoCapture capture) {
            Mat frame = new Mat();
            capture.read(frame);
            return frame;
        }

        public void releaseCapture(VideoCapture capture) {
            capture.release();
        }

        public double getFrameWidth(VideoCapture capture) {
            return capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        }

        public double getFrameHeight(VideoCapture capture) {
            return capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        }

        public double getFPS(VideoCapture capture) {
            return capture.get(Videoio.CAP_PROP_FPS);
        }
    }

    public static class VideoWriterExample {

        public VideoWriter createVideoWriter(String outputPath, int fps, int width, int height) {
            VideoWriter writer = new VideoWriter(
                outputPath,
                VideoWriter.fourcc('M', 'J', 'P', 'G'),
                fps,
                new Size(width, height)
            );
            return writer;
        }

        public void writeFrame(VideoWriter writer, Mat frame) {
            writer.write(frame);
        }

        public void releaseWriter(VideoWriter writer) {
            writer.release();
        }
    }

    public static class MorphologicalOperationsExample {

        public Mat erode(Mat image, int iterations) {
            Mat result = new Mat();
            erode(image, result, new Mat(), new Point(-1, -1), iterations);
            return result;
        }

        public Mat dilate(Mat image, int iterations) {
            Mat result = new Mat();
            dilate(image, result, new Mat(), new Point(-1, -1), iterations);
            return result;
        }

        public Mat open(Mat image) {
            Mat result = new Mat();
            Mat kernel = getStructuringElement(MORPH_RECT, new Size(5, 5));
            morphologyEx(image, result, MORPH_OPEN, kernel);
            return result;
        }

        public Mat close(Mat image) {
            Mat result = new Mat();
            Mat kernel = getStructuringElement(MORPH_RECT, new Size(5, 5));
            morphologyEx(image, result, MORPH_CLOSE, kernel);
            return result;
        }
    }

    public static void main(String[] args) {
        System.out.println("Computer Vision Solutions");
        System.out.println("=========================");

        ImageLoadingExample imageLoader = new ImageLoadingExample();
        System.out.println("Image loading examples available");

        ImageProcessingExample imageProc = new ImageProcessingExample();
        System.out.println("Image processing examples ready");

        FeatureDetectionExample featureDetect = new FeatureDetectionExample();
        System.out.println("Feature detection examples ready");

        ObjectDetectionExample objectDetect = new ObjectDetectionExample();
        System.out.println("Object detection examples ready");

        ColorDetectionExample colorDetect = new ColorDetectionExample();
        System.out.println("Color detection examples ready");

        VideoCaptureExample videoCapture = new VideoCaptureExample();
        System.out.println("Video capture examples ready");

        VideoWriterExample videoWriter = new VideoWriterExample();
        System.out.println("Video writer examples ready");

        MorphologicalOperationsExample morphOps = new MorphologicalOperationsExample();
        System.out.println("Morphological operations ready");
    }
}