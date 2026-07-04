# AWS Compute — Internals

## Lambda Execution Environment

### Lifecycle Phases
```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  PENDING  │───►│  INIT    │───►│ INVOKE   │───►│  SHUTDOWN │
│(download) │    │(runtime) │    │(handler) │    │(cleanup) │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
```

- **/tmp**: 512MB-10GB ephemeral storage (persists across warm invocations)
- **ENI**: VPC-enabled functions get an Elastic Network Interface
- **Firecracker**: Lambda runs on AWS Firecracker micro-VMs (not containers)
  - KVM-based, process-level isolation
  - Each execution environment isolated at micro-VM level
  - 5ms startup time (compared to ~100ms for Docker)

### SnapStart Mechanism
1. Function invoked → `Init` phase completes → snapshot taken
2. Snapshot saved (compressed, encrypted)
3. Next invocation: snapshot restored instead of `Init` phase
4. Restore time: ~100-200ms (vs 2-6s cold start without SnapStart)
5. Java-specific: Pre-warm JVM, load classes, initialize connection pools

```java
// SnapStart lifecycle hooks
public class App implements RequestHandler<...> {
    static {
        // Runs during Init phase — captured in snapshot
        DatabaseConnectionPool.initialize();
        CacheClient.warmUp();
    }
}
```

## EC2 Nitro Architecture

```
┌─────────────────────────────────────────────┐
│              EC2 Instance                    │
│  ┌─────────────────────────────────────────┐│
│  │          Guest OS (your AMI)            ││
│  ├─────────────────────────────────────────┤│
│  │  Nitro Security Chip (hardware root)    ││
│  ├─────────────────────────────────────────┤│
│  │  Nitro Cards (hardware offload):        ││
│  │  ┌────────┐ ┌────────┐ ┌─────────────┐ ││
│  │  │ ENA    │ │ NVMe   │ │ Nitro TPM   │ ││
│  │  │(100Gbps)│ │(storage)│ │(hardware TPM)│ │
│  │  └────────┘ └────────┘ └─────────────┘ ││
│  │  ┌────────────────────────────────────┐ ││
│  │  │ Nitro Hypervisor (med. 5-15µs)     │ ││
│  │  └────────────────────────────────────┘ ││
│  └─────────────────────────────────────────┘│
└─────────────────────────────────────────────┘
```

## ECS Control Plane

```
ECS API ──► Cluster State Store (DynamoDB)
              │
         ┌────▼────┐
         │ Scheduler│──► Container Instance
         └────┬────┘    └──► ECS Agent
              │              └──► Docker daemon
         ┌────▼────┐              └──► Container
         │ Service │──► Task Placement Strategy
         └─────────┘    ├─── binpack (least CPU)
                        ├─── spread (across AZs)
                        └─── distinctInstance
```
