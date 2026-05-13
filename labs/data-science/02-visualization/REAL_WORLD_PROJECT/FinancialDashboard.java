package visualization.finance;

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

public class FinancialDashboard extends JFrame {
    private List<StockData> stockData;
    private Portfolio portfolio;
    private LocalDate startDate;
    private LocalDate endDate;
    private String selectedStock = "AAPL";
    
    public FinancialDashboard() {
        setTitle("Financial Analytics Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 1000);
        
        stockData = generateSampleStockData();
        portfolio = new Portfolio();
        
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 12, 31);
        
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        add(createMenuBar(), BorderLayout.NORTH);
        add(createToolbar(), BorderLayout.BEFORE_FIRST_LINE);
        add(createMainContent(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Export Chart"));
        fileMenu.add(new JMenuItem("Export Report"));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem("Exit"));
        
        JMenu viewMenu = new JMenu("View");
        viewMenu.add(new JMenuItem("1M"));
        viewMenu.add(new JMenuItem("3M"));
        viewMenu.add(new JMenuItem("6M"));
        viewMenu.add(new JMenuItem("YTD"));
        viewMenu.add(new JMenuItem("1Y"));
        
        JMenu chartMenu = new JMenu("Charts");
        chartMenu.add(new JMenuItem("Candlestick"));
        chartMenu.add(new JMenuItem("Line"));
        chartMenu.add(new JMenuItem("Volume"));
        chartMenu.add(new JMenuItem("Indicators"));
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(chartMenu);
        
        return menuBar;
    }
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(new Color(44, 62, 80));
        toolbar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel stockLabel = new JLabel("Stock:");
        stockLabel.setForeground(Color.WHITE);
        toolbar.add(stockLabel);
        
        String[] stocks = {"AAPL", "GOOGL", "MSFT", "AMZN", "TSLA"};
        JComboBox<String> stockCombo = new JComboBox<>(stocks);
        stockCombo.setPreferredSize(new Dimension(100, 30));
        stockCombo.addActionListener(e -> {
            selectedStock = (String) stockCombo.getSelectedItem();
            updateCharts();
        });
        toolbar.add(stockCombo);
        
        toolbar.add(Box.createHorizontalStrut(30));
        
        JLabel dateLabel = new JLabel("Period:");
        dateLabel.setForeground(Color.WHITE);
        toolbar.add(dateLabel);
        
        String[] periods = {"1M", "3M", "6M", "YTD", "1Y"};
        JComboBox<String> periodCombo = new JComboBox<>(periods);
        periodCombo.setPreferredSize(new Dimension(80, 30));
        toolbar.add(periodCombo);
        
        toolbar.add(Box.createHorizontalStrut(30));
        
        JButton exportBtn = new JButton("Export PNG");
        exportBtn.addActionListener(e -> exportChart());
        toolbar.add(exportBtn);
        
        return toolbar;
    }
    
    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(236, 240, 241));
        
        main.add(createKPIPanel(), BorderLayout.NORTH);
        main.add(createChartGrid(), BorderLayout.CENTER);
        
        return main;
    }
    
    private JPanel createKPIPanel() {
        JPanel kpi = new JPanel(new GridLayout(1, 6, 15, 0));
        kpi.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        kpi.setBackground(new Color(236, 240, 241));
        
        Map<String, Double> metrics = calculateMetrics();
        
        kpi.add(createKPIBox("Current Price", "$" + formatPrice(metrics.get("price")),
            metrics.get("change") >= 0 ? new Color(46, 204, 113) : new Color(231, 76, 60)));
        kpi.add(createKPIBox("Day Change", formatChange(metrics.get("change")),
            metrics.get("change") >= 0 ? new Color(46, 204, 113) : new Color(231, 76, 60)));
        kpi.add(createKPIBox("Day Change %", formatPercent(metrics.get("changePct")),
            metrics.get("change") >= 0 ? new Color(46, 204, 113) : new Color(231, 76, 60)));
        kpi.add(createKPIBox("52W High", "$" + formatPrice(metrics.get("high52")),
            new Color(52, 73, 94)));
        kpi.add(createKPIBox("52W Low", "$" + formatPrice(metrics.get("low52")),
            new Color(52, 73, 94)));
        kpi.add(createKPIBox("Volume", formatVolume(metrics.get("volume")),
            new Color(52, 73, 94)));
        
        return kpi;
    }
    
    private JPanel createKPIBox(String label, String value, Color accent) {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(Color.WHITE);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                
                g2d.setColor(accent);
                g2d.fill(new RoundRectangle2D.Double(0, 0, 4, getHeight(), 4, 4));
            }
        };
        
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(new Color(127, 140, 141));
        box.add(lbl);
        
        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.BOLD, 20));
        val.setForeground(accent);
        box.add(val);
        
        return box;
    }
    
    private JPanel createChartGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 10, 10));
        grid.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
        grid.setBackground(new Color(236, 240, 241));
        
        grid.add(createChartPanel("Price Chart", createPriceChart()));
        grid.add(createChartPanel("Volume", createVolumeChart()));
        grid.add(createChartPanel("Portfolio Allocation", createAllocationChart()));
        grid.add(createChartPanel("Performance vs Benchmark", createPerformanceChart()));
        
        return grid;
    }
    
    private JPanel createChartPanel(String title, BufferedImage chart) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.setColor(new Color(189, 195, 199));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                
                if (chart != null) {
                    int margin = 50;
                    g2d.drawImage(chart, margin, 35, getWidth() - margin - 10, getHeight() - 55, null);
                }
                
                g2d.setColor(new Color(44, 62, 80));
                g2d.setFont(new Font("Arial", Font.BOLD, 13));
                g2d.drawString(title, 12, 22);
            }
        };
        
        return panel;
    }
    
    private BufferedImage createPriceChart() {
        int w = 650, h = 300;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, w, h);
        
        List<StockData> data = stockData.stream()
            .filter(d -> d.symbol.equals(selectedStock))
            .sorted(Comparator.comparing(d -> d.date))
            .collect(Collectors.toList());
        
        if (data.isEmpty()) return img;
        
        double minPrice = data.stream().mapToDouble(d -> d.low).min().orElse(0);
        double maxPrice = data.stream().mapToDouble(d -> d.high).max().orElse(100);
        double priceRange = maxPrice - minPrice;
        
        int margin = 40;
        int chartW = w - margin - 30;
        int chartH = h - margin - 40;
        
        g2d.setColor(new Color(236, 240, 241));
        for (int i = 0; i <= 5; i++) {
            int y = margin + i * chartH / 5;
            g2d.drawLine(margin, y, w - 30, y);
        }
        
        g2d.setColor(new Color(189, 195, 199));
        for (StockData d : data) {
            int x = margin + (int) ((data.indexOf(d) / (double) (data.size() - 1)) * chartW);
            g2d.drawLine(x, margin, x, h - 40);
        }
        
        g2d.setColor(new Color(52, 152, 219));
        g2d.setStroke(new BasicStroke(2));
        
        Path2D pricePath = new Path2D.Double();
        boolean first = true;
        for (StockData d : data) {
            int x = margin + (int) ((data.indexOf(d) / (double) (data.size() - 1)) * chartW);
            int y = h - 40 - (int) ((d.close - minPrice) / priceRange * chartH);
            if (first) {
                pricePath.moveTo(x, y);
                first = false;
            } else {
                pricePath.lineTo(x, y);
            }
        }
        g2d.draw(pricePath);
        
        double[] ma20 = calculateMA(data, 20);
        g2d.setColor(new Color(230, 126, 34));
        g2d.setStroke(new BasicStroke(1.5f));
        
        Path2D maPath = new Path2D.Double();
        first = true;
        for (int i = 0; i < data.size(); i++) {
            if (i >= 19 && !Double.isNaN(ma20[i])) {
                int x = margin + (int) ((i / (double) (data.size() - 1)) * chartW);
                int y = h - 40 - (int) ((ma20[i] - minPrice) / priceRange * chartH);
                if (first) {
                    maPath.moveTo(x, y);
                    first = false;
                } else {
                    maPath.lineTo(x, y);
                }
            }
        }
        g2d.draw(maPath);
        
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        
        for (int i = 0; i <= 5; i++) {
            double price = maxPrice - i * priceRange / 5;
            int y = margin + i * chartH / 5;
            g2d.drawString(String.format("$%.0f", price), 5, y + 4);
        }
        
        return img;
    }
    
    private double[] calculateMA(List<StockData> data, int period) {
        double[] ma = new double[data.size()];
        Arrays.fill(ma, Double.NaN);
        
        for (int i = period - 1; i < data.size(); i++) {
            double sum = 0;
            for (int j = i - period + 1; j <= i; j++) {
                sum += data.get(j).close;
            }
            ma[i] = sum / period;
        }
        
        return ma;
    }
    
    private BufferedImage createVolumeChart() {
        int w = 650, h = 300;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, w, h);
        
        List<StockData> data = stockData.stream()
            .filter(d -> d.symbol.equals(selectedStock))
            .sorted(Comparator.comparing(d -> d.date))
            .collect(Collectors.toList());
        
        if (data.isEmpty()) return img;
        
        double maxVol = data.stream().mapToDouble(d -> d.volume).max().orElse(1);
        
        int margin = 40;
        int chartW = w - margin - 30;
        int chartH = h - margin - 40;
        int barWidth = Math.max(1, chartW / data.size() - 2);
        
        for (int i = 0; i < data.size(); i++) {
            StockData d = data.get(i);
            int x = margin + (i * chartW / data.size()) + 1;
            int barHeight = (int) (d.volume / maxVol * chartH);
            int y = h - 40 - barHeight;
            
            Color barColor = d.close >= d.open ? new Color(46, 204, 113) : new Color(231, 76, 60);
            g2d.setColor(barColor);
            g2d.fill(new Rectangle2D.Double(x, y, barWidth, barHeight));
        }
        
        return img;
    }
    
    private BufferedImage createAllocationChart() {
        int w = 650, h = 300;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, w, h);
        
        String[] sectors = {"Technology", "Healthcare", "Finance", "Consumer", "Industrial"};
        double[] values = {35, 25, 20, 12, 8};
        String[] colors = {"#3498db", "#2ecc71", "#e74c3c", "#9b59b6", "#f39c12"};
        
        int centerX = 150;
        int centerY = 150;
        int radius = 100;
        
        double total = Arrays.stream(values).sum();
        double startAngle = 90;
        
        for (int i = 0; i < values.length; i++) {
            double angle = (values[i] / total) * 360;
            
            g2d.setColor(Color.decode(colors[i]));
            
            Arc2D arc = new Arc2D.Double(
                centerX - radius, centerY - radius,
                radius * 2, radius * 2,
                startAngle, -angle, Arc2D.PIE
            );
            g2d.fill(arc);
            
            g2d.setColor(Color.WHITE);
            g2d.draw(arc);
            
            if (angle > 20) {
                double midAngle = Math.toRadians(startAngle - angle / 2);
                int labelX = centerX + (int) ((radius + 40) * Math.cos(midAngle));
                int labelY = centerY - (int) ((radius + 40) * Math.sin(midAngle));
                
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.drawString(String.format("%.0f%%", values[i]), labelX - 15, labelY + 5);
            }
            
            startAngle -= angle;
        }
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        for (int i = 0; i < sectors.length; i++) {
            g2d.setColor(Color.decode(colors[i]));
            g2d.fill(new Rectangle2D.Double(w - 180, 50 + i * 35, 15, 15));
            g2d.setColor(Color.BLACK);
            g2d.drawString(sectors[i], w - 160, 62 + i * 35);
        }
        
        return img;
    }
    
    private BufferedImage createPerformanceChart() {
        int w = 650, h = 300;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, w, h);
        
        int points = 12;
        double[] stock = generatePerformanceData(points, 1.0);
        double[] benchmark = generatePerformanceData(points, 0.8);
        
        int margin = 50;
        int chartW = w - margin - 30;
        int chartH = h - margin - 40;
        
        g2d.setColor(new Color(236, 240, 241));
        for (int i = 0; i <= 4; i++) {
            int y = margin + i * chartH / 4;
            g2d.drawLine(margin, y, w - 30, y);
        }
        
        g2d.setColor(new Color(46, 204, 113));
        g2d.setStroke(new BasicStroke(2.5f));
        
        Path2D stockPath = new Path2D.Double();
        stockPath.moveTo(margin, h - 40);
        for (int i = 0; i < points; i++) {
            int x = margin + (i * chartW / (points - 1));
            int y = h - 40 - (int) (stock[i] * chartH);
            stockPath.lineTo(x, y);
        }
        g2d.draw(stockPath);
        
        g2d.setColor(new Color(231, 76, 60));
        g2d.setStroke(new BasicStroke(2));
        
        Path2D benchPath = new Path2D.Double();
        benchPath.moveTo(margin, h - 40);
        for (int i = 0; i < points; i++) {
            int x = margin + (i * chartW / (points - 1));
            int y = h - 40 - (int) (benchmark[i] * chartH);
            benchPath.lineTo(x, y);
        }
        g2d.draw(benchPath);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(new Color(46, 204, 113));
        g2d.fill(new Rectangle2D.Double(w - 140, 30, 15, 10));
        g2d.setColor(Color.BLACK);
        g2d.drawString(selectedStock + " (+15.2%)", w - 120, 40);
        
        g2d.setColor(new Color(231, 76, 60));
        g2d.fill(new Rectangle2D.Double(w - 140, 50, 15, 10));
        g2d.setColor(Color.BLACK);
        g2d.drawString("S&P 500 (+8.5%)", w - 120, 60);
        
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(margin, h - 40, margin, margin);
        g2d.drawLine(margin, h - 40, w - 30, h - 40);
        
        return img;
    }
    
    private double[] generatePerformanceData(int points, double factor) {
        double[] data = new double[points];
        Random rand = new Random(42);
        double value = 0.3;
        
        for (int i = 0; i < points; i++) {
            value += (rand.nextDouble() * 0.1 - 0.03) * factor;
            value = Math.max(0.1, Math.min(1.0, value));
            data[i] = value;
        }
        
        return data;
    }
    
    private Map<String, Double> calculateMetrics() {
        Map<String, Double> metrics = new HashMap<>();
        
        StockData latest = stockData.stream()
            .filter(d -> d.symbol.equals(selectedStock))
            .max(Comparator.comparing(d -> d.date))
            .orElse(null);
        
        if (latest != null) {
            metrics.put("price", latest.close);
            metrics.put("change", latest.close - latest.open);
            metrics.put("changePct", ((latest.close - latest.open) / latest.open) * 100);
            metrics.put("volume", (double) latest.volume);
            
            List<StockData> yearData = stockData.stream()
                .filter(d -> d.symbol.equals(selectedStock))
                .sorted(Comparator.comparing(d -> d.date))
                .collect(Collectors.toList());
            
            if (!yearData.isEmpty()) {
                metrics.put("high52", yearData.stream().mapToDouble(d -> d.high).max().orElse(0));
                metrics.put("low52", yearData.stream().mapToDouble(d -> d.low).min().orElse(0));
            }
        } else {
            metrics.put("price", 150.0);
            metrics.put("change", 2.5);
            metrics.put("changePct", 1.7);
            metrics.put("volume", 5000000.0);
            metrics.put("high52", 180.0);
            metrics.put("low52", 120.0);
        }
        
        return metrics;
    }
    
    private JPanel createStatusBar() {
        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT));
        status.setBackground(new Color(44, 62, 80));
        status.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        JLabel statusLabel = new JLabel("Last updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        status.add(statusLabel);
        
        return status;
    }
    
    private void updateCharts() {
        repaint();
    }
    
    private void exportChart() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = createPriceChart();
                ImageIO.write(img, "PNG", fc.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Chart exported successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
            }
        }
    }
    
    private String formatPrice(double val) {
        return String.format("%.2f", val);
    }
    
    private String formatChange(double val) {
        return (val >= 0 ? "+" : "") + String.format("%.2f", val);
    }
    
    private String formatPercent(double val) {
        return (val >= 0 ? "+" : "") + String.format("%.2f%%", val);
    }
    
    private String formatVolume(double val) {
        if (val >= 1_000_000) return String.format("%.1fM", val / 1_000_000);
        if (val >= 1_000) return String.format("%.1fK", val / 1_000);
        return String.format("%.0f", val);
    }
    
    private List<StockData> generateSampleStockData() {
        List<StockData> data = new ArrayList<>();
        String[] symbols = {"AAPL", "GOOGL", "MSFT", "AMZN", "TSLA"};
        Random rand = new Random(42);
        
        for (String symbol : symbols) {
            double price = 100 + rand.nextDouble() * 200;
            LocalDate date = LocalDate.of(2024, 1, 1);
            
            for (int i = 0; i < 252; i++) {
                double change = (rand.nextDouble() - 0.48) * 5;
                double open = price;
                double close = price + change;
                double high = Math.max(open, close) + rand.nextDouble() * 2;
                double low = Math.min(open, close) - rand.nextDouble() * 2;
                long volume = 1000000 + (long) (rand.nextDouble() * 5000000);
                
                data.add(new StockData(symbol, date, open, high, low, close, volume));
                
                price = close;
                date = date.plusDays(1);
                if (date.getDayOfWeek() == DayOfWeek.SATURDAY) date = date.plusDays(2);
                if (date.getDayOfWeek() == DayOfWeek.SUNDAY) date = date.plusDays(1);
            }
        }
        
        return data;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FinancialDashboard().setVisible(true));
    }
}

class StockData {
    String symbol;
    LocalDate date;
    double open, high, low, close;
    long volume;
    
    StockData(String symbol, LocalDate date, double open, double high, double low, double close, long volume) {
        this.symbol = symbol;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }
}

class Portfolio {
    Map<String, Integer> holdings = new HashMap<>();
    double cash = 100000;
    
    Portfolio() {
        holdings.put("AAPL", 100);
        holdings.put("GOOGL", 50);
        holdings.put("MSFT", 75);
        holdings.put("AMZN", 30);
        holdings.put("TSLA", 40);
    }
}