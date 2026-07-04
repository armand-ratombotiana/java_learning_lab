# Docker Flashcards

**Q: What is a Docker container?**
A: A running instance of a Docker image, isolated via namespaces and cgroups.

**Q: What is a Docker image?**
A: A read-only template with layered filesystem and metadata for creating containers.

**Q: What is Docker Compose?**
A: A tool for defining and running multi-container Docker applications using YAML.

**Q: What is a multi-stage build?**
A: Using multiple FROM statements in one Dockerfile to separate build and runtime environments.

**Q: What is the difference between CMD and ENTRYPOINT?**
A: ENTRYPOINT defines the executable; CMD provides default arguments.

**Q: What layer caching mechanism does Docker use?**
A: Each instruction's result is cached; cache is invalidated if instruction or context changes.

**Q: How does copy-on-write work?**
A: Image layers are read-only; when a container modifies a file, it's copied to the writable layer.

**Q: What is containerd?**
A: Industry-standard OCI container runtime that manages container lifecycle.

**Q: What port does Docker Hub registry use?**
A: 443 (HTTPS) for API communication.

**Q: What file format defines multi-container applications?**
A: docker-compose.yml (YAML format).
