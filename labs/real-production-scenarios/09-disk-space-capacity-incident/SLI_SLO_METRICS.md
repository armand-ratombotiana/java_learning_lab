# Lab 09 — Disk Space / Capacity: SLI/SLO/SLA Definitions

## Service: Infrastructure Monitoring

### SLIs

| Indicator | Definition | Target |
|-----------|-----------|--------|
| Disk utilization | Used / total per mount point | < 80% |
| Inode utilization | Used inodes / total per mount | < 80% |
| Disk growth rate | GB/week per mount point | < 10% of free |
| Log file size | Max log file per service | < 100MB |

### SLOs

| SLI | Target | Error Budget |
|-----|--------|-------------|
| Disk utilization < 80% | 99.9% | ~43.8 min/month |
| Inode utilization < 80% | 99.9% | ~43.8 min/month |
| Zero "disk full" incidents | 100% | 0 tolerance |

### Alerting

```yaml
groups:
  - name: disk_alerts
    rules:
      - alert: DiskSpaceWarning
        expr: node_filesystem_avail_bytes{mountpoint="/"} / node_filesystem_size_bytes{mountpoint="/"} < 0.2
        for: 5m
        annotations:
          summary: "Disk < 20% free on {{ $labels.instance }}"

      - alert: DiskSpaceCritical
        expr: node_filesystem_avail_bytes{mountpoint="/"} / node_filesystem_size_bytes{mountpoint="/"} < 0.1
        for: 1m
        annotations:
          summary: "Disk < 10% free on {{ $labels.instance }}"

      - alert: InodeUsage
        expr: node_filesystem_files_free{mountpoint="/"} / node_filesystem_files{mountpoint="/"} < 0.1
        for: 5m
        annotations:
          summary: "Inode usage critical on {{ $labels.instance }}"
```
