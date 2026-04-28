package com.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for polymorphism with Shape hierarchy.
 * Tests different shape implementations and abstract methods.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
@DisplayName("Polymorphism and Shape Tests")
class ShapeTest {

    private Circle circle;
    private Rectangle rectangle;

    @BeforeEach
    void setUp() {
        circle = new Circle(5.0);
        rectangle = new Rectangle(4.0, 6.0);
    }

    @Test
    @DisplayName("Should create circle with radius")
    void testCircleConstructor() {
        assertThat(circle.getRadius()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Should calculate circle area correctly")
    void testCircleArea() {
        double expectedArea = Math.PI * 5.0 * 5.0;
        assertThat(circle.calculateArea()).isCloseTo(expectedArea, offset(0.01));
    }

    @Test
    @DisplayName("Should calculate circle perimeter correctly")
    void testCirclePerimeter() {
        double expectedPerimeter = 2 * Math.PI * 5.0;
        assertThat(circle.calculatePerimeter()).isCloseTo(expectedPerimeter, offset(0.01));
    }

    @Test
    @DisplayName("Should set circle radius")
    void testSetCircleRadius() {
        circle.setRadius(10.0);
        assertThat(circle.getRadius()).isEqualTo(10.0);
    }

    @Test
    @DisplayName("Should not set negative radius")
    void testNegativeRadius() {
        circle.setRadius(-5.0);
        assertThat(circle.getRadius()).isEqualTo(5.0); // Should remain unchanged
    }

    @Test
    @DisplayName("Should get circle shape name")
    void testCircleShapeName() {
        assertThat(circle.getShapeName()).isEqualTo("Circle");
    }

    @Test
    @DisplayName("Should create rectangle with dimensions")
    void testRectangleConstructor() {
        assertThat(rectangle.getWidth()).isEqualTo(4.0);
        assertThat(rectangle.getHeight()).isEqualTo(6.0);
    }

    @Test
    @DisplayName("Should calculate rectangle area correctly")
    void testRectangleArea() {
        double expectedArea = 4.0 * 6.0;
        assertThat(rectangle.calculateArea()).isEqualTo(expectedArea);
    }

    @Test
    @DisplayName("Should calculate rectangle perimeter correctly")
    void testRectanglePerimeter() {
        double expectedPerimeter = 2 * (4.0 + 6.0);
        assertThat(rectangle.calculatePerimeter()).isEqualTo(expectedPerimeter);
    }

    @Test
    @DisplayName("Should get rectangle shape name")
    void testRectangleShapeName() {
        assertThat(rectangle.getShapeName()).isEqualTo("Rectangle");
    }

    @Test
    @DisplayName("Different shapes should have different areas")
    void testPolymorphismalAreas() {
        // Both are Shape objects but have different implementations
        Shape shape1 = new Circle(3.0);
        Shape shape2 = new Rectangle(3.0, 3.0);

        assertThat(shape1.calculateArea()).isNotEqualTo(shape2.calculateArea());
    }

    @Test
    @DisplayName("Circle should be instance of Shape")
    void testCircleInheritance() {
        assertThat(circle).isInstanceOf(Shape.class);
    }

    @Test
    @DisplayName("Rectangle should be instance of Shape")
    void testRectangleInheritance() {
        assertThat(rectangle).isInstanceOf(Shape.class);
    }

    @Test
    @DisplayName("Test circle toString")
    void testCircleToString() {
        String str = circle.toString();
        assertThat(str)
            .contains("Circle")
            .contains("5");
    }

    @Test
    @DisplayName("Test rectangle toString")
    void testRectangleToString() {
        String str = rectangle.toString();
        assertThat(str)
            .contains("Rectangle")
            .contains("4")
            .contains("6");
    }
}
