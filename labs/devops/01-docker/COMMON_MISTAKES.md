# Common Docker Mistakes

1. **Running as root** in containers — use `USER` directive.
2. **Ignoring layer caching** — order Dockerfile instructions from least to most frequently changing.
3. **Installing unnecessary packages** — bloats images and increases attack surface.
4. **Hardcoding credentials** in Dockerfiles or images.
5. **Using `latest` tag** in production — pin specific versions.
6. **Storing state in containers** — use volumes for persistent data.
7. **Building monolithic images** — prefer multi-stage builds and minimal base images.
8. **Exposing unnecessary ports** — only expose what's needed.
9. **Not setting resource limits** — containers can consume all host resources.
10. **Skipping health checks** — critical for orchestration platforms.
11. **Using `ADD` instead of `COPY`** — `COPY` is simpler and more predictable.
12. **Running `apt-get upgrade` in Dockerfile** — breaks reproducibility.
