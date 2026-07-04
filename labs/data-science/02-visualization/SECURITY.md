# Security in Data Visualization

## 1. Data Leakage in Chart Images

Chart images embedded in PDFs or HTML reports may contain sensitive data that is not visible but embedded in vector elements.

```java
// Avoid: SVG text elements for data values are extractable
chart.getStyler().setToolTipType(ToolTipType.NONE);  // disable tooltip metadata

// Use raster output for sensitive data
BitmapEncoder.saveBitmap(chart, "report.png", BitmapFormat.PNG);
```

## 2. XSS via Chart Labels

If chart labels include user-supplied text (e.g., customer names), SVG output to web pages can carry XSS attacks.

```java
// Sanitize all label inputs
private String sanitizeSvg(String input) {
    return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
}
```

## 3. Information Disclosure via Axis Range

Auto-scaled axes can reveal exact min/max values of sensitive columns.

```java
// Use round-number axis range to avoid revealing exact extremes
chart.getXYPlot().getRangeAxis().setAutoRange(false);
chart.getXYPlot().getRangeAxis().setRange(0, 1000);
```

## 4. Chart Spoofing (Visual Misrepresentation)

While not a technical vulnerability, presenting data in misleading ways (truncated axes, cherry-picked date ranges) is an ethical security issue for data scientists.

## 5. File Inclusion in Export Paths

If the export filename is user-controlled, validate the path to prevent directory traversal.

```java
if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
    throw new SecurityException("Invalid filename");
}
```
