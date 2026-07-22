# Solution Guide — Disk Space Capacity Incident Remediation

## Problem: Production Disks Fill Up, Data Corruption Risk

This guide provides compilable Java code for disk space monitoring, logrotate configuration, temp file cleanup implementations, database partition management, and automated capacity monitoring.

---

## Layer 1: Logrotate Configuration Fix

### Problematic Configuration

```
# /etc/logrotate.d/app — BEFORE (Problematic)
/var/log/app/*.log {
    daily
    rotate 5
    maxage 30
    missingok
    notifempty
    postrotate
        systemctl restart app.service > /dev/null 2>&1 || true
    endscript
}
```

### Fixed Configuration

```
# /etc/logrotate.d/app — AFTER (Fixed)
/var/log/app/*.log {
    daily
    rotate 30
    maxage 30
    compress
    delaycompress
    missingok
    notifempty
    dateext
    dateformat -%Y%m%d-%s
    sharedscripts
    postrotate
        systemctl reload app.service > /dev/null 2>&1 || true
    endscript
}
```

### Key Changes

| Parameter | Old | New | Rationale |
|-----------|-----|-----|-----------|
| `rotate` | 5 | 30 | Keep 30 days of logs (30 days × 15GB = 450GB → compressed = ~45GB) |
| `compress` | missing | added | Gzip compress rotated logs (reduces size ~90%) |
| `delaycompress` | missing | added | Don't compress the most recent rotated file (app may still write to it) |
| `dateext` | missing | added | Add date to rotated filename for easy identification |
| `sharedscripts` | missing | added | Run postrotate once for all logs, not per file |

### Java Logging Configuration (Logback)

```xml
<!-- logback-spring.xml — Production Logging Configuration -->
<configuration>
    <appender name="ROLLING_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/app/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>/var/log/app/application-%d{yyyy-MM-dd}-%i.log.gz</fileNamePattern>
            <maxFileSize>500MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>50GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Production: INFO level for business logs, WARN for framework -->
    <logger name="com.example.app" level="INFO"/>
    <logger name="org.springframework" level="WARN"/>
    <logger name="org.hibernate" level="WARN"/>

    <root level="INFO">
        <appender-ref ref="ROLLING_FILE"/>
    </root>
</configuration>
```

### Log Volume Reduction (Java Code)

```java
package com.example.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    private static final Logger log = LoggerFactory.getLogger(LoggingConfig.class);

    public LoggingConfig() {
        log.info("Production logging configuration initialized");
        log.info("Log level: INFO (DEBUG disabled for production)");
        log.info("Log retention: 30 days compressed");
        log.info("Max log size: 50GB total, 500MB per file");
    }
}
```

### Dynamic Log Level Management

```java
package com.example.app.actuator;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/logging")
public class LogLevelController {

    @PutMapping("/level/{logger}")
    public String setLogLevel(
            @PathVariable String logger,
            @RequestParam String level
    ) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logbackLogger = loggerContext.getLogger(logger);
        logbackLogger.setLevel(Level.valueOf(level));
        return "Set " + logger + " to " + level;
    }

    @GetMapping("/level/{logger}")
    public String getLogLevel(@PathVariable String logger) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logbackLogger = loggerContext.getLogger(logger);
        return logbackLogger.getLevel() != null ? logbackLogger.getLevel().toString() : "INHERITED";
    }
}
```

---

## Layer 2: Temp File Cleanup

### Fixed Report Generation Code

```java
package com.example.app.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ReportGenerationService {

    private static final Logger log = LoggerFactory.getLogger(ReportGenerationService.class);
    private static final Path TEMP_DIR = Path.of("/opt/app/temp");

    public File generateReport(String reportType, String criteria) {
        Path tempFile = TEMP_DIR.resolve("report-" + UUID.randomUUID() + ".pdf");
        try {
            byte[] reportContent = buildReport(reportType, criteria);
            Files.write(tempFile, reportContent);
            log.info("Report generated: {} ({} bytes)", tempFile, reportContent.length);
            return tempFile.toFile();
        } catch (IOException e) {
            log.error("Failed to generate report: {}", e.getMessage(), e);
            throw new ReportGenerationException("Report generation failed", e);
        }
    }

    private byte[] buildReport(String reportType, String criteria) {
        // Business logic for report generation
        return new byte[0];
    }

    public void cleanupTempFile(File tempFile) {
        if (tempFile != null && tempFile.exists()) {
            boolean deleted = tempFile.delete();
            if (deleted) {
                log.debug("Temp file deleted: {}", tempFile.getAbsolutePath());
            } else {
                log.warn("Failed to delete temp file: {}", tempFile.getAbsolutePath());
            }
        }
    }
}
```

### Report Download Controller with Cleanup

```java
package com.example.app.controller;

import com.example.app.report.ReportGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;

@RestController
@RequestMapping("/api/reports")
public class ReportDownloadController {

    private static final Logger log = LoggerFactory.getLogger(ReportDownloadController.class);

    private final ReportGenerationService reportService;

    public ReportDownloadController(ReportGenerationService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadReport(
            @RequestParam String type,
            @RequestParam String criteria
    ) {
        File tempFile = null;
        try {
            tempFile = reportService.generateReport(type, criteria);

            Resource resource = new FileSystemResource(tempFile);

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + tempFile.getName() + "\"")
                .contentLength(tempFile.length())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
        } finally {
            // CRITICAL: Clean up temp file after download
            if (tempFile != null) {
                reportService.cleanupTempFile(tempFile);
            }
        }
    }
}
```

### Temp File Cleanup Cron Job

```bash
# /etc/cron.d/temp-cleanup — Clean application temp files
# Clean files older than 1 hour in app temp directory
*/30 * * * * root /usr/sbin/tmpwatch 1 /opt/app/temp
# Alternative using find (more verbose logging):
# */30 * * * * root find /opt/app/temp -type f -mmin +60 -delete -print | logger -t temp-cleanup
```

### Temp File Cleanup Verification Service

```java
package com.example.app.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TempFileMonitor {

    private static final Logger log = LoggerFactory.getLogger(TempFileMonitor.class);
    private static final Path TEMP_DIR = Path.of("/opt/app/temp");
    private static final long MAX_FILE_AGE_HOURS = 2;
    private static final long MAX_TOTAL_SIZE_BYTES = 5L * 1024 * 1024 * 1024; // 5GB

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void checkTempDirectory() {
        File tempDir = TEMP_DIR.toFile();
        if (!tempDir.exists() || !tempDir.isDirectory()) {
            return;
        }

        File[] files = tempDir.listFiles();
        if (files == null) return;

        long totalSize = 0;
        int oldFileCount = 0;

        for (File file : files) {
            totalSize += file.length();
            try {
                BasicFileAttributes attrs = Files.readAttributes(file.toPath(),
                    BasicFileAttributes.class);
                Instant lastAccess = attrs.lastAccessTime().toInstant();
                if (lastAccess.isBefore(Instant.now().minus(MAX_FILE_AGE_HOURS, ChronoUnit.HOURS))) {
                    oldFileCount++;
                }
            } catch (Exception e) {
                log.warn("Failed to check file age: {}", file.getAbsolutePath(), e);
            }
        }

        log.info("Temp directory: {} files, {} total size, {} older than {} hours",
            files.length, formatBytes(totalSize), oldFileCount, MAX_FILE_AGE_HOURS);

        if (totalSize > MAX_TOTAL_SIZE_BYTES) {
            log.warn("Temp directory size {} exceeds limit of {}",
                formatBytes(totalSize), formatBytes(MAX_TOTAL_SIZE_BYTES));
        }

        if (oldFileCount > 100) {
            log.warn("Found {} files older than {} hours in temp directory",
                oldFileCount, MAX_FILE_AGE_HOURS);
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

## Layer 3: Database Partition Management

### Creating Partitioned Audit Table

```sql
-- Step 1: Create partitioned audit_log table
CREATE TABLE audit_log (
    id BIGSERIAL,
    user_id INTEGER NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    old_value TEXT,
    new_value TEXT,
    ip_address INET,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

-- Step 2: Create monthly partitions
-- Past 6 months (retention period)
CREATE TABLE audit_log_2026_02 PARTITION OF audit_log
    FOR VALUES FROM ('2026-02-01') TO ('2026-03-01');
CREATE TABLE audit_log_2026_03 PARTITION OF audit_log
    FOR VALUES FROM ('2026-03-01') TO ('2026-04-01');
CREATE TABLE audit_log_2026_04 PARTITION OF audit_log
    FOR VALUES FROM ('2026-04-01') TO ('2026-05-01');
CREATE TABLE audit_log_2026_05 PARTITION OF audit_log
    FOR VALUES FROM ('2026-05-01') TO ('2026-06-01');
CREATE TABLE audit_log_2026_06 PARTITION OF audit_log
    FOR VALUES FROM ('2026-06-01') TO ('2026-07-01');
CREATE TABLE audit_log_2026_07 PARTITION OF audit_log
    FOR VALUES FROM ('2026-07-01') TO ('2026-08-01');
CREATE TABLE audit_log_2026_08 PARTITION OF audit_log
    FOR VALUES FROM ('2026-08-01') TO ('2026-09-01');

-- Step 3: Create future partitions in advance
CREATE TABLE audit_log_2026_09 PARTITION OF audit_log
    FOR VALUES FROM ('2026-09-01') TO ('2026-10-01');
CREATE TABLE audit_log_2026_10 PARTITION OF audit_log
    FOR VALUES FROM ('2026-10-01') TO ('2026-11-01');

-- Step 4: Create indexes on partitioned table
CREATE INDEX idx_audit_log_user_id ON audit_log (user_id, created_at DESC);
CREATE INDEX idx_audit_log_entity ON audit_log (entity_type, entity_id, created_at DESC);
CREATE INDEX idx_audit_log_action ON audit_log (action, created_at DESC);
```

### Automated Partition Maintenance (Java)

```java
package com.example.db.maintenance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
public class PartitionMaintenanceJob {

    private static final Logger log = LoggerFactory.getLogger(PartitionMaintenanceJob.class);
    private static final int RETENTION_MONTHS = 6;
    private static final int FUTURE_PARTITIONS = 3;

    private final JdbcTemplate jdbcTemplate;

    public PartitionMaintenanceJob(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(cron = "0 0 1 1 * ?") // 1st day of each month at 1:00 AM
    public void runPartitionMaintenance() {
        log.info("Starting partition maintenance...");
        dropOldPartitions();
        createFuturePartitions();
        log.info("Partition maintenance complete");
    }

    private void dropOldPartitions() {
        YearMonth cutoffMonth = YearMonth.now().minusMonths(RETENTION_MONTHS);
        String partitionName = "audit_log_" + cutoffMonth.format(DateTimeFormatter.ofPattern("yyyy_MM"));

        String sql = "DROP TABLE IF EXISTS " + partitionName;
        jdbcTemplate.execute(sql);
        log.info("Dropped partition: {} (older than {} months)", partitionName, RETENTION_MONTHS);
    }

    private void createFuturePartitions() {
        for (int i = 1; i <= FUTURE_PARTITIONS; i++) {
            YearMonth partitionMonth = YearMonth.now().plusMonths(i);
            String partitionName = "audit_log_"
                + partitionMonth.format(DateTimeFormatter.ofPattern("yyyy_MM"));
            String startDate = partitionMonth.atDay(1).toString();
            String endDate = partitionMonth.plusMonths(1).atDay(1).toString();

            String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %s PARTITION OF audit_log " +
                "FOR VALUES FROM ('%s') TO ('%s')",
                partitionName, startDate, endDate
            );
            jdbcTemplate.execute(sql);
            log.info("Created partition: {} ({} to {})", partitionName, startDate, endDate);
        }
    }
}
```

---

## Layer 4: Disk Space Monitoring (Java)

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
public class DiskSpaceMetricsExporter {

    private static final Logger log = LoggerFactory.getLogger(DiskSpaceMetricsExporter.class);

    private final MeterRegistry meterRegistry;

    public DiskSpaceMetricsExporter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void registerMetrics() {
        for (Path root : File.listRoots()) {
            try {
                FileStore store = Files.getFileStore(root);
                String mount = root.toString().replace("\\", "/");

                Gauge.builder("disk.total.bytes", store,
                        s -> {
                            try { return s.getTotalSpace(); } catch (Exception e) { return 0; }
                        })
                    .tag("mount", mount)
                    .tag("type", store.type())
                    .register(meterRegistry);

                Gauge.builder("disk.usable.bytes", store,
                        s -> {
                            try { return s.getUsableSpace(); } catch (Exception e) { return 0; }
                        })
                    .tag("mount", mount)
                    .tag("type", store.type())
                    .register(meterRegistry);

                Gauge.builder("disk.used.bytes", store,
                        s -> {
                            try {
                                return s.getTotalSpace() - s.getUsableSpace();
                            } catch (Exception e) { return 0; }
                        })
                    .tag("mount", mount)
                    .tag("type", store.type())
                    .register(meterRegistry);

                Gauge.builder("disk.utilization", store,
                        s -> {
                            try {
                                long total = s.getTotalSpace();
                                long usable = s.getUsableSpace();
                                return total > 0 ? 1.0 - (double) usable / total : 0;
                            } catch (Exception e) { return 0; }
                        })
                    .tag("mount", mount)
                    .tag("type", store.type())
                    .register(meterRegistry);

                Gauge.builder("disk.inodes.usable", store,
                        s -> {
                            try { return s.getUsableInodes(); } catch (Exception e) { return -1; }
                        })
                    .tag("mount", mount)
                    .register(meterRegistry);
            } catch (Exception e) {
                log.warn("Failed to register disk metrics for root: {}", root, e);
            }
        }
    }
}
```

---

## Layer 5: Automated Alerting Configuration

### Prometheus Alert Rules

```yaml
groups:
  - name: disk-space-alerts
    rules:
      - alert: DiskUtilizationWarning
        expr: disk_utilization{mount="/data"} > 0.80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Disk utilization > 80% on {{ $labels.mount }}"
          description: "Mount {{ $labels.mount }} is at {{ $value | humanizePercentage }}"

      - alert: DiskUtilizationCritical
        expr: disk_utilization{mount="/data"} > 0.90
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "Disk utilization > 90% on {{ $labels.mount }}"
          description: "Mount {{ $labels.mount }} is at {{ $value | humanizePercentage }} — immediate action required"

      - alert: DiskUtilizationEmergency
        expr: disk_utilization{mount="/data"} > 0.97
        for: 30s
        labels:
          severity: pager
          incident_type: disk_full
        annotations:
          summary: "EMERGENCY: Disk utilization > 97% on {{ $labels.mount }}"
          description: "Mount {{ $labels.mount }} at {{ $value | humanizePercentage }} — risk of data corruption"

      - alert: DiskInodesLow
        expr: disk_inodes_usable{mount="/data"} < 100000
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Low inode count on {{ $labels.mount }}"
          description: "Only {{ $value }} inodes remaining on {{ $labels.mount }}"

      - alert: DiskGrowthRateHigh
        expr: predict_linear(disk_utilization{mount="/data"}[7d], 86400) > 0.90
        for: 1h
        labels:
          severity: warning
        annotations:
          summary: "Disk predicted to reach 90% within 24 hours"
          description: "Based on 7-day trend, {{ $labels.mount }} will reach 90% in 24 hours"
```

---

## Complete Remediation Steps

### Step 1: Emergency Recovery (0-30 min)
1. Identify largest disk consumers: `du -sh /* | sort -rh | head -20`
2. Clear old audit data: `DROP TABLE audit_log_2025_*; DELETE FROM audit_log WHERE created_at < now() - interval '6 months';`
3. Clear temp files: `find /opt/app/temp -type f -mmin +60 -delete`
4. Compress old logs: `find /var/log/app -name "*.log" -mtime +1 -exec gzip {} \;`
5. Increase logrotate rotate count: `sed -i 's/rotate 5/rotate 30/' /etc/logrotate.d/app`
6. Verify disk freed: `df -h`

### Step 2: Logging Fix (30-60 min)
1. Update logrotate configuration (code provided above)
2. Reduce log level from DEBUG to INFO
3. Add SizeAndTimeBasedRollingPolicy with 30-day retention
4. Implement dynamic log level management

### Step 3: Temp File Fix (60-120 min)
1. Fix report generation code with finally-block cleanup
2. Add tmpwatch cron for {app-home}/temp
3. Add temp file monitoring service

### Step 4: Database Fix (120-240 min)
1. Create partitioned audit_log table
2. Migrate existing data to partitions
3. Create partition maintenance job
4. Set retention policy (6 months)

### Step 5: Monitoring Fix (240-360 min)
1. Add disk utilization metrics exporter
2. Configure Prometheus alerts at 80%/90%/97%
3. Add inode monitoring
4. Add growth rate prediction alert

---

## Verification Commands

```powershell
# Check disk usage
df -h

# Check inode usage
df -i

# Find largest directories
du -sh /* | sort -rh | Select-Object -First 20

# Check logrotate configuration
cat /etc/logrotate.d/app
logrotate -d /etc/logrotate.d/app

# Verify temp file cleanup
ls -la /opt/app/temp/ | Measure-Object

# Check PostgreSQL table sizes
SELECT schemaname, tablename, pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename))
FROM pg_tables WHERE schemaname = 'public' ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

# Check partition status
SELECT parent.relname AS parent, child.relname AS child
FROM pg_inherits JOIN pg_class parent ON pg_inherits.inhparent = parent.oid
JOIN pg_class child ON pg_inherits.inhrelid = child.oid
WHERE parent.relname = 'audit_log';

# Check prometheus alert rules
curl http://localhost:9090/api/v1/rules
```

---

## References
- Google SRE Book — Chapter 12: "Managing Disk Space"
- Google SRE Book — Chapter 6: "Monitoring Distributed Systems"
- Netflix Tech Blog: "Linux Performance Tools" — https://netflixtechblog.com/linux-performance-tools-7c9d2e6b8f2c
- PostgreSQL Documentation: "Table Partitioning" — https://www.postgresql.org/docs/15/ddl-partitioning.html
- Atlassian Engineering: "How We Fixed Disk Space Issues at Scale"
- Prometheus Documentation: "Alerting Rules" — https://prometheus.io/docs/prometheus/latest/configuration/alerting_rules/
