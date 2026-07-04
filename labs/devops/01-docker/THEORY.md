# Docker Theory

## Containers vs Virtual Machines
- **VMs**: Hypervisor virtualizes hardware; each VM runs full OS + kernel. Heavy, slow boot, GB-size.
- **Containers**: OS-level virtualization; share host kernel. Lightweight, instant boot, MB-size.

## Key Concepts
- **Image**: Immutable snapshot (filesystem + metadata) used to create containers.
- **Container**: Running instance of an image; isolated process with its own filesystem, network, and process tree.
- **Dockerfile**: Declarative recipe to build an image layer-by-layer.
- **Layer**: Each instruction in a Dockerfile creates a read-only layer. Layers are cached and reused.
- **Registry**: Repository for storing/distributing images (Docker Hub, ECR, GCR).
- **Volume**: Persistent data storage independent of container lifecycle.
- **Network**: Bridge, host, overlay, and macvlan drivers for container communication.

## Docker Architecture
Client-server model: `docker` CLI communicates with `dockerd` daemon via REST API. Daemon manages images, containers, networks, and volumes.

## Container Lifecycle
Created -> Running -> Paused -> Stopped -> Deleted
