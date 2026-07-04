# Common Mistakes in Data Visualization

## 1. Truncated Y-Axis

Starting the y-axis at a non-zero value exaggerates differences.

```
Bad:  y starts at 490      Good: y starts at 0
     ┌──┐                    ┌──┐
     │  │ 500                │  │
   ──┘  └── 490           ──┘  └── 0
   490  500 looks huge    490  500 looks tiny
```

**Fix**: Always start bar charts at 0. For line charts, use a truncated axis only if the baseline is clearly marked.

## 2. Pie Charts with Many Slices

Human eyes cannot compare angles of >3 categories accurately.

**Fix**: Use a horizontal bar chart sorted by value.

## 3. 3D Charts

3D perspective distorts perception — front bars appear larger than back bars even when values are identical.

**Fix**: Use 2D charts. 3D never adds clarity.

## 4. Overlapping Labels

Axis labels, data labels, or legend entries that collide make charts unreadable.

**Fix**: Rotate x-axis labels 45°, reduce font size, or increase chart dimensions.

```java
chart.getStyler().setXAxisLabelRotation(45);
```

## 5. Rainbow Color Palettes

Using the full spectral rainbow maps to a perceptual scale (ROYGBIV) that is not monotonic — yellow falsely draws attention as the brightest point.

**Fix**: Use a perceptually uniform sequential palette (viridis, inferno).

## 6. Dual Y-Axes

Two different scales on the same chart can be manipulated to make trends appear correlated or diverging at will.

**Fix**: Use separate faceted charts side-by-side with aligned x-axes.
