# Monitoring and Alerting Guide — Disk Space Capacity

## Lab 09: Disk Space Capacity Incident

This guide documents the monitoring and alerting configuration required to detect disk space exhaustion, inode depletion, and capacity trends before they cause production incidents.

---

## 1. Disk Space Metrics

### Key Metrics to Monitor

| Metric | Command/Source | Description |
|--------|---------------|-------------|
| Disk Utilization | `df -h` | Percentage of disk used |
| Inode Utilization | `df -i` | Percentage of inodes used |
| Largest Directories | `du -sh /*` | Top-level directory sizes |
| File Count | `find /dir -type f | wc -l` | Number of files |
| Log Directory Size | `du -sh /var/log/app` | Application log size |
| Temp Directory Size | `du -sh /opt/app/temp` | Temp file size |
| Database Table Size | PostgreSQL pg_total_relation_size | Largest tables |
| Disk Growth Rate | Prometheus predict_linear | 7-day trend projection |

### Node Exporter Configuration

```yaml
# prometheus.yml — Node exporter configuration
- job_name: 'node'
  static_configs:
    - targets:
      - 'db-server-01:9100'
      - 'db-server-02:9100'
      - 'app-server-01:9100'
      - 'app-server-02:9100'

# Alert rules file
rule_files:
  - 'disk-alerts.yml'
```

### Prometheus Disk Alert Rules

```yaml
# disk-alerts.yml
groups:
  - name: disk-capacity
    rules:
      - alert: DiskUtilization80
        expr: 100 - (node_filesystem_avail_bytes{mountpoint="/data"} / node_filesystem_size_bytes{mountpoint="/data"} * 100) > 80
        for: 5m
        labels:
          severity: warning
          team: sre
        annotations:
          summary: "Disk > 80% on {{ $labels.instance }}"
          description: "{{ $labels.instance }} mount {{ $labels.mountpoint }} is at {{ $value }}%"

      - alert: DiskUtilization90
        expr: 100 - (node_filesystem_avail_bytes{mountpoint="/data"} / node_filesystem_size_bytes{mountpoint="/data"} * 100) > 90
        for: 2m
        labels:
          severity: critical
          team: sre
        annotations:
          summary: "Disk > 90% on {{ $labels.instance }}"
          description: "{{ $labels.instance }} mount {{ $labels.mountpoint }} is at {{ $value }}% — action required within 15 minutes"

      - alert: DiskUtilization97
        expr: 100 - (node_filesystem_avail_bytes{mountpoint="/data"} / node_filesystem_size_bytes{mountpoint="/data"} * 100) > 97
        for: 30s
        labels:
          severity: pager
          team: sre
          incident_type: disk_emergency
        annotations:
          summary: "EMERGENCY: Disk > 97% on {{ $labels.instance }}"
          description: "{{ $labels.instance }} mount {{ $labels.mountpoint }} at {{ $value }}% — risk of data corruption"

      - alert: InodeUtilization80
        expr: (node_filesystem_files_free{mountpoint="/data"} / node_filesystem_files{mountpoint="/data"}) * 100 < 20
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Inodes < 20% free on {{ $labels.instance }}"
          description: "Only {{ $value }}% inodes free on {{ $labels.instance }} {{ $labels.mountpoint }}"

      - alert: DiskGrowthRateWarning
        expr: predict_linear(node_filesystem_avail_bytes{mountpoint="/data"}[7d], 86400) < 0
        for: 1h
        labels:
          severity: warning
        annotations:
          summary: "Disk projected to fill in 24 hours"
          description: "{{ $labels.instance }} {{ $labels.mountpoint }} projected to fill within 24 hours based on 7-day trend"
```

---

## 2. Application-Level Disk Monitoring

### Spring Boot Actuator Disk Metrics

```java
package com.example.app.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ApplicationDiskMonitor {

    private static final Logger log = LoggerFactory.getLogger(ApplicationDiskMonitor.class);

    private static final Path LOG_DIR = Path.of("/var/log/app");
    private static final Path TEMP_DIR = Path.of("/opt/app/temp");
    private static final Path DATA_DIR = Path.of("/data/app");
    private static final long LOG_DIR_WARN_BYTES = 50L * 1024 * 1024 * 1024; // 50GB
    private static final long TEMP_DIR_WARN_BYTES = 5L * 1024 * 1024 * 1024; // 5GB
    private static final long DATA_DIR_WARN_BYTES = 400L * 1024 * 1024 * 1024; // 400GB

    private final MeterRegistry meterRegistry;

    public ApplicationDiskMonitor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void registerMonitors() {
        registerDirectoryMonitor("log", LOG_DIR, LOG_DIR_WARN_BYTES);
        registerDirectoryMonitor("temp", TEMP_DIR, TEMP_DIR_WARN_BYTES);
        registerDirectoryMonitor("data", DATA_DIR, DATA_DIR_WARN_BYTES);

        registerFileCountMonitor("log", LOG_DIR, 1000, 5000);
        registerFileCountMonitor("temp", TEMP_DIR, 500, 2000);
    }

    private void registerDirectoryMonitor(String name, Path path, long warnSize) {
        Gauge.builder("app.disk." + name + ".bytes", path,
                p -> getDirectorySize(p))
            .tag("path", path.toString())
            .register(meterRegistry);

        Gauge.builder("app.disk." + name + ".warning", path,
                p -> getDirectorySize(p) > warnSize ? 1 : 0)
            .tag("path", path.toString())
            .register(meterRegistry);
    }

    private void registerFileCountMonitor(String name, Path path, int warnCount, int critCount) {
        Gauge.builder("app.disk." + name + ".file.count", path,
                p -> countFiles(p))
            .tag("path", path.toString())
            .register(meterRegistry);
    }

    private long getDirectorySize(Path path) {
        try {
            if (Files.exists(path)) {
                return Files.walk(path)
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try { return Files.size(p); } catch (Exception e) { return 0; }
                    })
                    .sum();
            }
        } catch (Exception e) {
            log.warn("Failed to calculate size for {}", path, e);
        }
        return 0;
    }

    private long countFiles(Path path) {
        try {
            if (Files.exists(path)) {
                return Files.walk(path)
                    .filter(Files::isRegularFile)
                    .count();
            }
        } catch (Exception e) {
            log.warn("Failed to count files in {}", path, e);
        }
        return 0;
    }
}
```

---

## 3. Database Table Size Monitoring

### PostgreSQL Table Size Query (Prometheus Exporter)

```java
package com.example.db.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseTableSizeMonitor {

    private static final Logger log = LoggerFactory.getLogger(DatabaseTableSizeMonitor.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseTableSizeMonitor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void reportTableSizes() {
        String sql = """
            SELECT
                schemaname || '.' || tablename as table_name,
                pg_total_relation_size(schemaname || '.' || tablename) as total_bytes,
                pg_relation_size(schemaname || '.' || tablename) as table_bytes,
                pg_indexes_size(schemaname || '.' || tablename) as index_bytes
            FROM pg_tables
            WHERE schemaname = 'public'
            ORDER BY pg_total_relation_size(schemaname || '.' || tablename) DESC
            LIMIT 20
        """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        for (Map<String, Object> row : results) {
            String tableName = (String) row.get("table_name");
            long totalBytes = ((Number) row.get("total_bytes")).longValue();
            long tableBytes = ((Number) row.get("table_bytes")).longValue();
            long indexBytes = ((Number) row.get("index_bytes")).longValue();

            log.info("Table {}: total={}, data={}, indexes={}",
                tableName, formatBytes(totalBytes), formatBytes(tableBytes), formatBytes(indexBytes));

            if (totalBytes > 100L * 1024 * 1024 * 1024) { // > 100GB
                log.warn("Table {} exceeds 100GB ({}), review partitioning/retention",
                    tableName, formatBytes(totalBytes));
            }
        }
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
```

---

## 4. Log Volume Monitoring

```java
package com.example.app.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;

@Component
public class LogVolumeMonitor {

    private static final Logger log = LoggerFactory.getLogger(LogVolumeMonitor.class);

    private static final Path LOG_DIR = Path.of("/var/log/app");
    private static final long DAILY_LOG_BUDGET_BYTES = 15L * 1024 * 1024 * 1024; // 15GB
    private static final long MONTHLY_LOG_BUDGET_BYTES = 450L * 1024 * 1024 * 1024; // 450GB

    @Scheduled(cron = "0 0 0 * * ?") // midnight
    public void reportDailyLogVolume() {
        File logDir = LOG_DIR.toFile();
        if (!logDir.exists()) return;

        File[] todayLogs = logDir.listFiles(f -> {
            try {
                BasicFileAttributes attrs = Files.readAttributes(f.toPath(),
                    BasicFileAttributes.class);
                Instant lastModified = attrs.lastModifiedTime().toInstant();
                LocalDate modifiedDate = lastModified.atZone(ZoneId.systemDefault()).toLocalDate();
                return modifiedDate.equals(LocalDate.now());
            } catch (Exception e) {
                return false;
            }
        });

        if (todayLogs == null) return;

        long totalBytes = Arrays.stream(todayLogs)
            .mapToLong(File::length)
            .sum();

        log.info("Daily log volume: {} (budget: {}, usage: {:.1f}%)",
            formatBytes(totalBytes),
            formatBytes(DAILY_LOG_BUDGET_BYTES),
            (double) totalBytes / DAILY_LOG_BUDGET_BYTES * 100);

        if (totalBytes > DAILY_LOG_BUDGET_BYTES) {
            log.warn("Daily log volume {} exceeds budget of {}",
                formatBytes(totalBytes), formatBytes(DAILY_LOG_BUDGET_BYTES));
        }
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
```

---

## 5. Grafana Dashboard Layout

### Disk Capacity Dashboard

**Row 1 — Overview**
- Disk utilization gauge (all servers, current)
- Servers at critical level (>90%) — count
- Total disk capacity vs used (summary)
- Emergency alert status

**Row 2 — Per-Server Details**
- Disk utilization (time series, last 7 days)
- Inode utilization (time series, last 7 days)
- Largest directory sizes (bar chart)
- Predicted fill date (single stat)

**Row 3 — Application-Specific**
- Log directory size (per server)
- Temp directory size (per server)
- Database table sizes (top 10)
- Log volume per day (bar chart)

**Row 4 — Trends**
- Disk growth rate (7-day trend)
- Capacity forecast (next 30 days)
- Historical disk usage (3 months)
- Alert history

---

## 6. Alert Response Runbook

### Alert: Disk > 80%
1. Check which server and mount point
2. Identify largest directories: `du -sh /* | sort -rh | head -10`
3. Check growth rate — is this trending up or stable?
4. If trending up: create JIRA for investigation
5. If stable: monitor, no immediate action required

### Alert: Disk > 90%
1. Identify largest files/directories immediately
2. Determine cause: logs? temp files? database?
3. Take action:
   - Logs: compress old logs, verify logrotate
   - Temp files: run tmpwatch, fix application bug
   - Database: check table sizes, consider partition drop
4. Acknowledge alert, provide ETA for resolution

### Alert: Disk > 97%
1. DECLARE SEV1 incident immediately
2. Run emergency cleanup script
3. Identify and stop the process consuming disk
4. Free minimum 10% disk space
5. Verify database integrity
6. Monitor for 30 minutes after recovery

---

## 7. Monitoring Configuration Checklist

- [ ] Node exporter installed on all servers
- [ ] Prometheus alert rules for 80/90/97% thresholds
- [ ] Inode monitoring configured
- [ ] Disk growth rate prediction enabled
- [ ] Log directory monitored (per application)
- [ ] Temp directory monitored (per application)
- [ ] Database table size monitoring
- [ ] Grafana dashboard with all panels
- [ ] PagerDuty integration for critical alerts
- [ ] Automated emergency cleanup script available
- [ ] Capacity trend dashboard accessible to SRE team
- [ ] Weekly disk capacity report configured

---

## References
- Google SRE Book — Chapter 12: "Managing Disk Space"
- Google SRE Book — Chapter 6: "Monitoring Distributed Systems"
- Prometheus Documentation: "Alerting Rules"
- Grafana Documentation: "Dashboard Best Practices"
- Netflix Tech Blog: "Linux Performance Tools"
- PostgreSQL Documentation: "Monitoring Database Size"
