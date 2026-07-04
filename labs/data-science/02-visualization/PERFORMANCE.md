# Performance in Data Visualization

## Rendering Bottlenecks

### 1. Too Many Data Points

Rendering 1M points on a 800x600 pixel canvas is wasteful — only ~480K pixels exist, and many overlap.

**Strategy**: Downsample or use data bucketing.

```java
// Largest-Triangle-Three-Buckets (LTTB) downsampling
public static double[][] lttb(double[][] data, int threshold) {
    // Keeps shape better than uniform sampling
    // Implementation: splits data into threshold buckets,
    // picks point in each bucket that forms largest triangle with neighbors
}
```

### 2. SVG DOM Size

SVG output with thousands of path elements creates massive files that take time to serialize and parse.

**Strategy**: Use raster output (PNG) for large datasets, SVG only for small data (<10K points).

### 3. Frequent Redraws in Interactive Charts

Requesting chart repaint on every mouse move in Java Swing causes layout recalculations.

**Strategy**: Debouce repaint requests (200ms throttle).

```java
// Debounce mechanism
private long lastPaint = 0L;
private static final long THROTTLE_MS = 200;

public void requestRepaint() {
    long now = System.currentTimeMillis();
    if (now - lastPaint > THROTTLE_MS) {
        repaint();
        lastPaint = now;
    }
}
```

### 4. Font Rendering

String metrics for axis labels are expensive to compute, especially for rotated text.

**Strategy**: Cache font metrics or pre-render labels to off-screen images.

## Profiling Approach

```java
long start = System.nanoTime();
chart.draw(g2d, bounds);
long elapsed = System.nanoTime() - start;
System.out.println("Render time: " + (elapsed / 1_000_000) + " ms");
// If > 500ms for a static chart, downsampling is needed
```
