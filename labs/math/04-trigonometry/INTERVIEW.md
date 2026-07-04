# Interview Questions on Trigonometry

## Easy

1. Convert degrees to radians and vice versa.
2. Compute $\sin(x)$ using the Taylor series.
3. Calculate the distance between two points on Earth (Haversine formula).

## Medium

4. Implement the Discrete Fourier Transform.
5. Compute the rotation matrix for a 3D object.
6. Find the angle between two vectors.
7. Solve a triangle given SAS or ASA.

## Hard

8. Implement a fast sine using CORDIC or lookup table.
9. Compute the inverse DFT.
10. Implement the Goertzel algorithm (tone detection).
11. Optimize a rotation-heavy 3D renderer.

## Java: Haversine Distance

```java
public static double haversine(double lat1, double lon1,
                                double lat2, double lon2) {
    double R = 6371; // Earth radius in km
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
             + Math.cos(Math.toRadians(lat1))
             * Math.cos(Math.toRadians(lat2))
             * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
}
```

## Java: Angle Between Vectors

```java
public static double angleBetween(Vector v1, Vector v2) {
    double dot = v1.x() * v2.x() + v1.y() * v2.y() + v1.z() * v2.z();
    double mag1 = v1.magnitude();
    double mag2 = v2.magnitude();
    return Math.acos(dot / (mag1 * mag2));
}
```
