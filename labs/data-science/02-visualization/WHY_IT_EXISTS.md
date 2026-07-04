# Why Data Visualization Exists

Data visualization exists because human visual perception is the highest-bandwidth channel into the brain. A well-designed chart communicates patterns, outliers, and relationships that would be invisible in a table of numbers. Edward Tufte's foundational principle — *"Above all else show the data"* — captures the purpose: reveal the signal without distorting it.

## The Gap It Bridges

- **Statistical summaries** (means, correlations) compress information and hide distributions
- **Raw tables** cannot be scanned for trends beyond ~50 rows
- **Models** produce numbers; visualization produces understanding and trust

## Java Context

Java visualization libraries like XChart, JFreeChart, and Charts4j bring declarative charting to server-side and desktop applications. Unlike Python's matplotlib (interactive-first), Java charts often target rendered PNG/SVG for reports or dashboards.

```java
// XChart: minimal Java line chart
XYChart chart = new XYChart(600, 400);
chart.addSeries("sales", xData, yData);
BitmapEncoder.saveBitmap(chart, "./chart.png", BitmapFormat.PNG);
```
