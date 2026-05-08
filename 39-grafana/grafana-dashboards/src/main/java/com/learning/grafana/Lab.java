package com.learning.grafana;

import java.time.*;
import java.util.*;
import java.util.stream.*;

public class Lab {
    record Panel(String title, String type, String datasource, String query, int gridPos) {}

    record Row(String title, List<Panel> panels) {}

    record Datasource(String name, String type, String url) {}

    record Dashboard(String title, List<Row> rows, List<Datasource> datasources) {
        String render() {
            var sb = new StringBuilder();
            sb.append("Dashboard: ").append(title).append("\n");
            sb.append("=".repeat(title.length() + 10)).append("\n\n");
            for (var ds : datasources)
                sb.append("Datasource: ").append(ds.name).append(" (").append(ds.type).append(") -> ").append(ds.url).append("\n");
            sb.append("\n");
            for (var row : rows) {
                sb.append("Row: ").append(row.title()).append("\n");
                sb.append("-".repeat(row.title().length() + 5)).append("\n");
                for (var p : row.panels()) {
                    sb.append("  [").append(p.type()).append("] ").append(p.title());
                    sb.append(" | query: ").append(p.query());
                    sb.append(" | source: ").append(p.datasource()).append("\n");
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    interface TimeSeriesData {
        Map<Instant, Double> generate(int points);
    }

    static class MetricGenerator implements TimeSeriesData {
        final String pattern;
        final double base, variance;

        MetricGenerator(String pattern, double base, double variance) {
            this.pattern = pattern; this.base = base; this.variance = variance;
        }

        public Map<Instant, Double> generate(int points) {
            var data = new LinkedHashMap<Instant, Double>();
            var now = Instant.now();
            var rand = new Random(42);
            for (int i = 0; i < points; i++) {
                var ts = now.minus(Duration.ofMinutes((long) (points - i) * 5));
                double val = switch (pattern) {
                    case "sine" -> base + variance * Math.sin(i * 0.3) + rand.nextGaussian() * variance * 0.1;
                    case "ramp" -> base + (double) i / points * variance + rand.nextGaussian() * variance * 0.05;
                    default -> base + rand.nextGaussian() * variance;
                };
                data.put(ts, val);
            }
            return data;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Grafana Dashboard Concepts Lab ===\n");

        System.out.println("1. Dashboard Definition:");
        var prometheus = new Datasource("Prometheus", "prometheus", "http://prometheus:9090");
        var cloudwatch = new Datasource("CloudWatch", "cloudwatch", "arn:aws:...");
        var graphite = new Datasource("Graphite", "graphite", "http://graphite:8080");

        var dashboard = new Dashboard("Production Monitoring",
            List.of(
                new Row("Application Metrics",
                    List.of(
                        new Panel("HTTP Request Rate", "graph", "Prometheus", "rate(http_requests_total[5m])", 1),
                        new Panel("Error Rate", "graph", "Prometheus", "rate(http_errors_total[5m])", 2),
                        new Panel("P95 Latency", "graph", "Prometheus", "histogram_quantile(0.95, rate(...))", 3)
                    )
                ),
                new Row("Infrastructure",
                    List.of(
                        new Panel("CPU Usage", "gauge", "Prometheus", "avg(rate(cpu_seconds_total[1m]))", 4),
                        new Panel("Memory Usage", "gauge", "Prometheus", "process_resident_memory_bytes", 5),
                        new Panel("Disk I/O", "graph", "CloudWatch", "AWS/EBS VolumeReadOps", 6)
                    )
                ),
                new Row("Business Metrics",
                    List.of(
                        new Panel("Orders/Hour", "stat", "Graphite", "stats.orders.hourly.count", 7),
                        new Panel("Revenue", "bar-gauge", "Prometheus", "sum(revenue_total)", 8)
                    )
                )
            ),
            List.of(prometheus, cloudwatch, graphite)
        );

        System.out.println(dashboard.render());

        System.out.println("2. Panel Types:");
        System.out.println("   Time series: line graphs over time");
        System.out.println("   Stat: single numeric value with sparkline");
        System.out.println("   Bar gauge: horizontal/vertical threshold bars");
        System.out.println("   Table: tabular data with sorting/filtering");
        System.out.println("   Heatmap: 2D color-coded distribution");
        System.out.println("   Pie chart / Candlestick / Logs / Trace");

        System.out.println("\n3. Data Source Types:");
        System.out.println("   Prometheus, Graphite, InfluxDB, Elasticsearch");
        System.out.println("   CloudWatch, Azure Monitor, Google Cloud Monitoring");
        System.out.println("   Loki (logs), Tempo (traces), SQL databases");

        System.out.println("\n4. Generated Time Series Data (sine pattern):");
        var gen = new MetricGenerator("sine", 100, 30);
        var data = gen.generate(10);
        data.forEach((ts, val) ->
            System.out.println("   " + ts.toString().substring(0, 19) + " -> " + String.format("%.1f", val)));

        System.out.println("\n5. Alerting:");
        System.out.println("   Alert rules evaluate against dashboard queries");
        System.out.println("   Notification channels: Slack, PagerDuty, Email, Webhook");
        System.out.println("   Alert states: OK, Pending, Alerting");

        System.out.println("\n6. Annotations:");
        System.out.println("   Events overlaid on graphs (deployments, incidents)");
        System.out.println("   Query annotations from Prometheus, Elasticsearch, etc.");

        System.out.println("\n7. Variables / Templating:");
        System.out.println("   ${env}, ${service}, ${instance} - dynamic dashboard filtering");
        System.out.println("   Query variables: label_values(up, instance)");

        System.out.println("\n8. Provisioning:");
        System.out.println("   dashboards/ directory with JSON/YAML files");
        System.out.println("   datasources/ directory with datasource configs");
        System.out.println("   Grafana REST API for CRUD on dashboards");

        System.out.println("\n=== Lab Complete ===");
    }
}
