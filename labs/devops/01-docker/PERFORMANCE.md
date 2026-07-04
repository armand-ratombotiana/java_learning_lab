# Docker Performance

## Image Optimization
- **Minimal base images**: Alpine (~5MB) vs Ubuntu (~200MB)
- **Multi-stage builds**: Remove build-time dependencies from final image
- **Layer count**: Merge RUN commands to reduce layers (but balance with cacheability)
- **`.dockerignore`**: Exclude node_modules, .git, tests from build context

## Runtime Performance
- **Network**: Host mode reduces latency (no NAT bridge)
- **Volumes**: Bind mounts > named volumes > volumes in container layer
- **CPU**: Use `--cpus` to limit; avoid CPU throttling by setting appropriate limits
- **Memory**: Set `--memory` and `--memory-swap` for predictable behavior

## Benchmarking Tools
- `docker stats` - real-time resource usage
- `docker system df` - disk usage analysis
- `dive` - image layer inspection tool

## Common Bottlenecks
- I/O heavy workloads on overlay filesystem
- DNS resolution in bridge networks
- Sequential layer downloads from registries
