# Visualization Lab

## Overview
Data visualization is the graphical representation of information to communicate patterns, trends, and insights effectively. In Java, we use libraries like JFreeChart, XChart, and custom implementations to create various chart types.

## 1. Chart Types

### Line Charts
Best for: Time series data, trends over time
```
X-axis: Time/Dates
Y-axis: Numeric values
Use for: Stock prices, temperature trends, growth metrics
```

### Bar Charts
Best for: Comparing categories, discrete values
```
Vertical: Category comparisons
Horizontal: Ranking comparisons
Grouped: Multi-category comparison
```

### Scatter Plots
Best for: Showing relationships between two variables
```
X-axis: Independent variable
Y-axis: Dependent variable
Use for: Correlation analysis, outlier detection
```

### Pie/Donut Charts
Best for: Showing proportions of a whole
```
Slices: Categories
Size: Proportion
Use for: Market share, budget allocation
```

### Histograms
Best for: Distribution of single variable
```
Bins: Value ranges
Height: Frequency
Use for: Age distribution, income distribution
```

## 2. Chart Components

### Title
- Main chart title
- Axis titles
- Legend title

### Axes
- X-axis (independent variable)
- Y-axis (dependent variable)
- Grid lines
- Tick marks

### Data Elements
- Points
- Lines
- Bars
- Slices

### Annotations
- Labels
- Callouts
- Data point values

## 3. Chart Design Principles

### Color Selection
```java
// Use distinct colors for categories
String[] colors = {"#1f77b4", "#ff7f0e", "#2ca02c", "#d62728"};

// Sequential colormaps for continuous data
// Diverging colormaps for positive/negative values
```

### Typography
- Font: Sans-serif for readability
- Size: Title > Axis labels > Tick labels
- Weight: Bold for emphasis

### Layout
- Adequate spacing between elements
- Clear legend placement
- Appropriate aspect ratio

## 4. Common Java Visualization Libraries

### JFreeChart
```java
JFreeChart chart = ChartFactory.createLineChart(
    "Title", "X-Axis", "Y-Axis", dataset
);
ChartFrame frame = new ChartFrame("Chart", chart);
frame.pack();
frame.setVisible(true);
```

### XChart
```java
XYChart chart = QuickChart.getChart("Title", "X", "Y", seriesName, xData, yData);
SwingWrapper<XYChart> chartPanel = new SwingWrapper<>(chart);
chartPanel.displayChart();
```

## 5. Custom Implementations

### Basic 2D Rendering
```java
Graphics2D g2d = (Graphics2D) g;
g2d.setColor(Color.BLUE);
g2d.fill(new Rectangle2D.Double(x, y, width, height));
```

### Axis Drawing
```java
// X-axis line
g2d.draw(new Line2D.Double(margin, height - margin, width - margin, height - margin));

// Y-axis line
g2d.draw(new Line2D.Double(margin, margin, margin, height - margin));
```

## 6. Data Preparation for Visualization

### Aggregation
```java
// Group data before plotting
DataFrame monthly = sales.groupBy("month").sum("revenue");
```

### Normalization
```java
// Scale to 0-1 range for comparison
DataFrame normalized = normalize(df, "value");
```

### Binning
```java
// Create histogram bins
DataFrame binned = binData(df, "age", 10);
```

## 7. Interactive Features

### Tooltips
```java
// Display value on hover
chart.addMouseListener(new MouseAdapter() {
    public void mouseMoved(MouseEvent e) {
        // Show tooltip at mouse position
    }
});
```

### Zooming
```java
// Enable chart zoom
chart.getPlot().setDomainZoomable(true);
chart.getPlot().setRangeZoomable(true);
```

### Selection
```java
// Highlight selected data points
chart.addChartMouseListener(new ChartMouseListener() {
    public void chartMouseClicked(ChartMouseEvent event) {
        // Handle click on data point
    }
});
```

## 8. Export Options

### PNG
```java
ChartUtilities.saveChartAsPNG(new File("chart.png"), chart, width, height);
```

### JPEG
```java
ChartUtilities.saveChartAsJPEG(new File("chart.jpg"), chart, width, height);
```

### SVG
```java
// Using Batik library
SVGGraphics2D svgGenerator = new SVGGraphics2D(width, height);
chart.draw(svgGenerator);
```

## 9. Best Practices

1. **Choose appropriate chart type** for your data and message
2. **Label axes clearly** with units
3. **Include legend** for multi-series charts
4. **Use appropriate scale** (linear, log, time)
5. **Avoid chartjunk** (unnecessary decorations)
6. **Use color meaningfully** (sequential for values, categorical for groups)
7. **Consider accessibility** (color-blind friendly palettes)
8. **Export at appropriate resolution** (300 DPI for print, 72 DPI for web)

## 10. Advanced Visualizations

### Heatmaps
```java
// Color-coded matrix
// Row/column categories
// Color intensity = value
```

### Box Plots
```java
// Show distribution statistics
// Median, quartiles, outliers
```

### Violin Plots
```java
// Combination of box plot and density
// Show full distribution shape
```

### Network Graphs
```java
// Nodes and edges
// Clustering, pathways
```

### Geographic Maps
```java
// Choropleth maps
// Point maps
// Heat maps on maps
```