package visualization;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ChartComponents {
    public static class ChartColors {
        public static final String[] CATEGORICAL = {
            "#1f77b4", "#ff7f0e", "#2ca02c", "#d62728", "#9467bd",
            "#8c564b", "#e377c2", "#7f7f7f", "#bcbd22", "#17becf"
        };
        public static final String[] SEQUENTIAL = {
            "#f7fbff", "#deebf7", "#c6dbef", "#9ecae1", "#6baed6",
            "#4292c6", "#2171b5", "#08519c", "#08306b"
        };
        public static final String[] DIVERGING = {
            "#2166ac", "#4393c3", "#92c5de", "#d1e5f0", "#f7f7f7",
            "#fddbc7", "#f4a582", "#d6604d", "#b2182b"
        };
    }

    public static Color hexToColor(String hex) {
        return new Color(
            Integer.valueOf(hex.substring(1, 3), 16),
            Integer.valueOf(hex.substring(3, 5), 16),
            Integer.valueOf(hex.substring(5, 7), 16)
        );
    }

    public static abstract class Chart {
        protected String title = "";
        protected String xAxisLabel = "";
        protected String yAxisLabel = "";
        protected int width = 800;
        protected int height = 600;
        protected int margin = 60;
        protected Color background = Color.WHITE;
        
        public abstract void render(Graphics2D g2d);
        
        public BufferedImage toImage() {
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            render(g2d);
            return img;
        }
        
        protected void drawTitle(Graphics2D g2d) {
            g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            g2d.drawString(title, (width - titleWidth) / 2, 30);
        }
        
        protected void drawAxes(Graphics2D g2d, double xMin, double xMax, double yMin, double yMax) {
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
            
            int plotLeft = margin;
            int plotRight = width - margin;
            int plotTop = margin;
            int plotBottom = height - margin;
            
            g2d.setColor(Color.BLACK);
            g2d.draw(new Line2D.Double(plotLeft, plotBottom, plotRight, plotBottom));
            g2d.draw(new Line2D.Double(plotLeft, plotTop, plotLeft, plotBottom));
            
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            
            double xRange = xMax - xMin;
            int xTicks = 10;
            double xStep = xRange / xTicks;
            for (int i = 0; i <= xTicks; i++) {
                double xVal = xMin + i * xStep;
                int px = plotLeft + (int) ((xVal - xMin) / xRange * (plotRight - plotLeft));
                g2d.draw(new Line2D.Double(px, plotBottom, px, plotBottom + 5));
                g2d.drawString(formatNumber(xVal), px - 15, plotBottom + 20);
            }
            
            double yRange = yMax - yMin;
            int yTicks = 10;
            double yStep = yRange / yTicks;
            for (int i = 0; i <= yTicks; i++) {
                double yVal = yMax - i * yStep;
                int py = plotTop + (int) (i * (plotBottom - plotTop) / yTicks);
                g2d.draw(new Line2D.Double(plotLeft - 5, py, plotLeft, py));
                g2d.drawString(formatNumber(yVal), 5, py + 4);
            }
            
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2d.drawString(xAxisLabel, width / 2 - 30, height - 10);
            
            GraphicsContext gc = new GraphicsContext(g2d);
            g2d.translate(15, height / 2);
            g2d.rotate(-Math.PI / 2);
            g2d.drawString(yAxisLabel, -30, 0);
            g2d.rotate(Math.PI / 2);
            g2d.translate(-15, -height / 2);
        }
        
        protected String formatNumber(double val) {
            if (Math.abs(val) >= 1000000) return String.format("%.1fM", val / 1000000);
            if (Math.abs(val) >= 1000) return String.format("%.1fK", val / 1000);
            if (val == (int) val) return String.valueOf((int) val);
            return String.format("%.1f", val);
        }
        
        public void savePNG(String path) throws java.io.IOException {
            BufferedImage img = toImage();
            javax.imageio.ImageIO.write(img, "PNG", new java.io.File(path));
        }
    }
    
    public static class LineChart extends Chart {
        private List<double[]> dataPoints;
        private List<String> seriesNames;
        private List<Color> seriesColors;
        private boolean showPoints = true;
        private boolean showGrid = true;
        
        public LineChart() {
            this.dataPoints = new ArrayList<>();
            this.seriesNames = new ArrayList<>();
            this.seriesColors = new ArrayList<>();
        }
        
        public LineChart title(String title) { this.title = title; return this; }
        public LineChart xAxisLabel(String label) { this.xAxisLabel = label; return this; }
        public LineChart yAxisLabel(String label) { this.yAxisLabel = label; return this; }
        public LineChart showPoints(boolean show) { this.showPoints = show; return this; }
        public LineChart showGrid(boolean show) { this.showGrid = show; return this; }
        
        public LineChart addSeries(String name, double[] x, double[] y) {
            dataPoints.add(x);
            seriesNames.add(name);
            seriesColors.add(ChartColors.CATEGORICAL[seriesNames.size() % ChartColors.CATEGORICAL.length]);
            return this;
        }
        
        public void render(Graphics2D g2d) {
            g2d.setColor(background);
            g2d.fillRect(0, 0, width, height);
            
            if (!title.isEmpty()) drawTitle(g2d);
            
            double[] allX = new double[0];
            double[] allY = new double[0];
            for (double[] points : dataPoints) {
                allX = concatenate(allX, dataPoints.get(dataPoints.indexOf(points)));
            }
            
            double xMin = Double.MAX_VALUE, xMax = Double.MIN_VALUE;
            double yMin = Double.MAX_VALUE, yMax = Double.MIN_VALUE;
            
            for (int s = 0; s < dataPoints.size(); s++) {
                double[] x = dataPoints.get(s);
                double[] y = getYForSeries(s);
                for (int i = 0; i < x.length; i++) {
                    xMin = Math.min(xMin, x[i]);
                    xMax = Math.max(xMax, x[i]);
                    yMin = Math.min(yMin, y[i]);
                    yMax = Math.max(yMax, y[i]);
                }
            }
            
            drawAxes(g2d, xMin, xMax, yMin, yMax);
            
            if (showGrid) {
                g2d.setColor(new Color(230, 230, 230));
                int plotLeft = margin, plotRight = width - margin;
                int plotTop = margin, plotBottom = height - margin;
                for (int i = 0; i <= 10; i++) {
                    int x = plotLeft + i * (plotRight - plotLeft) / 10;
                    g2d.drawLine(x, plotTop, x, plotBottom);
                    int y = plotTop + i * (plotBottom - plotTop) / 10;
                    g2d.drawLine(plotLeft, y, plotRight, y);
                }
            }
            
            for (int s = 0; s < dataPoints.size(); s++) {
                double[] x = dataPoints.get(s);
                double[] y = getYForSeries(s);
                drawLine(g2d, x, y, xMin, xMax, yMin, yMax, seriesColors.get(s));
            }
            
            drawLegend(g2d);
        }
        
        private double[] getYForSeries(int seriesIndex) {
            return dataPoints.get(seriesIndex);
        }
        
        private double[] concatenate(double[] a, double[] b) {
            double[] result = new double[a.length + b.length];
            System.arraycopy(a, 0, result, 0, a.length);
            System.arraycopy(b, 0, result, a.length, b.length);
            return result;
        }
        
        private void drawLine(Graphics2D g2d, double[] x, double[] y, 
                              double xMin, double xMax, double yMin, double yMax,
                              Color color) {
            int plotLeft = margin, plotRight = width - margin;
            int plotTop = margin, plotBottom = height - margin;
            
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(2));
            
            Path2D path = new Path2D.Double();
            boolean first = true;
            
            for (int i = 0; i < x.length; i++) {
                int px = plotLeft + (int) ((x[i] - xMin) / (xMax - xMin) * (plotRight - plotLeft));
                int py = plotBottom - (int) ((y[i] - yMin) / (yMax - yMin) * (plotBottom - plotTop));
                
                if (first) {
                    path.moveTo(px, py);
                    first = false;
                } else {
                    path.lineTo(px, py);
                }
            }
            g2d.draw(path);
            
            if (showPoints) {
                g2d.setColor(color);
                for (int i = 0; i < x.length; i++) {
                    int px = plotLeft + (int) ((x[i] - xMin) / (xMax - xMin) * (plotRight - plotLeft));
                    int py = plotBottom - (int) ((y[i] - yMin) / (yMax - yMin) * (plotBottom - plotTop));
                    g2d.fill(new Ellipse2D.Double(px - 4, py - 4, 8, 8));
                }
            }
        }
        
        private void drawLegend(Graphics2D g2d) {
            if (seriesNames.isEmpty()) return;
            
            int legendX = width - margin - 150;
            int legendY = margin + 20;
            
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
            for (int i = 0; i < seriesNames.size(); i++) {
                g2d.setColor(seriesColors.get(i));
                g2d.fill(new Rectangle2D.Double(legendX, legendY + i * 20, 15, 10));
                g2d.setColor(Color.BLACK);
                g2d.drawString(seriesNames.get(i), legendX + 20, legendY + i * 20 + 10);
            }
        }
    }
    
    public static class BarChart extends Chart {
        private List<String> categories;
        private List<double[]> seriesData;
        private List<String> seriesNames;
        private boolean horizontal = false;
        private boolean stacked = false;
        
        public BarChart() {
            this.categories = new ArrayList<>();
            this.seriesData = new ArrayList<>();
            this.seriesNames = new ArrayList<>();
        }
        
        public BarChart categories(String... cats) {
            this.categories = Arrays.asList(cats);
            return this;
        }
        
        public BarChart addSeries(String name, double... values) {
            this.seriesNames.add(name);
            this.seriesData.add(values);
            return this;
        }
        
        public BarChart horizontal(boolean h) { this.horizontal = h; return this; }
        public BarChart stacked(boolean s) { this.stacked = s; return this; }
        
        public void render(Graphics2D g2d) {
            g2d.setColor(background);
            g2d.fillRect(0, 0, width, height);
            
            if (!title.isEmpty()) drawTitle(g2d);
            
            if (horizontal) {
                renderHorizontal(g2d);
            } else {
                renderVertical(g2d);
            }
        }
        
        private void renderVertical(Graphics2D g2d) {
            int plotLeft = margin, plotRight = width - margin - 150;
            int plotTop = margin + 40, plotBottom = height - margin;
            
            double yMax = 0;
            for (double[] series : seriesData) {
                for (double val : series) yMax = Math.max(yMax, val);
            }
            if (stacked) {
                for (int i = 0; i < categories.size(); i++) {
                    double sum = 0;
                    for (double[] series : seriesData) sum += series[i];
                    yMax = Math.max(yMax, sum);
                }
            }
            
            g2d.setColor(Color.BLACK);
            g2d.draw(new Line2D.Double(plotLeft, plotBottom, plotRight, plotBottom));
            
            int barGroupWidth = (plotRight - plotLeft) / categories.size();
            int numSeries = seriesData.size();
            int barWidth = stacked ? barGroupWidth - 20 : (barGroupWidth - 20) / numSeries;
            
            for (int c = 0; c < categories.size(); c++) {
                int groupX = plotLeft + c * barGroupWidth + 10;
                
                double cumulative = 0;
                for (int s = 0; s < numSeries; s++) {
                    double val = seriesData.get(s)[c];
                    int barHeight = (int) (val / yMax * (plotBottom - plotTop));
                    int barX = stacked ? groupX : groupX + s * barWidth;
                    int barY = stacked ? plotBottom - barHeight - (int)(cumulative / yMax * (plotBottom - plotTop)) : plotBottom - barHeight;
                    
                    Color color = hexToColor(ChartColors.CATEGORICAL[s % ChartColors.CATEGORICAL.length]);
                    g2d.setColor(color);
                    g2d.fill(new Rectangle2D.Double(barX, barY, barWidth - 2, barHeight));
                    g2d.setColor(Color.BLACK);
                    g2d.draw(new Rectangle2D.Double(barX, barY, barWidth - 2, barHeight));
                    
                    if (val > 0) {
                        g2d.drawString(formatNumber(val), barX + barWidth / 2 - 10, barY - 5);
                    }
                    
                    if (!stacked) {
                        cumulative += val;
                    }
                }
                
                g2d.setColor(Color.BLACK);
                g2d.drawString(categories.get(c), groupX + barGroupWidth / 2 - 20, plotBottom + 15);
            }
            
            drawLegend(g2d);
        }
        
        private void renderHorizontal(Graphics2D g2d) {
            int plotLeft = margin + 100, plotRight = width - margin - 150;
            int plotTop = margin + 40, plotBottom = height - margin;
            
            double xMax = 0;
            for (double[] series : seriesData) {
                for (double val : series) xMax = Math.max(xMax, val);
            }
            
            int barHeight = (plotBottom - plotTop) / categories.size() - 10;
            
            for (int c = 0; c < categories.size(); c++) {
                int y = plotTop + c * (barHeight + 10) + 5;
                
                g2d.setColor(Color.BLACK);
                g2d.drawString(categories.get(c), plotLeft - 95, y + barHeight / 2 + 5);
                
                double cumulative = 0;
                for (int s = 0; s < seriesData.size(); s++) {
                    double val = seriesData.get(s)[c];
                    int barWidth = (int) (val / xMax * (plotRight - plotLeft));
                    
                    Color color = hexToColor(ChartColors.CATEGORICAL[s % ChartColors.CATEGORICAL.length]);
                    g2d.setColor(color);
                    
                    int x = stacked ? plotLeft + (int)(cumulative / xMax * (plotRight - plotLeft)) : plotLeft;
                    g2d.fill(new Rectangle2D.Double(x, y, barWidth, barHeight));
                    g2d.setColor(Color.BLACK);
                    g2d.draw(new Rectangle2D.Double(x, y, barWidth, barHeight));
                    
                    if (val > 0 && barWidth > 50) {
                        g2d.drawString(formatNumber(val), x + 5, y + barHeight / 2 + 5);
                    }
                    
                    if (!stacked) {
                        cumulative += val;
                    }
                }
            }
            
            g2d.setColor(Color.BLACK);
            g2d.draw(new Line2D.Double(plotLeft, plotTop, plotLeft, plotBottom));
            g2d.draw(new Line2D.Double(plotLeft, plotBottom, plotRight, plotBottom));
            
            drawLegend(g2d);
        }
        
        private void drawLegend(Graphics2D g2d) {
            if (seriesNames.isEmpty()) return;
            
            int legendX = width - margin - 130;
            int legendY = margin + 50;
            
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
            for (int i = 0; i < seriesNames.size(); i++) {
                g2d.setColor(hexToColor(ChartColors.CATEGORICAL[i % ChartColors.CATEGORICAL.length]));
                g2d.fill(new Rectangle2D.Double(legendX, legendY + i * 20, 15, 12));
                g2d.setColor(Color.BLACK);
                g2d.drawString(seriesNames.get(i), legendX + 20, legendY + i * 20 + 10);
            }
        }
    }
    
    public static class ScatterPlot extends Chart {
        private List<double[]> seriesX;
        private List<double[]> seriesY;
        private List<String> seriesNames;
        private boolean showRegression = false;
        
        public ScatterPlot() {
            this.seriesX = new ArrayList<>();
            this.seriesY = new ArrayList<>();
            this.seriesNames = new ArrayList<>();
        }
        
        public ScatterPlot addSeries(String name, double[] x, double[] y) {
            seriesX.add(x);
            seriesY.add(y);
            seriesNames.add(name);
            return this;
        }
        
        public ScatterPlot showRegression(boolean show) { this.showRegression = show; return this; }
        
        public void render(Graphics2D g2d) {
            g2d.setColor(background);
            g2d.fillRect(0, 0, width, height);
            
            if (!title.isEmpty()) drawTitle(g2d);
            
            double xMin = Double.MAX_VALUE, xMax = Double.MIN_VALUE;
            double yMin = Double.MAX_VALUE, yMax = Double.MIN_VALUE;
            
            for (double[] x : seriesX) {
                for (double v : x) {
                    xMin = Math.min(xMin, v);
                    xMax = Math.max(xMax, v);
                }
            }
            for (double[] y : seriesY) {
                for (double v : y) {
                    yMin = Math.min(yMin, v);
                    yMax = Math.max(yMax, v);
                }
            }
            
            drawAxes(g2d, xMin, xMax, yMin, yMax);
            
            for (int s = 0; s < seriesX.size(); s++) {
                double[] x = seriesX.get(s);
                double[] y = seriesY.get(s);
                
                Color color = hexToColor(ChartColors.CATEGORICAL[s % ChartColors.CATEGORICAL.length]);
                g2d.setColor(color);
                
                int plotLeft = margin, plotRight = width - margin;
                int plotTop = margin, plotBottom = height - margin;
                
                for (int i = 0; i < x.length; i++) {
                    int px = plotLeft + (int) ((x[i] - xMin) / (xMax - xMin) * (plotRight - plotLeft));
                    int py = plotBottom - (int) ((y[i] - yMin) / (yMax - yMin) * (plotBottom - plotTop));
                    g2d.fill(new Ellipse2D.Double(px - 5, py - 5, 10, 10));
                }
                
                if (showRegression && x.length > 1) {
                    double[] regression = calculateLinearRegression(x, y);
                    double yIntercept = regression[0];
                    double slope = regression[1];
                    
                    g2d.setColor(new Color(100, 100, 100));
                    g2d.setStroke(new BasicStroke(2));
                    
                    int x1 = plotLeft;
                    int y1 = plotBottom - (int) ((yIntercept + slope * xMin - yMin) / (yMax - yMin) * (plotBottom - plotTop));
                    int x2 = plotRight;
                    int y2 = plotBottom - (int) ((yIntercept + slope * xMax - yMin) / (yMax - yMin) * (plotBottom - plotTop));
                    
                    g2d.draw(new Line2D.Double(x1, y1, x2, y2));
                }
            }
            
            drawLegend(g2d);
        }
        
        private double[] calculateLinearRegression(double[] x, double[] y) {
            int n = x.length;
            double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
            
            for (int i = 0; i < n; i++) {
                sumX += x[i];
                sumY += y[i];
                sumXY += x[i] * y[i];
                sumX2 += x[i] * x[i];
            }
            
            double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
            double yIntercept = (sumY - slope * sumX) / n;
            
            return new double[]{yIntercept, slope};
        }
        
        private void drawLegend(Graphics2D g2d) {
            if (seriesNames.isEmpty()) return;
            
            int legendX = width - margin - 130;
            int legendY = margin + 50;
            
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
            for (int i = 0; i < seriesNames.size(); i++) {
                g2d.setColor(hexToColor(ChartColors.CATEGORICAL[i % ChartColors.CATEGORICAL.length]));
                g2d.fill(new Ellipse2D.Double(legendX, legendY + i * 20, 12, 12));
                g2d.setColor(Color.BLACK);
                g2d.drawString(seriesNames.get(i), legendX + 18, legendY + i * 20 + 10);
            }
        }
    }
    
    public static class PieChart extends Chart {
        private List<String> labels;
        private List<Double> values;
        private boolean showPercentages = true;
        private boolean showLabels = true;
        
        public PieChart() {
            this.labels = new ArrayList<>();
            this.values = new ArrayList<>();
        }
        
        public PieChart addSlice(String label, double value) {
            this.labels.add(label);
            this.values.add(value);
            return this;
        }
        
        public PieChart showPercentages(boolean show) { this.showPercentages = show; return this; }
        public PieChart showLabels(boolean show) { this.showLabels = show; return this; }
        
        public void render(Graphics2D g2d) {
            g2d.setColor(background);
            g2d.fillRect(0, 0, width, height);
            
            if (!title.isEmpty()) drawTitle(g2d);
            
            double total = values.stream().mapToDouble(Double::doubleValue).sum();
            
            int centerX = width / 2 - 50;
            int centerY = height / 2;
            int radius = Math.min(width, height) / 2 - 100;
            
            double startAngle = 90;
            
            for (int i = 0; i < values.size(); i++) {
                double percentage = values.get(i) / total;
                double sweepAngle = percentage * 360;
                
                Color color = hexToColor(ChartColors.CATEGORICAL[i % ChartColors.CATEGORICAL.length]);
                g2d.setColor(color);
                
                Arc2D arc = new Arc2D.Double(
                    centerX - radius, centerY - radius,
                    radius * 2, radius * 2,
                    startAngle, -sweepAngle, Arc2D.PIE
                );
                g2d.fill(arc);
                
                g2d.setColor(Color.WHITE);
                g2d.draw(arc);
                
                if (sweepAngle > 15) {
                    double midAngle = Math.toRadians(startAngle - sweepAngle / 2);
                    int labelX = centerX + (int) ((radius + 40) * Math.cos(midAngle));
                    int labelY = centerY - (int) ((radius + 40) * Math.sin(midAngle));
                    
                    if (showPercentages) {
                        g2d.setColor(Color.BLACK);
                        g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
                        String text = String.format("%.1f%%", percentage * 100);
                        g2d.drawString(text, labelX - 20, labelY + 4);
                    }
                }
                
                startAngle -= sweepAngle;
            }
            
            drawLegend(g2d);
        }
        
        private void drawLegend(Graphics2D g2d) {
            int legendX = width - 180;
            int legendY = margin + 40;
            
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
            for (int i = 0; i < labels.size(); i++) {
                g2d.setColor(hexToColor(ChartColors.CATEGORICAL[i % ChartColors.CATEGORICAL.length]));
                g2d.fill(new Rectangle2D.Double(legendX, legendY + i * 25, 15, 15));
                g2d.setColor(Color.BLACK);
                g2d.drawString(labels.get(i), legendX + 20, legendY + i * 25 + 12);
            }
        }
    }
    
    public static class Histogram extends Chart {
        private double[] data;
        private int numBins = 10;
        
        public Histogram() {}
        
        public Histogram data(double[] data) {
            this.data = data;
            return this;
        }
        
        public Histogram bins(int bins) {
            this.numBins = bins;
            return this;
        }
        
        public void render(Graphics2D g2d) {
            g2d.setColor(background);
            g2d.fillRect(0, 0, width, height);
            
            if (!title.isEmpty()) drawTitle(g2d);
            
            double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
            for (double v : data) {
                min = Math.min(min, v);
                max = Math.max(max, v);
            }
            
            double binWidth = (max - min) / numBins;
            int[] frequencies = new int[numBins];
            
            for (double v : data) {
                int binIndex = (int) Math.min(numBins - 1, (v - min) / binWidth);
                frequencies[binIndex]++;
            }
            
            int maxFreq = Arrays.stream(frequencies).max().orElse(1);
            
            drawAxes(g2d, min, max, 0, maxFreq);
            
            int plotLeft = margin, plotRight = width - margin;
            int plotTop = margin, plotBottom = height - margin;
            
            int barWidth = (plotRight - plotLeft) / numBins - 4;
            
            g2d.setColor(hexToColor(ChartColors.SEQUENTIAL[5]));
            for (int i = 0; i < numBins; i++) {
                int barHeight = (int) ((double) frequencies[i] / maxFreq * (plotBottom - plotTop));
                int x = plotLeft + i * (barWidth + 4) + 2;
                int y = plotBottom - barHeight;
                
                g2d.fill(new Rectangle2D.Double(x, y, barWidth, barHeight));
                g2d.setColor(Color.BLACK);
                g2d.draw(new Rectangle2D.Double(x, y, barWidth, barHeight));
                
                g2d.drawString(String.format("%.0f", frequencies[i]), x + barWidth / 2 - 5, y - 5);
            }
        }
    }
    
    public static class Heatmap extends Chart {
        private String[] rowLabels;
        private String[] colLabels;
        private double[][] values;
        private double minVal, maxVal;
        
        public Heatmap() {}
        
        public Heatmap rowLabels(String... labels) {
            this.rowLabels = labels;
            return this;
        }
        
        public Heatmap colLabels(String... labels) {
            this.colLabels = labels;
            return this;
        }
        
        public Heatmap values(double[][] values) {
            this.values = values;
            minVal = Double.MAX_VALUE;
            maxVal = Double.MIN_VALUE;
            for (double[] row : values) {
                for (double v : row) {
                    minVal = Math.min(minVal, v);
                    maxVal = Math.max(maxVal, v);
                }
            }
            return this;
        }
        
        public void render(Graphics2D g2d) {
            g2d.setColor(background);
            g2d.fillRect(0, 0, width, height);
            
            if (!title.isEmpty()) drawTitle(g2d);
            
            int plotLeft = margin + 100;
            int plotTop = margin + 20;
            int cellWidth = 60;
            int cellHeight = 40;
            
            for (int r = 0; r < rowLabels.length; r++) {
                g2d.setColor(Color.BLACK);
                g2d.drawString(rowLabels[r], 5, plotTop + r * cellHeight + cellHeight / 2 + 5);
            }
            
            g2d.translate(plotLeft + colLabels.length * cellWidth / 2, plotTop - 10);
            g2d.rotate(-Math.PI / 2);
            g2d.drawString(xAxisLabel, -30, 0);
            g2d.rotate(Math.PI / 2);
            g2d.translate(-(plotLeft + colLabels.length * cellWidth / 2), -(plotTop - 10));
            
            for (int c = 0; c < colLabels.length; c++) {
                g2d.setColor(Color.BLACK);
                String label = colLabels[c].length() > 8 ? colLabels[c].substring(0, 8) : colLabels[c];
                g2d.drawString(label, plotLeft + c * cellWidth + cellWidth / 2 - 20, plotTop - 5);
            }
            
            for (int r = 0; r < values.length; r++) {
                for (int c = 0; c < values[r].length; c++) {
                    double normalized = (values[r][c] - minVal) / (maxVal - minVal);
                    Color color = interpolateColor(
                        hexToColor(ChartColors.SEQUENTIAL[0]),
                        hexToColor(ChartColors.SEQUENTIAL[8]),
                        normalized
                    );
                    g2d.setColor(color);
                    g2d.fill(new Rectangle2D.Double(
                        plotLeft + c * cellWidth,
                        plotTop + r * cellHeight,
                        cellWidth - 2,
                        cellHeight - 2
                    ));
                    
                    g2d.setColor(Color.BLACK);
                    String valStr = values[r][c] > 1000 ? 
                        String.format("%.0f", values[r][c]) : 
                        String.format("%.1f", values[r][c]);
                    g2d.drawString(valStr, 
                        plotLeft + c * cellWidth + cellWidth / 2 - 15,
                        plotTop + r * cellHeight + cellHeight / 2 + 4
                    );
                }
            }
        }
        
        private Color interpolateColor(Color c1, Color c2, double t) {
            t = Math.max(0, Math.min(1, t));
            int r = (int) (c1.getRed() + t * (c2.getRed() - c1.getRed()));
            int g = (int) (c1.getGreen() + t * (c2.getGreen() - c1.getGreen()));
            int b = (int) (c1.getBlue() + t * (c2.getBlue() - c1.getBlue()));
            return new Color(r, g, b);
        }
    }
    
    public static class BoxPlot extends Chart {
        private List<double[]> seriesData;
        private List<String> seriesNames;
        
        public BoxPlot() {
            this.seriesData = new ArrayList<>();
            this.seriesNames = new ArrayList<>();
        }
        
        public BoxPlot addSeries(String name, double[] data) {
            this.seriesData.add(data);
            this.seriesNames.add(name);
            return this;
        }
        
        public void render(Graphics2D g2d) {
            g2d.setColor(background);
            g2d.fillRect(0, 0, width, height);
            
            if (!title.isEmpty()) drawTitle(g2d);
            
            double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
            for (double[] data : seriesData) {
                for (double v : data) {
                    min = Math.min(min, v);
                    max = Math.max(max, v);
                }
            }
            
            drawAxes(g2d, -0.5, seriesData.size() - 0.5, min, max);
            
            int plotLeft = margin;
            int plotTop = margin;
            int plotBottom = height - margin;
            
            int boxWidth = 60;
            int spacing = (width - 2 * margin) / seriesData.size();
            
            for (int i = 0; i < seriesData.size(); i++) {
                double[] data = seriesData.get(i);
                Arrays.sort(data);
                
                double q1 = quantile(data, 0.25);
                double median = quantile(data, 0.5);
                double q3 = quantile(data, 0.75);
                double iqr = q3 - q1;
                double lowerWhisker = Math.max(data[0], q1 - 1.5 * iqr);
                double upperWhisker = Math.min(data[data.length - 1], q3 + 1.5 * iqr);
                
                int centerX = plotLeft + i * spacing + spacing / 2;
                
                g2d.setColor(hexToColor(ChartColors.CATEGORICAL[i % ChartColors.CATEGORICAL.length]));
                
                int yMin = plotBottom - (int) ((upperWhisker - min) / (max - min) * (plotBottom - plotTop));
                int yMax = plotBottom - (int) ((lowerWhisker - min) / (max - min) * (plotBottom - plotTop));
                g2d.draw(new Line2D.Double(centerX, yMin, centerX, yMax));
                g2d.draw(new Line2D.Double(centerX - 5, yMin, centerX + 5, yMin));
                g2d.draw(new Line2D.Double(centerX - 5, yMax, centerX + 5, yMax));
                
                int yQ1 = plotBottom - (int) ((q1 - min) / (max - min) * (plotBottom - plotTop));
                int yQ3 = plotBottom - (int) ((q3 - min) / (max - min) * (plotBottom - plotTop));
                g2d.draw(new Rectangle2D.Double(centerX - boxWidth / 2, yQ3, boxWidth, yQ1 - yQ3));
                
                int yMedian = plotBottom - (int) ((median - min) / (max - min) * (plotBottom - plotTop));
                g2d.setStroke(new BasicStroke(3));
                g2d.draw(new Line2D.Double(centerX - boxWidth / 2, yMedian, centerX + boxWidth / 2, yMedian));
                g2d.setStroke(new BasicStroke(1));
                
                g2d.setColor(Color.BLACK);
                g2d.drawString(seriesNames.get(i), centerX - 30, height - 20);
            }
        }
        
        private double quantile(double[] sortedData, double q) {
            double pos = q * (sortedData.length - 1);
            int idx = (int) Math.floor(pos);
            double frac = pos - idx;
            if (idx + 1 < sortedData.length) {
                return sortedData[idx] * (1 - frac) + sortedData[idx + 1] * frac;
            }
            return sortedData[idx];
        }
    }
}

class GraphicsContext {
    private Graphics2D g2d;
    GraphicsContext(Graphics2D g2d) { this.g2d = g2d; }
}

class ChartDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("Creating charts...");
        
        ChartComponents.LineChart lineChart = new ChartComponents.LineChart()
            .title("Sales Trends")
            .xAxisLabel("Month")
            .yAxisLabel("Revenue ($)")
            .addSeries("2023", new double[]{1,2,3,4,5,6,7,8,9,10,11,12}, 
                       new double[]{100,120,150,140,180,200,190,220,250,280,300,350})
            .addSeries("2024", new double[]{1,2,3,4,5,6,7,8,9,10,11,12}, 
                       new double[]{110,130,160,155,195,220,215,245,275,310,330,380});
        lineChart.savePNG("charts/line_chart.png");
        System.out.println("Saved line_chart.png");
        
        ChartComponents.BarChart barChart = new ChartComponents.BarChart()
            .title("Revenue by Region")
            .categories("North", "South", "East", "West", "Central")
            .addSeries("Q1", 150, 200, 180, 220, 170)
            .addSeries("Q2", 180, 220, 200, 250, 190)
            .addSeries("Q3", 200, 250, 220, 280, 210);
        barChart.savePNG("charts/bar_chart.png");
        System.out.println("Saved bar_chart.png");
        
        ChartComponents.PieChart pieChart = new ChartComponents.PieChart()
            .title("Market Share")
            .addSlice("Product A", 35)
            .addSlice("Product B", 28)
            .addSlice("Product C", 22)
            .addSlice("Product D", 15);
        pieChart.savePNG("charts/pie_chart.png");
        System.out.println("Saved pie_chart.png");
        
        ChartComponents.ScatterPlot scatter = new ChartComponents.ScatterPlot()
            .title("Height vs Weight")
            .xAxisLabel("Height (cm)")
            .yAxisLabel("Weight (kg)")
            .showRegression(true);
        
        double[] heights = {165, 170, 175, 180, 168, 172, 177, 182, 160, 185};
        double[] weights = {65, 72, 78, 85, 68, 75, 80, 88, 62, 92};
        scatter.addSeries("Data", heights, weights);
        scatter.savePNG("charts/scatter_plot.png");
        System.out.println("Saved scatter_plot.png");
        
        ChartComponents.Histogram hist = new ChartComponents.Histogram()
            .title("Age Distribution")
            .bins(15);
        double[] ages = new double[100];
        Random rand = new Random(42);
        for (int i = 0; i < ages.length; i++) {
            ages[i] = 20 + rand.nextGaussian() * 10;
        }
        hist.data(ages);
        hist.savePNG("charts/histogram.png");
        System.out.println("Saved histogram.png");
        
        ChartComponents.BoxPlot boxPlot = new ChartComponents.BoxPlot()
            .title("Score Distribution by Group");
        boxPlot.addSeries("Group A", generateNormalData(50, 75, 10));
        boxPlot.addSeries("Group B", generateNormalData(50, 80, 8));
        boxPlot.addSeries("Group C", generateNormalData(50, 70, 12));
        boxPlot.savePNG("charts/boxplot.png");
        System.out.println("Saved boxplot.png");
        
        ChartComponents.Heatmap heatmap = new ChartComponents.Heatmap()
            .title("Correlation Matrix")
            .rowLabels("A", "B", "C", "D", "E")
            .colLabels("A", "B", "C", "D", "E");
        double[][] corrMatrix = {
            {1.0, 0.8, 0.5, 0.3, 0.1},
            {0.8, 1.0, 0.6, 0.4, 0.2},
            {0.5, 0.6, 1.0, 0.7, 0.4},
            {0.3, 0.4, 0.7, 1.0, 0.8},
            {0.1, 0.2, 0.4, 0.8, 1.0}
        };
        heatmap.values(corrMatrix);
        heatmap.savePNG("charts/heatmap.png");
        System.out.println("Saved heatmap.png");
        
        System.out.println("\nAll charts created successfully!");
    }
    
    private static double[] generateNormalData(int n, double mean, double std) {
        Random rand = new Random();
        double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            double u1 = rand.nextDouble();
            double u2 = rand.nextDouble();
            double z = Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
            data[i] = mean + z * std;
        }
        return data;
    }
}