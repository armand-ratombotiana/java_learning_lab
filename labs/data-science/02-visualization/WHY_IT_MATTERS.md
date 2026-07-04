# Why Data Visualization Matters

A model is only as trusted as its results are explainable. Visualization is the primary vehicle for that explanation — to stakeholders, to regulators, and to yourself during exploratory analysis.

## Impact on Decision-Making

| Visualization | Insight | Non-Visual Alternative |
|---|---|---|
| Scatter plot | Nonlinear relationship between X and Y | Correlation r = 0.03 misses the U-shaped curve |
| Time series line | Seasonal spike every December | Mean of 120 hides Dec peak of 400 |
| Histogram | Bimodal distribution suggests two populations | Mean = 50, std = 15 — completely misleading |
| Box plot | Outlier cluster at high end | Z-score passes, but 5% of data is extreme |

## Java-Specific Relevance

Server-side Java apps generating reports need visualization code that produces publication-quality charts without interactive user input. XChart and JFreeChart fill this niche: thread-safe, SVG/PNG output, embeddable in PDFs.

```java
// Embedded chart for a PDF report
BufferedImage chartImage = chart.getBufferedImage();
ReportGenerator.addImage(report, chartImage, "Revenue by Quarter");
```
