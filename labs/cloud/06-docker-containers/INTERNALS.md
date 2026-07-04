# Docker — Internals

## Namespaces (Isolation)

| Namespace | What it isolates |
|-----------|-----------------|
| PID | Process IDs — container sees only its own processes |
| Network | Network stack — own interfaces, IP, routing, ports |
| Mount | Filesystem mount points — isolated filesystem view |
| UTS | Hostname and domain name |
| IPC | Inter-process communication (shared memory, semaphores) |
| User | User and group IDs — root inside = non-root outside |

```bash
# Inside container: PID 1 = your app
# Outside container: the same PID is different
# Namespace mapping: invisible from other containers
```

## cgroups (Resource Limits)

### CPU
```
docker run --cpus=1.5 myapp
# Container limited to 1.5 CPU cores
# Uses Completely Fair Scheduler (CFS) quotas
# quota_us / period_us = CPU limit
# period_us = 100000 (default)
# quota_us = 150000 (for 1.5 CPUs)
```

### Memory
```
docker run -m 512m --memory-reservation 256m myapp
# Hard limit: 512MB (OOM-killed if exceeded)
# Soft limit: 256MB (scheduled with preference)
# JVM: -Xmx should be ~70% of container memory
# -Xmx358m for 512m container
```

### I/O
```
docker run --device-read-bps /dev/sda:10mb --device-write-bps /dev/sda:10mb
# Throttle disk I/O
```

## Union Filesystem (OverlayFS)

```
Lower layers (from image):
  /var/lib/docker/overlay2/l/L1 (base OS)
  /var/lib/docker/overlay2/l/L2 (JRE)
  /var/lib/docker/overlay2/l/L3 (deps)
  /var/lib/docker/overlay2/l/L4 (app JAR)

Upper layer (container writable):
  /var/lib/docker/overlay2/<container-id>/diff

Merged view:
  /var/lib/docker/overlay2/<container-id>/merged
  └── Container sees: L1 + L2 + L3 + L4 + diff (merged)

COPY_ON_WRITE: Writing to a file from lower layer:
  1. File copied from lower to upper
  2. Write happens on upper copy
  3. Lower layer unchanged (cache preserved)
```

## Container Network Model

### Bridge Network (default)
```
docker0 (bridge):
  172.17.0.1 ─── docker0 (host)
       │
  172.17.0.2 ─── container-1 (veth pair → eth0@container)
  172.17.0.3 ─── container-2

  Port mapping: host:8080 → container:8080 (iptables DNAT)
```

### Overlay Network (Docker Swarm / multi-host)
```
  Overlay network across hosts:
  Host A: container-1 (10.0.0.2)
  Host B: container-2 (10.0.0.3)

  Uses VXLAN encapsulation (UDP 4789)
  Each container sees all services on same overlay
  DNS resolution: <service-name> → container IP
```
