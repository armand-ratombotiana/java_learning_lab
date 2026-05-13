# Testing Resources

Testing strategies and patterns for Java applications.

## Contents

- [Testing Strategies](./testing-strategies.md) - Testing pyramid and patterns
- [Official Documentation](#official-documentation)
- [Key Concepts](#key-concepts)

---

## Official Documentation

| Topic | Link |
|-------|------|
| JUnit 5 Docs | https://junit.org/junit5/docs/current/user-guide.html |
| Mockito Docs | https://site.mockito.org/ |
| AssertJ Docs | https://assertj.github.io/doc/ |

---

## Key Concepts

### Test Levels
- **Unit** - Individual methods/classes
- **Integration** - Component interaction
- **End-to-End** - Full system flow

### Test Types
- **Functional** - Feature behavior
- **Performance** - Load and stress
- **Security** - Vulnerability scanning

### Testing Pyramid
```
       ┌─────────┐
       │   E2E   │  Few, slow, expensive
      ┌──────────┐
      │Integration│ More, faster
     ┌───────────┐
     │   Unit    │ Many, fast, cheap
    └────────────┘
```