# Visual Guide to Docker

```
┌─────────────────────────────────────────────────────┐
│                    Docker Architecture              │
├─────────────────────────────────────────────────────┤
│  ┌──────────┐   ┌──────────┐   ┌──────────┐       │
│  │   CLI    │──▶│ dockerd  │──▶│containerd│       │
│  │ docker   │   │ (daemon) │   │ (runtime)│       │
│  └──────────┘   └──────────┘   └────┬─────┘       │
│                                      │             │
│                    ┌─────────────────▼──────┐       │
│                    │        runc           │       │
│                    │ (OCI runtime, creates  │       │
│                    │  container processes) │       │
│                    └───────────────────────┘       │
└─────────────────────────────────────────────────────┘

Docker Image Layers:
┌──────────────────────┐  ┌──────────────────────────┐
│  Layer N: App Code   │  │  Container Writable      │
├──────────────────────┤  ├──────────────────────────┤
│  Layer 3: Dependencies│  │  Upper Layer (R/W)       │
├──────────────────────┤  ├──────────────────────────┤
│  Layer 2: OS Libs    │  │  Layer N (R/O)           │
├──────────────────────┤  ├──────────────────────────┤
│  Layer 1: Base Image │  │  ...                     │
└──────────────────────┘  └──────────────────────────┘
    Image Layers            Running Container
```

## Container Lifecycle
```
docker pull → docker create → docker start → docker run
                    │                │
                    ▼                ▼
               ┌─────────┐    ┌──────────┐
               │ Created │───▶│ Running  │───▶ docker stop
               └─────────┘    └──────────┘       │
                                                  ▼
                                             ┌─────────┐
                                             │ Stopped │───▶ docker rm
                                             └─────────┘
```
