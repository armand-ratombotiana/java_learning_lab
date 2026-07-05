package com.learning.lab21;

/**
 * Demonstrates record patterns (preview in Java 19/20, finalized in Java 21).
 */
public class RecordPatternsExample {

    public static void showRecordPatterns() {
        System.out.println("=== Record Patterns ===");

        Object obj1 = new Point(5, 10);
        Object obj2 = new Line(new Point(0, 0), new Point(3, 4));

        printCoordinates(obj1);
        printCoordinates(obj2);
    }

    static void printCoordinates(Object obj) {
        if (obj instanceof Point(int x, int y)) {
            System.out.println("Point at (" + x + ", " + y + ")");
        } else if (obj instanceof Line(Point(int x1, int y1), Point(int x2, int y2))) {
            double dist = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            System.out.println("Line from (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 
                + "), length: " + String.format("%.2f", dist));
        }
    }
}

record Point(int x, int y) {}
record Line(Point start, Point end) {}
