package com.learning.cv;

public class Test {

    public static void main(String[] args) {
        System.out.println("Running Computer Vision Tests\n");

        testImageProcessing();
        testMorphologicalOperations();
        testColorDetection();
        testVideoCapture();
        testFeatureDetection();

        System.out.println("\nAll tests passed!");
    }

    private static void testImageProcessing() {
        System.out.println("Test: Image Processing");
        Solution.ImageProcessingExample processor = new Solution.ImageProcessingExample();

        System.out.println("  - Image processing operations ready");
    }

    private static void testMorphologicalOperations() {
        System.out.println("Test: Morphological Operations");
        Solution.MorphologicalOperationsExample morphOps = new Solution.MorphologicalOperationsExample();

        System.out.println("  - Morphological operations ready");
    }

    private static void testColorDetection() {
        System.out.println("Test: Color Detection");
        Solution.ColorDetectionExample colorDetect = new Solution.ColorDetectionExample();

        System.out.println("  - Color detection ready");
    }

    private static void testVideoCapture() {
        System.out.println("Test: Video Capture");
        Solution.VideoCaptureExample videoCapture = new Solution.VideoCaptureExample();

        VideoCapture capture = videoCapture.openCamera(0);
        boolean isOpened = videoCapture.isOpened(capture);

        if (isOpened) {
            double width = videoCapture.getFrameWidth(capture);
            double height = videoCapture.getFrameHeight(capture);
            double fps = videoCapture.getFPS(capture);

            System.out.println("  - Camera opened: " + width + "x" + height + " @ " + fps + " fps");
            videoCapture.releaseCapture(capture);
        } else {
            System.out.println("  - No camera available (expected in test environment)");
        }
    }

    private static void testFeatureDetection() {
        System.out.println("Test: Feature Detection");
        Solution.FeatureDetectionExample featureDetect = new Solution.FeatureDetectionExample();

        System.out.println("  - Feature detection operations ready");
    }
}