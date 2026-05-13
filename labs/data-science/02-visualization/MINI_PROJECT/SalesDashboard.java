package visualization.dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SalesDashboard extends JFrame {
    private DataFrame salesData;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Component> kpiCards = new LinkedHashMap<>();
    
    public SalesDashboard() {
        setTitle("Sales Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        
        try {
            salesData = loadSalesData("data/sales_data.csv");
        } catch (Exception e) {
            salesData = generateSampleData();
        }
        
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 12, 31);
        
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        add(createHeader(), BorderLayout.NORTH);
        add(createKPIPanel(), BorderLayout.BEFORE_FIRST_LINE);
        add(createChartArea(), BorderLayout.CENTER);
        
        setLocationRelativeTo(null);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel title = new JLabel("Sales Dashboard - 2024");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        
        JButton refreshBtn = new JButton("Refresh Data");
        refreshBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshBtn.addActionListener(e -> updateCharts());
        header.add(refreshBtn, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createKPIPanel() {
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        kpiPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        kpiPanel.setBackground(new Color(245, 245, 245));
        
        Map<String, Double> kpis = calculateKPIs();
        
        kpiPanel.add(createKPICard("Total Revenue", "$" + formatNumber(kpis.get("revenue")), 
            new Color(46, 204, 113)));
        kpiPanel.add(createKPICard("Total Orders", String.valueOf(kpis.get("orders").intValue()),
            new Color(52, 152, 219)));
        kpiPanel.add(createKPICard("Avg Order Value", "$" + formatNumber(kpis.get("avgOrder")),
            new Color(155, 89, 182)));
        kpiPanel.add(createKPICard("Top Region", kpis.get("topRegion").toString(),
            new Color(230, 126, 34)));
        
        return kpiPanel;
    }
    
    private JPanel createKPICard(String title, String value, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(Color.WHITE);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                
                g2d.setColor(color);
                g2d.fill(new RoundRectangle2D.Double(0, 0, 5, getHeight(), 5, 5));
            }
        };
        
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);
        card.add(titleLabel);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(new Color(50, 50, 50));
        card.add(valueLabel);
        
        return card;
    }
    
    private JPanel createChartArea() {
        JPanel chartArea = new JPanel(new GridLayout(2, 2, 15, 15));
        chartArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        chartArea.setBackground(new Color(245, 245, 245));
        
        chartArea.add(createChartPanel("Monthly Sales Trend", createTrendChart()));
        chartArea.add(createChartPanel("Sales by Region", createRegionChart()));
        chartArea.add(createChartPanel("Category Breakdown", createCategoryPieChart()));
        chartArea.add(createChartPanel("Revenue vs Units", createScatterChart()));
        
        return chartArea;
    }
    
    private JPanel createChartPanel(String title, BufferedImage chart) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(Color.WHITE);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                
                if (chart != null) {
                    int imgX = (getWidth() - chart.getWidth()) / 2;
                    int imgY = (getHeight() - chart.getHeight()) / 2;
                    g2d.drawImage(chart, Math.max(20, imgX), Math.max(40, imgY), null);
                }
                
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.setColor(new Color(50, 50, 50));
                g2d.drawString(title, 20, 25);
            }
        };
        
        panel.setPreferredSize(new Dimension(600, 350));
        return panel;
    }
    
    private BufferedImage createTrendChart() {
        ChartComponents.LineChart chart = new ChartComponents.LineChart()
            .title("Monthly Revenue")
            .xAxisLabel("Month")
            .yAxisLabel("Revenue ($)")
            .showGrid(true)
            .showPoints(true);
        
        Map<String, Double> monthlyData = salesData.stream()
            .collect(Collectors.groupingBy(
                r -> r.get("month").toString(),
                Collectors.summingDouble(r -> ((Number) r.get("total")).doubleValue())
            ));
        
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        double[] values = new double[12];
        for (int i = 0; i < months.length; i++) {
            values[i] = monthlyData.getOrDefault(months[i], 0.0);
        }
        
        double[] x = new double[12];
        for (int i = 0; i < 12; i++) x[i] = i + 1;
        
        chart.addSeries("Revenue", x, values);
        
        BufferedImage img = new BufferedImage(550, 280, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        chart.render(g2d);
        return img;
    }
    
    private BufferedImage createRegionChart() {
        ChartComponents.BarChart chart = new ChartComponents.BarChart()
            .title("Sales by Region")
            .horizontal(true);
        
        List<String> regions = Arrays.asList("North", "South", "East", "West", "Central");
        
        for (String region : regions) {
            double total = salesData.stream()
                .filter(r -> r.get("region").toString().equals(region))
                .mapToDouble(r -> ((Number) r.get("total")).doubleValue())
                .sum();
            chart.addSeries(region, total);
        }
        
        BufferedImage img = new BufferedImage(550, 280, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        chart.render(g2d);
        return img;
    }
    
    private BufferedImage createCategoryPieChart() {
        ChartComponents.PieChart chart = new ChartComponents.PieChart()
            .title("Revenue by Category")
            .showPercentages(true);
        
        Map<String, Double> categoryData = salesData.stream()
            .collect(Collectors.groupingBy(
                r -> r.get("category").toString(),
                Collectors.summingDouble(r -> ((Number) r.get("total")).doubleValue())
            ));
        
        categoryData.forEach((cat, val) -> chart.addSlice(cat, val));
        
        BufferedImage img = new BufferedImage(550, 280, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        chart.render(g2d);
        return img;
    }
    
    private BufferedImage createScatterChart() {
        ChartComponents.ScatterPlot chart = new ChartComponents.ScatterPlot()
            .title("Units Sold vs Revenue")
            .xAxisLabel("Units Sold")
            .yAxisLabel("Revenue ($)")
            .showRegression(true);
        
        List<Double> units = new ArrayList<>();
        List<Double> revenue = new ArrayList<>();
        
        for (Row row : salesData.rows()) {
            units.add(((Number) row.get("quantity")).doubleValue());
            revenue.add(((Number) row.get("total")).doubleValue());
        }
        
        double[] unitsArr = units.stream().mapToDouble(Double::doubleValue).toArray();
        double[] revenueArr = revenue.stream().mapToDouble(Double::doubleValue).toArray();
        
        chart.addSeries("Orders", unitsArr, revenueArr);
        
        BufferedImage img = new BufferedImage(550, 280, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        chart.render(g2d);
        return img;
    }
    
    private Map<String, Double> calculateKPIs() {
        Map<String, Double> kpis = new HashMap<>();
        
        double revenue = salesData.stream()
            .mapToDouble(r -> ((Number) r.get("total")).doubleValue())
            .sum();
        kpis.put("revenue", revenue);
        
        double orders = salesData.stream().count();
        kpis.put("orders", orders);
        
        kpis.put("avgOrder", orders > 0 ? revenue / orders : 0);
        
        Map<String, Double> regionTotals = salesData.stream()
            .collect(Collectors.groupingBy(
                r -> r.get("region").toString(),
                Collectors.summingDouble(r -> ((Number) r.get("total")).doubleValue())
            ));
        
        String topRegion = regionTotals.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
        kpis.put("topRegion", (double) topRegion.ordinal()); 
        kpis.put("topRegionStr", (double) 0);
        
        return kpis;
    }
    
    private DataFrame loadSalesData(String path) throws Exception {
        return CSVParser.readCSV(path);
    }
    
    private DataFrame generateSampleData() {
        List<String> cols = Arrays.asList("date", "product", "category", "region", "quantity", "unit_price", "total", "month");
        DataFrame df = new DataFrame(cols);
        
        String[] categories = {"Electronics", "Clothing", "Home", "Sports", "Books"};
        String[] regions = {"North", "South", "East", "West", "Central"};
        String[] products = {"Product A", "Product B", "Product C", "Product D", "Product E"};
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        
        Random rand = new Random(42);
        
        for (int i = 0; i < 500; i++) {
            int month = rand.nextInt(12) + 1;
            String region = regions[rand.nextInt(regions.length)];
            String category = categories[rand.nextInt(categories.length)];
            String product = products[rand.nextInt(products.length)];
            int quantity = rand.nextInt(20) + 1;
            double price = rand.nextDouble() * 200 + 20;
            double total = quantity * price;
            
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", "2024-" + String.format("%02d", month) + "-15");
            row.put("product", product);
            row.put("category", category);
            row.put("region", region);
            row.put("quantity", (double) quantity);
            row.put("unit_price", price);
            row.put("total", total);
            row.put("month", months[month - 1]);
            
            df.addRow(new Row(row));
        }
        
        return df;
    }
    
    private void updateCharts() {
        repaint();
    }
    
    private String formatNumber(double val) {
        if (val >= 1000000) return String.format("%.1fM", val / 1000000);
        if (val >= 1000) return String.format("%.1fK", val / 1000);
        return String.format("%.0f", val);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SalesDashboard().setVisible(true));
    }
}

class Row {
    private Map<String, Object> values;
    public Row(Map<String, Object> values) { this.values = values; }
    public Object get(String key) { return values.get(key); }
    public Map<String, Object> getValues() { return new LinkedHashMap<>(values); }
}

class DataFrame {
    private List<String> columns;
    private List<Row> rows = new ArrayList<>();
    
    public DataFrame(List<String> columns) { this.columns = columns; }
    
    public void addRow(Row row) { rows.add(row); }
    public List<Row> rows() { return rows; }
    public List<Row> stream() { return rows; }
    public int size() { return rows.size(); }
    
    static class Row {
        private Map<String, Object> values;
        public Row(Map<String, Object> values) { this.values = values; }
        public Object get(String key) { return values.get(key); }
    }
}

class CSVParser {
    public static DataFrame readCSV(String path) { return null; }
}

class ChartComponents {
    static class LineChart { LineChart title(String s) { return this; } LineChart xAxisLabel(String s) { return this; } LineChart yAxisLabel(String s) { return this; } LineChart showGrid(boolean b) { return this; } LineChart showPoints(boolean b) { return this; } LineChart addSeries(String name, double[] x, double[] y) { return this; } void render(Graphics2D g2d) {} }
    static class BarChart { BarChart title(String s) { return this; } BarChart horizontal(boolean b) { return this; } BarChart addSeries(String name, double... values) { return this; } void render(Graphics2D g2d) {} }
    static class PieChart { PieChart title(String s) { return this; } PieChart showPercentages(boolean b) { return this; } PieChart addSlice(String label, double value) { return this; } void render(Graphics2D g2d) {} }
    static class ScatterPlot { ScatterPlot title(String s) { return this; } ScatterPlot xAxisLabel(String s) { return this; } ScatterPlot yAxisLabel(String s) { return this; } ScatterPlot showRegression(boolean b) { return this; } ScatterPlot addSeries(String name, double[] x, double[] y) { return this; } void render(Graphics2D g2d) {} }
}