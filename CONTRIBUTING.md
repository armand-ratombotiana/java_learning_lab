# 🤝 Contributing to Java Learning Journey

Thank you for your interest in contributing to the Java Learning Journey! This document provides comprehensive guidelines for contributing code, documentation, and improvements to this project.

## 📌 Quick Start

1. **Clone repo**: `git clone https://github.com/armand-ratombotiana/JavaLearning.git`
2. **Create branch**: `git checkout -b feature/your-feature`
3. **Setup**: Install Java 21+, Maven 3.8.0+, and run `pre-commit install`
4. **Make changes**: Follow code standards (see below)
5. **Test**: Run `mvn clean verify -Pquality`
6. **Commit**: Follow commit message guidelines
7. **Push & PR**: Create a pull request with clear description

---

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Module Structure](#module-structure)

---

## 📜 Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inspiring community for all. Please be respectful and constructive in your interactions.

### Our Standards

**Positive behavior includes:**
- Using welcoming and inclusive language
- Being respectful of differing viewpoints
- Gracefully accepting constructive criticism
- Focusing on what is best for the community
- Showing empathy towards other community members

**Unacceptable behavior includes:**
- Trolling, insulting/derogatory comments, and personal attacks
- Public or private harassment
- Publishing others' private information without permission
- Other conduct which could reasonably be considered inappropriate

---

## 🎯 How Can I Contribute?

### 1. Reporting Bugs 🐛

Before creating bug reports, please check existing issues. When creating a bug report, include:

- **Clear title and description**
- **Steps to reproduce**
- **Expected vs actual behavior**
- **Screenshots** (if applicable)
- **Environment details** (Java version, OS, IDE)

**Example:**
```markdown
**Bug:** NullPointerException in UserService

**Steps to Reproduce:**
1. Navigate to module 02-spring-boot/01-spring-boot-basics
2. Run `mvn spring-boot:run`
3. Call GET /api/users endpoint

**Expected:** List of users returned
**Actual:** NullPointerException thrown

**Environment:**
- Java: 21
- Spring Boot: 3.2.0
- OS: Windows 11
```

### 2. Suggesting Enhancements 💡

Enhancement suggestions are tracked as GitHub issues. Include:

- **Clear title and description**
- **Use case and benefits**
- **Possible implementation approach**
- **Examples from other projects** (if applicable)

### 3. Adding New Modules 📚

We welcome new modules! Priority areas:

- **Core Java modules** (High priority)
- **Spring Boot examples** (High priority)
- **Design Patterns** (Medium priority)
- **Performance optimization** (Medium priority)
- **Real-world projects** (High priority)

### 4. Improving Documentation 📝

- Fix typos and grammatical errors
- Improve code comments
- Add missing documentation
- Create tutorials and guides
- Translate content

### 5. Writing Tests ✅

- Add unit tests
- Add integration tests
- Improve test coverage
- Add performance tests

---

## 🚀 Getting Started

### Prerequisites

```bash
# Required
☕ Java 21+
📦 Maven 3.8+ or Gradle 7+
🐳 Docker Desktop
🔧 Git
🔧 IDE (IntelliJ IDEA recommended)

# Optional
☸️ Kubernetes (Minikube)
🐘 PostgreSQL
🍃 MongoDB
📬 Kafka
```

### Fork and Clone

1. **Fork the repository** on GitHub
2. **Clone your fork:**
```bash
git clone https://github.com/YOUR_USERNAME/JavaLearning.git
cd JavaLearning
```

3. **Add upstream remote:**
```bash
git remote add upstream https://github.com/ORIGINAL_OWNER/JavaLearning.git
```

4. **Create a branch:**
```bash
git checkout -b feature/your-feature-name
# or
git checkout -b fix/your-bug-fix
```

---

## 🔄 Development Workflow

### 1. Sync with Upstream

```bash
git fetch upstream
git checkout main
git merge upstream/main
```

### 2. Create Feature Branch

```bash
git checkout -b feature/add-java-streams-module
```

### 3. Make Changes

- Write code following our [coding standards](#coding-standards)
- Add tests for new functionality
- Update documentation
- Ensure all tests pass

### 4. Test Your Changes

```bash
# Run tests
mvn clean test

# Run specific module tests
cd 01-core-java/04-streams-api
mvn test

# Run with Docker
docker-compose up --build
```

### 5. Commit Changes

```bash
git add .
git commit -m "feat: add Java Streams API module with examples"
```

### 6. Push to Your Fork

```bash
git push origin feature/add-java-streams-module
```

### 7. Create Pull Request

- Go to your fork on GitHub
- Click "New Pull Request"
- Fill in the PR template
- Wait for review

---

## 📏 Coding Standards

### Java Code Style

```java
// ✅ Good: Clear naming, proper formatting
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}

// ❌ Bad: Poor naming, no formatting
public class US {
    private UR ur;
    public U f(Long i){return ur.f(i).orElseThrow(()->new UNF(i));}
}
```

### Naming Conventions

- **Classes:** PascalCase (`UserService`, `OrderController`)
- **Methods:** camelCase (`findById`, `createUser`)
- **Variables:** camelCase (`userId`, `orderList`)
- **Constants:** UPPER_SNAKE_CASE (`MAX_RETRY_COUNT`)
- **Packages:** lowercase (`com.learning.service`)

### Code Organization

```
module-name/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/learning/
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── repository/
│   │   │       ├── model/
│   │   │       └── config/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   └── test/
│       └── java/
│           └── com/learning/
```

### Documentation

```java
/**
 * Service for managing user operations.
 * 
 * <p>This service provides CRUD operations for users and handles
 * business logic related to user management.</p>
 * 
 * @author Your Name
 * @since 1.0
 */
public class UserService {
    
    /**
     * Finds a user by their unique identifier.
     * 
     * @param id the user ID
     * @return the user if found
     * @throws UserNotFoundException if user doesn't exist
     */
    public User findById(Long id) {
        // Implementation
    }
}
```

### Testing Standards

```java
@Test
@DisplayName("Should find user by ID when user exists")
void shouldFindUserById_WhenUserExists() {
    // Given
    Long userId = 1L;
    User expectedUser = new User(userId, "John Doe");
    when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
    
    // When
    User actualUser = userService.findById(userId);
    
    // Then
    assertThat(actualUser).isEqualTo(expectedUser);
    verify(userRepository).findById(userId);
}
```

---

## 📝 Commit Guidelines

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat:** New feature
- **fix:** Bug fix
- **docs:** Documentation changes
- **style:** Code style changes (formatting)
- **refactor:** Code refactoring
- **test:** Adding or updating tests
- **chore:** Maintenance tasks

### Examples

```bash
# Feature
feat(core-java): add Collections Framework module

# Bug fix
fix(spring-boot): resolve NullPointerException in UserService

# Documentation
docs(readme): update installation instructions

# Test
test(quarkus): add integration tests for REST endpoints

# Refactor
refactor(vertx): improve error handling in WebSocket server
```

### Detailed Commit Message

```
feat(spring-boot): add Spring Security JWT authentication module

- Implement JWT token generation and validation
- Add user authentication endpoints
- Configure Spring Security with JWT
- Add integration tests for authentication flow

Closes #123
```

---

## 🔍 Pull Request Process

### PR Title Format

```
[Type] Brief description

Examples:
[Feature] Add Java Streams API module
[Fix] Resolve Docker Compose configuration issue
[Docs] Update Spring Boot README
```

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Code refactoring
- [ ] Test addition

## Changes Made
- Change 1
- Change 2
- Change 3

## Testing
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed

## Screenshots (if applicable)
Add screenshots here

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] Tests added/updated
- [ ] All tests passing
- [ ] No merge conflicts

## Related Issues
Closes #123
```

### Review Process

1. **Automated Checks:** CI/CD pipeline runs tests
2. **Code Review:** Maintainers review code
3. **Feedback:** Address review comments
4. **Approval:** At least one maintainer approval required
5. **Merge:** Maintainer merges PR

---

## 📦 Module Structure

### Standard Module Layout

```
module-name/
├── README.md                    # Module documentation
├── pom.xml                      # Maven configuration
├── docker-compose.yml           # Docker setup (if needed)
├── .gitignore
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/learning/
│   │   │       ├── Main.java
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── repository/
│   │   │       ├── model/
│   │   │       └── config/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   └── test/
│       ├── java/
│       │   └── com/learning/
│       └── resources/
├── exercises/
│   ├── exercise-01.md
│   └── solutions/
└── docs/
    └── additional-notes.md
```

### README Template

```markdown
# Module Name

## 🎯 Learning Objectives
- Objective 1
- Objective 2

## 📚 Topics Covered
- Topic 1
- Topic 2

## 🚀 Getting Started

### Prerequisites
- Requirement 1
- Requirement 2

### Running the Application
\`\`\`bash
mvn clean install
mvn exec:java
\`\`\`

## 💻 Code Examples

### Example 1
\`\`\`java
// Code here
\`\`\`

## 🧪 Testing
\`\`\`bash
mvn test
\`\`\`

## 📖 Additional Resources
- [Resource 1](link)
- [Resource 2](link)
```

---

## 🎨 Best Practices

### Code Quality

1. **Keep it Simple:** Write clear, readable code
2. **DRY Principle:** Don't Repeat Yourself
3. **SOLID Principles:** Follow SOLID design principles
4. **Error Handling:** Proper exception handling
5. **Logging:** Use appropriate logging levels

### Testing

1. **Test Coverage:** Aim for >80% coverage
2. **Test Naming:** Clear, descriptive test names
3. **Test Independence:** Tests should not depend on each other
4. **Test Data:** Use meaningful test data
5. **Assertions:** Clear, specific assertions

### Documentation

1. **Code Comments:** Explain "why", not "what"
2. **README Files:** Keep them up-to-date
3. **API Documentation:** Document public APIs
4. **Examples:** Provide working examples
5. **Diagrams:** Use diagrams for complex concepts

---

## 🏆 Recognition

Contributors will be recognized in:
- README.md Contributors section
- Release notes
- Project documentation

Top contributors may be invited to become maintainers!

---

## 📞 Getting Help

- **GitHub Issues:** For bugs and features
- **GitHub Discussions:** For questions and ideas
- **Email:** learning@javajourney.com
- **Discord:** [Join our community](https://discord.gg/java-learning)

---

## 📄 License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

<div align="center">

**Thank you for contributing to Java Learning Journey!**

Together, we're building the best Java learning resource! 🚀

</div>
