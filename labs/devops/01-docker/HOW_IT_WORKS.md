# How Docker Works

## Kernel Primitives
Docker uses Linux kernel features:
- **Namespaces**: Isolate processes (PID, network, mount, user, UTS, IPC, cgroup).
- **Cgroups**: Limit and account for resource usage (CPU, memory, disk I/O, network).
- **UnionFS**: Overlay filesystem layers (OverlayFS, AUFS) enabling sharing and copy-on-write.

## Image Build Process
1. Docker daemon receives build context from CLI.
2. Each Dockerfile instruction creates a new layer.
3. Layers are cached by instruction hash; unchanged layers are reused.
4. Final image is a read-only stack of layers with a metadata manifest.

## Container Runtime
1. Daemon fetches image layers from registry/local cache.
2. Creates writable container layer on top using snapshotter.
3. Configures namespaces and cgroups for isolation.
4. Runs entrypoint process with chroot into container filesystem.

## Networking
- **Bridge** (default): Private network, port forwarding via NAT.
- **Host**: Container uses host network stack directly.
- **Overlay**: Multi-host networking for Swarm/Compose.
