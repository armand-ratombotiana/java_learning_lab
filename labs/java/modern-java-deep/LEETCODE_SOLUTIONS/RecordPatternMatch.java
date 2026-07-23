package modernjava;

/**
 * Record patterns and pattern matching (Java 21+).
 * 
 * Features demonstrated:
 * - Record patterns (Java 21): type deconstruction in instanceof and switch
 * - Pattern matching for switch (Java 21): exhaustive, with guards
 * - Pattern matching for instanceof (Java 16+): no cast needed
 * - Nested record patterns
 * 
 * Time: O(1)
 * Space: O(1)
 */
public class RecordPatternMatch {

    sealed interface Shape permits Circle, Rectangle, Triangle {}
    record Circle(double radius) implements Shape {}
    record Rectangle(double width, double height) implements Shape {}
    record Triangle(double base, double height) implements Shape {}

    // Pattern matching for switch (Java 21+)
    static double area(Shape s) {
        return switch (s) {
            case Circle c && c.radius() > 0 -> Math.PI * c.radius() * c.radius();
            case Rectangle r -> r.width() * r.height();
            case Triangle t -> 0.5 * t.base() * t.height();
        };
    }

    // Nested record patterns (Java 21+)
    record Point(int x, int y) {}
    record Line(Point start, Point end) {}

    static boolean isHorizontal(Line line) {
        return switch (line) {
            case Line(Point(int x1, int y1), Point(int x2, int y2)) when y1 == y2 -> true;
            default -> false;
        };
    }

    // Instanceof pattern matching (Java 16+)
    static void printShape(Object obj) {
        if (obj instanceof Circle c) {
            System.out.println("Circle with radius " + c.radius());
        } else if (obj instanceof Rectangle r) {
            System.out.println("Rectangle " + r.width() + "x" + r.height());
        } else {
            System.out.println("Unknown shape");
        }
    }

    public static void main(String[] args) {
        Circle c = new Circle(5);
        Rectangle r = new Rectangle(3, 4);
        Triangle t = new Triangle(4, 3);

        assert area(c) == Math.PI * 25;
        assert area(r) == 12;
        assert area(t) == 6;

        // Instanceof pattern
        printShape(c);
        printShape(r);

        // Record patterns
        Line horizontal = new Line(new Point(0, 0), new Point(10, 0));
        Line vertical = new Line(new Point(0, 0), new Point(0, 10));

        assert isHorizontal(horizontal);
        assert !isHorizontal(vertical);

        System.out.println("All RecordPatternMatch tests passed.");
    }
}