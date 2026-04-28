# ⚡ Quick Start Guide - Java Learning Journey

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

**Get started with Java Learning Journey in 5 minutes!**

</div>

---

## 🎯 Choose Your Path

### 🌱 Complete Beginner
**Start here if you're new to Java**

```bash
# 1. Navigate to Core Java basics
cd 01-core-java/01-java-basics

# 2. Read the README
cat README.md

# 3. Run your first Java program
javac HelloWorld.java
java HelloWorld
```

**Next Steps:**
- Complete modules 01-03 in Core Java
- Move to OOP concepts
- Practice with exercises

---

### 🚀 Experienced Developer
**Jump to advanced topics**

```bash
# Choose your framework:

# Spring Boot
cd 02-spring-boot/01-spring-boot-basics
mvn spring-boot:run

# Quarkus
cd quarkus-learning/01-Introduction-to-Quarkus/hello-quarkus
mvn quarkus:dev

# Vert.x
cd EclipseVert.XLearning/01-vertx-basics
mvn clean compile exec:java
```

---

### 🏢 Enterprise Developer
**Focus on microservices and cloud**

```bash
# 1. Explore Quarkus (Cloud-Native)
cd quarkus-learning/11-Quarkus-Cloud-Native

# 2. Try Vert.x (Reactive)
cd EclipseVert.XLearning/07-microservices

# 3. Learn Spring Cloud
cd 02-spring-boot/05-spring-cloud
```

---

## 📦 Installation

### 1. Install Java 21

**Windows:**
```powershell
# Using Chocolatey
choco install openjdk21

# Or download from
# https://adoptium.net/
```

**macOS:**
```bash
# Using Homebrew
brew install openjdk@21

# Add to PATH
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
```

**Linux:**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# Fedora/RHEL
sudo dnf install java-21-openjdk-devel
```

**Verify Installation:**
```bash
java -version
# Should show: openjdk version "21.x.x"
```

---

### 2. Install Maven

**Windows:**
```powershell
choco install maven
```

**macOS:**
```bash
brew install maven
```

**Linux:**
```bash
sudo apt install maven  # Ubuntu/Debian
sudo dnf install maven  # Fedora/RHEL
```

**Verify:**
```bash
mvn -version
```

---

### 3. Install Docker (Optional but Recommended)

**Download Docker Desktop:**
- Windows/Mac: https://www.docker.com/products/docker-desktop
- Linux: https://docs.docker.com/engine/install/

**Verify:**
```bash
docker --version
docker-compose --version
```

---

### 4. Choose Your IDE

**IntelliJ IDEA (Recommended)**
- Download: https://www.jetbrains.com/idea/download/
- Community Edition is free
- Ultimate Edition has more features

**VS Code**
```bash
# Install VS Code
# Then install extensions:
# - Extension Pack for Java
# - Spring Boot Extension Pack
# - Quarkus Extension
```

**Eclipse**
- Download: https://www.eclipse.org/downloads/
- Install Spring Tools Suite

---

## 🚀 Running Your First Project

### Option 1: Core Java (No Framework)

```bash
# 1. Create a simple Java file
cat > HelloWorld.java << 'EOF'
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, Java Learning Journey!");
    }
}
EOF

# 2. Compile
javac HelloWorld.java

# 3. Run
java HelloWorld
```

---

### Option 2: Spring Boot

```bash
# 1. Create project using Spring Initializr
curl https://start.spring.io/starter.zip \
  -d dependencies=web \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=3.2.0 \
  -d baseDir=my-spring-app \
  -o my-spring-app.zip

# 2. Extract and run
unzip my-spring-app.zip
cd my-spring-app
./mvnw spring-boot:run

# 3. Test
curl http://localhost:8080
```

---

### Option 3: Quarkus

```bash
# 1. Navigate to existing project
cd quarkus-learning/01-Introduction-to-Quarkus/hello-quarkus

# 2. Run in dev mode (with live reload!)
mvn quarkus:dev

# 3. Test
curl http://localhost:8080/hello
```

---

### Option 4: Vert.x

```bash
# 1. Navigate to Vert.x basics
cd EclipseVert.XLearning/01-vertx-basics

# 2. Run with Docker
docker-compose up

# 3. Or run with Maven
mvn clean compile exec:java
```

---

## 📚 Learning Paths

### Path 1: Java Fundamentals (2-3 months)

```
Week 1-2:  Core Java Basics
Week 3-4:  OOP Concepts
Week 5-6:  Collections & Streams
Week 7-8:  Concurrency & I/O
Week 9-10: Generics & Reflection
Week 11-12: Java 21 Features
```

**Daily Schedule:**
- 📖 Theory: 1 hour
- 💻 Coding: 2 hours
- 🧪 Practice: 1 hour

---

### Path 2: Spring Boot Mastery (2-3 months)

```
Week 1-2:  Spring Boot Basics
Week 3-4:  Spring Data JPA
Week 5-6:  Spring Security
Week 7-8:  REST APIs
Week 9-10: Spring Cloud
Week 11-12: Production Deployment
```

**Projects:**
- Week 4: Task Manager API
- Week 8: E-commerce Backend
- Week 12: Microservices System

---

### Path 3: Cloud-Native Java (3-4 months)

```
Month 1: Quarkus Fundamentals
Month 2: Reactive Programming
Month 3: Microservices Patterns
Month 4: Kubernetes Deployment
```

**Technologies:**
- Quarkus / Micronaut
- Docker & Kubernetes
- Kafka / RabbitMQ
- PostgreSQL / MongoDB

---

## 🎯 Quick Wins (Learn in 1 Hour)

### 1. Java Streams API
```bash
cd 01-core-java/04-streams-api
# Read README and run examples
```

### 2. REST API with Quarkus
```bash
cd quarkus-learning/04-REST-Services
mvn quarkus:dev
# Test endpoints with curl
```

### 3. WebSocket Chat
```bash
cd EclipseVert.XLearning/06-websockets
docker-compose up
# Open browser to localhost:8080
```

### 4. Docker Basics
```bash
cd 10-cloud-native/01-docker-basics
docker-compose up
# Learn containerization
```

---

## 🔧 Common Issues & Solutions

### Issue 1: Java Version Mismatch

**Problem:**
```
Error: LinkageError occurred while loading main class
```

**Solution:**
```bash
# Check Java version
java -version

# Set JAVA_HOME
export JAVA_HOME=/path/to/java-21
export PATH=$JAVA_HOME/bin:$PATH
```

---

### Issue 2: Maven Build Fails

**Problem:**
```
[ERROR] Failed to execute goal
```

**Solution:**
```bash
# Clean and rebuild
mvn clean install -U

# Skip tests if needed
mvn clean install -DskipTests
```

---

### Issue 3: Port Already in Use

**Problem:**
```
Port 8080 is already in use
```

**Solution:**
```bash
# Find process using port
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill process or change port
# In application.properties:
server.port=8081
```

---

### Issue 4: Docker Not Running

**Problem:**
```
Cannot connect to Docker daemon
```

**Solution:**
```bash
# Start Docker Desktop
# Or on Linux:
sudo systemctl start docker
```

---

## 📖 Essential Commands Cheat Sheet

### Maven
```bash
mvn clean install          # Build project
mvn test                   # Run tests
mvn spring-boot:run        # Run Spring Boot
mvn quarkus:dev           # Run Quarkus in dev mode
mvn exec:java             # Run Java application
```

### Docker
```bash
docker-compose up          # Start services
docker-compose down        # Stop services
docker ps                  # List containers
docker logs <container>    # View logs
```

### Git
```bash
git clone <url>           # Clone repository
git pull                  # Update repository
git checkout -b feature   # Create branch
git add .                 # Stage changes
git commit -m "message"   # Commit changes
```

### Java
```bash
javac *.java              # Compile all Java files
java ClassName            # Run Java class
java -jar app.jar         # Run JAR file
java --version            # Check Java version
```

---

## 🎓 Next Steps

### After Quick Start:

1. **Choose Your Focus:**
   - Backend Development → Spring Boot
   - Cloud-Native → Quarkus
   - Reactive Systems → Vert.x
   - Microservices → All frameworks

2. **Set Learning Goals:**
   - Complete 1 module per week
   - Build 1 project per month
   - Contribute to open source

3. **Join Community:**
   - GitHub Discussions
   - Stack Overflow
   - Reddit r/java
   - Discord servers

4. **Practice Daily:**
   - LeetCode for algorithms
   - HackerRank for Java
   - Build personal projects

---

## 📞 Get Help

**Stuck? Here's how to get help:**

1. **Check Documentation:**
   - Module README files
   - Official framework docs
   - Stack Overflow

2. **Ask Questions:**
   - GitHub Issues (for bugs)
   - GitHub Discussions (for questions)
   - Discord community

3. **Debug:**
   - Read error messages carefully
   - Check logs
   - Use IDE debugger
   - Google the error

---

## 🏆 Your First Week Challenge

### Day 1: Setup
- [ ] Install Java 21
- [ ] Install Maven
- [ ] Install IDE
- [ ] Clone repository

### Day 2: Core Java
- [ ] Complete Java Basics module
- [ ] Write 5 simple programs
- [ ] Understand variables and types

### Day 3: OOP
- [ ] Learn classes and objects
- [ ] Create a simple class
- [ ] Understand inheritance

### Day 4: Collections
- [ ] Learn ArrayList and HashMap
- [ ] Practice with examples
- [ ] Solve 3 exercises

### Day 5: Streams
- [ ] Understand Stream API
- [ ] Practice filter, map, reduce
- [ ] Solve 3 stream problems

### Day 6: Framework
- [ ] Choose a framework
- [ ] Run hello world
- [ ] Create simple REST API

### Day 7: Project
- [ ] Build a TODO API
- [ ] Add CRUD operations
- [ ] Test with Postman

---

<div align="center">

**Ready to start your Java journey?**

[Back to Main README](./README.md) | [View All Modules](./PROJECT_STATUS.md)

**Questions?** Open an issue or join our community!

🚀 **Happy Learning!** 🚀

</div>