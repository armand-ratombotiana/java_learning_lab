# Container Security — Step-by-Step Guide

## 1. Image Scanning
- Use Trivy, Grype, or Snyk to scan for CVEs.
- Generate SBOM (CycloneDX, SPDX) for dependency tracking.

## 2. Dockerfile Best Practices
- Use distroless or scratch base images.
- Multi-stage builds: builder stage (fat) → runtime stage (minimal).
- Avoid `RUN apt-get upgrade` — pin versions.
- Use `USER 10001` — never run as root.

## 3. Rootless Containers
- Run containers with `--user` or use Podman's rootless mode.
- Rootless: no CAP_NET_BIND_SERVICE on ports <1024 without configuration.

## 4. Seccomp
- Default Docker seccomp profile blocks ~44 syscalls.
- Custom profiles allow only needed syscalls (e.g., `write`, `read`, `mmap`).

## 5. AppArmor
- Profile loaded per container: `--security-opt apparmor=my-profile`.
- Profiles define what files, network, and capabilities a container can access.

## 6. Falco (Runtime Security)
- Falco rules detect: shell in container, unexpected syscalls, file writes below /etc.
- Rule: `desc: "Shell in container" condition: spawned_process and container`

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/devops/deep/lab03/*.java
java --enable-preview -cp out com.devops.deep.lab03.ContainerSecurityLab
```
