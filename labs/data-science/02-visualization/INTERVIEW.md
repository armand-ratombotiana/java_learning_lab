# Interview Questions: Data Visualization

## Conceptual

1. **Explain the difference between exploratory and explanatory visualizations.**
   - Exploratory: for the analyst to find patterns (quick, many facets, interactive)
   - Explanatory: for the audience to understand a conclusion (polished, focused, annotated)

2. **What is the "data-ink ratio" and why does it matter?**
   - Proportion of ink (pixels) that represents data vs. decoration. Maximizing it reduces chart junk.

3. **When would you use a log scale vs. a linear scale?**
   - Log scale: data spans multiple orders of magnitude, multiplicative differences, power law distributions.

## Coding

4. **Write Java code to create a histogram using XChart.**

```java
public static XYChart createHistogram(double[] data, int bins) {
    double min = Arrays.stream(data).min().orElse(0);
    double max = Arrays.stream(data).max().orElse(1);
    double binWidth = (max - min) / bins;
    
    int[] counts = new int[bins];
    for (double v : data) {
        int bin = (int) ((v - min) / binWidth);
        if (bin >= bins) bin = bins - 1;
        counts[bin]++;
    }
    
    double[] binCenters = new double[bins];
    for (int i = 0; i < bins; i++) {
        binCenters[i] = min + (i + 0.5) * binWidth;
    }
    
    XYChart chart = new XYChart(600, 400);
    chart.addSeries("Frequency", binCenters, counts);
    return chart;
}
```

5. **How would you make a chart accessible to colorblind viewers?**
   - Use shape/pattern encoding in addition to color
   - Choose colorblind-safe palettes (Wong's palette, viridis)
   - Use high contrast (not just red/green differences)

6. **Design a dashboard visualization for monitoring an ML model's performance drift.**
   - Y-axis: accuracy / F1 / AUC
   - X-axis: time (weekly)
   - Color: train vs. test performance (should track together)
   - Annotations: deployment dates, retraining events
   - Facet by: model version
