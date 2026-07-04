# Step-by-Step Docker Guide

## 1. Verify Installation
```powershell
docker version
docker info
```

## 2. Pull and Run Your First Container
```powershell
docker pull hello-world
docker run hello-world
```

## 3. Interactive Container
```powershell
docker run -it --rm ubuntu:22.04 bash
```

## 4. Build a Docker Image
```powershell
mkdir myapp && cd myapp
# Create Dockerfile
docker build -t myapp:latest .
```

## 5. Run Container with Port Mapping
```powershell
docker run -d -p 8080:80 --name myweb nginx:alpine
```

## 6. Docker Compose
```powershell
# Create docker-compose.yml
docker-compose up -d
docker-compose logs -f
docker-compose down
```

## 7. Multi-stage Build
```powershell
docker build --target production -t myapp:prod .
```

## 8. Cleanup
```powershell
docker system prune -a
```
