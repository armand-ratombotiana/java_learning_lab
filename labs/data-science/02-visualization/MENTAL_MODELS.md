# Mental Models for Data Visualization

## 1. The Grammar of Graphics

Every chart is composed of layers: **data → aesthetic mapping → geometry → scale → facet**. Like sentence grammar, you can combine these elements to express any visual statement.

| Component | Purpose | Example |
|---|---|---|
| Data | Raw observations | (x, y) pairs |
| Aesthetic mapping | Bind columns to visual channels | x → column A, y → column B, color → category |
| Geometry | Visual mark type | point (scatter), line (line chart), bar (bar chart) |
| Scale | Map data values to screen coordinates | log scale, linear scale, ordinal scale |
| Facet | Split into subplots | one chart per region |

## 2. The Lie Factor (Tufte)

$$ \text{Lie Factor} = \frac{\text{size of effect shown in graphic}}{\text{size of effect in data}} $$

If the Lie Factor ≠ 1.0, the chart is distorting. Truncated y-axes, 3D perspective, and non-zero baselines are common violators.

## 3. Data-Ink Ratio (Tufte)

$$ \text{Data-Ink Ratio} = \frac{\text{ink used for data}}{\text{total ink in graphic}} $$

Maximize this ratio. Remove gridlines, redundant labels, and decorative 3D effects. Every pixel should either encode data or be absent.

## 4. Visual Hierarchy

The eye scans a chart in a Z-pattern (Western readers). Place the most important element at the top-left or center. Use size, color intensity, and position to signal importance in descending order.
