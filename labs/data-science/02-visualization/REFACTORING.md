# Refactoring Visualization Code

## Smell: Repeated Chart Configuration

Same styler configuration appears in every chart method.

```java
// Before: repeated in 10 methods
chart.getStyler().setChartBackgroundColor(Color.WHITE);
chart.getStyler().setPlotBackgroundColor(new Color(245, 245, 245));
chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
```

**Refactor**: Create a chart factory with default styles.

```java
public class ChartFactory {
    public static void applyDefaultStyle(XYChart chart) {
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBackgroundColor(new Color(245, 245, 245));
        chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
    }
}
```

## Smell: Data Preparation Mixed with Rendering

Plotting logic is intertwined with data querying and transformation.

```java
// Before: entangled
ResultSet rs = stmt.executeQuery("SELECT ...");
XYChart chart = new XYChart(800, 600);
while (rs.next()) { chart.addSeries(...); }
```

**Refactor**: Separate into data layer and visualization layer.

```java
// After: clean separation
DataFrame salesData = queryService.getQuarterlySales("2024");
ChartRenderer.renderLineChart(salesData, "quarter", "revenue", "region");
```

## Smell: Hardcoded Dimensions

Chart dimensions are magic numbers scattered across classes.

```java
// Before
new XYChart(800, 600);

// After
public static final int REPORT_WIDTH = 800;
public static final int REPORT_HEIGHT = 600;
```

## Smell: Missing Export Abstraction

Every chart method has its own export logic (PNG vs SVG vs PDF).

```java
// Refactor: single export utility
public class ChartExporter {
    public static void export(XYChart chart, String path, ExportFormat format) {
        switch (format) {
            case PNG -> BitmapEncoder.saveBitmap(chart, path, BitmapFormat.PNG);
            case SVG -> { /* Batik export */ }
            case PDF -> { /* iText integration */ }
        }
    }
}
```
