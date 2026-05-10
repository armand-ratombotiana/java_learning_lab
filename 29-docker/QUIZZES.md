# Docker Quizzes

## Quiz 1: Dockerfile Basics

**Question 1**: What is the correct order of Dockerfile instructions?
- A) FROM, RUN, COPY, EXPOSE, CMD
- B) FROM, COPY, RUN, EXPOSE, CMD
- C) FROM, EXPOSE, RUN, COPY, CMD
- D) FROM, RUN, EXPOSE, COPY, CMD

**Answer**: A

---

**Question 2**: Which instruction adds metadata to an image?
- A) LABEL
- B) METADATA
- C) ANNOTATION
- D) TAG

**Answer**: A

---

**Question 3**: What does EXPOSE do?
- A) Opens ports in the host
- B) Documents which ports the container listens on
- C) Maps ports to host
- D) Creates network

**Answer**: B

---

**Question 4**: How do you create a multi-stage build?
- A) Multiple FROM in one Dockerfile
- B) Multiple Dockerfiles
- C) Use docker-compose
- D) Use --multi-stage flag

**Answer**: A

---

**Question 5**: What is the purpose of .dockerignore?
- A) Exclude files from build context
- B) Exclude images
- C) Ignore build errors
- D) Skip health checks

**Answer**: A

---

## Quiz 2: Docker Compose

**Question 1**: Which key defines services in docker-compose.yml?
- A) apps:
- B) services:
- C) containers:
- D) images:

**Answer**: B

---

**Question 2**: How do you define dependency order?
- A) depends_on
- B) requires
- C) before
- D) order

**Answer**: A

---

**Question 3**: What does condition: service_healthy require?
- A) Health check in the image
- B) Health check in compose file
- C) External health checker
- D) Nothing

**Answer**: B

---

**Question 4**: How do you share data between containers?
- A) Shared memory
- B) Volumes
- C) Environment variables
- D) DNS

**Answer**: B

---

**Question 5**: What network mode by default in docker-compose?
- A) host
- B) bridge
- C) overlay
- D) none

**Answer**: B

---

## Quiz 3: Security

**Question 1**: How do you run container as non-root?
- A) USER nonroot
- B) RUN useradd && USER
- C) Use --user flag
- D) B and C

**Answer**: D

---

**Question 2**: Which capability should you drop?
- A) NET_BIND_SERVICE
- B) ALL
- C) SYS_ADMIN
- D) B and C

**Answer**: D

---

**Question 3**: What does read_only: true do?
- A) Read-only file system
- B) Read-only network
- C) Read-only volumes
- D) Read-only memory

**Answer**: A

---

**Question 4**: How should secrets be handled in Docker?
- A) Environment variables
- B) Volume mounts
- C) Build arguments
- D) All of the above

**Answer**: B

---

**Question 5**: What is no-new-privileges security option?
- A) Prevents new processes
- B) Prevents privilege escalation
- C) Disables new containers
- D) Blocks new networks

**Answer**: B

---

## Quiz 4: Networking

**Question 1**: What does 8080:8080 mean in port mapping?
- A) Host:Container
- B) Container:Host
- C) Both the same
- D) None of the above

**Answer**: A

---

**Question 2**: Which network is internal?
- A) bridge
- B) host
- C) overlay
- D) None of the above

**Answer**: D (internal is specific option)

---

**Question 3**: How do you resolve container by name?
- A) DNS
- B) IP address
- C) Host file
- D) None

**Answer**: A

---

**Question 4**: Which driver allows containers on different hosts?
- A) bridge
- B) host
- C) overlay
- D) macvlan

**Answer**: C

---

**Question 5**: What does extra_hosts add?
- A) Extra ports
- B) Host entries in /etc/hosts
- C) Extra networks
- D) Extra volumes

**Answer**: B

---

## Quiz 5: Volumes

**Question 1**: What is the difference between bind mount and volume?
- A) Bind mount is faster
- B) Volume is managed by Docker
- C) Bind mount is for development only
- D) Volume is for production

**Answer**: B

---

**Question 2**: How do you create named volume?
- A) volume: mydata
- B) volumes_from: mydata
- C) volume_mount: mydata
- D) mount: mydata

**Answer**: A

**Question 3**: Where are named volumes stored?
- A) /var/lib/docker/volumes
- B) /var/lib/docker/data
- C) /var/lib/docker/storage
- D) Depends on driver

**Answer**: A

---

**Question 4**: How do you make volume read-only?
- A) source:data, target:/data, readonly:true
- B) source:data, target:/data, ro:true
- C) source:data, target:/data, read_only:true
- D) All of the above

**Answer**: B

---

**Question 5**: What does tmpfs mount do?
- A) Mounts temporary file system in memory
- B) Creates temp directory
- C) Uses temp volume
- D) None

**Answer**: A

---

## Answer Key

| Quiz | Q1 | Q2 | Q3 | Q4 | Q5 |
|------|-----|-----|-----|-----|-----|
| 1    | A  | A  | B  | A  | A  |
| 2    | B  | A  | B  | B  | B  |
| 3    | D  | D  | A  | B  | B  |
| 4    | A  | D  | A  | C  | B  |
| 5    | B  | A  | A  | B  | A  |