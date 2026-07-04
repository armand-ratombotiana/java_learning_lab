# History of Docker

## Timeline

| Year | Milestone |
|------|-----------|
| 2000 | FreeBSD Jails — process isolation |
| 2001 | Linux VServer — OS-level virtualization |
| 2005 | OpenVZ — containerization for Linux |
| 2006 | **cgroups** (Google) — resource limiting for processes |
| 2008 | **LXC** (Linux Containers) — combines cgroups + namespaces |
| 2013 | **Docker** launches (dotCloud → Docker Inc.) — open-source container engine |
| 2014 | Docker 1.0; Docker Compose (formerly Fig); Docker Hub |
| 2015 | Docker Swarm (clustering); **Open Container Initiative (OCI)** established |
| 2016 | Docker wins container war (vs rkt); built into Windows Server |
| 2017 | Docker Enterprise Edition; containerd graduates to CNCF |
| 2018 | Docker Compose v3; Kubernetes wins orchestration war |
| 2019 | Docker Inc. sells Enterprise to Mirantis; community focus |
| 2020 | Docker Desktop for Apple Silicon; Docker Hub rate limits |
| 2021 | Docker Compose v2 (Go rewrite); BuildKit as default builder |
| 2022 | Docker Scout (supply chain security); WASM support |
| 2023 | Docker Desktop 4.20+; improved resource management |
| 2024 | Docker supports containerd as core runtime; BuildCloud |

## Key Insight
Docker didn't invent containers — it made them usable. Before Docker, containers required deep Linux expertise. Docker added: image layering (UnionFS), registry (Docker Hub), CLI ergonomics, and a developer-friendly workflow. This UX revolution made containers mainstream.
