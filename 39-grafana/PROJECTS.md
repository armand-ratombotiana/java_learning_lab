# Grafana Dashboard Projects

This module covers Grafana dashboard visualization, data source integration, alerting configuration, and dashboard provisioning for building comprehensive monitoring visualizations in Java applications.

## Mini-Project: Real-Time Monitoring Dashboard (2-4 Hours)

### Overview

Build a real-time monitoring dashboard with Grafana that demonstrates panel creation, data source configuration, dynamic variables, and alert integration for application observability.

### Technology Stack

- Java 21 with Spring Boot 3.x
- Spring Boot Actuator with Micrometer
- Prometheus (data source)
- Grafana
- Maven build system

### Project Structure

```
grafana-dashboards/
├── pom.xml
├── dashboard.json
└── src/
    └── main/
        ├── java/com/learning/grafana/
        │   ├── DashboardApplication.java
        │   ├── config/
        │   │   └── GrafanaConfig.java
        │   ├── service/
        │   │   └── DashboardDataService.java
        │   └── controller/
        │       └── DashboardController.java
        └── resources/
            └── application.properties
```

### Implementation

**pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.learning</groupId>
        <artifactId>java-learning-parent</artifactId>
        <version>1.0.0</version>
    </parent>
    
    <artifactId>grafana-dashboards</artifactId>
    
    <properties>
        <java.version>21</java.version>
        <spring-boot.version>3.2.0</spring-boot.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-core</artifactId>
            <version>1.12.0</version>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
            <version>1.12.0</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring-boot.version}</version>
            </plugin>
        </plugins>
    </build>
</project>
```

**GrafanaConfig.java**

```java
package com.learning.grafana.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class GrafanaConfig {
    
    @Bean
    public GrafanaProperties grafanaProperties() {
        GrafanaProperties props = new GrafanaProperties();
        
        props.setUrl("http://localhost:3000");
        props.setApiKey(System.getenv().getOrDefault("GRAFANA_API_KEY", "admin"));
        props.setDefaultDatasource("Prometheus");
        
        return props;
    }
    
    public static class GrafanaProperties {
        private String url;
        private String apiKey;
        private String defaultDatasource;
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getDefaultDatasource() { return defaultDatasource; }
        public void setDefaultDatasource(String defaultDatasource) { this.defaultDatasource = defaultDatasource; }
    }
}
```

**DashboardDataService.java**

```java
package com.learning.grafana.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DashboardDataService {
    
    private final Map<String, List<TimeSeriesPoint>> metrics = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Double>> currentValues = new ConcurrentHashMap<>();
    
    public static class TimeSeriesPoint {
        Instant timestamp;
        Double value;
        
        TimeSeriesPoint(Instant timestamp, Double value) {
            this.timestamp = timestamp;
            this.value = value;
        }
        
        public Instant getTimestamp() { return timestamp; }
        public Double getValue() { return value; }
    }
    
    public DashboardDataService() {
        initializeSampleMetrics();
    }
    
    private void initializeSampleMetrics() {
        recordMetric("http_requests_total", Instant.now(), 1523.0);
        recordMetric("http_requests_total", Instant.now().minusSeconds(60), 1450.0);
        recordMetric("http_requests_total", Instant.now().minusSeconds(120), 1398.0);
        
        recordMetric("orders_created_total", Instant.now(), 89.0);
        recordMetric("orders_created_total", Instant.now().minusSeconds(60), 85.0);
        
        recordMetric("active_users", Instant.now(), 234.0);
        recordMetric("active_users", Instant.now().minusSeconds(60), 228.0);
        
        recordMetric("memory_usage_bytes", Instant.now(), 4.2e9);
        recordMetric("cpu_usage_percent", Instant.now(), 45.3);
        
        recordMetric("cache_hit_ratio", Instant.now(), 0.87);
    }
    
    public void recordMetric(String name, Instant timestamp, Double value) {
        metrics.computeIfAbsent(name, k -> new ArrayList<>())
            .add(new TimeSeriesPoint(timestamp, value));
        
        currentValues.put(name, value);
    }
    
    public List<TimeSeriesPoint> getTimeSeries(String metricName, int points) {
        List<TimeSeriesPoint> data = metrics.getOrDefault(metricName, Collections.emptyList());
        
        return data.stream()
            .sorted(Comparator.comparing(TimeSeriesPoint::getTimestamp).reversed())
            .limit(points)
            .collect(Collectors.toList());
    }
    
    public Map<String, Double> getCurrentValues() {
        return new HashMap<>(currentValues);
    }
    
    public Double getCurrentValue(String metricName) {
        return currentValues.get(metricName);
    }
    
    public Map<String, Map<String, Double>> getMetricsByCategory() {
        Map<String, Map<String, Double>> categorized = new HashMap<>();
        
        categorized.put("application", Map.of(
            "http_requests_total", getCurrentValue("http_requests_total"),
            "orders_created_total", getCurrentValue("orders_created_total"),
            "active_users", getCurrentValue("active_users")
        ));
        
        categorized.put("infrastructure", Map.of(
            "memory_usage_bytes", getCurrentValue("memory_usage_bytes"),
            "cpu_usage_percent", getCurrentValue("cpu_usage_percent")
        ));
        
        categorized.put("cache", Map.of(
            "cache_hit_ratio", getCurrentValue("cache_hit_ratio")
        ));
        
        return categorized;
    }
    
    public List<Map<String, Object>> getAggregatedStats() {
        List<Map<String, Object>> stats = new ArrayList<>();
        
        for (Map.Entry<String, List<TimeSeriesPoint>> entry : metrics.entrySet()) {
            List<TimeSeriesPoint> values = entry.getValue();
            
            if (values.isEmpty()) continue;
            
            double avg = values.stream()
                .mapToDouble(TimeSeriesPoint::getValue)
                .average()
                .orElse(0.0);
            
            double max = values.stream()
                .mapToDouble(TimeSeriesPoint::getValue)
                .max()
                .orElse(0.0);
            
            double min = values.stream()
                .mapToDouble(TimeSeriesPoint::getValue)
                .min()
                .orElse(0.0);
            
            stats.add(Map.of(
                "metric", entry.getKey(),
                "avg", avg,
                "max", max,
                "min", min,
                "count", (double) values.size()
            ));
        }
        
        return stats;
    }
}
```

**DashboardController.java**

```java
package com.learning.grafana.controller;

import com.learning.grafana.service.DashboardDataService;
import com.learning.grafana.service.DashboardDataService.TimeSeriesPoint;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {
    
    private final DashboardDataService dataService;
    
    public DashboardController(DashboardDataService dataService) {
        this.dataService = dataService;
    }
    
    @GetMapping("/metrics/current")
    public Map<String, Double> getCurrentMetrics() {
        return dataService.getCurrentValues();
    }
    
    @GetMapping("/metrics/timeseries")
    public List<TimeSeriesPoint> getTimeSeries(
            @RequestParam String name,
            @RequestParam(defaultValue = "20") int points) {
        return dataService.getTimeSeries(name, points);
    }
    
    @GetMapping("/metrics/categories")
    public Map<String, Map<String, Double>> getMetricsByCategory() {
        return dataService.getMetricsByCategory();
    }
    
    @GetMapping("/metrics/stats")
    public List<Map<String, Object>> getAggregatedStats() {
        return dataService.getAggregatedStats();
    }
    
    @PostMapping("/metrics/record")
    public Map<String, String> recordMetric(
            @RequestParam String name,
            @RequestParam double value) {
        dataService.recordMetric(name, Instant.now(), value);
        return Map.of("status", "recorded", "metric", name, "value", String.valueOf(value));
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
```

**DashboardApplication.java**

```java
package com.learning.grafana;

import com.learning.grafana.service.DashboardDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.util.Map;

@SpringBootApplication
public class DashboardApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class, args);
    }
    
    @Bean
    CommandLineRunner demo(DashboardDataService service) {
        return args -> {
            System.out.println("=== Grafana Dashboard Demo ===\n");
            
            System.out.println("1. Current Metrics:");
            Map<String, Double> current = service.getCurrentValues();
            current.forEach((k, v) -> System.out.println("   " + k + ": " + String.format("%.2f", v)));
            
            System.out.println("\n2. Metrics by Category:");
            Map<String, Map<String, Double>> categorized = service.getMetricsByCategory();
            categorized.forEach((category, metrics) -> 
                System.out.println("   " + category + ": " + metrics.keySet()));
            
            System.out.println("\n3. Aggregated Stats:");
            service.getAggregatedStats().forEach(stat -> 
                System.out.println("   " + stat.get("metric") + " - avg: " + 
                    String.format("%.2f", (double) stat.get("avg"))));
            
            System.out.println("\n=== Demo Complete ===");
            System.exit(0);
        };
    }
}
```

**application.properties**

```properties
spring.application.name=grafana-dashboards
server.port=8080

management.endpoints.web.exposure.include=prometheus,health,metrics
management.endpoint.prometheus.enabled=true

grafana.url=http://localhost:3000
grafana.api-key=${GRAFANA_API_KEY:admin}
```

### dashboard.json

```json
{
  "dashboard": {
    "id": null,
    "uid": "learning-monitoring",
    "title": "Production Monitoring",
    "tags": ["production", "learning"],
    "timezone": "browser",
    "schemaVersion": 16,
    "version": 0,
    "refresh": "5s",
    "panels": [
      {
        "id": 1,
        "title": "HTTP Requests",
        "type": "graph",
        "gridPos": {"h": 8, "w": 12, "x": 0, "y": 0},
        "targets": [
          {
            "expr": "rate(http_requests_total[5m])",
            "legendFormat": "{{method}} {{path}}",
            "refId": "A"
          }
        ],
        "datasource": {"type": "prometheus", "uid": "prometheus"}
      },
      {
        "id": 2,
        "title": "Active Users",
        "type": "stat",
        "gridPos": {"h": 4, "w": 6, "x": 12, "y": 0},
        "targets": [
          {
            "expr": "active_users",
            "refId": "A"
          }
        ],
        "options": {
          "colorMode": "value",
          "graphMode": "area"
        },
        "datasource": {"type": "prometheus", "uid": "prometheus"}
      },
      {
        "id": 3,
        "title": "Error Rate",
        "type": "gauge",
        "gridPos": {"h": 4, "w": 6, "x": 18, "y": 0},
        "targets": [
          {
            "expr": "rate(http_errors_total[5m]) / rate(http_requests_total[5m]) * 100",
            "refId": "A"
          }
        ],
        "fieldConfig": {
          "defaults": {
            "unit": "percent",
            "thresholds": {
              "mode": "absolute",
              "steps": [
                {"color": "green", "value": null},
                {"color": "yellow", "value": 1},
                {"color": "red", "value": 5}
              ]
            }
          }
        },
        "datasource": {"type": "prometheus", "uid": "prometheus"}
      },
      {
        "id": 4,
        "title": "Orders Created",
        "type": "timeseries",
        "gridPos": {"h": 8, "w": 12, "x": 0, "y": 8},
        "targets": [
          {
            "expr": "rate(orders_created_total[5m])",
            "legendFormat": "Orders/min",
            "refId": "A"
          }
        ],
        "datasource": {"type": "prometheus", "uid": "prometheus"}
      }
    ],
    "templating": {
      "list": [
        {
          "name": "env",
          "type": "query",
          "query": "label_values(up, environment)",
          "current": {"value": "production", "text": "production"}
        },
        {
          "name": "service",
          "type": "query",
          "query": "label_values(up{service=\"$env\"}, service)",
          "current": {"value": "all", "text": "all"}
        }
      ]
    },
    "time": {
      "from": "now-1h",
      "to": "now"
    },
    "annotations": {
      "list": [
        {
          "name": "Deployments",
          "type": "prometheus",
          "expr": "deployments"
        }
      ]
    }
  }
}
```

### Build and Run

```bash
# Start Grafana
docker run -d --name grafana -p 3000:3000 -e GF_SECURITY_ADMIN_PASSWORD=admin grafana/grafana

# Build and run
cd 39-grafana/grafana-dashboards
mvn clean package -DskipTests
mvn spring-boot:run
```

### Expected Output

```
=== Grafana Dashboard Demo ===

1. Current Metrics:
   http_requests_total: 1523.00
   orders_created_total: 89.00
   active_users: 234.00
   memory_usage_bytes: 4200000000.00
   cpu_usage_percent: 45.30
   cache_hit_ratio: 0.87

2. Metrics by Category:
   application: [http_requests_total, orders_created_total, active_users]
   infrastructure: [memory_usage_bytes, cpu_usage_percent]
   cache: [cache_hit_ratio]

3. Aggregated Stats:
   http_requests_total - avg: 1457.00
   orders_created_total - avg: 87.00
   active_users - avg: 231.00

=== Demo Complete ===
```

---

## Real-World Project: Enterprise Operations Dashboard (8+ Hours)

### Overview

Build a comprehensive enterprise operations dashboard with Grafana that demonstrates multi-source data integration, complex visualizations, alerting with notifications, and automated provisioning for complete operational visibility.

### Key Features

1. **Multi-Source Data** - Prometheus, Loki, Tempo integration
2. **Complex Visualizations** - Heatmaps, geomaps, relationships
3. **Dynamic Templating** - Variables and repeated panels
4. **Alert Integration** - Unified alerts from panels
5. **Provisioning** - Automatic dashboard deployment
6. **Annotations** - Custom event markers
7. **Plugins** - Custom visualization plugins

### Project Structure

```
grafana-dashboards/
├── pom.xml
├── provisioning/
│   ├── dashboards/
│   │   ├── dashboard.yml
│   │   └── app-dashboard.json
│   └── datasources/
│       └── datasources.yml
├── alerts/
│   └── alerts.json
└── src/
    └── main/
        └── java/com/learning/grafana/
            ├── EnterpriseDashboardApplication.java
            ├── config/
            │   └── ProvisioningConfig.java
            ├── service/
            │   └── DataSourceService.java
            └── controller/
                └── EnterpriseController.java
```

### Implementation

**provisioning/dashboards/dashboard.yml**

```yaml
apiVersion: 1

providers:
  - name: 'Learning Dashboards'
    orgId: 1
    folder: 'Learning'
    type: file
    disableDeletion: false
    updateIntervalSeconds: 10
    allowUiUpdates: true
    options:
      path: /etc/grafana/provisioning/dashboards
```

**provisioning/datasources/datasources.yml**

```yaml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
    editable: true
    
  - name: Loki
    type: loki
    access: proxy
    url: http://loki:3100
    editable: true
    
  - name: Tempo
    type: tempo
    access: proxy
    url: http://tempo:3100
    editable: true
```

**app-dashboard.json**

```json
{
  "dashboard": {
    "uid": "enterprise-operations",
    "title": "Enterprise Operations Center",
    "tags": ["enterprise", "operations"],
    "timezone": "utc",
    "refresh": "10s",
    "panels": [
      {
        "title": "Service Health Overview",
        "type": "statusmap",
        "gridPos": {"h": 6, "w": 24, "x": 0, "y": 0},
        "targets": [
          {
            "expr": "up",
            "refId": "A"
          }
        ],
        "options": {
          "showLabels": true,
          "minState": "warning"
        }
      },
      {
        "title": "Request Latency Heatmap",
        "type": "heatmap",
        "gridPos": {"h": 8, "w": 12, "x": 0, "y": 6},
        "targets": [
          {
            "expr": "rate(http_request_duration_seconds_bucket[5m])",
            "refId": "A"
          }
        ],
        "options": {
          "calculate": true,
          "calculation": {"type": "histogram"}
        }
      },
      {
        "title": "Geographic Distribution",
        "type": "geomap",
        "gridPos": {"h": 8, "w": 12, "x": 12, "y": 6},
        "targets": [
          {
            "expr": "user_locations",
            "refId": "A"
          }
        ],
        "options": {
          "basemapConfig": {
            "location": "World"
          },
          "tooltip": {
            "mode": "all"
          }
        }
      },
      {
        "title": "Error Breakdown",
        "type": "piechart",
        "gridPos": {"h": 8, "w": 8, "x": 0, "y": 14},
        "targets": [
          {
            "expr": "sum by (error_type) (http_errors_total)",
            "refId": "A"
          }
        ]
      },
      {
        "title": "Resource Utilization",
        "type": "gauge",
        "gridPos": {"h": 8, "w": 8, "x": 8, "y": 14},
        "targets": [
          {
            "expr": "resource_utilization_percent",
            "refId": "A"
          }
        ]
      },
      {
        "title": "Recent Deployments",
        "type": "table",
        "gridPos": {"h": 8, "w": 8, "x": 16, "y": 14},
        "targets": [
          {
            "expr": "deployments",
            "format": "table",
            "refId": "A"
          }
        ],
        "transformations": [
          {"type": "organize", "options": {"excludeByName": {"Time": true}}}
        ]
      }
    ],
    "templating": {
      "list": [
        {
          "name": "environment",
          "type": "custom",
          "query": "production,staging,development",
          "current": {"value": "production", "text": "production"}
        },
        {
          "name": "service",
          "type": "query",
          "query": "label_values(up{env=\"$environment\"}, service)",
          "refresh": 1
        },
        {
          "name": "region",
          "type": "query",
          "query": "label_values(node_cpu_seconds_total, region)"
        }
      ]
    },
    "links": [
      {
        "title": "Service Details",
        "url": "/d/$${dashboard_uid}/service-details?service=$${service}"
      },
      {
        "title": "Logs",
        "url": "/explore?left=[\"now-1h\",\"now\",\"Loki\",{\"ref\":\"$${service}\"}]"
      }
    ],
    "annotations": {
      "list": [
        {
          "name": "Incidents",
          "datasource": "Prometheus",
          "expr": "incidents"
        },
        {
          "name": "Deployments",
          "datasource": "Prometheus",
          "expr": "deployments"
        }
      ]
    },
    "alert": {
      "name": "Enterprise Operations Alerts",
      "conditions": [
        {
          "target": {"refId": "A"},
          "operator": "gt",
          "reducer": {"type": "avg"},
          "params": {"threshold": 5}
        }
      ],
      "executionErrorState": "alerting",
      "for": "5m",
      "frequency": "1m",
      "handler": 1,
      "noDataState": "no_data"
    }
  }
}
```

**alerts/alerts.json**

```json
{
  "groups": [
    {
      "name": "enterprise-alerts",
      "folder": "Enterprise",
      "interval": "1m",
      "rules": [
        {
          "alert": "ServiceDown",
          "expr": "up == 0",
          "for": "2m",
          "labels": {
            "severity": "critical"
          },
          "annotations": {
            "summary": "Service {{ $labels.service }} is down",
            "description": "Service has been down for 2 minutes"
          }
        },
        {
          "alert": "HighErrorRate",
          "expr": "rate(http_errors_total[5m]) / rate(http_requests_total[5m]) > 0.05",
          "for": "5m",
          "labels": {
            "severity": "warning"
          },
          "annotations": {
            "summary": "High error rate for {{ $labels.service }}",
            "description": "Error rate is {{ $value | humanizePercentage }}"
          }
        },
        {
          "alert": "HighLatencyP95",
          "expr": "histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) > 2",
          "for": "10m",
          "labels": {
            "severity": "warning"
          },
          "annotations": {
            "summary": "P95 latency is {{ $value }}s"
          }
        },
        {
          "alert": "ResourceExhaustion",
          "expr": "resource_usage_percent > 90",
          "for": "5m",
          "labels": {
            "severity": "critical"
          },
          "annotations": {
            "summary": "Resource {{ $labels.resource }} at {{ $value }}%"
          }
        }
      ]
    }
  ]
}
```

**DataSourceService.java**

```java
package com.learning.grafana.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DataSourceService {
    
    private final Map<String, DataSourceConfig> datasources = new ConcurrentHashMap<>();
    
    public static class DataSourceConfig {
        private String name;
        private String type;
        private String url;
        private boolean isDefault;
        private Map<String, String> options;
        
        public DataSourceConfig(String name, String type, String url) {
            this.name = name;
            this.type = type;
            this.url = url;
            this.options = new HashMap<>();
        }
        
        public String getName() { return name; }
        public String getType() { return type; }
        public String getUrl() { return url; }
        public boolean isDefault() { return isDefault; }
        public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
        public Map<String, String> getOptions() { return options; }
    }
    
    public DataSourceService() {
        initializeDataSources();
    }
    
    private void initializeDataSources() {
        DataSourceConfig prometheus = new DataSourceConfig("Prometheus", "prometheus", "http://prometheus:9090");
        prometheus.setDefault(true);
        datasources.put("prometheus", prometheus);
        
        DataSourceConfig loki = new DataSourceConfig("Loki", "loki", "http://loki:3100");
        datasources.put("loki", loki);
        
        DataSourceConfig tempo = new DataSourceConfig("Tempo", "tempo", "http://tempo:3100");
        datasources.put("tempo", tempo);
        
        DataSourceConfig elasticsearch = new DataSourceConfig("Elasticsearch", 
            "elasticsearch", "http://elasticsearch:9200");
        datasources.put("elasticsearch", elasticsearch);
        
        DataSourceConfig cloudwatch = new DataSourceConfig("CloudWatch", "cloudwatch", 
            "arn:aws:...");
        cloudwatch.getOptions().put("authType", "default");
        datasources.put("cloudwatch", cloudwatch);
    }
    
    public DataSourceConfig getDataSource(String name) {
        return datasources.get(name);
    }
    
    public List<DataSourceConfig> getAllDataSources() {
        return new ArrayList<>(datasources.values());
    }
    
    public DataSourceConfig getDefaultDataSource() {
        return datasources.values().stream()
            .filter(DataSourceConfig::isDefault)
            .findFirst()
            .orElse(null);
    }
    
    public void configureDataSource(String name, Map<String, String> options) {
        DataSourceConfig config = datasources.get(name);
        if (config != null) {
            config.getOptions().putAll(options);
        }
    }
    
    public Map<String, Object> getDataSourceHealth() {
        Map<String, Object> health = new HashMap<>();
        
        for (DataSourceConfig ds : datasources.values()) {
            health.put(ds.getName(), Map.of(
                "type", ds.getType(),
                "url", ds.getUrl(),
                "status", "UP",
                "isDefault", ds.isDefault()
            ));
        }
        
        return health;
    }
}
```

### Build and Run

```bash
cd 39-grafana/grafana-dashboards
mvn clean package -DskipTests

# Provision dashboards
cp provisioning/dashboards/dashboard.yml /etc/grafana/provisioning/dashboards/
cp provisioning/datasources/datasources.yml /etc/grafana/provisioning/datasources/

# Run application
mvn spring-boot:run

# Access API
curl http://localhost:8080/api/datasources/health
curl http://localhost:8080/api/datasources
```

### API Endpoints

```
GET /api/datasources                  # List all data sources
GET /api/datasources/{name}           # Get data source details
GET /api/datasources/health         # Get health status
GET /api/annotations             # Get annotations
POST /api/annotations           # Create annotation
GET /api/alerts                # Get alerts
POST /api/alerts           # Create alert
GET /api/health
```

### Learning Outcomes

After completing these projects, you will understand:

1. **Panel Types** - Graphs, stats, gauges, heatmaps
2. **Data Sources** - Prometheus, Loki, Tempo, Elasticsearch
3. **Templating** - Variables and dynamic queries
4. **Provisioning** - Automated dashboard deployment
5. **Annotations** - Custom event markers
6. **Alerting** - Integrated alert management
7. **Transformations** - Data transformation pipeline
8. **JSON Model** - Dashboard JSON structure

### References

- Grafana JSON Schema: https://grafana.com/docs/grafana/latest/dashboards/json-models/
- Panel Options: https://grafana.com/docs/grafana/latest/panels-visualizations/
- Provisioning: https://grafana.com/docs/grafana/latest/administration/provisioning/