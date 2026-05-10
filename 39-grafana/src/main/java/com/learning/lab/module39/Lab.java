package com.learning.lab.module39;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 39: Grafana Lab ===\n");

        System.out.println("1. Grafana Server Configuration:");
        System.out.println("   - URL: http://localhost:3000");
        System.out.println("   - Default Credentials: admin/admin");
        System.out.println("   - Configuration: /etc/grafana/grafana.ini");
        System.out.println("   - Data Directory: /var/lib/grafana");

        System.out.println("\n2. Data Sources:");
        dataSourceConfiguration();

        System.out.println("\n3. Dashboard Configuration:");
        dashboardConfiguration();

        System.out.println("\n4. Panels and Visualizations:");
        panelVisualizations();

        System.out.println("\n5. Alerts and Notifications:");
        alertsConfiguration();

        System.out.println("\n6. Variables and Templating:");
        templateVariables();

        System.out.println("\n=== Grafana Lab Complete ===");
    }

    static void dataSourceConfiguration() {
        System.out.println("   Prometheus Data Source:");
        System.out.println("   - URL: http://localhost:9090");
        System.out.println("   - Access: Server (default)");
        System.out.println("   - Scrape Interval: 15s");
        System.out.println("   - Query Timeout: 60s");
        System.out.println("   - Type: Prometheus");

        System.out.println("\n   Elasticsearch Data Source:");
        System.out.println("   - URL: http://localhost:9200");
        System.out.println("   - Index Name: logstash-*");
        System.out.println("   - Time Field: @timestamp");
        System.out.println("   - Version: 8.x");

        System.out.println("\n   InfluxDB Data Source:");
        System.out.println("   - URL: http://localhost:8086");
        System.out.println("   - Database: telegraf");
        System.out.println("   - User: admin");
    }

    static void dashboardConfiguration() {
        System.out.println("   Dashboard Settings:");
        System.out.println("   - Title: Application Monitoring Dashboard");
        System.out.println("   - Description: Production metrics overview");
        System.out.println("   - Tags: [production, api, monitoring]");
        System.out.println("   - Time Range: Last 6 hours");
        System.out.println("   - Refresh: Every 30 seconds");
        System.out.println("   - Timezone: Browser Local");
    }

    static void panelVisualizations() {
        System.out.println("   Panel Types:");
        System.out.println("   - Graph: Time series visualization");
        System.out.println("   - Stat: Single value display");
        System.out.println("   - Gauge: Metric gauge visualization");
        System.out.println("   - Table: Tabular data display");
        System.out.println("   - Heatmap: Historical data visualization");
        System.out.println("   - Logs: Log entries display");
        System.out.println("   - Row: Panel grouping container");
    }

    static void alertsConfiguration() {
        System.out.println("   Alert Rules:");
        System.out.println("   - High CPU Usage: > 80% for 5 minutes");
        System.out.println("   - Memory Warning: > 90% for 10 minutes");
        System.out.println("   - Request Failures: > 5% error rate");
        System.out.println("   - Response Time: > 2s for 5 minutes");

        System.out.println("\n   Notification Channels:");
        System.out.println("   - Email: ops-team@company.com");
        System.out.println("   - Slack: #alerts channel");
        System.out.println("   - PagerDuty: integration enabled");
        System.out.println("   - Webhook: custom HTTP endpoint");
    }

    static void templateVariables() {
        System.out.println("   Variables:");
        System.out.println("   - \$env: Query (values: production, staging, dev)");
        System.out.println("   - \$service: Custom (values: api, web, worker)");
        System.out.println("   - \$region: Custom (values: us-east, us-west, eu)");
        System.out.println("   - \$interval: Interval (values: 1m, 5m, 15m)");

        System.out.println("\n   Usage in Queries:");
        System.out.println("   - rate(http_requests_total{env=\"$env\"}[5m])");
        System.out.println("   - sum by (service) (memory_usage{service=\"$service\"})");
    }
}