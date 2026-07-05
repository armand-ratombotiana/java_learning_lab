package com.learning.lab24;

/**
 * Demonstrates deconstruction patterns with records (nested pattern matching).
 */
public class DeconstructionPatternExample {

    public static void showDeconstruction() {
        System.out.println("=== Deconstruction Patterns ===");

        Object[] data = {
            new Circle2(new Point2(0, 0), 5),
            new Rectangle2(new Point2(1, 1), new Point2(4, 5)),
            "Not a shape"
        };

        for (Object obj : data) {
            if (obj instanceof Circle2(Point2(int x, int y), int r)) {
                System.out.println("Circle at (" + x + "," + y + ") radius=" + r);
            } else if (obj instanceof Rectangle2(Point2(int x1, int y1), Point2(int x2, int y2))) {
                int width = Math.abs(x2 - x1);
                int height = Math.abs(y2 - y1);
                System.out.println("Rectangle " + width + "x" + height + " at (" + x1 + "," + y1 + ")");
            } else {
                System.out.println("Unknown: " + obj);
            }
        }
    }
}

record Point2(int x, int y) {}
record Circle2(Point2 center, int radius) {}
record Rectangle2(Point2 topLeft, Point2 bottomRight) {}
