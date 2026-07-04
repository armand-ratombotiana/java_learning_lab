# Internals: How JFreeChart Renders

## Architecture Overview

JFreeChart renders charts onto a `Graphics2D` context — the same API used for Swing UI drawing. This makes it portable across screen, printer, and image export.

```
ChartPanel (Swing)        ──>  JFreeChart
    │                            │
    │                            ├──> Plot (e.g., XYPlot, CategoryPlot)
    │                            │       │
    │                            │       ├──> Dataset
    │                            │       ├──> Renderer (e.g., XYLineRenderer)
    │                            │       └──> Axis (NumberAxis, DateAxis)
    │                            │
    │                            └──> Title, Legend
    │
ChartUtilities/ChartFactory ──> PNG/SVG/PDF export
```

## Rendering Flow

1. **ChartPanel** receives a `paint(Graphics2D)` call from Swing
2. **JFreeChart.draw()** lays out title, legend, and plot area
3. **Plot.draw()** calculates data area within plot bounds
4. **Axis.range** auto-calculates or uses fixed range
5. **Renderer** iterates over dataset items, drawing shapes/lines via `Graphics2D`
6. **ChartUtilities** writes the `Graphics2D` buffer to a file

## Memory Model

JFreeChart keeps the entire dataset in memory (unlike streaming plot libraries). For large datasets (millions of points), use downsampling:

```java
// Downsample before plotting
XYSeries series = new XYSeries("data");
for (int i = 0; i < rawData.length; i += 10) {  // every 10th point
    series.add(rawData[i][0], rawData[i][1]);
}
```

## SVG Export

For vector output (reports, print), JFreeChart delegates to Batik's `SVGGraphics2D`, which records all `Graphics2D` calls as SVG elements.

```java
SVGGraphics2D svg2d = new SVGGraphics2D(800, 600);
chart.draw(svg2d, new Rectangle(800, 600));
File svgFile = new File("chart.svg");
FileUtils.write(svgFile, svg2d.getSVGElement(), "UTF-8");
```
