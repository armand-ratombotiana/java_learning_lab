# Mini Project: 2D/3D Shape Calculator with Visualization Data

## Implementation

```java
package com.mathacademy.geometry.mini;

import java.util.ArrayList;
import java.util.List;

public class ShapeCalculator {
    
    public interface Shape2D {
        double area();
        double perimeter();
    }
    
    public interface Shape3D {
        double volume();
        double surfaceArea();
    }
    
    public static class Rectangle implements Shape2D {
        double length, width;
        public Rectangle(double l, double w) { length = l; width = w; }
        public double area() { return length * width; }
        public double perimeter() { return 2 * (length + width); }
        public double diagonal() { return Math.sqrt(length*length + width*width); }
    }
    
    public static class Circle implements Shape2D {
        double radius;
        public Circle(double r) { radius = r; }
        public double area() { return Math.PI * radius * radius; }
        public double circumference() { return 2 * Math.PI * radius; }
        public double arcLength(double angle) { return radius * angle; }
        public double sectorArea(double angle) { return 0.5 * radius * radius * angle; }
    }
    
    public static class Triangle implements Shape2D {
        double a, b, c;
        public Triangle(double a, double b, double c) {
            this.a = a; this.b = b; this.c = c;
        }
        public double area() {
            double s = (a + b + c) / 2;
            return Math.sqrt(s * (s-a) * (s-b) * (s-c));
        }
        public double perimeter() { return a + b + c; }
        public double[] angles() {
            double alpha = Math.acos((b*b + c*c - a*a) / (2*b*c));
            double beta = Math.acos((a*a + c*c - b*b) / (2*a*c));
            double gamma = Math.PI - alpha - beta;
            return new double[]{alpha, beta, gamma};
        }
    }
    
    public static class Cuboid implements Shape3D {
        double length, width, height;
        public Cuboid(double l, double w, double h) {
            length = l; width = w; height = h;
        }
        public double volume() { return length * width * height; }
        public double surfaceArea() { return 2*(length*width + length*height + width*height); }
        public double spaceDiagonal() { return Math.sqrt(length*length + width*width + height*height); }
    }
    
    public static class Sphere implements Shape3D {
        double radius;
        public Sphere(double r) { radius = r; }
        public double volume() { return (4.0/3) * Math.PI * radius * radius * radius; }
        public double surfaceArea() { return 4 * Math.PI * radius * radius; }
        public double greatCircleArea() { return Math.PI * radius * radius; }
    }
    
    public static class Cylinder implements Shape3D {
        double radius, height;
        public Cylinder(double r, double h) { radius = r; height = h; }
        public double volume() { return Math.PI * radius * radius * height; }
        public double surfaceArea() { return 2 * Math.PI * radius * (radius + height); }
        public double lateralArea() { return 2 * Math.PI * radius * height; }
    }
    
    public static class Cone implements Shape3D {
        double radius, height;
        public Cone(double r, double h) { radius = r; height = h; }
        public double volume() { return (1.0/3) * Math.PI * radius * radius * height; }
        public double slantHeight() { return Math.sqrt(radius*radius + height*height); }
        public double surfaceArea() { return Math.PI * radius * (radius + slantHeight()); }
    }
    
    public static class Polygon implements Shape2D {
        List<double[]> vertices = new ArrayList<>();
        
        public void addVertex(double x, double y) {
            vertices.add(new double[]{x, y});
        }
        
        public double area() {
            double sum = 0;
            for (int i = 0; i < vertices.size(); i++) {
                double[] a = vertices.get(i);
                double[] b = vertices.get((i + 1) % vertices.size());
                sum += a[0] * b[1] - b[0] * a[1];
            }
            return Math.abs(sum) / 2;
        }
        
        public double perimeter() {
            double sum = 0;
            for (int i = 0; i < vertices.size(); i++) {
                double[] a = vertices.get(i);
                double[] b = vertices.get((i + 1) % vertices.size());
                sum += Math.sqrt(Math.pow(b[0]-a[0], 2) + Math.pow(b[1]-a[1], 2));
            }
            return sum;
        }
        
        public double[] centroid() {
            double cx = 0, cy = 0;
            for (double[] v : vertices) {
                cx += v[0];
                cy += v[1];
            }
            return new double[]{cx / vertices.size(), cy / vertices.size()};
        }
    }
    
    public static class Torus implements Shape3D {
        double majorRadius, minorRadius;
        public Torus(double R, double r) { majorRadius = R; minorRadius = r; }
        public double volume() { return (2 * Math.PI * majorRadius) * (Math.PI * minorRadius * minorRadius); }
        public double surfaceArea() { return (2 * Math.PI * majorRadius) * (2 * Math.PI * minorRadius); }
    }
}

public class ShapeCalculatorTest {
    public static void main(String[] args) {
        System.out.println("SHAPE CALCULATOR TEST");
        System.out.println("=====================");
        
        System.out.println("\n2D Shapes:");
        ShapeCalculator.Rectangle r = new ShapeCalculator.Rectangle(5, 3);
        System.out.println("Rectangle (5x3): Area=" + r.area() + ", Perimeter=" + r.perimeter());
        
        ShapeCalculator.Circle c = new ShapeCalculator.Circle(4);
        System.out.println("Circle (r=4): Area=" + c.area() + ", Circumference=" + c.circumference());
        
        ShapeCalculator.Triangle t = new ShapeCalculator.Triangle(3, 4, 5);
        System.out.println("Triangle (3,4,5): Area=" + t.area() + ", Perimeter=" + t.perimeter());
        
        System.out.println("\n3D Shapes:");
        ShapeCalculator.Cuboid cuboid = new ShapeCalculator.Cuboid(4, 3, 2);
        System.out.println("Cuboid (4x3x2): Volume=" + cuboid.volume() + ", SA=" + cuboid.surfaceArea());
        
        ShapeCalculator.Sphere sphere = new ShapeCalculator.Sphere(3);
        System.out.println("Sphere (r=3): Volume=" + sphere.volume() + ", SA=" + sphere.surfaceArea());
        
        ShapeCalculator.Cylinder cylinder = new ShapeCalculator.Cylinder(2, 5);
        System.out.println("Cylinder (r=2, h=5): Volume=" + cylinder.volume() + ", SA=" + cylinder.surfaceArea());
        
        ShapeCalculator.Cone cone = new ShapeCalculator.Cone(3, 4);
        System.out.println("Cone (r=3, h=4): Volume=" + cone.volume() + ", SA=" + cone.surfaceArea());
        
        ShapeCalculator.Torus torus = new ShapeCalculator.Torus(5, 2);
        System.out.println("Torus (R=5, r=2): Volume=" + torus.volume() + ", SA=" + torus.surfaceArea());
    }
}
```

## Running
```bash
cd labs/math/03-geometry/MINI_PROJECT
javac -d bin *.java
java ShapeCalculatorTest
```