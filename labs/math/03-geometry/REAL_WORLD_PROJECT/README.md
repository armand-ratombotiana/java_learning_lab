# Real World Project: CAD System & 3D Modeling Engine

## Implementation

```java
package com.mathacademy.geometry.realworld;

import java.util.ArrayList;
import java.util.List;

public class CADSystem {
    
    public static class Vector3D {
        double x, y, z;
        public Vector3D(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
        public Vector3D add(Vector3D v) { return new Vector3D(x+v.x, y+v.y, z+v.z); }
        public Vector3D subtract(Vector3D v) { return new Vector3D(x-v.x, y-v.y, z-v.z); }
        public Vector3D scale(double s) { return new Vector3D(x*s, y*s, z*s); }
        public double dot(Vector3D v) { return x*v.x + y*v.y + z*v.z; }
        public Vector3D cross(Vector3D v) {
            return new Vector3D(y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x);
        }
        public double magnitude() { return Math.sqrt(x*x + y*y + z*z); }
        public Vector3D normalize() { double m = magnitude(); return scale(1/m); }
        public double angle(Vector3D v) { return Math.acos(dot(v) / (magnitude() * v.magnitude())); }
    }
    
    public static class Vertex {
        Vector3D position;
        Vector3D normal;
        double[] texCoord = new double[2];
        public Vertex(Vector3D pos) { this.position = pos; this.normal = new Vector3D(0,0,1); }
        public Vertex(Vector3D pos, Vector3D norm) { this.position = pos; this.normal = norm; }
    }
    
    public static class Face {
        List<Integer> indices = new ArrayList<>();
        Vector3D normal;
        public Face() { this.normal = new Vector3D(0,0,0); }
        public void addIndex(int i) { indices.add(i); }
        public void computeNormal(List<Vertex> vertices) {
            if (indices.size() < 3) return;
            Vector3D v1 = vertices.get(indices.get(1)).position.subtract(vertices.get(indices.get(0)).position);
            Vector3D v2 = vertices.get(indices.get(2)).position.subtract(vertices.get(indices.get(0)).position);
            normal = v1.cross(v2).normalize();
        }
    }
    
    public static class Mesh {
        List<Vertex> vertices = new ArrayList<>();
        List<Face> faces = new ArrayList<>();
        String name;
        
        public Mesh(String name) { this.name = name; }
        public void addVertex(Vertex v) { vertices.add(v); }
        public void addFace(Face f) { faces.add(f); }
        
        public void computeNormals() {
            for (Face f : faces) {
                f.computeNormal(vertices);
            }
            for (Vertex v : vertices) {
                Vector3D sum = new Vector3D(0,0,0);
                int count = 0;
                for (Face f : faces) {
                    if (f.indices.contains(vertices.indexOf(v))) {
                        sum = sum.add(f.normal);
                        count++;
                    }
                }
                if (count > 0) v.normal = sum.scale(1.0/count).normalize();
            }
        }
        
        public double surfaceArea() {
            double area = 0;
            for (Face f : faces) {
                if (f.indices.size() >= 3) {
                    Vector3D v1 = vertices.get(f.indices.get(1)).position.subtract(vertices.get(f.indices.get(0)).position);
                    Vector3D v2 = vertices.get(f.indices.get(2)).position.subtract(vertices.get(f.indices.get(0)).position);
                    area += v1.cross(v2).magnitude() / 2;
                }
            }
            return area;
        }
        
        public double volume() {
            double vol = 0;
            for (Face f : faces) {
                if (f.indices.size() >= 3) {
                    Vector3D v1 = vertices.get(f.indices.get(1)).position.subtract(vertices.get(f.indices.get(0)).position);
                    Vector3D v2 = vertices.get(f.indices.get(2)).position.subtract(vertices.get(f.indices.get(0)).position);
                    vol += vertices.get(f.indices.get(0)).position.dot(v1.cross(v2));
                }
            }
            return Math.abs(vol) / 6;
        }
        
        public Vector3D centroid() {
            double cx = 0, cy = 0, cz = 0;
            for (Vertex v : vertices) {
                cx += v.position.x;
                cy += v.position.y;
                cz += v.position.z;
            }
            return new Vector3D(cx/vertices.size(), cy/vertices.size(), cz/vertices.size());
        }
    }
    
    public static class Matrix4x4 {
        double[][] m = new double[4][4];
        
        public Matrix4x4() {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    m[i][j] = (i == j) ? 1 : 0;
                }
            }
        }
        
        public static Matrix4x4 translation(double x, double y, double z) {
            Matrix4x4 m = new Matrix4x4();
            m.m[0][3] = x; m.m[1][3] = y; m.m[2][3] = z;
            return m;
        }
        
        public static Matrix4x4 rotationX(double angle) {
            Matrix4x4 m = new Matrix4x4();
            double c = Math.cos(angle), s = Math.sin(angle);
            m.m[1][1] = c; m.m[1][2] = -s;
            m.m[2][1] = s; m.m[2][2] = c;
            return m;
        }
        
        public static Matrix4x4 rotationY(double angle) {
            Matrix4x4 m = new Matrix4x4();
            double c = Math.cos(angle), s = Math.sin(angle);
            m.m[0][0] = c; m.m[0][2] = s;
            m.m[2][0] = -s; m.m[2][2] = c;
            return m;
        }
        
        public static Matrix4x4 rotationZ(double angle) {
            Matrix4x4 m = new Matrix4x4();
            double c = Math.cos(angle), s = Math.sin(angle);
            m.m[0][0] = c; m.m[0][1] = -s;
            m.m[1][0] = s; m.m[1][1] = c;
            return m;
        }
        
        public static Matrix4x4 scale(double x, double y, double z) {
            Matrix4x4 m = new Matrix4x4();
            m.m[0][0] = x; m.m[1][1] = y; m.m[2][2] = z;
            return m;
        }
        
        public Matrix4x4 multiply(Matrix4x4 other) {
            Matrix4x4 result = new Matrix4x4();
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    result.m[i][j] = 0;
                    for (int k = 0; k < 4; k++) {
                        result.m[i][j] += m[i][k] * other.m[k][j];
                    }
                }
            }
            return result;
        }
        
        public Vector3D transform(Vector3D v) {
            double x = m[0][0]*v.x + m[0][1]*v.y + m[0][2]*v.z + m[0][3];
            double y = m[1][0]*v.x + m[1][1]*v.y + m[1][2]*v.z + m[1][3];
            double z = m[2][0]*v.x + m[2][1]*v.y + m[2][2]*v.z + m[2][3];
            return new Vector3D(x, y, z);
        }
    }
    
    public static class PrimitiveFactory {
        public static Mesh createCube(double size) {
            Mesh mesh = new Mesh("Cube");
            double s = size / 2;
            mesh.addVertex(new Vertex(new Vector3D(-s,-s,-s)));
            mesh.addVertex(new Vertex(new Vector3D(s,-s,-s)));
            mesh.addVertex(new Vertex(new Vector3D(s,s,-s)));
            mesh.addVertex(new Vertex(new Vector3D(-s,s,-s)));
            mesh.addVertex(new Vertex(new Vector3D(-s,-s,s)));
            mesh.addVertex(new Vertex(new Vector3D(s,-s,s)));
            mesh.addVertex(new Vertex(new Vector3D(s,s,s)));
            mesh.addVertex(new Vertex(new Vector3D(-s,s,s)));
            int[][] faces = {{0,1,2,3},{4,5,6,7},{0,4,5,1},{2,6,7,3},{0,3,7,4},{1,5,6,2}};
            for (int[] f : faces) {
                Face face = new Face();
                for (int i : f) face.addIndex(i);
                mesh.addFace(face);
            }
            mesh.computeNormals();
            return mesh;
        }
        
        public static Mesh createSphere(double radius, int segments, int rings) {
            Mesh mesh = new Mesh("Sphere");
            for (int r = 0; r <= rings; r++) {
                double phi = Math.PI * r / rings;
                for (int s = 0; s <= segments; s++) {
                    double theta = 2 * Math.PI * s / segments;
                    double x = radius * Math.sin(phi) * Math.cos(theta);
                    double y = radius * Math.cos(phi);
                    double z = radius * Math.sin(phi) * Math.sin(theta);
                    mesh.addVertex(new Vertex(new Vector3D(x,y,z)));
                }
            }
            for (int r = 0; r < rings; r++) {
                for (int s = 0; s < segments; s++) {
                    int current = r * (segments + 1) + s;
                    int next = current + segments + 1;
                    Face f1 = new Face();
                    f1.addIndex(current); f1.addIndex(next); f1.addIndex(current + 1);
                    mesh.addFace(f1);
                    Face f2 = new Face();
                    f2.addIndex(current + 1); f2.addIndex(next); f2.addIndex(next + 1);
                    mesh.addFace(f2);
                }
            }
            mesh.computeNormals();
            return mesh;
        }
        
        public static Mesh createCylinder(double radius, double height, int segments) {
            Mesh mesh = new Mesh("Cylinder");
            mesh.addVertex(new Vertex(new Vector3D(0,0,0)));
            mesh.addVertex(new Vertex(new Vector3D(0,height,0)));
            for (int i = 0; i < segments; i++) {
                double theta = 2 * Math.PI * i / segments;
                double x = radius * Math.cos(theta);
                double z = radius * Math.sin(theta);
                mesh.addVertex(new Vertex(new Vector3D(x,0,z)));
                mesh.addVertex(new Vertex(new Vector3D(x,height,z)));
            }
            for (int i = 0; i < segments; i++) {
                Face f = new Face();
                f.addIndex(2 + 2*i); f.addIndex(2 + 2*((i+1)%segments));
                f.addIndex(2 + 2*((i+1)%segments) + 1);
                f.addIndex(2 + 2*i + 1);
                mesh.addFace(f);
            }
            for (int i = 0; i < segments; i++) {
                Face bottom = new Face();
                bottom.addIndex(2 + 2*i); bottom.addIndex(2 + 2*((i+1)%segments)); bottom.addIndex(0);
                mesh.addFace(bottom);
                Face top = new Face();
                top.addIndex(2 + 2*i + 1); top.addIndex(2 + 2*((i+1)%segments) + 1); top.addIndex(1);
                mesh.addFace(top);
            }
            mesh.computeNormals();
            return mesh;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("CAD SYSTEM - 3D MODELING ENGINE");
        System.out.println("================================");
        
        Mesh cube = PrimitiveFactory.createCube(2);
        System.out.println("\nCube: " + cube.name);
        System.out.println("  Vertices: " + cube.vertices.size());
        System.out.println("  Faces: " + cube.faces.size());
        System.out.println("  Surface Area: " + cube.surfaceArea());
        System.out.println("  Volume: " + cube.volume());
        
        Mesh sphere = PrimitiveFactory.createSphere(1, 32, 16);
        System.out.println("\nSphere: " + sphere.name);
        System.out.println("  Vertices: " + sphere.vertices.size());
        System.out.println("  Surface Area: " + sphere.surfaceArea());
        System.out.println("  Volume: " + sphere.volume());
        
        Mesh cylinder = PrimitiveFactory.createCylinder(1, 2, 24);
        System.out.println("\nCylinder: " + cylinder.name);
        System.out.println("  Vertices: " + cylinder.vertices.size());
        System.out.println("  Surface Area: " + cylinder.surfaceArea());
        System.out.println("  Volume: " + cylinder.volume());
        
        Matrix4x4 transform = Matrix4x4.translation(1, 2, 3)
            .multiply(Matrix4x4.rotationY(Math.PI / 4))
            .multiply(Matrix4x4.scale(2, 2, 2));
        System.out.println("\nTransformation applied to (1,0,0):");
        Vector3D original = new Vector3D(1, 0, 0);
        Vector3D transformed = transform.transform(original);
        System.out.println("  Result: " + transformed.x + ", " + transformed.y + ", " + transformed.z);
    }
}
```

## Running
```bash
cd labs/math/03-geometry/REAL_WORLD_PROJECT
javac -d bin *.java
java com.mathacademy.geometry.realworld.CADSystem
```