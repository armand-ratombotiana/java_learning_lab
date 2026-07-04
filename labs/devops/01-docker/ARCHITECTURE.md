# Docker Architecture

```
┌────────────────────────────────────────────────────────────┐
│                      Docker Host                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                   dockerd (Daemon)                    │  │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐  │  │
│  │  │ Image   │ │Container│ │ Volume  │ │ Network │  │  │
│  │  │ Manager │ │ Manager │ │ Manager │ │ Manager │  │  │
│  │  └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘  │  │
│  └───────┼────────────┼───────────┼────────────┼──────┘  │
│          │            │           │            │          │
│  ┌───────▼────────────▼───────────▼────────────▼──────┐  │
│  │                   containerd                        │  │
│  │          (OCI-compliant runtime manager)            │  │
│  └────────────────────┬───────────────────────────────┘  │
│                       │                                   │
│              ┌────────▼────────┐                          │
│              │      runc       │                          │
│              │  (OCI runtime)  │                          │
│              └────────┬────────┘                          │
│                       │                                   │
│              ┌────────▼────────┐                          │
│              │  Container(s)   │                          │
│              │  (isolated via  │                          │
│              │  namespaces +   │                          │
│              │  cgroups)       │                          │
│              └─────────────────┘                          │
└────────────────────────────────────────────────────────────┘
```

## Components
- **dockerd**: REST API, image management, build orchestration
- **containerd**: Container lifecycle (create, start, stop, exec)
- **runc**: Low-level OCI runtime using cgroups/namespaces
- **Registry**: Image storage (Docker Hub, private registries)
- **CLI**: User-facing command-line interface
