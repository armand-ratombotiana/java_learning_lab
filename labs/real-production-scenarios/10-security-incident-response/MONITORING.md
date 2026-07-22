# Monitoring and Alerting Guide — Security Incident Detection

## Lab 10: Security Incident Response — Compromised API Key

This guide documents the monitoring and alerting configuration required to detect API key compromise, anomalous API usage, and potential data exfiltration.

---

## 1. CloudTrail Monitoring

### Key CloudTrail Events to Monitor

| Event | Description | Alert Severity |
|-------|-------------|----------------|
| `GetSecretValue` | Secrets Manager access | Info |
| `RotateSecret` | Secret rotation event | Info |
| `UnauthorizedApiCall` | Failed API call | Warning |
| `ApiKeyUsageFromNewRegion` | API key used from new geographic region | Critical |
| `BulkExportCall` | Data export API called | Warning |
| `SensitiveEndpointAccess` | Admin/export endpoint accessed | Warning |
| `ConsoleLogin` | AWS console login | Info |

### CloudTrail Metric Filter

```json
{
    "filterPattern": "{ ($.errorCode = \"*Unauthorized*\" || $.errorCode = \"*AccessDenied*\") }",
    "metricTransformations": [
        {
            "metricName": "UnauthorizedApiCalls",
            "metricNamespace": "CloudTrailMetrics",
            "metricValue": "1",
            "defaultValue": 0
        }
    ]
}
```

### CloudTrail Anomaly Detection

```java
package com.example.security.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CloudTrailAnomalyDetector {

    private static final Logger log = LoggerFactory.getLogger(CloudTrailAnomalyDetector.class);

    private final Map<String, ApiKeyGeoStats> geoStats = new ConcurrentHashMap<>();
    private final Map<String, ApiKeyRateStats> rateStats = new ConcurrentHashMap<>();

    private static final double GEO_ANOMALY_THRESHOLD_KM = 1000.0;
    private static final int RATE_ANOMALY_MULTIPLIER = 5;
    private static final int RATE_WINDOW_MINUTES = 5;

    public void recordEvent(String apiKey, String sourceIp, String region, String endpoint) {
        recordGeoEvent(apiKey, sourceIp, region);
        recordRateEvent(apiKey, endpoint);
        checkForAnomalies(apiKey);
    }

    private void recordGeoEvent(String apiKey, String sourceIp, String region) {
        geoStats.computeIfAbsent(apiKey, k -> new ApiKeyGeoStats())
            .recordAccess(sourceIp, region);
    }

    private void recordRateEvent(String apiKey, String endpoint) {
        rateStats.computeIfAbsent(apiKey, k -> new ApiKeyRateStats())
            .recordCall(endpoint);
    }

    private void checkForAnomalies(String apiKey) {
        if (isGeoAnomalous(apiKey)) {
            log.warn("GEO ANOMALY: API key {} accessed from unexpected geographic region", apiKey);
        }
        if (isRateAnomalous(apiKey)) {
            log.warn("RATE ANOMALY: API key {} call rate {}x above baseline", apiKey,
                getCurrentRateVsBaseline(apiKey));
        }
    }

    private boolean isGeoAnomalous(String apiKey) {
        ApiKeyGeoStats stats = geoStats.get(apiKey);
        return stats != null && stats.hasMultipleRegions();
    }

    private boolean isRateAnomalous(String apiKey) {
        ApiKeyRateStats stats = rateStats.get(apiKey);
        return stats != null && stats.isAnomalous();
    }

    private double getCurrentRateVsBaseline(String apiKey) {
        ApiKeyRateStats stats = rateStats.get(apiKey);
        return stats != null ? stats.getCurrentRate() / Math.max(1, stats.getBaselineRate()) : 1.0;
    }

    static class ApiKeyGeoStats {
        private final Set<String> regions = ConcurrentHashMap.newKeySet();
        private final Map<String, Integer> ipCounts = new ConcurrentHashMap<>();

        void recordAccess(String sourceIp, String region) {
            regions.add(region);
            ipCounts.merge(sourceIp, 1, Integer::sum);
        }

        boolean hasMultipleRegions() {
            return regions.size() > 1;
        }

        Set<String> getRegions() { return regions; }
        int getUniqueIpCount() { return ipCounts.size(); }
    }

    static class ApiKeyRateStats {
        private final List<Long> callTimes = new ArrayList<>();
        private double baselineRate = 1.0; // 1 call per minute baseline

        void recordCall(String endpoint) {
            callTimes.add(System.currentTimeMillis());
        }

        boolean isAnomalous() {
            return getCurrentRate() > baselineRate * RATE_ANOMALY_MULTIPLIER;
        }

        double getCurrentRate() {
            long now = System.currentTimeMillis();
            long cutoff = now - (RATE_WINDOW_MINUTES * 60 * 1000);
            long recentCalls = callTimes.stream()
                .filter(t -> t > cutoff)
                .count();
            return (double) recentCalls / RATE_WINDOW_MINUTES;
        }

        double getBaselineRate() { return baselineRate; }
        void setBaselineRate(double rate) { this.baselineRate = rate; }
    }
}
```

---

## 2. WAF Logs Monitoring

### Cloudflare WAF Configuration

```yaml
# Cloudflare WAF Rules for API Security
rules:
  - name: "Block Known Bad IPs"
    action: block
    expression: "(ip.src eq 185.34.72.100) or (ip.src eq 185.34.72.0/24)"
  
  - name: "Rate Limit API Requests"
    action: block
    expression: "cf.zone.name eq \"api.company.com\""
    ratelimit:
      characteristics: ["ip.src", "http.request.headers.x-api-key"]
      period: 60
      requests_per_period: 1000
  
  - name: "Block Bulk Export from Non-VPN IPs"
    action: block
    expression: "(starts_with(http.request.uri.path, \"/api/v1/users/export\") and not ip.src eq 203.0.113.0/24)"
  
  - name: "Geo-Block Admin Access"
    action: block
    expression: "(starts_with(http.request.uri.path, \"/api/v1/admin\") and not ip.geoip.country in {\"US\" \"CA\"})"
  
  - name: "Header Validation"
    action: block
    expression: "not len(http.request.headers[\"x-api-key\"]) > 0"
```

### WAF Log Analysis

```java
package com.example.security.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class WafLogAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(WafLogAnalyzer.class);

    private final Map<String, List<WafEvent>> recentEvents = new ConcurrentHashMap<>();

    public void analyzeEvent(WafEvent event) {
        String key = event.getApiKey();
        recentEvents.computeIfAbsent(key, k -> new ArrayList<>()).add(event);

        // Check for pattern: many blocked requests from same IP
        List<WafEvent> ipEvents = recentEvents.values().stream()
            .flatMap(Collection::stream)
            .filter(e -> e.getSourceIp().equals(event.getSourceIp()))
            .collect(Collectors.toList());

        if (ipEvents.size() > 100) {
            log.warn("IP {} has {} blocked WAF events — possible scanning", event.getSourceIp(), ipEvents.size());
        }

        // Check for pattern: same key used from multiple IPs
        Set<String> ipsForThisKey = recentEvents.getOrDefault(key, List.of()).stream()
            .map(WafEvent::getSourceIp)
            .collect(Collectors.toSet());

        if (ipsForThisKey.size() > 3) {
            log.warn("API key {} used from {} different IPs — possible key sharing/compromise",
                maskKey(key), ipsForThisKey.size());
        }

        // Cleanup old events
        if (recentEvents.size() > 10000) {
            recentEvents.clear();
        }
    }

    public List<WafAlert> getActiveAlerts() {
        List<WafAlert> alerts = new ArrayList<>();

        for (Map.Entry<String, List<WafEvent>> entry : recentEvents.entrySet()) {
            String key = entry.getKey();
            List<WafEvent> events = entry.getValue();

            if (events.size() > 1000) {
                alerts.add(new WafAlert(
                    key,
                    "HIGH_VOLUME",
                    events.size() + " WAF events for this key in recent window",
                    events.get(0).getSourceIp(),
                    new Date()
                ));
            }
        }

        return alerts;
    }

    private String maskKey(String key) {
        if (key == null || key.length() <= 8) return "****";
        return key.substring(0, 4) + "****" + key.substring(key.length() - 4);
    }

    static class WafEvent {
        private final String apiKey;
        private final String sourceIp;
        private final String endpoint;
        private final String action;
        private final Date timestamp;

        public WafEvent(String apiKey, String sourceIp, String endpoint, String action) {
            this.apiKey = apiKey;
            this.sourceIp = sourceIp;
            this.endpoint = endpoint;
            this.action = action;
            this.timestamp = new Date();
        }

        public String getApiKey() { return apiKey; }
        public String getSourceIp() { return sourceIp; }
        public String getEndpoint() { return endpoint; }
        public String getAction() { return action; }
    }

    static class WafAlert {
        private final String apiKey;
        private final String type;
        private final String description;
        private final String sourceIp;
        private final Date timestamp;

        WafAlert(String apiKey, String type, String description, String sourceIp, Date timestamp) {
            this.apiKey = apiKey;
            this.type = type;
            this.description = description;
            this.sourceIp = sourceIp;
            this.timestamp = timestamp;
        }

        public String getApiKey() { return apiKey; }
        public String getType() { return type; }
        public String getDescription() { return description; }
    }
}
```

---

## 3. SIEM Integration (ELK Stack)

### ELK Index Configuration for API Security

```json
{
    "index_patterns": ["api-security-*"],
    "template": {
        "settings": {
            "number_of_shards": 3,
            "number_of_replicas": 2
        },
        "mappings": {
            "properties": {
                "@timestamp": { "type": "date" },
                "api_key": { "type": "keyword" },
                "source_ip": { "type": "ip" },
                "endpoint": { "type": "keyword" },
                "geoip": {
                    "properties": {
                        "country_name": { "type": "keyword" },
                        "city_name": { "type": "keyword" },
                        "location": { "type": "geo_point" }
                    }
                },
                "response_status": { "type": "integer" },
                "request_size": { "type": "long" },
                "user_agent": { "type": "text" },
                "anomaly_score": { "type": "float" }
            }
        }
    }
}
```

### ELK Security Alert Rules (Watcher)

```json
{
    "trigger": {
        "schedule": { "interval": "5m" }
    },
    "input": {
        "search": {
            "request": {
                "indices": ["api-security-*"],
                "body": {
                    "query": {
                        "bool": {
                            "filter": [
                                { "range": { "@timestamp": { "gte": "now-5m" } } }
                            ],
                            "must_not": [
                                { "term": { "source_ip": "10.0.0.0/8" } }
                            ]
                        }
                    },
                    "aggs": {
                        "api_keys": {
                            "terms": { "field": "api_key", "size": 100 },
                            "aggs": {
                                "ip_count": {
                                    "cardinality": { "field": "source_ip" }
                                }
                            }
                        }
                    }
                }
            }
        }
    },
    "condition": {
        "script": {
            "source": "ctx.payload.aggregations.api_keys.buckets.stream().anyMatch(b -> b.ip_count.value > 3)"
        }
    },
    "actions": {
        "slack": {
            "webhook": "https://hooks.slack.com/services/..."
        },
        "pagerduty": {
            "severity": "critical"
        }
    }
}
```

---

## 4. Prometheus Alert Rules

```yaml
groups:
  - name: security-incident-detection
    rules:
      - alert: ApiKeyGeoAnomaly
        expr: rate(api_key_geo_anomaly_total[5m]) > 0
        for: 1m
        labels:
          severity: critical
          incident_type: security
        annotations:
          summary: "API key geo-anomaly detected"
          description: "API key {{ $labels.api_key }} accessed from unexpected geographic region"

      - alert: ApiKeyRateAnomaly
        expr: rate(api_key_calls_total[5m]) > 5 * avg_over_time(rate(api_key_calls_total[1h])[24h:5m])
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "API key rate anomaly"
          description: "API key {{ $labels.api_key }} call rate {{ $value }} — {{ $labels.ratio }}x above baseline"

      - alert: BulkExportFromUnauthorizedIP
        expr: rate(http_requests_total{endpoint=~".*export.*", source_ip!~"10\\..*|203\\.0\\.113\\..*"}[5m]) > 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Bulk export from unauthorized IP"
          description: "Bulk export endpoint called from {{ $labels.source_ip }}"

      - alert: RateLimitViolations
        expr: rate(http_requests_total{status="429"}[5m]) > 10
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Rate limit violations increasing"
          description: "Rate limit violations at {{ $value }} per second"

      - alert: FailedAuthAttempts
        expr: rate(http_requests_total{status="401"}[5m]) > 5
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Failed authentication attempts"
          description: "401 errors at {{ $value }} per second — possible brute force"
```

---

## 5. Security Dashboard (Grafana)

### Security Monitoring Dashboard

**Row 1 — Incident Overview**
- Active security incidents (count)
- Revoked API keys in last 24h
- Blocked IPs (count)
- Rate limit violations (count)

**Row 2 — API Key Usage**
- Calls per API key (top 10, bar chart)
- Unique IPs per API key (top 10)
- Geo-distribution of API calls (map)
- Anomaly detection alerts (time series)

**Row 3 — Sensitive Endpoints**
- Bulk export calls (time series)
- Admin API calls (time series)
- Unauthorized access attempts (bar chart)
- IP whitelist violations (counter)

**Row 4 — WAF & IDS**
- Blocked requests (time series)
- Top blocked IPs (table)
- WAF rule match distribution (pie chart)
- IDS alerts (count per severity)

---

## 6. Alert Response Runbook

### Alert: API Key Geo-Anomaly
1. Identify the API key and affected service
2. Check CloudTrail log for source IPs and endpoints accessed
3. Determine if the IP is known (office VPN, partner integration)
4. If unknown: revoke key immediately
5. Rotate key, notify service owner
6. Investigate data accessed

### Alert: Rate Anomaly
1. Check if rate increase is expected (deployment, marketing campaign)
2. If unexpected: block the key temporarily
3. Investigate source of increased traffic
4. Check for data exfiltration patterns
5. Implement temporary rate limit reduction

### Alert: Bulk Export from Unauthorized IP
1. Block the IP at WAF immediately
2. Revoke the API key used
3. Check how much data was exported
4. Determine data sensitivity
5. Initiate data breach assessment

---

## 7. Monitoring Configuration Checklist

- [ ] CloudTrail logging enabled for all API calls
- [ ] CloudTrail metric filters for unauthorized calls
- [ ] CloudTrail anomaly detection for per-key usage
- [ ] Cloudflare WAF rules for API security
- [ ] WAF log analysis service implemented
- [ ] ELK SIEM configured with API security index
- [ ] Prometheus alert rules for security incidents
- [ ] Grafana security monitoring dashboard
- [ ] PagerDuty integration for security alerts
- [ ] Geo-anomaly detection for API keys
- [ ] Rate anomaly detection for API keys
- [ ] Failed authentication monitoring
- [ ] IP whitelist violation alerts

---

## References
- AWS CloudTrail Documentation: "Logging API Calls"
- Cloudflare WAF Documentation: "API Security Rules"
- ELK Stack Documentation: "Security Analytics"
- Prometheus Documentation: "Alerting Rules"
- Grafana Documentation: "Security Monitoring"
- OWASP API Security Top 10
- CrowdStrike 2024 Global Threat Report
