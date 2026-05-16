# Docker & Containers - QUIZ

## Section 1: Container Basics

**Q1: What is the difference between a container and a VM?**
- A) Containers are heavier than VMs
- B) Containers share the host OS kernel; VMs include their own OS
- C) VMs are faster to start than containers
- D) There is no difference

**Q2: What is a Docker image?**
- A) A running container instance
- B) A read-only template for creating containers
- C) A Dockerfile specification
- D) A Docker volume

**Q3: Which command creates a container from an image?**
- A) `docker create`
- B) `docker build`
- C) `docker run`
- D) `docker start`

**Q4: What is the default network driver for Docker?**
- A) host
- B) bridge
- C) overlay
- D) none

**Q5: How do containers communicate with each other?**
- A) Direct IP addressing
- B) User-defined networks with DNS
- C) Only through the host
- D) They cannot communicate

## Section 2: Dockerfile

**Q6: What does the FROM instruction do?**
- A) Specifies the base image
- B) Copies files to the container
- C) Sets environment variables
- D) Defines the entry point

**Q7: In a multi-stage build, what is the purpose of `AS builder`?**
- A) Names the build stage
- B) Sets environment variables
- C) Specifies architecture
- D) Defines build arguments

**Q8: Which instruction is used to copy files from host to container?**
- A) COPY
- B) ADD
- C) Both A and B
- D) Neither

**Q9: What does `RUN mvn package` do?**
- A) Runs the application
- B) Builds the application package
- C) Copies the package
- D) Tests the application

**Q10: How do you optimize layer caching in Dockerfiles?**
- A) Put instructions that change frequently at the top
- B) Put instructions that change frequently at the bottom
- C) Use only RUN instructions
- D) Remove all comments

## Section 3: Docker Compose

**Q11: What is the minimum version for current Docker Compose?**
- A) 1.0
- B) 2.0
- C) 3.9
- D) 4.0

**Q12: How do you define dependencies between services?**
- A) `depends_on`
- B) `links`
- C) `requires`
- D) Both A and B

**Q13: What does `condition: service_healthy` ensure?**
- A) Service starts quickly
- B) Dependent service only starts after health check passes
- C) Service runs in debug mode
- D) Service has unlimited resources

**Q14: How do you scale a service with docker-compose?**
- A) `docker-compose scale app=3`
- B) `docker-compose up --scale app=3`
- C) `docker-compose scale`
- D) Both B and deprecated A

**Q15: What is a named volume?**
- A) A volume with a random name
- B) A volume with a specific name for reuse
- C) A bind mount
- D) A tmpfs volume

## Section 4: Networking

**Q16: Which network mode removes network isolation?**
- A) bridge
- B) host
- C) overlay
- D) none

**Q17: What enables automatic DNS resolution between containers?**
- A) Default bridge network
- B) User-defined networks
- C) Docker daemon
- D) Container labels

**Q18: How do you map host port 8080 to container port 8080?**
- A) `-p 8080:8080`
- B) `-port 8080`
- C) `--expose 8080`
- D) Both A and C

**Q19: What is the purpose of an ingress network?**
- A) Internal container communication
- B) External traffic routing in Swarm
- C) Volume management
- D) Image building

**Q20: How do you create a custom network?**
- A) `docker network create mynet`
- B) `docker create network mynet`
- C) `docker network new mynet`
- D) `docker net create mynet`

## Section 5: Volumes

**Q21: What is a bind mount?**
- A) A named volume
- B) Maps a host file/directory to container
- C) An in-memory volume
- D) A remote volume

**Q22: When should you use tmpfs?**
- A) For permanent storage
- B) For sensitive data that shouldn't persist
- C) For database storage
- D) For large files

**Q23: How do you back up a volume?**
- A) `docker volume backup`
- B) `docker run -v vol:/data alpine tar cf /backup/backup.tar /data`
- C) `docker cp`
- D) Volumes cannot be backed up

**Q24: What happens to volume data when a container is removed?**
- A) Data is deleted
- B) Data persists in named volumes
- C) Data moves to another container
- D) Data is archived

**Q25: How do you share data between containers?**
- A) Bind mount
- B) Named volume
- C) tmpfs
- D) All of the above

## Section 6: Security

**Q26: Why should containers not run as root?**
- A) Performance issues
- B) Security risk - container breakout gives root on host
- C) It violates Docker terms
- D) Root is too slow

**Q27: What is the purpose of a .dockerignore file?**
- A) Ignore Docker daemon errors
- B) Exclude files from build context
- C) Skip Docker installation
- D) Define container ignore rules

**Q28: How do you add a non-root user in Dockerfile?**
- A) `RUN useradd app`
- B) `RUN addgroup -S app && adduser -S app -G app`
- C) `USER app`
- D) `ADD user`

**Q29: What does `--read-only` flag do?**
- A) Makes container read-only for users
- B) Makes filesystem read-only
- C) Prevents container from starting
- D) Creates read-only images

**Q30: Why use specific image tags instead of `latest`?**
- A) Smaller size
- B) Reproducibility - avoid unexpected updates
- C) Better performance
- D) Required by Docker

---

## Answers

1. B
2. B
3. C
4. B
5. B
6. A
7. A
8. C
9. B
10. B
11. C
12. A
13. B
14. D
15. B
16. B
17. B
18. D
19. B
20. A
21. B
22. B
23. B
24. B
25. D
26. B
27. B
28. B
29. B
30. B