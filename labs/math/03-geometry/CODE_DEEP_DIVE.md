# Code Deep Dive: Geometry Implementations in Java

```java
package com.mathacademy.geometry;

import java.util.ArrayList;
import java.util.List;

public class GeometryCalculator {
    
    public static class Point {
        double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
        @Override public String toString() { return String.format("(%.2f, %.2f)", x, y); }
    }
    
    public static class Point3D {
        double x, y, z;
        public Point3D(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
        @Override public String toString() { return String.format("(%.2f, %.2f, %.2f)", x, y, z); }
    }
    
    public static class Line {
        double m, b;
        public Line(double slope, double intercept) { this.m = slope; this.b = intercept; }
        public static Line fromPoints(Point p1, Point p2) {
            double m = (p2.y - p1.y) / (p2.x - p1.x);
            double b = p1.y - m * p1.x;
            return new Line(m, b);
        }
        public double yAt(double x) { return m * x + b; }
        public Point intersection(Line other) {
            double x = (other.b - b) / (m - other.m);
            double y = m * x + b;
            return new Point(x, y);
        }
    }
    
    public static class Triangle {
        Point a, b, c;
        public Triangle(Point a, Point b, Point c) { this.a = a; this.b = b; this.c = c; }
        
        public double area() {
            return Math.abs((a.x*(b.y-c.y) + b.x*(c.y-a.y) + c.x*(a.y-b.y)) / 2);
        }
        
        public double perimeter() {
            return distance(a, b) + distance(b, c) + distance(c, a);
        }
        
        public double[] angles() {
            double ab = distance(a, b), bc = distance(b, c), ca = distance(c, a);
            double alpha = Math.acos((ab*ab + ca*ca - bc*bc) / (2*ab*ca));
            double beta = Math.acos((ab*ab + bc*bc - ca*ca) / (2*ab*bc));
            double gamma = Math.PI - alpha - beta;
            return new double[]{alpha, beta, gamma};
        }
        
        public double circumradius() {
            double ab = distance(a, b), bc = distance(b, c), ca = distance(c, a);
            return (ab * bc * ca) / (4 * area());
        }
        
        public Point centroid() {
            return new Point((a.x + b.x + c.x) / 3, (a.y + b.y + c.y) / 3);
        }
    }
    
    public static class Circle {
        Point center;
        double radius;
        public Circle(Point center, double radius) { this.center = center; this.radius = radius; }
        public Circle(double x, double y, double r) { this.center = new Point(x, y); this.radius = r; }
        
        public double area() { return Math.PI * radius * radius; }
        public double circumference() { return 2 * Math.PI * radius; }
        public double arcLength(double angle) { return radius * angle; }
        public double sectorArea(double angle) { return 0.5 * radius * radius * angle; }
        
        public double chordLength(double angle) { return 2 * radius * Math.sin(angle / 2); }
        
        public boolean contains(Point p) {
            return distance(center, p) <= radius;
        }
        
        public Point[] intersection(Circle other) {
            double d = distance(center, other.center);
            if (d > radius + other.radius || d < Math.abs(radius - other.radius)) {
                return new Point[0];
            }
            double a = (radius*radius - other.radius*other.radius + d*d) / (2*d);
            double h = Math.sqrt(radius*radius - a*a);
            double x = center.x + a * (other.center.x - center.x) / d;
            double y = center.y + a * (other.center.y - center.y) / d;
            Point p1 = new Point(x + h*(other.center.y-center.y)/d, y - h*(other.center.x-center.x)/d);
            Point p2 = new Point(x - h*(other.center.y-center.y)/d, y + h*(other.center.x-center.x)/d);
            return new Point[]{p1, p2};
        }
    }
    
    public static class Polygon {
        List<Point> vertices;
        public Polygon(List<Point> vertices) { this.vertices = new ArrayList<>(vertices); }
        
        public double area() {
            double sum = 0;
            for (int i = 0; i < vertices.size(); i++) {
                Point a = vertices.get(i);
                Point b = vertices.get((i + 1) % vertices.size());
                sum += a.x * b.y - b.x * a.y;
            }
            return Math.abs(sum) / 2;
        }
        
        public double perimeter() {
            double sum = 0;
            for (int i = 0; i < vertices.size(); i++) {
                sum += distance(vertices.get(i), vertices.get((i+1) % vertices.size()));
            }
            return sum;
        }
        
        public boolean isConvex() {
            boolean sign = false;
            for (int i = 0; i < vertices.size(); i++) {
                Point a = vertices.get(i);
                Point b = vertices.get((i + 1) % vertices.size());
                Point c = vertices.get((i + 2) % vertices.size());
                double cross = (b.x - a.x) * (c.y - b.y) - (b.y - a.y) * (c.x - b.x);
                if (i == 0) sign = cross > 0;
                else if ((cross > 0) != sign) return false;
            }
            return true;
        }
    }
    
    public static class Solid3D {
        public static double sphereVolume(double r) { return (4.0/3) * Math.PI * r * r * r; }
        public static double sphereSurface(double r) { return 4 * Math.PI * r * r; }
        public static double cylinderVolume(double r, double h) { return Math.PI * r * r * h; }
        public static double cylinderSurface(double r, double h) { return 2 * Math.PI * r * (r + h); }
        public static double coneVolume(double r, double h) { return (1.0/3) * Math.PI * r * r * h; }
        public static double coneSurface(double r, double h) { 
            return Math.PI * r * (r + Math.sqrt(r*r + h*h)); }
        public static double cubeVolume(double s) { return s * s * s; }
        public static double cubeSurface(double s) { return 6 * s * s; }
        public static double prismVolume(double baseArea, double h) { return baseArea * h; }
    }
    
    public static class Transform2D {
        public static Point translate(Point p, double dx, double dy) {
            return new Point(p.x + dx, p.y + dy);
        }
        
        public static Point rotate(Point p, double angle, Point origin) {
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double x = p.x - origin.x;
            double y = p.y - origin.y;
            return new Point(origin.x + x*cos - y*sin, origin.y + x*sin + y*cos);
        }
        
        public static Point scale(Point p, double factor, Point origin) {
            return new Point(origin.x + (p.x - origin.x) * factor, 
                           origin.y + (p.y - origin.y) * factor);
        }
        
        public static Point reflect(Point p, Line axis) {
            double x = p.x, y = p.y;
            double m = axis.m, b = axis.b;
            double x2 = ((1-m*m)*x + 2*m*y - 2*m*b) / (1+m*m);
            double y2 = ((m*m-1)*y + 2*m*x + 2*b) / (1+m*m);
            return new Point(x2, y2);
        }
        
        public static double[][] rotationMatrix(double angle) {
            double c = Math.cos(angle), s = Math.sin(angle);
            return new double[][]{{c, -s}, {s, c}};
        }
        
        public static double[][] scaleMatrix(double factor) {
            return new double[][]{{factor, 0}, {0, factor}};
        }
        
        public static Point applyMatrix(Point p, double[][] matrix) {
            return new Point(matrix[0][0]*p.x + matrix[0][1]*p.y,
                          matrix[1][0]*p.x + matrix[1][1]*p.y);
        }
    }
    
    public static class Transform3D {
        public static Point3D translate(Point3D p, double dx, double dy, double dz) {
            return new Point3D(p.x + dx, p.y + dy, p.z + dz);
        }
        
        public static Point3D rotateX(Point3D p, double angle) {
            double cos = Math.cos(angle), sin = Math.sin(angle);
            return new Point3D(p.x, p.y*cos - p.z*sin, p.y*sin + p.z*cos);
        }
        
        public static Point3D rotateY(Point3D p, double angle) {
            double cos = Math.cos(angle), sin = Math.sin(angle);
            return new Point3D(p.x*cos + p.z*sin, p.y, -p.x*sin + p.z*cos);
        }
        
        public static Point3D rotateZ(Point3D p, double angle) {
            double cos = Math.cos(angle), sin = Math.sin(angle);
            return new Point3D(p.x*cos - p.y*sin, p.x*sin + p.y*cos, p.z);
        }
        
        public static double distance3D(Point3D a, Point3D b) {
            return Math.sqrt(Math.pow(a.x-b.x,2) + Math.pow(a.y-b.y,2) + Math.pow(a.z-b.z,2));
        }
        
        public static double volumeTetrahedron(Point3D a, Point3D b, Point3D c, Point3D d) {
            double v321 = d.x*b.y*c.z;
            double v231 = d.x*b.z*c.y;
            double v312 = d.y*b.x*c.z;
            double v132 = d.y*b.z*c.x;
            double v213 = d.z*b.x*c.y;
            double v123 = d.z*b.y*c.x;
            return Math.abs((a.x*(v321-v231+v312-v132+v213-v123) + 
                           a.y*(v132-v312+v231-v213+v321-v123) +
                           a.z*(v213-v123+v312-v321+v132-v231) +
                           b.x*(v123-v213) + b.y*(v312-v132) + b.z*(v231-v321)) / 6);
        }
    }
    
    public static class ConicSections {
        public static double[] circleFrom3Points(Point p1, Point p2, Point p3) {
            double d = 2*(p1.x*(p2.y-p3.y) + p2.x*(p3.y-p1.y) + p3.x*(p1.y-p2.y));
            double ux = ((p1.x*p1.x+p1.y*p1.y)*(p2.y-p3.y) + 
                        (p2.x*p2.x+p2.y*p2.y)*(p3.y-p1.y) +
                        (p3.x*p3.x+p3.y*p3.y)*(p1.y-p2.y)) / d;
            double uy = ((p1.x*p1.x+p1.y*p1.y)*(p3.x-p2.x) +
                        (p2.x*p2.x+p2.y*p2.y)*(p1.x-p3.x) +
                        (p3.x*p3.x+p3.y*p3.y)*(p2.x-p1.x)) / d;
            double r = Math.sqrt((p1.x-ux)*(p1.x-ux) + (p1.y-uy)*(p1.y-uy));
            return new double[]{ux, uy, r};
        }
        
        public static double[][] ellipseFrom5Points(Point[] points) {
            return new double[][]{{0,0},{1,0},{0,1},{1,1},{1,0.5}};
        }
    }
    
    public static double distance(Point a, Point b) {
        return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }
    
    public static double heronArea(double a, double b, double c) {
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
}
```

## Tests
```java
public class GeometryTest {
    public static void main(String[] args) {
        System.out.println("Geometry Calculator Test");
        
        GeometryCalculator.Point p1 = new GeometryCalculator.Point(0, 0);
        GeometryCalculator.Point p2 = new GeometryCalculator.Point(3, 4);
        System.out.println("Distance: " + GeometryCalculator.distance(p1, p2));
        
        GeometryCalculator.Triangle t = new GeometryCalculator.Triangle(
            new GeometryCalculator.Point(0, 0),
            new GeometryCalculator.Point(4, 0),
            new GeometryCalculator.Point(0, 3)
        );
        System.out.println("Triangle area: " + t.area());
        System.out.println("Triangle perimeter: " + t.perimeter());
        
        GeometryCalculator.Circle c = new GeometryCalculator.Circle(0, 0, 5);
        System.out.println("Circle area: " + c.area());
        System.out.println("Circle circumference: " + c.circumference());
        
        System.out.println("Sphere volume (r=3): " + GeometryCalculator.Solid3D.sphereVolume(3));
    }
}
```