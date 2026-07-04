# Step-by-Step: Building a Dashboard Chart in Java

**Goal**: Create a multi-series line chart of quarterly revenue by region for an executive dashboard.

## Step 1: Prepare the Data

```java
// Assume data from a query
Map<String, double[]> regions = new HashMap<>();
regions.put("North America", new double[]{120, 135, 148, 160});
regions.put("Europe",       new double[]{95,  102, 118, 130});
regions.put("Asia",         new double[]{60,  75,  90,  110});

double[] quarters = new double[]{1, 2, 3, 4};  // Q1–Q4 2024
```

## Step 2: Create the Chart Object

```java
XYChart chart = new XYChart(900, 500);
chart.setTitle("2024 Quarterly Revenue by Region");
chart.setXAxisTitle("Quarter");
chart.setYAxisTitle("Revenue ($M)");
```

## Step 3: Add Series with Distinct Colors

```java
Color[] colors = {Color.BLUE, Color.GREEN, Color.ORANGE};
int i = 0;
for (Map.Entry<String, double[]> entry : regions.entrySet()) {
    XYSeries series = chart.addSeries(entry.getKey(), quarters, entry.getValue());
    series.setLineColor(colors[i++]);
    series.setLineWidth(3.0f);
    series.setMarker(SeriesMarkers.NONE);
}
```

## Step 4: Style for Dashboard

```java
chart.getStyler().setChartBackgroundColor(Color.WHITE);
chart.getStyler().setPlotBackgroundColor(new Color(245, 245, 245));
chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
chart.getStyler().setAxisTickLabelsColor(Color.GRAY);
chart.getStyler().setPlotGridLinesColor(new Color(220, 220, 220));
```

## Step 5: Output as High-Res PNG for Report

```java
BitmapEncoder.saveBitmapWithDPI(chart, "revenue_q4.png", BitmapFormat.PNG, 300);
```

## Result

A clean, publication-ready line chart with three colored lines on a light gray plot background, saved at 300 DPI for print reports.
