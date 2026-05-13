# Visualization Flashcards

## Chart Types

### Card 1: Line Chart
**Q:** When should you use a line chart?
**A:** Showing trends over time or continuous data with many points.

### Card 2: Bar Chart
**Q:** When should you use a bar chart?
**A:** Comparing discrete categories or showing rankings.

### Card 3: Scatter Plot
**Q:** When should you use a scatter plot?
**A:** Showing relationships between two continuous variables.

### Card 4: Pie Chart
**Q:** When should you use a pie chart?
**A:** Showing proportions of a whole (best for 3-5 categories).

### Card 5: Histogram
**Q:** When should you use a histogram?
**A:** Showing distribution of a single continuous variable.

---

## Java Implementation

### Card 6: JFreeChart Import
**Q:** What import do you need for JFreeChart?
**A:** `import org.jfree.chart.*;` and `import org.jfree.data.*;`

### Card 7: Create Line Chart
**Q:** How do you create a basic line chart in JFreeChart?
**A:** `ChartFactory.createLineChart(title, xLabel, yLabel, dataset)`

### Card 8: Save to PNG
**Q:** How do you save a chart as PNG?
**A:** `ChartUtilities.saveChartAsPNG(file, chart, width, height)`

### Card 9: Display in Frame
**Q:** How do you display a chart in a window?
**A:** `ChartFrame frame = new ChartFrame("Title", chart); frame.pack(); frame.setVisible(true);`

### Card 10: Graphics2D Rendering
**Q:** What provides 2D drawing in Java?
**A:** `Graphics2D` (extends Graphics) with methods like draw(), fill(), setColor()

---

## Chart Components

### Card 11: Axis Configuration
**Q:** How do you configure chart axes?
**A:** Use `CategoryAxis`, `NumberAxis`, `ValueAxis` classes to set labels, ranges, grid

### Card 12: Legend
**Q:** How do you add a legend to a chart?
**A:** `chart.getLegend()` returns LegendItemCollection; or use LegendTitle

### Card 13: Title
**Q:** How do you set chart title?
**A:** `chart.setTitle("Title Text")` or `TextTitle title = new TextTitle("...")`

### Card 14: Grid Lines
**Q:** How do you add grid lines?
**A:** `plot.setDomainGridlinesVisible(true)` for vertical, `setRangeGridlinesVisible(true)` for horizontal

### Card 15: Tick Marks
**Q:** How do you configure tick marks on axes?
**A:** Use `setTickUnit()` on NumberAxis or `setTickLabelsVisible()` on CategoryAxis

---

## Data Operations

### Card 16: Aggregation for Charts
**Q:** How do you prepare data for time series charts?
**A:** Aggregate by time period (daily→monthly) using groupBy and sum/mean

### Card 17: Binning Data
**Q:** How do you prepare data for histograms?
**A:** Bin continuous data into ranges using discretization/binning functions

### Card 18: Normalization
**Q:** Why normalize data before plotting?
**A:** To compare series on different scales on the same chart

### Card 19: Rolling Average
**Q:** What does rolling average add to charts?
**A:** Smooths out fluctuations to show underlying trend

### Card 20: Percentage Calculation
**Q:** How do you convert to percentage of total?
**A:** Divide each value by sum of all values and multiply by 100

---

## Design Principles

### Card 21: Chartjunk
**Q:** What is chartjunk?
**A:** Unnecessary decorations (3D effects, gradients, shadows) that don't convey data

### Card 22: Data-Ink Ratio
**Q:** What is data-ink ratio?
**A:** Proportion of ink used for actual data vs decoration - maximize it

### Card 23: Color Schemes
**Q:** What color scheme for continuous data?
**A:** Sequential (light to dark) for low-to-high values

### Card 24: Diverging Colors
**Q:** When use diverging colors?
**A:** When showing positive vs negative (e.g., profit/loss)

### Card 25: Font Choice
**Q:** What font for chart readability?
**A:** Sans-serif fonts (Arial, Helvetica) are easier to read than serif

---

## Chart Styling

### Card 26: Line Thickness
**Q:** How do you change line thickness?
**A:** `series.setStroke(new BasicStroke(2.0f))`

### Card 27: Point Markers
**Q:** How do you add markers to line chart points?
**A:** `series.setBaseLinesVisible(true)` and `series.setBaseShapesVisible(true)`

### Card 28: Bar Spacing
**Q:** How do you adjust bar chart spacing?
**A:** `categoryPlot.setItemMargin()` controls space between groups

### Card 29: Color per Series
**Q:** How do you set colors for each series?
**A:** `series.setColor(Color.RED)` or use a paint scale

### Card 30: Transparency
**Q:** How do you add transparency?
**A:** Use `AlphaComposite` or set color with alpha channel (e.g., new Color(r,g,b,128))

---

## Advanced Charts

### Card 31: Box Plot
**Q:** What does a box plot show?
**A:** Median, quartiles (Q1, Q3), interquartile range, potential outliers

### Card 32: Heatmap
**Q:** What is a heatmap?
**A:** Grid where color intensity represents value - good for correlation matrices

### Card 33: Waterfall Chart
**Q:** What does a waterfall chart show?
**A:** Incremental changes from a starting value (like revenue waterfall)

### Card 34: Stacked Area
**Q:** When use stacked area chart?
**A:** Showing cumulative values of multiple series over time

### Card 35: Gauge Chart
**Q:** When use gauge chart?
**A:** Displaying single KPI against target zones (traffic light style)

---

## Interactive Features

### Card 36: Tooltip
**Q:** How do you add tooltips?
**A:** Implement ChartMouseListener, get entity at mouse position, show value

### Card 37: Zoom
**Q:** How do you enable chart zoom?
**A:** `plot.setDomainZoomable(true)` and `plot.setRangeZoomable(true)`

### Card 38: Legend Click
**Q:** How do you make legend clickable to toggle series?
**A:** Override legend item click handler to show/hide series

### Card 39: Crosshair
**Q:** What is crosshair in charts?
**A:** Vertical/horizontal lines following cursor showing exact values

### Card 40: Export Format
**Q:** What formats can you export charts to?
**A:** PNG, JPEG, SVG (using Batik), PDF (using iText)

---

## Statistical Charts

### Card 41: Q-Q Plot Purpose
**Q:** What does a Q-Q plot check?
**A:** Whether data follows a normal distribution

### Card 42: ACF Plot
**Q:** What does autocorrelation plot show?
**A:** Correlation of time series with itself at different lags

### Card 43: Residual Plot
**Q:** What does residual plot show?
**A:** Difference between actual and predicted values for model validation

### Card 44: Confidence Bands
**Q:** What do confidence interval bands show?
**A:** Range where true value is likely to fall (e.g., 95% CI)

### Card 45: CDF Plot
**Q:** What does cumulative distribution function plot show?
**A:** Probability that a random variable is less than or equal to a value

---

## Business Charts

### Card 46: Sales Funnel
**Q:** What does a sales funnel show?
**A:** Number of prospects at each stage of sales process

### Card 47: Gantt Chart
**Q:** What is a Gantt chart used for?
**A:** Visualizing project schedule with tasks and timelines

### Card 48: Pareto Chart
**Q:** What is a Pareto chart?
**A:** Bar chart sorted descending with cumulative percentage line

### Card 49: Sparkline
**Q:** What is a sparkline?
**A:** Small inline chart showing trend without axes (for dashboards)

### Card 50: Bullet Chart
**Q:** What does a bullet chart compare?
**A:** Actual value vs target/reference value in compact form

---

## Data Visualization Best Practices

### Card 51: Chart Selection
**Q:** How do you choose the right chart?
**A:** Ask: What relationship do you want to show? (comparison, trend, distribution, proportion)

### Card 52: Labeling
**Q:** What should axis labels include?
**A:** Variable name AND unit (e.g., "Revenue ($)")

### Card 53: Legend Placement
**Q:** Where should legend be placed?
**A:** Where it doesn't overlap data; right side or bottom for most charts

### Card 54: Number Formatting
**Q:** How should you format large numbers?
**A:** Use K for thousands, M for millions (e.g., 1.5M instead of 1,500,000)

### Card 55: Appropriate Scale
**Q:** When use logarithmic scale?
**A:** When data spans multiple orders of magnitude or shows percentage changes

---

## Troubleshooting

### Card 56: Empty Chart
**Q:** Chart shows nothing - what's wrong?
**A:** Check: data not empty, axis ranges correct, series added to dataset

### Card 57: Inverted Y-Axis
**Q:** Why might y-axis be inverted (0 at top)?
**A:** Check axis range - if max < min or data inverted, adjust with `setRange()`

### Card 58: Missing Data Points
**Q:** How do you handle missing data in line charts?
**A:** Set dataset to null for missing periods or use GapDataset

### Card 59: Overlapping Labels
**Q:** How fix overlapping axis labels?
**A:** Rotate labels, reduce font size, increase chart size, or reduce number of labels

### Card 60: Slow Rendering
**Q:** How improve chart rendering speed?
**A:** Reduce data points, use buffering, limit anti-aliasing, use appropriate chart type

---

## Custom Rendering

### Card 61: Shape Drawing
**Q:** How draw basic shapes?
**A:** `g2d.fill(new Rectangle2D.Double(x, y, w, h))`, `g2d.draw(new Line2D.Double(x1,y1,x2,y2))`

### Card 62: Text Drawing
**Q:** How draw text with custom font?
**A:** `g2d.setFont(new Font("Arial", Font.BOLD, 14)); g2d.drawString(text, x, y);`

### Card 63: Color from Hex
**Q:** How convert hex color string to Java Color?
**A:** `new Color(Integer.parseInt(hex.substring(1,3),16), Integer.parseInt(hex.substring(3,5),16), ...)`

### Card 64: Anti-aliasing
**Q:** How enable smooth rendering?
**A:** `g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)`

### Card 65: Transform
**Q:** How rotate text for axis labels?
**A:** `g2d.rotate(Math.toRadians(-45), x, y); g2d.drawString(...); g2d.rotate(Math.toRadians(45), x, y);`

---

## Animations

### Card 66: Chart Animation
**Q:** How add animation to chart loading?
**A:** Use javax.swing.Timer to incrementally update data or transparency

### Card 67: Data Transition
**Q:** How animate data changes?
**A:** Interpolate between old and new values over multiple frames

### Card 68: Hover Effect
**Q:** How highlight element on hover?
**A:** Change color/stroke on ChartMouseEvent, call repaint()

### Card 69: Loading Animation
**Q:** How show loading while chart renders?
**A:** Display progress indicator, update as data loads, remove when complete

### Card 70: Dynamic Update
**Q:** How update chart in real-time?
**A:** Use Timer to periodically fetch new data, update dataset, call fireSeriesChanged()

---

## Export and Sharing

### Card 71: PNG Export
**Q:** Code to export chart as PNG?
**A:** `BufferedImage img = chart.createBufferedImage(w, h); ImageIO.write(img, "png", file);`

### Card 72: SVG Export
**Q:** How export to SVG format?
**A:** Use Apache Batik's SVGGraphics2D to draw chart, then serialize

### Card 73: Resolution
**Q:** What resolution for web vs print?
**A:** 72 DPI for web, 150-300 DPI for print (higher quality)

### Card 74: Embed in Report
**Q:** How embed chart in PDF report?
**A:** Export chart as image, insert into PDF using iText or similar

### Card 75: Responsive HTML
**Q:** How make chart responsive in web?
**A:** Use JavaScript charting library or export SVG that scales

---

## JavaFX vs Swing

### Card 76: JavaFX Charts
**Q:** What chart package in JavaFX?
**A:** `javafx.scene.chart` with XYChart, PieChart, etc.

### Card 77: Swing vs JavaFX
**Q:** When prefer JavaFX over Swing for charts?
**A:** For modern UI, better graphics support, CSS styling

### Card 78: Chart Panel
**Q:** How display JFreeChart in Swing container?
**A:** `ChartPanel panel = new ChartPanel(chart); add(panel);`

### Card 79: FXML Integration
**Q:** How use charts with FXML?
**A:** Define Chart in FXML, inject with @FXML, set data in controller

### Card 80: CSS Styling
**Q:** How style JavaFX charts with CSS?
**A:** Use `-fx-chart-*` selectors (e.g., `-fx-background-color`)

---

## Real-World Applications

### Card 81: Dashboard Layout
**Q:** What makes good dashboard layout?
**A:** Most important at top-left, related metrics grouped, consistent sizing

### Card 82: KPI Display
**Q:** Best chart for single KPI?
**A:** Gauge, bullet chart, or large number with comparison to target

### Card 83: Time Series Analysis
**Q:** Charts for time series analysis?
**A:** Line chart, area chart, sparklines, annotated events

### Card 84: Comparison Chart
**Q:** Best charts for comparing groups?
**A:** Grouped bar, box plot, violin plot, radar chart

### Card 85: Distribution Chart
**Q:** Best charts for distributions?
**A:** Histogram, density plot, box plot, violin plot

---

## Color Theory

### Card 86: Color Blind Friendly
**Q:** What colors for color-blind accessibility?
**A:** Use patterns in addition to colors, or palette like ColorBrewer "viridis"

### Card 87: Brand Colors
**Q:** How incorporate brand colors?
**A:** Create custom Color[] array with brand hex codes

### Card 88: Sequential Palette
**Q:** What makes good sequential color palette?
**A:** Single hue varying from light to dark, perceptually uniform

### Card 89: Diverging Palette
**Q:** What is diverging palette?
**A:** Two hues meeting at neutral center - for positive/negative values

### Card 90: Heatmap Colors
**Q:** Best color scheme for heatmaps?
**A:** Sequential or diverging depending on data nature; often white-yellow-red

---

## Accessibility

### Card 91: Alt Text
**Q:** How make charts accessible?
**A:** Provide alt text/description, use patterns not just color

### Card 92: Screen Reader
**Q:** How support screen readers?
**A:** Add text summary of chart, ensure proper reading order

### Card 93: High Contrast
**Q:** When use high contrast colors?
**A:** For presentations, accessibility, or printing

### Card 94: Font Size Minimum
**Q:** What's minimum font size for readability?
**A:** 8-10pt for labels, 12-14pt for axis labels

### Card 95: Data Table Alternative
**Q:** When provide data table alongside chart?
**A:** When exact values needed, for accessibility, or when chart is complex

---

## Advanced Techniques

### Card 96: Custom Painter
**Q:** How create custom chart renderer?
**A:** Extend ChartPanel, override paintComponent(), use Graphics2D

### Card 97: Composite Chart
**Q:** How combine multiple chart types?
**A:** Use CombinedDomainXYPlot or CombinedCategoryPlot

### Card 98: Dynamic Legend
**Q:** How make legend interactive?
**A:** Add ItemStroke, ItemPaint for each series, handle clicks

### Card 99: Annotation Layer
**Q:** How add callouts/annotations?
**A:** Use Annotation interface, add to plot with `addAnnotation()`

### Card 100: Performance Monitoring
**Q:** How monitor chart performance?
**A:** Use profiling tools, check render time, optimize data operations