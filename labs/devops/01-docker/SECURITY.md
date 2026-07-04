# Docker Security

## Best Practices
- **Run as non-root**: Use `USER` directive, create dedicated user.
- **Minimal base images**: Reduce attack surface (Alpine, distroless).
- **Image scanning**: Use Docker Scout, Trivy, Snyk for vulnerability scanning.
- **Secrets management**: Use Docker secrets (Swarm) or build args (not env vars for secrets).
- **Read-only rootfs**: Use `--read-only` flag, mount tmpfs for write locations.
- **Capabilities**: Drop all capabilities (`--cap-drop ALL`), add only required ones.
- **Content trust**: Enable Docker Content Trust for image signing.
- **Non-root Docker socket**: Avoid mounting `/var/run/docker.sock` in containers.

## Supply Chain Security
- **SBOM**: Generate software bill of materials for images.
- **Signed images**: Use Notary or cosign for signature verification.
- **Base image provenance**: Use only official/verified base images.

## Network Security
- **User-defined networks**: Better isolation than default bridge.
- **Internal networks**: No external access for backend services.
- **TLS**: Encrypt daemon communication with TLS certificates.
