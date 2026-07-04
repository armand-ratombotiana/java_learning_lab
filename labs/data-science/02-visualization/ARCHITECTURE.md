# Architecture: Report Generation System

## System Diagram

```
┌──────────┐    ┌─────────────┐    ┌──────────────┐    ┌──────────┐
│  Query   │───>│  Data       │───>│  Chart       │───>│  Report  │
│  Service │    │  Transformer │    │  Renderer    │    │  Builder │
└──────────┘    └─────────────┘    └──────────────┘    └──────────┘
                                                    │
                                                    ├──> PDF Export
                                                    ├──> HTML Dashboard
                                                    └──> Image Archive
```

## Component Responsibilities

### Query Service
- Executes SQL, reads from DataFrame, or calls ML prediction API
- Returns typed `DataTable` objects

### Data Transformer
- Aggregates, filters, pivots data for specific chart types
- Converts raw data into (x, y, series) tuples
- Handles date bucketing (daily → monthly)

### Chart Renderer
- Maps data types to chart types via a registry:

```java
public class ChartRendererRegistry {
    private Map<ChartType, ChartRenderer> renderers = new HashMap<>();
    
    public ChartRendererRegistry() {
        renderers.put(ChartType.TIME_SERIES, new TimeSeriesRenderer());
        renderers.put(ChartType.SCATTER, new ScatterRenderer());
        renderers.put(ChartType.BAR, new BarRenderer());
        renderers.put(ChartType.HISTOGRAM, new HistogramRenderer());
    }
    
    public BufferedImage render(ChartSpec spec, DataTable data) {
        return renderers.get(spec.getType()).render(spec, data);
    }
}
```

### Report Builder
- Arranges chart images and tables into a page layout
- Supports portrait/landscape, multi-page, header/footer
- Outputs to PDF (iText), HTML (Thymeleaf), or image sequence

## Interface Specifications

```java
public interface ChartRenderer {
    BufferedImage render(ChartSpec spec, DataTable data);
    String getSupportedChartType();
}

public class ChartSpec {
    private ChartType type;
    private String title;
    private String xLabel, yLabel;
    private int width, height;
    private Palette palette;
    // getters/setters
}
```
