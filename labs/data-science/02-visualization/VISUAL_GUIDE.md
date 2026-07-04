# Visual Guide: Choosing the Right Chart

## Decision Tree

```
What do you want to show?
│
├── Distribution of one variable?
│   ├── Categorical → Bar chart
│   └── Numeric → Histogram or Density plot
│
├── Relationship between two variables?
│   ├── Both numeric → Scatter plot (+ trend line)
│   ├── One numeric, one categorical → Box plot or Violin plot
│   └── Both categorical → Mosaic plot or Heatmap of counts
│
├── Change over time?
│   ├── Single series → Line chart
│   ├── Multiple series → Line chart with color legend
│   └── Cumulative → Area chart
│
├── Composition of a whole?
│   ├── Few categories (< 6) → Pie chart (use sparingly)
│   └── Many categories → Horizontal bar chart
│
└── Comparing many categories?
    └── Bar chart (sorted descending)
```

## Common Chart Gallery

```
Scatter:     ⋅ ⋅ ⋅ ⋅⋅   ⋅    ⋅    ⋅    ⋅   ⋅⋅ ⋅ ⋅ ⋅⋅
              ⋅   ⋅   ⋅⋅⋅   ⋅   ⋅⋅⋅   ⋅  ⋅⋅   ⋅
                ⋅    ⋅  ⋅  ⋅⋅  ⋅⋅ ⋅  ⋅⋅  ⋅

Line:        ────╱╲───╱╲───╱╲───╱╲───
             ╱    ╲╱  ╲╱  ╲╱  ╲╱  ╲╱

Bar:         ████  ████
             ████  ████  ████
             ████  ████  ████  ████

Box:         ┌─┬──┬─┐
             │ │  │ │
             └─┴──┴─┘
```

## Color Palette Guidelines

| Purpose | Palette | Library Constant |
|---|---|---|
| Sequential | Light → dark blue | `Palette.sequential()` |
| Diverging | Red → white → blue | `Palette.diverging()` |
| Qualitative | Distinct hues | `Palette.category20()` |
| Accessibility | Colorblind-safe | `Palette.colorBlindSafe()` |
