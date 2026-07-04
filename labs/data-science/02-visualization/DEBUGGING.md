# Debugging Data Visualizations

## Chart Shows Blank or Empty

**Symptom**: Chart renders but shows no data.

**Diagnosis**: Data range is outside axis range, or axis auto-range failed.

```java
// Debug: print axis ranges
System.out.println("X range: " + chart.getXYPlot().getDomainAxis().getRange());
System.out.println("Y range: " + chart.getXYPlot().getRangeAxis().getRange());
System.out.println("Data minX: " + minX + ", maxX: " + maxX);
// If data is outside axis range, force the range
chart.getXYPlot().getDomainAxis().setRange(minX, maxX);
```

## Chart Is Blurry

**Symptom**: Low-resolution output.

**Diagnosis**: Default DPI is 72 for screen; save at 300 for print.

```java
// Fix
BitmapEncoder.saveBitmapWithDPI(chart, "highres.png", BitmapFormat.PNG, 300);
```

## Color Is Wrong

**Symptom**: Series appears in unexpected color or all series share the same color.

**Diagnosis**: `setLineColor()` is called before the series is added, or the color is overwritten by the styler.

```java
// Fix: configure color AFTER adding the series
XYSeries series = chart.addSeries("Revenue", x, y);
series.setLineColor(new Color(0, 114, 178));  // call after addSeries
```

## Labels Truncated

**Symptom**: X-axis labels show "..." at the edges.

**Diagnosis**: Chart width is too small for the number of ticks.

```java
// Fix: increase chart width, or rotate labels, or reduce ticks
chart.getStyler().setXAxisLabelRotation(45);
chart.getStyler().setPlotContentSize(0.95);  // give more room to plot area
```

## Java Heap Space Error with Large Data

**Symptom**: OutOfMemoryError when rendering millions of points.

**Fix**: Downsample before plotting.

```java
// Downsample to max 10K points
int step = Math.max(1, data.length / 10000);
for (int i = 0; i < data.length; i += step) {
    series.add(data[i][0], data[i][1]);
}
```
