# Monitoring & Logging Code Deep Dive

## Prometheus Configuration
```yaml
# prometheus.yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

alerting:
  alertmanagers:
    - static_configs:
        - targets: ['alertmanager:9093']

rule_files:
  - "alerts.yml"

scrape_configs:
  - job_name: 'kubernetes-pods'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
```

## Alerting Rules
```yaml
# alerts.yml
groups:
  - name: instance
    rules:
      - alert: InstanceDown
        expr: up == 0
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "Instance {{ $labels.instance }} down"

      - alert: HighCpuUsage
        expr: 100 - (avg by(instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100) > 80
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "High CPU usage on {{ $labels.instance }}"
```

## Grafana Dashboard JSON (Excerpt)
```json
{
  "title": "Kubernetes Cluster Overview",
  "panels": [
    {
      "title": "CPU Usage",
      "type": "graph",
      "targets": [
        {
          "expr": "sum(rate(container_cpu_usage_seconds_total[5m])) by (pod)",
          "legendFormat": "{{ pod }}"
        }
      ]
    }
  ]
}
```

## Logstash Pipeline
```ruby
input {
  beats {
    port => 5044
  }
}

filter {
  grok {
    match => { "message" => "%{IP:client_ip} %{WORD:method} %{URIPATHPARAM:request} %{NUMBER:status} %{NUMBER:bytes}" }
  }
  date {
    match => ["timestamp", "ISO8601"]
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "nginx-access-%{+YYYY.MM.dd}"
  }
}
```
