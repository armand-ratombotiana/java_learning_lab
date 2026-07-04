# Mental Models for Docker

## 1. The Shipping Container Analogy

```
Physical shipping:        Docker:
┌─────────────────┐      ┌─────────────────┐
│ Container         │      │ Docker Image    │
│ (standard size)  │      │ (standard format)│
├─────────────────┤      ├─────────────────┤
│ Goods inside     │      │ App + deps      │
│ (protected)      │      │ (isolated env)  │
├─────────────────┤      ├─────────────────┤
│ Stackable        │      │ Layered images  │
│ (intermodal)     │      │ (UnionFS layers)│
├─────────────────┤      ├─────────────────┤
│ Ship / Train /   │      │ Docker Engine   │
│ Truck            │      │ (any host)      │
└─────────────────┘      └─────────────────┘

Container standardization revolutionized shipping.
Docker container standardization revolutionized software.
```

## 2. The Onion (Image Layers)

```
Docker image = stack of read-only layers

┌────────────────────────────────────────────┐
│ Layer 5: app.jar           (R/W container) │  ← Changes here
├────────────────────────────────────────────┤
│ Layer 4: config files      (read-only)     │
├────────────────────────────────────────────┤
│ Layer 3: Maven dependencies (read-only)    │  ← Cached, rarely changes
├────────────────────────────────────────────┤
│ Layer 2: JRE 17            (read-only)     │  ← Cached
├────────────────────────────────────────────┤
│ Layer 1: Ubuntu 22.04      (read-only)     │  ← Cached
├────────────────────────────────────────────┤
│ Layer 0: Kernel            (shared with host)│
└────────────────────────────────────────────┘

Each layer is a diff. Pulling updates only downloads changed layers.
`docker pull` reuses cached layers from previous pulls.
```

## 3. The Process vs Machine

```
Mistaken view:
  Container = lightweight VM
  └── Has its own OS, init system, SSH

Correct view:
  Container = isolated process
  └── Shares host kernel
  └── Has isolated filesystem (from image)
  └── Has isolated network (virtual Ethernet)
  └── Has isolated PID space
  └── BUT: no separate kernel, no init system by default

Java JVM runs directly on host kernel.
Xmx must be within container memory limit.
```

## 4. The Lunch Box (Docker Compose)

```
Docker Compose = a meal with multiple containers

┌──────────────────────────────────────┐
│  docker-compose.yml                  │
│                                      │
│  ┌─────────────────────────────────┐ │
│  │ app: build ., ports: 8080       │ │
│  │ depends_on: [db, redis]         │ │
│  └─────────────────────────────────┘ │
│  ┌─────────────────────────────────┐ │
│  │ db: image: postgres:16, ports:  │ │
│  │ volumes: db-data               │ │
│  └─────────────────────────────────┘ │
│  ┌─────────────────────────────────┐ │
│  │ redis: image: redis:7-alpine    │ │
│  └─────────────────────────────────┘ │
└──────────────────────────────────────┘

docker compose up = start all services (one command)
```
