package com.algo.lab39;

import java.math.BigInteger;

/**
 * Elliptic curve operations over the Weierstrass form:
 * y^2 = x^3 + ax + b (mod p). Supports point addition,
 * point doubling, and scalar multiplication. Used in
 * ECDH, ECDSA, and factoring algorithms.
 */
public class EllipticCurve {
    private final BigInteger a, b, p;

    public EllipticCurve(BigInteger a, BigInteger b, BigInteger p) {
        this.a = a; this.b = b; this.p = p;
        BigInteger left = BigInteger.valueOf(4).multiply(a.pow(3));
        BigInteger right = BigInteger.valueOf(27).multiply(b.pow(2));
        if (left.add(right).mod(p).equals(BigInteger.ZERO))
            throw new IllegalArgumentException("Singular curve (discriminant = 0)");
    }

    public static class Point {
        public final BigInteger x, y;
        public static final Point INFINITY = new Point(null, null);

        private Point(BigInteger x, BigInteger y) { this.x = x; this.y = y; }

        public static Point of(BigInteger x, BigInteger y) { return new Point(x, y); }

        public boolean isInfinity() { return this == INFINITY; }

        public String toString() {
            return isInfinity() ? "INF" : "(" + x + ", " + y + ")";
        }
    }

    public Point add(Point p1, Point p2) {
        if (p1.isInfinity()) return p2;
        if (p2.isInfinity()) return p1;
        if (p1.x.equals(p2.x)) {
            if (p1.y.equals(p2.y)) return doublePoint(p1);
            return Point.INFINITY;
        }
        BigInteger m = p2.y.subtract(p1.y).multiply(p2.x.subtract(p1.x).modInverse(p)).mod(p);
        BigInteger x3 = m.pow(2).subtract(p1.x).subtract(p2.x).mod(p);
        BigInteger y3 = m.multiply(p1.x.subtract(x3)).subtract(p1.y).mod(p);
        return Point.of(x3, y3);
    }

    public Point doublePoint(Point p) {
        if (p.isInfinity() || p.y.equals(BigInteger.ZERO)) return Point.INFINITY;
        BigInteger m = BigInteger.valueOf(3).multiply(p.x.pow(2)).add(a)
                .multiply(BigInteger.valueOf(2).multiply(p.y).modInverse(p)).mod(p);
        BigInteger x3 = m.pow(2).subtract(p.x).subtract(p.x).mod(p);
        BigInteger y3 = m.multiply(p.x.subtract(x3)).subtract(p.y).mod(p);
        return Point.of(x3, y3);
    }

    public Point multiply(Point p, BigInteger k) {
        Point result = Point.INFINITY;
        Point addend = p;
        while (!k.equals(BigInteger.ZERO)) {
            if (k.testBit(0)) result = add(result, addend);
            addend = doublePoint(addend);
            k = k.shiftRight(1);
        }
        return result;
    }

    public boolean isOnCurve(Point p) {
        if (p.isInfinity()) return true;
        BigInteger left = p.y.pow(2).mod(p);
        BigInteger right = p.x.pow(3).add(a.multiply(p.x)).add(b).mod(p);
        return left.equals(right);
    }
}
