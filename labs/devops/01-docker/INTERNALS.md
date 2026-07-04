# Docker Internals

## Architecture Components
- **dockerd**: Persistent background daemon managing containers, images, networks, volumes.
- **containerd**: Industry-standard container runtime (OCI-compliant), manages container lifecycle.
- **runc**: Low-level OCI runtime, spawns and runs containers using kernel primitives.
- **containerd-shim**: Allows daemonless containers (runc process can exit, shim persists).

## Image Format (OCI)
- **Manifest**: JSON describing layers and configuration.
- **Config**: JSON with env, cmd, entrypoint, exposed ports, volumes.
- **Layer tarballs**: Filesystem diffs stored as compressed tar archives.
- **Index**: Reference multiple platform-specific manifests.

## Storage Drivers
- **OverlayFS2** (default): Merges lower (image) and upper (container) directories.
- **Devicemapper**, **Btrfs**, **ZFS**: Alternative drivers with different performance characteristics.

## Copy-on-Write
When a container modifies a file from an image layer, the storage driver copies it to the writable container layer before modification. Image layers remain read-only and shared.
