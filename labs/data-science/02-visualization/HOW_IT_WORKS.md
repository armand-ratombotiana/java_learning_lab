# How Data Visualization Works

## The Rendering Pipeline

```
Data → Bind to Aesthetics → Choose Geometry → Set Scales → Render
```

### Step 1: Data
A DataFrame with columns for x, y, color, size, facet, etc.

### Step 2: Bind to Aesthetics
Map columns to visual channels:

| Visual Channel | Best For | Example Mapping |
|---|---|---|
| x position | Independent variable | date |
| y position | Dependent variable | sales |
| color (hue) | Categories | region |
| color (intensity) | Continuous values | population |
| size | Third quantitative variable | revenue |
| shape | Categories (≤6) | product_type |

### Step 3: Choose Geometry

| Geometry | Data Relationship | When to Use |
|---|---|---|
| Point | Two numeric variables | Scatter plot, bubble chart |
| Line | Ordered x (time) | Time series, trend lines |
| Bar | One categorical + one numeric | Comparisons |
| Box/whisker | Distribution by category | Compare distributions |
| Histogram | Single numeric distribution | See shape of distribution |
| Area | Cumulative over time | Stacked areas, waterfall |

### Step 4: Set Scales

```java
// XChart: linear vs log scale
chart.getStyler().setYAxisLogarithmic(true);
```

### Step 5: Render

```java
// XChart rendering pipeline
XYChart chart = new XYChart(800, 600);
chart.setTitle("Sales by Quarter");
chart.setXAxisTitle("Quarter");
chart.setYAxisTitle("Revenue ($M)");

// Series data
chart.addSeries("North America", quarters, naRevenue);
chart.addSeries("Europe", quarters, euRevenue);

// Style
chart.getStyler().setChartBackgroundColor(Color.WHITE);
chart.getStyler().setLegendPosition(LegendPosition.InsideNE);

// Output
BitmapEncoder.saveBitmap(chart, "sales_report.png", BitmapFormat.PNG);
```
